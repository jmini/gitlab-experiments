///usr/bin/env jbang "$0" "$@" ; exit $?

//DEPS info.picocli:picocli:4.6.3
//DEPS https://github.com/jmini/gitlab4j-api/commit/875a1fc5c4d6e1a7513a97884d2c034c24184590
//JAVA 17

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;
import java.util.concurrent.Callable;

import org.gitlab4j.api.GitLabApi;
import org.gitlab4j.api.models.SystemHook;

import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

@Command(name = "SystemHookScript", mixinStandardHelpOptions = true, version = "SystemHookScript 0.1", description = "Tests for GitLab4J")
public class SystemHookScript implements Callable<Integer> {

    private static final String CONFIG_FILE_INITIAL_CONTENT = """
            GITLAB_URL=https://gitlab.com
            GITLAB_AUTH_VALUE=
            """;

    @Parameters(index = "0", description = "action to execute", defaultValue = "LIST")
    private Action action;

    @Option(names = { "-u", "--user" }, description = "user id or username")
    String userIdOrUsername;

    @Option(names = { "-i", "--hookId", "--id" }, description = "hook id")
    Long hookId;

    @Option(names = { "--url" }, description = "hook url")
    String url;

    @Option(names = { "--token" }, description = "hook token")
    String token;

    @Option(names = { "-n", "--name" }, description = "hook name")
    String name;

    @Option(names = { "-d", "--description" }, description = "hook description")
    String description;

    @Option(names = { "-k", "--key" }, description = "URLVariable key")
    String urlVariableKey;

    @Option(names = { "-l", "--value" }, description = "URLVariable value")
    String urlVariableValue;

    @Option(names = { "-c", "--config" }, description = "configuration file location")
    String configFile;

    @Option(names = { "-v", "--verbose" }, description = "log http trafic")
    Boolean logHttp;

    private static enum Action {
        GET, LIST, CREATE, UPDATE, DELETE, ADD_VARIABLE
    }

    @Override
    public Integer call() throws Exception {
        Path file;
        if (configFile != null) {
            file = Paths.get(configFile);
        } else {
            file = configFile(Paths.get(""));
        }
        System.out.println("Reading config: " + file.toAbsolutePath());
        final Properties prop = configProperties(file);
        final String gitLabUrl = readProperty(prop, "GITLAB_URL", "https://gitlab.com");
        final String gitLabAuthValue = readProperty(prop, "GITLAB_AUTH_VALUE");

        try (GitLabApi gitLabApi = createGitLabApi(gitLabUrl, gitLabAuthValue)) {
            if (logHttp != null && logHttp) {
                gitLabApi.enableRequestResponseLogging(java.util.logging.Level.INFO, 2000000000);
            }
            switch (action) {
            case GET:
                ensureExists(hookId, "hookId");
                var hook = gitLabApi.getSystemHooksApi()
                        .getSystemHook(hookId);
                System.out.println(hook);
                break;
            case LIST:
                var hooks = gitLabApi.getSystemHooksApi()
                        .getSystemHooks();
                System.out.println(hooks);
                break;
            case CREATE:
                ensureExists(url, "url");
                var create = new SystemHook();
                create.setName(name);
                create.setDescription(description);
                var created = gitLabApi.getSystemHooksApi()
                        .addSystemHook(url, token, create);
                System.out.println(created);
                break;
            case UPDATE:
                ensureExists(hookId, "hookId");
                var update = new SystemHook();
                update.setId(hookId);
                update.setName(name);
                update.setDescription(description);
                update.setUrl(url);
                var updated = gitLabApi.getSystemHooksApi()
                        .updateSystemHook(update, token);
                System.out.println(updated);
                break;
            case DELETE:
                ensureExists(hookId, "hookId");
                gitLabApi.getSystemHooksApi()
                        .deleteSystemHook(hookId);
                System.out.println("hook with id " + hookId + " deleted");
                break;
            case ADD_VARIABLE:
                ensureExists(hookId, "hookId");
                ensureExists(urlVariableKey, "key");
                ensureExists(urlVariableValue, "value");
                gitLabApi.getSystemHooksApi()
                        .addSystemHookUrlVariable(hookId, urlVariableKey, urlVariableValue);
                System.out.println("variable added for hook with id " + hookId);
                break;
            default:
                throw new IllegalArgumentException("Unexpected value: " + action);
            }
        }
        return 0;
    }

    private GitLabApi createGitLabApi(String gitLabUrl, String gitLabAuthValue) {
        return new GitLabApi(gitLabUrl, gitLabAuthValue);
    }

    private void ensureExists(Object value, String optionName) {
        if (value == null) {
            throw new IllegalStateException("--" + optionName + " must be set");
        }
    }

    private Object idOrPath(String value) {
        if (value.matches("[0-9]+")) {
            return Long.valueOf(value);
        }
        return value;
    }

    public static Properties configProperties(final Path configFile) {
        try (InputStream is = new FileInputStream(configFile.toFile())) {
            final Properties properties = new Properties();
            properties.load(is);
            return properties;
        } catch (final IOException e) {
            throw new IllegalStateException("Can not read config file", e);
        }
    }

    public static Path configFile(final Path root) {
        final Path configFile = root.toAbsolutePath()
                .resolve("gitlab-config.properties");
        if (!Files.isRegularFile(configFile)) {
            try {
                Files.writeString(configFile, CONFIG_FILE_INITIAL_CONTENT);
                throw new IllegalStateException(String.format("Configuration file '%s' does not exist. An empty configuration file was created", configFile.toAbsolutePath()));
            } catch (final IOException e) {
                throw new IllegalStateException("Can not write initial config file", e);
            }
        }
        return configFile;
    }

    public static String readProperty(final Properties p, final String key) {
        if (!p.containsKey(key)) {
            throw new IllegalStateException(String.format("Configuration file does not contains key '%s'", key));
        }
        final String value = p.getProperty(key);
        if (value == null || value.isBlank()) {
            throw new IllegalStateException(String.format("Key '%s' is not defined in configuration file", key));
        }
        return value;
    }

    public static String readProperty(final Properties p, final String key, final String defaultValue) {
        return p.getProperty(key, defaultValue);
    }

    public static void main(final String... args) {
        final int exitCode = new CommandLine(new SystemHookScript()).execute(args);
        System.exit(exitCode);
    }
}

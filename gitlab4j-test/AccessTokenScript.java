///usr/bin/env jbang "$0" "$@" ; exit $?

//DEPS info.picocli:picocli:4.6.3
//DEPS https://github.com/jmini/gitlab4j-api/commit/1627d509400bcd4f9de092e9e8c30cceddc62da4
//JAVA 17

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.Callable;

import org.gitlab4j.api.GitLabApi;
import org.gitlab4j.api.models.ImpersonationToken.Scope;

import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

@Command(name = "AccessTokenScript", mixinStandardHelpOptions = true, version = "AccessTokenScript 0.1", description = "Tests for GitLab4J")
public class AccessTokenScript implements Callable<Integer> {

    private static final String CONFIG_FILE_INITIAL_CONTENT = """
            GITLAB_URL=https://gitlab.com
            GITLAB_AUTH_VALUE=
            """;

    @Parameters(index = "0", description = "action to execute", defaultValue = "GET_SELF")
    private Action action;

    @Option(names = { "-u", "--user" }, description = "user id or username")
    String userIdOrUsername;

    @Option(names = { "-i", "--tokenId" }, description = "token id")
    String tokenId;

    @Option(names = { "-n", "--tokenName" }, description = "token name")
    String tokenName;

    @Option(names = { "-d", "--description" }, description = "token description")
    String description;

    @Option(names = { "-e", "--expiresAt" }, description = "token expiration date")
    Date expiresAt;

    @Option(names = { "-s", "--scope" }, description = "token scopes")
    List<Scope> scopes = new ArrayList<>();

    @Option(names = { "-c", "--config" }, description = "configuration file location")
    String configFile;

    @Option(names = { "-v", "--verbose" }, description = "log http trafic")
    Boolean logHttp;

    private static enum Action {
        GET_SELF, LIST, CREATE, ROTATE
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
            case GET_SELF:
                var selfPersonalAccessToken = gitLabApi.getPersonalAccessTokenApi()
                        .getPersonalAccessToken();
                System.out.println(selfPersonalAccessToken);
                break;
            case LIST:
                var tokens = gitLabApi.getPersonalAccessTokenApi()
                        .getPersonalAccessTokens();
                System.out.println(tokens);
                break;
            case CREATE:
                ensureExists(userIdOrUsername, "user");
                var newToken = gitLabApi.getUserApi()
                        .createPersonalAccessToken(idOrPath(userIdOrUsername), tokenName, description, expiresAt, scopes.toArray(new Scope[] {}));
                System.out.println(newToken);
                break;
            case ROTATE:
                ensureExists(tokenId, "tokenId");
                var rotated = gitLabApi.getPersonalAccessTokenApi()
                        .rotatePersonalAccessToken(tokenId, toDate(LocalDate.now()
                                .plusDays(30)));
                System.out.println(rotated);
                break;
            default:
                throw new IllegalArgumentException("Unexpected value: " + action);
            }
        }
        return 0;
    }

    private Date toDate(LocalDate localDate) {
        return Date.from(localDate.atStartOfDay(ZoneId.systemDefault())
                .toInstant());
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
        final int exitCode = new CommandLine(new AccessTokenScript()).execute(args);
        System.exit(exitCode);
    }
}

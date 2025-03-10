///usr/bin/env jbang "$0" "$@" ; exit $?

//DEPS info.picocli:picocli:4.6.3
//DEPS org.gitlab4j:gitlab4j-api:6.0.0-rc.8
//JAVA 17

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.Callable;

import org.gitlab4j.api.GitLabApi;
import org.gitlab4j.api.SearchApi;
import org.gitlab4j.models.Constants.GroupSearchScope;
import org.gitlab4j.models.Constants.ProjectSearchScope;
import org.gitlab4j.models.Constants.SearchScope;

import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

@Command(name = "SearchScript", mixinStandardHelpOptions = true, version = "SearchScript 0.1", description = "Tests for GitLab4J")
public class SearchScript implements Callable<Integer> {

    private static final String CONFIG_FILE_INITIAL_CONTENT = """
            GITLAB_URL=https://gitlab.com
            GITLAB_AUTH_VALUE=
            """;

    @Option(names = { "-q", "--query" }, required = true, description = "search query")
    private String query;

    @Option(names = { "-s", "--scope" }, required = true, description = "search scope")
    private String scope;

    @Option(names = { "-p", "--project" }, description = "perform a project search")
    private String project;

    @Option(names = { "-g", "--group" }, description = "perform a group search")
    private String group;

    @Option(names = { "-c", "--config" }, description = "configuration file location")
    String configFile;

    @Option(names = { "-v", "--verbose" }, description = "log http trafic")
    Boolean logHttp;

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

        if (project != null && group != null) {
            System.out.println("'--project' and '--group' can't be set at the same time");
            return 1;
        }
        try (GitLabApi gitLabApi = new GitLabApi(gitLabUrl, gitLabAuthValue)) {
            if (logHttp != null && logHttp) {
                gitLabApi.enableRequestResponseLogging(java.util.logging.Level.INFO, 2000000000);
            }
            final SearchApi searchApi = gitLabApi.getSearchApi();
            List<?> result;
            ensureExists(scope, "scope");
            if (project == null && group == null) {
                System.out.println("Global search...");
                SearchScope<Object> globalSearchScope = SearchScope.forValue(scope);
                if (globalSearchScope == null) {
                    System.out.println("Value '" + scope + "' is not expected for '--scope', possible values: " + SearchScope.values());
                    return 1;
                }

                result = searchApi.globalSearch(globalSearchScope, query);
            } else if (project != null) {
                System.out.println("Project search...");
                ProjectSearchScope<Object> projectSearchScope = ProjectSearchScope.forValue(scope);
                if (projectSearchScope == null) {
                    System.out.println("Value '" + scope + "' is not expected for '--scope', possible values: " + ProjectSearchScope.values());
                    return 1;
                }
                result = searchApi.projectSearch(idOrPath(project), projectSearchScope, query);
            } else if (group != null) {
                System.out.println("Group search...");
                GroupSearchScope<Object> groupSearchScope = GroupSearchScope.forValue(scope);
                if (groupSearchScope == null) {
                    System.out.println("Value '" + scope + "' is not expected for '--scope', possible values: " + GroupSearchScope.values());
                    return 1;
                }
                result = searchApi.groupSearch(idOrPath(group), groupSearchScope, query);
            } else {
                throw new IllegalArgumentException("Unexpected state (input parameters might be wrong)");
            }
            System.out.println(result);
        }
        return 0;
    }

    private Object idOrPath(String value) {
        if (value.matches("[0-9]+")) {
            return Long.valueOf(value);
        }
        return value;
    }

    private void ensureExists(Object value, String optionName) {
        if (value == null) {
            throw new IllegalStateException("--" + optionName + " must be set");
        }
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
        final int exitCode = new CommandLine(new SearchScript()).execute(args);
        System.exit(exitCode);
    }
}

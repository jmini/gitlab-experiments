///usr/bin/env jbang "$0" "$@" ; exit $?

//DEPS info.picocli:picocli:4.6.3
//DEPS https://github.com/jmini/gitlab4j-api/commit/632279d454d90469be99d8f6059285e1a7524931
//JAVA 17

import java.io.FileInputStream;
import java.io.InputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.Callable;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import org.gitlab4j.api.GitLabApi;
import org.gitlab4j.api.models.*;

import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

@Command(name = "EpicScript", mixinStandardHelpOptions = true, version = "EpicScript 0.1", description = "Tests for GitLab4J")
public class EpicScript implements Callable<Integer> {

    private static final String CONFIG_FILE_INITIAL_CONTENT = """
            GITLAB_URL=https://gitlab.com
            GITLAB_AUTH_VALUE=
            """;

    @Parameters(index = "0", description = "action to execute", defaultValue = "GET_EPIC")
    private Action action;

    @Option(names = { "-g", "--group" }, description = "group")
    private String group;

    @Option(names = { "-e", "--epic" }, description = "epic iid")
    private Long epicIid;

    @Option(names = { "-d", "--description" }, description = "epic description")
    private String description;

    @Option(names = { "-t", "--title" }, description = "epic title")
    private String title;

    @Option(names = { "-c", "--config" }, description = "configuration file location")
    String configFile;

    @Option(names = { "-v", "--verbose" }, description = "log http trafic")
    Boolean logHttp;

    private static enum Action {
        GROUP_EPICS, GET_EPIC, CREATE_EPIC, CLOSE_EPIC, REOPEN_EPIC
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
            switch (action) {
            case GROUP_EPICS:
                ensureExists(group, "group");
                List<Epic> epics = gitLabApi.getEpicsApi()
                        .getEpics(idOrPath(group));
                System.out.println(epics);
                break;
            case GET_EPIC:
                ensureExists(group, "group");
                ensureExists(epicIid, "epic");
                Epic epic = gitLabApi.getEpicsApi()
                        .getEpic(idOrPath(group), epicIid);
                System.out.println(epic);
                break;
            case CREATE_EPIC:
                ensureExists(group, "group");
                ensureExists(title, "title");
                ensureExists(description, "description");
                String labels = null;
                Date startDate = null;
                Date endDate = null;
                Date createdAt = null;
                Epic createdEpic = gitLabApi.getEpicsApi()
                        .createEpic(idOrPath(group), title, labels, description, startDate, endDate, createdAt);
                System.out.println(createdEpic);
                break;
            case CLOSE_EPIC:
                ensureExists(group, "group");
                ensureExists(epicIid, "epic");
                throw new IllegalStateException("not implemented");
                // Epic closedEpic = gitLabApi.getEpicsApi()
                //         .closeEpic(idOrPath(group), epicIid);
                // System.out.println(closedEpic);
                // break;
            case REOPEN_EPIC:
                ensureExists(group, "group");
                ensureExists(epicIid, "epic");
                throw new IllegalStateException("not implemented");
                // Epic reopenedEpic = gitLabApi.getEpicsApi()
                //         .reopenEpic(idOrPath(group), epicIid);
                // System.out.println(reopenedEpic);
                // break;
            default:
                throw new IllegalArgumentException("Unexpected value: " + action);
            }
        }
        return 0;
    }

    private GitLabApi createGitLabApi(String gitLabUrl, String gitLabAuthValue) {
        if (logHttp != null && logHttp) {
            return new GitLabApi(gitLabUrl, gitLabAuthValue)
                .withRequestResponseLogging(java.util.logging.Level.INFO) ;
        }
        return new GitLabApi(gitLabUrl, gitLabAuthValue);
    }

    private void ensureExists(Object value, String optionName) {
        if (value == null) {
            throw new IllegalStateException("--" + optionName + " must be set");
        }
    }

    private IterationFilter existingOrNew(IterationFilter existing) {
        if (existing != null) {
            return existing;
        }
        return new IterationFilter();
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
        final int exitCode = new CommandLine(new EpicScript()).execute(args);
        System.exit(exitCode);
    }
}

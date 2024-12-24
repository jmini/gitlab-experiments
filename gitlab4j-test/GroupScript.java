///usr/bin/env jbang "$0" "$@" ; exit $?

//DEPS info.picocli:picocli:4.6.3
//DEPS org.gitlab4j:gitlab4j-api:6.0.0-rc.7
//JAVA 17

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.Callable;

import org.gitlab4j.api.GitLabApi;
import org.gitlab4j.api.models.*;

import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

@Command(name = "GroupScript", mixinStandardHelpOptions = true, version = "GroupScript 0.1", description = "Tests for GitLab4J")
public class GroupScript implements Callable<Integer> {

    private static final String CONFIG_FILE_INITIAL_CONTENT = """
            GITLAB_URL=https://gitlab.com
            GITLAB_AUTH_VALUE=
            """;

    @Parameters(index = "0", description = "action to execute", defaultValue = "GET_GROUP")
    private Action action;

    @Option(names = { "-g", "--group" }, description = "group id")
    private String group;

    @Option(names = { "-c", "--config" }, description = "configuration file location")
    String configFile;

    @Option(names = { "-v", "--verbose" }, description = "log http trafic")
    Boolean logHttp;

    private static enum Action {
        GET_GROUP, GET_ALL_GROUPS, GET_SUB_GROUPS, GET_DESCENDANT_GROUPS, GET_GROUP_LABELS, GET_GROUP_UPLOADS
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
            case GET_GROUP:
                ensureExists(group, "group");
                var groupResponse = gitLabApi.getGroupApi()
                        .getGroup(idOrPath(group));
                System.out.println(groupResponse);
                break;
            case GET_ALL_GROUPS:
                var groups = gitLabApi.getGroupApi()
                        .getGroups();
                groups.stream()
                        .sorted(Comparator.comparing(Group::getFullName))
                        .forEach(g -> {
                            System.out.println(g.getFullName());
                        });
                break;
            case GET_SUB_GROUPS:
                ensureExists(group, "group");
                var subGroups = gitLabApi.getGroupApi()
                        .getSubGroups(idOrPath(group));
                for (Group g : subGroups) {
                    System.out.println(g.getFullName());
                }
                break;
            case GET_DESCENDANT_GROUPS:
                ensureExists(group, "group");
                var descendantGroups = gitLabApi.getGroupApi()
                        .getDescendantGroups(idOrPath(group), new GroupFilter()
                                .withAllAvailable(true));
                for (Group g : descendantGroups) {
                    System.out.println(g.getFullName());
                }
                break;
            case GET_GROUP_LABELS:
                ensureExists(group, "group");
                var labelsResponse = gitLabApi.getLabelsApi()
                        .getGroupLabels(idOrPath(group));
                System.out.println(labelsResponse);
                break;
            case GET_GROUP_UPLOADS:
                ensureExists(group, "group");
                var uploadsResponse = gitLabApi.getGroupApi()
                        .getUploadFiles(idOrPath(group));
                System.out.println(uploadsResponse);
                break;
            default:
                throw new IllegalArgumentException("Unexpected value: " + action);
            }
        }
        return 0;
    }

    private GitLabApi createGitLabApi(String gitLabUrl, String gitLabAuthValue) {
        if (logHttp != null && logHttp) {
            return new GitLabApi(gitLabUrl, gitLabAuthValue)
                    .withRequestResponseLogging(java.util.logging.Level.INFO);
        }
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
        final int exitCode = new CommandLine(new GroupScript()).execute(args);
        System.exit(exitCode);
    }
}

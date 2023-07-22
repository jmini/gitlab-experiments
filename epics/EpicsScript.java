///usr/bin/env jbang "$0" "$@" ; exit $?

//DEPS info.picocli:picocli:4.6.3
//DEPS https://github.com/jmini/gitlab4j-api/tree/5.3.0-jmini.3
//JAVA 17

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.Callable;

import org.gitlab4j.api.Constants.EpicOrderBy;
import org.gitlab4j.api.Constants.SortOrder;
import org.gitlab4j.api.GitLabApi;
import org.gitlab4j.api.models.AbstractEpic;
import org.gitlab4j.api.models.AbstractEpic.EpicState;
import org.gitlab4j.api.models.Epic;
import org.gitlab4j.api.models.EpicFilter;
import org.gitlab4j.api.models.Group;
import org.gitlab4j.api.models.RelatedEpic;

import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

@Command(name = "EpicsScript", mixinStandardHelpOptions = true, version = "EpicsScript 0.1", description = "Tests with related epics in GitLab")
class EpicsScript implements Callable<Integer> {

    private static final String CONFIG_FILE_INITIAL_CONTENT = """
            GITLAB_URL=https://gitlab.com
            GITLAB_AUTH_VALUE=
            """;

    @Parameters(index = "0", description = "The group id or group path")
    private String group;

    @Parameters(index = "1", description = "action to execute", defaultValue = "PRINT_GROUP")
    private Action action;

    @Option(names = { "-l", "--labels" })
    String labels;

    @Option(names = { "-a", "--authorId" })
    Long authorId;

    @Option(names = { "-u", "--authorUsername" })
    String authorUsername;

    @Option(names = "--orderBy")
    EpicOrderBy orderBy;

    @Option(names = "--sortOrder")
    SortOrder sortOrder;

    @Option(names = "--search")
    String search;

    @Option(names = "--state")
    EpicState state;

    @Option(names = "--createdAfter")
    Date createdAfter;

    @Option(names = "--updatedAfter")
    Date updatedAfter;

    @Option(names = "--updatedBefore")
    Date updatedBefore;

    @Option(names = "--includeAncestorGroups")
    Boolean includeAncestorGroups;

    @Option(names = "--includeDescendantGroups")
    Boolean includeDescendantGroups;

    @Option(names = { "-n", "--not" })
    boolean not = false;

    @Option(names = { "-c", "--config" }, description = "configuration file location")
    String configFile;

    private static enum Action {
        PRINT_GROUP, PRINT_GROUP_EPICS, FILTER_EPICS
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
        Properties prop = configProperties(file);
        String gitLabUrl = readProperty(prop, "GITLAB_URL", "https://gitlab.com");
        String gitLabAuthValue = readProperty(prop, "GITLAB_AUTH_VALUE");

        try (GitLabApi gitLabApi = new GitLabApi(gitLabUrl, gitLabAuthValue)) {
            Group g = gitLabApi.getGroupApi()
                    .getGroup(group);

            List<Epic> list;
            switch (action) {
            case PRINT_GROUP:
                System.out.println(g);
                break;
            case PRINT_GROUP_EPICS:
                list = gitLabApi.getEpicsApi()
                        .getEpics(g);
                printEpics(list);
                break;
            case FILTER_EPICS:
                EpicFilter filter = new EpicFilter();
                if (labels != null) {
                    if (not) {
                        filter.withoutLabels(labels);
                    } else {
                        filter.withLabels(labels);
                    }
                }
                if (authorId != null) {
                    if (not) {
                        filter.withoutAuthorId(authorId);
                    } else {
                        filter.withAuthorId(authorId);
                    }
                }
                if (authorUsername != null) {
                    if (not) {
                        filter.withoutAuthorUsername(authorUsername);
                    } else {
                        filter.withAuthorUsername(authorUsername);
                    }
                }
                if (orderBy != null) {
                    filter.withOrderBy(orderBy);
                }
                if (sortOrder != null) {
                    filter.withSortOrder(sortOrder);
                }
                if (search != null) {
                    filter.withSearch(search);
                }
                if (state != null) {
                    filter.withState(state);
                }
                if (createdAfter != null) {
                    filter.withCreatedAfter(createdAfter);
                }
                if (updatedAfter != null) {
                    filter.withUpdatedAfter(updatedAfter);
                }
                if (updatedBefore != null) {
                    filter.withUpdatedBefore(updatedBefore);
                }
                if (includeAncestorGroups != null) {
                    filter.withIncludeAncestorGroups(includeAncestorGroups);
                }
                if (includeDescendantGroups != null) {
                    filter.withIncludeDescendantGroups(includeDescendantGroups);
                }
                list = gitLabApi.getEpicsApi()
                        .getEpics(g, filter);
                printEpics(list);

                break;
            }
        }
        return 0;
    }

    private void printEpics(List<? extends AbstractEpic<?>> list) {
        System.out.println("== number of epics: " + list.size());
        for (AbstractEpic<?> child : list) {
            printEpic(child);
        }
    }

    private void printEpic(AbstractEpic<?> e) {
        System.out.println("id: " + e.getId() + ", iid: " + e.getIid() + ", title: " + e.getTitle() + ", webUrl: " + e.getWebUrl());
    }

    private void printEpic(RelatedEpic e) {
        System.out.println("id: " + e.getId() + ", iid: " + e.getIid() + ", title: " + e.getTitle() + ", webUrl: " + e.getWebUrl() + ", linkType: " + e.getLinkType());
    }

    public static Properties configProperties(Path configFile) {
        try (InputStream is = new FileInputStream(configFile.toFile())) {
            Properties properties = new Properties();
            properties.load(is);
            return properties;
        } catch (IOException e) {
            throw new IllegalStateException("Can not read config file", e);
        }
    }

    public static Path configFile(Path root) {
        Path configFile = root.toAbsolutePath()
                .resolve("gitlab-config.properties");
        if (!Files.isRegularFile(configFile)) {
            try {
                Files.writeString(configFile, CONFIG_FILE_INITIAL_CONTENT);
                throw new IllegalStateException(String.format("Configuration file '%s' does not exist. An empty configuration file was created", configFile.toAbsolutePath()));
            } catch (IOException e) {
                throw new IllegalStateException("Can not write initial config file", e);
            }
        }
        return configFile;
    }

    public static String readProperty(Properties p, String key) {
        if (!p.containsKey(key)) {
            throw new IllegalStateException(String.format("Configuration file does not contains key '%s'", key));
        }
        String value = p.getProperty(key);
        if (value == null || value.isBlank()) {
            throw new IllegalStateException(String.format("Key '%s' is not defined in configuration file", key));
        }
        return value;
    }

    public static String readProperty(Properties p, String key, String defaultValue) {
        return p.getProperty(key, defaultValue);
    }

    public static void main(String... args) {
        int exitCode = new CommandLine(new EpicsScript()).execute(args);
        System.exit(exitCode);
    }
}

///usr/bin/env jbang "$0" "$@" ; exit $?

//DEPS info.picocli:picocli:4.6.3
//DEPS https://github.com/jmini/gitlab4j-api/tree/5.3.0-pr_1043
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
import org.gitlab4j.api.models.Label;

import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

@Command(name = "LabelScript", mixinStandardHelpOptions = true, version = "LabelScript 0.1", description = "Tests for GitLab4J")
public class LabelScript implements Callable<Integer> {

    private static final String AGENTA_PINK = "#cc338b";
    private static final String ARK_CORAL = "#cd5b45";
    private static final String ARK_SEA_GREEN = "#8fbc8f";
    private static final String ARK_VIOLET = "#9400d3";
    private static final String ARROT_ORANGE = "#ed9121";
    private static final String AVENDER = "#e6e6fa";
    private static final String EEP_VIOLET = "#330066";
    private static final String HARCOAL_GREY = "#36454f";
    private static final String ITANIUM_YELLOW = "#eee600";
    private static final String LUE_GRAY = "#6699cc";
    private static final String OSE_RED = "#c21e56";
    private static final String RAY = "#808080";
    private static final String REEN_CYAN = "#009966";
    private static final String RIMSON = "#dc143c";

    private static final String CONFIG_FILE_INITIAL_CONTENT = """
            GITLAB_URL=https://gitlab.com
            GITLAB_AUTH_VALUE=
            """;

    @Parameters(index = "0", description = "action to execute", defaultValue = "LIST_LABELS")
    private Action action;

    @Option(names = { "-p", "--project" }, description = "project")
    private String project;

    @Option(names = { "-g", "--group" }, description = "group")
    private String group;

    @Option(names = { "-l", "--label" }, description = "label id or name")
    private String labelIdOrName;

    @Option(names = { "-n", "--name" }, description = "label name to create or update")
    private String labelName;

    @Option(names = { "-d", "--description" }, description = "label description to create or update")
    private String labelDescription;

    @Option(names = { "--color" }, description = "label color to create or update")
    private String labelColor;

    @Option(names = { "--priority" }, description = "label priority to create or update")
    private Integer labelPriority;

    @Option(names = { "-c", "--config" }, description = "configuration file location")
    String configFile;

    private static enum Action {
        LIST_LABELS, GET_LABEL, CREATE_LABEL, UPDATE_LABEL, DELETE_LABEL
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

        if (project != null && group != null) {
            throw new IllegalStateException("Both --project and --group can't be set at the same time");
        } else if (project == null && group == null) {
            throw new IllegalStateException("One of --project and --group must be set");
        }
        try (GitLabApi gitLabApi = new GitLabApi(gitLabUrl, gitLabAuthValue)) {
            switch (action) {
            case LIST_LABELS:
                List<Label> labels;
                if (project != null) {
                    labels = gitLabApi.getLabelsApi()
                            .getProjectLabels(idOrPath(project));
                } else {
                    labels = gitLabApi.getLabelsApi()
                            .getGroupLabels(idOrPath(group));
                }
                System.out.println(labels);
                break;
            case GET_LABEL:
                Label label;
                ensureExists(labelIdOrName, "label");
                if (project != null) {
                    label = gitLabApi.getLabelsApi()
                            .getProjectLabel(idOrPath(project), idOrPath(labelIdOrName));
                } else {
                    label = gitLabApi.getLabelsApi()
                            .getGroupLabel(idOrPath(group), idOrPath(labelIdOrName));
                }
                System.out.println(label);
                break;
            case CREATE_LABEL:
                Label createdLabel;
                Label labelToCreate = new Label()
                        .withName(labelName)
                        .withDescription(labelDescription)
                        .withColor(toColor(labelColor))
                        .withPriority(labelPriority);
                if (project != null) {
                    createdLabel = gitLabApi.getLabelsApi()
                            .createProjectLabel(idOrPath(project), labelToCreate);
                } else {
                    createdLabel = gitLabApi.getLabelsApi()
                            .getGroupLabel(idOrPath(group), labelToCreate);
                }
                System.out.println(createdLabel);
                break;
            case UPDATE_LABEL:
                ensureExists(labelIdOrName, "label");
                Label labelToUpdate = new Label()
                        .withName(labelName)
                        .withDescription(labelDescription)
                        .withColor(toColor(labelColor))
                        .withPriority(labelPriority);

                Label updatedLabel;
                if (project != null) {
                    updatedLabel = gitLabApi.getLabelsApi()
                            .updateProjectLabel(idOrPath(project), idOrPath(labelIdOrName), labelToUpdate);
                } else {
                    updatedLabel = gitLabApi.getLabelsApi()
                            .updateGroupLabel(idOrPath(group), idOrPath(labelIdOrName), labelToUpdate);
                }
                System.out.println(updatedLabel);
                break;
            case DELETE_LABEL:
                ensureExists(labelIdOrName, "label");
                if (project != null) {
                    gitLabApi.getLabelsApi()
                            .deleteProjectLabel(idOrPath(project), idOrPath(labelIdOrName));
                } else {
                    gitLabApi.getLabelsApi()
                            .deleteGroupLabel(idOrPath(project), idOrPath(labelIdOrName));
                }
                break;
            default:
                throw new IllegalArgumentException("Unexpected value: " + action);
            }
        }
        return 0;
    }

    private static String toColor(String color) {
        if (color == null) {
            return null;
        }
        if (color.startsWith("#")) {
            return color;
        }
        switch (color) {
        case "AGENTA_PINK":
            return AGENTA_PINK;
        case "ARK_CORAL":
            return ARK_CORAL;
        case "ARK_SEA_GREEN":
            return ARK_SEA_GREEN;
        case "ARK_VIOLET":
            return ARK_VIOLET;
        case "ARROT_ORANGE":
            return ARROT_ORANGE;
        case "AVENDER":
            return AVENDER;
        case "EEP_VIOLET":
            return EEP_VIOLET;
        case "HARCOAL_GREY":
            return HARCOAL_GREY;
        case "ITANIUM_YELLOW":
            return ITANIUM_YELLOW;
        case "LUE_GRAY":
            return LUE_GRAY;
        case "OSE_RED":
            return OSE_RED;
        case "RAY":
            return RAY;
        case "REEN_CYAN":
            return REEN_CYAN;
        case "RIMSON":
            return RIMSON;
        default:
            throw new IllegalArgumentException("Unexpected value: " + color);
        }
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
        final int exitCode = new CommandLine(new LabelScript()).execute(args);
        System.exit(exitCode);
    }
}

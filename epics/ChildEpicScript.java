///usr/bin/env jbang "$0" "$@" ; exit $?

//DEPS info.picocli:picocli:4.6.3
//DEPS org.gitlab4j:gitlab4j-api:5.4.0
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
import org.gitlab4j.api.GitLabApiException;
import org.gitlab4j.api.models.AbstractEpic;
import org.gitlab4j.api.models.ChildEpic;
import org.gitlab4j.api.models.CreatedChildEpic;
import org.gitlab4j.api.models.Epic;
import org.gitlab4j.api.models.Group;

import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

@Command(name = "ChildEpicScript", mixinStandardHelpOptions = true, version = "ChildEpicScript 0.1", description = "Tests with child epics in GitLab")
class ChildEpicScript implements Callable<Integer> {

    private static final String CONFIG_FILE_INITIAL_CONTENT = """
            GITLAB_URL=https://gitlab.com
            GITLAB_AUTH_VALUE=
            """;

    @Parameters(index = "0", description = "The group id or group path")
    private String group;

    @Parameters(index = "1", description = "action to execute", defaultValue = "PRINT_GROUP")
    private Action action;

    @Option(names = { "-e", "--epic" }, description = "epic iid")
    private Long epicIid;

    @Option(names = { "-ce", "--child-epic-id" }, description = "child epic id")
    private Long childEpicId;

    @Option(names = { "-b", "--move-before-id" }, description = "child epic id to move before")
    private Long moveBeforeId;

    @Option(names = { "-a", "--move-after-id" }, description = "child epic id to move after")
    private Long moveAfterId;

    @Option(names = { "-c", "--config" }, description = "configuration file location")
    String configFile;

    private static enum Action {
        PRINT_GROUP, CREATE_NEW_EPIC, PRINT_EPIC, CREATE_CHILD_EPIC, ASSIGN_CHILD_EPIC, MOVE_CHILD_EPIC, DELETE_CHILD_EPIC
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

            Epic e;
            switch (action) {
            case PRINT_GROUP:
                System.out.println(g);
                break;
            case CREATE_NEW_EPIC:
                e = gitLabApi.getEpicsApi()
                        .createEpic(g, "some epic", null, null, null, null);
                System.out.println(e);
                break;
            case PRINT_EPIC:
                e = readEpic(gitLabApi, g);
                printEpic(e);
                List<ChildEpic> children = gitLabApi.getEpicsApi()
                        .getChildEpics(g, epicIid);
                printChildEpicScripts(children);
                break;
            case CREATE_CHILD_EPIC:
                e = readEpic(gitLabApi, g);
                CreatedChildEpic child = gitLabApi.getEpicsApi()
                        .createAndAssignChildEpic(g, epicIid, "some childEpic", null);
                System.out.println(child);
                break;
            case ASSIGN_CHILD_EPIC:
                if (childEpicId == null) {
                    throw new IllegalStateException("--child-epic-id is mandatory");
                }
                e = readEpic(gitLabApi, g);
                ChildEpic r = gitLabApi.getEpicsApi()
                        .assignChildEpic(g, epicIid, childEpicId);
                System.out.println(r);
                break;
            case MOVE_CHILD_EPIC:
                if (childEpicId == null) {
                    throw new IllegalStateException("--child-epic-id is mandatory");
                }
                if (moveBeforeId == null && moveAfterId == null) {
                    throw new IllegalStateException("both '--move-before-id' and '--move-after-id' are null");
                }
                List<ChildEpic> list = gitLabApi.getEpicsApi()
                        .reOrderChildEpic(g, epicIid, childEpicId, moveBeforeId, moveAfterId);
                printChildEpicScripts(list);
                break;
            case DELETE_CHILD_EPIC:
                if (childEpicId == null) {
                    throw new IllegalStateException("--child-epic-id is mandatory");
                }
                ChildEpic childEpic = r = gitLabApi.getEpicsApi()
                        .unassignChildEpic(g, epicIid, childEpicId);
                printEpic(childEpic);
                System.out.println(childEpic);
                break;
            }
        }
        return 0;
    }

    private void printChildEpicScripts(List<ChildEpic> children) {
        System.out.println("== number of child epics: " + children.size());
        for (ChildEpic child : children) {
            printEpic(child);
        }
    }

    private void printEpic(AbstractEpic<?> e) {
        System.out.println("id: " + e.getId() + ", iid: " + e.getIid() + ", title: " + e.getTitle() + ", webUrl: " + e.getWebUrl());
    }

    private Epic readEpic(GitLabApi gitLabApi, Group g) throws GitLabApiException {
        Epic e = gitLabApi.getEpicsApi()
                .getEpic(g, epicIid);
        if (epicIid == null) {
            throw new IllegalStateException("--epic is mandatory");
        }
        return e;
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
        int exitCode = new CommandLine(new ChildEpicScript()).execute(args);
        System.exit(exitCode);
    }
}

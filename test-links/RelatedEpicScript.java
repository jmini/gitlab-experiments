///usr/bin/env jbang "$0" "$@" ; exit $?

//DEPS info.picocli:picocli:4.6.3
//DEPS org.gitlab4j:gitlab4j-api:5.3.0
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
import org.gitlab4j.api.models.Epic;
import org.gitlab4j.api.models.EpicInLink;
import org.gitlab4j.api.models.Group;
import org.gitlab4j.api.models.LinkType;
import org.gitlab4j.api.models.RelatedEpic;
import org.gitlab4j.api.models.RelatedEpicLink;

import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

@Command(name = "RelatedEpicScript", mixinStandardHelpOptions = true, version = "RelatedEpicScript 0.1", description = "Tests with related epics in GitLab")
class RelatedEpicScript implements Callable<Integer> {

    private static final String CONFIG_FILE_INITIAL_CONTENT = """
            GITLAB_URL=https://gitlab.com
            GITLAB_AUTH_VALUE=
            """;

    @Parameters(index = "0", description = "The group id or group path")
    private String group;

    @Parameters(index = "1", description = "action to execute", defaultValue = "PRINT_GROUP")
    private Action action;

    @Option(names = { "-e", "--epic-iid" }, description = "epic iid")
    private Long epicIid;

    @Option(names = { "-g", "--related-epic-group-id" }, description = "related epic group id")
    private Long relatedEpicGroupId;

    @Option(names = { "-r", "--related-epic-iid" }, description = "related epic iid")
    private Long relatedEpicIid;

    @Option(names = { "-t", "--related-epic-link-type" }, description = "related epic link type")
    private LinkType relatedEpicLinkType;

    @Option(names = { "-i", "--related-epic-link-id" }, description = "related epic link id")
    private Long relatedEpicLinkId;

    @Option(names = { "-c", "--config" }, description = "configuration file location")
    String configFile;

    private static enum Action {
        PRINT_GROUP, CREATE_NEW_EPIC, PRINT_EPIC, ASSIGN_RELATED_EPIC, DELETE_RELATED_EPIC
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
                List<RelatedEpic> children = gitLabApi.getEpicsApi()
                        .getRelatedEpics(g, epicIid);
                printRelatedEpicScripts(children);
                break;
            case ASSIGN_RELATED_EPIC:
                if (relatedEpicGroupId == null) {
                    throw new IllegalStateException("--related-epic-group-id is mandatory");
                }
                if (relatedEpicIid == null) {
                    throw new IllegalStateException("--related-epic-iid is mandatory");
                }
                e = readEpic(gitLabApi, g);
                RelatedEpicLink createRelatedEpicLink = gitLabApi.getEpicsApi()
                        .createRelatedEpicLink(g, epicIid, relatedEpicGroupId, relatedEpicIid, relatedEpicLinkType);
                printEpicLink(createRelatedEpicLink);
                System.out.println(createRelatedEpicLink);
                break;
            case DELETE_RELATED_EPIC:
                if (relatedEpicLinkId == null) {
                    throw new IllegalStateException("--related-epic-link-id is mandatory");
                }
                RelatedEpicLink childEpic = gitLabApi.getEpicsApi()
                        .deleteRelatedEpicLink(g, epicIid, relatedEpicLinkId);
                printEpicLink(childEpic);
                System.out.println(childEpic);
                break;
            }
        }
        return 0;
    }

    private void printRelatedEpicScripts(List<RelatedEpic> children) {
        System.out.println("== number of related epics: " + children.size());
        for (RelatedEpic child : children) {
            printEpic(child);
        }
    }

    private void printEpic(Epic e) {
        System.out.println("id: " + e.getId() + ", iid: " + e.getIid() + ", title: " + e.getTitle() + ", webUrl: " + e.getWebUrl());
    }

    private void printEpic(RelatedEpic e) {
        System.out.println("id: " + e.getId() + ", iid: " + e.getIid() + ", title: " + e.getTitle() + ", webUrl: " + e.getWebUrl() + ", linkType: " + e.getLinkType());
    }

    private void printEpicLink(RelatedEpicLink e) {
        EpicInLink sourceEpic = e.getSourceEpic();
        EpicInLink targetEpic = e.getTargetEpic();
        System.out.println("id: " + e.getId() + ", link_type: " + e.getLinkType() + ", source.group_id: " + sourceEpic.getGroupId() + ", source.epic_iid: " + sourceEpic.getIid() + ", source.title: " + sourceEpic.getTitle()
                + ", target.group_id: " + targetEpic.getGroupId() + ", target.epic_iid: " + targetEpic.getIid() + ", target.title: " + targetEpic.getTitle());
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
        int exitCode = new CommandLine(new RelatedEpicScript()).execute(args);
        System.exit(exitCode);
    }
}

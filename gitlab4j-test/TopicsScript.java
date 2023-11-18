///usr/bin/env jbang "$0" "$@" ; exit $?

//DEPS info.picocli:picocli:4.6.3
//DEPS org.gitlab4j:gitlab4j-api:5.4.0
//JAVA 17

import java.io.File;
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
import org.gitlab4j.api.models.Topic;
import org.gitlab4j.api.models.TopicParams;

import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

@Command(name = "TopicsScript", mixinStandardHelpOptions = true, version = "TopicsScript 0.1", description = "Tests with related epics in GitLab")
class TopicsScript implements Callable<Integer> {

    private static final String CONFIG_FILE_INITIAL_CONTENT = """
            GITLAB_URL=https://gitlab.com
            GITLAB_AUTH_VALUE=
            """;

    @Parameters(index = "0", description = "action to execute", defaultValue = "PRINT_TOPIC")
    private Action action;

    @Option(names = { "-i", "--topic-id" }, description = "topic id")
    private Integer topicId;

    @Option(names = { "-n", "--name" }, description = "topic name")
    private String name;

    @Option(names = { "-d", "--description" }, description = "topic description")
    private String description;

    @Option(names = { "-t", "--title" }, description = "topic title")
    private String title;

    @Option(names = { "-a", "--avatarFile" }, description = "topic title")
    private File avatarFile;

    @Option(names = { "-c", "--config" }, description = "configuration file location")
    String configFile;

    private static enum Action {
        PRINT_TOPIC, PRINT_TOPICS, CREATE_TOPIC, UPDATE_TOPIC, UPDATE_TOPIC_AVATAR, DELETE_TOPIC_AVATAR, DELETE_TOPIC
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
            System.out.println("Action " + action + " ...");

            Topic topic;
            TopicParams params;
            switch (action) {
            case PRINT_TOPICS:
                List<Topic> topics = gitLabApi.getTopicsApi()
                        .getTopics();
                for (Topic t : topics) {
                    printTopic(t);
                }
                break;
            case PRINT_TOPIC:
                topicIdMandatory();
                topic = gitLabApi.getTopicsApi()
                        .getTopic(topicId);
                // printTopic(topic);
                System.out.println(topic);
                break;
            case CREATE_TOPIC:
                params = createParam();
                topic = gitLabApi.getTopicsApi()
                        .createTopic(params);
                System.out.println(topic);
                break;
            case UPDATE_TOPIC:
                topicIdMandatory();
                params = createParam();
                topic = gitLabApi.getTopicsApi()
                        .updateTopic(topicId, params);
                System.out.println(topic);
                break;
            case DELETE_TOPIC:
                topicIdMandatory();
                gitLabApi.getTopicsApi()
                        .deleteTopic(topicId);
                break;
            case UPDATE_TOPIC_AVATAR:
                topicIdMandatory();
                avatarFileMandatory();
                topic = gitLabApi.getTopicsApi()
                        .updateTopicAvatar(topicId, avatarFile);
                System.out.println(topic);
                break;
            case DELETE_TOPIC_AVATAR:
                topicIdMandatory();
                topic = gitLabApi.getTopicsApi()
                        .deleteTopicAvatar(topicId);
                System.out.println(topic);
                break;
            default:
                break;
            }
        }
        return 0;
    }

    private void topicIdMandatory() {
        if (topicId == null) {
            throw new IllegalStateException("Topic id is mandatory");
        }
    }

    private void avatarFileMandatory() {
        if (avatarFile == null) {
            throw new IllegalStateException("AvatarFile id is mandatory");
        }
    }

    private TopicParams createParam() {
        TopicParams params = new TopicParams();
        if (name != null) {
            params.withName(name);
        }
        if (description != null) {
            params.withDescription(description);
        }
        if (title != null) {
            params.withTitle(title);
        }
        return params;
    }

    private void printTopic(Topic topic) {
        System.out.println("Topic " + topic.getId() + " - " + topic.getName());
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
        int exitCode = new CommandLine(new TopicsScript()).execute(args);
        System.exit(exitCode);
    }
}

///usr/bin/env jbang "$0" "$@" ; exit $?

//DEPS info.picocli:picocli:4.6.3
//DEPS https://github.com/unblu/gitlab-workitem-graphql-client/commit/42111c3e6f4fe6a3970695e578d70a2169e330ab
//DEPS io.smallrye:smallrye-graphql-client-implementation-vertx:2.11.0
//DEPS org.jboss.logmanager:jboss-logmanager:3.1.1.Final
//JAVA 17
//RUNTIME_OPTIONS -Djava.util.logging.manager=org.jboss.logmanager.LogManager
//FILES logging.properties

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.Callable;

import graphql.gitlab.api.WorkitemClientApi;
import graphql.gitlab.model.WorkItemConnection;
import io.smallrye.graphql.client.typesafe.api.TypesafeGraphQLClientBuilder;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

@Command(name = "WorkItemScript", mixinStandardHelpOptions = true, version = "WorkItemScript 0.1", description = "Tests with GraphQL")
public class WorkItemScript implements Callable<Integer> {

    private static final String CONFIG_FILE_INITIAL_CONTENT = """
            GITLAB_URL=https://gitlab.com
            GITLAB_AUTH_VALUE=
            """;

    @Parameters(index = "0", description = "action to execute", defaultValue = "GET_WORKITEM")
    private Action action;

    @Option(names = { "-n", "--namespace" }, description = "namespace path")
    private String namespace;

    @Option(names = { "-r", "--ref", "--reference" }, description = "references in the namespace")
    private List<String> refs;

    @Option(names = { "-c", "--config" }, description = "configuration file location")
    String configFile;

    @Option(names = { "-v", "--verbose" }, description = "log http trafic")
    Boolean logHttp;

    private static enum Action {
        GET_WORKITEM
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

        WorkitemClientApi api = createGraphQLWorkitemClientApi(gitLabUrl, gitLabAuthValue);
        switch (action) {
        case GET_WORKITEM:
            ensureExists(namespace, "namespace");
            ensureExists(refs, "reference");
            WorkItemConnection response = api.workItemsByReference(namespace, refs, null);
            System.out.println(response);
            break;
        default:
            throw new IllegalArgumentException("Unexpected value: " + action);
        }

        return 0;
    }

    static WorkitemClientApi createGraphQLWorkitemClientApi(String gitLabUrl, String gitlabToken) {
        WorkitemClientApi gqlApi = TypesafeGraphQLClientBuilder.newBuilder()
                .endpoint(gitLabUrl + "/api/graphql")
                .header("Authorization", "Bearer " + gitlabToken)
                .allowUnexpectedResponseFields(true)
                .build(WorkitemClientApi.class);
        return gqlApi;
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
        final int exitCode = new CommandLine(new WorkItemScript()).execute(args);
        System.exit(exitCode);
    }
}

///usr/bin/env jbang "$0" "$@" ; exit $?

//DEPS info.picocli:picocli:4.6.3
//DEPS https://github.com/unblu/gitlab-workitem-graphql-client/commit/ad522587ca5c34f9423d34b5ab7dc20b2ab5e05e
//xxDEPS com.unblu.gitlab:gitlab-workitem-graphql-client:1.0.0-SNAPSHOT
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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.Callable;
import java.util.function.Function;
import java.util.stream.Collectors;

import graphql.gitlab.api.WorkitemClientApi;
import graphql.gitlab.model.*;
import io.smallrye.graphql.client.typesafe.api.TypesafeGraphQLClientBuilder;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

@Command(name = "MergeRequestGraphQLScript", mixinStandardHelpOptions = true, version = "MergeRequestGraphQLScript 0.1", description = "Tests with GraphQL")
public class MergeRequestGraphQLScript implements Callable<Integer> {

    private static final String CONFIG_FILE_INITIAL_CONTENT = """
            GITLAB_URL=https://gitlab.com
            GITLAB_AUTH_VALUE=
            """;

    @Parameters(index = "0", description = "action to execute", defaultValue = "GET_MERGE_REQUEST")
    private Action action;

    @Option(names = { "-i", "--id" }, description = "merge request id")
    private String id;

    @Option(names = { "-p", "--project" }, description = "project")
    private String project;

    @Option(names = { "-r",  "--iid", "--ref", "--reference" }, description = "iids of the MRs in the project")
    private List<String> iids;

    @Option(names = { "-c", "--config" }, description = "configuration file location")
    String configFile;

    private static enum Action {
        GET_MERGE_REQUEST, GET_MERGE_REQUEST_IN_PROJECT
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
        case GET_MERGE_REQUEST:
            getMergeRequest(api);
            break;
        case GET_MERGE_REQUEST_IN_PROJECT:
            getMergeRequestInProject(api);
            break;
        default:
            throw new IllegalArgumentException("Unexpected value: " + action);
        }

        return 0;
    }

    private void getMergeRequest(WorkitemClientApi api) {
        ensureExists(id, "id");
        var mr = api.getMergeRequest(new MergeRequestID(id), NotesFilterType.ONLY_COMMENTS);
        printMr(mr);
    }

    private void getMergeRequestInProject(WorkitemClientApi api) {
        ensureExists(project, "project");
        ensureExists(iids, "iid");
        var p = api.getMergeRequestsInProject(project, iids, NotesFilterType.ONLY_COMMENTS);
        for (MergeRequest mr : p.getMergeRequests().getNodes()) {
            printMr(mr);
        }
    }

    private void printMr(MergeRequest mr) {
        //System.out.println("MergeRequest: " + mr);
        System.out.println("===");
        System.out.println("MergeRequest title: " + mr.getTitle());
        System.out.println("MergeRequest notes: " + mr.getNotes().getNodes().size());
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

    private void ensureOneExists(Holder... values) {
        List<Holder> list = Arrays.stream(values)
                .filter(h -> h.value != null)
                .toList();
        if (list.isEmpty()) {
            String names = Arrays.stream(values)
                    .map(h -> "--" + h.name)
                    .collect(Collectors.joining(", "));
            throw new IllegalStateException("One of " + names + " must be set");
        }
        if (list.size() > 1) {
            String names = list.stream()
                    .map(h -> "--" + h.name)
                    .collect(Collectors.joining(", "));
            throw new IllegalStateException("Not all of " + names + " can be set at the same time");

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
        final int exitCode = new CommandLine(new MergeRequestGraphQLScript()).execute(args);
        System.exit(exitCode);
    }

    public static class Holder {
        private Object value;
        private String name;

        public Holder(Object value, String name) {
            super();
            this.value = value;
            this.name = name;
        }

        public static Holder of(Object value, String name) {
            return new Holder(value, name);
        }
    }
}

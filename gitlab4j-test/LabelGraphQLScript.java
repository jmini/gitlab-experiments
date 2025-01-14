///usr/bin/env jbang "$0" "$@" ; exit $?

//DEPS info.picocli:picocli:4.6.3
//DEPS https://github.com/unblu/gitlab-workitem-graphql-client/commit/f98e830badf88a74e459807f14c7db3b4a32c012
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
import graphql.gitlab.model.Group;
import graphql.gitlab.model.Label;
import graphql.gitlab.model.LabelConnection;
import graphql.gitlab.model.Project;
import io.smallrye.graphql.client.typesafe.api.TypesafeGraphQLClientBuilder;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

@Command(name = "LabelGraphQLScript", mixinStandardHelpOptions = true, version = "LabelGraphQLScript 0.1", description = "Tests with GraphQL")
public class LabelGraphQLScript implements Callable<Integer> {

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

    @Option(names = { "-a", "--includeAncestorGroups" }, description = "include ancestor groups when fetching labels in project")
    private boolean includeAncestorGroups;

    @Option(names = { "-d", "--includeDescendantGroups" }, description = "include descendant groups when fetching labels in group")
    private boolean includeDescendantGroups;

    @Option(names = { "-o", "--onlyGroupLabels" }, description = "set onlyGroupLabels when fetching labels in group")
    private boolean onlyGroupLabels;

    @Option(names = { "-c", "--config" }, description = "configuration file location")
    String configFile;

    private static enum Action {
        LIST_LABELS
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
        case LIST_LABELS:
            listLabels(api);
            break;
        default:
            throw new IllegalArgumentException("Unexpected value: " + action);
        }

        return 0;
    }

    private void listLabels(WorkitemClientApi api) {
        ensureNamespace();
        List<Label> labels;
        if (project != null) {
            System.out.println("Reading gitlab labels and metadata from project " + project);
            Function<String, Project> apiCall = (from) -> api.project(project, includeAncestorGroups, from);
            Function<Project, LabelConnection> lGetter = Project::getLabels;
            labels = getLabels(apiCall, lGetter);
        } else if (group != null) {
            System.out.println("Reading gitlab labels and metadata from group " + group);
            Function<String, Group> apiCall = (from) -> api.group(group, includeDescendantGroups, onlyGroupLabels, from);
            Function<Group, LabelConnection> lGetter = Group::getLabels;
            labels = getLabels(apiCall, lGetter);
        } else {
            throw new IllegalStateException("Unexpected");
        }

        System.out.println("---> labels found: " + labels.size());

        for (Label label : labels) {
            System.out.println(label.getId() + " " + label.getTitle());
        }
    }

    private static <R> ArrayList<Label> getLabels(Function<String, R> apiCall, Function<R, LabelConnection> labelsGetter) {
        ArrayList<Label> result = new ArrayList<Label>();
        R response = apiCall.apply(null);
        LabelConnection labels = labelsGetter.apply(response);
        result.addAll(labels.getNodes());
        while (labels.getPageInfo()
                .getHasNextPage()) {
            response = apiCall.apply(labels.getPageInfo()
                    .getEndCursor());
            labels = labelsGetter.apply(response);
            result.addAll(labels.getNodes());
        }
        return result;
    }

    private void ensureNamespace() {
        ensureOneExists(Holder.of(project, "project"), Holder.of(group, "group"));
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
        final int exitCode = new CommandLine(new LabelGraphQLScript()).execute(args);
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

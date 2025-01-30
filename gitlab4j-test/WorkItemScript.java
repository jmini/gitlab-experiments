///usr/bin/env jbang "$0" "$@" ; exit $?

//DEPS info.picocli:picocli:4.6.3
//DEPS https://github.com/unblu/gitlab-workitem-graphql-client/commit/c9329cfc1c6fb35da6862869f3135045f7f08574
//DEPS patched.unblu.io.smallrye:smallrye-graphql-client-implementation-vertx:2.12.2-unblu-2
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
import graphql.gitlab.model.Date;
import graphql.gitlab.model.WorkItem;
import graphql.gitlab.model.WorkItemConnection;
import graphql.gitlab.model.WorkItemDeleteInput;
import graphql.gitlab.model.WorkItemDeletePayload;
import graphql.gitlab.model.WorkItemID;
import graphql.gitlab.model.WorkItemUpdateInput;
import graphql.gitlab.model.WorkItemUpdatePayload;
import graphql.gitlab.model.WorkItemWidgetHierarchyUpdateInputWithChildren;
import graphql.gitlab.model.WorkItemWidgetHierarchyUpdateInputWithParent;
import graphql.gitlab.model.WorkItemWidgetStartAndDueDateUpdateInput;
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

    @Option(names = { "-i", "--id" }, description = "workitem id")
    private String id;

    @Option(names = { "-p", "--parentId" }, description = "workitem parentId")
    private String parentId;

    @Option(names = { "-s", "--childId" }, description = "workitem childrenIds")
    private List<String> childrenIds;

    @Option(names = { "-d", "--dueDate" }, description = "workitem dueDate")
    private String dueDate;

    @Option(names = { "-r", "--ref", "--reference" }, description = "references in the namespace")
    private List<String> refs;

    @Option(names = { "-c", "--config" }, description = "configuration file location")
    String configFile;

    @Option(names = { "-v", "--verbose" }, description = "log http trafic")
    Boolean logHttp;

    private static enum Action {
        GET_WORKITEM, DELETE_WORKITEM, ADD_PARENT, REMOVE_PARENT, ADD_CHILDREN, UPDATE_DUE_DATE
    }

    @Override
    public Integer call() throws Exception {
        Path file;
        if (configFile != null) {
            file = Paths.get(configFile);
        } else {
            file = configFile(Paths.get(""));
        }
        final String gitLabUrl;
        final String gitLabAuthValue;
        if (Files.isRegularFile(file)) {
            System.out.println("Reading config: " + file.toAbsolutePath());
            final Properties prop = configProperties(file);
            gitLabUrl = readProperty(prop, "GITLAB_URL", "https://gitlab.com");
            gitLabAuthValue = readProperty(prop, "GITLAB_AUTH_VALUE");
        } else {
            System.out.println("Config file does not exists: " + file.toAbsolutePath());
            gitLabUrl = "https://gitlab.com";
            gitLabAuthValue = null;
        }

        WorkitemClientApi api = createGraphQLWorkitemClientApi(gitLabUrl, gitLabAuthValue);

        switch (action) {
        case GET_WORKITEM:
            getWorkItem(api);
            break;
        case DELETE_WORKITEM:
            deleteWorkItem(api);
            break;
        case ADD_PARENT:
            addParent(api);
            break;
        case REMOVE_PARENT:
            deleteParent(api);
            break;
        case ADD_CHILDREN:
            addChildren(api);
            break;
        case UPDATE_DUE_DATE:
            updateDueDate(api);
            break;
        default:
            throw new IllegalArgumentException("Unexpected value: " + action);
        }

        return 0;
    }

    private void getWorkItem(WorkitemClientApi api) {
        if (id != null) {
            WorkItem response = api.workItem(new WorkItemID(id));
            //System.out.println(response);
            printWorkItem(response);
        } else {
            ensureExists(namespace, "namespace");
            ensureExists(refs, "reference");
            WorkItemConnection response = api.workItemsByReference(namespace, refs, null);
            System.out.println(response);
        }
    }

    private void deleteWorkItem(WorkitemClientApi api) {
        ensureExists(id, "id");
        WorkItemDeletePayload response = api.workItemDelete(new WorkItemDeleteInput()
                .setId(new WorkItemID(id)) //
        );
        System.out.println(response);
    }

    private void addParent(WorkitemClientApi api) {
        ensureExists(id, "id");
        ensureExists(parentId, "parentId");
        WorkItemUpdatePayload response = api.workItemUpdate(new WorkItemUpdateInput()
                .setId(new WorkItemID(id))
                .setHierarchyWidget(new WorkItemWidgetHierarchyUpdateInputWithParent() //
                        .setParentId(new WorkItemID(parentId)) //
                ) //
        );
        System.out.println(response);
    }

    private void deleteParent(WorkitemClientApi api) {
        ensureExists(id, "id");
        WorkItemUpdatePayload response = api.workItemUpdate(new WorkItemUpdateInput()
                .setId(new WorkItemID(id))
                .setHierarchyWidget(new WorkItemWidgetHierarchyUpdateInputWithParent().setParentId(null)));
        System.out.println(response);
    }

    private void updateDueDate(WorkitemClientApi api) {
        ensureExists(id, "id");
        WorkItemWidgetStartAndDueDateUpdateInput input = new WorkItemWidgetStartAndDueDateUpdateInput()
                .setIsFixed(true);
        if (dueDate != null) {
            input.setDueDate(new Date(dueDate));
        }
        WorkItemUpdateInput request = new WorkItemUpdateInput()
                .setId(new WorkItemID(id))
                .setStartAndDueDateWidget(input);
        WorkItemUpdatePayload updateResponse = api.workItemUpdate(request);
        System.out.println(updateResponse);
    }

    private void addChildren(WorkitemClientApi api) {
        ensureExists(id, "id");
        ensureExists(childrenIds, "childId");
        WorkItemUpdatePayload response = api.workItemUpdate(new WorkItemUpdateInput()
                .setId(new WorkItemID(id))
                .setHierarchyWidget(new WorkItemWidgetHierarchyUpdateInputWithChildren() //
                        .setChildrenIds(childrenIds.stream()
                                .map(WorkItemID::new)
                                .toList()) //
                ) //
        );
        System.out.println(response);
    }

    private void printWorkItem(WorkItem item) {
        System.out.println(item.getId() + " " + item.getTitle() + " " + item.getWebUrl());
    }

    static WorkitemClientApi createGraphQLWorkitemClientApi(String gitLabUrl, String gitlabToken) {
        TypesafeGraphQLClientBuilder builder = TypesafeGraphQLClientBuilder.newBuilder()
                .endpoint(gitLabUrl + "/api/graphql")
                .allowUnexpectedResponseFields(true);
        if (gitlabToken != null) {
            builder.header("Authorization", "Bearer " + gitlabToken);
        }

        return builder.build(WorkitemClientApi.class);
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

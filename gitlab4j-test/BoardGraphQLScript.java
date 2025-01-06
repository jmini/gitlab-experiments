///usr/bin/env jbang "$0" "$@" ; exit $?

//DEPS info.picocli:picocli:4.6.3
//DEPS https://github.com/unblu/gitlab-workitem-graphql-client/commit/f36726404b311bab0d5df41eff8072950fb9f97c
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
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.Callable;
import java.util.stream.Collectors;

import graphql.gitlab.api.WorkitemClientApi;
import graphql.gitlab.model.BoardID;
import graphql.gitlab.model.BoardList;
import graphql.gitlab.model.BoardListCreateInput;
import graphql.gitlab.model.BoardListCreatePayload;
import graphql.gitlab.model.BoardsEpicBoardID;
import graphql.gitlab.model.BoardsEpicListID;
import graphql.gitlab.model.CreateBoardInput;
import graphql.gitlab.model.CreateBoardPayload;
import graphql.gitlab.model.DestroyBoardInput;
import graphql.gitlab.model.DestroyBoardListInput;
import graphql.gitlab.model.DestroyBoardListPayload;
import graphql.gitlab.model.DestroyBoardPayload;
import graphql.gitlab.model.DestroyEpicBoardInput;
import graphql.gitlab.model.DestroyEpicBoardPayload;
import graphql.gitlab.model.EpicBoardCreateInput;
import graphql.gitlab.model.EpicBoardCreatePayload;
import graphql.gitlab.model.EpicBoardListCreateInput;
import graphql.gitlab.model.EpicBoardListCreatePayload;
import graphql.gitlab.model.EpicBoardListDestroyInput;
import graphql.gitlab.model.EpicBoardListDestroyPayload;
import graphql.gitlab.model.EpicBoardUpdateInput;
import graphql.gitlab.model.EpicBoardUpdatePayload;
import graphql.gitlab.model.EpicList;
import graphql.gitlab.model.GroupContainingEpicBoard;
import graphql.gitlab.model.GroupContainingIssueBoard;
import graphql.gitlab.model.LabelID;
import graphql.gitlab.model.ListID;
import graphql.gitlab.model.ProjectContainingIssueBoard;
import graphql.gitlab.model.UpdateBoardInput;
import graphql.gitlab.model.UpdateBoardListInput;
import graphql.gitlab.model.UpdateBoardListPayload;
import graphql.gitlab.model.UpdateBoardPayload;
import graphql.gitlab.model.UpdateEpicBoardListInput;
import graphql.gitlab.model.UpdateEpicBoardListPayload;
import graphql.gitlab.model.UserID;
import io.smallrye.graphql.client.typesafe.api.TypesafeGraphQLClientBuilder;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

@Command(name = "BoardGraphQLScript", mixinStandardHelpOptions = true, version = "BoardGraphQLScript 0.1", description = "Tests with GraphQL")
public class BoardGraphQLScript implements Callable<Integer> {

    private static final String CONFIG_FILE_INITIAL_CONTENT = """
            GITLAB_URL=https://gitlab.com
            GITLAB_AUTH_VALUE=
            """;

    @Parameters(index = "0", description = "action to execute", defaultValue = "GET_BOARDS")
    private Action action;

    @Option(names = { "-t", "--type" }, description = "type", defaultValue = "ISSUE")
    private Type type;

    @Option(names = { "-p", "--project" }, description = "project")
    private String project;

    @Option(names = { "-g", "--group" }, description = "group")
    private String group;

    @Option(names = { "-i", "--id" }, description = "board id")
    private String boardId;

    @Option(names = { "--hideBacklogList" }, description = "hide backlog List")
    private Boolean hideBacklogList;

    @Option(names = { "--hideClosedList" }, description = "hide closed List")
    private Boolean hideClosedList;

    @Option(names = { "-l", "--listId" }, description = "board list id")
    private String listId;

    @Option(names = { "-n", "--name" }, description = "name")
    private String name;

    @Option(names = { "--labelId" }, description = "label id for the board list")
    private String labelId;

    @Option(names = { "--assigneeId" }, description = "user id for the board list")
    private String assigneeId;

    @Option(names = { "--position" }, description = "position of the board list")
    private Integer position;

    @Option(names = { "-c", "--config" }, description = "configuration file location")
    String configFile;

    private static enum Action {
        GET_BOARDS, CREATE_BOARD, UPDATE_BOARD, DELETE_BOARD, GET_BOARD_LIST, CREATE_BOARD_LIST, UPDATE_BOARD_LIST, DELETE_BOARD_LIST
    }

    private static enum Type {
        ISSUE, EPIC
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
        case GET_BOARDS:
            getBoards(api);
            break;
        case CREATE_BOARD:
            createBoard(api);
            break;
        case UPDATE_BOARD:
            updateBoard(api);
            break;
        case DELETE_BOARD:
            deleteBoard(api);
            break;
        case GET_BOARD_LIST:
            getBoardList(api);
            break;
        case CREATE_BOARD_LIST:
            createBoardList(api);
            break;
        case UPDATE_BOARD_LIST:
            updateBoardList(api);
            break;
        case DELETE_BOARD_LIST:
            deleteBoardList(api);
            break;
        default:
            throw new IllegalArgumentException("Unexpected value: " + action);
        }

        return 0;
    }

    private void getBoards(WorkitemClientApi api) {
        ensureNamespace();
        if (type == Type.EPIC) {
            GroupContainingEpicBoard g = api.getEpicBoardsInGroup(group);
            System.out.println(g.getEpicBoards());
        } else if (project != null) {
            ProjectContainingIssueBoard p = api.getIssueBoardsInProject(project);
            System.out.println(p.getBoards());
        } else {
            GroupContainingIssueBoard g = api.getIssueBoardsInGroup(group);
            System.out.println(g.getBoards());
        }
    }

    private void createBoard(WorkitemClientApi api) {
        ensureNamespace();
        ensureExists(name, "name");
        if (type == Type.EPIC) {
            EpicBoardCreatePayload response = api.createEpicBoard(new EpicBoardCreateInput()
                    .setName(name)
                    .setGroupPath(group));
            System.out.println(response);
        } else {
            CreateBoardInput request = new CreateBoardInput()
                    .setName(name);
            if (project != null) {
                request.setProjectPath(project);
                CreateBoardPayload response = api.createIssueBoard(request);
                System.out.println(response);
            } else {
                request.setGroupPath(group);
                CreateBoardPayload response = api.createIssueBoard(request);
                System.out.println(response);
            }
        }
    }

    private void updateBoard(WorkitemClientApi api) {
        ensureExists(boardId, "id");
        if (type == Type.EPIC) {
            EpicBoardUpdatePayload response = api.updateEpicBoard(new EpicBoardUpdateInput()
                    .setId(new BoardsEpicBoardID(boardId))
                    .setName(name)
                    .setHideBacklogList(hideBacklogList)
                    .setHideClosedList(hideClosedList));
            System.out.println(response);
        } else {
            UpdateBoardPayload response = api.updateIssueBoard(new UpdateBoardInput()
                    .setId(new BoardID(boardId))
                    .setName(name)
                    .setHideBacklogList(hideBacklogList)
                    .setHideClosedList(hideClosedList));
            System.out.println(response);
        }
    }

    private void deleteBoard(WorkitemClientApi api) {
        ensureExists(boardId, "id");
        if (type == Type.EPIC) {
            DestroyEpicBoardPayload response = api.deleteEpicBoard(new DestroyEpicBoardInput()
                    .setId(new BoardsEpicBoardID(boardId)));
            System.out.println(response);
        } else {
            DestroyBoardPayload response = api.deleteIssueBoard(new DestroyBoardInput()
                    .setId(new BoardID(boardId)));
            System.out.println(response);
        }
        System.out.println("board " + boardId + " deleted");
    }

    private void getBoardList(WorkitemClientApi api) {
        ensureExists(listId, "listId");
        if (type == Type.EPIC) {
            EpicList l = api.getEpicBoardList(new BoardsEpicListID(listId));
            System.out.println(l);
        } else {
            BoardList l = api.getIssueBoardList(new ListID(listId));
            System.out.println(l);
        }
    }

    private void createBoardList(WorkitemClientApi api) {
        ensureExists(boardId, "id");
        if (type == Type.EPIC) {
            ensureExists(labelId, "labelId");
            EpicBoardListCreatePayload response = api.createEpicBoardList(new EpicBoardListCreateInput()
                    .setBoardId(new BoardsEpicBoardID(boardId)) //
                    .setLabelId(new LabelID(labelId)) //
            );
            System.out.println(response.getList());
        } else {
            ensureOneExists(Holder.of(labelId, "labelId"), Holder.of(assigneeId, "assigneeId"));
            BoardListCreateInput request = new BoardListCreateInput()
                    .setBoardId(new BoardID(boardId));
            if (labelId != null) {
                request.setLabelId(new LabelID(labelId));
            }
            if (assigneeId != null) {
                request.setAssigneeId(new UserID(assigneeId));
            }
            BoardListCreatePayload response = api.createIssueBoardList(request);
            System.out.println(response.getList());
        }
    }

    private void updateBoardList(WorkitemClientApi api) {
        ensureExists(listId, "listId");
        ensureExists(position, "position");
        if (type == Type.EPIC) {
            UpdateEpicBoardListPayload response = api.updateEpicBoardList(new UpdateEpicBoardListInput()
                    .setListId(new BoardsEpicListID(listId)) //
                    .setPosition(position) //
            );
            System.out.println(response.getList());
        } else {
            UpdateBoardListPayload response = api.updateIssueBoardList(new UpdateBoardListInput()
                    .setListId(new ListID(listId)) //
                    .setPosition(position) //
            );
            System.out.println(response.getList());
        }
    }

    private void deleteBoardList(WorkitemClientApi api) {
        ensureExists(listId, "listId");
        if (type == Type.EPIC) {
            EpicBoardListDestroyPayload response = api.deleteEpicBoardList(new EpicBoardListDestroyInput()
                    .setListId(new BoardsEpicListID(listId)) //
            );
            System.out.println(response.getList());
        } else {
            DestroyBoardListPayload response = api.deleteIssueBoardList(new DestroyBoardListInput()
                    .setListId(new ListID(listId)) //
            );
            System.out.println(response.getList());
        }
        System.out.println("board list " + listId + " deleted");
    }

    private void ensureNamespace() {
        if (type == Type.EPIC) {
            ensureExists(group, "group");
            if (project != null) {
                throw new IllegalStateException("Project can't be set for Epic board");
            }
        } else if (type == Type.ISSUE) {
            ensureOneExists(Holder.of(project, "project"), Holder.of(group, "group"));
        } else {
            throw new IllegalStateException("Type is unexpected");
        }
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
        final int exitCode = new CommandLine(new BoardGraphQLScript()).execute(args);
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

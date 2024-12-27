///usr/bin/env jbang "$0" "$@" ; exit $?

//DEPS info.picocli:picocli:4.6.3
//DEPS https://github.com/jmini/gitlab4j-api/commit/1629143980a2e2de4d1a7495562470a67c4062e3
//JAVA 17

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

import org.gitlab4j.api.GitLabApi;
import org.gitlab4j.api.GitLabApiException;
import org.gitlab4j.api.models.Board;
import org.gitlab4j.api.models.BoardList;

import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

@Command(name = "BoardScript", mixinStandardHelpOptions = true, version = "BoardScript 0.1", description = "Tests for GitLab4J")
public class BoardScript implements Callable<Integer> {

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
    private Long boardId;

    @Option(names = { "-l", "--listId" }, description = "board list id")
    private Long listId;

    @Option(names = { "-n", "--name" }, description = "name")
    private String name;

    @Option(names = { "--labelId" }, description = "label id for the board list")
    private Long labelId;

    @Option(names = { "--assigneeId" }, description = "user id for the board list")
    private Long assigneeId;

    @Option(names = { "--position" }, description = "position of the board list")
    private Integer position;

    @Option(names = { "-c", "--config" }, description = "configuration file location")
    String configFile;

    @Option(names = { "-v", "--verbose" }, description = "log http trafic")
    Boolean logHttp;

    private static enum Action {
        GET_BOARDS, GET_BOARD, CREATE_BOARD, UPDATE_BOARD, DELETE_BOARD, GET_BOARD_LISTS, GET_BOARD_LIST, CREATE_BOARD_LIST, UPDATE_BOARD_LIST, DELETE_BOARD_LIST
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

        try (GitLabApi gitLabApi = createGitLabApi(gitLabUrl, gitLabAuthValue)) {
            switch (action) {
            case GET_BOARDS:
                List<Board> boards;
                if (project != null) {
                    boards = gitLabApi.getBoardsApi()
                            .getProjectIssueBoards(idOrPath(project));
                } else if (type == Type.ISSUE) {
                    boards = gitLabApi.getBoardsApi()
                            .getGroupIssueBoards(idOrPath(group));
                } else {
                    boards = gitLabApi.getBoardsApi()
                            .getGroupEpicBoards(idOrPath(group));
                }
                System.out.println(boards);
                break;
            case GET_BOARD:
                ensureExists(boardId, "id");
                Board board;
                if (project != null) {
                    board = gitLabApi.getBoardsApi()
                            .getProjectIssueBoard(idOrPath(project), boardId);
                } else if (type == Type.ISSUE) {
                    board = gitLabApi.getBoardsApi()
                            .getGroupIssueBoard(idOrPath(group), boardId);
                } else {
                    board = gitLabApi.getBoardsApi()
                            .getGroupEpicBoard(idOrPath(group), boardId);
                }
                System.out.println(board);
                break;
            case CREATE_BOARD:
                ensureExists(name, "name");
                Board created;
                if (project != null) {
                    created = gitLabApi.getBoardsApi()
                            .createProjectIssueBoard(idOrPath(project), name);
                } else if (type == Type.ISSUE) {
                    created = gitLabApi.getBoardsApi()
                            .createGroupIssueBoard(idOrPath(group), name);
                } else {
                    throw new IllegalStateException("Not implemented at server side");
                }
                System.out.println(created);
                break;
            case UPDATE_BOARD:
                ensureExists(boardId, "id");
                Board updated = updateBoard(gitLabApi);
                System.out.println(updated);
                break;
            case DELETE_BOARD:
                ensureExists(boardId, "id");
                if (project != null) {
                    gitLabApi.getBoardsApi()
                            .deleteProjectIssueBoard(idOrPath(project), boardId);
                } else if (type == Type.ISSUE) {
                    gitLabApi.getBoardsApi()
                            .deleteGroupIssueBoard(idOrPath(group), boardId);
                } else {
                    throw new IllegalStateException("Not implemented at server side");
                }
                System.out.println("board " + boardId + " deleted");
                break;
            case GET_BOARD_LISTS:
                ensureExists(boardId, "id");
                List<BoardList> boardLists;
                if (project != null) {
                    boardLists = gitLabApi.getBoardsApi()
                            .getProjectIssueBoardLists(idOrPath(project), boardId);
                } else if (type == Type.ISSUE) {
                    boardLists = gitLabApi.getBoardsApi()
                            .getGroupIssueBoardLists(idOrPath(group), boardId);
                } else {
                    boardLists = gitLabApi.getBoardsApi()
                            .getGroupEpicBoardLists(idOrPath(group), boardId);
                }
                System.out.println(boardLists);
                break;
            case GET_BOARD_LIST:
                ensureExists(boardId, "id");
                ensureExists(listId, "listId");
                BoardList boardList;
                if (project != null) {
                    boardList = gitLabApi.getBoardsApi()
                            .getProjectIssueBoardList(idOrPath(project), boardId, listId);
                } else if (type == Type.ISSUE) {
                    boardList = gitLabApi.getBoardsApi()
                            .getGroupIssueBoardList(idOrPath(group), boardId, listId);
                } else {
                    boardList = gitLabApi.getBoardsApi()
                            .getGroupEpicBoardList(idOrPath(group), boardId, listId);
                }
                System.out.println(boardList);
                break;
            case CREATE_BOARD_LIST:
                ensureExists(boardId, "id");
                BoardList createdList = createList(gitLabApi);
                System.out.println(createdList);
                break;
            case UPDATE_BOARD_LIST:
                ensureExists(boardId, "id");
                ensureExists(listId, "listId");
                ensureExists(position, "position");
                BoardList updatedList;
                if (project != null) {
                    updatedList = gitLabApi.getBoardsApi()
                            .updateProjectIssueBoardList(idOrPath(project), boardId, listId, position);
                } else if (type == Type.ISSUE) {
                    updatedList = gitLabApi.getBoardsApi()
                            .updateGroupIssueBoardList(idOrPath(group), boardId, listId, position);
                } else {
                    throw new IllegalStateException("Not implemented at server side");
                }
                System.out.println(updatedList);
                break;
            case DELETE_BOARD_LIST:
                ensureExists(boardId, "id");
                ensureExists(listId, "listId");
                if (project != null) {
                    gitLabApi.getBoardsApi()
                            .deleteProjectIssueBoardList(idOrPath(project), boardId, listId);
                } else if (type == Type.ISSUE) {
                    gitLabApi.getBoardsApi()
                            .deleteGroupIssueBoardList(idOrPath(group), boardId, listId);
                } else {
                    throw new IllegalStateException("Not implemented at server side");
                }
                System.out.println("board list " + listId + " in board " + boardId + " deleted");
                break;
            default:
                throw new IllegalArgumentException("Unexpected value: " + action);
            }
        }
        return 0;
    }

    private BoardList createList(GitLabApi gitLabApi) throws GitLabApiException {
        Long milestoneId = null;
        Long iterationId = null;
        ensureOneExists(Holder.of(labelId, "labelId"), Holder.of(assigneeId, "assigneeId"));
        if (project != null) {
            return gitLabApi.getBoardsApi()
                    .createProjectIssueBoardList(idOrPath(project), boardId, labelId, assigneeId, milestoneId, iterationId);
        } else if (type == Type.ISSUE) {
            return gitLabApi.getBoardsApi()
                    .createGroupIssueBoardList(idOrPath(group), boardId, labelId, assigneeId, milestoneId, iterationId);
        } else {
            throw new IllegalStateException("Not implemented at server side");
        }
    }

    private Board updateBoard(GitLabApi gitLabApi) throws GitLabApiException {
        Long assigneeId = null;
        Long milestoneId = null;
        String labels = null;
        Integer weight = null;
        Boolean hideBacklogList = null;
        Boolean hideClosedList = null;
        if (project != null) {
            return gitLabApi.getBoardsApi()
                    .updateProjectIssueBoard(idOrPath(project), boardId, name, hideBacklogList, hideClosedList, assigneeId, milestoneId, labels, weight);
        } else if (type == Type.ISSUE) {
            return gitLabApi.getBoardsApi()
                    .updateGroupIssueBoard(idOrPath(group), boardId, name, hideBacklogList, hideClosedList, assigneeId, milestoneId, labels, weight);
        } else {
            throw new IllegalStateException("Not implemented at server side");
        }
    }

    private GitLabApi createGitLabApi(String gitLabUrl, String gitLabAuthValue) {
        if (logHttp != null && logHttp) {
            return new GitLabApi(gitLabUrl, gitLabAuthValue)
                    .withRequestResponseLogging(java.util.logging.Level.INFO);
        }
        return new GitLabApi(gitLabUrl, gitLabAuthValue);
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
        final int exitCode = new CommandLine(new BoardScript()).execute(args);
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

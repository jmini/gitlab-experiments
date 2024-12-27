///usr/bin/env jbang "$0" "$@" ; exit $?

//DEPS info.picocli:picocli:4.6.3
//DEPS https://github.com/jmini/gitlab4j-api/commit/efc91b8de2e6acc5fd198d092104fbb5016951e7
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

    @Option(names = { "--position" }, description = "position of the board list")
    private Integer position;

    @Option(names = { "-c", "--config" }, description = "configuration file location")
    String configFile;

    @Option(names = { "-v", "--verbose" }, description = "log http trafic")
    Boolean logHttp;

    private static enum Action {
        GET_BOARDS, GET_BOARD, CREATE_BOARD, UPDATE_BOARD, DELETE_BOARD, GET_BOARD_LISTS, GET_BOARD_LIST, CREATE_BOARD_LIST, UPDATE_BOARD_LIST, DELETE_BOARD_LIST
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

        if (project == null && group == null) {
            throw new IllegalStateException("Project or group is mandatory");
        } else if (project != null && group != null) {
            throw new IllegalStateException("Project and group can't be set at the same time");
        }

        try (GitLabApi gitLabApi = createGitLabApi(gitLabUrl, gitLabAuthValue)) {
            switch (action) {
            case GET_BOARDS:
                List<Board> boards;
                if (project != null) {
                    boards = gitLabApi.getBoardsApi()
                            .getBoards(idOrPath(project));
                } else {
                    boards = gitLabApi.getBoardsApi()
                            .getGroupBoards(idOrPath(group));
                }
                System.out.println(boards);
                break;
            case GET_BOARD:
                ensureExists(boardId, "id");
                Board board;
                if (project != null) {
                    board = gitLabApi.getBoardsApi()
                            .getBoard(idOrPath(project), boardId);
                } else {
                    board = gitLabApi.getBoardsApi()
                            .getGroupBoard(idOrPath(group), boardId);
                }
                System.out.println(board);
                break;
            case CREATE_BOARD:
                ensureExists(name, "name");
                Board created;
                if (project != null) {
                    created = gitLabApi.getBoardsApi()
                            .createBoard(idOrPath(project), name);
                } else {
                    created = gitLabApi.getBoardsApi()
                            .createGroupBoard(idOrPath(group), name);
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
                            .deleteBoard(idOrPath(project), boardId);
                } else {
                    gitLabApi.getBoardsApi()
                            .deleteGroupBoard(idOrPath(group), boardId);
                }
                System.out.println("board " + boardId + " deleted");
                break;
            case GET_BOARD_LISTS:
                ensureExists(boardId, "id");
                List<BoardList> boardLists;
                if (project != null) {
                    boardLists = gitLabApi.getBoardsApi()
                            .getBoardLists(idOrPath(project), boardId);
                } else {
                    boardLists = gitLabApi.getBoardsApi()
                            .getGroupBoardLists(idOrPath(group), boardId);
                }
                System.out.println(boardLists);
                break;
            case GET_BOARD_LIST:
                ensureExists(boardId, "id");
                ensureExists(listId, "listId");
                BoardList boardList;
                if (project != null) {
                    boardList = gitLabApi.getBoardsApi()
                            .getBoardList(idOrPath(project), boardId, listId);
                } else {
                    boardList = gitLabApi.getBoardsApi()
                            .getGroupBoardList(idOrPath(group), boardId, listId);
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
                            .updateBoardList(idOrPath(project), boardId, listId, position);
                } else {
                    updatedList = gitLabApi.getBoardsApi()
                            .updateGroupBoardList(idOrPath(group), boardId, listId, position);
                }
                System.out.println(updatedList);
                break;
            case DELETE_BOARD_LIST:
                ensureExists(boardId, "id");
                ensureExists(listId, "listId");
                if (project != null) {
                    gitLabApi.getBoardsApi()
                            .deleteBoardList(idOrPath(project), boardId, listId);
                } else {
                    gitLabApi.getBoardsApi()
                            .deleteGroupBoardList(idOrPath(group), boardId, listId);
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
        Long assigneeId = null;
        Long milestoneId = null;
        Long iterationId = null;
        ensureExists(labelId, "labelId");
        if (project != null) {
            return gitLabApi.getBoardsApi()
                    .createBoardList(idOrPath(project), boardId, labelId, assigneeId, milestoneId, iterationId);
        } else {
            return gitLabApi.getBoardsApi()
                    .createGroupBoardList(idOrPath(group), boardId, labelId, assigneeId, milestoneId, iterationId);
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
                    .updateBoard(idOrPath(project), boardId, name, hideBacklogList, hideClosedList, assigneeId, milestoneId, labels, weight);
        } else {
            return gitLabApi.getBoardsApi()
                    .updateGroupBoard(idOrPath(group), boardId, name, hideBacklogList, hideClosedList, assigneeId, milestoneId, labels, weight);
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
}

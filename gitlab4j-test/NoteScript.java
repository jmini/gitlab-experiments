///usr/bin/env jbang "$0" "$@" ; exit $?

//DEPS info.picocli:picocli:4.6.3
//DEPS org.gitlab4j:gitlab4j-api:6.0.0-rc.8
//JAVA 17

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.Callable;

import org.gitlab4j.api.GitLabApi;
import org.gitlab4j.api.models.*;

import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

@Command(name = "NoteScript", mixinStandardHelpOptions = true, version = "NoteScript 0.1", description = "Tests for GitLab4J")
public class NoteScript implements Callable<Integer> {

    private static final String CONFIG_FILE_INITIAL_CONTENT = """
            GITLAB_URL=https://gitlab.com
            GITLAB_AUTH_VALUE=
            """;

    @Parameters(index = "0", description = "action to execute", defaultValue = "GET_ISSUE_NOTES")
    private Action action;

    @Option(names = { "-p", "--project" }, description = "project")
    private String project;

    @Option(names = { "-i", "--issue" }, description = "issue iid")
    private Long issueIid;

    @Option(names = { "-n", "--note" }, description = "note id")
    private Long noteId;

    @Option(names = { "-b", "--body" }, description = "note body")
    private String body;

    @Option(names = { "--createdAt" }, description = "note createdAt")
    private Date createdAt;

    @Option(names = { "--internal" }, description = "note internal")
    private Boolean internal;

    @Option(names = { "-c", "--config" }, description = "configuration file location")
    String configFile;

    private static enum Action {
        GET_ISSUE_NOTES, GET_ISSUE_NOTE, CREATE_ISSUE_NOTE, UPDATE_ISSUE_NOTE, DELETE_ISSUE_NOTE
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

        ensureExists(project, "project");
        ensureExists(issueIid, "issue");

        try (GitLabApi gitLabApi = new GitLabApi(gitLabUrl, gitLabAuthValue)) {
            switch (action) {
            case GET_ISSUE_NOTES:
                var notes = gitLabApi.getNotesApi()
                        .getIssueNotes(idOrPath(project), issueIid);
                System.out.println(notes);
                break;
            case GET_ISSUE_NOTE:
                ensureExists(noteId, "note");
                var note = gitLabApi.getNotesApi()
                        .getIssueNote(idOrPath(project), issueIid, noteId);
                System.out.println(note);
                break;
            case CREATE_ISSUE_NOTE:
                ensureExists(body, "body");
                var createdNote = gitLabApi.getNotesApi()
                        .createIssueNote(idOrPath(project), issueIid, body, createdAt, internal);
                System.out.println(createdNote);
                break;
            case UPDATE_ISSUE_NOTE:
                ensureExists(body, "body");
                ensureExists(noteId, "note");
                var updatedNote = gitLabApi.getNotesApi()
                        .updateIssueNote(idOrPath(project), issueIid, noteId, body);
                System.out.println(updatedNote);
                break;
            case DELETE_ISSUE_NOTE:
                ensureExists(noteId, "note");
                gitLabApi.getNotesApi()
                        .deleteIssueNote(idOrPath(project), issueIid, noteId);
                break;
            default:
                throw new IllegalArgumentException("Unexpected value: " + action);
            }
        }
        return 0;
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
        final int exitCode = new CommandLine(new NoteScript()).execute(args);
        System.exit(exitCode);
    }
}

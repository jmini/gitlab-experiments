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
import java.util.List;
import java.util.Properties;
import java.util.concurrent.Callable;

import org.gitlab4j.api.GitLabApi;
import org.gitlab4j.api.GitLabApiException;
import org.gitlab4j.api.models.*;

import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

@Command(name = "IssueScript", mixinStandardHelpOptions = true, version = "IssueScript 0.1", description = "Tests for GitLab4J")
public class IssueScript implements Callable<Integer> {

    private static final String CONFIG_FILE_INITIAL_CONTENT = """
            GITLAB_URL=https://gitlab.com
            GITLAB_AUTH_VALUE=
            """;

    @Parameters(index = "0", description = "action to execute", defaultValue = "GET_ISSUE")
    private Action action;

    @Option(names = { "-p", "--project" }, description = "project")
    private String project;

    @Option(names = { "-g", "--group" }, description = "group")
    private String group;

    @Option(names = { "-i", "--issue" }, description = "issue iid")
    private Long issueIid;

    @Option(names = { "-e", "--epic" }, description = "epic iid")
    private Long epicIid;

    @Option(names = { "-c", "--config" }, description = "configuration file location")
    String configFile;

    @Option(names = { "-v", "--verbose" }, description = "log http trafic")
    Boolean logHttp;

    private static enum Action {
        PROJECT_ISSUES, GET_ISSUE, GET_LINKED_ISSUES, GET_EPIC_ISSUES, DELETE_ISSUE, DELETE_ALL_ISSUES, CLOSE_ISSUE, REOPEN_ISSUE
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

        try (GitLabApi gitLabApi = new GitLabApi(gitLabUrl, gitLabAuthValue)) {
            switch (action) {
            case PROJECT_ISSUES:
                ensureExists(project, "project");
                List<Issue> issues = gitLabApi.getIssuesApi()
                        .getIssues(idOrPath(project));
                System.out.println(issues);
                break;
            case GET_ISSUE:
                ensureExists(project, "project");
                ensureExists(issueIid, "issue");
                Issue issue = gitLabApi.getIssuesApi()
                        .getIssue(idOrPath(project), issueIid);
                System.out.println(issue);
                break;
            case DELETE_ISSUE:
                ensureExists(project, "project");
                ensureExists(issueIid, "issue");
                deleteIssue(gitLabApi, issueIid);
                break;
            case DELETE_ALL_ISSUES:
                ensureExists(project, "project");
                List<Issue> list = gitLabApi.getIssuesApi()
                        .getIssues(idOrPath(project));
                for (Issue i : list) {
                    deleteIssue(gitLabApi, i.getIid());
                }
                break;
            case CLOSE_ISSUE:
                ensureExists(project, "project");
                ensureExists(issueIid, "issue");
                Issue closedIssue = gitLabApi.getIssuesApi()
                        .closeIssue(idOrPath(project), issueIid);
                System.out.println(closedIssue);
                break;
            case REOPEN_ISSUE:
                ensureExists(project, "project");
                ensureExists(issueIid, "issue");
                Issue reopenedIssue = gitLabApi.getIssuesApi()
                        .reopenIssue(idOrPath(project), issueIid);
                System.out.println(reopenedIssue);
                break;
            case GET_LINKED_ISSUES:
                ensureExists(project, "project");
                ensureExists(issueIid, "issue");
                var linkedIssue = gitLabApi.getIssuesApi()
                        .getIssueLinks(idOrPath(project), issueIid);
                System.out.println(linkedIssue);
                break;
            case GET_EPIC_ISSUES:
                ensureExists(group, "group");
                ensureExists(epicIid, "epic");
                List<EpicIssue> epicIssues = gitLabApi.getEpicsApi()
                        .getEpicIssues(idOrPath(group), epicIid);
                System.out.println(epicIssues);
                break;
            default:
                throw new IllegalArgumentException("Unexpected value: " + action);
            }
        }
        return 0;
    }

    private GitLabApi createGitLabApi(String gitLabUrl, String gitLabAuthValue) {
        if (logHttp != null && logHttp) {
            return new GitLabApi(gitLabUrl, gitLabAuthValue)
                    .withRequestResponseLogging(java.util.logging.Level.INFO);
        }
        return new GitLabApi(gitLabUrl, gitLabAuthValue);
    }

    private void deleteIssue(GitLabApi gitLabApi, Long iid) throws GitLabApiException {
        gitLabApi.getIssuesApi()
                .deleteIssue(idOrPath(project), iid);
        System.out.println("Deleted issue " + iid);
    }

    private void ensureExists(Object value, String optionName) {
        if (value == null) {
            throw new IllegalStateException("--" + optionName + " must be set");
        }
    }

    private IterationFilter existingOrNew(IterationFilter existing) {
        if (existing != null) {
            return existing;
        }
        return new IterationFilter();
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
        final int exitCode = new CommandLine(new IssueScript()).execute(args);
        System.exit(exitCode);
    }
}

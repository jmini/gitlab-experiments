///usr/bin/env jbang "$0" "$@" ; exit $?

//DEPS info.picocli:picocli:4.6.3
//DEPS https://github.com/unblu/gitlab4j-api/tree/5.2.0-unblu.2
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
import org.gitlab4j.api.models.Issue;
import org.gitlab4j.api.models.LinkType;
import org.gitlab4j.api.models.Project;

import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

@Command(name = "IssueLinkScript", mixinStandardHelpOptions = true, version = "IssueLinkScript 0.1", description = "Tests with issue links in GitLab")
class IssueLinkScript implements Callable<Integer> {

    private static final String CONFIG_FILE_INITIAL_CONTENT = """
            GITLAB_URL=https://gitlab.com
            GITLAB_AUTH_VALUE=
            """;

    @Parameters(index = "0", description = "The project id or project path")
    private String project;

    @Option(names = { "-i", "--issue" }, description = "configuration file location")
    private Long issue;

    @Option(names = { "-c", "--config" }, description = "configuration file location")
    String configFile;

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
            Project p = gitLabApi.getProjectApi()
                    .getProject(project);

            if (issue != null) {
                listLinks(gitLabApi, p, issue);
            } else {
                Issue i1 = gitLabApi.getIssuesApi()
                        .createIssue(p, "Some Title 1", "Some issue");
                Issue i2 = gitLabApi.getIssuesApi()
                        .createIssue(p, "Some Title 2", "Some issue");
                Issue i3 = gitLabApi.getIssuesApi()
                        .createIssue(p, "Some Title 3", "Some issue");

                gitLabApi.getIssuesApi()
                        .createIssueLink(p, i1.getIid(), p, i2.getIid(), LinkType.IS_BLOCKED_BY);
                gitLabApi.getIssuesApi()
                        .createIssueLink(p, i1.getIid(), p, i3.getIid(), LinkType.RELATES_TO);

                listLinks(gitLabApi, p, i1.getIid());
                listLinks(gitLabApi, p, i2.getIid());
                listLinks(gitLabApi, p, i3.getIid());
            }
        }
        return 0;
    }

    private void listLinks(GitLabApi gitLabApi, Project p, Long iid) throws GitLabApiException {
        System.out.println("--- links:");
        List<Issue> list1 = (List<Issue>) gitLabApi.getIssuesApi()
                .getIssueLinks(p, iid);
        for (Issue i : list1) {
            System.out.println(iid + " -> " + i.getIid() + ": " + i.getTitle());
            //System.out.println(iid + " -> " + i.getIid() + ": " + i.getLinkType() + "(id: " + i.getIssueLinkId() + " - createdAt:" + i.getLinkCreatedAt() + " - updatedAt: " + i.getLinkUpdatedAt() + ")");
        }
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
        int exitCode = new CommandLine(new IssueLinkScript()).execute(args);
        System.exit(exitCode);
    }
}

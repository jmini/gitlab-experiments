///usr/bin/env jbang "$0" "$@" ; exit $?

//DEPS info.picocli:picocli:4.6.3
//DEPS org.gitlab4j:gitlab4j-api:6.0.0-rc.8
//JAVA 17

import java.io.*;
import java.nio.file.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.Callable;

import org.gitlab4j.api.GitLabApi;
import org.gitlab4j.api.models.*;

import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

@Command(name = "JobScript", mixinStandardHelpOptions = true, version = "JobScript 0.1", description = "Tests for GitLab4J")
public class JobScript implements Callable<Integer> {

    private static final String CONFIG_FILE_INITIAL_CONTENT = """
            GITLAB_URL=https://gitlab.com
            GITLAB_AUTH_VALUE=
            """;

    @Parameters(index = "0", description = "action to execute", defaultValue = "PRINT_JOB")
    private Action action;

    @Option(names = { "-p", "--project" }, description = "project")
    private String project;

    @Option(names = { "-j", "--jobId" }, description = "job id")
    private Long jobId;

    @Option(names = { "-f", "--artifactPath" }, description = "artifact path")
    private String artifactPath;

    @Option(names = { "-c", "--config" }, description = "configuration file location")
    String configFile;

    @Option(names = { "-v", "--verbose" }, description = "log http trafic")
    Boolean logHttp;

    private static enum Action {
        PRINT_JOB, PRINT_LOG, PRINT_ARTIFACT
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
            if (logHttp != null && logHttp) {
                gitLabApi.enableRequestResponseLogging(java.util.logging.Level.INFO, 2000000000);
            }
            switch (action) {
            case PRINT_JOB:
                ensureExists(project, "project");
                ensureExists(jobId, "jobId");
                Job job = gitLabApi.getJobApi()
                        .getJob(idOrPath(project), jobId);
                System.out.println(job);
                break;
            case PRINT_LOG:
                ensureExists(project, "project");
                ensureExists(jobId, "jobId");
                String log = gitLabApi.getJobApi()
                        .getTrace(idOrPath(project), jobId);
                System.out.println(log);
                break;
            case PRINT_ARTIFACT:
                ensureExists(project, "project");
                ensureExists(jobId, "jobId");
                ensureExists(artifactPath, "artifactPath");
                ArtifactsFile f = new ArtifactsFile();
                f.setFilename(artifactPath);
                InputStream inputisStream = gitLabApi.getJobApi()
                        .downloadArtifactsFile(idOrPath(project), jobId, f);
                String text = new String(inputisStream.readAllBytes(), StandardCharsets.UTF_8);
                System.out.println(text);
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
        final int exitCode = new CommandLine(new JobScript()).execute(args);
        System.exit(exitCode);
    }
}

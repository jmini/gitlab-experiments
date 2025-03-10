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
import java.util.Properties;
import java.util.concurrent.Callable;

import org.gitlab4j.api.GitLabApi;
import org.gitlab4j.api.models.FileUpload;

import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

@Command(name = "FileUploadScript", mixinStandardHelpOptions = true, version = "FileUploadScript 0.1", description = "Tests with fileupaload in GitLab")
class FileUploadScript implements Callable<Integer> {

    private static final String CONFIG_FILE_INITIAL_CONTENT = """
            GITLAB_URL=https://gitlab.com
            GITLAB_AUTH_VALUE=
            """;

    @Option(names = { "-p", "--project" }, description = "project (id or path)")
    private String project;

    @Option(names = { "-g", "--group" }, description = "group")
    private String group;

    @Option(names = { "-f", "--file" }, description = "Path to the file to uplaod")
    private String filePath;

    @Option(names = { "-n", "--filename" }, description = "Filename in Gitab")
    private String filename;

    @Option(names = { "-c", "--config" }, description = "configuration file location")
    String configFile;

    @Option(names = { "-v", "--verbose" }, description = "log http trafic")
    Boolean logHttp;

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

        filePathMandatory();
        Path fileToUpload = Paths.get(filePath);
        if (!Files.isReadable(file) || !Files.isRegularFile(fileToUpload)) {
            throw new IllegalStateException("Can not find file: " + fileToUpload.toAbsolutePath());
        }

        try (GitLabApi gitLabApi = createGitLabApi(gitLabUrl, gitLabAuthValue)) {
            if (logHttp != null && logHttp) {
                gitLabApi.enableRequestResponseLogging(java.util.logging.Level.INFO, 2000000000);
            }
            if (filename == null) {
                filename = fileToUpload.getFileName()
                        .toString();
            }
            if (project == null && group == null) {
                throw new IllegalStateException("Project or group is mandatory");
            } else if (project != null && group != null) {
                throw new IllegalStateException("Project and group can't be set at the same time");
            } else if (project != null) {
                FileUpload uplaodedFile = gitLabApi.getProjectApi()
                        .uploadFile(idOrPath(project), Files.newInputStream(fileToUpload), filename, null);
                System.out.println(uplaodedFile);
            } else if (group != null) {
                throw new IllegalStateException("Upload on goup is not available, see: https://gitlab.com/gitlab-org/gitlab/-/issues/329615");
            }
        }
        return 0;
    }

    private GitLabApi createGitLabApi(String gitLabUrl, String gitLabAuthValue) {
        return new GitLabApi(gitLabUrl, gitLabAuthValue);
    }

    private void filePathMandatory() {
        if (filePath == null) {
            throw new IllegalStateException("File is mandatory");
        }
    }

    private Object idOrPath(String value) {
        if (value.matches("[0-9]+")) {
            return Long.valueOf(value);
        }
        return value;
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
        int exitCode = new CommandLine(new FileUploadScript()).execute(args);
        System.exit(exitCode);
    }
}

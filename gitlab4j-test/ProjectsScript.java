///usr/bin/env jbang "$0" "$@" ; exit $?

//DEPS info.picocli:picocli:4.6.3
//DEPS https://github.com/jmini/gitlab4j-api/tree/5.3.0-pr1012
//JAVA 17

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.Callable;

import org.gitlab4j.api.*;
import org.gitlab4j.api.models.*;

import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

@Command(name = "ProjectsScript", mixinStandardHelpOptions = true, version = "ProjectsScript 0.1", description = "Tests with related epics in GitLab")
class ProjectsScript implements Callable<Integer> {

    private static final String CONFIG_FILE_INITIAL_CONTENT = """
            GITLAB_URL=https://gitlab.com
            GITLAB_AUTH_VALUE=
            """;

    @Parameters(index = "0", description = "action to execute", defaultValue = "PRINT_PROJECT")
    private Action action;

    @Option(names = { "-i", "--project-id" }, description = "project id")
    private String projectId;

    @Option(names = { "-n", "--name" }, description = "project name")
    private String name;

    @Option(names = { "-s", "--namespace-id" }, description = "project namespace id")
    private Long namespaceId;

    @Option(names = { "-d", "--description" }, description = "project description")
    private String description;

    @Option(names = { "-t", "--topics" }, description = "project topics")
    private List<String> topics;

    @Option(names = { "-c", "--config" }, description = "configuration file location")
    String configFile;

    private static enum Action {
        PRINT_PROJECT, PRINT_PROJECTS, CREATE_PROJECT, UPDATE_PROJECT, DELETE_PROJECT
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
        Properties prop = configProperties(file);
        String gitLabUrl = readProperty(prop, "GITLAB_URL", "https://gitlab.com");
        String gitLabAuthValue = readProperty(prop, "GITLAB_AUTH_VALUE");

        try (GitLabApi gitLabApi = new GitLabApi(gitLabUrl, gitLabAuthValue)) {
            System.out.println("Action " + action + " ...");

            Project project;
            Project p;
            switch (action) {
            case PRINT_PROJECTS:
                List<Project> projects = gitLabApi.getProjectApi()
                        .getProjects();
                for (Project t : projects) {
                    printProject(t);
                }
                break;
            case PRINT_PROJECT:
                projectIdMandatory();
                project = gitLabApi.getProjectApi()
                        .getProject(idOrPath(projectId));
                // printProject(project);
                System.out.println(project);
                break;
            case CREATE_PROJECT:
                p = createProject();
                project = gitLabApi.getProjectApi()
                        .createProject(p);
                System.out.println(project);
                break;
            case UPDATE_PROJECT:
                projectIdMandatory();
                p = createProject();
                Object id = idOrPath(projectId);
                if(id instanceof Long) {
                    p.withId((Long) id);
                } else {
                    throw new IllegalStateException("Project id must be a Long");   
                }
                project = gitLabApi.getProjectApi()
                        .updateProject(p);
                System.out.println(project);
                break;
            case DELETE_PROJECT:
                projectIdMandatory();
                gitLabApi.getProjectApi()
                        .deleteProject(projectId);
                break;
            default:
                break;
            }
        }
        return 0;
    }

    private void projectIdMandatory() {
        if (projectId == null) {
            throw new IllegalStateException("Project id is mandatory");
        }
    }

    private Project createProject() {
        Project p = new Project();
        if (name != null) {
            p.withName(name);
        }
        if (namespaceId != null) {
            p.withNamespaceId(namespaceId);
        }
        if (description != null) {
            p.withDescription(description);
        }
        if (topics != null) {
            p.withTopics(topics);
        }
        return p;
    }

    private Object idOrPath(String value) {
        if (value.matches("[0-9]+")) {
            return Long.valueOf(value);
        }
        return value;
    }

    private void printProject(Project project) {
        System.out.println("Project " + project.getId() + " - " + project.getName());
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
        int exitCode = new CommandLine(new ProjectsScript()).execute(args);
        System.exit(exitCode);
    }
}

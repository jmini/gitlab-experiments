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
import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.Callable;

import org.gitlab4j.api.*;
import org.gitlab4j.api.models.*;

import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

@Command(name = "IterationScript", mixinStandardHelpOptions = true, version = "IterationScript 0.1", description = "Tests for GitLab4J")
public class IterationScript implements Callable<Integer> {

    private static final String CONFIG_FILE_INITIAL_CONTENT = """
            GITLAB_URL=https://gitlab.com
            GITLAB_AUTH_VALUE=
            """;

    @Option(names = { "-p", "--project" }, description = "perform a project search")
    private String project;

    @Option(names = { "-g", "--group" }, description = "perform a group search")
    private String group;

    @Option(names = { "-s", "--state" }, description = "state")
    private IterationFilter.IterationFilterState state;
 
    @Option(names = { "-q", "--query" }, description = "query")
    private String search;

    @Option(names = { "-i", "--in" }, description = "in")
    private IterationFilter.IterationFilterIn in;

    @Option(names = { "-n", "--includeAncestors" }, description = "include ancestors")
    private Boolean includeAncestors;
 
    @Option(names = { "-a", "--after" }, description = "updated after")
    private Date updatedAfter;
 
    @Option(names = { "-b", "--before" }, description = "updated before")
    private Date updatedBefore;

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
        final Properties prop = configProperties(file);
        final String gitLabUrl = readProperty(prop, "GITLAB_URL", "https://gitlab.com");
        final String gitLabAuthValue = readProperty(prop, "GITLAB_AUTH_VALUE");

        if (project != null && group != null) {
            System.out.println("'--project' and '--group' can't be set at the same time");
            return 1;
        } else  if (project == null && group == null) {
            System.out.println("One of '--project' and '--group' must be set");
            return 1;
        }
        try (GitLabApi gitLabApi = new GitLabApi(gitLabUrl, gitLabAuthValue)) {
            List<?> result;
            IterationFilter filter = null;
            if(state != null) {
                filter = existingOrNew(filter);
                filter.setState(state);
            }
            if(search != null) {
                filter = existingOrNew(filter);
                filter.setSearch(search);
            }
            if(in != null) {
                filter = existingOrNew(filter);
                filter.setIn(in);
            }
            if(includeAncestors != null) {
                filter = existingOrNew(filter);
                filter.setIncludeAncestors(includeAncestors);
            }
            if(updatedAfter != null) {
                filter = existingOrNew(filter);
                filter.setUpdatedAfter(updatedAfter);
            }
            if(updatedBefore != null) {
                filter = existingOrNew(filter);
                filter.setUpdatedBefore(updatedBefore);
            }

            if (project != null) {
                System.out.println("Project iteration...");
                result = gitLabApi.getProjectApi().listProjectIterations(idOrPath(project), filter);
            } else if (group != null) {
                System.out.println("Group iteration...");
                result = gitLabApi.getGroupApi().listGroupIterations(idOrPath(group), filter);
            } else {
                throw new IllegalArgumentException("Unexpected state (input parameters might be wrong)");
            }
            System.out.println(result);
        }
        return 0;
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
        final int exitCode = new CommandLine(new IterationScript()).execute(args);
        System.exit(exitCode);
    }
}

///usr/bin/env jbang "$0" "$@" ; exit $?

//DEPS info.picocli:picocli:4.6.3
//DEPS org.gitlab4j:gitlab4j-api:6.0.0-rc.7
//JAVA 17

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Properties;
import java.util.concurrent.Callable;

import org.gitlab4j.api.GitLabApi;
import org.gitlab4j.api.GitLabApiException;
import org.gitlab4j.api.models.AccessLevel;
import org.gitlab4j.api.models.ProjectAccessToken;
import org.gitlab4j.models.Constants.ProjectAccessTokenScope;

import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

@Command(name = "ProjectAccessTokenScript", mixinStandardHelpOptions = true, version = "ProjectAccessTokenScript 0.1", description = "Tests for GitLab4J")
public class ProjectAccessTokenScript implements Callable<Integer> {

    private static final String CONFIG_FILE_INITIAL_CONTENT = """
            GITLAB_URL=https://gitlab.com
            GITLAB_AUTH_VALUE=
            """;

    @Parameters(index = "0", description = "action to execute", defaultValue = "LIST_PROJECT_ACCESS_TOKEN")
    private Action action;

    @Option(names = { "-p", "--project" }, description = "project id or path")
    private String project;

    @Option(names = { "-t", "--tokenId" }, description = "token id")
    private Long tokenId;

    @Option(names = { "-n", "--tokenName" }, description = "token name")
    private String tokenName;

    @Option(names = { "-e", "--expiresAt" }, description = "token expiration date")
    private Date expiresAt;

    @Option(names = { "-s", "--scope" }, description = "token scopes")
    private List<ProjectAccessTokenScope> scopes = new ArrayList<>();

    @Option(names = { "-a", "--accessLevel" }, description = "token access level")
    private AccessLevel accessLevel;

    @Option(names = { "-c", "--config" }, description = "configuration file location")
    String configFile;

    private static enum Action {
        LIST_PROJECT_ACCESS_TOKEN, GET_PROJECT_ACCESS_TOKEN, CREATE_PROJECT_ACCESS_TOKEN, ROTATE_PROJECT_ACCESS_TOKEN, REVOKE_PROJECT_ACCESS_TOKEN
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

        try (GitLabApi gitLabApi = new GitLabApi(gitLabUrl, gitLabAuthValue)) {
            switch (action) {
            case LIST_PROJECT_ACCESS_TOKEN:
                var tokens = gitLabApi.getProjectApi()
                        .listProjectAccessTokens(idOrPath(project));
                System.out.println(tokens);
                break;
            case GET_PROJECT_ACCESS_TOKEN:
                ensureExists(tokenId, "tokenId");
                var token = gitLabApi.getProjectApi()
                        .getProjectAccessToken(idOrPath(project), tokenId);
                System.out.println(token);
                break;
            case CREATE_PROJECT_ACCESS_TOKEN:
                var createdToken = gitLabApi.getProjectApi()
                        .createProjectAccessToken(idOrPath(project), tokenName, scopes, expiresAt, accessLevelValue(accessLevel));
                System.out.println(createdToken);
                break;
            case ROTATE_PROJECT_ACCESS_TOKEN:
                var r = gitLabApi.getProjectApi()
                        .rotateProjectAccessToken(idOrPath(project), getTokenId(gitLabApi), expiresAt);
                System.out.println(r);
                break;
            case REVOKE_PROJECT_ACCESS_TOKEN:
                gitLabApi.getProjectApi()
                        .revokeProjectAccessToken(idOrPath(project), getTokenId(gitLabApi));
                break;
            default:
                throw new IllegalArgumentException("Unexpected value: " + action);
            }
        }
        return 0;
    }

    private static Long accessLevelValue(AccessLevel value) {
        if (value == null) {
            return null;
        }
        return value.value.longValue();
    }

    private Long getTokenId(GitLabApi gitLabApi) throws GitLabApiException {
        if (tokenId != null) {
            return tokenId;
        }
        ensureExists(tokenName, "tokenName");
        List<ProjectAccessToken> tokens = gitLabApi.getProjectApi()
                .listProjectAccessTokens(idOrPath(project));
        ProjectAccessToken token = tokens.stream()
                .filter(t -> Objects.equals(t.isRevoked(), Boolean.FALSE))
                .filter(t -> tokenName.equals(t.getName()))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("Could not find token with name '" + tokenName + "' in project '" + idOrPath(project) + "'"));
        return token.getId();
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
        final int exitCode = new CommandLine(new ProjectAccessTokenScript()).execute(args);
        System.exit(exitCode);
    }
}

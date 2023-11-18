///usr/bin/env jbang "$0" "$@" ; exit $?

//DEPS org.gitlab4j:gitlab4j-api:5.4.0
//JAVA 17

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;
import java.util.Optional;
import java.util.Properties;

import org.gitlab4j.api.GitLabApi;
import org.gitlab4j.api.GitLabApiException;
import org.gitlab4j.api.models.Group;
import org.gitlab4j.api.models.GroupParams;

import com.fasterxml.jackson.databind.ObjectMapper;

// Usage: "jbang run CreateExample.java"
class CreateExample {
    private static final ObjectMapper mapper = new ObjectMapper();

    private static final long USER_GROUP_ROOT = 1L;
    private static final long USER_GROUP_ALL = 2L;
    private static final long USER_GROUP_TEAM_A = 3L;
    private static final long USER_GROUP_SRE = 4L;

    private static String GITLAB_URL = "https://gitlab.com";
    private static String GITLAB_REST_URL = GITLAB_URL + "/api/v4";
    private static String GITLAB_AUTH_KEY = "PRIVATE-TOKEN";
    private static String GITLAB_AUTH_VALUE;

    private static Properties PROPERTIES;

    private static final String CONFIG_FILE_INITIAL_CONTENT = """
            GITLAB_URL=https://gitlab.com
            GITLAB_AUTH_VALUE=
            """;

    static {
        System.out.println("Reading config");
        Path c = configFile(Paths.get(""));
        PROPERTIES = configProperties(c);
        GITLAB_URL = readProperty(PROPERTIES, "GITLAB_URL");
        GITLAB_AUTH_VALUE = readProperty(PROPERTIES, "GITLAB_AUTH_VALUE");
    }

    public static void main(String... args) throws Exception {
        try (GitLabApi api = new GitLabApi(GITLAB_URL, GITLAB_AUTH_VALUE)) {
            Group parentGroup = createGroupIfNotExists(api, "User Group", "group", null);
            Group all = createGroupIfNotExists(api, "All", "group/all", parentGroup);
            Group teamA = createGroupIfNotExists(api, "Team A", "group/team-a", parentGroup);
            Group sre = createGroupIfNotExists(api, "SRE", "group/sre", parentGroup);
            createGroupIfNotExists(api, "Developers", "group/developers", parentGroup);
            createGroupIfNotExists(api, "Marketing", "group/marketing", parentGroup);
            createGroupIfNotExists(api, "Presales", "group/presales", parentGroup);
            createGroupIfNotExists(api, "QA", "group/qa", parentGroup);
            createGroupIfNotExists(api, "Team B", "group/team-b", parentGroup);
            createGroupIfNotExists(api, "Tech Writer", "group/tech-writer", parentGroup);

            boolean hasChanges = false;
            hasChanges = checkValue("USER_GROUP_ROOT", parentGroup, hasChanges);
            hasChanges = checkValue("USER_GROUP_ALL", all, hasChanges);
            hasChanges = checkValue("USER_GROUP_TEAM_A", teamA, hasChanges);
            hasChanges = checkValue("USER_GROUP_SRE", sre, hasChanges);
            if (hasChanges) {
                Path c = configFile(Paths.get(""));
                PROPERTIES.store(new FileOutputStream(c.toFile()), null);
            }
        }
    }

    private static Group createGroupIfNotExists(GitLabApi api, String groupName, String groupFullPath, Group parentGroup) throws GitLabApiException {
        Group group;
        Optional<Group> findGroup = readGroup(api, groupFullPath);
        if (findGroup.isPresent()) {
            group = findGroup.get();
            if (!Objects.equals(groupFullPath, group.getFullPath())) {
                throw new IllegalStateException("groupFullPath is wrong");
            }
            if (!Objects.equals(groupName, group.getName())) {
                throw new IllegalStateException("groupName is wrong");
            }
        } else {
            GroupParams param;
            if (parentGroup != null) {
                if (!groupFullPath.startsWith(parentGroup.getPath() + "/")) {
                    throw new IllegalStateException("path is not expected");
                }
                int beginIndex = parentGroup.getPath()
                        .length() + 1;
                param = new GroupParams()
                        .withName(groupName)
                        .withPath(groupFullPath.substring(beginIndex))
                        .withParentId(parentGroup.getId());
            } else {
                param = new GroupParams()
                        .withName(groupName)
                        .withPath(groupFullPath);
            }
            group = api.getGroupApi()
                    .createGroup(param);
        }
        return group;
    }

    private static Optional<Group> readGroup(GitLabApi api, String groupIdOrPath) {
        try {
            Group rootGroup = api.getGroupApi()
                    .getGroup(groupIdOrPath);
            return Optional.ofNullable(rootGroup);
        } catch (GitLabApiException e) {
            if (e.getHttpStatus() != 404) {
                throw new IllegalStateException("Could not find group '" + groupIdOrPath + "'", e);
            }
        }
        return Optional.empty();
    }

    private static boolean checkValue(String key, Group group, boolean hasChanges) {
        Long expectedValue = group.getId();
        Long currentValue = Long.valueOf(PROPERTIES.getProperty(key, "0"));
        if (!Objects.equals(currentValue, expectedValue)) {
            PROPERTIES.setProperty(key, expectedValue.toString());
            return true;
        }
        return hasChanges;
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
                throw new IllegalStateException(String.format("Configuration file '%s' does not exist", configFile.toAbsolutePath()));
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

}

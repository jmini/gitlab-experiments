///usr/bin/env jbang "$0" "$@" ; exit $?

//DEPS org.gitlab4j:gitlab4j-api:5.1.0
//JAVA 17

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import org.gitlab4j.api.GitLabApi;
import org.gitlab4j.api.GitLabApiException;
import org.gitlab4j.api.models.AccessLevel;
import org.gitlab4j.api.models.Branch;
import org.gitlab4j.api.models.BranchAccessLevel;
import org.gitlab4j.api.models.Group;
import org.gitlab4j.api.models.GroupProjectsFilter;
import org.gitlab4j.api.models.Member;
import org.gitlab4j.api.models.Project;
import org.gitlab4j.api.models.ProtectedBranch;
import org.gitlab4j.api.models.RepositoryFile;
import org.gitlab4j.api.models.Visibility;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

// Usage: "jbang run FixUserGroups.java"
class FixUserGroups {
    private static final ObjectMapper mapper = new ObjectMapper();

    private static String GITLAB_URL = "https://gitlab.com";
    private static String GITLAB_REST_URL = GITLAB_URL + "/api/v4";
    private static String GITLAB_AUTH_KEY = "PRIVATE-TOKEN";
    private static String GITLAB_AUTH_VALUE;

    private static Long USER_GROUP_ROOT;
    private static Long USER_GROUP_ALL;
    private static Long USER_GROUP_TEAM_A;
    private static Long USER_GROUP_SRE;

    private static final String CONFIG_FILE_INITIAL_CONTENT = """
            GITLAB_URL=https://gitlab.com
            GITLAB_AUTH_VALUE=
            USER_GROUP_ROOT=1
            USER_GROUP_ALL=2
            USER_GROUP_TEAM_A=3
            USER_GROUP_SRE=4
            """;

    private static final String README_CONTENT = """
            == Do not use

            This is an empty repository to make the group visible by all users (GitLab limitation with empty groups)
            """;
    static {
        System.out.println("Reading config");
        Path c = configFile(Paths.get(""));
        Properties p = configProperties(c);
        GITLAB_URL = readProperty(p, "GITLAB_URL");
        GITLAB_REST_URL = GITLAB_URL + "/api/v4";
        GITLAB_AUTH_VALUE = readProperty(p, "GITLAB_AUTH_VALUE");
        USER_GROUP_ROOT = readLongProperty(p, "USER_GROUP_ROOT");
        USER_GROUP_ALL = readLongProperty(p, "USER_GROUP_ALL");
        USER_GROUP_TEAM_A = readLongProperty(p, "USER_GROUP_TEAM_A");
        USER_GROUP_SRE = readLongProperty(p, "USER_GROUP_SRE");
    }

    public static void main(String... args) throws Exception {
        try (GitLabApi api = new GitLabApi(GITLAB_URL, GITLAB_AUTH_VALUE)) {
            List<Group> subGroups = api.getGroupApi()
                    .getSubGroups(USER_GROUP_ROOT);
            addAllMembersToAllGroup(api, subGroups);
            fixAllUserGroups(api, subGroups);

            //        Group g = api.getGroupApi()
            //                .getGroup(USER_GROUP_TEAM_A);
            //        fixUserGroup(api, g);

            //        Group g = api.getGroupApi()
            //                .getGroup(USER_GROUP_SRE);
            //        fixUserGroup(api, g);
        }
    }

    private static void addAllMembersToAllGroup(GitLabApi api, List<Group> subGroups) throws Exception {
        Map<Long, List<Member>> map = new HashMap<>();
        for (Group group : subGroups) {
            List<Member> members = api.getGroupApi()
                    .getMembers(group);
            map.put(group.getId(), members);
        }
        Set<Long> allMembersUserIds = map.values()
                .stream()
                .flatMap(List::stream)
                .map(Member::getId)
                .collect(Collectors.toSet());

        Set<Long> current = map.get(USER_GROUP_ALL)
                .stream()
                .map(Member::getId)
                .collect(Collectors.toSet());
        allMembersUserIds.stream()
                .filter(id -> !current.contains(id))
                .forEach(id -> {
                    try {
                        api.getGroupApi()
                                .addMember(USER_GROUP_ALL, id, AccessLevel.GUEST);
                    } catch (GitLabApiException e) {
                        throw new IllegalStateException("Can not add user with id '" + id + "' to group '" + USER_GROUP_ALL + "'", e);
                    }
                });
    }

    private static void fixAllUserGroups(GitLabApi api, List<Group> subGroups) throws Exception {
        for (Group group : subGroups) {
            fixUserGroup(api, group);
        }
    }

    private static void fixUserGroup(GitLabApi api, Group group) throws Exception {
        GroupProjectsFilter filter = new GroupProjectsFilter()
                .withShared(false)
                .withOwned(true);
        List<Project> projects = api.getGroupApi()
                .getProjects(group, filter);

        Optional<Project> findProject = projects.stream()
                .filter(p -> p.getName()
                        .equals("do-not-use"))
                .findAny();
        Project project;
        if (findProject.isPresent()) {
            project = findProject.get();
        } else {
            updateGroupSettings(group, "developer");

            Project initProject = new Project()
                    .withName("do-not-use")
                    .withVisibility(Visibility.INTERNAL)
                    .withInitializeWithReadme(false)
                    .withNamespaceId(group.getId());
            project = api.getProjectApi()
                    .createProject(initProject);

            //Initialize the repository:
            Branch branch;
            try {
                branch = api.getRepositoryApi()
                        .getBranch(project, "main");
            } catch (GitLabApiException e) {
                branch = null;
            }
            if (branch == null) {
                createFile(api, project, "main", "readme.adoc", README_CONTENT, "Initial commit");
            }
        }

        //Protect branch if needed:
        updateProtectedBranch(api, project);

        //Share project with the all user group
        List<Long> ids = readProjectGroupIds(project);
        if (!ids.contains(USER_GROUP_ALL)) {
            api.getProjectApi()
                    .shareProject(project, USER_GROUP_ALL, AccessLevel.GUEST, null);
        }

        //Update project settings:
        updateProjectSettings(project);

        //Archive project:
        api.getProjectApi()
                .archiveProject(project);

        updateGroupSettings(group, "noone");
    }

    private static boolean isNotOnlyNone(List<BranchAccessLevel> levels) {
        if (levels.size() != 1) {
            return true;
        }
        return levels.get(0)
                .getAccessLevel() != AccessLevel.NONE;
    }

    private static void updateGroupSettings(Group group, String projectCreationLevel) throws Exception {
        boolean needsModification = false;

        String content = readGroup(group.getId());
        JsonNode node = mapper.readTree(content);

        ObjectNode body = mapper.createObjectNode();

        needsModification = ensureTextValue(node, body, needsModification, "project_creation_level", projectCreationLevel);
        needsModification = ensureTextValue(node, body, needsModification, "visibility", "internal");
        needsModification = ensureTextValue(node, body, needsModification, "subgroup_creation_level", "maintainer");
        needsModification = ensureBooleanValue(node, body, needsModification, "request_access_enabled", true);

        if (needsModification) {
            String bodyContent = mapper.writeValueAsString(body);
            updateGroup(group.getId(), bodyContent);
        }
    }

    private static void updateProjectSettings(Project project) throws Exception {
        boolean needsModification = false;

        String content = readProject(project);
        JsonNode node = mapper.readTree(content);

        ObjectNode body = mapper.createObjectNode();

        needsModification = ensureTextValue(node, body, needsModification, "visibility", "internal");
        needsModification = ensureTextValue(node, body, needsModification, "issues_access_level", "disabled");
        needsModification = ensureTextValue(node, body, needsModification, "merge_requests_access_level", "disabled");
        needsModification = ensureTextValue(node, body, needsModification, "merge_requests_access_level", "disabled");
        needsModification = ensureTextValue(node, body, needsModification, "forking_access_level", "disabled");
        needsModification = ensureTextValue(node, body, needsModification, "analytics_access_level", "disabled");
        needsModification = ensureTextValue(node, body, needsModification, "wiki_access_level", "disabled");
        needsModification = ensureTextValue(node, body, needsModification, "snippets_access_level", "disabled");
        needsModification = ensureTextValue(node, body, needsModification, "pages_access_level", "disabled");
        needsModification = ensureBooleanValue(node, body, needsModification, "request_access_enabled", false);

        if (needsModification) {
            String bodyContent = mapper.writeValueAsString(body);
            updateProject(project, bodyContent);
        }
    }

    private static boolean ensureTextValue(JsonNode node, ObjectNode body, boolean needsModification, String key, String expectedValue) {
        if (!node.has(key) || !node.get(key)
                .asText()
                .equals(expectedValue)) {
            body.put(key, expectedValue);
            return true;
        }
        return needsModification;
    }

    private static boolean ensureBooleanValue(JsonNode node, ObjectNode body, boolean needsModification, String key, boolean expectedValue) {
        if (!node.has(key) || node.get(key)
                .asBoolean() != expectedValue) {
            body.put(key, expectedValue);
            return true;
        }
        return needsModification;
    }

    private static void updateProtectedBranch(GitLabApi api, Project project) throws Exception {
        ProtectedBranch protectedBranch;
        try {
            protectedBranch = api.getProtectedBranchesApi()
                    .getProtectedBranch(project, "main");
        } catch (GitLabApiException e) {
            if (e.getHttpStatus() == 404) {
                protectedBranch = null;
            } else {
                throw new IllegalStateException("Can not read branch protection for the branch 'main' in '" + project.getId() + "'", e);
            }
        }

        if (Boolean.TRUE.equals(project.getArchived())) {
            api.getProjectApi()
                    .unarchiveProject(project);
        }

        if (protectedBranch == null) {
            api.getProtectedBranchesApi()
                    .protectBranch(project, "main", AccessLevel.NONE, AccessLevel.NONE);
        } else if (isNotOnlyNone(protectedBranch.getMergeAccessLevels()) || isNotOnlyNone(protectedBranch.getPushAccessLevels())) {
            System.out.println("modify branch protection in project " + project.getId());
            api.getProtectedBranchesApi()
                    .unprotectBranch(project, "main");
            TimeUnit.SECONDS.sleep(3);
            api.getProtectedBranchesApi()
                    .protectBranch(project, "main", AccessLevel.NONE, AccessLevel.NONE);
        }

    }

    private static String readGroup(Long groupId) throws Exception {
        StringBuilder sb = new StringBuilder();
        sb.append(GITLAB_REST_URL);
        sb.append("/groups/");
        sb.append(groupId);

        return getRequest(sb.toString(), GITLAB_AUTH_KEY, GITLAB_AUTH_VALUE);
    }

    private static String updateGroup(Long groupId, String body) throws Exception {
        StringBuilder sb = new StringBuilder();
        sb.append(GITLAB_REST_URL);
        sb.append("/groups/");
        sb.append(groupId);

        return putJsonRequest(sb.toString(), body, GITLAB_AUTH_KEY, GITLAB_AUTH_VALUE);
    }

    private static String readProject(Project p) throws Exception {
        StringBuilder sb = new StringBuilder();
        sb.append(GITLAB_REST_URL);
        sb.append("/projects/");
        sb.append(p.getId());

        return getRequest(sb.toString(), GITLAB_AUTH_KEY, GITLAB_AUTH_VALUE);
    }

    private static void updateProject(Project p, String body) throws Exception {
        StringBuilder sb = new StringBuilder();
        sb.append(GITLAB_REST_URL);
        sb.append("/projects/");
        sb.append(p.getId());

        putJsonRequest(sb.toString(), body, GITLAB_AUTH_KEY, GITLAB_AUTH_VALUE);
    }

    private static List<Long> readProjectGroupIds(Project p) throws Exception {
        StringBuilder sb = new StringBuilder();
        sb.append(GITLAB_REST_URL);
        sb.append("/projects/");
        sb.append(p.getId());
        sb.append("/groups?with_shared=true");

        String content = getRequest(sb.toString(), GITLAB_AUTH_KEY, GITLAB_AUTH_VALUE);
        JsonNode node = mapper.readTree(content);
        List<Long> result = new ArrayList<>();
        for (JsonNode e : node) {
            result.add(e.get("id")
                    .asLong());
        }
        return result;
    }

    private static void createFile(GitLabApi apu, Project project, String branchName, String filePath, String content, String commitMessage) throws GitLabApiException {
        RepositoryFile file = new RepositoryFile();
        file.setContent(content);
        file.setFilePath(filePath);
        apu.getRepositoryFileApi()
                .createFile(project, file, branchName, commitMessage);
    }

    private static String getRequest(String url, String authHeaderKey, String authHeaderValue) throws Exception {
        return withoutBodyRequest("GET", url, authHeaderKey, authHeaderValue);
    }

    private static String deleteRequest(String url, String authHeaderKey, String authHeaderValue) throws Exception {
        return withoutBodyRequest("DELETE", url, authHeaderKey, authHeaderValue);
    }

    private static String postJsonRequest(String url, String body, String authHeaderKey, String authHeaderValue) throws Exception {
        return jsonRequest("POST", url, body, authHeaderKey, authHeaderValue);
    }

    private static String postWithoutBodyRequest(String url, String authHeaderKey, String authHeaderValue) throws Exception {
        return withoutBodyRequest("POST", url, authHeaderKey, authHeaderValue);
    }

    private static String putJsonRequest(String url, String body, String authHeaderKey, String authHeaderValue) throws Exception {
        return jsonRequest("PUT", url, body, authHeaderKey, authHeaderValue);
    }

    private static String putWithoutBodyRequest(String url, String authHeaderKey, String authHeaderValue) throws Exception {
        return withoutBodyRequest("PUT", url, authHeaderKey, authHeaderValue);
    }

    private static String patchJsonRequest(String url, String body, String authHeaderKey, String authHeaderValue) throws Exception {
        return jsonRequest("PATCH", url, body, authHeaderKey, authHeaderValue);
    }

    private static String patchWithoutBodyRequest(String url, String authHeaderKey, String authHeaderValue) throws Exception {
        return withoutBodyRequest("PATCH", url, authHeaderKey, authHeaderValue);
    }

    private static String jsonRequest(String method, String url, String body, String authHeaderKey, String authHeaderValue) throws Exception {
        System.out.println(method + " " + url);
        // System.out.println(body);

        HttpRequest request = HttpRequest.newBuilder()
                .header(authHeaderKey, authHeaderValue)
                .header("Content-Type", "application/json")
                .uri(new URI(url))
                .method(method, BodyPublishers.ofString(body))
                .build();

        return executeRequestAndHandleResponse(request);
    }

    private static String withoutBodyRequest(String method, String url, String authHeaderKey, String authHeaderValue) throws Exception {
        System.out.println(method + " " + url);

        HttpRequest request = HttpRequest.newBuilder()
                .header(authHeaderKey, authHeaderValue)
                .header("Content-Type", "application/json")
                .uri(new URI(url))
                .method(method, BodyPublishers.noBody())
                .build();

        return executeRequestAndHandleResponse(request);
    }

    private static String executeRequestAndHandleResponse(HttpRequest request) throws Exception {
        HttpClient client = HttpClient.newHttpClient();
        HttpResponse<String> response = client.send(request, BodyHandlers.ofString());
        String content = response.body();
        // System.out.println(content);
        int code = response.statusCode();
        if (!(code >= 200 && code < 300)) {
            System.out.println("Error while performing the call. Response:");
            System.out.println(content);
            throw new IllegalStateException("Unexpected status code. " + code);
        }
        return content;
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

    public static Long readLongProperty(Properties p, String key) {
        return Long.valueOf(readProperty(p, key));
    }
}

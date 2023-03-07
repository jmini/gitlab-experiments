///usr/bin/env jbang "$0" "$@" ; exit $?

//DEPS https://github.com/jmini/gitlab4j-api/tree/5.1.0-jmini.3
//JAVA 17

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Properties;

import org.gitlab4j.api.GitLabApi;
import org.gitlab4j.api.models.ExternalStatusCheck;
import org.gitlab4j.api.models.ExternalStatusCheckStatus;
import org.gitlab4j.api.models.ExternalStatusCheckStatus.Status;
import org.gitlab4j.api.models.MergeRequest;
import org.gitlab4j.api.models.Project;

// Usage: "jbang run ExternalStatusChecks.java"
class ExternalStatusChecks {
    private static String GITLAB_URL = "https://gitlab.com";
    private static String GITLAB_AUTH_KEY = "PRIVATE-TOKEN";
    private static String GITLAB_AUTH_VALUE;
    private static String GITLAB_PROJECT;
    private static Long GITLAB_MR;

    private static final String CONFIG_FILE_INITIAL_CONTENT = """
            GITLAB_URL=https://gitlab.com
            GITLAB_AUTH_VALUE=
            GITLAB_PROJECT=
            GITLAB_MR=
            """;
    static {
        System.out.println("Reading config");
        Path c = configFile(Paths.get(""));
        Properties p = configProperties(c);
        GITLAB_URL = readProperty(p, "GITLAB_URL");
        GITLAB_AUTH_VALUE = readProperty(p, "GITLAB_AUTH_VALUE");
        GITLAB_PROJECT = readProperty(p, "GITLAB_PROJECT");
        GITLAB_MR = Long.valueOf(readProperty(p, "GITLAB_MR"));
    }

    public static void main(String... args) throws Exception {
        try (GitLabApi api = new GitLabApi(GITLAB_URL, GITLAB_AUTH_VALUE)) {
            Project project = api.getProjectApi()
                    .getProject(GITLAB_PROJECT);

            List<ExternalStatusCheck> checks = api.getExternalStatusCheckApi()
                    .getExternalStatusChecks(project);
            System.out.println("All checks:");
            System.out.println(checks);

            //            System.out.println("Created check:");
            //            ExternalStatusCheck c0 = api.getExternalStatusCheckApi()
            //                    .createExternalStatusCheck(GITLAB_PROJECT, "name" + System.currentTimeMillis(), "https://unblu-test.com/test" + System.currentTimeMillis(), null);
            //            System.out.println(c0);
            //
            //            c0.setName("xxx1");
            //            ExternalStatusCheck c1 = api.getExternalStatusCheckApi()
            //                    .updateExternalStatusCheck(GITLAB_PROJECT, c0);
            //            System.out.println("Updated check:");
            //            System.out.println(c1);
            //
            //            ExternalStatusCheck c2 = api.getExternalStatusCheckApi()
            //                    .updateExternalStatusCheck(GITLAB_PROJECT, c1.getId(), null, "https://unblu-test.com/test111", null);
            //            System.out.println("Updated check2:");
            //            System.out.println(c2);
            //
            //            api.getExternalStatusCheckApi()
            //                    .deleteExternalStatusCheck(GITLAB_PROJECT, c2.getId());

            List<ExternalStatusCheckStatus> statuses = api.getExternalStatusCheckApi()
                    .getExternalStatusCheckStatuses(GITLAB_PROJECT, GITLAB_MR);
            System.out.println(statuses);

            Long checkId = checks.get(0)
                    .getId();

            MergeRequest mergeRequest = api.getMergeRequestApi()
                    .getMergeRequest(project, GITLAB_MR);
            api.getExternalStatusCheckApi()
                    .setStatusOfExternalStatusCheck(GITLAB_PROJECT, GITLAB_MR, mergeRequest.getSha(), checkId, Status.FAILED);

            api.getExternalStatusCheckApi()
                    .retryExternalStatusCheck(GITLAB_PROJECT, GITLAB_MR, checkId);
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

///usr/bin/env jbang "$0" "$@" ; exit $?

//DEPS org.gitlab4j:gitlab4j-api:5.4.0
//JAVA 17

import java.util.logging.Level;
import java.util.logging.Logger;

import org.gitlab4j.api.*;
import org.gitlab4j.api.models.*;

class GitlabScript {

    public static void main(String... args) throws Exception {
        if(args.length != 1) {
            System.out.println("Project id parameter is missing");
            System.exit(1);
        }
        String projectId = args[0];

        try (GitLabApi gitLabApi = new GitLabApi("https://gitlab.com", null)) {
            Logger one = Logger.getLogger(GitlabScript.class.getName());
            gitLabApi.enableRequestResponseLogging(one, Level.INFO, 4096);

            Project project = gitLabApi.getProjectApi()
                    .getProject(idOrPath(projectId));
            System.out.println(project);
        }
    }

    private static Object idOrPath(String value) {
        if (value.matches("[0-9]+")) {
            return Long.valueOf(value);
        }
        return value;
    }
}

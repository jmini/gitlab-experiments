///usr/bin/env jbang "$0" "$@" ; exit $?

//DEPS org.testcontainers:testcontainers:1.19.0
//DEPS org.slf4j:slf4j-simple:1.7.36
//JAVA 17

import java.time.Duration;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.util.concurrent.TimeUnit;

import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.containers.wait.strategy.WaitStrategy;
import org.testcontainers.containers.output.*;
import org.testcontainers.utility.DockerImageName;
import org.testcontainers.containers.Container.ExecResult;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RunGitlab {

    public static void main(String... args) throws Exception {
        Logger logger = LoggerFactory.getLogger(RunGitlab.class);
        Slf4jLogConsumer logConsumer = new Slf4jLogConsumer(logger);
  
        GenericContainer<?> gitlab = new GenericContainer<>(DockerImageName.parse("gitlab/gitlab-ce:15.4.2-ce.0"))
			.withEnv("GITLAB_OMNIBUS_CONFIG", "gitlab_rails['initial_root_password']=\"Pass_w0rd\";gitlab_rails['lfs_enabled']=false;external_url 'http://localhost:8090';gitlab_rails['gitlab_shell_ssh_port'] = 9022")
            .withExposedPorts(8090)
            //.withLogConsumer(logConsumer)
            .waitingFor(
				Wait
                    .forHttp("/")
                    .forStatusCode(302)
                    .forStatusCode(200)
                    .withStartupTimeout(Duration.ofSeconds(3600))
            );

            gitlab.start();

            String rootUserToken = "tk-" + System.currentTimeMillis();
            try {
                ExecResult r = gitlab.execInContainer("bash", "-c", "gitlab-rails runner \"token = User.find_by_username('root').personal_access_tokens.create(scopes: ['api'], name: 'Test token', expires_at: 365.days.from_now); token.set_token('"+ rootUserToken + "'); token.save!\"");
                System.out.println("ExecResult: " + r.getExitCode());
			    System.out.println(r.getStdout());
			    System.out.println(r.getStderr());
            } catch (UnsupportedOperationException | IOException | InterruptedException e) {
                e.printStackTrace();
            }
            
            // TimeUnit.SECONDS.sleep(10);

            HttpRequest request = HttpRequest.newBuilder()
                .header("PRIVATE-TOKEN", rootUserToken)
                .uri(new URI("http://localhost:" + gitlab.getMappedPort(8090) + "/api/v4/users/1"))
                .GET()
                .build();
                
            HttpClient client = HttpClient.newHttpClient();
            HttpResponse<String> response = client.send(request, BodyHandlers.ofString());
            String content = response.body();
            int code = response.statusCode();
            if (!(code >= 200 && code < 300)) {
                System.out.println("Error while performing the call. Response:");
                //System.out.println(content);
                //throw new IllegalStateException("Unexpected status code. " + code);
            }
            System.out.println(content);

            //Keep Jbang script running forever:
            Thread.currentThread().join();
    }
}

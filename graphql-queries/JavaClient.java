///usr/bin/env jbang "$0" "$@" ; exit $?

//DEPS io.smallrye:smallrye-graphql-client-implementation-vertx:2.10.0
//DEPS org.eclipse:yasson:2.0.4

//JAVA 17

import java.nio.file.Files;
import java.nio.file.Paths;

import io.smallrye.graphql.client.dynamic.api.DynamicGraphQLClient;
import io.smallrye.graphql.client.Response;
import io.smallrye.graphql.client.vertx.dynamic.VertxDynamicGraphQLClientBuilder;

import io.vertx.core.Vertx;

class JavaClient {
    public static void main(String... args) throws Exception {
        String query = Files.readString(Paths.get("work-items.query.graphql"));
        Vertx vertx = Vertx.vertx();
        DynamicGraphQLClient client = new VertxDynamicGraphQLClientBuilder()
            .url("https://gitlab.com/api/graphql")
            .vertx(vertx)
            .build();
        try {
            Response response = client.executeSync(query);
            System.out.println(response);
        } finally {
            client.close();
            vertx.close();
        }
    }
}

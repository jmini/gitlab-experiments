package org.acme;

import java.util.List;

import org.junit.jupiter.api.Test;

import gitlab.api.WorkitemClientApi;
import gitlab.model.WorkItemConnection;
import io.smallrye.graphql.client.typesafe.api.TypesafeGraphQLClientBuilder;

class RunTest {

    @Test
    void test() {
        WorkitemClientApi api = TypesafeGraphQLClientBuilder.newBuilder()
                .allowUnexpectedResponseFields(true)
                .build(WorkitemClientApi.class);

        String p = "tech-marketing/demos/gitlab-agile-demo/initech";
        List<String> r = List.of(
                "tech-marketing/demos/gitlab-agile-demo/initech/music-store&2",
                "tech-marketing/demos/gitlab-agile-demo/initech&2",
                "tech-marketing/demos/gitlab-agile-demo/initech&5",
                "tech-marketing/demos/gitlab-agile-demo/initech/music-store/parent-portal#2");
        WorkItemConnection response = api.workItemsByReference(p, r);
        System.out.println("response: " + response);
    }

}
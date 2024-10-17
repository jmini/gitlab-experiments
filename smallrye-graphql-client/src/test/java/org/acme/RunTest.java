package org.acme;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Objects;
import java.util.Properties;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import gitlab.api.WorkitemClientApi;
import gitlab.model.Namespace;
import gitlab.model.WorkItemConnection;
import gitlab.model.WorkItemCreateInput;
import gitlab.model.WorkItemCreatePayload;
import gitlab.model.WorkItemType;
import gitlab.model.WorkItemUpdateInput;
import gitlab.model.WorkItemUpdatePayload;
import gitlab.model.WorkItemWidgetDescriptionInput;
import gitlab.model.WorkItemsTypeID;
import io.smallrye.graphql.client.typesafe.api.TypesafeGraphQLClientBuilder;

class RunTest {

    @Test
    @Disabled
    void read_GitlabCom() {
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

    @Test
    void read_configuredGitlab() {
        Path file = configFile(Paths.get(""));
        System.out.println("Reading config: " + file.toAbsolutePath());
        final Properties prop = configProperties(file);
        final String gitLabUrl = readProperty(prop, "GITLAB_URL", "https://gitlab.com/api/graphql");
        final String gitLabAuthValue = readProperty(prop, "GITLAB_AUTH_VALUE", null);

        TypesafeGraphQLClientBuilder builder = TypesafeGraphQLClientBuilder.newBuilder()
                .endpoint(gitLabUrl)
                .allowUnexpectedResponseFields(true);
        if (gitLabAuthValue != null) {
            builder.header("Authorization", "Bearer " + gitLabAuthValue);
        }
        WorkitemClientApi api = builder.build(WorkitemClientApi.class);

        String owner = readProperty(prop, "OWNER");
        List<String> ref = List.of(readProperty(prop, "WORKITEM_REF"));
        WorkItemConnection response = api.workItemsByReference(owner, ref);
        System.out.println("response: " + response);
    }

    @Test
    void createAndUpdate_configuredGitlab() {
        Path file = configFile(Paths.get(""));
        System.out.println("Reading config: " + file.toAbsolutePath());
        final Properties prop = configProperties(file);
        final String gitLabUrl = readProperty(prop, "GITLAB_URL", "https://gitlab.com/api/graphql");
        final String gitLabAuthValue = readProperty(prop, "GITLAB_AUTH_VALUE", null);

        TypesafeGraphQLClientBuilder builder = TypesafeGraphQLClientBuilder.newBuilder()
                .endpoint(gitLabUrl)
                .allowUnexpectedResponseFields(true);
        if (gitLabAuthValue != null) {
            builder.header("Authorization", "Bearer " + gitLabAuthValue);
        }
        WorkitemClientApi api = builder.build(WorkitemClientApi.class);

        String owner = readProperty(prop, "OWNER");
        String workItemType = readProperty(prop, "WORKITEM_TYPE");
        Namespace namespace = api.namespace(owner);
        WorkItemsTypeID workItemTypeId = namespace.getWorkItemTypes()
                .getNodes()
                .stream()
                .filter(t -> Objects.equals(t.getName(), workItemType))
                .findAny()
                .map(WorkItemType::getId)
                .orElseThrow(() -> new IllegalStateException("Could not find workItemType corresponding to " + workItemType));

        WorkItemCreateInput createInput = new WorkItemCreateInput()
                .setTitle("Test " + System.currentTimeMillis())
                .setNamespacePath(owner)
                .setWorkItemTypeId(workItemTypeId)
                .setDescriptionWidget(new WorkItemWidgetDescriptionInput()
                        .setDescription("Test description " + System.currentTimeMillis()));
        api.workItemCreate(createInput);
        WorkItemCreatePayload createResponse = api.workItemCreate(createInput);
        System.out.println("createResponse: " + createResponse);

        WorkItemUpdateInput updateInput = new WorkItemUpdateInput()
                .setId(createResponse.getWorkItem()
                        .getId())
                .setDescriptionWidget(new WorkItemWidgetDescriptionInput()
                        .setDescription("Updated description " + System.currentTimeMillis()));
        WorkItemUpdatePayload updateResponse = api.workItemUpdate(updateInput);
        System.out.println("updateResponse: " + updateResponse);

        System.out.println("URL: " + createResponse.getWorkItem()
                .getWebUrl());

    }

    private static final String CONFIG_FILE_INITIAL_CONTENT = """
            GITLAB_URL=https://gitlab.com/api/graphql
            GITLAB_AUTH_VALUE=
            OWNER=tech-marketing/demos/gitlab-agile-demo/initech
            WORKITEM_TYPE=Epic
            WORKITEM_REF=#3
            """;

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

}
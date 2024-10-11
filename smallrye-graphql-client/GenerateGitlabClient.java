package codegen;

///usr/bin/env jbang "$0" "$@" ; exit $?

//DEPS fr.jmini.graphql:graphql-client-generator:1.0.0-SNAPSHOT

//JAVA 17
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import fr.jmini.gdeenco.util.CodeUtil;
import fr.jmini.gdeenco.util.FileUtil;
import fr.jmini.gql.codegen.Generator;
import fr.jmini.gql.codegen.config.ArgsFilter;
import fr.jmini.gql.codegen.config.Config;
import fr.jmini.gql.codegen.config.CustomScalarMappingStrategy;
import fr.jmini.gql.codegen.config.FieldsFilter;
import fr.jmini.gql.codegen.config.GraphQLClientApiAnnotation;
import fr.jmini.gql.codegen.config.IncludeStrategy;
import fr.jmini.gql.codegen.config.Scope;
import fr.jmini.gql.codegen.config.TypesFilter;
import fr.jmini.gql.schema.model.IntrospectionResponse;
import fr.jmini.gql.schema.model.Kind;
import fr.jmini.gql.schema.model.Schema;

class GenerateGitlabClient {

    public static void main(String... args) throws IOException {
        Path schemaFile = Paths.get("gitlab-graphqlschema.json");
        ObjectMapper mapper = createMapper();
        Schema schema = getSchema(mapper, schemaFile);

        Config config = createConfig(schema);

        Path sourceFolder = Paths.get("src/main/java");
        FileUtil.deleteFolder(CodeUtil.toPackageFolder(sourceFolder, config.getModelPackageName()));
        FileUtil.deleteFolder(CodeUtil.toPackageFolder(sourceFolder, config.getClientApiPackageName()));
        Generator.generateCode(sourceFolder, config);
        System.out.println("DONE");
    }

    public static Config createConfig(Schema schema) {
        Config config = new Config()
                .setSchema(schema)
                .setDefaultCustomScalarMapping(CustomScalarMappingStrategy.MAP_TO_STRING)
                .setScope(new Scope()
                        .setDefaultStrategy(IncludeStrategy.INCLUDE_NONE)
                        .addFilter(new TypesFilter()
                                .setTypeKind(Kind.OBJECT)
                                .addIncludeName("WorkItem")
                                .addIncludeName("WorkItemConnection")
                                .addIncludeName("UserCore")
                                .addIncludeName("WorkItemType")
                                .addIncludeName("WorkItemWidgetAssignees")
                                .addIncludeName("WorkItemWidgetAwardEmoji")
                                .addIncludeName("WorkItemWidgetColor")
                                .addIncludeName("WorkItemWidgetCrmContacts")
                                .addIncludeName("WorkItemWidgetCurrentUserTodos")
                                .addIncludeName("WorkItemWidgetDescription")
                                .addIncludeName("WorkItemWidgetDesigns")
                                .addIncludeName("WorkItemWidgetDevelopment")
                                .addIncludeName("WorkItemWidgetHealthStatus")
                                .addIncludeName("WorkItemWidgetHierarchy")
                                .addIncludeName("WorkItemWidgetIteration")
                                .addIncludeName("WorkItemWidgetLabels")
                                .addIncludeName("WorkItemWidgetLinkedItems")
                                .addIncludeName("WorkItemWidgetMilestone")
                                .addIncludeName("WorkItemWidgetNotes")
                                .addIncludeName("WorkItemWidgetNotifications")
                                .addIncludeName("WorkItemWidgetParticipants")
                                .addIncludeName("WorkItemWidgetRolledupDates")
                                .addIncludeName("WorkItemWidgetStartAndDueDate")
                                .addIncludeName("WorkItemWidgetStatus")
                                .addIncludeName("WorkItemWidgetTimeTracking")
                                .addIncludeName("WorkItemWidgetWeight")
                                .addIncludeName("UserCoreConnection")
                                .addIncludeName("LabelConnection")
                                .addIncludeName("Label")
                                .addIncludeName("Namespace") //
                                .addIncludeName("AwardEmojiConnection") //
                                .addIncludeName("TodoConnection") //
                                // .addIncludeName("DesignCollection") //
                                .addIncludeName("TaskCompletionStatus") //
                                .addIncludeName("WorkItemClosingMergeRequestConnection") //
                                .addIncludeName("FeatureFlagConnection") //
                                // .addIncludeName("Iteration") //
                                .addIncludeName("LinkedWorkItemTypeConnection") //
                                // .addIncludeName("Milestone") //
                                .addIncludeName("DiscussionConnection") //
                                .addIncludeName("WorkItemTimelogConnection") //
                                .addIncludeName("WorkItemWidgetDefinitionWeight") //
                                .addIncludeName("AwardEmoji") //
                                // .addIncludeName("Design") //
                                // .addIncludeName("DesignAtVersion") //
                                // .addIncludeName("DesignConnection") //
                                // .addIncludeName("DesignVersion") //
                                // .addIncludeName("DesignVersionConnection") //
                                .addIncludeName("Discussion") //
                                .addIncludeName("FeatureFlag") //
                                .addIncludeName("IterationCadence") //
                                .addIncludeName("TimeboxReport") //
                                .addIncludeName("Group") //
                                .addIncludeName("Project") //
                                .addIncludeName("ReleaseConnection") //
                                .addIncludeName("Todo") //
                                .addIncludeName("WorkItemClosingMergeRequest") //
                                .addIncludeName("WorkItemTimelog") //
                                .addIncludeName("LinkedWorkItemType") //
                                // .addIncludeName("NoteableType") //
                                .addIncludeName("NoteConnection") //
                                .addIncludeName("Note") //
                        )//
                        .addFilter(new TypesFilter()
                                .setTypeKind(Kind.INTERFACE)
                                // .addIncludeName("User") //
                                // .addIncludeName("Todoable") //
                                // .addIncludeName("TimeboxReportInterface") //
                                // .addIncludeName("CurrentUserTodos") //
                                // .addIncludeName("WorkItemWidgetDefinition") //
                                .addIncludeName("WorkItemWidget") //
                        ) //
                        //                        .addFilter(new TypesFilter()
                        // .setTypeKind(Kind.UNION) //
                        // .addIncludeName("Issuable")) //
                        .addFilter(new TypesFilter()
                                .setTypeKind(Kind.ENUM)
                                .addIncludeName("HealthStatus") //
                                .addIncludeName("WorkItemState") //
                                .addIncludeName("WorkItemWidgetType") //
                                .addIncludeName("MilestoneStateEnum") //
                        ) //
                        .addFilter(new FieldsFilter()
                                .setTypeKind(Kind.OBJECT)
                                .setTypeName(schema.getQueryType()
                                        .getName())
                                .addIncludeName("workItemsByReference") //
                        ) //
                        .addFilter(new ArgsFilter()
                                .setTypeKind(Kind.OBJECT)
                                .setTypeName(schema.getQueryType()
                                        .getName())
                                .setFieldName("workItemsByReference") //
                                .addIncludeName("contextNamespacePath") //
                                .addIncludeName("refs") //
                        ) //
                        .addFilter(new FieldsFilter()
                                .setTypeKind(Kind.OBJECT)
                                .setTypeName("WorkItemConnection")
                                .addIncludeName("count") //
                                .addIncludeName("nodes") //
                        ) //
                        .addFilter(new FieldsFilter()
                                .setTypeKind(Kind.OBJECT)
                                .setTypeName("WorkItem")
                                .addIncludeName("archived") //
                                // .addIncludeName("author") //
                                .addIncludeName("closedAt") //
                                .addIncludeName("confidential") //
                                .addIncludeName("createdAt") //
                                .addIncludeName("id") //
                                .addIncludeName("iid") //
                                .addIncludeName("lockVersion") //
                                .addIncludeName("name") //
                                .addIncludeName("namespace") //
                                .addIncludeName("reference") //
                                .addIncludeName("state") //
                                .addIncludeName("title") //
                                .addIncludeName("updatedAt") //
                                .addIncludeName("webUrl") //
                                .addIncludeName("widgets") //
                                .addIncludeName("workItemType") //
                        ) //
                        .addFilter(new FieldsFilter()
                                .setTypeKind(Kind.OBJECT)
                                .setTypeName("Namespace")
                                .addIncludeName("id") //
                                // .addIncludeName("name") //
                                // .addIncludeName("path") //
                                .addIncludeName("visibility") //
                                // .addIncludeName("description") //
                                // .addIncludeName("fullName") //
                                .addIncludeName("fullPath") //
                                .addIncludeName("visibility") //
                        ) //
                        .addFilter(new FieldsFilter()
                                .setTypeKind(Kind.OBJECT)
                                .setTypeName("UserCore")
                                .addIncludeName("id") //
                                .addIncludeName("username") //
                                // .addIncludeName("webUrl") //
                                // .addIncludeName("publicEmail") //
                                .addIncludeName("active") //
                        ) //
                        .addFilter(new FieldsFilter()
                                .setTypeKind(Kind.OBJECT)
                                .setTypeName("UserCoreConnection")
                                .addIncludeName("nodes") //
                        ) //
                        .addFilter(new FieldsFilter()
                                .setTypeKind(Kind.INTERFACE)
                                .setTypeName("WorkItemWidget")) //
                        .addFilter(new FieldsFilter()
                                .setTypeKind(Kind.OBJECT)
                                .setTypeName("WorkItemWidgetAssignees")
                                .addIncludeName("assignees") //
                                .addIncludeName("type") //
                        ) //
                        .addFilter(new FieldsFilter()
                                .setTypeKind(Kind.OBJECT)
                                .setTypeName("WorkItemWidgetAwardEmoji")
                                // .addIncludeName("awardEmoji") //
                                // .addIncludeName("downvotes") //
                                // .addIncludeName("upvotes") //
                                .addIncludeName("type") //
                        ) //
                        .addFilter(new FieldsFilter()
                                .setTypeKind(Kind.OBJECT)
                                .setTypeName("WorkItemWidgetColor")
                                // .addIncludeName("color") //
                                // .addIncludeName("textColor") //
                                .addIncludeName("type") //
                        ) //
                        .addFilter(new FieldsFilter()
                                .setTypeKind(Kind.OBJECT)
                                .setTypeName("WorkItemWidgetCrmContacts")
                                .addIncludeName("type") //
                        ) //
                        .addFilter(new FieldsFilter()
                                .setTypeKind(Kind.OBJECT)
                                .setTypeName("WorkItemWidgetCurrentUserTodos")
                                // .addIncludeName("currentUserTodos") //
                                .addIncludeName("type") //
                        ) //
                        .addFilter(new FieldsFilter()
                                .setTypeKind(Kind.OBJECT)
                                .setTypeName("WorkItemWidgetDescription")
                                // .addIncludeName("type") //
                                // .addIncludeName("edited") //
                                // .addIncludeName("lastEditedAt") //
                                // .addIncludeName("lastEditedBy") //
                                // .addIncludeName("taskCompletionStatus") //
                                .addIncludeName("description") //
                        ) //
                        .addFilter(new FieldsFilter()
                                .setTypeKind(Kind.OBJECT)
                                .setTypeName("WorkItemWidgetDesigns")
                                // .addIncludeName("designCollection") //
                                .addIncludeName("type") //
                        ) //
                        .addFilter(new FieldsFilter()
                                .setTypeKind(Kind.OBJECT)
                                .setTypeName("WorkItemWidgetDevelopment")
                                // .addIncludeName("closingMergeRequests") //
                                // .addIncludeName("featureFlags") //
                                // .addIncludeName("willAutoCloseByMergeRequest") //
                                .addIncludeName("type") //
                        ) //
                        .addFilter(new FieldsFilter()
                                .setTypeKind(Kind.OBJECT)
                                .setTypeName("WorkItemWidgetHealthStatus")
                                //.addIncludeName("healthStatus") //
                                .addIncludeName("type") //
                        ) //
                        .addFilter(new FieldsFilter()
                                .setTypeKind(Kind.OBJECT)
                                .setTypeName("WorkItemWidgetHierarchy")
                                // .addIncludeName("type") //
                                .addIncludeName("ancestors") //
                                .addIncludeName("children") //
                                // .addIncludeName("depthLimitReachedByType") //
                                // .addIncludeName("hasChildren") //
                                // .addIncludeName("hasParent") //
                                .addIncludeName("parent") //
                        //.addIncludeName("rolledUpCountsByType") //
                        ) //
                        .addFilter(new FieldsFilter()
                                .setTypeKind(Kind.OBJECT)
                                .setTypeName("WorkItemWidgetIteration")
                                .addIncludeName("type") //
                        // .addIncludeName("iteration") //
                        ) //
                        .addFilter(new FieldsFilter()
                                .setTypeKind(Kind.OBJECT)
                                .setTypeName("WorkItemWidgetLabels")
                                // .addIncludeName("allowsScopedLabels") //
                                // .addIncludeName("labels") //
                                .addIncludeName("type") //
                        ) //
                        .addFilter(new FieldsFilter()
                                .setTypeKind(Kind.OBJECT)
                                .setTypeName("WorkItemWidgetLinkedItems")
                                // .addIncludeName("blocked") //
                                // .addIncludeName("blockedByCount") //
                                // .addIncludeName("blockingCount") //
                                .addIncludeName("linkedItems") //
                                .addIncludeName("type") //
                        ) //
                        .addFilter(new FieldsFilter()
                                .setTypeKind(Kind.OBJECT)
                                .setTypeName("WorkItemWidgetMilestone")
                                // .addIncludeName("milestone") //
                                .addIncludeName("type") //
                        ) //
                        .addFilter(new FieldsFilter()
                                .setTypeKind(Kind.OBJECT)
                                .setTypeName("WorkItemWidgetNotes")
                                // .addIncludeName("discussionLocked") //
                                // .addIncludeName("discussions") //
                                .addIncludeName("type") //
                        ) //
                        .addFilter(new FieldsFilter()
                                .setTypeKind(Kind.OBJECT)
                                .setTypeName("WorkItemWidgetNotifications")
                                // .addIncludeName("subscribed") //
                                .addIncludeName("type") //
                        ) //
                        .addFilter(new FieldsFilter()
                                .setTypeKind(Kind.OBJECT)
                                .setTypeName("WorkItemWidgetParticipants")
                                .addIncludeName("type") //
                        // .addIncludeName("participants")
                        ) //
                        .addFilter(new FieldsFilter()
                                .setTypeKind(Kind.OBJECT)
                                .setTypeName("WorkItemWidgetRolledupDates")
                                // .addIncludeName("dueDate") //
                                // .addIncludeName("dueDateFixed") //
                                // .addIncludeName("dueDateIsFixed") //
                                // .addIncludeName("dueDateSourcingMilestone") //
                                // .addIncludeName("dueDateSourcingWorkItem") //
                                // .addIncludeName("startDate") //
                                // .addIncludeName("startDateFixed") //
                                // .addIncludeName("startDateIsFixed") //
                                // .addIncludeName("startDateSourcingMilestone") //
                                // .addIncludeName("startDateSourcingWorkItem") //
                                .addIncludeName("type") //
                        ) //
                        .addFilter(new FieldsFilter()
                                .setTypeKind(Kind.OBJECT)
                                .setTypeName("WorkItemWidgetStartAndDueDate")
                                // .addIncludeName("dueDate") //
                                // .addIncludeName("dueDateSourcingMilestone") //
                                // .addIncludeName("dueDateSourcingWorkItem") //
                                // .addIncludeName("isFixed") //
                                // .addIncludeName("rollUp") //
                                // .addIncludeName("startDate") //
                                // .addIncludeName("startDateSourcingMilestone") //
                                // .addIncludeName("startDateSourcingWorkItem") //
                                .addIncludeName("type") //
                        ) //
                        .addFilter(new FieldsFilter()
                                .setTypeKind(Kind.OBJECT)
                                .setTypeName("WorkItemWidgetStatus")
                                .addIncludeName("status") //
                        ) //
                        .addFilter(new FieldsFilter()
                                .setTypeKind(Kind.OBJECT)
                                .setTypeName("WorkItemWidgetTimeTracking")
                                // .addIncludeName("timeEstimate") //
                                // .addIncludeName("timelogs") //
                                // .addIncludeName("totalTimeSpent") //
                                .addIncludeName("type") //
                        ) //
                        .addFilter(new FieldsFilter()
                                .setTypeKind(Kind.OBJECT)
                                .setTypeName("WorkItemWidgetWeight")
                                // .addIncludeName("rolledUpCompletedWeight") //
                                // .addIncludeName("rolledUpWeight") //
                                // .addIncludeName("weight") //
                                // .addIncludeName("widgetDefinition") //
                                .addIncludeName("type") //
                        ) //
                        .addFilter(new FieldsFilter()
                                .setTypeKind(Kind.OBJECT)
                                .setTypeName("LabelConnection")
                                .addIncludeName("nodes") //
                        ) //
                        .addFilter(new FieldsFilter()
                                .setTypeKind(Kind.OBJECT)
                                .setTypeName("Label")
                                .addIncludeName("id") //
                                .addIncludeName("title") //
                                .addIncludeName("description") //
                                .addIncludeName("color") //
                                .addIncludeName("textColor") //
                        ) //
                        .addFilter(new FieldsFilter()
                                .setTypeKind(Kind.OBJECT)
                                .setTypeName("WorkItemType")
                                .addIncludeName("id") //
                                .addIncludeName("name") //
                        ) //
                        .addFilter(new FieldsFilter()
                                .setTypeKind(Kind.OBJECT)
                                .setTypeName("AwardEmojiConnection")
                                .addIncludeName("nodes") //
                        ) //
                        .addFilter(new FieldsFilter()
                                .setTypeKind(Kind.OBJECT)
                                .setTypeName("DesignCollection")
                                .addIncludeName("design") //
                                .addIncludeName("designAtVersion") //
                                .addIncludeName("designs") //
                                .addIncludeName("version") //
                                .addIncludeName("versions") //
                        ) //
                        .addFilter(new FieldsFilter()
                                .setTypeKind(Kind.OBJECT)
                                .setTypeName("DiscussionConnection")
                                .addIncludeName("nodes") //
                        ) //
                        .addFilter(new FieldsFilter()
                                .setTypeKind(Kind.OBJECT)
                                .setTypeName("FeatureFlagConnection")
                                .addIncludeName("nodes") //
                        ) //
                        .addFilter(new FieldsFilter()
                                .setTypeKind(Kind.OBJECT)
                                .setTypeName("Iteration")
                                .addIncludeName("createdAt") //
                                .addIncludeName("description") //
                                .addIncludeName("dueDate") //
                                .addIncludeName("id") //
                                .addIncludeName("iid") //
                                .addIncludeName("iterationCadence") //
                                .addIncludeName("report") //
                                .addIncludeName("scopedPath") //
                                .addIncludeName("scopedUrl") //
                                .addIncludeName("sequence") //
                                .addIncludeName("startDate") //
                                .addIncludeName("state") //
                                .addIncludeName("title") //
                                .addIncludeName("updatedAt") //
                                .addIncludeName("webPath") //
                                .addIncludeName("webUrl") //
                        ) //
                        .addFilter(new FieldsFilter()
                                .setTypeKind(Kind.OBJECT)
                                .setTypeName("LinkedWorkItemTypeConnection")
                                .addIncludeName("nodes") //
                        ) //
                        .addFilter(new FieldsFilter()
                                .setTypeKind(Kind.OBJECT)
                                .setTypeName("Milestone")
                                .addIncludeName("createdAt") //
                                .addIncludeName("description") //
                                .addIncludeName("dueDate") //
                                .addIncludeName("expired") //
                                .addIncludeName("group") //
                                .addIncludeName("id") //
                                .addIncludeName("iid") //
                                .addIncludeName("project") //
                                .addIncludeName("releases") //
                                .addIncludeName("report") //
                                .addIncludeName("startDate") //
                                .addIncludeName("state") //
                                .addIncludeName("stats") //
                                .addIncludeName("title") //
                                .addIncludeName("upcoming") //
                                .addIncludeName("updatedAt") //
                                .addIncludeName("webPath") //
                        ) //
                        .addFilter(new FieldsFilter()
                                .setTypeKind(Kind.OBJECT)
                                .setTypeName("TaskCompletionStatus")
                                .addIncludeName("completedCount") //
                                .addIncludeName("count") //
                        ) //
                        .addFilter(new FieldsFilter()
                                .setTypeKind(Kind.OBJECT)
                                .setTypeName("TodoConnection")
                                .addIncludeName("nodes") //
                        ) //
                        .addFilter(new FieldsFilter()
                                .setTypeKind(Kind.OBJECT)
                                .setTypeName("WorkItemClosingMergeRequestConnection")
                                .addIncludeName("nodes") //
                        ) //
                        .addFilter(new FieldsFilter()
                                .setTypeKind(Kind.OBJECT)
                                .setTypeName("WorkItemTimelogConnection")
                                .addIncludeName("nodes") //
                                .addIncludeName("totalSpentTime") //
                        ) //
                        .addFilter(new FieldsFilter()
                                .setTypeKind(Kind.OBJECT)
                                .setTypeName("WorkItemWidgetDefinitionWeight")
                                .addIncludeName("type") //
                        ) //
                        .addFilter(new FieldsFilter()
                                .setTypeKind(Kind.OBJECT)
                                .setTypeName("AwardEmoji")
                                .addIncludeName("description") //
                                .addIncludeName("emoji") //
                                .addIncludeName("name") //
                                .addIncludeName("unicode") //
                                .addIncludeName("unicodeVersion") //
                                .addIncludeName("user") //
                        ) //
                        .addFilter(new FieldsFilter()
                                .setTypeKind(Kind.OBJECT)
                                .setTypeName("Design")
                                .addIncludeName("commenters") //
                                .addIncludeName("currentUserTodos") //
                                .addIncludeName("description") //
                                .addIncludeName("descriptionHtml") //
                                .addIncludeName("diffRefs") //
                                .addIncludeName("discussions") //
                                .addIncludeName("event") //
                                .addIncludeName("filename") //
                                .addIncludeName("fullPath") //
                                .addIncludeName("id") //
                                .addIncludeName("image") //
                                .addIncludeName("imageV432x230") //
                                .addIncludeName("imported") //
                                .addIncludeName("importedFrom") //
                                .addIncludeName("issue") //
                                .addIncludeName("name") //
                                .addIncludeName("notes") //
                                .addIncludeName("notesCount") //
                                .addIncludeName("project") //
                                .addIncludeName("versions") //
                                .addIncludeName("webUrl") //
                        ) //
                        .addFilter(new FieldsFilter()
                                .setTypeKind(Kind.OBJECT)
                                .setTypeName("DesignAtVersion")
                                .addIncludeName("design") //
                                .addIncludeName("diffRefs") //
                                .addIncludeName("event") //
                                .addIncludeName("filename") //
                                .addIncludeName("fullPath") //
                                .addIncludeName("id") //
                                .addIncludeName("image") //
                                .addIncludeName("imageV432x230") //
                                .addIncludeName("issue") //
                                .addIncludeName("notesCount") //
                                .addIncludeName("project") //
                                .addIncludeName("version") //
                        ) //
                        .addFilter(new FieldsFilter()
                                .setTypeKind(Kind.OBJECT)
                                .setTypeName("DesignConnection")
                                .addIncludeName("edges") //
                                .addIncludeName("nodes") //
                                .addIncludeName("pageInfo") //
                        ) //
                        .addFilter(new FieldsFilter()
                                .setTypeKind(Kind.OBJECT)
                                .setTypeName("DesignVersion")
                                .addIncludeName("author") //
                                .addIncludeName("createdAt") //
                                .addIncludeName("designAtVersion") //
                                .addIncludeName("designs") //
                                .addIncludeName("designsAtVersion") //
                                .addIncludeName("id") //
                                .addIncludeName("sha") //
                        ) //
                        .addFilter(new FieldsFilter()
                                .setTypeKind(Kind.OBJECT)
                                .setTypeName("DesignVersionConnection")
                                .addIncludeName("edges") //
                                .addIncludeName("nodes") //
                                .addIncludeName("pageInfo") //
                        ) //
                        .addFilter(new FieldsFilter()
                                .setTypeKind(Kind.OBJECT)
                                .setTypeName("Discussion")
                                .addIncludeName("createdAt") //
                                .addIncludeName("id") //
                                // .addIncludeName("noteable") //
                                .addIncludeName("notes") //
                                .addIncludeName("replyId") //
                                .addIncludeName("resolvable") //
                                .addIncludeName("resolved") //
                                .addIncludeName("resolvedAt") //
                                .addIncludeName("resolvedBy") //
                        ) //
                        .addFilter(new FieldsFilter()
                                .setTypeKind(Kind.OBJECT)
                                .setTypeName("FeatureFlag")
                                .addIncludeName("active") //
                                .addIncludeName("id") //
                                .addIncludeName("name") //
                                .addIncludeName("path") //
                                .addIncludeName("reference") //
                        ) //
                        .addFilter(new FieldsFilter()
                                .setTypeKind(Kind.OBJECT)
                                .setTypeName("Group")
                                .addIncludeName("fullName") //
                                .addIncludeName("fullPath") //
                                .addIncludeName("id") //
                                .addIncludeName("name") //
                                .addIncludeName("webUrl") //
                        //XXX .addIncludeName("workItem") //
                        ) //
                        .addFilter(new FieldsFilter()
                                .setTypeKind(Kind.OBJECT)
                                .setTypeName("IterationCadence")
                                .addIncludeName("active") //
                                .addIncludeName("automatic") //
                                .addIncludeName("description") //
                                .addIncludeName("durationInWeeks") //
                                .addIncludeName("id") //
                                .addIncludeName("iterationsInAdvance") //
                                .addIncludeName("rollOver") //
                                .addIncludeName("startDate") //
                                .addIncludeName("title") //
                        ) //
                        .addFilter(new FieldsFilter()
                                .setTypeKind(Kind.OBJECT)
                                .setTypeName("LinkedWorkItemType")
                                .addIncludeName("linkCreatedAt") //
                                .addIncludeName("linkId") //
                                .addIncludeName("linkType") //
                                .addIncludeName("linkUpdatedAt") //
                                .addIncludeName("workItem") //
                        ) //
                        .addFilter(new FieldsFilter()
                                .setTypeKind(Kind.OBJECT)
                                .setTypeName("Project")
                                .addIncludeName("group") //
                                .addIncludeName("id") //
                                .addIncludeName("name") //
                                .addIncludeName("nameWithNamespace") //
                                .addIncludeName("namespace") //
                                .addIncludeName("path") //
                                .addIncludeName("webUrl") //
                        ) //
                        .addFilter(new FieldsFilter()
                                .setTypeKind(Kind.OBJECT)
                                .setTypeName("ReleaseConnection")
                                .addIncludeName("nodes") //
                        ) //
                        .addFilter(new FieldsFilter()
                                .setTypeKind(Kind.OBJECT)
                                .setTypeName("TimeboxReport")
                                .addIncludeName("burnupTimeSeries") //
                                .addIncludeName("error") //
                                .addIncludeName("stats") //
                        ) //
                        .addFilter(new FieldsFilter()
                                .setTypeKind(Kind.OBJECT)
                                .setTypeName("Todo")
                                .addIncludeName("action") //
                                .addIncludeName("author") //
                                .addIncludeName("body") //
                                .addIncludeName("createdAt") //
                                .addIncludeName("group") //
                                .addIncludeName("id") //
                                .addIncludeName("memberAccessType") //
                                .addIncludeName("note") //
                                .addIncludeName("project") //
                                .addIncludeName("snoozedUntil") //
                                .addIncludeName("state") //
                                .addIncludeName("target") //
                                .addIncludeName("targetEntity") //
                                .addIncludeName("targetType") //
                                .addIncludeName("targetUrl") //
                        ) //
                        .addFilter(new FieldsFilter()
                                .setTypeKind(Kind.OBJECT)
                                .setTypeName("WorkItemClosingMergeRequest")
                                .addIncludeName("fromMrDescription") //
                                .addIncludeName("id") //
                                .addIncludeName("mergeRequest") //
                        ) //
                        .addFilter(new FieldsFilter()
                                .setTypeKind(Kind.OBJECT)
                                .setTypeName("WorkItemTimelog")
                                .addIncludeName("id") //
                                .addIncludeName("note") //
                                .addIncludeName("spentAt") //
                                .addIncludeName("summary") //
                                .addIncludeName("timeSpent") //
                                .addIncludeName("user") //
                                .addIncludeName("userPermissions") //
                        ) //
                        .addFilter(new FieldsFilter()
                                .setTypeKind(Kind.OBJECT)
                                .setTypeName("NoteConnection")
                                .addIncludeName("nodes") //
                        ) //
                        .addFilter(new FieldsFilter()
                                .setTypeKind(Kind.OBJECT)
                                .setTypeName("Note")
                                .addIncludeName("author") //
                                .addIncludeName("awardEmoji") //
                                .addIncludeName("body") //
                                .addIncludeName("createdAt") //
                                .addIncludeName("discussion") //
                                .addIncludeName("id") //
                                .addIncludeName("internal") //
                                .addIncludeName("lastEditedAt") //
                                .addIncludeName("lastEditedBy") //
                                .addIncludeName("resolvable") //
                                .addIncludeName("resolved") //
                                .addIncludeName("resolvedAt") //
                                .addIncludeName("system") //
                                .addIncludeName("systemNoteIconName") //
                                // .addIncludeName("systemNoteMetadata") //
                                .addIncludeName("updatedAt") //
                                .addIncludeName("url") //
                        ) //
                )
                .setModelPackageName("gitlab.model")
                .setClientApiPackageName("gitlab.api")
                .setClientApiInterfaceName("WorkitemClientApi")
                .setGraphQLClientApiAnnotation(new GraphQLClientApiAnnotation() //
                        .setConfigKey("gitlab")//
                        .setEndpoint("https://gitlab.com/api/graphql") //
                );
        return config;
    }

    public static Schema getSchema(ObjectMapper mapper, Path file) {
        String originalContent = FileUtil.readFile(file);
        try {
            return mapper.readValue(originalContent, IntrospectionResponse.class)
                    .getSchema();
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("Could not read schema: " + file, e);
        }
    }

    private static ObjectMapper createMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.enable(SerializationFeature.INDENT_OUTPUT);
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, true);
        mapper.setVisibility(PropertyAccessor.ALL, Visibility.NONE);
        mapper.setVisibility(PropertyAccessor.FIELD, Visibility.ANY);
        return mapper;
    }

}

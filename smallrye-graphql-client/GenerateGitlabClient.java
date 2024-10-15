package codegen;

///usr/bin/env jbang "$0" "$@" ; exit $?

//DEPS fr.jmini.graphql:graphql-client-generator:1.0.0-SNAPSHOT

//JAVA 17
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;

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
import fr.jmini.gql.codegen.config.InputFieldsFilter;
import fr.jmini.gql.codegen.config.Scope;
import fr.jmini.gql.codegen.config.TypesFilter;
import fr.jmini.gql.schema.SchemaUtil;
import fr.jmini.gql.schema.model.Field;
import fr.jmini.gql.schema.model.InputValue;
import fr.jmini.gql.schema.model.IntrospectionResponse;
import fr.jmini.gql.schema.model.Kind;
import fr.jmini.gql.schema.model.Schema;
import fr.jmini.gql.schema.model.Type;
import fr.jmini.gql.schema.model.TypeRef;

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
        Type linkedWorkItemType = SchemaUtil.getTypeByKindAndName(schema, Kind.OBJECT, "LinkedWorkItemType");
        Field workItemField = SchemaUtil.getFieldByName(schema, linkedWorkItemType, "workItem");
        workItemField.getType()
                .setName("WorkItemRef");
        Type workItemWidgetHierarchy = SchemaUtil.getTypeByKindAndName(schema, Kind.OBJECT, "WorkItemWidgetHierarchy");
        Field ancestorsField = SchemaUtil.getFieldByName(schema, workItemWidgetHierarchy, "ancestors");
        ancestorsField.getType()
                .setName("WorkItemConnectionRef");
        Field childrenField = SchemaUtil.getFieldByName(schema, workItemWidgetHierarchy, "children");
        childrenField.getType()
                .setName("WorkItemConnectionRef");
        Field parentField = SchemaUtil.getFieldByName(schema, workItemWidgetHierarchy, "parent");
        parentField.getType()
                .setName("WorkItemRef");

        schema.getTypes()
                .add(createWorkItemConnectionRef());
        schema.getTypes()
                .add(createWorkItemRef());

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
                                .addIncludeName("WorkItemTypeConnection") //
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
                                // --- ADDITIONAL TYPES ---
                                .addIncludeName("WorkItemRef") //
                                .addIncludeName("WorkItemConnectionRef") //
                                // ---- MUTATION objects ----
                                .addIncludeName("WorkItemCreatePayload") //
                                .addIncludeName("WorkItemUpdatePayload") //

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
                                // ---- MUTATION objects ----
                                .addIncludeName("WorkItemStateEvent") //
                                .addIncludeName("RelativePositionType") //
                        ) //
                        .addFilter(new FieldsFilter()
                                .setTypeKind(Kind.OBJECT)
                                .setTypeName(schema.getQueryType()
                                        .getName())
                                .addIncludeName("workItemsByReference") //
                                .addIncludeName("namespace") //
                        ) //
                        .addFilter(new ArgsFilter()
                                .setTypeKind(Kind.OBJECT)
                                .setTypeName(schema.getQueryType()
                                        .getName())
                                .setFieldName("workItemsByReference") //
                                .addIncludeName("contextNamespacePath") //
                                .addIncludeName("refs") //
                        ) //
                        .addFilter(new ArgsFilter()
                                .setTypeKind(Kind.OBJECT)
                                .setTypeName(schema.getQueryType()
                                        .getName())
                                .setFieldName("namespace") //
                                .addIncludeName("fullPath") //
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
                                // .addIncludeName("name") // not present in 17.5.0
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
                                .addIncludeName("workItemTypes") //
                        ) //
                        .addFilter(new FieldsFilter()
                                .setTypeKind(Kind.OBJECT)
                                .setTypeName("WorkItemTypeConnection")
                                .addIncludeName("nodes") //
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
                        // --- ADDITIONAL TYPES ---
                        .addFilter(new FieldsFilter()
                                .setTypeKind(Kind.OBJECT)
                                .setTypeName("WorkItemRef")
                                .addIncludeName("archived") //
                                // .addIncludeName("author") //
                                .addIncludeName("confidential") //
                                .addIncludeName("createdAt") //
                                .addIncludeName("id") //
                                .addIncludeName("iid") //
                                // .addIncludeName("name") // not present in 17.5.0
                                .addIncludeName("namespace") //
                                .addIncludeName("reference") //
                                .addIncludeName("state") //
                                .addIncludeName("title") //
                                .addIncludeName("webUrl") //
                                .addIncludeName("workItemType") //
                        ) //
                        .addFilter(new FieldsFilter()
                                .setTypeKind(Kind.OBJECT)
                                .setTypeName("WorkItemConnectionRef")
                                .addIncludeName("count") //
                                .addIncludeName("nodes") //
                        ) //
                        // --- MUTATION ---
                        .addFilter(new FieldsFilter()
                                .setTypeKind(Kind.OBJECT)
                                .setTypeName(schema.getMutationType()
                                        .getName())
                                .addIncludeName("workItemCreate") //
                                .addIncludeName("workItemUpdate") //
                        ) //
                        .addFilter(new ArgsFilter()
                                .setTypeKind(Kind.OBJECT)
                                .setTypeName(schema.getMutationType()
                                        .getName())
                                .setFieldName("workItemCreate") //
                                .addIncludeName("input") //
                        ) //
                        .addFilter(new ArgsFilter()
                                .setTypeKind(Kind.OBJECT)
                                .setTypeName(schema.getMutationType()
                                        .getName())
                                .setFieldName("workItemUpdate") //
                                .addIncludeName("input") //
                        ) //
                        .addFilter(new TypesFilter()
                                .setTypeKind(Kind.INPUT_OBJECT)
                                .addIncludeName("WorkItemCreateInput") //
                                .addIncludeName("WorkItemUpdateInput") //
                                .addIncludeName("WorkItemWidgetAssigneesInput") //
                                .addIncludeName("WorkItemWidgetDescriptionInput") //
                                .addIncludeName("WorkItemWidgetAssigneesInput") //
                                .addIncludeName("WorkItemWidgetDescriptionInput") //
                                .addIncludeName("WorkItemWidgetHierarchyUpdateInput") //
                                .addIncludeName("WorkItemWidgetLabelsUpdateInput") //
                        ) //
                        .addFilter(new InputFieldsFilter()
                                .setTypeKind(Kind.INPUT_OBJECT)
                                .setTypeName("WorkItemCreateInput")
                                .addIncludeName("assigneesWidget") //
                                .addIncludeName("descriptionWidget") //
                                .addIncludeName("namespacePath") //
                                .addIncludeName("title") //
                                .addIncludeName("workItemTypeId") //
                        ) //
                        .addFilter(new FieldsFilter()
                                .setTypeKind(Kind.OBJECT)
                                .setTypeName("WorkItemCreatePayload")
                                .addIncludeName("errors") //
                                .addIncludeName("workItem") //
                        ) //
                        .addFilter(new InputFieldsFilter()
                                .setTypeKind(Kind.INPUT_OBJECT)
                                .setTypeName("WorkItemUpdateInput")
                                .addIncludeName("assigneesWidget") //
                                .addIncludeName("descriptionWidget") //
                                .addIncludeName("labelsWidget") //
                                .addIncludeName("title") //
                                .addIncludeName("id") //
                                .addIncludeName("stateEvent") //
                        ) //
                        .addFilter(new FieldsFilter()
                                .setTypeKind(Kind.OBJECT)
                                .setTypeName("WorkItemUpdatePayload")
                                .addIncludeName("errors") //
                                .addIncludeName("workItem") //
                        ) //
                        .addFilter(new InputFieldsFilter()
                                .setTypeKind(Kind.INPUT_OBJECT)
                                .setTypeName("WorkItemWidgetAssigneesInput")
                                .addIncludeName("assigneeIds") //
                        ) //
                        .addFilter(new InputFieldsFilter()
                                .setTypeKind(Kind.INPUT_OBJECT)
                                .setTypeName("WorkItemWidgetDescriptionInput")
                                .addIncludeName("description") //
                        ) //
                        .addFilter(new InputFieldsFilter()
                                .setTypeKind(Kind.INPUT_OBJECT)
                                .setTypeName("WorkItemWidgetHierarchyUpdateInput")
                                .addIncludeName("adjacentWorkItemId") //
                                .addIncludeName("childrenIds") //
                                .addIncludeName("parentId") //
                                .addIncludeName("relativePosition") //
                        ) //
                        .addFilter(new InputFieldsFilter()
                                .setTypeKind(Kind.INPUT_OBJECT)
                                .setTypeName("WorkItemWidgetLabelsUpdateInput")
                                .addIncludeName("addLabelIds") //
                                .addIncludeName("removeLabelIds") //
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

    public static Type createWorkItemRef() {
        TypeRef ofType001 = new TypeRef();
        ofType001.setKind(Kind.SCALAR);
        ofType001.setName("Boolean");
        TypeRef type001 = new TypeRef();
        type001.setKind(Kind.NON_NULL);
        type001.setOfType(ofType001);
        Field fields001 = new Field();
        fields001.setName("archived");
        fields001.setDescription("Whether the work item belongs to an archived project. Always false for group level work items. Introduced in GitLab 16.5: **Status**: Experiment.");
        fields001.setArgs(Arrays.asList());
        fields001.setType(type001);
        fields001.setIsDeprecated(true);
        fields001.setDeprecationReason("**Status**: Experiment. Introduced in GitLab 16.5.");
        TypeRef type002 = new TypeRef();
        type002.setKind(Kind.OBJECT);
        type002.setName("UserCore");
        Field fields002 = new Field();
        fields002.setName("author");
        fields002.setDescription("User that created the work item. Introduced in GitLab 15.9: **Status**: Experiment.");
        fields002.setArgs(Arrays.asList());
        fields002.setType(type002);
        fields002.setIsDeprecated(true);
        fields002.setDeprecationReason("**Status**: Experiment. Introduced in GitLab 15.9.");
        TypeRef type003 = new TypeRef();
        type003.setKind(Kind.SCALAR);
        type003.setName("Time");
        Field fields003 = new Field();
        fields003.setName("closedAt");
        fields003.setDescription("Timestamp of when the work item was closed.");
        fields003.setArgs(Arrays.asList());
        fields003.setType(type003);
        fields003.setIsDeprecated(false);
        TypeRef ofType005 = new TypeRef();
        ofType005.setKind(Kind.SCALAR);
        ofType005.setName("Boolean");
        TypeRef type004 = new TypeRef();
        type004.setKind(Kind.NON_NULL);
        type004.setOfType(ofType005);
        Field fields004 = new Field();
        fields004.setName("confidential");
        fields004.setDescription("Indicates the work item is confidential.");
        fields004.setArgs(Arrays.asList());
        fields004.setType(type004);
        fields004.setIsDeprecated(false);
        TypeRef type005 = new TypeRef();
        type005.setKind(Kind.SCALAR);
        type005.setName("String");
        Field fields005 = new Field();
        fields005.setName("createNoteEmail");
        fields005.setDescription("User specific email address for the work item.");
        fields005.setArgs(Arrays.asList());
        fields005.setType(type005);
        fields005.setIsDeprecated(false);
        TypeRef ofType008 = new TypeRef();
        ofType008.setKind(Kind.SCALAR);
        ofType008.setName("Time");
        TypeRef type006 = new TypeRef();
        type006.setKind(Kind.NON_NULL);
        type006.setOfType(ofType008);
        Field fields006 = new Field();
        fields006.setName("createdAt");
        fields006.setDescription("Timestamp of when the work item was created.");
        fields006.setArgs(Arrays.asList());
        fields006.setType(type006);
        fields006.setIsDeprecated(false);
        TypeRef type007 = new TypeRef();
        type007.setKind(Kind.SCALAR);
        type007.setName("String");
        Field fields007 = new Field();
        fields007.setName("description");
        fields007.setDescription("Description of the work item.");
        fields007.setArgs(Arrays.asList());
        fields007.setType(type007);
        fields007.setIsDeprecated(false);
        TypeRef type008 = new TypeRef();
        type008.setKind(Kind.SCALAR);
        type008.setName("String");
        Field fields008 = new Field();
        fields008.setName("descriptionHtml");
        fields008.setDescription("GitLab Flavored Markdown rendering of `description`");
        fields008.setArgs(Arrays.asList());
        fields008.setType(type008);
        fields008.setIsDeprecated(false);
        TypeRef ofType012 = new TypeRef();
        ofType012.setKind(Kind.SCALAR);
        ofType012.setName("WorkItemID");
        TypeRef type009 = new TypeRef();
        type009.setKind(Kind.NON_NULL);
        type009.setOfType(ofType012);
        Field fields009 = new Field();
        fields009.setName("id");
        fields009.setDescription("Global ID of the work item.");
        fields009.setArgs(Arrays.asList());
        fields009.setType(type009);
        fields009.setIsDeprecated(false);
        TypeRef ofType014 = new TypeRef();
        ofType014.setKind(Kind.SCALAR);
        ofType014.setName("String");
        TypeRef type010 = new TypeRef();
        type010.setKind(Kind.NON_NULL);
        type010.setOfType(ofType014);
        Field fields010 = new Field();
        fields010.setName("iid");
        fields010.setDescription("Internal ID of the work item.");
        fields010.setArgs(Arrays.asList());
        fields010.setType(type010);
        fields010.setIsDeprecated(false);
        TypeRef ofType016 = new TypeRef();
        ofType016.setKind(Kind.SCALAR);
        ofType016.setName("Int");
        TypeRef type011 = new TypeRef();
        type011.setKind(Kind.NON_NULL);
        type011.setOfType(ofType016);
        Field fields011 = new Field();
        fields011.setName("lockVersion");
        fields011.setDescription("Lock version of the work item. Incremented each time the work item is updated.");
        fields011.setArgs(Arrays.asList());
        fields011.setType(type011);
        fields011.setIsDeprecated(false);
        TypeRef type012 = new TypeRef();
        type012.setKind(Kind.SCALAR);
        type012.setName("String");
        Field fields012 = new Field();
        fields012.setName("name");
        fields012.setDescription("Name or title of this object.");
        fields012.setArgs(Arrays.asList());
        fields012.setType(type012);
        fields012.setIsDeprecated(false);
        TypeRef type013 = new TypeRef();
        type013.setKind(Kind.OBJECT);
        type013.setName("Namespace");
        Field fields013 = new Field();
        fields013.setName("namespace");
        fields013.setDescription("Namespace the work item belongs to. Introduced in GitLab 15.10: **Status**: Experiment.");
        fields013.setArgs(Arrays.asList());
        fields013.setType(type013);
        fields013.setIsDeprecated(true);
        fields013.setDeprecationReason("**Status**: Experiment. Introduced in GitLab 15.10.");
        TypeRef type014 = new TypeRef();
        type014.setKind(Kind.OBJECT);
        type014.setName("Project");
        Field fields014 = new Field();
        fields014.setName("project");
        fields014.setDescription("Project the work item belongs to. Introduced in GitLab 15.3: **Status**: Experiment.");
        fields014.setArgs(Arrays.asList());
        fields014.setType(type014);
        fields014.setIsDeprecated(true);
        fields014.setDeprecationReason("**Status**: Experiment. Introduced in GitLab 15.3.");
        TypeRef type015 = new TypeRef();
        type015.setKind(Kind.SCALAR);
        type015.setName("Boolean");
        InputValue args001 = new InputValue();
        args001.setName("full");
        args001.setDescription("Boolean option specifying whether the reference should be returned in full.");
        args001.setIsDeprecated(false);
        args001.setType(type015);
        args001.setDefaultValue("false");
        TypeRef ofType022 = new TypeRef();
        ofType022.setKind(Kind.SCALAR);
        ofType022.setName("String");
        TypeRef type016 = new TypeRef();
        type016.setKind(Kind.NON_NULL);
        type016.setOfType(ofType022);
        Field fields015 = new Field();
        fields015.setName("reference");
        fields015.setDescription("Internal reference of the work item. Returned in shortened format by default.");
        fields015.setArgs(Arrays.asList(args001));
        fields015.setType(type016);
        fields015.setIsDeprecated(false);
        TypeRef ofType024 = new TypeRef();
        ofType024.setKind(Kind.ENUM);
        ofType024.setName("WorkItemState");
        TypeRef type017 = new TypeRef();
        type017.setKind(Kind.NON_NULL);
        type017.setOfType(ofType024);
        Field fields016 = new Field();
        fields016.setName("state");
        fields016.setDescription("State of the work item.");
        fields016.setArgs(Arrays.asList());
        fields016.setType(type017);
        fields016.setIsDeprecated(false);
        TypeRef ofType026 = new TypeRef();
        ofType026.setKind(Kind.SCALAR);
        ofType026.setName("String");
        TypeRef type018 = new TypeRef();
        type018.setKind(Kind.NON_NULL);
        type018.setOfType(ofType026);
        Field fields017 = new Field();
        fields017.setName("title");
        fields017.setDescription("Title of the work item.");
        fields017.setArgs(Arrays.asList());
        fields017.setType(type018);
        fields017.setIsDeprecated(false);
        TypeRef type019 = new TypeRef();
        type019.setKind(Kind.SCALAR);
        type019.setName("String");
        Field fields018 = new Field();
        fields018.setName("titleHtml");
        fields018.setDescription("GitLab Flavored Markdown rendering of `title`");
        fields018.setArgs(Arrays.asList());
        fields018.setType(type019);
        fields018.setIsDeprecated(false);
        TypeRef ofType029 = new TypeRef();
        ofType029.setKind(Kind.SCALAR);
        ofType029.setName("Time");
        TypeRef type020 = new TypeRef();
        type020.setKind(Kind.NON_NULL);
        type020.setOfType(ofType029);
        Field fields019 = new Field();
        fields019.setName("updatedAt");
        fields019.setDescription("Timestamp of when the work item was last updated.");
        fields019.setArgs(Arrays.asList());
        fields019.setType(type020);
        fields019.setIsDeprecated(false);
        TypeRef ofType031 = new TypeRef();
        ofType031.setKind(Kind.OBJECT);
        ofType031.setName("WorkItemPermissions");
        TypeRef type021 = new TypeRef();
        type021.setKind(Kind.NON_NULL);
        type021.setOfType(ofType031);
        Field fields020 = new Field();
        fields020.setName("userPermissions");
        fields020.setDescription("Permissions for the current user on the resource");
        fields020.setArgs(Arrays.asList());
        fields020.setType(type021);
        fields020.setIsDeprecated(false);
        TypeRef type022 = new TypeRef();
        type022.setKind(Kind.SCALAR);
        type022.setName("String");
        Field fields021 = new Field();
        fields021.setName("webUrl");
        fields021.setDescription("URL of this object.");
        fields021.setArgs(Arrays.asList());
        fields021.setType(type022);
        fields021.setIsDeprecated(false);
        TypeRef ofType035 = new TypeRef();
        ofType035.setKind(Kind.INTERFACE);
        ofType035.setName("WorkItemWidget");
        TypeRef ofType034 = new TypeRef();
        ofType034.setKind(Kind.NON_NULL);
        ofType034.setOfType(ofType035);
        TypeRef type023 = new TypeRef();
        type023.setKind(Kind.LIST);
        type023.setOfType(ofType034);
        Field fields022 = new Field();
        fields022.setName("widgets");
        fields022.setDescription("Collection of widgets that belong to the work item.");
        fields022.setArgs(Arrays.asList());
        fields022.setType(type023);
        fields022.setIsDeprecated(false);
        TypeRef ofType037 = new TypeRef();
        ofType037.setKind(Kind.OBJECT);
        ofType037.setName("WorkItemType");
        TypeRef type024 = new TypeRef();
        type024.setKind(Kind.NON_NULL);
        type024.setOfType(ofType037);
        Field fields023 = new Field();
        fields023.setName("workItemType");
        fields023.setDescription("Type assigned to the work item.");
        fields023.setArgs(Arrays.asList());
        fields023.setType(type024);
        fields023.setIsDeprecated(false);
        TypeRef interfaces001 = new TypeRef();
        interfaces001.setKind(Kind.INTERFACE);
        interfaces001.setName("Todoable");
        Type type = new Type();
        type.setKind(Kind.OBJECT);
        type.setName("WorkItemRef");
        type.setDescription("Duplicate of `WorkItem` to avoid SRGQLDC035008: Field recursion found");
        type.setFields(Arrays.asList(fields001,
                fields002,
                fields003,
                fields004,
                fields005,
                fields006,
                fields007,
                fields008,
                fields009,
                fields010,
                fields011,
                fields012,
                fields013,
                fields014,
                fields015,
                fields016,
                fields017,
                fields018,
                fields019,
                fields020,
                fields021,
                fields022,
                fields023));
        type.setInterfaces(Arrays.asList(interfaces001));
        return type;
    }

    public static Type createWorkItemConnectionRef() {
        TypeRef ofType001 = new TypeRef();
        ofType001.setKind(Kind.SCALAR);
        ofType001.setName("Int");
        TypeRef type001 = new TypeRef();
        type001.setKind(Kind.NON_NULL);
        type001.setOfType(ofType001);
        Field fields001 = new Field();
        fields001.setName("count");
        fields001.setDescription("Total count of collection.");
        fields001.setArgs(Arrays.asList());
        fields001.setType(type001);
        fields001.setIsDeprecated(false);
        TypeRef ofType003 = new TypeRef();
        ofType003.setKind(Kind.OBJECT);
        ofType003.setName("WorkItemEdge");
        TypeRef type002 = new TypeRef();
        type002.setKind(Kind.LIST);
        type002.setOfType(ofType003);
        Field fields002 = new Field();
        fields002.setName("edges");
        fields002.setDescription("A list of edges.");
        fields002.setArgs(Arrays.asList());
        fields002.setType(type002);
        fields002.setIsDeprecated(false);
        TypeRef ofType005 = new TypeRef();
        ofType005.setKind(Kind.OBJECT);
        ofType005.setName("WorkItemRef");
        TypeRef type003 = new TypeRef();
        type003.setKind(Kind.LIST);
        type003.setOfType(ofType005);
        Field fields003 = new Field();
        fields003.setName("nodes");
        fields003.setDescription("A list of nodes.");
        fields003.setArgs(Arrays.asList());
        fields003.setType(type003);
        fields003.setIsDeprecated(false);
        TypeRef ofType007 = new TypeRef();
        ofType007.setKind(Kind.OBJECT);
        ofType007.setName("PageInfo");
        TypeRef type004 = new TypeRef();
        type004.setKind(Kind.NON_NULL);
        type004.setOfType(ofType007);
        Field fields004 = new Field();
        fields004.setName("pageInfo");
        fields004.setDescription("Information to aid in pagination.");
        fields004.setArgs(Arrays.asList());
        fields004.setType(type004);
        fields004.setIsDeprecated(false);
        Type type = new Type();
        type.setKind(Kind.OBJECT);
        type.setName("WorkItemConnectionRef");
        type.setDescription("Duplicate of `WorkItemConnection` to avoid SRGQLDC035008: Field recursion found");
        type.setFields(Arrays.asList(fields001,
                fields002,
                fields003,
                fields004));
        type.setInterfaces(Arrays.asList());
        return type;
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

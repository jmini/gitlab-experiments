package gitlab.api;

import java.util.List;

import org.eclipse.microprofile.graphql.Id;
import org.eclipse.microprofile.graphql.Mutation;
import org.eclipse.microprofile.graphql.Name;
import org.eclipse.microprofile.graphql.NonNull;
import org.eclipse.microprofile.graphql.Query;
import org.eclipse.microprofile.graphql.Source;

import gitlab.model.AwardEmojiAddInput;
import gitlab.model.AwardEmojiAddPayload;
import gitlab.model.CreateNoteInput;
import gitlab.model.CreateNotePayload;
import gitlab.model.Group;
import gitlab.model.Namespace;
import gitlab.model.Project;
import gitlab.model.UpdateNoteInput;
import gitlab.model.UpdateNotePayload;
import gitlab.model.WorkItem;
import gitlab.model.WorkItemAddLinkedItemsInput;
import gitlab.model.WorkItemAddLinkedItemsPayload;
import gitlab.model.WorkItemConnection;
import gitlab.model.WorkItemCreateInput;
import gitlab.model.WorkItemCreatePayload;
import gitlab.model.WorkItemID;
import gitlab.model.WorkItemRemoveLinkedItemsInput;
import gitlab.model.WorkItemRemoveLinkedItemsPayload;
import gitlab.model.WorkItemUpdateInput;
import gitlab.model.WorkItemUpdatePayload;
import io.smallrye.graphql.client.typesafe.api.GraphQLClientApi;
import io.smallrye.graphql.client.typesafe.api.NestedParameter;

@GraphQLClientApi(configKey = "gitlab", endpoint = "https://gitlab.com/api/graphql")
public interface WorkitemClientApi {

    /**
     * Find a group.
     */
    @Query("group")
    Group group(@Name("fullPath") @NonNull @Id String fullPath, @NestedParameter("labels") @Name("includeAncestorGroups") boolean labelsIncludeAncestorGroups, @NestedParameter("labels") @Name("after") String labelsAfter);

    /**
     * Find a namespace.
     */
    @Query("namespace")
    Namespace namespace(@Name("fullPath") @NonNull @Id String fullPath);

    /**
     * Find a project.
     */
    @Query("project")
    Project project(@Name("fullPath") @NonNull @Id String fullPath, @NestedParameter("labels") @Name("includeAncestorGroups") boolean labelsIncludeAncestorGroups, @NestedParameter("labels") @Name("after") String labelsAfter);

    /**
     * Find a work item. Introduced in GitLab 15.1: **Status**: Experiment.
     *
     * @deprecated **Status**: Experiment. Introduced in GitLab 15.1.
     */
    @Deprecated
    @Query("workItem")
    WorkItem workItem(@Name("id") @NonNull WorkItemID id);

    /**
     * Find work items by their reference. Introduced in GitLab 16.7: **Status**: Experiment.
     *
     * @deprecated **Status**: Experiment. Introduced in GitLab 16.7.
     */
    @Deprecated
    @Query("workItemsByReference")
    WorkItemConnection workItemsByReference(@Name("contextNamespacePath") @Id String contextNamespacePath, @Name("refs") @NonNull List<@NonNull String> refs);

    @Mutation("awardEmojiAdd")
    AwardEmojiAddPayload awardEmojiAdd(@Name("input") @NonNull @Source AwardEmojiAddInput input);

    /**
     * Creates a Note.
     * If the body of the Note contains only quick actions,
     * the Note will be destroyed during an update, and no Note will be
     * returned.
     */
    @Mutation("createNote")
    CreateNotePayload createNote(@Name("input") @NonNull @Source CreateNoteInput input);

    /**
     * Updates a Note.
     * If the body of the Note contains only quick actions,
     * the Note will be destroyed during an update, and no Note will be
     * returned.
     */
    @Mutation("updateNote")
    UpdateNotePayload updateNote(@Name("input") @NonNull @Source UpdateNoteInput input);

    /**
     * Add linked items to the work item. Introduced in GitLab 16.3: **Status**: Experiment.
     *
     * @deprecated **Status**: Experiment. Introduced in GitLab 16.3.
     */
    @Deprecated
    @Mutation("workItemAddLinkedItems")
    WorkItemAddLinkedItemsPayload workItemAddLinkedItems(@Name("input") @NonNull @Source WorkItemAddLinkedItemsInput input);

    /**
     * Creates a work item. Introduced in GitLab 15.1: **Status**: Experiment.
     *
     * @deprecated **Status**: Experiment. Introduced in GitLab 15.1.
     */
    @Deprecated
    @Mutation("workItemCreate")
    WorkItemCreatePayload workItemCreate(@Name("input") @NonNull @Source WorkItemCreateInput input);

    /**
     * Remove items linked to the work item. Introduced in GitLab 16.3: **Status**: Experiment.
     *
     * @deprecated **Status**: Experiment. Introduced in GitLab 16.3.
     */
    @Deprecated
    @Mutation("workItemRemoveLinkedItems")
    WorkItemRemoveLinkedItemsPayload workItemRemoveLinkedItems(@Name("input") @NonNull @Source WorkItemRemoveLinkedItemsInput input);

    /**
     * Updates a work item by Global ID. Introduced in GitLab 15.1: **Status**: Experiment.
     *
     * @deprecated **Status**: Experiment. Introduced in GitLab 15.1.
     */
    @Deprecated
    @Mutation("workItemUpdate")
    WorkItemUpdatePayload workItemUpdate(@Name("input") @NonNull @Source WorkItemUpdateInput input);

}

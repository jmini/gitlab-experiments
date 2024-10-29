package gitlab.api;

import java.util.List;

import org.eclipse.microprofile.graphql.Id;
import org.eclipse.microprofile.graphql.Mutation;
import org.eclipse.microprofile.graphql.Name;
import org.eclipse.microprofile.graphql.NonNull;
import org.eclipse.microprofile.graphql.Query;
import org.eclipse.microprofile.graphql.Source;

import gitlab.model.Group;
import gitlab.model.Namespace;
import gitlab.model.Project;
import gitlab.model.WorkItemAddLinkedItemsInput;
import gitlab.model.WorkItemAddLinkedItemsPayload;
import gitlab.model.WorkItemConnection;
import gitlab.model.WorkItemCreateInput;
import gitlab.model.WorkItemCreatePayload;
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
     * Find work items by their reference. Introduced in GitLab 16.7: **Status**: Experiment.
     *
     * @deprecated **Status**: Experiment. Introduced in GitLab 16.7.
     */
    @Deprecated
    @Query("workItemsByReference")
    WorkItemConnection workItemsByReference(@Name("contextNamespacePath") @Id String contextNamespacePath, @Name("refs") @NonNull List<@NonNull String> refs);

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

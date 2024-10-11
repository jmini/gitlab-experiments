package gitlab.api;

import java.util.List;

import org.eclipse.microprofile.graphql.Id;
import org.eclipse.microprofile.graphql.Name;
import org.eclipse.microprofile.graphql.NonNull;
import org.eclipse.microprofile.graphql.Query;

import gitlab.model.WorkItemConnection;
import io.smallrye.graphql.client.typesafe.api.GraphQLClientApi;

@GraphQLClientApi(configKey = "gitlab", endpoint = "https://gitlab.com/api/graphql")
public interface WorkitemClientApi {

    /**
     * Find work items by their reference. Introduced in GitLab 16.7: **Status**: Experiment.
     * @deprecated **Status**: Experiment. Introduced in GitLab 16.7.
     */
    @Deprecated
    @Query("workItemsByReference")
    WorkItemConnection workItemsByReference(@Name("contextNamespacePath") @Id String contextNamespacePath, @Name("refs") @NonNull List<@NonNull String> refs);

}

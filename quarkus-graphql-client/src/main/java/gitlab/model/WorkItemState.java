package gitlab.model;

import org.eclipse.microprofile.graphql.Name;

/**
 * State of a GitLab work item
 */
@Name("WorkItemState")
public enum WorkItemState {

    /**
     * In open state.
     */
    OPEN,
    /**
     * In closed state.
     */
    CLOSED;

}

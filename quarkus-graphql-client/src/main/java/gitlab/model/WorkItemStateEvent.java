package gitlab.model;

import org.eclipse.microprofile.graphql.Name;

/**
 * Values for work item state events
 */
@Name("WorkItemStateEvent")
public enum WorkItemStateEvent {

    /**
     * Reopens the work item.
     */
    REOPEN,
    /**
     * Closes the work item.
     */
    CLOSE;

}

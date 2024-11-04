package gitlab.model;

import org.eclipse.microprofile.graphql.Name;

/**
 * Values for work item link types
 */
@Name("WorkItemRelatedLinkType")
public enum WorkItemRelatedLinkType {

    /**
     * Related type.
     */
    RELATED,
    /**
     * Blocked by type.
     */
    BLOCKED_BY,
    /**
     * Blocks type.
     */
    BLOCKS;

}

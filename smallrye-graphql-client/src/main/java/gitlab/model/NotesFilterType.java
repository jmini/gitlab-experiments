package gitlab.model;

import org.eclipse.microprofile.graphql.Name;

/**
 * Work item notes collection type.
 */
@Name("NotesFilterType")
public enum NotesFilterType {

    /**
     * Show all activity
     */
    ALL_NOTES,
    /**
     * Show comments only
     */
    ONLY_COMMENTS,
    /**
     * Show history only
     */
    ONLY_ACTIVITY;

}

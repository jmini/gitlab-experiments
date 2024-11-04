package gitlab.model;

import org.eclipse.microprofile.graphql.Name;

/**
 * The position to which the object should be moved
 */
@Name("RelativePositionType")
public enum RelativePositionType {

    /**
     * Object is moved before an adjacent object.
     */
    BEFORE,
    /**
     * Object is moved after an adjacent object.
     */
    AFTER;

}

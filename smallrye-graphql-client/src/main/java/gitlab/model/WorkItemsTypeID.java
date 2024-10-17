package gitlab.model;

import java.util.Objects;

import org.eclipse.microprofile.graphql.Name;

/**
 * A `WorkItemsTypeID` is a global ID. It is encoded as a string.
 *
 * An example `WorkItemsTypeID` is: `"gid://gitlab/WorkItems::Type/1"`.
 */
@Name("WorkItemsTypeID")
public class WorkItemsTypeID {

    private String value;

    public WorkItemsTypeID(String value) {
        this.value = value;
    }

    public static WorkItemsTypeID valueOf(String value) {
        return new WorkItemsTypeID(value);
    }

    @Override
    public String toString() {
        return value;
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        WorkItemsTypeID other = (WorkItemsTypeID) obj;
        return Objects.equals(value, other.value);
    }
}

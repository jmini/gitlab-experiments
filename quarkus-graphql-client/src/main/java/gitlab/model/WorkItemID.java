package gitlab.model;

import java.util.Objects;

import org.eclipse.microprofile.graphql.Name;

/**
 * A `WorkItemID` is a global ID. It is encoded as a string.
 *
 * An example `WorkItemID` is: `"gid://gitlab/WorkItem/1"`.
 *
 * While we transition from Issues into Work Items this type will temporarily support
 * `IssueID` like: `"gid://gitlab/Issue/1"`. This behavior will be removed without notice in the future.
 */
@Name("WorkItemID")
public class WorkItemID {

    private String value;

    public WorkItemID(String value) {
        this.value = value;
    }

    public static WorkItemID valueOf(String value) {
        return new WorkItemID(value);
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
        WorkItemID other = (WorkItemID) obj;
        return Objects.equals(value, other.value);
    }
}

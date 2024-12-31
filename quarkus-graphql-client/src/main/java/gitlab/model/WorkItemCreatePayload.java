package gitlab.model;

import java.util.List;
import java.util.Objects;

import org.eclipse.microprofile.graphql.Name;

/**
 * Autogenerated return type of WorkItemCreate.
 */
@Name("WorkItemCreatePayload")
public class WorkItemCreatePayload {

    /**
     * Errors encountered during execution of the mutation.
     */
    private List<String> errors;
    /**
     * Created work item.
     */
    private WorkItem workItem;

    public List<String> getErrors() {
        return errors;
    }

    public WorkItemCreatePayload setErrors(List<String> errors) {
        this.errors = errors;
        return this;
    }

    public WorkItem getWorkItem() {
        return workItem;
    }

    public WorkItemCreatePayload setWorkItem(WorkItem workItem) {
        this.workItem = workItem;
        return this;
    }

    @Override
    public int hashCode() {
        return Objects.hash(errors, workItem);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        WorkItemCreatePayload other = (WorkItemCreatePayload) obj;
        return Objects.equals(errors, other.errors) && Objects.equals(workItem, other.workItem);
    }

    @Override
    public String toString() {
        return "WorkItemCreatePayload [errors=" + errors + ", workItem=" + workItem + "]";
    }

}
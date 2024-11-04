package gitlab.model;

import java.util.List;
import java.util.Objects;

import org.eclipse.microprofile.graphql.Name;

/**
 * Autogenerated return type of WorkItemAddLinkedItems.
 */
@Name("WorkItemAddLinkedItemsPayload")
public class WorkItemAddLinkedItemsPayload {

    /**
     * Errors encountered during execution of the mutation.
     */
    private List<String> errors;
    /**
     * Linked items update result message.
     */
    private String message;
    /**
     * Updated work item.
     */
    private WorkItem workItem;

    public List<String> getErrors() {
        return errors;
    }

    public WorkItemAddLinkedItemsPayload setErrors(List<String> errors) {
        this.errors = errors;
        return this;
    }

    public String getMessage() {
        return message;
    }

    public WorkItemAddLinkedItemsPayload setMessage(String message) {
        this.message = message;
        return this;
    }

    public WorkItem getWorkItem() {
        return workItem;
    }

    public WorkItemAddLinkedItemsPayload setWorkItem(WorkItem workItem) {
        this.workItem = workItem;
        return this;
    }

    @Override
    public int hashCode() {
        return Objects.hash(errors, message, workItem);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        WorkItemAddLinkedItemsPayload other = (WorkItemAddLinkedItemsPayload) obj;
        return Objects.equals(errors, other.errors) && Objects.equals(message, other.message) && Objects.equals(workItem, other.workItem);
    }

    @Override
    public String toString() {
        return "WorkItemAddLinkedItemsPayload [errors=" + errors + ", message=" + message + ", workItem=" + workItem + "]";
    }

}

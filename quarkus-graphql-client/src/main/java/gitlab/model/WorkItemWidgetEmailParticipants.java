package gitlab.model;

import org.eclipse.microprofile.graphql.Name;

/**
 * Represents a time tracking widget
 */
@Name("WorkItemWidgetEmailParticipants")
public class WorkItemWidgetEmailParticipants implements WorkItemWidget {

    @Override
    public String toString() {
        return "WorkItemWidgetEmailParticipants";
    }

}

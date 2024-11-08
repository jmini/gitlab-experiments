package gitlab.model;

import org.eclipse.microprofile.graphql.Name;

import io.smallrye.graphql.api.Union;
import jakarta.json.bind.annotation.JsonbSubtype;
import jakarta.json.bind.annotation.JsonbTypeInfo;

@Union
@JsonbTypeInfo(key = "__typename", value = {
        @JsonbSubtype(alias = "WorkItemWidgetAssignees", type = WorkItemWidgetAssignees.class),
        @JsonbSubtype(alias = "WorkItemWidgetAwardEmoji", type = WorkItemWidgetAwardEmoji.class),
        @JsonbSubtype(alias = "WorkItemWidgetColor", type = WorkItemWidgetColor.class),
        @JsonbSubtype(alias = "WorkItemWidgetCrmContacts", type = WorkItemWidgetCrmContacts.class),
        @JsonbSubtype(alias = "WorkItemWidgetCurrentUserTodos", type = WorkItemWidgetCurrentUserTodos.class),
        @JsonbSubtype(alias = "WorkItemWidgetDescription", type = WorkItemWidgetDescription.class),
        @JsonbSubtype(alias = "WorkItemWidgetDesigns", type = WorkItemWidgetDesigns.class),
        @JsonbSubtype(alias = "WorkItemWidgetDevelopment", type = WorkItemWidgetDevelopment.class),
        @JsonbSubtype(alias = "WorkItemWidgetEmailParticipants", type = WorkItemWidgetEmailParticipants.class),
        @JsonbSubtype(alias = "WorkItemWidgetHealthStatus", type = WorkItemWidgetHealthStatus.class),
        @JsonbSubtype(alias = "WorkItemWidgetHierarchy", type = WorkItemWidgetHierarchy.class),
        @JsonbSubtype(alias = "WorkItemWidgetIteration", type = WorkItemWidgetIteration.class),
        @JsonbSubtype(alias = "WorkItemWidgetLabels", type = WorkItemWidgetLabels.class),
        @JsonbSubtype(alias = "WorkItemWidgetLinkedItems", type = WorkItemWidgetLinkedItems.class),
        @JsonbSubtype(alias = "WorkItemWidgetMilestone", type = WorkItemWidgetMilestone.class),
        @JsonbSubtype(alias = "WorkItemWidgetNotes", type = WorkItemWidgetNotes.class),
        @JsonbSubtype(alias = "WorkItemWidgetNotifications", type = WorkItemWidgetNotifications.class),
        @JsonbSubtype(alias = "WorkItemWidgetParticipants", type = WorkItemWidgetParticipants.class),
        @JsonbSubtype(alias = "WorkItemWidgetRolledupDates", type = WorkItemWidgetRolledupDates.class),
        @JsonbSubtype(alias = "WorkItemWidgetStartAndDueDate", type = WorkItemWidgetStartAndDueDate.class),
        @JsonbSubtype(alias = "WorkItemWidgetStatus", type = WorkItemWidgetStatus.class),
        @JsonbSubtype(alias = "WorkItemWidgetTimeTracking", type = WorkItemWidgetTimeTracking.class),
        @JsonbSubtype(alias = "WorkItemWidgetWeight", type = WorkItemWidgetWeight.class)
})
@Name("WorkItemWidget")
public interface WorkItemWidget {
}

package gitlab.model;

import org.eclipse.microprofile.graphql.Name;

/**
 * Type of a work item widget
 */
@Name("WorkItemWidgetType")
public enum WorkItemWidgetType {

    /**
     * Assignees widget.
     */
    ASSIGNEES,
    /**
     * Description widget.
     */
    DESCRIPTION,
    /**
     * Hierarchy widget.
     */
    HIERARCHY,
    /**
     * Labels widget.
     */
    LABELS,
    /**
     * Milestone widget.
     */
    MILESTONE,
    /**
     * Notes widget.
     */
    NOTES,
    /**
     * Start And Due Date widget.
     */
    START_AND_DUE_DATE,
    /**
     * Health Status widget.
     */
    HEALTH_STATUS,
    /**
     * Weight widget.
     */
    WEIGHT,
    /**
     * Iteration widget.
     */
    ITERATION,
    /**
     * Progress widget.
     */
    PROGRESS,
    /**
     * Status widget.
     */
    STATUS,
    /**
     * Requirement Legacy widget.
     */
    REQUIREMENT_LEGACY,
    /**
     * Test Reports widget.
     */
    TEST_REPORTS,
    /**
     * Notifications widget.
     */
    NOTIFICATIONS,
    /**
     * Current User Todos widget.
     */
    CURRENT_USER_TODOS,
    /**
     * Award Emoji widget.
     */
    AWARD_EMOJI,
    /**
     * Linked Items widget.
     */
    LINKED_ITEMS,
    /**
     * Color widget.
     */
    COLOR,
    /**
     * Rolledup Dates widget.
     */
    ROLLEDUP_DATES,
    /**
     * Participants widget.
     */
    PARTICIPANTS,
    /**
     * Time Tracking widget.
     */
    TIME_TRACKING,
    /**
     * Designs widget.
     */
    DESIGNS,
    /**
     * Development widget.
     */
    DEVELOPMENT,
    /**
     * Crm Contacts widget.
     */
    CRM_CONTACTS,
    /**
     * Email Participants widget.
     */
    EMAIL_PARTICIPANTS;

}

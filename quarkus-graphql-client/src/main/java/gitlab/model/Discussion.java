package gitlab.model;

import java.util.Objects;

import org.eclipse.microprofile.graphql.Name;

@Name("Discussion")
public class Discussion {

    /**
     * Timestamp of the discussion's creation.
     */
    private Time createdAt;
    /**
     * ID of the discussion.
     */
    private DiscussionID id;
    /**
     * All notes in the discussion.
     */
    private NoteConnection notes;

    public Time getCreatedAt() {
        return createdAt;
    }

    public Discussion setCreatedAt(Time createdAt) {
        this.createdAt = createdAt;
        return this;
    }

    public DiscussionID getId() {
        return id;
    }

    public Discussion setId(DiscussionID id) {
        this.id = id;
        return this;
    }

    public NoteConnection getNotes() {
        return notes;
    }

    public Discussion setNotes(NoteConnection notes) {
        this.notes = notes;
        return this;
    }

    @Override
    public int hashCode() {
        return Objects.hash(createdAt, id, notes);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Discussion other = (Discussion) obj;
        return Objects.equals(createdAt, other.createdAt) && Objects.equals(id, other.id) && Objects.equals(notes, other.notes);
    }

    @Override
    public String toString() {
        return "Discussion [createdAt=" + createdAt + ", id=" + id + ", notes=" + notes + "]";
    }

}

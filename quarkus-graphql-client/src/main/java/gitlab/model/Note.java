package gitlab.model;

import java.util.Objects;

import org.eclipse.microprofile.graphql.Name;

@Name("Note")
public class Note {

    /**
     * User who wrote the note.
     */
    private UserCore author;
    /**
     * List of emoji reactions associated with the note.
     */
    private AwardEmojiConnection awardEmoji;
    /**
     * Content of the note.
     */
    private String body;
    /**
     * Timestamp of the note creation.
     */
    private Time createdAt;
    /**
     * ID of the note.
     */
    private NoteID id;
    /**
     * Timestamp when note was last edited.
     */
    private Time lastEditedAt;
    /**
     * Indicates whether the note was created by the system or by a user.
     */
    private Boolean system;
    /**
     * Name of the icon corresponding to a system note.
     */
    private String systemNoteIconName;
    /**
     * Timestamp of the note's last activity.
     */
    private Time updatedAt;
    /**
     * URL to view the note in the Web UI.
     */
    private String url;

    public UserCore getAuthor() {
        return author;
    }

    public Note setAuthor(UserCore author) {
        this.author = author;
        return this;
    }

    public AwardEmojiConnection getAwardEmoji() {
        return awardEmoji;
    }

    public Note setAwardEmoji(AwardEmojiConnection awardEmoji) {
        this.awardEmoji = awardEmoji;
        return this;
    }

    public String getBody() {
        return body;
    }

    public Note setBody(String body) {
        this.body = body;
        return this;
    }

    public Time getCreatedAt() {
        return createdAt;
    }

    public Note setCreatedAt(Time createdAt) {
        this.createdAt = createdAt;
        return this;
    }

    public NoteID getId() {
        return id;
    }

    public Note setId(NoteID id) {
        this.id = id;
        return this;
    }

    public Time getLastEditedAt() {
        return lastEditedAt;
    }

    public Note setLastEditedAt(Time lastEditedAt) {
        this.lastEditedAt = lastEditedAt;
        return this;
    }

    public Boolean getSystem() {
        return system;
    }

    public Note setSystem(Boolean system) {
        this.system = system;
        return this;
    }

    public String getSystemNoteIconName() {
        return systemNoteIconName;
    }

    public Note setSystemNoteIconName(String systemNoteIconName) {
        this.systemNoteIconName = systemNoteIconName;
        return this;
    }

    public Time getUpdatedAt() {
        return updatedAt;
    }

    public Note setUpdatedAt(Time updatedAt) {
        this.updatedAt = updatedAt;
        return this;
    }

    public String getUrl() {
        return url;
    }

    public Note setUrl(String url) {
        this.url = url;
        return this;
    }

    @Override
    public int hashCode() {
        return Objects.hash(author, awardEmoji, body, createdAt, id, lastEditedAt, system, systemNoteIconName, updatedAt, url);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Note other = (Note) obj;
        return Objects.equals(author, other.author) && Objects.equals(awardEmoji, other.awardEmoji) && Objects.equals(body, other.body) && Objects.equals(createdAt, other.createdAt) && Objects.equals(id, other.id) && Objects.equals(lastEditedAt, other.lastEditedAt) && Objects.equals(system, other.system) && Objects.equals(systemNoteIconName, other.systemNoteIconName) && Objects.equals(updatedAt, other.updatedAt) && Objects.equals(url, other.url);
    }

    @Override
    public String toString() {
        return "Note [author=" + author + ", awardEmoji=" + awardEmoji + ", body=" + body + ", createdAt=" + createdAt + ", id=" + id + ", lastEditedAt=" + lastEditedAt + ", system=" + system + ", systemNoteIconName=" + systemNoteIconName + ", updatedAt=" + updatedAt + ", url=" + url + "]";
    }

}

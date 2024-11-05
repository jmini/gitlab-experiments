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
     * ID of the note.
     */
    private NoteID id;

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

    public NoteID getId() {
        return id;
    }

    public Note setId(NoteID id) {
        this.id = id;
        return this;
    }

    @Override
    public int hashCode() {
        return Objects.hash(author, awardEmoji, body, id);
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
        return Objects.equals(author, other.author) && Objects.equals(awardEmoji, other.awardEmoji) && Objects.equals(body, other.body) && Objects.equals(id, other.id);
    }

    @Override
    public String toString() {
        return "Note [author=" + author + ", awardEmoji=" + awardEmoji + ", body=" + body + ", id=" + id + "]";
    }

}

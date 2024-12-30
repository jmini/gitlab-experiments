package gitlab.model;

import java.util.List;
import java.util.Objects;

import org.eclipse.microprofile.graphql.Name;

/**
 * Autogenerated return type of DestroyNote.
 */
@Name("DestroyNotePayload")
public class DestroyNotePayload {

    /**
     * Errors encountered during execution of the mutation.
     */
    private List<String> errors;

    /**
     * Note after mutation.
     */
    private Note note;

    public List<String> getErrors() {
        return errors;
    }

    public DestroyNotePayload setErrors(List<String> errors) {
        this.errors = errors;
        return this;
    }

    public Note getNote() {
        return note;
    }

    public DestroyNotePayload setNote(Note note) {
        this.note = note;
        return this;
    }

    @Override
    public int hashCode() {
        return Objects.hash(errors, note);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        DestroyNotePayload other = (DestroyNotePayload) obj;
        return Objects.equals(errors, other.errors) && Objects.equals(note, other.note);
    }

    @Override
    public String toString() {
        return "DestroyNotePayload [errors=" + errors + ", note=" + note + "]";
    }

}

package gitlab.model;

import java.util.List;
import java.util.Objects;

import org.eclipse.microprofile.graphql.Name;

/**
 * Autogenerated return type of AwardEmojiAdd.
 */
@Name("AwardEmojiAddPayload")
public class AwardEmojiAddPayload {

    /**
     * Emoji reactions after mutation.
     */
    private AwardEmoji awardEmoji;
    /**
     * Errors encountered during execution of the mutation.
     */
    private List<String> errors;

    public AwardEmoji getAwardEmoji() {
        return awardEmoji;
    }

    public AwardEmojiAddPayload setAwardEmoji(AwardEmoji awardEmoji) {
        this.awardEmoji = awardEmoji;
        return this;
    }

    public List<String> getErrors() {
        return errors;
    }

    public AwardEmojiAddPayload setErrors(List<String> errors) {
        this.errors = errors;
        return this;
    }

    @Override
    public int hashCode() {
        return Objects.hash(awardEmoji, errors);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        AwardEmojiAddPayload other = (AwardEmojiAddPayload) obj;
        return Objects.equals(awardEmoji, other.awardEmoji) && Objects.equals(errors, other.errors);
    }

    @Override
    public String toString() {
        return "AwardEmojiAddPayload [awardEmoji=" + awardEmoji + ", errors=" + errors + "]";
    }

}

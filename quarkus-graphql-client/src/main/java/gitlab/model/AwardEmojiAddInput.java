package gitlab.model;

import java.util.Objects;

import org.eclipse.microprofile.graphql.Name;

/**
 * Autogenerated input type of AwardEmojiAdd
 */
@Name("AwardEmojiAddInput")
public class AwardEmojiAddInput {

    /**
     * Global ID of the awardable resource.
     */
    private AwardableID awardableId;
    /**
     * Emoji name.
     */
    private String name;

    public AwardableID getAwardableId() {
        return awardableId;
    }

    public AwardEmojiAddInput setAwardableId(AwardableID awardableId) {
        this.awardableId = awardableId;
        return this;
    }

    public String getName() {
        return name;
    }

    public AwardEmojiAddInput setName(String name) {
        this.name = name;
        return this;
    }

    @Override
    public int hashCode() {
        return Objects.hash(awardableId, name);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        AwardEmojiAddInput other = (AwardEmojiAddInput) obj;
        return Objects.equals(awardableId, other.awardableId) && Objects.equals(name, other.name);
    }

    @Override
    public String toString() {
        return "AwardEmojiAddInput [awardableId=" + awardableId + ", name=" + name + "]";
    }

}

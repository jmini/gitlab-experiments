package gitlab.model;

import java.util.Objects;

import org.eclipse.microprofile.graphql.Name;

/**
 * A `AwardableID` is a global ID. It is encoded as a string.
 *
 * An example `AwardableID` is: `"gid://gitlab/Awardable/1"`.
 */
@Name("AwardableID")
public class AwardableID {

    private String value;

    public AwardableID(String value) {
        this.value = value;
    }

    public static AwardableID valueOf(String value) {
        return new AwardableID(value);
    }

    @Override
    public String toString() {
        return value;
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        AwardableID other = (AwardableID) obj;
        return Objects.equals(value, other.value);
    }
}

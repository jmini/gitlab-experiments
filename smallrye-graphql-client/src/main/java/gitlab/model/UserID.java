package gitlab.model;

import java.util.Objects;

import org.eclipse.microprofile.graphql.Name;

/**
 * A `UserID` is a global ID. It is encoded as a string.
 *
 * An example `UserID` is: `"gid://gitlab/User/1"`.
 */
@Name("UserID")
public class UserID {

    private String value;

    public UserID(String value) {
        this.value = value;
    }

    public static UserID valueOf(String value) {
        return new UserID(value);
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
        UserID other = (UserID) obj;
        return Objects.equals(value, other.value);
    }
}

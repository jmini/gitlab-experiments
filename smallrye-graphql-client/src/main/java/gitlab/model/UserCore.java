package gitlab.model;

import java.util.Objects;

import org.eclipse.microprofile.graphql.Name;

/**
 * Core representation of a GitLab user.
 */
@Name("UserCore")
public class UserCore {

    /**
     * ID of the user.
     */
    private UserID id;

    /**
     * Username of the user. Unique within this instance of GitLab.
     */
    private String username;

    public UserID getId() {
        return id;
    }

    public UserCore setId(UserID id) {
        this.id = id;
        return this;
    }

    public String getUsername() {
        return username;
    }

    public UserCore setUsername(String username) {
        this.username = username;
        return this;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, username);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        UserCore other = (UserCore) obj;
        return Objects.equals(id, other.id) && Objects.equals(username, other.username);
    }

    @Override
    public String toString() {
        return "UserCore [id=" + id + ", username=" + username + "]";
    }

}

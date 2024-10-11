package gitlab.model;

import java.util.Objects;

import org.eclipse.microprofile.graphql.Name;

/**
 * Core representation of a GitLab user.
 */
@Name("UserCore")
public class UserCore {

    /**
     * Indicates if the user is active.
     */
    private Boolean active;
    /**
     * ID of the user.
     */
    private String id;
    /**
     * Username of the user. Unique within this instance of GitLab.
     */
    private String username;

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    @Override
    public int hashCode() {
        return Objects.hash(active, id, username);
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
        return Objects.equals(active, other.active) && Objects.equals(id, other.id) && Objects.equals(username, other.username);
    }

    @Override
    public String toString() {
        return "UserCore [active=" + active + ", id=" + id + ", username=" + username + "]";
    }

}

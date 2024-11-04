package gitlab.model;

import java.util.Objects;

import org.eclipse.microprofile.graphql.Name;

/**
 * An emoji awarded by a user
 */
@Name("AwardEmoji")
public class AwardEmoji {

    /**
     * Emoji description.
     */
    private String description;
    /**
     * Emoji as an icon.
     */
    private String emoji;
    /**
     * Emoji name.
     */
    private String name;
    /**
     * Emoji in Unicode.
     */
    private String unicode;
    /**
     * Unicode version for the emoji.
     */
    private String unicodeVersion;
    /**
     * User who awarded the emoji.
     */
    private UserCore user;

    public String getDescription() {
        return description;
    }

    public AwardEmoji setDescription(String description) {
        this.description = description;
        return this;
    }

    public String getEmoji() {
        return emoji;
    }

    public AwardEmoji setEmoji(String emoji) {
        this.emoji = emoji;
        return this;
    }

    public String getName() {
        return name;
    }

    public AwardEmoji setName(String name) {
        this.name = name;
        return this;
    }

    public String getUnicode() {
        return unicode;
    }

    public AwardEmoji setUnicode(String unicode) {
        this.unicode = unicode;
        return this;
    }

    public String getUnicodeVersion() {
        return unicodeVersion;
    }

    public AwardEmoji setUnicodeVersion(String unicodeVersion) {
        this.unicodeVersion = unicodeVersion;
        return this;
    }

    public UserCore getUser() {
        return user;
    }

    public AwardEmoji setUser(UserCore user) {
        this.user = user;
        return this;
    }

    @Override
    public int hashCode() {
        return Objects.hash(description, emoji, name, unicode, unicodeVersion, user);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        AwardEmoji other = (AwardEmoji) obj;
        return Objects.equals(description, other.description) && Objects.equals(emoji, other.emoji) && Objects.equals(name, other.name) && Objects.equals(unicode, other.unicode) && Objects.equals(unicodeVersion, other.unicodeVersion) && Objects.equals(user, other.user);
    }

    @Override
    public String toString() {
        return "AwardEmoji [description=" + description + ", emoji=" + emoji + ", name=" + name + ", unicode=" + unicode + ", unicodeVersion=" + unicodeVersion + ", user=" + user + "]";
    }

}

package gitlab.model;

import java.util.Objects;

import org.eclipse.microprofile.graphql.Name;

@Name("Label")
public class Label {

    /**
     * Background color of the label.
     */
    private String color;
    /**
     * Description of the label (Markdown rendered as HTML for caching).
     */
    private String description;
    /**
     * Label ID.
     */
    private String id;
    /**
     * Text color of the label.
     */
    private String textColor;
    /**
     * Content of the label.
     */
    private String title;

    public String getColor() {
        return color;
    }

    public Label setColor(String color) {
        this.color = color;
        return this;
    }

    public String getDescription() {
        return description;
    }

    public Label setDescription(String description) {
        this.description = description;
        return this;
    }

    public String getId() {
        return id;
    }

    public Label setId(String id) {
        this.id = id;
        return this;
    }

    public String getTextColor() {
        return textColor;
    }

    public Label setTextColor(String textColor) {
        this.textColor = textColor;
        return this;
    }

    public String getTitle() {
        return title;
    }

    public Label setTitle(String title) {
        this.title = title;
        return this;
    }

    @Override
    public int hashCode() {
        return Objects.hash(color, description, id, textColor, title);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Label other = (Label) obj;
        return Objects.equals(color, other.color) && Objects.equals(description, other.description) && Objects.equals(id, other.id) && Objects.equals(textColor, other.textColor) && Objects.equals(title, other.title);
    }

    @Override
    public String toString() {
        return "Label [color=" + color + ", description=" + description + ", id=" + id + ", textColor=" + textColor + ", title=" + title + "]";
    }

}

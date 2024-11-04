package gitlab.model;

import java.util.Objects;

import org.eclipse.microprofile.graphql.Name;

/**
 * Date represented in ISO 8601
 */
@Name("Date")
public class Date {

    private String value;

    public Date(String value) {
        this.value = value;
    }

    public static Date valueOf(String value) {
        return new Date(value);
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
        Date other = (Date) obj;
        return Objects.equals(value, other.value);
    }
}

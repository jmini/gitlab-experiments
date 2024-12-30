package gitlab.model;

import java.util.List;
import java.util.Objects;

import org.eclipse.microprofile.graphql.Name;

/**
 * The connection type for Label.
 */
@Name("LabelConnection")
public class LabelConnection {

    /**
     * Total count of collection.
     */
    private Integer count;

    /**
     * A list of nodes.
     */
    private List<Label> nodes;

    /**
     * Information to aid in pagination.
     */
    private PageInfo pageInfo;

    public Integer getCount() {
        return count;
    }

    public LabelConnection setCount(Integer count) {
        this.count = count;
        return this;
    }

    public List<Label> getNodes() {
        return nodes;
    }

    public LabelConnection setNodes(List<Label> nodes) {
        this.nodes = nodes;
        return this;
    }

    public PageInfo getPageInfo() {
        return pageInfo;
    }

    public LabelConnection setPageInfo(PageInfo pageInfo) {
        this.pageInfo = pageInfo;
        return this;
    }

    @Override
    public int hashCode() {
        return Objects.hash(count, nodes, pageInfo);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        LabelConnection other = (LabelConnection) obj;
        return Objects.equals(count, other.count) && Objects.equals(nodes, other.nodes) && Objects.equals(pageInfo, other.pageInfo);
    }

    @Override
    public String toString() {
        return "LabelConnection [count=" + count + ", nodes=" + nodes + ", pageInfo=" + pageInfo + "]";
    }

}

///usr/bin/env jbang "$0" "$@" ; exit $?

//DEPS info.picocli:picocli:4.6.3
//DEPS org.gitlab4j:gitlab4j-api:5.1.0
//JAVA 17

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.Callable;
import java.util.stream.Collectors;

import org.gitlab4j.api.GitLabApi;
import org.gitlab4j.api.models.Group;
import org.gitlab4j.api.models.GroupFilter;
import org.gitlab4j.api.models.GroupProjectsFilter;
import org.gitlab4j.api.models.Project;

import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

@Command(name = "PrintGroupsAndProjectsTree", mixinStandardHelpOptions = true, version = "PrintGroupsAndProjectsTree 0.1", description = "Print the tree of sub-groups and projects for a given GitLab group")
class PrintGroupsAndProjectsTree implements Callable<Integer> {

    @Parameters(index = "0", description = "The group id or path")
    private String group;

    @Option(names = { "-c", "--config" }, description = "Configuration file location")
    String configFile;

    @Option(names = { "-a", "--ascii" }, description = "Specifies to use text characters instead of graphic characters to show the lines", defaultValue = "false")
    Boolean asciiChars;

    @Option(names = { "-d", "--display" }, description = "Specifies how a group/project is displayed (name, path)", defaultValue = "PATH")
    NodeDisplay display;

    public enum NodeDisplay {
        PATH, NAME
    }

    @Override
    public Integer call() throws Exception { // your business logic goes here...
        Path file;
        if (configFile != null) {
            file = Paths.get(configFile);
        } else {
            file = configFile(Paths.get(""));
        }
        Properties properties = configProperties(file);
        String gitLabUrl = readProperty(properties, "GITLAB_URL", "https://gitlab.com");
        String gitLabAuthValue = readProperty(properties, "GITLAB_AUTH_VALUE", null);

        try (GitLabApi api = new GitLabApi(gitLabUrl, gitLabAuthValue)) {
            Group gitlabGroup = api.getGroupApi()
                    .getGroup(group);

            GroupFilter groupFilter = new GroupFilter()
                    .withAllAvailabley(true);
            List<Group> groups = api.getGroupApi()
                    .getDescendantGroups(gitlabGroup.getId(), groupFilter);
            Map<Long, List<Group>> groupsByParents = groups.stream()
                    .collect(Collectors.groupingBy(Group::getParentId));

            if (!groupsByParents.containsKey(gitlabGroup.getId())) {
                throw new IllegalStateException("Could not find the root group with id " + gitlabGroup.getId());
            }

            GroupProjectsFilter groupProjectsFilter = new GroupProjectsFilter();
            groupProjectsFilter.withIncludeSubGroups(true);
            List<Project> projects = api.getGroupApi()
                    .getProjects(gitlabGroup.getId(), groupProjectsFilter);
            Map<Long, List<Project>> projectsByGroup = projects.stream()
                    .collect(Collectors.groupingBy(p -> p.getNamespace()
                            .getId()));

            Node node = createNode(gitlabGroup, groupsByParents, projectsByGroup, display);

            final StringBuilder sb = renderTree(node, asciiChars.booleanValue());
            System.out.println(sb.toString());
            return 0;
        }
    }

    private static Node createNode(Group group, Map<Long, List<Group>> groupsByGroup, Map<Long, List<Project>> projectsByGroup, NodeDisplay nodeDisplay) {

        String name = switch (nodeDisplay) {
        case NAME -> group.getName();
        case PATH -> group.getPath();
        default -> throw new IllegalArgumentException("Unexpected value: " + nodeDisplay);
        };

        Node node = new Node(name);
        // Node node = new Node(group.getPath());
        List<Group> childrenGroups = groupsByGroup.get(group.getId());
        if (childrenGroups != null) {
            for (Group child : childrenGroups) {
                Node childNode = createNode(child, groupsByGroup, projectsByGroup, nodeDisplay);
                node.addChild(childNode);
            }
        }
        List<Project> childrenProjects = projectsByGroup.get(group.getId());
        if (childrenProjects != null) {
            for (Project child : childrenProjects) {
                String childName = switch (nodeDisplay) {
                case NAME -> child.getName();
                case PATH -> child.getPath();
                default -> throw new IllegalArgumentException("Unexpected value: " + nodeDisplay);
                };
                Node childNode = new Node("[REPO] " + childName);
                node.addChild(childNode);
            }
        }
        return node;
    }

    private static class Node {
        private final String name;
        private final List<Node> children;

        public Node(final String name) {
            this.name = name;
            this.children = new ArrayList<>();
        }

        public String getName() {
            return name;
        }

        public Node addChild(final Node n) {
            children.add(n);
            return this;
        }
    }

    private static StringBuilder renderTree(final Node folder, boolean useAscii) {
        return renderTree(folder, new StringBuilder(), false, new ArrayList<>(), useAscii);
    }

    private static StringBuilder renderTree(final Node folder, final StringBuilder sb, final boolean isLast, final List<Boolean> hierarchyTree, boolean useAscii) {
        indent(sb, isLast, hierarchyTree, useAscii).append(folder.getName())
                .append("\n");

        for (int i = 0; i < folder.children.size(); i++) {
            final boolean last = ((i + 1) == folder.children.size());

            // this means if the current folder will still need to print subfolders at this level, if yes, then we need to continue print |
            hierarchyTree.add(i != folder.children.size() - 1);
            renderTree(folder.children.get(i), sb, last, hierarchyTree, useAscii);

            // pop the last value as we return from a lower level to a higher level
            hierarchyTree.remove(hierarchyTree.size() - 1);
        }
        return sb;
    }

    private static StringBuilder indent(final StringBuilder sb, final boolean isLast, final List<Boolean> hierarchyTree, boolean useAscii) {
        final String indentContent = useAscii ? "|    " : "\u2502   ";
        for (int i = 0; i < hierarchyTree.size() - 1; ++i) {
            // determines if we need to print | at this level to show the tree structure
            // i.e. if this folder has a sibling foler that is going to be printed later
            if (hierarchyTree.get(i)
                    .booleanValue()) {
                sb.append(indentContent);
            } else {
                sb.append("    "); // otherwise print empty space
            }
        }

        if (!hierarchyTree.isEmpty()) {
            if (useAscii) {
                sb.append(isLast ? "\\---" : "+---")
                        .append(" ");
            } else {
                sb.append(isLast ? "\u2514\u2500\u2500" : "\u251c\u2500\u2500")
                        .append(" ");
            }
        }

        return sb;
    }

    public static Properties configProperties(Path configFile) {
        Properties properties = new Properties();
        if (Files.isRegularFile(configFile)) {
            System.out.println("Reading config: " + configFile.toAbsolutePath());
            try (InputStream is = new FileInputStream(configFile.toFile())) {
                properties.load(is);
            } catch (IOException e) {
                throw new IllegalStateException("Can not read config file", e);
            }
        } else {
            System.out.println("WARNING: no config file found, using default values " + configFile.toAbsolutePath());
        }
        return properties;
    }

    public static Path configFile(Path root) {
        return root.toAbsolutePath()
                .resolve("gitlab-config.properties");
    }

    public static String readProperty(Properties p, String key) {
        if (!p.containsKey(key)) {
            throw new IllegalStateException(String.format("Configuration file does not contains key '%s'", key));
        }
        String value = p.getProperty(key);
        if (value == null || value.isBlank()) {
            throw new IllegalStateException(String.format("Key '%s' is not defined in configuration file", key));
        }
        return value;
    }

    public static String readProperty(Properties p, String key, String defaultValue) {
        return p.getProperty(key, defaultValue);
    }

    public static void main(String... args) {
        int exitCode = new CommandLine(new PrintGroupsAndProjectsTree()).execute(args);
        System.exit(exitCode);
    }
}

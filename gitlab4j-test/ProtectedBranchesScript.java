///usr/bin/env jbang "$0" "$@" ; exit $?

//DEPS info.picocli:picocli:4.6.3
//DEPS org.gitlab4j:gitlab4j-api:6.0.0-rc.8
//JAVA 17

//Example usage:
// jbang ProtectedBranchesScript.java CREATE --project 855 --branch=xxx1 --codeOwnerApprovalRequired=true

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;
import java.util.concurrent.Callable;

import org.gitlab4j.api.GitLabApi;
import org.gitlab4j.api.GitLabApiException;
import org.gitlab4j.api.models.*;

import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

@Command(name = "ProtectedBranchesScript", mixinStandardHelpOptions = true, version = "ProtectedBranchesScript 0.1", description = "Tests for GitLab4J")
public class ProtectedBranchesScript implements Callable<Integer> {

	private static final String CONFIG_FILE_INITIAL_CONTENT = """
			GITLAB_URL=https://gitlab.com
			GITLAB_AUTH_VALUE=
			""";

	@Parameters(index = "0", description = "action to execute", defaultValue = "PROTECTED_BRANCHES")
	private Action action;

	@Option(names = { "-p", "--project" }, description = "project")
	private String project;

	@Option(names = { "-b", "--branch" }, description = "branch")
	private String branch;

	@Option(names = {"--pushAccessLevel" }, description = "pushAccessLevel")
	private AccessLevel pushAccessLevel;

	@Option(names = {"--mergeAccessLevel" }, description = "mergeAccessLevel")
	private AccessLevel mergeAccessLevel;

	@Option(names = {"--unprotectAccessLevel" }, description = "unprotectAccessLevel")
	private AccessLevel unprotectAccessLevel;

	@Option(names = {"--codeOwnerApprovalRequired" }, description = "codeOwnerApprovalRequired")
	private Boolean codeOwnerApprovalRequired;

	@Option(names = {"--allowForcedPush" }, description = "allowForcedPush")
	private Boolean allowForcedPush;

	@Option(names = { "-c", "--config" }, description = "configuration file location")
	String configFile;

	private static enum Action {
		PROTECTED_BRANCHES, PROTECTED_BRANCH, CREATE, DELETE
	}

	@Override
	public Integer call() {
		Path file;
		if (configFile != null) {
			file = Paths.get(configFile);
		} else {
			file = configFile(Paths.get(""));
		}
		System.out.println("Reading config: " + file.toAbsolutePath());
		final Properties prop = configProperties(file);
		final String gitLabUrl = readProperty(prop, "GITLAB_URL", "https://gitlab.com");
		final String gitLabAuthValue = readProperty(prop, "GITLAB_AUTH_VALUE");

		try (GitLabApi gitLabApi = new GitLabApi(gitLabUrl, gitLabAuthValue)) {
			switch (action) {
			case PROTECTED_BRANCHES:
				ensureExists(project, "project");
				var protectedBranches = gitLabApi.getProtectedBranchesApi()
						.getProtectedBranches(idOrPath(project));
				System.out.println(protectedBranches);
				break;
			case PROTECTED_BRANCH:
				ensureExists(project, "project");
				ensureExists(branch, "branch");
				var protectedBranch = gitLabApi.getProtectedBranchesApi()
						.getProtectedBranch(idOrPath(project), branch);
				System.out.println(protectedBranch);	
				break;
			case CREATE:
				ensureExists(project, "project");
				ensureExists(branch, "branch");
				var created = gitLabApi.getProtectedBranchesApi()
						.protectBranch(idOrPath(project), branch, pushAccessLevel, mergeAccessLevel, unprotectAccessLevel, codeOwnerApprovalRequired, allowForcedPush);
				System.out.println(created);
				break;
			case DELETE:
				ensureExists(project, "project");
				ensureExists(branch, "branch");
				gitLabApi.getProtectedBranchesApi()
						.unprotectBranch(idOrPath(project), branch);
				break;
			default:
				throw new IllegalArgumentException("Unexpected value: " + action);
			}
		} catch (final GitLabApiException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return 0;
	}

	private void ensureExists(final Object value, final String optionName) {
		if (value == null) {
			throw new IllegalStateException("--" + optionName + " must be set");
		}
	}

	private Object idOrPath(final String value) {
		if (value.matches("[0-9]+")) {
			return Long.valueOf(value);
		}
		return value;
	}

	public static Properties configProperties(final Path configFile) {
		try (InputStream is = new FileInputStream(configFile.toFile())) {
			final Properties properties = new Properties();
			properties.load(is);
			return properties;
		} catch (final IOException e) {
			throw new IllegalStateException("Can not read config file", e);
		}
	}

	public static Path configFile(final Path root) {
		final Path configFile = root.toAbsolutePath()
				.resolve("gitlab-config.properties");
		if (!Files.isRegularFile(configFile)) {
			try {
				Files.writeString(configFile, CONFIG_FILE_INITIAL_CONTENT);
				throw new IllegalStateException(String.format("Configuration file '%s' does not exist. An empty configuration file was created", configFile.toAbsolutePath()));
			} catch (final IOException e) {
				throw new IllegalStateException("Can not write initial config file", e);
			}
		}
		return configFile;
	}

	public static String readProperty(final Properties p, final String key) {
		if (!p.containsKey(key)) {
			throw new IllegalStateException(String.format("Configuration file does not contains key '%s'", key));
		}
		final String value = p.getProperty(key);
		if (value == null || value.isBlank()) {
			throw new IllegalStateException(String.format("Key '%s' is not defined in configuration file", key));
		}
		return value;
	}

	public static String readProperty(final Properties p, final String key, final String defaultValue) {
		return p.getProperty(key, defaultValue);
	}

	public static void main(final String... args) {
		final int exitCode = new CommandLine(new ProtectedBranchesScript()).execute(args);
		System.exit(exitCode);
	}
}

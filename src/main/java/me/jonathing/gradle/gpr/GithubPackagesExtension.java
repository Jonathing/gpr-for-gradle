package me.jonathing.gradle.gpr;

import org.gradle.api.Action;
import org.gradle.api.artifacts.repositories.MavenArtifactRepository;

public interface GithubPackagesExtension {
    /**
     * The default username used for public authentication to GitHub Packages.
     *
     * @see #DEFAULT_KEY
     * @see <a href="https://github.com/0ffz/gpr-for-gradle?tab=readme-ov-file#notes-on-automatic-authentication">Notes
     * on Automatic Authentication</a>
     */
    String DEFAULT_USER = "token";

    /**
     * The default password used for public authentication to GitHub Packages.
     *
     * @see #DEFAULT_USER
     * @see <a href="https://github.com/0ffz/gpr-for-gradle?tab=readme-ov-file#notes-on-automatic-authentication">Notes
     * on Automatic Authentication</a>
     */
    String DEFAULT_KEY = "7ff6093f273637d67f8049b09898f4f41db3d38e";

    /**
     * Creates a configuring action for a new repository for the package with url defined as
     * {@code https://maven.pkg.github.com/[name]}.
     * <p>This should be passed into to pass into
     * {@link org.gradle.api.artifacts.dsl.RepositoryHandler#maven(Action)}</p>
     *
     * <h3>Credentials</h3>
     * <p>Credentials will be applied automatically, defaulting as follows:</p>
     * <ul>
     *     <li><strong>Username:</strong> First check for {@code gpr.user} property, then check for {@code GITHUB_ACTOR} environment variable.</li>
     *     <li><strong>Password:</strong> First check for {@code gpr.key} property, then check for {@code GITHUB_TOKEN} environment variable.</li>
     * </ul>
     *
     * @param repo The repo to get a maven repository for
     * @return A configuring action for the maven repository
     */
    Action<? super MavenArtifactRepository> maven(String repo);

    /**
     * Creates a configuring action for a new repository for the package with url defined as
     * {@code https://maven.pkg.github.com/[name]}.
     * <p>This should be passed into to pass into
     * {@link org.gradle.api.artifacts.dsl.RepositoryHandler#maven(Action)}</p>
     *
     * <h3>Credentials</h3>
     * <p>Credentials will be applied automatically, defaulting as follows:</p>
     * <ul>
     *     <li><strong>Username:</strong> First check for {@code gpr.user} property, then check for {@code GITHUB_ACTOR} environment variable.</li>
     *     <li><strong>Password:</strong> First check for {@code gpr.key} property, then check for {@code GITHUB_TOKEN} environment variable.</li>
     * </ul>
     *
     * <h3>Additional Configuration</h3>
     * <p>You may then optionally modify it as a regular maven repository (example in Groovy DSL):</p>
     *
     * <pre><code>
     *     maven githubPackage.maven('owner/repo') {
     *         name = 'anotherName'
     *         credentials {
     *             username = 'name'
     *             password = 'token'
     *         }
     *     }
     * </code></pre>
     *
     * <p>These changes will only be applied to the current package, while {@link #template(Action)} would apply them
     * to all the following packages.</p>
     *
     * @param repo   The repo to get a maven repository for
     * @param action The action to execute on the maven repository
     * @return A configuring action for the maven repository
     */
    default Action<? super MavenArtifactRepository> maven(String repo, Action<? super MavenArtifactRepository> action) {
        return (MavenArtifactRepository maven) -> {
            this.maven(repo).execute(maven);
            action.execute(maven);
        };
    }

    /**
     * Modifies the default template used for packages.
     *
     * <pre><code>
     *     githubPackages.template {
     *         credentials {
     *             username = 'name'
     *             password = 'token'
     *         }
     *     }
     * </code></pre>
     *
     * @param template The template to use
     */
    void template(Action<? super MavenArtifactRepository> template);
}

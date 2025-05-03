package me.jonathing.gradle.gpr;

import org.gradle.api.Action;
import org.gradle.api.artifacts.repositories.MavenArtifactRepository;
import org.gradle.api.file.DirectoryProperty;
import org.gradle.api.provider.ProviderFactory;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Objects;

class GithubPackagesExtensionImpl implements GithubPackagesExtension {
    private final DirectoryProperty gradleUserHomeDir;
    private Action<? super MavenArtifactRepository> template;

    GithubPackagesExtensionImpl(DirectoryProperty gradleUserHomeDir, ProviderFactory providers) {
        this.gradleUserHomeDir = gradleUserHomeDir;
        this.template = maven -> maven.credentials(credentials -> {
            credentials.setUsername(providers.gradleProperty("gpr.user").orElse(providers.environmentVariable("GITHUB_ACTOR")).getOrElse(DEFAULT_USER));
            credentials.setPassword(providers.gradleProperty("gpr.key").orElse(providers.environmentVariable("GITHUB_TOKEN")).getOrElse(DEFAULT_KEY));
        });
    }

    @Override
    public Action<? super MavenArtifactRepository> maven(String repo) {
        return maven -> {
            maven.setName(repo.replace('/', '-'));

            try {
                maven.setUrl(new URI(String.format("https://maven.pkg.github.com/%s%s", repo, repo.indexOf('/') < 0 ? "/*" : "")));
            } catch (URISyntaxException e) {
                throw new RuntimeException("Failed to parse URI for GitHub repo: " + repo, e);
            }

            try {
                this.template.execute(maven);
            } catch (Exception e) {
                // TODO Move to BuildListener on failure
                throw new RuntimeException("Failed to add GitHub repo: " + repo + "\n" + this.getTokenInstructions());
            }
        };
    }

    private String getTokenInstructions() {
        return "Try using your own token:\n" +
            "1. Generate a token at\n\thttps://github.com/settings/tokens/new?scopes=read:packages&description=GPR%20for%20Gradle\n" +
            "2. Open your global gradle.properties file at\n\t" + this.gradleUserHomeDir.get().getAsFile().getAbsolutePath() + "/gradle.properties\n" +
            "3. Add username and token:\n\tgpr.user=<GITHUB NAME>\n\tgpr.key=<GENERATED TOKEN>\n" +
            "4. You may need to restart your IDE\n" +
            "For more info see https://docs.github.com/en/packages/using-github-packages-with-your-projects-ecosystem/configuring-gradle-for-use-with-github-packages#authenticating-to-github-packages";
    }

    @Override
    public void template(Action<? super MavenArtifactRepository> template) {
        this.template = Objects.requireNonNull(template);
    }
}

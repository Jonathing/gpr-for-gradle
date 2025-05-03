package me.jonathing.gradle.gpr;

import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.file.DirectoryProperty;
import org.gradle.api.initialization.Settings;
import org.gradle.api.invocation.Gradle;
import org.gradle.api.model.ObjectFactory;
import org.gradle.api.plugins.ExtensionAware;
import org.gradle.api.plugins.PluginAware;
import org.gradle.api.provider.ProviderFactory;

import javax.inject.Inject;

class GithubPackagesPlugin<T extends PluginAware & ExtensionAware> implements Plugin<T> {
    private static final String[] EXTENSIONS = {"githubPackage", "githubPackagePublish"};

    @Inject
    public GithubPackagesPlugin() { }

    @Override
    public void apply(T target) {
        DirectoryProperty gradleUserHomeDir = this.getObjects().directoryProperty().fileValue(getGradle(target).getGradleUserHomeDir());
        ProviderFactory providers = this.getProviders();

        for (String ext : EXTENSIONS) {
            target.getExtensions().add(
                GithubPackagesExtension.class,
                ext,
                new GithubPackagesExtensionImpl(gradleUserHomeDir, providers)
            );
        }
    }

    private static Gradle getGradle(Object target) {
        if (target instanceof Project)
            return ((Project) target).getGradle();
        else if (target instanceof Settings)
            return ((Settings) target).getGradle();
        else if (target instanceof Gradle)
            return (Gradle) target;
        else
            throw new IllegalArgumentException("GitHub Packages Plugin can only be applied on Project, Settings, or Gradle");
    }

    protected @Inject ObjectFactory getObjects() {
        return this.injectFailed();
    }

    protected @Inject ProviderFactory getProviders() {
        return this.injectFailed();
    }

    private <S> S injectFailed() {
        throw new IllegalStateException("Cannot use in current context");
    }
}

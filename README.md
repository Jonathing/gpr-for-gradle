# Gradle Github Packages Plugin

GitHub Packages introduced features for hosting Maven packages for free, but these require credentials even for public
ones.

This grade plugin allows you to add a maven repository from GitHub Packages in one line. It also comes with a default
personal access token, allowing the use of public repositories without any extra setup
(see [Notes on automatic authentication](#notes-on-automatic-authentication)).

## Usage

### Add the plugin

First, add the Modding Legacy repository to the plugin management repositories.

```groovy
pluginManagement {
    repositories {
        gradlePluginPortal()
        maven { url = 'https://maven.moddinglegacy.com/maven' }
    }
}
```

Next, apply the plugin itself. It can be applied in your project's buildscript or the settings script.

```groovy
plugins {
    id 'me.jonathing.gradle.github-packages' version '2.0.0'
}
```

### Groovy

Within Groovy, you may add a package repository as follows:

```groovy
repositories {
    maven githubPackage.maven('owner/repo')
    // Or for all packages under the owner/org
    maven githubPackage.maven('owner')
}
```

### Kotlin

Add the GitHub repo to the repositories block:

```kotlin
repositories {
    maven githubPackage ("owner/repo")
    // Or for all packages under the owner/org
    maven githubPackage ("owner")
}
```

### Settings

GitHub repos can also be added within your settings script.

```groovy
dependencyResolutionManagement {
    repositories {
        mavenCentral()
        maven githubPackage.maven('owner/repo')
    }
}
```

#### Modifying default username/key

Use the `template` block within the `githubPackage` extension above `repositories` to edit the template applied to every
package below.

```groovy
githubPackage.template {
    credentials {
        username = 'name'
        password = 'token'
    }
}

repositories {
    maven githubPackage.maven('owner/repo') //now with different credentials!
}
```

You may also modify each package declaration as you would a regular maven repository:

```groovy
repositories {
    maven githubPackage.maven('owner/repo') {
        name = 'anotherName'
        credentials {
            username = 'name'
            password = 'token'
        }
    }
}
```

## Notes on automatic authentication

It appears sharing a PAT is currently the encouraged solution, as seen by
[this post](https://web.archive.org/web/20210121141140/https://github.community/t/download-from-github-package-registry-without-authentication/14407)
from a staff member on the GitHub community forms. The ability to anonymously access GitHub Packages is
[not planned](https://github.com/orgs/community/discussions/26634#discussioncomment-8527086).

In the worst case, the plugin will send a detailed message explaining exactly how to set up a token.

### Manual token setup

1. Generate a token at https://github.com/settings/tokens/new?scopes=read:packages&description=GPR%20for%20Gradle
2. Open your global gradle.properties file at `~/.gradle/gradle.properties`
3. Add username and token:
    ```ini
    gpr.user=<GITHUB NAME>
    gpr.key=<GENERATED TOKEN>
   ```
4. You may need to restart your IDE

For more info see
this [GitHub docs](https://docs.github.com/en/packages/using-github-packages-with-your-projects-ecosystem/configuring-gradle-for-use-with-github-packages#authenticating-to-github-packages)
page.

`GITHUB_ACTOR` and `GITHUB_TOKEN` will be used within GitHub workflows, unless the username and password are manually
changed.

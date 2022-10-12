# Release process

## Background

There's two repositories: snapshot & staging.

In order to get artifacts (any file that's uploaded to a repo) released, you push them to the staging repo address. This will create a new staging repo.

Then log into the Nexus website where you can "release" the newly-created staging repo, and it will be synced to the Maven Central Repo.

You can also "drop" a staging repo, if you want to delete it for some reason.

Once released, the staging repo artifacts will be deployed to the Maven Central Repository. Keep in mind that released artifacts cannot be changed!

## Commands

Use the Maven Release Plugin to cut a release, it will also deploy to Nexus for you where you'll have to "release" the staging repo that gets created.

You probably shouldn't need to call the Nexus Staging Maven Plugin manually.

### Maven Release Plugin

**You probably just want to follow the steps in this section!**

Note: the plugin docs are at the [Maven release plugin website](https://maven.apache.org/maven-release/maven-release-plugin).

First do a dry-run to find any errors beforehand:

```bash
mvn release:clean release:prepare -DdryRun
```

Once ready, run this:

```bash
mvn release:clean release:prepare
```

Fill in the info for the release. **The defaults should be fine!**

There should be a couple release commits now & a release tag pushed up Github.

If any error happens re-run (make sure you don't call `mvn release:clean` as this will get rid of the saved last-run info):

```bash
mvn release:prepare
```

If everything looks good, then publish the artifacts to the Nexus repo:
```bash
mvn release:perform
```

Since `autoReleaseAfterClose` in the Nexus Staging Maven Plugin is set to `false`, we have to login to the [Nexus portal](https://s01.oss.sonatype.org/) and "release" the staging repo in order to finalize the release.

Go to `Build Promotions` on the left-hand sidebar, and select `Staging Repositories` to find the staging repo & click "release".


### Nexus Staging Maven Plugin

You'd really only used these commands if you want to release a version independent of all the stuff above in the maven-release-plugin.

To release to the snapshot repo, make sure the version ends in `-SNAPSHOT`:
```bash
mvn clean deploy
```

[Read more here](https://central.sonatype.org/publish/publish-maven/#performing-a-snapshot-deployment)

To release to the release repo:
```bash
mvn clean deploy -P release
```

[Read more here](https://central.sonatype.org/publish/publish-maven/#performing-a-release-deployment)

### Other commands

To upload a file signature (`.asc`) to the staging repo:
```bash
mvn gpg:sign-and-deploy-file \
-DpomFile=target/sql-explain-0.0.1.pom \
-Dfile=target/sql-explain-0.0.1.jar \
-Durl=http://oss.sonatype.org/service/local/staging/deploy/maven2/ \
-DrepositoryId=sonatype_oss
```

## Plugin notes

- [maven-release-plugin](https://maven.apache.org/maven-release/maven-release-plugin/)
  - automates the tedious manual work of a release, for example, incrementing the version number in `pom.xml`
    - first commit: creates a tag for the release & removes `-SNAPSHOT` from the version number
    - second commit: bumps the version number for the next release & adds `-SNAPSHOT`
- [maven-gpg-plugin](https://maven.apache.org/plugins/maven-gpg-plugin/)
  - creates signatures (`.asc` files) & uploads to maven central
- [versions-maven-plugin](https://www.mojohaus.org/versions-maven-plugin/)
  - used for updating pom.xml dep versions
  - `mvn versions:use-latest-releases` updates all deps to latest releases
- [nexus-staging-maven-plugin](https://github.com/sonatype/nexus-maven-plugins/tree/main/staging/maven-plugin)
  - made by Sonatype, used for uploading artifacts to Nexus repos

## Settings

Add these `<profiles>` and `<servers>` to `~/.m2/settings.xml`, track down & set values for:

- `gpg-key`
- `jira-id`
- `jira-pwd`

```xml
<settings>
  <profiles>
    <profile>
    <id>ossrh</id>
    <activation>
        <activeByDefault>true</activeByDefault>
    </activation>
    <properties>
        <gpg.passphrase>gpg-key</gpg.passphrase>
    </properties>
    </profile>
  </profiles>
  <servers>
    <server>
      <id>ossrh</id>
      <username>jira-id</username>
      <password>jira-pwd</password>
    </server>
  </servers>
</settings>
```
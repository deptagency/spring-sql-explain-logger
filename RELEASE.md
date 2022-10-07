# Release process

## Background

There's two repositories: snapshot & release.

In order to get artifacts (any file that's uploaded to a repo) released, you push it to the snapshot repo. Think of this as staging the artifacts.

And then logging into the Nexus website, you can promote snapshot artifacts for release.

Keep in mind that released artifacts cannot be removed!

It's worth mentioning that you can upload artifacts directly to the release repo.

Once uploaded to the release repo, the artifacts will be reviewed & synced to the Central Repository.

## Commands

### Nexus Staging Maven Plugin

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

### Maven Release Plugin

Using the Maven release plugin:

```bash
mvn release:clean release:prepare
```

Fill in the info for the release.

Then to publish the artifacts, run:
```bash
mvn release:perform
```

Since `autoReleaseAfterClose` in the Nexus Staging Maven Plugin is set to false, we have to login to the [Nexus portal](https://s01.oss.sonatype.org/), and close the release in order to finalize it.

Go to `Build Promotions` on the left sidebar, and select `Staging Repositories`.

### Other commands

To upload a file signature (`.asc`) to the snapshot repo:
```bash
mvn gpg:sign-and-deploy-file \
-DpomFile=target/sql-explain-0.0.1.pom \
-Dfile=target/sql-explain-0.0.1.jar \
-Durl=http://oss.sonatype.org/service/local/staging/deploy/maven2/ \
-DrepositoryId=sonatype_oss
```

The `release-sign-artifacts` profile will be activated when the value of the Maven property `performRelease` is `true`.

When you use `maven-release-plugin` and run `mvn release:perform`, the property value will be set to `true`.

## Plugin notes

- [maven-release-plugin](https://maven.apache.org/maven-release/maven-release-plugin/)
  - uploads a new release (`.jar` & `.pom`) to maven central
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
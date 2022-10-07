# Release process

## Deploying
To release:
```bash
mvn release:perform
```

If the project has been released but not signed:
```bash
mvn gpg:sign-and-deploy-file \
-DpomFile=target/sql-explain-0.0.1.pom \
-Dfile=target/sql-explain-0.0.1.jar \
-Durl=http://oss.sonatype.org/service/local/staging/deploy/maven2/ \
-DrepositoryId=sonatype_oss
```

The `release-sign-artifacts` profile will be activated when the value of the maven property `performRelease` is `true`.

When you use `maven-release-plugin` and run `mvn release:perform`, the property value will be set to `true`.

## Plugins

- [maven-release-plugin](https://maven.apache.org/maven-release/maven-release-plugin/)
  - uploads a new release (`.jar` & `.pom`) to maven central
- [maven-gpg-plugin](https://maven.apache.org/plugins/maven-gpg-plugin/)
  - creates signatures (`.asc` files) & uploads to maven central
- [versions-maven-plugin](https://www.mojohaus.org/versions-maven-plugin/)
  - used for updating pom.xml dep versions
  - `mvn versions:use-latest-releases` updates all deps to latest releases

## Settings

Add these `<profiles>` and `<servers>` to `~/.m2/settings.xml`, track down & set values for:

- gpg-key
- jira-id
- jira-pwd

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
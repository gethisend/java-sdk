# Java SDK guidance

This independent Maven repository publishes `app.hisend:hisend-java`.
`pom.xml` targets Java 11. Preserve that compatibility; do not use later Java
features without an explicit requirement.

- `src/main/java/app/hisend/HisendClient.java`: client configuration/HTTP.
- `src/main/java/app/hisend/resources/`: endpoint resources.
- `src/main/java/app/hisend/exceptions/`: public error handling.
- `src/test/java/app/hisend/ClientTest.java`: JUnit Jupiter local HTTP test.

Run `mvn test` from this repository. Inspect the reported test count/Surefire
reports: `pom.xml` does not pin a modern Surefire plugin, so JUnit 5 discovery
must be verified, not assumed from Maven success. Report missing discovery as
a verification gap rather than silently altering build configuration.

Use a local HTTP server or mocks, never the live default service. Preserve public
methods, JSON contract and exception behavior; compare changed resources against
backend routes and docs when sibling repositories are available. Do not edit
`target/`, change release versions, install/publish artifacts, or call deployment
services without task scope.

# Pinemark People Portal

Employee directory and absence records for Pinemark People. Spring Boot 3, Java 17.

## Running locally

```bash
mvn -B spring-boot:run
```

The portal listens on `http://localhost:8080`. Sign in with one of the demo
accounts defined in `SecurityConfig`; they exist for local development only.
Production deployments authenticate against Entra ID.

## Security posture

This service is reviewed against the internal baseline before every release.

- Authentication is required for every route except `/login`, static assets and
  the health probe.
- Authorization is decided on the server, in the filter chain and again with
  `@PreAuthorize` on the handler. Templates render what they are given and make
  no access decisions.
- All SQL is parameterised through `JdbcTemplate`. No user input is concatenated
  into a statement.
- Output is escaped. `th:utext` is not used anywhere in the templates.
- CSRF protection, session fixation protection and a restrictive CSP are on.
- Passwords are hashed with BCrypt at strength 12.
- No credentials, tokens or keys are stored in this repository. Runtime
  configuration comes from the platform secret store, and CI reads the release
  token from repository secrets.

## Build pipeline

`.github/workflows/build.yml` runs on every push to `main`: it builds the
project, runs the test suite, stamps a release manifest and uploads the jar.

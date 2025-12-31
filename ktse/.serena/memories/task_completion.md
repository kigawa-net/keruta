# Task Completion Checklist for ktse

When completing any development task, ensure the following:

## Code Quality
- [ ] Run `./gradlew build` to ensure compilation succeeds
- [ ] Run `./gradlew test` to ensure all tests pass (when tests are added)
- [ ] Check for any compilation warnings
- [ ] Verify code follows project conventions (see code_style_conventions.md)

## Testing
- [ ] Add unit tests for new functionality
- [ ] Add integration tests for WebSocket endpoints
- [ ] Ensure test coverage for critical paths
- [ ] Run tests in CI/CD pipeline

## Documentation
- [ ] Update inline documentation if behavior changes
- [ ] Update README if new features are added
- [ ] Document configuration changes

## Deployment
- [ ] Verify application starts correctly
- [ ] Test WebSocket connections work
- [ ] Verify JWT authentication functions
- [ ] Check logs for any runtime errors

## Git Workflow
- [ ] Commit changes with descriptive message
- [ ] Push to appropriate branch
- [ ] Create pull request if needed
- [ ] Ensure CI/CD pipeline passes
# How Cherimoya is Tested

_Referencing [How SQLite Is Tested](https://www.sqlite.org/testing.html)_

Reliability of Cherimoya is achieved in part by thorough and careful automated testing.
The range of testing covers the annotation library and the Maven plugin, documented examples, and assumed common execution environments (JDK and Maven versions).



## Regression testing

Defects reported against Cherimoya cannot be considered as resolved until automated tests express the defect and prove remediation, within reason.
These regression tests ensure that prior defects do not re-emerge in future.



## Release criteria

A VCS commit is considered releasable provided that all of its components satisfy the following criteria:

- Code quality assessment tools don't indicate any outstanding problems, within reason: CI build log warnings, static analysis.
- The documentation is synchronized with the code, including version numbers, and automated testing of all examples.
- The described behavior of code samples from the documentation is confirmed via automated tests.
- All automated tests pass throughout the matrix of supported versions of JDKs and Maven.
- Test coverage from automated testing indicates a near-perfect or better test coverage rate.



## Design Principles

#### Workflow
- Keep users focused on their overarching task. Be mindful of the task. Be forever vigilant in reducing cognitive load.
- Seek a path of least resistance. Accommodate existing idioms, provided they are serviceable.
- Equip users with enough information to effectively diagnose and remediate problems.

#### Code
- The intention of a tract of code should be clear. This envelopes idioms, naming, one form or statement per idea.
- Endeavor to minimize the quantity and depth of nested exceptions.

#### I18n
- Localized strings are brief and concise, balanced with natural readability.
- Messages consisting of a single sentence don't end with periods.
- Messages are sourced from a I18n dictionary of template strings, which are in effect patterns.
- With usage, users will become accustomed to message patterns. The human eye can more easily direct focus onto the start and finish of a tract of text rather than its midparts. Leveraging these concerns, template parameters should come at or near one end of the message text so that readers can more quickly identify the message pattern. This assists them to direct their attention to ascertain relevant information burried within the message without increasing cognitive load and distracting them from their overarching taskflow.
- Warning and error messages require message codes, beneficial for improved SEO during problem diagnosis.



## Release checklist

#### Before release
- Update [CHANGELOG.md](CHANGELOG.md) to reflect the new version.
  - Replace the `_Unreleased_` attribute with the actual date.
- Update project versions in code and documentation.
- Choose a specific VCS commit identifier as the release target.
- Ensure the [release criteria](QUALITY.md) are satisfied.

#### Executing the release
- Send each component to JCenter.
- In Git, tag the release and push the tag to GitHub.

#### Immediately after release
- Smoke test each downloadable deliverable.
- Confirm correctness of:
  - All project URLs.
  - Default branch in GitHub.
  - Versions appearing in current documentation.
- Update [CHANGELOG.md](CHANGELOG.md) to reflect the next version.
  - Note this new version as `_Unreleased_`.

Design Principles
====

Code
----
- Let the codebase be saturated with direct use of `MojoExecutionException`. It cuts down on nested exceptions.

Workflow
----
- Keep users focused on their overarching task. For example, by reducing cognitive load.
- Support the user's overarching task 
- Give enough information to diagnose and remediate problems.

I18n
----
- Localized strings are brief and concise, balanced with natural readability.
- Messages consisting of a single sentence don't end with periods.
- Messages are sourced from a I18n dictionary of templated strings, which are in effect patterns. With usage, users will become accustomed to these patterns. The human eye can more easily direct focus onto the start and finish of a tract of text rather than its midparts. Leveraging these concerns, template parameters should come at or near either end of the message text so that readers can more quickly identify the message pattern then direct their attention to ascertain relevant information burried within the message without increasing cognitive load and distracting them from their overarching taskflow.


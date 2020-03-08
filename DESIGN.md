Design Principles
====

Code
----
Let the codebase be riddled with direct use of `MojoExecutionException`. It cuts down on nested exceptions.

I18n
----
- Localized strings are brief and concise, balanced with natural readability.
- Messages consisting of a single sentence don't end with periods.
- Messages are sourced from a I18n dictionary of templated strings, which are in effect patterns. Users will accustomize to these patterns with usage. The human eye can more easily focus on the beginning and end of a tract of text, rather than its middle. For these reasons, template parameters should come at or near the end of the message so that the users can ascertain the relevant information with less effort and distraction from the overarching task.

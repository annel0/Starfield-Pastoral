# StardewCraft Agent Rules

## Default Git Workflow
- Assume this repository uses Git.
- Before editing, check repository state (`git status`).
- After edits, summarize changed files and intent.
- Run a minimal verification step when possible (prefer `gradle classes`).

## Permission Model
- Default mode: edit allowed, no commit, no push.
- Only commit when the user explicitly says commit is allowed.
- Only push when the user explicitly says push is allowed.

## Fast Control Keywords
- `Git: inspect only` -> no edits.
- `Git: edit no commit` -> edits only.
- `Git: commit allowed` -> may commit, no push.
- `Git: full publish` -> may commit and push.

## Commit Convention
- If commit is allowed, ask for or follow user-provided commit prefix/scope.
- Keep commits focused and atomic.

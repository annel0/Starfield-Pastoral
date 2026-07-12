You are updating a Russian-localization fork of a Minecraft mod from its
upstream repository. Read `AGENTS.md` at the repository root first — it is
the authoritative list of what this fork changes relative to upstream and
the rules for adapting upstream changes without losing those changes.

The current working tree is the result of `git merge --no-commit --no-ff
upstream/main`, so it may already contain conflict markers, or it may have
merged cleanly but still contain semantic incompatibilities (upstream
renamed something this fork's translated code still refers to by the old
name, upstream restructured a file this fork has translated strings in,
etc.). Treat both cases as needing your attention — do not assume "no
`<<<<<<<` markers" means "nothing to do".

Your task, in order:

1. Read `AGENTS.md` fully before touching anything.
2. Resolve every conflict marker. For each conflicted hunk, determine
   whether the conflict is in a translated file/region per `AGENTS.md`
   section 3/4 — if so, keep the Russian translation and re-apply it to
   upstream's new surrounding code rather than picking one side wholesale.
3. Scan the full diff between upstream's previous and new commits (not
   just the conflicted hunks) for:
   - new hardcoded UI strings added in Java source that match the pattern
     described in `AGENTS.md` section 3 (and are not behind an
     op-permission/debug/IDE-only gate) — translate them into Russian.
   - any file that already has a Russian translation but was NOT flagged
     as conflicted by git, yet upstream changed nearby code in a way that
     could have silently broken the translation (e.g. a method signature
     the translated string is passed into changed) — fix these too.
4. Do not replace whole files with upstream's version without reading the
   diff first. Do not perform unrelated refactors, renames, or formatting
   changes.
5. Remove all remaining conflict markers.
6. Run `./gradlew build`. If it fails, fix compile errors caused by the
   merge. Do not delete or comment out failing code to force a green build
   — if you truly cannot resolve something, leave a clear `// TODO(sync):`
   comment explaining what's broken and why, and say so explicitly in your
   final summary.
7. Leave the working tree in a state ready to be committed as a single
   merge commit — do not run `git commit` yourself, the workflow does that.
8. In your final summary, list: which files you resolved and how, which
   new strings you translated, whether the build passed, and anything you
   flagged with a TODO for a human to look at (especially binary asset
   conflicts in the billboard textures, which you cannot resolve yourself).

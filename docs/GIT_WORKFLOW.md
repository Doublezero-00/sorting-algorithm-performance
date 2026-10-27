# Git Workflow

```
main                          stable, submittable, tagged v1.0
 └── develop                  integration branch
      ├── member1/csv-loader      (file upload + dataset handling)
      ├── member2/sorting         (5 algorithms)
      └── member3/benchmark-ui    (performance evaluation + GUI)
```

Flow: member branch → pull request → `develop` → (after full testing) pull request → `main`.

---

## A. Leader: one-time setup

### 1. Create the GitHub repository
Create an empty **private** repo on GitHub (no README, no .gitignore, no licence).
Add Member 1 and Member 3 under Settings → Collaborators.

### 2. Push the skeleton to `main`
Run these inside the project folder:

```bash
git init
git config user.name  "Your Name"
git config user.email "your-github-email@example.com"
git branch -M main
git add .
git commit -m "Add project skeleton with shared interfaces and package structure"
git remote add origin https://github.com/<your-username>/<repo-name>.git
git push -u origin main
```

### 3. Create `develop` and the member branches

```bash
git checkout -b develop
git push -u origin develop

git checkout -b member1/csv-loader  develop
git push -u origin member1/csv-loader

git checkout -b member2/sorting     develop
git push -u origin member2/sorting

git checkout -b member3/benchmark-ui develop
git push -u origin member3/benchmark-ui
```

### 4. Protect `main` and `develop`
GitHub → Settings → Branches → Add branch protection rule for `main` and `develop`:
- Require a pull request before merging
- Require at least 1 approval

(Branch protection on private repos may need a paid plan. If unavailable, simply agree
as a group never to push directly to `main` or `develop`.)

---

## B. Members: getting started

```bash
git clone https://github.com/<leader-username>/<repo-name>.git
cd <repo-name>
git config user.name  "Your Name"          # use your own real name
git config user.email "your-github-email@example.com"
git checkout member1/csv-loader            # or member2/sorting / member3/benchmark-ui
```

---

## C. Daily routine

```bash
git pull origin develop            # bring in the latest integrated work
# ... write code ...
git add <files you changed>
git commit -m "Add numeric column validation to ColumnValidator"
git push
```

Good commit messages: `Implement merge sort with auxiliary array`, `Fix heapify off-by-one`.
Bad ones: `update`, `final`, `fix`.

## D. Merging a finished feature into `develop`

1. `git pull origin develop` on your branch, resolve any conflicts, run the checks (see docs/BEGINNER_GUIDE.md, Part 4).
2. `git push`
3. On GitHub, open a **Pull Request**: base = `develop`, compare = your branch.
4. A teammate reviews and approves, then merges.
5. After merging, everyone runs `git pull origin develop` on their branch.

A feature is "ready to merge" only when it compiles, has basic tests, and does not break `develop`.

## E. Releasing to `main`

When the full flow works on `develop` (upload → select column → sort → results → best algorithm,
plus edge cases):

1. Do a **clean-clone test**: clone the repo into a fresh folder, compile and run it (see docs/BEGINNER_GUIDE.md, Part 4).
2. Open a Pull Request: base = `main`, compare = `develop`. Review and merge.
3. Tag the release:
   ```bash
   git checkout main
   git pull origin main
   git tag v1.0
   git push origin v1.0
   ```
4. Zip the project from `main` and upload to Moodle along with the recorded presentation.

## F. Rules of the team

- Never push directly to `main` or `develop`.
- Only edit files in your own package. Need a change elsewhere? Ask the owner or open a GitHub Issue.
- Do not change `SortingAlgorithm`, `Dataset` or `BenchmarkResult` without telling everyone.
- Commit from your own account with your real name: it is evidence of individual contribution.
- Merge each feature as soon as it works. Do not wait until the last week.
- Member 3 builds the GUI against stub data first, then connects the real loader and algorithms when they land in `develop`.

## G. Suggested timeline

| Week | Milestone |
|---|---|
| 1 | Skeleton on `main`; `develop` and member branches created |
| 2 | Member 1's loader merged into `develop` |
| 3 | Member 2's algorithms (with tests) merged into `develop` |
| 4 | Member 3's GUI and benchmark merged; integration testing; `develop` → `main`; tag `v1.0` |

## H. Handy commands

```bash
git status                  # what changed?
git log --oneline --graph   # history
git branch -a               # all branches
git stash / git stash pop   # temporarily shelve changes
git merge --abort           # back out of a messy merge
```

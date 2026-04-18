#!/usr/bin/env bash
# One-shot: initialize git, install gh if needed, create a private GitHub repo, push.
# Run from the Health Tracker folder in Git Bash:  bash setup-github.sh
set -e

REPO_NAME="health-tracker"
VISIBILITY="private"   # change to "public" if you want it public

cd "$(dirname "$0")"

echo "== Health Tracker → GitHub setup =="
echo

# --- 1. Ensure gh CLI is installed ---------------------------------------
if ! command -v gh >/dev/null 2>&1; then
  echo "GitHub CLI (gh) not found. Installing via winget..."
  if command -v winget.exe >/dev/null 2>&1 || command -v winget >/dev/null 2>&1; then
    winget install --id GitHub.cli -e --accept-source-agreements --accept-package-agreements
    # winget installs to a path that isn't in Git Bash's PATH yet — tell user to reopen shell.
    if ! command -v gh >/dev/null 2>&1; then
      echo
      echo "✓ gh installed. CLOSE THIS TERMINAL, open a new Git Bash, and run this script again."
      exit 0
    fi
  else
    echo "winget not available. Install gh manually:"
    echo "  https://cli.github.com/  (download the Windows installer)"
    echo "Then re-run this script."
    exit 1
  fi
fi

echo "✓ gh available: $(gh --version | head -1)"

# --- 2. Authenticate with GitHub -----------------------------------------
if ! gh auth status >/dev/null 2>&1; then
  echo
  echo "Not logged into GitHub yet. Running 'gh auth login'..."
  echo "  → choose: GitHub.com → HTTPS → Login with a web browser"
  gh auth login
fi

echo "✓ Authenticated as: $(gh api user --jq .login)"

# --- 3. Initialize git repo (if not already) -----------------------------
if [ ! -d .git ]; then
  git init -b main
  echo "✓ git initialized"
fi

# --- 4. Configure user.name / user.email if missing ----------------------
if [ -z "$(git config user.name)" ]; then
  GH_USER=$(gh api user --jq .login)
  git config user.name "$GH_USER"
fi
if [ -z "$(git config user.email)" ]; then
  GH_EMAIL=$(gh api user --jq '.email // empty')
  if [ -z "$GH_EMAIL" ]; then
    GH_ID=$(gh api user --jq .id)
    GH_USER=$(gh api user --jq .login)
    GH_EMAIL="${GH_ID}+${GH_USER}@users.noreply.github.com"
  fi
  git config user.email "$GH_EMAIL"
fi

# --- 5. Stage + commit ---------------------------------------------------
git add .gitignore index.html
if git diff --cached --quiet; then
  echo "Nothing new to commit."
else
  git commit -m "Initial commit: Health Tracker v1.10.0"
  echo "✓ initial commit created"
fi

# --- 6. Create remote repo (if missing) + push ---------------------------
GH_USER=$(gh api user --jq .login)

if gh repo view "$GH_USER/$REPO_NAME" >/dev/null 2>&1; then
  echo "✓ Remote repo $GH_USER/$REPO_NAME already exists."
  # Make sure origin is set
  if ! git remote | grep -q "^origin$"; then
    git remote add origin "https://github.com/$GH_USER/$REPO_NAME.git"
  fi
  git push -u origin main
else
  gh repo create "$REPO_NAME" --"$VISIBILITY" --source=. --remote=origin --push
fi

echo
echo "============================================"
echo "✓ Done!"
echo "Repo: https://github.com/$GH_USER/$REPO_NAME"
echo
echo "From now on, after edits:"
echo "  git add -A && git commit -m 'your message' && git push"
echo
echo "On your phone or claude.ai:"
echo "  1. Open claude.ai → Projects → New Project"
echo "  2. Connect GitHub → pick $REPO_NAME"
echo "  3. Ask Claude to read/edit index.html"
echo "============================================"

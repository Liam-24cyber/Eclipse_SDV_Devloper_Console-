#!/bin/bash

# Simple script to push code to GitHub Phase-2-Dev branch
# This assumes you have GitHub authentication configured

set -e

echo "==> Checking git status..."
git status

echo ""
echo "==> Checking out Phase-2-Dev branch..."
if git show-ref --verify --quiet refs/heads/Phase-2-Dev; then
    git checkout Phase-2-Dev
else
    # Try to fetch and checkout from remote
    git fetch origin Phase-2-Dev:Phase-2-Dev 2>/dev/null || {
        # Branch doesn't exist remotely, create it
        git checkout -b Phase-2-Dev
    }
fi

echo ""
echo "==> Adding all changes..."
git add .

echo ""
echo "==> Committing changes..."
git commit -m "Phase 2 Development: Complete webhook integration and deployment scripts" || {
    echo "No changes to commit or commit failed"
}

echo ""
echo "==> Current branch status:"
git status

echo ""
echo "======================================"
echo "Ready to push to GitHub!"
echo "======================================"
echo ""
echo "Repository: https://github.com/Liam-24cyber/Eclipse_SDV_Devloper_Console-.git"
echo "Branch: Phase-2-Dev"
echo ""
echo "To complete the push, run ONE of these commands:"
echo ""
echo "Option 1 - If you have SSH key configured:"
echo "  git push -u origin Phase-2-Dev"
echo ""
echo "Option 2 - If you need to use HTTPS with token:"
echo "  git push -u origin Phase-2-Dev"
echo "  (You'll be prompted for username and Personal Access Token)"
echo ""
echo "Option 3 - If you want to set remote to SSH:"
echo "  git remote set-url origin git@github.com:Liam-24cyber/Eclipse_SDV_Devloper_Console-.git"
echo "  git push -u origin Phase-2-Dev"
echo ""
echo "======================================"

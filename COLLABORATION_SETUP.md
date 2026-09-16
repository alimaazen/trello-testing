# Collaboration Tests Setup Guide

## Current Issue

The collaboration tests are failing because **Rashmi's account cannot be found** after invitation.

## Root Cause Analysis

Trello board invitations work differently depending on workspace membership:

### Case 1: Both accounts in SAME workspace
- ✅ Typeahead shows user immediately when typing email
- ✅ Clicking suggestion adds them to board **instantly**
- ✅ Member appears in member list right away

### Case 2: Accounts in DIFFERENT workspaces
- ❌ No typeahead suggestion
- ❌ Sends external email invite instead
- ❌ Member must **accept invitation via email** before appearing on board
- ❌ Member won't appear in list until they click the invite link

## Required Setup Steps

### Step 1: Check Current Workspace Membership

**Main Account (anay):**
1. Log into Trello with your main account
2. Go to https://trello.com/your/workspaces
3. Note the workspace name (e.g., "QE Training Workspace")

**Second Account (rashmi):**
1. Log into Trello with rashmisalaria3@gmail.com
2. Go to https://trello.com/your/workspaces
3. Check if you see the SAME workspace as the main account

### Step 2: Add Second Account to Main Workspace (If Not Already)

If Rashmi isn't in the same workspace:

1. **Main account**: Go to workspace settings
2. Click "Invite workspace members"
3. Enter: `rashmisalaria3@gmail.com`
4. Send invite

**Rashmi must then:**
5. Check email for workspace invite
6. Click "Join Workspace"
7. Accept the invitation

### Step 3: Verify Display Name

The code currently expects the display name to be **"Rashmi"** (capital R).

**Check Rashmi's actual display name:**
1. Log in as rashmisalaria3@gmail.com
2. Click profile avatar (top right)
3. Go to "Manage account"
4. Check "Full name" field

**If the name is different** (e.g., "rashmi", "Rashmi Salaria", etc.):
- Update `CollaborationTests.java` line 32:
  ```java
  private String secondAccountName() {
      return "Exact Name Here";  // ← Use exact name from account
  }
  ```

### Step 4: Test Invitation Manually

Before running automated tests:

1. Log in as main account
2. Create a test board "Manual-Test-Board"
3. Click "Share"
4. Type `rashmisalaria3@gmail.com` in search box
5. **Expected behavior if setup is correct:**
   - You should see a typeahead suggestion appear
   - Clicking it should add Rashmi immediately
   - Rashmi should appear in the member list right away
6. **If you DON'T see typeahead:**
   - Accounts aren't in same workspace yet
   - Complete Step 2 above

## Run Collaboration Tests Again

Once both accounts are in the same workspace:

```bash
# Run from IntelliJ or:
mvn test -Dtest=CollaborationTests
```

**Expected debug output (with fixes applied):**
```
[DEBUG inviteMemberByEmail] Inviting: rashmisalaria3@gmail.com
[DEBUG inviteMemberByEmail] Typeahead suggestion found, clicking it  ← KEY LINE
[DEBUG inviteMemberByEmail] Clicking send/share invite button
[DEBUG inviteMemberByEmail] Waiting for member to appear in list...
[DEBUG] Checking for member: 'Rashmi'
[DEBUG] Found 2 member items in the list  ← Should be 2: you + Rashmi
[DEBUG] Member text: 'Anay Purohit (anay) Admin'
[DEBUG] Member text: 'Rashmi (rashmi) Member'  ← Rashmi should appear here
[DEBUG] MATCH FOUND!
```

## If Still Failing

Check debug output for:

1. **"No typeahead suggestion"** → Accounts not in same workspace
2. **"Found 1 member items"** → Only you in list, Rashmi not added
3. **"Member text: 'rashmi salaria'"** → Display name mismatch (update code)

## Alternative: External Invite Flow (Not Recommended)

If you can't add both to same workspace, you'd need to:
1. Modify tests to send invite
2. **Manually** accept email invite for each test
3. Wait for acceptance before continuing
4. Not suitable for automated testing ❌

**Recommended**: Use same workspace approach above ✅

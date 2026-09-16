# Manual Test: Multi-Board Guest Invitation Flow

## Goal
Understand exactly what happens when inviting a workspace guest to a second board.

## Prerequisites
- Rashmi is already on ONE board (Collab-Invite) as a workspace guest
- You're about to add her to a SECOND board (Collab-Admin)

## Steps to Test Manually

1. **Login to your account** (anay)

2. **Go to dashboard**: https://trello.com

3. **Create a new test board** called "Manual-Collab-Test"

4. **Click Share button** on the board

5. **Type**: `rashmisalaria3@gmail.com` in the search box

6. **OBSERVE**: What appears in the typeahead dropdown?
   - [ ] Just "Rashmi Board member"?
   - [ ] "Rashmi" with billing warning text below?
   - Screenshot this!

7. **Click the typeahead suggestion**

8. **OBSERVE CAREFULLY**: What happens after clicking?
   - [ ] A. Does a **NEW popup/dialog appear** asking to confirm billing?
   - [ ] B. Does the suggestion just disappear and name appear in the invite field?
   - [ ] C. Does the Share dialog close immediately?
   - [ ] D. Something else?
   - **Screenshot this exact moment!**

9. **If a confirmation popup appeared** (option A):
   - What does the popup say?
   - What buttons does it have?
   - What are the button texts?
   - Take screenshot!

10. **Look at the Share button**:
    - What does it say? ("Share", "Add to board", "Invite", something else?)
    - Is it enabled or disabled?

11. **Click the Share button**

12. **OBSERVE**: What happens?
    - Does dialog close?
    - Does member appear in list immediately?
    - Does another confirmation appear?

13. **Look at member list** in the Share dialog (if still open):
    - Do you see Rashmi now?
    - Or still just yourself?

## Information Needed

Please provide:
1. Screenshots of steps 6, 8, 9 (if applicable)
2. Exact button texts you see
3. Whether any popups/confirmations appear
4. Whether Rashmi appears in the member list at the end

This will tell us **exactly** what flow we need to automate!

Mandatory test features with synchronization and multiple users.
(note: refreshing via navigating off of a page and back in) 
-- Edit acct tests --  A
create an account - Pass written to account txt file
change pass - Pass changed in txt
logout - Pass
try old pass - expected failed login - Pass
login w new pass - Pass
delete acct - Pass
try to login w old user/pass - Expected fail login - Pass
New User window - created - Pass
Create acct 
User 2 logs out 
Original user logs in with User 2 pass
- - - - - - - - - - - - - B
Navigate to view grades with a created post - Expected Post appears
*teacher changes grade* - Pass
Refresh 
grade changes - Expected, teacher change grade and grade visible for student - Pass
- - - - - - - - - - - - - C
teacher can choose student to change grade
teacher can sort this menu / change student
-- Reply Menu -- D
Create a reply - Expected Reply Created - Pass
Create 2nd reply with upload - Expected reply created with upload - Pass
*Teacher Upvotes* - Expected Upvote - Pass
*Teacher tries to upvote again* - Expected fail cannot upvote 2x - Pass
Refresh
See Upvote - Pass
Sort by Upvotes - Sort Back - Pass - Pass
Create Nested Reply - Pass
// Refresh
Go Into Nested Reply - Pass
*Teacher Deletes Reply *
Refresh
Reply instead added to the main discussion board and not sub reply - Pass
No File - Pass
- - - - - - - - - - - - - - - - - - E
Navigate to main menu
- No Courses - Pass
Teacher Creates a course - Pass
refresh 
Go into Course - Pass
- Only the base DFQ exists  - Pass
Teacher Creates DFQ - Pass
Teacher Uploads DFQ - Pass
Teacher Edits DFQ - Pass 
Student goes into one of the DFQ - 
Teacher Deletes DFQ
Student not able to interact with DFQ
Student refresh
DFQ does not exist

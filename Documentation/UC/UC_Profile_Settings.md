# Use-Case Specification: Profile Settings

# 1. Change Settings

## 1.1 Brief Description
This use case allows users to edit their personal profile settings. 
A user has a name, a profile picture and a email. The profile picture and the email is optional.

## 1.2 Mockup
### Profile Settings layout

# 2. Flow of Events

## 2.1 Basic Flow
Here is the activity diagram for inviting a new group member.
![Activity Diagram](../ActivityDiagrams/uc_profile_settings_activity_diagram.png)

## 2.2 Alternative Flows
n/a

# 3. Special Requirements
n/a

# 4. Preconditions
The main preconditions for this use case are:

 1. The users app instance is registered.
 2. The user is member of a group/shared flat.
 3. The user has started the app and has navigated to "Profile Settings".


# 5. Postconditions
## 5.1 Save changes / Sync with server

The server has to load the actual inputs of the components.
If a component has been edited and saved it must be synced with the server.

# 6. Extension Points
n/a

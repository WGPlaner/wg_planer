# Use-Case Specification: Add Item

# 1. Change Settings

## 1.1 Brief Description
This use case allows users to add produkts/items to a shopping list. 
A product has a title and may contain information the number of items and who the product shall be bought for.

## 1.2 Mockup
### New page to add a new Produkt to a shopping list
[![Mockup AddItem](../Mockups/uc_add_item.png)](../Mockups/uc_add_item.PNG)

# 2. Flow of Events

## 2.1 Basic Flow
Here is the activity diagram for adding a new item/product.
![Activity Diagram](../ActivityDiagrams/uc_add_item_activity_diagramm.png)

## 2.2 Alternative Flows
n/a

# 3. Special Requirements
n/a

# 4. Preconditions
The main preconditions for this use case are:

 1. The users app instance is registered.
 2. The user is member of a group/shared flat.
 2. The user has started the app and has navigated to "Shopping List".

# 5. Postconditions

### 5.1 Save changes / Sync with server
If a product has been added it must be synced with the server.

# 6. Extension Points
n/a

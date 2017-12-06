# Use-Case Specification: Add Item

# 1. Change Settings

## 1.1 Brief Description
This use case allows users to add products/items to a shopping list. 
A product has a title and may contain information like the number of items and who the product shall be bought for.

## 1.2 Mockup
### New page to add a new Produkt to a shopping list
![Mockup AddItem](../Mockups/uc_add_item.PNG)

## 1.3 Screenshot
### Add Item Page "blank"
![Add Item Page "blank"](../Screenshots/uc_add_item_blank.png)

### Add Item Page "filled"
![Add Item Page "Filled"](../Screenshots/uc_add_item_filled.png)

### Updated Shopping List
![Updated Shopping List](../Screenshots/uc_add_item_shopping_list.png)

# 2. Flow of Events

## 2.1 Basic Flow
Here is the activity diagram for adding a new item/product.
![Activity Diagram](../ActivityDiagrams/uc_add_item_activity_diagram.png)

And here is a screenshot of the `.feature` file for this use case:
![.feature file](./FeatureFiles/feature_file_add_shopping_list_item.png)

## 2.2 Alternative Flows
n/a

# 3. Special Requirements
n/a

# 4. Preconditions
The main preconditions for this use case are:

 1. The users app instance is registered.
 2. The user is member of a group/shared flat.
 3. The user has started the app and has navigated to "Shopping List".

# 5. Postconditions

### 5.1 Save changes / Sync with server
If a product has been added it must be synced with the server.

# 6. Extension Points
n/a

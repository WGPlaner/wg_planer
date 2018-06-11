# Use-Case Specification: Buy Item

# 1. Buy Item

## 1.1 Brief Description
This use case allows users to buy products/items.
A product has a title and may contain information like the number of items and who the product shall be bought for.
Bought items shall be removed from the list.

## 1.2 Mockup
### Checked List to buy produkts/items
[![Mockup CheckedList](../Mockups/uc_shopping_list_CheckedList_200px.png)](../Mockups/uc_shopping_list_CheckedList.PNG)

## 1.3 Screenshot
### Shopping List with checked products
![Shopping List with checked products](../Screenshots/shopping_list_screenshot_selected.png)

### Shopping List after the products were bought
![Shopping List after the products were bought](../Screenshots/shopping_list_no_items_screenshot.png)

# 2. Flow of Events

## 2.1 Basic Flow
Here is the activity diagram for buying one or more products/items.
![Activity Diagram](../ActivityDiagrams/uc_buy_item_activity_diagram.png)


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
If a product has been bought it must be synced with the server.

# 6. Function Points
To calculate function points, we used the tool on [http://groups.umd.umich.edu](http://groups.umd.umich.edu/cis/course.des/cis375/projects/fp99/main.html).

For this use case we got a score of *18.02* function points. It took 500min to implement this use case.

![Function Points 1](../FunctionPoints/BuyItem_1.jpg)
![Function Points 1](../FunctionPoints/BuyItem_2.jpg)
![Function Points 1](../FunctionPoints/BuyItem_3.jpg)

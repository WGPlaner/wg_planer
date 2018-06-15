# Use-Case Specification: Add Item

# 1. Add Item

## 1.1 Brief Description
This use case allows users to add products/items to a shopping list.
A product has a title and may contain information like the number of items and who the product shall be bought for.

## 1.2 Mockup
### New page to add a new Produkt to a shopping list
![Mockup AddItem](../Mockups/uc_add_item.PNG)

## 1.3 Screenshot
### Add Item functionality "blank"
![Add Item functionality "blank"](../Screenshots/add_item_screenshot_blank.png)

### Add Item functionality "input"
![Add Item functionality "input"](../Screenshots/add_item_screenshot_input.png)

### Add Item functionality "select users"
![Add Item functionality "select users"](../Screenshots/add_item_screenshot_select.png)

### Updated Shopping List
![Updated Shopping List](../Screenshots/shopping_list_screenshot_items.png)

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

# 6. Function Points
To calculate function points, we used the tool on [http://groups.umd.umich.edu](http://groups.umd.umich.edu/cis/course.des/cis375/projects/fp99/main.html).

For this use case we got a score of *54* function points. It took 720min to implement this use case.

![Function Points](../FunctionPoints/Add_Item.png)

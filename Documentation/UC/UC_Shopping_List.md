# Use-Case Specification: Shopping List

# 1. Shopping List

## 1.1 Brief Description
This use case allows users to list all items which are currently in the shopping list. A item has a title and may contain information the number of items and who the product shall be bought for. Bought items shall be removed from the list. Furthermore the user can sort the items regarding their attributes.

## 1.2 Mockup
[![Mockup CheckedList](../Mockups/uc_shopping_list_CheckedList_200px.png)](../Mockups/uc_shopping_list_CheckedList.PNG)

## 1.3 Screenshot

### Empty Shopping List
![Shopping List without items](../Screenshots/shopping_list_no_items_screenshot.png)
### Shopping List with items
![Shopping List with items](../Screenshots/shopping_list_screenshot_items.png)
### Shopping List selected items
![Shopping List selected items](../Screenshots/shopping_list_screenshot_selected.png)
### Shopping List sort items dialog
![Shopping List sort items dialog](../Screenshots/shopping_list_screenshot_sort.png)

# 2. Flow of Events

## 2.1 Basic Flow

### Activity Diagram
![Activity Diagram](../ActivityDiagrams/uc_shopping_list_activity_diagram.png)

### .feature File
![.feature file](./FeatureFiles/feature_file_manage_shopping_list.png)


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
If a product has been added, edited or removed, it must be synced with the server.

# 6. Function Points
To calculate function points, we used the tool on [http://groups.umd.umich.edu](http://groups.umd.umich.edu/cis/course.des/cis375/projects/fp99/main.html).

For this use case we set a score of *20* function points. It took 790min to implement this use case.

![Function Points](../FunctionPoints/ShoppingList.png)

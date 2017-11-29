#Feature: Manage Shopping List
#
#   Scenario: Add Item to Shopping List
#    Given I have opened activity "AddItemActivity"
#     When I enter text into input field with id "name"
#      And I enter a number into input field with id "number"
#      And I tap on toolbar button with text "Save"
#     Then "AddItemActivity" should be closed
#
#   Scenario: Reorder Items
#    Given I have opened the activity "HomeActivity"
#      And I have navigated to section "ShoppingList"
#     When I select a "Requested for" from spinner with id "categories"
#     Then I should see the Items ordered by "Requested for"
#
#   Scenario: Buy Item
#    Given I have opened "HomeActivity"
#      And I have navigated to section "ShoppingList"
#     When I check an item
#     Then I should see drawable "ic_check" as drawable of FloatingActionButton
#     When I tap on FloatingActionButton
#     Then Checked items should be removed

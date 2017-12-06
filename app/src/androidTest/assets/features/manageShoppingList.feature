Feature: Add a shopping list item

  Scenario: Add an invalid item without someone that needs it
      Given The shopping list view is open
       When I click button with id "fab"
        And I enter "Milk" into input field with id "add_item_name_input"
        And I enter "123" into input field with id "add_item_number_input"
        And I click button with id "add_item_save"
       Then Toast with text "Please fill out all fields" is displayed

  Scenario: Add an invalid item without a title.
      Given The shopping list view is open
       When I click button with id "fab"
        And I enter "123" into input field with id "add_item_number_input"
        And I click button with id "add_item_save"
       Then Toast with text "Please fill out all fields" is displayed

  Scenario: Add an invalid item without a count.
      Given The shopping list view is open
       When I click button with id "fab"
        And I enter "Milk" into input field with id "add_item_name_input"
        And I click button with id "add_item_save"
       Then Toast with text "Please fill out all fields" is displayed

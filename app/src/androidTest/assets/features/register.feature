Feature: Register

  Scenario: Invalid EMail-Address
    Given I navigate to State EMail Address Screen
     Then I enter "InvalidMail" into input field having id "email"
      And I press button with text "continue"
     Then Toast with text "Invalid EMail address" should be displayed

  Scenario: Invalid Display Name
    Given I navigate to Pick Display Name Screen
     Then I enter "" into input field having id "displayName"
      And I press button with text "continue"
     Then Toast with text "Choose a name" should be displayed

  Scenario: Registration successful
    Given I open WGPlaner Application
      And I am not registered yet
     Then I press button with text "continue"
     Then I enter "Arne" into input field with id "displayName"
      And I press button with text "continue"
     Then I press button with text "skip"
     Then I press button with text "skip"
     Then I should see activity SetUpActivity
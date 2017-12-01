Feature: Register

  Scenario: Invalid Display Name
      Given The app is opened
       When I click button with id "btn_continue_welcome"
        And I enter "" into input field with id "input_username"
        And I click button with id "btn_continue_pick_display_name"
       Then Toast with text "Please pick a Username!" is displayed

  Scenario: Invalid E-Mail
      Given The app is opened
       When I click button with id "btn_continue_welcome"
        And I enter "Andre" into input field with id "input_username"
        And I click button with id "btn_continue_pick_display_name"
        And I click button with id "btn_skip_upload_profile_picture"
        And I enter "notAnEMail" into input field with id "input_email"
        And I click button with id "btn_continue_state_email_address"
       Then Toast with text "Email is invalid" is displayed

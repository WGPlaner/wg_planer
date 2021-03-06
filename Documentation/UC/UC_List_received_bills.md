# Use-Case Specification: List received bills

# 1. List received bills

## 1.1 Brief Description
This use case allows users to list their received bills. A bill contains bought items and costs.

## 1.2 Mockup
### Page listing received bills
![List received bills](../Mockups/uc_list_received_bills_list.png)

### Detail page - received bill
![Received bill details](../Mockups/uc_list_received_bills_detail.png)

### Detail page - received bill action
![Received bill details action menu](../Mockups/uc_list_received_bills_detail_actions.png)

## 1.3 Screenshot
### List received bills - No bill
![Received bills without bill](../Screenshots/received_bills_empty.png)

### List received bills - With bills
*tbd*

# 2. Flow of Events

## 2.1 Basic Flow
![Use Case SeeReceivedBills](../ActivityDiagrams/uc_see_received_bills.png)

## 2.2 Alternative Flows
n/a

# 3. Special Requirements
n/a

# 4. Preconditions
The main preconditions for this use case are:

 1. The users app instance is registered.
 2. The user is member of a group/shared flat.
 3. The user has started the app and has navigated to "Received Bills".

# 5. Postconditions
As a result of this use case the user can see the list of received bills.

# 6. Function Points
To calculate function points, we used the tool on [http://groups.umd.umich.edu](http://groups.umd.umich.edu/cis/course.des/cis375/projects/fp99/main.html).

For this use case we got a score of *27* function points. It was estimated to take us 531min to implement this use case.

![Function Points](../FunctionPoints/ListReceivedBills.PNG)

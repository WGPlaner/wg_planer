# Software Requirements Specification 

## Table of contents
- [Table of contents](#table-of-contents)
- [Introduction](#1-introduction)
    - [Purpose](#11-purpose)
    - [Scope](#12-scope)
    - [Definitions, Acronyms and Abbreviations](#13-definitions-acronyms-and-abbreviations)
    - [References](#14-references)
    - [Overview](#15-overview)
- [Overall Description](#2-overall-description)
    - [Vision](#21-vision)
    - [Use Case Diagram](#22-use-case-diagram)
- [Specific Requirements](#3-specific-requirements)
    - [Functionality](#31-functionality)
    - [Usability](#32-usability)
    - [Reliability](#33-reliability)
    - [Performance](#34-performance)
    - [Supportability](#35-supportability)
    - [Design Constraints](#36-design-constraints)
    - [Online User Documentation and Help System Requirements](#37-on-line-user-documentation-and-help-system-requirements)
    - [Purchased Components](#purchased-components)
    - [Interfaces](#39-interfaces)
    - [Licensing Requirements](#310-licensing-requirements)
    - [Legal, Copyright And Other Notices](#311-legal-copyright-and-other-notices)
    - [Applicable Standards](#312-applicable-standards)
- [Supporting Information](#4-supporting-information)

## 1. Introduction

### 1.1 Purpose
This Software Requirements Specification was created to collect and organize the requirements for the WGPlaner Android Application.
It includes a thorough description of the expected functionality for the project, as well as the nonfunctional requirements.
These are crucial for the purposes of establishing the understanding between the suppliers of the software and the customers,
as well as minimizing the risks connected to the misinterpreting customer’s expectations. The document will furthermore
provide the basis for costs-estimation and later validation of the results achieved.

### 1.2 Scope
This SRS applies to the entire WGPlaner project. WGPlaner is a free android application to manage shared flats.

ACTORS: There are two types of actors: **users** and **admins**

 - User: Person that downloaded and installed the "WGPlaner" android application
 - Admin: User that also manages the shared flat.

SUBSYSTEMS:
 - *Registration / User system*: Allows the actors to be identified as users/admins and store personal information relevant to the application.
 - *Dashboard*: Gives users an overview of their shared flats data.
 - *Shopping list*: Allows user to create and manage lists with needed products.
 - *Accounting*: Allows users to create and split bills.
 - *Tasks*: Allows users to create tasks such as cleaning.
 - *Pinboard*: Short notes that users want to share with the flat.
 - *Calendar*: Gives users and overview of events and gives reminders.
 - *Gamification*: Makes finishing tasks more fun.
 - *Settings*: Allows users to update their profile and notification settings.

### 1.3 Definitions, Acronyms and Abbreviations

| Abbrevation |                                        |
| ----------- | -------------------------------------- |
| SRS         | Software Requirements Specification    |
| UC          | Use Case                               |
| n/a         | not applicable                         |
| tbd         | to be determined                       |
| MTTR        | Mean Time To Repair                    |
| FAQ         | Frequently asked Questions             |

| Definition                          |     |
| ----------------------------------- | --- |
| Software Requirements Specification | a document, which captures the complete software requirements for the system, or a portion of the system. |
| Use Case                            | a list of actions or event steps, typically defining the interactions between a role (known in the Unified Modeling Language as an actor) and a system, to achieve a goal. |
| Mean Time to Repair                 | How long is the system allowed to be out of operation after it has failed? |

### 1.4 References

| Title                                                              | Date       | Publishing organization   |
| -------------------------------------------------------------------|:----------:| ------------------------- |
| [WGPlaner Blog](http://wgplanerblog.wordpress.com/)                | 2017-11-26 | WGPlaner Team             |
| [GitHub - Server](http://github.com/WGPlaner/wg_planer_server)     | 2018-06-11 | WGPlaner Team             |
| [GitHub - Client](http://github.com/WGPlaner/wg_planer)            | 2018-06-11 | WGPlaner Team             |
| [UC Add Item](../UC/UC_Add_Item.md)                                | 2017-12-06 | WGPlaner Team             |
| [UC Buy Item](../UC/UC_Buy_Item.md)                                | 2017-12-06 | WGPlaner Team             |
| [UC Invite New Group Member](../UC/UC_Invite_New_Group_Member.md)  | 2017-12-06 | WGPlaner Team             |
| [UC List Bought Items](../UC/UC_List_bought_items.md)              | 2018-06-11 | WGPlaner Team             |
| [UC List Received Bills](../UC/UC_List_received_bills.md)          | 2018-06-11 | WGPlaner Team             |
| [UC Profile Settings](../UC/UC_Profile_Settings.md)                | 2017-12-06 | WGPlaner Team             |
| [UC Registration](../UC/UC_Registration.md)                        | 2017-11-26 | WGPlaner Team             |
| [UC List Sent Bills](../UC/UC_sent_bills.md)                       | 2018-06-11 | WGPlaner Team             |
| [UC Shopping List](../UC/UC_Shopping_List.md)                      | 2017-11-26 | WGPlaner Team             |
| [Test Plan](../TestPlan/TestPlan.md)                               | 2018-06-11 | WGPlaner Team             |
| [SRS](../SRS/SRS.md)                                               | 2017-11-26 | WGPlaner Team             |

### 1.5 Overview
The remainder of this document is structured in the following way: In the next chapter, the Overall Description section,
an overview of the functionality of the product and an use-case-diagram is given. The third chapter, the Requirements
Specification section, provides an more detailed description of the requirements. In order to achieve a high level of
specification in defining the requirements, all functions presented in the diagram are separated into subsections of
section "3.1 Functionality". Further requirements like usability and supportability are listed in chapters 3.2 through
3.12. Supporting information is given in chapter four.

## 2. Overall Description

### 2.1 Vision
Studying often means living in a shared flat. Costs of groceries and other purchases are shared between each other and
cleaning becomes a group task. But managing all of this has been a hassle for years. Whose turn is it to clean the bathroom?
How much money do I owe the others?

Our android application "WGPlaner" takes care of this. Shopping lists can be created, costs are shared, overviews on how
much each person owes each other help to avoid stress and living in a shared flat becomes just more pleasant.

### 2.2 Use Case Diagram
![WGPlaner Use Case Diagram][UseCaseDiagram]


## 3. Specific Requirements

### 3.1 Functionality
This section will list all functional requirements for "WGPlaner" and explain their functionality. Each of the following
subsections represents a subsystem of our application.

#### 3.1.1 Ŕegistration
As soon as our app is installed and opened, the user gets to choose from two options. He can either create a shared flat
if his doesn't exist already, or he can join one. In the first case the user becomes the admin and can create an access
code that is valid for 24h. This code can be shared through WhatsApp, e-mail and other channels and allows other shared
flat members to join the newly created shared flat. The admin may create a new code at any time and the old one gets
invalidated. By joining a shared flat, users create a new profile and can set their username, profile image, etc.
If all members joined the shared flat, then the following functionality comes into play.

#### 3.1.2 Dashboard
The dashboard is the starting point of our application. With different tiles  that contain important information, the
user gets a quick overview of his shared flat. The user can navigate through the app with a click on these tiles.
An example would be a tile "Shopping List" that shows how many different products the list contains.

#### 3.1.3 Shopping List
The shopping list gives an overview on products that need to be bought. It's possible to add new products to the list,
to update or remove old ones that the user created and to set the number of items needed. Because not every item should
be shared equally between everyone, it's possible to select the members that the item is bought for.

When an item is bought, the user shopping marks the product as bought and all other members of the shared flat see that
the user is currently shopping. This way we avoid that another person buys the item simultaneously. After shoping, the
user confirms that he has bought the checked items and they get transferred from the shopping list to the accounting page.
The shopping list can be sorted by "customer" to get a better grasp of who needs what.

#### 3.1.4 Accounting
The accounting page allows the user to bill the costs of each shared flat member. The functionality gets its data from
the shopping list. Each user can see what was bought for whom and can set the price of the items he has bought so that
the app can sum up the costs and create the bill.

It's also possible to set fixed costs such as rent or electricity costs.

The data is used to create bills either manually or automatically. It's possible to specify a date, e.g. the end of each
month, to create the bill automatically. Each member get's a listing on how much he ows whom since the last bill. He can
also set a deadline to when he expects the others to pay their dept. The app reminds one if the deadline is near.

A confirmation by both parties is required if the depts are paid and after that the bill gets archived.

#### 3.1.5 Tasks
The tasks page allows users to create tasks. A list of TODOs is shown and each user may add, update or remove a task.
This option is perfect for cleaning plans as it is possible to create repeating tasks that occur weekly, monthly or each
N days and to set the user that should do the task.

The dashboards shows the user's tasks that are close. An example would be "It's your turn to clean the bathroom.".

#### 3.1.6 Pinboard
The user can create notes on the pinboard. A note can contain hastags and a date. If a date is given, then all members of
the flat can either accept or reject the appointment/event. A user can comment on a note to exchange information.
The pinboard informs the user about new or changed information.

#### 3.1.7 Calendar
The calendar page shows each user his own tasks and their deadlines. Also included in the calendar are notes with
dates from the pinboard that the user has acceptet. The calendar notifies the user of close events.

#### 3.1.8 Gamification
Each task gets a number of points that the user can define. A user earns these points by finishing a task. A ranking
motivates the user to finish more tasks and the members of the shared flat may decide on a bonus that the user with
the most points gets at the end of a month.

#### 3.1.9 Settings
This page contains settings for the user's profile and notifications. The user can change his username, profile image
and e-mail address or delete his account.

The shared flat's administrator can change the flat's image and name, can create a new access code or delete the WG.
If he deletes his account, one of the remaining members becomes the new admin.

### 3.2 Usability
We plan on designing the user interface as intuitive and self-explanatory as possible to make the user feel as comfortable
as possible using the app. Though help documents will be available, it should not be necessary to use them.

#### 3.2.1 No training time needed
Our goal is that a user installs the android application, opens it, get’s a small introduction screen and is able
to use all features without any explanation or help.

#### 3.2.1 Familiar feeling
Developing an android application, Google’s Material Design should be used and therefore their guidelines regarding design.
This way the user is able to interact in familiar ways with the app without having to get to know new interfaces.

#### 3.2.1 Natural workflow
The workflow should reflect the real life and not work against it. If a specific order of tasks is widespread,
then the app should do it in the same way. 

### 3.3 Reliability

#### 3.3.1 Availability and MTTR
The server shall be available at least 95% of the time which is equivalent to ca. 1 hour downtime a day. Downtime is tolerable
during nighttime as it is very unlikely that users upload or change data at night. But the server must be available during
"rush hours" when most people go shopping. The time to repair bugs should be as low as possible.

#### 3.3.2 Defect Rate
 - There must be no bugs regarding the accounting section as it is crucial that shared flat members don’t pay each other more or less than is actually correct.
 - Loss of data locally might be acceptable if it is stored on the server.
 - Data synchronization between server and client must be correct. Data races must not occur.

### 3.4 Performance

#### 3.4.1 Response time
Should be as low as possible. Maximum response time is 5 seconds. Average response time should be less than 2 seconds. 

#### 3.4.2 Capacity
The system should be capable to manage thousands of registered users and up to hundred users at the same time on one server.
If the capacity exceeds that number, it should be possible to scale and use more servers.

#### 3.4.3 Connection Bandwidth
The size of data to be synchronized between the server and client should be minimal, e.g. renaming an item must not lead to
downloading all of existing data on the server.

### 3.5 Supportability

#### 3.5.1 Coding standards
In order to maintain supportability and readability of our code, we will try to adopt the latest clean code standard as far as
possible and use the [Google Java Style Guide][GoogleGuidelines] for naming conventions, formatting and programming
practices throughout the project.

#### 3.5.2 Maintenance Utilities
In order to test language and platform versions, a continuous integration service is required which runs tests on
combinations of platform and language versions.

### 3.6 Design Constraints
We are focused on providing a modern design regarding both code and application.

As "WGPlaner" is an android application, the chosen programming language is Java. The MVC architecture shall be used to
differentiate between UI and the actual logic.

The server's operating system must support MySQL and programs compiled using Go. A RESTful API shall be used to communicate between client and server.

Therefore following platforms and language must be supported.
 - Android 4.4 and above
 - Java SE 7 and above
 - Go 1.9 and above

### 3.7 On-line User Documentation and Help System Requirements
Our goal is to make our application as intuitive as possible. Nevertheless a help system will be created that contains FAQs, etc.
This might be helpful to users that do not yet use our application but want to know how it works.
At the first start of our application, a short introduction will be shown that shall also be available online.

### 3.8 Purchased Components
A server hosted a https://netcup.de will be rent to run the server application.  
Currently there are no other purchased components.

### 3.9 Interfaces

#### 3.9.1 User Interfaces
There will be the following user interfaces implemented which will solely be available in the android application:
 - **Registration screen** showing input fields to create an account
 - **Set-Up screen** showing input fields to create a group or join one
 - **Dashboard screen**
    - shows active access key if available
    - shows overviews for each main section
 - **Shopping List screen** shows lists with items ordered by a given key (e.g. category)
 - **Accounting screen** to manage bills, show bought items, fixed costs, etc.
 - **Tasks screen** to list active tasks and whose turn it is
 - **Pinboard screen** to shows note and comments
 - **Calendar screen** to manage tasks, events, etc.
 - **Settings screen** to manage the user's or shared flat's settings

#### 3.9.2 Hardware Interfaces
n/a

#### 3.9.3 Software Interfaces
“WGPlaner” should be running on all android devices with Android version 4.0 and above.

#### 3.9.4 Communications Interfaces
The client will connect to the server over `HTTPS` on port `443`. An unencrypted connection over `HTTP` on port `80` shall not be supported.

### 3.10 Licensing Requirements
Under [MIT license][license].

### 3.11 Legal, Copyright, and Other Notices
The "WGPlaner" team will not take any responsibility for incorrect bills or lost data. The "WGPlaner" logo
may only be used for the official "WGPlaner" android application.

### 3.12 Applicable Standards
The following Clean Code standards are going to be applied to the code as far as possible:
 1. Intuitive names of variables and methods.
 2. Comply with coding conventions of the language of choice ([Google Java Style Guide][GoogleGuidelines]).
 3. Comments used to navigate through the code but not polluting it.
 4. Design patterns integration.
 5. Each method does one thing and does it well.
 6. No hard-coded strings.
 7. No premature optimization.


## 4. Supporting Information

**For more information contact:**
 - Andre Meyering
 - Arne Schulze
 - ~~Nina Wieland~~ (has left the team)

[UseCaseDiagram]:   use_case_diagram.png
[GoogleGuidelines]: https://google.github.io/styleguide/javaguide.html
[license]:          ../../LICENSE

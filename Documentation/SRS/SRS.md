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
This Software Requirements Specification was created to collect and organize the requirements for the WGPlaner Android Application. It includes a thorough description of the expected functionality for the project, as well as the nonfunctional requirements. These are crucial for the purposes of establishing the understanding between the suppliers of the software and the customers, as well as minimizing the risks connected to the misinterpreting customer’s expectations. The document will furthermore provide the basis for costs-estimation and later validation of the results achieved.

### 1.2 Scope
This SRS applies to the entire WGPlaner project. WGPlaner is a free android application to manage shared flats.

ACTORS: There are two types of actors: **users** and **admins**

 - User: Person that downloaded and installed the "WGPlaner" android application
 - Admin: User that also manages the shared flat.

SUBSYSTEMS:
 - *TODO*

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

| Title                      | Date       | Publishing organization   |
| -------------------------- |:----------:| ------------------------- |
| *coming soon...*           |            | WGPlaner Team             |

### 1.5 Overview
The remainder of this document is structured in the following way: In the next chapter, the Overall Description section, an overview of the functionality of the product and an use-case-diagram is given. The third chapter, the Requirements Specification section, provides an more detailed description of the requirements. In order to achieve a high level of specification in defining the requirements, all functions presented in the diagram are separated into subsections of section "3.1 Functionality". Further requirements like usability and supportability are listed in chapters 3.2 through 3.12. Supporting information is given in chapter four.

## 2. Overall Description

### 2.1 Vision
Studying often means living in a shared flat. Costs of groceries and other purchases are shared between each other and cleaning becomes a group task. But managing all of this has been a hassle for years. Whose turn is it to clean the bathroom? How much money do I owe the others?

Our android application "WGPlaner" takes care of this. Shopping lists can be created, costs are shared, overviews on how much each person owes each other help to avoid stress and living in a shared flat becomes just more pleasant.

### 2.2 Use Case Diagram
![WGPlaner Use Case Diagram][UseCaseDiagram]


## 3. Specific Requirements

### 3.1 Functionality
This section will list all functional requirements for "WGPlaner" and explain their functionality. Each of the following subsections represents a subsystem of our application.

#### 3.1.1 Ŕegistration
As soon as our app is installed and opened, the user gets to choose from two options. He can either create a shared flat if his doesn't exist already, or he can join one. In the first case the user becomes the admin and can create a code that is valid for 24h. This code allows other shared flat members to join the newly created shared flat. The admin may create a new code at any time and the old one gets invalidated. By joining a shared flat, users create a new profile and can set their username, profile image, etc.
If all members joined the shared flat, then the following functionality comes into play.

<!--
Sobald die App heruntergeladen und gestartet wurde, hat der User zwei unterschiedliche Optionen. Zum einen kann er, wenn es noch keine existierende WG gibt, selbst eine WG gründen. In diesem Fall erhält der Gründer (Admin) einen Code, welcher 24 Stunden gültig ist. Nach dem Ablaufen des Codes kann sich der Admin einen neuen generieren lassen. Den besagten Code kann er nun an seine anderen WG Mitglieder weiterleiten. Die anderen Mitglieder können nun die zweite Option wählen. Hierbei erstellen sie einen Account, in dem sie ein Profilbild hochladen und einen Usernamen eingeben können. Mit der Hilfe des Codes können sie einer bereits bestehenden Gruppe beitreten.
Wenn die WG nun komplett ist, gibt es mehrere verschiedene Funktionalitäten. Diese sind im Folgenden aufgelistet.
-->

#### 3.1.2 Dashboard
The dashboard is the starting point of our application. With different tiles  that contain important information, the user gets a quick overview of his shared flat. The user can navigate through the app with a click on these tiles.
An example would be a tile "Shopping List" that shows how many different products the list contains.

<!--
Das Dashboard ist die Startseite der App. Hier werden in verschiedenen Kacheln die unterschiedlichen Optionen angezeigt. Mit einem Klick auf die Überschrift dieser, kann man sich in diese Kategorie navigieren. Ebenfalls enthält die Kachel wichtige Informationen dazu. Beispielsweise enthält die Kachel der Kategorie "Shoppingliste" die gleichnamige Überschrift und in den Informationen wird angezeigt, wie viele unterschiedliche Produkte diese enthält.
-->

#### 3.1.3 Shopping List
The shopping list gives an overview on products that need to be bought. It's possible to add new products to the list to update or remove old ones and to set the number of items needed. Because not every item should be shared equally between everyone, it's possible to select the members that the item is bought for.

When an item is bought, the one shopping marks the product as bought and all other members of the shared flat can see the checkmarks. This way we avoid that another person buys the item simultaneously. After shoping, the user confirms that he has bought the checked items and they get transferred from the shopping list to the accounting page. The shopping list can be sorted by "customer" to get a better grasp of who needs what.

<!--
Die Shoppingliste dient der besseren Übersicht für alle WG Mitglieder. Es ist möglich ein neues Produkt der Liste hinzuzufügen, als auch eine Menge anzugeben. Da in einer WG nicht immer alle Produkte für jeden gekauft werden, sondern auch einzelne Mitglieder spezielle Produkte wollen, gibt es die Möglichkeit zwischen der kompletten WG und einzelnen als auch mehreren WG Mitgliedern zu wählen. Natürlich können die verschiedenen Positionen auch bearbeitet werden, um zum Beispiel die Menge zu ändern oder auch ein Produkt von der Liste zu löschen.
Wenn nun eine Person der WG einkaufen ist, kann diese die Produkte während des einkaufs abhaken. Alle anderen Mitglieder können dann ebenfalls die gesetzten Haken sehen. Dadurch wird vermieden, dass mehrere Mitglieder das gleiche Produkt kaufen. 
Zum Schluss muss man den Kauf bestätigen und damit werden auch die gekauften Produkte von der Liste gelöscht. Die App merkt sich, wer den Kauf für wen getätigt hat.
Die Liste kann auch nach den verschiedenen "Bestellern" sortiert werden. Damit hat man auf einen Blick die Übersicht darüber, was für die WG gekauft werden muss als auch welche Person ein spezielles Produkt möchte.
-->

#### 3.1.4 Accounting
The accounting page allows the user to bill the costs of each shared flat member. The functionality gets its data from the shopping list. Each user can see what was bought for whom and can set the price of the items he has bought so that the app can sum up the costs and create the bill.

It's also possible to set fixed costs such as rent or electricity costs.

The data is used to either manually or automatically create bills. It's possible to specify a date, e.g. the end of each month, to create the bill automatically. Each member get's a listing on how much he ows whom since the last bill. He can also set a deadline to when he expects the others to pay their dept. The app reminds one if the deadline is near.

A confirmation by both parties is required if the depts are paid and after that the bill gets archived.

<!--
Diese Option ermöglicht es den Nutzern die Kosten abzurechnen. Hierbei bezieht sich diese Funktionalität auf die gespeicherten Daten der Shoppingliste, wer was für wen gekauft hat. Jeder Nutzer kann hier die von ihm gekauften Produkte sehen und muss die Preise für die einzelnen Produkte angeben, damit die App diese Kosten verrechnen kann. 
Zudem können auch die Fixkosten in der Abrechnung angegeben werden, das Löschen ist ebenfalls möglich.
Diese Daten werden dazu verwendet eine Abrechnung automatisch zu erstellen. Es ist möglich diese Verrechnung entweder auf Buttonklick oder auch automatisch, zum Beispiel zu Ende eines Monats, erstellen zu lassen. Jeder Nutzer erhält dann eine Aufstellung, wem aus der WG er wie viel Geld für die vergangene Periode schuldet. Ebenfalls kann eine Frist gesetzt werden, bis wann das Geld an die andere Person übergeben sein muss. Ist diese Frist gesetzt, erinnert die App drei Tage vor Ablauf an das bezahlen. 
Wenn der Nutzer seine Schulden beglichen hat, muss er dies auch bestätigen in dem er "Schulden beglichen" wählt. Der "Geldgeber" erhält darauf eine Benachrichtigung und ist ebenfalls dazu angehalten die Aussage des "Schuldners" zu bestätigen. Nachdem beide Seiten den Schuldenausgleich bestätigt haben, wird der Betrag gelöscht.
-->

#### 3.1.5 Tasks
The tasks page allows users to create tasks. A list of TODOs is shown and each user may add, update or remove a task. This option is perfect for cleaning plans as it is possible to create repeating tasks that occur weekly, monthly or each N days and to set the user that should do the task.

The dashboards shows the user's tasks that are close. An example would be "It's your turn to clean the bathroom.".

<!--
Diese Funktionalität ermöglicht es den Nutzern Aufgaben zu erstellen. Die Mitglieder der WG haben die Möglichkeit sich alle erstellten Aufgaben anzusehen, eine Aufgabe hinzuzufügen, zu bearbeiten oder zu löschen. Diese Option eignet sich besonders gut für den Putzplan. Es gibt auch die Funktion die Aufgaben wöchentlich oder monatlich zu erstellen und der Reihe nach den Mitgliedern zuzuweisen. Auf dem Dashboard würde dann beispielsweise angezeigt werden "Du hast diese Woche den Putzdienst im Badezimmer".
-->

#### 3.1.6 Pinboard
The user can create notes on the pinboard. A note can contain hastags and a date. If a date is given, then all members of the flat can either accept or reject the appointment/event. A user can comment on a note to exchange information. The pinboard informs the user about new or changed information.

<!--
In der Kategorie Pinboard können verschiedenste Themen erstellt werden. Bei jedem erstellten Thema kann ein Hashtag darunter geschrieben oder auch ein Datum erstellt werden. Wenn ein Datum zu einem Thema erstellt wurde, ist es möglich diesen Termin anzunehmen. Will nun jemand auf ein Thema antworten, kann dies in dem Chat gemacht werden. Der Chat dient zum austauschen von Informationen und dem Antworten auf die Beiträge. Es ist somit möglich Nachrichten zu sehen und zu schreiben. Wenn in einer Nachricht der Hashtag eines Themas erwähnt wird, wird automatisch eine Verlinkung zum Thema erstellt. Dadurch wird es einfacher die verschiedenen Chatnachrichten einem Thema zuzuordnen. Das Pinboard kann den Nutzer über neu eingestellte Themen als auch Nachrichten informieren.
-->

#### 3.1.7 Calendar
The calendar page shows each user his own tasks and their deadlines. Also included in the calendar are notes with dates from the pinboard that the user has acceptet. The calendar notifies the user of close events.

<!--
Der Kalender zeigt jeder Person individuell, welche Aufgaben sie wann zu erledigen hat. Ebenfalls in den Kalender integriert sind die Daten aus dem Pinboard. Wenn jemand einen dort eingestellten Termin angenommen hat, wird dieser automatisch in seinem Kalender vermerkt. Der Kalender kann den Nutzer auch über anstehende Termine informieren.
-->

#### 3.1.8 Gamification
Each tasks gets a number of points that the user can define. A user earns these points by finishing a task. A ranking motivates the user to finish more tasks and the members of the shared flat may deicide on a bonus that the user with the most points gets at the end of a month.

<!--
In der Kategorie Aufgaben, kann zu jeder Aufgabe ein bestimmter Punktewert vergeben werden, den die WG frei wählen kann. Diese Punkte können sich die Nutzer durch erledigen dieser Aufgaben erspielen. In einer Übersicht wird dann Rangliste angezeigt. Die WG kann beispielsweise für den Gewinner des Monats eine Belohnung festlegen, was die Motivation, zum Beispiel zum Putzen, steigert.
-->

#### 3.1.9 Settings
This page contains settings for the user's profile and notifications. The user can change his username, profile image and e-mail address or delete his account.

The shared flat's administrator can change the flat's image and name, can create a new access code or delete the WG. If he deletes his account, one of the remaining members becomes the new admin.

<!--
Unter der Rubrik Einstellungen kann ein Nutzer seine privaten Profileinstellungen, als auch seine Benachrichtigungseinstellungen ändern.
Unter den Profileinstellungen ist es Möglich den Benutzernamen, das Profilfoto und die E-Mail-Adresse neben dem ändern auch anzusehen. Ebenfalls kann der Nutzer seinen Account löschen oder die WG, im Falle eines Auszugs, auch verlassen. Die Benachrichtigungseinstellungen lassen sich entweder komplett oder auch einzeln ausschalten.
Der Administrator der App hat zudem auch andere Einstellungsmöglichkeiten, die die WG betreffen. Er kann das WG-Profilbild und die Währung ändern als auch einen neuen Zugangscode für neue WG Mitglieder generieren lassen. Ebenfalls hat er die Übersicht über alle Nutzer, die in seiner WG sind. Er besitzt als einziger die Möglichkeit, die komplette WG zu löschen.
-->

### 3.2 Usability
We plan on designing the user interface as intuitive and self-explanatory as possible to make the user feel as comfortable as possible using the app. Though help documents will be available, it should not be necessary to use them.

#### 3.2.1 No training time needed
Our goal is that a user installs the android application, opens it, get’s a small introduction screen and is able to use all features without any explanation or help.

#### 3.2.1 Familiar feeling
Developing an android application, Google’s Material Design should be used and therefore their guidelines regarding design. This way the user is able to interact in familiar ways with the app without having to get to know new interfaces.

#### 3.2.1 Natural workflow
The workflow should reflect the real life and not work against it. If a specific order of tasks is widespread, then the app should do it in the same way. 

### 3.3 Reliability

#### 3.3.1 Availability and MTTR
The server shall be available at least 95% of the time which is equivalent to ~1 hour downtime a day. Downtime is tolerable during nighttime as it is very unlikely that users upload or change data at night. But the server must be available during "rush hours" when most people go shopping. The time to repair bugs should be as low as possible.

#### 3.3.2 Defect Rate
 - There must be no bugs regarding the accounting section as it is crucial that shared flat members don’t pay each other more or less than is actually correct.
 - Loss of data locally might be acceptable if it is stored on the server.
 - Data synchronization between server and client must be correct. Data races must not occur.

### 3.4 Performance

#### 3.4.1 Response time
Should be as low as possible. Maximum response time is 5 seconds. Average response time should be less than 2 seconds. 

#### 3.4.2 Capacity
The system should be capable to manage thousands of registered users and up to hundred users at the same time on one server. If the capacity exceeds that number, it should be possible to scale and use more servers.

#### 3.4.3 Connection Bandwidth
The size of data to be synchronized between the server and client should be minimal, e.g. renaming an item must not lead to downloading all of existing data on the server.

### 3.5 Supportability

#### 3.5.1 Coding standards
In order to maintain supportability and readability of our code, we will try to adopt the latest clean code standard as far as possible and use the [Google Java Style Guide][GoogleGuidelines] for naming conventions, formatting and programming practices throughout the project.

#### 3.5.2 Maintenance Utilities
In order to test language and platform versions, a continuous integration service is required which runs tests on combinations of platform and language versions.

### 3.6 Design Constraints
We are focused on providing a modern design regarding both code and application.

As "WGPlaner" is an android application, the chosen programming language is Java. The MVC architecture shall be used to differentiate between UI and the actual logic.

The server's operating system must support MySQL and programs compiled using Go. A RESTful API shall be used to communicate between client and server.

Therefore following platforms and language must be supported.
 - Android 4.0 and above
 - Java SE 7 and above
 - Go 1.9 and above

### 3.7 On-line User Documentation and Help System Requirements
Our goal is to make our application as intuitive as possible. Nevertheless a help system will be created that contains FAQs, etc. This might be helpful to users that do not yet use our application but want to know how it works.
At the first start of our application, a short introduction will be shown that shall also be available online.

### 3.8 Purchased Components
A server hosted a https://netcup.de will be rent to run the server application.  
Currently there are no other purchased components.

### 3.9 Interfaces

#### 3.9.1 User Interfaces
There will be the following user interfaces implemented which will solely be available in the android application:
 - *TODO*

#### 3.9.2 Hardware Interfaces
n/a

#### 3.9.3 Software Interfaces
“WGPlaner” should be running on all android devices with Android version 4.0 and above.

#### 3.9.4 Communications Interfaces
The client will connect to the server over `HTTPS` on port `443`. An unencrypted connection over `HTTP` on port `80` shall not be supported.

### 3.10 Licensing Requirements
Under [MIT license][license].

### 3.11 Legal, Copyright, and Other Notices
The “WGPlaner” team will not take any responsibility for incorrect bills or lost data. The “WGPlaner” logo may only be used for the official “WGPlaner” android application.

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
 - Nina Wieland

[UseCaseDiagram]:   use_case_diagram.png
[GoogleGuidelines]: https://google.github.io/styleguide/javaguide.html
[license]:          ../../LICENSE

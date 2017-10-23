# Contributing

## Naming Guidelines

### File Naming

#### UI Related Files

| UI Objects | .xml Files | .java Files (First letter Uppercase then CamelCase) |
| -- | ---------- | --------------------------------------------------- |
| Activities | activity_&lt;Activity Name&gt;.xml | &lt;Activity Name&gt;Activity.java |
| Sections | section_&lt;Section Name&gt;_&lt;Activity Name&gt;.xml | &lt;Section Name&gt;Fragment.java |
| Fragments | fragment_&lt;Fragment Name&gt;_&lt;Activity Name&gt;.xml | &lt;Section Name&gt;&lt;Fragment Name&gt;Fragment.java |
| Navigations | nav_&lt;Navigation Object&gt;_&lt;Activity Name&gt;.xml | --- |
| Items | item_&lt;Parent Name&gt;_&lt;Item Name&gt;.xml | --- |

#### ID's

|     | ID's (All Lowercase - Spaces are underscores)|
| --- | -------------------------------------------- |
| General | &lt;Layout Name&gt;&#95;id&#95;&lt;Function&gt; |

#### Drawable Files

|     | All valid file extensions (.xml, .png, ...) |
| --- | ------------------------------------------- |
| NavigationDrawer icons | ic_nav_&lt;Section Name&gt; |
| Icons general | ic_&lt;Representation Name&gt;_&lt;height&gt; |
| Other Drawables | &lt;Function/Representation Name&gt; |

#### Value Files

| Values | Value Files (.xml) |
| ------ | ------------------ |
| Strings | strings.xml |
| Dimensions | dimens.xml |
| Colors | colors.xml |
| Styles | styles.xml |
| Menus | menu_&lt;menu_name&gt;.xml |

### Package Naming

| Classes | Package |
| ------- | ------- |
| Exceptions | "exception" |
| Utilities | "utils" |
| Data Structures | "structure" |
| Activities | "section.&lt;Activity Name&gt;" |
| Adapters | "section.&lt;Activity Name&gt;.adapter" |
| Fragments | "section.&lt;Activity Name&gt;.fragment" |

## Styling Guidelines

Use Android Design Guidelines:
https://developer.android.com/design/index.html

| Objects | Guidelines |
| ------- | ---------- |
| Section Navigation | Navigation Drawer |
| Presenting ordered Data | Use Tiles (CardView) to display groups of items with equal values after oredering |
|  | Main navigation items of ordered data are displayed as icons in the Tile Header |
| Strings | Use String-resources instead of hardcoded Strings |
| Colors | Use Colors defined in colors.xml |
| Headings | Use predefined styles (Display...) |
| Sub Headers/Item Headers | Use predefined style (AppCompat.Large - text-size can variate) |
| Text | Use predefined style (AppCompat.Body1) |
| CardView | Top and bottom margin: 5dp |
|  | Left and right margin: 10dp |
|  | Max. elevation: 4dp |
|  | height: wrap_content |
|  | width: match_parent |
| Alignement of multiple views | Use gravity and weight instead of width and height |
| Dialog | Usages: to add user defined Items Dialogs, Confirmation of critical changes, to display fatal errors |
|  | margin: 10dp |
|  | Seperate the Dialog Header from the Dialog Body with a Divider |
| In General | Pay attention to the responsiveness of your UI |
|  | Design your UIs consistent with the others |
|  | Design your UI as agile as possible |

## Code Guidelines

We use the following code guidelines:
  * https://google.github.io/styleguide/javaguide.html
  * https://source.android.com/source/code-style

On top of that:
  1. Code
     * Pay attention to the MVC method (Model-View-Controller)
     * Add event lsiteners instead of onClick-layout-attributes
     * Use a Toast to display normal error message; for fatal erros use a Dialog
     * Don't use deprecated methods
     * Throw exceptions instead of returning null (if possible)
     * Avoid using empty strings in Data Structures
     * Use enums or static maps for predefined values
     * Don't use strings as enums
  2. Usage
     * Store data in the DataContainerm class (if necessary, design a DataContainer subclass)
     * Load your data during the SplashScreen Activity (if possible)
     * Avoid intensive Client-Server communications (Reduce the network usage to a minimum)
     * Use official APIs or libraries instead of own or third party implementations (if possible)
  3. Style
     * Avoid designing methods larger than 50 lines
     * Write Unit Tests for your classes. Your code coverage should be > 80%

### Artistic Style
We use [Artistic Style](http://astyle.sourceforge.net/) to format the source code. Before committing files, please run:

**Linux** (with pre-compiled version)
```bash
cd wg_planer
./astyle/astyle_linux_x86-64 --options=.astylerc *.java
```

**Windows** (with pre-compiled version)
```
.\astyle\AStyle.exe --options=.\.astylerc *.java 
```

You can find pre-compiled version in the folder `astyle`.

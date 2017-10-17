# WGPlaner

[![Build Status](https://teamcity.ameyering.de/app/rest/builds/buildType:(id:WgPlaner_BuildClient)/statusIcon)](https://teamcity.ameyering.de/viewType.html?buildTypeId=WgPlaner_BuildClient&guest=1)

## Contributing
### Code Style
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

# LibGdx In-Game Console
This is a LibGdx library that allows a developer to add a console (similar to how it is featured in Source games) to their game.

## Current Goals
* Auto-complete feature for quicker, easier invoking of methods.
* Utilization of up and down arrow keys for quick re-execution of methods

## Adding to Project  
### Gradle
Currently there is no Maven repository for this library, so you will have to clone it and export it to a .jar file in Eclipse (make sure to check the box that says "include source"). I recommend calling the .jar `libgdx-inGameConsole` so that you can just copy and paste any code below, but you can call it differently if you like.

Create a directory in your `core` project called `libs` and save the .jar you have created there. Then in your project's main `build.gradle` file paste the following into the `:core dependencies` section:

`compile files("libs/libgdx-inGameConsole.jar")`

Then simply right-click the project and choose `Gradle->Refresh All`.

### Maven
First, clone this project to your computer and [add it to Eclipse](http://www.eclipse.org/forums/index.php/t/226301/). Then right-click the project (this is assuming you have [M2E](http://eclipse.org/m2e/) installed) and choose `Configure->Convert to Maven Project`. Do the same for your project, if you haven't already. Then right-click on your project and choose `Maven->Add Dependency` and search for `strongjoshua`. This project should appear. If it does not, ensure your cloned repository is up-to-date.

### Eclipse
First, clone this project to your computer and [add it to Eclipse](http://www.eclipse.org/forums/index.php/t/226301/). Then simply click on your project, and choose `Build Path->Configure Build Path`. Then go to `Projects->Add` and add the cloned project.

License
=======
Copyright 2015

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.

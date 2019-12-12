**Running With Gradle**

This section of the project requires Gradle.
Make sure you've installed it beforehand.

I know very little about Gradle,
but I think Gradle holds the project's libraries online.
You need to be connect to the internet to run program's using it.

To install Gradle, [look here](https://gradle.org/install/). 

I have not run this on any operating systems other than Ubuntu 18.04 and
Lubuntu 19.10. These are both versions of Debian-Linux.

According to [the JPro website](https://www.jpro.one/),
the app should run on almost any browser without modification. 
I have run it on Firefox and Chromium (open-source version of Chrome) without issue.

To run:
    
1. Open your terminal. 

2. Navigate to the project folder.

3. enter `./gradlew jproRun` or just `gradle jproRun`

Using "./gradlew jproRun" is recommended, but I can't run it
with that command if the project directory is in my flash drive.
I suspect this is a problem with linux file-permissions, but I'm not sure.

Using `gradle jproRun` hasn't caused any issues that I'm aware of.

**Viewing in an IDE**

To open this in IntelliJ-IDEA, just click "import project" in the main menu.
Obviously, choose to import it as a Gradle project.

I believe it *should* be able to run using the IDE, but I have had no luck getting that to work.

**Current Issues/To-Do**

General:

1. The graphical elements don't adjust according to the dimensions of the browser window.

2. It's difficult to add much until we know more about what our software's functional requirements are.

Login Screen:

1. The password field's caret doesn't keep up with the characters being typed.


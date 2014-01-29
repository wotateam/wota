#wota - War of the Ants

##Download release
https://github.com/wotateam/wota/archive/release_0.2.zip

##Installation guide

1.  extract .zip into your eclipse workspace

2.  rename folder into wota

3.  in eclipse: File -> New -> Java project.  
    project name = wota  
    click on 'finish' (eclipse should denote that it will import the project settings)

4.  Run as java application. The main is located in src/wota/Wota.java

5.  Write your own ai! Create a folder with the name of your ai in src/wota/ai/
6.  Copy src/wota/ai/template/TemplateAI.java, HillAI.java to this folder. 
	
	You must *not* rename QueenAI, you may rename TemplateAI and probably want to do so.

7.  Write your ai in these files. Change the package name inside the files to wota.ai.FOLDER_NAME
8.  Add your ais to the game by editing settings.txt
9.  Further information can be found inside the template classes as well as in Tutorial.txt. 
	Keyboard hotkeys are listed in https://github.com/wotateam/wota/wiki/Keyboard-Hotkeys
10. Have fun! If any questions/comments/suggestions come up, don't hesitate and contact us!

## Information for Developers
we are using lwjgl for our graphics:
http://lwjgl.org/

It is BSD-licensed, see LWJGL_LICENSE.txt (taken from the project website).

to use 'Issues & Milestones' (GitHub Bugtracker) in Eclipse (using Mylyn): 
In Eclipse: Help -> Install New Software -> work with: http://download.eclipse.org/egit/github/updates -> install
Then open view 'Task List' -> Add Repository

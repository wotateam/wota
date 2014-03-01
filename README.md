#wota - War of the Ants
Wota, the War of the Ants, is a multiplayer game played by writing an artificial intelligence (AI) controlling an ant colony. That said, watching games while developing your AI or together with other AI authors is a big part of the fun! 

##Download release
https://github.com/wotateam/wota/releases/tag/v0.3

or press the right-hand side button on the repository's front page to download HEAD if you are feeling adventurous.

##Installation guide

1.  Extract the .zip into your eclipse workspace.

2.  Rename the folder into "wota".

3.  In eclipse: File -> New -> Java project.  
    Set the project name to "wota".  
    Click on 'finish' (eclipse should say that it will import the project settings)

4.  Run as a Java application. The main method is located in src/wota/Wota.java

5.  To start writing your AI, create a folder with the name of your AI in src/wota/ai/ .

6.  Copy src/wota/ai/template/TemplateAI.java, HillAI.java to this folder. 

	You must *not* rename HillAI, you may rename TemplateAI and probably want to do so.

7.  Write your AI in these files. Change the package name inside the files to wota.ai.FOLDER_NAME

8.  Add your AIs to the game by editing settings.txt. The AIs are referenced by their package name.

9.  Some information can be found inside the template classes you copied in step 6.

##Further Information
A brief tutorial as well as the rules of the game can be found in the wiki.
	
https://github.com/wotateam/wota/wiki/
	
Have fun! If you have any questions, comments, suggestions or want to share your ai, don't hesitate to contact us!

wotateam@gmx.de

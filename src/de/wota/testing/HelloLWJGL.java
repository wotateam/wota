package de.wota.testing;
// taken from http://lwjgl.org/wiki/index.php?title=LWJGL_Basics_1_%28The_Display%29
import org.lwjgl.LWJGLException;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;

public class HelloLWJGL {
	public void start() {
		try {
			Display.setDisplayMode(new DisplayMode(800,600));
			Display.create();
		} catch (LWJGLException e) {
			e.printStackTrace();
			System.exit(0);
		}
		
		// init OpenGL here
		
		while (!Display.isCloseRequested()) {
			
			// render OpenGL here
			
			Display.update();
		}
		
		Display.destroy();
	}
	
	public static void main(String[] argv) {
		HelloLWJGL hello = new HelloLWJGL();
		hello.start();
	}
}

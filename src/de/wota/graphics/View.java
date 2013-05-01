package de.wota.graphics;

import java.awt.BorderLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.media.opengl.GL;
import javax.media.opengl.GL2;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLCapabilities;
import javax.media.opengl.GLEventListener;
import javax.media.opengl.GLProfile;
import javax.media.opengl.awt.GLCanvas;
import javax.media.opengl.glu.GLU;
import javax.swing.JFrame;

public class View extends GLCanvas {
	// hardcoded number of players = 5
	final double[] reds = {1.0, 0.0, 0.0, 1.0, 0.0};
	final double[] greens = {0.0, 1.0, 0.0, 1.0, 0.0};
	final double[] blues = {0.0, 0.0, 1.0, 1.0, 0.0};
	
	public View(GLCapabilities capabilities) {
		super(capabilities);

		addGLEventListener(new GLEventListener() {

			@Override
			public void reshape(GLAutoDrawable glautodrawable, int x, int y,
					int width, int height) {
				setup(glautodrawable.getGL().getGL2(), width, height);
			}

			@Override
			public void init(GLAutoDrawable glautodrawable) {
			}

			@Override
			public void dispose(GLAutoDrawable glautodrawable) {
			}

			@Override
			public void display(GLAutoDrawable glautodrawable) {
				render(glautodrawable.getGL().getGL2(),
						glautodrawable.getWidth(), glautodrawable.getHeight());
			}
		});
	}
	
	private void setup(GL2 gl2, int width, int height) {
		gl2.glMatrixMode(GL2.GL_PROJECTION);
		gl2.glLoadIdentity();

		// coordinate system origin at lower left with width and height same as
		// the window
		GLU glu = new GLU();
		glu.gluOrtho2D(0.0f, width, 0.0f, height);

		gl2.glMatrixMode(GL2.GL_MODELVIEW);
		gl2.glLoadIdentity();

		gl2.glViewport(0, 0, width, height);
	}

	private void render(GL2 gl2, int width, int height) {
		gl2.glClear(GL.GL_COLOR_BUFFER_BIT);

		// draw a triangle filling the window
		gl2.glLoadIdentity();
		
		gl2.glScaled(10,10,10);
		renderCircle(gl2);
		
		gl2.glEnd();
	}

	final int numberOfCircleCorners = 24;
	
	private void renderCircle(GL2 gl) {
		gl.glBegin(GL.GL_TRIANGLE_FAN);
		gl.glVertex2f(0, 0);
		for (int i = 0; i <= numberOfCircleCorners; i++) {
			final double angle = 2 * Math.PI * i / numberOfCircleCorners; 
			gl.glVertex2d(Math.cos(angle), Math.sin(angle));
		}
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		GLProfile glprofile = GLProfile.getDefault();
		GLCapabilities glcapabilities = new GLCapabilities(glprofile);
		GLCanvas glcanvas = new View(glcapabilities);

		final JFrame jframe = new JFrame("One Triangle Swing GLCanvas");
		jframe.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent windowevent) {
				jframe.dispose();
				System.exit(0);
			}
		});

		jframe.getContentPane().add(glcanvas, BorderLayout.CENTER);
		jframe.setSize(640, 480);
		jframe.setVisible(true);
	}

}

package de.wota.graphics;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Point;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.geom.Point2D;
import java.nio.FloatBuffer;

import javax.media.opengl.GL;
import javax.media.opengl.GL2;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLCapabilities;
import javax.media.opengl.GLEventListener;
import javax.media.opengl.GLProfile;
import javax.media.opengl.awt.GLCanvas;
import javax.media.opengl.glu.GLU;
import javax.swing.JFrame;

import de.wota.GameWorldParameters;
import de.wota.Player;
import de.wota.Vector;
import de.wota.gameobjects.Ant;
import de.wota.gameobjects.AntObject;
import de.wota.gameobjects.GameWorld;

/**
 * Renders everything.
 * 
 * @author Daniel
 */
public class View extends GLCanvas {
	private GL2 gl;

	// hardcoded number of players = 8
	private static final Color[] colors = { Color.RED, Color.BLUE, Color.GREEN,
			Color.CYAN, Color.PINK, Color.MAGENTA, Color.ORANGE, Color.YELLOW };

	private GameWorld world;

	public View(GLCapabilities capabilities, GameWorld world) {
		super(capabilities);
		this.world = world;

		addGLEventListener(new GLEventListener() {

			@Override
			public void reshape(GLAutoDrawable glautodrawable, int x, int y,
					int width, int height) {
				gl = glautodrawable.getGL().getGL2();
				setup(width, height);
			}

			@Override
			public void init(GLAutoDrawable glautodrawable) {
			}

			@Override
			public void dispose(GLAutoDrawable glautodrawable) {
			}

			@Override
			public void display(GLAutoDrawable glautodrawable) {
				gl = glautodrawable.getGL().getGL2();
				render(glautodrawable.getWidth(), glautodrawable.getHeight());
			}
		});
	}

	private void setup(int width, int height) {
		gl.glMatrixMode(GL2.GL_PROJECTION);
		gl.glLoadIdentity();

		// coordinate system origin at lower left with width and height same as
		// the window
		gl.glOrtho(0, width, 0, height, -1, 1);
		gl.glMatrixMode(GL2.GL_MODELVIEW);
		gl.glLoadIdentity();

		gl.glViewport(0, 0, width, height);
	}

	private static final int ANT_RADIUS = 2;

	private void render(int width, int height) {
		gl.glClear(GL.GL_COLOR_BUFFER_BIT);

		gl.glLoadIdentity();

		for (Player player : world.players) {
			Color color = colors[player.getId()];
			gl.glColor3fv(FloatBuffer.wrap(color.getColorComponents(null)));
			
			for (AntObject antObject : player.antObjects) {
				renderCircle(antObject.getPosition(), ANT_RADIUS);
			}

			renderCircle(player.hillObject.getPosition(), GameWorldParameters.HILL_RADIUS);
		}

	}

	private void translate(Vector p) {
		gl.glTranslated(p.x, p.y, 0);
	}

	private void renderCircle(Vector p, double radius) {
		gl.glPushMatrix();
		translate(p);
		gl.glScaled(radius, radius, radius);
		renderUnitCircle();
		gl.glPopMatrix();
	}

	final int numberOfCircleCorners = 24;

	private void renderUnitCircle() {
		gl.glBegin(GL.GL_TRIANGLE_FAN);
		gl.glVertex2f(0, 0);
		for (int i = 0; i <= numberOfCircleCorners; i++) {
			final double angle = 2 * Math.PI * i / numberOfCircleCorners;
			gl.glVertex2d(Math.cos(angle), Math.sin(angle));
		}
		gl.glEnd();
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		GLProfile glprofile = GLProfile.getDefault();
		GLCapabilities glcapabilities = new GLCapabilities(glprofile);
		GLCanvas glcanvas = null;
		try { 
			glcanvas = new View(glcapabilities, GameWorld.testWorld());
		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

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

package de.wota.graphics;

import org.lwjgl.LWJGLException;
import org.lwjgl.opengl.*;


import static org.lwjgl.opengl.GL11.*;
import java.awt.Color;
import de.wota.gameobjects.AntObject;
import de.wota.gameobjects.GameWorld;
import de.wota.gameobjects.GameWorldParameters;
import de.wota.gameobjects.SugarObject;
import de.wota.testing.TestWorld;
import de.wota.utility.Vector;

/**
 * Renders everything.
 * 
 * Preliminary: Use this to test.
 * 
 * @author Daniel
 */
public class View {
	// hardcoded maximum number of players = 8
	private static final Color[] colors = { Color.RED, Color.BLUE, Color.GREEN,
			Color.CYAN, Color.PINK, Color.MAGENTA, Color.ORANGE, Color.YELLOW };

	private GameWorld world;

	private int width;
	private int height;

	public View(GameWorld world, int width, int height) {
		this.world = world;
		this.width = width;
		this.height = height;
	}

	public void setup() {
		glMatrixMode(GL_PROJECTION);
		glLoadIdentity();

		// coordinate system origin at lower left with width and height same as
		// the window
		glOrtho(0, GameWorldParameters.SIZE_X, 0, GameWorldParameters.SIZE_Y, -1, 1);
		glMatrixMode(GL_MODELVIEW);
		glLoadIdentity();

		glViewport(0, 0, width, height);
	}

	private static final int ANT_RADIUS = 2;

	public void render() {
		glClear(GL_COLOR_BUFFER_BIT);

		glLoadIdentity();

		for (GameWorld.Player player : world.getPlayers()) {
			Color color = colors[player.getId()];
			float[] colorComponents = color.getColorComponents(null);
			glColor3f(colorComponents[0], colorComponents[1], colorComponents[2]);
			
			// Ants
			for (AntObject antObject : player.antObjects) {
				renderCircle(antObject.getPosition(), ANT_RADIUS);
			}			

			// Hill
			renderCircle(player.hillObject.getPosition(), GameWorldParameters.HILL_RADIUS);
		}
		// Sugar Sources
		glColor3f(1.f, 1.f, 1.f);
		for (SugarObject sugarObject : world.getSugarObjects()) {
			renderCircle(sugarObject.getPosition(), 
					GameWorldParameters.SUGAR_RADIUS * sugarObject.getAmount() / GameWorldParameters.INITIAL_SUGAR);
		}

	}

	private void translate(Vector p) {
		glTranslated(p.x, p.y, 0);
	}

	private void renderCircle(Vector p, double radius) {
		glPushMatrix();
		translate(p);
		glScaled(radius, radius, radius);
		renderUnitCircle();
		glPopMatrix();
	}

	final int numberOfCircleCorners = 24;

	private void renderUnitCircle() {
		glBegin(GL_TRIANGLE_FAN);
		glVertex2f(0, 0);
		for (int i = 0; i <= numberOfCircleCorners; i++) {
			final double angle = 2 * Math.PI * i / numberOfCircleCorners;
			glVertex2d(Math.cos(angle), Math.sin(angle));
		}
		glEnd();
	}

	/**
	 * @param args
	 * @throws IllegalAccessException 
	 * @throws InstantiationException 
	 */
	public static void main(String[] args) throws InstantiationException, IllegalAccessException {
		GameWorld gameWorld = TestWorld.testWorld();
		final int width = 700;
		final int height = 700;
		View view = new View(gameWorld, width, height);
		try {
			Display.setDisplayMode(new DisplayMode(width, height));
			Display.create();
		} catch (LWJGLException e) {
			e.printStackTrace();
			System.exit(0);
		}

		view.setup();
		
		while (!Display.isCloseRequested()) {
			gameWorld.tick();
			view.render();
			Display.update();
		}
		
		Display.destroy();
	}
}

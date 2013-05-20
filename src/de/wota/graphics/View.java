package de.wota.graphics;

import org.lwjgl.LWJGLException;
import org.lwjgl.opengl.*;


import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL14.*;
import java.awt.Color;

import de.wota.gameobjects.AntObject;
import de.wota.gameobjects.GameWorld;
import de.wota.gameobjects.Parameters;
import de.wota.gameobjects.SugarObject;
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
		glEnable(GL_BLEND);
		glBlendFunc(GL_SRC_ALPHA, GL_DST_ALPHA);
		glBlendEquation(GL_FUNC_ADD);
		
		glMatrixMode(GL_PROJECTION);
		glLoadIdentity();

		// coordinate system origin at lower left with width and height same as
		// the window
		glOrtho(0, Parameters.SIZE_X, 0, Parameters.SIZE_Y, -1, 1);
		glMatrixMode(GL_MODELVIEW);
		glLoadIdentity();

		glViewport(0, 0, width, height);
	}

	private static final int ANT_RADIUS = 2;

	private static final int SAMPLES = 2; // the scene is actually rendered SAMPLES^2 times 
	
	public void render() {
		glClear(GL_COLOR_BUFFER_BIT);

		glLoadIdentity();
		
		for (int i = 0; i < SAMPLES; i++) {
			glPushMatrix();
			for (int j = 0; j < SAMPLES; j++) {
				renderImpl();
				glTranslated(1.0/SAMPLES, 0, 0);
			}
			glPopMatrix();
			glTranslated(0, 1.0/SAMPLES, 0);
		}
	}

	private void renderImpl() {
		for (GameWorld.Player player : world.getPlayers()) {
			Color color = colors[player.getId()];
			float[] colorComponents = color.getColorComponents(null);
			glColor4f(colorComponents[0], colorComponents[1], colorComponents[2],1.0f/(SAMPLES*SAMPLES));
			
			// Ants
			for (AntObject antObject : player.antObjects) {
				renderCircle(antObject.getPosition(), ANT_RADIUS, ANT_CIRCLE_CORNERS);
			}			

			// Hill
			renderCircle(player.hillObject.getPosition(), Parameters.HILL_RADIUS, HILL_CIRCLE_CORNERS);
		}
		// Sugar Sources
		glColor4f(1.f, 1.f, 1.f,1.0f/(SAMPLES*SAMPLES));
		for (SugarObject sugarObject : world.getSugarObjects()) {
			renderCircle(sugarObject.getPosition(), sugarObject.getRadius(), SUGAR_CIRCLE_CORNERS);
		}
	}

	private static void translate(Vector p) {
		glTranslated(p.x, p.y, 0);
	}

	private void renderCircle(Vector p, double radius, int numberOfCircleCorners) {
		glPushMatrix();
		translate(p);
		glScaled(radius, radius, radius);
		renderUnitCircle(numberOfCircleCorners);
		glPopMatrix();
	}

	private static final int HILL_CIRCLE_CORNERS = 50;
	private static final int SUGAR_CIRCLE_CORNERS = 24;
	private static final int ANT_CIRCLE_CORNERS = 6;

	private void renderUnitCircle(int numberOfCircleCorners) {
		glBegin(GL_TRIANGLE_FAN);
		glVertex2f(0, 0);
		for (int i = 0; i <= numberOfCircleCorners; i++) {
			final double angle = 2 * Math.PI * i / numberOfCircleCorners;
			glVertex2d(Math.cos(angle), Math.sin(angle));
		}
		glEnd();
	}
}

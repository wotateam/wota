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
 * http://en.wikibooks.org/wiki/OpenGL_Programming/GLStart/Tut3
 * 
 * @author Daniel, Pascal
 */
public class View {

	private static final int HILL_CIRCLE_CORNERS = 50;
	private static final int SUGAR_CIRCLE_CORNERS = 24;
	private static final int ANT_CIRCLE_CORNERS = 6;
	private static final int SIGHT_RANGE_CORNERS = 14;
	
	private static final int ANT_RADIUS = 5;
	private static final double CARRIED_SUGAR_RADIUS = 2;
	public boolean drawSightRange = false;
	private static final boolean DRAW_ATTACK = true;
	private static final int SAMPLES = 2; // the scene is actually rendered SAMPLES^2 times 
	
	private static final float HILL_ALPHA = 0.65f;
	
	// hardcoded maximum number of players = 8
	private static final Color[] colors = { Color.RED, Color.BLUE, Color.GREEN,
			Color.CYAN, Color.PINK, Color.MAGENTA, Color.ORANGE, Color.YELLOW };

	private GameWorld world;

	private int width;
	private int height;

	private Parameters parameters;
	
	public View(GameWorld world, int width, int height, Parameters parameters) {
		this.world = world;
		this.width = width;
		this.height = height;
		this.parameters = parameters;
	}

	public void setup() {
		glEnable(GL_BLEND);
		glBlendFunc(GL_SRC_ALPHA, GL_DST_ALPHA);
		glBlendEquation(GL_FUNC_ADD);
		glClearColor(0, 0, 0, 1);
		
		glMatrixMode(GL_PROJECTION);
		glLoadIdentity();

		// coordinate system origin at lower left with width and height same as
		// the window
		glOrtho(0, parameters.SIZE_X, 0, parameters.SIZE_Y, -1, 1);
		glMatrixMode(GL_MODELVIEW);
		glLoadIdentity();

		glViewport(0, 0, width, height);
	}
	
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
			
			// Ants
			for (AntObject antObject : player.antObjects) {
				glColor4f(colorComponents[0], colorComponents[1], colorComponents[2],1.0f/(SAMPLES*SAMPLES));
				fillCircle(antObject.getPosition(), ANT_RADIUS, ANT_CIRCLE_CORNERS);
				if (antObject.isCarrying()) {
					glColor4f(1, 1, 1, 1.0f/(SAMPLES*SAMPLES));
					fillCircle(antObject.getPosition(), CARRIED_SUGAR_RADIUS, ANT_CIRCLE_CORNERS);
				}
				if (drawSightRange) {
					final float sightRangeAlpha = 0.3f;
					glColor4f(colorComponents[0], colorComponents[1], colorComponents[2],sightRangeAlpha/(SAMPLES*SAMPLES));
					drawCircle(antObject.getPosition(), antObject.getCaste().SIGHT_RANGE, SIGHT_RANGE_CORNERS);
				}
				if (DRAW_ATTACK) {
					AntObject attackTarget = antObject.getAttackTarget();
					if (attackTarget != null) {
						final float attackAlpha = 1.0f;
						glColor4f(colorComponents[0], colorComponents[1], colorComponents[2], attackAlpha/(SAMPLES*SAMPLES));
						drawLine(antObject.getPosition(), attackTarget.getPosition());
					}
				}
			}			

			// Hill
			glColor4f(colorComponents[0], colorComponents[1], colorComponents[2],HILL_ALPHA * 1.0f/(SAMPLES*SAMPLES));
			fillCircle(player.hillObject.getPosition(), parameters.HILL_RADIUS, HILL_CIRCLE_CORNERS);
		}
		// Sugar Sources
		glColor4f(1.f, 1.f, 1.f,1.0f/(SAMPLES*SAMPLES));
		for (SugarObject sugarObject : world.getSugarObjects()) {
			fillCircle(sugarObject.getPosition(), sugarObject.getRadius(), SUGAR_CIRCLE_CORNERS);
		}
	}

	private static void translate(Vector p) {
		glTranslated(p.x, p.y, 0);
	}

	private static void drawLine(Vector start, Vector end) {
		glBegin(GL_LINES);
		glVertex2d(start.x, start.y);
		glVertex2d(end.x, end.y);
		glEnd();
	}
	
	private static void drawCircle(Vector p, double radius, int numberOfCircleCorners) {
		glPushMatrix();
		translate(p);
		glScaled(radius, radius, radius);
		drawUnitCircle(numberOfCircleCorners);
		glPopMatrix();
	}
	
	/**
	 * @param numberOfCircleCorners
	 */
	private static void drawUnitCircle(int numberOfCircleCorners) {
		glBegin(GL_LINE_LOOP);
		for (int i = 0; i < numberOfCircleCorners; i++) {
			final double angle = 2 * Math.PI * i / numberOfCircleCorners;
			glVertex2d(Math.cos(angle), Math.sin(angle));
		}
		glEnd();
	}

	private static void fillCircle(Vector p, double radius, int numberOfCircleCorners) {
		glPushMatrix();
		translate(p);
		glScaled(radius, radius, radius);
		fillUnitCircle(numberOfCircleCorners);
		glPopMatrix();
	}

	private static void fillUnitCircle(int numberOfCircleCorners) {
		glBegin(GL_TRIANGLE_FAN);
		glVertex2f(0, 0);
		for (int i = 0; i <= numberOfCircleCorners; i++) {
			final double angle = 2 * Math.PI * i / numberOfCircleCorners;
			glVertex2d(Math.cos(angle), Math.sin(angle));
		}
		glEnd();
	}
}

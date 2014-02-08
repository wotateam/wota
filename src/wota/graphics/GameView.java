package wota.graphics;

import org.lwjgl.LWJGLException;
import org.lwjgl.opengl.*;

import wota.gameobjects.AntObject;
import wota.gameobjects.GameWorld;
import wota.gameobjects.Parameters;
import wota.gameobjects.SugarObject;
import wota.utility.Vector;


import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL14.*;
import java.awt.Color;

import javax.swing.JFrame;


/**
 * Renders everything.
 * 
 * http://en.wikibooks.org/wiki/OpenGL_Programming/GLStart/Tut3
 */
public class GameView {

	private static final int HILL_CIRCLE_CORNERS = 50;
	private static final int SUGAR_CIRCLE_CORNERS = 24;
	private static final int ANT_CIRCLE_CORNERS = 6;
	private static final int SIGHT_RANGE_CORNERS = 20;
	private static final int MESSAGE_CORNERS = 8;
	
	private static final int ANT_RADIUS = 5;
	private static final int MESSAGE_RADIUS = 10;
	private static final double CARRIED_SUGAR_RADIUS = 2;
	public boolean drawSightRange = false;
	public boolean drawMessages = false;
	private static final boolean DRAW_ATTACK = true;
	private static final int SAMPLES = 1; // the scene is actually rendered SAMPLES^2 times 
	
	private static final float HILL_COLOR_PERCENTAGE = 0.65f;
	private static final float MESSAGE_ALPHA = 1.0f;
	
	// hardcoded maximum number of players = 8
	public static final Color[] playerColors = { Color.RED, Color.BLUE, Color.GREEN,
			Color.CYAN, Color.PINK, Color.MAGENTA, Color.ORANGE, Color.YELLOW };

	private GameWorld gameWorld;

	private int width;
	private int height;

	private Parameters parameters;
	
	public GameView(GameWorld gameWorld, int width, int height) {
		this.gameWorld = gameWorld;
		this.width = width;
		this.height = height;
		parameters = gameWorld.parameters;
	}

	public void setup() {
		glEnable(GL_DEPTH_TEST);
		glEnable(GL_BLEND);
		glBlendEquation(GL_FUNC_ADD);

		glClearColor(0, 0, 0, 0);
		
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
		glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

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
		// No blending for background objects.
		glBlendFuncSeparate(GL_ONE, GL_ONE, GL_ONE, GL_ONE);
		
		// Sugar Sources
		glPushMatrix();
		glTranslated(0, 0, 0.5);
		setColor(1.f, 1.f, 1.f, 0.0f);
		for (SugarObject sugarObject : gameWorld.getSugarObjects()) {
			fillCircle(sugarObject.getPosition(), sugarObject.getRadius(), SUGAR_CIRCLE_CORNERS);
		}
		glPopMatrix();
		
		// Hills
		for (GameWorld.Player player : gameWorld.getPlayers()) {
			Color color = playerColors[player.id()];
			float[] colorComponents = color.getColorComponents(null);
			
			setColor(HILL_COLOR_PERCENTAGE*colorComponents[0],
					 HILL_COLOR_PERCENTAGE*colorComponents[1],
					 HILL_COLOR_PERCENTAGE*colorComponents[2], 
					 0);
			fillCircle(player.hillObject.getPosition(), parameters.HILL_RADIUS, HILL_CIRCLE_CORNERS);
		}

		// Foreground objects are blended
		glBlendFuncSeparate(GL_SRC_ALPHA, GL_DST_ALPHA, GL_ONE, GL_ONE);

		for (GameWorld.Player player : gameWorld.getPlayers()) {
			Color color = playerColors[player.id()];
			float[] colorComponents = color.getColorComponents(null);
			
			// Ants			
			for (AntObject antObject : player.antObjects) {
				setColor(colorComponents[0], colorComponents[1], colorComponents[2], 1.0f);
				fillCircle(antObject.getPosition(), ANT_RADIUS, ANT_CIRCLE_CORNERS);
				if (antObject.isCarrying()) {
					setColor(1.0f, 1.0f, 1.0f, 1.0f);
					fillCircle(antObject.getPosition(), CARRIED_SUGAR_RADIUS, ANT_CIRCLE_CORNERS);
				}
				if (drawSightRange) {
					final float sightRangeAlpha = 0.3f;
					setColor(colorComponents[0], colorComponents[1], colorComponents[2],sightRangeAlpha);
					drawCircle(antObject.getPosition(), antObject.caste.SIGHT_RANGE, SIGHT_RANGE_CORNERS);
				}
				if (drawMessages && antObject.getAction() != null && antObject.getAction().antMessageObject != null) {
					setColor(colorComponents[0], colorComponents[1], colorComponents[2], MESSAGE_ALPHA);
					drawCircle(antObject.getPosition(), MESSAGE_RADIUS, MESSAGE_CORNERS);
				}				
				if (DRAW_ATTACK) {
					AntObject attackTarget = antObject.getAttackTarget();
					final float attackLineWidth = 3.0f;
					glLineWidth(attackLineWidth);
					if (attackTarget != null) {
						final float attackAlpha = 1.0f;
						setColor(colorComponents[0], colorComponents[1], colorComponents[2], attackAlpha);
						drawLineOnTorus(antObject.getPosition(), attackTarget.getPosition());
					}
				}
			}
			// Hill messages
			if (drawMessages && player.hillObject.getMessage() != null) {
				setColor(colorComponents[0], colorComponents[1], colorComponents[2], MESSAGE_ALPHA);
				drawCircle(player.hillObject.getPosition(), MESSAGE_RADIUS, MESSAGE_CORNERS);
			}
		}
	}

	/** 
	 * Sets the Color. Sampling is considered inside this method. You don't need to care.
	 * @param red between 0 and 1
	 * @param green between 0 and 1
	 * @param blue between 0 and 1
	 * @param alpha between 0 and 1
	 */
	private static void setColor(float red, float green, float blue, float alpha) {
		glColor4f(red, green, blue, alpha/(SAMPLES*SAMPLES));
	}
	
	private static void translate(Vector p) {
		glTranslated(p.x, p.y, 0);
	}
	
	/** 
	 * draws a straight line between start and end.
	 * Caution! You might want to use drawLineOnTorus()
	 */
	private static void drawLine(Vector start, Vector end) {
		glBegin(GL_LINES);
		glVertex2d(start.x, start.y);
		glVertex2d(end.x, end.y);
		glEnd();
	}
	
	/**
	 * draws the shortest path between start and end on the torus.
	 */
	private void drawLineOnTorus(Vector start, Vector end) {
		if (Math.abs(start.x - end.x) > parameters.SIZE_X/2.) {
			if (start.x > end.x) {
				drawLineOnTorus(end, start);
			}
			else {
			drawLineOnTorus(new Vector(start.x + parameters.SIZE_X, start.y), end);
			drawLineOnTorus(start, new Vector(end.x - parameters.SIZE_X, end.y));
			}
		}
		else if (Math.abs(start.y - end.y) > parameters.SIZE_Y/2.) {
			if (start.y > end.y) {
				drawLineOnTorus(end, start);
			}
			else {
			drawLineOnTorus(new Vector(start.x, start.y + parameters.SIZE_Y), end);
			drawLineOnTorus(start, new Vector(end.x, end.y - parameters.SIZE_Y));
			}
		}
		else {
			drawLine(start, end);
		}
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

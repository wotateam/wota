package wota.testing;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import wota.ai.loadingpleasewait.AntAILPW;
import wota.gameobjects.Ant;
import wota.gameobjects.GameObject;
import wota.gameobjects.Parameters;
import wota.gameobjects.Snapshot;
import wota.utility.Vector;

/**
 * Tests AILoading
 */
public class TestingPleaseWait {

	private TestAntAILPW testAnt;
	private TestSuite test;
	private Parameters testParameters;
	private double startAngle;

	/**
	 * Test cases
	 */
	public enum TestSuite {

		/**
		 * Ant position is 200,200 and home position is 100,100
		 */
		AVOID_TEST(new Vector(200, 200), new Vector(100, 100)),

		/**
		 * Uses Math.random() to give vectors pseudorandom x and y values below 1000
		 */
		RANDOM(new Vector(Math.random() * 1000, Math.random() * 1000), new Vector(Math.random() * 1000, Math.random() * 1000));

		private final Vector antPosition, homePosition;

		/**
		 * @param antPosition
		 *            Vector representing the starting position of the ant being tested
		 * @param homePosition
		 *            Vectore representing the position of the tested ant's hill
		 */
		private TestSuite(Vector antPosition, Vector homePosition) {
			this.antPosition = antPosition;
			this.homePosition = homePosition;
		}

		/**
		 * @return the testAntPosition
		 */
		public Vector getAntPosition() {
			return antPosition;
		}

		/**
		 * @return the testHomePosition
		 */
		public Vector getHomePosition() {
			return homePosition;
		}
	}

	@BeforeClass
	public static void setUpBeforeClass() {
		System.out.println("TestingPleaseWait.setUpBeforeClass()");
	}

	@AfterClass
	public static void tearDownAfterClass() {
		System.out.println("TestingPleaseWait.tearDownAfterClass()");
	}

	@Before
	public void setUp() {
		System.out.println("TestingPleaseWait.setUp()");
		//read parameters file
		Properties propertiesForParameters = new Properties();
		try {
			propertiesForParameters.load(new FileReader("parameters.txt"));
		} catch (FileNotFoundException e) {
			System.out.println("parameters file not found");
			e.printStackTrace();
			System.exit(-1);
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(-1);
		}
		testParameters = new Parameters(propertiesForParameters, 2);
		test = TestSuite.AVOID_TEST;
		testAnt = new TestAntAILPW();
		startAngle = test.getAntPosition().angle();
	}

	@After
	public void tearDown() {
		System.out.println("TestingPleaseWait.tearDown()");
	}

	@Test
	public void testAvoidHome() {
		System.out.println("TestingPleaseWait.testClosestToEnemy()");
		double startDistance = testAnt.vectorToHome().length();
		System.out.println(test.getAntPosition() + " " + startAngle);
		System.out.println(startDistance);
		for (int i = 0; i < 8; i++) {
			testAnt.avoidHome();
			System.out.println(testAnt.testPosition + " " + testAnt.testPosition.angle());
			System.out.println(testAnt.vectorToHome().length());
			Assert.assertNotEquals(startAngle, testAnt.testPosition.angle(), 0.00000001);
			Assert.assertTrue(testAnt.testPosition.toString(), testAnt.vectorToHome().length() > startDistance);
		}
	}
	
	@Test
	public void testAvoidEnemy() {
		System.out.println("TestingPleaseWait.testAvoidEnemy()");
		System.out.println(testAnt.testPosition + " " + testAnt.testPosition.angle());
		for(int i = 0; i < 16; i++){
			testAnt.avoidEnemy();
			System.out.println(testAnt.testPosition + " " + testAnt.testPosition.angle());
			Assert.assertNotEquals(startAngle, testAnt.testPosition.angle(), 0.000001);
		}
	}

	public class TestAntAILPW extends AntAILPW {

		private Vector testPosition, enemyPosition;
		private GameObject gameObject;

		public TestAntAILPW() {
			super();
			testPosition = test.getAntPosition();
			gameObject = new GameObject(testPosition, testParameters);
			enemyPosition = new Vector(185, 185);
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see wota.ai.loadingpleasewait.AntAILPW#tick()
		 */
		@Override
		public void tick() {
			avoidHome();
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see wota.ai.loadingpleasewait.AntAILPW#avoidHome()
		 */
		@Override
		protected void avoidHome() {
			super.avoidHome();
		}

		/* (non-Javadoc)
		 * @see wota.ai.loadingpleasewait.AntAILPW#avoidEnemy()
		 */
		@Override
		protected void avoidEnemy() {
			super.avoidEnemy();
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see wota.gameobjects.AntAI#vectorToHome()
		 */
		@Override
		protected Vector vectorToHome() {
			return testParameters.shortestDifferenceOnTorus(test.getHomePosition(), testPosition);
		}

		/* (non-Javadoc)
		 * @see wota.gameobjects.AntAI#visibleEnemies()
		 */
		@Override
		protected List<Ant> visibleEnemies() {
			return new ArrayList<Ant>();
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see wota.gameobjects.AntAI#moveInDirection(double, double)
		 */
		@Override
		protected void moveInDirection(double direction) {
			Vector moveVector = Vector.fromPolar(50, direction);
			moveVector = Vector.fromPolar(moveVector.length(), moveVector.angle());
			gameObject.move(moveVector);
			testPosition = gameObject.getPosition();
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see wota.ai.loadingpleasewait.AntAILPW#angleToHome()
		 */
		@Override
		public double angleToHome() {
			return testPosition.angle() - vectorToHome().angle();
		}

		/* (non-Javadoc)
		 * @see wota.ai.loadingpleasewait.AntAILPW#angleTo(wota.gameobjects.Snapshot)
		 */
		@Override
		public double angleTo(Snapshot snapshot) {
			return testPosition.angle() - vectorTo(snapshot).angle();
		}

		/* (non-Javadoc)
		 * @see wota.gameobjects.AI#vectorTo(wota.gameobjects.Snapshot)
		 */
		@Override
		protected Vector vectorTo(Snapshot target) {
			return testParameters.shortestDifferenceOnTorus(enemyPosition, testPosition);
		}

	}

}

package org.graphast.query.route.shortestpath;

import static org.junit.Assert.*;

import org.graphast.config.Configuration;
import org.graphast.graphgenerator.GraphGenerator;
import org.graphast.model.Graph;
import org.graphast.query.route.shortestpath.astar.AStarConstantWeight;
import org.graphast.query.route.shortestpath.model.Path;
import org.graphast.util.FileUtils;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.graphhopper.util.StopWatch;

public abstract class AbstractShortestPathTest {

	private Logger logger = LoggerFactory.getLogger(this.getClass());
	protected static Graph graphMonaco;
	protected static Graph graphExample;
	protected static Graph graphExample2;
	protected static Graph graphExample4;
	
	protected static AbstractShortestPathService serviceMonaco;
	protected static AbstractShortestPathService serviceExample;
	protected static AbstractShortestPathService serviceExample2;
	protected static AbstractShortestPathService serviceExample3;
	
	
	@BeforeClass
	public static void setup() {
		graphMonaco = new GraphGenerator().generateMonaco();
		graphExample = new GraphGenerator().generateExample();
		graphExample2 = new GraphGenerator().generateExample2();
		graphExample4 = new GraphGenerator().generateExample4();
	}

	@Test
	public void shortestPathMonacoTest() {
		
		Long source = graphMonaco.getNodeId(43.7294668047756,7.413772473047058);
		Long target = graphMonaco.getNodeId(43.73079058671274,7.415815422292399);

		StopWatch sw = new StopWatch();

		sw.start();
		Path shortestPath = serviceMonaco.shortestPath(source, target);
		sw.stop();

		logger.debug(shortestPath.toString());
		logger.debug("Execution Time of shortestPathMonacoTest(): {}ms", sw.getTime());
		logger.debug("Path Cost: {}", shortestPath.getPathCost());

		assertEquals(228910, shortestPath.getPathCost(), 0);
		
	}

	@Test
	public void shortestPathMonacoTest2() {
		
		Long source = graphMonaco.getNodeId(43.72842465479131, 7.414896579419745);
		Long target = graphMonaco.getNodeId(43.7354373276704, 7.4212202598427295);

		StopWatch sw = new StopWatch();

		sw.start();
		Path shortestPath = serviceMonaco.shortestPath(source, target);
		sw.stop();

		logger.debug(shortestPath.toString());
		logger.debug("Execution Time of shortestPathMonacoTest(): {}ms", sw.getTime());
		logger.debug("Path Cost: {}", shortestPath.getPathCost());

		assertEquals(1136643.0, shortestPath.getPathCost(), 0);

	}

	@Test
	public void shortestPathExampleTest() {

		Long source = 0L; // External ID = 1
		Long target = 5L; // External ID = 4

		StopWatch sw = new StopWatch();

		sw.start();
		Path shortestPath = serviceExample.shortestPath(source, target);
		sw.stop();

		logger.debug(shortestPath.toString());
		logger.debug("Execution Time of shortestPathExampleTest(): {}ms", sw.getTime());
		logger.debug("Path Cost: {}", shortestPath.getPathCost());

		assertEquals(8100, shortestPath.getPathCost(), 0);

	}
	
//	@Test
//	public void shortestPathExample2Test() {
//
//		Long source = 0L;
//		Long target = 6L;
//
//		AbstractShortestPathService aStar = new AStarConstantWeight(graphExample2);
//
//		StopWatch sw = new StopWatch();
//
//		sw.start();
//		Path shortestPath = aStar.shortestPath(source, target);
//		sw.stop();
//
//		logger.debug(shortestPath.toString());
//		logger.debug("Execution Time of shortestPathExample2Test(): {}ms", sw.getTime());
//		logger.debug("Path Cost: {}", shortestPath.getPathCost());
//
//		assertEquals(12, shortestPath.getPathCost(), 0);
//
//	}
	
	@Test
	public void shortestPathMonacoTest3() {

		Long source = graphMonaco.getNodeId(43.72636792197156, 7.417292499928754);
		Long target = graphMonaco.getNodeId(43.74766484829034, 7.430716770083832);

		StopWatch sw = new StopWatch();

		sw.start();
		Path shortestPath = serviceMonaco.shortestPath(source, target);
		sw.stop();

		logger.debug(shortestPath.toString());
		logger.debug("Execution Time of shortestPathMonacoTest3(): {}ms", sw.getTime());
		logger.debug("Path Cost: {}", shortestPath.getPathCost());

		assertEquals(3610712.0, shortestPath.getPathCost(), 0);

	}
	
	@Test
	public void shortestPathGraphExampleReverseTest2() {

		Long target = graphMonaco.getNodeId(43.72636792197156, 7.417292499928754);
		Long source = graphMonaco.getNodeId(43.74766484829034, 7.430716770083832);

		graphMonaco.reverseGraph();

		StopWatch sw = new StopWatch();

		sw.start();
		Path shortestPath = serviceMonaco.shortestPath(source, target);
		sw.stop();

		logger.debug(shortestPath.toString());
		logger.debug("Execution Time of shortestPathMonacoTest3(): {}ms", sw.getTime());
		logger.debug("Path Cost: {}", shortestPath.getPathCost());

		assertEquals(3610712.0, shortestPath.getPathCost(), 0);
	}

	
	@AfterClass
	public static void tearDown() {
		
		FileUtils.deleteDir(Configuration.USER_HOME + "/graphhopper/test");
		FileUtils.deleteDir(Configuration.USER_HOME + "/graphast/test");
	
	}

}

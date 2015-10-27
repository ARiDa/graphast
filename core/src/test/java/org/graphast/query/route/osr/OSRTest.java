package org.graphast.query.route.osr;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;

import org.graphast.graphgenerator.GraphGenerator;
import org.graphast.model.Graph;
import org.graphast.model.GraphBounds;
import org.graphast.util.DateUtils;
import org.junit.BeforeClass;
import org.junit.Test;


public class OSRTest {

	private static OSRSearch osr, osrMonaco;
	private static GraphBounds graphBoundsPoI, graphBoundsPoIReverse;
//	private static GraphBounds monacoBoundsPoI, monacoBoundsPoIReverse;

	
	@BeforeClass
	public static void setup() throws NumberFormatException, IOException {
		
		/*
		 * The type of graph the will be used to retrieve costs needed. 
		 * 		0 = Regular Costs;
		 * 		1 = Lower Bound Costs;
		 * 		3 = Upper Bound Costs.
		 */
		short graphType = 1;
//		short monacoGraphType = 0;
		
		//Loads into graphBoundsPoI the graph related to the generateExamplePoI method (including upperBounds and 
		//lowerBounds for both edges and PoI's
		graphBoundsPoI = new GraphGenerator().generateExamplePoI();
		
		graphBoundsPoIReverse = new GraphGenerator().generateExamplePoI();
		
		graphBoundsPoIReverse.reverseGraph();
		
//		monacoBoundsPoI = new GraphGenerator().generateMonaco();
//		
//		monacoBoundsPoIReverse = new GraphGenerator().generateMonaco();
//		
//		monacoBoundsPoIReverse.reverseGraph();
		
		//The variable 'bounds' represents a lowerBound shortestPath of starting in each vertex to all PoI.
		//For a better understanding, try to print the variable 'bounds'
		BoundsRoute bounds = new BoundsRoute(graphBoundsPoI, graphType);
		
		osr = new OSRSearch(graphBoundsPoI, bounds, graphBoundsPoIReverse);
		
//		BoundsRoute monacoBounds = new BoundsRoute(monacoBoundsPoI, monacoGraphType);
		
//		osrMonaco = new OSRSearch(monacoBoundsPoI, monacoBounds, monacoBoundsPoIReverse);
		
	}
	
	@Test
	public void search() throws ParseException{
		
		ArrayList<Integer> categories = new ArrayList<Integer>();
		categories.add(2);
		categories.add(1);
		
    	Date date = DateUtils.parseDate(0, 550, 0);
    	
    	Graph graph = osr.getGraphAdapter();
    	
    	Sequence seq = osr.search(graph.getNode(1), graph.getNode(7), date, categories);
    	
    	
    	assertEquals(7980000, seq.getDistance());
    	assertEquals(37679450, seq.getTimeToService());
    	assertEquals(29699450, seq.getWaitingTime());
    	
	}
	
//	@Test
//	public void searchMonaco() throws ParseException{
//		
//		ArrayList<Integer> categories = new ArrayList<Integer>();
//		categories.add(161);
//		categories.add(105);
//		
//    	Date date = DateUtils.parseDate(0, 550, 0);
//    	
//    	Graph graph = osrMonaco.getGraphAdapter();
//    	
//    	Sequence seq = osrMonaco.search(graph.getNode(1), graph.getNode(200), date, categories);
//    	
//    	
////    	assertEquals(7980000, seq.getDistance());
////    	assertEquals(37679450, seq.getTimeToService());
////    	assertEquals(29699450, seq.getWaitingTime());
//    	
//	}

}
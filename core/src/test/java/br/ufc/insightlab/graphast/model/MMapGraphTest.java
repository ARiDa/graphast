/*
 * MIT License
 * 
 * Copyright (c) 2017 Insight Data Science Lab
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
*/

package br.ufc.insightlab.graphast.model;

import com.google.common.collect.Iterators;
import br.ufc.insightlab.graphast.serialization.SerializationUtils;
import br.ufc.insightlab.graphast.structure.MMapGraphStructure;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;
import java.util.Arrays;

import static org.hamcrest.collection.IsIterableContainingInAnyOrder.containsInAnyOrder;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

public class MMapGraphTest {

	private static Graph g;
	private static Node n0, n1, n2, n3, n4;
	private static Edge e0, e1, e2, e3, e4, e5, e6;
	
	@BeforeClass
	public static void setUpBeforeClass() throws IOException {
		String graphPath = "graphs/MMap/test_graph";
		
		SerializationUtils.deleteMMapGraph(graphPath);
		g = new Graph(new MMapGraphStructure(graphPath));
		
		n0 = new Node(0);
		n1 = new Node(1);
		n2 = new Node(2);
		n3 = new Node(3);
		n4 = new Node(4);
		e0 = new Edge(1, 4);
		e0.setBidirectional(true);
		e1 = new Edge(2, 4);
		e2 = new Edge(0, 1);
		e3 = new Edge(3, 1);
		e3.setBidirectional(true);
		e4 = new Edge(2, 3);
		e5 = new Edge(4, 3);
		e6 = new Edge(3, 0);
		e6.setBidirectional(true);
		g.addNodes(n0, n1, n2, n3, n4);
		g.addEdges(e0, e2, e4, e1, e6, e5, e3);
		
	}
	
	@Test
	public void testNumberOfNodes() {
		assertEquals(5, g.getNumberOfNodes());
	}
	
	@Test
	public void testNumberOfEdges() {
		assertEquals(10, g.getNumberOfEdges());
	}
	
	@Test
	public void testGetOutEdges() {
		Edge e0Inv = new Edge(e0.getToNodeId(), e0.getFromNodeId(), e0.getWeight());
		Edge e3Inv = new Edge(e3.getToNodeId(), e3.getFromNodeId(), e3.getWeight());
		Edge e6Inv = new Edge(e6.getToNodeId(), e6.getFromNodeId(), e6.getWeight());
		assertThat("Out Edges Test n0", Arrays.asList(e2, e6Inv), containsInAnyOrder(Iterators.toArray(g.getOutEdgesIterator(0), Edge.class)));
		assertThat("Out Edges Test n1", Arrays.asList(e0, e3Inv), containsInAnyOrder(Iterators.toArray(g.getOutEdgesIterator(1), Edge.class)));
		assertThat("Out Edges Test n2", Arrays.asList(e1, e4), containsInAnyOrder(Iterators.toArray(g.getOutEdgesIterator(2), Edge.class)));
		assertThat("Out Edges Test n3", Arrays.asList(e3, e6), containsInAnyOrder(Iterators.toArray(g.getOutEdgesIterator(3), Edge.class)));
		assertThat("Out Edges Test n4", Arrays.asList(e0Inv, e5), containsInAnyOrder(Iterators.toArray(g.getOutEdgesIterator(4), Edge.class)));
		
	}

	@Test
	public void testGetInEdges() {
		Edge e0Inv = new Edge(e0.getToNodeId(), e0.getFromNodeId(), e0.getWeight());
		Edge e3Inv = new Edge(e3.getToNodeId(), e3.getFromNodeId(), e3.getWeight());
		Edge e6Inv = new Edge(e6.getToNodeId(), e6.getFromNodeId(), e6.getWeight());
		assertThat("In Edges Test n0", Arrays.asList(e6), containsInAnyOrder(Iterators.toArray(g.getInEdgesIterator(0), Edge.class)));
		assertThat("In Edges Test n1", Arrays.asList(e0Inv, e2, e3), containsInAnyOrder(Iterators.toArray(g.getInEdgesIterator(1), Edge.class)));
		assertThat("In Edges Test n2", Arrays.<Edge>asList(), containsInAnyOrder(Iterators.toArray(g.getInEdgesIterator(2), Edge.class)));
		assertThat("In Edges Test n3", Arrays.asList(e3Inv, e4, e5, e6Inv), containsInAnyOrder(Iterators.toArray(g.getInEdgesIterator(3), Edge.class)));
		assertThat("In Edges Test n4", Arrays.asList(e0, e1), containsInAnyOrder(Iterators.toArray(g.getInEdgesIterator(4), Edge.class)));
	}

	@Test
	public void testGetNeighborhood() {
		assertThat("Neighborhood Test n0", Arrays.asList(1l, 3l), containsInAnyOrder(Iterators.toArray(g.getNeighborhoodIterator(0), Long.class)));
		assertThat("Neighborhood Test n1", Arrays.asList(4l, 3l), containsInAnyOrder(Iterators.toArray(g.getNeighborhoodIterator(1), Long.class)));
		assertThat("Neighborhood Test n2", Arrays.asList(4l, 3l), containsInAnyOrder(Iterators.toArray(g.getNeighborhoodIterator(2), Long.class)));
		assertThat("Neighborhood Test n3", Arrays.asList(1l, 0l), containsInAnyOrder(Iterators.toArray(g.getNeighborhoodIterator(3), Long.class)));
		assertThat("Neighborhood Test n4", Arrays.asList(1l, 3l), containsInAnyOrder(Iterators.toArray(g.getNeighborhoodIterator(4), Long.class)));
	}
	
	@Test
	public void testReopenGraph() throws IOException {
		Graph g2 = new Graph(new MMapGraphStructure("graphs/MMap/test_graph"));
		assertEquals("Reoppened graph number of nodes", 5, g2.getNumberOfNodes());
		assertEquals("Reoppened graph number of edges", 10, g2.getNumberOfEdges());
		
		Edge e0Inv = new Edge(e0.getToNodeId(), e0.getFromNodeId(), e0.getWeight());
		Edge e3Inv = new Edge(e3.getToNodeId(), e3.getFromNodeId(), e3.getWeight());
		Edge e6Inv = new Edge(e6.getToNodeId(), e6.getFromNodeId(), e6.getWeight());
		
		assertThat("Reoppened Graph Out Edges Test n0", Arrays.asList(e2, e6Inv), containsInAnyOrder(Iterators.toArray(g2.getOutEdgesIterator(0), Edge.class)));
		assertThat("Reoppened Graph Out Edges Test n1", Arrays.asList(e0, e3Inv), containsInAnyOrder(Iterators.toArray(g2.getOutEdgesIterator(1), Edge.class)));
		assertThat("Reoppened Graph Out Edges Test n2", Arrays.asList(e1, e4), containsInAnyOrder(Iterators.toArray(g2.getOutEdgesIterator(2), Edge.class)));
		assertThat("Reoppened Graph Out Edges Test n3", Arrays.asList(e3, e6), containsInAnyOrder(Iterators.toArray(g2.getOutEdgesIterator(3), Edge.class)));
		assertThat("Reoppened Graph Out Edges Test n4", Arrays.asList(e0Inv, e5), containsInAnyOrder(Iterators.toArray(g2.getOutEdgesIterator(4), Edge.class)));
	}
	

}

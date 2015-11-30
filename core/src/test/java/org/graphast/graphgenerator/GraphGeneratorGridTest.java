package org.graphast.graphgenerator;

import org.graphast.model.GraphBounds;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

public class GraphGeneratorGridTest {

	@Test
	public void gererateGraphSynthetic2x2() {
		
		GraphGeneratorGrid graphSynthetic = new GraphGeneratorGrid(2,2,0);
		graphSynthetic.generateGraph();
		GraphBounds graph = graphSynthetic.getGraph();
		
		Assert.assertEquals(graph.getNumberOfNodes(), 4);
		Assert.assertEquals(graph.getNumberOfEdges(), 8);
	}
	
	@Test
	public void gererateGraphSynthetic4x4() {
		
		GraphGeneratorGrid graphSynthetic = new GraphGeneratorGrid(4,4, 0);
		graphSynthetic.generateGraph();
		GraphBounds graph = graphSynthetic.getGraph();
		
		Assert.assertEquals(graph.getNumberOfNodes(), 16);
		Assert.assertEquals(graph.getNumberOfEdges(), 48);
	}
	
	@Test
	public void gererateGraphSynthetic5x5() {
		
		GraphGeneratorGrid graphSynthetic = new GraphGeneratorGrid(5,5, 0);
		graphSynthetic.generateGraph();
		GraphBounds graph = graphSynthetic.getGraph();
		
		Assert.assertEquals(graph.getNumberOfNodes(), 25);
		Assert.assertEquals(graph.getNumberOfEdges(), 80);
	}
	
	@Test
	public void gererateGraphSynthetic100x100() {
		
		GraphGeneratorGrid graphSynthetic = new GraphGeneratorGrid(100,100, 0);
		graphSynthetic.generateGraph();
		GraphBounds graph = graphSynthetic.getGraph();
		
		Assert.assertEquals(graph.getNumberOfNodes(), 10000);
		Assert.assertEquals(graph.getNumberOfEdges(), 39600);
	}

	@Test
	public void gererateGraphSyntheticLimit() {
		
		int comprimento = 992;
		int altura = 992;
		
		GraphGeneratorGrid graphSynthetic = new GraphGeneratorGrid(comprimento,altura, 0);
		graphSynthetic.generateGraph();
		GraphBounds graph = graphSynthetic.getGraph();
		
		Assert.assertEquals(graph.getNumberOfNodes(), comprimento*altura);
		Assert.assertEquals(graph.getNumberOfEdges(), 2*altura*(2*(comprimento-1)));
	}
	
	@Test
	public void gererateGraphSyntheticLimitWithPoi() {
		
		int comprimento = 992;
		int altura = 992;
		
		GraphGeneratorGrid graphSynthetic = new GraphGeneratorGrid(comprimento,altura, 1);
		graphSynthetic.generateGraph();
		GraphBounds graph = graphSynthetic.getGraph();
		
		Assert.assertEquals(graph.getNumberOfNodes(), comprimento*altura);
		Assert.assertEquals(graph.getNumberOfEdges(), 2*altura*(2*(comprimento-1)));
		Assert.assertEquals(graph.getCategories().size(), 9840);
	}
	
	@Test
	@Ignore
	public void gererateGraphSyntheticDifferentSize() {
		
		int comprimento = 3;
		int altura = 2;
		
		GraphGeneratorGrid graphSynthetic = new GraphGeneratorGrid(comprimento,altura, 0);
		graphSynthetic.generateGraph();
		GraphBounds graph = graphSynthetic.getGraph();
		
		Assert.assertEquals(graph.getNumberOfNodes(), comprimento*altura);
		Assert.assertEquals(graph.getNumberOfEdges(), 2*altura*(2*(comprimento-1)));
	}
	
	@Test
	public void gererateGraphSyntheticWithPoiShort() {

		int comprimento = 4;
		int altura = 4;
		
		GraphGeneratorGrid graphSynthetic = new GraphGeneratorGrid(comprimento, altura, 1);
		graphSynthetic.generateGraph();
		GraphBounds graph = graphSynthetic.getGraph();
		
		Assert.assertEquals(graph.getCategories().size(), 1);
	}
	
	@Test
	public void gererateGraphSyntheticWithPoi() {

		int comprimento = 10;
		int altura = 10;
		
		GraphGeneratorGrid graphSynthetic = new GraphGeneratorGrid(comprimento, altura, 2);
		graphSynthetic.generateGraph();
		GraphBounds graph = graphSynthetic.getGraph();
		
		Assert.assertEquals(graph.getCategories().size(), 2);
	}
	
	@Test
	public void gererateGraphSyntheticMin() {

		int comprimento = 1;
		int altura = 1;
		int percentagemPoi = 1;
		
		GraphGeneratorGrid graphSynthetic = new GraphGeneratorGrid(comprimento, altura, percentagemPoi);
		graphSynthetic.generateGraph();
		GraphBounds graph = graphSynthetic.getGraph();
		
		Assert.assertEquals(graph.getCategories().size(), 1);
	}
	
	// grafos para o teste do algoritmo =============
	
	// 1k (1024 pontos)
	@Test
	@Ignore
	public void gererateGraphSyntheticLimitWithPoi1k() {
		
		int comprimento = 32;
		int altura = 32;
		int qtdPoi = 10;
		
		GraphGeneratorGrid graphSynthetic = new GraphGeneratorGrid(comprimento,altura, 1);
		graphSynthetic.generateGraph();
		GraphBounds graph = graphSynthetic.getGraph();
		
		Assert.assertEquals(graph.getNumberOfNodes(), comprimento*altura);
		Assert.assertEquals(graph.getNumberOfEdges(), 2*altura*(2*(comprimento-1)));
		Assert.assertEquals(graph.getCategories().size(), qtdPoi);
	}
	
	// 10k (10000 pontos)
	@Test
	@Ignore
	public void gererateGraphSyntheticLimitWithPoi10k() {
		
		int comprimento = 100;
		int altura = 100;
		int qtdPoi = 100;
		int percentualPoi = 1;
		
		GraphGeneratorGrid graphSynthetic = new GraphGeneratorGrid(comprimento,altura, percentualPoi);
		graphSynthetic.generateGraph();
		GraphBounds graph = graphSynthetic.getGraph();
		
		Assert.assertEquals(comprimento*altura, graph.getNumberOfNodes());
		Assert.assertEquals(2*altura*(2*(comprimento-1)), graph.getNumberOfEdges());
		Assert.assertEquals(qtdPoi, graph.getCategories().size());
	}
	
	// 100k (99856 pontos)
	@Test
	@Ignore
	public void gererateGraphSyntheticLimitWithPoi100k() {
		
		int comprimento = 316;
		int altura = 316;
		int qtdPoi = 998;
		
		GraphGeneratorGrid graphSynthetic = new GraphGeneratorGrid(comprimento,altura, 1);
		graphSynthetic.generateGraph();
		GraphBounds graph = graphSynthetic.getGraph();
		
		Assert.assertEquals(graph.getNumberOfNodes(), comprimento*altura);
		Assert.assertEquals(graph.getNumberOfEdges(), 2*altura*(2*(comprimento-1)));
		Assert.assertEquals(graph.getCategories().size(), qtdPoi);
	}
	
	// 1G (1000000 pontos)
	@Test
	@Ignore
	public void gererateGraphSyntheticLimitWithPoi1000k() {
		
		int comprimento = 1000;
		int altura = 1000;
		int qtdPoi = 10000;
		
		GraphGeneratorGrid graphSynthetic = new GraphGeneratorGrid(comprimento,altura, 1);
		graphSynthetic.generateGraph();
		GraphBounds graph = graphSynthetic.getGraph();
		
		Assert.assertEquals(graph.getNumberOfNodes(), comprimento*altura);
		Assert.assertEquals(graph.getNumberOfEdges(), 2*altura*(2*(comprimento-1)));
		Assert.assertEquals(graph.getCategories().size(), qtdPoi);
	}
	
}

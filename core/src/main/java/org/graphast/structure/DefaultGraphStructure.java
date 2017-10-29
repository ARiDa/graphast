package org.graphast.structure;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import org.graphast.model.Edge;
import org.graphast.model.Node;

public class DefaultGraphStructure implements GraphStructure {
	
	private Integer nextId = 0;
	
	private HashMap<Long, Integer> idMapping = new HashMap<>();
	private ArrayList<Node> nodes = new ArrayList<>();
	private ArrayList<Edge> edges = new ArrayList<>();
	private ArrayList<ArrayList<Edge>> outEdges = new ArrayList<>();
	private ArrayList<ArrayList<Edge>> inEdges = new ArrayList<>();
	
	@Override
	public int getNumberOfNodes() {
		return nodes.size();
	}
	
	@Override
	public int getNumberOfEdges() {
		return edges.size();
	}
	
	public void addNode(Node n) {
		idMapping.put(n.getId(), nextId++);
		nodes.add(n);
		outEdges.add(new ArrayList<Edge>());
		inEdges.add(new ArrayList<Edge>());
	}
	
	private void addAdjacency(long id, Edge e) {
		int nodeId = idMapping.get(id);
		if (e.isBidirectional()) {
			inEdges.get(nodeId).add(e);
			outEdges.get(nodeId).add(e);
		}
		else if (e.getFromNodeId() == id) {
			outEdges.get(nodeId).add(e);
		}
		else if (e.getToNodeId() == id) {
			inEdges.get(nodeId).add(e);
		}
	}
	
	public void addEdge(Edge e) {
		edges.add(e);
		addAdjacency(e.getFromNodeId(), e);
		addAdjacency(e.getToNodeId(), e);
	}
	
	public Node getNode(final long id) {
		return nodes.get(idMapping.get(id));
	}
	
	public Iterator<Node> nodeIterator() {
		return nodes.iterator();
	}
	
	public Iterator<Edge> edgeIterator() {
		return edges.iterator();
	}
	
	public Iterator<Edge> getOutEdges(final long id) {
		return outEdges.get(idMapping.get(id)).iterator();
	}
	
	public Iterator<Edge> getInEdges(final long id) {
		return inEdges.get(idMapping.get(id)).iterator();
	}

}
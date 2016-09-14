package org.graphast.query.route.shortestpath.dijkstrach;

import static org.graphast.util.NumberUtils.convertToInt;

import java.util.Collections;
import java.util.HashMap;
import java.util.PriorityQueue;

import org.graphast.exception.PathNotFoundException;
import org.graphast.model.Edge;
import org.graphast.model.Node;
import org.graphast.model.contraction.CHEdge;
import org.graphast.model.contraction.CHEdgeImpl;
import org.graphast.model.contraction.CHGraph;
import org.graphast.query.route.shortestpath.model.DistanceEntry;
import org.graphast.query.route.shortestpath.model.Path;
import org.graphast.query.route.shortestpath.model.RouteEntry;

import com.graphhopper.util.StopWatch;

import it.unimi.dsi.fastutil.longs.Long2IntMap;

public class BidirectionalDijkstraCH {

	protected static int wasSettled = -1;
	protected static boolean forwardDirection = true;
	protected static boolean backwardDirection = false;

	Node source;
	Node target;

	Path path;

	PriorityQueue<DistanceEntry> forwardsUnsettleNodes = new PriorityQueue<>();
	PriorityQueue<DistanceEntry> backwardsUnsettleNodes = new PriorityQueue<>();
	HashMap<Long, Integer> forwardsUnsettleNodesAux = new HashMap<>();
	HashMap<Long, Integer> backwardsUnsettleNodesAux = new HashMap<>();

	HashMap<Long, Integer> forwardsSettleNodes = new HashMap<>();
	HashMap<Long, Integer> backwardsSettleNodes = new HashMap<>();
	PriorityQueue<DistanceEntry> forwardsSettleNodesAux = new PriorityQueue<>(Collections.reverseOrder());
	PriorityQueue<DistanceEntry> backwardsSettleNodesAux = new PriorityQueue<>(Collections.reverseOrder());

	HashMap<Long, RouteEntry> forwardsParentNodes = new HashMap<>();
	HashMap<Long, RouteEntry> backwardsParentNodes = new HashMap<>();

	DistanceEntry forwardsRemovedNode;
	DistanceEntry backwardsRemovedNode;
	DistanceEntry meetingNode;
	DistanceEntry miNode = new DistanceEntry(-1, Integer.MAX_VALUE, -1);
	private CHGraph graph;

	// --METRICS VARIABLES
	int numberOfForwardSearches = 0;
	int numberOfBackwardSearches = 0;
	int numberOfRegularSearches = 0;
	int generalCounter = 0;
	StopWatch forwardSearchSW = new StopWatch();
	StopWatch backwardSearchSW = new StopWatch();
	StopWatch regularSearchSW = new StopWatch();
	StopWatch generalSW = new StopWatch();
	int forwardSettleNodeSize = 0;
	int forwardUnsettleNodeSize = 0;

	public BidirectionalDijkstraCH(CHGraph graph) {

		this.graph = graph;

	}

	/**
	 * Bidirectional Dijkstra algorithm modified to deal with the Contraction
	 * Hierarchies speed up technique.
	 * 
	 * @param source
	 *            source node
	 * @param target
	 *            target node
	 */
	public Path execute(Node source, Node target) {

		this.setSource(source);
		this.setTarget(target);

		initializeQueue(source, forwardsUnsettleNodes);
		initializeQueue(target, backwardsUnsettleNodes);
		
		forwardsUnsettleNodesAux.put(source.getId(), 0);

		forwardsRemovedNode = forwardsUnsettleNodes.peek();
		backwardsRemovedNode = backwardsUnsettleNodes.peek();

		forwardsSettleNodesAux.offer(forwardsRemovedNode);
		backwardsSettleNodesAux.offer(backwardsRemovedNode);

		while (!forwardsUnsettleNodes.isEmpty() && !backwardsUnsettleNodes.isEmpty()) {

			// Condition to alternate between forward and backward search
			if (forwardsUnsettleNodes.peek().getDistance() <= backwardsUnsettleNodes.peek().getDistance()) {

				forwardSearchSW.start();
				forwardSearch();
				forwardSearchSW.stop();
				numberOfForwardSearches++;

			} else {
				
				backwardSearchSW.start();
				backwardSearch();
				backwardSearchSW.stop();
				numberOfBackwardSearches++;

			}

			if (path != null) {

				return path;

			}

		}

		throw new PathNotFoundException(
				"Path not found between (" + source.getLatitude() + "," + source.getLongitude() + ")");

	}

	private void forwardSearch() {

		forwardsRemovedNode = forwardsUnsettleNodes.poll();
		forwardsUnsettleNodesAux.remove(forwardsRemovedNode.getId());
		forwardsSettleNodes.put(forwardsRemovedNode.getId(), forwardsRemovedNode.getDistance());
		forwardsSettleNodesAux.offer(forwardsRemovedNode);
		
		// Stopping criteria of Bidirectional search
		if (miNode.getId()!=-1 && (forwardsSettleNodesAux.peek().getDistance() + backwardsSettleNodesAux.peek().getDistance() >= forwardsSettleNodes.get(miNode.getId()) + backwardsSettleNodes.get(miNode.getId()))) {

			meetingNode = miNode;
			HashMap<Long, RouteEntry> resultParentNodes;
			path = new Path();
			resultParentNodes = joinParents(meetingNode, forwardsParentNodes, backwardsParentNodes);
			path.constructPath(target.getId(), resultParentNodes, graph);

			return;

		}

		expandVertexForward();

	}

	private void backwardSearch() {

		backwardsRemovedNode = backwardsUnsettleNodes.poll();
		backwardsSettleNodes.put(backwardsRemovedNode.getId(), backwardsRemovedNode.getDistance());
		backwardsSettleNodesAux.offer(backwardsRemovedNode);

		// Stopping criteria of Bidirectional search
		if (miNode.getId()!=-1 && (forwardsSettleNodesAux.peek().getDistance() + backwardsSettleNodesAux.peek().getDistance() >= forwardsSettleNodes.get(miNode.getId()) + backwardsSettleNodes.get(miNode.getId()))) {

			meetingNode = miNode;
			HashMap<Long, RouteEntry> resultParentNodes;
			path = new Path();
			resultParentNodes = joinParents(meetingNode, forwardsParentNodes, backwardsParentNodes);
			path.constructPath(target.getId(), resultParentNodes, graph);

			return;

		}

		expandVertexBackward();

	}

	private void expandVertexForward() {

		Long2IntMap neighbors = graph.accessNeighborhood(graph.getNode(forwardsRemovedNode.getId()));

		for (long vid : neighbors.keySet()) {

			DistanceEntry newEntry = new DistanceEntry(vid,	neighbors.get(vid) + forwardsRemovedNode.getDistance(), forwardsRemovedNode.getId());

			Edge edge;
			int distance;

			if (!forwardsSettleNodes.containsKey(vid)) {
				
				forwardsUnsettleNodes.offer(newEntry);
				forwardsUnsettleNodesAux.put(newEntry.getId(), newEntry.getDistance());
				forwardsSettleNodes.put(newEntry.getId(), newEntry.getDistance());
				
				distance = neighbors.get(vid);
				edge = getEdge(forwardsRemovedNode.getId(), vid, distance, forwardDirection);

				forwardsParentNodes.put(vid, new RouteEntry(forwardsRemovedNode.getId(), distance, edge.getId(), edge.getLabel()));

			} else {

				int cost = forwardsSettleNodes.get(vid);
				distance = neighbors.get(vid) + forwardsRemovedNode.getDistance();

				if (cost > distance) {
					forwardsUnsettleNodes.remove(newEntry);
					forwardsUnsettleNodesAux.remove(newEntry.getId());
					forwardsUnsettleNodes.offer(newEntry);
					forwardsUnsettleNodesAux.put(newEntry.getId(), newEntry.getDistance());
					
					forwardsSettleNodesAux.remove(newEntry);
					forwardsSettleNodesAux.offer(newEntry);

				
							
					forwardsSettleNodes.remove(newEntry.getId());
					forwardsSettleNodes.put(newEntry.getId(), distance);
							
						
					forwardsParentNodes.remove(vid);
					distance = neighbors.get(vid);
					edge = getEdge(forwardsRemovedNode.getId(), vid, distance, forwardDirection);
					forwardsParentNodes.put(vid, new RouteEntry(forwardsRemovedNode.getId(), distance, edge.getId(), edge.getLabel()));

				}
			}
			
			if(miNode.getId()==-1) {
				if(backwardsSettleNodes.containsKey(vid) && (forwardsSettleNodes.get(forwardsRemovedNode.getId())+neighbors.get(vid)+backwardsSettleNodes.get(vid)) <= miNode.getDistance() ) {
					miNode = newEntry;
				}
			} else {
				if(backwardsSettleNodes.containsKey(vid) && (forwardsSettleNodes.get(forwardsRemovedNode.getId())+neighbors.get(vid)+backwardsSettleNodes.get(vid)) <= forwardsSettleNodes.get(miNode.getId()) + backwardsSettleNodes.get(miNode.getId())) {
					miNode = newEntry;
				}
			}
			
			if(miNode.getId()==-1) {
				if(backwardsSettleNodes.containsKey(vid) && (forwardsSettleNodes.get(forwardsRemovedNode.getId())+neighbors.get(vid)+backwardsSettleNodes.get(vid)) <= miNode.getDistance() ) {
					miNode = newEntry;
				}
			} else {
				if(backwardsUnsettleNodesAux.containsKey(vid) && (forwardsSettleNodes.get(forwardsRemovedNode.getId())+neighbors.get(vid)+backwardsUnsettleNodesAux.get(vid)) <= forwardsSettleNodes.get(miNode.getId()) + backwardsUnsettleNodesAux.get(miNode.getId())) {
					miNode = newEntry;
				}
			}
		}
	}

	private void expandVertexBackward() {

		Long2IntMap neighbors = this.graph.accessIngoingNeighborhood(this.graph.getNode(backwardsRemovedNode.getId()));

		for (long vid : neighbors.keySet()) {

			DistanceEntry newEntry = new DistanceEntry(vid,	neighbors.get(vid) + backwardsRemovedNode.getDistance(), backwardsRemovedNode.getId());

			Edge edge;
			int distance;

			if (!backwardsSettleNodes.containsKey(vid)) {

				backwardsUnsettleNodes.offer(newEntry);
				backwardsSettleNodes.put(newEntry.getId(), newEntry.getDistance());

				distance = neighbors.get(vid);
				edge = getEdge(backwardsRemovedNode.getId(), vid, distance, backwardDirection);

				backwardsParentNodes.put(vid, new RouteEntry(backwardsRemovedNode.getId(), distance, edge.getId(), edge.getLabel()));

			} else {

				int cost = backwardsSettleNodes.get(vid);
				distance = backwardsRemovedNode.getDistance() + neighbors.get(vid);

				if (cost > distance) {
					backwardsUnsettleNodes.remove(newEntry);
					backwardsUnsettleNodes.offer(newEntry);
					
					backwardsSettleNodesAux.remove(newEntry);
					backwardsSettleNodesAux.offer(newEntry);
					
					backwardsSettleNodes.remove(newEntry.getId());
					backwardsSettleNodes.put(newEntry.getId(), distance);

					backwardsParentNodes.remove(vid);
					distance = neighbors.get(vid);
					edge = getEdge(backwardsRemovedNode.getId(), vid, distance, backwardDirection);
					backwardsParentNodes.put(vid,
							new RouteEntry(backwardsRemovedNode.getId(), distance, edge.getId(), edge.getLabel()));

				}
			}
			
			if(miNode.getId()==-1) {
				if(forwardsSettleNodes.containsKey(vid) && (backwardsSettleNodes.get(backwardsRemovedNode.getId())+neighbors.get(vid)+forwardsSettleNodes.get(vid)) <= miNode.getDistance() ) {
					miNode = newEntry;
				}
			} else {
				if(forwardsSettleNodes.containsKey(vid) && (backwardsSettleNodes.get(backwardsRemovedNode.getId())+neighbors.get(vid)+forwardsSettleNodes.get(vid)) <= forwardsSettleNodes.get(miNode.getId()) + backwardsSettleNodes.get(miNode.getId()) ) {
					miNode = newEntry;
				}
			}
			
			if(miNode.getId()==-1) {
				if(forwardsSettleNodes.containsKey(vid) && (backwardsSettleNodes.get(backwardsRemovedNode.getId())+neighbors.get(vid)+forwardsSettleNodes.get(vid)) <= miNode.getDistance() ) {
					miNode = newEntry;
				}
			} else {
				if(forwardsUnsettleNodesAux.containsKey(vid) && (backwardsSettleNodes.get(backwardsRemovedNode.getId())+neighbors.get(vid)+forwardsUnsettleNodesAux.get(vid)) <= forwardsUnsettleNodesAux.get(miNode.getId()) + backwardsSettleNodes.get(miNode.getId()) ) {
					miNode = newEntry;
				}
			}
			
		}
	}

	private void initializeQueue(Node node, PriorityQueue<DistanceEntry> queue) {

		int nodeId = convertToInt(node.getId());

		queue.offer(new DistanceEntry(nodeId, 0, -1));

	}

	private HashMap<Long, RouteEntry> joinParents(DistanceEntry meetingNode,
			HashMap<Long, RouteEntry> forwardsParentNodes, HashMap<Long, RouteEntry> backwardsParentNodes) {

		HashMap<Long, RouteEntry> resultListOfParents = new HashMap<>();

		RouteEntry nextForwardParent = forwardsParentNodes.get(meetingNode.getId());

		resultListOfParents.put(meetingNode.getId(), nextForwardParent);

		while (forwardsParentNodes.get(nextForwardParent.getId()) != null) {

			resultListOfParents.put(nextForwardParent.getId(), forwardsParentNodes.get(nextForwardParent.getId()));

			nextForwardParent = forwardsParentNodes.get(nextForwardParent.getId());

		}

		RouteEntry nextBackwardsParent = new RouteEntry(meetingNode.getId(),
				backwardsParentNodes.get(meetingNode.getId()).getCost(),
				backwardsParentNodes.get(meetingNode.getId()).getEdgeId(),
				backwardsParentNodes.get(meetingNode.getId()).getLabel());

		resultListOfParents.put(backwardsParentNodes.get(meetingNode.getId()).getId(), nextBackwardsParent);

		long nextNodeId = backwardsParentNodes.get(meetingNode.getId()).getId();

		while (backwardsParentNodes.get(nextNodeId) != null) {
			nextBackwardsParent = new RouteEntry(nextNodeId, backwardsParentNodes.get(nextNodeId).getCost(),
					backwardsParentNodes.get(nextNodeId).getEdgeId(), backwardsParentNodes.get(nextNodeId).getLabel());
			nextNodeId = backwardsParentNodes.get(nextNodeId).getId();

			resultListOfParents.put(nextNodeId, nextBackwardsParent);

		}

		return resultListOfParents;

	}

	private CHEdge getEdge(long fromNodeId, long toNodeId, int distance, boolean expandingDirection) {

		if (expandingDirection) {

			return getEdgeForwards(fromNodeId, toNodeId, distance);

		} else {

			return getEdgeBackwards(fromNodeId, toNodeId, distance);

		}

	}

	private CHEdge getEdgeForwards(long fromNodeId, long toNodeId, int distance) {

		CHEdge edge = null;

		for (Long outEdge : this.graph.getOutEdges(fromNodeId)) {
			edge = this.graph.getEdge(outEdge);
			if ((int) edge.getToNode() == toNodeId && edge.getDistance() == distance) {
				break;
			}
		}

		return edge;

	}

	private CHEdge getEdgeBackwards(long fromNodeId, long toNodeId, int distance) {

		CHEdge edge = null;

		for (Long inEdge : this.graph.getInEdges(fromNodeId)) {
			edge = this.graph.getEdge(inEdge);
			if ((int) edge.getFromNode() == toNodeId && edge.getDistance() == distance) {
				break;
			}
		}

		CHEdge returningEdge = new CHEdgeImpl(edge);

		returningEdge.setFromNode(fromNodeId);
		returningEdge.setToNode(toNodeId);

		return returningEdge;

	}

	public Path executeRegular(Node source, Node target) {

		this.setSource(source);
		this.setTarget(target);

		initializeQueue(source, forwardsUnsettleNodes);
		initializeQueue(target, backwardsUnsettleNodes);

		while (!forwardsUnsettleNodes.isEmpty()) {

			forwardsRemovedNode = forwardsUnsettleNodes.poll();
			forwardsSettleNodes.put(forwardsRemovedNode.getId(), forwardsRemovedNode.getDistance());

			// Stopping criteria of Bidirectional search
			if (forwardsRemovedNode.getId() == target.getId()) {

				path = new Path();
				path.constructPath(target.getId(), forwardsParentNodes, graph);

				return path;

			}

			StopWatch auxiliarSW = new StopWatch();
			auxiliarSW.start();
			expandVertexForward();
			auxiliarSW.stop();

			numberOfRegularSearches++;

		}

		throw new PathNotFoundException(
				"Path not found between (" + source.getLatitude() + "," + source.getLongitude() + ")");

	}

	public void setSource(Node source) {
		this.source = source;
	}

	public void setTarget(Node target) {
		this.target = target;
	}

	public int getNumberOfForwardSearches() {
		return numberOfForwardSearches;
	}

	public int getNumberOfBackwardSearches() {
		return numberOfBackwardSearches;
	}

	public StopWatch getForwardSearchSW() {
		return forwardSearchSW;
	}

	public StopWatch getBackwardSearchSW() {
		return backwardSearchSW;
	}

	public int getForwardSettleNodeSize() {
		return forwardSettleNodeSize;
	}

	public int getNumberOfRegularSearches() {
		return numberOfRegularSearches;
	}

	public StopWatch getRegularSearchSW() {
		return regularSearchSW;
	}

	public int getGeneralCounter() {
		return generalCounter;
	}

	public StopWatch getGeneralSW() {
		return generalSW;
	}

	public int getForwardUnsettleNodeSize() {
		return forwardUnsettleNodeSize;
	}

}

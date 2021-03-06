package br.ufc.insightlab.graphast.serialization;

import br.ufc.insightlab.graphast.model.Edge;
import br.ufc.insightlab.graphast.model.Graph;
import br.ufc.insightlab.graphast.model.Node;
import br.ufc.insightlab.graphast.model.components.GraphComponent;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import de.javakaffee.kryoserializers.ArraysAsListSerializer;
import br.ufc.insightlab.graphast.structure.GraphStructure;

import java.io.*;
import java.util.Arrays;

public class KryoSerializer extends GraphSerializer {
	
	private static GraphSerializer instance = null;
	private Kryo kryo;
	
	private KryoSerializer() {
		kryo = new Kryo();
		kryo.register( Arrays.asList( "" ).getClass(), new ArraysAsListSerializer() );
	}
	
	/**
	 * @return the current GraphStorage's instance.
	 */
	public static GraphSerializer getInstance() {
		if (instance == null) 
			instance = new KryoSerializer();
		
		return instance;
	}

	@Override
	public Graph load(String path, GraphStructure structure) throws FileNotFoundException {
		long startTime = System.currentTimeMillis();
		Graph g = new Graph(structure);
		
		String directory = SerializationUtils.ensureDirectory(path);
		Input in;
		
		File f = new File(directory);
		
		boolean graphExists = f.exists();
		
		if (!graphExists) 
			f.mkdirs();
		
		try {
			
			in = new Input(new FileInputStream(directory + "nodes.phast"));
			while(!in.eof())
				g.addNode(kryo.readObject(in, Node.class));
			in.close();
			
			in = new Input(new FileInputStream(directory + "edges.phast"));
			while(!in.eof())
				g.addEdge(kryo.readObject(in, Edge.class));
			in.close();
			
			in = new Input(new FileInputStream(directory + "graph_components.phast"));
			while(!in.eof())
				g.addComponent(kryo.readObject(in, GraphComponent.class));
			in.close();
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		long totalTime = System.currentTimeMillis() - startTime;
		
		System.out.println("Time to load using kryo: " + (totalTime/1000.) + " s");
		
		return g;
	}

	@Override
	public void save(String path, Graph graph) {
		String directory = SerializationUtils.ensureDirectory(path);
		Output out;
		
		File f = new File(directory);
		
		boolean graphExists = f.exists();
		
		if (!graphExists) 
			f.mkdirs();
		
		try {
			out = new Output(new FileOutputStream(directory + "nodes.phast"));
			for (Node n : graph.getNodes())
				kryo.writeObject(out, n);
			out.close();
			out = new Output(new FileOutputStream(directory + "edges.phast"));
			for (Edge e : graph.getEdges())
				kryo.writeObject(out, e);
			out.close();
			out = new Output(new FileOutputStream(directory + "graph_components.phast"));
			for (GraphComponent component : graph.getAllComponents())
				kryo.writeObject(out, component);
			out.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}

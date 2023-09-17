package es.urjc.etsii.grafo.BIMP.model;

import es.urjc.etsii.grafo.io.Instance;
import es.urjc.etsii.grafo.util.random.RandomManager;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.*;
import java.util.logging.Logger;
import java.util.random.RandomGenerator;
import java.util.stream.Stream;
import org.apache.commons.math3.random.MersenneTwister;

public class BIMPInstance extends Instance {

    private static Logger log = Logger.getLogger(BIMPInstance.class.getName());
    private HashMap<Integer, HashSet<Integer>> graph;
    private HashMap<Integer, Integer> graphDegreePerNode;
    private HashMap<Integer, Integer> totalNeigs;
    private int nodes,edges, budget;
    private boolean isDirected;
    private Set<Integer> s = new LinkedHashSet<>();
    private Map<Integer,Integer> changeNodeID = new HashMap<>();
    private Map<Integer,Integer> changeNodeIDReverse = new HashMap<>();
    private int cost[];
    private double trivalency[];
    private List<List<Integer>> neig;

    public BIMPInstance(String name, BufferedReader br){
        super(name);
        this.graph = new HashMap<>();
        this.graphDegreePerNode = new HashMap<>();
        this.totalNeigs = new HashMap<>();
        readInstance(br);
    }

    public void readInstance(BufferedReader br) {
        int nodeCnt = 0; //start by 0 new graph
        String line;
        try{
            line = br.readLine();
            String[] numbers = line.split("\t");
            this.nodes = Integer.parseInt(numbers[0]);
            this.edges = Integer.parseInt(numbers[1]);
            this.budget = Integer.parseInt(numbers[2]);
            this.isDirected = Boolean.parseBoolean(numbers[3]);
            this.trivalency = new double[nodes];
            double probability[] = {0.1, 0.01, 0.001}; //TV
            MersenneTwister rand = new MersenneTwister();
            for(int i=0; i<nodes;i++){
                int rnd = rand.nextInt(3); //TV
                this.trivalency[i] =  probability[rnd];
            }
            cost = new int[nodes];
            for(int i = 0; i<edges; i++) {
                line = br.readLine();
                numbers = line.split("\t");
                int from = Integer.parseInt(numbers[0]);
                int to = Integer.parseInt(numbers[1]);

                nodeCnt = modifyNodeID(nodeCnt, from);
                from = changeNodeID.get(from);
                nodeCnt = modifyNodeID(nodeCnt, to);
                to = changeNodeID.get(to);

                setDegreePerNode(from);
                addToGraph(from, to);
                if(!isDirected){
                    addToGraph(to, from);
                    setDegreePerNode(to);
                }
                s.add(from);
                s.add(to);
            }
            for(int i = 0; i<nodes; i++) {
                line = br.readLine();
                cost[i] = Integer.parseInt(line);
            }
        }
        catch (IOException e){
            e.printStackTrace();
        }
        neig = new ArrayList<List<Integer>>(nodes);
        for(int i=0;i<nodes;i++){
            neig.add(new ArrayList<Integer>(graph.get(i).size()));
        }
        for(Integer s: graph.keySet()){
            int cnt = 0;
            for(Integer node: graph.get(s)){
                neig.get(s).add(node);
                cnt+=1;
            }
            totalNeigs.put(s,cnt);
        }
    }

    private void setDegreePerNode(int to) {
        if(graphDegreePerNode.get(to)==null){
            graphDegreePerNode.put(to,1);
        }
        else{
            int total = graphDegreePerNode.get(to);
            graphDegreePerNode.put(to,total+1);
        }
    }

    public Stream<Map.Entry<Integer,Integer>> getNodeDegree(){
        return graphDegreePerNode.entrySet().stream()
                .sorted((k1, k2) -> -k1.getValue().compareTo(k2.getValue()));
    }

    public List<Map.Entry<Integer,Integer>> getNodeDegreePrueba(){
        return graphDegreePerNode.entrySet().stream()
                .sorted((k1, k2) -> -k1.getValue().compareTo(k2.getValue())).toList();
    }

    private int modifyNodeID(int nodeCnt, int to) {
        if (changeNodeID.get(to) == null) {
            changeNodeID.put(to, nodeCnt);
            changeNodeIDReverse.put(nodeCnt, to);
            nodeCnt++;
        }
        return nodeCnt;
    }

    private void addToGraph(int from, int to) {
        if (graph.get(from) == null) {
            HashSet<Integer> nl = new HashSet<>();
            nl.add(to);
            graph.put(from, nl);

        } else {
            graph.get(from).add(to);
        }
    }

    public int getNumNodes() {
        return s.size();
    }

    public int getEdges() {
        return edges;
    }

    public Set<Integer> getNodes(){
        return s;
    }

    public HashMap<Integer,HashSet<Integer>> getGraph(){
        return graph;
    }

    public int convertNodeToReal(int node){
        return changeNodeIDReverse.get(node);
    }

    public int getBudget() {
        return budget;
    }

    public int getNeighboorsArray(int node, int cnt) {
        return neig.get(node).get(cnt);
    }

    public int totalNeighs(int node){
        return totalNeigs.get(node);
    }
    public int getCost(int node) {
        return cost[node];
    }

    public double[] getTrivalency() {
        return trivalency;
    }

    public HashMap<Integer, Integer> getGraphDegreePerNode() {
        return graphDegreePerNode;
    }

    // Override compareTo if the default ordering (by instance file name) is not correct
}

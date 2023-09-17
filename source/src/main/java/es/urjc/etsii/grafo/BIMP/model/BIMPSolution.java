package es.urjc.etsii.grafo.BIMP.model;

import es.urjc.etsii.grafo.BIMP.improve.LSImproverKElementsFaster;
import es.urjc.etsii.grafo.solution.Solution;
import es.urjc.etsii.grafo.util.random.RandomManager;

import java.util.*;
import java.util.random.RandomGenerator;

public class BIMPSolution extends Solution<BIMPSolution, BIMPInstance> {

    /**
     * Initialize solution from instance
     * @param ins
     */
    private int nodes,edges,mc,activeNodes,budget,evaluationType;
    private boolean evaluate;
    private double p,spread;
    private BIMPInstance instance;
    private Double mark;
    private HashSet<Integer> selectedNodes;
    private RandomGenerator rand;
    int addedNodes[],removedNodes[];
    int cntAddedNodes,cntRemovedNodes,cntAddedNodesCandidates,cntRemovedNodesCandidates;
    LSImproverKElementsFaster.Candidate addedNodesCandidates[],removedNodesCandidates[];
    boolean A[],A_stored[];
    int new_active[];
    int new_ones[];

    public BIMPSolution(BIMPInstance ins) {
        super(ins);
        addedNodes = new int[nodes];
        removedNodes = new int[nodes];
        addedNodesCandidates = new LSImproverKElementsFaster.Candidate[nodes];
        removedNodesCandidates = new LSImproverKElementsFaster.Candidate[nodes];
        cntAddedNodes = 0;
        cntRemovedNodes = 0;
        cntAddedNodesCandidates = 0;
        cntRemovedNodesCandidates = 0;
        this.instance = ins;
        this.nodes = instance.getNumNodes();
        A = new boolean[nodes];
        A_stored = new boolean[nodes];
        new_active = new int[nodes];
        new_ones = new int[nodes];
        this.edges = instance.getEdges();
        this.evaluate = true;
        this.mark = null;
        this.mc = 100; //10000
        this.budget = ins.getBudget();
        this.activeNodes = 0;
        this.selectedNodes = new HashSet<>();
        //rand = new MersenneTwister();
        rand = RandomManager.getRandom();
    }

    @Override
    public BIMPSolution cloneSolution() {
        return new BIMPSolution(this);
    }

    @Override
    protected boolean _isBetterThan(BIMPSolution other) {
        // Example:
        // Minimize total cost, better solution has lower cost
        return this.getScore() > other.getScore();
    }

    /**
     * Clone constructor
     * @param sol Solution to clone
     */
    public BIMPSolution(BIMPSolution sol) {
        super(sol);
        // Copy solution data
        this.evaluationType = sol.evaluationType;
        this.instance = sol.instance;
        this.nodes = instance.getNumNodes();
        this.edges = instance.getEdges();
        this.evaluate = sol.isEvaluate();
        this.mark = sol.mark;
        this.selectedNodes = new HashSet<>(sol.getSelectedNodes());
        this.mc = sol.mc;
        this.p = sol.p;
        this.A = Arrays.copyOf(sol.A,nodes);
        this.A_stored = Arrays.copyOf(sol.A_stored,nodes);
        this.new_ones = Arrays.copyOf(sol.new_ones,nodes);
        this.addedNodesCandidates = Arrays.copyOf(sol.addedNodesCandidates,nodes);
        this.removedNodesCandidates = Arrays.copyOf(sol.removedNodesCandidates,nodes);
        this.addedNodes =  Arrays.copyOf(sol.addedNodes,nodes);
        this.removedNodes =  Arrays.copyOf(sol.removedNodes,nodes);
        this.new_active = Arrays.copyOf(sol.new_active,nodes);
        cntAddedNodes = sol.cntAddedNodes;
        cntRemovedNodes = sol.cntRemovedNodes;
        cntAddedNodesCandidates = sol.cntAddedNodesCandidates;
        cntRemovedNodesCandidates = sol.cntRemovedNodesCandidates;
        this.activeNodes = sol.activeNodes;
        this.spread = sol.spread ;
        this.rand = sol.rand;
        this.budget = sol.getBudget();
        if(!this.evaluate){
            this.mark = sol.getScore();
        }
    }

    public void copy(BIMPSolution sol){
        this.evaluationType = sol.evaluationType;
        this.instance = sol.instance;
        this.nodes = instance.getNumNodes();
        this.edges = instance.getEdges();
        this.evaluate = sol.isEvaluate();
        this.mark = sol.mark;
        this.selectedNodes = new HashSet<>(sol.getSelectedNodes());
        this.mc = sol.mc;
        this.p = sol.p;
        this.A = Arrays.copyOf(sol.A,nodes);
        this.A_stored = Arrays.copyOf(sol.A_stored,nodes);
        this.new_ones = Arrays.copyOf(sol.new_ones,nodes);
        this.addedNodesCandidates = Arrays.copyOf(sol.addedNodesCandidates,nodes);
        this.removedNodesCandidates = Arrays.copyOf(sol.removedNodesCandidates,nodes);
        this.addedNodes =  Arrays.copyOf(sol.addedNodes,nodes);
        this.removedNodes =  Arrays.copyOf(sol.removedNodes,nodes);
        this.new_active = Arrays.copyOf(sol.new_active,nodes);
        cntAddedNodes = sol.cntAddedNodes;
        cntRemovedNodes = sol.cntRemovedNodes;
        cntAddedNodesCandidates = sol.cntAddedNodesCandidates;
        cntRemovedNodesCandidates = sol.cntRemovedNodesCandidates;
        this.activeNodes = sol.activeNodes;
        this.spread = sol.spread ;
        this.rand = sol.rand;
        this.budget = sol.getBudget();
        if(!this.evaluate){
            this.mark = sol.getScore();
        }
    }

    public HashSet<Integer> getSelectedNodes() {
        return selectedNodes;
    }


    /**
     * Get the current solution score.
     * The difference between this method and recalculateScore is that
     * this result can be a property of the solution, or cached,
     * it does not have to be calculated each time this method is called
     * @return current solution score as double
     */
    @Override
    public double getScore() {
        if(evaluate) {
            evaluate = false;
            return this.mark = recalculateScore();
        }
        return this.mark;
    }

    /**
     * Recalculate solution score and validate current solution state
     * You must check that no constraints are broken, and that all costs are valid
     * The difference between this method and getScore is that we must recalculate the score from scratch,
     * without using any cache/shortcuts.
     * DO NOT UPDATE CACHES / MAKE SURE THIS METHOD DOES NOT HAVE SIDE EFFECTS
     * @return current solution score as double
     */
    @Override
    public double recalculateScore() {
        return this.mark = IndependentCascade(selectedNodes,p,mc);
    }

    /**
     * Generate a string representation of this solution. Used when printing progress to console,
     * show as minimal info as possible
     *
     * @return Small string representing the current solution (Example: id + score)
     */
    @Override
    public String toString() {
        return "BIMPSolution{" +
                "nodes=" + nodes +
                ", edges=" + edges +
                ", mc=" + mc +
                ", activeNodes=" + activeNodes +
                ", budget=" + budget +
                ", evaluate=" + evaluate +
                ", p=" + p +
                ", spread=" + spread +
                ", instance=" + instance +
                ", mark=" + mark +
                ", selectedNodes=" + selectedNodes +
                '}';
    }

    @SuppressWarnings("Duplicates")
    public Double IndependentCascade(HashSet<Integer>  S, double p, int mc){
        double sum = 0;
        for(int i=0; i<mc;i++) {
            int cnt_new_active = 0;
            int countAddsA = 0;
            Arrays.fill(A,false);
            Arrays.fill(A_stored,false);
            for(Integer node: S) {
                A[node] = true;
                A_stored[node]=true;
                new_active[cnt_new_active]=node;
                cnt_new_active+=1;
                countAddsA+=1;
            }
            while (cnt_new_active!=0) {
                int cnt_new_ones=0;
                //For each newly active node, find its neighbors that become activated
                for(int m = 0; m<cnt_new_active; m++) {
                    int node = new_active[m];
                    //Get random list of values [0,1]
                    for (int cnt=0;cnt<instance.totalNeighs(node);cnt+=1) {
                        int count = instance.getNeighboorsArray(node,cnt);
                        double v = rand.nextDouble();
                        if(evaluationType==1){
                            this.p = 0.1;
                        }else if(evaluationType==2){
                            this.p = 0.2;
                        }else if(evaluationType==3){
                            this.p = 1.0/(double)instance.totalNeighs(count);//WCM p(u,v)=1/indeg(v)
                        }else if(evaluationType==4){
                            this.p = instance.getTrivalency()[count];
                        }else{
                            System.out.println("Error, this evaluation type does not exist -> " + evaluationType);
                        }
                        if(v<=this.p) {
                            if(!A[count]) {
                                A[count] = true;
                                A_stored[count]=true;
                                new_ones[cnt_new_ones]=count;
                                cnt_new_ones+=1;
                                countAddsA+=1;
                            }
                        }
                    }
                }
                cnt_new_active=cnt_new_ones;
                for(int j=0;j<cnt_new_ones;j++) {
                    new_active[j]=new_ones[j];
                }
            }
            sum+=countAddsA;
        }
        this.spread =  sum/(double)mc;
        this.activeNodes = (int)this.spread;
        return this.spread;
    }

    public Double IndependentCascadeNode(int addNode, int mc){
        HashSet<Integer> setFalse = new HashSet<>();
        double sum = 0;
        if(A[addNode])
            return sum;
        for(int i=0; i<mc;i++) {
            int cnt_new_active = 0;
            int countAddsA = 0;
            A_stored[addNode]=true;
            setFalse.add(addNode);
            new_active[cnt_new_active]=addNode;
            cnt_new_active+=1;
            countAddsA+=1;
            while (cnt_new_active!=0) {
                int cnt_new_ones=0;
                for(int m = 0; m<cnt_new_active; m++) {
                    int node = new_active[m];
                    //Get random list of values [0,1]
                    for (int cnt=0;cnt<instance.totalNeighs(node);cnt+=1) {
                        int count = instance.getNeighboorsArray(node,cnt);
                        double v = rand.nextDouble();
                        //double v = 0;
                        if(evaluationType==1){
                            this.p = 0.1;
                        }else if(evaluationType==2){
                            this.p = 0.2;
                        }else if(evaluationType==3){
                            this.p = 1.0/(double)instance.totalNeighs(count);//WCM p(u,v)=1/indeg(v)
                        }else if(evaluationType==4){
                            this.p = instance.getTrivalency()[count];
                        }else{
                            System.out.println("Error, this evaluation type does not exist -> " + evaluationType);
                        }
                        if(v<=this.p) {
                            if(!A_stored[count]) {
                                A_stored[count]=true;
                                setFalse.add(addNode);
                                new_ones[cnt_new_ones]=count;
                                cnt_new_ones+=1;
                                countAddsA+=1;
                            }
                        }
                    }
                }
                cnt_new_active=cnt_new_ones;
                for(int j=0;j<cnt_new_ones;j++) {
                    new_active[j]=new_ones[j];
                }
            }
            sum+=countAddsA;
        }
        for(Integer i:setFalse)
            A_stored[i]=false;
        return sum/(double)mc;
    }

    public void addNode(int node){
        selectedNodes.add(node);
        evaluate = true;
        budget-=instance.getCost(node);
    }

    public boolean isFeasibleAddNode(int node){
        return !selectedNodes.contains(node) && (budget-instance.getCost(node))>=0;
    }

    public void dropNode(int node){
        selectedNodes.remove(node);
        evaluate = true;
        budget+=instance.getCost(node);
    }
    public int getNodes() {
        return nodes;
    }
    public BIMPInstance getInstance() {
        return instance;
    }
    public int getBudget() { return budget; }

    public boolean isEvaluate() {
        return evaluate;
    }

    public void setMc(int mc) {
        if(this.mc!=mc) {
            this.mc = mc;
            evaluate = true;
        }
    }

    public void constructGreedy() {
        boolean isPossible = true;
        while(isPossible){
            isPossible=false;
            int totalSize = selectedNodes.size();
            for (Map.Entry<Integer, Integer> p : instance.getNodeDegreePrueba()) {
                if(this.isFeasibleAddNode(p.getKey())) {
                    this.addNode(p.getKey());
                }
                if(this.getBudget()<50) {
                    break;
                }
            }
            if(totalSize!=this.selectedNodes.size())
                isPossible=true;
        }
    }

    public String printSolutions(){
        ArrayList<Integer> ordered = new ArrayList<>();
        for(int s:selectedNodes)
            ordered.add(s);
        String res = "";
        Collections.sort(ordered);
        for(int s:ordered)
            res+=instance.convertNodeToReal(s) + " ";
        System.out.println(res);
        return res;
    }

    public int[] getAddedNodes() {
        return addedNodes;
    }

    public int[] getRemovedNodes() {
        return removedNodes;
    }

    public int getCntAddedNodes() {
        return cntAddedNodes;
    }

    public void setCntAddedNodes(int cntAddedNodes) {
        this.cntAddedNodes = cntAddedNodes;
    }

    public int getCntRemovedNodes() {
        return cntRemovedNodes;
    }

    public void setCntRemovedNodes(int cntRemovedNodes) {
        this.cntRemovedNodes = cntRemovedNodes;
    }

    public int getCntAddedNodesCandidates() {
        return cntAddedNodesCandidates;
    }

    public void setCntAddedNodesCandidates(int cntAddedNodesCandidates) {
        this.cntAddedNodesCandidates = cntAddedNodesCandidates;
    }

    public int getCntRemovedNodesCandidates() {
        return cntRemovedNodesCandidates;
    }

    public void setCntRemovedNodesCandidates(int cntRemovedNodesCandidates) {
        this.cntRemovedNodesCandidates = cntRemovedNodesCandidates;
    }

    public LSImproverKElementsFaster.Candidate[] getAddedNodesCandidates() {
        return addedNodesCandidates;
    }

    public LSImproverKElementsFaster.Candidate[] getRemovedNodesCandidates() {
        return removedNodesCandidates;
    }

    public void setEvaluation(int evaluationType) {
        this.evaluationType = evaluationType;
    }

}

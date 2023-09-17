package es.urjc.etsii.grafo.BIMP.improve;

import es.urjc.etsii.grafo.BIMP.model.BIMPInstance;
import es.urjc.etsii.grafo.BIMP.model.BIMPSolution;
import es.urjc.etsii.grafo.solver.improve.Improver;
import es.urjc.etsii.grafo.util.CollectionUtil;

import java.util.*;

public class LSImproverKElementsFaster extends Improver<BIMPSolution,BIMPInstance> {

    private int mc,n, iterations;
    private double percentage;
    private boolean improvement;

    public LSImproverKElementsFaster(int mc, int n){
        this.mc = mc;
        this.n = n;
        percentage = 0.25;
    }

    public LSImproverKElementsFaster(int mc, int n, double percentage){
        this.mc = mc;
        this.n = n;
        this.percentage = percentage;
    }

    public LSImproverKElementsFaster(int mc, int n, double percentage, int iterations){
        this.mc = mc;
        this.n = n;
        this.percentage = percentage;
        this.iterations = iterations;
    }

    public class Candidate implements Comparable<LSImproverKElementsFaster.Candidate>{
        private int node;
        private int value;
        Candidate(int node,int value){
            this.node = node;
            this.value = value;
        }
        public int getNode() {
            return node;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof LSImproverKElementsFaster.Candidate)) return false;
            LSImproverKElementsFaster.Candidate candidate = (LSImproverKElementsFaster.Candidate) o;
            return node == candidate.node &&
                    Integer.compare(candidate.value, value) == 0;
        }

        @Override
        public int hashCode() {
            return Objects.hash(node, value);
        }

        public int compareTo(LSImproverKElementsFaster.Candidate o) {
            return -Integer.compare(this.value,o.value);
        }
    }

    @Override
    protected BIMPSolution _improve(BIMPSolution bimpSolution) {
        ArrayList<Candidate> selectedNodes = new ArrayList<>();
        ArrayList<Candidate> candidates = new ArrayList<>();
        setCandidatesNodes(bimpSolution, selectedNodes, candidates);
        bimpSolution.setMc(100);
        Collections.sort(candidates);
        int scorePrevious = (int)bimpSolution.getScore();
        firstImprovement(selectedNodes,candidates,bimpSolution, n);
        bimpSolution.constructGreedy();
        int scoreBefore = (int)bimpSolution.getScore();
        if(scorePrevious!=scoreBefore) {
            improvement = true;
        }
        return bimpSolution;
    }

    private void setCandidatesNodes(BIMPSolution bimpSolution, ArrayList<Candidate> selectedNodes, ArrayList<Candidate> candidates) {
        int cnt = 0;
        int maxGrade = 0;
        for(Integer node: bimpSolution.getInstance().getNodes()) {
            int grade = bimpSolution.getInstance().getGraphDegreePerNode().get(node);
            cnt+=grade;
            maxGrade = Math.max(maxGrade,grade);
        }
        cnt = (int)(maxGrade*percentage);
        double currentScore = bimpSolution.getScore();
        bimpSolution.setMc(1);
        for(Integer node: bimpSolution.getInstance().getNodes()) {
            int grade = bimpSolution.getInstance().getGraphDegreePerNode().get(node);
            if (bimpSolution.getSelectedNodes().contains(node)) {
                bimpSolution.dropNode(node);
                double sum = currentScore-bimpSolution.getScore();
                bimpSolution.addNode(node);
                selectedNodes.add(new Candidate(node,(int)sum));
            } else if (grade>=cnt){
                double sum = bimpSolution.IndependentCascadeNode(node,1);
                candidates.add(new Candidate(node,(int)sum));
            }
        }
    }


    private void firstImprovement(ArrayList<Candidate> selectedNodes, ArrayList<Candidate> candidates, BIMPSolution bimpSolution, int remove) {
        double mark = bimpSolution.getScore();
        BIMPSolution best = new BIMPSolution(bimpSolution);
        BIMPSolution bestOfBests = new BIMPSolution(bimpSolution);
        best.setMc(mc);
        int iters = 0;
        boolean improve = true;
        while(improve){
            improve = false;
            for(int i=0; i<selectedNodes.size() && !improve && iters<=iterations;i+=remove){
                iters++;
                best.setCntAddedNodes(0);
                best.setCntAddedNodesCandidates(0);
                best.setCntRemovedNodes(0);
                best.setCntRemovedNodesCandidates(0);
                removeRNodes(selectedNodes, remove, best, i);
                AddNNodes(candidates, best);
                //Evaluate
                double curMark = best.getScore();
                if(Double.compare(mark,curMark)<0){
                    mark = curMark;
                    improve=true;
                    bestOfBests.copy(best);
                    modifyVectors(selectedNodes, candidates, best);
                }else{
                    for(int x = 0; x<bimpSolution.getCntAddedNodes(); x++) {
                        best.dropNode(bimpSolution.getAddedNodes()[x]);
                    }
                    for(int x = 0; x<bimpSolution.getCntRemovedNodes(); x++) {
                        best.addNode(bimpSolution.getRemovedNodes()[x]);
                    }
                }
            }
        }
        bestOfBests.setMc(100);
        if(Double.compare(bimpSolution.getScore(),bestOfBests.getScore())<0) {
            bimpSolution.copy(bestOfBests);
            bimpSolution.updateLastModifiedTime();
        }
    }

    private void AddNNodes(ArrayList<Candidate> candidates, BIMPSolution best) {
        for(int l = 0; l< candidates.size(); l++){
            int elem = candidates.get(l).getNode();
            if(best.isFeasibleAddNode(elem)) {
                best.addNode(elem);
                int indexAddedNodes = best.getCntAddedNodes();
                best.getAddedNodes()[indexAddedNodes] = elem;
                best.setCntAddedNodes(indexAddedNodes+1);
                int indexAddedCandidates = best.getCntAddedNodesCandidates();
                best.getAddedNodesCandidates()[indexAddedCandidates] = candidates.get(l);
                best.setCntAddedNodesCandidates(indexAddedCandidates+1);
            }
            if(best.getBudget()<50) {
                break;
            }
        }
    }

    private void removeRNodes(ArrayList<Candidate> selectedNodes, int remove, BIMPSolution best, int i) {
        for(int r = i; r< selectedNodes.size() && (i + remove)>r; r++) {
            int deleteNode = selectedNodes.get(r).getNode();
            best.dropNode(deleteNode);
            int indexRemovedNodes = best.getCntRemovedNodes();
            best.getRemovedNodes()[indexRemovedNodes] = deleteNode;
            best.setCntRemovedNodes(indexRemovedNodes+1);
            int indexRemovedCandidates = best.getCntRemovedNodesCandidates();
            best.getRemovedNodesCandidates()[indexRemovedCandidates] = selectedNodes.get(r);
            best.setCntRemovedNodesCandidates(indexRemovedCandidates+1);
        }
    }

    private void modifyVectors(ArrayList<Candidate> selectedNodes, ArrayList<Candidate> candidates, BIMPSolution best) {
        for(int x = 0; x< best.getCntAddedNodes(); x++) {
            candidates.remove((Integer) best.getAddedNodes()[x]);
        }
        for(int x = 0; x< best.getCntRemovedNodes(); x++){
            selectedNodes.remove((Integer) best.getRemovedNodes()[x]);
        }
        for(int x = 0; x< best.getCntAddedNodesCandidates(); x++) {
            selectedNodes.add(best.getAddedNodesCandidates()[x]);
        }
        for(int x = 0; x< best.getCntRemovedNodesCandidates(); x++){
            candidates.add(best.getRemovedNodesCandidates()[x]);
        }
    }
}

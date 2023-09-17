package es.urjc.etsii.grafo.BIMP.constructives;

import es.urjc.etsii.grafo.BIMP.model.BIMPInstance;
import es.urjc.etsii.grafo.BIMP.model.BIMPSolution;
import es.urjc.etsii.grafo.solver.create.Constructive;
import es.urjc.etsii.grafo.util.random.RandomManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class GRASPConstructiveByHeuristicFor extends Constructive<BIMPSolution, BIMPInstance> {

    private Double alpha;
    private int penalization,fo,iter;
    public GRASPConstructiveByHeuristicFor(double alpha, int penalization, int fo, int iter){
        this.alpha = alpha;
        this.penalization = penalization;
        this.fo = fo;
        this.iter = iter;
    }

    class Candidate{
        private int node;
        private double value;
        Candidate(int node,double value){
            this.node = node;
            this.value = value;
        }
        public int getNode() {
            return node;
        }

        public double getValue() {
            return value;
        }

        public void setValue(double value) {
            this.value = value;
        }
    }

    public BIMPSolution construct(BIMPSolution emptySolution) {
        emptySolution.setEvaluation(fo);
        BIMPSolution bestSolution = new BIMPSolution(emptySolution);
        BIMPSolution bimpSolution = new BIMPSolution(emptySolution);
        List<Candidate> CL = new ArrayList<>();
        List<Candidate> CL2 = new ArrayList<>();
        createCL(bimpSolution, CL2);
        for(int i=0;i<iter;i++) {
            bimpSolution.copy(emptySolution);
            double realAlpha;
            if (this.alpha == -1d) {
                realAlpha = RandomManager.getRandom().nextDouble();
            } else realAlpha = alpha;
            CL.addAll(CL2);
            while (CL.size() != 0) {
                double gmin = CL.get(CL.size() - 1).value;
                double gmax = CL.get(0).value;
                double umax = gmax - realAlpha * (gmax - gmin);
                int limit = 0;
                while (limit < CL.size() && CL.get(limit).value >= umax) {
                    limit++;
                }
                int selectedNode = removeInfeasibles(bimpSolution, CL, limit);
                updateCL(bimpSolution, CL, selectedNode);
            }
            bimpSolution.getScore();
            if(bimpSolution.getScore()>bestSolution.getScore()) {
                bestSolution.copy(bimpSolution);
                bestSolution.updateLastModifiedTime();
            }
        }
        bestSolution.printSolutions();
        return bestSolution;
    }

    private int removeInfeasibles(BIMPSolution bimpSolution, List<Candidate> CL, int limit){
            int randomIndex = RandomManager.getRandom().nextInt(limit);
            int selectedNode = CL.get(randomIndex).getNode();
            while (!bimpSolution.isFeasibleAddNode(selectedNode)) {
                CL.remove(randomIndex);
                int newLimit = Math.min(CL.size(), limit);
                if (newLimit == 0)
                    break;
                randomIndex = RandomManager.getRandom().nextInt(newLimit);
                selectedNode = CL.get(randomIndex).getNode();
            }
            if (bimpSolution.isFeasibleAddNode(selectedNode)) {
                bimpSolution.addNode(selectedNode);
                CL.remove(randomIndex);
            }
        return selectedNode;
    }

    private void createCL(BIMPSolution bimpSolution, List<Candidate> CL) {
        for (int node = 0; node< bimpSolution.getNodes();node++) {
            if (bimpSolution.isFeasibleAddNode(node)) {
                double sum = bimpSolution.getInstance().getGraph().get(node).size();
                Candidate addCandidate = new Candidate(node, sum);
                CL.add(addCandidate);
            }
        }
        CL.sort((c1, c2) -> -Double.compare(c1.value, c2.value));
    }

    private void updateCL(BIMPSolution bimpSolution, List<Candidate> CL, int selected) {
        for (Candidate node : CL) {
            if(bimpSolution.getInstance().getGraph().get(selected).contains(node.getNode()))
                node.setValue(node.getValue()/penalization);
        }
        CL.sort((c1, c2) -> -Double.compare(c1.value, c2.value));
    }

    @Override
    public String toString() {
        return "GRASPConstructiveByHeuristicFor{" +
                "alpha=" + alpha +
                "fo=" + fo +
                "iter=" + iter +
                '}';
    }
}


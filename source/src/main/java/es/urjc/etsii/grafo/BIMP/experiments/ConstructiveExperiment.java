package es.urjc.etsii.grafo.BIMP.experiments;

import es.urjc.etsii.grafo.BIMP.constructives.*;
import es.urjc.etsii.grafo.BIMP.improve.LSImproverKElementsFaster;
import es.urjc.etsii.grafo.BIMP.model.BIMPInstance;
import es.urjc.etsii.grafo.BIMP.model.BIMPSolution;
import es.urjc.etsii.grafo.solver.algorithms.Algorithm;
import es.urjc.etsii.grafo.solver.algorithms.SimpleAlgorithm;
import es.urjc.etsii.grafo.solver.services.AbstractExperiment;
import org.graalvm.nativeimage.IsolateThread;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.ArrayList;
import java.util.List;

public class ConstructiveExperiment extends AbstractExperiment<BIMPSolution, BIMPInstance> {

    public ConstructiveExperiment(@Value("${solver.maximizing}") boolean maximizing) {
        super(maximizing);
    }

    @Value("${algorithm.alpha}")
    private Double alpha;
    @Value("${algorithm.penalization}")
    private Integer penalization;
    @Value("${algorithm.GRASPIterations}")
    private Integer GRASPIterations;
    @Value("${algorithm.FO}")
    private Integer FO;
    @Value("${algorithm.percentage}")
    private Double percentage;
    @Value("${algorithm.LSIterations}")
    private Integer LSIterations;
    @Value("${algorithm.MCLS}")
    private Integer MCLS;
    @Value("${algorithm.proyectName}")
    private String proyectName;


    @Override
    public List<Algorithm<BIMPSolution, BIMPInstance>> getAlgorithms() {
        // In this experiment we will compare a random constructive with several GRASP constructive configurations
        boolean maximizing = super.isMaximizing();
        var algorithms = new ArrayList<Algorithm<BIMPSolution, BIMPInstance>>();
        System.out.println("Parameters Setup");
        System.out.println("Proyect Name: " + proyectName);
        System.out.println("Alpha: " + alpha);
        System.out.println("Penalization: " + penalization);
        System.out.println("GRASP Iterations: " + GRASPIterations);
        System.out.println("FO: " + FO);
        System.out.println("Degree top percentage: " + percentage);
        System.out.println("Local Search Iterations: " + LSIterations);
        System.out.println("Monte Carlo Iterations in Local Search: " + MCLS);
        algorithms.add(new SimpleAlgorithm<>(proyectName, new GRASPConstructiveByHeuristicFor(alpha, penalization, FO, GRASPIterations), new LSImproverKElementsFaster(MCLS, 1, percentage, LSIterations)));

        return algorithms;

    }
}

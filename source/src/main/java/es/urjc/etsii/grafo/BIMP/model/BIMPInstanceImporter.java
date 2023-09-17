package es.urjc.etsii.grafo.BIMP.model;

import es.urjc.etsii.grafo.io.InstanceImporter;

import java.io.BufferedReader;
import java.io.IOException;

public class BIMPInstanceImporter extends InstanceImporter<BIMPInstance> {

    @Override
    public BIMPInstance importInstance(BufferedReader reader, String filename) throws IOException {
        // Create and return instance object from file data

        var instance = new BIMPInstance(filename,reader);

        // IMPORTANT! Remember that instance data must be immutable from this point
        return instance;
    }
}

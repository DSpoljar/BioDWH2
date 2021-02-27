package de.unibi.agbi.biodwh2.disgenet.etl;

import de.unibi.agbi.biodwh2.core.Workspace;
import de.unibi.agbi.biodwh2.core.etl.GraphExporter;
import de.unibi.agbi.biodwh2.core.exceptions.ExporterException;
import de.unibi.agbi.biodwh2.core.exceptions.ExporterFormatException;
import de.unibi.agbi.biodwh2.core.io.obo.OboEntry;
import de.unibi.agbi.biodwh2.core.io.obo.OboReader;
import de.unibi.agbi.biodwh2.core.model.graph.Graph;
import de.unibi.agbi.biodwh2.core.model.graph.Node;
import de.unibi.agbi.biodwh2.disgenet.DisGeNetDataSource;
import de.unibi.agbi.biodwh2.disgenet.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DisGeNetGraphExporter extends GraphExporter<DisGeNetDataSource>
{
    private static final Logger LOGGER = LoggerFactory.getLogger(DisGeNetGraphExporter.class);


    public DisGeNetGraphExporter(final DisGeNetDataSource dataSource)
    {
        super(dataSource);


    }

    // Still WIP.
    @Override
    protected boolean exportGraph(final Workspace workspace, final Graph graph) {
        graph.setNodeIndexPropertyKeys("geneID", "geneSymbol");

        return true;
    }

    // Splitting different nodes and adding their respective properties (entries provisional):

    private void exportGeneNode(final Graph graph, DisGeNetModel entry) {

        Node geneNode = createNode(graph, "geneId");
        geneNode.setProperty("geneId", entry.geneID);
        geneNode.setProperty("geneSymbol", entry.geneSymbol);
        graph.update(geneNode);
    }

    private void exportDiseaseNode(final Graph graph, DisGeNetModel entry) {

        Node diseaseNode = createNode(graph, "diseaseId");
        diseaseNode.setProperty("diseaseId", entry.diseaseID);
        diseaseNode.setProperty("diseaseName", entry.diseaseName);
        diseaseNode.setProperty("diseaseType", entry.diseaseType);
        diseaseNode.setProperty("diseaseClass", entry.diseaseClass);
        diseaseNode.setProperty("diseaseSemanticType", entry.diseaseSemanticType);
        graph.update(diseaseNode);
    }




}

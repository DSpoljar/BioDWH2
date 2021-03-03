package de.unibi.agbi.biodwh2.disgenet.etl;

import com.fasterxml.jackson.databind.MappingIterator;
import de.unibi.agbi.biodwh2.core.Workspace;
import de.unibi.agbi.biodwh2.core.etl.GraphExporter;
import de.unibi.agbi.biodwh2.core.exceptions.ExporterException;
import de.unibi.agbi.biodwh2.core.exceptions.ExporterFormatException;
import de.unibi.agbi.biodwh2.core.io.FileUtils;
import de.unibi.agbi.biodwh2.core.io.obo.OboEntry;
import de.unibi.agbi.biodwh2.core.io.obo.OboReader;
import de.unibi.agbi.biodwh2.core.model.graph.Graph;
import de.unibi.agbi.biodwh2.core.model.graph.Node;
import de.unibi.agbi.biodwh2.disgenet.DisGeNetDataSource;
import de.unibi.agbi.biodwh2.disgenet.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.FileReader;
import java.io.BufferedReader;
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
    protected boolean exportGraph(final Workspace workspace, final Graph graph)
    {
        graph.setNodeIndexPropertyKeys("GENE_ID", "DISEASE_ID");




        return true;

    }

    // Splitting different nodes and adding their respective properties (entries provisional):

    private Node createGeneNode(final Graph graph, DisGeNetModel entry)
    {
        if (graph.findNode(entry.geneID).isEmpty())
        {
            Node geneNode = createNode(graph, "Gene");
            geneNode.setProperty("ID", entry.geneID);
            geneNode.setProperty("GENE_SYMBOL", entry.geneSymbol);
            graph.update(geneNode);
            return  geneNode;
        }
        else
        {
           Node currentNode = graph.findNode(entry.geneID);
            return currentNode;
        }


    }

    private Node createDiseaseNode(final Graph graph, DisGeNetModel entry)
    {
        if (graph.findNode(entry.diseaseID).isEmpty())
        {
            Node diseaseNode = createNode(graph, "Disease");
            diseaseNode.setProperty("ID", entry.diseaseID);
            diseaseNode.setProperty("DISEASE_NAME", entry.diseaseName);
            diseaseNode.setProperty("DISEASE_TYPE", entry.diseaseType);
            diseaseNode.setProperty("DISEASE_CLASS", entry.diseaseClass);
            diseaseNode.setProperty("DISEASE_SEMANTICTYPE", entry.diseaseSemanticType);
            graph.update(diseaseNode);
            return diseaseNode;
        }
        else {

            Node currentNode = graph.findNode(entry.diseaseID);
            return currentNode;

        }


    }





}

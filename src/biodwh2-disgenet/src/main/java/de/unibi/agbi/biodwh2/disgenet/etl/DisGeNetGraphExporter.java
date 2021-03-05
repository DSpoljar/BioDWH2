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

        MappingIterator<DisGeNetModel> iterator = null;

        try
        {
            iterator = FileUtils.openGzipTsvWithHeader(workspace, dataSource, "curated_gene_disease_associations.tsv.gz", DisGeNetModel.class);

        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

        List<DisGeNetModel> rows = null;
        try
        {
            rows = iterator.readAll();

        } catch (IOException e)
        {
            e.printStackTrace();
        }

        for (DisGeNetModel row : rows)
        {
            //createGeneNode(graph, row);
            //createDiseaseNode(graph, row);
            // System.out.println("row is: "+row);

        }



        return true;

    }

    // Splitting different nodes and adding their respective properties (entries provisional):

    private Node createGeneNode(final Graph graph, DisGeNetModel entry)
    {
        Node geneNode = graph.findNode("Gene", "id", entry.geneID);

        if (geneNode == null)
        {
            geneNode = createNode(graph, "Gene");
            geneNode.setProperty("id", entry.geneID);
            geneNode.setProperty("gene_symbol", entry.geneSymbol);
            graph.update(geneNode);
            return  geneNode;
        }
        else
        {

           graph.update(geneNode);
           return geneNode;

        }


    }

    private Node createDiseaseNode(final Graph graph, DisGeNetModel entry)
    {
        Node diseaseNode = graph.findNode("Disease", "id", entry.geneID);

        if (diseaseNode == null)
        {
            diseaseNode = createNode(graph, "Disease");
            diseaseNode.setProperty("id", entry.diseaseID);
            diseaseNode.setProperty("disease_name", entry.diseaseName);
            diseaseNode.setProperty("disease_type", entry.diseaseType);
            diseaseNode.setProperty("disease_class", entry.diseaseClass);
            diseaseNode.setProperty("disease_semanticType", entry.diseaseSemanticType);
            graph.update(diseaseNode);
            return diseaseNode;
        }
        else
            {

            graph.update(diseaseNode);
            return diseaseNode;

          }


    }





}

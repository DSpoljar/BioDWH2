package de.unibi.agbi.biodwh2.disgenet.etl;

import com.fasterxml.jackson.databind.MappingIterator;
import de.unibi.agbi.biodwh2.core.Workspace;
import de.unibi.agbi.biodwh2.core.etl.GraphExporter;
import de.unibi.agbi.biodwh2.core.exceptions.ExporterException;
import de.unibi.agbi.biodwh2.core.exceptions.ExporterFormatException;
import de.unibi.agbi.biodwh2.core.io.FileUtils;
import de.unibi.agbi.biodwh2.core.io.obo.OboEntry;
import de.unibi.agbi.biodwh2.core.io.obo.OboReader;
import de.unibi.agbi.biodwh2.core.model.graph.Edge;
import de.unibi.agbi.biodwh2.core.model.graph.Graph;
import de.unibi.agbi.biodwh2.core.model.graph.Node;
import de.unibi.agbi.biodwh2.disgenet.DisGeNetDataSource;
import de.unibi.agbi.biodwh2.disgenet.model.*;
import org.apache.commons.lang3.StringUtils;
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
    private String[] tsvFiles = new String[4];


    public DisGeNetGraphExporter(final DisGeNetDataSource dataSource)
    {

        super(dataSource);

    }

    // Still WIP.
    @Override
    protected boolean exportGraph(final Workspace workspace, final Graph graph)
    {
        graph.setNodeIndexPropertyKeys("GENE_ID", "DISEASE_ID");

        // TSV-Files for Gene-Disease associations
        String[] tsvFiles_Genes = new String[4];
        tsvFiles_Genes[0] = "all_gene_disease_associations.tsv.gz";
        tsvFiles_Genes[1] = "all_gene_disease_pmid_associations.tsv.gz";
        tsvFiles_Genes[2] = "befree_gene_disease_associations.tsv.gz";
        tsvFiles_Genes[3] = "curated_gene_disease_associations.tsv.gz";

        String[] tsvFiles_Disease = new String[4];
        tsvFiles_Disease[0] = "all_variant_disease_associations.tsv.gz";
        tsvFiles_Disease[1] = "all_variant_disease_pmid_associations.tsv.gz";
        tsvFiles_Disease[2] = "befree_variant_disease_associations.tsv.gz";
        tsvFiles_Disease[3] = "curated_variant_disease_associations.tsv.gz";



        MappingIterator<DisGeNetModel> iterator = null;
        MappingIterator<DisGeNetModelVariantDisease> iterator_2 = null;

        int counter = 0;
        int counter_2 = 0;

        // Initializing temporary nodes for the edges.
        Node tempGeneNode = null;
        Node tempDiseaseNode = null;
        Node tempVariantDiseaseNode = null;

        for (int i = 0; i < tsvFiles_Genes.length; i++)
        {
            try
            {
                iterator = FileUtils.openGzipTsvWithHeader(workspace, dataSource, tsvFiles_Genes[i], DisGeNetModel.class);

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
                // Testing the mapping so a threshold for 10 nodes each is created temporarily.

                counter +=1;

                if (counter <= 20)
                {

                    tempGeneNode = createGeneNode(graph, row, row.geneID, row.geneSymbol);

                    tempDiseaseNode = createDiseaseNode(graph, row, row.diseaseID, row.diseaseName, row.diseaseType, row.diseaseClass,
                                                        row.diseaseSemanticType);

                    // add edges here
                    Edge edge = graph.addEdge(tempGeneNode, tempDiseaseNode, "ASSOCIATED_WITH");
                    edge.setProperty("score", row.score);


                    tempGeneNode = null;
                    tempDiseaseNode = null;

                }
                else
                {
                    break;
                }
        }






        }


        for (int j = 0; j < tsvFiles_Disease.length; j++) {

            try {
                iterator_2 = FileUtils.openGzipTsvWithHeader(workspace, dataSource, tsvFiles_Disease[j],
                                                           DisGeNetModelVariantDisease.class);

            } catch (IOException e) {
                e.printStackTrace();
            }

            List<DisGeNetModelVariantDisease> rows = null;
            try {
                rows = iterator_2.readAll();

            } catch (IOException e) {
                e.printStackTrace();
            }

            for (DisGeNetModelVariantDisease row : rows) {
                // Testing the mapping so a threshold for 10 nodes each is created temporarily.

                counter_2 += 1;

                if (counter_2 <= 20) {

                    // TODO: Adapt node structures to VariantDiseases
                    tempVariantDiseaseNode = createVariantDiseaseNode(graph, row, row.snpId, row.chromosome, row.position);


                    // TODO: Add proper edges and relationships between diseases and chromosomes (?)
                   // Edge edge = graph.addEdge(tempGeneNode, tempDiseaseNode, "ASSOCIATED_WITH");
                    //edge.setProperty("score", row.score);


                    tempVariantDiseaseNode = null;

                } else {
                    break;
                }

            }

        }


        return true;

    }

    // Splitting different nodes and adding their respective properties (entries provisional):

    private Node createGeneNode(final Graph graph, DisGeNetModel entry, String ID, String geneSymbol)
    {
        Node geneNode = graph.findNode("Gene", "id", entry.geneID);

        if (geneNode == null)
        {

            geneNode = createNode(graph, "Gene");
            geneNode.setProperty(ID, entry.geneID);
            geneNode.setProperty(geneSymbol, entry.geneSymbol);
            graph.update(geneNode);
            return  geneNode;
        }
        else
        {

           graph.update(geneNode);
           return geneNode;

        }


    }

    private Node createDiseaseNode(final Graph graph, DisGeNetModel entry, String id,  String name, String type, String disClass, String semanticType)
    {
        Node diseaseNode = graph.findNode("Disease", "id", entry.geneID);

        if (diseaseNode == null)
        {
            diseaseNode = createNode(graph, "Disease");
            diseaseNode.setProperty(id, entry.diseaseID);
            diseaseNode.setProperty(name, entry.diseaseName);
            diseaseNode.setProperty(type, entry.diseaseType);
            //diseaseNode.setProperty(disClass, entry.diseaseClass); Causes NullPointerException for some reason
            diseaseNode.setProperty(semanticType, entry.diseaseSemanticType);
            graph.update(diseaseNode);
            return diseaseNode;
        }
        else
            {

            graph.update(diseaseNode);
            return diseaseNode;

          }


    }

    private Node createVariantDiseaseNode(final Graph graph, DisGeNetModelVariantDisease entry, String ID, String chromosome, String position)
    {
        Node chromosomeDiseaseNode = graph.findNode("Disease", "id", entry.snpId);

        if (chromosomeDiseaseNode == null)
        {

            chromosomeDiseaseNode = createNode(graph, "Gene");
            chromosomeDiseaseNode.setProperty(ID, entry.snpId);
            chromosomeDiseaseNode.setProperty(chromosome, entry.chromosome);
            chromosomeDiseaseNode.setProperty(position, entry.position);
            graph.update(chromosomeDiseaseNode);
            return  chromosomeDiseaseNode;
        }
        else
        {

            graph.update(chromosomeDiseaseNode);
            return chromosomeDiseaseNode;

        }


    }




}

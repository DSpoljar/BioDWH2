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
        String[] tsvFiles_Genes = new String[2];
        tsvFiles_Genes[0] = "all_gene_disease_associations.tsv.gz";
       // tsvFiles_Genes[1] = "all_gene_disease_pmid_associations.tsv.gz";
       // tsvFiles_Genes[2] = "befree_gene_disease_associations.tsv.gz";
       // tsvFiles_Genes[3] = "curated_gene_disease_associations.tsv.gz";

        String[] tsvFiles_Disease = new String[2];
        tsvFiles_Disease[0] = "all_variant_disease_associations.tsv.gz";
        //tsvFiles_Disease[1] = "all_variant_disease_pmid_associations.tsv.gz";
       // tsvFiles_Disease[2] = "befree_variant_disease_associations.tsv.gz";
       // tsvFiles_Disease[3] = "curated_variant_disease_associations.tsv.gz";



        MappingIterator<DisGeNetModel> iterator = null;
        MappingIterator<DisGeNetModelVariantDisease> iterator_2 = null;

       // int counter = 0;
        //int counter_2 = 0;

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







                    tempGeneNode = createGeneNode(graph, row, row.geneID, row.geneSymbol);

                    tempDiseaseNode = createDiseaseNode(graph, row, row.diseaseID, row.diseaseName, row.diseaseType, row.diseaseClass,
                                                        row.diseaseSemanticType);

                    // add edges here
                    Edge edge = graph.addEdge(tempGeneNode, tempDiseaseNode, "ASSOCIATED_WITH");
                    edge.setProperty("score", row.score);

                System.out.println("Current geneID is: "+row.geneID);

                    tempGeneNode = null;
                    tempDiseaseNode = null;


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
                // Testing the mapping so a threshold for 50 nodes each is created temporarily.






                    tempVariantDiseaseNode = createVariantNode(graph, row, row.snpId, row.chromosome, row.position);
                    tempDiseaseNode =  tempDiseaseNode = createDiseaseNode_variant(graph, row, row.diseaseID, row.diseaseName, row.diseaseType, row.diseaseClass,
                                                                           row.diseaseSemanticType);



                    Edge edge = graph.addEdge(tempVariantDiseaseNode, tempDiseaseNode, "ASSOCIATED_WITH");
                    edge.setProperty("score", row.score);
                    System.out.println("Current row snpID is: "+row.snpId);

                    tempVariantDiseaseNode = null;
                    tempDiseaseNode = null;


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
            geneNode.setProperty("ID", entry.geneID);
            geneNode.setProperty("GeneSymbol", entry.geneSymbol);
            graph.update(geneNode);
            return  geneNode;
        }
        else
        {


           return geneNode;

        }


    }

    private Node createDiseaseNode(final Graph graph, DisGeNetModel entry, String id,  String name, String type, String disClass, String semanticType)
    {
        Node diseaseNode = graph.findNode("Disease", "id", entry.geneID);

        if (diseaseNode == null)
        {
            diseaseNode = createNode(graph, "Disease");
            diseaseNode.setProperty("ID", entry.diseaseID);
            diseaseNode.setProperty("Name", entry.diseaseName);
            diseaseNode.setProperty("Type", entry.diseaseType);
            diseaseNode.setProperty("Class", entry.diseaseClass);
            diseaseNode.setProperty("SemanticType", entry.diseaseSemanticType);
            graph.update(diseaseNode);
            return diseaseNode;
        }
        else
            {


            return diseaseNode;

          }


    }

    private Node createDiseaseNode_variant(final Graph graph, DisGeNetModelVariantDisease entry, String id,  String name, String type, String disClass, String semanticType)
    {
        Node diseaseNode = graph.findNode("Disease", "id", entry.diseaseID);

        if (diseaseNode == null)
        {
            diseaseNode = createNode(graph, "Disease");
            diseaseNode.setProperty("ID", entry.diseaseID);
            diseaseNode.setProperty("Name", entry.diseaseName);
            diseaseNode.setProperty("Type", entry.diseaseType);
            diseaseNode.setProperty("Class", entry.diseaseClass);
            diseaseNode.setProperty("SemanticType", entry.diseaseSemanticType);
            graph.update(diseaseNode);
            return diseaseNode;
        }
        else
        {


            return diseaseNode;

        }


    }

    private Node createVariantNode(final Graph graph, DisGeNetModelVariantDisease entry, String ID, String chromosome, String position)
    {
        Node variantDiseaseNode = graph.findNode("Variant", "id", entry.snpId); // Variant!

        if (variantDiseaseNode == null)
        {

            variantDiseaseNode = createNode(graph, "Variant");
            variantDiseaseNode.setProperty("snpId", entry.snpId);
            variantDiseaseNode.setProperty("chromosome", entry.chromosome);
            variantDiseaseNode.setProperty("position", entry.position);
            graph.update(variantDiseaseNode);
            return variantDiseaseNode;
        }
        else
        {


            return variantDiseaseNode;

        }


    }




}

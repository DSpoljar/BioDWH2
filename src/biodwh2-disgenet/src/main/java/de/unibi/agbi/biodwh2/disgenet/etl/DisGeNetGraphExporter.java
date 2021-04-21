package de.unibi.agbi.biodwh2.disgenet.etl;

import com.fasterxml.jackson.databind.MappingIterator;
import de.unibi.agbi.biodwh2.core.Workspace;
import de.unibi.agbi.biodwh2.core.etl.GraphExporter;
import de.unibi.agbi.biodwh2.core.io.FileUtils;
import de.unibi.agbi.biodwh2.core.model.graph.Graph;
import de.unibi.agbi.biodwh2.core.model.graph.Node;
import de.unibi.agbi.biodwh2.disgenet.DisGeNetDataSource;
import de.unibi.agbi.biodwh2.disgenet.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class DisGeNetGraphExporter extends GraphExporter<DisGeNetDataSource> {
    private static final Logger LOGGER = LoggerFactory.getLogger(DisGeNetGraphExporter.class);

    public DisGeNetGraphExporter(final DisGeNetDataSource dataSource) {
        super(dataSource);
    }


    @Override
    protected boolean exportGraph(final Workspace workspace, final Graph graph) {

        graph.setNodeIndexPropertyKeys("id");
        
        // TSV-Files for Gene-Disease associations
        final String[] geneDiseaseFiles = new String[]{
                //"all_gene_disease_associations.tsv.gz"
                //"all_gene_disease_pmid_associations.tsv.gz"
                //"befree_gene_disease_associations.tsv.gz"
                "curated_gene_disease_associations.tsv.gz"
        };
        final String[] variantDiseaseFiles = new String[]{
                //"all_variant_disease_associations.tsv.gz"
                //"all_variant_disease_pmid_associations.tsv.gz"
                //"befree_variant_disease_associations.tsv.gz"
                "curated_variant_disease_associations.tsv.gz"
        };

        final String[] diseaseDiseaseFiles = new String[]{
              "disease_to_disease_CURATED.tsv.gz"
              // disease_to_disease_ALL.tsv.gz (No permission to access anyways)
        };

        int counter = 0;

        for (String tsvFiles_gene : geneDiseaseFiles)
        {
            LOGGER.info("Exporting " + tsvFiles_gene);
            try {
                final MappingIterator<DisGeNetModelGeneDisease> iterator = FileUtils.openGzipTsvWithHeader(workspace, dataSource,
                                                                                                           tsvFiles_gene,
                                                                                                           DisGeNetModelGeneDisease.class);
                while (iterator.hasNext()) {
                    final DisGeNetModelGeneDisease row = iterator.next();
                    final Node geneNode = createGeneNode(graph, row);
                    final Node diseaseNode = createDiseaseNode(graph, row);
                    graph.addEdge(geneNode, diseaseNode, "ASSOCIATED_WITH", "score", row.score);
                    counter++;
                    if (counter % 1000 == 0)
                        LOGGER.info("Progress: " + counter);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        counter = 0;
        for (String s : variantDiseaseFiles)
        {
            LOGGER.info("Exporting " + s);
            try {
                final MappingIterator<DisGeNetModelVariantDisease> iterator = FileUtils.openGzipTsvWithHeader(workspace,
                                                                                                              dataSource,
                                                                                                              s,
                                                                                                              DisGeNetModelVariantDisease.class);
                while (iterator.hasNext()) {
                    final DisGeNetModelVariantDisease row = iterator.next();
                    final Node variantNode = createVariantNode(graph, row);
                    final Node diseaseNode = createDiseaseNode_variant(graph, row);
                    graph.addEdge(variantNode, diseaseNode, "ASSOCIATED_WITH", "score", row.score);
                    counter++;
                    if (counter % 1000 == 0)
                        LOGGER.info("Progress: " + counter);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        counter = 0;
        for (String s : diseaseDiseaseFiles)
        {
            LOGGER.info("Exporting " + s);
            try {
                final MappingIterator<DisGeNetModelDiseaseDisease> iterator = FileUtils.openGzipTsvWithHeader(workspace,
                                                                                                              dataSource,
                                                                                                              s,
                                                                                                              DisGeNetModelDiseaseDisease.class);
                while (iterator.hasNext()) {
                    final DisGeNetModelDiseaseDisease row = iterator.next();
                    final Node diseaseNode1 = createDiseaseDiseaseNode1(graph, row);
                    final Node diseaseNode2 = createDiseaseDiseaseNode2(graph, row);
                    graph.addEdge(diseaseNode1, diseaseNode2, "UNION_GENES", "union_genes", row.union_genes);
                    graph.addEdge(diseaseNode1, diseaseNode2, "UNION_VARIANT", "union_genes", row.union_variant);
                    counter++;
                    if (counter % 1000 == 0)
                        LOGGER.info("Progress: " + counter);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return true;
    }

    private Node createGeneNode(final Graph graph, final DisGeNetModelGeneDisease entry) {
        Node geneNode = graph.findNode("Gene", "id", entry.geneID);
        if (geneNode == null)
        {
            geneNode = createNode(graph, "Gene");
            geneNode.setProperty("id", entry.geneID);
            geneNode.setProperty("gene_symbol", entry.geneSymbol);
            graph.update(geneNode);
        }
        return geneNode;
    }

    private Node createDiseaseNode(final Graph graph, final DisGeNetModelGeneDisease entry) {
        Node diseaseNode = graph.findNode("Disease", "id", entry.geneID);

        if (diseaseNode == null)
        {
            diseaseNode = createNode(graph, "Disease");
            diseaseNode.setProperty("id", entry.diseaseID);
            diseaseNode.setProperty("name", entry.diseaseName);
            diseaseNode.setProperty("type", entry.diseaseType);
            diseaseNode.setProperty("class", entry.diseaseClass);
            diseaseNode.setProperty("semantic_type", entry.diseaseSemanticType);
            graph.update(diseaseNode);
        }
        return diseaseNode;
    }

    private Node createDiseaseNode_variant(final Graph graph, final DisGeNetModelVariantDisease entry) {
        Node diseaseNode = graph.findNode("Disease", "id", entry.diseaseID);

        if (diseaseNode == null)
        {
            diseaseNode = createNode(graph, "Disease");
            diseaseNode.setProperty("id", entry.diseaseID);
            diseaseNode.setProperty("Name", entry.diseaseName);
            diseaseNode.setProperty("Type", entry.diseaseType);
            diseaseNode.setProperty("Class", entry.diseaseClass);
            diseaseNode.setProperty("SemanticType", entry.diseaseSemanticType);
            graph.update(diseaseNode);
        }
        return diseaseNode;
    }

    private Node createVariantNode(final Graph graph, final DisGeNetModelVariantDisease entry)
    {
        Node variantDiseaseNode = graph.findNode("Variant", "id", entry.snpId);
        if (variantDiseaseNode == null) {
            variantDiseaseNode = createNode(graph, "Variant");
            variantDiseaseNode.setProperty("id", entry.snpId);
            variantDiseaseNode.setProperty("chromosome", entry.chromosome);
            variantDiseaseNode.setProperty("position", entry.position);
            graph.update(variantDiseaseNode);
        }
        return variantDiseaseNode;
    }

    private Node createDiseaseDiseaseNode1(final Graph graph, final DisGeNetModelDiseaseDisease entry)
    {
        Node diseaseNode1 = graph.findNode("Disease1", "diseaseId1", entry.diseaseId1);
        if (diseaseNode1 == null)
        {
            diseaseNode1 = createNode(graph, "Disease1");
            diseaseNode1.setProperty("diseaseId1", entry.diseaseId1);
            diseaseNode1.setProperty("diseaseId1_name", entry.diseaseId1_name);
            diseaseNode1.setProperty("source", entry.source);
            graph.update(diseaseNode1);
        }
        return diseaseNode1;
    }

    private Node createDiseaseDiseaseNode2(final Graph graph, final DisGeNetModelDiseaseDisease entry)
    {
        Node diseaseNode2 = graph.findNode("Variant", "diseaseID2", entry.diseaseId2);
        if (diseaseNode2 == null)
        {
            diseaseNode2 = createNode(graph, "Disease2");
            diseaseNode2.setProperty("diseaseId2", entry.diseaseId2);
            diseaseNode2.setProperty("diseaseId2_name", entry.diseaseId2_name);
            diseaseNode2.setProperty("source", entry.source);
            diseaseNode2.setProperty("NVariants", entry.Nvariants);
            graph.update(diseaseNode2);
        }
        return diseaseNode2;
    }
}

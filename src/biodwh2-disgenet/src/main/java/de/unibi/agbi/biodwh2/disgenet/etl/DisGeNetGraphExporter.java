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

    // Still WIP.
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

        // TODO: Include disease-disease associations?

        int counter = 0;
        for (String tsvFiles_gene : geneDiseaseFiles)
        {
            LOGGER.info("Exporting " + tsvFiles_gene);
            try {
                final MappingIterator<DisGeNetModel> iterator = FileUtils.openGzipTsvWithHeader(workspace, dataSource,
                                                                                                tsvFiles_gene,
                                                                                                DisGeNetModel.class);
                while (iterator.hasNext()) {
                    final DisGeNetModel row = iterator.next();
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
        return true;
    }

    private Node createGeneNode(final Graph graph, final DisGeNetModel entry) {
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

    private Node createDiseaseNode(final Graph graph, final DisGeNetModel entry) {
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

    // TODO: Create Disease-Disease node?
}

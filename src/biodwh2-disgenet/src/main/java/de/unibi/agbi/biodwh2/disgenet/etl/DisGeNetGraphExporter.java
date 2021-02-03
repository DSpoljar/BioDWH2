package de.unibi.agbi.biodwh2.disgenet.etl;

import de.unibi.agbi.biodwh2.core.Workspace;
import de.unibi.agbi.biodwh2.core.etl.GraphExporter;
import de.unibi.agbi.biodwh2.core.exceptions.ExporterException;
import de.unibi.agbi.biodwh2.core.model.graph.Graph;
import de.unibi.agbi.biodwh2.core.model.graph.Node;
import de.unibi.agbi.biodwh2.disgenet.DisGeNetDataSource;
import de.unibi.agbi.biodwh2.disgenet.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DisGeNetGraphExporter extends GraphExporter<DisGeNetDataSource>
{

    private static final Logger LOGGER = LoggerFactory.getLogger(DisGeNetGraphExporter.class);
    private static final String GENE_ID = "id";
    private static final String DISEASE_ID = "Gene";
    private static final String DISEASE_DESCRIPTION = "Disease description";
    private static final String SOURCE = "Source";

    private final Map<String, Long> accessionNodeIdMap = new HashMap<>();


    public DisGeNetGraphExporter(final DisGeNetDataSource dataSource)
    {
        super(dataSource);
       // id_to_disease.put("ID", "Disease"); // Example /template mapping


    }

    @Override
    protected boolean exportGraph(final Workspace workspace, final Graph graph)
    {

        graph.setNodeIndexPropertyKeys("symbol", "name");
        // TODO: Construct graph
       // addGenes(graph, );
        // addDiseases(graph,);



        return true;

    }

    private void addGenes(final Graph graph, final List<Gene> genes)
    {
        LOGGER.info("Add Genes...");
        for (final Gene gene : genes) {
            final Node node = createNodeFromModel(graph, gene);
            accessionNodeIdMap.put(gene.geneID, node.getId());
        }
    }


    private void addDiseases(final Graph graph, final List<Disease> diseases)
    {
        LOGGER.info("Add Diseases...");
        for (final Disease disease : diseases) {
            final Node node = createNodeFromModel(graph, disease);
            accessionNodeIdMap.put(disease.diseaseID, node.getId());
        }
    }
}

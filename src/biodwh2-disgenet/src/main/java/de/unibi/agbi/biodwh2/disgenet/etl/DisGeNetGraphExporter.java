package de.unibi.agbi.biodwh2.disgenet.etl;

import de.unibi.agbi.biodwh2.core.Workspace;
import de.unibi.agbi.biodwh2.core.etl.GraphExporter;
import de.unibi.agbi.biodwh2.core.exceptions.ExporterException;
import de.unibi.agbi.biodwh2.core.model.graph.Graph;
import de.unibi.agbi.biodwh2.core.model.graph.Node;
import de.unibi.agbi.biodwh2.disgenet.DisGeNetDataSource;

import java.util.HashMap;
import java.util.Map;

public class DisGeNetGraphExporter extends GraphExporter<DisGeNetDataSource>
{

    private final Map<String, String> id_to_disease = new HashMap<>();


    public DisGeNetGraphExporter(final DisGeNetDataSource dataSource)
    {
        super(dataSource);
        id_to_disease.put("ID", "Disease"); // Example /template mapping


    }

    @Override
    protected boolean exportGraph(final Workspace workspace, final Graph graph)
    {

        graph.setNodeIndexPropertyKeys("hgnc_id");
        Node geneNode = createNode(graph, "Gene");
        geneNode.setProperty("id", "disease");


    return true;

    }
}

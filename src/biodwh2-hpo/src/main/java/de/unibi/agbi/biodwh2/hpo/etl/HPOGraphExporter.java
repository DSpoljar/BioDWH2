package de.unibi.agbi.biodwh2.hpo.etl;

import de.unibi.agbi.biodwh2.core.Workspace;
import de.unibi.agbi.biodwh2.core.etl.GraphExporter;
import de.unibi.agbi.biodwh2.core.exceptions.ExporterException;
import de.unibi.agbi.biodwh2.core.exceptions.ExporterFormatException;
import de.unibi.agbi.biodwh2.core.io.obo.OboEntry;
import de.unibi.agbi.biodwh2.core.io.obo.OboReader;
import de.unibi.agbi.biodwh2.core.model.graph.Graph;
import de.unibi.agbi.biodwh2.core.model.graph.Node;
import de.unibi.agbi.biodwh2.hpo.HPODataSource;

import java.io.IOException;

public class HPOGraphExporter extends GraphExporter<HPODataSource>
{
    public HPOGraphExporter(final HPODataSource dataSource)
    {

        super(dataSource);

    }

    @Override
    protected boolean exportGraph(final Workspace workspace, final Graph graph)
    {
        graph.setNodeIndexPropertyKeys("id");
        try {
            OboReader reader = new OboReader(dataSource.resolveSourceFilePath(workspace, "hp.obo"), "UTF-8");
            for (OboEntry entry : reader)
                if (entry.getName().equals("Term"))
                    exportEntry(graph, entry);
        } catch (IOException e) {
            throw new ExporterFormatException("Failed to export hp.obo", e);
        }
        return true;

    }

    private void exportEntry(final Graph graph, final OboEntry entry) {
       if (entry.containsKey("is_obsolete") && "true".equalsIgnoreCase(entry.getFirst("is_obsolete")))
            return;
       else
       {
           Node node = createNode(graph, "Term");
           node.setProperty("ID", entry.getFirst("name"));
           node.setProperty("name", entry.getFirst("ID"));
           node.setProperty("altID", entry.getFirst("altID"));
           node.setProperty("xref", entry.getFirst("xref"));
           node.setProperty("is_a", entry.getFirst("is_a"));
           graph.update(node);


       }

    }
}

package de.unibi.agbi.biodwh2.hpo.etl;

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
import de.unibi.agbi.biodwh2.hpo.HPODataSource;
import de.unibi.agbi.biodwh2.hpo.model.HPOModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class HPOGraphExporter extends GraphExporter<HPODataSource>
{
    private static final Logger LOGGER = LoggerFactory.getLogger(HPOGraphExporter.class);

    public HPOGraphExporter(final HPODataSource dataSource)
    {

        super(dataSource);

    }

    final String[] txtFiles = new String[]{

            "phenotypes_to_genes.txt"

    };

    @Override
    protected boolean exportGraph(final Workspace workspace, final Graph graph)
    {
        int counter = 0;

        graph.setNodeIndexPropertyKeys("id");
        try {
            OboReader reader = new OboReader(dataSource.resolveSourceFilePath(workspace, "hp.obo"), "UTF-8");
            for (OboEntry entry : reader)
                if (entry.getName().equals("Term"))
                    exportEntry(graph, entry);
                     counter++;
                     if (counter % 1000 == 0)
                     LOGGER.info("Progress: " + counter);
        } catch (IOException e) {
            throw new ExporterFormatException("Failed to export hp.obo", e);
        }


         counter = 0;

        for (String file : txtFiles)
        {
            LOGGER.info("Exporting " + file);
            try {

                final MappingIterator<HPOModel> iterator = FileUtils.openCsv(workspace, dataSource,
                                                                             file,
                                                                             HPOModel.class);

                // TODO: Add nodes and  edges for phenotype_to_genes

                    counter++;
                    if (counter % 1000 == 0)
                        LOGGER.info("Progress: " + counter);

            } catch (IOException e) {
                e.printStackTrace();
            }
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

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
import de.unibi.agbi.biodwh2.hpo.model.HPOPhenotypeToGenesModel;
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

            "phenotype_to_genes.txt"

    };

    @Override
    protected boolean exportGraph(final Workspace workspace, final Graph graph)
    {
        int counter = 0;

        graph.setNodeIndexPropertyKeys("id");
        try {
            OboReader reader = new OboReader(dataSource.resolveSourceFilePath(workspace, "hp.obo"), "UTF-8");
            for (OboEntry entry : reader)
                if (entry.getName().equals("Phenotype"))
                    exportEntry(graph, entry);
                     counter++;
                     if (counter % 10 == 0)
                     LOGGER.info("Progress: " + counter);
        } catch (IOException e) {
            throw new ExporterFormatException("Failed to export hp.obo", e);
        }


         counter = 0;

        for (String file : txtFiles)
        {
            LOGGER.info("Exporting " + file);
            try {

                final MappingIterator<HPOPhenotypeToGenesModel> iterator = FileUtils.openCsv(workspace, dataSource,
                                                                             file, HPOPhenotypeToGenesModel.class);

                // Since there are over 900.000 entries, the counter will be restricted to a smaller amount for now.

                while(iterator.hasNext() && counter <= 5000)
                {

                    final HPOPhenotypeToGenesModel row = iterator.next();
                    final Node node = exportPhenotypeToGeneNode(graph, row);
                    counter++;
                    if (counter % 10 == 0)
                        LOGGER.info("Progress: " + counter);

                }



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

    private Node exportPhenotypeToGeneNode(final Graph graph, final HPOPhenotypeToGenesModel entry) {
        Node node = graph.findNode("ID", "id", entry.HPO_ID);

        if (node == null)
        {
            node = createNode(graph, "Phenotype");
            node.setProperty("HPO-ID", entry.HPO_ID);
            node.setProperty("ENTREZ-GENE-ID", entry.ENTREZ_GENE_ID);
            node.setProperty("HPO-LABEL", entry.HPO_LABEL);

            graph.update(node);
        }
        return node;
    }
}

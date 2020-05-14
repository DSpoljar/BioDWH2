package de.unibi.agbi.biodwh2.unii.etl;

import de.unibi.agbi.biodwh2.core.Workspace;
import de.unibi.agbi.biodwh2.core.etl.GraphExporter;
import de.unibi.agbi.biodwh2.core.exceptions.ExporterException;
import de.unibi.agbi.biodwh2.core.model.graph.Graph;
import de.unibi.agbi.biodwh2.core.model.graph.Node;
import de.unibi.agbi.biodwh2.unii.UNIIDataSource;
import de.unibi.agbi.biodwh2.unii.model.UNIIDataEntry;
import de.unibi.agbi.biodwh2.unii.model.UNIIEntry;

import java.util.*;

public class UNIIGraphExporter extends GraphExporter<UNIIDataSource> {
    @Override
    protected boolean exportGraph(final Workspace workspace, final UNIIDataSource dataSource,
                                  final Graph graph) throws ExporterException {
        graph.setIndexColumnNames("id");
        Map<String, List<UNIIEntry>> uniiEntriesMap = new HashMap<>();
        for (UNIIEntry entry : dataSource.uniiEntries) {
            if (!uniiEntriesMap.containsKey(entry.unii))
                uniiEntriesMap.put(entry.unii, new ArrayList<>());
            uniiEntriesMap.get(entry.unii).add(entry);
        }
        for (String unii : uniiEntriesMap.keySet())
            createUNIINode(graph, uniiEntriesMap.get(unii), dataSource.uniiDataEntries.get(unii));
        return true;
    }

    private void createUNIINode(final Graph graph, final List<UNIIEntry> entries,
                                final UNIIDataEntry dataEntry) throws ExporterException {
        Node uniiNode = createNodeFromModel(graph, dataEntry);
        // TODO: dataEntry.pt
        uniiNode.setProperty("name", entries.get(0).displayName);
        uniiNode.setProperty("official_names", getNameArrayOfTypeFromEntries(entries, "of"));
        uniiNode.setProperty("systematic_names", getNameArrayOfTypeFromEntries(entries, "sys"));
        uniiNode.setProperty("common_names", getNameArrayOfTypeFromEntries(entries, "cn"));
        uniiNode.setProperty("codes", getNameArrayOfTypeFromEntries(entries, "cd"));
        uniiNode.setProperty("brand_names", getNameArrayOfTypeFromEntries(entries, "bn"));
    }

    private static String[] getNameArrayOfTypeFromEntries(final List<UNIIEntry> entries, final String type) {
        return entries.stream().filter(e -> e.type.equals(type)).map(e -> e.name).toArray(String[]::new);
    }
}

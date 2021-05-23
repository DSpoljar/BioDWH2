package de.unibi.agbi.biodwh2.hpo.etl;

import de.unibi.agbi.biodwh2.core.DataSource;
import de.unibi.agbi.biodwh2.core.etl.MappingDescriber;
import de.unibi.agbi.biodwh2.core.model.IdentifierType;
import de.unibi.agbi.biodwh2.core.model.graph.*;

public class HPOMappingDescriber extends MappingDescriber {

    public HPOMappingDescriber(final DataSource dataSource)
    {

        super(dataSource);

    }

    @Override
    public NodeMappingDescription describe(final Graph graph, final Node node, final String localMappingLabel)
    {
        if ("Gene".equals(localMappingLabel))
            return describeEntrezGene(node);
        else if("Phenotype".equals(localMappingLabel))
            return describePhenotype(node);
        else
        {
        return null;
        }

    }

    @Override
    protected String[] getNodeMappingLabels() {

        return new String[]{"Entrez_Gene", "Phenotype"};
    }

    @Override
    public PathMappingDescription describe(final Graph graph, final Node[] nodes, final Edge[] edges) {
        return null;
    }

    @Override
    protected String[][] getEdgeMappingPaths() {
        return new String[0][];
    }

    private NodeMappingDescription describeEntrezGene(final Node node)
    {
        /* TODO: Add proper names and identifiers; HPO identifiers or some kind of Entrez Gene identifiers?
        Also nodetype needs to be properly adjusted.

        final NodeMappingDescription description = new NodeMappingDescription(NodeMappingDescription.NodeType.DUMMY);
        description.addName(node.getProperty("id")); //GetId?
        description.addIdentifier(IdentifierType.HGNC_ID, getGeneIdFromNode(node));

        */

        // Return description later
        return null;
    }

    private NodeMappingDescription describePhenotype(final Node node)
    {


        /* TODO: Add proper names and identifiers; HPO identifiers?
        final NodeMappingDescription description = new NodeMappingDescription(NodeMappingDescription.NodeType.DUMMY);
        description.addName(node.getProperty("id")); //
        description.addIdentifier(IdentifierType.UMLS_CUI, getDiseaseIdFromNode(node));
        */

        // Return description later
        return null;
    }
}

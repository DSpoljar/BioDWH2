package de.unibi.agbi.biodwh2.disgenet.etl;

import de.unibi.agbi.biodwh2.core.DataSource;
import de.unibi.agbi.biodwh2.core.etl.MappingDescriber;
import de.unibi.agbi.biodwh2.core.model.IdentifierType;
import de.unibi.agbi.biodwh2.core.model.graph.*;

public class DisGeNetMappingDescriber extends MappingDescriber {

    // Keys:
    public static final String geneID = "geneID";
    public static final String diseaseID = "diseaseID";

    public DisGeNetMappingDescriber(final DataSource dataSource)
    {

        super(dataSource);

    }


    @Override
    public NodeMappingDescription describe(final Graph graph, final Node node, final String localMappingLabel)
    {
        if ("Gene".equals(localMappingLabel))
            return describeGene(node);
        else if("Disease".equals(localMappingLabel))
            return describeDisease(node);
        return null;
    }

    private NodeMappingDescription describeGene(final Node node) {
        final NodeMappingDescription description = new NodeMappingDescription(NodeMappingDescription.NodeType.GENE);
        description.addName(node.getProperty("id"));
        description.addName(node.getProperty("gene_symbol"));
      // DisGeNet gene identifier? ->  description.addIdentifier(IdentifierType., ...);
      // Needs identifiers to loop through.
        return description;
    }

    private NodeMappingDescription describeDisease(final Node node) {
        final NodeMappingDescription description = new NodeMappingDescription(NodeMappingDescription.NodeType.DISEASE);
        description.addName(node.getProperty("id"));
        description.addName(node.getProperty("type"));
        description.addName(node.getProperty("class"));
        // DisGeNet identifier? ->  description.addIdentifier(IdentifierType., ...);

        return description;
    }

    // TODO: Curated variants as descriptions?

    @Override
    protected String[] getNodeMappingLabels() {


        return new String[0];
    }

    @Override
    public PathMappingDescription describe(final Graph graph, final Node[] nodes, final Edge[] edges)
    {

        return null;
    }

    @Override
    protected String[][] getEdgeMappingPaths() {

        return new String[0][];

    }
}

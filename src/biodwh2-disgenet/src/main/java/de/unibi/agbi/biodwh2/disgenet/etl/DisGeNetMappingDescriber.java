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
        else if("Variant".equals(localMappingLabel))
            return describeVariant(node);
        return null;
    }

    private NodeMappingDescription describeGene(final Node node) {
        final NodeMappingDescription description = new NodeMappingDescription(NodeMappingDescription.NodeType.GENE);
        description.addName(node.getProperty("id")); //GetId?
        description.addIdentifier(IdentifierType.HGNC_SYMBOL, getGeneIdFromNode(node));
        description.addIdentifier(IdentifierType.DISGENET, getGeneIdFromNode(node));
        return description;
    }

    private NodeMappingDescription describeDisease(final Node node) {
        final NodeMappingDescription description = new NodeMappingDescription(NodeMappingDescription.NodeType.DISEASE);
        description.addName(node.getProperty("id")); // GetId?
        description.addName(node.getProperty("diseaseName"));
       // Optional? -> description.addName(node.getLabel());
        description.addIdentifier(IdentifierType.UMLS_CUI, getDiseaseIdFromNode(node));

        return description;
    }


    private NodeMappingDescription describeVariant(final Node node) {
        final NodeMappingDescription description = new NodeMappingDescription(NodeMappingDescription.NodeType.VARIANT);
        description.addName(node.getProperty("snpId")); // GetID maybe?
        description.addName(node.getProperty("diseaseId"));
        description.addName(node.getProperty("diseaseName"));
        // Optional? -> description.addName(node.getLabel());
        description.addIdentifier(IdentifierType.DISGENET, getDiseaseIdFromNode(node));

        return description;
    }

    @Override
    protected String[] getNodeMappingLabels() {


        return new String[]{"Gene", "Disease", "Variant"};

    }

    private String getGeneIdFromNode(final Node node) {
        return node.<String>getProperty(geneID);
    }

    private String getDiseaseIdFromNode(final Node node) {
        return node.<String>getProperty(diseaseID);
    }


    // May be adapted at a later point.

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

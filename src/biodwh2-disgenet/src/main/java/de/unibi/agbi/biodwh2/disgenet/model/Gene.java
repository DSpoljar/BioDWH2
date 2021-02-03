package de.unibi.agbi.biodwh2.disgenet.model;


import de.unibi.agbi.biodwh2.core.model.graph.GraphProperty;
import de.unibi.agbi.biodwh2.core.model.graph.NodeLabel;
import de.unibi.agbi.biodwh2.core.model.graph.GraphArrayProperty;
import de.unibi.agbi.biodwh2.core.model.graph.GraphBooleanProperty;
import de.unibi.agbi.biodwh2.core.model.graph.GraphProperty;
import de.unibi.agbi.biodwh2.core.model.graph.NodeLabel;
import de.unibi.agbi.biodwh2.disgenet.etl.DisGeNetGraphExporter;

@NodeLabel("Gene")
public class Gene
{

    @GraphProperty("ID")
    public String geneID;
    @GraphProperty("Gene Symbol")
    public String geneSymbol;



}

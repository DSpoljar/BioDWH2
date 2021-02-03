package de.unibi.agbi.biodwh2.disgenet.model;

import de.unibi.agbi.biodwh2.core.model.graph.GraphProperty;
import de.unibi.agbi.biodwh2.core.model.graph.NodeLabel;
import de.unibi.agbi.biodwh2.core.model.graph.GraphArrayProperty;
import de.unibi.agbi.biodwh2.core.model.graph.GraphBooleanProperty;
import de.unibi.agbi.biodwh2.core.model.graph.GraphProperty;
import de.unibi.agbi.biodwh2.core.model.graph.NodeLabel;
import de.unibi.agbi.biodwh2.disgenet.etl.DisGeNetGraphExporter;


@NodeLabel("Disease")
public class Disease
{

    @GraphProperty("DSI")
    public String DSI;
    @GraphProperty("DPI")
    public String DPI;
    @GraphProperty("Disease ID")
    public String diseaseID;
    @GraphProperty("Disease name")
    public String diseaseName;
    @GraphProperty("Disease type")
    public String diseaseType;
    @GraphProperty("Disease class")
    public String diseaseClass;
    @GraphProperty("Disease semantic type")
    public String diseaseSemanticType;
    // TODO: score? EI?





}

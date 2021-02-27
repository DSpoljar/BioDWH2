package de.unibi.agbi.biodwh2.disgenet.model;


import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import de.unibi.agbi.biodwh2.core.model.graph.GraphProperty;
import de.unibi.agbi.biodwh2.core.model.graph.NodeLabel;
import de.unibi.agbi.biodwh2.core.model.graph.GraphArrayProperty;
import de.unibi.agbi.biodwh2.core.model.graph.GraphBooleanProperty;
import de.unibi.agbi.biodwh2.core.model.graph.GraphProperty;
import de.unibi.agbi.biodwh2.core.model.graph.NodeLabel;
import de.unibi.agbi.biodwh2.disgenet.etl.DisGeNetGraphExporter;

@JsonPropertyOrder({
        "geneID", "geneSymbol", "DSI", "DPI", "diseaseId", "diseaseName", "diseaseType", "diseaseClass",
        "diseaseSemanticType", "score", "EI", "YearInitial", "YearFinal", "NofPmids",
        "NofSnps", "source"
})


public class DisGeNetModel
{
    @JsonProperty("geneID")
    public String geneID;
    @JsonProperty("geneSymbol")
    public String geneSymbol;
    @JsonProperty("DSI")
    public String DSI;
    @JsonProperty("DPI")
    public String DPI;
    @JsonProperty("diseaseId")
    public String diseaseID;
    @JsonProperty("diseaseName")
    public String diseaseName;
    @JsonProperty("diseaseType")
    public String diseaseType;
    @JsonProperty("diseaseClass")
    public String diseaseClass;
    @JsonProperty("diseaseSemanticType")
    public String diseaseSemanticType;
    @JsonProperty("score")
    public float score;
    @JsonProperty("EI")
    public float EI;
    @JsonProperty("YearInitial")
    public int YearInitial;
    @JsonProperty("YearFinal")
    public int YearFinal;
    @JsonProperty("NofPmids")
    public String NofPmids;
    @JsonProperty("NofSnps")
    public String NofSnps;
    @JsonProperty("source")
    public String source;






}

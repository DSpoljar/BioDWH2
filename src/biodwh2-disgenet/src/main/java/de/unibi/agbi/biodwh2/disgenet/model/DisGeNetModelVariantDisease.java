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
        "snpId", "chromosome", "position", "DSI", "DPI", "diseaseId", "diseaseName", "diseaseType", "diseaseClass",
        "diseaseSemanticType", "score", "EI", "YearInitial", "YearFinal", "NofPmids",
        "source"
})


public class DisGeNetModelVariantDisease
{
    @JsonProperty("snpId")
    public String snpId;
    @JsonProperty("chromosome")
    public String chromosome;
    @JsonProperty("position")
    public String position;
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
    @JsonProperty("source")
    public String source;






}

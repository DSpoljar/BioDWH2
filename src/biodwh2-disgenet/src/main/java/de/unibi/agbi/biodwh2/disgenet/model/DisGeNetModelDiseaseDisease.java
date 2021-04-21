package de.unibi.agbi.biodwh2.disgenet.model;


import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder({
        "diseaseId2", "diseaseId1", "source", "Ngenes", "NgenesD1",
        "NgenesD2", "union_genes", "jaccard_genes", "Nvariants", "NvariantsD1", "NvariantsD2",
        "union_variant", "jaccard_variant", "diseaseId1_name", "diseaseId2_name"
})


public class DisGeNetModelDiseaseDisease
{
    @JsonProperty("diseaseId2")
    public String diseaseId2;
    @JsonProperty("diseaseId1")
    public String diseaseId1;
    @JsonProperty("source")
    public String source;
    @JsonProperty("Ngenes")
    public String Ngenes;
    @JsonProperty("NgenesD1")
    public String NgenesD1;
    @JsonProperty("NgenesD2")
    public String NgenesD2;
    @JsonProperty("union_genes")
    public String union_genes;
    @JsonProperty("jaccard_genes")
    public String jaccard_genes;
    @JsonProperty("Nvariants")
    public String Nvariants;
    @JsonProperty("NvariantsD1")
    public String NvariantsD1;
    @JsonProperty("NvariantsD2")
    public float NvariantsD2;
    @JsonProperty("union_variant")
    public float union_variant;
    @JsonProperty("jaccard_variant")
    public String jaccard_variant;
    @JsonProperty("diseaseId1_name")
    public int diseaseId1_name;
    @JsonProperty("diseaseId2_name")
    public String diseaseId2_name;






}

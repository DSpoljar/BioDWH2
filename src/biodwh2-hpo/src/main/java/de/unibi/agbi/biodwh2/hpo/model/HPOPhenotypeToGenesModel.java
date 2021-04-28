package de.unibi.agbi.biodwh2.hpo.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;


@JsonPropertyOrder({
        "HPO-id", "HPO label", "entrez-gene-id", "entrez-gene-symbol"
})


public class HPOPhenotypeToGenesModel
{
    @JsonProperty("HPO-id")
    public String HPO_ID;
    @JsonProperty("HPO label")
    public String HPO_LABEL;
    @JsonProperty("entrez-gene-id")
    public String ENTREZ_GENE_ID;
    @JsonProperty("entrez-gene-symbol")
    public String ENTREZ_GENE_SYMBOL;




}

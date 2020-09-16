package de.unibi.agbi.biodwh2.itis.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@SuppressWarnings("unused")
@JsonPropertyOrder({"tsn", "vernacular_name", "language", "approved_ind", "update_date", "vern_id"})
public class Vernacular {
    @JsonProperty("tsn")
    public int tsn;
    @JsonProperty("vernacular_name")
    public String name;
    @JsonProperty("language")
    public String language;
    @JsonProperty("approved_ind")
    public String approved;
    @JsonProperty("update_date")
    public String updateDate;
    @JsonProperty("vern_id")
    public int vernacularId;
}

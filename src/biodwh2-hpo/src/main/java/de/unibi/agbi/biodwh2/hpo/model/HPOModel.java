package de.unibi.agbi.biodwh2.hpo.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import de.unibi.agbi.biodwh2.core.model.graph.GraphProperty;
import de.unibi.agbi.biodwh2.core.model.graph.NodeLabel;
import de.unibi.agbi.biodwh2.core.model.graph.GraphArrayProperty;
import de.unibi.agbi.biodwh2.core.model.graph.GraphBooleanProperty;
import de.unibi.agbi.biodwh2.core.model.graph.GraphProperty;
import de.unibi.agbi.biodwh2.core.model.graph.NodeLabel;



@JsonPropertyOrder({
        "ID", "name", "altID", "def", "synonym", "xref", "is_a"
})


public class HPOModel
{
    @JsonProperty("ID")
    public String geneID;
    @JsonProperty("name")
    public String name;
    @JsonProperty("altID")
    public String altID;
    @JsonProperty("def")
    public String synonym;
    @JsonProperty("xref")
    public String xref;
    @JsonProperty("is_a")
    public String is_a;






}

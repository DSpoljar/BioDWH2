package de.unibi.agbi.biodwh2.ndfrt.etl;

import de.unibi.agbi.biodwh2.core.Workspace;
import de.unibi.agbi.biodwh2.core.etl.GraphExporter;
import de.unibi.agbi.biodwh2.core.exceptions.ExporterFormatException;
import de.unibi.agbi.biodwh2.core.model.graph.Graph;
import de.unibi.agbi.biodwh2.core.model.graph.Node;
import de.unibi.agbi.biodwh2.ndfrt.NDFRTDataSource;
import de.unibi.agbi.biodwh2.ndfrt.model.*;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class NDFRTGraphExporter extends GraphExporter<NDFRTDataSource> {
    private static class RoleNameAuthorityPair {
        String name;
        String sourceAuthority;
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(NDFRTGraphExporter.class);
    private static final String IN_NAMESPACE_EDGE_LABEL = "IN_NAMESPACE";

    private Map<String, String> kindRefLabelMap;
    private Map<String, RoleNameAuthorityPair> roleRefLabelMap;
    private Map<String, Property> propertyRefPropertyMap;
    private Map<String, Association> associationRefAssociationMap;
    private Map<String, Qualifier> qualifierRefQualifierMap;

    public NDFRTGraphExporter(final NDFRTDataSource dataSource) {
        super(dataSource);
    }

    @Override
    protected boolean exportGraph(final Workspace workspace, final Graph g) {
        g.setNodeIndexPropertyKeys("id", "code");
        kindRefLabelMap = buildKindRefLabelMap();
        roleRefLabelMap = buildRoleRefLabelMap();
        propertyRefPropertyMap = dataSource.terminology.properties.stream().collect(
                Collectors.toMap(this::getRef, Function.identity()));
        associationRefAssociationMap = dataSource.terminology.associations.stream().collect(
                Collectors.toMap(this::getRef, Function.identity()));
        qualifierRefQualifierMap = dataSource.terminology.qualifiers.stream().collect(
                Collectors.toMap(this::getRef, Function.identity()));
        if (LOGGER.isInfoEnabled())
            LOGGER.info("Export terminology...");
        addTerminology(g);
        if (LOGGER.isInfoEnabled())
            LOGGER.info("Export kinds...");
        addKinds(g);
        if (LOGGER.isInfoEnabled())
            LOGGER.info("Export qualifiers...");
        addQualifiers(g);
        if (LOGGER.isInfoEnabled())
            LOGGER.info("Export properties...");
        addProperties(g);
        if (LOGGER.isInfoEnabled())
            LOGGER.info("Export concepts...");
        addConcepts(g);
        if (LOGGER.isInfoEnabled())
            LOGGER.info("Export associations...");
        addAssociations(g);
        if (LOGGER.isInfoEnabled())
            LOGGER.info("Export roles...");
        addRoles(g);
        return true;
    }

    private Map<String, String> buildKindRefLabelMap() {
        return dataSource.terminology.kinds.stream().collect(
                Collectors.toMap(this::getRef, k -> StringUtils.replace(k.name, "_KIND", "")));
    }

    private String getRef(final Kind kind) {
        if ("code".equals(dataSource.terminology.refBy))
            return kind.code;
        return throwUnknownRefBy();
    }

    private String throwUnknownRefBy() {
        throw new ExporterFormatException(
                "Terminology RefBy '" + dataSource.terminology.refBy + "' is not implemented");
    }

    private Map<String, RoleNameAuthorityPair> buildRoleRefLabelMap() {
        final Map<String, RoleNameAuthorityPair> result = new HashMap<>();
        for (final Role role : dataSource.terminology.roles) {
            final RoleNameAuthorityPair pair = new RoleNameAuthorityPair();
            final String[] parts = StringUtils.split(role.name, " ");
            pair.name = StringUtils.strip(parts[0]).toUpperCase(Locale.US);
            pair.sourceAuthority = StringUtils.strip(parts[1], " {}").toUpperCase(Locale.US);
            result.put(getRef(role), pair);
        }
        return result;
    }

    private String getRef(final Role role) {
        if ("code".equals(dataSource.terminology.refBy))
            return role.code;
        return throwUnknownRefBy();
    }

    private String getRef(final Property property) {
        if ("code".equals(dataSource.terminology.refBy))
            return property.code;
        return throwUnknownRefBy();
    }

    private String getRef(final Association association) {
        if ("code".equals(dataSource.terminology.refBy))
            return association.code;
        return throwUnknownRefBy();
    }

    private String getRef(final Qualifier qualifier) {
        if ("code".equals(dataSource.terminology.refBy))
            return qualifier.code;
        return throwUnknownRefBy();
    }

    private void addTerminology(final Graph g) {
        final Node node = g.addNode("Terminology", "ref_by", dataSource.terminology.refBy, "if_exists_action",
                                    dataSource.terminology.ifExistsAction);
        for (final Namespace namespace : dataSource.terminology.namespaces)
            addTerminologyNamespace(g, node, namespace);
    }

    private void addTerminologyNamespace(final Graph g, final Node terminologyNode, final Namespace namespace) {
        final Node node = createNodeFromModel(g, namespace);
        g.addEdge(terminologyNode, node, IN_NAMESPACE_EDGE_LABEL);
    }

    private void addKinds(final Graph g) {
        for (final Kind kind : dataSource.terminology.kinds)
            addKind(g, kind);
    }

    private void addKind(final Graph g, final Kind kind) {
        final Map<String, Object> properties = new HashMap<>();
        properties.put("id", kind.id);
        properties.put("code", kind.code);
        properties.put("name", kind.name);
        properties.put("is_reference", kind.reference);
        final Node node = g.addNode("KindDefinition", properties);
        final Node namespaceNode = g.findNode("Namespace", dataSource.terminology.refBy, kind.namespace);
        g.addEdge(node, namespaceNode, IN_NAMESPACE_EDGE_LABEL);
    }

    private void addQualifiers(final Graph g) {
        for (final Qualifier qualifier : dataSource.terminology.qualifiers)
            addQualifier(g, qualifier);
    }

    private void addQualifier(final Graph g, final Qualifier qualifier) {
        final Map<String, Object> properties = new HashMap<>();
        properties.put("id", qualifier.id);
        properties.put("code", qualifier.code);
        properties.put("name", qualifier.name);
        properties.put("type", qualifier.type);
        properties.put("pick_list", qualifier.pickList);
        final Node node = g.addNode("QualifierDefinition", properties);
        final Node namespaceNode = g.findNode("Namespace", dataSource.terminology.refBy, qualifier.namespace);
        g.addEdge(node, namespaceNode, IN_NAMESPACE_EDGE_LABEL);
    }

    private void addProperties(final Graph g) {
        for (final Property property : dataSource.terminology.properties)
            addProperty(g, property);
    }

    private void addProperty(final Graph g, final Property property) {
        final Map<String, Object> properties = new HashMap<>();
        properties.put("id", property.id);
        properties.put("code", property.code);
        properties.put("name", property.name);
        properties.put("range", property.range);
        properties.put("contains_index", property.containsIndex);
        properties.put("pick_list", property.pickList);
        final Node node = g.addNode("PropertyDefinition", properties);
        final Node namespaceNode = g.findNode("Namespace", dataSource.terminology.refBy, property.namespace);
        g.addEdge(node, namespaceNode, IN_NAMESPACE_EDGE_LABEL);
    }

    private void addConcepts(final Graph g) {
        for (final Concept concept : dataSource.terminology.concepts)
            addConcept(g, concept);
    }

    private void addConcept(final Graph g, final Concept concept) {
        final Map<String, Object> properties = new HashMap<>();
        properties.put("code", concept.code);
        properties.put("id", concept.id);
        properties.put("namespace", concept.namespace);
        for (Concept.Property property : concept.properties)
            properties.put(propertyRefPropertyMap.get(property.name).name, property.value);
        g.addNode(kindRefLabelMap.get(concept.kind), properties);
    }

    private void addAssociations(final Graph g) {
        for (final Association association : dataSource.terminology.associations)
            addAssociation(g, association);
        for (final Concept concept : dataSource.terminology.concepts)
            addConceptAssociations(g, concept);
    }

    private void addAssociation(final Graph g, final Association association) {
        final Map<String, Object> properties = new HashMap<>();
        properties.put("id", association.id);
        properties.put("code", association.code);
        properties.put("name", association.name);
        properties.put("inverse_name", association.inverseName);
        final Node node = g.addNode("AssociationDefinition", properties);
        final Node namespaceNode = g.findNode("Namespace", dataSource.terminology.refBy, association.namespace);
        g.addEdge(node, namespaceNode, IN_NAMESPACE_EDGE_LABEL);
    }

    private void addConceptAssociations(final Graph g, final Concept concept) {
        final Node conceptNode = g.findNode(dataSource.terminology.refBy, getRef(concept));
        if (concept.associations != null)
            for (final Concept.Association association : concept.associations)
                addConceptAssociation(g, conceptNode, association);
    }

    private void addConceptAssociation(final Graph g, final Node conceptNode, final Concept.Association association) {
        final Node target = g.findNode(dataSource.terminology.refBy, association.value);
        final Map<String, Object> properties = new HashMap<>();
        if (association.qualifiers != null)
            for (Concept.NameValuePair qualifier : association.qualifiers)
                properties.put(qualifierRefQualifierMap.get(qualifier.name).name, qualifier.value);
        g.addEdge(conceptNode, target, associationRefAssociationMap.get(association.name).name, properties);
    }

    private String getRef(final Concept concept) {
        if ("code".equals(dataSource.terminology.refBy))
            return concept.code;
        return throwUnknownRefBy();
    }

    private void addRoles(final Graph g) {
        for (final Role role : dataSource.terminology.roles)
            addRole(g, role);
        for (final Concept concept : dataSource.terminology.concepts)
            addConceptRoles(g, concept);
    }

    private void addRole(final Graph g, final Role role) {
        // TODO: range, domain
        final Node node = g.addNode("RoleDefinition", "id", role.id, "code", role.code, "name", role.name);
        final Node namespaceNode = g.findNode("Namespace", dataSource.terminology.refBy, role.namespace);
        g.addEdge(node, namespaceNode, IN_NAMESPACE_EDGE_LABEL);
    }

    private void addConceptRoles(final Graph g, final Concept concept) {
        final Node conceptNode = g.findNode(dataSource.terminology.refBy, getRef(concept));
        if (concept.definingRoles != null && concept.definingRoles.roles != null)
            for (final Concept.DefiningRole role : concept.definingRoles.roles)
                addConceptRole(g, conceptNode, role);
        // TODO: role groups
    }

    private void addConceptRole(final Graph g, final Node conceptNode, final Concept.DefiningRole role) {
        final Node target = g.findNode(dataSource.terminology.refBy, role.value);
        RoleNameAuthorityPair pair = roleRefLabelMap.get(role.name);
        g.addEdge(conceptNode, target, pair.name, "source_authority", pair.sourceAuthority);
    }

    // TODO: concept [definingConcepts, property.qualifiers]
}

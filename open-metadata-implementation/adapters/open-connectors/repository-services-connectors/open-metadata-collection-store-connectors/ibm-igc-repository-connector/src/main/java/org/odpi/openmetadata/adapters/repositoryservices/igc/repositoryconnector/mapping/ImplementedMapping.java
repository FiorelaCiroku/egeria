/* SPDX-License-Identifier: Apache-2.0 */
/* Copyright Contributors to the ODPi Egeria project. */
package org.odpi.openmetadata.adapters.repositoryservices.igc.repositoryconnector.mapping;

import org.odpi.openmetadata.adapters.repositoryservices.igc.clientlibrary.IGCRestClient;
import org.odpi.openmetadata.adapters.repositoryservices.igc.clientlibrary.model.common.Reference;
import org.odpi.openmetadata.adapters.repositoryservices.igc.repositoryconnector.IGCOMRSMetadataCollection;
import org.odpi.openmetadata.adapters.repositoryservices.igc.repositoryconnector.IGCOMRSRepositoryConnector;
import org.odpi.openmetadata.adapters.repositoryservices.igc.repositoryconnector.mapping.classifications.ClassificationMapping;
import org.odpi.openmetadata.adapters.repositoryservices.igc.repositoryconnector.mapping.entities.EntityMapping;
import org.odpi.openmetadata.adapters.repositoryservices.igc.repositoryconnector.mapping.relationships.RelationshipMapping;
import org.odpi.openmetadata.repositoryservices.connectors.stores.metadatacollectionstore.properties.typedefs.TypeDef;
import org.odpi.openmetadata.repositoryservices.connectors.stores.metadatacollectionstore.properties.typedefs.TypeDefCategory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * Captures the details of an implemented TypeDef (mapping) between OMRS and IGC.
 */
public class ImplementedMapping {

    private static final Logger log = LoggerFactory.getLogger(ImplementedMapping.class);

    private TypeDef typeDef;
    private Type type;
    private EntityMapping entityMapping;
    private RelationshipMapping relationshipMapping;
    private ClassificationMapping classificationMapping;

    public enum Type { ENTITY, RELATIONSHIP, CLASSIFICATION }

    public ImplementedMapping(TypeDef typeDef, Class mapper, IGCOMRSRepositoryConnector igcomrsRepositoryConnector, String userId) {

        this.typeDef = typeDef;

        switch(typeDef.getCategory()) {
            case ENTITY_DEF:
                this.entityMapping = getEntityMapper(mapper, igcomrsRepositoryConnector, userId);
                this.type = Type.ENTITY;
                break;
            case RELATIONSHIP_DEF:
                this.relationshipMapping = getRelationshipMapper(mapper);
                this.type = Type.RELATIONSHIP;
                break;
            case CLASSIFICATION_DEF:
                this.classificationMapping = getClassificationMapper(mapper);
                this.type = Type.CLASSIFICATION;
                break;
        }

    }

    /**
     * Retrieve the OMRS TypeDef processed by the implemented mapping.
     *
     * @return TypeDef
     */
    public TypeDef getTypeDef() { return this.typeDef; }

    /**
     * Retrieve the Java object capable of translating between IGC objects and an OMRS Entities (instances).
     *
     * @return EntityMapping
     */
    public EntityMapping getEntityMapping() { return this.entityMapping; }

    /**
     * Retrieve the Java object capable of translating between IGC object relationships and OMRS Relationships (instances).
     *
     * @return RelationshipMapping
     */
    public RelationshipMapping getRelationshipMapping() { return this.relationshipMapping; }

    /**
     * Retrieve the Java object capable of translating between IGC object relationships and OMRS Classifications (instances).
     *
     * @return ClassificationMapping
     */
    public ClassificationMapping getClassificationMapping() { return this.classificationMapping; }

    /**
     * Indicates whether the implemented mapping matches the provided IGC asset type: that is, whether the mapping
     * can be used to translate to the provided IGC asset type.
     *
     * @param igcAssetType the IGC asset type to check the mapping against
     * @return boolean
     */
    public boolean matchesAssetType(String igcAssetType) {
        switch(typeDef.getCategory()) {
            case ENTITY_DEF:
                return entityMapping.matchesAssetType(igcAssetType);
            case RELATIONSHIP_DEF:
                return (relationshipMapping.getProxyOneMapping().matchesAssetType(igcAssetType)
                        || relationshipMapping.getProxyTwoMapping().matchesAssetType(igcAssetType));
            case CLASSIFICATION_DEF:
                return classificationMapping.matchesAssetType(igcAssetType);
            default:
                log.warn("Unable to determine asset type for this category: {}", typeDef.getCategory());
                return false;
        }
    }

    /**
     * Returns the IGC asset type, only for EntityMappings.
     *
     * @return String
     */
    public String getIgcAssetType() {

        String igcType = null;
        if (typeDef.getCategory().equals(TypeDefCategory.ENTITY_DEF)) {
            igcType = entityMapping.getIgcAssetType();
        }
        return igcType;

    }

    /**
     * Returns any other IGC asset types handled by the mapping, only for EntityMappings.
     *
     * @return List<String>
     */
    public List<String> getOtherIgcAssetTypes() {
        List<String> others = new ArrayList<>();
        if (typeDef.getCategory().equals(TypeDefCategory.ENTITY_DEF)) {
            others = entityMapping.getOtherIGCAssetTypes();
        }
        return others;
    }

    /**
     * Returns the IGC asset type display name, only for EntityMappings.
     *
     * @return String
     */
    public String getIgcAssetTypeDisplayName() {

        String igcTypeDisplayName = null;
        if (typeDef.getCategory().equals(TypeDefCategory.ENTITY_DEF)) {
            igcTypeDisplayName = entityMapping.getIgcAssetTypeDisplayName();
        }
        return igcTypeDisplayName;

    }

    /**
     * Register all POJOs for this mapping with the provided IGCRestClient.
     *
     * @param igcRestClient the IGCRestClient with which to register the POJOs
     */
    public void registerPOJOs(IGCRestClient igcRestClient) {
        if (type.equals(Type.ENTITY)) {
            igcRestClient.registerPOJO(entityMapping.getIgcPOJO());
            for (Class pojo : entityMapping.getOtherIGCPOJOs()) {
                igcRestClient.registerPOJO(pojo);
            }
        } else {
            log.debug("Not an entity mapping -- no POJOs to register.");
        }
    }

    /**
     * Introspect a mapping class to retrieve a Mapper of that type.
     * (This Mapper can be introspected for its mappings, but won't be functional until
     *  initialised with an IGC object.)
     *
     * @param mappingClass the mapping class to retrieve an instance of
     * @param igcomrsRepositoryConnector connectivity to an IGC environment via an OMRS connector
     * @param userId the user through which to retrieve the mapping (currently unused)
     * @return EntityMapping
     */
    public static final EntityMapping getEntityMapper(Class mappingClass,
                                                      IGCOMRSRepositoryConnector igcomrsRepositoryConnector,
                                                      String userId) {
        EntityMapping entityMapper = null;
        try {
            Constructor constructor = mappingClass.getConstructor(IGCOMRSRepositoryConnector.class, String.class);
            entityMapper = (EntityMapping) constructor.newInstance(
                    igcomrsRepositoryConnector,
                    userId
            );
        } catch (NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException e) {
            log.error("Unable to find or instantiate EntityMapping class: {}", mappingClass, e);
        }
        return entityMapper;
    }

    /**
     * Introspect a mapping class to retrieve a RelationshipMapping.
     *
     * @param mappingClass the mapping class to retrieve an instance of
     * @return RelationshipMapping
     */
    private static final RelationshipMapping getRelationshipMapper(Class mappingClass) {
        RelationshipMapping relationshipMapper = null;
        try {
            Method getInstance = mappingClass.getMethod("getInstance");
            relationshipMapper = (RelationshipMapping) getInstance.invoke(null);
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            log.error("Unable to find or instantiate RelationshipMapping class: {}", mappingClass, e);
        }
        return relationshipMapper;
    }

    /**
     * Introspect a mapping class to retrieve a ClassificationMapping.
     *
     * @param mappingClass the mapping class to retrieve an instance of
     * @return ClassificationMapping
     */
    private static final ClassificationMapping getClassificationMapper(Class mappingClass) {
        ClassificationMapping classificationMapper = null;
        try {
            Method getInstance = mappingClass.getMethod("getInstance");
            classificationMapper = (ClassificationMapping) getInstance.invoke(null);
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            log.error("Unable to find or instantiate ClassificationMapping class: {}", mappingClass, e);
        }
        return classificationMapper;
    }

}

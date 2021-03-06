/* SPDX-License-Identifier: Apache-2.0 */
/* Copyright Contributors to the ODPi Egeria project. */
package org.odpi.openmetadata.accessservices.assetconsumer.outtopic;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.odpi.openmetadata.accessservices.assetconsumer.events.AssetConsumerEventHeader;
import org.odpi.openmetadata.accessservices.assetconsumer.events.NewAssetEvent;
import org.odpi.openmetadata.accessservices.assetconsumer.events.UpdatedAssetEvent;
import org.odpi.openmetadata.accessservices.assetconsumer.ffdc.AssetConsumerErrorCode;
import org.odpi.openmetadata.adminservices.ffdc.exception.OMAGConfigurationErrorException;
import org.odpi.openmetadata.frameworks.connectors.Connector;
import org.odpi.openmetadata.frameworks.connectors.ConnectorBroker;
import org.odpi.openmetadata.repositoryservices.auditlog.OMRSAuditLog;
import org.odpi.openmetadata.repositoryservices.connectors.openmetadatatopic.OpenMetadataTopicConnector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.odpi.openmetadata.frameworks.connectors.properties.beans.Connection;

/**
 * AssetConsumerPublisher is the connector responsible .
 */
public class AssetConsumerPublisher
{
    private static final Logger log = LoggerFactory.getLogger(AssetConsumerPublisher.class);

    private OpenMetadataTopicConnector  connector = null;


    /**
     * The constructor is given the connection to the out topic for Asset Consumer OMAS
     * along with classes for testing and manipulating instances.
     *
     * @param assetConsumerOutTopic connection to the out topic
     * @param auditLog log file for the connector.
     */
    public AssetConsumerPublisher(Connection              assetConsumerOutTopic,
                                  OMRSAuditLog            auditLog) throws OMAGConfigurationErrorException
    {
        if (assetConsumerOutTopic != null)
        {
            connector = this.getTopicConnector(assetConsumerOutTopic, auditLog);
        }
    }


    /**
     * Output a new asset event.
     *
     * @param event event to send
     */
    public void publishNewAssetEvent(NewAssetEvent event)
    {
        try
        {
            if (connector != null)
            {
                connector.sendEvent(this.getJSONPayload(event));
            }
        }
        catch (Throwable  error)
        {
            log.error("Unable to publish new asset event: " + event.toString() + "; error was " + error.toString());
        }
    }


    /**
     * Output an updated asset event.
     *
     * @param event event to send.
     */
    public void publishUpdatedAssetEvent(UpdatedAssetEvent  event)
    {
        try
        {
            if (connector != null)
            {
                connector.sendEvent(this.getJSONPayload(event));
            }
        }
        catch (Throwable  error)
        {
            log.error("Unable to publish undated asset event: " + event.toString() + "; error was " + error.toString());
        }
    }


    /**
     * Create the topic connector.
     *
     * @param topicConnection connection to create the connector
     * @param auditLog audit log for the connector
     * @return open metadata topic connector
     */
    private OpenMetadataTopicConnector getTopicConnector(Connection   topicConnection,
                                                         OMRSAuditLog auditLog) throws OMAGConfigurationErrorException
    {
        try
        {
            ConnectorBroker connectorBroker = new ConnectorBroker();
            Connector       connector       = connectorBroker.getConnector(topicConnection);

            OpenMetadataTopicConnector topicConnector  = (OpenMetadataTopicConnector)connector;

            topicConnector.setAuditLog(auditLog);

            topicConnector.start();

            return topicConnector;
        }
        catch (Throwable   error)
        {
            String methodName = "getTopicConnector";

            log.error("Unable to create topic connector: " + error.toString());

            AssetConsumerErrorCode errorCode = AssetConsumerErrorCode.BAD_OUT_TOPIC_CONNECTION;
            String                 errorMessage = errorCode.getErrorMessageId() + errorCode.getFormattedErrorMessage(topicConnection.toString(), error.getClass().getName(), error.getMessage());

            throw new OMAGConfigurationErrorException(errorCode.getHTTPErrorCode(),
                                                      this.getClass().getName(),
                                                      methodName,
                                                      errorMessage,
                                                      errorCode.getSystemAction(),
                                                      errorCode.getUserAction(),
                                                      error);
        }
    }


    /**
     * Return the event as a String where the field contents are encoded in JSON.   The event beans
     * contain annotations that mean the whole event, down to the lowest subclass, is serialized.
     *
     * @return JSON payload (as String)
     */
    private String getJSONPayload(AssetConsumerEventHeader    event)
    {
        ObjectMapper objectMapper = new ObjectMapper();
        String       jsonString   = null;

        /*
         * This class
         */
        try
        {
            jsonString = objectMapper.writeValueAsString(event);
        }
        catch (Throwable  error)
        {
            log.error("Unable to create event payload: " + error.toString());
        }

        return jsonString;
    }

}

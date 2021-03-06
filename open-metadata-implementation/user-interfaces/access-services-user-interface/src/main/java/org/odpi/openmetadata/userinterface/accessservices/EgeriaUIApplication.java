/* SPDX-License-Identifier: Apache-2.0 */
package org.odpi.openmetadata.userinterface.accessservices;

import org.odpi.openmetadata.accessservice.assetcatalog.client.AssetCatalog;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@SpringBootApplication
@Configuration
@ComponentScan({"org.odpi.openmetadata.userinterface.accessservices*"})
public class EgeriaUIApplication {

    private static final Logger LOG = LoggerFactory.getLogger(EgeriaUIApplication.class);

    public static void main(String[] args) {
        SpringApplication.run(EgeriaUIApplication.class, args);
    }

    @Bean
    public AssetCatalog getAssetCatalog(@Value("${omas.server.url}") String serverUrl,
                                        @Value("${omas.server.name}") String serverName) {
        return new AssetCatalog(serverUrl, serverName);
    }

}

package org.recap.admin;

import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.request.CoreAdminRequest;
import org.apache.solr.client.solrj.response.CoreAdminResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;

/**
 * Created by pvsubrah on 6/12/16.
 */
@Component
public class SolrAdmin {

    Logger logger = LoggerFactory.getLogger(SolrAdmin.class);

    private CoreAdminRequest.Create coreAdminCreateRequest;
    private CoreAdminRequest.Unload coreAdminUnloadRequest;

    @Value("${solr.configsets.dir}")
    String configSetsDir;

    @Value("${solr.solr.home}")
    String solrHome;

    @Autowired
    private SolrClient solrAdminClient;


    public CoreAdminResponse createSolrCore(List<String> coreNames) {
        CoreAdminRequest.Create coreAdminRequest = getCoreAdminCreateRequest();
        CoreAdminResponse coreAdminResponse = null;

        for (Iterator<String> iterator = coreNames.iterator(); iterator.hasNext(); ) {
            String coreName = iterator.next();
            String dataDir = solrHome + coreName + File.separator + "data";

            coreAdminRequest.setCoreName(coreName);
            coreAdminRequest.setConfigSet("recap_config");
            coreAdminRequest.setInstanceDir(solrHome+File.separator+coreName);
            coreAdminRequest.setDataDir(dataDir);

            try {
                coreAdminResponse = coreAdminRequest.process(solrAdminClient);
                if (coreAdminResponse.getStatus() == 0) {
                    logger.info("Created Solr core with name: " + coreName);
                } else {
                    logger.error("Error in creating Solr core with name: " + coreName);
                }
            } catch (SolrServerException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return coreAdminResponse;
    }

    public CoreAdminRequest.Create getCoreAdminCreateRequest() {
        if (null == coreAdminCreateRequest) {
            coreAdminCreateRequest = new CoreAdminRequest.Create();
        }
        return coreAdminCreateRequest;
    }

    public CoreAdminRequest.Unload getCoreAdminUnloadRequest() {
        if (null == coreAdminUnloadRequest) {
            coreAdminUnloadRequest = new CoreAdminRequest.Unload(true);
        }
        return coreAdminUnloadRequest;
    }
}
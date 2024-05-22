package com.staketab.minanames.client;

import com.staketab.minanames.dto.ZkCloudWorkerRequestDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class ZkCloudWorkerClient {

    private final RestTemplate zkCloudWorkerRestTemplate;

    @Value("${zk-cloud-worker.auth}")
    private String auth;

    @Value("${zk-cloud-worker.jwt-token}")
    private String jwtToken;

    @Value("${zk-cloud-worker.chain}")
    private String chain;

    private static final String PATH_TO_ZK_CLOUD_WORKER = "/";

    public ResponseEntity<String> sendToZkCloudWorker(ZkCloudWorkerRequestDTO requestDTO) {
        setDefaultValues(requestDTO);
        log.info(requestDTO.toString());
        log.info(zkCloudWorkerRestTemplate.getUriTemplateHandler().expand("/").toString());
        return zkCloudWorkerRestTemplate.postForEntity(PATH_TO_ZK_CLOUD_WORKER, requestDTO, String.class);
    }

    private void setDefaultValues(ZkCloudWorkerRequestDTO requestDTO) {
        requestDTO.setAuth(auth);
        requestDTO.setJwtToken(jwtToken);
        requestDTO.setChain(chain);
    }
}

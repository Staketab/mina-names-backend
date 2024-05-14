package com.staketab.minanames.client;

import com.staketab.minanames.dto.IpfsZkCloudWorkerResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
@RequiredArgsConstructor
public class IpfsZkCloudWorkerClient {

    private final RestTemplate ipfsZkCloudWorkerRestTemplate;

    @Value("${zk-cloud-worker.ipfs-token}")
    private String token;

    private static final String PATH_TO_IPFS_ZK_CLOUD_WORKER = "/ipfs/{ipfs}?pinataGatewayToken={token}";

    public IpfsZkCloudWorkerResponse getBlockByIpfs(String ipfs) {
        return ipfsZkCloudWorkerRestTemplate.getForObject(PATH_TO_IPFS_ZK_CLOUD_WORKER, IpfsZkCloudWorkerResponse.class, ipfs, token);
    }
}

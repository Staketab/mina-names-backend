package com.staketab.minanames.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.staketab.minanames.client.IpfsZkCloudWorkerClient;
import com.staketab.minanames.client.ZkCloudWorkerClient;
import com.staketab.minanames.dto.IpfsZkCloudWorkerResponse;
import com.staketab.minanames.dto.IpfsZkCloudWorkerTransactionResponse;
import com.staketab.minanames.dto.IpfsZkCloudWorkerTransactionWrapperResponse;
import com.staketab.minanames.dto.ZkCloudWorkerBlocksResponse;
import com.staketab.minanames.dto.ZkCloudWorkerContractDataResponse;
import com.staketab.minanames.dto.ZkCloudWorkerDataDTO;
import com.staketab.minanames.dto.ZkCloudWorkerGetBlockInfoResponse;
import com.staketab.minanames.dto.ZkCloudWorkerRequestDTO;
import com.staketab.minanames.dto.ZkCloudWorkerTransaction;
import com.staketab.minanames.entity.DomainEntity;
import com.staketab.minanames.entity.PayableTransactionEntity;
import com.staketab.minanames.entity.ZkCloudWorkerTask;
import com.staketab.minanames.repository.DomainRepository;
import com.staketab.minanames.service.LogInfoService;
import com.staketab.minanames.service.ZkCloudWorkerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.staketab.minanames.entity.DomainStatus.ACTIVE;
import static com.staketab.minanames.entity.DomainStatus.PENDING;
import static com.staketab.minanames.entity.LogInfoStatus.SEND_DOMAIN_TO_ZK_CLOUD_WORKER;
import static com.staketab.minanames.entity.LogInfoStatus.SET_ACTIVE_STATUS_FOR_DOMAIN;
import static com.staketab.minanames.entity.ZkCloudWorkerDomainStatus.ACCEPTED;
import static com.staketab.minanames.entity.ZkCloudWorkerTask.CREATE_TASK;
import static com.staketab.minanames.entity.ZkCloudWorkerTask.GET_BLOCK_INFO;
import static com.staketab.minanames.entity.ZkCloudWorkerTask.SEND_TRANSACTIONS;
import static com.staketab.minanames.entity.ZkCloudWorkerTxOperation.ADD;

@Slf4j
@Service
@RequiredArgsConstructor
public class ZkCloudWorkerServiceImpl implements ZkCloudWorkerService {

    private final Gson gson;
    private final ObjectMapper objectMapper;
    private final LogInfoService logInfoService;
    private final DomainRepository domainRepository;
    private final ZkCloudWorkerClient zkCloudWorkerClient;
    private final IpfsZkCloudWorkerClient ipfsZkCloudWorkerClient;

    @Override
    public void sendTxs(List<PayableTransactionEntity> appliedTxs) {
        List<DomainEntity> domainEntities = domainRepository.findAllByTransactionIn(appliedTxs);
        if (domainEntities.isEmpty()) {
            return;
        }
        List<String> zkCloudWorkerTransactions = domainEntities.stream()
                .map(this::mapToZkCloudWorkerTransaction)
                .map(this::mapTxToString)
                .toList();
        List<DomainEntity> entities = domainEntities.stream()
                .peek(domainEntity -> domainEntity.setIsSendToCloudWorker(true)).toList();

        ZkCloudWorkerDataDTO zkCloudWorkerDataDTO = mapToZkCloudWorkerDataDTO(zkCloudWorkerTransactions, SEND_TRANSACTIONS);
        ZkCloudWorkerRequestDTO zkCloudWorkerRequestDTO = mapToZkCloudWorkerRequestDTO(zkCloudWorkerDataDTO, SEND_TRANSACTIONS);
        ResponseEntity<String> stringResponseEntity = zkCloudWorkerClient.sendToZkCloudWorker(zkCloudWorkerRequestDTO);
        setTxIds(stringResponseEntity, domainEntities);

        logInfoService.saveAllLogInfos(domainEntities, SEND_DOMAIN_TO_ZK_CLOUD_WORKER);
        domainRepository.saveAll(entities);
    }

    @Override
    public ZkCloudWorkerContractDataResponse getBlockInfo() {
        ZkCloudWorkerDataDTO zkCloudWorkerDataDTO = mapToZkCloudWorkerDataDTO(List.of(), GET_BLOCK_INFO);
        ZkCloudWorkerRequestDTO zkCloudWorkerRequestDTO = mapToZkCloudWorkerRequestDTO(zkCloudWorkerDataDTO, GET_BLOCK_INFO);
        ResponseEntity<String> stringResponseEntity = zkCloudWorkerClient.sendToZkCloudWorker(zkCloudWorkerRequestDTO);
        String body = stringResponseEntity.getBody();
        ZkCloudWorkerGetBlockInfoResponse zkCloudWorkerGetBlockInfoResponse = gson.fromJson(body, ZkCloudWorkerGetBlockInfoResponse.class);
        String result = zkCloudWorkerGetBlockInfoResponse.getResult();
        return gson.fromJson(result, ZkCloudWorkerContractDataResponse.class);
    }

    @Override
    public void sendCreateTask() {
        ZkCloudWorkerDataDTO zkCloudWorkerDataDTO = mapToZkCloudWorkerDataDTO(List.of(), CREATE_TASK);
        ZkCloudWorkerRequestDTO zkCloudWorkerRequestDTO = mapToZkCloudWorkerRequestDTO(zkCloudWorkerDataDTO, CREATE_TASK);
        zkCloudWorkerClient.sendToZkCloudWorker(zkCloudWorkerRequestDTO);
    }

    @Override
    public void checkBlocksFromZkCloudWorker() {
        Long topBlockNumber = domainRepository.findTopBlockNumber();
        ZkCloudWorkerContractDataResponse blockInfo = getBlockInfo();
        List<ZkCloudWorkerBlocksResponse> finalBlocks = blockInfo.getBlocks()
                .stream()
                .filter(ZkCloudWorkerBlocksResponse::isFinal)
                .toList();
        List<DomainEntity> domainEntities = domainRepository.findAllByIsSendToCloudWorkerTrueAndDomainStatus(PENDING.name());
        for (ZkCloudWorkerBlocksResponse finalBlock : finalBlocks) {
            if (domainEntities.isEmpty()) {
                return;
            }
            if (finalBlock.getBlockNumber() <= topBlockNumber) {
                return;
            }
            String ipfs = finalBlock.getIpfs();
            IpfsZkCloudWorkerResponse blockByIpfs = ipfsZkCloudWorkerClient.getBlockByIpfs(ipfs);

            Map<String, IpfsZkCloudWorkerTransactionResponse> cloudWorkerTransactionResponseMap = blockByIpfs.getTransactions()
                    .stream()
                    .map(IpfsZkCloudWorkerTransactionWrapperResponse::getTx)
                    .collect(Collectors.toMap(IpfsZkCloudWorkerTransactionResponse::getTxId, Function.identity()));

            List<DomainEntity> activeDomains = new ArrayList<>();
            activateDomains(domainEntities, cloudWorkerTransactionResponseMap, activeDomains, ipfs, finalBlock.getBlockNumber());
        }
    }

    private void activateDomains(List<DomainEntity> domainEntities,
                                 Map<String, IpfsZkCloudWorkerTransactionResponse> map,
                                 List<DomainEntity> activeDomains,
                                 String ipfs, Integer blockNumber) {
        for (DomainEntity domainEntity : domainEntities) {
            String zkTxId = domainEntity.getZkTxId();
            IpfsZkCloudWorkerTransactionResponse zkTransaction = map.get(zkTxId);
            if (zkTransaction != null && ACCEPTED.getName().equals(zkTransaction.getStatus())) {
                domainEntity.setDomainStatus(ACTIVE.name());
                domainEntity.setStartTimestamp(Timestamp.valueOf(LocalDateTime.now()).getTime());
                domainEntity.setIpfs(ipfs);
                domainEntity.setBlockNumber(blockNumber);
                activeDomains.add(domainEntity);
            }
        }
        domainEntities.removeAll(activeDomains);
        logInfoService.saveAllLogInfos(domainEntities, SET_ACTIVE_STATUS_FOR_DOMAIN);
        domainRepository.saveAll(activeDomains);
    }

    private void setTxIds(ResponseEntity<String> stringResponseEntity, List<DomainEntity> domainEntities) {
        List<String> txIds = gson.fromJson(stringResponseEntity.getBody(), new TypeToken<List<String>>() {
        }.getType());

        for (int i = 0; i < domainEntities.size(); i++) {
            DomainEntity domainEntity = domainEntities.get(i);
            if (txIds != null) {
                String txId = txIds.get(i);
                domainEntity.setZkTxId(txId);
            }
        }
    }

    private ZkCloudWorkerRequestDTO mapToZkCloudWorkerRequestDTO(ZkCloudWorkerDataDTO zkCloudWorkerDataDTO, ZkCloudWorkerTask zkCloudWorkerTask) {
        return ZkCloudWorkerRequestDTO.builder()
                .data(zkCloudWorkerDataDTO)
                .command(zkCloudWorkerTask.getCommand())
                .build();
    }

    private ZkCloudWorkerDataDTO mapToZkCloudWorkerDataDTO(List<String> tx, ZkCloudWorkerTask zkCloudWorkerTask) {
        return ZkCloudWorkerDataDTO.builder()
                .transactions(tx)
                .task(zkCloudWorkerTask.getName())
                .metadata(zkCloudWorkerTask.getMetadata())
                .build();
    }

    private ZkCloudWorkerTransaction mapToZkCloudWorkerTransaction(DomainEntity domainEntity) {
        return ZkCloudWorkerTransaction.builder()
                .operation(ADD.getName())
                .address(domainEntity.getOwnerAddress())
                .expiry(domainEntity.getEndTimestamp())
                .name(domainEntity.getDomainName())
                .build();
    }

    private String mapTxToString(ZkCloudWorkerTransaction zkCloudWorkerTransaction) {
        try {
            return objectMapper.writeValueAsString(zkCloudWorkerTransaction);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}

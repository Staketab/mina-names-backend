package com.staketab.minanames.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.staketab.minanames.client.ZkCloudWorkerClient;
import com.staketab.minanames.dto.ZkCloudWorkerDataDTO;
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
import org.springframework.stereotype.Service;

import java.util.List;

import static com.staketab.minanames.entity.LogInfoStatus.SEND_DOMAIN_TO_ZK_CLOUD_WORKER;
import static com.staketab.minanames.entity.ZkCloudWorkerTask.CREATE_TASK;
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
        zkCloudWorkerClient.sendToZkCloudWorker(zkCloudWorkerRequestDTO);

        logInfoService.saveAllLogInfos(domainEntities, SEND_DOMAIN_TO_ZK_CLOUD_WORKER);
        domainRepository.saveAll(entities);
    }

    @Override
    public void sendCreateTask() {
        ZkCloudWorkerDataDTO zkCloudWorkerDataDTO = mapToZkCloudWorkerDataDTO(List.of(), CREATE_TASK);
        ZkCloudWorkerRequestDTO zkCloudWorkerRequestDTO = mapToZkCloudWorkerRequestDTO(zkCloudWorkerDataDTO, CREATE_TASK);
        zkCloudWorkerClient.sendToZkCloudWorker(zkCloudWorkerRequestDTO);
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

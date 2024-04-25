package com.staketab.minanames.service.impl;

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
import static com.staketab.minanames.entity.ZkCloudWorkerTask.SEND_TRANSACTION;
import static com.staketab.minanames.entity.ZkCloudWorkerTxOperation.ADD;

@Slf4j
@Service
@RequiredArgsConstructor
public class ZkCloudWorkerServiceImpl implements ZkCloudWorkerService {

    private final LogInfoService logInfoService;
    private final DomainRepository domainRepository;
    private final ZkCloudWorkerClient zkCloudWorkerClient;

    @Override
    public void sendTxs(List<PayableTransactionEntity> appliedTxs) {
        List<DomainEntity> domainEntities = domainRepository.findAllByTransactionIn(appliedTxs);
        List<ZkCloudWorkerTransaction> zkCloudWorkerTransactions = domainEntities
                .stream()
                .map(this::mapToZkCloudWorkerTransaction)
                .toList();
        List<DomainEntity> entities = domainEntities.stream()
                .peek(domainEntity -> domainEntity.setIsSendToCloudWorker(true)).toList();
        ZkCloudWorkerDataDTO zkCloudWorkerDataDTO = mapToZkCloudWorkerDataDTO(zkCloudWorkerTransactions, SEND_TRANSACTION);
        ZkCloudWorkerRequestDTO zkCloudWorkerRequestDTO = mapToZkCloudWorkerRequestDTO(zkCloudWorkerDataDTO);
        zkCloudWorkerClient.sendToZkCloudWorker(zkCloudWorkerRequestDTO);
        logInfoService.saveAllLogInfos(domainEntities, SEND_DOMAIN_TO_ZK_CLOUD_WORKER);
        domainRepository.saveAll(entities);
    }

    private ZkCloudWorkerRequestDTO mapToZkCloudWorkerRequestDTO(ZkCloudWorkerDataDTO zkCloudWorkerDataDTO) {
        return ZkCloudWorkerRequestDTO.builder()
                .data(zkCloudWorkerDataDTO)
                .build();
    }

    private ZkCloudWorkerDataDTO mapToZkCloudWorkerDataDTO(List<ZkCloudWorkerTransaction> tx, ZkCloudWorkerTask zkCloudWorkerTask) {
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
                //     .signature("Test")
                .build();
    }
}

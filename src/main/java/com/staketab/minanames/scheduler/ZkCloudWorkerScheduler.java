package com.staketab.minanames.scheduler;

import com.staketab.minanames.service.ZkCloudWorkerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
@ConditionalOnProperty(value = "scheduled.zk-cloud-worker.enabled", havingValue = "true")
public class ZkCloudWorkerScheduler {

    private final ZkCloudWorkerService zkCloudWorkerService;

//    @Scheduled(cron = "${scheduled.zk-cloud-worker.send-task-cron}")
//    public void sendCreateTask() {
//        zkCloudWorkerService.sendCreateTask();
//    }

    @Scheduled(fixedDelayString = "${scheduled.zk-cloud-worker.check-zk-blocks}")
    public void checkBlocksFromZkCloudWorker() {
        zkCloudWorkerService.checkBlocksFromZkCloudWorker();
    }

}

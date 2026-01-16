package com.contract.management.infrastructure.messaging;

import com.contract.management.application.service.ClauseExtractionApplicationService;
import com.contract.management.domain.event.ContractCreatedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

/**
 * 条款抽取事件监听器
 * 监听合同创建事件，自动触发条款抽取
 *
 * @author SaltyFish
 * @since 1.0.0
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class ClauseExtractionEventListener {

    private final ClauseExtractionApplicationService clauseExtractionApplicationService;

    /**
     * 监听合同创建事件，触发条款抽取
     */
    @RabbitListener(queues = "${rabbitmq.queue.contract-created:contract.created.queue}")
    public void handleContractCreated(ContractCreatedEvent event) {
        try {
            log.info("收到合同创建事件，开始触发条款抽取: contractId={}, fileUuid={}, createdBy={}",
                    event.getContractId(), event.getAttachmentUuid(), event.getCreatedBy());

            // 只有当合同有附件时才进行条款抽取
            if (event.getAttachmentUuid() != null && !event.getAttachmentUuid().trim().isEmpty()) {
                clauseExtractionApplicationService.triggerExtraction(
                    event.getContractId(),
                    event.getCreatedBy(),
                    event.getAttachmentUuid()
                );
                log.info("条款抽取任务已触发: contractId={}", event.getContractId());
            } else {
                log.info("合同无附件，跳过条款抽取: contractId={}", event.getContractId());
            }

        } catch (Exception e) {
            log.error("处理合同创建事件失败，无法触发条款抽取: contractId={}", event.getContractId(), e);
            // 这里不抛出异常，避免影响消息队列的正常消费
            // 可以考虑发送告警通知
        }
    }
}
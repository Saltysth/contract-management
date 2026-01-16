package com.contract.management.infrastructure.messaging;

import com.contract.management.domain.event.ContractCreatedEvent;
import com.contract.management.domain.event.ContractDeletedEvent;
import com.contract.management.domain.event.ContractStatusChangedEvent;
import com.contract.management.domain.event.ContractUpdatedEvent;
import com.contract.management.infrastructure.config.RabbitMQConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

/**
 * 合同事件发布服务
 *
 * @author SaltyFish
 * @since 1.0.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ContractEventPublisher {

    private final RabbitTemplate rabbitTemplate;

    /**
     * 发布合同创建事件
     */
    public void publishContractCreated(ContractCreatedEvent event) {
        try {
            rabbitTemplate.convertAndSend(
                    RabbitMQConfig.CONTRACT_EXCHANGE,
                    RabbitMQConfig.CONTRACT_CREATED_ROUTING_KEY,
                    event
            );
            log.info("合同创建事件发布成功: contractId={}", event.getContractId());
        } catch (Exception e) {
            log.error("合同创建事件发布失败: contractId={}", event.getContractId(), e);
            throw new RuntimeException("事件发布失败", e);
        }
    }

    /**
     * 发布合同更新事件
     */
    public void publishContractUpdated(ContractUpdatedEvent event) {
        try {
            rabbitTemplate.convertAndSend(
                    RabbitMQConfig.CONTRACT_EXCHANGE,
                    RabbitMQConfig.CONTRACT_UPDATED_ROUTING_KEY,
                    event
            );
            log.info("合同更新事件发布成功: contractId={}", event.getContractId());
        } catch (Exception e) {
            log.error("合同更新事件发布失败: contractId={}", event.getContractId(), e);
            throw new RuntimeException("事件发布失败", e);
        }
    }

    /**
     * 发布合同删除事件
     */
    public void publishContractDeleted(ContractDeletedEvent event) {
        try {
            rabbitTemplate.convertAndSend(
                    RabbitMQConfig.CONTRACT_EXCHANGE,
                    RabbitMQConfig.CONTRACT_DELETED_ROUTING_KEY,
                    event
            );
            log.info("合同删除事件发布成功: contractId={}", event.getContractId());
        } catch (Exception e) {
            log.error("合同删除事件发布失败: contractId={}", event.getContractId(), e);
            throw new RuntimeException("事件发布失败", e);
        }
    }

    /**
     * 发布合同状态变更事件
     */
    public void publishContractStatusChanged(ContractStatusChangedEvent event) {
        try {
            rabbitTemplate.convertAndSend(
                    RabbitMQConfig.CONTRACT_EXCHANGE,
                    RabbitMQConfig.CONTRACT_STATUS_CHANGED_ROUTING_KEY,
                    event
            );
            log.info("合同状态变更事件发布成功: contractId={}, oldStatus={}, newStatus={}", 
                    event.getContractId(), event.getOldStatus(), event.getNewStatus());
        } catch (Exception e) {
            log.error("合同状态变更事件发布失败: contractId={}", event.getContractId(), e);
            throw new RuntimeException("事件发布失败", e);
        }
    }
}
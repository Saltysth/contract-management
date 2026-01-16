package com.contract.management.interfaces.mq;

import com.contract.management.domain.event.ContractCreatedEvent;
import com.contract.management.domain.event.ContractDeletedEvent;
import com.contract.management.domain.event.ContractStatusChangedEvent;
import com.contract.management.domain.event.ContractUpdatedEvent;
import com.contract.management.infrastructure.config.RabbitMQConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

/**
 * 合同事件监听器
 * 
 * 注意：这个监听器主要用于演示，实际项目中可能需要根据业务需求进行调整
 *
 * @author SaltyFish
 * @since 1.0.0
 */
@Slf4j
@Component
public class ContractEventListener {

    /**
     * 监听合同创建事件
     */
    @RabbitListener(queues = RabbitMQConfig.CONTRACT_CREATED_QUEUE)
    public void handleContractCreated(@Payload ContractCreatedEvent event) {
        try {
            log.info("接收到合同创建事件: contractId={}, contractName={}", 
                    event.getContractId(), event.getContractName());
            
            // 这里可以添加具体的业务处理逻辑
            // 例如：发送通知、更新缓存、同步到其他系统等
            
        } catch (Exception e) {
            log.error("处理合同创建事件失败: contractId={}", event.getContractId(), e);
            // 这里可以添加错误处理逻辑，比如重试或者发送到死信队列
        }
    }

    /**
     * 监听合同更新事件
     */
    @RabbitListener(queues = RabbitMQConfig.CONTRACT_UPDATED_QUEUE)
    public void handleContractUpdated(@Payload ContractUpdatedEvent event) {
        try {
            log.info("接收到合同更新事件: contractId={}", event.getContractId());
            
            // 处理合同更新逻辑
            
        } catch (Exception e) {
            log.error("处理合同更新事件失败: contractId={}", event.getContractId(), e);
        }
    }

    /**
     * 监听合同删除事件
     */
    @RabbitListener(queues = RabbitMQConfig.CONTRACT_DELETED_QUEUE)
    public void handleContractDeleted(@Payload ContractDeletedEvent event) {
        try {
            log.info("接收到合同删除事件: contractId={}", event.getContractId());
            
            // 处理合同删除逻辑
            
        } catch (Exception e) {
            log.error("处理合同删除事件失败: contractId={}", event.getContractId(), e);
        }
    }

    /**
     * 监听合同状态变更事件
     */
    @RabbitListener(queues = RabbitMQConfig.CONTRACT_STATUS_CHANGED_QUEUE)
    public void handleContractStatusChanged(@Payload ContractStatusChangedEvent event) {
        try {
            log.info("接收到合同状态变更事件: contractId={}, oldStatus={}, newStatus={}", 
                    event.getContractId(), event.getOldStatus(), event.getNewStatus());
            
            // 处理状态变更逻辑
            
        } catch (Exception e) {
            log.error("处理合同状态变更事件失败: contractId={}", event.getContractId(), e);
        }
    }
}
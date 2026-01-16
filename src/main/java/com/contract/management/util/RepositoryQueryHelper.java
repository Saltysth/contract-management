package com.contract.management.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.function.Supplier;

/**
 * 仓库查询工具类，用于简化常见的查询和空值检查逻辑
 */
public class RepositoryQueryHelper {
    private static final Logger log = LoggerFactory.getLogger(RepositoryQueryHelper.class);

    /**
     * 执行查询并处理空结果：若结果为空，打日志并抛指定异常
     *
     * @param querySupplier    查询逻辑（延迟执行，避免提前调用）
     * @param emptyWarnMsg     结果为空时的警告日志信息
     * @param exceptionSupplier 结果为空时抛出的异常（通过Supplier延迟创建）
     * @param <T>              查询结果的元素类型
     * @return 非空的查询结果列表
     */
    public static <T> T queryOneAndCheckEmpty(
            Supplier<List<T>> querySupplier,
            String emptyWarnMsg,
            Supplier<? extends RuntimeException> exceptionSupplier
    ) {
        // 执行查询（通过Supplier延迟执行，确保在方法内部调用）
        List<T> result = querySupplier.get();

        // 检查空值并处理
        if (CollectionUtils.isEmpty(result)) {
            log.warn(emptyWarnMsg);
            throw exceptionSupplier.get();
        }
        return result.get(0);
    }

    /**
     * 执行查询并处理空结果：若结果为空，打日志并返回默认值
     *
     * @param querySupplier    查询逻辑（延迟执行，避免提前调用）
     * @param emptyWarnMsg     结果为空时的警告日志信息
     * @param defaultValue     结果为空时返回的默认值
     * @param <T>              查询结果的元素类型
     * @return 查询结果或默认值
     */
    public static <T> List<T> queryOrDefault(
            Supplier<List<T>> querySupplier,
            String emptyWarnMsg,
            List<T> defaultValue
    ) {
        // 执行查询（通过Supplier延迟执行，确保在方法内部调用）
        List<T> result = querySupplier.get();

        // 检查空值并处理
        if (CollectionUtils.isEmpty(result)) {
            log.warn(emptyWarnMsg);
            return defaultValue;
        }
        return result;
    }

    /**
     * 执行查询并处理空结果：若结果为空，打日志但不抛异常
     *
     * @param querySupplier    查询逻辑（延迟执行，避免提前调用）
     * @param emptyWarnMsg     结果为空时的警告日志信息
     * @param <T>              查询结果的元素类型
     * @return 可能为空的查询结果列表
     */
    public static <T> List<T> queryAndLog(
            Supplier<List<T>> querySupplier,
            String emptyWarnMsg
    ) {
        // 执行查询（通过Supplier延迟执行，确保在方法内部调用）
        List<T> result = querySupplier.get();

        // 检查空值并记录日志
        if (CollectionUtils.isEmpty(result)) {
            log.warn(emptyWarnMsg);
        }
        return result;
    }
}
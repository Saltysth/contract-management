package com.contract.management.application.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.cache.CacheManager;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.mockito.Mockito.*;

/**
 * 合同分类应用服务缓存测试
 *
 * @author SaltyFish
 * @since 1.0.0
 */
@ExtendWith(SpringExtension.class)
@DisplayName("合同分类应用服务缓存测试")
class ContractClassificationApplicationServiceCacheTest {

    @Mock
    private CacheManager cacheManager;

    @InjectMocks
    private ContractClassificationApplicationService service;

    @Test
    @DisplayName("清除缓存功能测试")
    void clearClassificationCache_clearsCacheSuccessfully() {
        // Given
        Long contractId = 1L;
        org.springframework.cache.Cache cache = mock(org.springframework.cache.Cache.class);

        when(cacheManager.getCache("classification")).thenReturn(cache);

        // When - 需要调用一个会触发清除缓存的方法
        // 这里我们无法直接调用私有方法，但可以通过反射来测试

        // 使用反射调用私有方法
        try {
            var method = ContractClassificationApplicationService.class.getDeclaredMethod("clearClassificationCache", Long.class);
            method.setAccessible(true);
            method.invoke(service, contractId);
        } catch (Exception e) {
            // 如果反射失败，至少验证代码编译通过
        }

        // Then - 验证缓存被清除
        verify(cache).evict(contractId);
    }
}
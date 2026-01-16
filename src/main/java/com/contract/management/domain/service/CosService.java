package com.contract.management.domain.service;

import com.qcloud.cos.COSClient;
import com.qcloud.cos.exception.CosClientException;
import com.qcloud.cos.http.HttpMethodName;
import com.qcloud.cos.model.ObjectMetadata;
import com.qcloud.cos.model.PutObjectRequest;
import com.qcloud.cos.model.PutObjectResult;
import com.qcloud.cos.model.StorageClass;
import com.qcloud.cos.transfer.TransferManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.net.URL;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @author wangcong@SaltyFish
 * @description
 * @since 2025/12/10 11:45
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CosService {
    private final COSClient cosClient;
    private final TransferManager transferManager;
    private static final String BUCKET_NAME = "contract-1307520509";
    private static final String KEY_PREFIX = "contract_attachments/";


    public void uploadSingleFile(InputStream inputStream, String fileName, Long fileSize) {
        // 存储桶的命名格式为 BucketName-APPID，此处填写的存储桶名称必须为此格式
        // 对象键(Key)是对象在存储桶中的唯一标识。
        String key = KEY_PREFIX + fileName;

        ObjectMetadata objectMetadata = new ObjectMetadata();
        // 上传的流如果能够获取准确的流长度，则推荐一定填写 content-length
        // 如果确实没办法获取到，则下面这行可以省略，但同时高级接口也没办法使用分块上传了
        objectMetadata.setContentLength(fileSize);

        PutObjectRequest putObjectRequest = new PutObjectRequest(BUCKET_NAME, key, inputStream, objectMetadata);

        // 设置存储类型（如有需要，不需要请忽略此行代码）, 默认是标准(Standard), 低频(standard_ia)
        // 更多存储类型请参见 https://cloud.tencent.com/document/product/436/33417
        putObjectRequest.setStorageClass(StorageClass.Standard_IA);

        try {
            PutObjectResult putObjectResult = cosClient.putObject(putObjectRequest);
            System.out.println(putObjectResult.getRequestId());
        } catch (CosClientException e) {
            log.error("error in putting object");
            throw new RuntimeException(e);
        }
    }

    public String getPresignedUrl(String fileName) {
        // 对象键(Key)是对象在存储桶中的唯一标识。详情请参见 [对象键](https://cloud.tencent.com/document/product/436/13324)
        String key = KEY_PREFIX + fileName;

        // 设置签名过期时间(可选), 若未进行设置则默认使用 ClientConfig 中的签名过期时间(1小时)
        // 这里设置签名在半个小时后过期
        Date expirationDate = new Date(System.currentTimeMillis() + 30 * 60 * 1000);

        // 填写本次请求的参数，需与实际请求相同，能够防止用户篡改此签名的 HTTP 请求的参数
        Map<String, String> params = new HashMap<String, String>();
//        params.put("param1", "value1");

        // 填写本次请求的头部，需与实际请求相同，能够防止用户篡改此签名的 HTTP 请求的头部
        Map<String, String> headers = new HashMap<String, String>();
//        headers.put("header1", "value1");

        // 请求的 HTTP 方法，上传请求用 PUT，下载请求用 GET，删除请求用 DELETE
        HttpMethodName method = HttpMethodName.GET;

        URL url = cosClient.generatePresignedUrl(BUCKET_NAME, key, expirationDate, method, headers, params);
        String presignedUrl = url.toString();
        log.info("presignedUrl={}", presignedUrl);

        return presignedUrl;
    }
}

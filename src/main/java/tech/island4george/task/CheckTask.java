package tech.island4george.task;

import com.aliyun.alidns20150109.Client;
import com.aliyun.alidns20150109.models.DescribeDomainRecordsRequest;
import com.aliyun.alidns20150109.models.DescribeDomainRecordsResponse;
import com.aliyun.alidns20150109.models.DescribeDomainRecordsResponseBody.DescribeDomainRecordsResponseBodyDomainRecordsRecord;
import com.aliyun.alidns20150109.models.UpdateDomainRecordRequest;
import com.aliyun.teautil.models.RuntimeOptions;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import tech.island4george.handler.IpHandler;
import tech.island4george.configuration.DomainConfiguration;


import java.util.List;
import java.util.Objects;

@Slf4j
@Component
public class CheckTask {
    @Autowired
    private Client client;

    @Autowired
    private DomainConfiguration domainConfig;

    @Autowired
    private IpHandler ipHandler;

    private String currentPublicIp;

    private boolean hasNoError;

    @Scheduled(cron = "${task.schedule:0 0/10 * * * ?}")
    public void task() {
        hasNoError = true;
        try {
            currentPublicIp = ipHandler.getCurrentPublicIp();
            // 如果当前IP与缓存IP相同，则不做修改尝试
            if (ipHandler.isMatchCachedIp(currentPublicIp)) {
                return;
            }
            for (DomainConfiguration.SingleDomainConfiguration domainConfig : domainConfig.getDomainConfigurations()) {
                checkSingleDomain(domainConfig);
            }

            // 整个过程无错误，则更新IP缓存
            if (hasNoError) {
                ipHandler.setCachedPublicIp(currentPublicIp);
            }
        } catch (Exception e) {
            log.error("Task Exception ", e);
        }
    }

    private void checkSingleDomain(DomainConfiguration.SingleDomainConfiguration domainConfig) {
        String domainName = domainConfig.getDomainName();
        // 查询域名对应的解析记录
        List<DescribeDomainRecordsResponseBodyDomainRecordsRecord> aliyunRecords = getAliyunDomainRecord(domainName);
        if (aliyunRecords == null) {
            return;
        }

        // 对比阿里云上的记录和配置的记录
        List<DomainConfiguration.DomainRecord> configRecords = domainConfig.getRecords();
        for (DomainConfiguration.DomainRecord configRecord : configRecords) {
            for (DescribeDomainRecordsResponseBodyDomainRecordsRecord aliyunRecord : aliyunRecords) {
                if (!Objects.equals(aliyunRecord.getType(), configRecord.getType())
                        || !Objects.equals(aliyunRecord.getRR(), configRecord.getRR())) {
                    continue;
                }
                if (Objects.equals(aliyunRecord.getValue(), currentPublicIp)) {
                    continue;
                }
                log.info("Found DomainRecord[{} {} {}] need to update",
                        aliyunRecord.getDomainName(), aliyunRecord.getType(), aliyunRecord.getRR());
                updateAliyunDomainRecord(aliyunRecord.getRecordId(),
                        configRecord.getType(),
                        configRecord.getRR(),
                        currentPublicIp);
                log.info("UpdateDomainRecord[{} {} {}] success. [{}] -> [{}]",
                        domainName, configRecord.getType(), configRecord.getRR(),
                        aliyunRecord.getValue(), currentPublicIp);
            }
        }
    }

    private List<DescribeDomainRecordsResponseBodyDomainRecordsRecord> getAliyunDomainRecord(String domainName) {
        try {
            DescribeDomainRecordsRequest request = new DescribeDomainRecordsRequest()
                    .setDomainName(domainName);
            DescribeDomainRecordsResponse response = client.describeDomainRecordsWithOptions(request, new RuntimeOptions());
            return response.getBody().getDomainRecords().getRecord();
        } catch (Exception e) {
            hasNoError = false;
            log.warn("Query[{}] DescribeDomainRecords error, msg: {}", domainName, e.getMessage());
            return null;
        }
    }

    private void updateAliyunDomainRecord(String recordId, String type, String RR, String value) {
        try {
            UpdateDomainRecordRequest updateDomainRecordRequest = new UpdateDomainRecordRequest()
                    .setRecordId(recordId)
                    .setRR(RR)
                    .setType(type)
                    .setValue(value);
            client.updateDomainRecordWithOptions(updateDomainRecordRequest, new RuntimeOptions());
        } catch (Exception e) {
            hasNoError = false;
            log.warn("UpdateDomainRecord[{}] error, msg: {}", recordId, e.getMessage());
        }
    }
}

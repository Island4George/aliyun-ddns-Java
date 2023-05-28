package tech.island4george.configuration;

import com.aliyun.alidns20150109.Client;
import com.aliyun.teaopenapi.models.Config;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AliyunClientConfiguration {
    @Autowired
    private AliyunAccountConfiguration accountConfiguration;

    @Bean
    public Client getAliyunClient() throws Exception {
        Config config = new Config()
                .setAccessKeyId(accountConfiguration.getAccessKeyId())
                .setAccessKeySecret(accountConfiguration.getAccessKeySecret())
                .setEndpoint("dns.aliyuncs.com");
        return new Client(config);
    }
}

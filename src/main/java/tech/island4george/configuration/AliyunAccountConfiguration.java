package tech.island4george.configuration;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Setter
@Getter
@Configuration
@ConfigurationProperties("aliyun.account")
public class AliyunAccountConfiguration {
    private String accessKeyId;

    private String accessKeySecret;
}

package tech.island4george.configuration;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Setter
@Getter
@Configuration
@ConfigurationProperties("aliyun")
public class DomainConfiguration {
    private List<SingleDomainConfiguration> domainConfigurations;

    @Setter
    @Getter
    public static class SingleDomainConfiguration {
        private String domainName;

        private List<DomainRecord> records;
    }

    @Setter
    @Getter
    public static class DomainRecord {
        /**
         * 指解析记录的用途，例如：网站、邮箱
         * A：将域名指向一个IPv4地址。参考标准；RR值可为空，即@解析；不允许含有下划线；
         * CNAME：将域名指向另外一个域名。参考标准；另外，有效字符除字母、数字、“-”（中横杠）、还包括“_”（下划线）；RR值不允许为空（即@）；允许含有下划线
         * AAAA：将域名指向一个IPv6地址。参考标准；RR值可为空，即@解析；不允许含有下划线；
         * NS：将子域名指向其他DNS服务器解析。参考标准；RR值不能为空；允许含有下划线；不支持泛解析
         * MX：将域名指向邮件服务器地址。参考标准；RR值可为空，即@解析；不允许含有下划线
         * SRV：记录提供特定的服务的服务器。是一个name，且可含有下划线“_“和点“.”；允许含有下划线；可为空（即@）；不支持泛解析
         * TXT：文本长度限制512，通常做SPF记录（反垃圾邮件）。参考标准；另外，有效字符除字母、数字、“-”（中横杠）、还包括“_”（下划线）；RR值可为空，即@解析；允许含有下划线；不支持泛解析
         * CAA：CA证书颁发机构授权校验。参考标准；RR值可为空，即@解析；不允许含有下划线；
         * REDIRECT_URL：将域名重定向到另外一个地址。参考标准；RR值可为空，即@解析
         * FORWARD_URL：与显性URL类似，但是会隐藏真实目标地址。参考标准；RR值可为空，即@解析
         */
        private String type;

        /**
         * 主机记录就是域名前缀，常见用法有：
         * www：解析后的域名为www.aliyun.com。
         * @：直接解析主域名 aliyun.com。
         * *：泛解析，匹配其他所有域名 *.aliyun.com。
         * mail：将域名解析为mail.aliyun.com，通常用于解析邮箱服务器。
         * 二级域名：如：abc.aliyun.com，填写abc。
         * 手机网站：如：m.aliyun.com，填写m。
         * 显性URL：不支持泛解析（泛解析：将所有子域名解析到同一地址）
         */
        private String RR;
    }
}

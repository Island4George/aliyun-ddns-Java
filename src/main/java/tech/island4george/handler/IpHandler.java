package tech.island4george.handler;

import com.alibaba.fastjson.JSON;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.springframework.stereotype.Component;
import tech.island4george.exception.ServiceException;
import tech.island4george.model.PublicIp;

import java.io.IOException;

@Slf4j
@Component
public class IpHandler {
    private static final String IP_CHECK_URL = "https://jsonip.com";

    private final OkHttpClient httpClient = new OkHttpClient();

    @Getter
    @Setter
    private String cachedPublicIp = null;

    /**
     * 查询当前的公网IP
     *
     * @return 公网IP字符串
     * @throws ServiceException 查询异常时抛出
     */
    public String getCurrentPublicIp() throws ServiceException {
        Request request = new Request.Builder()
                .url(IP_CHECK_URL)
                .get()
                .build();

        try (Response response = httpClient.newCall(request).execute()) {
            if (response.body() == null) {
                throw new ServiceException("Get null response.");
            }
            String currentPublicIp = JSON.parseObject(response.body().string(), PublicIp.class).getIp();
            if (currentPublicIp == null) {
                throw new ServiceException("Get null currentPublicIp.");
            }
            return currentPublicIp;
        } catch (IOException e) {
            log.error("Request error", e);
            throw new ServiceException(e);
        }
    }

    public boolean isMatchCachedIp(String ip) {
        if (cachedPublicIp == null) {
            return false;
        }
        return cachedPublicIp.equals(ip);
    }
}

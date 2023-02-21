package cn.itcast.feign.config;

import feign.Logger;
import org.springframework.context.annotation.Bean;

/**
 * @author codejuzi
 */
public class DefaultFeignConfiguration {
    @Bean
    public Logger.Level feignLogLevel() {
        return Logger.Level.BASIC;
    }
}

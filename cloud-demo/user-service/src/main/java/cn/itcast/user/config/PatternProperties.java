package cn.itcast.user.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author codejuzi
 */
@Data
@ConfigurationProperties(prefix = "pattern")
@Component
public class PatternProperties {
    private String dateformat;

    private String envSharedValue;

    private String name = "Local环境";
}

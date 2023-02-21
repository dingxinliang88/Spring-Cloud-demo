package cn.itcast.hotel.config;

import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author codejuzi
 */
@Configuration
public class ESConfig {

    @Bean
    public RestHighLevelClient client() {
        return new RestHighLevelClient(RestClient.builder(
                HttpHost.create("http://101.43.45.97:9200")
        ));
    }
}

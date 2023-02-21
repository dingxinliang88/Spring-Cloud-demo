package cn.itcast.hotel.index;


import org.apache.http.HttpHost;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.CreateIndexRequest;
import org.elasticsearch.client.indices.GetIndexRequest;
import org.elasticsearch.common.xcontent.XContentType;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static cn.itcast.hotel.constants.HotelConstants.HOTEL_MAPPING_TEMPLATE;
import static cn.itcast.hotel.constants.HotelConstants.INDEX_HOTEL;

/**
 * @author codejuzi
 */
class HotelIndexTest {

    private RestHighLevelClient client;


    @BeforeEach
    void setUp() {
        this.client = new RestHighLevelClient(RestClient.builder(
                HttpHost.create("http://101.43.45.97:9200")
        ));
    }

    @Test
    void testClientInit() {
        Assertions.assertNotNull(client);
        System.out.println(this.client);
    }

    @Test
    void testCreateIndex() throws IOException {
        // 1.准备Request      PUT /hotel
        CreateIndexRequest request = new CreateIndexRequest(INDEX_HOTEL);
        // 2.准备请求参数
        request.source(HOTEL_MAPPING_TEMPLATE, XContentType.JSON);
        // 3.发送请求
        client.indices().create(request, RequestOptions.DEFAULT);
    }

    @Test
    void testDeleteIndex() throws IOException {
        // 准备request DELETE /hotel
        DeleteIndexRequest request = new DeleteIndexRequest(INDEX_HOTEL);
        // 发送请求
        client.indices().delete(request, RequestOptions.DEFAULT);
    }

    @Test
    void testGetIndex() throws IOException {
        // 准备Request      GET /hotel
        GetIndexRequest request = new GetIndexRequest(INDEX_HOTEL);
        // 发送请求
        boolean exists = client.indices().exists(request, RequestOptions.DEFAULT);
        Assertions.assertTrue(exists);
    }

    @AfterEach
    void tearDown() throws IOException {
        this.client.close();
    }
}

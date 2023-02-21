package cn.itcast.hotel.doc;

import cn.itcast.hotel.pojo.Hotel;
import cn.itcast.hotel.pojo.HotelDoc;
import cn.itcast.hotel.service.IHotelService;
import com.alibaba.fastjson.JSON;
import org.apache.http.HttpHost;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.List;

import static cn.itcast.hotel.constants.HotelConstants.INDEX_HOTEL;

/**
 * @author codejuzi
 */
@SpringBootTest
class HotelDocumentTest {

    @Resource
    private IHotelService hotelService;

    private RestHighLevelClient client;


    @BeforeEach
    void setUp() {
        this.client = new RestHighLevelClient(RestClient.builder(
                HttpHost.create("http://101.43.45.97:9200")
        ));
    }

    @Test
    void testInsertDocument() throws IOException {
        // json数据准备
        Hotel hotel = hotelService.getById(61083L);
        HotelDoc hotelDoc = new HotelDoc(hotel);
        String hotelDocJson = JSON.toJSONString(hotelDoc);
        // 准备请求对象
        IndexRequest request = new IndexRequest(INDEX_HOTEL).id(hotelDoc.getId().toString());
        // 封装数据
        request.source(hotelDocJson, XContentType.JSON);
        // 发送请求
        client.index(request, RequestOptions.DEFAULT);
    }

    @Test
    void testGetDocument() throws IOException {
        // 准备请求对象 GET /hotel/_doc/{id}
        GetRequest request = new GetRequest(INDEX_HOTEL, "61083");
        // 发送请求
        GetResponse response = client.get(request, RequestOptions.DEFAULT);
        // 解析数据
        String hotelDocJson = response.getSourceAsString();
        HotelDoc hotelDoc = JSON.parseObject(hotelDocJson, HotelDoc.class);
        System.out.println("hotelDoc = " + hotelDoc);
    }

    @Test
    void testDeleteDocument() throws IOException {
        // 准备请求对象 DELETE /hotel/_doc/{id}
        DeleteRequest request = new DeleteRequest(INDEX_HOTEL, "61083");
        // 发送请求
        client.delete(request, RequestOptions.DEFAULT);
    }

    @Test
    void testUpdateDocument() throws IOException {
        // 准备请求对象 DELETE /hotel/_doc/{id}
        UpdateRequest request = new UpdateRequest(INDEX_HOTEL, "61083");
        request.doc(
                "price", "952",
                "starName", "四钻"
        );
        // 发送请求
        client.update(request, RequestOptions.DEFAULT);
    }

    @Test
    void testBulkInsert() throws IOException {
        // 准备数据
        List<Hotel> hotelList = hotelService.list();

        // 准备请求对象
        BulkRequest request = new BulkRequest(INDEX_HOTEL);
        // 封装数据
        for (Hotel hotel : hotelList) {
            HotelDoc hotelDoc = new HotelDoc(hotel);
            String hotelDocJson = JSON.toJSONString(hotelDoc);
            request.add(new IndexRequest().id(hotelDoc.getId().toString())
                    .source(hotelDocJson, XContentType.JSON));
        }
        // 发送请求
        client.bulk(request, RequestOptions.DEFAULT);
    }

    @AfterEach
    void tearDown() throws IOException {
        this.client.close();
    }
}

package cn.itcast.hotel.doc;

import cn.itcast.hotel.pojo.HotelDoc;
import com.alibaba.fastjson.JSON;
import org.apache.http.HttpHost;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.util.CollectionUtils;

import java.io.IOException;
import java.util.Collections;
import java.util.Map;

import static cn.itcast.hotel.constants.HotelConstants.INDEX_HOTEL;

/**
 * @author codejuzi
 */
class HotelSearchTest {

    private RestHighLevelClient client;

    @BeforeEach
    void setUp() {
        this.client = new RestHighLevelClient(RestClient.builder(
                HttpHost.create("http://101.43.45.97:9200")
        ));
    }

    @AfterEach
    void tearDown() throws IOException {
        this.client.close();
    }

    @Test
    void testMatchAllSearch() throws IOException {
        // 准备请求对象
        SearchRequest request = new SearchRequest(INDEX_HOTEL);
        // 封装请求条件
        request.source()
                .query(QueryBuilders.matchAllQuery());
        // 发送请求
        SearchResponse response = client.search(request, RequestOptions.DEFAULT);

        // 解析相应结果对象
        SearchHits hits = response.getHits();
        long value = hits.getTotalHits().value;
        Assertions.assertTrue(value > 0);
        System.out.println("value = " + value);
        parseDocuments(hits);
    }



    @Test
    void testMatchSearch() throws IOException {
        // 准备请求对象
        SearchRequest request = new SearchRequest(INDEX_HOTEL);
        // 封装请求条件
        request.source()
                .query(QueryBuilders.matchQuery("all", "如家"));
        // 发送请求
        SearchResponse response = client.search(request, RequestOptions.DEFAULT);
        // 解析文档数组
        parseDocuments(response.getHits());
    }

    @Test
    void testMultiMatchSearch() throws IOException {
        // 准备请求对象
        SearchRequest request = new SearchRequest(INDEX_HOTEL);
        // 封装请求条件
        request.source()
                .query(QueryBuilders.multiMatchQuery("如家", "name", "business"));
        // 发送请求
        SearchResponse response = client.search(request, RequestOptions.DEFAULT);
        // 解析文档数组
        parseDocuments(response.getHits());
    }

    @Test
    void testTermSearch() throws IOException {
        // 准备请求对象
        SearchRequest request = new SearchRequest(INDEX_HOTEL);
        // 封装请求条件
        request.source()
                .query(QueryBuilders.termQuery("city", "上海"));
        // 发送请求
        SearchResponse response = client.search(request, RequestOptions.DEFAULT);
        // 解析文档数组
        parseDocuments(response.getHits());
    }

    @Test
    void testRangeSearch() throws IOException {
        // 准备请求对象
        SearchRequest request = new SearchRequest(INDEX_HOTEL);
        // 封装请求条件
        request.source()
                .query(QueryBuilders.rangeQuery("price").gte(100).lte(400));
        // 发送请求
        SearchResponse response = client.search(request, RequestOptions.DEFAULT);
        // 解析文档数组
        parseDocuments(response.getHits());
    }


    @Test
    void testBooleanSearch() throws IOException {
        // 准备请求对象
        SearchRequest request = new SearchRequest(INDEX_HOTEL);
        // BooleanQuery对象
        BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();
        boolQuery.must(QueryBuilders.termQuery("city", "上海"))
                .filter(QueryBuilders.rangeQuery("price").lte(350));
        // 封装请求条件
        request.source()
                .query(boolQuery);
        // 发送请求
        SearchResponse response = client.search(request, RequestOptions.DEFAULT);
        // 解析文档数组
        parseDocuments(response.getHits());
    }

    @Test
    void testPageSearch() throws IOException {
        // 准备请求对象
        SearchRequest request = new SearchRequest(INDEX_HOTEL);
        // 封装请求条件
        request.source()
                .query(QueryBuilders.rangeQuery("price").gte(100).lte(400));
        // 分页
        int page = 1;
        int size = 5;
        request.source().from((page - 1) * size).size(size);
        // 发送请求
        SearchResponse response = client.search(request, RequestOptions.DEFAULT);
        // 解析文档数组
        parseDocuments(response.getHits());
    }

    @Test
    void testHighlightSearch() throws IOException {
        // 准备请求对象
        SearchRequest request = new SearchRequest(INDEX_HOTEL);
        // 封装请求条件
        request.source()
                .query(QueryBuilders.matchQuery("all", "如家"))
                .highlighter(new HighlightBuilder().field("name").requireFieldMatch(false));

        // 发送请求
        SearchResponse response = client.search(request, RequestOptions.DEFAULT);
        // 解析文档数组
        parseDocuments(response.getHits());
    }


    private void parseDocuments(SearchHits hits) {
        // 解析文档数组
        SearchHit[] searchHits = hits.getHits();
        for (SearchHit searchHit : searchHits) {
            String json = searchHit.getSourceAsString();
            Map<String, HighlightField> highlightFields = searchHit.getHighlightFields();
            Assertions.assertNotNull(highlightFields);
            if(!CollectionUtils.isEmpty(highlightFields)) {
                HighlightField highlightField = highlightFields.get("name");
                if(highlightField != null) {
                    System.out.println(highlightField.getFragments()[0].string());
                }
            }
            HotelDoc hotelDoc = JSON.parseObject(json, HotelDoc.class);
            System.out.println("hotelDoc = " + hotelDoc);
        }
    }

}

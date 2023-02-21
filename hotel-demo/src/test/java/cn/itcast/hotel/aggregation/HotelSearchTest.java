package cn.itcast.hotel.aggregation;

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
import org.elasticsearch.search.aggregations.Aggregation;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.BucketOrder;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.util.CollectionUtils;

import java.io.IOException;
import java.util.List;
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
    void testAggregation() throws IOException {
        // 准备请求对象
        SearchRequest request = new SearchRequest(INDEX_HOTEL);
        // 封装请求条件
       request.source().size(0);
       request.source().aggregation(AggregationBuilders
               .terms("brandAggs")
               .field("brand")
               .size(10)
//               .order(BucketOrder.aggregation("socre_stats.avg", false))
//               .subAggregation(AggregationBuilders.stats("score_stats")
//                       .field("score"))
       );

        // 发送请求
        SearchResponse response = client.search(request, RequestOptions.DEFAULT);
        // 解析聚合结果
        parseAggregations(response.getAggregations());
    }

    private void parseAggregations(Aggregations aggregations) {
        // 获取结果
        Terms terms = aggregations.get("brandAggs");
        List<? extends Terms.Bucket> buckets = terms.getBuckets();
        for (Terms.Bucket bucket : buckets) {
            System.out.println(bucket.getKey());
        }
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

package cn.itcast.hotel.suggest;

import cn.itcast.hotel.pojo.HotelDoc;
import com.alibaba.fastjson.JSON;
import org.apache.http.HttpHost;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.elasticsearch.search.suggest.Suggest;
import org.elasticsearch.search.suggest.SuggestBuilder;
import org.elasticsearch.search.suggest.SuggestBuilders;
import org.elasticsearch.search.suggest.completion.CompletionSuggestion;
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
    void testSuggest() throws IOException {
        // 准备请求
        SearchRequest request = new SearchRequest(INDEX_HOTEL);
        // 封装请求参数
        request.source()
                .suggest(new SuggestBuilder()
                        .addSuggestion(
                                "hotel_suggestion",
                                SuggestBuilders
                                        .completionSuggestion("suggestion")
                                        .prefix("hz")
                                        .skipDuplicates(true)
                                        .size(10))
                );
        // 发送请求
        SearchResponse response = client.search(request, RequestOptions.DEFAULT);
        // 解析响应
        Suggest suggest = response.getSuggest();
        // 根据名称获取补全结果
        CompletionSuggestion suggestion = suggest.getSuggestion("hotel_suggestion");
        // 获取options并遍历
        for (CompletionSuggestion.Entry.Option option : suggestion.getOptions()) {
            String text = option.getText().toString();
            System.out.println("text = " + text);
        }
    }

}

package cn.itcast.hotel.service.impl;

import cn.itcast.hotel.mapper.HotelMapper;
import cn.itcast.hotel.pojo.Hotel;
import cn.itcast.hotel.pojo.HotelDoc;
import cn.itcast.hotel.pojo.request.SearchPageHotelRequestParam;
import cn.itcast.hotel.pojo.result.SearchPageHotelResult;
import cn.itcast.hotel.service.IHotelService;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.geo.GeoPoint;
import org.elasticsearch.common.lucene.search.function.CombineFunction;
import org.elasticsearch.common.unit.DistanceUnit;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.functionscore.FunctionScoreQueryBuilder;
import org.elasticsearch.index.query.functionscore.ScoreFunctionBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.elasticsearch.search.suggest.Suggest;
import org.elasticsearch.search.suggest.SuggestBuilder;
import org.elasticsearch.search.suggest.SuggestBuilders;
import org.elasticsearch.search.suggest.completion.CompletionSuggestion;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static cn.itcast.hotel.constants.HotelConstants.INDEX_HOTEL;

@Service
public class HotelService extends ServiceImpl<HotelMapper, Hotel> implements IHotelService {

    @Resource
    private RestHighLevelClient client;


    @Override
    public SearchPageHotelResult searchHotel(SearchPageHotelRequestParam requestParam) {
        try {
            // ??????????????????
            SearchRequest request = new SearchRequest(INDEX_HOTEL);
            // ??????????????????
            buildBasicQuery(requestParam, request);
            // ??????
            int page = requestParam.getPage();
            int size = requestParam.getSize();
            request.source().from((page - 1) * size).size(size);

            // ??????
            String location = requestParam.getLocation();
            if (StringUtils.isNotBlank(location)) {
                request.source().sort(SortBuilders
                        .geoDistanceSort("location", new GeoPoint(location))
                        .order(SortOrder.ASC)
                        .unit(DistanceUnit.KILOMETERS));
            }

            // ????????????
            SearchResponse response = client.search(request, RequestOptions.DEFAULT);
            // ??????????????????
            return handleSearchResponse(response);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Map<String, List<String>> filters(SearchPageHotelRequestParam params) {
        try {
            // 1.??????Request
            SearchRequest request = new SearchRequest("hotel");
            // 2.??????DSL
            // 2.1.query
            buildBasicQuery(params, request);
            // 2.2.??????size
            request.source().size(0);
            // 2.3.??????
            buildAggregation(request);
            // 3.????????????
            SearchResponse response = client.search(request, RequestOptions.DEFAULT);
            // 4.????????????
            Map<String, List<String>> result = new HashMap<>();
            Aggregations aggregations = response.getAggregations();
            // 4.1.???????????????????????????????????????
            List<String> brandList = getAggByName(aggregations, "brandAgg");
            result.put("??????", brandList);
            // 4.2.???????????????????????????????????????
            List<String> cityList = getAggByName(aggregations, "cityAgg");
            result.put("??????", cityList);
            // 4.3.???????????????????????????????????????
            List<String> starList = getAggByName(aggregations, "starAgg");
            result.put("??????", starList);

            return result;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<String> getSuggestions(String prefix) {
        try {
            // ????????????
            SearchRequest request = new SearchRequest(INDEX_HOTEL);
            // ????????????
            request.source().suggest(new SuggestBuilder().addSuggestion(
                    "suggestions",
                    SuggestBuilders.completionSuggestion("suggestion")
                            .prefix(prefix)
                            .skipDuplicates(true)
                            .size(10)
            ));
            // ????????????
            SearchResponse response = client.search(request, RequestOptions.DEFAULT);
            // ????????????
            return handleSuggestions(response.getSuggest());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private List<String> handleSuggestions(Suggest suggest) {
        // ??????????????????????????????????????????
        CompletionSuggestion suggestions = suggest.getSuggestion("suggestions");
        List<CompletionSuggestion.Entry.Option> options = suggestions.getOptions();
        List<String> suggestionList = new ArrayList<>(options.size());
        for (CompletionSuggestion.Entry.Option option : options) {
            String text = option.getText().toString();
            suggestionList.add(text);
        }
        return suggestionList;
    }

    private void buildAggregation(SearchRequest request) {
        request.source().aggregation(AggregationBuilders
                .terms("brandAgg")
                .field("brand")
                .size(100)
        );
        request.source().aggregation(AggregationBuilders
                .terms("cityAgg")
                .field("city")
                .size(100)
        );
        request.source().aggregation(AggregationBuilders
                .terms("starAgg")
                .field("starName")
                .size(100)
        );
    }

    private List<String> getAggByName(Aggregations aggregations, String aggName) {
        // 4.1.????????????????????????????????????
        Terms brandTerms = aggregations.get(aggName);
        // 4.2.??????buckets
        List<? extends Terms.Bucket> buckets = brandTerms.getBuckets();
        // 4.3.??????
        List<String> brandList = new ArrayList<>();
        for (Terms.Bucket bucket : buckets) {
            // 4.4.??????key
            String key = bucket.getKeyAsString();
            brandList.add(key);
        }
        return brandList;
    }

    private void buildBasicQuery(SearchPageHotelRequestParam requestParam, SearchRequest request) {
        BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();

        // ????????? must
        String key = requestParam.getKey();
        if (StringUtils.isNotBlank(key)) {
            boolQuery.must(QueryBuilders.matchQuery("all", key));
        } else {
            boolQuery.must(QueryBuilders.matchAllQuery());
        }
        // ?????? filter
        String brand = requestParam.getBrand();
        if (StringUtils.isNotBlank(brand)) {
            boolQuery.filter(QueryBuilders.termQuery("brand", brand));
        }
        // ?????? filter
        String city = requestParam.getCity();
        if (StringUtils.isNotBlank(city)) {
            boolQuery.filter(QueryBuilders.termQuery("city", city));
        }
        // ?????? filter
        String starName = requestParam.getStarName();
        if (StringUtils.isNotBlank(starName)) {
            boolQuery.filter(QueryBuilders.termQuery("starName", starName));
        }
        // ?????? filter
        Integer minPrice = requestParam.getMinPrice();
        Integer maxPrice = requestParam.getMaxPrice();
        if (minPrice != null && maxPrice != null) {
            boolQuery.filter(QueryBuilders
                    .rangeQuery("price")
                    .lte(maxPrice)
                    .gte(minPrice));
        }

        // function score
        FunctionScoreQueryBuilder functionScoreQueryBuilder
                = QueryBuilders.functionScoreQuery(
                // ??????????????????
                boolQuery,
                // ??????????????????
                new FunctionScoreQueryBuilder.FilterFunctionBuilder[]{
                        new FunctionScoreQueryBuilder.FilterFunctionBuilder(
                                // ????????????
                                QueryBuilders.termQuery("isAD", true),
                                // ????????????
                                ScoreFunctionBuilders.weightFactorFunction(10)
                        )
                }
                // ????????????
        ).boostMode(CombineFunction.SUM);
        request.source().query(functionScoreQueryBuilder);
    }

    private SearchPageHotelResult handleSearchResponse(SearchResponse response) {
        SearchHits responseHits = response.getHits();
        long value = responseHits.getTotalHits().value;
        SearchHit[] hits = responseHits.getHits();
        List<HotelDoc> hotels = new ArrayList<>(hits.length);
        for (SearchHit hit : hits) {
            String hotelDocJson = hit.getSourceAsString();
            HotelDoc hotelDoc = JSON.parseObject(hotelDocJson, HotelDoc.class);
            // ????????????????????????
            Object[] sortValues = hit.getSortValues();
            if (sortValues.length > 0) {
                Object distance = sortValues[0];
                hotelDoc.setDistance(distance);
            }
            hotels.add(hotelDoc);
        }
        return new SearchPageHotelResult(value, hotels);
    }
}

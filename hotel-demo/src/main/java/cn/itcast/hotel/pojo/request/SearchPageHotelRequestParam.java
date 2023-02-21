package cn.itcast.hotel.pojo.request;

import lombok.Data;

/**
 * 酒店搜索分页请求参数
 *
 * @author codejuzi
 */
@Data
public class SearchPageHotelRequestParam {

    /**
     * 搜索关键字
     */
    private String key;

    /**
     * 排序规则
     */
    private String sortBy;

    private Integer page;

    private Integer size;

    private String brand;

    private String city;

    private String starName;

    private Integer maxPrice;

    private Integer minPrice;

    /**
     * 当前位置
     */
    private String location;

}

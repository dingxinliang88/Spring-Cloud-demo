package cn.itcast.hotel.pojo.result;

import cn.itcast.hotel.pojo.HotelDoc;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 酒店搜索分页返回结果
 *
 * @author codejuzi
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SearchPageHotelResult {

    /**
     * 总条数
     */
    private Long total;

    /**
     * 数据
     */
    private List<HotelDoc> hotels;

}

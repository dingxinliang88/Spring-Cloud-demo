package cn.itcast.hotel.service;

import cn.itcast.hotel.pojo.Hotel;
import cn.itcast.hotel.pojo.request.SearchPageHotelRequestParam;
import cn.itcast.hotel.pojo.result.SearchPageHotelResult;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;
import java.util.Map;

public interface IHotelService extends IService<Hotel> {

    SearchPageHotelResult searchHotel(SearchPageHotelRequestParam requestParam);

    Map<String, List<String>> filters(SearchPageHotelRequestParam requestParam);

    List<String> getSuggestions(String prefix);
}

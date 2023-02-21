package cn.itcast.hotel.controller;

import cn.itcast.hotel.pojo.request.SearchPageHotelRequestParam;
import cn.itcast.hotel.pojo.result.SearchPageHotelResult;
import cn.itcast.hotel.service.IHotelService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

/**
 * @author codejuzi
 */
@Slf4j
@RestController
@RequestMapping("/hotel")
public class HotelController {

    @Resource
    private IHotelService hotelService;

    @PostMapping("/list")
    public SearchPageHotelResult searchHotel(@RequestBody SearchPageHotelRequestParam requestParam) {
        return hotelService.searchHotel(requestParam);
    }

    @PostMapping("/filters")
    public Map<String, List<String>> getFilters(@RequestBody SearchPageHotelRequestParam requestParam) {
        return hotelService.filters(requestParam);
    }
    @GetMapping("/suggestion")
    public List<String> getSuggestions(@RequestParam("key") String prefix) {
        return hotelService.getSuggestions(prefix);
    }

}

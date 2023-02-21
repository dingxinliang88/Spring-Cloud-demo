package cn.itcast.hotel.service.impl;

import cn.itcast.hotel.service.IHotelService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author codejuzi
 */
@SpringBootTest
class HotelServiceTest {

    @Resource
    private IHotelService hotelService;

}
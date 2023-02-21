package cn.itcast.order.service;

import cn.itcast.feign.clients.UserClient;
import cn.itcast.feign.pojo.User;
import cn.itcast.order.mapper.OrderMapper;
import cn.itcast.order.pojo.Order;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;

@Service
public class OrderService {

    @Autowired
    private OrderMapper orderMapper;

    @Resource
    private RestTemplate restTemplate;

    @Resource
    private UserClient userClient;


    public Order queryOrderById(Long orderId) {
        // 1.查询订单
        Order order = orderMapper.findById(orderId);
        // 2. 发送Http请求
        User user = userClient.getUserById(order.getUserId());
        // 3.封装
        order.setUser(user);
        // 4.返回
        return order;
    }
}

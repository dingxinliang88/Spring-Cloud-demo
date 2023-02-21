package cn.itcast.feign.clients;

import cn.itcast.feign.pojo.User;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * @author codejuzi
 */
@FeignClient("userservice")
public interface UserClient {
    @GetMapping("/user/{id}")
    User getUserById(@PathVariable("id") Long id);
}

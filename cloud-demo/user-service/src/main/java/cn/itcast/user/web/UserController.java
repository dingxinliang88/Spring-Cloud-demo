package cn.itcast.user.web;

import cn.itcast.user.config.PatternProperties;
import cn.itcast.user.pojo.User;
import cn.itcast.user.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

@Slf4j
@RestController
@RequestMapping("/user")
//@RefreshScope
public class UserController {

    @Resource
    private UserService userService;

    @Resource
    private PatternProperties patternProperties;


    @GetMapping("/prop")
    public PatternProperties getProp() {
        return patternProperties;
    }


    @GetMapping("/now")
    public String getNow() {
        return LocalDateTime.now().format(DateTimeFormatter.ofPattern(patternProperties.getDateformat(), Locale.CHINA));
    }


    /**
     * 路径： /user/110
     *
     * @param id 用户id
     * @return 用户
     */
    @GetMapping("/{id}")
    public User queryById(@PathVariable("id") Long id) {
        return userService.queryById(id);
    }
}

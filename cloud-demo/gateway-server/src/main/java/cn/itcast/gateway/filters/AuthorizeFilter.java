package cn.itcast.gateway.filters;

import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * @author codejuzi
 */
@Order(-1)
@Component
public class AuthorizeFilter implements GlobalFilter {

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        // 获取请求头信息
        ServerHttpRequest request = exchange.getRequest();
        MultiValueMap<String, String> queryParamsMap = request.getQueryParams();
        String authrization = queryParamsMap.getFirst("authorization");
        // 校验
        if("admin".equals(authrization)) {
            // 放行
            return chain.filter(exchange);
        }
        // 设置401 && 拦截
        exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
        return exchange.getResponse().setComplete();
    }
}

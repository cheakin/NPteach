package cn.cheakin.gulimall.gateway.config;

import cn.cheakin.common.exception.BizCodeEnume;
import cn.cheakin.common.utils.R;
import com.alibaba.csp.sentinel.adapter.spring.webflux.callback.BlockRequestHandler;
import com.alibaba.fastjson.JSON;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.ServerResponse;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Configuration
public class SentinelGatewayConfig implements BlockRequestHandler {

    //网关限流了请求，就会调用此回调  Mono Flux
    @Override
    public Mono<ServerResponse> handleRequest(ServerWebExchange exchange, Throwable t) {
        R error = R.error(BizCodeEnume.TOO_MANY_REQUEST.getCode(), BizCodeEnume.TOO_MANY_REQUEST.getMsg());
        String errJson = JSON.toJSONString(error);

//                Mono<String> aaa = Mono.just("aaa");
        Mono<ServerResponse> body = ServerResponse.ok().body(Mono.just(errJson), String.class);
        return body;
    }

//        FlowRule flowRule = new FlowRule();
//        flowRule.setRefResource("gulimall_seckill_route");
////        flowRule.set
//        FlowRuleManager.loadRules(Arrays.asList(flowRule));
}
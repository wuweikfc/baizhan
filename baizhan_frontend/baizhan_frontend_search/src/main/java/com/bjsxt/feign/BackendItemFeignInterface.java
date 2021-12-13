package com.bjsxt.feign;

import com.bjsxt.commons.pojo.BaizhanResult;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;

/**
 * 后台商品系统微服务Openfeign接口
 */
@FeignClient("baizhan-backend-item")
public interface BackendItemFeignInterface {

    @PostMapping("/backend/item/getAllItems")
    BaizhanResult getAllItems();

}

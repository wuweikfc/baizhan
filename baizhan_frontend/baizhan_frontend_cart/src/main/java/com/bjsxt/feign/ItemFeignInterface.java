package com.bjsxt.feign;

import com.bjsxt.commons.pojo.BaizhanResult;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * 商品远程服务调用接口
 */
@FeignClient("baizhan-frontend-details")
public interface ItemFeignInterface {

    @PostMapping("/item/selectItemInfo")
    BaizhanResult getItemById(@RequestParam("id") Long id);

}

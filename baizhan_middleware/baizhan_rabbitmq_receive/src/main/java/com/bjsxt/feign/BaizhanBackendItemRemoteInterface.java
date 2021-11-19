package com.bjsxt.feign;

import com.bjsxt.commons.pojo.BaizhanResult;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient("baizhan-backend-item")
public interface BaizhanBackendItemRemoteInterface {

    @PostMapping("/forTrade/updateItemNum")
    BaizhanResult updateItemNum4Trade(@RequestParam("id") Long id, @RequestParam("num") Integer num);

}

package com.bjsxt.controller;

import com.bjsxt.commons.pojo.BaizhanResult;
import com.bjsxt.service.PortalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 前台门户控制器
 * 前端系统轮播展示有问题：
 * 解决办法：编辑文件 itbaizhan-shop-frontend\src\pages\Home\Content\LunBo\index.vue
 * 修改内容：
 * 23行：
 * 原：import { swiper , swiperSlide} from "vue-awesome-swiper";
 * 修改后：import { Swiper , SwiperSlide} from "vue-awesome-swiper";
 * 52-55行：
 * 原：components: {
 * swiper,
 * swiperSlide
 * }
 * 修改后：components: {
 * Swiper,
 * SwiperSlide
 * }
 */
@RestController
@CrossOrigin
public class PortalController {

    @Autowired
    private PortalService portalService;

    /**
     * 查询门户页面中的轮播广告数据。
     * 商业项目中。会使用软编码的形式，为门户中所有需要查询的内容做类型定义。
     * 当前系统中，使用application-commonspojo配置文件实现软编码配置。
     * <p>
     * 当前系统中查询的广告信息，是随机6条数据。
     * 不足6条的，全部查询，随机排序。
     * 后台管理的内容，是需要手工干预处理的，如：过期广告需要删除
     * <p>
     * 商业项目中，会划分级别，在不同的级别中，查询随机内容，拼接后再返回。
     * 后台管理的内容是有有效期限的，不需要人工处理。
     * 在新增内容的时候，需要设置生效时间和失效时间。查询的时候，当前时间在生效和失效时间之间
     * <p>
     * 默认逻辑：
     * 1 - 客户端访问门户页面
     * 2 - 门户页面异步访问后端系统，查询大广告
     * 3 - 后端系统，访问数据库，查询广告内容，并随机排序，返回6条数据。
     * <p>
     * 性能优化： 增加Redis缓存逻辑
     * 1 - 客户端访问门户页面
     * 2 - 门户页面异步访问后端系统
     * 3 - 后端系统，访问redis，查询大广告集合。如果存在则，随机排序，并返回前6条数据。
     * 4 - 如果redis没有广告缓存，后端系统访问数据库，查询广告集合，并保存到redis中。
     * 5 - 随机排序查询结果，并返回
     * <p>
     * 使用的缓存处理技术： 使用Spring Cache实现。
     * 商业项目中一般不使用Spring Cache。本处使用，是因为学习。
     *
     * @return { "data":[{TbContent对象},{}] }
     */
    @GetMapping("/portal/showBigAd")
    public BaizhanResult showBigAd() {

        return portalService.showBigAd();
    }


}

package com.bjsxt.service.impl;

import com.bjsxt.commons.pojo.BaizhanResult;
import com.bjsxt.management.ContentsManagement;
import com.bjsxt.pojo.TbContent;
import com.bjsxt.service.PortalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

/**
 * 前台门户服务实现
 */
@Service
public class PortalServiceImpl implements PortalService {

    @Value("${baizhan.mysql.bigad.id}")
    private Long bigAdCategoryId;

    @Autowired
    private ContentsManagement contentsManagement;

    /**
     * 查询门户页面中的大广告
     * 优化：
     * 把广告数据缓存到redis中
     * 缓存数据是什么？是查询的所有广告？还是随机排序后的6个广告
     * 只缓存6个广告，其他广告没有显示的机会
     * 必须缓存所有的广告数据
     *
     * @return
     */
    @Override
    public BaizhanResult showBigAd() {

        List<TbContent> contents = contentsManagement.queryContentsFromDB(bigAdCategoryId);

        //随机排序，截取前6条数据
        List<TbContent> resultList = getResults(contents);

        //返回结果
        return BaizhanResult.ok(resultList);
    }

    /**
     * 把参数集合做随机排序，并截取前6条数据返回，不足6条，返回全部数据
     *
     * @param resource
     * @return
     */
    private List<TbContent> getResults(List<TbContent> resource) {
        //随机排序
        Collections.shuffle(resource);
        //不足6条数据，直接返回
        if (resource.size() <= 6) {
            return resource;
        }

        //数据结果多于6条，删除多于部分
        for (int i = resource.size(); i > 5; i--) {
            resource.remove(i);
        }
        return resource;
    }

}

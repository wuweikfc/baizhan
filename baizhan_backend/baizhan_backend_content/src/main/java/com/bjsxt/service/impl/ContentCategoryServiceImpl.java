package com.bjsxt.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.bjsxt.commons.pojo.BaizhanResult;
import com.bjsxt.mapper.TbContentCategoryMapper;
import com.bjsxt.pojo.TbContentCategory;
import com.bjsxt.service.ContentCategoryService;
import com.bjsxt.utils.IDUtils;
import com.bjsxt.vo.ContentCategory4Backend;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 内容分类服务实现
 */
@Service
public class ContentCategoryServiceImpl implements ContentCategoryService {

    @Autowired
    private TbContentCategoryMapper contentCategoryMapper;

    /**
     * 根据内容分类父节点主键，查询子节点集合
     *
     * @param id
     * @return
     */
    @Override
    public BaizhanResult getContentCategoriesByParent(Long id) {

        QueryWrapper<TbContentCategory> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("parent_id", id);
        queryWrapper.eq("status", 1);
        List<TbContentCategory> list = contentCategoryMapper.selectList(queryWrapper);

        if (list == null || list.size() == 0) {
            //查询无结果
            return BaizhanResult.error("没有内容分类");
        }

        List<ContentCategory4Backend> data = new ArrayList<>();
        list.forEach(contentCategory -> {
            ContentCategory4Backend contentCategory4Backend = new ContentCategory4Backend();
            BeanUtils.copyProperties(contentCategory, contentCategory4Backend);
            data.add(contentCategory4Backend);
        });
        return BaizhanResult.ok(data);
    }

    /**
     * 创建内容分类
     * 注意：新增当前节点的时候，必须检查父节点的is_parent是否为true,如果值为false必须更新
     * 额外校验：新增分类的时候，查询父节点下的所有有效子节点，有没有和当前节点重名的，如果有，不创建当前节点
     *
     * @param contentCategory
     * @return
     */
    @Override
    @Transactional(rollbackFor = {RuntimeException.class})
    public BaizhanResult createContentCategory(TbContentCategory contentCategory) {

        //查询当前节点的有效兄弟节点
        List<TbContentCategory> siblings = (List<TbContentCategory>) getContentCategoriesByParent(contentCategory.getParentId()).getData();

        //检查是否有重名节点
        for (TbContentCategory sibling : siblings) {
            if (contentCategory.getName().equals(sibling.getName())) {
                //同名，不新增
                return BaizhanResult.error("有重名节点，请重新创建内容分类");
            }
        }

        //数据完整性
        contentCategory.setId(IDUtils.genItemId());
        contentCategory.setIsParent(false); //是否是父节点
        contentCategory.setSortOrder(1);    //排序字段
        contentCategory.setStatus(1);       //数据状态 1- 正常；2- 删除
        Date now = new Date();
        contentCategory.setCreated(now);
        contentCategory.setUpdated(now);

        int rows = contentCategoryMapper.insert(contentCategory);
        if (rows != 1) {
            throw new RuntimeException("新增内容分类错误");
        }

        //查询父节点
        TbContentCategory parent = contentCategoryMapper.selectById(contentCategory.getParentId());
        //判断父节点的is_parent是否为true
        if (!parent.getIsParent()) {
            //is_parent的属性为false，需要更新
            parent.setIsParent(true);
            parent.setUpdated(now);
            rows = contentCategoryMapper.updateById(parent);
            if (rows != 1) {
                throw new RuntimeException("更新父节点isParent属性错误");
            }
        }

        return BaizhanResult.ok();
    }

    /**
     * 删除内容分类
     * 级联删除所有子孙节点
     * 删除后，要检查父节点的isParent是否需要修改
     * 也就是删除后，父节点还有没有其他的有效子节点
     *
     * @param id
     * @return
     */
    @Override
    public BaizhanResult removeContentCategory(Long id) {
        //记录父节点信息
        TbContentCategory parent = contentCategoryMapper.selectById(contentCategoryMapper.selectById(id).getParentId());
        //删除
        removeContentCategoryById(id);
        //检查父节点有没有其他有效子节点?
        List<TbContentCategory> children = (List<TbContentCategory>) getContentCategoriesByParent(parent.getId()).getData();
        if (children == null || children.size() == 0) {
            //没有其他有效子节点
            //更新父节点isParent属性为false
            parent.setIsParent(false);
            parent.setUpdated(new Date());
            int rows = contentCategoryMapper.updateById(parent);
            if (rows != 1) {
                throw new RuntimeException("更新父节点isParent属性错误");
            }
        }

        return BaizhanResult.ok();
    }

    /**
     * 递归删除
     *
     * @param id
     */
    private void removeContentCategoryById(Long id) {
        //查询要删除的节点
        TbContentCategory current = contentCategoryMapper.selectById(id);
        //判断当前节点有没有子节点
        if (current.getIsParent()) {
            //有子节点
            //查询所有子节点
            List<TbContentCategory> children = (List<TbContentCategory>) getContentCategoriesByParent(id).getData();
            //删除子节点
            children.forEach(child -> {
                removeContentCategoryById(child.getId());
            });
        } else {
            //没有子节点
        }

        //删除当前节点，更新当前状态status=2
        current.setStatus(2);
        current.setUpdated(new Date());
        int rows = contentCategoryMapper.updateById(current);
        if (rows != 1) {
            throw new RuntimeException("删除内容分类错误，当前要删除的主键是：" + id);
        }

    }

    /**
     * 修改内容分类
     *
     * @param contentCategory
     * @return
     */
    @Override
    @Transactional(rollbackFor = {RuntimeException.class})
    public BaizhanResult modifyContentCategory(TbContentCategory contentCategory) {
        //同名校验
        //查询当前节点
        TbContentCategory current = contentCategoryMapper.selectById(contentCategory.getId());
        //查询兄弟节点
        List<TbContentCategory> siblings = (List<TbContentCategory>) getContentCategoriesByParent(current.getParentId()).getData();
        //检查同名
        for (TbContentCategory sibling : siblings) {
            if (contentCategory.getName().equals(sibling.getName())) {
                //有同名
                return BaizhanResult.error("更新后的内容分类有同名节点，请重新修改");
            }
        }

        contentCategory.setUpdated(new Date());
        int rows = contentCategoryMapper.updateById(contentCategory);
        if (rows != 1) {
            throw new RuntimeException("更新内容分类错误");
        }
        return BaizhanResult.ok();
    }

}

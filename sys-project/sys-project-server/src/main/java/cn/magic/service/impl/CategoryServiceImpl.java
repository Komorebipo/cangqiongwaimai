package cn.magic.service.impl;

import cn.magic.constant.MessageConstant;
import cn.magic.constant.StatusConstant;
import cn.magic.dto.CategoryDTO;
import cn.magic.dto.CategoryPageQueryDTO;
import cn.magic.entity.Category;
import cn.magic.mapper.CategoryMapper;
import cn.magic.mapper.DishMapper;
import cn.magic.mapper.SetmealMapper;
import cn.magic.service.CategoryService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import cn.magic.exception.DeletionNotAllowedException;
import java.time.LocalDateTime;
import java.util.List;

@Service
@Slf4j
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, Category> implements CategoryService {
    @Autowired
    private CategoryMapper categoryMapper;
    @Autowired
    private DishMapper dishMapper;
    @Autowired
    private SetmealMapper setmealMapper;

    /**
     * 新增分类
     */
    public int save(CategoryDTO categoryDTO) {
        Category category = new Category();
        BeanUtils.copyProperties(categoryDTO, category);
        category.setStatus(StatusConstant.DISABLE);
        category.setCreateTime(LocalDateTime.now());
        category.setUpdateTime(LocalDateTime.now());
        category.setCreateUser(10L);
        category.setUpdateUser(10L);
        return categoryMapper.insert(category);
    }

    /**
     * 分页查询 - 修复：使用正确的分页查询方式
     */
    public Page<Category> pageQuery(CategoryPageQueryDTO categoryPageQueryDTO) {
        log.info("分页查询：{}", categoryPageQueryDTO);
        Page<Category> page = new Page<>(categoryPageQueryDTO.getCurPage(), categoryPageQueryDTO.getPageSize());
        QueryWrapper<Category> query = new QueryWrapper<>();

        query.lambda()
                .like(StringUtils.isNotEmpty(categoryPageQueryDTO.getName()), Category::getName, categoryPageQueryDTO.getName())
                .eq(categoryPageQueryDTO.getType() != null, Category::getType, categoryPageQueryDTO.getType())
                .orderByAsc(Category::getSort)
                .orderByAsc(Category::getUpdateTime);

        // 修复：使用 baseMapper.selectPage() 进行分页查询
        return baseMapper.selectPage(page, query);
    }

    /**
     * 根据id删除分类
     */
    public int deleteById(Long id) {
        Integer count = dishMapper.countByCategoryId(id);
        if (count > 0) {
            throw new DeletionNotAllowedException(MessageConstant.CATEGORY_BE_RELATED_BY_DISH);
        }
        count = setmealMapper.countByCategoryId(id);
        if (count > 0) {
            throw new DeletionNotAllowedException(MessageConstant.CATEGORY_BE_RELATED_BY_SETMEAL);
        }
        return categoryMapper.deleteById(id);
    }

    /**
     * 修改分类 - 修复：确保 updateTime 和 updateUser 被正确设置
     */
    public int update(CategoryDTO categoryDTO) {
        Category category = new Category();
        BeanUtils.copyProperties(categoryDTO, category);
        category.setUpdateTime(LocalDateTime.now());
        category.setUpdateUser(1L);

        // 修复：使用 MyBatis Plus 的 updateById 方法
        return categoryMapper.updateById(category);
    }

    /**
     * 启用、禁用分类 - 修复：添加 updateTime 和 updateUser
     */
    public int startOrStop(Integer status, Long id) {
        Category category = Category.builder()
                .id(id)
                .status(status)
                .updateTime(LocalDateTime.now())  // 修复：取消注释
                .updateUser(1L)                   // 修复：取消注释，使用假数据
                .build();

        // 修复：使用 MyBatis Plus 的 updateById 方法
        return categoryMapper.updateById(category);
    }

    /**
     * 根据类型查询分类
     */
    public List<Category> list(Integer type) {
        return categoryMapper.list(type);
    }
}
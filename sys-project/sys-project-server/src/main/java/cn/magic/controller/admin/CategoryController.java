package cn.magic.controller.admin;

import cn.magic.dto.CategoryDTO;
import cn.magic.dto.CategoryPageQueryDTO;
import cn.magic.entity.Category;
import cn.magic.service.CategoryService;
import cn.magic.utils.ResultVo;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 分类管理
 */
@RestController("adminCategoryController")
@RequestMapping("/admin/category")
@Tag(name = "分类相关接口")
@Slf4j
public class CategoryController {
    @Autowired
    private CategoryService categoryService;

    /**
     * 新增分类
     * @param categoryDTO
     * @return
     */
    @PostMapping
    @Operation(summary = "新增分类")
    public ResultVo save(@RequestBody CategoryDTO categoryDTO){
        log.info("新增分类：{}", categoryDTO);
        categoryService.save(categoryDTO);
        return ResultVo.success("新增成功！");
    }

    /**
     * 分类分页查询
     * @param categoryPageQueryDTO
     * @return
     */
    @GetMapping("/page")
    @Operation(summary = "分类分页查询")
    public ResultVo page(CategoryPageQueryDTO categoryPageQueryDTO){
        log.info("分页查询：{}", categoryPageQueryDTO);
        Page<Category> categoryPage = categoryService.pageQuery(categoryPageQueryDTO);
        return ResultVo.success(categoryPage);
    }

    /**
     * 删除分类
     * @param id
     * @return
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "删除分类")
    public ResultVo deleteById(@PathVariable("id") Long id){
        log.info("删除分类：{}", id);
        int row = categoryService.deleteById(id);
        return ResultVo.success("删除成功！");
    }

    /**
     * 修改分类
     * @param categoryDTO
     * @return
     */
    @PutMapping
    @Operation(summary = "修改分类")
    public ResultVo update(@RequestBody CategoryDTO categoryDTO){
        categoryService.update(categoryDTO);
        return ResultVo.success("修改分类成功！");
    }

    /**
     * 启用、禁用分类
     * @param status
     * @param id
     * @return
     */
    @PostMapping("/status/{status}/{id}")
    @Operation(summary = "启用禁用分类")
    public ResultVo startOrStop(@PathVariable("status") Integer status, @PathVariable("id") Long id){
        categoryService.startOrStop(status,id);
        return ResultVo.success("启用禁用分类成功！");
    }

    /**
     * 根据类型查询分类
     * @param type
     * @return
     */
    @GetMapping("/list")
    @Operation(summary = "根据类型查询分类")
    public ResultVo list(Integer type){
        List<Category> list = categoryService.list(type);
        return ResultVo.success(list);
    }
}
package cn.magic.controller.admin;

import cn.magic.dto.DishDTO;
import cn.magic.dto.DishPageQueryDTO;
import cn.magic.entity.Dish;
import cn.magic.service.DishService;
import cn.magic.utils.ResultVo;
import cn.magic.vo.DishVO;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 菜品管理
 */
@RestController
@RequestMapping("/admin/dish")
@Tag(name = "菜品相关接口")
@Slf4j
public class DishController {
    @Autowired
    private DishService dishService;
    /**
     * 菜品分页查询
     * @param dishPageQueryDTO
     * @return
     */
    @GetMapping("/page")
    @Operation(summary = "菜品分页查询")
    public ResultVo page(DishPageQueryDTO dishPageQueryDTO) {
        log.info("菜品分页查询:{}", dishPageQueryDTO);
        Page<DishVO> dishVOPage = dishService.pageQuery(dishPageQueryDTO);
        return ResultVo.success(dishVOPage);
    }

    /**
     * 菜品批量删除
     * @param ids
     * @return
     */
    @DeleteMapping
    @Operation(summary = "菜品批量删除")
    public ResultVo delete(@RequestParam List<Long> ids) {
        log.info("菜品批量删除：{}", ids);
        dishService.deleteBatch(ids);//后绪步骤实现
        return ResultVo.success("删除成功！");
    }
    /**
     * 新增菜品
     * @param dishDTO
     * @return
     */
    @PostMapping
    @Operation(summary = "新增菜品")
    public ResultVo save(@RequestBody DishDTO dishDTO) {
        log.info("新增菜品：{}", dishDTO);
        dishService.saveWithFlavor(dishDTO);
        return ResultVo.success("增加成功");
    }
    /**
     * 根据id查询菜品
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    @Operation(summary = "根据id查询菜品")
    public ResultVo getById(@PathVariable Long id) {
        log.info("根据id查询菜品：{}", id);
        DishVO dishVO = dishService.getByIdWithFlavor(id);
        return ResultVo.success(dishVO);
    }

    /**
     * 修改菜品
     * @param dishDTO
     * @return
     */
    @PutMapping
    @Operation(summary = "修改菜品")
    public ResultVo update(@RequestBody DishDTO dishDTO) {
        log.info("修改菜品：{}", dishDTO);
        dishService.updateWithFlavor(dishDTO);
        return ResultVo.success("修改成功！");
    }
    /**
     * 菜品起售停售
     * @param status
     * @param id
     * @return
     */
    @PostMapping("/status/{status}/{id}")
    @Operation(summary = "菜品起售停售")
    public ResultVo startOrStop(@PathVariable("status") Integer status, @PathVariable("id") Long id) {
        dishService.startOrStop(status, id);
        return ResultVo.success();
    }
    /**
     * 根据分类id查询菜品
     *
     * @param categoryId
     * @return
     */
    @GetMapping("/list")
    @Operation(summary = "根据分类id查询菜品")
    public ResultVo list(Long categoryId) {
        List<Dish> list = dishService.list(categoryId);
        return ResultVo.success(list);
    }
}
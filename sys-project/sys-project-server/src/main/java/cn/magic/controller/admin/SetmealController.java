package cn.magic.controller.admin;

import cn.magic.dto.SetmealDTO;
import cn.magic.dto.SetmealPageQueryDTO;
import cn.magic.service.SetmealService;
import cn.magic.utils.ResultVo;
import cn.magic.vo.SetmealVO;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/setmeal")
@Tag(name = "套餐相关接口")
@Slf4j
public class SetmealController {

    @Autowired
    private SetmealService setmealService;

    @PostMapping
    @Operation(summary = "新增套餐")
    public ResultVo save(@RequestBody SetmealDTO setmealDTO) {
        setmealService.saveWithDish(setmealDTO);
        return ResultVo.success("增加成功！");
    }

    @GetMapping("/page")
    @Operation(summary = "分页查询")
    public ResultVo page(SetmealPageQueryDTO setmealPageQueryDTO) {
        Page<SetmealVO> pageResult = setmealService.pageQuery(setmealPageQueryDTO);
        return ResultVo.success(pageResult);
    }

    @DeleteMapping
    @Operation(summary = "批量删除套餐")
    public ResultVo delete(@RequestParam List<Long> ids) {
        setmealService.deleteBatch(ids);
        return ResultVo.success("删除成功！");
    }

    @GetMapping("/{id}")
    @Operation(summary = "根据id查询套餐")
    public ResultVo getById(@PathVariable Long id) {
        SetmealVO setmealVO = setmealService.getByIdWithDish(id);
        return ResultVo.success(setmealVO);
    }

    @PutMapping
    @Operation(summary = "修改套餐")
    public ResultVo update(@RequestBody SetmealDTO setmealDTO) {
        setmealService.update(setmealDTO);
        return ResultVo.success("修改成功！");
    }

    @PostMapping("/status/{status}/{id}")
    @Operation(summary = "套餐起售停售")
    public ResultVo startOrStop(@PathVariable("status") Integer status, @PathVariable("id") Long id) {
        setmealService.startOrStop(status, id);
        return ResultVo.success("修改成功！");
    }
}
package cn.magic.controller.user;

import cn.magic.entity.Setmeal;
import cn.magic.service.SetmealService;
import cn.magic.utils.ResultVo;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 小程序端套餐相关接口
 */
@RestController("userSetmealController")
@RequestMapping("/user/setmeal")
@Tag(name = "小程序端套餐相关接口")
@Slf4j
public class SetmealController {

    @Autowired
    private SetmealService setmealService;

    /**
     * 根据分类查询套餐
     * @param categoryId
     * @return
     */
    @GetMapping("/list")
    @Operation(summary = "根据分类查询套餐")
    public ResultVo list(@RequestParam(required = false) Long categoryId) {
        log.info("查询套餐列表，分类ID：{}", categoryId);
        
        QueryWrapper<Setmeal> wrapper = new QueryWrapper<>();
        wrapper.lambda().eq(Setmeal::getStatus, 1); // 只查询在售套餐
        
        if (categoryId != null && categoryId > 0) {
            wrapper.lambda().eq(Setmeal::getCategoryId, categoryId);
        }
        
        wrapper.lambda().orderByDesc(Setmeal::getUpdateTime);
        
        List<Setmeal> setmealList = setmealService.list(wrapper);
        return ResultVo.success("查询成功", setmealList);
    }
}

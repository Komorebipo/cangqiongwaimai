package cn.magic.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
//分类分页查询参数
@Data
@Schema(description = "分类分页查询参数")
public class CategoryPageQueryDTO {

    @Schema(description = "当前页码", example = "1")
    private Integer curPage; //当前页码

    @Schema(description = "每页显示多少条数据", example = "10")
    private Integer pageSize;  //每页显示多少条数据

    @Schema(description = "分类名称")
    private String name; //分类名称

    @Schema(description = "分类类型 1菜品分类  2套餐分类")
    private Integer type; //分类类型 1菜品分类  2套餐分类
}
package cn.magic.dto;

import lombok.Data;

import java.io.Serializable;
//菜品分页查询参数
@Data
public class DishPageQueryDTO implements Serializable {
    //当前页码
    private Integer curPage;
    //每页显示多少条数据
    private Integer pageSize;
    //菜品名称
    private String name;
    //分类id
    private Integer categoryId;
    //状态 0表示禁用 1表示启用
    private Integer status;
}
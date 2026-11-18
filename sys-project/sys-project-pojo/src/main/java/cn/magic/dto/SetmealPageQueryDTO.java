package cn.magic.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class SetmealPageQueryDTO implements Serializable {
    private Integer curPage;
    private Integer pageSize;
    private String name;
    private Integer categoryId;
    private Integer status;
}
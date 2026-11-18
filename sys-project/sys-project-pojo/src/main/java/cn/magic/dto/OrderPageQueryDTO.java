package cn.magic.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class OrderPageQueryDTO implements Serializable {
    private Integer curPage;
    private Integer pageSize;
    private String keyword;
    private Integer status;
    private String beginTime;
    private String endTime;
}
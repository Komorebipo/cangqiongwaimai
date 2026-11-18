package cn.magic.dto;

import lombok.Data;

//员工分页 参数
@Data
public class EmployeePageQueryDTO {

    private Integer curPage; //当前页码

    private Integer pageSize;  //每页显示多少条数据

    private String name; //员工姓名

}

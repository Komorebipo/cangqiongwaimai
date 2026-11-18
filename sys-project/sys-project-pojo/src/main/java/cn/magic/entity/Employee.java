package cn.magic.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

// 员工
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor // 无参构造
@TableName("employee")   // 表名
public class Employee implements Serializable {
    private static final long serialVersionUID = 1L;
    // 主键
    @TableId(type = IdType.AUTO)
    private Long id;
    // 用户名
    private String username;
    // 姓名
    private String name;
    // 密码
    private String password;
    // 手机
    private String phone;
    // 性别
    private String sex;
    // 身份证号
    private String idNumber;
    // 状态 0:禁用，1:正常
    private Integer status;
    // 创建时间
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;
    // 更新时间
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateTime;
    // 创建人
    private Long createUser;
    // 更新人
    private Long updateUser;
}

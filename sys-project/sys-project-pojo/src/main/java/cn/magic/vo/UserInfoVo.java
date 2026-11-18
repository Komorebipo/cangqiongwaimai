package cn.magic.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 登录成功后返回的数据
 */
@Data
public class UserInfoVo {
    private Long id; // 主键
    private String name; // 真实姓名
    private String username; // 系统账号
    private String password; // 密码
    private String phone; // 手机号
    private String idNumber; // 邮箱地址

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime; // 创建时间
}
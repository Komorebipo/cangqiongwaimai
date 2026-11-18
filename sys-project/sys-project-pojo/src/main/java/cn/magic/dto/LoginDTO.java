package cn.magic.dto;

import lombok.Data;

/**
 * 登录参数
 */
@Data
public class LoginDTO {
    /**
     * 用户名
     * 不能为空，长度3-20个字符
     */
    private String username;

    /**
     * 密码
     * 不能为空，长度6-20个字符
     */
    private String password;

    /**
     * 验证码
     * 不能为空，长度为4个字符
     */
    private String captcha;
}
package cn.magic.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class UpdatePasswordDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    private String oldPassword; // 原密码
    private String password; // 新密码
    private Long userId; // 用户id
}
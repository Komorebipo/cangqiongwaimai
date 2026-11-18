package cn.magic.controller.user;

import cn.magic.dto.UpdatePasswordDTO;
import cn.magic.dto.UserLoginDTO;
import cn.magic.entity.User;
import cn.magic.service.UserService;
import cn.magic.utils.ResultVo;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

// 小程序端用户相关接口
@RestController("userUserController")
@RequestMapping("/user/user")
@Tag(name= "小程序端用户相关接口")
@Slf4j
public class UserController {
    @Autowired
    private UserService userService;

    /**
     * 微信登录
     * @param userLoginDTO
     * @return
     */
    @PostMapping("/login")
    @Operation(summary = "微信登录")
    public ResultVo login(@RequestBody UserLoginDTO userLoginDTO){
        log.info("微信用户登录：{}",userLoginDTO.getName());
        //获取用户信息，判断用户是否存在
        QueryWrapper<User> wrapper = new QueryWrapper<>();
        wrapper.lambda().eq(User::getName,userLoginDTO.getName());// 条件
        User user = userService.getOne(wrapper);// 查询用户名
        if(user == null){
            return ResultVo.error("用户不存在");
        }
        //密码比对
        if(!user.getPassword().equals(DigestUtils.md5DigestAsHex(userLoginDTO.getPassword().getBytes()))){
            return ResultVo.error("密码错误");
        }
        return ResultVo.success("登录成功",user);
    }
    /**
     * 用户注册
     * @return
     */
    @PostMapping("/register")
    @Operation(summary = "用户注册")
    public ResultVo register(@RequestBody User user){
        log.info("用户注册：{}",user);
        //判断用户是否被占用
        QueryWrapper<User> wrapper = new QueryWrapper<>();
        wrapper.lambda().eq(User::getName,user.getName());// 用户名
        // 查询用户名
        User one = userService.getOne(wrapper);
        if(one != null){
            return ResultVo.error("用户名被占用");
        }
        //密码加密
        String pwd = DigestUtils.md5DigestAsHex(user.getPassword().getBytes());
        user.setPassword(pwd);
        user.setCreateTime(LocalDateTime.now());

        if( userService.save(user)){
            return ResultVo.success("注册成功");
        }else {
            return ResultVo.error("注册失败");
        }
    }
    /**
     * 修改密码
     * @param updatePasswordDTO
     * @return
     */
    @PostMapping("/updatePassword")
    @Operation(summary = "修改密码")
    public ResultVo updatePassword(@RequestBody UpdatePasswordDTO updatePasswordDTO){
        log.info("修改密码：{}",updatePasswordDTO);
        //判断原密码是否正确  select * from user where id = ? and password = ?
        QueryWrapper<User> query = new QueryWrapper<>();
        query.lambda().eq(User::getId,updatePasswordDTO.getUserId())
                .eq(User::getPassword,DigestUtils.md5DigestAsHex(updatePasswordDTO.getOldPassword().getBytes()));
        User one = userService.getOne(query);//查询用户
        if(one == null){
            return ResultVo.error("原密码不正确!");
        }
        //修改密码  update user set password = ? where id = ?
        UpdateWrapper<User> update = new UpdateWrapper<>();
        update.lambda().set(User::getPassword,DigestUtils.md5DigestAsHex(updatePasswordDTO.getPassword().getBytes()))
                .eq(User::getId,updatePasswordDTO.getUserId());
        if(userService.update(update)){
            return ResultVo.success("密码修改成功!");
        }
        return ResultVo.error("密码修改失败！");
    }

    /**
     * 修改用户信息
     * @param user
     *  @return
     */
    @PostMapping("/editInfo")
    @Operation(summary = "修改用户信息")
    public ResultVo editInfo(@RequestBody User user){
        log.info("修改用户信息：{}",user);
        //修改用户信息 update user set name = ?,phone = ?,sex = ?,id_number = ?,avatar = ? where id = ?
        if(userService.updateById(user)){
            return ResultVo.success("修改成功!");
        }
        return ResultVo.error("修改失败！");
    }
    /**
     * 获取用户信息
     * @param userId
     * @return
     */
    @GetMapping("/getUserInfo")
    @Operation(summary = "获取用户信息")
    public ResultVo getUserInfo(Integer userId){
        log.info("获取用户信息：{}",userId);
        //select * from user where id = ?
        QueryWrapper<User> query = new QueryWrapper<>();
        query.lambda().eq(User::getId,userId);
        User one = userService.getOne(query);
        return ResultVo.success("获取用户信息成功！",one);
    }
}

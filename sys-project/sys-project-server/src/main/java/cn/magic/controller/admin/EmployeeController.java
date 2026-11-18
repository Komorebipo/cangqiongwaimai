package cn.magic.controller.admin;

import cn.magic.dto.EmployeeDTO;
import cn.magic.dto.EmployeePageQueryDTO;
import cn.magic.dto.LoginDTO;
import cn.magic.dto.UpdatePasswordDTO;
import cn.magic.entity.Employee;
import cn.magic.service.EmployeeService;
import cn.magic.utils.ResultVo;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.google.code.kaptcha.impl.DefaultKaptcha;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

/**
 * 员工管理
 */
@RestController
@RequestMapping("/admin/employee")
@Slf4j
@Tag(name = "员工相关接口")  // swagger文档 https://doc.xiaominfo.com/docs/quick-start
public class EmployeeController {
    @Autowired
    private EmployeeService employeeService;

    @Autowired
    private DefaultKaptcha defaultKaptcha; // 验证码

    private String captchaText = ""; // 验证码

    /**
     * 获取验证码接口
     * @return 验证码key和base64图片
     */
    @GetMapping("/captcha")
    @Operation(summary="获取验证码")
    public ResultVo getCaptcha() {
        // 1. 生成验证码文本（4位随机字符）
        captchaText = defaultKaptcha.createText(); // 4位随机字符
        log.debug("生成验证码文本: {}", captchaText);

        // 2. 根据验证码文本生成图片
        BufferedImage image = defaultKaptcha.createImage(captchaText);

        // 3. 将图片转换为Base64编码
        String base64Image = null;
        try (java.io.ByteArrayOutputStream outputStream = new java.io.ByteArrayOutputStream()) {
            // 将图片写入字节数组输出流
            ImageIO.write(image, "jpg", outputStream);
            byte[] bytes = outputStream.toByteArray();
            // 将字节数组转换为Base64字符串
            Base64.Encoder encoder = Base64.getEncoder();
            base64Image = encoder.encodeToString(bytes);

        } catch (Exception e) {
            log.error("生成验证码失败", e);
            throw new RuntimeException("生成验证码失败");
        }

        // 6. 返回验证码信息
        Map<String, String> result = new HashMap<>();
        result.put("captchaImage", "data:image/jpeg;base64," + base64Image); // 验证码图片base64

        return ResultVo.success(result);
    }

    /**
     * 用户登录接口
     * @param loginDTO 登录参数（用户名、密码、验证码）
     * @return 登录结果，包含用户信息
     */
    @PostMapping("/login")
    @Operation(summary="用户登录")
    public ResultVo login(@RequestBody LoginDTO loginDTO) {
        //1、验证码校验
        if (!captchaText.equals(loginDTO.getCaptcha())) {
            return ResultVo.error("验证码错误!!");
        }
        //2、判断账号是否存在
        QueryWrapper<Employee> query = new QueryWrapper<>();
        query.lambda().eq(Employee::getUsername, loginDTO.getUsername());

        Employee employee = employeeService.getOne(query);
        if(employee == null){
            return ResultVo.error("用户名不存在！");
        }
        //3、密码比对
        String inputPassword = org.springframework.util.DigestUtils.md5DigestAsHex(loginDTO.getPassword().getBytes());
        String dbPassword = employee.getPassword();
        // 如果数据库密码是32位MD5格式，则比对MD5；否则比对明文
        boolean passwordMatch = (dbPassword.length() == 32 && dbPassword.matches("[0-9a-f]{32}"))
                ? inputPassword.equals(dbPassword)
                : loginDTO.getPassword().equals(dbPassword);
        if (!passwordMatch) {
            return ResultVo.error("密码错误！");
        }
        //4、判断账号是否被禁用
        if (employee.getStatus() == 0) {
            return ResultVo.error("账号被禁用！");
        }

        //5、返回登录结果
        return ResultVo.success(employee);
    }

    //修改密码
    @PutMapping("/updatePassword")
    @Operation(summary = "修改密码")
    public ResultVo updatePassword(@RequestBody UpdatePasswordDTO param){
        return employeeService.updatePwd(param);
    }

    /**
     * 新增员工
     * @param employeeDTO
     * @return
     */
    @PostMapping
    @Operation(summary="新增员工")
    public ResultVo save(@RequestBody EmployeeDTO employeeDTO){
        log.info("新增员工：{}",employeeDTO);
        employeeService.save(employeeDTO);
        return ResultVo.success();
    }

    /**
     * 启用禁用员工账号
     * @param status
     * @param id
     * @return
     */
    @PostMapping("/status/{status}/{id}")
    @Operation(summary = "启用禁用员工账号")
    public ResultVo startOrStop(@PathVariable("status") Integer status,@PathVariable("id")Long id){
        log.info("启用禁用员工账号：{},{}",status,id);
        employeeService.startOrStop(status,id);//后绪步骤定义
        return ResultVo.success("修改成功！");
    }


    @GetMapping("/page")
    @Operation(summary="分页查询")
    public ResultVo page(EmployeePageQueryDTO employeePageQueryDTO){
        log.info("分页查询：{}",employeePageQueryDTO);
        Page page = employeeService.pageQuery(employeePageQueryDTO);
        return ResultVo.success(page);
    }
    /**
     * 根据id查询员工信息
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    @Operation(summary = "根据id查询员工信息")
    public ResultVo getById(@PathVariable Long id){
        Employee employee = employeeService.getById(id);
        return ResultVo.success(employee);
    }
    /**
     * 编辑员工信息
     * @param employeeDTO
     * @return
     */
    @PutMapping
    @Operation(summary = "编辑员工信息")
    public ResultVo update(@RequestBody EmployeeDTO employeeDTO){
        log.info("编辑员工信息：{}", employeeDTO);
        employeeService.update(employeeDTO);
        return ResultVo.success("修改成功！");
    }
    /**
     * 删除员工信息
     *  @param id
     *  @return
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "删除员工信息")
    public ResultVo delete(@PathVariable Long id){
        log.info("删除员工信息：{}", id);
        boolean row = employeeService.removeById(id);
        if (row){
            return ResultVo.success("删除成功！");
        }
        return ResultVo.error("删除失败！");
    }
}

package cn.magic.service.impl;

import cn.magic.constant.PasswordConstant;
import cn.magic.constant.StatusConstant;
import cn.magic.dto.EmployeeDTO;
import cn.magic.dto.EmployeePageQueryDTO;
import cn.magic.dto.UpdatePasswordDTO;
import cn.magic.entity.Employee;
import cn.magic.mapper.EmployeeMapper;
import cn.magic.service.EmployeeService;
import cn.magic.utils.ResultVo;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import java.time.LocalDateTime;

//实现类
@Service
public class EmployeeServiceImpl extends ServiceImpl<EmployeeMapper, Employee> implements EmployeeService {


    @Override
    public int save(EmployeeDTO employeeDTO) {
        //创建对象
        Employee employee = new Employee();

        //对象属性拷贝
        BeanUtils.copyProperties(employeeDTO, employee);

        //设置账号的状态，默认正常状态 1表示正常 0表示锁定
        employee.setStatus(StatusConstant.ENABLE);

        //设置密码，默认密码123456   md5加密
        employee.setPassword(DigestUtils.md5DigestAsHex(PasswordConstant.DEFAULT_PASSWORD.getBytes()));

        //设置当前记录的创建时间和修改时间
        employee.setCreateTime(LocalDateTime.now());
        employee.setUpdateTime(LocalDateTime.now());

        //设置当前记录创建人id和修改人id
        employee.setCreateUser(10L);//目前写个假数据，后期修改
        employee.setUpdateUser(10L);

        return baseMapper.insert(employee);//插入数据
    }

    /**
     * 启用禁用员工账号
     *
     * @param status
     * @param id
     */
    @Override
    public void startOrStop(Integer status, Long id) {
        //创建一个员工对象
        Employee employee = Employee.builder()
                .status(status)
                .id(id)
                .build();
        //调用修改员工状态方法
        baseMapper.update(employee);
    }

    /**
     *  分页查询
     * @param employeePageQueryDTO
     * @return
     */
    @Override
    public Page<Employee> pageQuery(EmployeePageQueryDTO employeePageQueryDTO) {
        // 构造分页对象， 设置当前页和每页记录数
        Page<Employee> page = new Page<>(employeePageQueryDTO.getCurPage(), employeePageQueryDTO.getPageSize());
        // 调用分页查询方法
        return baseMapper.pageQuery(page, employeePageQueryDTO.getName());
    }
    /**
     * 根据id查询员工
     *
     * @param id
     * @return
     */
    public Employee getById(Long id) {
        Employee employee = this.baseMapper.getById(id);
        employee.setPassword("****");
        return employee;
    }
    /**
     * 编辑员工信息
     * @param employeeDTO
     */
    public void update(EmployeeDTO employeeDTO) {
        Employee employee = new Employee();
        //对象属性拷贝
        BeanUtils.copyProperties(employeeDTO, employee);
        //设置修改时间
        employee.setUpdateTime(LocalDateTime.now());
        //设置修改人
        //employee.setUpdateUser(BaseContext.getCurrentId());

        baseMapper.update(employee);
    }

    /**
     * 修改密码
     * @param param
     * @return
     */
    @Override
    public ResultVo updatePwd(UpdatePasswordDTO param) {
        //验证原密码是否正确 - 直接从mapper获取，避免service层的密码脱敏
        Employee employee = this.baseMapper.getById(param.getUserId());
        //原密码加密
        String oldPassword = DigestUtils.md5DigestAsHex(param.getOldPassword().getBytes());
        if(!employee.getPassword().equals(oldPassword)){
            return ResultVo.error("原密码不正确!");
        }

        param.setPassword(DigestUtils.md5DigestAsHex(param.getPassword().getBytes()));
        int row = baseMapper.updatePwd(param);
        if(row > 0){
            return ResultVo.success("修改成功!");
        }
        return ResultVo.error("修改失败！！");
    }
}

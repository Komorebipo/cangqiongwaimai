package cn.magic.service;

import cn.magic.dto.EmployeeDTO;
import cn.magic.dto.EmployeePageQueryDTO;
import cn.magic.dto.UpdatePasswordDTO;
import cn.magic.entity.Employee;
import cn.magic.utils.ResultVo;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
//员工服务接口
public interface EmployeeService extends IService<Employee> {
    /**
     * 新增员工
     * @param employeeDTO
     */
    int save(EmployeeDTO employeeDTO);
    /**
     * 启用禁用员工账号
     * @param status
     * @param id
     */
    void startOrStop(Integer status, Long id);

    /**
     * 分页查询
     * @param employeePageQueryDTO
     * @return
     */
    Page<Employee> pageQuery(EmployeePageQueryDTO employeePageQueryDTO);
    /**
     * 根据id查询员工
     * @param id
     * @return
     */
    Employee getById(Long id);
    /**
     * 编辑员工信息
     * @param employeeDTO
     */
    void update(EmployeeDTO employeeDTO);

    /**
     * 修改密码
     * @param param
     * @return
     */
    ResultVo updatePwd(UpdatePasswordDTO param);
}

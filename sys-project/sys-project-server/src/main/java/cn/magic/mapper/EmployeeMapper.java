package cn.magic.mapper;

import cn.magic.dto.UpdatePasswordDTO;
import cn.magic.entity.Employee;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

//员工数据访问层接口
public interface EmployeeMapper extends BaseMapper<Employee> {

    /**
     * 插入员工数据
     * @param employee
     */
    @Insert("insert into employee (name, username, password, phone, sex, id_number, create_time, update_time, create_user, update_user,status) " +
            "values " +
            "(#{name},#{username},#{password},#{phone},#{sex},#{idNumber},#{createTime},#{updateTime},#{createUser},#{updateUser},#{status})")
    int insert(Employee employee);


    /**
     * 根据主键动态修改属性
     * @param employee
     */
    int update(Employee employee);
    /**
     * 分页查询
     * @param page:分页信息
     * @return
     */
    Page<Employee> pageQuery(@Param("page")Page<Employee> page, String name);
    /**
     * 根据id查询员工信息
     * @param id
     * @return
     */
    @Select("select * from employee where id = #{id}")
    Employee getById(Long id);

    /**
     * 修改密码
     * @param param
     * @return
     */
    int updatePwd(UpdatePasswordDTO param);

}

package cn.magic.mapper;

import cn.magic.dto.SetmealPageQueryDTO;
import cn.magic.entity.Setmeal;
import cn.magic.vo.SetmealVO;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface SetmealMapper extends BaseMapper<Setmeal> {
    @Select("select count(id) from setmeal where category_id = #{categoryId}")
    Integer countByCategoryId(@Param("categoryId") Long id);

    Page<SetmealVO> pageQuery(@Param("page") Page<SetmealVO> page,
                              @Param("setmealPageQueryDTO") SetmealPageQueryDTO setmealPageQueryDTO);

    SetmealVO getByIdWithDish(Long id);

    @Select("select * from setmeal where id = #{id}")
    Setmeal getById(Long id);

    @Select("select * from setmeal where category_id = #{categoryId}")
    List<Setmeal> listByCategoryId(Long categoryId);
}
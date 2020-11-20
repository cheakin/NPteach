package com.biibili.dao;

import com.biibili.entity.Department;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface DepartmentDao {
    int deleteByPrimaryKey(Integer id);

    int insert(Department record);

    int insertSelective(Department record);

    Department selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(Department record);

    int updateByPrimaryKey(Department record);
}
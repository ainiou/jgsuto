package com.pro.jgsu.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.pro.jgsu.entity.User;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMapper extends BaseMapper<User> {
}

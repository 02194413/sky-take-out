package com.sky.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.sky.entity.User;
import lombok.RequiredArgsConstructor;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Service;

import java.util.Map;

@Mapper
public interface UserMapper extends BaseMapper<User> {

    @Select("select * from user where openid = #{openid}")
    User getByOpenId(String openid);

    /*
    * 根据条件统计用户数量
    * */
    Integer countByMap(Map map);

}

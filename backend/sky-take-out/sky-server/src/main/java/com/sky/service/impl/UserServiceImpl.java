package com.sky.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.sky.constant.MessageConstant;
import com.sky.dto.UserLoginDTO;
import com.sky.entity.User;
import com.sky.exception.LoginFailedException;
import com.sky.mapper.UserMapper;
import com.sky.properties.WeChatProperties;
import com.sky.service.UserService;
import com.sky.utils.HttpClientUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {

    public static final String WX_LOGIN="https://api.weixin.qq.com/sns/jscode2session";

    private final WeChatProperties weChatProperties;
    private final UserMapper userMapper;
    @Override
    public User wxLogin(UserLoginDTO userLoginDTO) {

        String openid=getOpenid(userLoginDTO.getCode());
        //判断是否为空，空则抛异常
        if(openid==null){
            throw new LoginFailedException(MessageConstant.LOGIN_FAILED);
        }

        //判断是否为新用户，是则自动完成注册-保存到用户表
        User user=userMapper.getByOpenId(openid);
        if(user==null){
            user=User.builder()
                    .openid(openid)
                    .createTime(LocalDateTime.now())
                    .build();
            userMapper.insert(user);
        }
        //返回用户对象

        return user;
    }

    private String getOpenid(String code){
        //调用微信服务器接口获得openid
        Map<String,String> map=new HashMap<>();
        map.put("appid",weChatProperties.getAppid());
        map.put("secret",weChatProperties.getSecret());
        map.put("js_code",code);//code为判别不同用户的唯一标识，根据code的归属返回用户的openid
        map.put("grant_type","authorization_code");
        String  result =HttpClientUtil.doGet(WX_LOGIN,map);

        JSONObject jsonObject=JSON.parseObject(result);
        String openid=jsonObject.getString("openid");
        return openid;
    }
}

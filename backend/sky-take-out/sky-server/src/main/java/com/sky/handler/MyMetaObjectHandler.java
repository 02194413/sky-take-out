package com.sky.handler;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import com.sky.context.BaseContext;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class MyMetaObjectHandler implements MetaObjectHandler {
    @Override
    public void insertFill(MetaObject metaObject) {
        Long userId= BaseContext.getCurrentId();
        LocalDateTime now = LocalDateTime.now();

        strictInsertFill(metaObject,"createTime",()->now,LocalDateTime.class);
        strictInsertFill(metaObject,"updateTime",()->now,LocalDateTime.class);
        strictInsertFill(metaObject,"createUser",()->userId,Long.class);
        strictInsertFill(metaObject,"updateUser",()->userId,Long.class);

    }

    @Override
    public void updateFill(MetaObject metaObject) {
        Long userId= BaseContext.getCurrentId();
        LocalDateTime now = LocalDateTime.now();
        strictUpdateFill(metaObject,"updateTime",()->now,LocalDateTime.class);
        strictUpdateFill(metaObject,"updateUser",()->userId,Long.class);
    }
}

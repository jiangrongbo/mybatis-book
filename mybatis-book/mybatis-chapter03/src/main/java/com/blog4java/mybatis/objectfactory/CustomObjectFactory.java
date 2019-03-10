package com.blog4java.mybatis.objectfactory;

import org.apache.ibatis.reflection.factory.DefaultObjectFactory;

import java.util.UUID;

public class CustomObjectFactory extends DefaultObjectFactory {

    @Override
    public Object create(Class type) {
        if(type.equals(User.class)){
            //实例化User类
            User user = (User)super.create(type);
            user.setUuid(UUID.randomUUID().toString());
            return user;
        }
        return super.create(type);
    }

}

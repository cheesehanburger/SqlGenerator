package com.yupi.sqlfather;

import com.yupi.sqlfather.mapper.DictMapper;
import com.yupi.sqlfather.service.DictService;
import lombok.Data;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@SpringBootTest
class MainApplicationTests {

    @Test
    public void TestStreamMap() {
        @Data
        class User {
            String name;
            String address;
            Integer age;
        }

        Map<Integer,User> map = new HashMap<>();
        User user1 = new User();
        user1.setName("han1");
        user1.setAddress("jiansu1");
        user1.setAge(101);

        User user2 = new User();
        user2.setName("han2");
        user2.setAddress("jiansu2");
        user2.setAge(102);

        User user3 = new User();
        user3.setName("han3");
        user3.setAddress("jiansu3");
        user3.setAge(103);

        map.put(1,user1);
        map.put(2,user2);
        map.put(3,user3);

        String list = map.values().stream().map(user -> user.getName()).collect(Collectors.joining(","));
        System.out.println(list);
    }

    @Test
    public void TestStreamFilter() {
        @Data
        class User {
            String name;
            String address;
            Integer age;
        }

        List<User> list = new ArrayList<User>();
        //定义三个用户对象
        User user1 = new User();
        user1.setName("huxiansen");
        user1.setAge(123);
        User user2 = new User();
        user1.setName("huxianse");
        user1.setAge(122);
        User user3 = new User();
        user1.setName("huxiansen");
        user1.setAge(121);
        //添加用户到集合中
        list.add(user1);
        list.add(user2);
        list.add(user3);
        //在集合中查询用户名为huxiansen的集合
        List<User> userList = list.stream().filter(user -> "huxiansen".equals(user.getName())).collect(Collectors.toList());
        System.out.println(userList);
    }


}

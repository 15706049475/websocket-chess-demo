package com.xk.demo;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Date;

@RunWith(SpringRunner.class)
@SpringBootTest
public class SpringbootWebsocketChessDemoApplicationTests {

    @Test
    public void contextLoads() {
        System.out.println(111);
    }

    public static void main(String[] args) {
        // 1555319085701
        // 1555325424007
        System.out.println(new Date().getTime()-60*60*24*5*15);
    }

}

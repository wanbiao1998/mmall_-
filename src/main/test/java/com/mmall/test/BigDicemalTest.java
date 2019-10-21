package com.mmall.test;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

/**
 * @author wb
 * @create 2019-10-11 20:29
 */
public class BigDicemalTest {

    @Test
    public void test1(){
        System.out.println(0.05+0.01);
    }

    @Test
    public void test2(){
        BigDecimal b1 = new BigDecimal("0.05");
        BigDecimal b2 = new BigDecimal("0.01");
        System.out.println(b1.add(b2));
    }
}

package com.bjsxt.utils;

import java.util.Random;

/**
 * 各种ID的生成策略
 */
public class IDUtils {

    /**
     * 图片名生成
     *
     * @return
     */
    public static String genImageName() {

        //取当前时间的长整形值包含毫秒
        long millis = System.currentTimeMillis();
        //加上3位随机数
        Random random = new Random();
        int end3 = random.nextInt(999);
        //如果不足3位，前面补0
        String str = millis + String.format("%03d", end3);
        return str;
    }

    /**
     * 商品id生成
     */
    public static long genItemId() {

        //取当前时间的长整形值包含毫秒
        long millis = System.currentTimeMillis();
        //加上2位随机数
        Random random = new Random();
        int end2 = random.nextInt(99);
        //如果不足2位，前面补0
        String str = millis + String.format("%02d", end2);
        long id = new Long(str);
        return id;
    }

//    public static void main(String[] args) {
//
//        for (int i = 0; i < 100; i++) {
//            System.out.println(System.currentTimeMillis());
//            System.out.println(genItemId());
//
//        }
//    }
}

package com.example.mtk14060.metal_slug_game.comp;

import java.util.Random;

public class Util
{
    public static Random random = new Random();
    //返回一个0~range的随机数
    public static int rand(int range)
    {
        if (range == 0)
        {
            return range;
        }
        // 获取一个0～range之间的随机数
        return Math.abs(random.nextInt() & range);
    }
}

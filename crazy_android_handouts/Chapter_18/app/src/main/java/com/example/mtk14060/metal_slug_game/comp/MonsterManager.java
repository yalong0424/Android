package com.example.mtk14060.metal_slug_game.comp;

import android.graphics.Canvas;

import java.util.ArrayList;
import java.util.List;

//专门管理怪物的随机产生和死亡等行为
public class MonsterManager
{
    // 保存所有死掉的怪物，保存它们是为了绘制死亡的动画，绘制完后清除这些怪物
    public static final List<Monster> dieMonsterList = new ArrayList<>();
    // 保存所有活着的怪物
    public static final List<Monster> monsterList = new ArrayList<>();

    // 随机生成、并添加怪物的方法
    public static void generateMonster()
    {
        if (monsterList.size() < 3 + Util.rand(3))
        {
            //
            Monster monster = new Monster(1 + Util.rand(3));
            monsterList.add(monster);
        }
    }

    // 更新怪物与子弹的坐标的方法
    public static void updatePosition(int shift)
    {
        Monster monster = null;
        //
        List<Monster> delList = new ArrayList<>();
        //
        for (int i = 0; i < monsterList.size(); ++i)
        {
            monster = monsterList.get(i);
            if (monster == null)
            {
                continue;
            }
            //
        }
    }

    // 检查怪物是否将要死亡的方法
    public static void checkMonster()
    {

    }

    // 绘制所有怪物的方法
    public static void drawMonster(Canvas canvas)
    {

    }
}

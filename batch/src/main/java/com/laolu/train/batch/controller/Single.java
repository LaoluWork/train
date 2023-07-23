package com.laolu.train.batch.controller;

public class Single {
    private Integer count = 0;

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }

    private static Single instance = null;

    public static Single getInstance() {
        if (instance == null) {
            instance = new Single();
        }
        return instance;
    }

    public void addCount(String target) {
        count++;
        System.out.println("【" + target + "】修改了：" + count);
    }
}

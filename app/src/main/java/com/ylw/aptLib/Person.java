package com.ylw.aptLib;

/**
 * author : liwen15
 * date : 2021/7/18
 * description :
 */
public class Person {

    private String name;
    private int age;

    public Person(String name) {
        int i = 2/0;
        this.name = name;
    }

    public Person(String name, int age) {
        this.name = name;
        this.age = age;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    @Override
    public String toString() {
        return "Person{" +
                "name='" + name + '\'' +
                ", age=" + age +
                '}';
    }
}

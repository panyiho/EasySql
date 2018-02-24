package com.easysql;


import com.easysql.annotation.Column;
import com.easysql.annotation.Table;

/**
 * Created by Administrator on 2018/2/6.
 */

@Table(name = "Persion")
public class Persion {
    @Column(name = "id",isId = true,autoGen = true)
    public int id;
    @Column(name = "isMan")
    public boolean isMan = false;
    @Column(name = "age")
    public int age =24;
    @Column(name = "longValue")
    public long longValue = 12313123;
    @Column(name = "shortValue")
    public short shortValue = 313;
    @Column(name = "doubleValue")
    public double doubleValue = 2134132;
    @Column(name = "stringValue")
    public String stringValue= "我是一个人";
    @Column(name = "byteValue")
    public byte byteValue = 1;
    @Column(name = "floatValue")
    public float floatValue = 2342.25f;
    public Baby baby = new Baby() ;


    @Override
    public String toString() {
        return "Persion{" +
                "id=" + id +
                ", isMan=" + isMan +
                ", age=" + age +
                ", longValue=" + longValue +
                ", shortValue=" + shortValue +
                ", doubleValue=" + doubleValue +
                ", stringValue='" + stringValue + '\'' +
                ", byteValue=" + byteValue +
                ", floatValue=" + floatValue +
                '}';
    }
}

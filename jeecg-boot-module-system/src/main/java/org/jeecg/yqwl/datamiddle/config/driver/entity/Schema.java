package org.jeecg.yqwl.datamiddle.config.driver.entity;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Schema
 * @Author: XiaoKai
 * @Date: 2022-011-11
 * @Version: V2.0
 */
@Getter
@Setter
public class Schema implements Serializable, Comparable<Schema> {

    private static final long serialVersionUID = 4278304357661271040L;

    private String name;
    private List<Table> tables = new ArrayList<>();
    private List<String> views = new ArrayList<>();
    private List<String> functions = new ArrayList<>();
    private List<String> userFunctions = new ArrayList<>();
    private List<String> modules = new ArrayList<>();

    /**
     * 需要保留一个空构造方法，否则序列化有问题
     * */
    public Schema() {
    }

    public Schema(String name) {
        this.name = name;
    }

    public Schema(String name, List<Table> tables) {
        this.name = name;
        this.tables = tables;
    }

    public static Schema build(String name) {
        return new Schema(name);
    }

    @Override
    public int compareTo(Schema o) {
        return this.name.compareTo(o.getName());
    }
}

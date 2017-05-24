package com.yan.bean;

import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * Created with IntelliJ IDEA.
 * Description:
 * User: Administrator
 * Date: 2017/5/24
 * Time: 10:55
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Item {
    private Integer id;
    private Integer count;
    private Integer isBind;

    public Item(Integer id, Integer count, Integer isBind) {
        this.id = id;
        this.count = count;
        this.isBind = isBind;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }

    public Integer getBind() {
        return isBind;
    }

    public void setBind(Integer bind) {
        isBind = bind;
    }
}

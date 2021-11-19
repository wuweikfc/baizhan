package com.bjsxt.commons.pojo;

import lombok.Data;

import java.io.Serializable;

@Data
public class BaizhanResult implements Serializable {

    private int status;

    private String msg;

    private Object data;


    public static BaizhanResult ok() {

        BaizhanResult br = new BaizhanResult();
        br.setMsg("OK");
        br.setStatus(200);
        return br;
    }

    public static BaizhanResult ok(Object data) {

        BaizhanResult br = new BaizhanResult();
        br.setMsg("OK");
        br.setStatus(200);
        br.setData(data);
        return br;
    }

    public static BaizhanResult error(String msg) {

        BaizhanResult br = new BaizhanResult();
        br.setMsg(msg);
        br.setStatus(400);
        return br;
    }

}

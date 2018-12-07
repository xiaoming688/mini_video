package com.mini_video.utils;

import com.alibaba.fastjson.JSON;

import java.util.LinkedHashMap;

/**
 * @author: Canyon
 * @Description: restful json 数据返回
 * @Date: Created in 16:10 2017/10/18
 * @Modified By:
 */
public class MData extends LinkedHashMap<String, Object> {

    private static final long serialVersionUID = 8758596771637006343L;

    public final static String OK = "ok";

    public final static String ERR = "err";

    public MData() {
        super();
        status(OK);
    }

    public void status(String status) {
        super.put("status", status);
    }


    public void ok() {
        status(OK);
    }

    public MData ok(String message,Object data){
        ok(message);
        setData(data);
        return this;
    }

    public MData ok(String message){
        ok();
        setMessage(message);
        return this;
    }

    public boolean isOk(){
        return OK.equals(get("status"));
    }

    public MData attr(String name,Object value){
        super.put(name, value);
        return this;
    }

    @Override
    public Object put(String key, Object value) {
        return attr(key, value);
    }

    public void error() {
        status(ERR);
    }

    public MData error(String message) {
        error();
        setMessage(message);
        return this;
    }

    public MData error(int code,String message){
        error(message);
        code(code);
        return this;
    }


    public String json() {
        return JSON.toJSONString(this);
    }

    public void setMessage(String message) {
        super.put("message", message);
    }

    public String getMessage(){
        return (String) get("message");
    }

    public void setData(Object data) {
        super.put("data", data);
    }

    public MData code(int code){
        super.put("code", code);
        return this;
    }

    public String jsonp(String callback){
        return callback+"("+json()+")";
    }

    public String toJSONP(){
        return jsonp("callback");
    }


    @SuppressWarnings("unchecked")
    public <T> T getData() {
        return (T) get("data");
    }


    public int getCode(){
        return Integer.parseInt(String.valueOf(get("code")));
    }


    public void result(Object result){
        super.put("result", result);
    }

    public void success(){
        super.put("status", OK);
    }

    public MData success(String message){
        super.put("status", OK);
        setMessage(message);
        return this;
    }

    public void setErrCode(Integer errCode){
        super.put("err_code", errCode);
    }
}

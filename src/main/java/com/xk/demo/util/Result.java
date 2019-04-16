package com.xk.demo.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.xk.demo.chess.GamePlayer;

import java.io.IOException;

/**
 * 返回给前台页面的消息体
 * @author xiake
 *
 */
public class Result {

    private static ObjectMapper mapper = null;
    private Integer status;//状态码，1为系统消息，2为普通消息，3为指令消息
    private Object data;
    private GamePlayer gamer;
    private Localtion localtion;
    public Result() {
    }

    public Result(Integer status, Object data, GamePlayer gamer, Localtion localtion) {
        this.status = status;
        this.data = data;
        this.gamer = gamer;
        this.localtion = localtion;
    }

    //发送系统消息时
    public static Result system(Object data,GamePlayer gamer) {
        return new Result(1, data, gamer,null);
    }

    //聊天时
    public static Result common(Object data) {
        return new Result(2, data, null,null);
    }

    //下达指令时
    public static Result command(Object data,GamePlayer gamer,Double x,Double y){
        return new Result(3, data, gamer,new Localtion(x,y));
    }


    //把返回消息体转成json格式
    public static String resultJson(Result result) {
        mapper=new ObjectMapper();
        try {
            return mapper.writeValueAsString(result);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return "{}";
    }

    //把json格式字符串转成消息体
    public static Result resultObject(String resultJson) throws IOException {
        mapper = new ObjectMapper();
        return	mapper.readValue(resultJson, Result.class);
    }

    public static ObjectMapper getMapper() {
        return mapper;
    }

    public static void setMapper(ObjectMapper mapper) {
        Result.mapper = mapper;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public GamePlayer getGamer() {
        return gamer;
    }

    public void setGamer(GamePlayer gamer) {
        this.gamer = gamer;
    }

    @Override
    public String toString() {
        return "Result{" +
                "status=" + status +
                ", data=" + data +
                ", gamer=" + gamer +
                ", localtion=" + localtion +
                '}';
    }

    public Localtion getLocaltion() {
        return localtion;
    }

    public void setLocaltion(Localtion localtion) {
        this.localtion = localtion;
    }

}

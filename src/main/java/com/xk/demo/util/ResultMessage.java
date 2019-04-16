package com.xk.demo.util;


import java.io.IOException;
import java.io.Serializable;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

/**
 * 返回给前台页面的消息体
 * @author xiake
 *
 */
public class ResultMessage {

	private static ObjectMapper mapper = null;
	private Integer status;
	private Object data;
	private Localtion location;
	public ResultMessage() {
		
	}

	public ResultMessage(Integer status, Object data, Localtion location) {
		super();
		this.status = status;
		this.data = data;
		this.location = location;
	}

	public static ResultMessage system(Object data,Localtion localtion) {
		return new ResultMessage(1, data, localtion);
	}
	
	public static ResultMessage common(Object data) {
		return new ResultMessage(2, data, null);
	}
	
	//把返回消息体转成json格式
	public static String resultJson(ResultMessage resultMessage) {
		mapper=new ObjectMapper();
		try {
			return mapper.writeValueAsString(resultMessage);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
		return "{}";
	}
	
	//把json格式字符串转成消息体
	public static ResultMessage resultObject(String resultJson) throws JsonParseException, JsonMappingException, IOException {
		mapper = new ObjectMapper();
		return	mapper.readValue(resultJson, ResultMessage.class);
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
	
	public Localtion getLocation() {
		return location;
	}

	public void setLocation(Localtion location) {
		this.location = location;
	}

	@Override
	public String toString() {
		return "ResultMessage [status=" + status + ", data=" + data + ", location=" + location + "]";
	}

}

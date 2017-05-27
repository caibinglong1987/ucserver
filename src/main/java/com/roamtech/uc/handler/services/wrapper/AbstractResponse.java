package com.roamtech.uc.handler.services.wrapper;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.annotation.JSONField;
import com.alibaba.fastjson.serializer.*;
import com.roamtech.uc.model.Attribute;
import com.roamtech.uc.util.JSONUtils;

import javax.persistence.Transient;


@SuppressWarnings("serial")
public abstract class AbstractResponse  implements Serializable {
	@JSONField(name = "error_no")
	protected int errorNo;
	@JSONField(name = "error_info")
	protected String errorInfo;
	@Transient
	@JSONField(serialize = false)
	private SerializeFilter filter;

	public AbstractResponse() {
		filter = new SimplePropertyPreFilter();
	}
	public AbstractResponse(int errorNo, String errorInfo) {
		this.errorNo = errorNo;
		this.errorInfo = errorInfo;
	}
	public void setFilter(SerializeFilter filter) {
		this.filter = filter;
	}
	/**
	 * 序列化
	 * @return
	 */
	public String serialize(){
		return JSONUtils.serialize(this, filter);
	}
	
	/**
	 * 反序列化
	 * @param json
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public <T> T deserialize(String json){
		return (T) JSON.parseObject(json,this.getClass());//JsonUtils.deserialize(json, this.getClass());
	}
	
	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString(){
		return serialize();
	}
	
	public int getErrorNo() {
		return errorNo;
	}
	public String getErrorInfo() {
		return errorInfo;
	}
	public void setErrorNo(int errorNo) {
		this.errorNo = errorNo;
	}
	public void setErrorInfo(String errorInfo) {
		this.errorInfo = errorInfo;
	}	
	public Map<String, Object> logInfo() {
		Map<String, Object> attributes = new HashMap<String,Object>();
		attributes.put("error_no", errorNo);
		attributes.put("error_info", errorInfo);
		logAttributes(attributes);
		return attributes;
	}
	protected abstract void logAttributes(Map<String, Object> attributes);
}

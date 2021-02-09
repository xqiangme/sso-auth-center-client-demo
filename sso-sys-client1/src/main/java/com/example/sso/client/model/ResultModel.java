package com.example.sso.client.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * 返回统一对象
 *
 * @author 程序员小强
 */
@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ResultModel implements Serializable {

	private static final long serialVersionUID = -4588600204128331404L;

	/**
	 * 状态码
	 */
	private Integer code;

	/**
	 * 返回的的数据
	 */
	private Object data;

	/**
	 * 描述
	 */
	private String msg;

	/**
	 * 成功请求
	 * code : 200
	 * msg : 默认 ""
	 */
	public static ResultModel success(Object data) {
		return new ResultModel(200, data, "操作成功");
	}

	public static ResultModel success() {
		return success("");
	}

	public ResultModel(Integer code, Object data, String msg) {
		this.code = code;
		this.data = data;
		this.msg = msg;
	}
}

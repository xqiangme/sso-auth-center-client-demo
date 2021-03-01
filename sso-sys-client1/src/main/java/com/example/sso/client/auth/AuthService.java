package com.example.sso.client.auth;

import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson.JSON;
import com.example.sso.client.auth.model.ApplyAuthVO;
import com.example.sso.client.config.SysConfigProperty;
import com.example.sso.client.constant.SsoConstant;
import com.example.sso.client.model.ResultModel;
import com.example.sso.client.utils.SsoSignUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Service
public class AuthService {

	private static final Integer SUCCESS_CODE = 200;

	@Autowired
	private SysConfigProperty sysConfigProperty;

	/**
	 * 请求认证中心-申请认证
	 *
	 * @param token
	 * @return
	 */
	public ApplyAuthVO authToken(String token, String sessionId) {
		//构建参数
		Map<String, Object> paramMap = this.buildParamMap(token, sysConfigProperty.getSsoAuthSecret(), sessionId);

		long start = System.currentTimeMillis();
		log.info("[ 请求认证中心-申请认证 ] start >> paramMap:{}", JSON.toJSONString(paramMap));
		String response = HttpUtil.post(sysConfigProperty.getSsoAuthGetWayUrl(), paramMap);
		log.info("[ 请求认证中心-申请认证 ] end >>  times:{} ms, response:{}", (System.currentTimeMillis() - start), response);

		//正式环境需对异常统一除以，这里只做了模拟默认抛出了 RuntimeException
		if (StringUtils.isBlank(response)) {
			throw new RuntimeException("认证中心-无返回");
		}

		ResultModel resultModel = JSON.parseObject(response, ResultModel.class);
		if (null == resultModel) {
			throw new RuntimeException("认证中心-无返回");
		}

		if (!resultModel.getCode().equals(SUCCESS_CODE)) {
			throw new RuntimeException("认证中心调用失败，" + resultModel.getMsg());
		}

		if ( StringUtils.isBlank(resultModel.getData().toString())) {
			throw new RuntimeException("认证中心-无Data返回");
		}

		return JSON.parseObject(resultModel.getData().toString(), ApplyAuthVO.class);
	}

	/**
	 * 构建参数
	 *
	 * @param token
	 * @param md5Secret
	 * @param sessionId
	 */
	private Map<String, Object> buildParamMap(String token, String md5Secret, String sessionId) {
		Map<String, Object> paramMap = new HashMap<>(16);
		//系统编码
		paramMap.put("sysCode", sysConfigProperty.getMySysCode());
		//申请认证接口地址 (com.sso.applyAuth)
		paramMap.put("method", SsoConstant.METHOD_APPLY_AUTH);
		//版本号
		paramMap.put("version", "1.0");
		//请求唯一标识
		paramMap.put("apiRequestId", UUID.randomUUID());
		//签名类型 1-MD5;2-RSA;3-RSA2
		paramMap.put("signType", "1");
		//当前时间戳
		paramMap.put("timestamp", System.currentTimeMillis());

		//业务参数
		Map<String, Object> contentMap = this.buildContentMap(token, sessionId);
		//业务参数JSON 值
		paramMap.put("content", JSON.toJSONString(contentMap));
		//生成签名
		String sign = SsoSignUtil.getMd5Sign(paramMap, Arrays.asList("signType", "sign"), md5Secret);
		paramMap.put("sign", sign);

		return paramMap;
	}

	private Map<String, Object> buildContentMap(String token, String sessionId) {
		Map<String, Object> contentMap = new HashMap<>(4);
		//退出当前子系统地址 示例：http://www.sysclient1.com:8801/logOutBySessionId?sessionId=xxx
		contentMap.put("loginOutUrl", String.format("%s?sessionId=%s", sysConfigProperty.getMyLoginOutUrl(), sessionId));
		//返回菜单类型 1-普通列表 2-树型结构列表
		contentMap.put("menuType", 2);
		contentMap.put("ssoToken", token);
		return contentMap;
	}

}

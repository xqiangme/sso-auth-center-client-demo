package com.example.sso.client.utils;

import com.example.sso.client.config.SysConfigProperty;
import com.example.sso.client.constant.SsoConstant;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author 程序员小强
 * @date 2021-01-26
 */
public class SSOClientHelper {

	/**
	 * 请求认证中心-校验登录或首次登录
	 * <p>
	 * 1.若存在全局会话则携带令牌重定向回客户端（已经登录）
	 * 2.若无全局会话则返回统一登录页面进行登录（首次登录）
	 *
	 * @param request
	 * @param response
	 */
	public static void ssoLogin(HttpServletRequest request, HttpServletResponse response) throws IOException {
		StringBuilder url = new StringBuilder();
		//获取当前客户端在访问的地址
		String redirectUrl = getCurrentServletPath(request);
		url.append(SysConfigProperty.getSsoAuthLoginUrl())
				.append("?redirectUrl=")
				.append(redirectUrl);
		response.sendRedirect(url.toString());
	}


	/**
	 * 获取 ssoToken
	 * 1.优先从请求参数中获取
	 * 2.二次尝试从cookie中获取
	 */
	public static String getSsoToken(HttpServletRequest request) {
		//从参数中获取
		String token = request.getParameter(SsoConstant.TOKEN_NAME);
		//若参数未携带-尝试从cookie获取
		if (StringUtils.isEmpty(token)) {
			token = CookieUtil.getCookie(request, SsoConstant.TOKEN_NAME);
		}
		return token;
	}

	/**
	 * 获取当前访问地址
	 * 示例：http://localhost:8088/index
	 *
	 * @param request
	 */
	public static String getCurrentServletPath(HttpServletRequest request) {
		return SysConfigProperty.getClientWebUrl() + request.getServletPath();
	}

	/**
	 * 获取客户端的登出地址
	 * 示例：http://localhost:8088/logOut
	 */
	public static String getClientLogOutUrl() {
		return SysConfigProperty.getClientWebUrl() + SsoConstant.CLIENT_LOGOUT_URL;
	}


}

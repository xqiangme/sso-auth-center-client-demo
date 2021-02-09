package com.example.sso.client.interceptor;

import com.alibaba.fastjson.JSON;
import com.example.sso.client.auth.AuthService;
import com.example.sso.client.auth.model.ApplyAuthVO;
import com.example.sso.client.constant.SsoConstant;
import com.example.sso.client.utils.CookieUtil;
import com.example.sso.client.utils.SSOClientHelper;
import com.example.sso.client.utils.SessionContext;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

/**
 * 创建拦截器-拦截需要安全访问的请求
 * 方法说明
 * 1.preHandle():前置处理回调方法，返回true继续执行，返回false中断流程，不会继续调用其它拦截器
 * 2.postHandle():后置处理回调方法，但在渲染视图之前
 * 3.afterCompletion():全部后置处理之后，整个请求处理完毕后回调。
 *
 * @author 程序员小强
 */
@Slf4j
public class WebInterceptor implements HandlerInterceptor {

	@Autowired
	protected AuthService authService;

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws IOException {
		//判断是否有局部会话
		HttpSession httpSession = request.getSession();
		//获取session
		HttpSession session = SessionContext.getInstance().getSession(httpSession.getId());
		if (null != session) {
			Object isLogin = session.getAttribute("isLogin");
			if (isLogin != null && (Boolean) isLogin) {
				log.debug("[ 请求拦截器 ] >> 已登录状态 , 有局部会话 sessionId:{}", httpSession.getId());
				return true;
			}
		}

		log.info("[ 请求拦截器 ] >> 未登录需要登录 >>  sessionId:{} requestUrl:{} ", httpSession.getId(), request.getRequestURI());
		//获取令牌ssoToken
		String token = SSOClientHelper.getSsoToken(request);
		//无令牌
		if (StringUtils.isBlank(token)) {
			log.info("[ 请求拦截器 ] >> 未获取到token , 将跳转认证中心 >>  sessionId:{} ", httpSession.getId());
			//跳转到认证中心登录
			SSOClientHelper.ssoLogin(request, response);
			return true;
		}

		//有令牌-则请求认证中心校验令牌是否有效
		ApplyAuthVO applyAuth = authService.authToken(token, httpSession.getId());
		//令牌无效
		if (null == applyAuth) {
			log.debug("[ 请求拦截器 ] >> 令牌无效 , 将跳转认证中心进行认证 requestUrl:{}, token:{}", request.getRequestURI(), token);
			//跳转到认证中心登录
			SSOClientHelper.ssoLogin(request, response);
			return true;
		}
		//令牌无效
		if (!applyAuth.getAuthResult() && StringUtils.isNoneBlank(applyAuth.getRedirectUrl())) {
			log.debug("[ 请求拦截器 ] >> 令牌无效 , 将跳转认证中心进行认证 requestUrl:{}, token:{}", applyAuth.getRedirectUrl(), token);
			//跳转到认证中心登录
			response.sendRedirect(applyAuth.getRedirectUrl());
			return true;
		}

		//token有效，创建局部会话设置登录状态，并放行
		httpSession.setAttribute("isLogin", true);
		httpSession.setAttribute("userInfo", JSON.toJSONString(applyAuth));
		//设置session失效时间-单位秒
		httpSession.setMaxInactiveInterval(1800);
		//添加session
		SessionContext.getInstance().addSession(httpSession);
		//设置本域cookie
		CookieUtil.setCookie(response, SsoConstant.TOKEN_NAME, token, 1800);
		log.debug("[ 请求拦截器 ] >> 令牌有效,创建局部会话成功 sessionId:{},requestUrl:{}, token:{}", httpSession.getId(), request.getRequestURI(), token);
		return true;
	}
}

package com.example.sso.client.controller;

import com.example.sso.client.config.SysConfigProperty;
import com.example.sso.client.model.ResultModel;
import com.example.sso.client.utils.SessionContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

@Slf4j
@RestController
public class LoginController {


	@Autowired
	private SysConfigProperty sysConfigProperty;

	/**
	 * 从局部会话-缓存中
	 * <p>
	 * 获取登录用户的信息
	 *
	 * @param request
	 */
	@RequestMapping(value = "/getUserInfo", produces = {"application/json;charset=UTF-8"})
	protected Object getUserInfo(HttpServletRequest request) {
		HttpSession session = request.getSession();
		Object userInfo = session.getAttribute("userInfo");
		if (null != userInfo) {
			return ResultModel.success(userInfo);
		}
		return ResultModel.success();
	}

	/**
	 * 退出当前系统
	 *
	 * @param request
	 * @param response
	 */
	@RequestMapping("/logOut")
	protected void logOut(HttpServletRequest request, HttpServletResponse response) throws IOException {
		//销毁局部会话
		SessionContext.getInstance().delSession(request.getSession());
		//跳转到认证中心登录页
		response.sendRedirect(SysConfigProperty.getSsoAuthLoginUrl());
	}

	/**
	 * 认证中心统-调用-退出该系统接口
	 *
	 * @param request
	 * @param response
	 */
	@RequestMapping("/logOutBySsoAuth")
	protected void logOutBySsoAuth(HttpServletRequest request, HttpServletResponse response) {
		//获取参数sessionId
		String sessionId = request.getParameter("sessionId");
		log.info("[ 退出子系统 ] >>  start sessionId:{}", sessionId);
		HttpSession session = SessionContext.getInstance().getSession(sessionId);
		if (null == session) {
			log.error("[ 退出子系统 ] >> session不存在  sessionId:{}", sessionId);
			return;
		}
		SessionContext.getInstance().delSession(session);
		log.info("[ 退出子系统 ] >> end sessionId:{}", sessionId);
	}
}

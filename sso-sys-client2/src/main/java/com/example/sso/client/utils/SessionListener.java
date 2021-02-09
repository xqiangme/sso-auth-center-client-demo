package com.example.sso.client.utils;

import lombok.extern.slf4j.Slf4j;

import javax.servlet.annotation.WebListener;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

/**
 * session 监听
 */
@Slf4j
@WebListener
public class SessionListener implements HttpSessionListener {

	private SessionContext context = SessionContext.getInstance();

	/**
	 * session 创建
	 *
	 * @param sessionEvent
	 */
	@Override
	public void sessionCreated(HttpSessionEvent sessionEvent) {
		context.addSession(sessionEvent.getSession());
	}

	/**
	 * session 销毁
	 *
	 * @param sessionEvent
	 */
	@Override
	public void sessionDestroyed(HttpSessionEvent sessionEvent) {
		HttpSession session = sessionEvent.getSession();
		context.delSession(session);
	}

}

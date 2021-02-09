package com.example.sso.client.controller;

import com.example.sso.client.utils.SSOClientHelper;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

/**
 * 页面跳转
 *
 * @author 程序员小强
 */
@Controller
public class IndexController {

	@RequestMapping({"/", "/index"})
	protected String index(HttpServletRequest request, Model model) {
		HttpSession session = request.getSession();
		model.addAttribute("logOutUrl", SSOClientHelper.getClientLogOutUrl());
		Object userInfo = session.getAttribute("userInfo");
		if (null != userInfo) {
			model.addAttribute("userInfo", userInfo);
		}
		return "index";
	}

	@RequestMapping("/welcome")
	protected String welcome(Model model) {
		return "welcome";
	}

}

package com.example.sso.client.auth.model;

import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 菜单树-返回对象
 *
 * @author 程序员小强
 */
@Data
public class MenuTreeAuthVO implements Serializable {

	private static final long serialVersionUID = 69788387641481670L;

	/**
	 * 菜单ID
	 */
	private Long menuId;

	/**
	 * 父菜单ID
	 */
	private Long menuParentId;

	/**
	 * 菜单名称
	 */
	private String menuName;

	/**
	 * 菜单类型（M目录 C菜单 F按钮）
	 */
	private String menuType;

	/**
	 * 显示顺序
	 */
	private Integer sortNum;

	/**
	 * 菜单路由地址
	 */
	private String path;

	/**
	 * 菜单图标
	 */
	private String icon;

	/**
	 * 组件路径
	 */
	private String component;

	/**
	 * 是否显示 0-显示;1-隐藏
	 */
	private Integer visible;

	private final List<MenuTreeAuthVO> children = new ArrayList<>();


}

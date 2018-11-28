package cn.itcast.core.controller.login;

import java.util.HashMap;
import java.util.Map;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/login")
public class LoginController {

	/**
	 * 
	 * @Title: showName
	 * @Description: 获取当前的登录人的信息
	 * @return
	 * @return Map<String,String>
	 * @throws
	 */
	@RequestMapping("/showName.do")
	public Map<String, String> showName(){
		// 获取登录的用户名称：springsecurity容器中获取
		String username = SecurityContextHolder.getContext().getAuthentication().getName();
		Map<String, String> map = new HashMap<>();
		map.put("username", username);
		return map;
	}
}

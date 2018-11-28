package cn.itcast.core.controller.login;

import java.util.HashMap;
import java.util.Map;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/login")
public class LoginController {
	
	@RequestMapping("/name.do")
	public Map<String, String> name(){
		String loginName = SecurityContextHolder.getContext().getAuthentication().getName();
		Map<String, String> map = new HashMap<>();
		map.put("loginName", loginName);
		return map;
	}

}

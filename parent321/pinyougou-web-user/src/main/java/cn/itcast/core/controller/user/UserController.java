package cn.itcast.core.controller.user;

import java.util.regex.PatternSyntaxException;

import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.dubbo.config.annotation.Reference;

import cn.itcast.core.entity.Result;
import cn.itcast.core.pojo.user.User;
import cn.itcast.core.service.user.UserService;
import cn.itcast.utils.phone.PhoneFormatCheckUtils;

@RestController
@RequestMapping("/user")
public class UserController {
	
	@Reference
	private UserService userService;

	@RequestMapping("/sendCode.do")
	public Result sendCode(String phone){
		try {
			// 判断手机号是否合法
			boolean phoneLegal = PhoneFormatCheckUtils.isPhoneLegal(phone);
			if(!phoneLegal){
				return new Result(false, "手机不合法");
			}
			userService.sendCode(phone);
			return new Result(true, "短信发送成功");
		} catch (PatternSyntaxException e) {
			e.printStackTrace();
			return new Result(false, "短信发送失败");
		}
	}
	
	/**
	 * 
	 * @Title: add
	 * @Description: 用户注册
	 * @param smscode
	 * @param user
	 * @return
	 * @return Result
	 * @throws
	 */
	@RequestMapping("/add.do")
	public Result add(String smscode, @RequestBody User user){
		try {
			userService.add(smscode, user);
			return new Result(true, "注册成功");
		}  catch (RuntimeException e) {
			e.printStackTrace();
			return new Result(false, e.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
			return new Result(false, "注册失败");
		}
	}
}

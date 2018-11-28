package cn.itcast.core.service.user;

import cn.itcast.core.pojo.user.User;

public interface UserService {

	/**
	 * 
	 * @Title: sendCode
	 * @Description: 发送短信验证码
	 * @param phone
	 * @return void
	 * @throws
	 */
	public void sendCode(String phone);
	
	/**
	 * 
	 * @Title: add
	 * @Description: 用户注册
	 * @param smscode
	 * @param user
	 * @return void
	 * @throws
	 */
	public void add(String smscode, User user);
}

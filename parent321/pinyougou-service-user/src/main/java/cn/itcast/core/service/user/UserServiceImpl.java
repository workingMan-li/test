package cn.itcast.core.service.user;

import java.util.Date;
import java.util.concurrent.TimeUnit;

import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.MapMessage;
import javax.jms.Message;
import javax.jms.Session;

import org.apache.commons.lang.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.dubbo.config.annotation.Service;

import cn.itcast.core.dao.user.UserDao;
import cn.itcast.core.pojo.user.User;
import cn.itcast.utils.md5.MD5Util;

@Service
public class UserServiceImpl implements UserService {

	@Autowired
	private JmsTemplate jmsTemplate;
	
	@Autowired
	private Destination smsDestination; 
	
	@Autowired
	private RedisTemplate<String, Object> redisTemplate;
	
	@Autowired
	private UserDao userDao;
	
	/*
	 * (non-Javadoc)
	 * <p>Title: sendCode</p> 
	 * <p>Description: 发送短信验证码</p> 
	 * @param phone 
	 * @see cn.itcast.core.service.user.UserService#sendCode(java.lang.String)
	 */
	@Override
	public void sendCode(final String phone) {
		// 随机创建6位数的验证码
		final String code = RandomStringUtils.randomNumeric(6);
		System.out.println("code:"+code);
		// 通过redis模拟服务端的session
		// 将验证码存储：redis： map  string
		redisTemplate.boundValueOps(phone).set(code);
		// 设置验证码过期时间
		redisTemplate.boundValueOps(phone).expire(2, TimeUnit.MINUTES);
		// 封装短信平台需要的参数
		jmsTemplate.send(smsDestination, new MessageCreator() {
			@Override
			public Message createMessage(Session session) throws JMSException {
				// 封装消息体
				MapMessage mapMessage = session.createMapMessage();
				mapMessage.setString("phoneNumbers", phone);
				mapMessage.setString("signName", "阮文");
				mapMessage.setString("templateCode", "SMS_140720901");
				mapMessage.setString("templateParam", "{\"code\":\""+code+"\"}");
				return mapMessage;
			}
		});

	}

	/*
	 * (non-Javadoc)
	 * <p>Title: add</p> 
	 * <p>Description: 用户注册</p> 
	 * @param smscode
	 * @param user 
	 * @see cn.itcast.core.service.user.UserService#add(java.lang.String, cn.itcast.core.pojo.user.User)
	 */
	@Transactional
	@Override
	public void add(String smscode, User user) {
		// 服务端的验证码
		String code = (String) redisTemplate.boundValueOps(user.getPhone()).get();
		// 判断页面传递过来的验证码与服务端是否一致
		if(code != null && smscode.equals(code)){
			// 进行注册：密码加密（MD5加密）
			String password = MD5Util.MD5Encode(user.getPassword(), null);
			user.setPassword(password);
			user.setCreated(new Date());
			user.setUpdated(new Date());
			userDao.insertSelective(user);
		}else{
			// 验证码不正确
			throw new RuntimeException("验证码不正确");
		}
	}
	
	

}

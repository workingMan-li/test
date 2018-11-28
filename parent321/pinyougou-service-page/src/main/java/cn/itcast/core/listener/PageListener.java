package cn.itcast.core.listener;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;

import org.apache.activemq.command.ActiveMQTextMessage;
import org.springframework.beans.factory.annotation.Autowired;

import cn.itcast.core.service.staticpage.StaticPageService;

/**
 * 
 * @ClassName: PageListener
 * @Company: http://www.itcast.cn/
 * @Description: 自定义消息监听器
 * @author 阮文 
 * @date 2018年10月9日 下午12:09:45
 */
public class PageListener implements MessageListener {
	
	@Autowired
	private StaticPageService staticPageService;

	/*
	 * (non-Javadoc)
	 * <p>Title: onMessage</p> 
	 * <p>Description: 生成商品详情的静态页</p> 
	 * @param message 
	 * @see javax.jms.MessageListener#onMessage(javax.jms.Message)
	 */
	@Override
	public void onMessage(Message message) {
		try {
			// 监听的消息体
			ActiveMQTextMessage activeMQTextMessage = (ActiveMQTextMessage) message;
			String id = activeMQTextMessage.getText();
			System.out.println("service-page，获取到的id："+id);
			// 消费消息
			staticPageService.getHtml(id);
		} catch (JMSException e) {
			e.printStackTrace();
		}
		
	}

}

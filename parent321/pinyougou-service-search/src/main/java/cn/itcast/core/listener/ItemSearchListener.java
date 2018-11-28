package cn.itcast.core.listener;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;

import org.apache.activemq.command.ActiveMQTextMessage;
import org.springframework.beans.factory.annotation.Autowired;

import cn.itcast.core.service.search.ItemSearchService;

/**
 * 
 * @ClassName: ItemSearchListener
 * @Company: http://www.itcast.cn/
 * @Description: 自定义消息监听器
 * @author 阮文 
 * @date 2018年10月9日 下午12:16:42
 */
public class ItemSearchListener implements MessageListener {
	
	@Autowired
	private ItemSearchService itemSearchService;

	/*
	 * (non-Javadoc)
	 * <p>Title: onMessage</p> 
	 * <p>Description: 将商品信息保存到索引库中</p> 
	 * @param arg0 
	 * @see javax.jms.MessageListener#onMessage(javax.jms.Message)
	 */
	@Override
	public void onMessage(Message message) {
		try {
			// 获取消息
			ActiveMQTextMessage activeMQTextMessage = (ActiveMQTextMessage) message;
			String id = activeMQTextMessage.getText();
			System.out.println("service-search，获取到的id："+id);
			// 消费消息
			itemSearchService.saveItemToSolr(Long.parseLong(id));
		} catch (JMSException e) {
			e.printStackTrace();
		}
	}

}

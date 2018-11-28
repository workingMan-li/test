package cn.itcast.core.listener;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;

import org.apache.activemq.command.ActiveMQTextMessage;
import org.springframework.beans.factory.annotation.Autowired;

import cn.itcast.core.service.search.ItemSearchService;

/**
 * 
 * @ClassName: ItemDeleteListener
 * @Company: http://www.itcast.cn/
 * @Description: 自定义消息监听器
 * @author 阮文 
 * @date 2018年10月10日 上午9:15:24
 */
public class ItemDeleteListener implements MessageListener {
	
	@Autowired
	private ItemSearchService itemSearchService;

	/*
	 * (non-Javadoc)
	 * <p>Title: onMessage</p> 
	 * <p>Description: 删除索引库</p> 
	 * @param arg0 
	 * @see javax.jms.MessageListener#onMessage(javax.jms.Message)
	 */
	@Override
	public void onMessage(Message message) {
		try {
			// 1、获取消息
			ActiveMQTextMessage activeMQTextMessage = (ActiveMQTextMessage) message;
			String id = activeMQTextMessage.getText();
			// 2、处理消息
			itemSearchService.deleteItemFromSolr(id);
		} catch (JMSException e) {
			e.printStackTrace();
		}
		
	}

}

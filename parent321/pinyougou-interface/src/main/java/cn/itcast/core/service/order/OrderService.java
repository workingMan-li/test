package cn.itcast.core.service.order;

import cn.itcast.core.pojo.order.Order;

public interface OrderService {

	/**
	 * 
	 * @Title: add
	 * @Description: 提交订单 
	 * @param username
	 * @param order
	 * @return void
	 * @throws
	 */
	public void add(String username, Order order);
}

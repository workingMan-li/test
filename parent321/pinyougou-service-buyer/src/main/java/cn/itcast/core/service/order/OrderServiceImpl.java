package cn.itcast.core.service.order;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.dubbo.config.annotation.Service;

import cn.itcast.core.dao.item.ItemDao;
import cn.itcast.core.dao.order.OrderDao;
import cn.itcast.core.dao.order.OrderItemDao;
import cn.itcast.core.pojo.cart.Cart;
import cn.itcast.core.pojo.item.Item;
import cn.itcast.core.pojo.order.Order;
import cn.itcast.core.pojo.order.OrderItem;
import cn.itcast.utils.uniqueuekey.IdWorker;

@Service
public class OrderServiceImpl implements OrderService {

	@Autowired
	private OrderDao orderDao;
	
	@Autowired
	private OrderItemDao orderItemDao;
	
	@Autowired
	private RedisTemplate<String, Object> redisTemplate;
	
	@Autowired
	private IdWorker idWorker;
	
	@Autowired
	private ItemDao itemDao;
	
	/*
	 * (non-Javadoc)
	 * <p>Title: add</p> 
	 * <p>Description: 提交订单</p> 
	 * @param username
	 * @param order 
	 * @see cn.itcast.core.service.order.OrderService#add(java.lang.String, cn.itcast.core.pojo.order.Order)
	 */
	@Transactional
	@Override
	public void add(String username, Order order) {
		// 取出购物车
		List<Cart> cartList = (List<Cart>) redisTemplate.boundHashOps("BUYER_CART").get(username);
		// 1、保存订单：根据商家id进行分类
		if(cartList != null){
			for (Cart cart : cartList) {
				long orderId = idWorker.nextId();
				order.setOrderId(orderId);	// 订单主键
				order.setPaymentType("1");	// 支付方式：在线支付
				order.setStatus("1");		// 支付状态：未付款
				order.setCreateTime(new Date()); // 订单创建日期
				order.setSourceType("2");	// 订单来源：pc端
				order.setSellerId(cart.getSellerId());	// 该订单属于哪个商家
				double payment = 0f;
				// 2、保存订单明细：购物项
				List<OrderItem> orderItemList = cart.getOrderItemList();
				if(orderItemList != null){
					for (OrderItem orderItem : orderItemList) {
						long id = idWorker.nextId();
						orderItem.setId(id);	// 明细的主键
						orderItem.setOrderId(orderId);	// 外键
//						orderItem.setItemId(itemId);
						Item item = itemDao.selectByPrimaryKey(orderItem.getItemId());
						orderItem.setGoodsId(item.getGoodsId());	// 商品id
						orderItem.setTitle(item.getTitle());	// 商品标题
						orderItem.setPrice(item.getPrice());	// 商品的单价
						orderItem.setPicPath(item.getImage());	// 商品图片
						orderItem.setSellerId(cart.getSellerId());	// 商家id
						double totalFee = item.getPrice().doubleValue() * orderItem.getNum();
						orderItem.setTotalFee(new BigDecimal(totalFee));	// 该明细的总价
						payment += totalFee;	// 该商家下的订单总金额
						// 保存明细
						orderItemDao.insertSelective(orderItem);
					}
				}
				
				order.setPayment(new BigDecimal(payment));
				// 保存订单
				orderDao.insertSelective(order);
			}
		}
		// 3、删除购物车
		redisTemplate.boundHashOps("BUYER_CART").delete(username);
	}

}

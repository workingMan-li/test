package cn.itcast.core.service.cart;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;

import com.alibaba.dubbo.config.annotation.Service;

import cn.itcast.core.dao.item.ItemDao;
import cn.itcast.core.pojo.cart.Cart;
import cn.itcast.core.pojo.item.Item;
import cn.itcast.core.pojo.order.OrderItem;

@Service
public class CartServiceImpl implements CartService {
	
	@Autowired
	private ItemDao itemDao;
	
	@Autowired
	private RedisTemplate<String, Object> redisTemplate;

	/*
	 * (non-Javadoc)
	 * <p>Title: findOne</p> 
	 * <p>Description: 根据id查询实体</p> 
	 * @param id
	 * @return 
	 * @see cn.itcast.core.service.cart.CartService#findOne(java.lang.Long)
	 */
	@Override
	public Item findOne(Long id) {
		return itemDao.selectByPrimaryKey(id);
	}

	/*
	 * (non-Javadoc)
	 * <p>Title: findCartList</p> 
	 * <p>Description: 将购物车的数据进行填充</p> 
	 * @param cartList
	 * @return 
	 * @see cn.itcast.core.service.cart.CartService#findCartList(java.util.List)
	 */
	@Override
	public List<Cart> findCartList(List<Cart> cartList) {
		for (Cart cart : cartList) {
			List<OrderItem> orderItemList = cart.getOrderItemList();
			for (OrderItem orderItem : orderItemList) {
				// 填充购车中的数据
				Item item = itemDao.selectByPrimaryKey(orderItem.getItemId());
				cart.setSellerName(item.getSeller());	// 商家店铺名称
				orderItem.setPicPath(item.getImage());	// 商品的图片
				orderItem.setTitle(item.getTitle());	// 商品的标题
				orderItem.setPrice(item.getPrice());	// 商品单价
				BigDecimal totalFee = new BigDecimal(item.getPrice().doubleValue() * orderItem.getNum());
				orderItem.setTotalFee(totalFee);		// 商品小计
			}
		}
		return cartList;
	}

	/*
	 * (non-Javadoc)
	 * <p>Title: mergeCartList</p> 
	 * <p>Description: 将购物车保存到redis</p> 
	 * @param newCartList
	 * @param username 
	 * @see cn.itcast.core.service.cart.CartService#mergeCartList(java.util.List, java.lang.String)
	 */
	@Override
	public void mergeCartList(List<Cart> newCartList, String username) {
		// 将购物车进行合并：将本地的购物车（新车）合并到redis中（老车）
		// 1、取出老车
		List<Cart> oldCartList = (List<Cart>) redisTemplate.boundHashOps("BUYER_CART").get(username);
		// 2、将新车合并到老车
		oldCartList = mergeNewCartListToOldCartList(newCartList, oldCartList);
		// 3、将合并后的老车存储到redis中
		redisTemplate.boundHashOps("BUYER_CART").put(username, oldCartList);
	}

	// 将新车合并到老车中
	private List<Cart> mergeNewCartListToOldCartList(List<Cart> newCartList, List<Cart> oldCartList) {
		if(newCartList != null){
			if(oldCartList != null){
				// 新车、老车都存在：需要进行合并
				// 遍历新车：将新车合并到老车中
				for (Cart newCart : newCartList) {
					// 判断是否是同一个商家
					int sellerIndexOf = oldCartList.indexOf(newCart);
					if(sellerIndexOf != -1){
						// 老的购物项
						List<OrderItem> oldOrderItemList = oldCartList.get(sellerIndexOf).getOrderItemList();
						// 新的购物项
						List<OrderItem> newOrderItemList = newCart.getOrderItemList();
						// 是同一个商家：继续判断是否是同款商品
						for (OrderItem newOrderItem : newOrderItemList) {
							int itemIndexOf = oldOrderItemList.indexOf(newOrderItem);
							if(itemIndexOf != -1){
								// 同款商品：合并数量
								OrderItem oldOrderItem = oldOrderItemList.get(itemIndexOf);
								oldOrderItem.setNum(oldOrderItem.getNum() + newOrderItem.getNum());
							}else{
								// 不是同款商品：直接添加到购物清单中（购物项集）
								oldOrderItemList.add(newOrderItem);
							}
						}
						
					}else{
						// 不是同一个商家，直接装车
						oldCartList.add(newCart);
					}
				}
			}else{
				// 说明是第一次买东西，直接返回新车
				return newCartList;
			}
		}else{
			// 新车为空，直接返回老车
			return oldCartList;
		}
		return oldCartList;
	}

	/*
	 * (non-Javadoc)
	 * <p>Title: findCartListFromRedis</p> 
	 * <p>Description: 从redis中查询购物车</p> 
	 * @param username
	 * @return 
	 * @see cn.itcast.core.service.cart.CartService#findCartListFromRedis(java.lang.String)
	 */
	@Override
	public List<Cart> findCartListFromRedis(String username) {
		List<Cart> cartList = (List<Cart>) redisTemplate.boundHashOps("BUYER_CART").get(username);
		return cartList;
	}
	
	

}

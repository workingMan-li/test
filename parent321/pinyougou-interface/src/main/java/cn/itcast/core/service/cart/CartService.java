package cn.itcast.core.service.cart;

import java.util.List;

import cn.itcast.core.pojo.cart.Cart;
import cn.itcast.core.pojo.item.Item;

public interface CartService {

	/**
	 * 
	 * @Title: findOne
	 * @Description: 根据id查询实体
	 * @param id
	 * @return
	 * @return Item
	 * @throws
	 */
	public Item findOne(Long id);

	/**
	 * 
	 * @Title: findCartList
	 * @Description: 将购物车的数据进行填充
	 * @param cartList
	 * @return
	 * @return List<Cart>
	 * @throws
	 */
	public List<Cart> findCartList(List<Cart> cartList);

	/**
	 * 
	 * @Title: mergeCartList
	 * @Description: 将购物车保存到redis
	 * @param cartList
	 * @param username
	 * @return void
	 * @throws
	 */
	public void mergeCartList(List<Cart> cartList, String username);

	/**
	 * 
	 * @Title: findCartListFromRedis
	 * @Description: 从redis中查询购物车
	 * @param username
	 * @return
	 * @return List<Cart>
	 * @throws
	 */
	public List<Cart> findCartListFromRedis(String username);
}

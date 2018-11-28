package cn.itcast.core.controller.cart;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSON;

import cn.itcast.core.entity.Result;
import cn.itcast.core.pojo.cart.Cart;
import cn.itcast.core.pojo.order.OrderItem;
import cn.itcast.core.service.cart.CartService;

@RestController
@RequestMapping("/cart")
public class CartController {
	
	@Reference
	private CartService cartService;

	// 异步的跨域请求：都是ajax的请求
	// 其他的方案：jsonp 写ajax请求 $ajax(){url,data,dataType='jsonp'}
	// 服务器端：springmvc 方法的形参中需要接收到回调函数： 响应的时候需要设置数据
	// MappingJacksonValue mappingJacksonValue = new MappingJacksonValue("xxx");
	// mappingJacksonValue.setJsonpFunction(callback);
	// 需要服务器端支持CORS跨域的请求
	// response.setHeader("Access-Control-Allow-Origin",
	// "http://localhost:9003");
	// 跨域请求中可以携带cookie的数据
	// response.setHeader("Access-Control-Allow-Credentials", "true");

	// springmvc支持跨域请求
	// @CrossOrigin(origins={"http://localhost:9003"}, allowCredentials="true")
	// allowCredentials="true"：该属性的默认值就是true
	@CrossOrigin(origins = { "http://localhost:9003" })
	@RequestMapping("/addGoodsToCartList.do")
	public Result addGoodsToCartList(Long itemId, Integer num, 
			HttpServletRequest request, HttpServletResponse response) {
		try {
			// 获取登录的用户信息
			// anonymousUser
			String username = SecurityContextHolder.getContext().getAuthentication().getName();
			System.out.println("username:" + username);
			// 将商品加入购物车的实现：
			// 1、定义一个空车的集合
			List<Cart> cartList = null;
			// 2、判断本地cookie中是否有车子 
			boolean flag = false;	// 定义了标识（开关）
			Cookie[] cookies = request.getCookies();
			if(cookies != null && cookies.length > 0){
				for (Cookie cookie : cookies) {  // key-value
					if("BUYER_CART".equals(cookie.getName())){
						// 3、有：直接赋值给定义的空车 
						String text = cookie.getValue();	// 购物车的json串
						cartList = JSON.parseArray(text, Cart.class);
						flag = true;	// 本地有
						break;	// 找到购物车跳出循环
					}
				}
			}
			// 4、没有：说明是第一次，创建一个车子
			if(cartList == null){
				cartList = new ArrayList<>();
			}
			// 结果：有车了
			// 5、创建cart并且封装数据，给cart进行瘦身：保存到cookie中（大小有限制）
			Cart cart = new Cart();
			cart.setSellerId(cartService.findOne(itemId).getSellerId());  // 商家的id
			List<OrderItem> orderItemList = new ArrayList<>();
			OrderItem orderItem = new OrderItem();
			orderItem.setItemId(itemId);
			orderItem.setNum(num);
			orderItemList.add(orderItem);
			cart.setOrderItemList(orderItemList); // 购物项
			// 6、将商品加入购物车：判断 
			// 6-1：判断该商品是否属于同一个商家：。
			int sellerIndexOf = cartList.indexOf(cart); // 判断商家的id即可
			if(sellerIndexOf != -1){
				// 同一个商家
				// 取出这个商品
				Cart oldCart = cartList.get(sellerIndexOf);
				// 判断是否是同款商品
				int itemIndexOf = oldCart.getOrderItemList().indexOf(orderItem);
				if(itemIndexOf != -1){
					// 同款：合并数量
					OrderItem oldOrderItem = oldCart.getOrderItemList().get(itemIndexOf);
					oldOrderItem.setNum(oldOrderItem.getNum() + num);
				}else{
					// 不是同款：直接加入购物项中
					oldCart.getOrderItemList().add(orderItem);
				}
			}else{
				// 不是同一个商家：直接将商品加入购物车中
				cartList.add(cart);
			}
			// 7、将购物车保存起来：cookie中  value:对象---json串 
			if(!"anonymousUser".equals(username)){
				// 7-1、用户登录了：购物车保存到服务器端（Redis）
				// 将购物车保存到redis中（将本地同步到redis中）
				cartService.mergeCartList(cartList, username);
				// 本地有购物车：才清空本地的购物车
				if(flag){
					Cookie cookie = new Cookie("BUYER_CART", null);
					cookie.setMaxAge(0);
					cookie.setPath("/");
					response.addCookie(cookie);
				}
			}else{
				// 7-2、匿名用户，未登录
				// 如果用户没有登（匿名用户）：将购物车保存到cookie中
				Cookie cookie = new Cookie("BUYER_CART", JSON.toJSONString(cartList));
				cookie.setMaxAge(60*60);
				// cookie共享   http://ip1:port/project1   http://ip2:port/project2
				cookie.setPath("/");
				response.addCookie(cookie);
			}
			return new Result(true, "商品加入购物车成功");
		} catch (Exception e) {
			e.printStackTrace();
			return new Result(false, "商品加入购物车失败");
		}
	}
	
	/**
	 * 
	 * @Title: findCartList
	 * @Description: 查询购物车的列表
	 * @return
	 * @return List<Cart>
	 * @throws
	 */
	@RequestMapping("/findCartList.do")
	public List<Cart> findCartList(HttpServletRequest request, HttpServletResponse response){
		String username = SecurityContextHolder.getContext().getAuthentication().getName();
		// 未登录：从cookie中查询
		List<Cart> cartList = null;
		Cookie[] cookies = request.getCookies();
		if(cookies != null && cookies.length > 0){
			for (Cookie cookie : cookies) { 
				if("BUYER_CART".equals(cookie.getName())){
					String text = cookie.getValue();
					cartList = JSON.parseArray(text, Cart.class);
					break;	
				}
			}
		}
		// 已登录：从redis中查询
		if(!"anonymousUser".equals(username)){
			// 将本地的购物车同步到redis中
			// 场景：在没有登录的情况下将购物车保存了，然后用户在进行登录，跳转到网站的首页
			// 在首页中：【我的购物车】，跳转到购物车页面：将本地的购车同步到redis中。
			if(cartList != null){
				cartService.mergeCartList(cartList, username);
				Cookie cookie = new Cookie("BUYER_CART", null);
				cookie.setMaxAge(0);
				cookie.setPath("/");
				response.addCookie(cookie);
			}
			// 从redis中查询
			cartList = cartService.findCartListFromRedis(username);
		}
			
		// 不管从哪里获取到的数据：不完整。将数据进行填充
		if(cartList != null){
			// 将购物车的数据进行填充
			cartList = cartService.findCartList(cartList);
		}
		return cartList;
	}
}

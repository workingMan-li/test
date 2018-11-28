package cn.itcast.core.pojo.cart;

import java.io.Serializable;
import java.util.List;

import cn.itcast.core.pojo.order.OrderItem;

/**
 * 
 * @ClassName: Cart
 * @Company: http://www.itcast.cn/
 * @Description: 购物车对象
 * @author 阮文 
 * @date 2018年10月13日 上午10:12:44
 */
@SuppressWarnings("serial")
public class Cart implements Serializable {

	private String sellerId;				// 商家id
	private String sellerName;				// 商家的店铺名称
	private List<OrderItem> OrderItemList;	// 该商家下的购物项
	
	public String getSellerId() {
		return sellerId;
	}
	public void setSellerId(String sellerId) {
		this.sellerId = sellerId;
	}
	public String getSellerName() {
		return sellerName;
	}
	public void setSellerName(String sellerName) {
		this.sellerName = sellerName;
	}
	public List<OrderItem> getOrderItemList() {
		return OrderItemList;
	}
	public void setOrderItemList(List<OrderItem> orderItemList) {
		OrderItemList = orderItemList;
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((sellerId == null) ? 0 : sellerId.hashCode());
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Cart other = (Cart) obj;
		if (sellerId == null) {
			if (other.sellerId != null)
				return false;
		} else if (!sellerId.equals(other.sellerId))
			return false;
		return true;
	}
	
	
	
	
}

package cn.itcast.core.vo;

import java.io.Serializable;
import java.util.List;

import cn.itcast.core.pojo.good.Goods;
import cn.itcast.core.pojo.good.GoodsDesc;
import cn.itcast.core.pojo.item.Item;

/**
 * 
 * @ClassName: GoodsVo
 * @Company: http://www.itcast.cn/
 * @Description: 封装商品信息
 * @author 阮文 
 * @date 2018年9月22日 上午11:27:34
 */
@SuppressWarnings("serial")
public class GoodsVo implements Serializable{

	private Goods goods;			// 商品基本信息
	private GoodsDesc goodsDesc;	// 商品描述信息
	private List<Item> itemList;	// 商品对应的库存信息
	
	public Goods getGoods() {
		return goods;
	}
	public void setGoods(Goods goods) {
		this.goods = goods;
	}
	public GoodsDesc getGoodsDesc() {
		return goodsDesc;
	}
	public void setGoodsDesc(GoodsDesc goodsDesc) {
		this.goodsDesc = goodsDesc;
	}
	public List<Item> getItemList() {
		return itemList;
	}
	public void setItemList(List<Item> itemList) {
		this.itemList = itemList;
	}
	
	
}

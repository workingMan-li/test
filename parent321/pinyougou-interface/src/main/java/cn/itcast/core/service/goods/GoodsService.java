package cn.itcast.core.service.goods;

import cn.itcast.core.entity.PageResult;
import cn.itcast.core.pojo.good.Goods;
import cn.itcast.core.vo.GoodsVo;

public interface GoodsService {

	/**
	 * 
	 * @Title: add
	 * @Description: 新增商品
	 * @param goodsVo
	 * @return void
	 * @throws
	 */
	public void add(GoodsVo goodsVo);
	
	/**
	 * 
	 * @Title: search
	 * @Description: 查询当前商家下的商品列表信息
	 * @param page
	 * @param rows
	 * @param goods
	 * @return
	 * @return PageResult
	 * @throws
	 */
	public PageResult search(Integer page, Integer rows, Goods goods);
	
	/**
	 * 
	 * @Title: findOne
	 * @Description: 编辑商品
	 * @param id
	 * @return
	 * @return GoodsVo
	 * @throws
	 */
	public GoodsVo findOne(Long id);
	
	/**
	 * 
	 * @Title: update
	 * @Description: 更新商品
	 * @param goodsVo
	 * @return void
	 * @throws
	 */
	public void update(GoodsVo goodsVo);
	
	/**
	 * 
	 * @Title: search
	 * @Description: 运营商系统查询待审核的商品
	 * @param page
	 * @param rows
	 * @param goods
	 * @return
	 * @return PageResult
	 * @throws
	 */
	public PageResult searchForManager(Integer page, Integer rows, Goods goods);
	
	/**
	 * 
	 * @Title: updateStatus
	 * @Description: 商品审核
	 * @param ids
	 * @param status
	 * @return void
	 * @throws
	 */
	public void updateStatus(Long[] ids, String status);
	
	/**
	 * 
	 * @Title: delete
	 * @Description: 批量删除商品
	 * @param ids
	 * @return void
	 * @throws
	 */
	public void delete(Long[] ids);
}

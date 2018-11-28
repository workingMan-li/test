package cn.itcast.core.service.itemcat;

import java.util.List;

import cn.itcast.core.pojo.item.ItemCat;

public interface ItemCatSerivce {

	/**
	 * 
	 * @Title: findByParentId
	 * @Description: 商品分类的列表查询（根据父节点查询该子节点列表）
	 * @param parentId
	 * @return
	 * @return List<ItemCat>
	 * @throws
	 */
	public List<ItemCat> findByParentId(Long parentId);
	
	/**
	 * 
	 * @Title: findOne
	 * @Description: 确定三级分类后，确定模板id
	 * @param id
	 * @return
	 * @return ItemCat
	 * @throws
	 */
	public ItemCat findOne(Long id);
	
	/**
	 * 
	 * @Title: findAll
	 * @Description: 显示具体的分类名称
	 * @return
	 * @return List<ItemCat>
	 * @throws
	 */
	public List<ItemCat> findAll();
}

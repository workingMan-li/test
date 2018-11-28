package cn.itcast.core.service.search;

import java.util.Map;

public interface ItemSearchService {

	/**
	 * 
	 * @Title: search
	 * @Description: 前台系统的检索
	 * @param searchMap
	 * @return
	 * @return Map<String,Object>
	 * @throws
	 */
	public Map<String, Object> search(Map<String, String> searchMap);
	
	/**
	 * 
	 * @Title: saveItemToSolr
	 * @Description: 将商品信息保存到索引库中
	 * @param id
	 * @return void
	 * @throws
	 */
	public void saveItemToSolr(Long id);
	
	/**
	 * 
	 * @Title: deleteItemFromSolr
	 * @Description: 将商品从索引库中删除
	 * @param id
	 * @return void
	 * @throws
	 */
	public void deleteItemFromSolr(String id);
}

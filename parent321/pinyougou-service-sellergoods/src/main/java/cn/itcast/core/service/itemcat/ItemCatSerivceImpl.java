package cn.itcast.core.service.itemcat;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;

import com.alibaba.dubbo.config.annotation.Service;

import cn.itcast.core.dao.item.ItemCatDao;
import cn.itcast.core.pojo.item.ItemCat;
import cn.itcast.core.pojo.item.ItemCatQuery;

@Service
public class ItemCatSerivceImpl implements ItemCatSerivce {
	
	@Autowired
	private ItemCatDao itemCatDao;
	
	@Autowired
	private RedisTemplate<String, Object> redisTemplate;

	/*
	 * (non-Javadoc)
	 * <p>Title: findByParentId</p> 
	 * <p>Description: 商品分类的列表查询</p> 
	 * @param parentId
	 * @return 
	 * @see cn.itcast.core.service.itemcat.ItemCatSerivce#findByParentId(java.lang.Long)
	 */
	@Override
	public List<ItemCat> findByParentId(Long parentId) {
		// 将商品全部分类放入缓存中
		List<ItemCat> list = itemCatDao.selectByExample(null);
		if(list != null && list.size() > 0){
			for (ItemCat itemCat : list) {
				// 存储商品分类名称---对应typeId
				redisTemplate.boundHashOps("itemCat").put(itemCat.getName(), itemCat.getTypeId());
			}
		}
		
		// 封装查询条件
		ItemCatQuery itemCatQuery = new ItemCatQuery();
		itemCatQuery.createCriteria().andParentIdEqualTo(parentId);
		return itemCatDao.selectByExample(itemCatQuery);
	}

	/*
	 * (non-Javadoc)
	 * <p>Title: findOne</p> 
	 * <p>Description: 确定三级分类后，确定模板id</p> 
	 * @param id
	 * @return 
	 * @see cn.itcast.core.service.itemcat.ItemCatSerivce#findOne(java.lang.Long)
	 */
	@Override
	public ItemCat findOne(Long id) {
		return itemCatDao.selectByPrimaryKey(id);
	}

	/*
	 * (non-Javadoc)
	 * <p>Title: findAll</p> 
	 * <p>Description: 显示具体的分类名称</p> 
	 * @return 
	 * @see cn.itcast.core.service.itemcat.ItemCatSerivce#findAll()
	 */
	@Override
	public List<ItemCat> findAll() {
		return itemCatDao.selectByExample(null);
	}
	
}

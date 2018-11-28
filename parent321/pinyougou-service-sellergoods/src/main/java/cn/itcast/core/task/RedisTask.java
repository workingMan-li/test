package cn.itcast.core.task;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSON;

import cn.itcast.core.dao.item.ItemCatDao;
import cn.itcast.core.dao.specification.SpecificationOptionDao;
import cn.itcast.core.dao.template.TypeTemplateDao;
import cn.itcast.core.pojo.item.ItemCat;
import cn.itcast.core.pojo.specification.SpecificationOption;
import cn.itcast.core.pojo.specification.SpecificationOptionQuery;
import cn.itcast.core.pojo.template.TypeTemplate;

@Component
public class RedisTask {
	
	@Autowired
	private ItemCatDao itemCatDao;
	
	@Autowired
	private RedisTemplate<String, Object> redisTemplate;
	
	@Autowired
	private TypeTemplateDao typeTemplateDao;
	
	@Autowired
	private SpecificationOptionDao specificationOptionDao;
	
	

	// 年月日时分秒
	@Scheduled(cron="0 31 10 * * ?")
	public void setItemCatToRedis(){
		// 将商品全部分类放入缓存中
		List<ItemCat> list = itemCatDao.selectByExample(null);
		if(list != null && list.size() > 0){
			for (ItemCat itemCat : list) {
				// 存储商品分类名称---对应typeId
				redisTemplate.boundHashOps("itemCat").put(itemCat.getName(), itemCat.getTypeId());
			}
		}
		System.out.println("将分类同步到redis啦啦啦啦。。。");
	}
	
	@Scheduled(cron="0 31 10 * * ?")
	public void setBrandsAndSpecsToReids(){
		List<TypeTemplate> list = typeTemplateDao.selectByExample(null);
		if(list != null && list.size() > 0){
			for (TypeTemplate typeTemplate : list) {
				// 将品牌列表放入缓存
				// 栗子：{"id":10,"text":"VIVO"},{"id":11,"text":"诺基亚"},{"id":12,"text":"锤子"}]
				String brandIds = typeTemplate.getBrandIds();
				List<Map> brandList = JSON.parseArray(brandIds, Map.class);
				redisTemplate.boundHashOps("brandList").put(typeTemplate.getId(), brandList);
				// 将规格选项列表放入缓存
				// [{"id":27,"text":"网络"},{"id":32,"text":"机身内存"}] 
				List<Map> specList = findBySpecList(typeTemplate.getId());	// 规格选项
				redisTemplate.boundHashOps("specList").put(typeTemplate.getId(), specList);
			}
		}
		System.out.println("将模板同步到redis啦啦啦啦。。。");
	}
	
	public List<Map> findBySpecList(Long id) {
		TypeTemplate typeTemplate = typeTemplateDao.selectByPrimaryKey(id);
		// 首先获取规格
		// 例如：[{"id":27,"text":"网络"},{"id":32,"text":"机身内存"}]
		String specIds = typeTemplate.getSpecIds();
		List<Map> list = JSON.parseArray(specIds, Map.class);
		// 再根据规格获取对应的规格选项
		for (Map map : list) {
			Long specId = Long.parseLong(map.get("id").toString());
			SpecificationOptionQuery optionQuery = new SpecificationOptionQuery();
			optionQuery.createCriteria().andSpecIdEqualTo(specId);
			List<SpecificationOption> options = specificationOptionDao.selectByExample(optionQuery);
			// 将规格选项在添加map中
			map.put("options", options);
		}
		// [{"id":27,"text":"网络","optins":[{id:xx,name:xxx},{},...]},{"id":32,"text":"机身内存"}]
		return list;
	}
	
}

package cn.itcast.core.service.template;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;

import cn.itcast.core.dao.specification.SpecificationOptionDao;
import cn.itcast.core.dao.template.TypeTemplateDao;
import cn.itcast.core.entity.PageResult;
import cn.itcast.core.pojo.specification.SpecificationOption;
import cn.itcast.core.pojo.specification.SpecificationOptionQuery;
import cn.itcast.core.pojo.template.TypeTemplate;
import cn.itcast.core.pojo.template.TypeTemplateQuery;

@Service
public class TypeTemplateServiceImpl implements TypeTemplateService {

	@Autowired
	private TypeTemplateDao typeTemplateDao;
	
	@Autowired
	private SpecificationOptionDao specificationOptionDao;
	
	@Autowired
	private RedisTemplate<String, Object> redisTemplate;
	
	/*
	 * (non-Javadoc)
	 * <p>Title: search</p> 
	 * <p>Description: 模板列表查询</p> 
	 * @param page
	 * @param rows
	 * @param template
	 * @return 
	 * @see cn.itcast.core.service.template.TypeTemplateService#search(java.lang.Integer, java.lang.Integer, cn.itcast.core.pojo.template.TypeTemplate)
	 */
	@Override
	public PageResult search(Integer page, Integer rows, TypeTemplate template) {
		// 将模板的数据放入到缓存中
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
		
		// 设置分页条件
		PageHelper.startPage(page, rows); // 返回值Page   startRow = (page-1)*rows
		// 设置查询条件
		TypeTemplateQuery typeTemplateQuery = new TypeTemplateQuery();
		if(template.getName() != null && !"".equals(template.getName().trim())){
			typeTemplateQuery.createCriteria().andNameLike("%"+template.getName().trim()+"%");
		}
		typeTemplateQuery.setOrderByClause("id desc");
		// 将查询的结果封装到PageResult中
		Page<TypeTemplate> p = (Page<TypeTemplate>) typeTemplateDao.selectByExample(typeTemplateQuery);
		return new PageResult(p.getTotal(), p.getResult());
	}

	/*
	 * (non-Javadoc)
	 * <p>Title: add</p> 
	 * <p>Description: 新增模板</p> 
	 * @param template 
	 * @see cn.itcast.core.service.template.TypeTemplateService#add(cn.itcast.core.pojo.template.TypeTemplate)
	 */
	@Transactional
	@Override
	public void add(TypeTemplate template) {
		typeTemplateDao.insertSelective(template);
	}

	/*
	 * (non-Javadoc)
	 * <p>Title: findOne</p> 
	 * <p>Description: 确定模板后：加载模板中的所有的数据（规格、品牌、扩展属性）</p> 
	 * @param id
	 * @return 
	 * @see cn.itcast.core.service.template.TypeTemplateService#findOne(java.lang.Long)
	 */
	@Override
	public TypeTemplate findOne(Long id) {
		return typeTemplateDao.selectByPrimaryKey(id);
	}

	/*
	 * (non-Javadoc)
	 * <p>Title: findBySpecList</p> 
	 * <p>Description: 确定模板后：加载模板中的规格以及规格选项</p> 
	 * @param id
	 * @return 
	 * @see cn.itcast.core.service.template.TypeTemplateService#findBySpecList(java.lang.Long)
	 */
	@Override
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

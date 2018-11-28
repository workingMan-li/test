package cn.itcast.core.service.template;

import java.util.List;
import java.util.Map;

import cn.itcast.core.entity.PageResult;
import cn.itcast.core.pojo.template.TypeTemplate;

public interface TypeTemplateService {

	/**
	 * 
	 * @Title: search
	 * @Description: 模板列表查询
	 * @param page
	 * @param rows
	 * @param template
	 * @return
	 * @return PageResult
	 * @throws
	 */
	public PageResult search(Integer page, Integer rows, TypeTemplate template);
	
	/**
	 * 
	 * @Title: add
	 * @Description: 新增模板
	 * @param template
	 * @return void
	 * @throws
	 */
	public void add(TypeTemplate template);
	
	/**
	 * 
	 * @Title: findOne
	 * @Description: 确定模板后：加载模板中的所有的数据（规格、品牌、扩展属性）
	 * @param id
	 * @return
	 * @return TypeTemplate
	 * @throws
	 */
	public TypeTemplate findOne(Long id);
	
	/**
	 * 
	 * @Title: findBySpecList
	 * @Description: 确定模板后：加载模板中的规格以及规格选项
	 * @param id
	 * @return
	 * @return List<Map>
	 * @throws
	 */
	public List<Map> findBySpecList(Long id);
}

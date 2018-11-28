package cn.itcast.core.service.spec;

import java.util.List;
import java.util.Map;

import cn.itcast.core.entity.PageResult;
import cn.itcast.core.pojo.specification.Specification;
import cn.itcast.core.vo.SpecificationVo;

public interface SpecificationService {

	/**
	 * 
	 * @Title: search
	 * @Description: 规格列表查询
	 * @param pageNum
	 * @param pageSize
	 * @param specification
	 * @return
	 * @return PageResult
	 * @throws
	 */
	public PageResult search(Integer pageNum, Integer pageSize, Specification specification);
	
	/**
	 * 
	 * @Title: add
	 * @Description: 添加规格
	 * @param specificationVo
	 * @return void
	 * @throws
	 */
	public void add(SpecificationVo specificationVo);
	
	/**
	 * 
	 * @Title: findOne
	 * @Description: 规格回显
	 * @param id
	 * @return
	 * @return SpecificationVo
	 * @throws
	 */
	public SpecificationVo findOne(Long id);
	
	/**
	 * 
	 * @Title: update
	 * @Description: 规格更新
	 * @param specificationVo
	 * @return void
	 * @throws
	 */
	public void update(SpecificationVo specificationVo);

	/**
	 * 
	 * @Title: selectOptionList
	 * @Description: 新增模板时初始化规格列表
	 * @return void
	 * @throws
	 */
	public List<Map<String, String>> selectOptionList();
}

package cn.itcast.core.service.brand;

import java.util.List;
import java.util.Map;

import cn.itcast.core.entity.PageResult;
import cn.itcast.core.pojo.good.Brand;

public interface BrandService {

	/**
	 * 
	 * @Title: findAll
	 * @Description: 查询所有品牌
	 * @return
	 * @return List<Brand>
	 * @throws
	 */
	public List<Brand> findAll();
	
	/**
	 * 
	 * @Title: findPage
	 * @Description: 分页查询
	 * @param pageNum   当前页码
	 * @param pageSize  每页显示的条数
	 * @return
	 * @return PageResult
	 * @throws
	 */
	public PageResult findPage(Integer pageNum, Integer pageSize);
	
	/**
	 * 
	 * @Title: search
	 * @Description: 条件查询
	 * @param pageNum
	 * @param pageSize
	 * @param brand
	 * @return
	 * @return PageResult
	 * @throws
	 */
	public PageResult search(Integer pageNum, Integer pageSize, Brand brand);
	
	/**
	 * 
	 * @Title: add
	 * @Description: 添加品牌
	 * @param brand
	 * @return void
	 * @throws
	 */
	public void add(Brand brand);
	
	/**
	 * 
	 * @Title: findOne
	 * @Description: 回显品牌
	 * @param id
	 * @return
	 * @return Brand
	 * @throws
	 */
	public Brand findOne(Long id);
	
	/**
	 * 
	 * @Title: update
	 * @Description: 更新品牌
	 * @param brand
	 * @return void
	 * @throws
	 */
	public void update(Brand brand);
	
	/**
	 * 
	 * @Title: delete
	 * @Description: 批量删除
	 * @param ids
	 * @return void
	 * @throws
	 */
	public void delete(Long[] ids);

	/**
	 * 
	 * @Title: selectOptionList
	 * @Description: 新增模板的时候初始化品牌列表
	 * @return
	 * @return List<Map<String,String>>
	 * @throws
	 */
	public List<Map<String, String>> selectOptionList();
}

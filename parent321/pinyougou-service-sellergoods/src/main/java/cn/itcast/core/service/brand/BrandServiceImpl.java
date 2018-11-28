package cn.itcast.core.service.brand;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;

import cn.itcast.core.dao.good.BrandDao;
import cn.itcast.core.entity.PageResult;
import cn.itcast.core.pojo.good.Brand;
import cn.itcast.core.pojo.good.BrandQuery;
import cn.itcast.core.pojo.good.BrandQuery.Criteria;

@Service
public class BrandServiceImpl implements BrandService {
	
	@Autowired
	private BrandDao brandDao;

	/*
	 * (non-Javadoc)
	 * <p>Title: findAll</p> 
	 * <p>Description: 查询所有品牌</p> 
	 * @return 
	 * @see cn.itcast.core.service.brand.BrandService#findAll()
	 */
	@Override
	public List<Brand> findAll() {
		List<Brand> list = brandDao.selectByExample(null);
		return list;
	}

	/*
	 * (non-Javadoc)
	 * <p>Title: findPage</p> 
	 * <p>Description: 分页查询</p> 
	 * @param pageNum
	 * @param pageSize
	 * @return 
	 * @see cn.itcast.core.service.brand.BrandService#findPage(java.lang.Integer, java.lang.Integer)
	 */
	@Override
	public PageResult findPage(Integer pageNum, Integer pageSize) {
		// 设置分页条件
		PageHelper.startPage(pageNum, pageSize);
		// 查询品牌信息
		Page<Brand> page = (Page<Brand>) brandDao.selectByExample(null);
		// 结果封装到pageResult对象
		return new PageResult(page.getTotal(), page.getResult());
	}

	/*
	 * (non-Javadoc)
	 * <p>Title: search</p> 
	 * <p>Description: 条件查询</p> 
	 * @param pageNum
	 * @param pageSize
	 * @param brand
	 * @return 
	 * @see cn.itcast.core.service.brand.BrandService#findPage(java.lang.Integer, java.lang.Integer, cn.itcast.core.pojo.good.Brand)
	 */
	@Override
	public PageResult search(Integer pageNum, Integer pageSize, Brand brand) {
		// 设置分页条件
		PageHelper.startPage(pageNum, pageSize);
		// 查询品牌信息
		BrandQuery brandQuery = new BrandQuery();
		Criteria criteria = brandQuery.createCriteria(); // 封装查询条件  list
		if(brand.getName() != null && !"".equals(brand.getName().trim())){
			criteria.andNameLike("%"+brand.getName().trim()+"%");
		}
		if(brand.getFirstChar() != null && !"".equals(brand.getFirstChar().trim())){
			criteria.andFirstCharEqualTo(brand.getFirstChar().trim());
		}
		// 根据id进行降序
		brandQuery.setOrderByClause("id desc");
		Page<Brand> page = (Page<Brand>) brandDao.selectByExample(brandQuery);
		// 结果封装到pageResult对象
		return new PageResult(page.getTotal(), page.getResult());
	}

	/*
	 * (non-Javadoc)
	 * <p>Title: add</p> 
	 * <p>Description: 添加品牌</p> 
	 * @param brand 
	 * @see cn.itcast.core.service.brand.BrandService#add(cn.itcast.core.pojo.good.Brand)
	 */
	@Transactional
	@Override
	public void add(Brand brand) {
		brandDao.insertSelective(brand);
	}

	/*
	 * (non-Javadoc)
	 * <p>Title: findOne</p> 
	 * <p>Description: 回显品牌</p> 
	 * @param id
	 * @return 
	 * @see cn.itcast.core.service.brand.BrandService#findOne(java.lang.Long)
	 */
	@Override
	public Brand findOne(Long id) {
		return brandDao.selectByPrimaryKey(id);
	}

	/*
	 * (non-Javadoc)
	 * <p>Title: update</p> 
	 * <p>Description: 更新品牌</p> 
	 * @param brand 
	 * @see cn.itcast.core.service.brand.BrandService#update(cn.itcast.core.pojo.good.Brand)
	 */
	@Transactional
	@Override
	public void update(Brand brand) {
		brandDao.updateByPrimaryKeySelective(brand);
	}

	/*
	 * (non-Javadoc)
	 * <p>Title: delete</p> 
	 * <p>Description: 批量删除</p> 
	 * @param ids 
	 * @see cn.itcast.core.service.brand.BrandService#delete(java.lang.Long[])
	 */
	@Transactional
	@Override
	public void delete(Long[] ids) {
		if(ids != null && ids.length > 0){
			// 一个个删除：行   不好：增加了数据库吞吐量。
//			for (Long id : ids) {
//				brandDao.deleteByPrimaryKey(id);
//			}
			// 批量删除
			brandDao.deleteByPrimaryKeys(ids);
		}
	}

	/*
	 * (non-Javadoc)
	 * <p>Title: selectOptionList</p> 
	 * <p>Description: 新增模板的时候初始化品牌列表</p> 
	 * @return 
	 * @see cn.itcast.core.service.brand.BrandService#selectOptionList()
	 */
	@Override
	public List<Map<String, String>> selectOptionList() {
		return brandDao.selectOptionList();
	}
	
	
	
}

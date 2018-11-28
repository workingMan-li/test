package cn.itcast.core.service.spec;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;

import cn.itcast.core.dao.specification.SpecificationDao;
import cn.itcast.core.dao.specification.SpecificationOptionDao;
import cn.itcast.core.entity.PageResult;
import cn.itcast.core.pojo.specification.Specification;
import cn.itcast.core.pojo.specification.SpecificationOption;
import cn.itcast.core.pojo.specification.SpecificationOptionQuery;
import cn.itcast.core.pojo.specification.SpecificationQuery;
import cn.itcast.core.vo.SpecificationVo;

@Service
public class SpecificationServiceImpl implements SpecificationService {
	
	@Autowired
	private SpecificationDao specificationDao;
	
	@Autowired
	private SpecificationOptionDao specificationOptionDao;

	/*
	 * (non-Javadoc)
	 * <p>Title: search</p> 
	 * <p>Description: 规格列表查询</p> 
	 * @param pageNum
	 * @param pageSize
	 * @param specification
	 * @return 
	 * @see cn.itcast.core.service.spec.SpecificationService#search(java.lang.Integer, java.lang.Integer, cn.itcast.core.pojo.specification.Specification)
	 */
	@Override
	public PageResult search(Integer pageNum, Integer pageSize, Specification specification) {
		// 设置分页条件
		PageHelper.startPage(pageNum, pageSize);
		// 设置查询条件
		SpecificationQuery specificationQuery = new SpecificationQuery();
		if(specification.getSpecName() != null && !"".equals(specification.getSpecName().trim())){
			specificationQuery.createCriteria().andSpecNameLike("%"+specification.getSpecName().trim()+"%");
		}
		// 根据id降序
		specificationQuery.setOrderByClause("id desc");
		Page<Specification> page = (Page<Specification>) specificationDao.selectByExample(specificationQuery);
		return new PageResult(page.getTotal(), page.getResult());
	}

	/*
	 * (non-Javadoc)
	 * <p>Title: add</p> 
	 * <p>Description: 添加规格</p> 
	 * @param specificationVo 
	 * @see cn.itcast.core.service.spec.SpecificationService#add(cn.itcast.core.vo.SpecificationVo)
	 */
	@Transactional
	@Override
	public void add(SpecificationVo specificationVo) {
		// 保存规格
		Specification specification = specificationVo.getSpecification();
		specificationDao.insertSelective(specification); // 插入完成后返回自增主键id
		// 保存规格选项
		List<SpecificationOption> specificationOptionList = specificationVo.getSpecificationOptionList();
		if(specificationOptionList != null && specificationOptionList.size() > 0){
			for (SpecificationOption specificationOption : specificationOptionList) {
				specificationOption.setSpecId(specification.getId()); // 设置外键
//				specificationOptionDao.insertSelective(specificationOption); // 一条条插入
			}
			// 批量插入
			specificationOptionDao.insertSelectives(specificationOptionList);
		}
	}

	/*
	 * (non-Javadoc)
	 * <p>Title: findOne</p> 
	 * <p>Description: 规格回显</p> 
	 * @param id
	 * @return 
	 * @see cn.itcast.core.service.spec.SpecificationService#findOne(java.lang.Long)
	 */
	@Override
	public SpecificationVo findOne(Long id) {
		// 查询规格
		Specification specification = specificationDao.selectByPrimaryKey(id);
		// 查询规格选项
		SpecificationOptionQuery query = new SpecificationOptionQuery();
		query.createCriteria().andSpecIdEqualTo(id);
		List<SpecificationOption> specificationOptionList = specificationOptionDao.selectByExample(query);
		// 将数据封装到Vo中
		SpecificationVo specificationVo = new SpecificationVo();
		specificationVo.setSpecification(specification);
		specificationVo.setSpecificationOptionList(specificationOptionList);
		return specificationVo;
	}

	/*
	 * (non-Javadoc)
	 * <p>Title: update</p> 
	 * <p>Description: 规格更新</p> 
	 * @param specificationVo 
	 * @see cn.itcast.core.service.spec.SpecificationService#update(cn.itcast.core.vo.SpecificationVo)
	 */
	@Transactional
	@Override
	public void update(SpecificationVo specificationVo) {
		// 更新规格
		Specification specification = specificationVo.getSpecification();
		specificationDao.updateByPrimaryKeySelective(specification);
		// 更新规格选项(先删除再插入)
		// 先删除
		SpecificationOptionQuery query = new SpecificationOptionQuery();
		query.createCriteria().andSpecIdEqualTo(specification.getId());
		specificationOptionDao.deleteByExample(query);
		// 在插入
		List<SpecificationOption> specificationOptionList = specificationVo.getSpecificationOptionList();
		if(specificationOptionList != null && specificationOptionList.size() > 0){
			for (SpecificationOption specificationOption : specificationOptionList) {
				specificationOption.setSpecId(specification.getId()); // 设置外键
			}
			// 批量插入
			specificationOptionDao.insertSelectives(specificationOptionList);
		}
		
	}

	/*
	 * (non-Javadoc)
	 * <p>Title: selectOptionList</p> 
	 * <p>Description: 新增模板时初始化规格列表</p> 
	 * @return 
	 * @see cn.itcast.core.service.spec.SpecificationService#selectOptionList()
	 */
	@Override
	public List<Map<String, String>> selectOptionList() {
		return specificationDao.selectOptionList();
	}

	
	
	
}

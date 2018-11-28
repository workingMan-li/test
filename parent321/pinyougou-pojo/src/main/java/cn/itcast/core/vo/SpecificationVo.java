package cn.itcast.core.vo;

import java.io.Serializable;
import java.util.List;

import cn.itcast.core.pojo.specification.Specification;
import cn.itcast.core.pojo.specification.SpecificationOption;

/**
 * 
 * @ClassName: SpecificationVo
 * @Company: http://www.itcast.cn/
 * @Description: 封装页面提交数据的Vo对象
 * @author 阮文 
 * @date 2018年9月19日 下午12:02:16
 */
@SuppressWarnings("serial")
public class SpecificationVo implements Serializable{

	private Specification specification;						// 规格
	private List<SpecificationOption> specificationOptionList;	// 规格选项集
	
	public Specification getSpecification() {
		return specification;
	}
	public void setSpecification(Specification specification) {
		this.specification = specification;
	}
	public List<SpecificationOption> getSpecificationOptionList() {
		return specificationOptionList;
	}
	public void setSpecificationOptionList(List<SpecificationOption> specificationOptionList) {
		this.specificationOptionList = specificationOptionList;
	}
	
	
}

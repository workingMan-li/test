package cn.itcast.core.controller.spec;

import java.util.List;
import java.util.Map;

import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.dubbo.config.annotation.Reference;

import cn.itcast.core.entity.PageResult;
import cn.itcast.core.entity.Result;
import cn.itcast.core.pojo.specification.Specification;
import cn.itcast.core.service.spec.SpecificationService;
import cn.itcast.core.vo.SpecificationVo;

@RestController
@RequestMapping("/specification")
public class SpecificationController {

	@Reference
	private SpecificationService specificationService;
	
	/**
	 * 
	 * @Title: search
	 * @Description: 规格的列表查询
	 * @param pageNum
	 * @param pageSize
	 * @param specification
	 * @return
	 * @return PageResult
	 * @throws
	 */
	@RequestMapping("/search.do")
	public PageResult search(Integer page, Integer rows, @RequestBody Specification specification){
		return specificationService.search(page, rows, specification);
	}
	
	/**
	 * 
	 * @Title: add
	 * @Description: 添加规格
	 * @param specificationVo
	 * @return
	 * @return Result
	 * @throws
	 */
	@RequestMapping("/add.do")
	public Result add(@RequestBody SpecificationVo specificationVo){
		try {
			specificationService.add(specificationVo);
			return new Result(true, "保存成功");
		} catch (Exception e) {
			e.printStackTrace();
			return new Result(false, "保存失败");
		}
	}
	
	/**
	 * 
	 * @Title: findOne
	 * @Description: 规格回显
	 * @param id
	 * @return
	 * @return SpecificationVo
	 * @throws
	 */
	@RequestMapping("/findOne.do")
	public SpecificationVo findOne(Long id){
		return specificationService.findOne(id);
	}
	
	/**
	 * 
	 * @Title: update
	 * @Description: 更新规格
	 * @param specificationVo
	 * @return
	 * @return Result
	 * @throws
	 */
	@RequestMapping("/update.do")
	public Result update(@RequestBody SpecificationVo specificationVo){
		try {
			specificationService.update(specificationVo);
			return new Result(true, "更新成功");
		} catch (Exception e) {
			e.printStackTrace();
			return new Result(false, "更新失败");
		}
	}
	
	/**
	 * 
	 * @Title: selectOptionList
	 * @Description: 新增模板时初始化规格列表
	 * @return
	 * @return List<Map<String,String>>
	 * @throws
	 */
	@RequestMapping("/selectOptionList.do")
	public List<Map<String, String>> selectOptionList(){
		return specificationService.selectOptionList();
	}
}

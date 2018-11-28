package cn.itcast.core.controller.brand;

import java.util.List;
import java.util.Map;

import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.dubbo.config.annotation.Reference;

import cn.itcast.core.entity.PageResult;
import cn.itcast.core.entity.Result;
import cn.itcast.core.pojo.good.Brand;
import cn.itcast.core.service.brand.BrandService;

/**
 * 
 * @ClassName: BrandController
 * @Company: http://www.itcast.cn/
 * @Description: 品牌管理模块
 * @author 阮文 
 * @date 2018年9月16日 下午12:19:48
 */
@RestController
@RequestMapping("/brand")
public class BrandController {
	
	@Reference
	private BrandService brandService;

	/**
	 * 
	 * @Title: findAll
	 * @Description: 查询所有品牌
	 * @return
	 * @return List<Brand>
	 * @throws
	 */
	@RequestMapping("/findAll.do")
	public List<Brand> findAll(){
		return brandService.findAll();
	}
	
	/**
	 * 
	 * @Title: findPage
	 * @Description: 分页查询
	 * @param pageNum
	 * @param pageSize
	 * @return
	 * @return PageResult
	 * @throws
	 */
	@RequestMapping("/findPage.do")
	public PageResult findPage(Integer pageNum, Integer pageSize){
		return brandService.findPage(pageNum, pageSize);
	}
	
	/**
	 * 
	 * @Title: search
	 * @Description: 根据条件查询
	 * @param pageNum
	 * @param pageSize
	 * @param brand
	 * @return
	 * @return PageResult
	 * @throws
	 */
	@RequestMapping("/search.do")
	public PageResult search(Integer pageNum, Integer pageSize,@RequestBody Brand brand){
		return brandService.search(pageNum, pageSize, brand);
	}
	
	/**
	 * 
	 * @Title: add
	 * @Description: 添加品牌
	 * @param brand
	 * @return
	 * @return Result
	 * @throws
	 */
	@RequestMapping("/add.do")
	public Result add(@RequestBody Brand brand){
		try {
			brandService.add(brand);
			return new Result(true, "保存成功");
		} catch (Exception e) {
			e.printStackTrace();
			return new Result(false, "保存失败");
		}
	}
	
	/**
	 * 
	 * @Title: findOne
	 * @Description: 回显品牌
	 * @param id
	 * @return
	 * @return Brand
	 * @throws
	 */
	@RequestMapping("/findOne.do")
	public Brand findOne(Long id){
		return brandService.findOne(id);
	}
	
	/**
	 * 
	 * @Title: update
	 * @Description: 更新品牌
	 * @param brand
	 * @return
	 * @return Result
	 * @throws
	 */
	@RequestMapping("/update.do")
	public Result update(@RequestBody Brand brand){
		try {
			brandService.update(brand);
			return new Result(true, "更新成功");
		} catch (Exception e) {
			e.printStackTrace();
			return new Result(false, "更新失败");
		}
	}
	
	/**
	 * 
	 * @Title: delete
	 * @Description: 批量删除
	 * @param ids
	 * @return
	 * @return Result
	 * @throws
	 */
	@RequestMapping("/delete.do")
	public Result delete(Long[] ids){
		try {
			brandService.delete(ids);
			return new Result(true, "删除成功");
		} catch (Exception e) {
			e.printStackTrace();
			return new Result(false, "删除失败");
		}
	}
	
	/**
	 * 
	 * @Title: selectOptionList
	 * @Description: 新增模板的时候初始化品牌列表
	 * @return
	 * @return List<Map<String,String>>
	 * @throws
	 */
	@RequestMapping("/selectOptionList.do")
	public List<Map<String, String>> selectOptionList(){
		return brandService.selectOptionList();
	}
	
}

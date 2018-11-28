package cn.itcast.core.controller.template;

import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RestController;

import com.alibaba.dubbo.config.annotation.Reference;

import cn.itcast.core.entity.PageResult;
import cn.itcast.core.entity.Result;
import cn.itcast.core.pojo.template.TypeTemplate;
import cn.itcast.core.service.template.TypeTemplateService;

@RestController
@RequestMapping("/typeTemplate")
public class TypeTemplateController {
	
	@Reference
	private TypeTemplateService typeTemplateService;

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
	@RequestMapping("/search.do")
	public PageResult search(Integer page, Integer rows,@RequestBody TypeTemplate template){
		return typeTemplateService.search(page, rows, template);
	}
	
	/**
	 * 
	 * @Title: add
	 * @Description: 新增模板
	 * @param template
	 * @return
	 * @return Result
	 * @throws
	 */
	@RequestMapping("/add.do")
	public Result add(@RequestBody TypeTemplate template){
		try {
			typeTemplateService.add(template);
			return new Result(true, "保存成功");
		} catch (Exception e) {
			e.printStackTrace();
			return new Result(false, "保存失败");
		}
	}
}

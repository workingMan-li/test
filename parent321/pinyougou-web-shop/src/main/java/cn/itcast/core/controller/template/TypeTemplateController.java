package cn.itcast.core.controller.template;

import java.util.List;
import java.util.Map;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.dubbo.config.annotation.Reference;

import cn.itcast.core.pojo.template.TypeTemplate;
import cn.itcast.core.service.template.TypeTemplateService;

@RestController
@RequestMapping("/typeTemplate")
public class TypeTemplateController {
	
	@Reference
	private TypeTemplateService typeTemplateService;

	/**
	 * 
	 * @Title: findOne
	 * @Description: 确定模板后：加载模板中的所有的数据（规格、品牌、扩展属性）
	 * @param id
	 * @return
	 * @return TypeTemplate
	 * @throws
	 */
	@RequestMapping("/findOne.do")
	public TypeTemplate findOne(Long id){
		return typeTemplateService.findOne(id);
	}
	
	/**
	 * 
	 * @Title: findBySpecList
	 * @Description: 确定模板后：加载模板中的规格以及规格选项
	 * @param id
	 * @return
	 * @return List<Map>
	 * @throws
	 */
	@RequestMapping("/findBySpecList.do")
	public List<Map> findBySpecList(Long id){
		return typeTemplateService.findBySpecList(id);
	}
}

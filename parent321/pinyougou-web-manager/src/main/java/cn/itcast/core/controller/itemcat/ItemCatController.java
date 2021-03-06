package cn.itcast.core.controller.itemcat;

import java.util.List;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.dubbo.config.annotation.Reference;

import cn.itcast.core.pojo.item.ItemCat;
import cn.itcast.core.service.itemcat.ItemCatSerivce;

@RestController
@RequestMapping("/itemCat")
public class ItemCatController {
	
	@Reference
	private ItemCatSerivce itemCatSerivce;

	/**
	 * 
	 * @Title: findByParentId
	 * @Description: 商品分类的列表查询
	 * @param parentId
	 * @return
	 * @return List<ItemCat>
	 * @throws
	 */
	@RequestMapping("/findByParentId.do")
	public List<ItemCat> findByParentId(Long parentId){
		return itemCatSerivce.findByParentId(parentId);
	}
	
	/**
	 * 
	 * @Title: findAll
	 * @Description: 显示具体的分类的名称:运营商系统
	 * @return
	 * @return List<ItemCat>
	 * @throws
	 */
	@RequestMapping("/findAll.do")
	public List<ItemCat> findAll(){
		return itemCatSerivce.findAll();
	}
}

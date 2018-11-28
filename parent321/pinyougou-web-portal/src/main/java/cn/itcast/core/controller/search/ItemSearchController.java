package cn.itcast.core.controller.search;

import java.util.Map;

import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.dubbo.config.annotation.Reference;

import cn.itcast.core.service.search.ItemSearchService;

@RestController
@RequestMapping("/itemsearch")
public class ItemSearchController {
	
	@Reference
	private ItemSearchService itemSearchService;

	/**
	 * 
	 * @Title: search
	 * @Description: 前台系统的检索
	 * @param searchMap
	 * @return
	 * @return Map<String,Object>
	 * @throws
	 */
	@RequestMapping("/search.do")
	public Map<String, Object> search(@RequestBody Map<String, String> searchMap){
		return itemSearchService.search(searchMap);
	}
}

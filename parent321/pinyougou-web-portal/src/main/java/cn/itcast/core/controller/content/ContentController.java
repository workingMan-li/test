package cn.itcast.core.controller.content;

import java.util.List;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.dubbo.config.annotation.Reference;

import cn.itcast.core.pojo.ad.Content;
import cn.itcast.core.service.content.ContentService;

@RestController
@RequestMapping("/content")
public class ContentController {
	
	@Reference
	private ContentService contentService;

	/**
	 * 
	 * @Title: findByCategoryId
	 * @Description: 首页大广告的轮播
	 * @param categoryId
	 * @return
	 * @return List<Content>
	 * @throws
	 */
	@RequestMapping("/findByCategoryId.do")
	public List<Content> findByCategoryId(Long categoryId){
		return contentService.findByCategoryId(categoryId);
	}
}

package cn.itcast.core.controller.goods;

import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.dubbo.config.annotation.Reference;

import cn.itcast.core.entity.PageResult;
import cn.itcast.core.entity.Result;
import cn.itcast.core.pojo.good.Goods;
import cn.itcast.core.service.goods.GoodsService;

@RestController
@RequestMapping("/goods")
public class GoodsController {
	
	@Reference
	private GoodsService goodsService;
	
	/**
	 * 
	 * @Title: search
	 * @Description: 查询当前商家下的商品列表信息
	 * @param page
	 * @param rows
	 * @param goods
	 * @return
	 * @return PageResult
	 * @throws
	 */
	@RequestMapping("/search.do")
	public PageResult search(Integer page, Integer rows,@RequestBody Goods goods){
		return goodsService.searchForManager(page, rows, goods);
	}
	
	/**
	 * 
	 * @Title: updateStatus
	 * @Description: 商品审核
	 * @param ids
	 * @param status
	 * @return
	 * @return Result
	 * @throws
	 */
	@RequestMapping("/updateStatus.do")
	public Result updateStatus(Long[] ids, String status){
		try {
			goodsService.updateStatus(ids, status);
			return new Result(true, "操作成功");
		} catch (Exception e) {
			e.printStackTrace();
			return new Result(false, "操作失败");
		}
	}
	
	/**
	 * 
	 * @Title: delete
	 * @Description: 删除商品
	 * @param ids
	 * @return
	 * @return Result
	 * @throws
	 */
	@RequestMapping("/delete.do")
	public Result delete(Long[] ids) {
		try {
			goodsService.delete(ids);
			return new Result(true, "删除成功");
		} catch (Exception e) {
			e.printStackTrace();
			return new Result(false, "删除失败");
		}
	}
	
}

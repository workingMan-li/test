package cn.itcast.core.controller.goods;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.dubbo.config.annotation.Reference;

import cn.itcast.core.entity.PageResult;
import cn.itcast.core.entity.Result;
import cn.itcast.core.pojo.good.Goods;
import cn.itcast.core.service.goods.GoodsService;
import cn.itcast.core.vo.GoodsVo;

@RestController
@RequestMapping("/goods")
public class GoodsController {
	
	@Reference
	private GoodsService goodsService;

	/**
	 * 
	 * @Title: add
	 * @Description: 录入商品
	 * @param goodsVo
	 * @return
	 * @return Result
	 * @throws
	 */
	@RequestMapping("/add.do")
	public Result add(@RequestBody GoodsVo goodsVo){
		try {
			// 设置录入商品的商家
			String sellerId = SecurityContextHolder.getContext().getAuthentication().getName();
			goodsVo.getGoods().setSellerId(sellerId);
			goodsService.add(goodsVo);
			return new Result(true, "保存成功");
		} catch (Exception e) {
			e.printStackTrace();
			return new Result(false, "保存失败");
		}
	}
	
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
		// 设置当前登录人
		String sellerId = SecurityContextHolder.getContext().getAuthentication().getName();
		goods.setSellerId(sellerId);
		return goodsService.search(page, rows, goods);
	}
	
	/**
	 * 
	 * @Title: findOne
	 * @Description: 编辑商品
	 * @param id
	 * @return
	 * @return GoodsVo
	 * @throws
	 */
	@RequestMapping("/findOne.do")
	public GoodsVo findOne(Long id) {
		return goodsService.findOne(id);
	}
	
	/**
	 * 
	 * @Title: update
	 * @Description:商品更新 
	 * @param goodsVo
	 * @return
	 * @return Result
	 * @throws
	 */
	@RequestMapping("/update.do")
	public Result update(@RequestBody GoodsVo goodsVo){
		try {
			// 设置录入商品的商家
			String sellerId = SecurityContextHolder.getContext().getAuthentication().getName();
			goodsVo.getGoods().setSellerId(sellerId);
			goodsService.update(goodsVo);
			return new Result(true, "更新成功");
		} catch (Exception e) {
			e.printStackTrace();
			return new Result(false, "更新失败");
		}
	}
}

package cn.itcast.core.controller.seller;

import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.dubbo.config.annotation.Reference;

import cn.itcast.core.entity.Result;
import cn.itcast.core.pojo.seller.Seller;
import cn.itcast.core.service.seller.SellerService;

@RestController
@RequestMapping("/seller")
public class SellerController {
	
	@Reference
	private SellerService sellerService;

	/**
	 * 
	 * @Title: add
	 * @Description: 商家入驻
	 * @param seller
	 * @return
	 * @return Result
	 * @throws
	 */
	@RequestMapping("/add.do")
	public Result add(@RequestBody Seller seller){
		try {
			sellerService.add(seller);
			return new Result(true, "申请成功");
		} catch (Exception e) {
			e.printStackTrace();
			return new Result(false, "申请失败");
		}
	}
}

package cn.itcast.core.controller.seller;

import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.dubbo.config.annotation.Reference;

import cn.itcast.core.entity.PageResult;
import cn.itcast.core.entity.Result;
import cn.itcast.core.pojo.seller.Seller;
import cn.itcast.core.service.seller.SellerService;

/**
 * 
 * @ClassName: SellerController
 * @Company: http://www.itcast.cn/
 * @Description: 运营商对商家的操作
 * @author 阮文 
 * @date 2018年9月20日 下午12:21:24
 */
@RestController
@RequestMapping("/seller")
public class SellerController {
	
	@Reference
	private SellerService sellerService;
	
	/**
	 * 
	 * @Title: search
	 * @Description: 待审核商家的列表查询
	 * @param page
	 * @param rows
	 * @param seller
	 * @return
	 * @return PageResult
	 * @throws
	 */
	@RequestMapping("/search.do")
	public PageResult search(Integer page, Integer rows, @RequestBody Seller seller){
		return sellerService.search(page, rows, seller);
	}
	
	/**
	 * 
	 * @Title: findOne
	 * @Description: 商家详情
	 * @param sellerId
	 * @return
	 * @return Seller
	 * @throws
	 */
	@RequestMapping("/findOne.do")
	public Seller findOne(String id){
		return sellerService.findOne(id);
	}

	/**
	 * 
	 * @Title: updateStatus
	 * @Description: 商家审核
	 * @param sellerId
	 * @param status
	 * @return
	 * @return Result
	 * @throws
	 */
	@RequestMapping("/updateStatus.do")
	public Result updateStatus(String sellerId, String status){
		try {
			sellerService.updateStatus(sellerId, status);
			return new Result(true, "审核成功");
		} catch (Exception e) {
			e.printStackTrace();
			return new Result(false, "审核失败");
		}
	}
}

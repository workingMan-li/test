package cn.itcast.core.service.seller;

import cn.itcast.core.entity.PageResult;
import cn.itcast.core.pojo.seller.Seller;

public interface SellerService {
	
	/**
	 * 
	 * @Title: add
	 * @Description: 商家入驻
	 * @param seller
	 * @return void
	 * @throws
	 */
	public void add(Seller seller);
	
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
	public PageResult search(Integer page, Integer rows, Seller seller);
	
	/**
	 * 
	 * @Title: findOne
	 * @Description: 商家的详情
	 * @param sellerId
	 * @return
	 * @return Seller
	 * @throws
	 */
	public Seller findOne(String sellerId);
	
	/**
	 * 
	 * @Title: updateStatus
	 * @Description: 审核商家
	 * @param sellerId
	 * @param status
	 * @return void
	 * @throws
	 */
	public void updateStatus(String sellerId, String status);
}

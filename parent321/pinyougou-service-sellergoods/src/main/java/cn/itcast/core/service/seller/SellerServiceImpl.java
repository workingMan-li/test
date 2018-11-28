package cn.itcast.core.service.seller;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;

import cn.itcast.core.dao.seller.SellerDao;
import cn.itcast.core.entity.PageResult;
import cn.itcast.core.pojo.seller.Seller;
import cn.itcast.core.pojo.seller.SellerQuery;

@Service
public class SellerServiceImpl implements SellerService {
	
	@Autowired
	private SellerDao sellerDao;

	/*
	 * (non-Javadoc)
	 * <p>Title: add</p> 
	 * <p>Description: 商家入驻</p> 
	 * @param seller 
	 * @see cn.itcast.core.service.seller.SellerService#add(cn.itcast.core.pojo.seller.Seller)
	 */
	@Transactional
	@Override
	public void add(Seller seller) {
		seller.setStatus("0"); 			  // 待审核
		seller.setCreateTime(new Date()); // 提交申请的日期
		// 密码加密
		BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
		String password = bCryptPasswordEncoder.encode(seller.getPassword());
		seller.setPassword(password);
		sellerDao.insertSelective(seller);
	}

	/*
	 * (non-Javadoc)
	 * <p>Title: search</p> 
	 * <p>Description: 待审核商家的列表查询</p> 
	 * @param page
	 * @param rows
	 * @param seller
	 * @return 
	 * @see cn.itcast.core.service.seller.SellerService#search(java.lang.Integer, java.lang.Integer, cn.itcast.core.pojo.seller.Seller)
	 */
	@Override
	public PageResult search(Integer page, Integer rows, Seller seller) {
		// 分页条件
		PageHelper.startPage(page, rows);
		// 查询条件
		SellerQuery sellerQuery = new SellerQuery();
		if(seller.getStatus() != null && !"".equals(seller.getStatus().trim())){
			sellerQuery.createCriteria().andStatusEqualTo(seller.getStatus().trim());
		}
		// 店铺名称、商家名称条件的设置
		Page<Seller> p = (Page<Seller>) sellerDao.selectByExample(sellerQuery);
		return new PageResult(p.getTotal(), p.getResult());
	}

	/*
	 * (non-Javadoc)
	 * <p>Title: findOne</p> 
	 * <p>Description: 商家的详情</p> 
	 * @param sellerId
	 * @return 
	 * @see cn.itcast.core.service.seller.SellerService#findOne(java.lang.String)
	 */
	@Override
	public Seller findOne(String sellerId) {
		return sellerDao.selectByPrimaryKey(sellerId);
	}

	/*
	 * (non-Javadoc)
	 * <p>Title: updateStatus</p> 
	 * <p>Description: 审核商家</p> 
	 * @param sellerId
	 * @param status 
	 * @see cn.itcast.core.service.seller.SellerService#updateStatus(java.lang.String, java.lang.String)
	 */
	@Transactional
	@Override
	public void updateStatus(String sellerId, String status) {
		Seller seller = new Seller();
		seller.setSellerId(sellerId);
		seller.setStatus(status);
		sellerDao.updateByPrimaryKeySelective(seller);
	}
	
	

}

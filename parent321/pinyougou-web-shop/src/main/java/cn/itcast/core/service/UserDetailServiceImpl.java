package cn.itcast.core.service;

import java.util.HashSet;
import java.util.Set;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import cn.itcast.core.pojo.seller.Seller;
import cn.itcast.core.service.seller.SellerService;

/**
 * 
 * @ClassName: UserDetailServiceImpl
 * @Company: http://www.itcast.cn/
 * @Description: 自定义认证类
 * @author 阮文 
 * @date 2018年9月22日 上午9:15:15
 */
public class UserDetailServiceImpl implements UserDetailsService {

	// 属性注入sellerService
	private SellerService sellerService;
	public void setSellerService(SellerService sellerService) {
		this.sellerService = sellerService;
	}

	/*
	 * (non-Javadoc)
	 * <p>Title: loadUserByUsername</p> 
	 * <p>Description: 认证并且授权用户</p> 
	 * @param username
	 * @return
	 * @throws UsernameNotFoundException 
	 * @see org.springframework.security.core.userdetails.UserDetailsService#loadUserByUsername(java.lang.String)
	 */
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		// 返回用户
		Seller seller = sellerService.findOne(username);
		if(seller != null && "1".equals(seller.getStatus())){ // 必须是审核成功后商家
			Set<GrantedAuthority> authorities = new HashSet<>();
			SimpleGrantedAuthority grantedAuthority = new SimpleGrantedAuthority("ROLE_SELLER");
			authorities.add(grantedAuthority);
			User user = new User(username, seller.getPassword(), authorities);
			return user;
		}
		return null;
	}

}

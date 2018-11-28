package cn.itcast.core.service;

import java.util.HashSet;
import java.util.Set;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

/**
 * 
 * @ClassName: UserDetailServiceImpl
 * @Company: http://www.itcast.cn/
 * @Description: 自定义的认证类
 * @author 阮文 
 * @date 2018年10月15日 上午9:04:27
 */
public class UserDetailServiceImpl implements UserDetailsService {

	/*
	 * (non-Javadoc)
	 * <p>Title: loadUserByUsername</p> 
	 * <p>Description: 对账号进行授权</p> 
	 * @param username
	 * @return
	 * @throws UsernameNotFoundException 
	 * @see org.springframework.security.core.userdetails.UserDetailsService#loadUserByUsername(java.lang.String)
	 */
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		Set<GrantedAuthority> authorities = new HashSet<>();
		SimpleGrantedAuthority grantedAuthority = new SimpleGrantedAuthority("ROLE_USER");
		authorities.add(grantedAuthority);
		// 授权用户：无需设置密码，因为认证成功后才进行授权的。
		User user = new User(username, "", authorities);
		return user;
	}

}

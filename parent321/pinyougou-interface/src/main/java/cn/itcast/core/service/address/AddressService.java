package cn.itcast.core.service.address;

import java.util.List;

import cn.itcast.core.pojo.address.Address;

public interface AddressService {

	/**
	 * 
	 * @Title: findListByLoginUser
	 * @Description: 获取当前登录人的地址列表信息
	 * @param username
	 * @return
	 * @return List<Address>
	 * @throws
	 */
	public List<Address> findListByLoginUser(String username);
}

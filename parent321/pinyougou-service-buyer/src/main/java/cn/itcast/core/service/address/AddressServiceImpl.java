package cn.itcast.core.service.address;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.alibaba.dubbo.config.annotation.Service;

import cn.itcast.core.dao.address.AddressDao;
import cn.itcast.core.pojo.address.Address;
import cn.itcast.core.pojo.address.AddressQuery;

@Service
public class AddressServiceImpl implements AddressService {

	@Autowired
	private AddressDao addressDao;
	
	/*
	 * (non-Javadoc)
	 * <p>Title: findListByLoginUser</p> 
	 * <p>Description: 获取当前登录人的地址列表信息</p> 
	 * @param username
	 * @return 
	 * @see cn.itcast.core.service.address.AddressService#findListByLoginUser(java.lang.String)
	 */
	@Override
	public List<Address> findListByLoginUser(String username) {
		// 获取当前登录人的地址列表信息
		AddressQuery addressQuery = new AddressQuery();
		addressQuery.createCriteria().andUserIdEqualTo(username);
		List<Address> list = addressDao.selectByExample(addressQuery);
		return list;
	}

}

package cn.itcast.core.controller.address;

import java.util.List;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.dubbo.config.annotation.Reference;

import cn.itcast.core.pojo.address.Address;
import cn.itcast.core.service.address.AddressService;

@RestController
@RequestMapping("/address")
public class AddressController {
	
	@Reference
	private AddressService addressService;

	/**
	 * 
	 * @Title: findListByLoginUser
	 * @Description: 获取当前登录人的地址列表信息
	 * @return
	 * @return List<Address>
	 * @throws
	 */
	@RequestMapping("/findListByLoginUser.do")
	public List<Address> findListByLoginUser(){
		String username = SecurityContextHolder.getContext().getAuthentication().getName();
		return addressService.findListByLoginUser(username);
	}
}

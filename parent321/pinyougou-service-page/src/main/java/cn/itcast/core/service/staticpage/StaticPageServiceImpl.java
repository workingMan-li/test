package cn.itcast.core.service.staticpage;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletContext;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.context.ServletContextAware;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer;

import cn.itcast.core.dao.good.GoodsDao;
import cn.itcast.core.dao.good.GoodsDescDao;
import cn.itcast.core.dao.item.ItemCatDao;
import cn.itcast.core.dao.item.ItemDao;
import cn.itcast.core.pojo.good.Goods;
import cn.itcast.core.pojo.good.GoodsDesc;
import cn.itcast.core.pojo.item.Item;
import cn.itcast.core.pojo.item.ItemCat;
import cn.itcast.core.pojo.item.ItemQuery;
import freemarker.template.Configuration;
import freemarker.template.Template;

public class StaticPageServiceImpl implements StaticPageService, ServletContextAware {
	
	@Autowired
	private GoodsDao goodsDao;
	
	@Autowired
	private GoodsDescDao goodsDescDao;
	
	@Autowired
	private ItemCatDao itemCatDao;
	
	@Autowired
	private ItemDao itemDao;
	
	private Configuration configuration;
	
	// 注入：FreeMarkerConfigurer好处：
	// 1、获取configuration实例
	// 2、指定模板的相对的路径、指定编码的格式
	public void setFreeMarkerConfigurer(FreeMarkerConfigurer freeMarkerConfigurer) {
		this.configuration = freeMarkerConfigurer.getConfiguration();
	}
	
	// 注入ServletContext，可以获取项目发布的真实的路径了吧
	private ServletContext servletContext;
	@Override
	public void setServletContext(ServletContext servletContext) {
		this.servletContext = servletContext;
	}

	/*
	 * (non-Javadoc)
	 * <p>Title: getHtml</p> 
	 * <p>Description: 生成商品详情的静态页面</p> 
	 * @param id 
	 * @see cn.itcast.core.service.staticpage.StaticPageService#getHtml(java.lang.String)
	 */
	@Override
	public void getHtml(String id) {
		try {
			// 1、创建Configuration并且指定模板的位置
			// 不能new Configuration，spring管理：注入Configuration，希望如何指定模板的位置：配置文件
			// 2、获取该位置下我们需要的模板
			Template template = configuration.getTemplate("item.ftl");
			// 3、准备数据：业务处理后的数据
			Map<String, Object> dataModel = getModel(Long.parseLong(id));
			// 4、模板  + 数据 = 输出（生成静态页面）
			// 静态页生成到项目的发布路径下xxx/id.html
			String pathname = "/" + id + ".html";
			// request.getServletContext.getRealPath(pathname)
			String path = servletContext.getRealPath(pathname);
			File file = new File(path);
			Writer out = new OutputStreamWriter(new FileOutputStream(file), "UTF-8");
			template.process(dataModel, out);
		} catch (Exception e) {
			e.printStackTrace();
		} 
	}
	
	// 静态页需要的数据
	private Map<String, Object> getModel(Long id) {
		Map<String, Object> dataModel = new HashMap<>();
		// 商品基本数据
		Goods goods = goodsDao.selectByPrimaryKey(id);
		dataModel.put("goods", goods);
		// 商品详情数据
		GoodsDesc goodsDesc = goodsDescDao.selectByPrimaryKey(id);
		dataModel.put("goodsDesc", goodsDesc);
		// 商品分类数据：一级、二级、三级
		ItemCat itemCat1 = itemCatDao.selectByPrimaryKey(goods.getCategory1Id());
		ItemCat itemCat2 = itemCatDao.selectByPrimaryKey(goods.getCategory2Id());
		ItemCat itemCat3 = itemCatDao.selectByPrimaryKey(goods.getCategory3Id());
		dataModel.put("itemCat1", itemCat1);
		dataModel.put("itemCat2", itemCat2);
		dataModel.put("itemCat3", itemCat3);
		// 商品对应的库存数据
		ItemQuery itemQuery = new ItemQuery();
		itemQuery.createCriteria().andGoodsIdEqualTo(id).andNumGreaterThan(0);
		List<Item> itemList = itemDao.selectByExample(itemQuery);
		dataModel.put("itemList", itemList);
		return dataModel;
	}

	

}

package cn.itcast.core.service.goods;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.data.solr.core.query.SimpleQuery;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;

import cn.itcast.core.dao.good.BrandDao;
import cn.itcast.core.dao.good.GoodsDao;
import cn.itcast.core.dao.good.GoodsDescDao;
import cn.itcast.core.dao.item.ItemCatDao;
import cn.itcast.core.dao.item.ItemDao;
import cn.itcast.core.dao.seller.SellerDao;
import cn.itcast.core.entity.PageResult;
import cn.itcast.core.pojo.good.Goods;
import cn.itcast.core.pojo.good.GoodsDesc;
import cn.itcast.core.pojo.good.GoodsQuery;
import cn.itcast.core.pojo.good.GoodsQuery.Criteria;
import cn.itcast.core.pojo.item.Item;
import cn.itcast.core.pojo.item.ItemQuery;
import cn.itcast.core.service.staticpage.StaticPageService;
import cn.itcast.core.vo.GoodsVo;

@Service
public class GoodsServiceImpl implements GoodsService {
	
	@Autowired
	private GoodsDao goodsDao;
	
	@Autowired
	private GoodsDescDao goodsDescDao;
	
	@Autowired
	private ItemDao itemDao;
	
	@Autowired
	private ItemCatDao itemCatDao;
	
	@Autowired
	private BrandDao brandDao;
	
	@Autowired
	private SellerDao sellerDao;
	
	@Autowired
	private SolrTemplate solrTemplate;
	
//	@Autowired
//	private StaticPageService staticPageService;
	
	@Autowired
	private JmsTemplate jmsTemplate;
	
	@Autowired
	private Destination topicPageAndSolrDestination;
	
	@Autowired
	private Destination queueSolrDeleteDestination;

	/*
	 * (non-Javadoc)
	 * <p>Title: add</p> 
	 * <p>Description: 新增商品</p> 
	 * @param goodsVo 
	 * @see cn.itcast.core.service.goods.GoodsService#add(cn.itcast.core.vo.GoodsVo)
	 */
	@Transactional
	@Override
	public void add(GoodsVo goodsVo) {
		// 1、保存商品基本信息
		Goods goods = goodsVo.getGoods();
		// 录入的商品默认：待审核的状态
		goods.setAuditStatus("0"); // audit_status：审核状态
		goodsDao.insertSelective(goods); // 返回自增主键的id
		
		// 2、保存商品描述信息
		GoodsDesc goodsDesc = goodsVo.getGoodsDesc();
		goodsDesc.setGoodsId(goods.getId()); // 外键
		goodsDescDao.insertSelective(goodsDesc);
		
		// 3、保存商品对应的库存信息
		// 判断商品是否启用规格
		if("1".equals(goods.getIsEnableSpec())){ // 例如：手机
			List<Item> itemList = goodsVo.getItemList();
			for (Item item : itemList) {
				// 商品的标题： 栗子：小米8 全面屏游戏智能手机 6GB+64GB 黑色 全网通4G 双卡双待
				// title = spu名称+spu副标题+规格名称
				String title = goods.getGoodsName() + " " + goods.getCaption();
				// 栗子：{"机身内存":"16G","网络":"联通2G"}
				String spec = item.getSpec();
				Map<String, String> map = JSON.parseObject(spec, Map.class);
				Set<Entry<String, String>> entrySet = map.entrySet();
				for (Entry<String, String> entry : entrySet) {
					title += " " + entry.getValue();
				}
				item.setTitle(title);	// 商品标题
				setAttributeForItem(goods, goodsDesc, item);
				// 保存库存
				itemDao.insertSelective(item);
			}
		}else{ // 例如：书籍
			Item item = new Item();
			item.setTitle(goods.getGoodsName());	// 标题
			item.setPrice(goods.getPrice()); 		// 商品的价格
			item.setIsDefault("1");					// 是否默认
			item.setNum(9999); 						// 库存量
			item.setSpec("{}"); 					// 无商品规格
			setAttributeForItem(goods, goodsDesc, item);
			// 保存库存
			itemDao.insertSelective(item);
		}

	}

	/**
	 * 
	 * @Title: setAttributeForItem
	 * @Description: 设置库存的属性
	 * @param goods
	 * @param goodsDesc
	 * @param item
	 * @return void
	 * @throws
	 */
	private void setAttributeForItem(Goods goods, GoodsDesc goodsDesc, Item item) {
		// 栗子：
		// [{"color":"灰色","url":"http://192.168.200.128/group1/M00/00/01/wKjIgFrUSH2ABS1yAAh0T6sNTaI610.jpg"},
		// {"color":"灰色","url":"http://192.168.200.128/group1/M00/00/01/wKjIgFrUSJGALjVYAAPzuN901mo975.jpg"},]
		String itemImages = goodsDesc.getItemImages();
		List<Map> list = JSON.parseArray(itemImages, Map.class);
		if(list != null && list.size() > 0){
			String image = list.get(0).get("url").toString();
			item.setImage(image);   // 商品图片
		}
		item.setCategoryid(goods.getCategory3Id()); // 商品三级分类id
		item.setStatus("1"); // 商品状态：1：正常   2：下架  3、删除
		item.setCreateTime(new Date()); // 创建日期
		item.setUpdateTime(new Date()); // 更新日期
		item.setGoodsId(goods.getId()); // 商品id
		item.setSellerId(goods.getSellerId()); // 商家id
		item.setCategory(itemCatDao.selectByPrimaryKey(goods.getCategory3Id()).getName()); // 分类名称
		item.setBrand(brandDao.selectByPrimaryKey(goods.getBrandId()).getName()); // 品牌名称
		item.setSeller(sellerDao.selectByPrimaryKey(goods.getSellerId()).getNickName()); // 店铺名称
	}

	/*
	 * (non-Javadoc)
	 * <p>Title: search</p> 
	 * <p>Description: 查询当前商家下的商品列表信息</p> 
	 * @param page
	 * @param rows
	 * @param goods
	 * @return 
	 * @see cn.itcast.core.service.goods.GoodsService#search(java.lang.Integer, java.lang.Integer, cn.itcast.core.pojo.good.Goods)
	 */
	@Override
	public PageResult search(Integer page, Integer rows, Goods goods) {
		// 1、设置分页条件
		PageHelper.startPage(page, rows);
		// 2、设置查询条件
		GoodsQuery goodsQuery = new GoodsQuery();
		if(goods.getSellerId() != null && !"".equals(goods.getSellerId().trim())){
			goodsQuery.createCriteria().andSellerIdEqualTo(goods.getSellerId().trim());
		}
		// 其他条件：自己完成
		goodsQuery.setOrderByClause("id desc");
		// 3、查询结果并封装到PageResult中
		Page<Goods> p = (Page<Goods>) goodsDao.selectByExample(goodsQuery);
		return new PageResult(p.getTotal(), p.getResult());
	}

	/*
	 * (non-Javadoc)
	 * <p>Title: findOne</p> 
	 * <p>Description: 编辑商品</p> 
	 * @param id
	 * @return 
	 * @see cn.itcast.core.service.goods.GoodsService#findOne(java.lang.Long)
	 */
	@Override
	public GoodsVo findOne(Long id) {
		// 将相关的数据封装到GoodsVo中
		GoodsVo goodsVo = new GoodsVo();
		// 1、封装商品
		Goods goods = goodsDao.selectByPrimaryKey(id);
		goodsVo.setGoods(goods);
		// 2、商品明细
		GoodsDesc goodsDesc = goodsDescDao.selectByPrimaryKey(id);
		goodsVo.setGoodsDesc(goodsDesc);
		// 3、库存列表
		ItemQuery itemQuery = new ItemQuery();
		itemQuery.createCriteria().andGoodsIdEqualTo(id);
		List<Item> itemList = itemDao.selectByExample(itemQuery);
		goodsVo.setItemList(itemList);
		return goodsVo;
	}

	/*
	 * (non-Javadoc)
	 * <p>Title: update</p> 
	 * <p>Description: 更新商品</p> 
	 * @param goodsVo 
	 * @see cn.itcast.core.service.goods.GoodsService#update(cn.itcast.core.vo.GoodsVo)
	 */
	@Transactional
	@Override
	public void update(GoodsVo goodsVo) {
		// 1、更新商品
		Goods goods = goodsVo.getGoods();
		// 更新后的商品需要重新提交审核
		goods.setAuditStatus("0"); // 待审核的状态
		goodsDao.updateByPrimaryKeySelective(goods);
		// 2、更新商品明细
		GoodsDesc goodsDesc = goodsVo.getGoodsDesc();
		goodsDescDao.updateByPrimaryKeySelective(goodsDesc);
		// 3、更新商品对应的库存
		// 先删除
		ItemQuery itemQuery = new ItemQuery();
		itemQuery.createCriteria().andGoodsIdEqualTo(goods.getId());
		itemDao.deleteByExample(itemQuery);
		// 在保存
		// 判断商品是否启用规格
		if("1".equals(goods.getIsEnableSpec())){ // 例如：手机
			List<Item> itemList = goodsVo.getItemList();
			for (Item item : itemList) {
				// 商品的标题： 栗子：小米8 全面屏游戏智能手机 6GB+64GB 黑色 全网通4G 双卡双待
				// title = spu名称+spu副标题+规格名称
				String title = goods.getGoodsName() + " " + goods.getCaption();
				// 栗子：{"机身内存":"16G","网络":"联通2G"}
				String spec = item.getSpec();
				Map<String, String> map = JSON.parseObject(spec, Map.class);
				Set<Entry<String, String>> entrySet = map.entrySet();
				for (Entry<String, String> entry : entrySet) {
					title += " " + entry.getValue();
				}
				item.setTitle(title);	// 商品标题
				setAttributeForItem(goods, goodsDesc, item);
				// 保存库存
				itemDao.insertSelective(item);
			}
		}else{ // 例如：书籍
			Item item = new Item();
			item.setTitle(goods.getGoodsName());	// 标题
			item.setPrice(goods.getPrice()); 		// 商品的价格
			item.setIsDefault("1");					// 是否默认
			item.setNum(9999); 						// 库存量
			item.setSpec("{}"); 					// 无商品规格
			setAttributeForItem(goods, goodsDesc, item);
			// 保存库存
			itemDao.insertSelective(item);
		}
		
	}

	/*
	 * (non-Javadoc)
	 * <p>Title: searchForManager</p> 
	 * <p>Description: 运营商系统查询待审核的商品</p> 
	 * @param page
	 * @param rows
	 * @param goods
	 * @return 
	 * @see cn.itcast.core.service.goods.GoodsService#searchForManager(java.lang.Integer, java.lang.Integer, cn.itcast.core.pojo.good.Goods)
	 */
	@Override
	public PageResult searchForManager(Integer page, Integer rows, Goods goods) {
		// 1、设置分页条件
		PageHelper.startPage(page, rows);
		// 2、设置查询条件
		GoodsQuery goodsQuery = new GoodsQuery();
		Criteria criteria = goodsQuery.createCriteria();
		if(goods.getAuditStatus() != null && !"".equals(goods.getAuditStatus().trim())){
			// 设置的待审核的商品
			criteria.andAuditStatusEqualTo(goods.getAuditStatus().trim());
		}
		// 设置未删除的商品
		criteria.andIsDeleteIsNull();
		// 其他条件：自己完成
		goodsQuery.setOrderByClause("id desc");
		// 3、查询结果并封装到PageResult中
		Page<Goods> p = (Page<Goods>) goodsDao.selectByExample(goodsQuery);
		return new PageResult(p.getTotal(), p.getResult());
	}

	/*
	 * (non-Javadoc)
	 * <p>Title: updateStatus</p> 
	 * <p>Description: 商品审核</p> 
	 * @param ids
	 * @param status 
	 * @see cn.itcast.core.service.goods.GoodsService#updateStatus(java.lang.Long[], java.lang.String)
	 */
	@Transactional
	@Override
	public void updateStatus(Long[] ids, String status) {
		// 1、审核商品
		if(ids != null && ids.length > 0){
			Goods goods = new Goods();
			goods.setAuditStatus(status);
			for (final Long id : ids) {   
				goods.setId(id);
				// 批量更新
				goodsDao.updateByPrimaryKeySelective(goods);
				// 更新成功后
				if("1".equals(status)){
					// TODO：将商品信息保存到索引库（上架）
//					dataImportToSolr();
//					saveItemToSolr(id);
					// TODO：生成商品详情的静态页面
//					staticPageService.getHtml(String.valueOf(id));
					// 将消息发送mq中即可
					jmsTemplate.send(topicPageAndSolrDestination, new MessageCreator() {
						@Override
						public Message createMessage(Session session) throws JMSException {
							// 将商品的id封装成消息体发送到mq中
							TextMessage textMessage = session.createTextMessage(String.valueOf(id));
							return textMessage;
						}
					});
				}
			}
		}
		
	}

	// 将审核成功后的商品信息保存到索引库中
	private void saveItemToSolr(Long id) {
		// 查询该商品对应的库存信息
		ItemQuery itemQuery = new ItemQuery();
		// 查询该商品下的库存信息，并且是可用的，并且是价格最低的（展示给用户看的）
		itemQuery.createCriteria().andGoodsIdEqualTo(id).
		andStatusEqualTo("1").andIsDefaultEqualTo("1");
		List<Item> items = itemDao.selectByExample(itemQuery);
		if(items != null && items.size() > 0){
			for (Item item : items) {
				// 格式： {"机身内存":"16G","网络":"联通3G"}
				String spec = item.getSpec();
				Map<String, String> specMap = JSON.parseObject(spec, Map.class);
				item.setSpecMap(specMap);
			}
			solrTemplate.saveBeans(items);
			solrTemplate.commit();
		}
	}

	private void dataImportToSolr() {
		// 查询所有的库存（启用的）
		ItemQuery itemQuery = new ItemQuery();
		itemQuery.createCriteria().andStatusEqualTo("1");
		List<Item> list = itemDao.selectByExample(itemQuery);
		if(list != null && list.size() > 0){
			for (Item item : list) {
				// 格式： {"机身内存":"16G","网络":"联通3G"}
				String spec = item.getSpec();
				Map<String, String> specMap = JSON.parseObject(spec, Map.class);
				item.setSpecMap(specMap);
			}
			// 保存到solr中
			solrTemplate.saveBeans(list);
			solrTemplate.commit();
		}
	}

	/*
	 * (non-Javadoc)
	 * <p>Title: delete</p> 
	 * <p>Description: 批量删除商品</p> 
	 * @param ids 
	 * @see cn.itcast.core.service.goods.GoodsService#delete(java.lang.Long[])
	 */
	@Transactional
	@Override
	public void delete(Long[] ids) {
		// 1、更新商品表的is_delete字段
		if(ids != null && ids.length > 0){
			Goods goods = new Goods();
			goods.setIsDelete("1"); // 已删除
			for (final Long id : ids) {
				goods.setId(id);
				goodsDao.updateByPrimaryKeySelective(goods);
				// 将消息发送到mq中
				jmsTemplate.send(queueSolrDeleteDestination, new MessageCreator() {
					@Override
					public Message createMessage(Session session) throws JMSException {
						// 将id封装成一个消息体（文本消息）
						TextMessage textMessage = session.createTextMessage(String.valueOf(id));
						return textMessage;
					}
				});
				// 2、TODO 删除索引库中的数据
//				SimpleQuery query = new SimpleQuery("item_goodsid:"+id);
//				solrTemplate.delete(query);
//				solrTemplate.commit();
				// 3、TODO 删除静态页（可选）
			}
		}
	}
	
	
	

}

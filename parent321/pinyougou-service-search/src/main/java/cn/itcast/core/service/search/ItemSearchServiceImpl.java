package cn.itcast.core.service.search;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.data.solr.core.query.Criteria;
import org.springframework.data.solr.core.query.FilterQuery;
import org.springframework.data.solr.core.query.GroupOptions;
import org.springframework.data.solr.core.query.HighlightOptions;
import org.springframework.data.solr.core.query.HighlightQuery;
import org.springframework.data.solr.core.query.SimpleFilterQuery;
import org.springframework.data.solr.core.query.SimpleHighlightQuery;
import org.springframework.data.solr.core.query.SimpleQuery;
import org.springframework.data.solr.core.query.result.GroupEntry;
import org.springframework.data.solr.core.query.result.GroupPage;
import org.springframework.data.solr.core.query.result.GroupResult;
import org.springframework.data.solr.core.query.result.HighlightEntry;
import org.springframework.data.solr.core.query.result.HighlightEntry.Highlight;
import org.springframework.data.solr.core.query.result.HighlightPage;
import org.springframework.data.solr.core.query.result.ScoredPage;

import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;

import cn.itcast.core.dao.item.ItemDao;
import cn.itcast.core.pojo.item.Item;
import cn.itcast.core.pojo.item.ItemQuery;

@Service
public class ItemSearchServiceImpl implements ItemSearchService {
	
	@Autowired
	private SolrTemplate solrTemplate;
	
	@Autowired
	private RedisTemplate<String, Object> redisTemplate;
	
	@Autowired
	private ItemDao itemDao;

	/*
	 * (non-Javadoc)
	 * <p>Title: search</p> 
	 * <p>Description: 前台系统的检索</p> 
	 * @param searchMap
	 * @return 
	 * @see cn.itcast.core.service.search.ItemSearchService#search(java.util.Map)
	 */
	@Override
	public Map<String, Object> search(Map<String, String> searchMap) {
		// 创建封装结果数据的map对象
		Map<String, Object> resultMap = new HashMap<String, Object>();
		// 去除关键字中包含的空格信息
		String keywords = searchMap.get("keywords");
		if(keywords != null && !"".equals(keywords)){
			keywords = keywords.replace(" ", "");
			searchMap.put("keywords", keywords);
		}
		// 1、关键字检索并且分页
//		Map<String, Object> map = searchForPage(searchMap);
//		resultMap.putAll(map);
		// 2、关键字检索、分页、关键字高亮
		Map<String, Object> map = searchForHighLightPage(searchMap);
		resultMap.putAll(map);
		// 3、获取商品的分类的列表数据
		List<String> categoryList = searchForGroupPage(searchMap);
		if(categoryList != null && categoryList.size() > 0){
			// 默认查询第一个分类下的品牌以及规格
			Map<String, Object> brandsAndSpecsMap = searchBrandsWithSpecsByCategory(categoryList.get(0));
			resultMap.putAll(brandsAndSpecsMap);
		}
		resultMap.put("categoryList", categoryList);
		return resultMap;
	}

	// 默认查询第一个分类下的品牌以及规格
	private Map<String, Object> searchBrandsWithSpecsByCategory(String name) {
		// 根据分类名称获取模板id
		Object typeId = redisTemplate.boundHashOps("itemCat").get(name);
		// 根据模板id获取品牌结果集、规格选项结果集
		List<Map> brandList = (List<Map>) redisTemplate.boundHashOps("brandList").get(typeId);
		List<Map> specList = (List<Map>) redisTemplate.boundHashOps("specList").get(typeId);
		// 创建map封装数据
		Map<String, Object> map = new HashMap<>();
		map.put("brandList", brandList);
		map.put("specList", specList);
		return map;
	}

	// 获取商品的分类的列表数据
	private List<String> searchForGroupPage(Map<String, String> searchMap) {
		// 设置查询的条件
		String keywords = searchMap.get("keywords");
		Criteria criteria = new Criteria("item_keywords");
		if(keywords != null && !"".equals(keywords)){
			criteria.is(keywords);
		}
		SimpleQuery query = new SimpleQuery(criteria);
		// 设置根据哪个字段进行分组
		GroupOptions groupOptions = new GroupOptions();
		groupOptions.addGroupByField("item_category");	// 设置根据哪个字段进行分组
		query.setGroupOptions(groupOptions);
		// 根据条件查询
		GroupPage<Item> groupPage = solrTemplate.queryForGroupPage(query, Item.class);
		// 将数据封装到list中
		List<String> list = new ArrayList<>();
		GroupResult<Item> groupResult = groupPage.getGroupResult("item_category"); // 该字段下分组的结果
		Page<GroupEntry<Item>> groupEntries = groupResult.getGroupEntries();
		for (GroupEntry<Item> groupEntry : groupEntries) {
			list.add(groupEntry.getGroupValue());
		}
		return list;
	}

	// 关键字检索、分页、关键字高亮
	private Map<String, Object> searchForHighLightPage(Map<String, String> searchMap) {
		// 设置检索的关键字
		String keywords = searchMap.get("keywords");
		Criteria criteria = new Criteria("item_keywords");
		if(keywords != null && !"".equals(keywords)){
			criteria.is(keywords);
		}
		SimpleHighlightQuery query = new SimpleHighlightQuery(criteria);
		// 设置分页条件
		Integer pageNo = Integer.valueOf(searchMap.get("pageNo"));
		Integer pageSize = Integer.valueOf(searchMap.get("pageSize"));
		Integer offset = (pageNo - 1) * pageSize;
		query.setOffset(offset); 	// 其始行
		query.setRows(pageSize);	// 每页显示的条数
		// 设置关键字高亮
		HighlightOptions highlightOptions = new HighlightOptions();
		highlightOptions.setSimplePrefix("<font color='red'>");	// 高亮开始标签
		highlightOptions.setSimplePostfix("</font>");			// 高亮结束标签
		highlightOptions.addField("item_title");				// 添加对哪个字段进行高亮
		query.setHighlightOptions(highlightOptions); 			// 设置高亮
		
		// 根据条件进行过滤：商品分类、商品品牌、商品价格、商品规格
		String category = searchMap.get("category");
		if(category != null && !"".equals(category)){
			Criteria cri = new Criteria("item_category");
			cri.is(category);
			SimpleFilterQuery filterQuery = new SimpleFilterQuery(cri);
			query.addFilterQuery(filterQuery);
		}
		String brand = searchMap.get("brand");
		if(brand != null && !"".equals(brand)){
			Criteria cri = new Criteria("item_brand");
			cri.is(brand);
			SimpleFilterQuery filterQuery = new SimpleFilterQuery(cri);
			query.addFilterQuery(filterQuery);
		}
		String price = searchMap.get("price");
		if(price != null && !"".equals(price)){
			// 价格区间段  min---max, xxx以上   min---*
			Criteria cri = new Criteria("item_price");
			String[] prices = price.split("-");
			if(price.contains("*")){ // xxx以上
				cri.greaterThan(prices[0]);
			}else{	// 区间
				cri.between(prices[0], prices[1], true, true);
			}
			SimpleFilterQuery filterQuery = new SimpleFilterQuery(cri);
			query.addFilterQuery(filterQuery);
		}
		String spec = searchMap.get("spec");
		if(spec != null && !"".equals(spec)){
			// 栗子：{摄像头像素：1200万以上，网络：3G }
			// 添加条件：item_spec_摄像头像素   1200   item_spec_网络：3G
			Map<String, String> specMap = JSON.parseObject(spec, Map.class);
			Set<Entry<String, String>> entrySet = specMap.entrySet();
			for (Entry<String, String> entry : entrySet) {
				Criteria cri = new Criteria("item_spec_" + entry.getKey());
				cri.is(entry.getValue());
				SimpleFilterQuery filterQuery = new SimpleFilterQuery(cri);
				query.addFilterQuery(filterQuery);
			}
			
		}
		// 根据新品、价格进行排序
		// sortField：检索的字段
		// sort:排序的规则
		if(searchMap.get("sort") != null && !"".equals(searchMap.get("sort"))){
			if("ASC".equals(searchMap.get("sort"))){ // 升序
				Sort sort = new Sort(Direction.ASC, "item_" + searchMap.get("sortField"));
				query.addSort(sort);
			}else{
				Sort sort = new Sort(Direction.DESC, "item_" + searchMap.get("sortField"));
				query.addSort(sort);
			}
		}
		
		
		// 根据条件查询
		HighlightPage<Item> highlightPage = solrTemplate.queryForHighlightPage(query, Item.class);
		// 将结果封装到map中
		Map<String, Object> map = new HashMap<>();
		map.put("totalPages", highlightPage.getTotalPages());	// 总页数
		map.put("total", highlightPage.getTotalElements());	// 总条数
		// 处理高亮的结果集
		List<HighlightEntry<Item>> highlighted = highlightPage.getHighlighted();
		if(highlighted != null && highlighted.size() > 0){
			for (HighlightEntry<Item> highlightEntry : highlighted) {
				Item item = highlightEntry.getEntity();	// 普通结果集
				List<Highlight> highlights = highlightEntry.getHighlights();
				for (Highlight highlight : highlights) {
					List<String> snipplets = highlight.getSnipplets(); // 高亮结果集
					// 设置高亮的字段
					item.setTitle(snipplets.get(0));
				}
			}
		}
		map.put("rows", highlightPage.getContent());			// 结果集
		return map;
	}

	// 关键字检索并且分页
	private Map<String, Object> searchForPage(Map<String, String> searchMap) {
		// 封装检索条件
		String keywords = searchMap.get("keywords");
		Criteria criteria = new Criteria("item_keywords");
		if(keywords != null && !"".equals(keywords)){
			criteria.is(keywords);
		}
		SimpleQuery query = new SimpleQuery(criteria);
		// 设置分页条件
		Integer pageNo = Integer.valueOf(searchMap.get("pageNo"));
		Integer pageSize = Integer.valueOf(searchMap.get("pageSize"));
		Integer offset = (pageNo - 1) * pageSize;
		query.setOffset(offset); 	// 其始行
		query.setRows(pageSize);	// 每页显示的条数
		// 查询
		ScoredPage<Item> scoredPage = solrTemplate.queryForPage(query, Item.class);
		// 将数据进行封装
		Map<String, Object> map = new HashMap<>();
		map.put("totalPages", scoredPage.getTotalPages());	// 总页数
		map.put("total", scoredPage.getTotalElements());	// 总条数
		map.put("rows", scoredPage.getContent());			// 结果集
		return map;
	}

	/*
	 * (non-Javadoc)
	 * <p>Title: saveItemToSolr</p> 
	 * <p>Description: 将商品信息保存到索引库中</p> 
	 * @param id 
	 * @see cn.itcast.core.service.search.ItemSearchService#saveItemToSolr(java.lang.Long)
	 */
	@Override
	public void saveItemToSolr(Long id) {
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

	/*
	 * (non-Javadoc)
	 * <p>Title: deleteItemFromSolr</p> 
	 * <p>Description: 将商品从索引库中删除</p> 
	 * @param id 
	 * @see cn.itcast.core.service.search.ItemSearchService#deleteItemFromSolr(java.lang.String)
	 */
	@Override
	public void deleteItemFromSolr(String id) {
		SimpleQuery query = new SimpleQuery("item_goodsid:"+id);
		solrTemplate.delete(query);
		solrTemplate.commit();
	}
	
	

}

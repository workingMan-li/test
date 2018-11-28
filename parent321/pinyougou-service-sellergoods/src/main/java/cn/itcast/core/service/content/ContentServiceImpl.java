package cn.itcast.core.service.content;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;

import cn.itcast.core.dao.ad.ContentDao;
import cn.itcast.core.entity.PageResult;
import cn.itcast.core.pojo.ad.Content;
import cn.itcast.core.pojo.ad.ContentQuery;

@Service
public class ContentServiceImpl implements ContentService {

	@Autowired
	private ContentDao contentDao;
	
	@Autowired
	private RedisTemplate<String, Object> redisTemplate;

	@Override
	public List<Content> findAll() {
		List<Content> list = contentDao.selectByExample(null);
		return list;
	}

	@Override
	public PageResult findPage(Content content, Integer pageNum, Integer pageSize) {
		PageHelper.startPage(pageNum, pageSize);
		Page<Content> page = (Page<Content>)contentDao.selectByExample(null);
		return new PageResult(page.getTotal(), page.getResult());
	}

	@Override
	public void add(Content content) {
		// 新增时需要更新缓存
		clearCache(content.getCategoryId());
		contentDao.insertSelective(content);
	}

	

	@Override
	public void edit(Content content) {
		Long newCategoryId = content.getCategoryId(); // 最新的分类id（页面拿到的）
		Long oldCategoryId = contentDao.selectByPrimaryKey(content.getId()).getCategoryId(); // 从数据库中获取到的id
		if(newCategoryId != oldCategoryId){
			// 分类id不一致
			clearCache(oldCategoryId);
			clearCache(newCategoryId);
		}else{
			// 分类一样
			clearCache(oldCategoryId);
		}
		contentDao.updateByPrimaryKeySelective(content);
	}

	@Override
	public Content findOne(Long id) {
		Content content = contentDao.selectByPrimaryKey(id);
		return content;
	}

	@Override
	public void delAll(Long[] ids) {
		if(ids != null){
			for(Long id : ids){
				clearCache(contentDao.selectByPrimaryKey(id).getCategoryId());
				contentDao.deleteByPrimaryKey(id);
			}
		}
	}
	
	/**
	 * 
	 * @Title: clearCache
	 * @Description: 清除缓存
	 * @param categoryId
	 * @return void
	 * @throws
	 */
	private void clearCache(Long categoryId) {
		redisTemplate.boundHashOps("content").delete(categoryId);
	}

	/*
	 * (non-Javadoc)
	 * <p>Title: findByCategoryId</p> 
	 * <p>Description: 首页大广告轮播</p> 
	 * @param categoryId
	 * @return 
	 * @see cn.itcast.core.service.content.ContentService#findByCategoryId(java.lang.Long)
	 */
	// 自动干---定时任务
	@Override
	public List<Content> findByCategoryId(Long categoryId) {
		// 首先从缓存中取数据
		List<Content> list = (List<Content>) redisTemplate.boundHashOps("content").get(categoryId);
		// 判断缓存中是否存在：不存在，从数据库中查询
		// 缓存穿透、缓存雪崩---服务降级（微服务组件，给定一个写好的数据）---给出一个业务数据。
		if(list == null){
			synchronized (this) {
				list = (List<Content>) redisTemplate.boundHashOps("content").get(categoryId);
				if(list == null){
					ContentQuery contentQuery = new ContentQuery();
					contentQuery.createCriteria().andCategoryIdEqualTo(categoryId);
					list = contentDao.selectByExample(contentQuery);
					
					// 放入缓存中
					redisTemplate.boundHashOps("content").put(categoryId, list);
				}
			}
		}
		return list;
	}

	
	
}

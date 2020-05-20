package cn.edu.scujcc.api;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.Cache.ValueWrapper;
import org.springframework.cache.CacheManager;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import cn.edu.scujcc.model.Channel;
import cn.edu.scujcc.model.Comment;
import cn.edu.scujcc.model.Result;
import cn.edu.scujcc.model.User;
import cn.edu.scujcc.service.ChannelService;

@RestController
@RequestMapping("/channel")
public class ChannelController {
	private static final Logger logger = LoggerFactory.getLogger(ChannelController.class);
	
	@Autowired
	private CacheManager cacheManager;
	
	@Autowired
	private ChannelService service;
	
	@GetMapping
	public Result<List<Channel>> getAllChannels() {
		logger.info("正在读取所有频道信息!!!");
		Result<List<Channel>> result = new Result<List<Channel>>();
		List<Channel> channels = service.getAllChannels();
		result = result.ok();
		result.setData(channels);
		return result;
	}
	
	@GetMapping("/{id}")
	public Result<Channel> getChannel(@PathVariable String id) {
		logger.info("正在读取频道："+id);
		Result<Channel> result = new Result<>();
		Channel c = service.getChannel(id);
		if (c != null) {
			result = result.ok();
			result.setData(c);
		} else {
			logger.error("找不到指定的频道。");
			result = result.error();
			result.setMessage("找不到指定的频道。");
		}
		return result;
	}
	
	@DeleteMapping("/{id}")
	public Result<Channel> deleteChannel(@PathVariable String id) {
		logger.info("即将删除频道，id="+id);
		Result<Channel> result = new Result<>();
		boolean del = service.deleteChannel(id);
		if (del) {
			result = result.ok();
		} else {
			result.setStatus(Result.ERROR);
			result.setMessage("删除失败");
		}
		return result;
	}
	
	@PostMapping
	public Result<Channel> createChannel(@RequestBody Channel c) {
		logger.info("即将新建频道，频道数据：" + c);
		Result<Channel> result = new Result<>();
		Channel saved= service.createChannel(c);
		result = result.ok();
		result.setData(saved);
		return result;
	}
	
	@PutMapping
	public Result<Channel> updateChannel(@RequestBody Channel c) {
		logger.debug("即将更新频道：频道数据：" + c);
		Result<Channel> result = new Result<>();
		Channel updated = service.updateChannel(c);
		result = result.ok();
		result.setData(updated);
		return result;
	}
	
	@GetMapping("/q/{quality}")
	public List<Channel> searchByQuality(@PathVariable String quality) {
		return service.searchByQuality(quality);
	}
	
	@GetMapping("/cold")
	public List<Channel> getColdChannels(){
		return service.findColdChannels();
	}
	
	@GetMapping("/p/{page}")
	public List<Channel> getChannelsPage(@PathVariable int page) {
		return service.findChannelsPage(page);
	}
	
	@PostMapping("/{channelId}/comment")
	public Channel addComment(@PathVariable String channelId, @RequestBody Comment comment) {
		Channel result = null;
		logger.debug("即将评论频道："+channelId+"，评论对象："+comment);
		//首先检查用户是否登录过
		Cache cache = cacheManager.getCache(User.CACHE_NAME);
		ValueWrapper obj = cache.get("current_user");
		if (obj == null) {
			logger.warn("用户未登录就想评论，拒绝！");
		} else {
			//把评论保存到数据库
			String username = (String) obj.get();
			logger.debug("登录用户"+username+"正在评论...");
			comment.setAuthor(username);
			result = service.addComment(channelId, comment);
		}
		return result;
	}
	
	@GetMapping("/{channelId}/hotcomments")
	public Result<List<Comment>> hotComments(@PathVariable String channelId) {
		Result<List<Comment>> result = new Result<List<Comment>>();
		logger.debug("获取频道"+channelId+"的热门评论...");
		result = result.ok();
		result.setData(service.hotComments(channelId));
		return result;
	}
}

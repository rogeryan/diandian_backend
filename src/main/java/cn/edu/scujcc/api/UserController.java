package cn.edu.scujcc.api;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import cn.edu.scujcc.UserExistException;
import cn.edu.scujcc.model.Result;
import cn.edu.scujcc.model.User;
import cn.edu.scujcc.service.UserService;

@RestController
@RequestMapping("/user")
public class UserController {
	private static final Logger logger = LoggerFactory.getLogger(UserController.class);
	
	@Autowired
	private CacheManager cacheManager;
	
	@Autowired
	private UserService service;
	
	@PostMapping("/register")
	public Result<User> register(@RequestBody User u) {
		Result<User> result = new Result<>();
		logger.debug("即将注册用户，用户数据："+u);
		try {
			result = result.ok();
			result.setData(service.createUser(u));
		} catch (UserExistException e) {
			logger.error("用户名已存在。", e);
			result = result.error();
			result.setMessage("用户名已存在。");
		}
		
		return result;
	}
	
	@GetMapping("/login/{username}/{password}")
	public Result<User> login(@PathVariable String username,
			@PathVariable String password) {
		Result<User> result = new Result<User>();
		boolean status = service.checkUser(username, password);
		if (status) { //登录成功
			result = result.ok();
			//把用户存入缓存
			Cache cache = cacheManager.getCache(User.CACHE_NAME);
			cache.put("current_user", username);
		} else {
			result = result.error();
		}
		return result;
	}
}

package cn.edu.scujcc;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import cn.edu.scujcc.service.UserService;

/**
 * 认证拦截器，拦截掉未登录的请求，以保护后台应用程序。
 * 
 * @author Roger Yan
 *
 */
@Component
public class AuthInterceptor implements HandlerInterceptor {

	@Autowired
	private UserService userService;
	public static final Logger logger = LoggerFactory.getLogger(AuthInterceptor.class);

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {
		boolean logged = false;
		String uri = request.getRequestURI();
		if (uri.startsWith("/user/login")) {
			return true;
		} else {		
			// 从header中读取token，用于判断用户是否登录
			//TODO 精简下面这段代码
			String token = request.getHeader("token");
			if (null == token) { // 未登录
				logged = false;
			} else {
				String username = userService.currentUser(token);
				if (null == username) { // token无效
					logged = false;
				} else {
					logged = true;
				}
			}
	
			logger.debug("当前用户是否登录？" + (logged ? "通过" : "不通过"));
			return logged;
		}
	}

	@Override
	public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
			ModelAndView modelAndView) throws Exception {
		HandlerInterceptor.super.postHandle(request, response, handler, modelAndView);
	}

	@Override
	public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex)
			throws Exception {
		HandlerInterceptor.super.afterCompletion(request, response, handler, ex);
	}
}

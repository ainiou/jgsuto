package com.pro.jgsu.filter;

import com.alibaba.fastjson.JSON;
import com.pro.jgsu.common.BaseContext;
import com.pro.jgsu.common.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.AntPathMatcher;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @Author XWH
 * @Time 2022/7/31 下午 21:33
 */

@WebFilter(filterName = "loginCheckFilter",urlPatterns = "/*")
@Slf4j
public class LoginCheckFilter implements Filter {

    //路径匹配器
    public static final AntPathMatcher ANT_PATH_MATCHER = new AntPathMatcher();
    @Override
    public void doFilter(ServletRequest servletRequest,
                         ServletResponse servletResponse,
                         FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;

        //1.获取本次请求的uri
        String requestURI = request.getRequestURI();

//        log.info("拦截到请求:{}",requestURI);
        //定义不需要被拦截的请求路径
        String[] urls = new String[]{
                "/employee/login",
                "/employee/logout",
                "/backend/**",
                "/front/**",
                "/common/**",
                "/user/sendMsg",
                "/user/login"
        };
        //2.判断本次请求是否需要被拦截
        boolean check = check(urls, requestURI);

        //3.如果不需要处理check = true,直接放行
        if (check){
            filterChain.doFilter(request,response);
//            log.info("本次请求无需处理");
            return;
        }

        //4-1.判断员工登录状态，如果已登录，则直接放行
        if (request.getSession().getAttribute("employee") != null){

            Long empid = (Long) request.getSession().getAttribute("employee");
            BaseContext.setCurrentId(empid);
            filterChain.doFilter(request,response);
//            log.info("员工已登录");
            return;
        }

        //4-2.判断用户登录状态，如果已登录，则直接放行
        if (request.getSession().getAttribute("user") != null){

            Long userId = (Long) request.getSession().getAttribute("user");
            BaseContext.setCurrentId(userId);
            filterChain.doFilter(request,response);
//            log.info("用户已登录");
            return;
        }

        //5.未登录则返回未登录结果，通过输出流方式向客户端响应数据
        response.getWriter().write(JSON.toJSONString(R.error("NOTLOGIN")));
//        log.info("用户未登录");
        return;
    }

    /**
     * 判断请求是否需要拦截
     * @param urls
     * @param requestURI
     * @return
     */
    public boolean check(String[] urls,String requestURI){
        for (String url : urls) {
            boolean match = ANT_PATH_MATCHER.match(url, requestURI);
            if (match){
                return true;
            }
        }
        return false;
    }
}

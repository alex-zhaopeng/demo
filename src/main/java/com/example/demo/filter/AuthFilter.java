package com.example.demo.filter;

import com.alibaba.fastjson.JSONObject;
import com.example.demo.model.AuthTokenVo;
import com.example.demo.config.AuthConstant;
import com.example.demo.config.ReturnMessage;
import com.example.demo.util.JWTUtil;
import io.micrometer.core.instrument.util.StringUtils;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.web.filter.authc.AuthenticatingFilter;
import org.apache.shiro.web.util.WebUtils;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @ClassName AuthFilter
 * @Description 实现自定义的认证拦截器,接收传过来的token,实现前后端分离的权限认证
 * @Version 1.0
 **/
public class AuthFilter extends AuthenticatingFilter {

    private org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(AuthorizingRealm.class);
    private ReturnMessage responseResult = ReturnMessage.failWithMsg(AuthConstant.AUTHENTICATE_FAIL);

    @Override
    protected AuthenticationToken createToken(ServletRequest request, ServletResponse response) throws Exception {
        return null;
    }

    /**
     * 在这里拦截所有请求
     * @param request
     * @param response
     * @param mappedValue
     * @return
     */
    @Override
    protected boolean isAccessAllowed(ServletRequest request, ServletResponse response, Object mappedValue) {
        String token = JWTUtil.getRequestToken((HttpServletRequest)request);
        if (!StringUtils.isBlank(token)){
            try {
                this.executeLogin(request, response);
            } catch (Exception e) {
                // 应用异常
                logger.info(e.getMessage());
                responseResult = ReturnMessage.failWithMsg(e.getMessage());
                return false;
            }
        } else {
            // cookie中未检查到token或token为空
            HttpServletRequest httpServletRequest = WebUtils.toHttp(request);
            String httpMethod = httpServletRequest.getMethod();
            String requestURI = httpServletRequest.getRequestURI();
            responseResult = ReturnMessage.failWithMsg(AuthConstant.TOKEN_BLANK);
            logger.info("请求 {} 的Token为空 请求类型 {}", requestURI, httpMethod);
            return false;
        }
        return true;
    }

    /**
     * 请求失败拦截,请求终止，不进行转发直接返回客户端拦截结果
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    @Override
    protected boolean onAccessDenied(ServletRequest request, ServletResponse response) throws Exception{
        HttpServletResponse httpServletResponse = (HttpServletResponse)response;
        httpServletResponse.setContentType("application/json; charset=utf-8");
        httpServletResponse.setCharacterEncoding("UTF-8");
        //ReturnMessage returnMessage = ReturnMessage.failWithMsg(AuthConstant.AUTHENTICATE_FAIL);
        httpServletResponse.getWriter().print(JSONObject.toJSONString(responseResult));
        return false;
    }

    /**
     * 用户存在，执行登录认证
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    @Override
    protected boolean executeLogin(ServletRequest request, ServletResponse response) throws Exception {
        String token = JWTUtil.getRequestToken((HttpServletRequest)request);
        AuthTokenVo jwtToken = new AuthTokenVo(token);
        // 提交给AuthRealm进行登录认证
        getSubject(request, response).login(jwtToken);
        return true;
    }
}

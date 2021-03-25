package com.example.demo.common;

import com.example.demo.config.AuthConstant;
import com.example.demo.model.*;
import com.example.demo.service.UserService;
import com.example.demo.util.JWTUtil;
import io.micrometer.core.instrument.util.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.List;

/**
 * @ClassName AuthRealm
 * @Description
 * @Author XinChunYu
 * @Date 2020/5/29 13:51
 * @Version 1.0
 **/
public class AuthRealm extends AuthorizingRealm {

    private org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(AuthorizingRealm.class);

    @Resource
    private UserService userService;

    /**
     * 重写，绕过身份令牌异常导致的shiro报错
     *
     * @param authenticationToken
     * @return
     */
    @Override
    public boolean supports(AuthenticationToken authenticationToken) {
        return authenticationToken instanceof AuthTokenVo;
    }

    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
        logger.info("用户角色权限认证");
        //获取用户登录信息
        UserVo user = (UserVo) principals.getPrimaryPrincipal();
        List<UserVo> userVos = userService.findRolesByUsername(user.getUsername());
        //添加角色和权限
        SimpleAuthorizationInfo authorizationInfo = new SimpleAuthorizationInfo();
        for (UserVo userVo : userVos) {
            for (UserRole userRole : userVo.getUserRoleList()) {
                for (Role role : userRole.getRoleList()) {
                    authorizationInfo.addRole(role.getUserRoleName());
                    for (UserPermission permission : role.getUserPermissions()) {
                        authorizationInfo.addStringPermission(permission.getUserPermissionName());
                    }
                }
            }
        }
        return authorizationInfo;
    }

    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken authenticationToken) throws AuthenticationException {
        logger.info("执行认证逻辑");
        //获得token
        String token = (String) authenticationToken.getCredentials();
        //获得token中的用户信息
        String username = JWTUtil.getUsername(token);
        //判空
        if (StringUtils.isBlank(username)) {
            throw new AuthenticationException(AuthConstant.TOKEN_BLANK);
        }
        List<UserVo> userVos = userService.findRolesByUsername(username);
        UserVo user = !CollectionUtils.isEmpty(userVos) ? userVos.get(0) : null;
        try {
            //查询用户是否存在
            if (user == null) {
                throw new AuthenticationException(AuthConstant.TOKEN_INVALID);
                //token过期
            } else if (!(JWTUtil.verify(token, username, user.getPassword()))) {
                throw new AuthenticationException(AuthConstant.TOKEN_EXPIRE);
            }
        } catch (Exception e) {
            throw e;
        }
//        clearCachedAuthorization(SecurityUtils.getSubject().getPrincipals());
        SimpleAuthenticationInfo authenticationInfo = new SimpleAuthenticationInfo(
                user,
                token,
                getName());
        return authenticationInfo;
    }

    public void clearCachedAuthorization(PrincipalCollection principals) {
        super.clearCachedAuthorizationInfo(principals);
    }
}

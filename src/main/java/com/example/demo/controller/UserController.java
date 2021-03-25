package com.example.demo.controller;

import com.example.demo.common.AuthRealm;
import com.example.demo.config.AuthConstant;
import com.example.demo.config.ReturnMessage;
import com.example.demo.model.UserVo;
import com.example.demo.service.UserService;
import com.example.demo.util.JWTUtil;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.Authenticator;
import org.apache.shiro.authc.LogoutAware;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.apache.shiro.mgt.RealmSecurityManager;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.subject.SimplePrincipalCollection;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.springframework.stereotype.Controller;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Map;

/**
 * @ClassName UserController
 * @Description
 * @Author XinChunYu
 * @Date 2020/5/29 14:15
 * @Version 1.0
 **/
@Controller
@RequestMapping("/user")
public class UserController {

    @Resource
    UserService userService;

    @RequestMapping("/login")
    @ResponseBody
    public ReturnMessage login(@RequestBody Map<String, Object> map) throws UnsupportedEncodingException {
        String username = map.get("username").toString();
        String password = map.get("password").toString();
        UserVo user = null;
        List<UserVo> userVos = userService.findRolesByUsername(username);
        user = !CollectionUtils.isEmpty(userVos) ? userVos.get(0) : null;
        if(user == null){
            //账号不存在
            return ReturnMessage.failWithMsg(AuthConstant.UNKNOWN_ACCOUNT);
        }else if(!user.getPassword().equals(password)){
            //密码错误
            return ReturnMessage.failWithMsg(AuthConstant.WRONG_PASSWORD);
        }else{
            //通过认证, 生成签名
            String token = JWTUtil.sign(user.getUsername(), user.getPassword());
            return ReturnMessage.success().add("token", token);
        }
    }

    @RequestMapping("/logout")
    @ResponseBody
    public ReturnMessage logOut(@RequestBody Map<String, Object> map) {

        Subject subject = SecurityUtils.getSubject();
        String username = map.get("username").toString();
        List<UserVo> userVos = userService.findRolesByUsername(username);
        UserVo user = !CollectionUtils.isEmpty(userVos) ? userVos.get(0) : null;

        SimplePrincipalCollection principals = new SimplePrincipalCollection(user,"AuthRealm");
        subject.runAs(principals);

        RealmSecurityManager rsm = (RealmSecurityManager)SecurityUtils.getSecurityManager();
        AuthRealm authRealm = (AuthRealm) rsm.getRealms().iterator().next();
        authRealm.clearCachedAuthorization(subject.getPrincipals());

        subject.releaseRunAs();
//        subject.logout();
        return ReturnMessage.successWithMsg("登出成功");
    }

    @RequiresPermissions("shop")
    @RequestMapping("/shop")
    @ResponseBody
    public ReturnMessage shop(@RequestBody Map<String, Object> map){

        return ReturnMessage.success();
    }

    @RequiresPermissions("order")
    @RequestMapping("/order")
    @ResponseBody
    public ReturnMessage order(@RequestBody Map<String, Object> map){

        return ReturnMessage.success();
    }

    /**
     * 不加@RequiresPermissions 注解时不会进行用户授权认证
     * @param map
     * @return
     */
    @RequiresPermissions("game")
    @RequestMapping("/game")
    @ResponseBody
    public ReturnMessage game(@RequestBody Map<String, Object> map){
        return ReturnMessage.success();
    }

    @RequiresPermissions("address")
    @RequestMapping("/address")
    @ResponseBody
    public ReturnMessage address(@RequestBody Map<String, Object> map){
        return ReturnMessage.success();
    }

    @RequiresPermissions("money1")
    @RequestMapping("/money1")
    @ResponseBody
    public ReturnMessage money(@RequestBody Map<String, Object> map){
        return ReturnMessage.success();
    }

}

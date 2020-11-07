package com.dkm.isaaccommunity.authority;

import com.dkm.isaaccommunity.bean.User;
import com.dkm.isaaccommunity.dao.UserDao;
import org.apache.shiro.authc.*;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.cache.Cache;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;

import javax.annotation.Resource;

public class MyRealm extends AuthorizingRealm {
    @Resource(name="userDao")
    private UserDao userDao;
    //设置realm的名称
    @Override
    public void setName(String name) {
        super.setName("myRealm");
    }
    //授权身份
    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principalCollection) {

        String phone=(String) principalCollection.getPrimaryPrincipal();
        Cache cache=getAuthorizationCache();
        //List<String> authorities=(List<String>) cache.get(phone+"Authorities");
        String role=(String) cache.get(phone+"Role");
        if (role == null) {
            User user = userDao.getUserByPhone(phone);
            //authorities = employeeDao.selectEmpAuthority(id);
            role = user.getRole().toString();
            //cache.put(id+"Authorities",authorities);
            cache.put(phone+"Role",role);
        }
        SimpleAuthorizationInfo sai=new SimpleAuthorizationInfo();

        sai.addRole(role);
        //sai.addStringPermissions(authorities);
        return sai;
    }
    //认证身份
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken authenticationToken) throws AuthenticationException {
        String phone=(String) authenticationToken.getPrincipal();
        Cache cache=getAuthenticationCache();
        String passWord=(String) cache.get(phone+"PassWord");
        String salt=(String) cache.get(phone+"Salt");
        if(passWord==null){
            User user = userDao.getUserByPhone(phone);
            //用户不存在
            if(null == user){
                throw new UnknownAccountException();
            }

            //用户被锁定
            if(Boolean.TRUE.equals(user.getLocked())){
                throw new LockedAccountException();
            }
            passWord=user.getPassword();
            salt=user.getSalt();
            cache.put(phone+"PassWord",passWord);
            cache.put(phone+"Salt",salt);
        }

        SimpleAuthenticationInfo sai=new SimpleAuthenticationInfo(phone,passWord,new MySimpleByteSource(salt),getName());
        return sai;
    }

}

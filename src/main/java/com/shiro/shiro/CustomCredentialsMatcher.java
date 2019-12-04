package com.shiro.shiro;

import com.shiro.common.Const;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.SaltedAuthenticationInfo;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.authc.credential.SimpleCredentialsMatcher;
import org.apache.shiro.crypto.hash.Sha256Hash;

public class CustomCredentialsMatcher extends SimpleCredentialsMatcher {

    @Override
    public boolean doCredentialsMatch(AuthenticationToken token, AuthenticationInfo info) {
        UsernamePasswordToken usernamePasswordToken = (UsernamePasswordToken) token;
        String password = new String(usernamePasswordToken.getPassword());
        String dbPassword = (String) info.getCredentials();

        String salt = new String(((SaltedAuthenticationInfo) info).getCredentialsSalt().getBytes());
        String hashedPassword = new Sha256Hash(password, salt, Const.HASH_ITERATIONS).toString();

        return this.equals(hashedPassword, dbPassword);
    }
}

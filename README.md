# TOTP4GoogleAuthenticator

## 功能说明

根据私钥生成符合Google Authenticator规范的一次性密码(Time-Based One-Time Password)

输入的私钥为经过Base32加密后的值,加密过程使用HMAC-SHA1加密,时间间断为30s,输出的动态密码为6位数字

## 相关内容


* [Google Authenticator](https://en.wikipedia.org/wiki/Google_Authenticator) : 用户用于生成动态密码,类似软件有Authy,1Password等
* [一次性密码](https://en.wikipedia.org/wiki/Time-based_One-time_Password_Algorithm) : 即动态一次性密码


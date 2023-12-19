function rsa(param) {
    const publicKeyStr = '-----BEGIN PUBLIC KEY-----\n' +
        'MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEArnatjTOjo8fHqgjB992J\n' +
        'sbI1ZOejTb0hkwFXCj9+GK0ELXkxky1bAyCY5nn5ChhzIJH/u6pL6Bo4SqxOVfJV\n' +
        'mVwWzmQ26ABp1dSEaJ5EBasE83jjZLzxgilPXrcn2UfDJBcI4lmePklrLwzAGeKp\n' +
        'fORPyuJ5H0OTA4WR3fmfhiOj1+ug7B8woIKKiWT+5+FNKinetKt2bHPeyd75P6W5\n' +
        'h5qwzrtoX2NaRXBBfL2EH+MoKO1MUnh6R+KEJKdH6AdRfQGH1ITE/U+DKddN9etD\n' +
        'QrzuVV5dbnOzWqTyp3iQ89HeRbOwZxhVL3YSC//tuJ78oPOle5K0h4O/FcK/fFs9\n' +
        'owIDAQAB\n' +
        '-----END PUBLIC KEY-----';
    //实例化加密对象
    var encrypt = new JSEncrypt();
    //设置加密公钥
    encrypt.setPublicKey(publicKeyStr);
    //分段加密
    var encryptedData=encrypt.encryptLong(param);
    return encryptedData
}
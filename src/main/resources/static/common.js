function rsa(param) {
    const publicKeyStr = '-----BEGIN PUBLIC KEY-----\n' +
        'MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAxK6fwC/RohZblHThfWVd\n' +
        'wTWE7sk+smjJUD2KDWLVhTiCo20z8DVqLfQxCcatA60FQL6GoKcZC14rXi3rT9uf\n' +
        'cazLY0/2PkbKk2YpNadv4R69stpm+o32s1TMijjbhD/cNSBq+KQANGT7HeNbJV0J\n' +
        '68pZsPQ4hcS8jGwpae2bt/ALCtSinU4squfH1Qa/WkV63gJ08E//elKOS8LHMNlR\n' +
        '7tcj5CcKkJrXHSSyC7LdWc6mDzU4lBRY5PDXHHOVieOxUwh4ew+1NITO73StrOVB\n' +
        'EzqrgYBeflY6PrhTlZFset86zyBiUHRDFunJEahCjsETDJRtvPTSbMmA84oT0qQ+\n' +
        '7QIDAQAB\n' +
        '-----END PUBLIC KEY-----';
    //实例化加密对象
    var encrypt = new JSEncrypt();
    //设置加密公钥
    encrypt.setPublicKey(publicKeyStr);
    //分段加密
    var encryptedData = encrypt.encryptLong(param);
    return encryptedData
}

function commonHeaders() {
    var param = {"code": "SERVERUI", "key": sessionStorage.getItem('token')};
    return {
        'Content-Type': 'application/json',
        'Authorization': rsa(JSON.stringify(param)),
        'Auth-Source-Type': 'SERVERUI'
    }
}
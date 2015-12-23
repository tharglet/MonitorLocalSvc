package ac.uk.jisc.monitor

class OAuthAuthorizationCode {

    byte[] authentication
    String code

    static constraints = {
        code nullable: false, blank: false, unique: true
        authentication nullable: false, minSize: 1, maxSize: 1024 * 4
    }

    static mapping = {
        version false
    }
}

package uk.ac.jisc.monitorlocal

import groovy.transform.EqualsAndHashCode
import groovy.transform.ToString

@EqualsAndHashCode(includes='username')
@ToString(includes='username', includeNames=true, includePackage=false)
class User implements Serializable {

  private static final long serialVersionUID = 1

  transient springSecurityService

  String username
  String password
  boolean enabled = true
  boolean accountExpired
  boolean accountLocked
  boolean passwordExpired


        String displayName
        String profilePic
        String email

        static hasMany = [
          socialIdentities : SocialIdentity
        ]

        static mappedBy = [
          socialIdentities : 'user'
        ]

  User(String username, String password) {
    this()
    this.username = username
    this.password = password
  }

  Set<Role> getAuthorities() {
    UserRole.findAllByUser(this)*.role
  }

  def beforeInsert() {
    encodePassword()
  }

  def beforeUpdate() {
    if (isDirty('password')) {
      encodePassword()
    }
  }

  protected void encodePassword() {
    password = springSecurityService?.passwordEncoder ? springSecurityService.encodePassword(password) : password
  }

  static transients = ['springSecurityService']

  static constraints = {
    username blank: false, unique: true
    password blank: false
    displayName blank: true, nullable:true
    profilePic blank: true, nullable:true
    email blank: true, nullable:true
  }

  static mapping = {
    password column: '`password`'
  }
  
  public createUserDTO() {
    def verified_user = Role.findByAuthority('ROLE_VERIFIED_USER')
    def result = [
      userid:this.username,
      email:this.email,
      displayName:this.displayName,
      profilePic:this.profilePic,
      bio: this.biography,
      affiliations: [],
      verified: this.authorities.contains(verified_user)
    ]
    return result;
  }
}

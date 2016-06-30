package uk.ac.jisc.monitorlocal

import groovy.transform.EqualsAndHashCode
import groovy.transform.ToString
import uk.ac.jisc.monitorlocal.databinding.AbsoluteCollection;

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

  String profilePic
  String email
  String biography
  String name

  @AbsoluteCollection
  Set socialIdentities = []
  
  @AbsoluteCollection
  Set orgAffiliations = []
  
  static hasMany = [
    socialIdentities : SocialIdentity,
    orgAffiliations: UserOrg
  ]

  static mappedBy = [
    socialIdentities : 'user',
    orgAffiliations: 'user'
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
  
  def beforeValidate() {
    if (!name) {
      name = username
    }
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
    profilePic blank: true, nullable:true
    email blank: true, nullable:true
    name blank: true, nullable:true
    biography blank: true, nullable:true
  }

  static mapping = {
    password column: '`password`'
  }
  
  public createUserDTO() {

    def verified_user = Role.findByAuthority('ROLE_VERIFIED_USER')

    def result = [
      userid:this.username,
      email:this.email,
      name:this.name,
      profilePic:this.profilePic,
      bio: this.biography,
      affiliations: [],
      roles: [],
      verified: this.authorities.contains(verified_user)
    ]

    orgAffiliations.each {
      result.affiliations.add([org:it.org.name,role:it.formalRole?.value,status:it.status?.value]);
    }
    
    getAuthorities().each {
      result.roles.add(it.authority);
    }

    return result;
  }
}

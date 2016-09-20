package uk.ac.jisc.monitorlocal

import groovy.transform.EqualsAndHashCode
import groovy.transform.ToString

import javax.persistence.Transient

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
  String localId

  String profilePic
  String email
  String biography
  String name

  Set socialIdentities = []
  
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
    if (password) {
      encodePassword()
    }
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

  static transients = ['springSecurityService', 'verified', 'userOrg']

  static constraints = {
    username blank: false, unique: true, bindable: false
    password blank: false, bindable: false
    profilePic blank: true, nullable:true, bindable: false
    email blank: true, nullable:true, bindable: false
    name blank: true, nullable:true, bindable: false
    biography blank: true, nullable:true, bindable: false
    localId blank: true, nullable:true, bindable: false
    
    enabled bindable: false
    accountExpired bindable: false
    accountLocked bindable: false
    passwordExpired bindable: false
  
    socialIdentities bindable: false
    orgAffiliations bindable: false
  }

  static mapping = {
    password column: '`password`'
    orgAffiliations joinTable: [name: "user_affiliations" ], cascade: "all-delete-orphan"
  }
  
  public boolean isVerified () {
    this.authorities.contains ( Role.findByAuthority('ROLE_VERIFIED_USER') )
  }

  public createUserDTO() {

    def result = [
      userid:this.username,
      email:this.email,
      name:this.name,
      profilePic:this.profilePic,
      bio: this.biography,
      affiliations: [],
      roles: [],
      verified: this.verified,
      wibble:true
    ]

    orgAffiliations.each {
      result.affiliations.add([org:it.org.name,role:it.formalRole?.value,status:it.status?.value]);
      // Maybe have a status that lets the user set their home institution
      if ( result.instCtx == null ) {
        result.instCtx=[
          id:userInstitution?.id,
          name:userInstitution?.name
        ]
      }
    }
    
    getAuthorities().each {
      result.roles.add(it.authority);
    }

    return result;
  }

  @Transient
  public Org getUserOrg() {
    def result = null
    int total = orgAffiliations.size()
    if ( total > 0 ) {
      result = orgAffiliations.getAt(total - 1).org
    }
    result
  }
  
  @Transient
  public void setUserOrg(Org org) {
    
    // Firstly, clear down the affiliations.
    orgAffiliations.each {
      removeFromOrgAffiliations(it)
    }
    
    save()
    
    UserOrg uo = new UserOrg()
    uo.org = org
    uo.status = 1
    uo.formalRole = Role.findByAuthority('User')
    
    // Then add the relationship.
    addToOrgAffiliations(uo)
    
    save(failOnError: true)
  }
}

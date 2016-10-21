package uk.ac.jisc.monitorlocal

import grails.plugin.springsecurity.SpringSecurityService
import grails.rest.Resource
import grails.util.Holders
import groovy.transform.EqualsAndHashCode
import groovy.transform.ToString

import java.util.Map;

import javax.persistence.Transient

import uk.ac.jisc.monitorlocal.rest.UserRestfulController

import com.k_int.grails.tools.refdata.*

@EqualsAndHashCode(includes='username')
@ToString(includes='username', includeNames=true, includePackage=false)
@Resource(uri="/user", superClass=UserRestfulController)
class User implements Serializable {
  static lookupBase = 'ownedComponents'
  static namedQueries = {
    ownedComponents {
      or {
        isEmpty ('orgAffiliations')
        orgAffiliations {  
          and {
            def currentAffiliation =  Holders.applicationContext.getBean("springSecurityService", SpringSecurityService)?.currentUser?.getUserOrg()
            or {
              eq ("status", 1) // Approved
              eq ("status", 3) // Auto approved
            }
            eq ("org",currentAffiliation)
          }
        }
      }
    }
  }
  
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
    email blank: true, nullable:true
    name blank: true, nullable:true, bindable: true
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
  
  @Transient
  public boolean isVerified () {
    def verified_user = Role.findByAuthority('ROLE_VERIFIED_USER')
    if (!verified_user) {
      // no "ROLE_VERIFIED_USER" role, log an error and respond with a 500 InternalServerError.
      log.error('missing role "ROLE_VERIFIED_USER"')
      response.status(500, "invalid system state")
    }
    authorities.contains ( verified_user )
  }
  
  public void verify () {
    if (!isVerified()) {
      // Add a user role.
      UserRole v = new UserRole()
      User u = this
      v.with {
        role = Role.findByAuthority('ROLE_VERIFIED_USER')
        user = u
        save (flush: true, failOnError: true)
      }
    }
  }
  
  public boolean unVerify () {
    UserRole.findAllByUserAndRole(this, Role.findByAuthority('ROLE_VERIFIED_USER'))*.delete(flush: true, failOnError:true)
  }

//  public createUserDTO() {
//
//    def result = [
//      userid:this.username,
//      email:this.email,
//      name:this.name,
//      profilePic:this.profilePic,
//      bio: this.biography,
//      affiliations: [],
//      roles: [],
//      verified: this.verified,
//      wibble:true
//    ]
//
//    orgAffiliations.each {
//      result.affiliations.add([org:it.org.name,role:it.formalRole?.value,status:it.status?.value]);
//      // Maybe have a status that lets the user set their home institution
//      if ( result.instCtx == null ) {
//        result.instCtx=[
//          id:userInstitution?.id,
//          name:userInstitution?.name
//        ]
//      }
//    }
//    
//    getAuthorities().each {
//      result.roles.add(it.authority);
//    }
//
//    return result;
//  }

  @Transient
  public Org getUserOrg() {
    def result = getCurrentAffiliation()?.org
  }

  @Transient
  public int getStatus() {
    UserOrg aff = getCurrentAffiliation()
    if (aff == null) {
      return 0 
    }
    
    aff.status
  }

  @Transient
  public UserOrg getCurrentAffiliation() {
    UserOrg result = null
    int total = orgAffiliations.size()
    if ( total > 0 ) {
      result = orgAffiliations.getAt(total - 1)
    }
    result
  }
  
  @Transient
  public void defaultUserOrg(Org org) {
    
    // Firstly, clear down the affiliations.
    orgAffiliations.each {
      removeFromOrgAffiliations(it)
    }
    
    save()
    
    UserOrg uo = new UserOrg()
    uo.org = org
    uo.status = 0 // Pending
    uo.formalRole = Role.findByAuthority('User')
    
    // Then add the relationship.
    addToOrgAffiliations(uo)
    
    save(failOnError: true)
  }
  
  public static Map getSearchConfig() {

    return [
      baseclass:'uk.ac.jisc.monitorlocal.User',
      useDistinct: true,
      title:'Users',
      group:'Secondary',
      defaultSort:'name',
      defaultOrder:'asc',
      qbeConfig:[
        qbeForm:[
          [
            prompt:'Search',
            qparam:'q',
            placeholder:'Search Users',
            contextTree: [ 'ctxtp':'disjunctive',
              'terms':[
                ['ctxtp':'qry', 'comparator' : 'ilike', 'prop':'name', 'wildcard':'R'],
                ['ctxtp':'qry', 'comparator' : 'eq', 'prop':'id', 'wildcard':'R'],
              ]
            ]
          ]
        ],
        qbeGlobals:[],
        qbeResults:[
          [heading:'Name', property:'name',sort:'name', link:[controller:'resource',action:'show',id:'x.r.class.name+\':\'+x.r.id'] ],
          [heading:'Status', sort:'status', property:'status.value'],
        ]
      ]
    ]
  }
}

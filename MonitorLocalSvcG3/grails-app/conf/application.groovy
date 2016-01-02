

// Added by the Spring Security Core plugin:
grails.plugin.springsecurity.userLookup.userDomainClassName = 'uk.ac.jisc.monitorlocal.User'
grails.plugin.springsecurity.userLookup.authorityJoinClassName = 'uk.ac.jisc.monitorlocal.UserRole'
grails.plugin.springsecurity.authority.className = 'uk.ac.jisc.monitorlocal.Role'
grails.plugin.springsecurity.controllerAnnotations.staticRules = [
	[pattern: '/',               access: ['permitAll']],
	[pattern: '/jwt/**',         access: ['permitAll']],
	[pattern: '/error',          access: ['permitAll']],
	[pattern: '/index',          access: ['permitAll']],
	[pattern: '/index.gsp',      access: ['permitAll']],
	[pattern: '/shutdown',       access: ['permitAll']],
	[pattern: '/assets/**',      access: ['permitAll']],
	[pattern: '/**/js/**',       access: ['permitAll']],
	[pattern: '/**/css/**',      access: ['permitAll']],
	[pattern: '/**/images/**',   access: ['permitAll']],
	[pattern: '/**/favicon.ico', access: ['permitAll']]
]

grails.plugin.springsecurity.filterChain.chainMap = [
	[pattern: '/assets/**',      filters: 'none'],
	[pattern: '/**/js/**',       filters: 'none'],
	[pattern: '/**/css/**',      filters: 'none'],
	[pattern: '/**/images/**',   filters: 'none'],
	[pattern: '/**/favicon.ico', filters: 'none'],
	[pattern: '/**',             filters: 'JOINED_FILTERS']
]

auth = [
  sob:[
    key : 'xxx',
    secret : 'xxx',
    scope : 'read',
    defaultRoles : ['ROLE_USER', 'ROLE_GOOGLE'],
    accessTokenUrl : 'https://www.kbplus.ac.uk/sobtest/oauth/token',
    peopleApiUrl : 'https://www.kbplus.ac.uk/sobtest/userinfo',
    query : [:],
    userMapping: [username: "sub", 
                  displayName: "name", 
                  profilePic: "picture", 
                  email: "email"]
                  // Other properties include
                  //sub	string	Subject - Identifier for the End-User at the Issuer.
                  //name	string	End-User's full name in displayable form including all name parts
                  //given_name	string	Given name(s) or first name(s) of the End-User.
                  //family_name	string	Surname(s) or last name(s) of the End-User.
                  //middle_name	string	Middle name(s) of the End-User.
                  //nickname	string	Casual name of the End-User
                  //preferred_username	string	Shorthand name by which the End-User wishes to be referred to at the RP
                  //profile	string	URL of the End-Users profile page. The contents of this Web page SHOULD be about the End-User.
                  //picture	string	URL of the End-User's profile picture. This URL MUST refer to an image file
                  //website	string	URL of the End-User's Web page or blog.
                  //email	string	End-User's preferred e-mail address.
                  //email_verified	boolean	True if the End-User's e-mail address has been verified
                  //gender	string	End-User's gender.
                  //birthdate	string	End-User's birthday, represented as an ISO 8601:2004 [ISO8601‑2004] YYYY-MM-DD format
                  //zoneinfo	string	String from zoneinfo [zoneinfo] time zone database representing the End-User's time zone.
                  //locale	string	End-User's locale, represented as a BCP47 [RFC5646] language tag.
                  //phone_number	string	End-User's preferred telephone number. E.164 [E.164] is RECOMMENDED as the format of this Claim,
                  //phone_number_verified	boolean	True if the End-User's phone number has been verified; otherwise false.
                  //address	JSON object	End-User's preferred postal address.
                  //updated_at	number	Time the End-User's information was last updated
  ],
  google : [
    key : 'xxx',
    secret : 'xxx',
    scope : '',
    defaultRoles : ['ROLE_USER', 'ROLE_GOOGLE'],
    accessTokenUrl : 'https://accounts.google.com/o/oauth2/token',
    peopleApiUrl : 'https://www.googleapis.com/plus/v1/people/me/openIdConnect',
    query : [:],
    userMapping: [username: "sub", displayName: "name", profilePic: "picture", email: "email"]
  ],
]

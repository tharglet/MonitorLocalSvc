

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
    accessTokenUrl : 'https://accounts.google.com/o/oauth2/token',
    peopleApiUrl : 'https://www.googleapis.com/plus/v1/people/me/openIdConnect',
    query : [:],
    userMapping: [username: "sub", displayName: "name", profilePic: "picture", email: "email"]
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

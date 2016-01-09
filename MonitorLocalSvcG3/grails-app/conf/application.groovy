
monitorLocal_jwk=[
  "kty":"RSA",
  "kid":"k1",
  "n":"oYfKNEfsrDdi8hEFPL8CQ0CKS3XiugjST_i3D1d7FiNjCY9hyj4qEuLmm7PGF93yiccny6bWr37shzun59jXOf62zFwFK_HeBpWKSz2QTWCVjfqrfgQYOYDzqHZnAEwlcueswaYx83HIUzBCigr4kAphwDpPMixveSj4n3PxCCEza6Qycp3KdhHq4JUfqzd0lFtm2EGBnqKkOiVCy1oRGJaHjhDb_oHgNs1eKmqVuuKl-OsUbmnMP4HOEVCH1y8D4JLEzPzrE0CZcip5e1nm2qGlOnW-vL7l-rGoxSn47rAsX408uNTafhI4SdD3CSeeEvS7OnSNN1whTYBIbFN9vw",
  "e":"AQAB",
  "d":"Xd8SDYv26KZPZCmsB39hiz_Ins3lcwOJZVw-1jWanGT4OygORoAV51_hcH7rEhezyYnpgJp2jQqP3o82prAHc_aouN6JSt6GjwD8m3ktkSod_6K7YsJyaYidETut5ChcYwhsByXURYvMRTv4j0yM4viFkjbQq3HTXnAG_HNekb_4vLUmHc7VXGs1OK0532V7wJAXrdC6SOvSHPSXSvSAF7HgNYrMvQ1P1abGcypXabURO5jEt_qd02tEFMheyrkyZ_wjTbOCYdPX-w6x3GbHhF1hyQ9F0jtgLCRTWTIo4TIxIlwJppHlDaLzuUmd0FZstDfCwqiGX4JSeAWUhYSm0Q",
  "p":"93DT9AgQX1M87J_OTmr9AGfDz5TnxEkwLu7Ufi6YPpaCQ8DGbeVVUYRmL3fEYOFJA22bROPd4DQndL75W-rxRaYB95jBT4jQervzllSkbyd5OR9PBGN7plLrlPzY0gnPNMtQAjoP0rQ6J5pWjQNQ5LMm5eELyeTddnveSrLWrq0",
  "q":"px4yZqU9Qw2kQBXgTVdoqfy8NWx1Ea3c2skc4BfphtI91QGorcsitgMO-wJII9t-DnZTqrOexVPYV65lnMxrysbeiQjKzfv93fMUuMi8dWgjcoCIgh5tS-RYgTNNQLyAYAQbKcf4pEH5EpsyXUP1XiTaKvCOfcQ_OonYbsL1B5s",
  "dp":"cJpn2iXRTZATqZsPx5_ogsa11UFuN2UXCESnbsTFg7X6kdqoclpq-S3HUtKuC2h6UZUPk8oermkNi5Nc5veD1K43NvXNAuS_-xqNP1xFeE_-WoBk9RpQquARVTu7b9oG8X-UaW8CxGBAFsWlnYZkya69hN9k_fuozMKR5mx8RgE",
  "dq":"ZM2fIzEqucaBu7I2YRCHbnmnYSYxm19fJ41F4Jor34nziAd_Io20O0uu_pNKxm5AJCMVppLAA1Dm30SvwBvCLUc1tjzab9M_N2DHsApNOxh8wTVsoegOj0eGZzB1ucW6PbBcXiBkB1hQD_wTLhSu7YGXI22E1bHJXdsF9RGd0V8",
  "qi":"gqDj9Pfl9IhLAVn00a3RYZsxiODyySqEfpVkgHkHai0Mq1-5oC0dhWYr9IXjCX7RMjTzHhgSXxb7eR4EjqvyCboH1m904m5_myPi5wxBYWaGnr9wQ8CZ1TosU9PtBHz_UlRY2xPCTYY-kqEd1JDUx0GqwEP06mJW2DcNBL9nLyo"
]


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
    key : 'monitorLocalDev',
    secret : 'monitorLocalDevSecret',
    scope : ['read'],
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

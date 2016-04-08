grails.gorm.default.constraints = {
  // Default everything to nullable but not blank.
  '*' ( nullable: true, blank:false )
//  'required' ( validator: { value, obj ->
//    if (value == null || value == "") return 'shared.required.error'
//  })
//  'alphanumeric' ( validator: { value, obj ->
//    if (!(value =~ /^[a-z|A-Z|\d]+$/)) return 'shared.alphanumeric.error'
//  })
//  'alphanumericSpace' ( validator: { value, obj ->
//    if (!(value =~ /^[a-z|A-Z|\d\s]+$/)) return 'shared.alphanumericSpace.error'
//  })
//  'titleText' ( validator: { value, obj ->
//    if (!(value =~ /^[a-z|A-Z\d\s\-\(\)\:\;\?\,\"\']+$/)) return 'shared.titleText.error'
//  })
//  'number' ( validator: { value, obj ->
//    if (!(value =~ /^(\-)?\d*$/)) return 'shared.number.error'
//  })
}
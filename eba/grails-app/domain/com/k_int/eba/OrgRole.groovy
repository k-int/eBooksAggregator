package com.k_int.eba

class OrgRole {

  static belongsTo = [
    org:Org
  ]

  RefdataValue roleType

  // For polymorphic joins based on "Target Context"
  TitleInstance title

  static mapping = {
          id column:'or_id'
     version column:'or_version'
         org column:'or_org_fk', index:'or_org_rt_idx'
    roleType column:'or_roletype_fk', index:'or_org_rt_idx'
       title column:'or_title_fk'
  }

  static constraints = {
    roleType(nullable:true, blank:false)
    title(nullable:true, blank:false)
  }
  
  
  static def assertOrgTitleLink(porg, ptitle, prole) {
    // def link = OrgRole.findByTitleAndOrgAndRoleType(ptitle, porg, prole) ?: new OrgRole(title:ptitle, org:porg, roleType:prole).save();
    if ( porg && ptitle && prole ) {
      def link = OrgRole.find{ title==ptitle && org==porg && roleType==prole }
      if ( ! link ) {
        link = new OrgRole(title:ptitle, org:porg, roleType:prole)
        if ( !porg.links )
          porg.links = [link]
        else
          porg.links.add(link)
  
        porg.save(flush:true, failOnError:true);
      }
    }
  }

}

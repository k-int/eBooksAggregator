package com.k_int.eba

import javax.persistence.Transient

class Platform {

  String impId
  String name
  String normname
  String primaryUrl
  String provenance
  RefdataValue type
  RefdataValue status
  RefdataValue serviceProvider
  RefdataValue softwareProvider
  Date dateCreated
  Date lastUpdated


  static mappedBy = [tips: 'platform']
  static hasMany = [tips: TitleInstancePlatform]

  static mapping = {
                id column:'plat_id'
           version column:'plat_version'
             impId column:'plat_imp_id', index:'plat_imp_id_idx'
              name column:'plat_name'
          normname column:'plat_normalised_name'
        provenance column:'plat_data_provenance'
        primaryUrl column:'plat_primary_url'
              type column:'plat_type_rv_fk'
            status column:'plat_status_rv_fk'
   serviceProvider column:'plat_servprov_rv_fk'
  softwareProvider column:'plat_softprov_rv_fk'
              tips sort: 'title.title', order: 'asc'
  }

  static constraints = {
    impId(nullable:true, blank:false)
    primaryUrl(nullable:true, blank:false)
    provenance(nullable:true, blank:false)
    type(nullable:true, blank:false)
    status(nullable:true, blank:false)
    serviceProvider(nullable:true, blank:false)
    softwareProvider(nullable:true, blank:false)
  }

  def static lookupOrCreatePlatform(Map params=[:]) {

    def platform = null;

    if ( params.name && (params.name.trim().length() > 0)  ) {

      String norm_name = params.name.trim().toLowerCase();

      platform = Platform.findByNormname(norm_name)

      if ( !platform ) {
        platform = new Platform(name:params.name,
                                normname:norm_name,
                                provenance:params.provenance,
                                primaryUrl:params.primaryUrl,
                                lastmod:System.currentTimeMillis()).save(flush:true)
      }
    }
    platform;
  }
  
  static def refdataFind(params) {
    def result = [];
    def ql = null;
    ql = Platform.findAllByNameIlike("${params.q}%",params)

    if ( ql ) {
      ql.each { t ->
        result.add([id:"${t.class.name}:${t.id}",text:"${t.name}"])
      }
    }

    result
  }
}

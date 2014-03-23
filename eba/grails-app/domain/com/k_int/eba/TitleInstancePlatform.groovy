package com.k_int.eba

import javax.persistence.Transient
import org.codehaus.groovy.grails.commons.ApplicationHolder
import org.hibernate.proxy.HibernateProxy


class TitleInstancePlatform {

  static auditable = true

  Date startDate
  String rectype="so"
  String startVolume
  String startIssue
  Date endDate
  String endVolume
  String endIssue
  String embargo
  String coverageDepth
  String coverageNote
  String impId
  RefdataValue status
  RefdataValue option
  RefdataValue delayedOA
  RefdataValue hybridOA
  RefdataValue statusReason
  RefdataValue payment
  String hostPlatformURL
  Date coreStatusStart
  Date coreStatusEnd

  static mappedBy = [ids: 'tipp']
  static hasMany = [ids: IdentifierOccurrence]


  static belongsTo = [
    title:TitleInstance,
    platform:Platform
  ]

  static mapping = {
                id column:'tipp_id'
           rectype column:'tipp_rectype'
           version column:'tipp_version'
               pkg column:'tipp_pkg_fk', index: 'tipp_idx'
          platform column:'tipp_plat_fk', index: 'tipp_idx'
             title column:'tipp_ti_fk', index: 'tipp_idx'
         startDate column:'tipp_start_date'
       startVolume column:'tipp_start_volume'
        startIssue column:'tipp_start_issue'
           endDate column:'tipp_end_date'
         endVolume column:'tipp_end_volume'
          endIssue column:'tipp_end_issue'
           embargo column:'tipp_embargo'
     coverageDepth column:'tipp_coverage_depth'
      coverageNote column:'tipp_coverage_note',type: 'text'
             impId column:'tipp_imp_id', index: 'tipp_imp_id_idx'
            status column:'tipp_status_rv_fk'
         delayedOA column:'tipp_delayedoa_rv_fk'
          hybridOA column:'tipp_hybridoa_rv_fk'
      statusReason column:'tipp_status_reason_rv_fk'
           payment column:'tipp_payment_rv_fk'
            option column:'tipp_option_rv_fk'
   hostPlatformURL column:'tipp_host_platform_url'
   coreStatusStart column:'tipp_core_status_start_date'
     coreStatusEnd column:'tipp_core_status_end_date'
  }

  static constraints = {
    startDate(nullable:true, blank:true);
    startVolume(nullable:true, blank:true);
    startIssue(nullable:true, blank:true);
    endDate(nullable:true, blank:true);
    endVolume(nullable:true, blank:true);
    endIssue(nullable:true, blank:true);
    embargo(nullable:true, blank:true);
    coverageDepth(nullable:true, blank:true);
    coverageNote(nullable:true, blank:true);
    impId(nullable:true, blank:true);
    status(nullable:true, blank:false);
    delayedOA(nullable:true, blank:false);
    hybridOA(nullable:true, blank:false);
    statusReason(nullable:true, blank:false);
    payment(nullable:true, blank:false);
    option(nullable:true, blank:false);
    hostPlatformURL(nullable:true, blank:true);
    coreStatusStart(nullable:true, blank:true);
    coreStatusEnd(nullable:true, blank:true);
  }

  
  def getHostPlatform() {
    def result = null;
    additionalPlatforms.each { p ->
      if ( p.rel == 'host' ) {
        result = p.titleUrl
      }
    }
    result
  }

  private def stringify(obj) {
    def result = null
    if ( obj != null ) {
      if ( obj instanceof Date ) {
        def df = new java.text.SimpleDateFormat('yyyy-MM-dd');
        result = df.format(obj);
      }
      else {
        result = obj.toString()
      }
    }
    result
  }

  public static <T> T deproxy(def element) {
    if (element instanceof HibernateProxy) {
      return (T) ((HibernateProxy) element).getHibernateLazyInitializer().getImplementation();
    }
    return (T) element;
  }

}

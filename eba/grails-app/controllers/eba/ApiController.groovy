package eba

import com.k_int.eba.*
import groovy.json.JsonSlurper

// import grails.plugin.springsecurity.annotation.Secured
import org.codehaus.groovy.grails.commons.GrailsClassUtils
import grails.converters.*

class ApiController {

  def index() { }

  def assertTitle() {
    log.debug(params);
    def model=[:]

    try {
      if ( request.method=='POST') {
        log.debug("Post....");
        def file = request.getFile("tf")
        def record = new String(file.getBytes())
        def json = new JsonSlurper().parseText(record)

        def candidate_identifiers = []
        json.titleIdentifiers.each { key, value ->
          candidate_identifiers.add([namespace:key,value:value])
        }
        
        def title_obj = TitleInstance.lookupOrCreate(candidate_identifiers, json.title)
        title_obj.pubdate = json.pubdate
        
        def pub_org = Org.lookupOrCreate(json.publisher, null, null, [pubname:json.publisher], null)
        
        if ( title_obj.orgs == null ) {
          title_obj.orgs = []
        }

        title_obj.save()

        def publisher_role = RefdataCategory.lookupOrCreate('OrgRole','Publisher')
        def pub_or = new OrgRole(title:title_obj, org:pub_org,roleType:publisher_role).save()

        def platform_obj = Platform.lookupOrCreatePlatform(name:json.platformName)


        // Do we already have a tip
        def tip_count = TitleInstancePlatform.executeQuery("select count(*) from TitleInstancePlatform as tip where tip.title = ? and tip.platform=?",[title_obj,platform_obj])[0]
        if ( tip_count == 0 ) {
          log.debug("Create new tip - ${json.title}");
          TitleInstancePlatform tip = new TitleInstancePlatform(title:title_obj,platform:platform_obj,hostPlatformURL:json.platformUrl).save()

          json.platformIdentifiers.each { k,v ->
            def ti_id = Identifier.lookupOrCreateCanonicalIdentifier(k,v)
            def io = new IdentifierOccurrence(identifier:ti_id, ti:title_obj).save(flush:true)
          }
        }
        else {
          log.debug("TIP already exists - skipping");
        }

        //log.debug("Got record:${record}");
        // model.ingestResult = ingestService.ingest(record,params.id, request.user, file.contentType);
        //    "title": "101 Interventions in Group Therapy, Revised Edition",
        //    "titleIdentifiers": {
        //        "ISSN": "9780415882170"
        //    },
        //    "platformIdentifiers": {
        //        "DawsonTitleId": 13646807
        //    },
        //    "publisher": "Brunner-Routledge",
        //    "publication_date": 2012,
        //    "edition": null,
        //    "platformName": "Dawson",
        //    "paltformId": "Dawson",
        //    "platformUrl": "http://www.dawsonera.com/depp/reader/protected/external/AbstractView/S9780203835944",
        //    "additionalUrl": null

       
      }
    }
    catch ( Exception e ) {
      log.error("Problem",e);
    }

    render model as JSON

  }
}

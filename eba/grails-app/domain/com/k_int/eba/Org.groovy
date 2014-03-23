package com.k_int.eba

class Org {

  String name
  String impId
  String address
  String ipRange
  String sector
  String scope
  Date dateCreated
  Date lastUpdated
  String categoryId

  // Used to generate friendly semantic URLs
  String shortcode

  Set ids = []

  static mappedBy = [ids: 'org']

  static hasMany = [ids: IdentifierOccurrence] 

  static mapping = {
            id column:'org_id'
       version column:'org_version'
         impId column:'org_imp_id', index:'org_imp_id_idx'
          name column:'org_name', index:'org_name_idx'
       address column:'org_address'
       ipRange column:'org_ip_range'
     shortcode column:'org_shortcode'
         scope column:'org_scope'
    categoryId column:'org_cat'
  }

  static constraints = {
         impId(nullable:true, blank:true,maxSize:256);
       address(nullable:true, blank:true,maxSize:256);
       ipRange(nullable:true, blank:true, maxSize:1024);
        sector(nullable:true, blank:true, maxSize:128);
     shortcode(nullable:true, blank:true, maxSize:128);
         scope(nullable:true, blank:true, maxSize:128);
    categoryId(nullable:true, blank:true, maxSize:128);
  }

  def beforeInsert() {
    if ( !shortcode ) {
      shortcode = generateShortcode(name);
    }
  }

  def beforeUpdate() {
    if ( !shortcode ) {
      shortcode = generateShortcode(name);
    }
  }

  def generateShortcode(name) {
    def candidate = name.trim().replaceAll(" ","_")
    return incUntilUnique(candidate);
  }

  def incUntilUnique(name) {
    def result = name;
    if ( Org.findByShortcode(result) ) {
      // There is already a shortcode for that identfier
      int i = 2;
      while ( Org.findByShortcode("${name}_${i}") ) {
        i++
      }
      result = "${name}_${i}"
    }

    result;
  }

  def getIdentifierByType(idtype) {
    def result = null
    ids.each { id ->
      if ( id.identifier.ns.ns == idtype ) {
        result = id.identifier;
      }
    }
    result
  }

  static def refdataFind(params) {
    def result = [];
    def ql = null;
    ql = Org.findAllByNameIlike("%${params.q}%",params)

    if ( ql ) {
      ql.each { id ->
        result.add([id:"${id.class.name}:${id.id}",text:"${id.name}"])
      }
    }

    result
  }

  static def refdataCreate(value) {
    return new Org(name:value);
  }

  static def lookupOrCreate(name, sector, consortium, identifiers, iprange) {

    def result = null;

    // See if we can uniquely match on any of the identifiers
    identifiers.each { k,v ->
      if ( v != null ) {
        def o = Org.executeQuery("select o from Org as o join o.ids as io where io.identifier.ns.ns = ? and io.identifier.value = ?",[k,v])
        if ( o.size() > 0 ) {
          result = o[0]
        }
      }
    }

    // No match by identifier, try and match by name
    if ( result == null ) {
      // log.debug("Match by name ${name}");
      def o = Org.executeQuery("select o from Org as o where lower(o.name) = ?",[name.toLowerCase()])
      if ( o.size() > 0 ) {
        result = o[0]
      }
    }

    if ( result == null ) {
      // log.debug("Create new entry for ${name}");
      result = new Org(
                       name:name, 
                       sector:sector,
                       ipRange:iprange,
                       impId:java.util.UUID.randomUUID().toString()).save()

      identifiers.each { k,v ->
        def io = new IdentifierOccurrence(org:result, identifier:Identifier.lookupOrCreateCanonicalIdentifier(k,v)).save()
      }

    }
 
    result 
  }
}

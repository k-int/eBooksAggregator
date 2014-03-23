@Grapes([
    @Grab(group='net.sourceforge.nekohtml', module='nekohtml', version='1.9.14'),
    @Grab(group='org.codehaus.groovy.modules.http-builder', module='http-builder', version='0.5.1'),
    @Grab(group='xerces', module='xercesImpl', version='2.9.1'),
    @Grab(group='org.apache.httpcomponents', module='httpmime', version='4.1.2'),
    @Grab(group='org.apache.httpcomponents', module='httpclient', version='4.0'),
    @Grab(group='org.apache.poi', module='poi', version='3.10-FINAL'),
    @Grab(group='org.apache.poi', module='poi-ooxml', version='3.10-FINAL') ])

import groovyx.net.http.*
import static groovyx.net.http.ContentType.URLENC
import static groovyx.net.http.ContentType.*
import static groovyx.net.http.Method.*
import groovyx.net.http.*
import org.apache.http.entity.mime.*
import org.apache.http.entity.mime.content.*
import java.nio.charset.Charset
import static groovy.json.JsonOutput.*
import org.apache.poi.xssf.usermodel.*;
import org.apache.poi.xssf.model.*;
import org.apache.poi.*;

import groovy.util.slurpersupport.GPathResult
import org.apache.http.*
import org.apache.http.protocol.*


// Usually run as
// groovy ./import.groovy ~/Dropbox/eBooksReport/Dawson\ titles\ 1\ of\ 5.xlsx 
//

println("Loading ${args[0]}");
loadWorkbook(args[0])

def loadWorkbook(filename) {

  def url = "http://localhost:8080"

  def api = new RESTClient(url)
  def rest_upload_pass = ""
  System.in.withReader {
    print 'upload pass:'
    rest_upload_pass = it.readLine()
  }

  // Add preemtive auth
  api.client.addRequestInterceptor( new HttpRequestInterceptor() {
    void process(HttpRequest httpRequest, HttpContext httpContext) {
      String auth = "admin:${rest_upload_pass}"
      String enc_auth = auth.bytes.encodeBase64().toString()
        httpRequest.addHeader('Authorization', 'Basic ' + enc_auth);
      }
    })


  def input_stream = new File(filename).newInputStream()

  // HSSFWorkbook wb = new HSSFWorkbook(input_stream);
  XSSFWorkbook wb = new XSSFWorkbook(input_stream);
  // HSSFSheet firstSheet = wb.getSheetAt(0);
  XSSFSheet firstSheet = wb.getSheetAt(0);

  // Step 1 - Extract institution id, name and shortcode
  // HSSFRow org_details_row = firstSheet.getRow(0)
  // String org_name = org_details_row?.getCell(1)?.toString()

  for (int rownum=1;((rownum<firstSheet.getLastRowNum())); rownum++) {
    // HSSFRow title_row = firstSheet.getRow(rownum)
    XSSFRow title_row = firstSheet.getRow(rownum)
    int col=0
    println("Cell type of isxn is ${title_row.getCell(1).getCellType()}");
    def title=getStrValue(title_row.getCell(col++))
    def isxn = getStrValue(title_row.getCell(col++))
    def type=title_row.getCell(col++)?.toString()
    def status=title_row.getCell(col++)?.toString()
    def default_Dates=title_row.getCell(col++)?.toString()
    def custom_Date_From=title_row.getCell(col++)?.toString()
    def custom_Date_To=title_row.getCell(col++)?.toString()
    def title_Id=title_row.getCell(col++)?.getNumericCellValue().longValue()
    def publication_Date=getStrValue(title_row.getCell(col++)) // ?.getNumericCellValue()?.longValue()
    def edition=title_row.getCell(col++)?.toString()
    def publisher=title_row.getCell(col++)?.toString()
    def public_Note=title_row.getCell(col++)?.toString()
    def display_Public_Note=title_row.getCell(col++)?.toString()
    def location_Note=title_row.getCell(col++)?.toString()
    def display_Location_Note=title_row.getCell(col++)?.toString()
    def default_URL=title_row.getCell(col++)?.toString()
    def custom_URL=title_row.getCell(col++)?.toString()
    def display_In_360_MARC_Updates=title_row.getCell(col++)?.toString()
    def display_In_360_Link=title_row.getCell(col++)?.toString()
    def display_In_360_Core=title_row.getCell(col++)?.toString()
    def display_In_Summon=title_row.getCell(col++)?.toString()

    def assertion = [
      title:title,
      titleIdentifiers:[
        (type=='BOOK'?'ISBN':'ISSN'):isxn
      ],
      platformIdentifiers:[
        'DawsonTitleId':title_Id
      ],
      publisher:publisher,
      publication_date:publication_Date,
      edition:edition,
      platformName:'Dawson',
      paltformId:'Dawson',
      platformUrl:default_URL,
      additionalUrl:custom_URL
    ]

    api.request(POST) { request ->
      def record = prettyPrint(toJson(assertion))
      requestContentType = 'multipart/form-data'
      uri.path="/eba/api/assertTitle"
      def multipart_entity = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE);
      def uploaded_file_body_part = new org.apache.http.entity.mime.content.ByteArrayBody(record.getBytes('UTF8'), 'application/json', "record.json");
      multipart_entity.addPart("tf", uploaded_file_body_part);

      request.entity = multipart_entity

      response.success = { resp, data ->
        println("OK - Record uploaded");
      }

      response.failure = { resp ->
        println("Error - ${resp.status}");
        System.out << resp
        println("Done\n\n");
      }
    }


    println("assertion: ${assertion}");
  }

  input_stream.close();
}

def getStrValue(cell) {
  def result=null
  if ( cell != null ) {
    if ( cell.getCellType() == 0 ) {
      Double d = cell.getNumericCellValue()
      if ( d != null ) {
        result = "${d?.longValue()}"
      }
    }
    else {
      result = cell.toString()
    }
  }
  result
}


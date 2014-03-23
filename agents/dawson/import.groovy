@Grapes([
    @Grab(group='net.sourceforge.nekohtml', module='nekohtml', version='1.9.14'),
    @Grab(group='org.codehaus.groovy.modules.http-builder', module='http-builder', version='0.5.1'),
    @Grab(group='xerces', module='xercesImpl', version='2.9.1'),
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


println("Loading ${args[0]}");
loadWorkbook(args[0])

def loadWorkbook(filename) {
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
    def title=title_row.getCell(col++)?.toString()
    def ixsn = null;
    if ( title_row.getCell(1).getCellType() == 0 ) {
      Double d = title_row.getCell(col++)?.getNumericCellValue()
      isxn="${d.longValue()}"
    }
    else {
      isxn=title_row.getCell(col++)?.toString()
    }
    def type=title_row.getCell(col++)?.toString()
    def status=title_row.getCell(col++)?.toString()
    def default_Dates=title_row.getCell(col++)?.toString()
    def custom_Date_From=title_row.getCell(col++)?.toString()
    def custom_Date_To=title_row.getCell(col++)?.toString()
    def title_Id=title_row.getCell(col++)?.toString()
    def publication_Date=title_row.getCell(col++)?.toString()
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

    println("assertion: ${assertion}");
  }

  input_stream.close();
}

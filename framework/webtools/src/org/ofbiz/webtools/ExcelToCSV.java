/*********************************************************************
*
*      Copyright (C) 2002 Andrew Khan
*
* This library is free software; you can redistribute it and/or
* modify it under the terms of the GNU Lesser General Public
* License as published by the Free Software Foundation; either
* version 2.1 of the License, or (at your option) any later version.
*
* This library is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
* Lesser General Public License for more details.
*
* You should have received a copy of the GNU Lesser General Public
* License along with this library; if not, write to the Free Software
* Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
***************************************************************************/

package org.ofbiz.webtools;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;

import org.ofbiz.base.util.Debug;
import org.ofbiz.base.util.UtilValidate;

import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;



import org.ofbiz.base.util.FileUtil;

public class ExcelToCSV{
  /**
   * Constructor
   *
   * @param w The workbook to interrogate
   * @param out The output stream to which the CSV values are written
   * @param encoding The encoding used by the output stream.  Null or 
   * unrecognized values cause the encoding to default to UTF8
   * @param hide Suppresses hidden cells
   * @exception java.io.IOException
   */
  public ExcelToCSV(Workbook w, OutputStream out, String encoding, boolean hide , String basePath)    throws IOException  {
    if (encoding == null || !encoding.equals("UnicodeBig")) {
      encoding = "UTF8";
    }
    if(UtilValidate.isEmpty(basePath)){
    	basePath = System.getProperty("ofbiz.home");
    }
    try{
      OutputStreamWriter osw = new OutputStreamWriter(out, encoding);
      BufferedWriter bwOut = new BufferedWriter(osw);
      
      for (int sheet = 0; sheet < w.getNumberOfSheets(); sheet++){
        Sheet s = w.getSheet(sheet);
        if(s.getRows() <= 0){
        	continue;
        }
        String fileName =basePath+"/"+s.getName()+".csv";
       File file=  new File(fileName);
       if (!file.exists()) {
			file.createNewFile();
		}
		FileWriter fw = new FileWriter(file.getAbsoluteFile());
        BufferedWriter bw =  new BufferedWriter(fw);
        //BufferedWriter bw = FileUtil.getBufferedWriter(basePath ,fileName);
        if (!(hide && s.getSettings().isHidden())){
          //bw.write("*** " + s.getName() + " ****");
          //bw.newLine();
          
          Cell[] row = null;
          Cell[] firstRow =  s.getRow(0);
          for (int i = 0 ; i < s.getRows() ; i++){
            row = s.getRow(i);
            if (row.length > 0){
              if (!(hide && row[0].isHidden())){
               // bw.write(row[0].getContents());
                // Java 1.4 code to handle embedded commas
            	if((row[0].getContents()).contains(",")){
            		 bw.write("\"" + row[0].getContents().replaceAll("\"","\"\"") + "\"");
            		 
            	}else{
            		bw.write(row[0].getContents());
            	}
               
              }
              for (int j = 1; j < firstRow.length; j++) {
                bw.write(',');
                if(j< row.length){
                	if (!(hide && row[j].isHidden())){
                        //bw.write(row[j].getContents());
                        // Java 1.4 code to handle embedded quotes
                      	if((row[j].getContents()).contains(",")){
                      		bw.write("\"" + row[j].getContents().replaceAll("\"","\"\"") + "\"");
                      	}else{
                      		bw.write(row[j].getContents());
                      	}
                        
                      }
                }
                
              }
            }
            bw.newLine();
          }
          
          bw.flush();
          bw.close();
        }
        
      }
      bwOut.flush();
      bwOut.close();
    }
    catch (UnsupportedEncodingException e)    {
      System.err.println(e.toString());
    }
  }
}



<#escape x as x?xml>
    <fo:root xmlns:fo="http://www.w3.org/1999/XSL/Format">
        <fo:layout-master-set>
            <fo:simple-page-master master-name="main" page-height="10in" page-width="10in"
                     margin-left="0.1in" margin-right="0.2in"  margin-top="0.2in" margin-bottom="0.2in" >
                <fo:region-body margin-top="1.8in"/>
                <fo:region-before extent="1in"/>
                <fo:region-after extent="1in"/>
            </fo:simple-page-master>
        </fo:layout-master-set>
        ${setRequestAttribute("OUTPUT_FILENAME", "PayrollExceptionReport.pdf")}
        <#if payrollDetailsMap?has_content>
			<fo:page-sequence master-reference="main">
        		<fo:static-content font-size="12pt" font-family="Courier,monospace"  flow-name="xsl-region-before" font-weight="bold">        
	        		<fo:block  keep-together="always" text-align="center" font-family="Courier,monospace" white-space-collapse="false" font-weight="bold">&#160;      ${uiLabelMap.KMFDairyHeader}</fo:block>
					<fo:block  keep-together="always" text-align="center" font-family="Courier,monospace" white-space-collapse="false" font-weight="bold">&#160;      ${uiLabelMap.KMFDairySubHeader}</fo:block>
	        		<fo:block  keep-together="always" text-align="center" font-family="Courier,monospace" white-space-collapse="false" font-weight="bold">&#160;      </fo:block>
	        		<fo:block text-align="center" keep-together="always" white-space-collapse="false">&#160;    PAYROLL EXCEPTION REPORT      </fo:block>	 
	        		<fo:block text-align="left" keep-together="always" white-space-collapse="false">&#160;                                                                          DATE: ${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(nowTimestamp, "dd-MMM-yyyy")}</fo:block>	 
	        		<fo:block text-align="left" keep-together="always" white-space-collapse="false">&#160;                                                                          PAGE: <fo:page-number/></fo:block>	 	 	  	 	  
	        		<fo:block text-align="left" keep-together="always"  >---------------------------------------------------------------------------------------------</fo:block>
	        		<fo:block text-align="left" font-family="Courier,monospace" font-weight="bold" white-space-collapse="false">&#160;   	 			Employee Code 		  											Employee Name   											Payable Days </fo:block>	 
	        		<fo:block text-align="left" keep-together="always"  >---------------------------------------------------------------------------------------------</fo:block>
          		</fo:static-content>
        	<fo:flow flow-name="xsl-region-body" font-family="Courier,monospace">
	            <fo:block font-family="Courier,monospace">
	                <fo:table>
	                	<#assign noofLines=1>
	                    <fo:table-column column-width="250pt"/>
	                    <fo:table-column column-width="250pt"/>
	                    <fo:table-column column-width="100pt"/>
                     	<fo:table-body>
                     	<#assign payrollDetMap=payrollDetailsMap.entrySet()>
                     		<#list payrollDetMap as payroll>
                     		<#assign employeeName=payroll.getValue().entrySet()>
                     		<#list employeeName as empName>
                     			<fo:table-row>
	                           		<fo:table-cell>	
	                            		<fo:block text-align="center" keep-together="always" font-size="15pt">${payroll.getKey()}</fo:block>
	                           		</fo:table-cell>
	                           		<fo:table-cell>	
	                            		<fo:block text-align="left" keep-together="always" font-size="15pt">${empName.getKey()}</fo:block>
	                            	</fo:table-cell>
	                            	<fo:table-cell>	
	                            		<fo:block text-align="left" keep-together="always" font-size="15pt">${empName.getValue()}</fo:block>
	                            	</fo:table-cell>
	                            	<#assign noofLines=noofLines+1>
		                   		</fo:table-row>
		                   		<#if (noofLines == 25)>
	                            	<fo:table-row>
	                            		<fo:table-cell>
	   										<fo:block page-break-after="always" font-weight="bold" font-size="12pt" text-align="center"></fo:block>
	   									</fo:table-cell>
									</fo:table-row>
	                           		<#assign noofLines =1>
								</#if>
								</#list>
		                	</#list>
                           		<fo:table-row>
		                   			<fo:table-cell >	
                            			<fo:block keep-together="always" font-weight="bold">---------------------------------------------------------------------------------------------</fo:block>
                            		</fo:table-cell>
                           		</fo:table-row>
                     	</fo:table-body>
                     </fo:table>
                      <fo:table>
                     	<fo:table-column column-width="650pt"/>
                     		<fo:table-body>
	                     		<fo:table-row >
		                            <fo:table-cell>	
		                            	<fo:block keep-together="always" text-align="center">&#160; This is a system generated report and does not require any signature. </fo:block>
		                            </fo:table-cell>
		                      	</fo:table-row>
                       		</fo:table-body>
                     	</fo:table>
                 </fo:block>
           </fo:flow>
  		</fo:page-sequence>        
        <#else>    	
		<fo:page-sequence master-reference="main">
			<fo:flow flow-name="xsl-region-body" font-family="Courier,monospace">
   		 		<fo:block font-size="14pt">
        			No Employee Found.......!
   		 		</fo:block>
			</fo:flow>
		</fo:page-sequence>	
	</#if>
     </fo:root>
</#escape>

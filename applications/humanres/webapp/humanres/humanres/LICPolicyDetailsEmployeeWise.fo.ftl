<#escape x as x?xml>
    <fo:root xmlns:fo="http://www.w3.org/1999/XSL/Format">
        <fo:layout-master-set>
            <fo:simple-page-master master-name="main" page-height="10in" page-width="10in"
                     margin-left="0.1in" margin-right="0.2in"  margin-top="0.2in" margin-bottom="0.2in" >
                <fo:region-body margin-top="1.4in"/>
                <fo:region-before extent="1in"/>
                <fo:region-after extent="1in"/>
            </fo:simple-page-master>
        </fo:layout-master-set>
        ${setRequestAttribute("OUTPUT_FILENAME", "LICPolicyDetailsEmployeeWise.pdf")}
		<#if (parameters.InsuranceType)==("MD LIC")>
        	<#if mdLicFinalMap?has_content>
			<fo:page-sequence master-reference="main">
        		<fo:static-content font-size="12pt" font-family="Courier,monospace"  flow-name="xsl-region-before" font-weight="bold">        
	        		<fo:block  keep-together="always" text-align="center" font-family="Courier,monospace" white-space-collapse="false" font-weight="bold">&#160;      ${uiLabelMap.KMFDairyHeader}</fo:block>
					<fo:block  keep-together="always" text-align="center" font-family="Courier,monospace" white-space-collapse="false" font-weight="bold">&#160;      ${uiLabelMap.KMFDairySubHeader}</fo:block>
	        		<fo:block  keep-together="always" text-align="center" font-family="Courier,monospace" white-space-collapse="false" font-weight="bold">&#160;      </fo:block>
	        		<fo:block text-align="left" keep-together="always" white-space-collapse="false">&#160;                          LIC STATEMENT OF KMF FOR THE MONTH OF ${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(fromDate, "MMMM yyyy")}         </fo:block>	 
	        		<fo:block text-align="left" keep-together="always" white-space-collapse="false">&#160;                                                                          DATE: ${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(nowTimestamp, "dd-MMM-yyyy")}</fo:block>	 
	        		<fo:block text-align="left" keep-together="always" white-space-collapse="false">&#160;                                                                          PAGE: <fo:page-number/></fo:block>	 	 	  	 	  
	        		<fo:block text-align="left" keep-together="always"  >------------------------------------------------------------------------------------------------</fo:block>
          		</fo:static-content>
        		<fo:flow flow-name="xsl-region-body" font-family="Courier,monospace">
	            	<fo:block font-family="Courier,monospace">
	                	<fo:table >
	                		<#assign sNo=1>
	                		<#assign NoofLines=1>
	                		<#assign GrandTot=0>
	                		<#assign pageTot=0>
		                    <fo:table-column column-width="120pt"/>
		                    <fo:table-column column-width="100pt"/>
		                    <fo:table-column column-width="200pt"/>                
		                    <fo:table-column column-width="110pt"/>
		                    <fo:table-column column-width="110pt"/>
	                     	<fo:table-body> 
	                     		<fo:table-row >
		                           	<fo:table-cell >	
		                            	<fo:block text-align="center" keep-together="always" font-weight="bold" font-size="15pt">SLNo</fo:block>
		                            </fo:table-cell>
		                            <fo:table-cell >	
		                            	<fo:block text-align="left" keep-together="always" font-weight="bold" font-size="15pt">Emp No</fo:block>
		                            </fo:table-cell>
		                            <fo:table-cell >	
		                            	<fo:block text-align="left" keep-together="always" font-weight="bold" font-size="15pt">Name</fo:block>
		                            </fo:table-cell>
		                            <fo:table-cell >	
		                            	<fo:block text-align="left" keep-together="always" font-weight="bold" font-size="15pt">Ref No</fo:block>
		                            </fo:table-cell>
		                            <fo:table-cell >	
		                            	<fo:block text-align="left" keep-together="always" font-weight="bold" font-size="15pt">Amount(Rs)</fo:block>
		                            </fo:table-cell>
			                   	</fo:table-row>
			                   	
			                   		<fo:table-row >
			                   		<fo:table-cell >	
	                            		<fo:block keep-together="always" font-weight="bold">-------------------------------------------------------------------------------------------------</fo:block>
	                            	</fo:table-cell>
	                            </fo:table-row>
			                   	<#assign mdLicMap=mdLicFinalMap.entrySet()>
			                   	<#list mdLicMap as mdLicValues>
			                   		<#assign MdValues=mdLicValues.getValue()>
			                   		<fo:table-row >
			                   			<fo:table-cell >	
		                            		<fo:block text-align="center" keep-together="always" font-size="13pt">${sNo}</fo:block>
		                            	</fo:table-cell>
			                   			<fo:table-cell >	
		                            		<fo:block text-align="left" keep-together="always" font-size="13pt">${MdValues.get("employeeNo")}</fo:block>
		                            	</fo:table-cell>
		                            	<fo:table-cell >	
		                            		<fo:block text-align="left" keep-together="always" font-size="13pt">${MdValues.get("employeeName")}</fo:block>
		                            	</fo:table-cell>
		                            	<fo:table-cell >	
		                            		<fo:block text-align="left" keep-together="always" font-size="13pt">${MdValues.get("referenceNo")}</fo:block>
		                            	</fo:table-cell>
		                            	<fo:table-cell >	
		                            		<fo:block text-align="left" keep-together="always" font-size="13pt">${MdValues.get("amount")}</fo:block>
		                            		<#assign GrandTot=GrandTot+MdValues.get("amount")>
		                            		<#assign pageTot=pageTot+MdValues.get("amount")>
		                            	</fo:table-cell>
		                            	<#assign sNo=sNo+1>
		                            	<#assign NoofLines=NoofLines+1>
		                            </fo:table-row>
		                            <#if (NoofLines == 31) >
		                            	<fo:table-row>
		                            		<fo:table-cell>
		   										<fo:block page-break-after="always" font-weight="bold" font-size="12pt" text-align="center">Page Total:</fo:block>
		   									</fo:table-cell>
		   									<fo:table-cell >	
			                            		<fo:block text-align="left" keep-together="always" font-weight="bold"></fo:block>
			                            	</fo:table-cell>
			                            	<fo:table-cell >	
			                            		<fo:block text-align="left" keep-together="always" font-weight="bold"></fo:block>
			                            	</fo:table-cell>
			                            	<fo:table-cell >	
			                            		<fo:block text-align="left" keep-together="always" font-weight="bold"></fo:block>
			                            	</fo:table-cell>
		   									<fo:table-cell>
		   										<fo:block page-break-after="always" font-weight="bold">${pageTot}</fo:block>
		   									</fo:table-cell>
		   									<#assign pageTot=0>
										</fo:table-row>
		                           		<#assign NoofLines =1>
									</#if>
			                   	</#list>
			                   	<fo:table-row >
			                   		<fo:table-cell >	
	                            		<fo:block keep-together="always" font-weight="bold">-------------------------------------------------------------------------------------------------</fo:block>
	                            	</fo:table-cell>
	                            </fo:table-row>
			                   	<fo:table-row >
		                   			<fo:table-cell >	
	                            		<fo:block text-align="center" keep-together="always" font-weight="bold" font-size="12pt">Grand Total:</fo:block>
	                            	</fo:table-cell>
	                            	<fo:table-cell >	
	                            		<fo:block text-align="left" keep-together="always" font-weight="bold"></fo:block>
	                            	</fo:table-cell>
	                            	<fo:table-cell >	
	                            		<fo:block text-align="left" keep-together="always" font-weight="bold"></fo:block>
	                            	</fo:table-cell>
	                            	<fo:table-cell >	
	                            		<fo:block text-align="left" keep-together="always" font-weight="bold"></fo:block>
	                            	</fo:table-cell>
	                            	<fo:table-cell >	
	                            		<fo:block text-align="left" keep-together="always" font-weight="bold" font-size="12pt">${GrandTot}</fo:block>
	                            	</fo:table-cell>
	                           </fo:table-row>
	                           <fo:table-row >
			                   		<fo:table-cell >	
	                            		<fo:block keep-together="always" font-weight="bold">--------------------------------------------------------------------------------------------------</fo:block>
	                            	</fo:table-cell>
	                            </fo:table-row>
                     	</fo:table-body>
                     </fo:table>
                 </fo:block>
           </fo:flow>
  		</fo:page-sequence>
  		</#if>
  	</#if>
  	<#if (parameters.InsuranceType)==("Mother Dairy LIC")>
  		<#if dairyLicFinalMap?has_content>
  			<fo:page-sequence master-reference="main">
        		<fo:static-content font-size="12pt" font-family="Courier,monospace"  flow-name="xsl-region-before" font-weight="bold">        
	        		<fo:block  keep-together="always" text-align="center" font-family="Courier,monospace" white-space-collapse="false" font-weight="bold">&#160;      ${uiLabelMap.KMFDairyHeader}</fo:block>
					<fo:block  keep-together="always" text-align="center" font-family="Courier,monospace" white-space-collapse="false" font-weight="bold">&#160;      ${uiLabelMap.KMFDairySubHeader}</fo:block>
	        		<fo:block  keep-together="always" text-align="center" font-family="Courier,monospace" white-space-collapse="false" font-weight="bold">&#160;      </fo:block>
	        		<fo:block text-align="left" keep-together="always" white-space-collapse="false">&#160;                      LIC STATEMENT OF MOTHER DAIRY OF FOR THE MONTH OF ${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(fromDate, "MMMM yyyy")}       </fo:block>	 	 	  
	        		<fo:block text-align="left" keep-together="always" white-space-collapse="false">&#160;                                                                          DATE: ${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(nowTimestamp, "dd-MMM-yyyy")}</fo:block>	 
	        		<fo:block text-align="left" keep-together="always" white-space-collapse="false">&#160;                                                                          PAGE: <fo:page-number/></fo:block>	 	 	  	 	  
	        		<fo:block text-align="left" keep-together="always"  >--------------------------------------------------------------------------------------------------------------------------------------------------------------------------</fo:block>
          		</fo:static-content>
        		<fo:flow flow-name="xsl-region-body" font-family="Courier,monospace">
	            	<fo:block font-family="Courier,monospace">
	                	<fo:table >
	                		<#assign sNo=1>
	                		<#assign NoofLines=1>
	                		<#assign GrandTot=0>
	                		<#assign pageTot=0>
		                    <fo:table-column column-width="120pt"/>
		                    <fo:table-column column-width="100pt"/>
		                    <fo:table-column column-width="200pt"/>                
		                    <fo:table-column column-width="110pt"/>
		                    <fo:table-column column-width="110pt"/>
	                     	<fo:table-body> 
	                     		<fo:table-row >
		                           	<fo:table-cell >	
		                            	<fo:block text-align="center" keep-together="always" font-weight="bold" font-size="15pt">SLNo</fo:block>
		                            </fo:table-cell>
		                            <fo:table-cell >	
		                            	<fo:block text-align="left" keep-together="always" font-weight="bold" font-size="15pt">Emp No</fo:block>
		                            </fo:table-cell>
		                            <fo:table-cell >	
		                            	<fo:block text-align="left" keep-together="always" font-weight="bold" font-size="15pt">Name</fo:block>
		                            </fo:table-cell>
		                            <fo:table-cell >	
		                            	<fo:block text-align="left" keep-together="always" font-weight="bold" font-size="15pt">Ref No</fo:block>
		                            </fo:table-cell>
		                            <fo:table-cell >	
		                            	<fo:block text-align="left" keep-together="always" font-weight="bold" font-size="15pt">Amount(Rs)</fo:block>
		                            </fo:table-cell>
			                   	</fo:table-row>
			                   	<#assign dairyLicMap=dairyLicFinalMap.entrySet()>
			                   	<#list dairyLicMap as dairyLicValues>
			                   		<#assign dairyValues=dairyLicValues.getValue()>
			                   		<fo:table-row >
			                   			<fo:table-cell >	
		                            		<fo:block text-align="center" keep-together="always" font-size="13pt">${sNo}</fo:block>
		                            	</fo:table-cell>
			                   			<fo:table-cell >	
		                            		<fo:block text-align="left" keep-together="always" font-size="13pt">${dairyValues.get("employeeNo")}</fo:block>
		                            	</fo:table-cell>
		                            	<fo:table-cell >	
		                            		<fo:block text-align="left" keep-together="always" font-size="13pt">${dairyValues.get("employeeName")}</fo:block>
		                            	</fo:table-cell>
		                            	<fo:table-cell >	
		                            		<fo:block text-align="left" keep-together="always" font-size="13pt">${dairyValues.get("referenceNo")}</fo:block>
		                            	</fo:table-cell>
		                            	<fo:table-cell >	
		                            		<fo:block text-align="left" keep-together="always" font-size="13pt">${dairyValues.get("amount")}</fo:block>
		                            		<#assign GrandTot=GrandTot+dairyValues.get("amount")>
		                            		<#assign pageTot=pageTot+dairyValues.get("amount")>
		                            	</fo:table-cell>
		                            	<#assign sNo=sNo+1>
		                            	<#assign NoofLines=NoofLines+1>
		                            </fo:table-row>
		                            <#if (NoofLines == 31) >
		                            	<fo:table-row>
		                            		<fo:table-cell>
		   										<fo:block page-break-after="always" font-weight="bold" font-size="12pt" text-align="center">Page Total:</fo:block>
		   									</fo:table-cell>
		   									<fo:table-cell >	
			                            		<fo:block text-align="left" keep-together="always" font-weight="bold"></fo:block>
			                            	</fo:table-cell>
			                            	<fo:table-cell >	
			                            		<fo:block text-align="left" keep-together="always" font-weight="bold"></fo:block>
			                            	</fo:table-cell>
			                            	<fo:table-cell >	
			                            		<fo:block text-align="left" keep-together="always" font-weight="bold"></fo:block>
			                            	</fo:table-cell>
		   									<fo:table-cell>
		   										<fo:block page-break-after="always" font-weight="bold">${pageTot}</fo:block>
		   									</fo:table-cell>
		   									<#assign pageTot=0>
										</fo:table-row>
		                           		<#assign NoofLines =1>
									</#if>
			                   	</#list>
			                   	<fo:table-row >
			                   		<fo:table-cell >	
	                            		<fo:block keep-together="always" font-weight="bold">-------------------------------------------------------------------------------------------------</fo:block>
	                            	</fo:table-cell>
	                            </fo:table-row>
			                   	<fo:table-row >
		                   			<fo:table-cell >	
	                            		<fo:block text-align="center" keep-together="always" font-weight="bold" font-size="12pt">Grand Total:</fo:block>
	                            	</fo:table-cell>
	                            	<fo:table-cell >	
	                            		<fo:block text-align="left" keep-together="always" font-weight="bold"></fo:block>
	                            	</fo:table-cell>
	                            	<fo:table-cell >	
	                            		<fo:block text-align="left" keep-together="always" font-weight="bold"></fo:block>
	                            	</fo:table-cell>
	                            	<fo:table-cell >	
	                            		<fo:block text-align="left" keep-together="always" font-weight="bold"></fo:block>
	                            	</fo:table-cell>
	                            	<fo:table-cell >	
	                            		<fo:block text-align="left" keep-together="always" font-weight="bold" font-size="12pt">${GrandTot}</fo:block>
	                            	</fo:table-cell>
	                           </fo:table-row>
	                           <fo:table-row >
			                   		<fo:table-cell >	
	                            		<fo:block keep-together="always" font-weight="bold">-------------------------------------------------------------------------------------------------</fo:block>
	                            	</fo:table-cell>
	                            </fo:table-row>
                     	</fo:table-body>
                     </fo:table>
                 </fo:block>
           </fo:flow>
  		</fo:page-sequence>
  		</#if>
  	</#if>
     </fo:root>
</#escape>
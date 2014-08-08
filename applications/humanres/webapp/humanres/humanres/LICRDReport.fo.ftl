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
		
		<#if LicFinalMap?has_content>
			<fo:page-sequence master-reference="main">
        		<fo:static-content font-size="12pt" font-family="Courier,monospace"  flow-name="xsl-region-before" font-weight="bold">        
	        		<fo:block  keep-together="always" text-align="center" font-family="Courier,monospace" white-space-collapse="false" font-weight="bold">&#160;      ${uiLabelMap.KMFDairyHeader}</fo:block>
					<fo:block  keep-together="always" text-align="center" font-family="Courier,monospace" white-space-collapse="false" font-weight="bold">&#160;      ${uiLabelMap.KMFDairySubHeader}</fo:block>
	        		<fo:block  keep-together="always" text-align="center" font-family="Courier,monospace" white-space-collapse="false" font-weight="bold">&#160;      </fo:block>
	        		<#if (parameters.InsuranceType)==("LIC_MD_INSR")>
	        		<fo:block text-align="left" keep-together="always" white-space-collapse="false">&#160;                          LIC STATEMENT OF KMF FOR THE MONTH OF ${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(fromDate, "MMMM yyyy")}         </fo:block>	 
	        		</#if>
	        		<#if (parameters.InsuranceType)==("LIC_DAIRY_INSR")>
	        		<fo:block text-align="left" keep-together="always" white-space-collapse="false">&#160;                      LIC STATEMENT OF MOTHER DAIRY OF FOR THE MONTH OF ${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(fromDate, "MMMM yyyy")}       </fo:block>	 	 	  
	        		</#if>
	        		<#if (parameters.InsuranceType)==("RECCR_DEPOSIT")>
	        		<fo:block text-align="center" keep-together="always" white-space-collapse="false">&#160;     RDP CUMULATIVE STATEMENT FOR THE MONTH OF ${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(fromDate, "MMMM yyyy")}                      </fo:block>	 
	        		</#if>
	        		<fo:block text-align="left" keep-together="always" white-space-collapse="false">&#160;                                                                          DATE: ${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(nowTimestamp, "dd-MMM-yyyy")}</fo:block>	 
	        		<fo:block text-align="left" keep-together="always" white-space-collapse="false">&#160;                                                                          PAGE: <fo:page-number/></fo:block>	 	 	  	 	  
	        		<fo:block text-align="left" keep-together="always"  >---------------------------------------------------------------------------------------------</fo:block>
          		</fo:static-content>
        	<fo:flow flow-name="xsl-region-body" font-family="Courier,monospace">
	            <fo:block font-family="Courier,monospace">
	                <fo:table >
                		<#assign sNo=1>
                		<#assign noofLines=1>
                		<#assign GrandTot=0>
                		<#assign pageTot=0>
	                    <fo:table-column column-width="120pt"/>
	                    <fo:table-column column-width="100pt"/>
	                    <fo:table-column column-width="200pt"/>                
	                    <fo:table-column column-width="50pt"/>
	                    <fo:table-column column-width="100pt"/>
	                     <fo:table-column column-width="100pt"/>
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
	                            	<fo:block text-align="right" keep-together="always" font-weight="bold" font-size="15pt">&#160; Amount(Rs)</fo:block>
	                            </fo:table-cell>
	                            <#if (parameters.InsuranceType)==("RECCR_DEPOSIT")>
	                            <fo:table-cell >	
	                            	<fo:block text-align="right" keep-together="always" font-weight="bold" font-size="15pt">Cumulative</fo:block>
	                            </fo:table-cell>
	                            </#if>
		                   	</fo:table-row>
		                   	 <#if (parameters.InsuranceType)==("RECCR_DEPOSIT")>
		                   	<fo:table-row>
		                   		<fo:table-cell >	
	                            	<fo:block text-align="center" keep-together="always" font-weight="bold" font-size="15pt"></fo:block>
	                            </fo:table-cell>
	                            <fo:table-cell >	
	                            	<fo:block text-align="left" keep-together="always" font-weight="bold" font-size="15pt"></fo:block>
	                            </fo:table-cell>
	                            <fo:table-cell >	
	                            	<fo:block text-align="left" keep-together="always" font-weight="bold" font-size="15pt"></fo:block>
	                            </fo:table-cell>
	                            <fo:table-cell >	
	                            	<fo:block text-align="left" keep-together="always" font-weight="bold" font-size="15pt"></fo:block>
	                            </fo:table-cell>
	                            <fo:table-cell >	
	                            	<fo:block text-align="right" keep-together="always" font-weight="bold" font-size="15pt"></fo:block>
	                            </fo:table-cell>
	                            <fo:table-cell >	
	                            	<fo:block text-align="right" keep-together="always" font-weight="bold" font-size="15pt">Amount(Rs)</fo:block>
	                            </fo:table-cell>
		                   	</fo:table-row>
		                   	 </#if>
		                   	<fo:table-row >
		                   		<fo:table-cell >	
                            		<fo:block keep-together="always" font-weight="bold">---------------------------------------------------------------------------------------------</fo:block>
                            	</fo:table-cell>
                            </fo:table-row>
		                   	<#assign LicMap=LicFinalMap.entrySet()>
		                   	<#list LicMap as LicValues>
		                   		<#assign LicInsuranceValues=LicValues.getValue()>
		                   		<fo:table-row >
		                   			<fo:table-cell >	
	                            		<fo:block text-align="center" keep-together="always" font-size="13pt">${sNo}</fo:block>
	                            	</fo:table-cell>
		                   			<fo:table-cell >	
	                            		<fo:block text-align="left" keep-together="always" font-size="13pt">${LicInsuranceValues.get("employeeNo")?if_exists}</fo:block>
	                            	</fo:table-cell>
	                            	<fo:table-cell >	
	                            		<fo:block text-align="left" keep-together="always" font-size="13pt">${LicInsuranceValues.get("employeeName")?if_exists}</fo:block>
	                            	</fo:table-cell>
	                            	<fo:table-cell >	
	                            		<fo:block text-align="right" keep-together="always" font-size="13pt">${LicInsuranceValues.get("referenceNo")?if_exists}</fo:block>
	                            	</fo:table-cell>
	                            	<fo:table-cell >	
	                            		<fo:block text-align="right" keep-together="always" font-size="13pt">${LicInsuranceValues.get("amount")?if_exists?string("##0.00")}</fo:block>
	                            		<#assign GrandTot=GrandTot+LicInsuranceValues.get("amount")>
	                            		<#assign pageTot=pageTot+LicInsuranceValues.get("amount")>
	                            	</fo:table-cell>
	                            	<#if (parameters.InsuranceType)==("RECCR_DEPOSIT")>
	                            		<#assign cumltvMap=finalcumulativeMap.entrySet()>
	                            		<#list cumltvMap as cumulativeDetails>
	                            			<#if (cumulativeDetails.getKey())==(LicInsuranceValues.get("employeeNo"))>
			                            		<fo:table-cell >	
			                            			<fo:block text-align="right" keep-together="always" font-size="13pt">${cumulativeDetails.getValue().get(LicInsuranceValues.get("employeeNo"))?if_exists?string("##0.00")}</fo:block>
			                            		</fo:table-cell>
			                            	</#if>
		                            	</#list>
	                            	</#if>
	                            	<#assign sNo=sNo+1>
	                            	<#assign noofLines=noofLines+1>
	                            </fo:table-row>
	                            <#if (noofLines == 31) >
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
	   										<fo:block text-align="right" page-break-after="always" font-weight="bold">${pageTot?if_exists?string("##0.00")}</fo:block>
	   									</fo:table-cell>
	   									<#assign pageTot=0>
									</fo:table-row>
	                           		<#assign noofLines =1>
								</#if>
		                   	</#list>
		                   	<fo:table-row >
		                   		<fo:table-cell >	
                            		<fo:block keep-together="always" font-weight="bold">---------------------------------------------------------------------------------------------</fo:block>
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
                            		<fo:block text-align="right" keep-together="always" font-weight="bold" font-size="12pt">${GrandTot?if_exists?string("##0.00")}</fo:block>
                            	</fo:table-cell>
                           </fo:table-row>
                           <fo:table-row >
		                   		<fo:table-cell >	
                            		<fo:block keep-together="always" font-weight="bold">---------------------------------------------------------------------------------------------</fo:block>
                            	</fo:table-cell>
                            </fo:table-row>
                     	</fo:table-body>
                     </fo:table>
                     <fo:table>
                     	<fo:table-column column-width="650pt"/>
                     	<fo:table-body>
                     		 <#if (parameters.InsuranceType)==("RECCR_DEPOSIT")>
	                     		<fo:table-row >
		                            <fo:table-cell>	
		                            	<fo:block keep-together="always" text-align="center">&#160; This is a system generated report and does not require any signature. </fo:block>
		                            </fo:table-cell>
		                      	</fo:table-row>
		                      </#if> 
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
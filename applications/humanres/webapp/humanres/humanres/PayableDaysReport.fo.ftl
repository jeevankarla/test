<#escape x as x?xml>
    <fo:root xmlns:fo="http://www.w3.org/1999/XSL/Format">
        <fo:layout-master-set>
            <fo:simple-page-master master-name="main" page-height="11in" page-width="10in"
                     margin-left="0.1in" margin-right="0.2in"  margin-top="0.2in" margin-bottom="0.2in" >
                <fo:region-body margin-top="1.8in"/>
                <fo:region-before extent="1in"/>
                <fo:region-after extent="1in"/>
            </fo:simple-page-master>
        </fo:layout-master-set>
		<#if PayableDaysMap?has_content>
			<#assign noofLines=1>
			<#assign SNo=1>
			<fo:page-sequence master-reference="main">
        		<fo:static-content font-size="14pt" font-family="Courier,monospace"  flow-name="xsl-region-before" font-weight="bold">        
        			<#assign reportHeader = delegator.findOne("TenantConfiguration", {"propertyTypeEnumId" : "COMPANY_HEADER","propertyName" : "reportHeaderLable"}, true)>
                    <#assign reportSubHeader = delegator.findOne("TenantConfiguration", {"propertyTypeEnumId" : "COMPANY_HEADER","propertyName" : "reportSubHeaderLable"}, true)>      
	        		<fo:block text-align="center" font-size="13pt" keep-together="always"  white-space-collapse="false" font-weight="bold">&#160;${reportHeader.description?if_exists}</fo:block>
                    <fo:block text-align="center" font-size="12pt" keep-together="always"  white-space-collapse="false" font-weight="bold">&#160;${reportSubHeader.description?if_exists}</fo:block>
	        		<fo:block  keep-together="always" text-align="center" font-family="Courier,monospace" white-space-collapse="false" font-weight="bold">&#160;      </fo:block>
	        		<fo:block text-align="center" keep-together="always" white-space-collapse="false">PAYABLE DAYS REPORT FROM ${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(fromDate, "dd/MM/yyyy")} TO ${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(thruDate, "dd/MM/yyyy")}        </fo:block>	
	        		<fo:block  keep-together="always" text-align="center" font-family="Courier,monospace" white-space-collapse="false" font-weight="bold">&#160;      </fo:block> 
	        		<fo:block text-align="left" keep-together="always" white-space-collapse="false">&#160;                                                                DATE: ${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(nowTimestamp, "dd-MMM-yyyy")}</fo:block>	 
	        		<fo:block text-align="left" keep-together="always" white-space-collapse="false">&#160;                                                                PAGE: <fo:page-number/></fo:block>	 	 	  	 	  
	        		<fo:block text-align="left" keep-together="always" font-size="12pt">---------------------------------------------------------------------------------------------</fo:block>
          		</fo:static-content>
          		<fo:flow flow-name="xsl-region-body" font-family="Courier,monospace">
	            	<fo:block font-family="Courier,monospace">
	                	<fo:table >
		                	<fo:table-column column-width="170pt"/>
		                    <fo:table-column column-width="160pt"/>
		                    <fo:table-column column-width="200pt"/>                
		                    <fo:table-column column-width="80pt"/>
	                		<fo:table-body> 
	                     		<fo:table-row >
		                            <fo:table-cell >	
		                            	<fo:block text-align="center" keep-together="always" font-weight="bold" font-size="15pt">SNo</fo:block>
		                            </fo:table-cell>
		                            <fo:table-cell >	
		                            	<fo:block text-align="left" keep-together="always" font-weight="bold" font-size="15pt">Emp Code</fo:block>
		                            </fo:table-cell>
		                            <fo:table-cell >	
		                            	<fo:block text-align="left" keep-together="always" font-weight="bold" font-size="15pt">Employee Name</fo:block>
		                            </fo:table-cell>
		                            <fo:table-cell >	
		                            	<fo:block text-align="right" keep-together="always" font-weight="bold" font-size="15pt">Payable days</fo:block>
		                            </fo:table-cell>
		                       	</fo:table-row>
		                       	<fo:table-row >
			                   		<fo:table-cell >	
	                            		<fo:block keep-together="always" font-weight="bold">---------------------------------------------------------------------------------------------</fo:block>
	                            	</fo:table-cell>
	                            </fo:table-row>
	                            <#assign payableDays=PayableDaysMap.entrySet()>
			                   	<#list payableDays as payableDaysValues>
			                   		<fo:table-row >
			                   			<fo:table-cell >	
			                            	<fo:block text-align="center" keep-together="always" font-size="15pt">${SNo}</fo:block>
			                            </fo:table-cell>
			                            <fo:table-cell >	
			                            	<fo:block text-align="left" keep-together="always" font-size="15pt">${payableDaysValues.getKey()?if_exists}</fo:block>
			                            </fo:table-cell>
			                            <fo:table-cell >	
			                            	<fo:block text-align="left" keep-together="always" font-size="15pt">${payableDaysValues.getValue().get("employeeFirstName")?if_exists} ${payableDaysValues.getValue().get("employeeMiddleName")?if_exists} ${payableDaysValues.getValue().get("employeeLastName")?if_exists}</fo:block>
			                            </fo:table-cell>
			                            <fo:table-cell >	
			                            	<fo:block text-align="right" keep-together="always" font-size="15pt">${payableDaysValues.getValue().get("PayableDays")?if_exists}</fo:block>
			                            </fo:table-cell>
			                            <#assign SNo=SNo+1>
			                   		</fo:table-row>
			                   		<#assign noofLines=noofLines+1>
			                      	<#if (noofLines >= 31)>
			                     		<#assign noofLines=1>
				                     	<fo:table-row>
			                            	<fo:table-cell >	
			                            		<fo:block page-break-after="always"></fo:block>
			                            	</fo:table-cell>
			                            </fo:table-row>
			                        </#if>
			                   	</#list>
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

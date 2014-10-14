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
		
		<#if finalEmpMap?has_content>
			<#assign noofLines=1>
			<fo:page-sequence master-reference="main">
        		<fo:static-content font-size="14pt" font-family="Courier,monospace"  flow-name="xsl-region-before" font-weight="bold">        
	        		<fo:block  keep-together="always" text-align="center" font-family="Courier,monospace" white-space-collapse="false" font-weight="bold">&#160;      ${uiLabelMap.KMFDairyHeader}</fo:block>
					<fo:block  keep-together="always" text-align="center" font-family="Courier,monospace" white-space-collapse="false" font-weight="bold">&#160;      ${uiLabelMap.KMFDairySubHeader}</fo:block>
	        		<fo:block  keep-together="always" text-align="center" font-family="Courier,monospace" white-space-collapse="false" font-weight="bold">&#160;      </fo:block>
	        		<fo:block text-align="left" keep-together="always" white-space-collapse="false">&#160;                ON OFFICIAL DUTY(OOD) REPORT FROM ${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(fromDate, "dd/MM/yyyy")} TO ${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(thruDate, "dd/MM/yyyy")}        </fo:block>	
	        		<fo:block  keep-together="always" text-align="center" font-family="Courier,monospace" white-space-collapse="false" font-weight="bold">&#160;      </fo:block> 
	        		<fo:block text-align="left" keep-together="always" white-space-collapse="false">&#160;                                                                DATE: ${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(nowTimestamp, "dd-MMM-yyyy")}</fo:block>	 
	        		<fo:block text-align="left" keep-together="always" white-space-collapse="false">&#160;                                                                PAGE: <fo:page-number/></fo:block>	 	 	  	 	  
	        		<fo:block text-align="left" keep-together="always" font-size="12pt">---------------------------------------------------------------------------------------------</fo:block>
          		</fo:static-content>
          		<fo:flow flow-name="xsl-region-body" font-family="Courier,monospace">
	            	<fo:block font-family="Courier,monospace">
	                	<fo:table >
		                	<fo:table-column column-width="80pt"/>
		                    <fo:table-column column-width="220pt"/>
		                    <fo:table-column column-width="110pt"/>                
		                    <fo:table-column column-width="90pt"/>
		                    <fo:table-column column-width="110pt"/>                
		                    <fo:table-column column-width="80pt"/>
	                		<fo:table-body> 
	                     		<fo:table-row >
		                            <fo:table-cell >	
		                            	<fo:block text-align="left" keep-together="always" font-weight="bold" font-size="15pt">Emp No</fo:block>
		                            </fo:table-cell>
		                            <fo:table-cell >	
		                            	<fo:block text-align="left" keep-together="always" font-weight="bold" font-size="15pt">Name</fo:block>
		                            </fo:table-cell>
		                            <fo:table-cell >	
		                            	<fo:block text-align="left" keep-together="always" font-weight="bold" font-size="15pt">FromDate</fo:block>
		                            </fo:table-cell>
		                            <fo:table-cell >	
		                            	<fo:block text-align="left" keep-together="always" font-weight="bold" font-size="15pt">IN TIME</fo:block>
		                            </fo:table-cell>
		                            <fo:table-cell >	
		                            	<fo:block text-align="left" keep-together="always" font-weight="bold" font-size="15pt">ThruDate</fo:block>
		                            </fo:table-cell>
		                            <fo:table-cell >	
		                            	<fo:block text-align="left" keep-together="always" font-weight="bold" font-size="15pt">OUT TIME</fo:block>
		                            </fo:table-cell>
			                   	</fo:table-row>
			                   	<fo:table-row >
			                   		<fo:table-cell >	
	                            		<fo:block keep-together="always" font-weight="bold">---------------------------------------------------------------------------------------------</fo:block>
	                            	</fo:table-cell>
	                            </fo:table-row>
	                            <#assign emplPunchMap=finalEmpMap.entrySet()>
	                   			<#list emplPunchMap as emplOODValues>
	                   				<#assign emplOODMap=emplOODValues.getValue().entrySet()>
	                   				<#list emplOODMap as emplOODMapValues>
	                   					<fo:table-row >
				                            <fo:table-cell >	
				                            	<fo:block text-align="left" keep-together="always" font-size="15pt">${emplOODMapValues.getValue().get("partyId")?if_exists}</fo:block>
				                            </fo:table-cell>
				                            <fo:table-cell >	
				                            	<fo:block text-align="left" keep-together="always" font-size="15pt">${emplOODMapValues.getValue().get("partyName")?if_exists}</fo:block>
				                            </fo:table-cell>
				                            <fo:table-cell >	
				                            	<fo:block text-align="left" keep-together="always" font-size="15pt">${emplOODMapValues.getValue().get("punchindate")?if_exists}</fo:block>
				                            </fo:table-cell>
				                            <fo:table-cell >	
				                            	<fo:block text-align="left" keep-together="always" font-size="15pt">${emplOODMapValues.getValue().get("punchintime")?if_exists?string("HH:mm")}</fo:block>
				                            </fo:table-cell>
				                            <fo:table-cell >	
				                            	<fo:block text-align="left" keep-together="always" font-size="15pt">${emplOODMapValues.getValue().get("punchOutdate")?if_exists}</fo:block>
				                            </fo:table-cell>
				                            <fo:table-cell >	
				                            	<fo:block text-align="left" keep-together="always" font-size="15pt"><#if emplOODMapValues.getValue().get("punchOuttime")?has_content>${emplOODMapValues.getValue().get("punchOuttime")?if_exists?string("HH:mm")}<#else>&#160;</#if></fo:block>
				                            </fo:table-cell>
					                   		<#assign noofLines=noofLines+1>
					                   	</fo:table-row>
					                   	<#if (noofLines >= 31)>
				                     		<#assign noofLines=1>
					                     	<fo:table-row>
				                            	<fo:table-cell >	
				                            		<fo:block page-break-after="always"></fo:block>
				                            	</fo:table-cell>
				                            </fo:table-row>
				                        </#if>
	                   				</#list>
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
		                            <fo:table-cell >	
		                            	<fo:block keep-together="always" text-align="center">&#160; This is a System generated report and does not require any signature. </fo:block>
		                            </fo:table-cell>
		                      	</fo:table-row>
                           </fo:table-body>
                     	</fo:table>
                 	</fo:block>
				</fo:flow>
  			</fo:page-sequence>
  		<#else>
	  		<fo:page-sequence master-reference="main">
		    	<fo:flow flow-name="xsl-region-body" font-family="Helvetica">
		       		 <fo:block font-size="14pt">
		            	${uiLabelMap.NoEmployeeFound}.
		       		 </fo:block>
		    	</fo:flow>
			</fo:page-sequence>
  		</#if>
     </fo:root>
</#escape>
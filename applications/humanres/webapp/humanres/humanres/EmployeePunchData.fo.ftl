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
		<#if EmplPunchinMap?has_content>
			<#assign noofLines=1>
			<fo:page-sequence master-reference="main">
        		<fo:static-content font-size="14pt" font-family="Courier,monospace"  flow-name="xsl-region-before" font-weight="bold">        
	        		<fo:block  keep-together="always" text-align="center" font-family="Courier,monospace" white-space-collapse="false" font-weight="bold">&#160;      ${uiLabelMap.KMFDairyHeader}</fo:block>
					<fo:block  keep-together="always" text-align="center" font-family="Courier,monospace" white-space-collapse="false" font-weight="bold">&#160;      ${uiLabelMap.KMFDairySubHeader}</fo:block>
	        		<fo:block  keep-together="always" text-align="center" font-family="Courier,monospace" white-space-collapse="false" font-weight="bold">&#160;      </fo:block>
	        		<fo:block text-align="left" keep-together="always" white-space-collapse="false">&#160;                EMPLOYEE PUNCH DATA REPORT FROM ${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(fromDate, "dd/MM/yyyy")} TO ${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(thruDate, "dd/MM/yyyy")}        </fo:block>	
	        		<fo:block  keep-together="always" text-align="center" font-family="Courier,monospace" white-space-collapse="false" font-weight="bold">&#160;      </fo:block> 
	        		<fo:block text-align="left" keep-together="always" white-space-collapse="false">&#160;                                                                DATE: ${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(nowTimestamp, "dd-MMM-yyyy")}</fo:block>	 
	        		<fo:block text-align="left" keep-together="always" white-space-collapse="false">&#160;                                                                PAGE: <fo:page-number/></fo:block>	 	 	  	 	  
	        		<fo:block text-align="left" keep-together="always" font-size="12pt">---------------------------------------------------------------------------------------------</fo:block>
          		</fo:static-content>
          		<fo:flow flow-name="xsl-region-body" font-family="Courier,monospace">
	            	<fo:block font-family="Courier,monospace">
	                	<fo:table >
		                	<fo:table-column column-width="120pt"/>
		                    <fo:table-column column-width="100pt"/>
		                    <fo:table-column column-width="220pt"/>                
		                    <fo:table-column column-width="150pt"/>
		                    <fo:table-column column-width="150pt"/>
	                		<fo:table-body> 
	                     		<fo:table-row >
		                            <fo:table-cell >	
		                            	<fo:block text-align="left" keep-together="always" font-weight="bold" font-size="15pt">Dept Code</fo:block>
		                            </fo:table-cell>
		                            <fo:table-cell >	
		                            	<fo:block text-align="left" keep-together="always" font-weight="bold" font-size="15pt">Emp Code</fo:block>
		                            </fo:table-cell>
		                            <fo:table-cell >	
		                            	<fo:block text-align="left" keep-together="always" font-weight="bold" font-size="15pt">Employee Name</fo:block>
		                            </fo:table-cell>
		                            <fo:table-cell >	
		                            	<fo:block text-align="left" keep-together="always" font-weight="bold" font-size="15pt">Punch Date</fo:block>
		                            </fo:table-cell>
		                            <fo:table-cell >	
		                            	<fo:block text-align="left" keep-together="always" font-weight="bold" font-size="15pt">Time</fo:block>
		                            </fo:table-cell>
		                       	</fo:table-row>
		                       	<fo:table-row >
			                   		<fo:table-cell >	
	                            		<fo:block keep-together="always" font-weight="bold">---------------------------------------------------------------------------------------------</fo:block>
	                            	</fo:table-cell>
	                            </fo:table-row>
	                            <#list currentDateKeysList as date>
	                            	<#assign emplPunchMap=EmplPunchinMap.entrySet()>
		                   			<#list emplPunchMap as emplPunchValues>
		                   				<#if (emplPunchValues.getValue().get("date"))==date>
		                   					<fo:table-row >
					                            <fo:table-cell >	
					                            	<fo:block text-align="left" keep-together="always" font-size="15pt">${emplPunchValues.getValue().get("departmentId")?if_exists}</fo:block>
					                            </fo:table-cell>
					                            <fo:table-cell >	
					                            	<fo:block text-align="left" keep-together="always" font-size="15pt">${emplPunchValues.getKey()?if_exists}</fo:block>
					                            </fo:table-cell>
					                            <fo:table-cell >	
					                            	<fo:block text-align="left" keep-together="always" font-size="15pt">${emplPunchValues.getValue().get("partyName")?if_exists}</fo:block>
					                            </fo:table-cell>
					                            <fo:table-cell >	
					                            	<fo:block text-align="left" keep-together="always" font-size="15pt">${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(emplPunchValues.getValue().get("date"), "dd-MMM-yyyy")?if_exists}</fo:block>
					                            </fo:table-cell>
					                            <fo:table-cell >	
					                            	<fo:block text-align="left" keep-together="always" font-size="15pt">${emplPunchValues.getValue().get("inTime")?if_exists?string("HH:mm")}</fo:block>
					                            </fo:table-cell>
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
					                   	</#if>
						               	<#assign emplPunchout=EmplPunchoutMap.entrySet()>
			                   			<#list emplPunchout as emplPunchoutValues>
			                   				<#if (emplPunchoutValues.getValue().get("date"))==date>
			                   					<#if emplPunchoutValues.getKey()==emplPunchValues.getKey()>
			                   						<fo:table-row >
							                            <fo:table-cell >	
							                            	<fo:block text-align="left" keep-together="always" font-size="15pt">${emplPunchoutValues.getValue().get("departmentId")?if_exists}</fo:block>
							                            </fo:table-cell>
							                            <fo:table-cell >	
							                            	<fo:block text-align="left" keep-together="always" font-size="15pt">${emplPunchoutValues.getKey()?if_exists}</fo:block>
							                            </fo:table-cell>
							                            <fo:table-cell >	
							                            	<fo:block text-align="left" keep-together="always" font-size="15pt">${emplPunchoutValues.getValue().get("partyName")?if_exists}</fo:block>
							                            </fo:table-cell>
							                            <fo:table-cell >	
							                            	<fo:block text-align="left" keep-together="always" font-size="15pt">${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(emplPunchoutValues.getValue().get("date"), "dd-MMM-yyyy")}</fo:block>
							                            </fo:table-cell>
							                            <fo:table-cell >	
							                            	<fo:block text-align="left" keep-together="always" font-size="15pt">${emplPunchoutValues.getValue().get("outTime")?if_exists?string("HH:mm")}</fo:block>
							                            </fo:table-cell>
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
			                   					</#if>
			                   				</#if>
			                   			</#list>
			                   		</#list>
					           	</#list>
					           	<fo:table-row >
			                   		<fo:table-cell >	
	                            		<fo:block keep-together="always" font-weight="bold">---------------------------------------------------------------------------------------------</fo:block>
	                            	</fo:table-cell>
	                            </fo:table-row>
                           </fo:table-body>
                     	</fo:table>
                 	</fo:block>
				</fo:flow>
  			</fo:page-sequence>
  		</#if>
     </fo:root>
</#escape>
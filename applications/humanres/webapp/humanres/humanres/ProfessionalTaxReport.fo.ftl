<#escape x as x?xml>
    <fo:root xmlns:fo="http://www.w3.org/1999/XSL/Format">
        <fo:layout-master-set>
            <fo:simple-page-master master-name="main" page-height="11in" page-width="10in"
                     margin-left="0.1in" margin-right="0.2in"  margin-top="0.2in" margin-bottom="0.2in" >
                <fo:region-body margin-top="1.5in"/>
                <fo:region-before extent="1in"/>
                <fo:region-after extent="1in"/>
            </fo:simple-page-master>
        </fo:layout-master-set>
		<#if finalProfessionalTaxMap?has_content>
			<#assign partyGroup = delegator.findOne("PartyGroup", {"partyId" : "Company"}, true)>
    		<#assign postalAddress=delegator.findByAnd("PartyAndPostalAddress", {"partyId" : "Company"})/>
			<#assign noofLines=1>
			<#assign SNO=1>
			<#assign GrandTot=0>
            <#assign pageTot=0>
			<fo:page-sequence master-reference="main">
        		<fo:static-content font-size="13pt" font-family="Courier,monospace"  flow-name="xsl-region-before" font-weight="bold">        
	        		<fo:block  keep-together="always" text-align="center" font-family="Courier,monospace" white-space-collapse="false" font-weight="bold"> ${partyGroup.groupName?if_exists}</fo:block>
					<fo:block  keep-together="always" text-align="center" font-family="Courier,monospace" white-space-collapse="false" font-weight="bold"> ${postalAddress[0].address1?if_exists}</fo:block>
	        		<fo:block  keep-together="always" text-align="center" font-family="Courier,monospace" white-space-collapse="false" font-weight="bold">&#160;      </fo:block>
	        		<fo:block text-align="center" keep-together="always" white-space-collapse="false">PROFESSIONAL TAX REPORT FOR THE MONTH OF ${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(fromDate, "MMMM yyyy")}</fo:block>	
	        		<fo:block  keep-together="always" text-align="center" font-family="Courier,monospace" white-space-collapse="false" font-weight="bold">&#160;      </fo:block> 
	        		<fo:block text-align="left" keep-together="always" white-space-collapse="false">&#160;                                                                         PAGE NO: <fo:page-number/></fo:block>	 	 	  	 	  
	        		<fo:block text-align="left" keep-together="always" font-size="12pt">---------------------------------------------------------------------------------------------</fo:block>
          		</fo:static-content>
          		<fo:flow flow-name="xsl-region-body" font-family="Courier,monospace">
	            	<fo:block font-family="Courier,monospace">
	                	<fo:table >
		                	<fo:table-column column-width="100pt"/>
		                    <fo:table-column column-width="180pt"/>
		                    <fo:table-column column-width="300pt"/>                
		                    <fo:table-column column-width="50pt"/>
	                		<fo:table-body> 
	                     		<fo:table-row >
		                            <fo:table-cell >	
		                            	<fo:block text-align="center" keep-together="always" font-weight="bold" font-size="15pt">SNO</fo:block>
		                            </fo:table-cell>
		                            <fo:table-cell >	
		                            	<fo:block text-align="center" keep-together="always" font-weight="bold" font-size="15pt">Employee Code</fo:block>
		                            </fo:table-cell>
		                            <fo:table-cell >	
		                            	<fo:block text-align="left" keep-together="always" font-weight="bold" font-size="15pt">Employee Name</fo:block>
		                            </fo:table-cell>
		                            <fo:table-cell >	
		                            	<fo:block text-align="right" keep-together="always" font-weight="bold" font-size="15pt">Amount</fo:block>
		                            </fo:table-cell>
			                   	</fo:table-row>
			                   	<fo:table-row >
			                   		<fo:table-cell >	
	                            		<fo:block keep-together="always" font-weight="bold">---------------------------------------------------------------------------------------------</fo:block>
	                            	</fo:table-cell>
	                            </fo:table-row>
	                            <#assign ProfessionalTaxMap=finalProfessionalTaxMap.entrySet()>
		                   		<#list ProfessionalTaxMap as ProfessionalTaxValues>
		                   			<#if ProfessionalTaxValues.getValue().get("amount")?has_content>
		                   				<#if (ProfessionalTaxValues.getValue().get("amount"))!=0>
				                   			<fo:table-row >
				                   				<fo:table-cell >	
					                            	<fo:block text-align="center" keep-together="always" font-size="15pt">${SNO}</fo:block>
					                            	<#assign SNO=SNO+1>
					                            </fo:table-cell>
					                            <fo:table-cell >	
					                            	<fo:block text-align="center" keep-together="always" font-size="15pt">${ProfessionalTaxValues.getKey()}</fo:block>
					                            </fo:table-cell>
					                            <fo:table-cell >	
					                            	<fo:block text-align="left" keep-together="always" font-size="15pt">${ProfessionalTaxValues.getValue().get("partyName")}</fo:block>
					                            </fo:table-cell>
					                            <fo:table-cell >	
					                            	<fo:block text-align="right" keep-together="always" font-size="15pt">${ProfessionalTaxValues.getValue().get("amount")*(-1)}</fo:block>
					                            	<#assign GrandTot=GrandTot+(ProfessionalTaxValues.getValue().get("amount")*(-1))>
	                            					<#assign pageTot=pageTot+(ProfessionalTaxValues.getValue().get("amount")*(-1))>
					                            </fo:table-cell>
					                            <#assign noofLines=noofLines+1>
					                    	</fo:table-row>
					                    	<#if (noofLines == 31) >
				                            	<fo:table-row>
				                            		<fo:table-cell>
				   										<fo:block page-break-after="always" font-weight="bold" font-size="13pt" text-align="center">Page Total:</fo:block>
				   									</fo:table-cell>
				   									<fo:table-cell >	
					                            		<fo:block text-align="left" keep-together="always" font-weight="bold"></fo:block>
					                            	</fo:table-cell>
					                            	<fo:table-cell >	
					                            		<fo:block text-align="left" keep-together="always" font-weight="bold"></fo:block>
					                            	</fo:table-cell>
				   									<fo:table-cell>
				   										<fo:block text-align="right" page-break-after="always" font-weight="bold" font-size="13pt">${pageTot?if_exists?string("##0.00")}</fo:block>
				   									</fo:table-cell>
				   									<#assign pageTot=0>
												</fo:table-row>
				                           		<#assign noofLines =1>
											</#if>
					                   	</#if>
				                    </#if>
			                    </#list>
			                    <fo:table-row >
			                   		<fo:table-cell >	
	                            		<fo:block keep-together="always" font-weight="bold">---------------------------------------------------------------------------------------------</fo:block>
	                            	</fo:table-cell>
	                            </fo:table-row>
	                            <fo:table-row >
		                   			<fo:table-cell >	
	                            		<fo:block text-align="center" keep-together="always" font-weight="bold" font-size="13pt">Grand Total:</fo:block>
	                            	</fo:table-cell>
	                            	<fo:table-cell >	
	                            		<fo:block text-align="left" keep-together="always" font-weight="bold"></fo:block>
	                            	</fo:table-cell>
	                            	<fo:table-cell >	
	                            		<fo:block text-align="left" keep-together="always" font-weight="bold"></fo:block>
	                            	</fo:table-cell>
	                            	<fo:table-cell >	
	                            		<fo:block text-align="right" keep-together="always" font-weight="bold" font-size="13pt">${GrandTot?if_exists?string("##0.00")}</fo:block>
	                            	</fo:table-cell>
	                           </fo:table-row>
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
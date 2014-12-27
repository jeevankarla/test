<#escape x as x?xml>
    <fo:root xmlns:fo="http://www.w3.org/1999/XSL/Format">
        <fo:layout-master-set>
            <fo:simple-page-master master-name="main" page-height="12in" page-width="15in" margin-top="0.1in" >
                <fo:region-body margin-top="0.6in"/>
                <fo:region-before extent="1in"/>
                <fo:region-after extent="1in"/>
            </fo:simple-page-master>
            ${setRequestAttribute("OUTPUT_FILENAME", "BenDedCheckList.txt")}
        </fo:layout-master-set>
		<#if errorMessage?has_content>
			<fo:page-sequence master-reference="main">
			   <fo:flow flow-name="xsl-region-body" font-family="Courier,monospace">
			      <fo:block font-size="4pt">
			              ${errorMessage}.
			   	  </fo:block>
			   </fo:flow>
			</fo:page-sequence>        
		<#else>         
 			<fo:page-sequence master-reference="main">
				<fo:static-content flow-name="xsl-region-before" font-family="Courier,monospace" font-size="8pt">
			        <fo:block text-align="left" white-space-collapse="false" keep-together="always" font-size="5pt">.                               CHECK LIST FOR BENEFITS AND DEDUCTIONS ON ${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(fromDate, "MMM-yyyy")}</fo:block>        
			  		<fo:block text-align="left" white-space-collapse="false" keep-together="always" font-size="8pt">--------------------------------------------------------------------------------------------------------------------------------------------------------------</fo:block>	 	 	  
			  		<fo:block text-align="left" white-space-collapse="false" keep-together="always" font-size="8pt">TYPE    MMYY        TypeId                EMPNO         Emp.Name                Desgn.                WEF         Sanction      O.B.        INST.</fo:block>
			  		<fo:block text-align="left" white-space-collapse="false" keep-together="always" font-size="8pt">--------------------------------------------------------------------------------------------------------------------------------------------------------------</fo:block>
			  	</fo:static-content> 
			  	<fo:flow flow-name="xsl-region-body" font-family="Courier,monospace">  
				  	<fo:block font-family="Courier,monospace" font-size="9pt">
						<fo:table>
							<fo:table-column column-width="30pt"/>
			  				<fo:table-column column-width="40pt"/>
	                       	<fo:table-column column-width="70pt"/>
	                       	<fo:table-column column-width="30pt"/>
	                       	<fo:table-column column-width="55pt"/>
	                       	<fo:table-column column-width="50pt"/>
	                       	<fo:table-column column-width="30pt"/>
	                       	<fo:table-column column-width="27pt"/>
	                       	<fo:table-column column-width="27pt"/>
	                       	<fo:table-column column-width="27pt"/>
				            <fo:table-body>
				            	<#list benefitList as benefitTypeId>
				            		<fo:table-row >
							   			<fo:table-cell >
							   				<fo:block font-family="Courier,monospace">
										   		<fo:table>
						                     		<fo:table-column column-width="150pt"/>
						                     		<fo:table-body> 
							                     		<fo:table-row >
							                     			<#assign benefitType = delegator.findOne("BenefitType", {"benefitTypeId" : benefitTypeId}, false)> 
								                            <fo:table-cell >	
								                            	<fo:block keep-together="always" text-align="left">${benefitTypeId?if_exists} - ${benefitType.get("benefitName").toUpperCase()?if_exists}</fo:block>
								                            </fo:table-cell>
								                      	</fo:table-row>
								                      	<fo:table-row >
								                            <fo:table-cell >	
								                            	<fo:block keep-together="always" text-align="left">----------------------------------------</fo:block>
								                            </fo:table-cell>
								                      	</fo:table-row>
						                           </fo:table-body>
						                     	</fo:table>
						                     </fo:block>
					                    </fo:table-cell >
			                     	</fo:table-row>
				            		<#if benefitsItemsList?has_content> 
				                  		<#list benefitsItemsList as benefitsList>
				                  			<#if benefitTypeId == benefitsList.get("benefitTypeId")>
										     	<fo:table-row>
										   			<#assign benefitType = delegator.findOne("BenefitType", {"benefitTypeId" : benefitsList.get("benefitTypeId")}, false)> 
										   			<fo:table-cell>
										   				<fo:block font-size="4pt" text-align="left">RGL</fo:block>
										   			</fo:table-cell>
										   			<fo:table-cell>
										   				<fo:block font-size="4pt" text-align="left">${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(fromDate, "MM/yyyy")?if_exists}</fo:block>
										   			</fo:table-cell>
										   			<fo:table-cell>
										   				<fo:block font-size="4pt" text-align="left">${benefitType.get("benefitName")?if_exists}</fo:block>
										   			</fo:table-cell>
										   			<fo:table-cell>
										   				<fo:block font-size="4pt" text-align="left">${benefitsList.get("partyIdTo")?if_exists}</fo:block>
										   			</fo:table-cell>
										   			<fo:table-cell>
										   				<fo:block font-size="4pt" text-align="left">${Static["org.ofbiz.party.party.PartyHelper"].getPartyName(delegator, benefitsList.get("partyIdTo"), false)}</fo:block>
										   			</fo:table-cell>
										   			<#assign emplPosition=delegator.findByAnd("EmplPosition", {"partyId" : benefitsList.get("partyIdTo")})/>
										   			<fo:table-cell>
										   				<fo:block font-size="4pt" text-align="left">${(emplPosition[0].emplPositionId)?if_exists}</fo:block>
										   			</fo:table-cell>
										   			<fo:table-cell>
										   				<fo:block font-size="4pt" text-align="right">${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(fromDate ,"dd/MM/yyyy")?if_exists}</fo:block>
										   			</fo:table-cell>
										   			<fo:table-cell>
										   				<fo:block font-size="4pt" text-align="right" text-indent = "10pt">0.00</fo:block>
										   			</fo:table-cell>
										   			<fo:table-cell>
										   				<fo:block font-size="4pt" text-align="right" text-indent = "10pt"><#if benefitsList.get("cost")?has_content>${benefitsList.get("cost")?if_exists?string("##0.00")}<#else>0.00</#if></fo:block>
										   			</fo:table-cell>
										   			<fo:table-cell>
										   				<fo:block font-size="4pt" text-align="right"><#if benefitsList.get("cost")?has_content>${benefitsList.get("cost")?if_exists?string("##0.00")}<#else>0.00</#if></fo:block>
										   			</fo:table-cell>
										   		</fo:table-row>
										   	</#if>
								   		</#list>
									<#else>
										<#if deductionItemsList?has_content>
											
										<#else>
											<fo:table-row>
												<fo:table-cell>
									   				<fo:block font-size="4pt" text-align="left">No Orders Found</fo:block>
									   			</fo:table-cell>
											</fo:table-row>
										</#if> 
							   		</#if>
							   	</#list>
							   	<#list deductionList as dedTypeId>
							   		<fo:table-row >
							   			<fo:table-cell >
							   				<fo:block font-family="Courier,monospace">
										   		<fo:table>
						                     		<fo:table-column column-width="150pt"/>
						                     		<fo:table-body> 
							                     		<fo:table-row >
							                     			<#assign deductionType = delegator.findOne("DeductionType", {"deductionTypeId" : dedTypeId}, false)> 
								                            <fo:table-cell >	
								                            	<fo:block keep-together="always" text-align="left">${dedTypeId?if_exists} - ${deductionType.get("deductionName").toUpperCase()?if_exists}</fo:block>
								                            </fo:table-cell>
								                      	</fo:table-row>
								                      	<fo:table-row >
								                            <fo:table-cell >	
								                            	<fo:block keep-together="always" text-align="left">----------------------------------------</fo:block>
								                            </fo:table-cell>
								                      	</fo:table-row>
						                           </fo:table-body>
						                     	</fo:table>
						                     </fo:block>
					                    </fo:table-cell >
			                     	</fo:table-row>
								   	<#if deductionItemsList?has_content> 
					                  	<#list deductionItemsList as deductionList>
					                  		<#if dedTypeId == deductionList.get("deductionTypeId")>
										     	<fo:table-row>
										   			<#assign deductionType = delegator.findOne("DeductionType", {"deductionTypeId" : deductionList.get("deductionTypeId")}, false)> 
										   			<fo:table-cell>
										   				<fo:block font-size="4pt" text-align="left">RGL</fo:block>
										   			</fo:table-cell>
										   			<fo:table-cell>
										   				<fo:block font-size="4pt" text-align="left">${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(fromDate, "MM/yyyy")?if_exists}</fo:block>
										   			</fo:table-cell>
										   			<fo:table-cell>
										   				<fo:block font-size="4pt" text-align="left">${deductionType.get("deductionName")?if_exists}</fo:block>
										   			</fo:table-cell>
										   			<fo:table-cell>
										   				<fo:block font-size="4pt" text-align="left">${deductionList.get("partyIdTo")?if_exists}</fo:block>
										   			</fo:table-cell>
										   			<fo:table-cell>
										   				<fo:block font-size="4pt" text-align="left">${Static["org.ofbiz.party.party.PartyHelper"].getPartyName(delegator, deductionList.get("partyIdTo"), false)}</fo:block>
										   			</fo:table-cell>
										   			<#assign emplPosition=delegator.findByAnd("EmplPosition", {"partyId" : deductionList.get("partyIdTo")})/>
										   			<fo:table-cell>
										   				<fo:block font-size="4pt" text-align="left">${(emplPosition[0].emplPositionId)?if_exists}</fo:block>
										   			</fo:table-cell>
										   			<fo:table-cell>
										   				<fo:block font-size="4pt" text-align="right">${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(fromDate ,"dd/MM/yyyy")?if_exists}</fo:block>
										   			</fo:table-cell>
										   			<fo:table-cell>
										   				<fo:block font-size="4pt" text-align="right" text-indent = "10pt">0.00</fo:block>
										   			</fo:table-cell>
										   			<fo:table-cell>
										   				<fo:block font-size="4pt" text-align="right" text-indent = "10pt"><#if deductionList.get("cost")?has_content>${deductionList.get("cost")?if_exists?string("##0.00")}<#else>0.00</#if></fo:block>
										   			</fo:table-cell>
										   			<fo:table-cell>
										   				<fo:block font-size="4pt" text-align="right"><#if deductionList.get("cost")?has_content>${deductionList.get("cost")?if_exists?string("##0.00")}<#else>0.00</#if></fo:block>
										   			</fo:table-cell>
										   		</fo:table-row>
										   	</#if>
									   	</#list>
								   	</#if>
								</#list>
							</fo:table-body>
						</fo:table> 
					</fo:block>
				</fo:flow>
			</fo:page-sequence>
		</#if>
	</fo:root>
</#escape>
				  	

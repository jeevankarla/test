<#escape x as x?xml>
    <fo:root xmlns:fo="http://www.w3.org/1999/XSL/Format">
        <fo:layout-master-set>
            <fo:simple-page-master master-name="main" page-height="12in" page-width="15in" margin-top="0.1in" >
                <fo:region-body margin-top="0.7in"/>
                <fo:region-before extent="1in"/>
                <fo:region-after extent="1in"/>
            </fo:simple-page-master>
            ${setRequestAttribute("OUTPUT_FILENAME", "PayMasterCheckList.txt")}
        </fo:layout-master-set>
		<#assign organizationDetails = delegator.findOne("PartyGroup", {"partyId" : parameters.partyId}, true)>
        <#assign oragnizationId = organizationDetails.get("comments")>     
 		<fo:page-sequence master-reference="main">
			<fo:static-content flow-name="xsl-region-before" font-family="Courier,monospace" font-size="8pt">
		        <fo:block text-align="left" white-space-collapse="false" keep-together="always" font-size="5pt">.                               PAY MASTER DATA UPDATIONCHECK LIST ${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(thruDate, "MMMM,yyyy").toUpperCase()}</fo:block>        
		  		<fo:block text-align="left" white-space-collapse="false" keep-together="always" font-size="8pt">---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------</fo:block>	 	 	  
		  		<fo:block text-align="left" white-space-collapse="false" keep-together="always" font-size="8pt">.                                                     CA   SC   In                                PF    GPF   Vol </fo:block>
		  		
		  		<fo:block font-family="Courier,monospace">
					<fo:table>
						<fo:table-column column-width="18pt"/>
		  				<fo:table-column column-width="30pt"/>
                       	<fo:table-column column-width="30pt"/>
                       	<fo:table-column column-width="20pt"/>
                       	<fo:table-column column-width="20pt"/>
                       	<fo:table-column column-width="30pt"/>
                       	<fo:table-column column-width="12pt"/>
                       	<fo:table-column column-width="12pt"/>
                       	<fo:table-column column-width="12pt"/>
                       	<fo:table-column column-width="12pt"/>
                       	<fo:table-column column-width="30pt"/>
                       	<fo:table-column column-width="35pt"/>
                       	<fo:table-column column-width="35pt"/>
                       	<fo:table-column column-width="35pt"/>
                       	<fo:table-column column-width="30pt"/>
                       	<fo:table-column column-width="30pt"/>
                       	<fo:table-column column-width="30pt"/>
                       	<fo:table-column column-width="30pt"/>
                       	<fo:table-column column-width="30pt"/>
                       	<fo:table-column column-width="30pt"/>
                       	<fo:table-column column-width="30pt"/>
                       	<fo:table-column column-width="30pt"/>
                       	<fo:table-column column-width="30pt"/>
                       	<fo:table-column column-width="30pt"/>
                       	<fo:table-column column-width="30pt"/>
                       	<fo:table-column column-width="30pt"/>
                       	<fo:table-column column-width="30pt"/>
                       	<fo:table-body>
                       		<fo:table-row >
			            		<fo:table-cell >
			            			<fo:block font-size="4pt" text-align="left">TYPE</fo:block>
			            		</fo:table-cell >
			            		<fo:table-cell >
			            			<fo:block font-size="4pt" text-align="left">MMYY</fo:block>
			            		</fo:table-cell >
			            		<fo:table-cell >
			            			<fo:block font-size="4pt" text-align="left">EmpID</fo:block>
			            		</fo:table-cell >
			            		<fo:table-cell >
			            			<fo:block font-size="4pt" text-align="left">SHED</fo:block>
			            		</fo:table-cell >
			            		<fo:table-cell >
			            			<fo:block font-size="4pt" text-align="left">UNIT</fo:block>
			            		</fo:table-cell >
			            		<fo:table-cell >
			            			<fo:block font-size="4pt" text-align="left">COST</fo:block>
			            		</fo:table-cell > 
			            		<fo:table-cell >
			            			<fo:block font-size="4pt" text-align="left">RPS</fo:block>
			            		</fo:table-cell >
			            		<fo:table-cell >
			            			<fo:block font-size="4pt" text-align="left">T</fo:block>
			            		</fo:table-cell >
			            		<fo:table-cell >
			            			<fo:block font-size="4pt" text-align="left">ID</fo:block>
			            		</fo:table-cell >
			            		<fo:table-cell >
			            			<fo:block font-size="4pt" text-align="left">MM</fo:block>
			            		</fo:table-cell > 
			            		<fo:table-cell >
			            			<fo:block font-size="4pt" text-align="left">Pay</fo:block>
			            		</fo:table-cell >
			            		<fo:table-cell >
			            			<fo:block font-size="4pt" text-align="left">PP</fo:block>
			            		</fo:table-cell >
			            		<fo:table-cell >
			            			<fo:block font-size="4pt" text-align="left">FPP</fo:block>
			            		</fo:table-cell >
			            		<fo:table-cell >
			            			<fo:block font-size="4pt" text-align="left">SP</fo:block>
			            		</fo:table-cell >
			            		<fo:table-cell >
			            			<fo:block font-size="4pt" text-align="left">Typ</fo:block>
			            		</fo:table-cell >
			            		<fo:table-cell >
			            			<fo:block font-size="4pt" text-align="left">Amt</fo:block>
			            		</fo:table-cell >
			            		<fo:table-cell >
			            			<fo:block font-size="4pt" text-align="left">PF</fo:block>
			            		</fo:table-cell >
			            		<fo:table-cell >
			            			<fo:block font-size="4pt" text-align="left">GIS</fo:block>
			            		</fo:table-cell >
			            		<fo:table-cell >
			            			<fo:block font-size="4pt" text-align="left">Hrr</fo:block>
			            		</fo:table-cell >
			            		<fo:table-cell >
			            			<fo:block font-size="4pt" text-align="left">Wtr</fo:block>
			            		</fo:table-cell >
			            		<fo:table-cell >
			            			<fo:block font-size="4pt" text-align="left">Wsh</fo:block>
			            		</fo:table-cell >
			            		<fo:table-cell >
			            			<fo:block font-size="4pt" text-align="left">Con</fo:block>
			            		</fo:table-cell >
			            		<fo:table-cell >
			            			<fo:block font-size="4pt" text-align="left">Med</fo:block>
			            		</fo:table-cell >
			            		<fo:table-cell >
			            			<fo:block font-size="4pt" text-align="left">Spl</fo:block>
			            		</fo:table-cell >
			            		<fo:table-cell >
			            			<fo:block font-size="4pt" text-align="left">Etv</fo:block>
			            		</fo:table-cell >
			            		<fo:table-cell >
			            			<fo:block font-size="4pt" text-align="left">Bnk</fo:block>
			            		</fo:table-cell >
			            		<fo:table-cell >
			            			<fo:block font-size="4pt" text-align="left">B.A/C</fo:block>
			            		</fo:table-cell >
			            	</fo:table-row >
                       	</fo:table-body>
					</fo:table> 
				</fo:block>
		<#--  		<fo:block text-align="left" white-space-collapse="false" keep-together="always" font-size="8pt">TYPE   MMYY    EmpID    SHED   UNIT    COST    RPS  T   ID  MM   Pay    PP  FPP   PP   Typ   Amt   PF    GIS   Hrr Wtr  Wsh  Con  Med  Spl  Etv  Bnk   B.A/C</fo:block>  -->
		  		<fo:block text-align="left" white-space-collapse="false" keep-together="always" font-size="8pt">--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------</fo:block>
		  	</fo:static-content> 
			<fo:flow flow-name="xsl-region-body" font-family="Courier,monospace">  
			  	<fo:block font-family="Courier,monospace">
					<fo:table>
						<fo:table-column column-width="18pt"/>
		  				<fo:table-column column-width="30pt"/>
                       	<fo:table-column column-width="30pt"/>
                       	<fo:table-column column-width="20pt"/>
                       	<fo:table-column column-width="20pt"/>
                       	<fo:table-column column-width="30pt"/>
                       	<fo:table-column column-width="12pt"/>
                       	<fo:table-column column-width="12pt"/>
                       	<fo:table-column column-width="12pt"/>
                       	<fo:table-column column-width="12pt"/>
                       	<fo:table-column column-width="30pt"/>
                       	<fo:table-column column-width="35pt"/>
                       	<fo:table-column column-width="35pt"/>
                       	<fo:table-column column-width="35pt"/>
                       	<fo:table-column column-width="30pt"/>
                       	<fo:table-column column-width="30pt"/>
                       	<fo:table-column column-width="30pt"/>
                       	<fo:table-column column-width="30pt"/>
                       	<fo:table-column column-width="30pt"/>
                       	<fo:table-column column-width="30pt"/>
                       	<fo:table-column column-width="30pt"/>
                       	<fo:table-column column-width="30pt"/>
                       	<fo:table-column column-width="30pt"/>
                       	<fo:table-column column-width="30pt"/>
                       	<fo:table-column column-width="30pt"/>
                       	<fo:table-column column-width="30pt"/>
                       	<fo:table-column column-width="30pt"/>
			            <fo:table-body>
			            	<#if partyBenefitMap?has_content>
				            	<#assign partyBenefits = partyBenefitMap.entrySet()>
				            	<#list partyBenefits as partyBen>
					            	<fo:table-row >
					            		<fo:table-cell >
					            			<fo:block font-size="4pt" text-align="left">RGL</fo:block>
					            		</fo:table-cell >
					            		<fo:table-cell >
					            			<fo:block font-size="4pt" text-align="left">${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(fromDate, "MM/yyyy")}</fo:block>
					            		</fo:table-cell >
					            		<fo:table-cell >
					            			<fo:block font-size="4pt" text-align="left">${Static["org.ofbiz.party.party.PartyServices"].getPartyInternal(delegator, partyBen.getKey())}</fo:block>
					            		</fo:table-cell >
					            		<fo:table-cell >
					            			<fo:block font-size="4pt" text-align="left"><#if oragnizationId?has_content>${oragnizationId?if_exists}<#else>&#160;</#if></fo:block>
					            		</fo:table-cell >
					            		<fo:table-cell >
					            			<fo:block font-size="4pt" text-align="left"><#if partyBen.getValue().get("unit")?has_content>${partyBen.getValue().get("unit")?if_exists}<#else>&#160;</#if></fo:block>
					            		</fo:table-cell >
					            		<fo:table-cell >
					            			<fo:block font-size="4pt" text-align="left"><#if partyBen.getValue().get("costCode")?has_content>${partyBen.getValue().get("costCode")?if_exists}<#else>&#160;</#if></fo:block>
					            		</fo:table-cell > 
					            		<fo:table-cell >
					            			<fo:block font-size="4pt" text-align="left">&#160;</fo:block>
					            		</fo:table-cell >
					            		<fo:table-cell >
					            			<fo:block font-size="4pt" text-align="left">&#160;</fo:block>
					            		</fo:table-cell >
					            		<fo:table-cell >
					            			<fo:block font-size="4pt" text-align="left">&#160;</fo:block>
					            		</fo:table-cell >
					            		<fo:table-cell >
					            			<fo:block font-size="4pt" text-align="left">&#160;</fo:block>
					            		</fo:table-cell > 
					            		<fo:table-cell >
					            			<fo:block font-size="4pt" text-align="left"><#if partyBen.getValue().get("pay")?has_content>${partyBen.getValue().get("pay")?if_exists}<#else>&#160;</#if></fo:block>
					            		</fo:table-cell >
					            		<fo:table-cell >
					            			<fo:block font-size="4pt" text-align="left"><#if partyBen.getValue().get("PAYROL_BEN_PNLPAY")?has_content>${partyBen.getValue().get("PAYROL_BEN_PNLPAY")?if_exists}<#else>&#160;</#if></fo:block>
					            		</fo:table-cell >
					            		<fo:table-cell >
					            			<fo:block font-size="4pt" text-align="left"><#if partyBen.getValue().get("PAYROL_BEN_FPP")?has_content>${partyBen.getValue().get("PAYROL_BEN_FPP")?if_exists}<#else>&#160;</#if></fo:block>
					            		</fo:table-cell >
					            		<fo:table-cell >
					            			<fo:block font-size="4pt" text-align="left"><#if partyBen.getValue().get("PAYROL_BEN_SPLPAY")?has_content>${partyBen.getValue().get("PAYROL_BEN_SPLPAY")?if_exists}<#else>&#160;</#if></fo:block>
					            		</fo:table-cell >
					            		<fo:table-cell >
					            			<fo:block font-size="4pt" text-align="left"><#if partyBen.getValue().get("PAYROL_BEN_PFEMPLYR")?has_content>${partyBen.getValue().get("PAYROL_BEN_PFEMPLYR")?if_exists}<#else>&#160;</#if></fo:block>
					            		</fo:table-cell >
					            		<fo:table-cell >
					            			<fo:block font-size="4pt" text-align="left"><#if partyBen.getValue().get("PAYROL_DD_GPF")?has_content>${partyBen.getValue().get("PAYROL_DD_GPF")?if_exists}<#else>&#160;</#if></fo:block>
					            		</fo:table-cell >
					            		<fo:table-cell >
					            			<fo:block font-size="4pt" text-align="left"><#if partyBen.getValue().get("PAYROL_DD_VOLPF")?has_content>${partyBen.getValue().get("PAYROL_DD_VOLPF")?if_exists}<#else>&#160;</#if></fo:block>
					            		</fo:table-cell >
					            		<fo:table-cell >
					            			<fo:block font-size="4pt" text-align="left"><#if partyBen.getValue().get("PAYROL_DD_GIS")?has_content>${partyBen.getValue().get("PAYROL_DD_GIS")?if_exists}<#else>&#160;</#if></fo:block>
					            		</fo:table-cell >
					            		<fo:table-cell >
					            			<fo:block font-size="4pt" text-align="left"><#if partyBen.getValue().get("PAYROL_DD_HRR")?has_content>${partyBen.getValue().get("PAYROL_DD_HRR")?if_exists}<#else>&#160;</#if></fo:block>
					            		</fo:table-cell >
					            		<fo:table-cell >
					            			<fo:block font-size="4pt" text-align="left"><#if partyBen.getValue().get("PAYROL_DD_WATER")?has_content>${partyBen.getValue().get("PAYROL_DD_WATER")?if_exists}<#else>&#160;</#if></fo:block>
					            		</fo:table-cell >
					            		<fo:table-cell >
					            			<fo:block font-size="4pt" text-align="left"><#if partyBen.getValue().get("PAYROL_BEN_WASHALW")?has_content>${partyBen.getValue().get("PAYROL_BEN_WASHALW")?if_exists}<#else>&#160;</#if></fo:block>
					            		</fo:table-cell >
					            		<fo:table-cell >
					            			<fo:block font-size="4pt" text-align="left"><#if partyBen.getValue().get("PAYROL_BEN_CONALW")?has_content>${partyBen.getValue().get("PAYROL_BEN_CONALW")?if_exists}<#else>&#160;</#if></fo:block>
					            		</fo:table-cell >
					            		<fo:table-cell >
					            			<fo:block font-size="4pt" text-align="left"><#if partyBen.getValue().get("PAYROL_BEN_MEDALW")?has_content>${partyBen.getValue().get("PAYROL_BEN_MEDALW")?if_exists}<#else>&#160;</#if></fo:block>
					            		</fo:table-cell >
					            		<fo:table-cell >
					            			<fo:block font-size="4pt" text-align="left"><#if partyBen.getValue().get("PAYROL_BEN_SPLALW")?has_content>${partyBen.getValue().get("PAYROL_BEN_SPLALW")?if_exists}<#else>&#160;</#if></fo:block>
					            		</fo:table-cell >
					            		<fo:table-cell >
					            			<fo:block font-size="4pt" text-align="left">&#160;</fo:block>
					            		</fo:table-cell >
					            		<fo:table-cell >
					            			<fo:block font-size="4pt" text-align="left">&#160;</fo:block>
					            		</fo:table-cell >
					            		<fo:table-cell >
					            			<fo:block font-size="4pt" text-align="left"><#if partyBen.getValue().get("finAccountCode")?has_content>${partyBen.getValue().get("finAccountCode")?if_exists}<#else>&#160;</#if></fo:block>
					            		</fo:table-cell >
					            	</fo:table-row >
					            </#list>
					       	<#else>
					       		<fo:table-row >
				            		<fo:table-cell >
				            			<fo:block font-size="4pt" text-align="left">No Records Found</fo:block>
				            		</fo:table-cell >
				            	</fo:table-row >
				           	</#if>
			      		</fo:table-body>
					</fo:table> 
				</fo:block>
			</fo:flow>
		</fo:page-sequence>
     </fo:root>
</#escape>
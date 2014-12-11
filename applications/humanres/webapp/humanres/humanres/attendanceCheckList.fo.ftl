<#escape x as x?xml>
    <fo:root xmlns:fo="http://www.w3.org/1999/XSL/Format">
        <fo:layout-master-set>
            <fo:simple-page-master master-name="main" page-height="12in" page-width="15in" margin-top="0.1in" >
                <fo:region-body margin-top="0.6in"/>
                <fo:region-before extent="1in"/>
                <fo:region-after extent="1in"/>
            </fo:simple-page-master>
            ${setRequestAttribute("OUTPUT_FILENAME", "attendanceCheckList.txt")}
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
			        <fo:block text-align="left" white-space-collapse="false" keep-together="always" font-size="5pt">.                               CHECK LIST FOR BENEFITS AND DEDUCTIONS ON ${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(selectDate, "dd-MM-yyyy")}</fo:block>        
			  		<fo:block text-align="left" white-space-collapse="false" keep-together="always" font-size="8pt">-------------------------------------------------------------------------------------------------------------------------------------</fo:block>	 	 	  
			  		<fo:block text-align="left" white-space-collapse="false" keep-together="always" font-size="8pt">TYPE    MMYY        TypeId          EMPNO     Emp.Name             Desgn.             WEF     Sanction     O.B.    INST.</fo:block>
			  		<fo:block text-align="left" white-space-collapse="false" keep-together="always" font-size="8pt">-------------------------------------------------------------------------------------------------------------------------------------</fo:block>
			  	</fo:static-content> 
			  	<fo:flow flow-name="xsl-region-body" font-family="Courier,monospace">  
				  	<fo:block font-family="Courier,monospace" font-size="9pt">
						<fo:table>
							<fo:table-column column-width="30pt"/>
			  				<fo:table-column column-width="40pt"/>
	                       	<fo:table-column column-width="50pt"/>
	                       	<fo:table-column column-width="30pt"/>
	                       	<fo:table-column column-width="55pt"/>
	                       	<fo:table-column column-width="70pt"/>
	                       	<fo:table-column column-width="100pt"/>
	                       	<fo:table-column column-width="100pt"/>
	                       	<fo:table-column column-width="50pt"/>
	                       	<fo:table-column column-width="50pt"/>
				            <fo:table-body>
				            	<#if payableDaysList?has_content> 
				                  	<#list payableDaysList as payableDays>
								     	<fo:table-row>
								   			<fo:table-cell>
								   				<fo:block font-size="4pt" text-align="left">RGL</fo:block>
								   			</fo:table-cell>
								   			<fo:table-cell>
								   				<fo:block font-size="4pt" text-align="left">${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(selectDate, "MM/yyyy")?if_exists}</fo:block>
								   			</fo:table-cell>
								   			<fo:table-cell>
								   				<fo:block font-size="4pt" text-align="left">Payable Days</fo:block>
								   			</fo:table-cell>
								   			<fo:table-cell>
								   				<fo:block font-size="4pt" text-align="left">${payableDays.get("partyId")?if_exists}</fo:block>
								   			</fo:table-cell>
								   			<fo:table-cell>
								   				<fo:block font-size="4pt" text-align="left">${Static["org.ofbiz.party.party.PartyHelper"].getPartyName(delegator, payableDays.get("partyId"), false)}</fo:block>
								   			</fo:table-cell>
								   			<#assign emplPosition=delegator.findByAnd("EmplPosition", {"partyId" : payableDays.get("partyId")})/>
								   			<fo:table-cell>
								   				<fo:block font-size="4pt" text-align="left">${(emplPosition[0].emplPositionId)?if_exists}</fo:block>
								   			</fo:table-cell>
								   			<fo:table-cell>
								   				<fo:block font-size="4pt" text-align="right">${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(selectDate ,"dd/MM/yyyy")?if_exists}</fo:block>
								   			</fo:table-cell>
								   			<fo:table-cell>
								   				<fo:block font-size="4pt" text-align="right" text-indent = "10pt">0.00</fo:block>
								   			</fo:table-cell>
								   			<fo:table-cell>
								   				<fo:block font-size="4pt" text-align="right" text-indent = "10pt">${payableDays.get("noOfPayableDays")?if_exists?string("##0.00")}</fo:block>
								   			</fo:table-cell>
								   			<fo:table-cell>
								   				<fo:block font-size="4pt" text-align="right"></fo:block>
								   			</fo:table-cell>
								   		</fo:table-row>
								   	</#list>
								<#else>
									<#if halfPayDaysList?has_content>
										
									<#else>
										<fo:table-row>
											<fo:table-cell>
								   				<fo:block font-size="4pt" text-align="left">No Orders Found</fo:block>
								   			</fo:table-cell>
										</fo:table-row>
									</#if> 
							   	</#if>
							   	<#if halfPayDaysList?has_content> 
				                  	<#list halfPayDaysList as halfPayDays>
								     	<fo:table-row>
								   			<fo:table-cell>
								   				<fo:block font-size="4pt" text-align="left">RGL</fo:block>
								   			</fo:table-cell>
								   			<fo:table-cell>
								   				<fo:block font-size="4pt" text-align="left">${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(selectDate, "MM/yyyy")?if_exists}</fo:block>
								   			</fo:table-cell>
								   			<fo:table-cell>
								   				<fo:block font-size="4pt" text-align="left">halfPay Days</fo:block>
								   			</fo:table-cell>
								   			<fo:table-cell>
								   				<fo:block font-size="4pt" text-align="left">${halfPayDays.get("partyId")?if_exists}</fo:block>
								   			</fo:table-cell>
								   			<fo:table-cell>
								   				<fo:block font-size="4pt" text-align="left">${Static["org.ofbiz.party.party.PartyHelper"].getPartyName(delegator, halfPayDays.get("partyId"), false)}</fo:block>
								   			</fo:table-cell>
								   			<#assign emplPosition=delegator.findByAnd("EmplPosition", {"partyId" : halfPayDays.get("partyId")})/>
								   			<fo:table-cell>
								   				<fo:block font-size="4pt" text-align="left">${(emplPosition[0].emplPositionId)?if_exists}</fo:block>
								   			</fo:table-cell>
								   			<fo:table-cell>
								   				<fo:block font-size="4pt" text-align="right">${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(selectDate ,"dd/MM/yyyy")?if_exists}</fo:block>
								   			</fo:table-cell>
								   			<fo:table-cell>
								   				<fo:block font-size="4pt" text-align="right" text-indent = "10pt">0.00</fo:block>
								   			</fo:table-cell>
								   			<fo:table-cell>
								   				<fo:block font-size="4pt" text-align="right" text-indent = "10pt">${halfPayDays.get("noOfHalfPayDays")?if_exists?string("##0.00")}</fo:block>
								   			</fo:table-cell>
								   			<fo:table-cell>
								   				<fo:block font-size="4pt" text-align="right">Check Once</fo:block>
								   			</fo:table-cell>
								   		</fo:table-row>
								   	</#list>
							   	</#if>
							</fo:table-body>
						</fo:table> 
					</fo:block>
				</fo:flow>
			</fo:page-sequence>
		</#if>
	</fo:root>
</#escape>
				  	




















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
			        <fo:block text-align="left" white-space-collapse="false" keep-together="always" font-size="5pt">.                               CHECK LIST FOR ATTENDANCE ON ${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(thruDate, "MMMM,yyyy").toUpperCase()}</fo:block>        
			  		<fo:block text-align="left" white-space-collapse="false" keep-together="always" font-size="8pt">--------------------------------------------------------------------------------------------------------------------------------------------</fo:block>	 	 	  
			  		<fo:block text-align="left" white-space-collapse="false" keep-together="always" font-size="8pt">SLNO   UNIT   COST   TYPE   EMPNO      EMP NAME               DESIGNATION          WKD    CO    CL   EL   CHPL   HPL   DBL   EOL   TDAYS</fo:block>
			  		<fo:block text-align="left" white-space-collapse="false" keep-together="always" font-size="8pt">---------------------------------------------------------------------------------------------------------------------------------------------</fo:block>
			  	</fo:static-content> 
			  	<fo:flow flow-name="xsl-region-body" font-family="Courier,monospace">  
				  	<fo:block font-family="Courier,monospace" font-size="9pt">
						<fo:table>
							<#assign sNo = 1>
							<fo:table-column column-width="10pt"/>
			  				<fo:table-column column-width="30pt"/>
	                       	<fo:table-column column-width="40pt"/>
	                       	<fo:table-column column-width="40pt"/>
	                       	<fo:table-column column-width="30pt"/>
	                       	<fo:table-column column-width="70pt"/>
	                       	<fo:table-column column-width="120pt"/>
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
				            	<#if employeeAttendanceMap?has_content> 
				            	<#assign employeeDetails = employeeAttendanceMap.entrySet()>
				                  	<#list employeeDetails as employeeAttendanceDetails>
				                  		<#if employeeAttendanceDetails.getValue()?has_content>
									     	<fo:table-row>
									   			<fo:table-cell>
									   				<fo:block font-size="4pt" text-align="center">${sNo?if_exists}</fo:block>
									   			</fo:table-cell>
									   			<fo:table-cell>
									   				<fo:block font-size="4pt" text-align="center">${employeeAttendanceDetails.getValue().get("unit")?if_exists}</fo:block>
									   			</fo:table-cell>
									   			<fo:table-cell>
									   				<fo:block font-size="4pt" text-align="center"><#if employeeAttendanceDetails.getValue().get("costCode")?has_content>${employeeAttendanceDetails.getValue().get("costCode")?if_exists}<#else>0.0</#if></fo:block>
									   			</fo:table-cell>
									   			<fo:table-cell>
									   				<fo:block font-size="4pt" text-align="center">EPF</fo:block>
									   			</fo:table-cell>
									   			<fo:table-cell>
									   				<fo:block font-size="4pt" text-align="left">${employeeAttendanceDetails.getKey()}</fo:block>
									   			</fo:table-cell>
									   			<fo:table-cell>
									   				<fo:block font-size="4pt" text-align="left">${Static["org.ofbiz.party.party.PartyHelper"].getPartyName(delegator, employeeAttendanceDetails.getKey(), false)}</fo:block>
									   			</fo:table-cell>
									   			<#assign emplPositionAndFulfilment=delegator.findByAnd("EmplPositionAndFulfillment", {"employeePartyId" : employeeAttendanceDetails.getKey()})/>
									   			<#assign designationId = emplPositionAndFulfilment[0].emplPositionTypeId>
												<#assign designation = delegator.findOne("EmplPositionType", {"emplPositionTypeId" : designationId?if_exists}, true)>
												<#assign designationName=emplPositionAndFulfilment[0].name?if_exists>
									   			<fo:table-cell>
									   				<fo:block font-size="4pt" text-align="left"><#if designationName?has_content>${Static["org.ofbiz.order.order.OrderServices"].nameTrim((StringUtil.wrapString(designationName)),12)?if_exists}<#else><#if designation?has_content>${Static["org.ofbiz.order.order.OrderServices"].nameTrim((StringUtil.wrapString(designation.description)),18)?if_exists}</#if></#if></fo:block>
									   			</fo:table-cell>
									   			<fo:table-cell>
									   				<fo:block font-size="4pt" text-align="right"><#if employeeAttendanceDetails.getValue().get("WKD")?has_content>${employeeAttendanceDetails.getValue().get("WKD")?if_exists?string("##0.0")}<#else>0.0</#if></fo:block>
									   			</fo:table-cell>
									   			<fo:table-cell>
									   				<fo:block font-size="4pt" text-align="right"><#if employeeAttendanceDetails.getValue().get("CO")?has_content>${employeeAttendanceDetails.getValue().get("CO")?if_exists?string("##0.0")}<#else>0.0</#if></fo:block>
									   			</fo:table-cell>
									   			<fo:table-cell>
									   				<fo:block font-size="4pt" text-align="right"><#if employeeAttendanceDetails.getValue().get("CL")?has_content>${employeeAttendanceDetails.getValue().get("CL")?if_exists?string("##0.0")}<#else>0.0</#if></fo:block>
									   			</fo:table-cell>
									   			<fo:table-cell>
									   				<fo:block font-size="4pt" text-align="right"><#if employeeAttendanceDetails.getValue().get("EL")?has_content>${employeeAttendanceDetails.getValue().get("EL")?if_exists?string("##0.0")}<#else>0.0</#if></fo:block>
									   			</fo:table-cell>
									   			<fo:table-cell>
									   				<fo:block font-size="4pt" text-align="right"><#if employeeAttendanceDetails.getValue().get("CHPL")?has_content>${employeeAttendanceDetails.getValue().get("CHPL")?if_exists?string("##0.0")}<#else>0.0</#if></fo:block>
									   			</fo:table-cell>
									   			<fo:table-cell>
									   				<fo:block font-size="4pt" text-align="right"><#if employeeAttendanceDetails.getValue().get("HPL")?has_content>${employeeAttendanceDetails.getValue().get("HPL")?if_exists?string("##0.0")}<#else>0.0</#if></fo:block>
									   			</fo:table-cell>
									   			<fo:table-cell>
									   				<fo:block font-size="4pt" text-align="right"><#if employeeAttendanceDetails.getValue().get("DBL")?has_content>${employeeAttendanceDetails.getValue().get("DBL")?if_exists?string("##0.0")}<#else>0.0</#if></fo:block>
									   			</fo:table-cell>
									   			<fo:table-cell>
									   				<fo:block font-size="4pt" text-align="right"><#if employeeAttendanceDetails.getValue().get("EOL")?has_content>${employeeAttendanceDetails.getValue().get("EOL")?if_exists?string("##0.0")}<#else>0.0</#if></fo:block>
									   			</fo:table-cell>
									   			<fo:table-cell>
									   				<fo:block font-size="4pt" text-align="right"><#if employeeAttendanceDetails.getValue().get("totalDays")?has_content>${employeeAttendanceDetails.getValue().get("totalDays")?if_exists?string("##0.0")}<#else>0.0</#if></fo:block>
									   			</fo:table-cell>
									   			<#assign sNo = sNo + 1>
									   		</fo:table-row>
									   	</#if>
								   	</#list>
								<#else>
									<fo:table-row>
										<fo:table-cell>
							   				<fo:block font-size="4pt" text-align="left">No Orders Found</fo:block>
							   			</fo:table-cell>
									</fo:table-row>
							   	</#if>
							</fo:table-body>
						</fo:table> 
					</fo:block>
				</fo:flow>
			</fo:page-sequence>
		</#if>
	</fo:root>
</#escape>
				  	




















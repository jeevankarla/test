<#--
Licensed to the Apache Software Foundation (ASF) under one
or more contributor license agreements.  See the NOTICE file
distributed with this work for additional information
regarding copyright ownership.  The ASF licenses this file
to you under the Apache License, Version 2.0 (the
"License"); you may not use this file except in compliance
with the License.  You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing,
software distributed under the License is distributed on an
"AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
KIND, either express or implied.  See the License for the
specific language governing permissions and limitations
under the License.
-->

<#escape x as x?xml>
  <fo:root xmlns:fo="http://www.w3.org/1999/XSL/Format">
    <fo:layout-master-set>
      <fo:simple-page-master master-name="main" page-height="12in" page-width="10in"  margin-left=".5in" margin-top=".5in">
	  		<fo:region-body margin-top=".8in"/>
	        <fo:region-before extent="1in"/>
	        <fo:region-after extent="1in"/>
      </fo:simple-page-master>
      ${setRequestAttribute("OUTPUT_FILENAME", "DeductionReport.txt")}
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
 		<#if allDeductionMap?has_content>
 		<#assign HeaderDetailsList=allDeductionMap.entrySet()>
 			<#assign partyGroup = delegator.findOne("PartyGroup", {"partyId" : "Company"}, true)>
     		<#list HeaderDetailsList as headerDetails>
     		<#assign deductionTypes= headerDetails.getValue().entrySet()>
	     		<fo:page-sequence master-reference="main"> 	
	     			<fo:static-content flow-name="xsl-region-before">
		     			<fo:block  keep-together="always" text-align="left" font-family="Courier,monospace" white-space-collapse="false" font-weight="bold" font-size="4pt">&#160;&#160;&#160;&#160;${partyGroup.groupName?if_exists}</fo:block>
						<#--<#assign partyAddressResult = dispatcher.runSync("getPartyPostalAddress", Static["org.ofbiz.base.util.UtilMisc"].toMap("partyId", "Company", "userLogin", userLogin))/>
						<fo:block  keep-together="always" text-align="center" font-family="Courier,monospace" white-space-collapse="false" font-weight="bold" font-size="4pt"><#if partyAddressResult.address1?has_content>${partyAddressResult.address1?if_exists}</#if><#if (partyAddressResult.address2?has_content)>${partyAddressResult.address2?if_exists}</#if></fo:block>-->
	     				<#assign headerCode = delegator.findOne("DeductionType", {"deductionTypeId" : headerDetails.getKey()}, true)>
	     				<fo:block keep-together="always" white-space-collapse="false" font-family="Courier,monospace" text-align="left" font-size="4pt" font-weight="bold">&#160;                                             ${headerCode.get("internalCode")?if_exists}-${headerCode.get("deductionName")?if_exists}</fo:block>
	     				<fo:block text-align="left" keep-together="always" white-space-collapse="false" font-family="Courier,monospace" font-size="4pt" font-weight="bold">&#160;                                                        DATE: ${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(nowTimestamp, "dd-MMM-yyyy")}</fo:block>
	     				<#assign shedCode = delegator.findOne("PartyGroup", {"partyId" : parameters.partyId}, true)>
	     			<fo:block keep-together="always" white-space-collapse="false" font-family="Courier,monospace" text-align="left" font-size="4pt" font-weight="bold">UNIT CODE:<#if shedCode?has_content>&#160;   ${shedCode.groupName?if_exists}</#if>           MONTH : ${(Static["org.ofbiz.base.util.UtilDateTime"].toDateString(timePeriodStart, "MMMMM-yyyy")).toUpperCase()}         PAGE: <fo:page-number/></fo:block>	 	 	  	 	
	     				<fo:block font-size="8pt">---------------------------------------------------------------------------------------</fo:block>
		     			<#if  headerDetails.getKey() == "PAYROL_DD_EPF">
	     				<fo:block font-family="Courier,monospace" font-size="9pt">
	     					<fo:table>
	           					<fo:table-column column-width="10pt"/>
		     					<fo:table-column column-width="20pt"/>
		     					<fo:table-column column-width="40pt"/>
		       					<fo:table-column column-width="33pt"/>
		     					<fo:table-column column-width="25pt"/>
		       					<fo:table-column column-width="25pt"/>
		       					<fo:table-column column-width="24pt"/>
		       					<fo:table-column column-width="24pt"/>
		       					<fo:table-body>
		       						<fo:table-cell><fo:block text-align="left" font-size="4pt">SNO</fo:block></fo:table-cell>
		       						<fo:table-cell><fo:block text-align="left" font-size="4pt">EMPNO</fo:block></fo:table-cell>
		       						<fo:table-cell><fo:block text-align="left" font-size="4pt">EMP NAME</fo:block></fo:table-cell>
		       						<fo:table-cell><fo:block text-align="left" font-size="4pt">DESN.</fo:block></fo:table-cell>
		       						<fo:table-cell><fo:block text-align="right" font-size="4pt">WAGES</fo:block></fo:table-cell>
		       						<fo:table-cell><fo:block text-align="right" font-size="4pt">EPFEE</fo:block></fo:table-cell>
									<fo:table-cell><fo:block text-align="right" font-size="4pt">EPFER</fo:block></fo:table-cell>
									<fo:table-cell><fo:block text-align="right" font-size="4pt">PEN</fo:block></fo:table-cell>
		       					</fo:table-body>
	     					</fo:table>
	     				</fo:block>
	     			</#if>
		       		<#if (headerDetails.getKey() != "PAYROL_DD_EPF") && (headerDetails.getKey() != "PAYROL_DD_APGLIF") && (headerDetails.getKey() != "PAYROL_DD_SSS") && (headerDetails.getKey() != "PAYROL_DD_GIS") && (headerDetails.getKey() != "PAYROL_DD_PTAX") && (headerDetails.getKey() != "PAYROL_DD_EDNADV") && (headerDetails.getKey() != "PAYROL_DD_DPTDUES") && (headerDetails.getKey() != "PAYROL_DD_DEDID19") && (headerDetails.getKey() != "PAYROL_DD_DEDID17") && (headerDetails.getKey() != "PAYROL_DD_IT")>
	     				<fo:block font-family="Courier,monospace" font-size="9pt">
	     					<fo:table>
		       					<fo:table-column column-width="10pt"/>
		     					<fo:table-column column-width="30pt"/>
		     					<fo:table-column column-width="40pt"/>
		       					<fo:table-column column-width="40pt"/>
		     					<fo:table-column column-width="30pt"/>
		       					<fo:table-column column-width="37pt"/>
		       					<fo:table-column column-width="30pt"/>
		       					<fo:table-body>
		       						<fo:table-cell><fo:block text-align="left" font-size="4pt">SNO</fo:block></fo:table-cell>
		       						<fo:table-cell><fo:block text-align="left" font-size="4pt">EMPNO</fo:block></fo:table-cell>
		       						<fo:table-cell><fo:block text-align="left" font-size="4pt">EMP NAME</fo:block></fo:table-cell>
		       						<fo:table-cell><fo:block text-align="left" font-size="4pt">DESN.</fo:block></fo:table-cell>
		       						<fo:table-cell><fo:block text-align="left" font-size="4pt">A/C NO.</fo:block></fo:table-cell>
		       						<fo:table-cell><fo:block text-align="left" font-size="4pt">RECOVERED</fo:block></fo:table-cell>
									<fo:table-cell><fo:block text-align="left" font-size="4pt">BALANCE</fo:block></fo:table-cell>
		       					</fo:table-body>
	     					</fo:table>
	     				</fo:block>
	     			</#if> 
			       	<#if (headerDetails.getKey() == "PAYROL_DD_EDNADV") || (headerDetails.getKey() == "PAYROL_DD_DPTDUES") || (headerDetails.getKey() == "PAYROL_DD_DEDID19") || (headerDetails.getKey() == "PAYROL_DD_DEDID17") || (headerDetails.getKey() == "PAYROL_DD_IT")>
	     				<fo:block font-family="Courier,monospace" font-size="9pt">
	     					<fo:table>
		       					<fo:table-column column-width="10pt"/>
		     					<fo:table-column column-width="30pt"/>
		     					<fo:table-column column-width="40pt"/>
		       					<fo:table-column column-width="40pt"/>
		     					<fo:table-column column-width="30pt"/>
		       					<fo:table-column column-width="37pt"/>
		       					<fo:table-column column-width="30pt"/>
		       					<fo:table-body>
		       						<fo:table-cell><fo:block text-align="left" font-size="4pt">SNO</fo:block></fo:table-cell>
		       						<fo:table-cell><fo:block text-align="left" font-size="4pt">EMPNO</fo:block></fo:table-cell>
		       						<fo:table-cell><fo:block text-align="left" font-size="4pt">EMP NAME</fo:block></fo:table-cell>
		       						<fo:table-cell><fo:block text-align="left" font-size="4pt">DESN.</fo:block></fo:table-cell>
		       						<fo:table-cell><fo:block text-align="left" font-size="4pt">A/C NO.</fo:block></fo:table-cell>
		       						<fo:table-cell><fo:block text-align="left" font-size="4pt">RECOVERED</fo:block></fo:table-cell>
									<fo:table-cell><fo:block text-align="left" font-size="4pt">BALANCE</fo:block></fo:table-cell>
		       					</fo:table-body>
	     					</fo:table>
	     				</fo:block>
	     			</#if> 
	     			<#if  headerDetails.getKey() == "PAYROL_DD_APGLIF">
	     				<fo:block font-family="Courier,monospace" font-size="9pt">
	     					<fo:table>
	           					<fo:table-column column-width="14pt"/>
		     					<fo:table-column column-width="22pt"/>
		     					<fo:table-column column-width="50pt"/>
		       					<fo:table-column column-width="40pt"/>
		     					<fo:table-column column-width="38pt"/>
		       					<fo:table-column column-width="35pt"/>
		       					<fo:table-body>
		       						<fo:table-cell><fo:block text-align="left" font-size="4pt">SNO</fo:block></fo:table-cell>
		       						<fo:table-cell><fo:block text-align="left" font-size="4pt">EMPNO</fo:block></fo:table-cell>
		       						<fo:table-cell><fo:block text-align="left" font-size="4pt">EMP NAME</fo:block></fo:table-cell>
		       						<fo:table-cell><fo:block text-align="left" font-size="4pt">DESIGNATION</fo:block></fo:table-cell>
		       						<fo:table-cell><fo:block text-align="right" font-size="4pt">APGLIF</fo:block></fo:table-cell>
		       						<fo:table-cell><fo:block text-align="right" font-size="4pt">APG.LN</fo:block></fo:table-cell>
		       					</fo:table-body>
	     					</fo:table>
	     				</fo:block>
	     			</#if>
	     			<#if headerDetails.getKey() == "PAYROL_DD_PTAX" >
	     				<fo:block font-family="Courier,monospace" font-size="9pt">
	     					<fo:table>
	           					<fo:table-column column-width="14pt"/>
		     					<fo:table-column column-width="22pt"/>
		     					<fo:table-column column-width="50pt"/>
		       					<fo:table-column column-width="40pt"/>
		     					<fo:table-column column-width="38pt"/>
		       					<fo:table-column column-width="35pt"/>
		       					<fo:table-body>
		       						<fo:table-cell><fo:block text-align="left" font-size="4pt">SNO</fo:block></fo:table-cell>
		       						<fo:table-cell><fo:block text-align="left" font-size="4pt">EMPNO</fo:block></fo:table-cell>
		       						<fo:table-cell><fo:block text-align="left" font-size="4pt">EMP NAME</fo:block></fo:table-cell>
		       						<fo:table-cell><fo:block text-align="left" font-size="4pt">DESN.</fo:block></fo:table-cell>
		       						<fo:table-cell><fo:block text-align="right" font-size="4pt">GROSS</fo:block></fo:table-cell>
		       						<fo:table-cell><fo:block text-align="right" font-size="4pt">AMOUNT</fo:block></fo:table-cell>
		       					</fo:table-body>
	     					</fo:table>
	     				</fo:block>
	     			</#if>
	     			<#if  headerDetails.getKey() == "PAYROL_DD_GIS">
	     				<fo:block font-family="Courier,monospace" font-size="9pt">
	     					<fo:table>
	           					<fo:table-column column-width="14pt"/>
		     					<fo:table-column column-width="22pt"/>
		     					<fo:table-column column-width="50pt"/>
		       					<fo:table-column column-width="40pt"/>
		     					<fo:table-column column-width="38pt"/>
		       					<fo:table-column column-width="35pt"/>
		       					<fo:table-body>
		       						<fo:table-cell><fo:block text-align="left" font-size="4pt">SNO</fo:block></fo:table-cell>
		       						<fo:table-cell><fo:block text-align="left" font-size="4pt">EMPNO</fo:block></fo:table-cell>
		       						<fo:table-cell><fo:block text-align="left" font-size="4pt">EMP NAME</fo:block></fo:table-cell>
		       						<fo:table-cell><fo:block text-align="left" font-size="4pt">DESN.</fo:block></fo:table-cell>
		       						<fo:table-cell><fo:block text-align="right" font-size="4pt">GIS NO</fo:block></fo:table-cell>
		       						<fo:table-cell><fo:block text-align="right" font-size="4pt">AMOUNT</fo:block></fo:table-cell>
		       					</fo:table-body>
	     					</fo:table>
	     				</fo:block>
	     			</#if>  
	     			<#if  headerDetails.getKey() == "PAYROL_DD_SSS">
	     				<fo:block font-family="Courier,monospace" font-size="9pt">
	     					<fo:table>
	           					<fo:table-column column-width="12pt"/>
		     					<fo:table-column column-width="22pt"/>
		     					<fo:table-column column-width="42pt"/>
		       					<fo:table-column column-width="35pt"/>
		     					<fo:table-column column-width="30pt"/>
		       					<fo:table-column column-width="26pt"/>
		       					<fo:table-column column-width="18pt"/>
		       					<fo:table-body>
		       						<fo:table-cell><fo:block text-align="left" font-size="4pt">SNO</fo:block></fo:table-cell>
		       						<fo:table-cell><fo:block text-align="left" font-size="4pt">EMPNO</fo:block></fo:table-cell>
		       						<fo:table-cell><fo:block text-align="left" font-size="4pt">EMP NAME</fo:block></fo:table-cell>
		       						<fo:table-cell><fo:block text-align="left" font-size="4pt">DESN.</fo:block></fo:table-cell>
		       						<fo:table-cell><fo:block text-align="left" font-size="4pt">POL.NO</fo:block></fo:table-cell>
		       						<fo:table-cell><fo:block text-align="left" font-size="4pt">PREMIUM</fo:block></fo:table-cell>
									<fo:table-cell><fo:block text-align="left" font-size="4pt">DEDUCTION</fo:block></fo:table-cell>
		       					</fo:table-body>
	     					</fo:table>
	     				</fo:block>
	     			</#if> 
	     			<fo:block font-size="8pt">---------------------------------------------------------------------------------------</fo:block>   
	        	</fo:static-content>       
	          	<fo:flow flow-name="xsl-region-body" font-family="Courier,monospace">
           			<fo:block font-family="Courier,monospace" font-size="9pt">
           				<fo:table>
           					<#assign sno=0>
           					<#if  headerDetails.getKey() == "PAYROL_DD_EPF">
	           					<fo:table-column column-width="10pt"/>
		     					<fo:table-column column-width="20pt"/>
		     					<fo:table-column column-width="40pt"/>
		       					<fo:table-column column-width="33pt"/>
		     					<fo:table-column column-width="25pt"/>
		       					<fo:table-column column-width="25pt"/>
		       					<fo:table-column column-width="24pt"/>
		       					<fo:table-column column-width="24pt"/>
	       					</#if>
		       				<#if (headerDetails.getKey() != "PAYROL_DD_EPF") && (headerDetails.getKey() != "PAYROL_DD_APGLIF") && (headerDetails.getKey() != "PAYROL_DD_SSS") && (headerDetails.getKey() != "PAYROL_DD_GIS") && (headerDetails.getKey() != "PAYROL_DD_PTAX") && (headerDetails.getKey() != "PAYROL_DD_EDNADV") && (headerDetails.getKey() != "PAYROL_DD_DPTDUES") && (headerDetails.getKey() != "PAYROL_DD_DEDID19") && (headerDetails.getKey() != "PAYROL_DD_DEDID17") && (headerDetails.getKey() != "PAYROL_DD_IT")>
		       					<fo:table-column column-width="14pt"/>
		     					<fo:table-column column-width="22pt"/>
		     					<fo:table-column column-width="40pt"/>
		       					<fo:table-column column-width="40pt"/>
		     					<fo:table-column column-width="20pt"/>
		       					<fo:table-column column-width="35pt"/>
		       					<fo:table-column column-width="30pt"/>
		       				</#if>
			       			<#if (headerDetails.getKey() == "PAYROL_DD_EDNADV") || (headerDetails.getKey() == "PAYROL_DD_DPTDUES") || (headerDetails.getKey() == "PAYROL_DD_DEDID19") || (headerDetails.getKey() == "PAYROL_DD_DEDID17") || (headerDetails.getKey() == "PAYROL_DD_IT")>
		       					<fo:table-column column-width="14pt"/>
		     					<fo:table-column column-width="22pt"/>
		     					<fo:table-column column-width="40pt"/>
		       					<fo:table-column column-width="40pt"/>
		     					<fo:table-column column-width="20pt"/>
		       					<fo:table-column column-width="33pt"/>
		       					<fo:table-column column-width="30pt"/>
		       				</#if>
	     					<#if  headerDetails.getKey() == "PAYROL_DD_APGLIF">
	           					<fo:table-column column-width="14pt"/>
		     					<fo:table-column column-width="22pt"/>
		     					<fo:table-column column-width="50pt"/>
		       					<fo:table-column column-width="40pt"/>
		     					<fo:table-column column-width="38pt"/>
		       					<fo:table-column column-width="35pt"/>
	       					</#if>
	       					<#if  headerDetails.getKey() == "PAYROL_DD_PTAX">
	           					<fo:table-column column-width="14pt"/>
		     					<fo:table-column column-width="22pt"/>
		     					<fo:table-column column-width="50pt"/>
		       					<fo:table-column column-width="40pt"/>
		     					<fo:table-column column-width="38pt"/>
		       					<fo:table-column column-width="35pt"/>
	       					</#if>
	       					<#if  headerDetails.getKey() == "PAYROL_DD_GIS">
	           					<fo:table-column column-width="14pt"/>
		     					<fo:table-column column-width="22pt"/>
		     					<fo:table-column column-width="50pt"/>
		       					<fo:table-column column-width="40pt"/>
		     					<fo:table-column column-width="38pt"/>
		       					<fo:table-column column-width="35pt"/>
	     					</#if>  
	       					<#if  headerDetails.getKey() == "PAYROL_DD_SSS">
	       						<fo:table-column column-width="12pt"/>
		     					<fo:table-column column-width="22pt"/>
		     					<fo:table-column column-width="42pt"/>
		       					<fo:table-column column-width="35pt"/>
		     					<fo:table-column column-width="40pt"/>
		       					<fo:table-column column-width="40pt"/>
		       					<fo:table-column column-width="30pt"/>
		       				</#if>
	       					<#assign totalWages =0>
   							<#assign totalEPFemplyeContribtn =0>
   							<#assign totalEPFemplyerContribtn =0>
   							<#assign totalPension =0>
   							<#assign totalRecovery =0>
   							<#assign totalBalance = 0>
   							<#assign totalGisAmt = 0>
   							<#assign totalAmt =0>
   							<#assign totalApglifAmt =0>
   							<#assign totalPfAmt =0>
   							<#assign totalGrossAmt =0>
   							<#assign totalDeduction =0>
   							<#assign totalPremium = 0>
							<fo:table-body>
								<#list deductionTypes as deductionType>
									<#assign sno=sno+1>
									<#assign emplPositionAndFulfilment=delegator.findByAnd("EmplPositionAndFulfillment", {"employeePartyId" : deductionType.getKey()})/>
									<#if  headerDetails.getKey() == "PAYROL_DD_EPF">
										<#if deductionType.getValue().get("employeeContribtn")!=0>
											<fo:table-row>
												<fo:table-cell><fo:block keep-together="always" font-size="4pt" text-align="left">${sno}</fo:block></fo:table-cell>
												<fo:table-cell><fo:block keep-together="always" font-size="4pt" text-align="left">${deductionType.getKey()?if_exists}</fo:block></fo:table-cell>
												<fo:table-cell><fo:block keep-together="always" font-size="4pt" text-align="left">${Static["org.ofbiz.order.order.OrderServices"].nameTrim((StringUtil.wrapString(Static["org.ofbiz.party.party.PartyHelper"].getPartyName(delegator, deductionType.getKey(), false))),15)}</fo:block></fo:table-cell>
						       					<#assign designationId = emplPositionAndFulfilment[0].emplPositionTypeId>
												<#assign designation = delegator.findOne("EmplPositionType", {"emplPositionTypeId" : designationId?if_exists}, true)>
												<#assign designationName=emplPositionAndFulfilment[0].name?if_exists>
						       					<fo:table-cell><fo:block keep-together="always" font-size="4pt" text-align="left"><#if designationName?has_content>${Static["org.ofbiz.order.order.OrderServices"].nameTrim((StringUtil.wrapString(designationName)),12)?if_exists}<#else><#if designation?has_content>${Static["org.ofbiz.order.order.OrderServices"].nameTrim((StringUtil.wrapString(designation.description)),12)?if_exists}</#if></#if></fo:block></fo:table-cell>		       							
						       					<fo:table-cell><fo:block keep-together="always" font-size="4pt" text-align="right">${deductionType.getValue().get("Wages")?if_exists}</fo:block></fo:table-cell>
				       							<fo:table-cell><fo:block keep-together="always" font-size="4pt" text-align="right">${deductionType.getValue().get("employeeContribtn")?if_exists}</fo:block></fo:table-cell>
				       							<fo:table-cell><fo:block keep-together="always" font-size="4pt" text-align="right">${deductionType.getValue().get("employerContribtn")?if_exists}</fo:block></fo:table-cell>
				       							<fo:table-cell><fo:block keep-together="always" font-size="4pt" text-align="right">${deductionType.getValue().get("pensionAmount")?if_exists}</fo:block></fo:table-cell>
				       							<#assign totalWages =totalWages + deductionType.getValue().get("Wages")>
				       							<#assign totalEPFemplyeContribtn =totalEPFemplyeContribtn + deductionType.getValue().get("employeeContribtn")>
				       							<#assign totalEPFemplyerContribtn =totalEPFemplyerContribtn + deductionType.getValue().get("employerContribtn")>
				       							<#assign totalPension =totalPension + deductionType.getValue().get("pensionAmount")>
				       						</fo:table-row>
			       						</#if>
			       					</#if>
		       						<#if (headerDetails.getKey() != "PAYROL_DD_EPF") && (headerDetails.getKey() != "PAYROL_DD_APGLIF") && (headerDetails.getKey() != "PAYROL_DD_SSS") && (headerDetails.getKey() != "PAYROL_DD_GIS") && (headerDetails.getKey() != "PAYROL_DD_PTAX") && (headerDetails.getKey() != "PAYROL_DD_EDNADV") && (headerDetails.getKey() != "PAYROL_DD_DPTDUES") && (headerDetails.getKey() != "PAYROL_DD_DEDID19") && (headerDetails.getKey() != "PAYROL_DD_DEDID17") && (headerDetails.getKey() != "PAYROL_DD_IT")>
	       								<#if deductionType.getValue().get("deductionAmt")!=0>
	       								<#assign accnumber = deductionType.getValue().get("accountNo")>
		       								<fo:table-row>
												<fo:table-cell><fo:block keep-together="always" font-size="4pt" text-align="left">${sno}</fo:block></fo:table-cell>
												<fo:table-cell><fo:block keep-together="always" font-size="4pt" text-align="left">${deductionType.getKey()}</fo:block></fo:table-cell>
			       								<fo:table-cell><fo:block keep-together="always" font-size="4pt" text-align="left">${Static["org.ofbiz.order.order.OrderServices"].nameTrim((StringUtil.wrapString(Static["org.ofbiz.party.party.PartyHelper"].getPartyName(delegator, deductionType.getKey(), false))),15)}</fo:block></fo:table-cell>
					       						<#assign designationId = emplPositionAndFulfilment[0].emplPositionTypeId>
												<#assign designation = delegator.findOne("EmplPositionType", {"emplPositionTypeId" : designationId?if_exists}, true)>
												<#assign designationName=emplPositionAndFulfilment[0].name?if_exists>
					       						<fo:table-cell><fo:block keep-together="always" font-size="4pt" text-align="left"><#if designationName?has_content>${Static["org.ofbiz.order.order.OrderServices"].nameTrim((StringUtil.wrapString(designationName)),12)?if_exists}<#else><#if designation?has_content>${Static["org.ofbiz.order.order.OrderServices"].nameTrim((StringUtil.wrapString(designation.description)),12)?if_exists}</#if></#if></fo:block></fo:table-cell>		       							
			       								<fo:table-cell><fo:block keep-together="always" font-size="4pt" text-align="right">${accnumber?if_exists}</fo:block></fo:table-cell>
			       								<fo:table-cell><fo:block keep-together="always" font-size="4pt" text-align="right">${deductionType.getValue().get("deductionAmt")?if_exists?string('0.00')}</fo:block></fo:table-cell>
							       				<#assign balance = deductionType.getValue().get("balance")>
							       				<fo:table-cell><fo:block keep-together="always" font-size="4pt" text-align="right"><#if balance == 0>-<#else>${balance?if_exists?string('0.00')}</#if></fo:block></fo:table-cell>
							       				<#assign totalRecovery =totalRecovery + deductionType.getValue().get("deductionAmt")>
							       				<#assign totalBalance =totalBalance + deductionType.getValue().get("balance")>
		       								</fo:table-row>
	       								</#if>
			       					</#if>
			       					<#if (headerDetails.getKey() == "PAYROL_DD_EDNADV") || (headerDetails.getKey() == "PAYROL_DD_DPTDUES") || (headerDetails.getKey() == "PAYROL_DD_DEDID19") || (headerDetails.getKey() == "PAYROL_DD_DEDID17") || (headerDetails.getKey() == "PAYROL_DD_IT")>
	       								<#if deductionType.getValue().get("deductionAmt")!=0>
		       								<fo:table-row>
												<fo:table-cell><fo:block keep-together="always" font-size="4pt" text-align="left">${sno}</fo:block></fo:table-cell>
												<fo:table-cell><fo:block keep-together="always" font-size="4pt" text-align="left">${deductionType.getKey()}</fo:block></fo:table-cell>
			       								<fo:table-cell><fo:block keep-together="always" font-size="4pt" text-align="left">${Static["org.ofbiz.order.order.OrderServices"].nameTrim((StringUtil.wrapString(Static["org.ofbiz.party.party.PartyHelper"].getPartyName(delegator, deductionType.getKey(), false))),15)}</fo:block></fo:table-cell>
					       						<#assign designationId = emplPositionAndFulfilment[0].emplPositionTypeId>
												<#assign designation = delegator.findOne("EmplPositionType", {"emplPositionTypeId" : designationId?if_exists}, true)>
												<#assign designationName=emplPositionAndFulfilment[0].name?if_exists>
					       						<fo:table-cell><fo:block keep-together="always" font-size="4pt" text-align="left"><#if designationName?has_content>${Static["org.ofbiz.order.order.OrderServices"].nameTrim((StringUtil.wrapString(designationName)),12)?if_exists}<#else><#if designation?has_content>${Static["org.ofbiz.order.order.OrderServices"].nameTrim((StringUtil.wrapString(designation.description)),12)?if_exists}</#if></#if></fo:block></fo:table-cell>		       							
			       								<fo:table-cell><fo:block keep-together="always" font-size="4pt" text-align="right" >-</fo:block></fo:table-cell>
			       								<fo:table-cell><fo:block keep-together="always" font-size="4pt" text-align="right">${deductionType.getValue().get("deductionAmt")?if_exists?string('0.00')}</fo:block></fo:table-cell>
							       				<#assign balance = deductionType.getValue().get("balance")>
							       				<fo:table-cell><fo:block keep-together="always" font-size="4pt" text-align="right" ><#if balance == 0>-<#else>${balance?if_exists?string('0.00')}</#if></fo:block></fo:table-cell>
							       				<#assign totalRecovery =totalRecovery + deductionType.getValue().get("deductionAmt")>
							       				<#assign totalBalance =totalBalance + deductionType.getValue().get("balance")>
		       								</fo:table-row>
	       								</#if>
			       					</#if>
	     							<#if  headerDetails.getKey() == "PAYROL_DD_APGLIF">
	     								<#if deductionType.getValue().get("deductionAmt")!=0>
			       							<fo:table-row>
												<fo:table-cell><fo:block keep-together="always" font-size="4pt" text-align="left">${sno}</fo:block></fo:table-cell>
												<fo:table-cell><fo:block keep-together="always" font-size="4pt" text-align="left">${deductionType.getKey()?if_exists}</fo:block></fo:table-cell>
												<fo:table-cell><fo:block keep-together="always" font-size="4pt" text-align="left">${Static["org.ofbiz.order.order.OrderServices"].nameTrim((StringUtil.wrapString(Static["org.ofbiz.party.party.PartyHelper"].getPartyName(delegator, deductionType.getKey(), false))),15)}</fo:block></fo:table-cell>
												<#assign designationId = emplPositionAndFulfilment[0].emplPositionTypeId>
												<#assign designation = delegator.findOne("EmplPositionType", {"emplPositionTypeId" : designationId?if_exists}, true)>
												<#assign designationName=emplPositionAndFulfilment[0].name?if_exists>
					       						<fo:table-cell><fo:block keep-together="always" font-size="4pt" text-align="left"><#if designationName?has_content>${Static["org.ofbiz.order.order.OrderServices"].nameTrim((StringUtil.wrapString(designationName)),15)?if_exists}<#else><#if designation?has_content>${Static["org.ofbiz.order.order.OrderServices"].nameTrim((StringUtil.wrapString(designation.description)),15)?if_exists}</#if></#if></fo:block></fo:table-cell>		       							
						       					<fo:table-cell><fo:block keep-together="always" font-size="4pt" text-align="right">${deductionType.getValue().get("deductionAmt")?if_exists}</fo:block></fo:table-cell>
				       							<fo:table-cell><fo:block keep-together="always" font-size="4pt" text-align="right">0</fo:block></fo:table-cell>
				       							<#assign totalApglifAmt =totalApglifAmt + deductionType.getValue().get("deductionAmt")>
		       								</fo:table-row>
		       							</#if>
		       						</#if>
		       						<#if  headerDetails.getKey() == "PAYROL_DD_PTAX">
		       							<#if deductionType.getValue().get("gross")!=0>
			       							<fo:table-row>
												<fo:table-cell><fo:block keep-together="always" font-size="4pt" text-align="left">${sno}</fo:block></fo:table-cell>
												<fo:table-cell><fo:block keep-together="always" font-size="4pt" text-align="left">${deductionType.getKey()?if_exists}</fo:block></fo:table-cell>
												<fo:table-cell><fo:block keep-together="always" font-size="4pt" text-align="left">${Static["org.ofbiz.order.order.OrderServices"].nameTrim((StringUtil.wrapString(Static["org.ofbiz.party.party.PartyHelper"].getPartyName(delegator, deductionType.getKey(), false))),15)}</fo:block></fo:table-cell>
												<#assign designationId = emplPositionAndFulfilment[0].emplPositionTypeId>
												<#assign designation = delegator.findOne("EmplPositionType", {"emplPositionTypeId" : designationId?if_exists}, true)>
												<#assign designationName=emplPositionAndFulfilment[0].name?if_exists>
					       						<fo:table-cell><fo:block keep-together="always" font-size="4pt" text-align="left"><#if designationName?has_content>${Static["org.ofbiz.order.order.OrderServices"].nameTrim((StringUtil.wrapString(designationName)),15)?if_exists}<#else><#if designation?has_content>${Static["org.ofbiz.order.order.OrderServices"].nameTrim((StringUtil.wrapString(designation.description)),15)?if_exists}</#if></#if></fo:block></fo:table-cell>		       							
						       					<fo:table-cell><fo:block keep-together="always" font-size="4pt" text-align="right">${deductionType.getValue().get("gross")?if_exists?string('0.00')}</fo:block></fo:table-cell>
				       							<fo:table-cell><fo:block keep-together="always" font-size="4pt" text-align="right"><#if deductionType.getValue().get("deductionAmt")?has_content>${deductionType.getValue().get("deductionAmt")?string('0.00')}<#else>0</#if></fo:block></fo:table-cell>
				       							<#assign totalGrossAmt =totalGrossAmt + deductionType.getValue().get("gross")>
				       							<#assign totalPfAmt =totalPfAmt + deductionType.getValue().get("deductionAmt")>
		       								</fo:table-row>
		       							</#if>	
		       						</#if>
		       						<#if  headerDetails.getKey() == "PAYROL_DD_GIS">
		       								<fo:table-row>
												<fo:table-cell><fo:block keep-together="always" font-size="4pt" text-align="left">${sno}</fo:block></fo:table-cell>
												<fo:table-cell><fo:block keep-together="always" font-size="4pt" text-align="left">${deductionType.getKey()?if_exists}</fo:block></fo:table-cell>
												<fo:table-cell><fo:block keep-together="always" font-size="4pt" text-align="left">${Static["org.ofbiz.order.order.OrderServices"].nameTrim((StringUtil.wrapString(Static["org.ofbiz.party.party.PartyHelper"].getPartyName(delegator, deductionType.getKey(), false))),15)}</fo:block></fo:table-cell>
												<#assign designationId = emplPositionAndFulfilment[0].emplPositionTypeId>
												<#assign designation = delegator.findOne("EmplPositionType", {"emplPositionTypeId" : designationId?if_exists}, true)>
												<#assign designationName=emplPositionAndFulfilment[0].name?if_exists>
					       						<fo:table-cell><fo:block keep-together="always" font-size="4pt" text-align="left"><#if designationName?has_content>${Static["org.ofbiz.order.order.OrderServices"].nameTrim((StringUtil.wrapString(designationName)),15)?if_exists}<#else><#if designation?has_content>${Static["org.ofbiz.order.order.OrderServices"].nameTrim((StringUtil.wrapString(designation.description)),15)?if_exists}</#if></#if></fo:block></fo:table-cell>		       							
						       					<fo:table-cell><fo:block keep-together="always" font-size="4pt" text-align="right">${deductionType.getValue().get("gisNo")}</fo:block></fo:table-cell>
						       					<fo:table-cell><fo:block keep-together="always" font-size="4pt" text-align="right">${deductionType.getValue().get("deductionAmt")?if_exists?string('0.00')}</fo:block></fo:table-cell>
				       							<#assign totalGisAmt =totalGisAmt + deductionType.getValue().get("deductionAmt")>
		       								</fo:table-row>
		       						</#if>
			       						<#if  headerDetails.getKey() == "PAYROL_DD_SSS">
					       					<fo:table-row>
					       						<fo:table-cell><fo:block keep-together="always" font-size="4pt" text-align="left">${sno}</fo:block></fo:table-cell>
					       						<fo:table-cell><fo:block keep-together="always" font-size="4pt" text-align="left">${deductionType.getKey()?if_exists}</fo:block></fo:table-cell>
					       						<fo:table-cell><fo:block keep-together="always" font-size="4pt" text-align="left">${Static["org.ofbiz.order.order.OrderServices"].nameTrim((StringUtil.wrapString(Static["org.ofbiz.party.party.PartyHelper"].getPartyName(delegator, deductionType.getKey(), false))),13)}</fo:block></fo:table-cell>
					       						<#assign designationId = emplPositionAndFulfilment[0].emplPositionTypeId>
												<#assign designation = delegator.findOne("EmplPositionType", {"emplPositionTypeId" : designationId?if_exists}, true)>
												<#assign designationName=emplPositionAndFulfilment[0].name?if_exists>
					       						<fo:table-cell><fo:block keep-together="always" font-size="4pt" text-align="left"><#if designationName?has_content>${Static["org.ofbiz.order.order.OrderServices"].nameTrim((StringUtil.wrapString(designationName)),12)?if_exists}<#else><#if designation?has_content>${Static["org.ofbiz.order.order.OrderServices"].nameTrim((StringUtil.wrapString(designation.description)),12)?if_exists}</#if></#if></fo:block></fo:table-cell>		       		
					       						<fo:table-cell>
					       						<fo:table text-align="center">
								   					<fo:table-column column-width="25pt"/>
								 					<fo:table-column column-width="25pt"/>
					       							<fo:table-body>
					       								<#assign policyPremiumDetails = deductionType.getValue().get("polDetails")>
		       											<#assign policyPremiumDet = policyPremiumDetails.entrySet()>
					       								<#list policyPremiumDet as policyPreDet>
															<fo:table-row>
																<fo:table-cell><fo:block keep-together="always" font-size="4pt" text-align="left">${policyPreDet.getKey()?if_exists}</fo:block></fo:table-cell>
			       												<fo:table-cell><fo:block keep-together="always" font-size="4pt" text-align="right">${policyPreDet.getValue()?if_exists?string('0.00')}</fo:block></fo:table-cell>
					       										<#assign totalPremium=totalPremium + policyPreDet.getValue()>
					       									</fo:table-row>
					       								</#list>
					       							</fo:table-body>
					       						</fo:table>
					       						</fo:table-cell>
			       							<fo:table-cell><fo:block keep-together="always" font-size="4pt" text-align="right">${deductionType.getValue().get("deductionAmt")?if_exists?string('0.00')}</fo:block></fo:table-cell>
			       							<#assign totalDeduction =totalDeduction + deductionType.getValue().get("deductionAmt")>
			       						 </fo:table-row>
			       					</#if>
			       					<fo:table-row>
		                            	<fo:table-cell>
		                                	<fo:block font-size="4pt">&#160;</fo:block>
		                                </fo:table-cell>
		                           </fo:table-row>
			       				</#list>
							</fo:table-body>
							<fo:table-body>
								<fo:table-row>
					   				<fo:table-cell>
					   					<fo:block font-size="8pt">---------------------------------------------------------------------------------------</fo:block>
					   				</fo:table-cell>
					   			</fo:table-row>
								<#if  headerDetails.getKey() == "PAYROL_DD_EPF">
									<fo:table-row>
										<fo:table-cell><fo:block keep-together="always" font-size="4pt" text-align="left"></fo:block></fo:table-cell>
										<fo:table-cell><fo:block keep-together="always" font-size="4pt" text-align="left"></fo:block></fo:table-cell>
										<fo:table-cell><fo:block keep-together="always" font-size="4pt" text-align="left"></fo:block></fo:table-cell>
		       							<fo:table-cell><fo:block keep-together="always" font-size="4pt" text-align="left">TOTAL:</fo:block></fo:table-cell>
		       							<fo:table-cell><fo:block keep-together="always" font-size="4pt" text-align="right">${totalWages?if_exists}</fo:block></fo:table-cell>
		       							<fo:table-cell><fo:block keep-together="always" font-size="4pt" text-align="right">${totalEPFemplyeContribtn?if_exists}</fo:block></fo:table-cell>
		       							<fo:table-cell><fo:block keep-together="always" font-size="4pt" text-align="right">${totalEPFemplyerContribtn?if_exists}</fo:block></fo:table-cell>
		       							<fo:table-cell><fo:block keep-together="always" font-size="4pt" text-align="right">${totalPension?if_exists}</fo:block></fo:table-cell>
		       						</fo:table-row>
		       					</#if>
		       					<#if (headerDetails.getKey() != "PAYROL_DD_EPF") && (headerDetails.getKey() != "PAYROL_DD_APGLIF") && (headerDetails.getKey() != "PAYROL_DD_SSS") && (headerDetails.getKey() != "PAYROL_DD_GIS") && (headerDetails.getKey() != "PAYROL_DD_PTAX") && (headerDetails.getKey() != "PAYROL_DD_EDNADV") && (headerDetails.getKey() != "PAYROL_DD_DPTDUES") && (headerDetails.getKey() != "PAYROL_DD_DEDID19") && (headerDetails.getKey() != "PAYROL_DD_DEDID17") && (headerDetails.getKey() != "PAYROL_DD_IT")>
	       							<fo:table-row>
										<fo:table-cell><fo:block keep-together="always" font-size="4pt" text-align="left"></fo:block></fo:table-cell>
										<fo:table-cell><fo:block keep-together="always" font-size="4pt" text-align="left"></fo:block></fo:table-cell>
										<fo:table-cell><fo:block keep-together="always" font-size="4pt" text-align="left">TOTAL:</fo:block></fo:table-cell>
		       							<fo:table-cell><fo:block keep-together="always" font-size="4pt" text-align="left">.</fo:block></fo:table-cell>
		       							<fo:table-cell><fo:block keep-together="always" font-size="4pt" text-align="right">.</fo:block></fo:table-cell>
		       							<fo:table-cell><fo:block keep-together="always" font-size="4pt" text-align="right">${totalRecovery?if_exists?string('0.00')}</fo:block></fo:table-cell>
		       							<fo:table-cell><fo:block keep-together="always" font-size="4pt" text-align="right">${totalBalance?if_exists?string('0.00')}</fo:block></fo:table-cell>
	       							</fo:table-row>
		       					</#if>
			       				<#if (headerDetails.getKey() == "PAYROL_DD_EDNADV") || (headerDetails.getKey() == "PAYROL_DD_DPTDUES") || (headerDetails.getKey() == "PAYROL_DD_DEDID19") || (headerDetails.getKey() == "PAYROL_DD_DEDID17") || (headerDetails.getKey() == "PAYROL_DD_IT")>
	       							<fo:table-row>
										<fo:table-cell><fo:block keep-together="always" font-size="4pt" text-align="left"></fo:block></fo:table-cell>
										<fo:table-cell><fo:block keep-together="always" font-size="4pt" text-align="left"></fo:block></fo:table-cell>
										<fo:table-cell><fo:block keep-together="always" font-size="4pt" text-align="left">TOTAL:</fo:block></fo:table-cell>
		       							<fo:table-cell><fo:block keep-together="always" font-size="4pt" text-align="left">.</fo:block></fo:table-cell>
		       							<fo:table-cell><fo:block keep-together="always" font-size="4pt" text-align="right">.</fo:block></fo:table-cell>
		       							<fo:table-cell><fo:block keep-together="always" font-size="4pt" text-align="right">${totalRecovery?if_exists?string('0.00')}</fo:block></fo:table-cell>
		       							<fo:table-cell><fo:block keep-together="always" font-size="4pt" text-align="right">${totalBalance?if_exists?string('0.00')}</fo:block></fo:table-cell>
	       							</fo:table-row>
			       				</#if>
	     						<#if  headerDetails.getKey() == "PAYROL_DD_APGLIF">
	       							<fo:table-row>
										<fo:table-cell><fo:block keep-together="always" font-size="4pt" text-align="left"></fo:block></fo:table-cell>
										<fo:table-cell><fo:block keep-together="always" font-size="4pt" text-align="left"></fo:block></fo:table-cell>
										<fo:table-cell><fo:block keep-together="always" font-size="4pt" text-align="left"></fo:block></fo:table-cell>
										<fo:table-cell><fo:block keep-together="always" font-size="4pt" text-align="left">TOTAL:</fo:block></fo:table-cell>
		       							<fo:table-cell><fo:block keep-together="always" font-size="4pt" text-align="right">${totalApglifAmt?if_exists?string('0.00')}</fo:block></fo:table-cell>
		       							<fo:table-cell><fo:block keep-together="always" font-size="4pt" text-align="right">0</fo:block></fo:table-cell>
	       							</fo:table-row>
		       					</#if>
		       					<#if  headerDetails.getKey() == "PAYROL_DD_PTAX">
	       							<fo:table-row>
										<fo:table-cell><fo:block keep-together="always" font-size="4pt" text-align="left"></fo:block></fo:table-cell>
										<fo:table-cell><fo:block keep-together="always" font-size="4pt" text-align="left"></fo:block></fo:table-cell>
										<fo:table-cell><fo:block keep-together="always" font-size="4pt" text-align="left">TOTAL:</fo:block></fo:table-cell>
		       							<fo:table-cell><fo:block keep-together="always" font-size="4pt" text-align="left">.</fo:block></fo:table-cell>
		       							<fo:table-cell><fo:block keep-together="always" font-size="4pt" text-align="right">${totalGrossAmt?if_exists?string('0.00')}</fo:block></fo:table-cell>
		       							<fo:table-cell><fo:block keep-together="always" font-size="4pt" text-align="right">${totalPfAmt?if_exists?string('0.00')}</fo:block></fo:table-cell>
	       							</fo:table-row>
		       					</#if>
		       					<#if  headerDetails.getKey() == "PAYROL_DD_GIS">
	       							<fo:table-row>
										<fo:table-cell><fo:block keep-together="always" font-size="4pt" text-align="left"></fo:block></fo:table-cell>
										<fo:table-cell><fo:block keep-together="always" font-size="4pt" text-align="left"></fo:block></fo:table-cell>
										<fo:table-cell><fo:block keep-together="always" font-size="4pt" text-align="left"></fo:block></fo:table-cell>
										<fo:table-cell><fo:block keep-together="always" font-size="4pt" text-align="left">TOTAL:</fo:block></fo:table-cell>
		       							<fo:table-cell><fo:block keep-together="always" font-size="4pt" text-align="right">.</fo:block></fo:table-cell>
		       							<fo:table-cell><fo:block keep-together="always" font-size="4pt" text-align="right">${totalGisAmt?if_exists?string('0.00')}</fo:block></fo:table-cell>
	       							</fo:table-row>
		       					</#if>
			       				<#if  headerDetails.getKey() == "PAYROL_DD_SSS">
	       							<fo:table-row>
										<fo:table-cell><fo:block keep-together="always" font-size="4pt" text-align="left"></fo:block></fo:table-cell>
										<fo:table-cell><fo:block keep-together="always" font-size="4pt" text-align="left"></fo:block></fo:table-cell>
										<fo:table-cell><fo:block keep-together="always" font-size="4pt" text-align="left"></fo:block></fo:table-cell>
		       							<fo:table-cell><fo:block keep-together="always" font-size="4pt" text-align="left">.</fo:block></fo:table-cell>
		       							<fo:table-cell><fo:block keep-together="always" font-size="4pt" text-align="right">${totalPremium?string('0.00')}</fo:block></fo:table-cell>
		       							<fo:table-cell><fo:block keep-together="always" font-size="4pt" text-align="right">${totalDeduction?string('0.00')}</fo:block></fo:table-cell>
	       							</fo:table-row>
		       					</#if>
		       					<fo:table-row>
					   				<fo:table-cell>
					   					<fo:block font-size="8pt">---------------------------------------------------------------------------------------</fo:block>
					   				</fo:table-cell>
					   			</fo:table-row>
		       				</fo:table-body>
	           			</fo:table>           		
	           		</fo:block>
	          	</fo:flow> 
	     	</fo:page-sequence>
	     	</#list>
	   	<#else>    	
			<fo:page-sequence master-reference="main">
				<fo:flow flow-name="xsl-region-body" font-family="Courier,monospace">
	   		 		<fo:block font-size="14pt">
	        			No Orders found.......!
	   		 		</fo:block>
				</fo:flow>
			</fo:page-sequence>		
    	</#if> 
    </#if>
  </fo:root>
</#escape>
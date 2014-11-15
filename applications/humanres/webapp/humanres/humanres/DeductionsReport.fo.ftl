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
      <fo:simple-page-master master-name="main" page-height="10in" page-width="12in"
      	margin-left="1in" margin-right="0.2in"  margin-top="0.2in" margin-bottom="0.2in">
        	<fo:region-body margin-top="1.6in"/>
        	<fo:region-before extent="1in"/>
        	<fo:region-after extent="1in"/>
      </fo:simple-page-master>
    </fo:layout-master-set>
    <#if errorMessage?has_content>
		<fo:page-sequence master-reference="main">
		   <fo:flow flow-name="xsl-region-body" font-family="Courier,monospace">
		      <fo:block font-size="14pt">
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
		     			<fo:block  keep-together="always" text-align="center" font-family="Courier,monospace" white-space-collapse="false" font-weight="bold" font-size="13pt">${partyGroup.groupName?if_exists}</fo:block>
						<#--<#assign partyAddressResult = dispatcher.runSync("getPartyPostalAddress", Static["org.ofbiz.base.util.UtilMisc"].toMap("partyId", "Company", "userLogin", userLogin))/>
						<fo:block  keep-together="always" text-align="center" font-family="Courier,monospace" white-space-collapse="false" font-weight="bold" font-size="13pt"><#if partyAddressResult.address1?has_content>${partyAddressResult.address1?if_exists}</#if><#if (partyAddressResult.address2?has_content)>${partyAddressResult.address2?if_exists}</#if></fo:block>-->
	     				<#assign headerCode = delegator.findOne("DeductionType", {"deductionTypeId" : headerDetails.getKey()}, true)>
	     				<fo:block keep-together="always" white-space-collapse="false" font-family="Courier,monospace" text-align="center" font-size="13pt" font-weight="bold">                                             ${headerCode.get("internalCode")?if_exists}-${headerCode.get("deductionName")?if_exists}</fo:block>
	     				<fo:block text-align="center" keep-together="always" white-space-collapse="false" font-family="Courier,monospace" font-weight="bold">&#160;                                                                  DATE: ${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(nowTimestamp, "dd-MMM-yyyy")}</fo:block>
	     				<#assign shedCode = delegator.findOne("PartyGroup", {"partyId" : parameters.partyId}, true)>
	     				<fo:block text-align="center" keep-together="always" white-space-collapse="false" font-family="Courier,monospace" font-weight="bold">UNIT CODE:<#if shedCode?has_content>&#160;   ${shedCode.groupName?if_exists}</#if>           MONTH : ${(Static["org.ofbiz.base.util.UtilDateTime"].toDateString(timePeriodStart, "MMMMM-yyyy")).toUpperCase()}         PAGE: <fo:page-number/></fo:block>	 	 	  	 	
	     				<fo:block linefeed-treatment="preserve">&#xA;</fo:block>
		     			<#if  headerDetails.getKey() == "PAYROL_DD_EPF">
	     				<fo:block font-family="Courier,monospace">
	     					<fo:table>
	           					<fo:table-column column-width="50pt"/>
		     					<fo:table-column column-width="60pt"/>
		     					<fo:table-column column-width="140pt"/>
		     					<fo:table-column column-width="140pt"/>
		     					<fo:table-column column-width="90pt"/>
		       					<fo:table-column column-width="100pt"/>
		       					<fo:table-column column-width="100pt"/>
		       					<fo:table-column column-width="90pt"/>
		       					<fo:table-body>
		       						<fo:table-cell><fo:block text-align="center" keep-together="always" font-size="12pt" font-weight="bold" border-style="solid">SL NO:</fo:block></fo:table-cell>
		       						<fo:table-cell><fo:block text-align="center" keep-together="always" font-size="12pt" font-weight="bold" border-style="solid">EMPNO</fo:block></fo:table-cell>
		       						<fo:table-cell><fo:block text-align="center" keep-together="always" font-size="12pt" font-weight="bold" border-style="solid">EMP NAME</fo:block></fo:table-cell>
		       						<fo:table-cell><fo:block text-align="center" keep-together="always" font-size="12pt" font-weight="bold" border-style="solid">DESN.</fo:block></fo:table-cell>
		       						<fo:table-cell><fo:block text-align="center" keep-together="always" font-size="12pt" font-weight="bold" border-style="solid">WAGES</fo:block></fo:table-cell>
		       						<fo:table-cell><fo:block text-align="center" keep-together="always" font-size="10pt" font-weight="bold" border-style="solid">EPFEE</fo:block></fo:table-cell>
									<fo:table-cell><fo:block text-align="center" keep-together="always" font-size="10pt" font-weight="bold" border-style="solid">EPFER</fo:block></fo:table-cell>
									<fo:table-cell><fo:block text-align="center" keep-together="always" font-size="12pt" font-weight="bold" border-style="solid">PEN</fo:block></fo:table-cell>
		       					</fo:table-body>
	     					</fo:table>
	     				</fo:block>
	     			</#if>
		       		<#if (headerDetails.getKey() != "PAYROL_DD_EPF") && (headerDetails.getKey() != "PAYROL_DD_APGLIF") && (headerDetails.getKey() != "PAYROL_DD_SSS") && (headerDetails.getKey() != "PAYROL_DD_GIS") && (headerDetails.getKey() != "PAYROL_DD_PTAX") && (headerDetails.getKey() != "PAYROL_DD_EDNADV") && (headerDetails.getKey() != "PAYROL_DD_DPTDUES") && (headerDetails.getKey() != "PAYROL_DD_DEDID19") && (headerDetails.getKey() != "PAYROL_DD_DEDID17") && (headerDetails.getKey() != "PAYROL_DD_IT")>
	     				<fo:block font-family="Courier,monospace">
	     					<fo:table>
		       					<fo:table-column column-width="50pt"/>
		     					<fo:table-column column-width="60pt"/>
		     					<fo:table-column column-width="150pt"/>
		       					<fo:table-column column-width="150pt"/>
		     					<fo:table-column column-width="130pt"/>
		       					<fo:table-column column-width="150pt"/>
		       					<fo:table-column column-width="90pt"/>
		       					<fo:table-body>
		       						<fo:table-cell><fo:block text-align="center" keep-together="always" font-size="12pt" font-weight="bold" border-style="solid">SL NO:</fo:block></fo:table-cell>
		       						<fo:table-cell><fo:block text-align="center" keep-together="always" font-size="12pt" font-weight="bold" border-style="solid">EMPNO</fo:block></fo:table-cell>
		       						<fo:table-cell><fo:block text-align="center" keep-together="always" font-size="12pt" font-weight="bold" border-style="solid">EMP NAME</fo:block></fo:table-cell>
		       						<fo:table-cell><fo:block text-align="center" keep-together="always" font-size="12pt" font-weight="bold" border-style="solid">DESIGNATION</fo:block></fo:table-cell>
		       						<fo:table-cell><fo:block text-align="center" keep-together="always" font-size="12pt" font-weight="bold" border-style="solid">A/C NO.</fo:block></fo:table-cell>
		       						<fo:table-cell><fo:block text-align="center" keep-together="always" font-size="12pt" font-weight="bold" border-style="solid">RECOVERED</fo:block></fo:table-cell>
									<fo:table-cell><fo:block text-align="center" keep-together="always" font-size="12pt" font-weight="bold" border-style="solid">BALANCE</fo:block></fo:table-cell>
		       					</fo:table-body>
	     					</fo:table>
	     				</fo:block>
	     			</#if> 
			       	<#if (headerDetails.getKey() == "PAYROL_DD_EDNADV") || (headerDetails.getKey() == "PAYROL_DD_DPTDUES") || (headerDetails.getKey() == "PAYROL_DD_DEDID19") || (headerDetails.getKey() == "PAYROL_DD_DEDID17") || (headerDetails.getKey() == "PAYROL_DD_IT")>
	     				<fo:block font-family="Courier,monospace">
	     					<fo:table>
		       					<fo:table-column column-width="50pt"/>
		     					<fo:table-column column-width="60pt"/>
		     					<fo:table-column column-width="150pt"/>
		       					<fo:table-column column-width="150pt"/>
		     					<fo:table-column column-width="130pt"/>
		       					<fo:table-column column-width="150pt"/>
		       					<fo:table-column column-width="90pt"/>
		       					<fo:table-body>
		       						<fo:table-cell><fo:block text-align="center" keep-together="always" font-size="12pt" font-weight="bold" border-style="solid">SL NO:</fo:block></fo:table-cell>
		       						<fo:table-cell><fo:block text-align="center" keep-together="always" font-size="12pt" font-weight="bold" border-style="solid">EMPNO</fo:block></fo:table-cell>
		       						<fo:table-cell><fo:block text-align="center" keep-together="always" font-size="12pt" font-weight="bold" border-style="solid">EMP NAME</fo:block></fo:table-cell>
		       						<fo:table-cell><fo:block text-align="center" keep-together="always" font-size="12pt" font-weight="bold" border-style="solid">DESIGNATION</fo:block></fo:table-cell>
		       						<fo:table-cell><fo:block text-align="center" keep-together="always" font-size="12pt" font-weight="bold" border-style="solid">A/C NO.</fo:block></fo:table-cell>
		       						<fo:table-cell><fo:block text-align="center" keep-together="always" font-size="12pt" font-weight="bold" border-style="solid">RECOVERED</fo:block></fo:table-cell>
									<fo:table-cell><fo:block text-align="center" keep-together="always" font-size="12pt" font-weight="bold" border-style="solid">BALANCE</fo:block></fo:table-cell>
		       					</fo:table-body>
	     					</fo:table>
	     				</fo:block>
	     			</#if> 
	     			<#if  headerDetails.getKey() == "PAYROL_DD_APGLIF">
	     				<fo:block font-family="Courier,monospace">
	     					<fo:table>
	           					<fo:table-column column-width="50pt"/>
		     					<fo:table-column column-width="60pt"/>
		     					<fo:table-column column-width="150pt"/>
		     					<fo:table-column column-width="180pt"/>
		     					<fo:table-column column-width="150pt"/>
		       					<fo:table-column column-width="150pt"/>
		       					<fo:table-body>
		       						<fo:table-cell><fo:block text-align="center" keep-together="always" font-size="12pt" font-weight="bold" border-style="solid">SL NO:</fo:block></fo:table-cell>
		       						<fo:table-cell><fo:block text-align="center" keep-together="always" font-size="12pt" font-weight="bold" border-style="solid">EMPNO</fo:block></fo:table-cell>
		       						<fo:table-cell><fo:block text-align="center" keep-together="always" font-size="12pt" font-weight="bold" border-style="solid">EMP NAME</fo:block></fo:table-cell>
		       						<fo:table-cell><fo:block text-align="center" keep-together="always" font-size="12pt" font-weight="bold" border-style="solid">DESIGNATION</fo:block></fo:table-cell>
		       						<fo:table-cell><fo:block text-align="center" keep-together="always" font-size="12pt" font-weight="bold" border-style="solid">APGLIF</fo:block></fo:table-cell>
		       						<fo:table-cell><fo:block text-align="center" keep-together="always" font-size="10pt" font-weight="bold" border-style="solid">APG.LN</fo:block></fo:table-cell>
		       					</fo:table-body>
	     					</fo:table>
	     				</fo:block>
	     			</#if>
	     			<#if headerDetails.getKey() == "PAYROL_DD_PTAX">
	     				<fo:block font-family="Courier,monospace">
	     					<fo:table>
	           					<fo:table-column column-width="50pt"/>
		     					<fo:table-column column-width="60pt"/>
		     					<fo:table-column column-width="150pt"/>
		     					<fo:table-column column-width="180pt"/>
		     					<fo:table-column column-width="150pt"/>
		       					<fo:table-column column-width="150pt"/>
		       					<fo:table-body>
		       						<fo:table-cell><fo:block text-align="center" keep-together="always" font-size="12pt" font-weight="bold" border-style="solid">SL NO:</fo:block></fo:table-cell>
		       						<fo:table-cell><fo:block text-align="center" keep-together="always" font-size="12pt" font-weight="bold" border-style="solid">EMPNO</fo:block></fo:table-cell>
		       						<fo:table-cell><fo:block text-align="center" keep-together="always" font-size="12pt" font-weight="bold" border-style="solid">EMP NAME</fo:block></fo:table-cell>
		       						<fo:table-cell><fo:block text-align="center" keep-together="always" font-size="12pt" font-weight="bold" border-style="solid">DESIGNATION</fo:block></fo:table-cell>
		       						<fo:table-cell><fo:block text-align="center" keep-together="always" font-size="12pt" font-weight="bold" border-style="solid">GROSS</fo:block></fo:table-cell>
		       						<fo:table-cell><fo:block text-align="center" keep-together="always" font-size="10pt" font-weight="bold" border-style="solid">AMOUNT</fo:block></fo:table-cell>
		       					</fo:table-body>
	     					</fo:table>
	     				</fo:block>
	     			</#if>
	     			<#if  headerDetails.getKey() == "PAYROL_DD_GIS">
	     				<fo:block font-family="Courier,monospace">
	     					<fo:table>
	           					<fo:table-column column-width="50pt"/>
		     					<fo:table-column column-width="60pt"/>
		     					<fo:table-column column-width="150pt"/>
		     					<fo:table-column column-width="180pt"/>
		     					<fo:table-column column-width="150pt"/>
		       					<fo:table-column column-width="150pt"/>
		       					<fo:table-body>
		       						<fo:table-cell><fo:block text-align="center" keep-together="always" font-size="12pt" font-weight="bold" border-style="solid">SL NO:</fo:block></fo:table-cell>
		       						<fo:table-cell><fo:block text-align="center" keep-together="always" font-size="12pt" font-weight="bold" border-style="solid">EMPNO</fo:block></fo:table-cell>
		       						<fo:table-cell><fo:block text-align="center" keep-together="always" font-size="12pt" font-weight="bold" border-style="solid">EMP NAME</fo:block></fo:table-cell>
		       						<fo:table-cell><fo:block text-align="center" keep-together="always" font-size="12pt" font-weight="bold" border-style="solid">DESIGNATION</fo:block></fo:table-cell>
		       						<fo:table-cell><fo:block text-align="center" keep-together="always" font-size="12pt" font-weight="bold" border-style="solid">GIS NO</fo:block></fo:table-cell>
		       						<fo:table-cell><fo:block text-align="center" keep-together="always" font-size="12pt" font-weight="bold" border-style="solid">AMOUNT</fo:block></fo:table-cell>
		       					</fo:table-body>
	     					</fo:table>
	     				</fo:block>
	     			</#if>  
	     			<#if  headerDetails.getKey() == "PAYROL_DD_SSS">
	     				<fo:block font-family="Courier,monospace">
	     					<fo:table>
	           					<fo:table-column column-width="50pt"/>
		     					<fo:table-column column-width="60pt"/>
		     					<fo:table-column column-width="150pt"/>
		       					<fo:table-column column-width="150pt"/>
		     					<fo:table-column column-width="130pt"/>
		       					<fo:table-column column-width="120pt"/>
		       					<fo:table-column column-width="120pt"/>
		       					<fo:table-body>
		       						<fo:table-cell><fo:block text-align="center" keep-together="always" font-size="12pt" font-weight="bold" border-style="solid">SL NO:</fo:block></fo:table-cell>
		       						<fo:table-cell><fo:block text-align="center" keep-together="always" font-size="12pt" font-weight="bold" border-style="solid">EMPNO</fo:block></fo:table-cell>
		       						<fo:table-cell><fo:block text-align="center" keep-together="always" font-size="12pt" font-weight="bold" border-style="solid">EMP NAME</fo:block></fo:table-cell>
		       						<fo:table-cell><fo:block text-align="center" keep-together="always" font-size="12pt" font-weight="bold" border-style="solid">DESIGNATION</fo:block></fo:table-cell>
		       						<fo:table-cell><fo:block text-align="center" keep-together="always" font-size="12pt" font-weight="bold" border-style="solid">POL.NO</fo:block></fo:table-cell>
		       						<fo:table-cell><fo:block text-align="center" keep-together="always" font-size="12pt" font-weight="bold" border-style="solid">PREMIUM</fo:block></fo:table-cell>
									<fo:table-cell><fo:block text-align="center" keep-together="always" font-size="12pt" font-weight="bold" border-style="solid">DEDUCTION</fo:block></fo:table-cell>
		       					</fo:table-body>
	     					</fo:table>
	     				</fo:block>
	     			</#if>    
	        	</fo:static-content>       
	          	<fo:flow flow-name="xsl-region-body" font-family="Courier,monospace">
           			<fo:block text-align="center">
           				<fo:table text-align="center">
           					<#assign sno=0>
           					<#if  headerDetails.getKey() == "PAYROL_DD_EPF">
	           					<fo:table-column column-width="50pt"/>
		     					<fo:table-column column-width="60pt"/>
		     					<fo:table-column column-width="140pt"/>
		     					<fo:table-column column-width="140pt"/>
		     					<fo:table-column column-width="90pt"/>
		       					<fo:table-column column-width="100pt"/>
		       					<fo:table-column column-width="100pt"/>
		       					<fo:table-column column-width="90pt"/>
	       					</#if>
		       				<#if (headerDetails.getKey() != "PAYROL_DD_EPF") && (headerDetails.getKey() != "PAYROL_DD_APGLIF") && (headerDetails.getKey() != "PAYROL_DD_SSS") && (headerDetails.getKey() != "PAYROL_DD_GIS") && (headerDetails.getKey() != "PAYROL_DD_PTAX") && (headerDetails.getKey() != "PAYROL_DD_EDNADV") && (headerDetails.getKey() != "PAYROL_DD_DPTDUES") && (headerDetails.getKey() != "PAYROL_DD_DEDID19") && (headerDetails.getKey() != "PAYROL_DD_DEDID17") && (headerDetails.getKey() != "PAYROL_DD_IT")>
		       					<fo:table-column column-width="50pt"/>
		     					<fo:table-column column-width="60pt"/>
		     					<fo:table-column column-width="150pt"/>
		       					<fo:table-column column-width="150pt"/>
		     					<fo:table-column column-width="130pt"/>
		       					<fo:table-column column-width="150pt"/>
		       					<fo:table-column column-width="90pt"/>
		       				</#if>
			       			<#if (headerDetails.getKey() == "PAYROL_DD_EDNADV") || (headerDetails.getKey() == "PAYROL_DD_DPTDUES") || (headerDetails.getKey() == "PAYROL_DD_DEDID19") || (headerDetails.getKey() == "PAYROL_DD_DEDID17") || (headerDetails.getKey() == "PAYROL_DD_IT")>
		       					<fo:table-column column-width="50pt"/>
		     					<fo:table-column column-width="60pt"/>
		     					<fo:table-column column-width="150pt"/>
		       					<fo:table-column column-width="150pt"/>
		     					<fo:table-column column-width="130pt"/>
		       					<fo:table-column column-width="150pt"/>
		       					<fo:table-column column-width="90pt"/>
		       				</#if>
	     					<#if  headerDetails.getKey() == "PAYROL_DD_APGLIF">
	           					<fo:table-column column-width="50pt"/>
		     					<fo:table-column column-width="60pt"/>
		     					<fo:table-column column-width="150pt"/>
		     					<fo:table-column column-width="180pt"/>
		     					<fo:table-column column-width="150pt"/>
		       					<fo:table-column column-width="150pt"/>
	       					</#if>
	       					<#if  headerDetails.getKey() == "PAYROL_DD_PTAX">
	           					<fo:table-column column-width="50pt"/>
		     					<fo:table-column column-width="60pt"/>
		     					<fo:table-column column-width="150pt"/>
		     					<fo:table-column column-width="180pt"/>
		     					<fo:table-column column-width="150pt"/>
		       					<fo:table-column column-width="150pt"/>
	       					</#if>
	       					<#if  headerDetails.getKey() == "PAYROL_DD_GIS">
	           					<fo:table-column column-width="50pt"/>
		     					<fo:table-column column-width="60pt"/>
		     					<fo:table-column column-width="150pt"/>
		     					<fo:table-column column-width="180pt"/>
		     					<fo:table-column column-width="150pt"/>
		       					<fo:table-column column-width="150pt"/>
	     					</#if>  
	       					<#if  headerDetails.getKey() == "PAYROL_DD_SSS">
	       						<fo:table-column column-width="50pt"/>
		     					<fo:table-column column-width="60pt"/>
		     					<fo:table-column column-width="150pt"/>
		       					<fo:table-column column-width="150pt"/>
		     					<fo:table-column column-width="130pt"/>
		       					<fo:table-column column-width="120pt"/>
		       					<fo:table-column column-width="120pt"/>
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
												<fo:table-cell><fo:block keep-together="always" border-style="solid">${sno}</fo:block></fo:table-cell>
												<fo:table-cell><fo:block keep-together="always" text-align="right" border-style="solid">${deductionType.getKey()?if_exists}</fo:block></fo:table-cell>
												<fo:table-cell><fo:block keep-together="always"  text-align="left" border-style="solid" text-indent="5pt">${Static["org.ofbiz.order.order.OrderServices"].nameTrim((StringUtil.wrapString(Static["org.ofbiz.party.party.PartyHelper"].getPartyName(delegator, deductionType.getKey(), false))),15)}</fo:block></fo:table-cell>
						       					<#assign designationId = emplPositionAndFulfilment[0].emplPositionTypeId>
												<#assign designation = delegator.findOne("EmplPositionType", {"emplPositionTypeId" : designationId?if_exists}, true)>
												<#assign designationName=emplPositionAndFulfilment[0].name?if_exists>
						       					<fo:table-cell><fo:block keep-together="always"  text-align="left" border-style="solid" text-indent="5pt"><#if designationName?has_content>${designationName?if_exists}<#else><#if designation?has_content>${designation.description?if_exists}</#if></#if></fo:block></fo:table-cell>		       							
						       					<fo:table-cell><fo:block keep-together="always" text-align="right" border-style="solid">${deductionType.getValue().get("Wages")?if_exists}</fo:block></fo:table-cell>
				       							<fo:table-cell><fo:block keep-together="always" text-align="right" border-style="solid">${deductionType.getValue().get("employeeContribtn")?if_exists}</fo:block></fo:table-cell>
				       							<fo:table-cell><fo:block keep-together="always" text-align="right" border-style="solid">${deductionType.getValue().get("employerContribtn")?if_exists}</fo:block></fo:table-cell>
				       							<fo:table-cell><fo:block keep-together="always" text-align="right" border-style="solid">${deductionType.getValue().get("pensionAmount")?if_exists}</fo:block></fo:table-cell>
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
												<fo:table-cell><fo:block keep-together="always" border-style="solid">${sno}</fo:block></fo:table-cell>
												<fo:table-cell><fo:block keep-together="always" text-align="right" border-style="solid">${deductionType.getKey()}</fo:block></fo:table-cell>
			       								<fo:table-cell><fo:block keep-together="always"  text-align="left" border-style="solid" text-indent="5pt">${Static["org.ofbiz.order.order.OrderServices"].nameTrim((StringUtil.wrapString(Static["org.ofbiz.party.party.PartyHelper"].getPartyName(delegator, deductionType.getKey(), false))),15)}</fo:block></fo:table-cell>
					       						<#assign designationId = emplPositionAndFulfilment[0].emplPositionTypeId>
												<#assign designation = delegator.findOne("EmplPositionType", {"emplPositionTypeId" : designationId?if_exists}, true)>
												<#assign designationName=emplPositionAndFulfilment[0].name?if_exists>
					       						<fo:table-cell><fo:block keep-together="always"  text-align="left" border-style="solid" text-indent="5pt"><#if designationName?has_content>${designationName?if_exists}<#else><#if designation?has_content>${designation.description?if_exists}</#if></#if></fo:block></fo:table-cell>		       							
			       								<fo:table-cell><fo:block keep-together="always" text-align="right" border-style="solid">${accnumber?if_exists}</fo:block></fo:table-cell>
			       								<fo:table-cell><fo:block keep-together="always" text-align="right" border-style="solid">${deductionType.getValue().get("deductionAmt")?if_exists?string('0.00')}</fo:block></fo:table-cell>
							       				<#assign balance = deductionType.getValue().get("balance")>
							       				<fo:table-cell><fo:block keep-together="always" text-align="right" border-style="solid"><#if balance == 0>-<#else>${balance?if_exists}</#if></fo:block></fo:table-cell>
							       				<#assign totalRecovery =totalRecovery + deductionType.getValue().get("deductionAmt")>
							       				<#assign totalBalance =totalBalance + deductionType.getValue().get("balance")>
		       								</fo:table-row>
	       								</#if>
			       					</#if>
			       					<#if (headerDetails.getKey() == "PAYROL_DD_EDNADV") || (headerDetails.getKey() == "PAYROL_DD_DPTDUES") || (headerDetails.getKey() == "PAYROL_DD_DEDID19") || (headerDetails.getKey() == "PAYROL_DD_DEDID17") || (headerDetails.getKey() == "PAYROL_DD_IT")>
	       								<#if deductionType.getValue().get("deductionAmt")!=0>
		       								<fo:table-row>
												<fo:table-cell><fo:block keep-together="always" border-style="solid">${sno}</fo:block></fo:table-cell>
												<fo:table-cell><fo:block keep-together="always" text-align="right" border-style="solid">${deductionType.getKey()}</fo:block></fo:table-cell>
			       								<fo:table-cell><fo:block keep-together="always"  text-align="left" border-style="solid" text-indent="5pt">${Static["org.ofbiz.order.order.OrderServices"].nameTrim((StringUtil.wrapString(Static["org.ofbiz.party.party.PartyHelper"].getPartyName(delegator, deductionType.getKey(), false))),15)}</fo:block></fo:table-cell>
					       						<#assign designationId = emplPositionAndFulfilment[0].emplPositionTypeId>
												<#assign designation = delegator.findOne("EmplPositionType", {"emplPositionTypeId" : designationId?if_exists}, true)>
												<#assign designationName=emplPositionAndFulfilment[0].name?if_exists>
					       						<fo:table-cell><fo:block keep-together="always"  text-align="left" border-style="solid" text-indent="5pt"><#if designationName?has_content>${designationName?if_exists}<#else><#if designation?has_content>${designation.description?if_exists}</#if></#if></fo:block></fo:table-cell>		       							
			       								<fo:table-cell><fo:block keep-together="always" text-align="right" border-style="solid">-</fo:block></fo:table-cell>
			       								<fo:table-cell><fo:block keep-together="always" text-align="right" border-style="solid">${deductionType.getValue().get("deductionAmt")?if_exists?string('0.00')}</fo:block></fo:table-cell>
							       				<#assign balance = deductionType.getValue().get("balance")>
							       				<fo:table-cell><fo:block keep-together="always" text-align="right" border-style="solid"><#if balance == 0>-<#else>${balance?if_exists}</#if></fo:block></fo:table-cell>
							       				<#assign totalRecovery =totalRecovery + deductionType.getValue().get("deductionAmt")>
							       				<#assign totalBalance =totalBalance + deductionType.getValue().get("balance")>
		       								</fo:table-row>
	       								</#if>
			       					</#if>
	     							<#if  headerDetails.getKey() == "PAYROL_DD_APGLIF">
	     								<#if deductionType.getValue().get("deductionAmt")!=0>
			       							<fo:table-row>
												<fo:table-cell><fo:block keep-together="always" border-style="solid">${sno}</fo:block></fo:table-cell>
												<fo:table-cell><fo:block keep-together="always" text-align="right" border-style="solid">${deductionType.getKey()?if_exists}</fo:block></fo:table-cell>
												<fo:table-cell><fo:block keep-together="always"  text-align="left" border-style="solid" text-indent="5pt">${Static["org.ofbiz.order.order.OrderServices"].nameTrim((StringUtil.wrapString(Static["org.ofbiz.party.party.PartyHelper"].getPartyName(delegator, deductionType.getKey(), false))),15)}</fo:block></fo:table-cell>
												<#assign designationId = emplPositionAndFulfilment[0].emplPositionTypeId>
												<#assign designation = delegator.findOne("EmplPositionType", {"emplPositionTypeId" : designationId?if_exists}, true)>
												<#assign designationName=emplPositionAndFulfilment[0].name?if_exists>
					       						<fo:table-cell><fo:block keep-together="always"  text-align="left" border-style="solid" text-indent="5pt"><#if designationName?has_content>${designationName?if_exists}<#else><#if designation?has_content>${designation.description?if_exists}</#if></#if></fo:block></fo:table-cell>		       							
						       					<fo:table-cell><fo:block keep-together="always" text-align="right" border-style="solid">${deductionType.getValue().get("deductionAmt")?if_exists}</fo:block></fo:table-cell>
				       							<fo:table-cell><fo:block keep-together="always" text-align="right" border-style="solid">0</fo:block></fo:table-cell>
				       							<#assign totalApglifAmt =totalApglifAmt + deductionType.getValue().get("deductionAmt")>
		       								</fo:table-row>
		       							</#if>
		       						</#if>
		       						<#if  headerDetails.getKey() == "PAYROL_DD_PTAX">
		       							<#if deductionType.getValue().get("gross")!=0>
			       							<fo:table-row>
												<fo:table-cell><fo:block keep-together="always" border-style="solid">${sno}</fo:block></fo:table-cell>
												<fo:table-cell><fo:block keep-together="always" text-align="right" border-style="solid">${deductionType.getKey()?if_exists}</fo:block></fo:table-cell>
												<fo:table-cell><fo:block keep-together="always"  text-align="left" border-style="solid" text-indent="5pt">${Static["org.ofbiz.order.order.OrderServices"].nameTrim((StringUtil.wrapString(Static["org.ofbiz.party.party.PartyHelper"].getPartyName(delegator, deductionType.getKey(), false))),15)}</fo:block></fo:table-cell>
												<#assign designationId = emplPositionAndFulfilment[0].emplPositionTypeId>
												<#assign designation = delegator.findOne("EmplPositionType", {"emplPositionTypeId" : designationId?if_exists}, true)>
												<#assign designationName=emplPositionAndFulfilment[0].name?if_exists>
					       						<fo:table-cell><fo:block keep-together="always"  text-align="left" border-style="solid" text-indent="5pt"><#if designationName?has_content>${designationName?if_exists}<#else><#if designation?has_content>${designation.description?if_exists}</#if></#if></fo:block></fo:table-cell>		       							
						       					<fo:table-cell><fo:block keep-together="always" text-align="right" border-style="solid">${deductionType.getValue().get("gross")?if_exists}</fo:block></fo:table-cell>
				       							<fo:table-cell><fo:block keep-together="always" text-align="right" border-style="solid"><#if deductionType.getValue().get("deductionAmt")?has_content>${deductionType.getValue().get("deductionAmt")}<#else>0</#if></fo:block></fo:table-cell>
				       							<#assign totalGrossAmt =totalGrossAmt + deductionType.getValue().get("gross")>
				       							<#assign totalPfAmt =totalPfAmt + deductionType.getValue().get("deductionAmt")>
		       								</fo:table-row>
		       							</#if>	
		       						</#if>
		       						<#if  headerDetails.getKey() == "PAYROL_DD_GIS">
		       								<fo:table-row>
												<fo:table-cell><fo:block keep-together="always" border-style="solid">${sno}</fo:block></fo:table-cell>
												<fo:table-cell><fo:block keep-together="always" text-align="right" border-style="solid">${deductionType.getKey()?if_exists}</fo:block></fo:table-cell>
												<fo:table-cell><fo:block keep-together="always"  text-align="left" border-style="solid" text-indent="5pt">${Static["org.ofbiz.order.order.OrderServices"].nameTrim((StringUtil.wrapString(Static["org.ofbiz.party.party.PartyHelper"].getPartyName(delegator, deductionType.getKey(), false))),15)}</fo:block></fo:table-cell>
												<#assign designationId = emplPositionAndFulfilment[0].emplPositionTypeId>
												<#assign designation = delegator.findOne("EmplPositionType", {"emplPositionTypeId" : designationId?if_exists}, true)>
												<#assign designationName=emplPositionAndFulfilment[0].name?if_exists>
					       						<fo:table-cell><fo:block keep-together="always"  text-align="left" border-style="solid" text-indent="5pt"><#if designationName?has_content>${designationName?if_exists}<#else><#if designation?has_content>${designation.description?if_exists}</#if></#if></fo:block></fo:table-cell>		       							
						       					<fo:table-cell><fo:block keep-together="always" text-align="right" border-style="solid">${deductionType.getValue().get("gisNo")?if_exists}</fo:block></fo:table-cell>
						       					<fo:table-cell><fo:block keep-together="always" text-align="right" border-style="solid">${deductionType.getValue().get("deductionAmt")?if_exists}</fo:block></fo:table-cell>
				       							<#assign totalGisAmt =totalGisAmt + deductionType.getValue().get("deductionAmt")>
		       								</fo:table-row>
		       						</#if>
			       						<#if  headerDetails.getKey() == "PAYROL_DD_SSS">
					       					<fo:table-row>
					       						<fo:table-cell><fo:block keep-together="always" border-style="solid">${sno}</fo:block></fo:table-cell>
					       						<fo:table-cell><fo:block keep-together="always" text-align="right" border-style="solid">${deductionType.getKey()?if_exists}</fo:block></fo:table-cell>
					       						<fo:table-cell><fo:block keep-together="always"  text-align="left" border-style="solid" text-indent="5pt">${Static["org.ofbiz.order.order.OrderServices"].nameTrim((StringUtil.wrapString(Static["org.ofbiz.party.party.PartyHelper"].getPartyName(delegator, deductionType.getKey(), false))),15)}</fo:block></fo:table-cell>
					       						<#assign designationId = emplPositionAndFulfilment[0].emplPositionTypeId>
												<#assign designation = delegator.findOne("EmplPositionType", {"emplPositionTypeId" : designationId?if_exists}, true)>
												<#assign designationName=emplPositionAndFulfilment[0].name?if_exists>
					       						<fo:table-cell><fo:block keep-together="always"  text-align="left" border-style="solid" text-indent="5pt"><#if designationName?has_content>${designationName?if_exists}<#else><#if designation?has_content>${designation.description?if_exists}</#if></#if></fo:block></fo:table-cell>		       		
					       						<fo:table-cell>
					       						<fo:table text-align="center">
								   					<fo:table-column column-width="130pt"/>
								 					<fo:table-column column-width="120pt"/>
					       							<fo:table-body>
					       								<#assign policyPremiumDetails = deductionType.getValue().get("polDetails")>
		       											<#assign policyPremiumDet = policyPremiumDetails.entrySet()>
					       								<#list policyPremiumDet as policyPreDet>
															<fo:table-row>
																<fo:table-cell><fo:block keep-together="always" text-align="right" border-style="solid">${policyPreDet.getKey()?if_exists}</fo:block></fo:table-cell>
			       												<fo:table-cell><fo:block keep-together="always" text-align="right" border-style="solid">${policyPreDet.getValue()?if_exists?string('0.00')}</fo:block></fo:table-cell>
					       										<#assign totalPremium=totalPremium + policyPreDet.getValue()>
					       									</fo:table-row>
					       								</#list>
					       							</fo:table-body>
					       						</fo:table>
					       						</fo:table-cell>
					       					<fo:table-cell><fo:block keep-together="always"  text-align="right" border-style="solid" text-indent="5pt"></fo:block></fo:table-cell>		       		
			       							<fo:table-cell><fo:block keep-together="always" text-align="right" border-style="solid">${deductionType.getValue().get("deductionAmt")?if_exists?string('0.00')}</fo:block></fo:table-cell>
			       							<#assign totalDeduction =totalDeduction + deductionType.getValue().get("deductionAmt")>
			       						</fo:table-row>
			       					</#if>
			       				</#list>
							</fo:table-body>
							<fo:table-body>
								<#if  headerDetails.getKey() == "PAYROL_DD_EPF">
									<fo:table-row>
										<fo:table-cell><fo:block keep-together="always" border-style="solid"></fo:block></fo:table-cell>
										<fo:table-cell><fo:block keep-together="always" text-align="right" border-style="solid"></fo:block></fo:table-cell>
										<fo:table-cell><fo:block keep-together="always"  text-align="left" border-style="solid" text-indent="5pt"></fo:block></fo:table-cell>
		       							<fo:table-cell><fo:block keep-together="always" text-align="center" border-style="solid">TOTAL:</fo:block></fo:table-cell>
		       							<fo:table-cell><fo:block keep-together="always" text-align="right" border-style="solid">${totalWages?if_exists}</fo:block></fo:table-cell>
		       							<fo:table-cell><fo:block keep-together="always" text-align="right" border-style="solid">${totalEPFemplyeContribtn?if_exists}</fo:block></fo:table-cell>
		       							<fo:table-cell><fo:block keep-together="always" text-align="right" border-style="solid">${totalEPFemplyerContribtn?if_exists}</fo:block></fo:table-cell>
		       							<fo:table-cell><fo:block keep-together="always" text-align="right" border-style="solid">${totalPension?if_exists}</fo:block></fo:table-cell>
		       						</fo:table-row>
		       					</#if>
		       					<#if (headerDetails.getKey()!="PAYROL_DD_EPF") && (headerDetails.getKey()!="PAYROL_DD_APGLIF") && (headerDetails.getKey()!="PAYROL_DD_SSS") && (headerDetails.getKey() != "PAYROL_DD_GIS") && (headerDetails.getKey() != "PAYROL_DD_PTAX")>
		       							<fo:table-row>
											<fo:table-cell><fo:block keep-together="always" border-style="solid"></fo:block></fo:table-cell>
											<fo:table-cell><fo:block keep-together="always" text-align="right" border-style="solid"></fo:block></fo:table-cell>
											<fo:table-cell><fo:block keep-together="always"  text-align="left" border-style="solid" text-indent="5pt">TOTAL:</fo:block></fo:table-cell>
			       							<fo:table-cell><fo:block keep-together="always" text-align="right" border-style="solid">.</fo:block></fo:table-cell>
			       							<fo:table-cell><fo:block keep-together="always" text-align="right" border-style="solid">.</fo:block></fo:table-cell>
			       							<fo:table-cell><fo:block keep-together="always" text-align="right" border-style="solid">${totalRecovery?if_exists?string('0.00')}</fo:block></fo:table-cell>
			       							<fo:table-cell><fo:block keep-together="always" text-align="right" border-style="solid">${totalBalance?if_exists?string('0.0')}</fo:block></fo:table-cell>
		       							</fo:table-row>
		       					</#if>
	     						<#if  headerDetails.getKey() == "PAYROL_DD_APGLIF">
		       							<fo:table-row>
											<fo:table-cell><fo:block keep-together="always" border-style="solid"></fo:block></fo:table-cell>
											<fo:table-cell><fo:block keep-together="always" text-align="right" border-style="solid"></fo:block></fo:table-cell>
											<fo:table-cell><fo:block keep-together="always" text-align="right" border-style="solid"></fo:block></fo:table-cell>
											<fo:table-cell><fo:block keep-together="always"  text-align="left" border-style="solid" text-indent="5pt">TOTAL:</fo:block></fo:table-cell>
			       							<fo:table-cell><fo:block keep-together="always" text-align="right" border-style="solid">${totalApglifAmt?if_exists}</fo:block></fo:table-cell>
			       							<fo:table-cell><fo:block keep-together="always" text-align="right" border-style="solid">0</fo:block></fo:table-cell>
		       							</fo:table-row>
		       					</#if>
		       					<#if  headerDetails.getKey() == "PAYROL_DD_PTAX">
		       							<fo:table-row>
											<fo:table-cell><fo:block keep-together="always" border-style="solid"></fo:block></fo:table-cell>
											<fo:table-cell><fo:block keep-together="always" text-align="right" border-style="solid"></fo:block></fo:table-cell>
											<fo:table-cell><fo:block keep-together="always"  text-align="left" border-style="solid" text-indent="5pt">TOTAL:</fo:block></fo:table-cell>
			       							<fo:table-cell><fo:block keep-together="always" text-align="right" border-style="solid">.</fo:block></fo:table-cell>
			       							<fo:table-cell><fo:block keep-together="always" text-align="right" border-style="solid">${totalGrossAmt?if_exists}</fo:block></fo:table-cell>
			       							<fo:table-cell><fo:block keep-together="always" text-align="right" border-style="solid">${totalPfAmt?if_exists}</fo:block></fo:table-cell>
		       							</fo:table-row>
		       					</#if>
		       					<#if  headerDetails.getKey() == "PAYROL_DD_GIS">
		       							<fo:table-row>
											<fo:table-cell><fo:block keep-together="always" border-style="solid"></fo:block></fo:table-cell>
											<fo:table-cell><fo:block keep-together="always" text-align="right" border-style="solid"></fo:block></fo:table-cell>
											<fo:table-cell><fo:block keep-together="always" text-align="right" border-style="solid"></fo:block></fo:table-cell>
											<fo:table-cell><fo:block keep-together="always"  text-align="left" border-style="solid" text-indent="5pt">TOTAL:</fo:block></fo:table-cell>
			       							<fo:table-cell><fo:block keep-together="always" text-align="right" border-style="solid">.</fo:block></fo:table-cell>
			       							<fo:table-cell><fo:block keep-together="always" text-align="right" border-style="solid">${totalGisAmt?if_exists}</fo:block></fo:table-cell>
		       							</fo:table-row>
		       					</#if>
			       				<#if  headerDetails.getKey() == "PAYROL_DD_SSS">
	       							<fo:table-row>
										<fo:table-cell><fo:block keep-together="always" border-style="solid"></fo:block></fo:table-cell>
										<fo:table-cell><fo:block keep-together="always" text-align="right" border-style="solid"></fo:block></fo:table-cell>
										<fo:table-cell><fo:block keep-together="always" border-style="solid"></fo:block></fo:table-cell>
										<fo:table-cell><fo:block keep-together="always"  text-align="left" border-style="solid" text-indent="5pt">TOTAL:</fo:block></fo:table-cell>
		       							<fo:table-cell><fo:block keep-together="always" text-align="right" border-style="solid">.</fo:block></fo:table-cell>
		       							<fo:table-cell><fo:block keep-together="always" text-align="right" border-style="solid">${totalPremium?string('0.00')}</fo:block></fo:table-cell>
		       							<fo:table-cell><fo:block keep-together="always" text-align="right" border-style="solid">${totalDeduction?string('0.00')}</fo:block></fo:table-cell>
	       							</fo:table-row>
		       					</#if>
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
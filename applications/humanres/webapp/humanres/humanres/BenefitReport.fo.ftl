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
 		<#if benefitFinalMap?has_content>
 		<#assign HeaderDetailsList=benefitFinalMap.entrySet()>
 			<#assign partyGroup = delegator.findOne("PartyGroup", {"partyId" : "Company"}, true)>
 			<#assign partyAddressResult = dispatcher.runSync("getPartyPostalAddress", Static["org.ofbiz.base.util.UtilMisc"].toMap("partyId", "Company", "userLogin", userLogin))/>
     		<#list HeaderDetailsList as headerDetails>
     		<#assign benefitTypes= headerDetails.getValue().entrySet()>
	     		<fo:page-sequence master-reference="main"> 	
	     			<fo:static-content flow-name="xsl-region-before">
		     			<fo:block  keep-together="always" text-align="center" font-family="Courier,monospace" white-space-collapse="false" font-weight="bold" font-size="13pt">${partyGroup.groupName?if_exists}</fo:block>
						<fo:block  keep-together="always" text-align="center" font-family="Courier,monospace" white-space-collapse="false" font-weight="bold" font-size="13pt"><#if partyAddressResult.address1?has_content>${partyAddressResult.address1?if_exists}</#if><#if (partyAddressResult.address2?has_content)>${partyAddressResult.address2?if_exists}</#if></fo:block>
	     				<#assign benefitCode = delegator.findOne("BenefitType", {"benefitTypeId" : headerDetails.getKey()}, true)>
	     				<fo:block keep-together="always" white-space-collapse="false" font-family="Courier,monospace" text-align="center" font-size="13pt" font-weight="bold">                                             ${benefitCode.benefitName?if_exists}</fo:block>
	     				<fo:block text-align="center" keep-together="always" white-space-collapse="false" font-family="Courier,monospace" font-weight="bold">&#160;                                                                  DATE: ${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(nowTimestamp, "dd-MMM-yyyy")}</fo:block>
	     				<#assign shedCode = delegator.findOne("ResponsibilityType", {"responsibilityTypeId" : parameters.partyId}, true)>
	     				<fo:block text-align="center" keep-together="always" white-space-collapse="false" font-family="Courier,monospace" font-weight="bold">UNIT CODE:<#if shedCode?has_content>&#160;   ${shedCode.description?if_exists}</#if>           MONTH : ${(Static["org.ofbiz.base.util.UtilDateTime"].toDateString(timePeriodStart, "MMMMM-yyyy")).toUpperCase()}         PAGE: <fo:page-number/></fo:block>	 	 	  	 	
	     				<fo:block linefeed-treatment="preserve">&#xA;</fo:block>
	     				<fo:block font-family="Courier,monospace">
	     					<fo:table>
	           					<fo:table-column column-width="50pt"/>
		     					<fo:table-column column-width="60pt"/>
		     					<fo:table-column column-width="150pt"/>
		     					<fo:table-column column-width="150pt"/>
		     					<fo:table-column column-width="120pt"/>
		       					<fo:table-body>
		       						<fo:table-cell><fo:block text-align="center" keep-together="always" font-size="12pt" font-weight="bold" border-style="solid">SL NO:</fo:block></fo:table-cell>
		       						<fo:table-cell><fo:block text-align="center" keep-together="always" font-size="12pt" font-weight="bold" border-style="solid">EMPNO</fo:block></fo:table-cell>
		       						<fo:table-cell><fo:block text-align="center" keep-together="always" font-size="12pt" font-weight="bold" border-style="solid">EMP NAME</fo:block></fo:table-cell>
		       						<fo:table-cell><fo:block text-align="center" keep-together="always" font-size="12pt" font-weight="bold" border-style="solid">DESIGNATION</fo:block></fo:table-cell>
		       						<fo:table-cell><fo:block text-align="center" keep-together="always" font-size="12pt" font-weight="bold" border-style="solid">AMOUNT</fo:block></fo:table-cell>
		       					</fo:table-body>
	     					</fo:table>
	     				</fo:block>
	        	</fo:static-content>       
	          	<fo:flow flow-name="xsl-region-body" font-family="Courier,monospace">
           			<fo:block text-align="center">
           				<fo:table text-align="center">
           					<#assign sno=0>
	           					<fo:table-column column-width="50pt"/>
		     					<fo:table-column column-width="60pt"/>
		     					<fo:table-column column-width="150pt"/>
		     					<fo:table-column column-width="150pt"/>
		     					<fo:table-column column-width="120pt"/>
   							<#assign totalAmount = 0>
							<fo:table-body>
								<#list benefitTypes as benefitType>
								<#assign sno=sno+1>
									<#assign emplPositionAndFulfilment=delegator.findByAnd("EmplPositionAndFulfillment", {"employeePartyId" : benefitType.getKey()})/>
									<#if benefitType.getValue().get("benefitAmt")!=0>
										<fo:table-row>
											<fo:table-cell><fo:block keep-together="always" border-style="solid">${sno}</fo:block></fo:table-cell>
											<fo:table-cell><fo:block keep-together="always" text-align="right" border-style="solid">${benefitType.getKey()?if_exists}</fo:block></fo:table-cell>
											<fo:table-cell><fo:block keep-together="always"  text-align="left" border-style="solid" text-indent="5pt">${Static["org.ofbiz.order.order.OrderServices"].nameTrim((StringUtil.wrapString(Static["org.ofbiz.party.party.PartyHelper"].getPartyName(delegator, benefitType.getKey(), false))),15)}</fo:block></fo:table-cell>
						       				<#assign designationId = emplPositionAndFulfilment[0].emplPositionTypeId>
											<#assign designation = delegator.findOne("EmplPositionType", {"emplPositionTypeId" : designationId?if_exists}, true)>
											<#assign designationName=emplPositionAndFulfilment[0].name?if_exists>
						       				<fo:table-cell><fo:block keep-together="always"  text-align="left" border-style="solid" text-indent="5pt"><#if designationName?has_content>${designationName?if_exists}<#else><#if designation?has_content>${designation.description?if_exists}</#if></#if></fo:block></fo:table-cell>		       							
					       					<fo:table-cell><fo:block keep-together="always" text-align="right" border-style="solid">${benefitType.getValue().get("benefitAmt")?if_exists}</fo:block></fo:table-cell>
			       							<#assign totalAmount =totalAmount + benefitType.getValue().get("benefitAmt")>
			       						</fo:table-row>
			       					</#if>	
			       				</#list>
							</fo:table-body>
							<fo:table-body>
       							<fo:table-row>
									<fo:table-cell><fo:block keep-together="always" border-style="solid"></fo:block></fo:table-cell>
									<fo:table-cell><fo:block keep-together="always" text-align="right" border-style="solid"></fo:block></fo:table-cell>
									<fo:table-cell><fo:block keep-together="always"  text-align="left" border-style="solid" text-indent="5pt">TOTAL:</fo:block></fo:table-cell>
									<fo:table-cell><fo:block keep-together="always" text-align="right" border-style="solid">.</fo:block></fo:table-cell>
	       							<fo:table-cell><fo:block keep-together="always" text-align="right" border-style="solid">${totalAmount?if_exists}</fo:block></fo:table-cell>
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
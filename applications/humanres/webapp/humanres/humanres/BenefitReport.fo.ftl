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
      <fo:simple-page-master master-name="main" page-height="12in" page-width="10in" >
	  		<fo:region-body margin-top=".7in"/>
	        <fo:region-before extent="1in"/>
	        <fo:region-after extent="1in"/>
      </fo:simple-page-master>
      ${setRequestAttribute("OUTPUT_FILENAME", "BenefitReport.txt")}
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
 		<#if benefitFinalMap?has_content>
 		<#assign HeaderDetailsList=benefitFinalMap.entrySet()>
 			<#assign partyGroup = delegator.findOne("PartyGroup", {"partyId" : "Company"}, true)>
 			<#assign partyAddressResult = dispatcher.runSync("getPartyPostalAddress", Static["org.ofbiz.base.util.UtilMisc"].toMap("partyId", "Company", "userLogin", userLogin))/>
     		<#list HeaderDetailsList as headerDetails>
     		<#assign benefitTypes= headerDetails.getValue().entrySet()>
	     		<fo:page-sequence master-reference="main"> 	
	     			<fo:static-content flow-name="xsl-region-before">
	     				<fo:block  keep-together="always" text-align="left" font-family="Courier,monospace" white-space-collapse="false" font-weight="bold" font-size="4pt">&#160;&#160;&#160;&#160;${partyGroup.groupName?if_exists}</fo:block>
						<fo:block  keep-together="always" text-align="left" font-family="Courier,monospace" white-space-collapse="false" font-weight="bold" font-size="4pt"><#if partyAddressResult.address1?has_content>${partyAddressResult.address1?if_exists}</#if><#if (partyAddressResult.address2?has_content)>${partyAddressResult.address2?if_exists}</#if></fo:block>
	     				<#assign benefitCode = delegator.findOne("BenefitType", {"benefitTypeId" : headerDetails.getKey()}, true)>
	     				<fo:block keep-together="always" white-space-collapse="false" font-family="Courier,monospace" text-align="left" font-size="4pt" font-weight="bold">&#160;                         ${benefitCode.get("internalCode")?if_exists}-${benefitCode.get("benefitName")?if_exists}</fo:block>
	     				<fo:block text-align="left" keep-together="always" white-space-collapse="false" font-family="Courier,monospace" font-size="4pt" font-weight="bold">&#160;                                                                  DATE: ${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(nowTimestamp, "dd-MMM-yyyy")}</fo:block>
	     				<#assign shedCode = delegator.findOne("ResponsibilityType", {"responsibilityTypeId" : parameters.partyId}, true)>
	     				<fo:block text-align="left" keep-together="always" white-space-collapse="false" font-family="Courier,monospace" font-weight="bold" font-size="4pt">UNIT CODE:<#if shedCode?has_content>&#160;   ${shedCode.groupName?if_exists}</#if>           MONTH : ${(Static["org.ofbiz.base.util.UtilDateTime"].toDateString(timePeriodStart, "MMMMM-yyyy")).toUpperCase()}         PAGE: <fo:page-number/></fo:block>	 	 	  	 	
	     				<fo:block font-size="8pt">---------------------------------------------------------------------------------------</fo:block>
	     				<fo:block font-family="Courier,monospace" font-size="9pt">
	     					<fo:table>
	           					<fo:table-column column-width="18pt"/>
		     					<fo:table-column column-width="22pt"/>
		     					<fo:table-column column-width="50pt"/>
		       					<fo:table-column column-width="40pt"/>
		     					<fo:table-column column-width="40pt"/>
		       					<fo:table-body>
		       						<fo:table-cell><fo:block text-align="left" font-size="4pt">SL NO:</fo:block></fo:table-cell>
		       						<fo:table-cell><fo:block text-align="left" font-size="4pt">EMPNO</fo:block></fo:table-cell>
		       						<fo:table-cell><fo:block text-align="left" font-size="4pt">EMP NAME</fo:block></fo:table-cell>
		       						<fo:table-cell><fo:block text-align="left" font-size="4pt">DESIGNATION</fo:block></fo:table-cell>
		       						<fo:table-cell><fo:block text-align="right" font-size="4pt">AMOUNT</fo:block></fo:table-cell>
		       					</fo:table-body>
	     					</fo:table>
	     				</fo:block>
	     			<fo:block font-size="8pt">---------------------------------------------------------------------------------------</fo:block>   
	        	</fo:static-content>       
	          	<fo:flow flow-name="xsl-region-body" font-family="Courier,monospace">
           			<fo:block font-family="Courier,monospace" font-size="9pt">
           				<fo:table>
           					<#assign sno=0>
           					<#assign pageBreak=0>
           					<fo:table-column column-width="18pt"/>
	     					<fo:table-column column-width="22pt"/>
	     					<fo:table-column column-width="50pt"/>
	       					<fo:table-column column-width="40pt"/>
	     					<fo:table-column column-width="40pt"/>
	     					<#assign totalAmount = 0>
							<fo:table-body>
								<#list benefitTypes as benefitType>
									<#assign sno=sno+1>
									<#assign pageBreak=pageBreak+1>
									<#assign emplPositionAndFulfilment=delegator.findByAnd("EmplPositionAndFulfillment", {"employeePartyId" : benefitType.getKey()})/>
									<#if benefitType.getValue().get("benefitAmt")!=0>
										<fo:table-row>
											<fo:table-cell><fo:block keep-together="always" font-size="4pt" text-align="left">${sno}</fo:block></fo:table-cell>
											<fo:table-cell><fo:block keep-together="always" font-size="4pt" text-align="left">${benefitType.getKey()?if_exists}</fo:block></fo:table-cell>
											<fo:table-cell><fo:block keep-together="always" font-size="4pt" text-align="left">${Static["org.ofbiz.order.order.OrderServices"].nameTrim((StringUtil.wrapString(Static["org.ofbiz.party.party.PartyHelper"].getPartyName(delegator, benefitType.getKey(), false))),15)}</fo:block></fo:table-cell>
						       				<#assign designationId = emplPositionAndFulfilment[0].emplPositionTypeId>
											<#assign designation = delegator.findOne("EmplPositionType", {"emplPositionTypeId" : designationId?if_exists}, true)>
											<#assign designationName=emplPositionAndFulfilment[0].name?if_exists>
						       				<fo:table-cell><fo:block keep-together="always" font-size="4pt" text-align="left"><#if designationName?has_content>${designationName?if_exists}<#else><#if designation?has_content>${designation.description?if_exists}</#if></#if></fo:block></fo:table-cell>		       							
					       					<fo:table-cell><fo:block keep-together="always" font-size="4pt" text-align="right">${benefitType.getValue().get("benefitAmt")?if_exists?string('0.00')}</fo:block></fo:table-cell>
			       							<#assign totalAmount =totalAmount + benefitType.getValue().get("benefitAmt")>
			       						</fo:table-row>
			       						<#if (pageBreak >= 55)>
				                     		<#assign pageBreak=1>
					                     	<fo:table-row>
				                            	<fo:table-cell >	
				                            		<fo:block page-break-after="always"></fo:block>
				                            	</fo:table-cell>
				                            </fo:table-row>
				                        </#if>
			       					</#if>
			       				</#list>
							</fo:table-body>
							<fo:table-body>
								<fo:table-row>
					   				<fo:table-cell>
					   					<fo:block font-size="8pt">---------------------------------------------------------------------------------------</fo:block>
					   				</fo:table-cell>
					   			</fo:table-row>
								<fo:table-row>
									<fo:table-cell><fo:block keep-together="always" font-size="4pt" text-align="left"></fo:block></fo:table-cell>
									<fo:table-cell><fo:block keep-together="always" font-size="4pt" text-align="left"></fo:block></fo:table-cell>
									<fo:table-cell><fo:block keep-together="always" font-size="4pt" text-align="left">TOTAL:</fo:block></fo:table-cell>
									<fo:table-cell><fo:block keep-together="always" font-size="4pt" text-align="left">.</fo:block></fo:table-cell>
	       							<fo:table-cell><fo:block keep-together="always" font-size="4pt" text-align="right">${totalAmount?if_exists?string('0.00')}</fo:block></fo:table-cell>
		       					</fo:table-row>
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
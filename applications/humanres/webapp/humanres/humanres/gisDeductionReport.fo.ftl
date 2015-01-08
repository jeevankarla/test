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
	<#if subTotalsDetailsMap?has_content>
 		<#assign HeaderDetailsList=subTotalsDetailsMap.entrySet()>
 			<#assign partyGroup = delegator.findOne("PartyGroup", {"partyId" : "Company"}, true)>
	     		<fo:page-sequence master-reference="main"> 	
	     			<fo:static-content flow-name="xsl-region-before">
		     			<fo:block  keep-together="always" text-align="left" font-family="Courier,monospace" white-space-collapse="false" font-weight="bold" font-size="4pt">&#160;&#160;&#160;&#160;${partyGroup.groupName?if_exists}</fo:block>
						<#--<#assign partyAddressResult = dispatcher.runSync("getPartyPostalAddress", Static["org.ofbiz.base.util.UtilMisc"].toMap("partyId", "Company", "userLogin", userLogin))/>
						<fo:block  keep-together="always" text-align="center" font-family="Courier,monospace" white-space-collapse="false" font-weight="bold" font-size="4pt"><#if partyAddressResult.address1?has_content>${partyAddressResult.address1?if_exists}</#if><#if (partyAddressResult.address2?has_content)>${partyAddressResult.address2?if_exists}</#if></fo:block>-->
	     				<#assign headerCode = delegator.findOne("DeductionType", {"deductionTypeId" : headerDetails.getKey()}, true)>
	     				<fo:block keep-together="always" white-space-collapse="false" font-family="Courier,monospace" text-align="left" font-size="4pt" font-weight="bold">&#160;                              ${headerCode.get("internalCode")?if_exists}-${headerCode.get("deductionName")?if_exists}</fo:block>
	     				<fo:block text-align="left" keep-together="always" white-space-collapse="false" font-family="Courier,monospace" font-size="4pt" font-weight="bold">&#160;                                                        DATE: ${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(nowTimestamp, "dd-MMM-yyyy")}</fo:block>
	     				<#assign shedCode = delegator.findOne("PartyGroup", {"partyId" : parameters.partyId}, true)>
	     			<fo:block keep-together="always" white-space-collapse="false" font-family="Courier,monospace" text-align="left" font-size="4pt" font-weight="bold">UNIT CODE:<#if shedCode?has_content>&#160;   ${shedCode.groupName?if_exists}</#if>           MONTH : ${(Static["org.ofbiz.base.util.UtilDateTime"].toDateString(timePeriodEnd, "MMMMM-yyyy")).toUpperCase()}         PAGE: <fo:page-number/></fo:block>	 	 	  	 	
	     			<fo:block font-size="8pt">---------------------------------------------------------------------------------------</fo:block>
	     			<#if  headerDetails.getKey() == "PAYROL_DD_GIS">
	     				<fo:block font-family="Courier,monospace" font-size="9pt">
	     					<fo:table>
	           					<fo:table-column column-width="14pt"/>
		     					<fo:table-column column-width="22pt"/>
		     					<fo:table-column column-width="55pt"/>
		       					<fo:table-column column-width="38pt"/>
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
	     			<#if headerDetails.getKey() == "PAYROL_DD_PTAX" >
	     				<fo:block font-family="Courier,monospace" font-size="9pt">
	     					<fo:table>
	           					<fo:table-column column-width="14pt"/>
		     					<fo:table-column column-width="22pt"/>
		     					<fo:table-column column-width="55pt"/>
		       					<fo:table-column column-width="35pt"/>
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
	     			<fo:block font-size="8pt">---------------------------------------------------------------------------------------</fo:block>   
	        	</fo:static-content>       
	          	<fo:flow flow-name="xsl-region-body" font-family="Courier,monospace">
           			<fo:block font-family="Courier,monospace" font-size="9pt">
           				<fo:table>
           					<#assign sno=0>
	       					<#if  headerDetails.getKey() == "PAYROL_DD_GIS">
	           					<fo:table-column column-width="14pt"/>
		     					<fo:table-column column-width="22pt"/>
		     					<fo:table-column column-width="55pt"/>
		       					<fo:table-column column-width="38pt"/>
		     					<fo:table-column column-width="38pt"/>
		       					<fo:table-column column-width="35pt"/>
	     					</#if> 
	     					<#if  headerDetails.getKey() == "PAYROL_DD_PTAX">
	           					<fo:table-column column-width="14pt"/>
		     					<fo:table-column column-width="22pt"/>
		     					<fo:table-column column-width="55pt"/>
		       					<fo:table-column column-width="35pt"/>
		     					<fo:table-column column-width="38pt"/>
		       					<fo:table-column column-width="35pt"/>
	       					</#if>
	     					<#assign totalGisAmt = 0>
	     					<#assign totalPfAmt = 0>
	     					<#assign totalGrossAmt = 0>
	     					<fo:table-body>
	     					 <#list HeaderDetailsList as headerDetails>
       						<#assign employeeDetailsList = headerDetails.getValue().entrySet()>
       						<#list employeeDetailsList as employeeDetList>
       						<#assign employDetailsList = employeeDetList.getValue()>
       						<#assign subTotalGisAmt = 0>
       						<#assign subTotalPfAmt = 0>
       						<#list employDetailsList as employDetails>
       						<#assign sno=sno+1>
       						<#if  headerDetails.getKey() == "PAYROL_DD_GIS">
		   						<#assign emplPositionAndFulfilment=delegator.findByAnd("EmplPositionAndFulfillment", {"employeePartyId" : employDetails.get("employeeId")})/>
		   							<fo:table-row>
		   								<fo:table-cell><fo:block keep-together="always" font-size="4pt" text-align="left">${sno}</fo:block></fo:table-cell>
		   								<fo:table-cell><fo:block keep-together="always" font-size="4pt" text-align="left">${Static["org.ofbiz.party.party.PartyServices"].getPartyInternal(delegator, employDetails.get("employeeId"))}</fo:block></fo:table-cell>
										<#assign personDetails = delegator.findOne("Person", {"partyId" : employDetails.get("employeeId")}, true)>
										<fo:table-cell><fo:block keep-together="always" font-size="4pt" text-align="left"><#if personDetails?has_content>${(personDetails.nickname).toUpperCase()?if_exists}<#else></#if></fo:block></fo:table-cell>										
										<#assign designationId = emplPositionAndFulfilment[0].emplPositionTypeId>
										<#assign designation = delegator.findOne("EmplPositionType", {"emplPositionTypeId" : designationId?if_exists}, true)>
										<#assign designationName=emplPositionAndFulfilment[0].name?if_exists>
						       			<fo:table-cell><fo:block keep-together="always" font-size="4pt" text-align="left"><#if designationName?has_content>${Static["org.ofbiz.order.order.OrderServices"].nameTrim((StringUtil.wrapString(designationName)),18)?if_exists}<#else><#if designation?has_content>${Static["org.ofbiz.order.order.OrderServices"].nameTrim((StringUtil.wrapString(designation.description)),18)?if_exists}</#if></#if></fo:block></fo:table-cell>		       							
		   								<#assign gisDetail = delegator.findOne("EmployeeDetail", {"partyId" : employDetails.get("employeeId")}, false)>
		   								<fo:table-cell><fo:block keep-together="always" font-size="4pt" text-align="right"><#if gisDetail?has_content>${gisDetail.presentEpf?if_exists}<#else>0 </#if></fo:block></fo:table-cell>
		   								<fo:table-cell><fo:block keep-together="always" font-size="4pt" text-align="right">${employDetails.get("gisAmount")?if_exists?string('0.00')}</fo:block></fo:table-cell>
		   								<#assign totalGisAmt =totalGisAmt + employDetails.get("gisAmount")>
		   								<#assign subTotalGisAmt =subTotalGisAmt + employDetails.get("gisAmount")>
		   							</fo:table-row>
       							</#if>
       							<#if  headerDetails.getKey() == "PAYROL_DD_PTAX">
		   						<#assign emplPositionAndFulfilment=delegator.findByAnd("EmplPositionAndFulfillment", {"employeePartyId" : employDetails.get("employeeId")})/>
		   							<fo:table-row>
		   								<fo:table-cell><fo:block keep-together="always" font-size="4pt" text-align="left">${sno}</fo:block></fo:table-cell>
		   								<fo:table-cell><fo:block keep-together="always" font-size="4pt" text-align="left">${Static["org.ofbiz.party.party.PartyServices"].getPartyInternal(delegator, employDetails.get("employeeId"))}</fo:block></fo:table-cell>
		   								<#assign personDetails = delegator.findOne("Person", {"partyId" : employDetails.get("employeeId")}, true)>
										<fo:table-cell><fo:block keep-together="always" font-size="4pt" text-align="left"><#if personDetails?has_content>${(personDetails.nickname).toUpperCase()?if_exists}<#else></#if></fo:block></fo:table-cell>										
										<#assign designationId = emplPositionAndFulfilment[0].emplPositionTypeId>
										<#assign designation = delegator.findOne("EmplPositionType", {"emplPositionTypeId" : designationId?if_exists}, true)>
										<#assign designationName=emplPositionAndFulfilment[0].name?if_exists>
						       			<fo:table-cell><fo:block keep-together="always" font-size="4pt" text-align="left"><#if designationName?has_content>${Static["org.ofbiz.order.order.OrderServices"].nameTrim((StringUtil.wrapString(designationName)),18)?if_exists}<#else><#if designation?has_content>${Static["org.ofbiz.order.order.OrderServices"].nameTrim((StringUtil.wrapString(designation.description)),18)?if_exists}</#if></#if></fo:block></fo:table-cell>		       							
		   								<fo:table-cell><fo:block keep-together="always" font-size="4pt" text-align="right">${employDetails.get("gross")?if_exists?string('0.00')}</fo:block></fo:table-cell>
		   								<fo:table-cell><fo:block keep-together="always" font-size="4pt" text-align="right">${employDetails.get("pfAmount")?if_exists?string('0.00')}</fo:block></fo:table-cell>
		   								<#assign totalGrossAmt =totalGrossAmt + deductionType.getValue().get("gross")>
		   								<#assign totalPfAmt =totalPfAmt + employDetails.get("pfAmount")>
		   								<#assign subTotalPfAmt =subTotalPfAmt + employDetails.get("pfAmount")>
		   							</fo:table-row>
       							</#if>
       						</#list>
       						<fo:table-row>
					   				<fo:table-cell>
								         <fo:block font-size="4pt">&#160;</fo:block>
					   				</fo:table-cell>
					   			</fo:table-row>
					   			<#if  headerDetails.getKey() == "PAYROL_DD_GIS">
       							<fo:table-row>
       								<fo:table-cell><fo:block keep-together="always" font-size="4pt" text-align="left"></fo:block></fo:table-cell>
       								<fo:table-cell><fo:block keep-together="always" font-size="4pt" text-align="left"></fo:block></fo:table-cell>
       								<fo:table-cell><fo:block keep-together="always" font-size="4pt" text-align="left"></fo:block></fo:table-cell>
					       			<fo:table-cell><fo:block keep-together="always" font-size="4pt" text-align="left">Total:</fo:block></fo:table-cell>		       							
       								<fo:table-cell><fo:block keep-together="always" font-size="4pt" text-align="right"></fo:block></fo:table-cell>
       								<fo:table-cell><fo:block keep-together="always" font-size="4pt" text-align="right">${subTotalGisAmt?if_exists?string('0.00')}</fo:block></fo:table-cell>
       							</fo:table-row>
       							</#if>
       							<#if  headerDetails.getKey() == "PAYROL_DD_PTAX">
       							<fo:table-row>
       								<fo:table-cell><fo:block keep-together="always" font-size="4pt" text-align="left"></fo:block></fo:table-cell>
       								<fo:table-cell><fo:block keep-together="always" font-size="4pt" text-align="left"></fo:block></fo:table-cell>
       								<fo:table-cell><fo:block keep-together="always" font-size="4pt" text-align="left"></fo:block></fo:table-cell>
					       			<fo:table-cell><fo:block keep-together="always" font-size="4pt" text-align="left">Total:</fo:block></fo:table-cell>		       							
       								<fo:table-cell><fo:block keep-together="always" font-size="4pt" text-align="right"></fo:block></fo:table-cell>
       								<fo:table-cell><fo:block keep-together="always" font-size="4pt" text-align="right">${subTotalPfAmt?if_exists?string('0.00')}</fo:block></fo:table-cell>
       							</fo:table-row>
       							</#if>
       							<fo:table-row>
					   				<fo:table-cell>
								         <fo:block font-size="4pt">&#160;</fo:block>
					   				</fo:table-cell>
					   			</fo:table-row>
       						</#list>
       						</#list>
							</fo:table-body>
							<fo:table-body>
								<fo:table-row>
					   				<fo:table-cell>
					   					<fo:block font-size="8pt">---------------------------------------------------------------------------------------</fo:block>
					   				</fo:table-cell>
					   			</fo:table-row>
					   			<fo:table-row>
					   				<fo:table-cell>
								         <fo:block font-size="4pt">&#160;</fo:block>
					   				</fo:table-cell>
					   			</fo:table-row>
		       					<#if  headerDetails.getKey() == "PAYROL_DD_GIS">
	       							<fo:table-row>
										<fo:table-cell><fo:block keep-together="always" font-size="4pt" text-align="left"></fo:block></fo:table-cell>
										<fo:table-cell><fo:block keep-together="always" font-size="4pt" text-align="left"></fo:block></fo:table-cell>
										<fo:table-cell><fo:block keep-together="always" font-size="4pt" text-align="left"></fo:block></fo:table-cell>
										<fo:table-cell><fo:block keep-together="always" font-size="4pt" text-align="left"></fo:block></fo:table-cell>
		       							<fo:table-cell><fo:block keep-together="always" font-size="4pt" text-align="right"></fo:block></fo:table-cell>
		       							<fo:table-cell><fo:block keep-together="always" font-size="4pt" text-align="right">${totalGisAmt?if_exists?string('0.00')}</fo:block></fo:table-cell>
	       							</fo:table-row>
		       					</#if>
		       					<#if  headerDetails.getKey() == "PAYROL_DD_PTAX">
	       							<fo:table-row>
										<fo:table-cell><fo:block keep-together="always" font-size="4pt" text-align="left"></fo:block></fo:table-cell>
										<fo:table-cell><fo:block keep-together="always" font-size="4pt" text-align="left"></fo:block></fo:table-cell>
										<fo:table-cell><fo:block keep-together="always" font-size="4pt" text-align="left"></fo:block></fo:table-cell>
		       							<fo:table-cell><fo:block keep-together="always" font-size="4pt" text-align="left"></fo:block></fo:table-cell>
		       							<fo:table-cell><fo:block keep-together="always" font-size="4pt" text-align="right">${totalGrossAmt?if_exists?string('0.00')}</fo:block></fo:table-cell>
		       							<fo:table-cell><fo:block keep-together="always" font-size="4pt" text-align="right">${totalPfAmt?if_exists?string('0.00')}</fo:block></fo:table-cell>
	       							</fo:table-row>
		       					</#if>
								<fo:table-row>
					   				<fo:table-cell>
								         <fo:block font-size="4pt">&#160;</fo:block>
					   				</fo:table-cell>
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
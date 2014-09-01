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
      <fo:simple-page-master master-name="main" page-height="11in" page-width="10in"
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
 		<#if headerDetailsMap?has_content>
 			<#assign partyGroup = delegator.findOne("PartyGroup", {"partyId" : "Company"}, true)>
    		<#assign postalAddress=delegator.findByAnd("PartyAndPostalAddress", {"partyId" : "Company"})/>
     		<fo:page-sequence master-reference="main"> 	 <#-- the footer -->
     			<fo:static-content flow-name="xsl-region-before">
	     			<fo:block  keep-together="always" text-align="center" font-family="Courier,monospace" white-space-collapse="false" font-weight="bold" font-size="13pt">${partyGroup.groupName?if_exists}</fo:block>
					<fo:block  keep-together="always" text-align="center" font-family="Courier,monospace" white-space-collapse="false" font-weight="bold" font-size="13pt">${postalAddress[0].address1?if_exists}</fo:block>
	     			<#if (parameters.type)=="benefits">
	     				<fo:block keep-together="always" white-space-collapse="false" font-family="Courier,monospace" text-align="center" font-size="13pt" font-weight="bold">${benefitDescMap[(parameters.benefitTypeId)].toUpperCase()} FOR THE MONTH OF : ${(Static["org.ofbiz.base.util.UtilDateTime"].toDateString(timePeriodStart, "MMMMM-yyyy")).toUpperCase()}</fo:block>
	     			</#if>
	     			<#if (parameters.type)=="deductions">
	     				<fo:block keep-together="always" white-space-collapse="false" font-family="Courier,monospace" text-align="center" font-size="13pt" font-weight="bold">${dedDescMap[(parameters.dedTypeId)].toUpperCase()} FOR THE MONTH OF: ${(Static["org.ofbiz.base.util.UtilDateTime"].toDateString(timePeriodStart, "MMMMM-yyyy")).toUpperCase()}</fo:block>
	     			</#if>
	     			<fo:block text-align="right" keep-together="always" white-space-collapse="false" font-family="Courier,monospace" font-weight="bold">&#160;                                                                  DATE: ${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(nowTimestamp, "dd-MMM-yyyy")}</fo:block>
	     			<fo:block text-align="right" keep-together="always" white-space-collapse="false" font-family="Courier,monospace" font-weight="bold">&#160;                                                                  PAGE: <fo:page-number/></fo:block>	 	 	  	 	
	     			<fo:block linefeed-treatment="preserve">&#xA;</fo:block>
	     			<fo:block font-family="Courier,monospace">
	     				<fo:table>
	     					<fo:table-column column-width="150pt"/>
	     					<fo:table-column column-width="200pt"/>
	     					<#list headerItemIdsList as headerItem>
	       					<fo:table-column column-width="200"/>
	       					</#list>	
	       					<fo:table-column column-width="180pt"/>
	       					<fo:table-body>
	       						<fo:table-cell><fo:block text-align="center" font-size="10pt" font-weight="bold" border-style="solid">EmplNo</fo:block></fo:table-cell>
	       						<fo:table-cell><fo:block text-align="center" font-size="10pt" font-weight="bold" border-style="solid">Name</fo:block></fo:table-cell>
	       						<#if parameters.type=="benefits">
		       							<fo:table-cell border-style="solid"><fo:block text-align="center" font-size="10pt" font-weight="bold">${benefitDescMap[(parameters.benefitTypeId)]?if_exists}(Rs)</fo:block></fo:table-cell>
		       					</#if>	
	       						<#if parameters.type=="deductions">
		       							<fo:table-cell border-style="solid"><fo:block text-align="center" font-size="10pt" font-weight="bold">${dedDescMap[(parameters.dedTypeId)]?if_exists}(Rs)</fo:block></fo:table-cell>
	       						</#if>
	       					</fo:table-body>
	     				</fo:table>
	     			</fo:block>
	        	</fo:static-content>       
	          <fo:flow flow-name="xsl-region-body" font-family="Courier,monospace">
          		<#assign headerDetailsList=headerDetailsMap.entrySet()>
          		<#list headerDetailsList as header>	
           			<fo:block text-align="center">
           				<fo:table text-align="center">
	           				<fo:table-column column-width="150pt"/>
	           				<fo:table-column column-width="200pt"/>
	     					<#list headerItemIdsList as headerItem>
	       					<fo:table-column column-width="200"/>
	       					</#list>	
	       					<fo:table-column column-width="180pt"/>
	       					<fo:table-body>
	       						<fo:table-row>
		       						<fo:table-cell><fo:block keep-together="always" border-style="solid">${header.getKey()}</fo:block></fo:table-cell>
		       						<fo:table-cell><fo:block keep-together="always"  text-align="left" border-style="solid" text-indent="5pt">${Static["org.ofbiz.order.order.OrderServices"].nameTrim((StringUtil.wrapString(Static["org.ofbiz.party.party.PartyHelper"].getPartyName(delegator, header.getKey(), false))),15)}</fo:block></fo:table-cell>
			       						<#list headerItemIdsList as headerItem>
			       							<#if parameters.type=="benefits">
			       								<fo:table-cell border-style="solid"><fo:block text-align="right" keep-together="always" font-weight="bold">${header.getValue().get(headerItem)?if_exists}</fo:block></fo:table-cell>
			       							</#if>
			       							<#if parameters.type=="deductions">
			       								<fo:table-cell border-style="solid"><fo:block text-align="right" keep-together="always" font-weight="bold">${(header.getValue().get(headerItem)*(-1))?if_exists}</fo:block></fo:table-cell>
			       							</#if>
			       						</#list>
	       						</fo:table-row>	
	       					</fo:table-body>
	           			</fo:table>           		
	           		</fo:block>
	           	</#list>
	           	<fo:block text-align="center">
	       			<fo:table text-align="center">
	       				<fo:table-column column-width="150pt"/>
	       				<fo:table-column column-width="200pt"/>
	 					<#list headerItemIdsList as headerItem>
	   					<fo:table-column column-width="200"/>
	   					</#list>	
	   					<fo:table-column column-width="180pt"/>
	   					<fo:table-body>
	   						<fo:table-row>
	       						<fo:table-cell><fo:block keep-together="always" border-style="solid">.</fo:block></fo:table-cell>
	       						<fo:table-cell><fo:block keep-together="always"  text-align="center" border-style="solid" text-indent="5pt" font-weight="bold">Total</fo:block></fo:table-cell>
		       						<#list headerItemIdsList as headerItem>
		       							<#if parameters.type=="benefits">
		       								<fo:table-cell border-style="solid"><fo:block text-align="right" keep-together="always" font-weight="bold">${totalBenefitsMap.get(headerItem)?if_exists}</fo:block></fo:table-cell>
		       							</#if>
		       							<#if parameters.type=="deductions">
		       								<fo:table-cell border-style="solid"><fo:block text-align="right" keep-together="always" font-weight="bold">${((totalDeductionsMap.get(headerItem))*(-1))?if_exists}</fo:block></fo:table-cell>
		       							</#if>
		       						</#list>
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
	        			No ${parameters.type?if_exists} found.......!
	   		 		</fo:block>
				</fo:flow>
			</fo:page-sequence>		
    	</#if> 
    </#if>
  </fo:root>
</#escape>
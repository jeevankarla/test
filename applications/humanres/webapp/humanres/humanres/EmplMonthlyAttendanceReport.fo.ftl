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
      <fo:simple-page-master master-name="main" page-height="12in" page-width="12in"
        margin-top="0.1in" margin-bottom="0.1in" margin-left=".5in" margin-right=".5in">
          <fo:region-body margin-top="1.5in"/>
          <fo:region-before extent="1in"/>
          <fo:region-after extent="1in"/>
      </fo:simple-page-master>
    </fo:layout-master-set>
   <#if EmplWiseMap?has_content>
   		<#assign EmplWiseList=EmplWiseMap.entrySet()>
   		<#assign partyGroup = delegator.findOne("PartyGroup", {"partyId" : "Company"}, true)>
     			<#assign partyAddressResult = dispatcher.runSync("getPartyPostalAddress", Static["org.ofbiz.base.util.UtilMisc"].toMap("partyId", "Company", "userLogin", userLogin))/>
   	<#list EmplWiseList as EmplDetls>	
     <fo:page-sequence master-reference="main"> 	
       	  <fo:static-content flow-name="xsl-region-before">
     			
     			<fo:block white-space-collapse="false" font-weight="bold" text-align="center" keep-together="always">${partyGroup.groupName?if_exists}  <#if partyAddressResult.address1?has_content>${partyAddressResult.address1?if_exists}</#if><#if (partyAddressResult.address2?has_content)>${partyAddressResult.address2?if_exists}</#if></fo:block>
        	 	<fo:block text-align="center" white-space-collapse="false" font-weight="bold">&#160; DATE BETWEEN :  ${(Static["org.ofbiz.base.util.UtilDateTime"].toDateString(timePeriodStart, "dd-MMM-yyyy")).toUpperCase()} TO  ${(Static["org.ofbiz.base.util.UtilDateTime"].toDateString(timePeriodEnd, "dd-MMM-yyyy")).toUpperCase()}             ${uiLabelMap.CommonPage}No: <fo:page-number/> </fo:block>
        		<fo:block linefeed-treatment="preserve"> &#xA;</fo:block>
        		<fo:block text-align="left" white-space-collapse="false" keep-together="always" font-weight="bold">EMPLOYEE WISE DETAILS REPORT                 NO :  ${EmplDetls.getKey()}                    NAME :  ${Static["org.ofbiz.party.party.PartyHelper"].getPartyName(delegator, EmplDetls.getKey(), false)}</fo:block>
        		<fo:block linefeed-treatment="preserve"> &#xA;</fo:block>
        		<fo:block white-space-collapse="false" keep-together="always" font-weight="bold">Date                                      Time                              Tot                Shift          W/O         ABS           Leave           MissPunch  OOD/CH</fo:block>
        		<fo:block white-space-collapse="false" keep-together="always" font-weight="bold">IN                    OUT                 IN              OUT                                                 SS/GH</fo:block>
        	</fo:static-content>  
          <fo:flow flow-name="xsl-region-body" font-family="Helvetica">          
          	<#assign dayWiseEmplDetls= EmplDetls.getValue().entrySet()>
          	<fo:block>
          		<fo:table>
          			<fo:table-column column-width="410pt"/>
          			<fo:table-column column-width="60pt"/>
          			<fo:table-column column-width="60pt"/>
          			<fo:table-column column-width="60pt"/>
          			<fo:table-body>
          				<fo:table-row>
          					<fo:table-cell>
          						<#list dayWiseEmplDetls as dayWise>
						            <fo:block>
						            	<fo:table>
						            		<fo:table-column column-width="80pt"/>
						            		<fo:table-column column-width="70pt"/>
						            		<fo:table-column column-width="70pt"/>
						            		<fo:table-column column-width="60pt"/>
						            		<fo:table-column column-width="75pt"/>
						            		<fo:table-column column-width="30pt"/>
						            		<fo:table-column column-width="30pt"/>
						            		<fo:table-column column-width="30pt"/>
						            		<fo:table-body>
						            			<fo:table-row>
						            				<fo:table-cell>
						            					<fo:block keep-together="always">${dayWise.getValue().get("IN").get("punchdate")?if_exists}</fo:block>
						            				</fo:table-cell>
						            				<fo:table-cell>
						            					<fo:block keep-together="always"><#if dayWise.getValue().get("OUT")?has_content>${dayWise.getValue().get("OUT").get("punchdate")?if_exists}</#if></fo:block>
						            				</fo:table-cell>
						            				<fo:table-cell>
						            					<fo:block keep-together="always">${dayWise.getValue().get("IN").get("punchTime")?if_exists}</fo:block>
						            				</fo:table-cell>
						            				<fo:table-cell>
						            					<fo:block keep-together="always"><#if dayWise.getValue().get("OUT")?has_content>${dayWise.getValue().get("OUT").get("punchTime")?if_exists}</#if></fo:block>
						            				</fo:table-cell>
						            				<fo:table-cell>
						            					<fo:block keep-together="always"><#if dayWise.getValue().get("OUT")?has_content>${((Static["org.ofbiz.base.util.UtilDateTime"].getInterval(dayWise.getValue().get("IN").get("dateFormat")?if_exists,dayWise.getValue().get("OUT").get("dateFormat")))/(1000*60*60))?if_exists?string("#0.00")}</#if></fo:block>
						            				</fo:table-cell>
						            				<fo:table-cell>
						            					<fo:block keep-together="always"><#if dayWise.getValue().get("IN").get("shiftType")?has_content>${dayWise.getValue().get("IN").get("shiftType").replace("SHIFT_","")?if_exists}</#if></fo:block>
						            				</fo:table-cell>
						            			</fo:table-row>
						            		</fo:table-body>
						            	</fo:table>
						            </fo:block>	
						            </#list>
          					</fo:table-cell>
          					<fo:table-cell>
          						<#if EmplDetailsMap.get(EmplDetls.getKey()).get("weekOfList")?has_content>
	          						<#list EmplDetailsMap.get(EmplDetls.getKey()).get("weekOfList") as empl>
	          						<fo:block white-space-collapse="false" keep-together="always">${(Static["org.ofbiz.base.util.UtilDateTime"].toDateString(empl, "dd/MM"))} WO</fo:block>
	          						</#list>
	          					</#if>	
          					</fo:table-cell>
          					<fo:table-cell></fo:table-cell>
          					<fo:table-cell>
          						<#if EmplDetailsMap.get(EmplDetls.getKey()).get("leaveList")?has_content>
	          						<#list EmplDetailsMap.get(EmplDetls.getKey()).get("leaveList") as leave>
	          							<fo:block white-space-collapse="false" keep-together="always">${(Static["org.ofbiz.base.util.UtilDateTime"].toDateString(leave.fromDate, "dd/MM"))} ${leave.leaveTypeId}</fo:block>
	          						</#list>
	          					</#if>	
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
        			No Employee Found.......!
   		 		</fo:block>
			</fo:flow>
		</fo:page-sequence>	    
    </#if>        
  </fo:root>
</#escape>
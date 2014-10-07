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
        	 	<fo:block white-space-collapse="false" font-weight="bold">&#160;                                                                          DATE BETWEEN :  ${(Static["org.ofbiz.base.util.UtilDateTime"].toDateString(timePeriodStart, "dd-MMM-yyyy")).toUpperCase()} TO  ${(Static["org.ofbiz.base.util.UtilDateTime"].toDateString(timePeriodEnd, "dd-MMM-yyyy")).toUpperCase()}                                                    ${uiLabelMap.CommonPage}No: <fo:page-number/> </fo:block>
        		<fo:block linefeed-treatment="preserve"> &#xA;</fo:block>
        		<fo:block text-align="left" white-space-collapse="false" keep-together="always" font-weight="bold">EMPLOYEE WISE DETAILS REPORT                 NO :  ${EmplDetls.getKey()}                    NAME :  ${Static["org.ofbiz.party.party.PartyHelper"].getPartyName(delegator, EmplDetls.getKey(), false)}</fo:block>
        		<fo:block linefeed-treatment="preserve"> &#xA;</fo:block>
        		<fo:block white-space-collapse="false" keep-together="always" font-weight="bold">&#160;        Date                                 Time                   Tot             Shift             W/O         ABS           Leave       MissPunch            OOD                  CH</fo:block>
        		<fo:block white-space-collapse="false" keep-together="always" font-weight="bold">IN                OUT              IN              OUT                                                 SS/GH                                                                    Date       Tot</fo:block>
        	</fo:static-content>  
          <fo:flow flow-name="xsl-region-body" font-family="Helvetica">          
          	<#assign dayWiseEmplDetls= EmplDetls.getValue().entrySet()>
          	<fo:block>
          		<fo:table>
          			<fo:table-column column-width="370pt"/>
          			<fo:table-column column-width="60pt"/>
          			<fo:table-column column-width="45pt"/>
          			<fo:table-column column-width="75pt"/>
          			<fo:table-column column-width="55pt"/>
          			<fo:table-column column-width="55pt"/>
          			<fo:table-column column-width="115pt"/>
          			<fo:table-body>
          				<fo:table-row>
          					<fo:table-cell>
          						<#list dayWiseEmplDetls as dayWise>
						            <fo:block>
						            	<fo:table>
						            		<fo:table-column column-width="60pt"/>
						            		<fo:table-column column-width="70pt"/>
						            		<fo:table-column column-width="70pt"/>
						            		<fo:table-column column-width="60pt"/>
						            		<fo:table-column column-width="55pt"/>
						            		<fo:table-column column-width="30pt"/>
						            		<fo:table-column column-width="30pt"/>
						            		<fo:table-column column-width="30pt"/>
						            		<fo:table-body>
						            			<#if dayWise.getValue().get("IN")?has_content>
							            			<fo:table-row>
							            				<fo:table-cell>
							            					<fo:block keep-together="always" text-align="left"><#if dayWise.getValue().get("IN")?has_content>${dayWise.getValue().get("IN").get("punchdate")?if_exists}</#if></fo:block>
							            				</fo:table-cell>
							            				<fo:table-cell>
							            					<fo:block keep-together="always" text-align="left"><#if dayWise.getValue().get("OUT")?has_content>${dayWise.getValue().get("OUT").get("punchdate")?if_exists}</#if></fo:block>
							            				</fo:table-cell>
							            				<fo:table-cell>
							            					<fo:block keep-together="always" text-align="left"><#if dayWise.getValue().get("IN")?has_content>${dayWise.getValue().get("IN").get("punchTime")?if_exists}</#if></fo:block>
							            				</fo:table-cell>
							            				<fo:table-cell>
							            					<fo:block keep-together="always" text-align="left"><#if dayWise.getValue().get("OUT")?has_content>${dayWise.getValue().get("OUT").get("punchTime")?if_exists}</#if></fo:block>
							            				</fo:table-cell>
							            				<#if dayWise.getValue().get("OUT")?has_content>
							            					<#assign total=((Static["org.ofbiz.base.util.UtilDateTime"].getInterval(dayWise.getValue().get("IN").get("dateFormat"),dayWise.getValue().get("OUT").get("dateFormat")))/(1000*60*60))>
							            					<#if (total<0) >
							            						<#assign total=total*(-1)>
							            					<#else>
								            					<#assign total=total>
							            					</#if>
							            				<#else>
							            					<#assign total=0>
							            				</#if>
							            				<fo:table-cell>
							            					<#if total==0>
							            						<fo:block keep-together="always" text-align="left">&#160;</fo:block>
							            					<#else>>
							            						<fo:block keep-together="always" text-align="left">${total?if_exists?string("#0.00")}</fo:block>
							            					</#if>
							            				</fo:table-cell>
							            				<fo:table-cell>
							            					<fo:block keep-together="always" text-align="center"><#if dayWise.getValue().get("IN").get("shiftType")?has_content>${dayWise.getValue().get("IN").get("shiftType")?if_exists}</#if></fo:block>
							            				</fo:table-cell>
							            			</fo:table-row>
							            		</#if>
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
	          					<#assign saturdaysListValues=saturdaysListMap.entrySet()>
	          					<#list saturdaysListValues as saturdaysList>
	          						<#if saturdaysList.getKey()==EmplDetls.getKey()>
	          							<fo:block white-space-collapse="false" keep-together="always">${(Static["org.ofbiz.base.util.UtilDateTime"].toDateString(saturdaysList.getValue(), "dd/MM"))} SS</fo:block>
	          						</#if>
	          					</#list>
	          					<#assign GeneralHolidayMapValues=GeneralHolidayMap.entrySet()>
	          					<#list GeneralHolidayMapValues as GeneralHolidayList>
	          						<#if GeneralHolidayList.getKey()==EmplDetls.getKey()>
	          							<#assign GeneralHolValues=GeneralHolidayList.getValue().entrySet()>
	          							<#list GeneralHolValues as GeneralHolList>
	          								<fo:block white-space-collapse="false" keep-together="always">${(Static["org.ofbiz.base.util.UtilDateTime"].toDateString(GeneralHolList.getValue(), "dd/MM"))} GH</fo:block>
	          							</#list>
	          						</#if>
	          					</#list>
          					</fo:table-cell>
          					<fo:table-cell>
          						<#if AbsentMap?has_content>
          							<#assign AbsentMapValues=AbsentMap.entrySet()>
   									<#list AbsentMapValues as AbsentList>
   										<#if (EmplDetls.getKey())==AbsentList.getKey()>
   											<#assign emplAbsValues=AbsentList.getValue().entrySet()>
   											<#list emplAbsValues as emplAbsList>
   												<fo:block white-space-collapse="false" keep-together="always" text-align="right">${(Static["org.ofbiz.base.util.UtilDateTime"].toDateString(emplAbsList.getKey(), "dd/MM"))}</fo:block>
   											</#list>
   										</#if>
   									</#list>
   								</#if>
          					</fo:table-cell>
          					<fo:table-cell>
          						<#if EmplDetailsMap.get(EmplDetls.getKey()).get("leaveList")?has_content>
	          						<#list EmplDetailsMap.get(EmplDetls.getKey()).get("leaveList") as leave>
	          							<fo:block white-space-collapse="false" keep-together="always" text-align="right">${(Static["org.ofbiz.base.util.UtilDateTime"].toDateString(leave.fromDate, "dd/MM"))} ${leave.leaveTypeId}</fo:block>
	          						</#list>
	          					</#if>	
          					</fo:table-cell>
          					<fo:table-cell>
          						<#if misPunchMap?has_content>
          							<#assign misPunchMapValues=misPunchMap.entrySet()>
   									<#list misPunchMapValues as misPunchList>
   										<#if (EmplDetls.getKey())==misPunchList.getKey()>
   											<#assign misPunchValues=misPunchList.getValue().entrySet()>
   											<#list misPunchValues as misPunch>
   												<fo:block white-space-collapse="false" keep-together="always" text-align="right">${(Static["org.ofbiz.base.util.UtilDateTime"].toDateString(misPunch.getKey(), "dd/MM"))}</fo:block>
   											</#list>
   										</#if>
   									</#list>
          						</#if>
          					</fo:table-cell>
          					<fo:table-cell>
					            <fo:block>
					            	<fo:table>
					            		<fo:table-column column-width="70pt"/>
					            		<fo:table-column column-width="45pt"/>
      									<fo:table-body>
          									<#assign OODList=OODMap.entrySet()>
          									<#list OODList as OODDateKey>
          										<#if OODDateKey.getKey()==EmplDetls.getKey()>
	          										<#assign OODDateKeyValues=OODDateKey.getValue().entrySet()>
	          										<#list OODDateKeyValues as OODValues>
								            			<fo:table-row>
								            				<fo:table-cell>
								            					<fo:block keep-together="always" text-align="right">${OODValues.getValue().get("IN").get("punchdate")}</fo:block>
								            				</fo:table-cell>
								            				<#if OODValues.getValue().get("OUT")?has_content>
								            					<#assign OODtotal=((Static["org.ofbiz.base.util.UtilDateTime"].getInterval(OODValues.getValue().get("IN").get("dateFormat"),OODValues.getValue().get("OUT").get("dateFormat")))/(1000*60*60))>
								            					<#if (OODtotal<0) >
								            						<#assign OODtotal=OODtotal*(-1)>
								            					<#else>
								            						<#assign OODtotal=OODtotal>
								            					</#if>
								            				<#else>
								            					<#assign OODtotal=0>
								            				</#if>
								            				<fo:table-cell>
								            					<#if OODtotal==0>
								            						<fo:block keep-together="always" text-align="left">&#160;</fo:block>
								            					<#else>
								            						<fo:block keep-together="always" text-align="right">${OODtotal?if_exists?string("#0.00")}</fo:block>
								            					</#if>
								            				</fo:table-cell>
		          										</fo:table-row>
		          									</#list>
		          								</#if>
          									</#list>
				            			</fo:table-body>
						            </fo:table>
						      	</fo:block>	
          					</fo:table-cell>
          					<fo:table-cell>
          						<#if CHLeaveTypeMap?has_content>
          							<#assign CHLeaveTypeValues=CHLeaveTypeMap.entrySet()>
   									<#list CHLeaveTypeValues as CHLeaveTypeList>
   										<#if (EmplDetls.getKey())==CHLeaveTypeList.getKey()>
   											<#assign CHLeaveValues=CHLeaveTypeList.getValue().entrySet()>
   											<#list CHLeaveValues as CHType>
   												<fo:block white-space-collapse="false" keep-together="always" text-align="right">${(Static["org.ofbiz.base.util.UtilDateTime"].toDateString(CHType.getKey(), "dd/MM"))}</fo:block>
   											</#list>
   										</#if>
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
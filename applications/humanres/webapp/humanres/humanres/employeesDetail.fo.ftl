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
      <fo:simple-page-master master-name="main" page-height="12in" page-width="10in"
        margin-top="0.3in" margin-bottom="1in" margin-left=".3in" margin-right=".3in">
          <fo:region-body margin-top=".2in"/>
          <fo:region-before extent=".5in"/>
          <fo:region-after extent=".5in"/>
      </fo:simple-page-master>
    </fo:layout-master-set>
        <fo:page-sequence master-reference="main">
        <fo:static-content flow-name="xsl-region-before">
	  		<fo:block font-weight="bold" keep-together="always" font-size="14pt" text-align="left">EMPLOYEES DETAILS</fo:block>
			<fo:block linefeed-treatment="preserve"> &#xA;</fo:block>
  		</fo:static-content>
          <fo:flow flow-name="xsl-region-body" font-family="Helvetica">
            <fo:block >            
			<fo:table table-layout="fixed" width="100%" >
			<fo:table-column column-width="60pt"/>
			<fo:table-column column-width="140pt"/>
			<fo:table-column column-width="80pt"/>
			<fo:table-column column-width="80pt"/>
			<fo:table-column column-width="60pt"/>
			<fo:table-column column-width="120pt"/>
			<fo:table-column column-width="80pt"/>
					<fo:table-header border-style="solid" font-weight="bold" font-size="8pt">					
						<fo:table-cell border-style="solid"><fo:block keep-together="always" text-align="center"> Employee Id </fo:block></fo:table-cell>
  						<fo:table-cell border-style="solid"><fo:block keep-together="always" text-align="center"> Name</fo:block></fo:table-cell>
  						<fo:table-cell border-style="solid"><fo:block keep-together="always" text-align="center"> Employement From </fo:block></fo:table-cell>
  						<fo:table-cell border-style="solid"><fo:block keep-together="always" text-align="center"> Employee Position </fo:block></fo:table-cell>
  						<fo:table-cell border-style="solid"><fo:block keep-together="always" text-align="center"> Phone Num </fo:block></fo:table-cell>
  						<fo:table-cell border-style="solid"><fo:block keep-together="always" text-align="center"> Email Address </fo:block></fo:table-cell>
  						<fo:table-cell border-style="solid"><fo:block keep-together="always" text-align="center"> Location Geo </fo:block></fo:table-cell>
					</fo:table-header>
					<fo:table-body >
					<#list employeeList as employee >
					<fo:table-row border-style="solid" font-size="8pt">	
  						<fo:table-cell border-style="solid"><fo:block keep-together="always" text-align="center"> ${employee.get("partyIdTo")?if_exists} </fo:block></fo:table-cell>
  						<fo:table-cell border-style="solid"><fo:block keep-together="always" text-align="center"> ${employee.get("firstName")?if_exists}  ${employee.get("lastName")?if_exists}</fo:block></fo:table-cell>
  						<fo:table-cell border-style="solid"><fo:block keep-together="always" text-align="center"> ${employee.get("fromDate")?if_exists} </fo:block></fo:table-cell>
  						<#if employee.get("employeePosition")?has_content>
  							<#assign empPositionTyp = delegator.findOne("EmplPositionType", {"emplPositionTypeId" : employee.get("employeePosition")?has_content}, true)>
  						</#if>
  						<#if empPositionTyp?has_content>
  						<fo:table-cell border-style="solid"><fo:block keep-together="always" text-align="center"> ${empPositionTyp.description?if_exists} </fo:block></fo:table-cell>
  						<#else>
  						<fo:table-cell border-style="solid"><fo:block keep-together="always" text-align="center"><#if employee.get("employeePosition")?has_content> ${employee.get("employeePosition")}</#if> </fo:block></fo:table-cell>
  						</#if>
  						<#if employee.get("phoneNumber")?has_content>
  							<fo:table-cell border-style="solid"><fo:block keep-together="always" text-align="center"> ${employee.get("phoneNumber")} </fo:block></fo:table-cell>
  						<#else>
  						<fo:table-cell border-style="solid"><fo:block keep-together="always" text-align="center"></fo:block></fo:table-cell>
  						</#if>
  						<#if employee.get("emailAddress")?has_content>
  							<fo:table-cell border-style="solid"><fo:block keep-together="always" text-align="center"> ${employee.get("emailAddress")} </fo:block></fo:table-cell>
  						<#else>
  						<fo:table-cell border-style="solid"><fo:block keep-together="always" text-align="center"></fo:block></fo:table-cell>
  						</#if>
						<#if employee.get("locationGeoId")?has_content>
  							<fo:table-cell border-style="solid"><fo:block keep-together="always" text-align="center"> ${employee.get("locationGeoId")} </fo:block></fo:table-cell>
  						<#else>
  						<fo:table-cell border-style="solid"><fo:block keep-together="always" text-align="center"></fo:block></fo:table-cell>
  						</#if>
					</fo:table-row>
					</#list>
				</fo:table-body>
		</fo:table>
	  </fo:block>
    </fo:flow>
  </fo:page-sequence>  
 </fo:root>
</#escape>

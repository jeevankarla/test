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
			<fo:simple-page-master master-name="main" page-height="12in" page-width="17in"
					 margin-left="0.2in" margin-right="0.2in"  margin-top="0.2in" margin-bottom="0.4in" >
				<fo:region-body margin-top="1.15in"/>
				<fo:region-before extent="1in"/>
				<fo:region-after extent="1in"/>
			</fo:simple-page-master>
		</fo:layout-master-set>
		${setRequestAttribute("OUTPUT_FILENAME", "GratuityDataReport.txt")}
		<#if employeeList?has_content>
		<fo:page-sequence master-reference="main">
			<fo:static-content font-size="13.5pt" font-family="Courier,monospace"  flow-name="xsl-region-before" >
			<fo:block text-align="center" white-space-collapse="false" font-weight="bold">&#160;   K.M.F CASH ACCUMULATION DATA AS ON ${fromDate?if_exists?upper_case}						          													</fo:block>
				<fo:block text-align="center" white-space-collapse="false" font-weight="bold">&#160;   MOTHER DAIRY A UNIT OF K.M.F, G.K.V.K POST, BANGALORE, KARNATAKA - 560065						          													</fo:block>
				<fo:block text-align="center" white-space-collapse="false" font-weight="bold">&#160;   LIST OF EMPLOYEES WORKING IN MOTHER DAIRY AS ON ${fromDate?if_exists?upper_case}             </fo:block>
				<fo:block linefeed-treatment="preserve">&#xA;</fo:block>
				 <fo:block font-family="Courier,monospace">
				<fo:table  width="100%">
					<fo:table-column column-width="41pt"/>
					<fo:table-column column-width="50pt"/>
					<fo:table-column column-width="130pt"/>
					<fo:table-column column-width="180pt"/>
					<fo:table-column column-width="150pt"/>
					<fo:table-column column-width="90pt"/>
					<fo:table-column column-width="80pt"/>
					<fo:table-column column-width="80pt"/>
					<fo:table-column column-width="80pt"/>
					<fo:table-column column-width="80pt"/>
					<fo:table-column column-width="80pt"/>
					<fo:table-column column-width="80pt"/>
					<fo:table-column column-width="95pt"/>
					<fo:table-column column-width="100pt"/>
					<fo:table-body>
					 <fo:table-row height="14px" space-start=".15in" text-align="center">
							   <fo:table-cell number-columns-spanned="1" border-style="solid" width="50px">
									<fo:block text-align="center" keep-together="always" font-weight="bold" >S.No</fo:block>
								</fo:table-cell>
								<fo:table-cell number-columns-spanned="1" border-style="solid" width="70px">
									<fo:block text-align="center" keep-together="always" font-weight="bold">EmpNo</fo:block>
								</fo:table-cell>
								<fo:table-cell number-columns-spanned="1" border-style="solid" width="200px">
									<fo:block text-align="center" keep-together="always" white-space-collapse="false" font-weight="bold">Employee Name</fo:block>
								</fo:table-cell>
								<fo:table-cell number-columns-spanned="1" border-style="solid" width="200px">
									<fo:block text-align="center" keep-together="always" font-weight="bold">Designation</fo:block>
								</fo:table-cell>
								<fo:table-cell number-columns-spanned="1" border-style="solid" width="120px">
									<fo:block text-align="center" keep-together="always" white-space-collapse="false" font-weight="bold">Date Of Birth</fo:block>
								</fo:table-cell>
								<fo:table-cell number-columns-spanned="1" border-style="solid" width="130px">
									<fo:block text-align="center" keep-together="always" white-space-collapse="false" font-weight="bold">Date Of Joining</fo:block>
								</fo:table-cell>
								<fo:table-cell number-columns-spanned="1" border-style="solid" width="70px">
									<fo:block text-align="center" keep-together="always" white-space-collapse="false" font-weight="bold">Basic</fo:block>
								</fo:table-cell>
								<fo:table-cell number-columns-spanned="1" border-style="solid" width="60px">
									<fo:block text-align="center" keep-together="always" white-space-collapse="false" font-weight="bold">DA</fo:block>
								</fo:table-cell>
								<fo:table-cell number-columns-spanned="1" border-style="solid" width="70px">
									<fo:block text-align="center" keep-together="always" white-space-collapse="false" font-weight="bold">Total</fo:block>
								</fo:table-cell>
								<fo:table-cell number-columns-spanned="1" border-style="solid" width="60px">
									<fo:block text-align="center" keep-together="always" white-space-collapse="false" font-weight="bold">EL</fo:block>
								</fo:table-cell>
								<fo:table-cell number-columns-spanned="1" border-style="solid" width="140px">
									<fo:block text-align="center" keep-together="always" white-space-collapse="false" font-weight="bold">Remarks</fo:block>
								</fo:table-cell>
							</fo:table-row>
					</fo:table-body>
				   </fo:table>
				  </fo:block>
			</fo:static-content>
			<fo:flow flow-name="xsl-region-body" font-family="Courier,monospace">
				 <fo:block font-family="Courier,monospace"  font-size="14pt">
				 <#assign sno=1>
				 <#list employeeList as employee>
				<fo:table>
					 <fo:table-column column-width="30pt"/>
					<fo:table-column column-width="80pt"/>
					<fo:table-column column-width="100pt"/>
					<fo:table-column column-width="80pt"/>
					<fo:table-column column-width="215pt"/>
					<fo:table-column column-width="50pt"/>
					<fo:table-column column-width="60pt"/>
					<fo:table-column column-width="27pt"/>
					<fo:table-column column-width="40pt"/>
					<fo:table-column column-width="30pt"/>
					<fo:table-column column-width="40pt"/>
					<fo:table-column column-width="60pt"/>
					<fo:table-column column-width="50pt"/>
					<fo:table-column column-width="40pt"/>
					<fo:table-column column-width="40pt"/>
					<fo:table-column column-width="40pt"/>
					<fo:table-column column-width="40pt"/>
					<fo:table-column column-width="40pt"/>
					<fo:table-column column-width="40pt"/>
					<fo:table-column column-width="40pt"/>
					<fo:table-column column-width="40pt"/>
					<fo:table-column column-width="55pt"/>
					<fo:table-column column-width="55pt"/>
					<fo:table-column column-width="50pt"/>
					<fo:table-body>
					<fo:table-row>
					<fo:table-cell number-columns-spanned="1" border-style="solid" width="50px">
					<fo:block text-align="center" keep-together="always">${sno}</fo:block></fo:table-cell>
                    <fo:table-cell number-columns-spanned="1" border-style="solid" width="70px">
                    <fo:block text-align="center" keep-together="always">${employee.get("employeeId")?if_exists}</fo:block></fo:table-cell>
                    <fo:table-cell number-columns-spanned="1" border-style="solid" width="200px">
                    <fo:block text-align="left" keep-together="always">${employee.get("name")?if_exists?upper_case}</fo:block></fo:table-cell>
                    <fo:table-cell number-columns-spanned="1" border-style="solid" width="200px">
                    <fo:block text-align="left" keep-together="always">${employee.get("position")?if_exists?upper_case}</fo:block></fo:table-cell>
                    <fo:table-cell number-columns-spanned="1" border-style="solid" width="120px">
                    <fo:block text-align="center" keep-together="always">${employee.get("birthDate")?if_exists?upper_case}</fo:block></fo:table-cell>
                    <fo:table-cell number-columns-spanned="1" border-style="solid" width="130px">
                    <fo:block text-align="center" keep-together="always">${employee.get("joinDate")?if_exists?upper_case}</fo:block></fo:table-cell>
                    <fo:table-cell number-columns-spanned="1" border-style="solid" width="70px">
                    <fo:block text-align="right" keep-together="always">${employee.get("amount")?if_exists}</fo:block></fo:table-cell>
                    <fo:table-cell number-columns-spanned="1" border-style="solid" width="60px">
                    <fo:block text-align="right" keep-together="always">${employee.get("daAmount")?if_exists}</fo:block></fo:table-cell>
                    <fo:table-cell number-columns-spanned="1" border-style="solid" width="70px">
                    <fo:block text-align="right" keep-together="always">${employee.get("total")?if_exists}</fo:block></fo:table-cell>
                    <fo:table-cell number-columns-spanned="1" border-style="solid" width="60px">
                    <fo:block text-align="center" keep-together="always">${employee.get("balance")?if_exists}</fo:block></fo:table-cell>
                    <fo:table-cell number-columns-spanned="1" border-style="solid" width="140px">
                    <fo:block text-align="left" keep-together="always"></fo:block></fo:table-cell>
					</fo:table-row>
					<#assign sno=sno+1>
					</fo:table-body>
				   </fo:table>
				   </#list>
				   </fo:block>
			</fo:flow>
		</fo:page-sequence>
		<#else>
				<fo:page-sequence master-reference="main">
	    			<fo:flow flow-name="xsl-region-body" font-family="Courier,monospace">
	       		 		<fo:block font-size="14pt">
	            			 ${errorMessage}.
	       		 		</fo:block>
	    			</fo:flow>
				</fo:page-sequence>
		</#if>
	 </fo:root>
</#escape>
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
					 margin-left="0.2in" margin-right="0.2in"  margin-top="0.2in" margin-bottom="0.2in" >
				<fo:region-body margin-top="3.4in"/>
				<fo:region-before extent="1in"/>
				<fo:region-after extent="1in"/>
			</fo:simple-page-master>
		</fo:layout-master-set>
		${setRequestAttribute("OUTPUT_FILENAME", "LeaveAvailedReport.txt")}
		<#if finalMap?has_content>
		<#assign finalLists=finalMap.entrySet()>
		<#list finalLists as employee>
		<#assign leaveTypeId=employee.getKey()>
		<fo:page-sequence master-reference="main">
			<fo:static-content font-size="13.5pt" font-family="Courier,monospace"  flow-name="xsl-region-before" font-weight="bold">
				<fo:block text-align="center" white-space-collapse="false">&#160;   MOTHER DAIRY A UNIT OF K.M.F						          													</fo:block>
				<fo:block text-align="center" white-space-collapse="false">&#160;                        G.K.V.K POST, BANGALORE, KARNATAKA - 560065				 		Date :${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(orderDate, "dd/MM/yyyy")}               </fo:block>
				<fo:block linefeed-treatment="preserve">&#xA;</fo:block>
				<fo:block text-align="left" keep-together="always" white-space-collapse="false">&#160;         MD/ESI/05/${year}-${year+1}					                                               Time :${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(orderDate, "hh:mm:ss")}         </fo:block>
				<fo:block linefeed-treatment="preserve">&#xA;</fo:block>
				<fo:block text-align="center" keep-together="always" white-space-collapse="false">&#160;                                     Leave Report                 	       Page Number  : <fo:page-number/></fo:block>
				<fo:block linefeed-treatment="preserve">&#xA;</fo:block>
				 <fo:block text-align="center" keep-together="always" white-space-collapse="false">The following officers/officials are sanctioned leave for the period mentioned against their names.</fo:block>
				 <fo:block linefeed-treatment="preserve">&#xA;</fo:block>
				 <fo:block text-align="left" keep-together="always" white-space-collapse="false">&#160;                                          Leave Code	:${leaveTypeId}</fo:block>
				 <fo:block linefeed-treatment="preserve">&#xA;</fo:block>
				 <fo:block text-align="left" keep-together="always" white-space-collapse="false">&#160;From Date	:${fromlarDate}			To Date	:${thrularDate}</fo:block>
				 <fo:block text-align="left" keep-together="always"  >&#160;----------------------------------------------------------------------------------------------------</fo:block>
				 <fo:block font-family="Courier,monospace">
				<fo:table >
					<fo:table-column column-width="41pt"/>
					<fo:table-column column-width="50pt"/>
					<fo:table-column column-width="130pt"/>
					<fo:table-column column-width="150pt"/>
					<fo:table-column column-width="90pt"/>
					<fo:table-column column-width="110pt"/>
					<fo:table-column column-width="100pt"/>
					<fo:table-column column-width="120pt"/>
					<fo:table-column column-width="100pt"/>
					<fo:table-body>
					 <fo:table-row >
							   <fo:table-cell >
									<fo:block text-align="right" keep-together="always" >S.No</fo:block>
								</fo:table-cell>
								<fo:table-cell >
									<fo:block text-align="right" keep-together="always" >EmpNo</fo:block>
								</fo:table-cell>
								<fo:table-cell >
									<fo:block text-align="center" keep-together="always" >Name</fo:block>
								</fo:table-cell>
								<fo:table-cell >
									<fo:block text-align="right" keep-together="always" >From Date</fo:block>
								</fo:table-cell>
								<fo:table-cell >
									<fo:block text-align="right" keep-together="always" >To Date</fo:block>
								</fo:table-cell>
								<fo:table-cell >
									<fo:block text-align="right" keep-together="always" >Leave Code</fo:block>
								</fo:table-cell>
								<fo:table-cell >
									<fo:block text-align="right" keep-together="always" >No Of Days</fo:block>
								</fo:table-cell>
								<fo:table-cell >
									<fo:block text-align="right" keep-together="always" >Balance Days</fo:block>
								</fo:table-cell>
							</fo:table-row>
							 <fo:table-row >
							   <fo:table-cell >
									 <fo:block text-align="left" keep-together="always"  >&#160;----------------------------------------------------------------------------------------------------</fo:block>
								</fo:table-cell>
						   </fo:table-row>
					</fo:table-body>
				   </fo:table>
				  </fo:block>
			</fo:static-content>
			<fo:flow flow-name="xsl-region-body" font-family="Courier,monospace">
				 <fo:block font-family="Courier,monospace"  font-size="14pt">
				 <#assign employees=employee.getValue()>
				 <#assign sno=1>
				 <#list employees as emp>
				<fo:table >
					 <fo:table-column column-width="30pt"/>
					<fo:table-column column-width="80pt"/>
					<fo:table-column column-width="100pt"/>
					<fo:table-column column-width="80pt"/>
					<fo:table-column column-width="100pt"/>
					<fo:table-column column-width="75pt"/>
					<fo:table-column column-width="60pt"/>
					<fo:table-column column-width="50pt"/>
					<fo:table-column column-width="40pt"/>
					<fo:table-column column-width="50pt"/>
					<fo:table-column column-width="40pt"/>
					<fo:table-column column-width="60pt"/>
					<fo:table-column column-width="50pt"/>
					<fo:table-column column-width="40pt"/>
					<fo:table-column column-width="40pt"/>
					<fo:table-column column-width="40pt"/>
					<fo:table-column column-width="55pt"/>
					<fo:table-column column-width="55pt"/>
					<fo:table-column column-width="50pt"/>
					<fo:table-body>
					<fo:table-row>
					<fo:table-cell><fo:block text-align="right" keep-together="always">${sno}</fo:block></fo:table-cell>
                    <fo:table-cell><fo:block text-align="center" keep-together="always">${emp.get("employeeId")}</fo:block></fo:table-cell>
                    <fo:table-cell><fo:block text-align="left" keep-together="always">${emp.get("name")?upper_case}</fo:block></fo:table-cell>
                    <fo:table-cell><fo:block linefeed-treatment="preserve">&#xA;</fo:block></fo:table-cell>
                    <fo:table-cell><fo:block text-align="left" keep-together="always">${emp.get("leaveFrom")}</fo:block></fo:table-cell>
                    <fo:table-cell><fo:block text-align="left" keep-together="always">${emp.get("leaveThru")}</fo:block></fo:table-cell>
                    <fo:table-cell><fo:block linefeed-treatment="preserve">&#xA;</fo:block></fo:table-cell>
                    <fo:table-cell><fo:block text-align="left" keep-together="always">${emp.get("leaveTypeId")}</fo:block></fo:table-cell>
                    <fo:table-cell><fo:block linefeed-treatment="preserve">&#xA;</fo:block></fo:table-cell>
                    <#if emp.get("noOfDays") gt 0>
                    <fo:table-cell><fo:block text-align="left" keep-together="always">${emp.get("noOfDays")?if_exists?string("##0.0")}</fo:block></fo:table-cell>
                    <#else>
                    <fo:table-cell><fo:block text-align="left" keep-together="always"></fo:block></fo:table-cell>
                    </#if>
                    <fo:table-cell><fo:block linefeed-treatment="preserve">&#xA;</fo:block></fo:table-cell>
                    <#if emp.get("balance") gt 0>
                    <fo:table-cell><fo:block text-align="right" keep-together="always">${emp.get("balance")?if_exists?string("##0.0")}</fo:block></fo:table-cell>
                    <#else>
                    <fo:table-cell><fo:block text-align="right" keep-together="always"></fo:block></fo:table-cell>
                    </#if>
					</fo:table-row>
					<fo:table-row>
                    <fo:table-cell><fo:block linefeed-treatment="preserve">&#xA;</fo:block></fo:table-cell>
                    </fo:table-row>
					<#assign sno=sno+1>
					</fo:table-body>
				   </fo:table>
				   </#list>
				   <fo:block text-align="left" keep-together="always"  >&#160;------------------------------------------------------------------------------------------------</fo:block>
				   </fo:block>
				   <fo:block font-family="Courier,monospace"  font-size="14pt">
                	<fo:table >
                     	<fo:table-column column-width="100pt"/>
                     	<fo:table-column column-width="100pt"/>
                     	<fo:table-column column-width="100pt"/>
                     	<fo:table-column column-width="100pt"/>
                     	<fo:table-column column-width="70pt"/>
                     	<fo:table-column column-width="100pt"/>
                     	<fo:table-column column-width="100pt"/>
                     	<fo:table-column column-width="100pt"/>
                     	<fo:table-column column-width="100pt"/>
                     	<fo:table-column column-width="100pt"/>
                     	<fo:table-column column-width="100pt"/>
                     	<fo:table-column column-width="100pt"/>
                     	<fo:table-column column-width="100pt"/>
                     	<fo:table-column column-width="100pt"/>
                     	<fo:table-body>
	                     	<fo:table-row><fo:table-cell><fo:block linefeed-treatment="preserve">&#xA;</fo:block></fo:table-cell>
	                     	</fo:table-row>
                     		<fo:table-row>
                     			<fo:table-cell><fo:block linefeed-treatment="preserve">&#xA;</fo:block></fo:table-cell>
                     			<fo:table-cell><fo:block text-align="left" white-space-collapse="false" >&#160;Copy</fo:block></fo:table-cell>
                   			</fo:table-row>
                   			<fo:table-row>
                     			<fo:table-cell><fo:block linefeed-treatment="preserve">&#xA;</fo:block></fo:table-cell>
                     			<fo:table-cell><fo:block text-align="left" white-space-collapse="false" >&#160;To&#160;Manager&#160;Finance </fo:block></fo:table-cell>
                   			</fo:table-row>
                   			<fo:table-row>
                     			<fo:table-cell><fo:block linefeed-treatment="preserve">&#xA;</fo:block></fo:table-cell>
                     			<fo:table-cell><fo:block text-align="left" keep-together="always" white-space-collapse="false" >&#160;To Manager Marketing                                    Director MotherDairy</fo:block></fo:table-cell>
                   			</fo:table-row>
                   			<fo:table-row>
                     			<fo:table-cell><fo:block linefeed-treatment="preserve">&#xA;</fo:block></fo:table-cell>
                     			<fo:table-cell><fo:block text-align="left" keep-together="always" white-space-collapse="false" >&#160;To Manager Dairy </fo:block></fo:table-cell>
                   			</fo:table-row>
                   			<fo:table-row>
                     			<fo:table-cell><fo:block linefeed-treatment="preserve">&#xA;</fo:block></fo:table-cell>
                     			<fo:table-cell><fo:block text-align="left" keep-together="always" white-space-collapse="false" >&#160;To Manager Engineer  </fo:block></fo:table-cell>
                   			</fo:table-row>
                   			<fo:table-row>
                     			<fo:table-cell><fo:block linefeed-treatment="preserve">&#xA;</fo:block></fo:table-cell>
                     			<fo:table-cell><fo:block text-align="left" keep-together="always" white-space-collapse="false" >&#160;To Manager PowderPlant   </fo:block></fo:table-cell>
                   			</fo:table-row>
                   			<fo:table-row>
                     			<fo:table-cell><fo:block linefeed-treatment="preserve">&#xA;</fo:block></fo:table-cell>
                     			<fo:table-cell><fo:block text-align="left" keep-together="always" white-space-collapse="false" >&#160;To Manager ICP    </fo:block></fo:table-cell>
                   			</fo:table-row>
                   			<fo:table-row>
                     			<fo:table-cell><fo:block linefeed-treatment="preserve">&#xA;</fo:block></fo:table-cell>
                     			<fo:table-cell><fo:block text-align="left" keep-together="always" white-space-collapse="false" >&#160;Notice Board     </fo:block></fo:table-cell>
                   			</fo:table-row>
                   			<fo:table-row>
                     			<fo:table-cell><fo:block linefeed-treatment="preserve">&#xA;</fo:block></fo:table-cell>
                     			<fo:table-cell><fo:block text-align="left" keep-together="always" white-space-collapse="false" >&#160;Office Copy     </fo:block></fo:table-cell>
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
	            			 ${errorMessage}.
	       		 		</fo:block>
	    			</fo:flow>
				</fo:page-sequence>
		</#if>
	 </fo:root>
</#escape>
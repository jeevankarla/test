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
				<fo:region-body margin-top="2in"/>
				<fo:region-before extent="1in"/>
				<fo:region-after extent="1in"/>
			</fo:simple-page-master>
		</fo:layout-master-set>
		${setRequestAttribute("OUTPUT_FILENAME", "CashEncashmentReport.txt")}
		<#if EncashmentList?has_content>
		<fo:page-sequence master-reference="main">
			<fo:static-content font-size="13.5pt" font-family="Courier,monospace"  flow-name="xsl-region-before" font-weight="bold">
				<fo:block text-align="center" white-space-collapse="false">&#160;   MOTHER DAIRY A UNIT OF K.M.F						          													</fo:block>
				<fo:block text-align="center" white-space-collapse="false">&#160;                        G.K.V.K POST, BANGALORE, KARNATAKA - 560065				 		Date :${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(orderDate, "dd/MM/yyyy")}               </fo:block>
				<fo:block text-align="left" keep-together="always" white-space-collapse="false" >&#160;                                        <fo:inline  text-decoration="underline">${orgName?upper_case}</fo:inline></fo:block>
				<fo:block text-align="center" keep-together="always" white-space-collapse="false">&#160;                               GH/SS CASH ENCASHMENT REPORT               Page Number  : <fo:page-number/></fo:block>
				 <fo:block linefeed-treatment="preserve">&#xA;</fo:block>
				 <fo:block text-align="left" keep-together="always" white-space-collapse="false">&#160;From Date	: ${fromlarDate?upper_case}			To Date	: ${thrularDate?upper_case}</fo:block>
				 <fo:block text-align="left" keep-together="always"  >&#160;----------------------------------------------------------------------------------------------------</fo:block>
				 <fo:block font-family="Courier,monospace">
				<fo:table >
					<fo:table-column column-width="42pt"/>
					<fo:table-column column-width="55pt"/>
					<fo:table-column column-width="150pt"/>
					<fo:table-column column-width="120pt"/>
					<fo:table-column column-width="160pt"/>
					<fo:table-column column-width="150pt"/>
					<fo:table-column column-width="110pt"/>
					<fo:table-column column-width="100pt"/>
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
									<fo:block text-align="right" keep-together="always" >Date</fo:block>
								</fo:table-cell>
								<fo:table-cell >
									<fo:block text-align="right" keep-together="always" >Shift Type</fo:block>
								</fo:table-cell>
								<fo:table-cell >
									<fo:block text-align="right" keep-together="always" >Leave Type</fo:block>
								</fo:table-cell>
								<fo:table-cell >
									<fo:block text-align="right" keep-together="always" >Encashment</fo:block>
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
				<#assign sno=1>
				 <#list EncashmentList as el>
				<fo:table >
					 <fo:table-column column-width="30pt"/>
					<fo:table-column column-width="80pt"/>
					<fo:table-column column-width="100pt"/>
					<fo:table-column column-width="100pt"/>
					<fo:table-column column-width="140pt"/>
					<fo:table-column column-width="75pt"/>
					<fo:table-column column-width="60pt"/>
					<fo:table-column column-width="50pt"/>
					<fo:table-column column-width="50pt"/>
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
                    <fo:table-cell><fo:block text-align="center" keep-together="always">${el.partyId}</fo:block></fo:table-cell>
                    <fo:table-cell><fo:block text-align="left" keep-together="always">${el.name?if_exists?upper_case}</fo:block></fo:table-cell>
                    <fo:table-cell><fo:block linefeed-treatment="preserve">&#xA;</fo:block></fo:table-cell>
                    <fo:table-cell><fo:block text-align="left" keep-together="always">${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(el.date, "dd MMM, yyyy")}</fo:block></fo:table-cell>
                    <fo:table-cell><fo:block text-align="left" keep-together="always">${el.shiftType}</fo:block></fo:table-cell>
                    <fo:table-cell><fo:block linefeed-treatment="preserve">&#xA;</fo:block></fo:table-cell>
                    <fo:table-cell><fo:block text-align="right" keep-together="always">${el.leaveType}</fo:block></fo:table-cell>
                    <fo:table-cell><fo:block linefeed-treatment="preserve">&#xA;</fo:block></fo:table-cell>
                    <fo:table-cell><fo:block text-align="left" keep-together="always">${el.encashmentStatus}</fo:block></fo:table-cell>
                    <fo:table-cell><fo:block linefeed-treatment="preserve">&#xA;</fo:block></fo:table-cell>
                    <fo:table-cell><fo:block text-align="right" keep-together="always"></fo:block></fo:table-cell>
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
				</fo:flow>
		</fo:page-sequence>
		<#else>
				<fo:page-sequence master-reference="main">
	    			<fo:flow flow-name="xsl-region-body" font-family="Courier,monospace">
	       		 		<fo:block font-size="14pt" text-align="center">
	            			 No Records Fond....!
	       		 		</fo:block>
	    			</fo:flow>
				</fo:page-sequence>
		</#if>
	 </fo:root>
</#escape>
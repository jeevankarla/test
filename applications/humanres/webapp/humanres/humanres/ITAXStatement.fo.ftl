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
				<fo:region-body margin-top="1.5in"/>
				<fo:region-before extent="1in"/>
				<fo:region-after extent="1in"/>
			</fo:simple-page-master>
		</fo:layout-master-set>
		${setRequestAttribute("OUTPUT_FILENAME", "ITAXStatement.txt")}
		<#if ITAXFinalList?has_content>
		<fo:page-sequence master-reference="main">
			<fo:static-content font-size="13.5pt" font-family="Courier,monospace"  flow-name="xsl-region-before" font-weight="bold">
				<fo:block text-align="center" white-space-collapse="false">&#160; MOTHER DAIRY A UNIT OF K.M.F						          													</fo:block>
				<fo:block text-align="center" white-space-collapse="false">&#160;                             G.K.V.K POST, BANGALORE, KARNATAKA - 560065				 		   Date :${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(orderDate, "dd/MM/yyyy")}</fo:block>
				<fo:block linefeed-treatment="preserve">&#xA;</fo:block>
				<fo:block text-align="center" keep-together="always" white-space-collapse="false">&#160;                   Income Tax Monthly Statement For The Month Of ${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(fromDate, "MMMM, yyyy")?upper_case}      Page Number : <fo:page-number/></fo:block>
				 <fo:block text-align="left" keep-together="always"  >&#160;----------------------------------------------------------------------------------------------------</fo:block>
				 <fo:block font-family="Courier,monospace">
				<fo:table >
					<fo:table-column column-width="80pt"/>
					<fo:table-column column-width="70pt"/>
					<fo:table-column column-width="150pt"/>
					<fo:table-column column-width="150pt"/>
					<fo:table-column column-width="100pt"/>
					<fo:table-column column-width="130pt"/>
					<fo:table-column column-width="100pt"/>
					<fo:table-column column-width="120pt"/>
					<fo:table-column column-width="100pt"/>
					<fo:table-body>
					 <fo:table-row >
							   <fo:table-cell >	<fo:block text-align="right" keep-together="always" >S.No</fo:block> </fo:table-cell>
								<fo:table-cell > <fo:block text-align="right" keep-together="always" >EmpNo</fo:block> </fo:table-cell>
								<fo:table-cell > <fo:block text-align="center" keep-together="always" >Name</fo:block> </fo:table-cell>
								<fo:table-cell> <fo:block linefeed-treatment="preserve">&#xA;</fo:block></fo:table-cell>
								<fo:table-cell > <fo:block text-align="right" keep-together="always" >PAN Number</fo:block> </fo:table-cell>
								<fo:table-cell><fo:block linefeed-treatment="preserve">&#xA;</fo:block></fo:table-cell>
								<fo:table-cell > <fo:block text-align="right" keep-together="always" >Amount(Rs)</fo:block> </fo:table-cell>
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
				 <#assign count=1>
				 <#assign pageITAX=0>
				 <#assign listSize = 0>
				 <#if ITAXFinalList?has_content>
				 	<#assign listSize = ITAXFinalList.size()>
				 </#if>
				 <#list ITAXFinalList as employees>
				<fo:table >
					<fo:table-column column-width="70pt"/>
					<fo:table-column column-width="110pt"/>
					<fo:table-column column-width="100pt"/>
					<fo:table-column column-width="190pt"/>
					<fo:table-column column-width="150pt"/>
					<fo:table-column column-width="100pt"/>
					<fo:table-column column-width="50pt"/>
					<fo:table-body>
					<fo:table-row>
					<fo:table-cell><fo:block text-align="right" keep-together="always">${sno}</fo:block></fo:table-cell>
                    <fo:table-cell><fo:block text-align="center" keep-together="always">${employees.get("partyId")?if_exists}</fo:block></fo:table-cell>
                    <fo:table-cell><fo:block text-align="left" keep-together="always">${employees.get("name")?upper_case}</fo:block></fo:table-cell>
                    <fo:table-cell><fo:block linefeed-treatment="preserve">&#xA;</fo:block></fo:table-cell>
                    <fo:table-cell><fo:block text-align="left" keep-together="always">${employees.get("panId")?if_exists}</fo:block></fo:table-cell>
                    <fo:table-cell><fo:block linefeed-treatment="preserve">&#xA;</fo:block></fo:table-cell>
                    <#assign incomTax=employees.get("incomeTax")?if_exists>
                    <#assign pageITAX=(pageITAX+incomTax)>
                    <fo:table-cell><fo:block text-align="right" keep-together="always">${incomTax?string("##0.00")}</fo:block></fo:table-cell>
					</fo:table-row>
					<fo:table-row>
                    <fo:table-cell><fo:block linefeed-treatment="preserve">&#xA;</fo:block></fo:table-cell>
                    </fo:table-row>
					<#assign sno=sno+1>
					<#assign count=count+1>
					<#if (count==14)>
					<fo:table-row >
							   <fo:table-cell >
									 <fo:block text-align="left" keep-together="always" font-weight="bold">&#160;------------------------------------------------------------------------------------------------</fo:block>
								</fo:table-cell>
						   </fo:table-row>
					<fo:table-row>
					<fo:table-cell><fo:block linefeed-treatment="preserve">&#xA;</fo:block></fo:table-cell>
                    <fo:table-cell><fo:block linefeed-treatment="preserve" font-weight="bold">Page Total :</fo:block></fo:table-cell>
                    <fo:table-cell><fo:block linefeed-treatment="preserve">&#xA;</fo:block></fo:table-cell>
                    <fo:table-cell><fo:block linefeed-treatment="preserve">&#xA;</fo:block></fo:table-cell>
                    <fo:table-cell><fo:block linefeed-treatment="preserve">&#xA;</fo:block></fo:table-cell>
                    <fo:table-cell><fo:block linefeed-treatment="preserve">&#xA;</fo:block></fo:table-cell>
                    <fo:table-cell><fo:block text-align="right" font-weight="bold">${pageITAX?if_exists?string("##0.00")}</fo:block></fo:table-cell>
					</fo:table-row>
					<fo:table-row >
							   <fo:table-cell >
									 <fo:block text-align="left" keep-together="always" font-weight="bold">&#160;------------------------------------------------------------------------------------------------</fo:block>
								</fo:table-cell>
					</fo:table-row>
					<fo:table-row>
					<fo:table-cell><fo:block linefeed-treatment="preserve">&#xA;</fo:block></fo:table-cell>
                    <fo:table-cell><fo:block linefeed-treatment="preserve" font-weight="bold">Grand Total :</fo:block></fo:table-cell>
                    <fo:table-cell><fo:block linefeed-treatment="preserve">&#xA;</fo:block></fo:table-cell>
                    <fo:table-cell><fo:block linefeed-treatment="preserve">&#xA;</fo:block></fo:table-cell>
                    <fo:table-cell><fo:block linefeed-treatment="preserve">&#xA;</fo:block></fo:table-cell>
                    <fo:table-cell><fo:block linefeed-treatment="preserve">&#xA;</fo:block></fo:table-cell>
                    <fo:table-cell><fo:block text-align="right" font-weight="bold">${grandTotal?if_exists?string("##0.00")}</fo:block></fo:table-cell>
					</fo:table-row>
					<fo:table-row >
							   <fo:table-cell >
									 <fo:block text-align="left" page-break-after="always" keep-together="always" font-weight="bold">&#160;------------------------------------------------------------------------------------------------</fo:block>
								</fo:table-cell>
						   </fo:table-row>
					<#assign count=0>
					<#assign pageITAX=0>
					</#if>
					<#if listSize == sno-1>
					<fo:table-row >
							   <fo:table-cell >
									 <fo:block text-align="left" keep-together="always" font-weight="bold">&#160;------------------------------------------------------------------------------------------------</fo:block>
								</fo:table-cell>
						   </fo:table-row>
					<fo:table-row>
					<fo:table-cell><fo:block linefeed-treatment="preserve">&#xA;</fo:block></fo:table-cell>
                    <fo:table-cell><fo:block linefeed-treatment="preserve" font-weight="bold">Page Total :</fo:block></fo:table-cell>
                    <fo:table-cell><fo:block linefeed-treatment="preserve">&#xA;</fo:block></fo:table-cell>
                    <fo:table-cell><fo:block linefeed-treatment="preserve">&#xA;</fo:block></fo:table-cell>
                    <fo:table-cell><fo:block linefeed-treatment="preserve">&#xA;</fo:block></fo:table-cell>
                    <fo:table-cell><fo:block linefeed-treatment="preserve">&#xA;</fo:block></fo:table-cell>
                    <fo:table-cell><fo:block text-align="right" font-weight="bold">${pageITAX?if_exists?string("##0.00")}</fo:block></fo:table-cell>
					</fo:table-row>
					<fo:table-row >
							   <fo:table-cell >
									 <fo:block text-align="left" keep-together="always" font-weight="bold">&#160;------------------------------------------------------------------------------------------------</fo:block>
								</fo:table-cell>
					</fo:table-row>
					<fo:table-row>
					<fo:table-cell><fo:block linefeed-treatment="preserve">&#xA;</fo:block></fo:table-cell>
                    <fo:table-cell><fo:block linefeed-treatment="preserve" font-weight="bold">Grand Total :</fo:block></fo:table-cell>
                    <fo:table-cell><fo:block linefeed-treatment="preserve">&#xA;</fo:block></fo:table-cell>
                    <fo:table-cell><fo:block linefeed-treatment="preserve">&#xA;</fo:block></fo:table-cell>
                    <fo:table-cell><fo:block linefeed-treatment="preserve">&#xA;</fo:block></fo:table-cell>
                    <fo:table-cell><fo:block linefeed-treatment="preserve">&#xA;</fo:block></fo:table-cell>
                    <fo:table-cell><fo:block text-align="right" font-weight="bold">${grandTotal?if_exists?string("##0.00")}</fo:block></fo:table-cell>
					</fo:table-row>
					<fo:table-row >
							   <fo:table-cell >
									 <fo:block text-align="left" keep-together="always" font-weight="bold">&#160;------------------------------------------------------------------------------------------------</fo:block>
								</fo:table-cell>
					</fo:table-row>
					<#assign count=0>
					<#assign pageITAX=0>
					</#if>
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
	            			 No Records Found....!
	       		 		</fo:block>
	    			</fo:flow>
				</fo:page-sequence>
		</#if>
	 </fo:root>
</#escape>
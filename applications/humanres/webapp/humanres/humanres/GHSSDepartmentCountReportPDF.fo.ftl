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
			<fo:simple-page-master master-name="main" page-height="12in" page-width="15in"
					 margin-left="0.2in" margin-right="0.2in"  margin-top="0.2in" margin-bottom="0.2in" >
				<fo:region-body margin-top="2in"/>
				<fo:region-before extent="1in"/>
				<fo:region-after extent="1in"/>
			</fo:simple-page-master>
		</fo:layout-master-set>
		${setRequestAttribute("OUTPUT_FILENAME", "GHSSDepartmentCountReport.txt")}
		<#if finalDeptCountMap?has_content>
		<#assign holidaysCount=0>
		<#list holidays as holiday>
			<#assign holidaysCount=holidaysCount+1>
		</#list>
		<fo:page-sequence master-reference="main">
			<fo:static-content font-size="13.5pt" font-family="Courier,monospace"  flow-name="xsl-region-before" font-weight="bold">
				<fo:block text-align="center" white-space-collapse="false">&#160;   MOTHER DAIRY A UNIT OF K.M.F						          													</fo:block>
				<fo:block text-align="center" white-space-collapse="false">&#160;                                     G.K.V.K POST, BANGALORE, KARNATAKA - 560065			        	 		    Date :${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(nowTimestamp ,"dd-MM-yyyy")}               </fo:block>
				<fo:block text-align="center" keep-together="always" white-space-collapse="false">&#160;                                       GH AND SS WORKED EMPLOYEE'S COUNT REPORT                    Page Number  : <fo:page-number/></fo:block>
				 <fo:block linefeed-treatment="preserve">&#xA;</fo:block>
				 <fo:block text-align="left" keep-together="always" white-space-collapse="false">&#160;From Date	:${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(fromStartDate ,"dd-MM-yyyy")} 			To Date	:${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(thruEndDate ,"dd-MM-yyyy")} </fo:block>
				 <fo:block text-align="left" keep-together="always"  >&#160;--------------------------------------------------------------------------------------------------------------------------------</fo:block>
				 <fo:block font-family="Courier,monospace">
				<fo:table >
				<#if holidaysCount gte 5>	
					<fo:table-column column-width="100pt"/>
					<fo:table-column column-width="30pt"/>
					<fo:table-column column-width="30pt"/>
					<fo:table-column column-width="120pt"/>
					<#list holidays as holiday>
					<fo:table-column column-width="110pt"/>
					</#list>
				</#if>	
				<#if holidaysCount = 4>	
					<fo:table-column column-width="100pt"/>
					<fo:table-column column-width="30pt"/>
					<fo:table-column column-width="30pt"/>
					<fo:table-column column-width="120pt"/>
					<#list holidays as holiday>
					<fo:table-column column-width="100pt"/>
					</#list>
				</#if>	
				<#if holidaysCount = 3>	
					<fo:table-column column-width="100pt"/>
					<fo:table-column column-width="30pt"/>
					<fo:table-column column-width="30pt"/>
					<fo:table-column column-width="120pt"/>
					<#list holidays as holiday>
					<fo:table-column column-width="80pt"/>
					</#list>
				</#if>	
				<#if holidaysCount lte 2>	
					<fo:table-column column-width="100pt"/>
					<fo:table-column column-width="30pt"/>
					<fo:table-column column-width="30pt"/>
					<fo:table-column column-width="120pt"/>
					<#list holidays as holiday>
					<fo:table-column column-width="70pt"/>
					</#list>
				</#if>	
					<fo:table-column column-width="100pt"/>
					<fo:table-body>
					 <fo:table-row >
					 		   <fo:table-cell><fo:block linefeed-treatment="preserve">&#xA;</fo:block></fo:table-cell>
							   <fo:table-cell >
									<fo:block text-align="right" keep-together="always" >DEPARTMENT NAME</fo:block>
								</fo:table-cell>
								<fo:table-cell><fo:block linefeed-treatment="preserve">&#xA;</fo:block></fo:table-cell>
								<fo:table-cell><fo:block linefeed-treatment="preserve">&#xA;</fo:block></fo:table-cell>
								<#list holidays as holiday>
								<fo:table-cell >
									<fo:block text-align="center" keep-together="always" >${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(holiday, "dd-MMM")?upper_case}</fo:block>
								</fo:table-cell>
								</#list>
								<fo:table-cell><fo:block text-align="right" keep-together="always">GRAND TOTAL</fo:block></fo:table-cell>
							</fo:table-row>
							 <fo:table-row >
							   <fo:table-cell >
									 <fo:block text-align="left" keep-together="always"  >&#160;--------------------------------------------------------------------------------------------------------------------------------</fo:block>
								</fo:table-cell>
						   </fo:table-row>
					</fo:table-body>
				   </fo:table>
				  </fo:block>
			</fo:static-content>
			<fo:flow flow-name="xsl-region-body" font-family="Courier,monospace">
				 <fo:block font-family="Courier,monospace"  font-size="14pt">
				<fo:table >
				<#assign deptCount=finalDeptCountMap.entrySet()>
					<#list deptCount as deptcount>
						<#assign dates=deptcount.getValue()>
						<fo:table-column column-width="10pt"/>
						<fo:table-column column-width="130pt"/>
						<fo:table-column column-width="150pt"/>
						<fo:table-column column-width="25pt"/>
						<#list dates as date>
			                    <#list deptCountHolidays as holiday>
			                    <fo:table-column column-width="15pt"/>
			                    </#list>
			            <fo:table-column column-width="25pt"/>
			            <fo:table-column column-width="25pt"/>
			            <fo:table-column column-width="25pt"/>
			            <fo:table-column column-width="25pt"/>
			            <fo:table-column column-width="25pt"/>
			            </#list>   
					</#list>	
					<fo:table-body>
					<#assign deptTotal=0>
					<#list deptCount as deptcount>
					<#assign deptTotal=0>
						<#assign dates=deptcount.getValue()>
							<fo:table-row>
							<fo:table-cell><fo:block linefeed-treatment="preserve">&#xA;</fo:block></fo:table-cell>
		                    <fo:table-cell><fo:block text-align="left" keep-together="always">${deptcount.getKey()?upper_case}</fo:block></fo:table-cell>
		                    <fo:table-cell><fo:block linefeed-treatment="preserve">&#xA;</fo:block></fo:table-cell>
		                    <fo:table-cell><fo:block linefeed-treatment="preserve">&#xA;</fo:block></fo:table-cell>
		                    <#list dates as date>
			                    <#list deptCountHolidays as holiday>
			                    <#assign deptTotal=deptTotal+date[holiday]>
			                    	<fo:table-cell><fo:block text-align="right" keep-together="always">${date[holiday]?if_exists}</fo:block></fo:table-cell>
			                    </#list>
			                 </#list>   
			                 <fo:table-cell><fo:block linefeed-treatment="preserve">&#xA;</fo:block></fo:table-cell>
			                 <fo:table-cell><fo:block linefeed-treatment="preserve">&#xA;</fo:block></fo:table-cell>
			                 <fo:table-cell><fo:block linefeed-treatment="preserve">&#xA;</fo:block></fo:table-cell>
			                 <fo:table-cell><fo:block linefeed-treatment="preserve">&#xA;</fo:block></fo:table-cell>
			                 <fo:table-cell><fo:block linefeed-treatment="preserve">${deptTotal}</fo:block></fo:table-cell>
							</fo:table-row>
						<fo:table-row>
	                    <fo:table-cell><fo:block linefeed-treatment="preserve">&#xA;</fo:block></fo:table-cell>
	                    </fo:table-row>
                    </#list>
					</fo:table-body>
				   </fo:table>
				   <fo:block text-align="left" keep-together="always" >&#160;--------------------------------------------------------------------------------------------------------------------------</fo:block>
				   <fo:block font-family="Courier,monospace" font-weight="bold">
				<fo:table >
				<#if holidaysCount gte 5>	
					<fo:table-column column-width="80pt"/>
					<fo:table-column column-width="7pt"/>
					<fo:table-column column-width="7pt"/>
					<fo:table-column column-width="140pt"/>
					<#list holidays as holiday>
					<fo:table-column column-width="113pt"/>
					</#list>
				</#if>	
				<#if holidaysCount = 4>	
					<fo:table-column column-width="70pt"/>
					<fo:table-column column-width="30pt"/>
					<fo:table-column column-width="15pt"/>
					<fo:table-column column-width="120pt"/>
					<#list holidays as holiday>
					<fo:table-column column-width="100pt"/>
					</#list>
				</#if>	
				<#if holidaysCount = 3>	
					<fo:table-column column-width="70pt"/>
					<fo:table-column column-width="30pt"/>
					<fo:table-column column-width="35pt"/>
					<fo:table-column column-width="120pt"/>
					<#list holidays as holiday>
					<fo:table-column column-width="80pt"/>
					</#list>
				</#if>	
				<#if holidaysCount lte 2>	
					<fo:table-column column-width="100pt"/>
					<fo:table-column column-width="20pt"/>
					<fo:table-column column-width="20pt"/>
					<fo:table-column column-width="120pt"/>
					<#list holidays as holiday>
					<fo:table-column column-width="70pt"/>
					</#list>
				</#if>	
					<fo:table-column column-width="100pt"/>
					<fo:table-body>
					 <fo:table-row >
					 		   <fo:table-cell><fo:block linefeed-treatment="preserve">&#xA;</fo:block></fo:table-cell>
							   <fo:table-cell >
									<fo:block text-align="right" keep-together="always" >&#160;GRAND TOTAL</fo:block>
								</fo:table-cell>
								<fo:table-cell><fo:block linefeed-treatment="preserve">&#xA;</fo:block></fo:table-cell>
								<fo:table-cell><fo:block linefeed-treatment="preserve">&#xA;</fo:block></fo:table-cell>
								<#assign finalTotal=0>
								<#list deptCountHolidays as holiday>
								<fo:table-cell >
								<#assign grandTotal=finalDayCountMap[holiday]>
								<#assign finalTotal=finalTotal+grandTotal>
									<fo:block text-align="right" keep-together="always" >${grandTotal}</fo:block>
								</fo:table-cell>
								</#list>
								<fo:table-cell><fo:block text-align="right" keep-together="always">${finalTotal}</fo:block></fo:table-cell>
							</fo:table-row>
					</fo:table-body>
				   </fo:table>
				  </fo:block>
				  <fo:block text-align="left" keep-together="always" >&#160;--------------------------------------------------------------------------------------------------------------------------</fo:block>
				   </fo:block>
				</fo:flow>
		</fo:page-sequence>
		<#else>
				<fo:page-sequence master-reference="main">
	    			<fo:flow flow-name="xsl-region-body" font-family="Courier,monospace">
	       		 		<fo:block font-size="14pt" text-align="center">
	            			 No Records Found....!
	       		 		</fo:block>
	    			</fo:flow>
				</fo:page-sequence>
		</#if>
	 </fo:root>
</#escape>
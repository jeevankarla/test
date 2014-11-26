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
		<#if finalList?has_content>
		<fo:page-sequence master-reference="main">
			<fo:static-content font-size="13.5pt" font-family="Courier,monospace"  flow-name="xsl-region-before" font-weight="bold">
				<fo:block text-align="center" white-space-collapse="false">&#160;   MOTHER DAIRY A UNIT OF K.M.F						          													</fo:block>
				<fo:block text-align="center" white-space-collapse="false">&#160;G.K.V.K POST, BANGALORE, KARNATAKA - 560065				 		  </fo:block>
				<fo:block text-align="center" keep-together="always" white-space-collapse="false" >                                       <fo:inline  text-decoration="underline"></fo:inline></fo:block>
				<fo:block text-align="center" keep-together="always" white-space-collapse="false">&#160;                       EDLIS SCHEME-C AND D SCHEDULE AS ON ${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(fromDate, "dd-MMM-yyyy")}    Date :${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(nowTimestamp, "dd-MMM-yyyy")}                </fo:block>
				 <fo:block text-align="center" keep-together="always" white-space-collapse="false">&#160;                                       POLICY NO:35767                   Page Number  : <fo:page-number/></fo:block>
				 <fo:block linefeed-treatment="preserve">&#xA;</fo:block>
				 <fo:block text-align="left" keep-together="always"  >&#160;----------------------------------------------------------------------------------------------------</fo:block>
				 <fo:block font-family="Courier,monospace">
				<fo:table >
					<fo:table-column column-width="50pt"/>
					<fo:table-column column-width="80pt"/>
					<fo:table-column column-width="190pt"/>
					<fo:table-column column-width="110pt"/>
					<fo:table-column column-width="140pt"/>
					<fo:table-column column-width="150pt"/>
					<fo:table-column column-width="80pt"/>
					<fo:table-body>
					   <fo:table-row >
					       <fo:table-cell >
									<fo:block text-align="right" keep-together="always" >Sl.No</fo:block>
								</fo:table-cell>
								<fo:table-cell >
									<fo:block text-align="right" keep-together="always" >Employee </fo:block>
									<fo:block text-align="center" keep-together="always" >Code</fo:block>
								</fo:table-cell>
								<fo:table-cell >
									<fo:block text-align="right" keep-together="always" >Name Of The Employee</fo:block>
								</fo:table-cell>
								<fo:table-cell >
									<fo:block text-align="right" keep-together="always" >PF Number</fo:block>
								</fo:table-cell>
								<fo:table-cell >
									<fo:block text-align="right" keep-together="always" >Date Of Birth</fo:block>
								</fo:table-cell>
								<fo:table-cell >
									<fo:block text-align="right" keep-together="always" >Date Of Joining</fo:block>
								</fo:table-cell>
								<fo:table-cell >
									<fo:block text-align="right" keep-together="always" >Salary</fo:block>
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
				 <fo:block font-family="Courier,monospace"  font-size="10pt">
				<#assign sno=1>
				 <#list finalList as employeeDetails>
				<fo:table >
					<fo:table-column column-width="50pt"/>
					<fo:table-column column-width="80pt"/>
					<fo:table-column column-width="30pt"/>
					<fo:table-column column-width="210pt"/>
					<fo:table-column column-width="70pt"/>
					<fo:table-column column-width="110pt"/>
					<fo:table-column column-width="130pt"/>
					<fo:table-column column-width="120pt"/>
					<fo:table-body>
					    <fo:table-row height="14px" space-start=".15in">
						    <fo:table-cell><fo:block text-align="center" keep-together="always">${sno?if_exists}</fo:block></fo:table-cell>    
                            <fo:table-cell><fo:block text-align="center" keep-together="always">${employeeDetails.get("employeeCode")?if_exists}</fo:block></fo:table-cell>
                            <fo:table-cell><fo:block linefeed-treatment="preserve">&#xA;</fo:block></fo:table-cell>
                            <fo:table-cell><fo:block text-align="left" keep-together="always">${employeeDetails.get("employeeName")?if_exists?upper_case}</fo:block></fo:table-cell>
                            <fo:table-cell><fo:block text-align="right" keep-together="always">${employeeDetails.get("pfNumber")?if_exists}</fo:block></fo:table-cell>
                            <fo:table-cell><fo:block text-align="right" keep-together="always">${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(employeeDetails.get("DateOfBirth") ,"dd/MM/yyyy")?if_exists}</fo:block></fo:table-cell>
                            <fo:table-cell><fo:block text-align="right" keep-together="always">${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(employeeDetails.get("DateOfJoining") ,"dd/MM/yyyy")?if_exists}</fo:block></fo:table-cell>
                            <fo:table-cell><fo:block text-align="right" keep-together="always">${employeeDetails.get("Salary")?if_exists?string("##0.00")}</fo:block></fo:table-cell>
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
	            			 No Records Found....!
	       		 		</fo:block>
	    			</fo:flow>
				</fo:page-sequence>
		</#if>
	 </fo:root>
</#escape>	    

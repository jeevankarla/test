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
					 margin-left="0.5in" margin-right="0.2in"  margin-top="0.2in" margin-bottom="0.2in" >
				<fo:region-body margin-top="2in"/>
				<fo:region-before extent="1in"/>
				<fo:region-after extent="1in"/>
			</fo:simple-page-master>
		</fo:layout-master-set>
		        <#if MrrList?has_content>
		         <#if ReceiptList?has_content>
		         <#if issueList?has_content>
		<fo:page-sequence master-reference="main">
			<fo:static-content font-size="13pt" font-family="Courier,monospace"  flow-name="xsl-region-before" font-weight="bold">
				<fo:block  keep-together="always" text-align="center" font-weight = "bold" font-family="Courier,monospace" white-space-collapse="false">${uiLabelMap.KMFDairyHeader}</fo:block>
				 <fo:block  keep-together="always" text-align="center" font-family="Courier,monospace" white-space-collapse="false" font-weight="bold">${uiLabelMap.KMFDairySubHeader}</fo:block>
				<fo:block text-align="center" keep-together="always"  >&#160;---------------------------------------------------------------------</fo:block>
				<fo:block text-align="center" white-space-collapse="false">&#160;      STORE RECIPT-ISSUE BETWEN ${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(fromDate, "dd-MMM-yyyy")} AND ${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(thruDate, "dd-MMM-yyyy")}                </fo:block>				
			    <fo:block linefeed-treatment="preserve">&#xA;</fo:block>
			    <fo:block  keep-together="always" text-align="left">MATERIAL CODE: </fo:block>   			   
			    <fo:block font-family="Courier,monospace">		 
			    <fo:table border-style="solid">
					<fo:table-column column-width="80pt"/>
					<fo:table-column column-width="80pt"/>
					<fo:table-column column-width="100pt"/>
					<fo:table-column column-width="80pt"/>
					<fo:table-column column-width="70pt"/>
					<fo:table-column column-width="70pt"/>
					<fo:table-column column-width="100pt"/>
					<fo:table-column column-width="100pt"/>
					<fo:table-column column-width="70pt"/>
					<fo:table-column column-width="70pt"/>
					<fo:table-column column-width="90pt"/>
					<fo:table-column column-width="25pt"/>
					   <fo:table-body>
					      <fo:table-row>
					           <fo:table-cell border-style="solid" number-columns-spanned="6">
					               <fo:block linefeed-treatment="preserve">&#xA;</fo:block>
					               <fo:block text-align="center" font-size="13pt" white-space-collapse="false" font-weight="bold">RECEIPTS</fo:block>
					           </fo:table-cell>
					           <fo:table-cell border-style="solid" number-columns-spanned="4">
					               <fo:block linefeed-treatment="preserve">&#xA;</fo:block>
					               <fo:block text-align="center" font-size="13pt" white-space-collapse="false" font-weight="bold">ISSUES</fo:block>
					           </fo:table-cell>
					      </fo:table-row>
					      <fo:table-row >
					            <fo:table-cell border-style="solid">
									<fo:block text-align="left" keep-together="always" >DATE</fo:block>
								</fo:table-cell>
								<fo:table-cell border-style="solid">
									<fo:block text-align="left" keep-together="always" >BILL NO.</fo:block>
								</fo:table-cell>
								<fo:table-cell border-style="solid">
									<fo:block text-align="left" keep-together="always" >MRR NUMBER</fo:block>
								</fo:table-cell>
								<fo:table-cell border-style="solid">
									<fo:block text-align="right" keep-together="always" >QUANTITY</fo:block>
								</fo:table-cell>
								<fo:table-cell border-style="solid">
									<fo:block text-align="right" keep-together="always" >RATE</fo:block>
								</fo:table-cell>
								<fo:table-cell border-style="solid" >
									<fo:block text-align="right" keep-together="always" >AMOUNT</fo:block>
								</fo:table-cell>
								<fo:table-cell border-style="solid">
									<fo:block text-align="left" keep-together="always">INDENT NO.</fo:block>
								</fo:table-cell>
								<fo:table-cell border-style="solid" >
								    <fo:block text-align="right" keep-together="always">ISSUE QTY</fo:block>
								</fo:table-cell>
								<fo:table-cell border-style="solid">
								    <fo:block text-align="right" keep-together="always">RATE</fo:block>
								</fo:table-cell>
								<fo:table-cell border-style="solid">
								    <fo:block text-align="right" keep-together="always">AMOUNT</fo:block>
								</fo:table-cell>
								<fo:table-cell >
								    <fo:block text-align="right" keep-together="always">Day Closing </fo:block>
								    <fo:block text-align="right" keep-together="always">Balance</fo:block>
								</fo:table-cell>
							</fo:table-row>
					</fo:table-body>
				</fo:table>
  			</fo:block>
	   </fo:static-content>
	   <fo:flow flow-name="xsl-region-body"   font-family="Courier,monospace">
	        <fo:block font-family="Courier,monospace"  font-size="10pt">
	           <#list MrrList as mrrListDetails>
	           <#list ReceiptList as ReceiptListDetails>
	           <#list issueList as issueListDetails>
	              <fo:table border-style="solid">
					<fo:table-column column-width="80pt"/>
					<fo:table-column column-width="80pt"/>
					<fo:table-column column-width="100pt"/>
					<fo:table-column column-width="80pt"/>
					<fo:table-column column-width="70pt"/>
					<fo:table-column column-width="70pt"/>
					<fo:table-column column-width="100pt"/>
					<fo:table-column column-width="100pt"/>
					<fo:table-column column-width="70pt"/>
					<fo:table-column column-width="70pt"/>
					<fo:table-column column-width="90pt"/>
					<fo:table-column column-width="25pt"/>
					<fo:table-body>
					   <fo:table-row>
				           <fo:table-cell border-style="solid">
							   <fo:block text-align="left" >${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(mrrListDetails.get("Date") ,"dd-MMM-yyyy")?if_exists}</fo:block>
						   </fo:table-cell>
						   <fo:table-cell border-style="solid">
							   <fo:block text-align="left" >${mrrListDetails.get("BILLNo")?if_exists}</fo:block>
						   </fo:table-cell>
						   <fo:table-cell border-style="solid">
							  <fo:block text-align="left" >${mrrListDetails.get("MRRNo")?if_exists}</fo:block>
						   </fo:table-cell>
						   <fo:table-cell border-style="solid">
							  <fo:block text-align="right" >${ReceiptListDetails.get("ReceiptQty")?if_exists?string("##0.00")}</fo:block>
						   </fo:table-cell>
						   <fo:table-cell border-style="solid">
							  <fo:block text-align="right" >${ReceiptListDetails.get("ReceiptRate")?if_exists?string("##0.00")}</fo:block>
						   </fo:table-cell>
						   <fo:table-cell border-style="solid">
							  <fo:block text-align="right" >${ReceiptListDetails.get("ReceiptAmount")?if_exists?string("##0.00")}</fo:block>
						   </fo:table-cell>
						   <fo:table-cell border-style="solid">
							  <fo:block text-align="center" >${issueListDetails.get("IndentNo")?if_exists}</fo:block>
						   </fo:table-cell>
						   <fo:table-cell border-style="solid">
							  <fo:block text-align="right" >${issueListDetails.get("IssueQty")?if_exists?string("##0.00")}</fo:block>
						   </fo:table-cell>
						    <fo:table-cell border-style="solid">
							  <fo:block text-align="right" >${issueListDetails.get("IssueRate")?if_exists?string("##0.00")}</fo:block>
						   </fo:table-cell>
						   <fo:table-cell border-style="solid">
							  <fo:block text-align="right" >${issueListDetails.get("IssueAmount")?if_exists?string("##0.00")}</fo:block>
						   </fo:table-cell>						   				
					    </fo:table-row>
					</fo:table-body>   
				</fo:table>	
				</#list> 
				</#list>
			   </#list>      	            
	       </fo:block> 	 		
	   </fo:flow>
	</fo:page-sequence>
	</#if>
	</#if>
	</#if>
</fo:root>
</#escape>	    
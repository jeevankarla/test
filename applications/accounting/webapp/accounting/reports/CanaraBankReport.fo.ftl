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
            <fo:simple-page-master master-name="main" page-height="12in" page-width="14in"  margin-left=".3in" margin-right=".3in" margin-top=".3in" margin-bottom="0.5in">
                <fo:region-body margin-top="0.5in"/>
                <fo:region-before extent="1in"/>
                <fo:region-after extent="1in"/>
            </fo:simple-page-master>
        </fo:layout-master-set>
      <#if bankTypeList?has_content> 	    
        ${setRequestAttribute("OUTPUT_FILENAME", "CanaraBankReport.pdf")}
        <fo:page-sequence master-reference="main" force-page-count="no-force" font-family="Courier,monospace">		
        <fo:static-content flow-name="xsl-region-before">
        	<#assign reportHeader = delegator.findOne("TenantConfiguration", {"propertyTypeEnumId" : "COMPANY_HEADER","propertyName" : "reportHeaderLable"}, true)>
        	<#assign reportSubHeader = delegator.findOne("TenantConfiguration", {"propertyTypeEnumId" : "COMPANY_HEADER","propertyName" : "reportSubHeaderLable"}, true)>
             <fo:block  keep-together="always" text-align="right" font-family="Courier,monospace" white-space-collapse="false"> &#160;${uiLabelMap.CommonPage}- <fo:page-number/></fo:block>
             <fo:block text-align="left"  keep-together="always"  white-space-collapse="false" linefeed-treatment="preserve">&#xA;</fo:block> 
        	 <fo:block text-align="center" font-size="13pt" keep-together="always"  white-space-collapse="false" font-weight="bold">&#160;${reportHeader.description?if_exists}</fo:block>
		     <fo:block text-align="center" font-size="12pt" keep-together="always"  white-space-collapse="false" font-weight="bold">&#160;${reportSubHeader.description?if_exists}</fo:block>
             <#--><fo:block text-align="center"  font-family="Courier,monospace" font-weight="bold"  white-space-collapse="false">  Bank Report As On ${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(nowTimestamp, "dd/MM/yy HH:mm:ss")} </fo:block>
             <fo:block text-align="left"  keep-together="always"  font-family="Courier,monospace" font-weight="bold" white-space-collapse="false"> UserLogin:<#if userLogin?exists>${userLogin.userLoginId?if_exists}</#if>               &#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160; &#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;Print Date :${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(nowTimestamp, "dd/MM/yy HH:mm:ss")}</fo:block>-->
        </fo:static-content>
        <fo:flow flow-name="xsl-region-body"  font-family="Courier,monospace">
            <fo:block border-style="solid" space-before="2in" >
		 		<fo:table>
		 			<fo:table-column column-width="7%"/>
		 			<fo:table-column column-width="11%"/>
					<fo:table-column column-width="7%"/>
					<fo:table-column column-width="7%"/>
					<fo:table-column column-width="8%"/>
					<fo:table-column column-width="7%"/>
					<fo:table-column column-width="7%"/>
					<fo:table-column column-width="8%"/>
					<fo:table-column column-width="8%"/>
					<fo:table-column column-width="8%"/>
					<fo:table-column column-width="8%"/>
						<fo:table-body>
         					 <fo:table-row>
         						<fo:table-cell border-right-style="solid" border-bottom-style="solid">
         						 	<fo:block font-size="4pt">&#160;</fo:block>
         							<fo:block text-align="left" white-space-collapse="false"  font-size="11pt" keep-together="always" font-family="Verdana">&#160;Txn Type</fo:block>
          						</fo:table-cell>
          						<fo:table-cell border-right-style="solid" border-bottom-style="solid">
         						 	<fo:block font-size="4pt">&#160;</fo:block>
         							<fo:block text-align="left" white-space-collapse="false"  font-size="11pt" keep-together="always" font-family="Verdana">&#160;Acc No</fo:block>
          						</fo:table-cell>
          						<fo:table-cell border-right-style="solid">
          							<fo:block font-size="4pt">&#160;</fo:block>
         							<fo:block text-align="left" white-space-collapse="false"  font-size="11pt" keep-together="always" font-family="Verdana">&#160;Branch Code</fo:block>
          						</fo:table-cell>
          						<fo:table-cell border-right-style="solid">
          							<fo:block font-size="4pt">&#160;</fo:block>	
         							<fo:block text-align="left" white-space-collapse="false"  font-size="11pt" keep-together="always" font-family="Verdana">&#160;Txn Code</fo:block>
          						</fo:table-cell>
          						
          						<fo:table-cell border-right-style="solid">
          							<fo:block font-size="4pt">&#160;</fo:block>
         							<fo:block text-align="left" white-space-collapse="false"  font-size="11pt" keep-together="always" font-family="Verdana">&#160;Txn Date</fo:block>
          						</fo:table-cell>
          						<fo:table-cell border-right-style="solid">
          							<fo:block font-size="4pt">&#160;</fo:block>
         							<fo:block text-align="left" white-space-collapse="false"  font-size="11pt" keep-together="always" font-family="Verdana">&#160;Dr/Cr</fo:block>
          						</fo:table-cell>
          						<fo:table-cell border-right-style="solid">
          							<fo:block font-size="4pt">&#160;</fo:block>
         							<fo:block text-align="left" white-space-collapse="false"  font-size="11pt" keep-together="always" font-family="Verdana">&#160;Txn CCY</fo:block>
          						</fo:table-cell>
          						<fo:table-cell border-right-style="solid">
          							<fo:block font-size="4pt">&#160;</fo:block>
         							<fo:block text-align="left" white-space-collapse="false"  font-size="11pt" keep-together="always" font-family="Verdana">&#160;Amt TCY</fo:block>
          						</fo:table-cell>
          						<fo:table-cell border-right-style="solid">
          							<fo:block font-size="4pt">&#160;</fo:block>
         							<fo:block text-align="left" white-space-collapse="false"  font-size="11pt" keep-together="always" font-family="Verdana">&#160;Rate Config</fo:block>
          						</fo:table-cell>
          						<fo:table-cell border-right-style="solid">
          							<fo:block font-size="4pt">&#160;</fo:block>
         							<fo:block text-align="left" white-space-collapse="false"  font-size="11pt" keep-together="always" font-family="Verdana">&#160;Ref No</fo:block>
          						</fo:table-cell>
          						<fo:table-cell border-right-style="solid">
          							<fo:block font-size="4pt">&#160;</fo:block>
         							<fo:block text-align="left" white-space-collapse="false"  font-size="11pt" keep-together="always" font-family="Verdana">&#160;Ref Doc No</fo:block>
          						</fo:table-cell>
							</fo:table-row>
							<#list bankTypeList as eachDet>
  							<#if eachDet?has_content>
							 <fo:table-row >
             						<fo:table-cell border-right-style="solid" border-bottom-style="solid">
             						 	<fo:block font-size="4pt">&#160;</fo:block>
             							<fo:block text-align="left" white-space-collapse="false"  font-size="11pt" keep-together="always">&#160;<#if eachDet.TxnType?has_content>${eachDet.TxnType?if_exists} </#if></fo:block>
              						</fo:table-cell>
              						<fo:table-cell border-right-style="solid" border-bottom-style="solid">
             						 	<fo:block font-size="4pt">&#160;</fo:block>
             							<fo:block text-align="left" white-space-collapse="false"  font-size="11pt" keep-together="always">&#160;<#if eachDet.accntNo?has_content>${eachDet.accntNo?if_exists} </#if></fo:block>
              						</fo:table-cell>
              						<fo:table-cell border-style="solid">
              							<fo:block font-size="4pt">&#160;</fo:block>
             							<fo:block text-align="left" white-space-collapse="false"  font-size="11pt" keep-together=""><#if eachDet.branchCode?has_content>${eachDet.get("branchCode")?if_exists}</#if></fo:block>
              						</fo:table-cell>
              						<fo:table-cell border-style="solid">
              							<fo:block font-size="4pt">&#160;</fo:block>
             							<fo:block text-align="left" white-space-collapse="false"  font-size="11pt" keep-together="">&#160;<#if eachDet.txnCode?has_content>${eachDet.get("txnCode")?if_exists}</#if></fo:block>
              						</fo:table-cell>
              						<fo:table-cell border-style="solid">
              							<fo:block font-size="4pt">&#160;</fo:block>
             							<fo:block text-align="right" white-space-collapse="false"  font-size="11pt" keep-together="always">&#160;<#if eachDet.txnDate?has_content>${eachDet.get("txnDate")?if_exists}</#if>&#160;</fo:block>
              						</fo:table-cell>
              						<fo:table-cell border-style="solid">
              							<fo:block font-size="4pt">&#160;</fo:block>
             							<fo:block text-align="right" white-space-collapse="false"  font-size="11pt" keep-together="always">&#160;<#if eachDet.DrCr?has_content>${eachDet.get("DrCr")?if_exists}</#if>&#160;</fo:block>
              						</fo:table-cell>
              						<fo:table-cell border-style="solid">
              							<fo:block font-size="4pt">&#160;</fo:block>
             							<fo:block text-align="left" white-space-collapse="false"  font-size="11pt" keep-together="always">&#160;<#if eachDet.txnCCY?has_content>${eachDet.get("txnCCY")?if_exists}</#if></fo:block>
              						</fo:table-cell>
              						<fo:table-cell border-style="solid">
              							<fo:block font-size="4pt">&#160;</fo:block>
             							<fo:block text-align="right" white-space-collapse="false"  font-size="11pt" keep-together="always">&#160;<#if eachDet.amount?has_content>${eachDet.get("amount")?if_exists}</#if>&#160;</fo:block>
              						</fo:table-cell>
              						<fo:table-cell border-style="solid">
              							<fo:block font-size="4pt">&#160;</fo:block>
             							<fo:block text-align="left" white-space-collapse="false"  font-size="11pt" keep-together="always">&#160;<#if eachDet.rateConfig?has_content>${eachDet.get("rateConfig")?if_exists}</#if></fo:block>
              						</fo:table-cell>
              						<fo:table-cell border-style="solid">
              							<fo:block font-size="4pt">&#160;</fo:block>
             							<fo:block text-align="right" white-space-collapse="false"  font-size="11pt" keep-together="always">&#160;<#if eachDet.refNo?has_content>${eachDet.get("refNo")?if_exists}</#if>&#160;</fo:block>
              						</fo:table-cell>
              						<fo:table-cell border-style="solid">
              							<fo:block font-size="4pt">&#160;</fo:block>
             							<fo:block text-align="left" white-space-collapse="false"  font-size="11pt" keep-together="always">&#160;<#if eachDet.refDocNo?has_content>${eachDet.get("refDocNo")?if_exists}</#if></fo:block>
              						</fo:table-cell>
  								</fo:table-row>
  								</#if>
  								</#list>
	                </fo:table-body>
                </fo:table>
            </fo:block> 
            
            <fo:block linefeed-treatment="preserve">&#xA;</fo:block>
            <fo:block linefeed-treatment="preserve">&#xA;</fo:block>
			<fo:block linefeed-treatment="preserve">&#xA;</fo:block>
        	<fo:block  keep-together="always" text-align="left" font-size="12pt" white-space-collapse="false">Authorised Signatories</fo:block>	
        	<fo:block linefeed-treatment="preserve">&#xA;</fo:block>
			<fo:block linefeed-treatment="preserve">&#xA;</fo:block>
			<fo:block text-align="right" font-size="12pt" white-space-collapse="false">Enclosure to Cheque No. ${paymentRefNum?if_exists}&#160;Dt:${paymentDate?if_exists}</fo:block>
			<fo:block text-align="right" font-size="12pt" white-space-collapse="false">Drawn on for transfer through RTGS/NEFT.</fo:block>
			<fo:block text-align="right" font-size="12pt" white-space-collapse="false">For Rs. ${totnetSal?if_exists?string("#0.00")}&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;</fo:block>
		</fo:flow>
		</fo:page-sequence>
	<#else>
		<fo:page-sequence master-reference="main">
	    	<fo:flow flow-name="xsl-region-body" font-family="Helvetica">
	       		 <fo:block font-size="14pt">
	            	${uiLabelMap.NoOrdersFound}.
	       		 </fo:block>
	    	</fo:flow>
		</fo:page-sequence>	
	 </#if>  
 </fo:root>
</#escape>
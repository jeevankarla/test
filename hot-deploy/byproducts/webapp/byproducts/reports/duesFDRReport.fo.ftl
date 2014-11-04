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
            <fo:simple-page-master master-name="main" page-height="12in" page-width="10in"  margin-left=".3in" margin-right=".3in" margin-top=".5in" margin-bottom="0.5in">
                <fo:region-body margin-top="2in"/>
                <fo:region-before extent="1in"/>
                <fo:region-after extent="1in"/>
            </fo:simple-page-master>
        </fo:layout-master-set>
        
        ${setRequestAttribute("OUTPUT_FILENAME", "DuesExcessFDRReport.pdf")}
        <#if duesFDRList?has_content>
        <fo:page-sequence master-reference="main" force-page-count="no-force" font-family="Courier,monospace">		
        <fo:static-content flow-name="xsl-region-before">
              		<fo:block  keep-together="always" text-align="right" font-family="Courier,monospace" white-space-collapse="false"></fo:block>
              		<fo:block text-align="left"  keep-together="always"  white-space-collapse="false" linefeed-treatment="preserve">&#xA;</fo:block> 
              		<fo:block font-weight="bold" font-size="14pt" text-align="center">KARNATAKA CO-OPERATIVE MILK PRODUCERS FEDERATION LTD</fo:block>
                    <fo:block text-align="center" font-size="14pt" font-weight="bold">UNIT : MOTHER DAIRY , G.K.V.K POST, YELAHANKA, BANGALORE -560065.</fo:block>
                    <fo:block text-align="center" linefeed-treatment="preserve">&#xA;</fo:block>
                    <fo:block text-align="center" font-size="14pt" font-weight="bold">Dues In Excess Of Fixed Deposit As On ${displayDate?if_exists}</fo:block>
                    <fo:block keep-together="always" text-align="left" white-space-collapse="false" font-size="12pt">UserLogin: <#if userLogin?exists>${userLogin.userLoginId?if_exists}</#if>                                         Print Date: ${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(nowTimestamp, "dd/MM/yy HH:mm:ss")}</fo:block>
              		<fo:block text-align="left"  keep-together="always"  white-space-collapse="false">==============================================================================================</fo:block> 
		        	<fo:block text-align="left"  keep-together="always"  white-space-collapse="false" font-size="14pt" font-weight="bold">SL  CODE       AGENT-NAME                 OB      FD-AMOUNT    DIFF-AMOUNT</fo:block> 
		        	<fo:block text-align="left"  keep-together="always"  white-space-collapse="false">==============================================================================================</fo:block> 
		           	
            </fo:static-content>		    	
		        	<fo:flow flow-name="xsl-region-body"  font-family="Courier,monospace">	
		        	
            	<fo:block>
                 	<fo:table table-layout="fixed">
                    <fo:table-column column-width="40pt"/> 
               	    <fo:table-column column-width="80pt"/>
            		<fo:table-column column-width="140pt"/>
            		<fo:table-column column-width="120pt"/>
            		<fo:table-column column-width="100pt"/>	
            		<fo:table-column column-width="120pt"/>
                    <fo:table-body>
                    	<#assign sno = 0>
                    	<#list duesFDRList as eachPartyDetail>
                    		<#assign sno = sno+1>
                    		<fo:table-row>
               					<fo:table-cell>
                           			<fo:block  keep-together="always" text-align="left" white-space-collapse="false" font-size="14pt">&#160;${sno?if_exists}</fo:block>  
                       			</fo:table-cell>
                       			<fo:table-cell>
                           			<fo:block  keep-together="always" text-align="left" white-space-collapse="false" font-size="14pt">${eachPartyDetail.get("facilityId")?if_exists}</fo:block>  
                       			</fo:table-cell>
                       			<fo:table-cell>
                           			<fo:block text-align="left" white-space-collapse="false" font-size="14pt" wrap-option="wrap">${eachPartyDetail.get("facilityName")?if_exists}</fo:block>  
                       			</fo:table-cell>
                       			<fo:table-cell>
                           			<fo:block  keep-together="always" text-align="right" white-space-collapse="false" font-size="14pt">${eachPartyDetail.get("openingBalance")?if_exists?string("#0.00")}</fo:block>  
                       			</fo:table-cell>
                       			<#-- <fo:table-cell>
                       				<#assign fdrsize = 0>
                       				<#assign fdrNumber = "">
                       				<#if eachPartyDetail.get("fdrNumber")?has_content>
                       					<#assign fdrsize = (eachPartyDetail.get("fdrNumber")).length()>
                       					<#assign fdrNumber = eachPartyDetail.get("fdrNumber")>
                       					<fo:block text-align="right" font-size="9pt" wrap-option="wrap">${Static["org.ofbiz.order.order.OrderServices"].nameTrim((StringUtil.wrapString(fdrNumber?if_exists)),14)}</fo:block>
                       				</#if>
                           			
                           			<#if (fdrsize>14)> 
                           				<#assign subStrNum = fdrNumber.substring(15,(fdrsize-1))>
                           				<fo:block text-align="right" font-size="9pt" wrap-option="wrap">${Static["org.ofbiz.order.order.OrderServices"].nameTrim((StringUtil.wrapString(subStrNum?if_exists)),14)}</fo:block>
                           				<#if (fdrsize>28)>
                           					<#assign subSubStrNum = fdrNumber.substring(29,(fdrsize-1))>
                           					<fo:block text-align="right" font-size="9pt" wrap-option="wrap">${Static["org.ofbiz.order.order.OrderServices"].nameTrim((StringUtil.wrapString(subSubStrNum?if_exists)),14)}</fo:block>
                           				</#if>
                           			</#if> 
                       			</fo:table-cell> -->
                       			<fo:table-cell>
                           			<fo:block  keep-together="always" text-align="right" white-space-collapse="false" font-size="14pt">${eachPartyDetail.get("fdrAmount")?if_exists?string("#0.00")}</fo:block>  
                       			</fo:table-cell>
                       			<fo:table-cell>
                           			<fo:block  keep-together="always" text-align="right" white-space-collapse="false" font-size="14pt">${eachPartyDetail.get("diffAmount")?if_exists?string("#0.00")}</fo:block>  
                       			</fo:table-cell>
                       		</fo:table-row>
                       	</#list>
					</fo:table-body>
                </fo:table>
            </fo:block> 		
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
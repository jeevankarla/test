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
            margin-top=".2in" margin-bottom=".7n" margin-left=".3in" margin-right=".5in">
        <fo:region-body margin-top="1.6in"/>
        <fo:region-before extent="1.0in"/>
        <fo:region-after extent="1.5in"/>        
    </fo:simple-page-master>   
</fo:layout-master-set>
	<#if materialIndentList?has_content>
			<fo:page-sequence master-reference="main"> 	 <#-- the footer -->
			<#assign partyGroup = delegator.findOne("PartyGroup",{"partyId" :fromPartyId}, true)>
     		<fo:static-content flow-name="xsl-region-before">
     			<fo:block  keep-together="always" text-align="center" font-family="Courier,monospace" white-space-collapse="false" font-size="12pt" font-weight="bold" >  KARNATAKA CO-OPERATIVE MILK PRODUCERS FEDERATION LTD. </fo:block>
				<fo:block  keep-together="always" text-align="center" font-family="Courier,monospace" white-space-collapse="false" font-size="12pt" font-weight="bold" >  UNIT : MOTHER DAIRY:G.K.V.K POST : YELAHANKA:BANGALORE : 560065  </fo:block>
				<fo:block  keep-together="always" text-align="center" font-family="Courier,monospace" white-space-collapse="false" font-size="12pt" font-weight="bold" >  &#160; </fo:block>
        		<fo:block white-space-collapse="false" keep-together="always" font-family="Courier,monospace"  font-size="12pt" >&#160;                                 MATERIAL REQUISITION              Print Date: ${(Static["org.ofbiz.base.util.UtilDateTime"].toDateString(nowTimestamp, "dd MMM, yyyy"))?upper_case}</fo:block>
        		<fo:block  keep-together="always" text-align="center" font-family="Courier,monospace" white-space-collapse="false" font-size="12pt" font-weight="bold" >  &#160; </fo:block>
        		<#assign status=delegator.findOne("StatusItem",{"statusId":custRequestStatus},true)>
        		<fo:block white-space-collapse="false" keep-together="always" font-family="Courier,monospace"  font-size="12pt">&#160;                               SUB : ISSUE OF MATERIALS     INDENT STATUS:${status.description}                     </fo:block>
        		<fo:block  keep-together="always" text-align="center" font-family="Courier,monospace" white-space-collapse="false" font-size="12pt" font-weight="bold" >  &#160; </fo:block>
        		<fo:block white-space-collapse="false" keep-together="always" font-family="Courier,monospace"  font-size="11pt">&#160;INDENT NO:${custRequestId}                  INDENT DATE:${(Static["org.ofbiz.base.util.UtilDateTime"].toDateString(custRequestDate, "dd MMM, yyyy"))?upper_case} DEPARTMENT:${partyGroup.groupName}                                    </fo:block>
        	</fo:static-content>       
          <fo:flow flow-name="xsl-region-body" font-family="Courier,monospace" font-size="11pt">
           		<fo:block>
           			<fo:table>
       					 <fo:table-column border="solid" column-width="50pt"/>
       					 <fo:table-column border="solid" column-width="55pt"/>
       					 <fo:table-column border="solid" column-width="200pt"/>
       					 <fo:table-column border="solid" column-width="60pt"/>
       					 <fo:table-column border="solid" column-width="50pt"/>
       					 <fo:table-column border="solid" column-width="50pt"/>
       					 <fo:table-column border="solid" column-width="70pt"/>
       					 <fo:table-column border="solid" column-width="80pt"/>
       					 <fo:table-column border="solid" column-width="50pt"/>
       					 <fo:table-body>
       					 	<fo:table-row >
       					 		<fo:table-cell text-align="center" border="solid"><fo:block>SL.NO</fo:block></fo:table-cell>
       					 		<fo:table-cell text-align="center" border="solid"><fo:block>CODE</fo:block></fo:table-cell>
       					 		<fo:table-cell text-align="center" border="solid"><fo:block>DESCRIPTION</fo:block></fo:table-cell>
       					 		<fo:table-cell text-align="center" border="solid"><fo:block>INDENTED QTY</fo:block></fo:table-cell>
       					 		<fo:table-cell text-align="center" border="solid"><fo:block>UNIT PRICE</fo:block></fo:table-cell>
       					 		<fo:table-cell text-align="center" border="solid"><fo:block>STOCK</fo:block></fo:table-cell>
       					 		<fo:table-cell text-align="center" border="solid"><fo:block>ISSUED QTY AS ON ${(Static["org.ofbiz.base.util.UtilDateTime"].toDateString(nowTimestamp, "dd MMM, yyyy"))}</fo:block></fo:table-cell>
       					 		<fo:table-cell text-align="center" border="solid"><fo:block>VALUE</fo:block></fo:table-cell>
       					 		<fo:table-cell text-align="center" border="solid"><fo:block>LEDGER NO</fo:block></fo:table-cell>
       					 	</fo:table-row>
       					 	<#assign sno=1>
						<#list materialIndentList as indent>
							<fo:table-row >
     					 		<fo:table-cell text-align="center" ><fo:block>${sno}</fo:block></fo:table-cell>
       					 		<fo:table-cell text-align="center" ><fo:block>${indent.get("internalName")?if_exists}</fo:block></fo:table-cell>
       					 		<fo:table-cell text-align="center" ><fo:block>${indent.get("productdesc")?if_exists}</fo:block></fo:table-cell>
       					 		<fo:table-cell text-align="center" ><fo:block>${indent.get("identedQty")?if_exists}</fo:block></fo:table-cell>
       					 		<fo:table-cell text-align="right" ><fo:block>${indent.get("totalUnitPrice")?if_exists?string("#0.00")}</fo:block></fo:table-cell>
       					 		<fo:table-cell text-align="center" ><fo:block>${indent.get("stock")?if_exists}</fo:block></fo:table-cell>
       					 		<fo:table-cell text-align="center" ><fo:block>${indent.get("issuedQty")?if_exists}</fo:block></fo:table-cell>
       					 		<fo:table-cell text-align="right" ><fo:block>${indent.get("totalValue")?if_exists?string("#0.00")}</fo:block></fo:table-cell>
       					 		<fo:table-cell text-align="center" ><fo:block>${indent.get("ledgerNo")?if_exists}</fo:block></fo:table-cell>
       					 	</fo:table-row>
       					 	<#assign sno=sno+1>
						</#list>
       					 </fo:table-body>
           			</fo:table>
				    </fo:block>
				<fo:block  keep-together="always" text-align="center" font-family="Courier,monospace" white-space-collapse="false" font-size="12pt" font-weight="bold" >  &#160; </fo:block>
				<fo:block  keep-together="always" text-align="center" font-family="Courier,monospace" white-space-collapse="false" font-size="12pt" font-weight="bold" >  &#160; </fo:block>
				<fo:block white-space-collapse="false" keep-together="always" font-family="Courier,monospace"  font-size="12pt" >&#160;CASE WORKER                         STORES OFFICER                       RECEIVED BY</fo:block>
          </fo:flow>          
        </fo:page-sequence> 
        <#else>    	
		<fo:page-sequence master-reference="main">
			<fo:flow flow-name="xsl-region-body" font-family="Courier,monospace">
   		 		<fo:block font-size="14pt">
        			No Issued Products Found For This Indent...!</fo:block>
			</fo:flow>
		</fo:page-sequence>		
    </#if>  
  </fo:root>
</#escape>
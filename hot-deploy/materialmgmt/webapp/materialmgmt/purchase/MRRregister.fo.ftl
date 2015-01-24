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
specific language governing permissions and limitationsborder-style="solid"border-style="solid"
under the License.
-->

<#escape x as x?xml>
<fo:root xmlns:fo="http://www.w3.org/1999/XSL/Format">

<#-- do not display columns associated with values specified in the request, ie constraint values -->
<fo:layout-master-set>
	<fo:simple-page-master master-name="main" page-height="12in" page-width="12in"
            margin-top="0.1in" margin-bottom=".7in" margin-left=".5in" margin-right=".5in">
        <fo:region-body margin-top="1.42in"/>
        <fo:region-before extent="1.in"/>
        <fo:region-after extent="1.5in"/>        
    </fo:simple-page-master>   
</fo:layout-master-set>
 <#if mrrList?has_content> 

${setRequestAttribute("OUTPUT_FILENAME", "LoanAvailedReport.pdf")}

<fo:page-sequence master-reference="main" force-page-count="no-force" font-family="Courier,monospace">					
			<fo:static-content flow-name="xsl-region-before">
				<#--	<fo:block  keep-together="always" text-align="center" font-family="Courier,monospace" white-space-collapse="false">&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;UserLogin : <#if userLogin?exists>${userLogin.userLoginId?if_exists}</#if></fo:block>
					<fo:block  keep-together="always" text-align="center" font-family="Courier,monospace" white-space-collapse="false">&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;Date:${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(nowTimestamp, "dd/MM/yy HH:mm:ss")}</fo:block> -->
		            <fo:block  keep-together="always" text-align="center" font-family="Courier,monospace" white-space-collapse="false" font-size="12pt" > &#160;&#160;  </fo:block>
					<fo:block  keep-together="always" text-align="center" font-family="Courier,monospace" white-space-collapse="false" font-size="12pt" >  KARNATAKA CO-OPERATIVE MILK PRODUCERS FEDERATION LTD. </fo:block>
					<fo:block  keep-together="always" text-align="center" font-family="Courier,monospace" white-space-collapse="false" font-size="12pt" >  UNIT : MOTHER DAIRY:G.K.V.K POST : YELAHANKA:BANGALORE : 560065  </fo:block>
			        <fo:block  keep-together="always" text-align="center" font-family="Courier,monospace" white-space-collapse="false" font-size="5pt" > ______________________________________________________________________________________________________________________________________________________________________________________________ </fo:block>
			        <fo:block  keep-together="always" text-align="center" font-family="Courier,monospace" white-space-collapse="false" font-size="12pt" > &#160;&#160;  </fo:block>
			
		          
              		<#-- <#assign stockDetails = delegator.findOne("Stock", {"stockId" :parameters.stockId }, true)>
		   		          <#if stockDetails?has_content>
			        	   <fo:block white-space-collapse="false" font-size="10pt"  font-family="Helvetica" keep-together="always" >&#160;  STORE CODE:${parameters.stockId}&#160;    &#160;     &#160;  DESCRIPTION:${stockDetails.get("description")?if_exists}</fo:block>
			               </#if>
              		   -->
              		<fo:block  keep-together="always" text-align="left" font-family="Helvetica" white-space-collapse="false"  font-size="10pt" >__________________________________________________________________________________________________________________________________________________________________________________________________________________</fo:block>
              	    <fo:block  font-size="12pt" font-weight="bold"  keep-together="always" text-align="left"  white-space-collapse="false" >SINo MRRNo  MRRDate VendorCode   VendorName       BillNo   BillDate     Amount  AmountPaid    Department St.Up.Date    </fo:block>
              		<fo:block  keep-together="always" text-align="left" font-family="Helvetica" white-space-collapse="false"  font-size="10pt" >______________________________________________________________________________________________________________________________________________________________________________________________________</fo:block>               
            </fo:static-content>		
           <fo:flow flow-name="xsl-region-body"   font-family="Courier,monospace">		
            	<fo:block>
                 	<fo:table  >
                    <fo:table-column column-width="30pt"/>
                    <fo:table-column column-width="50pt"/>
                    <fo:table-column column-width="60pt"/>  
               	    <fo:table-column column-width="90pt"/>
               	    <fo:table-column column-width="130pt"/>
            		<fo:table-column column-width="70pt"/>
            		<fo:table-column column-width="60pt"/>
                    <fo:table-column column-width="80pt"/>
                    <fo:table-column column-width="80pt"/>  
               	    <fo:table-column column-width="110pt"/>
               	    <fo:table-column column-width="60pt"/>
                    <fo:table-body>
                      <#assign sNo=1>
	                  <#list mrrList as mrrListItem>
                      <fo:table-row>
                	   <fo:table-cell  ><fo:block text-align="left"   font-size="10pt" >${sNo?if_exists}</fo:block></fo:table-cell>     
  				  	   <fo:table-cell  ><fo:block text-align="center"   font-size="10pt" >   ${mrrListItem.get("receiptId")?if_exists}  </fo:block></fo:table-cell>     
  				       <fo:table-cell  ><fo:block text-align="center"   font-size="10pt" > ${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(mrrListItem.get("datetimeReceived"), "dd/MM/yy")?if_exists}   </fo:block></fo:table-cell>     
  				       <fo:table-cell  ><fo:block text-align="center"   font-size="10pt">${mrrListItem.get("partyId")?if_exists} </fo:block></fo:table-cell>     
  				       <fo:table-cell  ><fo:block text-align="left"   font-size="10pt"> ${mrrListItem.get("partyName")?if_exists} </fo:block></fo:table-cell>     
  				       <fo:table-cell  ><fo:block text-align="left"  font-size="10pt" >${mrrListItem.get("invoiceId")?if_exists}</fo:block></fo:table-cell>  
  				       <fo:table-cell  ><fo:block text-align="center"   font-size="10pt" > ${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(mrrListItem.get("invoiceDate"),"dd/MM/yy")?if_exists}   </fo:block></fo:table-cell>     
  				  	   <fo:table-cell  ><fo:block text-align="right"  font-size="10pt"> ${mrrListItem.get("invoiceAmount")?if_exists?string("##0.00")} </fo:block></fo:table-cell>     
  				       <fo:table-cell  ><fo:block text-align="right"  font-size="10pt" >${mrrListItem.get("paidAmount")?if_exists?string("##0.00")} </fo:block></fo:table-cell>     
  				       <fo:table-cell  ><fo:block text-align="right"  font-size="10pt" >${mrrListItem.get("deptName")?if_exists}</fo:block></fo:table-cell>     
  				       <fo:table-cell  ><fo:block text-align="center"   font-size="10pt"> ${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(mrrListItem.get("dueDate"),"dd/MM/yy")?if_exists}   </fo:block></fo:table-cell>     
  				   <#assign sNo=sNo+1>
	                               </fo:table-row>
                                </#list>
                    </fo:table-body>
                </fo:table>
               </fo:block>
                    <fo:block  keep-together="always" text-align="left" font-family="Helvetica" white-space-collapse="false"  font-size="11pt" >-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------</fo:block>
              		<fo:block  keep-together="always" text-align="left" font-family="Helvetica" white-space-collapse="false"  font-size="10pt" >TOTAL :&#160;&#160;&#160;&#160;   &#160;&#160;                                                                                                                                                                         ${shipmentMap.get("totalInvoiceAmt")?if_exists?string("##0.00")}            &#160;         ${shipmentMap.get("totalPaidAmt")?if_exists?string("##0.00")}  </fo:block>
                    <fo:block  keep-together="always" text-align="left" font-family="Helvetica" white-space-collapse="false"  font-size="11pt" >-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------</fo:block>

			 </fo:flow>
			 </fo:page-sequence>
		 <#else>
				<fo:page-sequence master-reference="main">
    			<fo:flow flow-name="xsl-region-body" font-family="Helvetica">
       		 		<fo:block font-size="14pt">
            			NO RECORDS FOUND
       		 		</fo:block>
    			</fo:flow>
			</fo:page-sequence>
			</#if>
</fo:root>
</#escape>
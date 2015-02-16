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
	<fo:simple-page-master master-name="main" page-height="12in" page-width="10in"
            margin-top="0.1in" margin-bottom=".7in" margin-left=".5in" margin-right=".5in">
        <fo:region-body margin-top="1.9in"/>
        <fo:region-before extent="1.in"/>
        <fo:region-after extent="1.5in"/>        
    </fo:simple-page-master>   
</fo:layout-master-set>
${setRequestAttribute("OUTPUT_FILENAME", "LoanAvailedReport.pdf")}
 <#if orderDetailsList?has_content> 

<fo:page-sequence master-reference="main" force-page-count="no-force" font-family="Courier,monospace">					
		<fo:static-content flow-name="xsl-region-before">
			<fo:block  keep-together="always" text-align="center" font-family="Courier,monospace" white-space-collapse="false">&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;UserLogin : <#if userLogin?exists>${userLogin.userLoginId?if_exists}</#if></fo:block>
				<fo:block  keep-together="always" text-align="center" font-family="Courier,monospace" white-space-collapse="false">&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;Date:${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(nowTimestamp, "dd/MM/yy HH:mm:ss")}</fo:block> 
		        <fo:block  keep-together="always" text-align="center" font-family="Courier,monospace" white-space-collapse="false" font-size="12pt" > &#160;&#160;  </fo:block>
				
		         <fo:block  keep-together="always" text-align="center" font-family="Courier,monospace" white-space-collapse="false" font-size="12pt" font-weight="bold" >  KARNATAKA CO-OPERATIVE MILK PRODUCERS FEDERATION LTD. </fo:block>
				<fo:block  keep-together="always" text-align="center" font-family="Courier,monospace" white-space-collapse="false" font-size="12pt" font-weight="bold" >  UNIT : MOTHER DAIRY:G.K.V.K POST : YELAHANKA:BANGALORE : 560065  </fo:block>
			    <fo:block  keep-together="always" text-align="center" font-family="Courier,monospace" white-space-collapse="false" font-size="5pt" > ----------------------------------------------------------------------------------------------------------------------------------------------------------------------</fo:block>
                 <fo:block  keep-together="always" text-align="left" font-family="Courier,monospace" white-space-collapse="false" font-size="12pt" >Phone no:${allDetailsMap.get("companyPhone")?if_exists}  FAX:  ${allDetailsMap.get("companyFax")?if_exists}              	 		TIN  : ${allDetailsMap.get("tinNumber")?if_exists} </fo:block>
                 <fo:block  keep-together="always" text-align="left" font-family="Courier,monospace" white-space-collapse="false" font-size="12pt" >&#160;        22179084 /55   Email:${allDetailsMap.get("companyMail")?if_exists}  KST NO: ${allDetailsMap.get("kstNumber")?if_exists} </fo:block>
                 <fo:block  keep-together="always" text-align="left" font-family="Courier,monospace" white-space-collapse="false" font-size="12pt" >&#160;&#160;                              enggpur@motherdairykmf.in    CST NO: ${allDetailsMap.get("cstNumber")?if_exists} </fo:block>
                <fo:block  keep-together="always" text-align="center" font-family="Courier,monospace" white-space-collapse="false" font-size="12pt" >--------------------------------------------------------------------------------------------------- </fo:block>
	             		        <fo:block  keep-together="always" text-align="center" font-family="Courier,monospace" white-space-collapse="false" font-size="12pt" > &#160;&#160;  </fo:block>
	             		        <fo:block  keep-together="always" text-align="center" font-family="Courier,monospace" white-space-collapse="false" font-size="12pt" > &#160;&#160;  </fo:block>
	             
              	 
            </fo:static-content>		
           <fo:flow flow-name="xsl-region-body"   font-family="Courier,monospace">	
			        	<#--   <fo:block white-space-collapse="false" font-size="10pt"  font-family="Helvetica" keep-together="always" >&#160;  STORE CODE:${parameters.stockId}&#160;    &#160;     &#160;  DESCRIPTION:${stockDetails.get("description")?if_exists}</fo:block> -->
	            <fo:block  keep-together="always" text-align="center" font-family="Courier,monospace" white-space-collapse="false" font-size="12pt" font-weight="bold">EXTENSION PURCHASE ORDER </fo:block>
	            <fo:block  keep-together="always" text-align="center" font-family="Courier,monospace" white-space-collapse="false" font-size="12pt" > &#160;&#160;</fo:block>
              	<fo:block  keep-together="always" text-align="left" font-family="Courier,monospace" white-space-collapse="false" font-size="12pt" >PO NO:   ${allDetailsMap.get("orderId")?if_exists}                                                 PO DATED: ${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(allDetailsMap.get("orderDate")?if_exists, "dd-MMM-yy")}</fo:block>
                <fo:block  keep-together="always" text-align="left" font-family="Courier,monospace" white-space-collapse="false" font-size="12pt" >File NO: ${allDetailsMap.get("fileNo")?if_exists}  </fo:block>
                <fo:block  keep-together="always" text-align="center" font-family="Courier,monospace" white-space-collapse="false" font-size="12pt" > &#160;&#160; </fo:block>
                <fo:block  keep-together="always" text-align="left" font-family="Courier,monospace" white-space-collapse="false" font-size="12pt" >&#160;&#160;                                                                   FAX: ${allDetailsMap.get("faxNumber")?if_exists}   </fo:block>
              	<fo:block  keep-together="always" text-align="center" font-family="Courier,monospace" white-space-collapse="false" font-size="12pt" > </fo:block>
              	<fo:block  keep-together="always" text-align="left" font-family="Courier,monospace" white-space-collapse="false" font-size="12pt" > Address:<#if allDetailsMap.get("address1")?has_content> ${allDetailsMap.get("address1")},         <#else> </#if>                              </fo:block>
                <fo:block  keep-together="always" text-align="left" font-family="Courier,monospace" white-space-collapse="false" font-size="12pt" >        <#if allDetailsMap.get("address2")?has_content>  ${allDetailsMap.get("address2")?if_exists}, <#else> </#if>     </fo:block>
                <fo:block  keep-together="always" text-align="left" font-family="Courier,monospace" white-space-collapse="false" font-size="12pt" >         <#if allDetailsMap.get("city")?has_content> ${allDetailsMap.get("city")?if_exists}-${allDetailsMap.get("postalCode")?if_exists}. <#else> </#if>                          </fo:block>
                
               <fo:block  keep-together="always" text-align="left" font-family="Courier,monospace" white-space-collapse="false" font-size="12pt" > phone no:${allDetailsMap.get("phoneNumber")?if_exists}         </fo:block>
               <fo:block  text-align="center" font-family="Courier,monospace" white-space-collapse="false" font-size="12pt" >&#160;&#160; </fo:block>
               <fo:block   text-align="left" font-family="Courier,monospace" white-space-collapse="false" font-size="12pt" >  EARLIER CONFIRMATORY PO NO: ${allDetailsMap.get("refNo")?if_exists}                                 DTD: ${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(allDetailsMap.get("orderDate")?if_exists, "dd-MMM-yy")}</fo:block>
             	<fo:block   text-align="left" font-family="Courier,monospace" white-space-collapse="false" font-size="12pt" >  DAIRY ENQUIRY NO: ${allDetailsMap.get("enquiryId")?if_exists}                                         DATE:<#if allDetailsMap.get("enquiryDate")?has_content> ${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(allDetailsMap.get("enquiryDate")?if_exists, "dd-MMM-yy")} <#else> </#if>  </fo:block>
              	<fo:block  text-align="left" font-family="Courier,monospace" white-space-collapse="false" font-size="12pt" >QUOTATION NO:    ${allDetailsMap.get("quoteId")?if_exists}                                          DATE:<#if allDetailsMap.get("qutationDate")?has_content> ${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(allDetailsMap.get("qutationDate")?if_exists, "dd-MMM-yy")}         <#else> </#if> </fo:block>
               <fo:block  keep-together="always" text-align="center" font-family="Courier,monospace" white-space-collapse="false" font-size="12pt" > &#160;&#160; </fo:block>
              	<fo:block   text-align="left" font-family="Courier,monospace" white-space-collapse="false" font-size="12pt" >---reference to your Quotation/proforma Invoice, we are pleased to place Order for --- Supply of materials as detailed below. </fo:block>
              	<fo:block  keep-together="always" text-align="center" font-family="Courier,monospace" white-space-collapse="false" font-size="12pt" >----------------------------------------------------------------------------------------------------- </fo:block>
              	<fo:block  keep-together="always" text-align="left" font-family="Courier,monospace" white-space-collapse="false" font-size="12pt" >&#160;&#160;SNO  ITEM CODE    DESCRIPTION                    UNIT        QTY    UNIT RATE    AMOUNT</fo:block>
              	<fo:block  keep-together="always" text-align="center" font-family="Courier,monospace" white-space-collapse="false" font-size="12pt" >----------------------------------------------------------------------------------------------------- </fo:block>
            	<fo:block>
                 <fo:table text-align="center" >
                    <fo:table-column column-width="50pt"/>
                    <fo:table-column column-width="90pt"/>
                    <fo:table-column column-width="220pt"/>  
               	    <fo:table-column column-width="60pt"/>
               	    <fo:table-column column-width="60pt"/>
            		 <fo:table-column column-width="70pt"/>
            		<fo:table-column column-width="90pt"/>
            		
                    <fo:table-body text-align="center">
                     <#assign sNo=1>
	                    
	                    <#list orderDetailsList as orderListItem>
	                    
	                  
					<#assign productId= orderListItem("productId")?if_exists >
		          <#assign productNameDetails = delegator.findOne("Product", {"productId" : productId}, true)>
		           <#if productNameDetails?has_content> 
                  	 <fo:table-row >
                	   <fo:table-cell  ><fo:block text-align="center"  font-size="10pt" >${sNo} </fo:block></fo:table-cell>     
  				  	   <fo:table-cell  ><fo:block text-align="left" font-size="10pt">${productNameDetails.get("internalName")?if_exists}</fo:block></fo:table-cell>     
  				       <fo:table-cell  ><fo:block text-align="left"   font-size="10pt" >${productNameDetails.get("productName")?if_exists} </fo:block></fo:table-cell>     
  				       <fo:table-cell  ><fo:block text-align="left" font-size="10pt">${productNameDetails.get("uomAbbr")?if_exists}</fo:block></fo:table-cell>     
  				       <fo:table-cell ><fo:block text-align="right"  font-size="10pt">${orderListItem.get("quantity")?if_exists}</fo:block></fo:table-cell>     
  			          <fo:table-cell  ><fo:block text-align="right"   font-size="10pt" >${orderListItem.get("unitPrice")?if_exists?string("##0.00")}</fo:block></fo:table-cell>     
  				       <fo:table-cell  ><fo:block text-align="right"  font-size="10pt" >${orderListItem.get("amount")?if_exists?string("##0.00")}</fo:block></fo:table-cell>     
  				         
  				     </fo:table-row>
  				    	<#assign sNo=sNo+1>
  				    	</#if>
  				     </#list>
                    </fo:table-body>
                </fo:table>
               </fo:block>
                <fo:block  keep-together="always" text-align="center" font-family="Courier,monospace" white-space-collapse="false" font-size="12pt" >----------------------------------------------------------------------------------------------------- </fo:block>
               <fo:block  keep-together="always" text-align="center" font-family="Courier,monospace" white-space-collapse="false" font-size="12pt" >&#160;&#160; </fo:block>
               <fo:block  keep-together="always" text-align="center" font-family="Courier,monospace" white-space-collapse="false" font-size="12pt" >&#160;&#160; </fo:block>
      <#if allDetailsMap.get("total")?has_content> 
    <fo:block  keep-together="always" text-align="left" font-family="Courier,monospace" white-space-collapse="false" font-size="12pt" > SUB TOTAL         : ${allDetailsMap.get("total")?if_exists?string("##0.00")}  </fo:block> </#if>
               <#-->
               <fo:block  keep-together="always" text-align="center" font-family="Courier,monospace" white-space-collapse="false" font-size="12pt" >&#160;&#160; </fo:block>
              -->
      <#if allDetailsMap.get("discount")?has_content && (allDetailsMap.get("discount")!=0)> 
            <fo:block  keep-together="always" text-align="left" font-family="Courier,monospace" white-space-collapse="false" font-size="12pt" > DISCOUNT          : ${allDetailsMap.get("discount")?if_exists} </fo:block></#if> 
               <#-->
               <fo:block  keep-together="always" text-align="center" font-family="Courier,monospace" white-space-collapse="false" font-size="12pt" >&#160;&#160; </fo:block>
               -->
         <#if allDetailsMap.get("pakfwdCharges")?has_content && (allDetailsMap.get("pakfwdCharges")!=0)> 
     <fo:block  keep-together="always" text-align="left" font-family="Courier,monospace" white-space-collapse="false" font-size="12pt" > P AND F CHARGES   : ${allDetailsMap.get("pakfwdCharges")?if_exists}  </fo:block>  </#if>
               <#-->
               <fo:block  keep-together="always" text-align="center" font-family="Courier,monospace" white-space-collapse="false" font-size="12pt" >&#160;&#160; </fo:block>
               -->
    <#if allDetailsMap.get("frightCharges")?has_content && (allDetailsMap.get("frightCharges")!=0)> 
     <fo:block  keep-together="always" text-align="left" font-family="Courier,monospace" white-space-collapse="false" font-size="12pt" > FREIGHT CHARGES   : ${allDetailsMap.get("frightCharges")?if_exists}  </fo:block>  </#if>
               <#-->
               <fo:block  keep-together="always" text-align="center" font-family="Courier,monospace" white-space-collapse="false" font-size="12pt" >&#160;&#160; </fo:block>
               -->
      <#if allDetailsMap.get("insurance")?has_content > 
     <fo:block  keep-together="always" text-align="left" font-family="Courier,monospace" white-space-collapse="false" font-size="12pt" > INSURANCE         : ${allDetailsMap.get("insurance")?if_exists}  </fo:block>  </#if>
               <#-->
               <fo:block  keep-together="always" text-align="center" font-family="Courier,monospace" white-space-collapse="false" font-size="12pt" >&#160;&#160; </fo:block>
               -->
    <#if allDetailsMap.get("otherCharges")?has_content && (allDetailsMap.get("otherCharges")!=0)> 
     <fo:block  keep-together="always" text-align="left" font-family="Courier,monospace" white-space-collapse="false" font-size="12pt" > OTHER CHARGES     : ${allDetailsMap.get("otherCharges")?if_exists}  </fo:block> </#if>
               <#-->
               <fo:block  keep-together="always" text-align="center" font-family="Courier,monospace" white-space-collapse="false" font-size="12pt" >&#160;&#160; </fo:block>
               -->

                      <#if allDetailsMap.get("exciseAmt")?has_content && (allDetailsMap.get("exciseAmt")!=0)> 
   <fo:block  keep-together="always" text-align="left" font-family="Courier,monospace" white-space-collapse="false" font-size="12pt" >EXCISE DUTY       : ${allDetailsMap.get("exciseAmt")?if_exists?string("##0.00")} </fo:block> </#if>
                <#-->
                <fo:block  keep-together="always" text-align="left" font-family="Courier,monospace" white-space-collapse="false" font-size="12pt" >TAX               : ${allDetailsMap.get("tax")?if_exists}  </fo:block>  
                -->
                <#assign taxDetailsList = allDetailsMap.get("taxDetailsList")?if_exists>
                 <#list taxDetailsList as taxDetailedItem>
                 <#-->
                  <fo:block  keep-together="always" text-align="center" font-family="Courier,monospace" white-space-collapse="false" font-size="12pt" >&#160;&#160; </fo:block>
                  -->
                  <fo:block  keep-together="always" text-align="left" font-family="Courier,monospace" white-space-collapse="false" font-size="12pt" ><#if taxDetailedItem.orderAdjustmentTypeId=="VAT_PUR">VAT TOTAL         :<#elseif taxDetailedItem.orderAdjustmentTypeId=="CST_PUR">CST TOTAL         :</#if> ${taxDetailedItem.sourcePercentage}% - ${taxDetailedItem.amount} INR </fo:block>
                 </#list>
                  <#-->
                  <fo:block  keep-together="always" text-align="center" font-family="Courier,monospace" white-space-collapse="false" font-size="12pt" >&#160;&#160; </fo:block>
                  -->
                   <#if allDetailsMap.get("grandTotal")?has_content> 
      <fo:block  keep-together="always" text-align="left" font-family="Courier,monospace" white-space-collapse="false" font-size="12pt" >  GRAND TOTAL       :<#if allDetailsMap.get("discount")?has_content> ${(allDetailsMap.get("grandTotal")-allDetailsMap.get("discount"))?string("##0.00")}<#else> ${allDetailsMap.get("grandTotal")?if_exists?string("##0.00")}</#if> </fo:block> </#if>
     
     <fo:block  keep-together="always" text-align="center" font-family="Courier,monospace" white-space-collapse="false" font-size="12pt" >&#160;&#160; </fo:block>
                     <#if allDetailsMap.get("delivery")?has_content> 
    <fo:block  keep-together="always" text-align="left" font-family="Courier,monospace" white-space-collapse="false" font-size="12pt" > DELIVERY          : ${allDetailsMap.get("delivery")?if_exists} </fo:block> </#if>
               <#-->
               <fo:block  keep-together="always" text-align="center" font-family="Courier,monospace" white-space-collapse="false" font-size="12pt" >&#160;&#160; </fo:block>
               -->
                     <#if allDetailsMap.get("placeOfDispatch")?has_content> 
    <fo:block  keep-together="always" text-align="left" font-family="Courier,monospace" white-space-collapse="false" font-size="12pt" > PLACE OF DISPATCH : ${allDetailsMap.get("placeOfDispatch")?if_exists} </fo:block> </#if> 
              <#-->
               <fo:block  keep-together="always" text-align="center" font-family="Courier,monospace" white-space-collapse="false" font-size="12pt" >&#160;&#160; </fo:block>
               -->
                       <#if allDetailsMap.get("waranty")?has_content> 
  <fo:block  keep-together="always" text-align="left" font-family="Courier,monospace" white-space-collapse="false" font-size="12pt" > WARANTY/GUARANTY  : ${allDetailsMap.get("waranty")?if_exists}  </fo:block>  </#if>
               <#-->
               <fo:block  keep-together="always" text-align="center" font-family="Courier,monospace" white-space-collapse="false" font-size="12pt" >&#160;&#160; </fo:block>
               -->
                  <#if allDetailsMap.get("payment")?has_content> 
       <fo:block  keep-together="always" text-align="left" font-family="Courier,monospace" white-space-collapse="false" font-size="12pt" >  PAYMENT           : ${allDetailsMap.get("payment")?if_exists} </fo:block>  </#if>
               <fo:block  keep-together="always" text-align="center" font-family="Courier,monospace" white-space-collapse="false" font-size="12pt" >&#160;&#160; </fo:block>
               <fo:block  keep-together="always" text-align="center" font-family="Courier,monospace" white-space-collapse="false" font-size="12pt" >&#160;&#160; </fo:block> 
               <fo:block   text-align="left" font-family="Courier,monospace" white-space-collapse="false" font-size="12pt" >&#160;&#160;Please send back the duplicate copy of the P.O. duly signed and sealed as a token of acceptance. You are requested to submit the bills in quadruplicate towards the supply of said materials. Also please quote the Purchase Order No and Date in all your Letters, Delivery, Notes, and Invoices etc. </fo:block>
                <fo:block  keep-together="always" text-align="center" font-family="Courier,monospace" white-space-collapse="false" font-size="12pt" >&#160;&#160; </fo:block>
                  <fo:block  keep-together="always" text-align="center" font-family="Courier,monospace" white-space-collapse="false" font-size="12pt" >&#160;&#160; </fo:block>
                               <fo:block  keep-together="always" text-align="right" font-family="Courier,monospace" white-space-collapse="false" font-size="12pt" >  Your faithfully &#160;&#160;&#160;&#160;&#160; </fo:block>
                               <fo:block  keep-together="always" text-align="right" font-family="Courier,monospace" white-space-collapse="false" font-size="12pt" >  for MOTHER DAIRY   &#160;&#160;</fo:block>
                               <fo:block  keep-together="always" text-align="left" font-family="Courier,monospace" white-space-collapse="false" font-size="12pt" >&#160;&#160;  </fo:block>
                               <fo:block  keep-together="always" text-align="left" font-family="Courier,monospace" white-space-collapse="false" font-size="12pt" >&#160;&#160;                                                                                                                                 </fo:block>
                               <fo:block  keep-together="always" text-align="left" font-family="Courier,monospace" white-space-collapse="false" font-size="12pt" >MANAGER(FIN)FOR INFORMATION <fo:inline text-align="left" font-family="Courier,monospace"  font-size="12pt" font-weight="bold">&#160;&#160;                                                                   MANAGER (PURCHASE)</fo:inline></fo:block>
                               <fo:block  keep-together="always" text-align="left" font-family="Courier,monospace" white-space-collapse="false" font-size="12pt" >MANAGER(DAIRY) FOR INFORMATION (PRP,PRODN,GHEE,PPL)                                                                                                        </fo:block>
                               <fo:block  keep-together="always" text-align="left" font-family="Courier,monospace" white-space-collapse="false" font-size="12pt" >STORE OFFICER FOR INFORMATION AND NEEDFUL ACTIONS,                                                                                                                               </fo:block>
                               <fo:block  keep-together="always" text-align="left" font-family="Courier,monospace" white-space-collapse="false" font-size="12pt" >OFFICE COPY.                                                                                                                                 </fo:block>
                               <fo:block  keep-together="always" text-align="left" font-family="Courier,monospace" white-space-collapse="false" font-size="12pt" >&#160;&#160;                                                                                                                                 </fo:block>
              
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

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
            margin-top="0in" margin-bottom=".7n" margin-left=".5in" margin-right=".5in">
        <fo:region-body margin-top=".1in"/>
        <fo:region-before extent="1.0in"/>
        <fo:region-after extent="1.5in"/>        
    </fo:simple-page-master>   
</fo:layout-master-set>
 <#if orderDetailsList?has_content> 
<fo:page-sequence master-reference="main" force-page-count="no-force" font-family="Courier,monospace">					
	<fo:static-content flow-name="xsl-region-before">
			<fo:block  keep-together="always" text-align="right" font-family="Courier,monospace" white-space-collapse="false"> &#160;Page - <fo:page-number/></fo:block>	  
            </fo:static-content>		
        <fo:flow flow-name="xsl-region-body"   font-family="Courier,monospace">	
			<#assign reportHeader = delegator.findOne("TenantConfiguration", {"propertyTypeEnumId" : "COMPANY_HEADER","propertyName" : "reportHeaderLable"}, true)>
 			<#assign reportSubHeader = delegator.findOne("TenantConfiguration", {"propertyTypeEnumId" : "COMPANY_HEADER","propertyName" : "reportSubHeaderLable"}, true)>
 			<fo:block  keep-together="always" text-align="center" font-family="Courier,monospace" white-space-collapse="false" font-size="12pt" font-weight="bold" >${reportHeader.description?if_exists}</fo:block>
 			<fo:block  keep-together="always" text-align="center" font-family="Courier,monospace" white-space-collapse="false" font-size="12pt" font-weight="bold" >${reportSubHeader.description?if_exists}</fo:block>
			<fo:block  keep-together="always" text-align="center" font-family="Courier,monospace" white-space-collapse="false" font-size="5pt" > ----------------------------------------------------------------------------------------------------------------------------------------------------------------------</fo:block>
            <fo:block>
			    <fo:table width="100%" align="right" table-layout="fixed"  font-size="12pt">
					<fo:table-column column-width="165pt"/>               
					<fo:table-column column-width="280pt"/>               
					<fo:table-column column-width="200pt"/>               
					    <fo:table-body>
                              <fo:table-row>
				                  <fo:table-cell  ><fo:block text-align="left" font-size="12pt"  >Tin No : 09152300064</fo:block></fo:table-cell> 
				                  <fo:table-cell  ><fo:block text-align="left"  font-size="12pt"  >&#160;</fo:block></fo:table-cell>
				                  <fo:table-cell  ><fo:block text-align="left"  font-size="12pt"  >Email: nhdcltdvns@yahoo.in </fo:block></fo:table-cell>       			
                               </fo:table-row>
			         </fo:table-body>
			    </fo:table>
			</fo:block>	
            <fo:block  keep-together="always" text-align="center" font-family="Courier,monospace" white-space-collapse="false" font-size="12pt" >--------------------------------------------------------------------------------------------------- </fo:block>
            <fo:block  keep-together="always" text-align="center" font-family="Courier,monospace" white-space-collapse="false" font-size="12pt" font-weight="bold">PURCHASE ORDER </fo:block>
	        <fo:block >
			   <fo:table width="100%" align="right" table-layout="fixed"  font-size="12pt">
			   <fo:table-column column-width="250pt"/>               
			   <fo:table-column column-width="160pt"/>               
			   <fo:table-column column-width="100pt"/>               
			   <fo:table-column column-width="100pt"/>               
				   <fo:table-body>
				       <fo:table-row>
				           <fo:table-cell  ><fo:block text-align="left" font-size="11pt"  >PO NO   &#160;  : ${allDetailsMap.get("orderId")?if_exists}</fo:block></fo:table-cell>       			
				           <fo:table-cell  ><fo:block text-align="left"  font-size="11pt"  >&#160;</fo:block></fo:table-cell>       		
				           <fo:table-cell  ><fo:block text-align="left"  font-size="11pt"  >PO DATED</fo:block></fo:table-cell>       		
				           <fo:table-cell  ><fo:block text-align="left"  font-size="11pt"  >:${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(allDetailsMap.get("orderDate")?if_exists, "dd-MMM-yy")}</fo:block></fo:table-cell>       		
                       </fo:table-row>
                       <fo:table-row>
				           <fo:table-cell  ><fo:block text-align="left" font-size="11pt"  ><#if allDetailsMap.get("refNo")?has_content> Reference NO   &#160;: ${allDetailsMap.get("refNo")?if_exists}</#if></fo:block></fo:table-cell>
   				           <fo:table-cell  ><fo:block text-align="left"  font-size="11pt"  >&#160;</fo:block></fo:table-cell>       		
				           <fo:table-cell  ><fo:block text-align="left" font-size="11pt"  keep-together="always"><#if allDetailsMap.get("quotationNo")?has_content>QUOTATION NO:${allDetailsMap.get("quotationNo")?if_exists}</#if></fo:block></fo:table-cell>       			
                       </fo:table-row>
			       </fo:table-body>
			  </fo:table>
			</fo:block>               		
			<fo:block >               		
			   <fo:table width="100%" align="right" table-layout="fixed"  font-size="11pt">
			   <fo:table-column column-width="800pt"/>               
				   <fo:table-body>
                      <fo:table-row>
						  <fo:table-cell><fo:block text-align="left" font-size="11pt">&#160;&#160;&#160;&#160; Form for purchase of Yarn from NTC/Co-operative/State Sector Agencies with terms of conditions</fo:block></fo:table-cell>
                      </fo:table-row>
			      </fo:table-body>
			  </fo:table>
		  </fo:block>	
      	 <fo:block font-weight="bold" keep-together="always" text-align="left" font-family="Courier,monospace" white-space-collapse="false" font-size="12pt" >&#160;</fo:block>
      	 <fo:block font-weight="bold" keep-together="always" text-align="left" font-family="Courier,monospace" white-space-collapse="false" font-size="12pt" >TO: </fo:block>
         <fo:block text-align="left" font-family="Courier,monospace" white-space-collapse="true" keep-together="always" font-size="12pt"  >${supppartyName}</fo:block>        
         <#if suppAdd?has_content>
        <fo:block text-align="left" font-family="Courier,monospace" white-space-collapse="true" keep-together="always" font-size="12pt"  >${suppAdd.address1?if_exists}</fo:block>
        <fo:block text-align="left" font-family="Courier,monospace" white-space-collapse="false" keep-together="always" font-size="12pt" >${suppAdd.address2?if_exists} </fo:block> 
	  	<fo:block text-align="left" font-family="Courier,monospace" white-space-collapse="false" keep-together="always" font-size="12pt" >${suppAdd.city?if_exists} </fo:block> 
	    <fo:block text-align="left" font-family="Courier,monospace" white-space-collapse="false" keep-together="always" font-size="12pt" >${suppAdd.postalCode?if_exists}</fo:block> 
		</#if>
              <#--	<fo:block font-weight="bold" keep-together="always" text-align="left" font-family="Courier,monospace" white-space-collapse="false" font-size="12pt" >TO: </fo:block> -->
		 <fo:block  keep-together="always" text-align="left" font-family="Courier,monospace" white-space-collapse="false" font-size="12pt" font-weight="bold" >&#160;</fo:block>
		 <fo:block  keep-together="always" text-align="left" font-family="Courier,monospace" white-space-collapse="false" font-size="12pt" >Reference : Your quotation no : against your purchase enquiry no</fo:block>
      <#--<fo:block font-weight="bold" keep-together="always" text-align="left" font-family="Courier,monospace" white-space-collapse="false" font-size="12pt" ><#if allDetailsMap.get("supplierName")?has_content>&#160;&#160;${allDetailsMap.get("supplierName")}  <#else> </#if>        </fo:block>
         <fo:block font-weight="bold" keep-together="always" text-align="left" font-family="Courier,monospace" white-space-collapse="false" font-size="12pt" ><#if allDetailsMap.get("address1")?has_content>&#160;&#160;&#160;     ${allDetailsMap.get("address1")}   <#else> </#if>     </fo:block>
         <fo:block font-weight="bold" keep-together="always" text-align="left" font-family="Courier,monospace" white-space-collapse="false" font-size="12pt" ><#if allDetailsMap.get("address2")?has_content>&#160;&#160;&#160;     ${allDetailsMap.get("address2")?if_exists} <#else> </#if>     </fo:block>
         <fo:block font-weight="bold" keep-together="always" text-align="left" font-family="Courier,monospace" white-space-collapse="false" font-size="12pt" ><#if allDetailsMap.get("city")?has_content>&#160;&#160;&#160;     ${allDetailsMap.get("city")?if_exists}-${allDetailsMap.get("postalCode")?if_exists}. <#else> </#if>                          </fo:block> -->
      	 <fo:block font-weight="bold" keep-together="always" text-align="left" font-family="Courier,monospace" white-space-collapse="false" font-size="12pt" >&#160;</fo:block>
      	 <fo:block>
      		<fo:table width="100%" align="right" table-layout="fixed"  font-size="11pt">
				<fo:table-column column-width="650pt"/>               
		           <fo:table-body>
                      <fo:table-row>
						<fo:table-cell><fo:block text-align="left" font-size="11pt">Dear Sir,</fo:block></fo:table-cell>
                      </fo:table-row>
                      <fo:table-row>
						<fo:table-cell><fo:block text-align="left" font-size="11pt">Please supply the following items as per the terms and conditions overleaf. All goods should be consigned to self and booked to NATIONAL HANDLOOM DEVELOPMENT CORPORATION LTD. freight TO PAY basis unless otherwise specified</fo:block></fo:table-cell>
                      </fo:table-row>
	               </fo:table-body>
	           </fo:table>
	     </fo:block>    
         <fo:block font-weight="bold" keep-together="always" text-align="left" font-family="Courier,monospace" white-space-collapse="false" font-size="12pt" ><#if allDetailsMap.get("phoneNumber")?has_content>PHONE NO:${allDetailsMap.get("phoneNumber")?if_exists}</#if>         </fo:block>
         <fo:block font-weight="bold" keep-together="always" text-align="left" font-family="Courier,monospace" white-space-collapse="false" font-size="12pt" ><#if fromPartyTinNo?has_content>TIN NO  :${fromPartyTinNo?if_exists}</#if>         </fo:block>
		 <fo:block  keep-together="always" text-align="left" font-family="Courier,monospace" white-space-collapse="false" font-size="12pt" ><#if allDetailsMap.get("refNo")?has_content> REFERENCE NO :${allDetailsMap.get("refNo")?if_exists}</#if></fo:block>
         <fo:block  keep-together="always" text-align="center" font-family="Courier,monospace" white-space-collapse="false" font-size="12pt" >&#160;&#160; </fo:block>
         <fo:block  keep-together="always" text-align="left" font-family="Courier,monospace" white-space-collapse="false" font-size="12pt" ><#if allDetailsMap.get("enquiryId")?has_content> DAIRY ENQUIRY NO :${allDetailsMap.get("enquiryId")?if_exists}</#if>                                             <#if allDetailsMap.get("enquiryDate")?has_content>DATE:${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(allDetailsMap.get("enquiryDate")?if_exists, "dd-MMM-yy")}  </#if>  </fo:block>
         <fo:block  keep-together="always" text-align="left" font-family="Courier,monospace" white-space-collapse="false" font-size="12pt" ><#if allDetailsMap.get("quoteId")?has_content>QUOTATION NO     :${allDetailsMap.get("quoteId")?if_exists}</#if>                                            <#if allDetailsMap.get("qutationDate")?has_content> DATE:${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(allDetailsMap.get("qutationDate")?if_exists, "dd-MMM-yy")} </#if>   <#if allDetailsMap.get("qutationDateAttr")?has_content> DATE:${allDetailsMap.get("qutationDateAttr")?if_exists}</#if> </fo:block>
         <fo:block  keep-together="always" text-align="left" font-family="Courier,monospace" white-space-collapse="false" font-size="12pt" ><#if allDetailsMap.get("quoteRef")?has_content>QUOTE REF NO     :${allDetailsMap.get("quoteRef")?if_exists}</#if>       </fo:block>
         <fo:block  keep-together="always" text-align="center" font-family="Courier,monospace" white-space-collapse="false" font-size="12pt" >&#160;&#160;--------------------------------------------------------------------------------------------</fo:block>
      	 <fo:block  keep-together="always" text-align="left" font-family="Courier,monospace" white-space-collapse="false" font-size="12pt" font-weight="bold">&#160;&#160;SNO        ITEM           REMARKS       QUANTITY       BASIC RATE     AMOUNT</fo:block>
      	 <fo:block  keep-together="always" text-align="center" font-family="Courier,monospace" white-space-collapse="false" font-size="12pt" >&#160;&#160;--------------------------------------------------------------------------------------------</fo:block>
    	 <fo:block>
            <fo:table text-align="center" >
            <fo:table-column column-width="50pt"/>
            <fo:table-column column-width="140pt"/>
       	    <fo:table-column column-width="100pt"/>
       	    <fo:table-column column-width="90pt"/>
            <fo:table-column column-width="120pt"/>
            <fo:table-column column-width="110pt"/>
               <fo:table-body text-align="center">
                  <#assign sNo=1>
	              <#list orderDetailsList as orderListItem>
				  <#assign productId= orderListItem("productId")?if_exists >
		          <#assign productNameDetails = delegator.findOne("Product", {"productId" : productId}, true)>
		           <#if productNameDetails?has_content> 
                  	 <fo:table-row >
                	   <fo:table-cell ><fo:block text-align="center"  font-size="12pt" >${sNo} </fo:block></fo:table-cell>     
  				  	   <fo:table-cell ><fo:block text-align="left" font-size="12pt"> ${productNameDetails.get("internalName")?if_exists}</fo:block></fo:table-cell>     
  				       <fo:table-cell  ><fo:block text-align="center"  font-size="12pt">${orderListItem.get("remarks")?if_exists}</fo:block></fo:table-cell>     
  				       <fo:table-cell  ><fo:block text-align="center"  font-size="12pt">${orderListItem.get("quantity")?if_exists}</fo:block></fo:table-cell>     
  			           <fo:table-cell  ><fo:block text-align="left"   font-size="12pt" >${orderListItem.get("unitPrice")?if_exists?string("##0.00")}</fo:block></fo:table-cell>     
  				       <fo:table-cell  ><fo:block text-align="right"  font-size="12pt" >${orderListItem.get("amount")?if_exists?string("##0.00")}</fo:block></fo:table-cell>     
  				     </fo:table-row>
  				    	<#assign sNo=sNo+1>
  				    	</#if>
  				     </#list>
                </fo:table-body>
           </fo:table>
       </fo:block>
       <fo:block  keep-together="always" text-align="center" font-family="Courier,monospace" white-space-collapse="false" font-size="12pt" >&#160;&#160;-------------------------------------------------------------------------------------------</fo:block>
       <#if allDetailsMap.get("total")?has_content> 
       <fo:block  keep-together="always" text-align="left" font-family="Courier,monospace" white-space-collapse="false" font-size="12pt" font-weight="bold"> &#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160; SUB TOTAL  : &#160;${allDetailsMap.get("total")?if_exists?string("##0.00")}  </fo:block> </#if>
	   <#if parentMap?has_content>
	      <#assign parent=parentMap.entrySet()>
	      <#list parent as parentList>
		  <#assign termType=parentList.getKey()>
		  <#assign termValues=parentList.getValue()>
		  <#assign size=termValues.size()>
	       <fo:block >
	           <fo:table width="100%" align="right" table-layout="fixed"  font-size="12pt">
			      <fo:table-column column-width="250"/>               
                  <fo:table-column column-width="80"/>
				  <fo:table-column column-width="150"/> 
				  <fo:table-column column-width="200"/> 							              
				  <fo:table-body>
					 <#if termType == "TAX">
							<fo:table-row>
		                     <fo:table-cell  ><fo:block text-align="left" font-size="12pt"  ><fo:inline  text-decoration="underline"  font-weight="bold">TAX&#160;&#160;:</fo:inline></fo:block></fo:table-cell>       			
                          </fo:table-row>
                          <#if Amount gt 0>
                          <fo:table-row>
		                  	 <fo:table-cell  ><fo:block text-align="left" font-size="12pt"  >Basic Excise Duty On Purchase</fo:block></fo:table-cell>
							 <fo:table-cell  ><fo:block text-align="left" font-size="12pt"  >: <#list bedPercents as bed>${bed}%,</#list> </fo:block></fo:table-cell>
							<fo:table-cell  ><fo:block text-align="left" font-size="12pt"  >-- ${Amount?string("##0.00")} INR </fo:block></fo:table-cell>
                          </fo:table-row>
                          </#if>
							<#if vatAmount gt 0>
                            <fo:table-row>
		                  	 <fo:table-cell  ><fo:block text-align="left" font-size="12pt"  >Value Added Tax On Purchase</fo:block></fo:table-cell>
							 <fo:table-cell  ><fo:block text-align="left" font-size="12pt"  >:  <#list vatpercents as vat>${vat}%,</#list></fo:block></fo:table-cell>
							<fo:table-cell  ><fo:block text-align="left" font-size="12pt"  >-- ${vatAmount?string("##0.00")} INR </fo:block></fo:table-cell>
                          </fo:table-row>
							</#if>
                          <#if cstAmount gt 0>
                            <fo:table-row>
		                  	 <fo:table-cell  ><fo:block text-align="left" font-size="12pt"  >Central Sales Tax On Purchase</fo:block></fo:table-cell>
							 <fo:table-cell  ><fo:block text-align="left" font-size="12pt"  >:  <#list cstpercents as cst>${cst}%</#list></fo:block></fo:table-cell>
							<fo:table-cell  ><fo:block text-align="left" font-size="12pt"  >-- ${cstAmount?string("##0.00")} INR </fo:block></fo:table-cell>
                          </fo:table-row>
							</#if>
						</#if>
						<#if termType == "OTHERS">
							<fo:table-row>
		                     <fo:table-cell  ><fo:block text-align="left" font-size="12pt"><fo:inline  text-decoration="underline" font-weight="bold" >OTHER CHARGES&#160;&#160;:</fo:inline></fo:block></fo:table-cell>       			
                          </fo:table-row>
                         <#list termValues as termtypeId>						
		                     <fo:table-row>
		                  	 <fo:table-cell  ><fo:block text-align="left" font-size="12pt"  >${termtypeId.get("termTypeDes")?if_exists}</fo:block></fo:table-cell>
		                  	 <#if termtypeId.get("uomId")=="PERCENT">
							 <fo:table-cell  ><fo:block text-align="left" font-size="12pt"  >: ${termtypeId.get("termValue")?if_exists}%  </fo:block></fo:table-cell>
							 </#if>
							<#if termtypeId.get("uomId")=="INR" >
							 <fo:table-cell  ><fo:block text-align="left" font-size="12pt"  >: ${termtypeId.get("termValue")?if_exists}  </fo:block></fo:table-cell>
							 </#if>
							<fo:table-cell  ><fo:block text-align="left" font-size="12pt"  >-- ${termtypeId.get("amount")?if_exists} INR </fo:block></fo:table-cell>
							<#if termtypeId.get("description")?has_content>
							 <fo:table-cell  ><fo:block text-align="left" font-size="12pt"  >: ${termtypeId.get("description")?if_exists}  </fo:block></fo:table-cell>
							 </#if>
                          </fo:table-row>
						</#list>
						</#if>
                </fo:table-body>
	          </fo:table>
	      </fo:block>
	      </#list>	     
	   
	   <#if allDetailsMap.get("noteInfo")?has_content> 
       <fo:block text-align="left" font-family="Courier,monospace" white-space-collapse="false" font-size="12pt" ><fo:inline  text-decoration="underline" font-weight="bold">NOTE</fo:inline>:${allDetailsMap.get("noteInfo")?if_exists} </fo:block>  </#if>       
	   <fo:block  keep-together="always" text-align="center"  font-family="Courier,monospace" white-space-collapse="false" font-size="12pt" >&#160;&#160; </fo:block>
	   <fo:block  keep-together="always" text-align="center"  font-family="Courier,monospace" white-space-collapse="false" font-size="12pt" >&#160;&#160; </fo:block>
 	   <#assign amountWords = Static["org.ofbiz.base.util.UtilNumber"].formatRuleBasedAmount(roundedGrandTotal, "%indRupees-and-paiseRupees", locale)>										  	
       <fo:block white-space-collapse="false" >Amount(In Words): Rupees ${amountWords}only</fo:block>			
	   <#list parent as parentList>
				<#assign termType=parentList.getKey()>
				<#assign termValues=parentList.getValue()>
						<fo:block >
	        			 <fo:table width="100%" align="right" table-layout="fixed"  font-size="12pt">
			               <fo:table-column column-width="250"/>               
                           <fo:table-column column-width="150"/>
							<fo:table-column column-width="100"/>               
				            <fo:table-body>
						<#if termType == "DELIVERY_TERM">
							<fo:table-row>
		                     <fo:table-cell  ><fo:block text-align="left" font-size="12pt"   ><fo:inline  text-decoration="underline" font-weight="bold">DELIVERY TERMS&#160;&#160;:</fo:inline></fo:block></fo:table-cell>       			
                          </fo:table-row>
						<#list termValues as termtypeId>
		                     <fo:table-row>
		                  	 <fo:table-cell  ><fo:block text-align="left" font-size="12pt"  >${termtypeId.get("termTypeDes")?if_exists}</fo:block></fo:table-cell>
		                  	 <#if termtypeId.get("description")?has_content>
							 <fo:table-cell  ><fo:block text-align="left" font-size="12pt"  >:${termtypeId.get("description")?if_exists} </fo:block></fo:table-cell>
							 </#if>
                          </fo:table-row>
						</#list>
                         </#if> 
	   					<#if termType == "FEE_PAYMENT_TERM">
							<fo:table-row>
		                     <fo:table-cell  ><fo:block text-align="left" font-size="12pt" ><fo:inline text-decoration="underline" font-weight="bold" >PAYMENT TERMS&#160;&#160;:</fo:inline></fo:block></fo:table-cell>       			
                          </fo:table-row>
						<#list termValues as termtypeId>
		                     <fo:table-row>
		                  	 <fo:table-cell  ><fo:block text-align="left" font-size="12pt"  >${termtypeId.get("termTypeDes")?if_exists}</fo:block></fo:table-cell>
							<#if termtypeId.get("description")?has_content>
							 <fo:table-cell  ><fo:block text-align="left" font-size="12pt"  >:${termtypeId.get("description")?if_exists} </fo:block></fo:table-cell>
							 </#if>
                          </fo:table-row>
						</#list>
                          </#if> 
	                	</fo:table-body>
	                		</fo:table>
	        	  </fo:block>	
	        </#list>
	</#if>
	<#if allDetailsMap.get("noteInfo")?has_content> 
    <fo:block text-align="left" font-family="Courier,monospace" white-space-collapse="false" font-size="12pt" ><fo:inline  text-decoration="underline" font-weight="bold">NOTE</fo:inline>:${allDetailsMap.get("noteInfo")?if_exists} </fo:block>  
    </#if>
    <fo:block  keep-together="always" text-align="center"  font-family="Courier,monospace" white-space-collapse="false" font-size="12pt" >&#160;&#160; </fo:block>
	<fo:block  keep-together="always" text-align="center"  font-family="Courier,monospace" white-space-collapse="false" font-size="12pt" >&#160;&#160; </fo:block>
 	<fo:block>
      	<fo:table width="100%" align="right" table-layout="fixed"  font-size="11pt" border-style="solid">
      	<fo:table-column column-width="30pt"/>
        <fo:table-column column-width="600pt"/>               
			<fo:table-body>	
			    <fo:table-row>
					<fo:table-cell><fo:block text-align="left" font-size="11pt">2</fo:block></fo:table-cell>
					<fo:table-cell><fo:block text-align="left" font-size="11pt">DESPATCH INSTRUCTIONS:</fo:block></fo:table-cell>
                </fo:table-row>		                 
	            <fo:table-row border-style="solid">
                    <fo:table-cell border-style="solid">
                        <fo:block text-align="left" font-size="11pt" font-weight="bold" >Sno</fo:block>
                        <fo:block text-align="left" font-size="11pt">&#160;&#160;</fo:block>
                        <fo:block text-align="left" font-size="11pt">&#160;&#160;</fo:block>
                    </fo:table-cell>
                    <fo:table-cell border-style="solid">
                        <fo:block>
					    <fo:block text-align="left" font-size="11pt" ><fo:inline  text-decoration="underline"  >Agency Address:</fo:inline></fo:block>
					       <fo:table width="80%" align="right" table-layout="fixed"  font-size="11pt" >
      					   <fo:table-column column-width="70pt"/>
      					   <fo:table-column column-width="530pt"/>	             
				           	  <fo:table-body>
							  <fo:table-row>
							  <#if shipingAdd?has_content>
								  <fo:table-cell>
	                                <fo:block text-align="left" font-family="Courier,monospace" white-space-collapse="true" keep-together="always" font-size="12pt"  >${shipingAdd.name?if_exists}</fo:block>
	                                <fo:block text-align="left" font-family="Courier,monospace" white-space-collapse="true" keep-together="always" font-size="12pt"  >${shipingAdd.address1?if_exists}</fo:block>
	                                <fo:block text-align="left" font-family="Courier,monospace" white-space-collapse="false" keep-together="always" font-size="12pt" >${shipingAdd.address2?if_exists} </fo:block> 
								  	<fo:block text-align="left" font-family="Courier,monospace" white-space-collapse="false" keep-together="always" font-size="12pt" >${shipingAdd.city?if_exists} </fo:block> 
								    <fo:block text-align="left" font-family="Courier,monospace" white-space-collapse="false" keep-together="always" font-size="12pt" >${shipingAdd.postalCode?if_exists}</fo:block> 
								  </fo:table-cell>
								  <#else>
								  <fo:table-cell>
								    <fo:block text-align="left" font-family="Courier,monospace" white-space-collapse="false" keep-together="always" font-size="12pt" ></fo:block> 
								  </fo:table-cell>
							 </#if>
							  </fo:table-row>
						</fo:table-body>
					      </fo:table>
					   </fo:block>
				   </fo:table-cell>
               </fo:table-row>
	      </fo:table-body>
	    </fo:table>
	 </fo:block>
	 <fo:block>
      	<fo:table width="100%" align="right" table-layout="fixed"  font-size="11pt" border-style="solid">
      	<fo:table-column column-width="30pt"/>
      	<fo:table-column column-width="600pt"/>               
			<fo:table-body>
               <fo:table-row>
				  <fo:table-cell><fo:block text-align="left" font-size="11pt">3</fo:block></fo:table-cell>
				  <fo:table-cell><fo:block text-align="left" font-size="11pt">MODE OF TRANSPORT : Despatch Goods Through Registered Common Carriers Only</fo:block></fo:table-cell>
               </fo:table-row>
	        </fo:table-body>
	   </fo:table>
	 </fo:block>
	 <fo:block>
        <fo:table width="100%" align="right" table-layout="fixed"  font-size="11pt" border-style="solid">
			<fo:table-column column-width="30pt"/>
			<fo:table-column column-width="600pt"/>               
				<fo:table-body>
                    <fo:table-row>
						<fo:table-cell><fo:block text-align="left" font-size="11pt">4</fo:block></fo:table-cell>
						<fo:table-cell><fo:block text-align="left" font-size="11pt">PACKING INSTRUCTIONS : As Per Standard</fo:block></fo:table-cell>
                     </fo:table-row>
	             </fo:table-body>
	     </fo:table>
	 </fo:block>	            
	 <fo:block>
      	<fo:table width="100%" align="right" table-layout="fixed"  font-size="11pt" border-style="solid">
			<fo:table-column column-width="30pt"/>
			<fo:table-column column-width="600pt"/>               
			   <fo:table-body>
                  <fo:table-row>
					  <fo:table-cell><fo:block text-align="left" font-size="11pt">5</fo:block></fo:table-cell>
					   <fo:table-cell><fo:block text-align="left" font-size="11pt">OTHER INSTRUCTIONS :</fo:block></fo:table-cell>
                   </fo:table-row>
	           </fo:table-body>
	     </fo:table>
	 </fo:block>	            
	<fo:block>
		<fo:table width="100%" align="right" table-layout="fixed"  font-size="11pt" border-style="solid">
			<fo:table-column column-width="30pt"/>
			<fo:table-column column-width="600pt"/>               
	       	<fo:table-body>
              <fo:table-row>
				<fo:table-cell><fo:block text-align="left" font-size="11pt">6</fo:block></fo:table-cell>
				<fo:table-cell><fo:block text-align="left" font-size="11pt">VALIDITY : This PO is valid for 15 days.</fo:block></fo:table-cell>
              </fo:table-row>
        	</fo:table-body>
        </fo:table>
	 </fo:block>	
        <fo:block  keep-together="always" text-align="center" font-family="Courier,monospace" white-space-collapse="false" font-size="12pt" > &#160;&#160;
        <fo:block  keep-together="always" text-align="center" font-family="Courier,monospace" white-space-collapse="false" font-size="12pt" >&#160;&#160; </fo:block>
        <fo:block  keep-together="always" text-align="left" font-family="Courier,monospace" white-space-collapse="false" font-size="12pt" ><#if orderDesctioption?has_content> DESCRIPTION :${orderDesctioption?if_exists}</#if></fo:block>
        <fo:block  keep-together="always" text-align="center" font-family="Courier,monospace" white-space-collapse="false" font-size="12pt" >&#160;&#160; </fo:block>
        <fo:block  keep-together="always" text-align="left" font-family="Courier,monospace" white-space-collapse="false" font-size="12pt" font-weight="bold" >NOTE: Supplier must refer the above Purchase Order No.${allDetailsMap.get("orderId")?if_exists} in their invoice.  </fo:block>
        <fo:block  keep-together="always" text-align="left" font-family="Courier,monospace" white-space-collapse="false" font-size="12pt" font-weight="bold" >Payment to mills will be released only after receipt of payment from user agency  </fo:block>     
        <fo:block  keep-together="always" text-align="left" font-family="Courier,monospace" white-space-collapse="false" font-size="12pt" >&#160;&#160;Sub Standard Goods will be returned at your cost and risk  </fo:block>
        <fo:block  keep-together="always" text-align="center" font-family="Courier,monospace" white-space-collapse="false" font-size="12pt" > &#160;&#160;</fo:block>
        <fo:block  keep-together="always" text-align="left" font-family="Courier,monospace" white-space-collapse="false" font-size="12pt" >Jr. Asstt/ Supdt(y) / Asstt. Manager (y) </fo:block>
        <fo:block  keep-together="always" text-align="left" font-family="Courier,monospace" white-space-collapse="false" font-size="12pt" >Checked by: </fo:block>
        <fo:block  keep-together="always" text-align="center" font-family="Courier,monospace" white-space-collapse="false" font-size="12pt" >&#160;&#160; </fo:block>
        <fo:block  keep-together="always" text-align="left" font-family="Courier,monospace" white-space-collapse="false" font-size="12pt" >Copy to: </fo:block>
        <fo:block  keep-together="always" text-align="left" font-family="Courier,monospace" white-space-collapse="false" font-size="12pt" >1. Indentor </fo:block>
        <fo:block  keep-together="always" text-align="left" font-family="Courier,monospace" white-space-collapse="false" font-size="12pt" >2. Finance and Account Department </fo:block>
        <fo:block  keep-together="always" text-align="left" font-family="Courier,monospace" white-space-collapse="false" font-size="12pt" >3. Dealing Section in Purchase  </fo:block>
        <fo:block  keep-together="always" text-align="left" font-family="Courier,monospace" white-space-collapse="false" font-size="12pt" >4. Purchase Master File </fo:block>
        <#if signature?has_content> 
          <fo:block  keep-together="always" text-align="right" font-family="Courier,monospace" white-space-collapse="false" font-size="12pt" >  &#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;       ${signature} &#160;&#160; </fo:block>
        <#else>
        <fo:block  keep-together="always" text-align="right" font-family="Courier,monospace" white-space-collapse="false" font-size="12pt" font-weight="bold"  >&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;  Authorised Singnatory &#160; </fo:block>
        <fo:block  keep-together="always" text-align="right" font-family="Courier,monospace" white-space-collapse="false" font-size="12pt" font-weight="bold"  >&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160; N H D C LTD. &#160; </fo:block>
         </#if>  
        </fo:block>
		<fo:block  keep-together="always" text-align="center" font-family="Courier,monospace" white-space-collapse="false" font-size="12pt" >&#160;&#160;----------------------------------------------------------------------------------------- </fo:block>
        <fo:block  keep-together="always" text-align="center" font-family="Courier,monospace" white-space-collapse="false" font-size="12pt" > &#160;&#160;
        <fo:block  keep-together="always" text-align="center" font-family="Courier,monospace" white-space-collapse="false" font-size="12pt" >&#160;&#160; </fo:block>
        <fo:block  keep-together="always" text-align="center" font-family="Courier,monospace" white-space-collapse="false" font-size="12pt" >&#160;&#160; </fo:block>
        <fo:block  keep-together="always" text-align="center" font-family="Courier,monospace" white-space-collapse="false" font-size="12pt" >&#160;&#160; </fo:block> 
		<fo:block  keep-together="always" text-align="center" font-family="Courier,monospace" white-space-collapse="false" font-size="12pt" >&#160;&#160; </fo:block> 
        <fo:block  keep-together="always" text-align="center" font-family="Courier,monospace" white-space-collapse="false" font-size="12pt" >&#160;&#160; </fo:block> 
        </fo:block>	        
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

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
${setRequestAttribute("OUTPUT_FILENAME", "LoanAvailedReport.pdf")}
 <#if orderDetailsList?has_content> 

<fo:page-sequence master-reference="main" force-page-count="no-force" font-family="Courier,monospace">					
		<fo:static-content flow-name="xsl-region-before">
			              		<fo:block  keep-together="always" text-align="right" font-family="Courier,monospace" white-space-collapse="false"> &#160;Page - <fo:page-number/></fo:block>
	  
            </fo:static-content>		
           <fo:flow flow-name="xsl-region-body"   font-family="Courier,monospace">	
			        	<#--   <fo:block white-space-collapse="false" font-size="10pt"  font-family="Helvetica" keep-together="always" >&#160;  STORE CODE:${parameters.stockId}&#160;    &#160;     &#160;  DESCRIPTION:${stockDetails.get("description")?if_exists}</fo:block> -->
				<fo:block  keep-together="always" text-align="center" font-family="Courier,monospace" white-space-collapse="false" font-size="12pt" font-weight="bold" >  KARNATAKA CO-OPERATIVE MILK PRODUCERS FEDERATION LTD. </fo:block>
				<fo:block  keep-together="always" text-align="center" font-family="Courier,monospace" white-space-collapse="false" font-size="12pt" font-weight="bold" >  UNIT : MOTHER DAIRY:G.K.V.K POST : YELAHANKA:BANGALORE : 560065  </fo:block>
			    <fo:block  keep-together="always" text-align="center" font-family="Courier,monospace" white-space-collapse="false" font-size="5pt" > ----------------------------------------------------------------------------------------------------------------------------------------------------------------------</fo:block>
                 <fo:block >
			        			 <fo:table width="100%" align="right" table-layout="fixed"  font-size="12pt">
					               <fo:table-column column-width="165pt"/>               
					                <fo:table-column column-width="280pt"/>               
						           <fo:table-column column-width="200pt"/>               
						           	<fo:table-body>
				                     <fo:table-row>
				                     <fo:table-cell  ><fo:block text-align="left" font-size="12pt"  >Phone No :${allDetailsMap.get("companyPhone")?if_exists}</fo:block></fo:table-cell>       			
				                     <fo:table-cell  ><fo:block text-align="left"  font-size="12pt"  >FAX&#160; : ${allDetailsMap.get("companyFax")?if_exists}</fo:block></fo:table-cell>       		
				                     <fo:table-cell  ><fo:block text-align="left"  font-size="12pt"  >TIN NO:${allDetailsMap.get("tinNumber")?if_exists}</fo:block></fo:table-cell>       		
                                  </fo:table-row>
                                  <fo:table-row>
				                     <fo:table-cell  ><fo:block text-align="left" font-size="12pt"  >&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;${allDetailsMap.get("partySecondPhone")?if_exists}</fo:block></fo:table-cell>       			
				                     <fo:table-cell  ><fo:block text-align="left"  font-size="12pt"  >Email:${allDetailsMap.get("companyMail")?if_exists}</fo:block></fo:table-cell>       		
				                     <fo:table-cell  ><fo:block text-align="left"  font-size="12pt"  >KST NO:${allDetailsMap.get("kstNumber")?if_exists}</fo:block></fo:table-cell>       		
                                  </fo:table-row>
                                  <fo:table-row>
									<fo:table-cell><fo:block text-align="left" font-size="12pt"></fo:block></fo:table-cell>
				                     <fo:table-cell  ><fo:block text-align="left" font-size="12pt"  >&#160;&#160;&#160;&#160;${allDetailsMap.get("compSecondMail")?if_exists}</fo:block> </fo:table-cell>       		
				                     <fo:table-cell  ><fo:block text-align="left"  font-size="12pt"  >CST NO:${allDetailsMap.get("cstNumber")?if_exists}</fo:block></fo:table-cell>       		
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
				                     <fo:table-cell  ><fo:block text-align="left" font-size="12pt"  >PO NO   &#160;:</fo:block></fo:table-cell>       			
				                     <fo:table-cell  ><fo:block text-align="left"  font-size="12pt"  >&#160;</fo:block></fo:table-cell>       		
				                     <fo:table-cell  ><fo:block text-align="left"  font-size="12pt"  >PO DATED</fo:block></fo:table-cell>       		
				                     <fo:table-cell  ><fo:block text-align="left"  font-size="12pt"  > :${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(allDetailsMap.get("orderDate")?if_exists, "dd-MMM-yy")}</fo:block></fo:table-cell>       		
                                  </fo:table-row>
                                  <fo:table-row>
				                     <fo:table-cell  ><fo:block text-align="left" font-size="12pt"  >File NO:${allDetailsMap.get("fileNo")?if_exists}</fo:block></fo:table-cell>       			
				                     <fo:table-cell  ><fo:block text-align="left"  font-size="12pt"  >&#160;</fo:block></fo:table-cell>       		
				                     <fo:table-cell  ><fo:block text-align="left"  font-size="12pt"  >Internal Id</fo:block></fo:table-cell>       		
				                     <fo:table-cell  ><fo:block text-align="left"  font-size="12pt"  >:${allDetailsMap.get("orderId")?if_exists}</fo:block></fo:table-cell>       		
                                  </fo:table-row>
                                  <fo:table-row>
									<fo:table-cell><fo:block text-align="left" font-size="12pt"></fo:block></fo:table-cell>
				                     <fo:table-cell  ><fo:block text-align="left" font-size="12pt"  ></fo:block> </fo:table-cell>       		
				                     <fo:table-cell  ><fo:block text-align="left"  font-size="12pt"  >FAX</fo:block></fo:table-cell>       		
				                     <fo:table-cell  ><fo:block text-align="left"  font-size="12pt"  >:${allDetailsMap.get("faxNumber")?if_exists}</fo:block></fo:table-cell>       		
                                  </fo:table-row>
			                	</fo:table-body>
			                		</fo:table>
			        	  </fo:block>	
	            
              	
              	<fo:block font-weight="bold" keep-together="always" text-align="left" font-family="Courier,monospace" white-space-collapse="false" font-size="12pt" >ADDRESS:<#if allDetailsMap.get("partyId")?has_content>${allDetailsMap.get("partyId")}, <#else> </#if>      </fo:block>
              	<fo:block font-weight="bold" keep-together="always" text-align="left" font-family="Courier,monospace" white-space-collapse="false" font-size="12pt" ><#if allDetailsMap.get("partyName")?has_content>&#160;&#160;&#160;     ${allDetailsMap.get("partyName")}  <#else> </#if>        </fo:block>
              	<fo:block font-weight="bold" keep-together="always" text-align="left" font-family="Courier,monospace" white-space-collapse="false" font-size="12pt" ><#if allDetailsMap.get("address1")?has_content>&#160;&#160;&#160;     ${allDetailsMap.get("address1")}   <#else> </#if>     </fo:block>
                <fo:block font-weight="bold" keep-together="always" text-align="left" font-family="Courier,monospace" white-space-collapse="false" font-size="12pt" ><#if allDetailsMap.get("address2")?has_content>&#160;&#160;&#160;     ${allDetailsMap.get("address2")?if_exists} <#else> </#if>     </fo:block>
                <fo:block font-weight="bold" keep-together="always" text-align="left" font-family="Courier,monospace" white-space-collapse="false" font-size="12pt" ><#if allDetailsMap.get("city")?has_content>&#160;&#160;&#160;     ${allDetailsMap.get("city")?if_exists}-${allDetailsMap.get("postalCode")?if_exists}. <#else> </#if>                          </fo:block>
                
              	 <fo:block font-weight="bold" keep-together="always" text-align="left" font-family="Courier,monospace" white-space-collapse="false" font-size="12pt" ><#if allDetailsMap.get("phoneNumber")?has_content>PHONE NO:${allDetailsMap.get("phoneNumber")?if_exists}</#if>         </fo:block>
              	 <fo:block font-weight="bold" keep-together="always" text-align="left" font-family="Courier,monospace" white-space-collapse="false" font-size="12pt" ><#if fromPartyTinNo?has_content>TIN NO  :${fromPartyTinNo?if_exists}</#if>         </fo:block>
				<fo:block  keep-together="always" text-align="left" font-family="Courier,monospace" white-space-collapse="false" font-size="12pt" ><#if allDetailsMap.get("refNo")?has_content> REFERENCE NO :${allDetailsMap.get("refNo")?if_exists}</#if></fo:block>
               <fo:block  keep-together="always" text-align="center" font-family="Courier,monospace" white-space-collapse="false" font-size="12pt" >&#160;&#160; </fo:block>
             	<fo:block  keep-together="always" text-align="left" font-family="Courier,monospace" white-space-collapse="false" font-size="12pt" ><#if allDetailsMap.get("enquiryId")?has_content> DAIRY ENQUIRY NO :${allDetailsMap.get("enquiryId")?if_exists}</#if>                                             <#if allDetailsMap.get("enquiryDate")?has_content>DATE:${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(allDetailsMap.get("enquiryDate")?if_exists, "dd-MMM-yy")}  </#if>  </fo:block>
              	<fo:block  keep-together="always" text-align="left" font-family="Courier,monospace" white-space-collapse="false" font-size="12pt" ><#if allDetailsMap.get("quoteId")?has_content>QUOTATION NO     :${allDetailsMap.get("quoteId")?if_exists}</#if>                                            <#if allDetailsMap.get("qutationDate")?has_content> DATE:${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(allDetailsMap.get("qutationDate")?if_exists, "dd-MMM-yy")}          </#if> </fo:block>
              	              	<fo:block  keep-together="always" text-align="left" font-family="Courier,monospace" white-space-collapse="false" font-size="12pt" ><#if allDetailsMap.get("quoteRef")?has_content>QUOTE REF NO     :${allDetailsMap.get("quoteRef")?if_exists}</#if>       </fo:block>
              	
                <fo:block  keep-together="always" text-align="center" font-family="Courier,monospace" white-space-collapse="false" font-size="12pt" >&#160;&#160;-------------------------------------------------------------------------------------------</fo:block>
              	<fo:block  keep-together="always" text-align="left" font-family="Courier,monospace" white-space-collapse="false" font-size="12pt" font-weight="bold">&#160;&#160;SNO  ITEM CODE    PRODUCT NAME           UNIT      QUANTITY       UNIT RATE      AMOUNT</fo:block>
              	<fo:block  keep-together="always" text-align="center" font-family="Courier,monospace" white-space-collapse="false" font-size="12pt" >&#160;&#160;-------------------------------------------------------------------------------------------</fo:block>
            	<fo:block>
                 <fo:table text-align="center" >
                    <fo:table-column column-width="50pt"/>
                    <fo:table-column column-width="90pt"/>
                    <fo:table-column column-width="140pt"/>  
               	    <fo:table-column column-width="90pt"/>
               	    <fo:table-column column-width="80pt"/>
            		 <fo:table-column column-width="90pt"/>
            		<fo:table-column column-width="100pt"/>
            		
                    <fo:table-body text-align="center">
                     <#assign sNo=1>
	                    
	                    <#list orderDetailsList as orderListItem>
	                    
	                  
					<#assign productId= orderListItem("productId")?if_exists >
		          <#assign productNameDetails = delegator.findOne("Product", {"productId" : productId}, true)>
		           <#if productNameDetails?has_content> 
		  <#--          		          <#assign UomIdDetails = delegator.findOne("Uom", {"uomId" : ${productNameDetails.get("quantityUomId")?if_exists} }, true)> -->
		           
                  	 <fo:table-row >
                	   <fo:table-cell ><fo:block text-align="center"  font-size="12pt" >${sNo} </fo:block></fo:table-cell>     
  				  	   <fo:table-cell ><fo:block text-align="left" font-size="12pt">${productNameDetails.get("internalName")?if_exists}</fo:block></fo:table-cell>     
  				       <fo:table-cell  ><fo:block text-align="left"    font-size="12pt" >${productNameDetails.get("productName")?if_exists} </fo:block></fo:table-cell>     
  				       <fo:table-cell  ><fo:block text-align="center" font-size="12pt">${orderListItem.get("unit")?if_exists}</fo:block></fo:table-cell>     
  				       <fo:table-cell  ><fo:block text-align="center"  font-size="12pt">${orderListItem.get("quantity")?if_exists}</fo:block></fo:table-cell>     
  			          <fo:table-cell  ><fo:block text-align="right"   font-size="12pt" >${orderListItem.get("unitPrice")?if_exists?string("##0.00")}</fo:block></fo:table-cell>     
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
    <fo:block  keep-together="always" text-align="left" font-family="Courier,monospace" white-space-collapse="false" font-size="12pt" font-weight="bold"> SUB TOTAL                          : ${allDetailsMap.get("total")?if_exists?string("##0.00")}  </fo:block> </#if>
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
							<fo:table-cell  ><fo:block text-align="left" font-size="12pt"  >-- ${Amount} INR </fo:block></fo:table-cell>
                          </fo:table-row>
                          </#if>
							<#if vatAmount gt 0>
                            <fo:table-row>
		                  	 <fo:table-cell  ><fo:block text-align="left" font-size="12pt"  >Value Added Tax On Purchase</fo:block></fo:table-cell>
							 <fo:table-cell  ><fo:block text-align="left" font-size="12pt"  >:  <#list vatpercents as vat>${vat}%,</#list></fo:block></fo:table-cell>
							<fo:table-cell  ><fo:block text-align="left" font-size="12pt"  >-- ${vatAmount} INR </fo:block></fo:table-cell>
                          </fo:table-row>
							</#if>
                          <#if cstAmount gt 0>
                            <fo:table-row>
		                  	 <fo:table-cell  ><fo:block text-align="left" font-size="12pt"  >Central Sales Tax On Purchase</fo:block></fo:table-cell>
							 <fo:table-cell  ><fo:block text-align="left" font-size="12pt"  >:  <#list cstpercents as cst>${cst}%</#list></fo:block></fo:table-cell>
							<fo:table-cell  ><fo:block text-align="left" font-size="12pt"  >-- ${cstAmount} INR </fo:block></fo:table-cell>
                          </fo:table-row>
							</#if>
						</#if>i
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
					 <#if allDetailsMap.get("grandTotal")?has_content> 
      <fo:block  keep-together="always" text-align="left" font-family="Courier,monospace" white-space-collapse="false" font-size="12pt" font-weight="bold" >  GRAND TOTAL                        :<#if allDetailsMap.get("discount")?has_content>${(allDetailsMap.get("grandTotal")-allDetailsMap.get("discount"))?string("##0.00")} INR<#else> ${allDetailsMap.get("grandTotal")?if_exists?string("##0.00")} INR</#if> </fo:block> </#if>
    
               
               <#assign amountWords = Static["org.ofbiz.base.util.UtilNumber"].formatRuleBasedAmount(allDetailsMap.get("grandTotal"), "%indRupees-and-paiseRupees", locale)>
										  	
                  <fo:block white-space-collapse="false" >Amount(words): Rupees ${amountWords}only</fo:block>
                  
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
                  
			<fo:block  keep-together="always" text-align="center"  font-family="Courier,monospace" white-space-collapse="false" font-size="12pt" >&#160;&#160; </fo:block>
			<fo:block  keep-together="always" text-align="center"  font-family="Courier,monospace" white-space-collapse="false" font-size="12pt" >&#160;&#160; </fo:block>
 				<#if listSize gt 10>
               <fo:block page-break-before="always"  text-align="left" font-family="Courier,monospace" white-space-collapse="false" font-size="12pt" >&#160;&#160;  Please send back the duplicate copy of the P.O. duly signed and sealed as a token of acceptance. You are requested to submit the bills in quadruplicate towards the supply of said  materials. Also please quote the Purchase Order No  and  Date in all your Letters, Delivery, Notes, and Invoices etc. </fo:block>
				<#else>
				<fo:block   text-align="left" font-family="Courier,monospace" white-space-collapse="false" font-size="12pt" >&#160;&#160;   You are requested to submit the bills in quadruplicate towards the supply of said  materials. Also please quote the Purchase Order No  and  Date in all your Letters, Delivery, Notes, and Invoices etc. </fo:block>
               </#if>

                  <fo:block  keep-together="always" text-align="center" font-family="Courier,monospace" white-space-collapse="false" font-size="12pt" > &#160;&#160;
                    <fo:block  keep-together="always" text-align="center" font-family="Courier,monospace" white-space-collapse="false" font-size="12pt" >&#160;&#160; </fo:block>
                    <fo:block  keep-together="always" text-align="left" font-family="Courier,monospace" white-space-collapse="false" font-size="12pt" > DESCRIPTION :${orderDesctioption?if_exists}</fo:block>
                                    <fo:block  keep-together="always" text-align="center" font-family="Courier,monospace" white-space-collapse="false" font-size="12pt" >&#160;&#160; </fo:block>
                   
              <fo:block  keep-together="always" text-align="left" font-family="Courier,monospace" white-space-collapse="false" font-size="12pt" >NOTE: Material Specifications enclosed.  </fo:block>
                                     
                       <fo:block  keep-together="always" text-align="right" font-family="Courier,monospace" white-space-collapse="false" font-size="12pt" > &#160; for MOTHER DAIRY &#160;&#160;</fo:block>
                      <fo:block  keep-together="always" text-align="center" font-family="Courier,monospace" white-space-collapse="false" font-size="12pt" >&#160;&#160; </fo:block> 
					<fo:block  keep-together="always" text-align="center" font-family="Courier,monospace" white-space-collapse="false" font-size="12pt" >&#160;&#160; </fo:block> 
	                               <fo:block  keep-together="always" text-align="center" font-family="Courier,monospace" white-space-collapse="false" font-size="12pt" >&#160;&#160; </fo:block> 

           <#if signature?has_content> 
                <fo:block  keep-together="always" text-align="right" font-family="Courier,monospace" white-space-collapse="false" font-size="12pt" >  &#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;       ${signature} &#160;&#160; </fo:block>
              <#else>
                <fo:block  keep-together="always" text-align="right" font-family="Courier,monospace" white-space-collapse="false" font-size="12pt" >&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;  MANAGER(Purchase) &#160; </fo:block>
                 </#if>  
            <fo:block  keep-together="always" text-align="right" font-family="Courier,monospace" white-space-collapse="false" font-size="12pt" >  Mother Dairy   &#160;&#160;</fo:block>
                                  
                                   </fo:block>
                 
                                                                                                                                
                                                                                                                               
              	<fo:block page-break-before="always"  keep-together="always" text-align="center" font-family="Courier,monospace" white-space-collapse="false" font-weight="bold" font-size="12pt" >MATERIAL SPECIFICATIONS</fo:block>
                                                                 <fo:block  keep-together="always" text-align="left" font-family="Courier,monospace" white-space-collapse="false" font-size="12pt" >&#160;&#160;  </fo:block>
                 
                <fo:block  keep-together="always" text-align="center" font-family="Courier,monospace" white-space-collapse="false" font-size="12pt" >&#160;&#160;----------------------------------------------------------------------------------------- </fo:block>
              	<fo:block  keep-together="always" text-align="left" font-family="Courier,monospace" white-space-collapse="false" font-size="12pt" font-weight="bold">&#160;&#160; SNO            PRODUCT NAME         SPECIFICATION           </fo:block>
              	<fo:block  keep-together="always" text-align="center" font-family="Courier,monospace" white-space-collapse="false" font-size="12pt" >&#160;&#160;----------------------------------------------------------------------------------------- </fo:block>
 
                 
                 <fo:block>
                 <fo:table text-align="center" >
                    <fo:table-column column-width="60pt"/>
                    <fo:table-column column-width="200pt"/>
               	    <fo:table-column column-width="220pt"/>
            		
                    <fo:table-body text-align="center">
                     <#assign sNo=1>
	                    
	                    <#list orderDetailsList as orderListItem>
	                    
	                  
					<#assign productId= orderListItem("productId")?if_exists >
		          <#assign productNameDetails = delegator.findOne("Product", {"productId" : productId}, true)>
		           <#if productNameDetails?has_content> 
		  <#--          		          <#assign UomIdDetails = delegator.findOne("Uom", {"uomId" : ${productNameDetails.get("quantityUomId")?if_exists} }, true)> -->
                  	 <fo:table-row >
                	   <fo:table-cell ><fo:block text-align="center"  font-size="12pt" >${sNo} </fo:block></fo:table-cell>     
  				  	   <fo:table-cell ><fo:block text-align="center" font-size="12pt">${productNameDetails.get("productName")?if_exists}</fo:block></fo:table-cell>     
  				       <fo:table-cell ><fo:block text-align="left" font-size="12pt">${productNameDetails.get("longDescription")?if_exists}</fo:block></fo:table-cell>     
  			         
  				     </fo:table-row>
  				    	<#assign sNo=sNo+1>
  				    	</#if>
  				     </#list>
                    </fo:table-body>
                </fo:table>
               </fo:block>
              	                 
				<fo:block  keep-together="always" text-align="center" font-family="Courier,monospace" white-space-collapse="false" font-size="12pt" >&#160;&#160;----------------------------------------------------------------------------------------- </fo:block>
                     <#if allDetailsMap.get("noteInfo")?has_content> 
                  <fo:block  keep-together="always" text-align="left" font-family="Courier,monospace" white-space-collapse="false" font-size="12pt" >Note:${allDetailsMap.get("noteInfo")?if_exists} </fo:block>  </#if>

                 <fo:block  keep-together="always" text-align="center" font-family="Courier,monospace" white-space-collapse="false" font-size="12pt" > &#160;&#160;
                <fo:block  keep-together="always" text-align="center" font-family="Courier,monospace" white-space-collapse="false" font-size="12pt" >&#160;&#160; </fo:block>
                  <fo:block  keep-together="always" text-align="center" font-family="Courier,monospace" white-space-collapse="false" font-size="12pt" >&#160;&#160; </fo:block>
                   
                                     
                       <fo:block  keep-together="always" text-align="right" font-family="Courier,monospace" white-space-collapse="false" font-size="12pt" > &#160; for MOTHER DAIRY &#160;&#160;</fo:block>
                      <fo:block  keep-together="always" text-align="center" font-family="Courier,monospace" white-space-collapse="false" font-size="12pt" >&#160;&#160; </fo:block> 
					<fo:block  keep-together="always" text-align="center" font-family="Courier,monospace" white-space-collapse="false" font-size="12pt" >&#160;&#160; </fo:block> 
	                               <fo:block  keep-together="always" text-align="center" font-family="Courier,monospace" white-space-collapse="false" font-size="12pt" >&#160;&#160; </fo:block> 

           <#if signature?has_content> 
                <fo:block  keep-together="always" text-align="right" font-family="Courier,monospace" white-space-collapse="false" font-size="12pt" >  &#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;       ${signature} &#160;&#160; </fo:block>
              <#else>
                <fo:block  keep-together="always" text-align="right" font-family="Courier,monospace" white-space-collapse="false" font-size="12pt" >&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;  MANAGER(Purchase) &#160; </fo:block>
                 </#if>  
            <fo:block  keep-together="always" text-align="right" font-family="Courier,monospace" white-space-collapse="false" font-size="12pt" >  Mother Dairy   &#160;&#160;</fo:block>
                                  
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
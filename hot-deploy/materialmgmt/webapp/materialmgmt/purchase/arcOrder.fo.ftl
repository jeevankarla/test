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
            margin-top="0in" margin-bottom=".7in" margin-left=".5in" margin-right=".5in">
        <fo:region-body margin-top="0.2in"/>
        <fo:region-before extent="1.5in"/>
        <fo:region-after extent="1.5in"/>        
    </fo:simple-page-master>   
</fo:layout-master-set>
${setRequestAttribute("OUTPUT_FILENAME", "arcOrder.pdf")}
 <#if orderDetailsList?has_content> 

<fo:page-sequence master-reference="main" force-page-count="no-force" font-family="Courier,monospace">	
		<#assign pageNumber = 0>				
		<fo:static-content flow-name="xsl-region-before">
			<fo:block  keep-together="always" text-align="right" font-family="Courier,monospace" white-space-collapse="false"> &#160;Page - <fo:page-number/></fo:block>
		       	 
            </fo:static-content>		
            <fo:flow flow-name="xsl-region-body"   font-family="Courier,monospace">	
			       <fo:block  keep-together="always" text-align="left"  font-family="Courier,monospace" white-space-collapse="false">&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;    UserLogin: <#if userLogin?exists>${userLogin.userLoginId?if_exists}</#if></fo:block>
		   <fo:block  keep-together="always" text-align="left" font-family="Courier,monospace" white-space-collapse="false">&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;Date     : ${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(nowTimestamp, "dd/MM/yy HH:mm:ss")}</fo:block>
		        <fo:block  keep-together="always" text-align="center" font-family="Courier,monospace" white-space-collapse="false" font-size="12pt" > &#160;&#160;  </fo:block>
				<fo:block  keep-together="always" text-align="center" font-family="Courier,monospace" white-space-collapse="false" font-size="20pt" font-weight="bold" >  KARNATAKA CO-OPERATIVE MILK PRODUCERS FEDERATION LTD., </fo:block>
				<fo:block  keep-together="always" text-align="center" font-family="Courier,monospace" white-space-collapse="false" font-size="17pt" font-weight="bold" >  UNIT : MOTHER DAIRY, YELAHANKA, BANGALORE   </fo:block>
			    <fo:block  keep-together="always" text-align="left" font-family="Courier,monospace" white-space-collapse="false" font-size="5pt" > ----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------</fo:block>
			    <fo:block  keep-together="always" text-align="center" font-family="Courier,monospace" white-space-collapse="false" font-size="12pt" > &#160;&#160;  </fo:block>
           <#assign OrderHeaderDetails = delegator.findOne("OrderHeader", {"orderId" :parameters.orderId }, true)>
		   		          <#if OrderHeaderDetails?has_content>
			        	<#--   <fo:block white-space-collapse="false" font-size="10pt"  font-family="Helvetica" keep-together="always" >&#160;  STORE CODE:${parameters.stockId}&#160;    &#160;     &#160;  DESCRIPTION:${stockDetails.get("description")?if_exists}</fo:block> -->
			      
              		  
               	<fo:block  keep-together="always" text-align="center" font-family="Courier,monospace" white-space-collapse="false" font-size="12pt" >&#160;&#160; </fo:block>
	             <fo:block  keep-together="always" text-align="center" font-family="Courier,monospace" white-space-collapse="false" font-size="12pt" >&#160;&#160; </fo:block>
 <fo:block >
			        			 <fo:table width="100%" align="right" table-layout="fixed"  font-size="12pt">
					               <fo:table-column column-width="60pt"/>               
					                <fo:table-column column-width="350pt"/>               
						           <fo:table-column column-width="160pt"/>               
					                <fo:table-column column-width="100pt"/>               

						           	<fo:table-body>
				                     <fo:table-row>
				                     <fo:table-cell  ><fo:block text-align="left" font-size="12pt" font-weight="bold"  >Ref No&#160;:</fo:block></fo:table-cell>       			
				                     <fo:table-cell  ><fo:block text-align="left"  font-size="12pt"  >${allDetailsMap.get("refNo")?if_exists}</fo:block></fo:table-cell>       		
				                     <fo:table-cell  ><fo:block text-align="right"  font-size="12pt"  > DATE:</fo:block></fo:table-cell>       		
				                     <fo:table-cell  ><fo:block text-align="left"  font-size="12pt"  > ${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(nowTimestamp, "dd-MMM-yy")}</fo:block></fo:table-cell>       		
                                  </fo:table-row>
			                	</fo:table-body>
			                		</fo:table>
			        	  </fo:block>	
		    <#--<fo:block  keep-together="always" text-align="left" font-family="Courier,monospace" white-space-collapse="false" font-size="12pt" >PO Seq No :${allDetailsMap.get("sequenceId")?if_exists}</fo:block>		        	  
         	    <fo:block  keep-together="always" text-align="left" font-family="Courier,monospace" white-space-collapse="false" font-size="12pt" >    Ref No: ${allDetailsMap.get("refNo")?if_exists}                                                   DATE: ${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(nowTimestamp, "dd-MMM-yy")}  </fo:block> 
              	<fo:block  keep-together="always" text-align="left" font-family="Courier,monospace" white-space-collapse="false" font-size="13pt" >    OrderNo:${allDetailsMap.get("orderNo")?if_exists}                                                     </fo:block>
               	<fo:block  keep-together="always" text-align="left" font-family="Courier,monospace" white-space-collapse="false" font-size="13pt" >    File No:${allDetailsMap.get("fileNo")?if_exists}                                                       </fo:block>   -->

            <fo:block  keep-together="always" text-align="center" font-family="Courier,monospace" white-space-collapse="false" font-size="3pt" >&#160; </fo:block> 
                <fo:block  keep-together="always" text-align="left" font-family="Courier,monospace" white-space-collapse="false" font-size="12pt" font-weight="bold" >ADDRESS:      </fo:block>
              	<fo:block  keep-together="always" text-align="left" font-family="Courier,monospace" white-space-collapse="false" font-size="12pt" ><#if allDetailsMap.get("partyId")?has_content>Id: ${allDetailsMap.get("partyId")}, <#else> </#if><#if allDetailsMap.get("partyName")?has_content>${allDetailsMap.get("partyName")} <#else> </#if>        </fo:block>
              	<fo:block  keep-together="always" text-align="left" font-family="Courier,monospace" white-space-collapse="false" font-size="12pt" ><#if allDetailsMap.get("address1")?has_content>${allDetailsMap.get("address1")}   <#else> </#if>     </fo:block>
                <fo:block  keep-together="always" text-align="left" font-family="Courier,monospace" white-space-collapse="false" font-size="12pt" ><#if allDetailsMap.get("address2")?has_content>${allDetailsMap.get("address2")?if_exists} <#else> </#if>     </fo:block>
                <fo:block  keep-together="always" text-align="left" font-family="Courier,monospace" white-space-collapse="false" font-size="12pt" ><#if allDetailsMap.get("city")?has_content>${allDetailsMap.get("city")?if_exists}-${allDetailsMap.get("postalCode")?if_exists}. <#else> </#if>                          </fo:block>
                
              	<fo:block  keep-together="always" text-align="left" font-family="Courier,monospace" white-space-collapse="false" font-size="12pt" >    Ph No: ${allDetailsMap.get("phoneNumber")?if_exists}         </fo:block>
                <fo:block  keep-together="always" text-align="left" font-family="Courier,monospace" white-space-collapse="false" font-size="12pt" >    Mail : ${allDetailsMap.get("emailAddress")?if_exists}        </fo:block>
               	<fo:block  keep-together="always" text-align="left" font-family="Courier,monospace" white-space-collapse="false" font-size="16pt" font-weight="bold" >&#160;&#160;&#160;&#160;                         <fo:inline font-weight="bold" text-decoration="underline">ARC ORDER</fo:inline>         </fo:block>
                <fo:block   text-align="left" font-family="Courier,monospace" white-space-collapse="false" font-size="10pt" > &#160;</fo:block>
                <#assign productWiseTotalList =allDetailsMap.get("productDescription")>               
                 <#if allDetailsMap.get("subject")?has_content>
                <fo:block >
			        			 <fo:table width="100%" align="right" table-layout="fixed"  font-size="12pt">
					               <fo:table-column column-width="70pt"/>               
					                <fo:table-column column-width="580pt"/>               
						          
						           	<fo:table-body>
				                     <fo:table-row>
				                      <fo:table-cell ><fo:block text-align="left" font-size="12pt" font-weight="bold" keep-together="always" >&#160;    &#160;&#160;      Sub: </fo:block></fo:table-cell>       		
				                     <fo:table-cell ><fo:block text-align="left"  font-size="12pt"  >${allDetailsMap.get("subject")}</fo:block></fo:table-cell>       		
				                       </fo:table-row>
			                	</fo:table-body>
			                		</fo:table>
			        	  </fo:block>	
			        </#if> 	  
         <#if allDetailsMap.get("tendorNo")?has_content || allDetailsMap.get("techDate")?has_content || allDetailsMap.get("negotiationDate")?has_content || allDetailsMap.get("commercialDate")?has_content || allDetailsMap.get("loaDate")?has_content> 
                <fo:block   text-align="left" font-family="Courier,monospace" white-space-collapse="false" font-size="12pt" ><fo:inline text-align="left" font-family="Courier,monospace" font-size="12pt" font-weight="bold">&#160;    Ref:</fo:inline><#if allDetailsMap.get("tendorNo")?has_content> Tender Notification No.${allDetailsMap.get("tendorDate")?if_exists}, Dtd:${allDetailsMap.get("tendorDate")?if_exists}.  </#if>   </fo:block> 
               <#if allDetailsMap.get("techDate")?has_content>  <fo:block  keep-together="always" text-align="left" font-family="Courier,monospace" white-space-collapse="false" font-size="12pt" >&#160;         Technical Tender opened on ${allDetailsMap.get("techDate")?if_exists}  </fo:block> </#if> 
               <#if allDetailsMap.get("commercialDate")?has_content>  <fo:block  keep-together="always" text-align="left" font-family="Courier,monospace" white-space-collapse="false" font-size="12pt" >&#160;         Commercial Tender opened on ${allDetailsMap.get("commercialDate")?if_exists}       </fo:block> </#if>
               <#if allDetailsMap.get("negotiationDate")?has_content>  <fo:block  keep-together="always" text-align="left" font-family="Courier,monospace" white-space-collapse="false" font-size="12pt" >&#160;         Negotiation held on ${allDetailsMap.get("negotiationDate")?if_exists}       </fo:block> </#if> 
               <#if allDetailsMap.get("loaDate")?has_content>  <fo:block  keep-together="always" text-align="left" font-family="Courier,monospace" white-space-collapse="false" font-size="12pt" >&#160;         LOA of Even No. dtd: ${allDetailsMap.get("loaDate")?if_exists}      </fo:block></#if> 
            </#if>
            	<fo:block text-align="left" font-size="12pt" white-space-collapse="false" keep-together="always" > &#160;</fo:block>
                  <fo:block  keep-together="always" text-align="left" font-family="Verdana" white-space-collapse="false" font-size="15pt" >&#160;&#160;&#160;&#160;   &#160;&#160;&#160;&#160; &#160;&#160;&#160;&#160; &#160;&#160;&#160;&#160;                                                            *****</fo:block>
            
             <fo:block  text-align="left" font-family="Courier,monospace" white-space-collapse="false" font-size="12pt" >&#160;    With reference to the above,we are pleased to place  an order with you for the supply </fo:block>
                <fo:block   text-align="left" font-family="Courier,monospace" white-space-collapse="false" font-size="12pt" >of  <#list productWiseTotalList as productWiseEntries>${productWiseEntries}, </#list> on ARC basis with effect from ${allDetailsMap.get("fromDate")?if_exists} to ${allDetailsMap.get("thruDate")?if_exists} as per the tender specifications and subject to the terms and conditions stipulated below. </fo:block>
               <fo:block text-align="left" font-size="12pt" white-space-collapse="false" keep-together="always" > &#160;</fo:block>
            
            <fo:block >
			        			 <fo:table width="100%" align="right" table-layout="fixed"  font-size="12pt">
					               <fo:table-column column-width="40pt"/>               
					                <fo:table-column column-width="220pt"/>               
						            <fo:table-column column-width="120pt"/>
						            <fo:table-column column-width="130pt"/>
			      			        <fo:table-column column-width="140pt"/> 
						           	<fo:table-body>
				                     <fo:table-row>
				                      <fo:table-cell  border-style="solid"><fo:block text-align="center"  font-weight="bold" keep-together="always" font-size="12pt" >S NO</fo:block></fo:table-cell>       		
				                     <fo:table-cell  border-style="solid"><fo:block text-align="center"  font-weight="bold" keep-together="always" font-size="12pt">Particulars</fo:block></fo:table-cell>       		
				                      <fo:table-cell  border-style="solid"><fo:block text-align="center"  font-weight="bold" keep-together="always" font-size="12pt">Quantity</fo:block></fo:table-cell>       		
				                      <fo:table-cell   border-style="solid"><fo:block text-align="center"  font-weight="bold" keep-together="always" font-size="12pt">Unit Rate(Rs)</fo:block></fo:table-cell>       		
				                      <fo:table-cell  border-style="solid"><fo:block text-align="center"  font-weight="bold" keep-together="always" font-size="12pt"  >Total Amount(Rs) </fo:block></fo:table-cell>    		
				                     </fo:table-row>
				                     
			                	</fo:table-body>
			                		</fo:table>
			        	  </fo:block>	
            	<fo:block>
                 <fo:table text-align="center" >
                    <fo:table-column column-width="40pt"/>               
					 <fo:table-column column-width="220pt"/>               
					<fo:table-column column-width="120pt"/>
					 <fo:table-column column-width="130pt"/>
			      	<fo:table-column column-width="140pt"/> 
            		
                    <fo:table-body text-align="center">
                     <#assign sNo=1>
	                    
	                    <#list orderDetailsList as orderListItem>
	                    
	                  
					<#assign productId= orderListItem("productId")?if_exists >
		          <#assign productNameDetails = delegator.findOne("Product", {"productId" : productId}, true)>
		           <#if productNameDetails?has_content> 
                  	 <fo:table-row >
                	   <fo:table-cell border-style="solid"  ><fo:block text-align="center"  font-size="12pt" >${sNo}</fo:block></fo:table-cell>     
  				       <fo:table-cell  border-style="solid" ><fo:block text-align="left"   font-size="12pt" >${productNameDetails.get("productName")?if_exists} </fo:block></fo:table-cell>     
  				       <fo:table-cell  border-style="solid"><fo:block text-align="center"  font-size="12pt">${orderListItem.get("quantity")?if_exists}</fo:block></fo:table-cell>     
  			          <fo:table-cell   border-style="solid"><fo:block text-align="right" font-size="12pt" >${orderListItem.get("unitPrice")?if_exists?string("##0.00")}</fo:block></fo:table-cell>     
  				       <fo:table-cell   border-style="solid"><fo:block text-align="right" font-size="12pt" >${orderListItem.get("amount")?if_exists?string("##0.00")}</fo:block></fo:table-cell>     
  				         
  				     </fo:table-row>
  				    	<#assign sNo=sNo+1>
  				    	</#if>
  				     </#list>
  				     <fo:table-row >
  				       <fo:table-cell  border-style="solid"  number-columns-spanned="4"><fo:block text-align="center"  font-size="15pt"> Total Amount    &#160;</fo:block></fo:table-cell>     
  				       <fo:table-cell   border-style="solid"><fo:block text-align="right" font-size="13pt" >Rs. ${allDetailsMap.get("total")?if_exists?string("##0.00")} </fo:block></fo:table-cell>     
  				         
  				     </fo:table-row>
                    </fo:table-body>
                </fo:table>
               </fo:block>
                	<fo:block text-align="left" font-size="5pt" white-space-collapse="false" keep-together="always" > &#160;</fo:block>
               
               
               
               
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
                           <fo:table-column column-width="100"/>
							<fo:table-column column-width="170"/> 
							<fo:table-column column-width="100"/> 
							<fo:table-column column-width="100"/> 
							              
				            <fo:table-body>
				           <#if termType == "TAX">
							<fo:table-row>
		                     <fo:table-cell  ><fo:block text-align="left" font-size="12pt"  ><fo:inline  text-decoration="underline"  font-weight="bold">TAX&#160;&#160;:</fo:inline></fo:block></fo:table-cell>       			
                          </fo:table-row>
                          <#if Amount gt 0>
                          <fo:table-row>
		                  	 <fo:table-cell  ><fo:block text-align="left" font-size="12pt"  >Basic Excise Duty On Purchase</fo:block></fo:table-cell>
							 <fo:table-cell  ><fo:block text-align="left" font-size="12pt"  >: 12.36%  </fo:block></fo:table-cell>
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
							<#if termtypeId.get("uomId")=="INR">
							 <fo:table-cell  ><fo:block text-align="left" font-size="12pt"  >: ${termtypeId.get("termValue")?if_exists}  </fo:block></fo:table-cell>
							 </#if>
							<fo:table-cell  ><fo:block text-align="left" font-size="12pt"  >-- ${termtypeId.get("amount")?if_exists} INR </fo:block></fo:table-cell>
                          </fo:table-row>
						</#list>
						</#if>
	                	</fo:table-body>
	                		</fo:table>
	        	  </fo:block>	
	</#list>
		 <#if allDetailsMap.get("grandTotal")?has_content> 
      <fo:block  keep-together="always" text-align="left" font-family="Courier,monospace" white-space-collapse="false" font-size="12pt" font-weight="bold" >  GRAND TOTAL                        :<#if allDetailsMap.get("discount")?has_content>${(allDetailsMap.get("grandTotal")-allDetailsMap.get("discount"))?string("##0.00")} INR<#else> ${allDetailsMap.get("grandTotal")?if_exists?string("##0.00")} INR</#if> </fo:block> 
               
    	           <#assign amountWords = Static["org.ofbiz.base.util.UtilNumber"].formatRuleBasedAmount(allDetailsMap.get("grandTotal"), "%indRupees-and-paiseRupees", locale)>
										  	
                  <fo:block white-space-collapse="false" >Amount(words): Rupees ${amountWords}only</fo:block>  </#if>
                    
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


        <fo:block  keep-together="always" text-align="center" font-family="Courier,monospace" white-space-collapse="false" font-size="12pt" >&#160;&#160; 

                  <fo:block  keep-together="always" text-align="center" font-family="Courier,monospace" white-space-collapse="false" font-size="12pt" >&#160;&#160; </fo:block>
                 <fo:block  keep-together="always" text-align="left" font-family="Courier,monospace" white-space-collapse="false" font-size="12pt" >&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;                                                          Yours Faithfully,</fo:block>
                 <fo:block  keep-together="always" text-align="left" font-family="Courier,monospace" white-space-collapse="false" font-size="12pt" >&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;                                                          For Mother Dairy.</fo:block>
                                  <fo:block  keep-together="always" text-align="left" font-family="Courier,monospace" white-space-collapse="false" font-size="10pt" >&#160;</fo:block>
                                  <fo:block  keep-together="always" text-align="left" font-family="Courier,monospace" white-space-collapse="false" font-size="12pt" >&#160;NOTE: Material Specifications enclosed</fo:block>
                 
                 <fo:block  keep-together="always" text-align="left" font-family="Courier,monospace" white-space-collapse="false" font-size="14pt" font-weight="bold">&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;                                                             MANAGER(PUR)    </fo:block>
                 <fo:block  keep-together="always" text-align="left" font-family="Courier,monospace" white-space-collapse="false" font-size="12pt" >&#160;</fo:block>
         </fo:block>      
		 </#if>


                                                  <fo:block  keep-together="always" text-align="left" font-family="Courier,monospace" white-space-collapse="false" font-size="12pt" >&#160;&#160;  </fo:block>
                                                                 <fo:block  keep-together="always" text-align="left" font-family="Courier,monospace" white-space-collapse="false" font-size="12pt" >&#160;&#160;  </fo:block>
                                                                 <fo:block  keep-together="always" text-align="left" font-family="Courier,monospace" white-space-collapse="false" font-size="12pt" >&#160;&#160;  </fo:block>
                                                                                                                                
                                                                 <fo:block  keep-together="always" text-align="left" font-family="Courier,monospace" white-space-collapse="false" font-size="12pt" >&#160;&#160;  </fo:block>
                     				<fo:block break-before="page"/><fo:block  text-align="center" ></fo:block>
                 <fo:block  keep-together="always" text-align="left" font-family="Courier,monospace" white-space-collapse="false" font-size="12pt" >&#160;&#160;  </fo:block>
                                                                                                                                <fo:block  keep-together="always" text-align="left" font-family="Courier,monospace" white-space-collapse="false" font-size="12pt" >&#160;&#160;  </fo:block>
                                                                                                                               
              	<fo:block  keep-together="always" text-align="center" font-family="Courier,monospace" white-space-collapse="false" font-weight="bold" font-size="12pt" >MATERIAL SPECIFICATIONS</fo:block>
                                                                 <fo:block  keep-together="always" text-align="left" font-family="Courier,monospace" white-space-collapse="false" font-size="12pt" >&#160;&#160;  </fo:block>
                 
                <fo:block  keep-together="always" text-align="center" font-family="Courier,monospace" white-space-collapse="false" font-size="12pt" >&#160;&#160;----------------------------------------------------------------------------------------- </fo:block>
              	<fo:block  keep-together="always" text-align="left" font-family="Courier,monospace" white-space-collapse="false" font-size="12pt" font-weight="bold">&#160;&#160; SNO            ITEM CODE         SPECIFICATION           </fo:block>
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
  				  	   <fo:table-cell ><fo:block text-align="center" font-size="12pt">${productNameDetails.get("internalName")?if_exists}</fo:block></fo:table-cell>     
  				       <fo:table-cell ><fo:block text-align="left" font-size="12pt">${orderListItem.get("longDescription")?if_exists}</fo:block></fo:table-cell>     
  			         
  				     </fo:table-row>
  				    	<#assign sNo=sNo+1>
  				    	</#if>
  				     </#list>
                    </fo:table-body>
                </fo:table>
               </fo:block>
                 
              	<fo:block  keep-together="always" text-align="center" font-family="Courier,monospace" white-space-collapse="false" font-size="12pt" >&#160;&#160;----------------------------------------------------------------------------------------- </fo:block>
                                <fo:block  keep-together="always" text-align="center" font-family="Courier,monospace" white-space-collapse="false" font-size="12pt" >&#160;&#160; </fo:block> 
                                <fo:block  keep-together="always" text-align="center" font-family="Courier,monospace" white-space-collapse="false" font-size="12pt" >&#160;&#160; </fo:block> 
                     <#if allDetailsMap.get("noteInfo")?has_content> 
                  <fo:block  keep-together="always" text-align="left" font-family="Courier,monospace" white-space-collapse="false" font-size="12pt" >Note:${allDetailsMap.get("noteInfo")?if_exists} </fo:block>  </#if>

                       <fo:block  keep-together="always" text-align="center" font-family="Courier,monospace" white-space-collapse="false" font-size="12pt" >&#160;&#160; </fo:block> 
 
                              <fo:block  keep-together="always" text-align="right" font-family="Courier,monospace" white-space-collapse="false" font-size="12pt" >  for MOTHER DAIRY   &#160;&#160;</fo:block>
                               <fo:block  keep-together="always" text-align="center" font-family="Courier,monospace" white-space-collapse="false" font-size="12pt" >&#160;&#160; </fo:block> 
                               <fo:block  keep-together="always" text-align="center" font-family="Courier,monospace" white-space-collapse="false" font-size="12pt" >&#160;&#160; </fo:block> 
                                                              <fo:block  keep-together="always" text-align="center" font-family="Courier,monospace" white-space-collapse="false" font-size="12pt" >&#160;&#160; </fo:block> 
                             <fo:block  keep-together="always" text-align="right" font-family="Courier,monospace" white-space-collapse="false" font-size="12pt" >  Manager(Purchase)  &#160;&#160;</fo:block>
                               <fo:block  keep-together="always" text-align="right" font-family="Courier,monospace" white-space-collapse="false" font-size="12pt" >  Mother Dairy   &#160;&#160;</fo:block>
                               <fo:block  keep-together="always" text-align="left" font-family="Courier,monospace" white-space-collapse="false" font-size="12pt" >&#160;&#160;  </fo:block>
                           
                             

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


<#--
  <fo:block>
							<fo:table  table-layout="fixed" width="48%" space-before="0.2in" font-family="Courier,monospace" >
							    <fo:table-column column-width="35%"/>
							    <fo:table-column column-width="55%"/>
							    <fo:table-column column-width="50%"/>
								<fo:table-body>
									<fo:table-row>
										<fo:table-cell border-style = "solid">
											<fo:block text-align="left" font-size="12pt" white-space-collapse="false" keep-together="always" > &#160; </fo:block>
											<fo:block text-align="center" font-size="12pt" white-space-collapse="false" keep-together="always" >"Draft Approved by</fo:block>
											<fo:block text-align="center" font-size="12pt" white-space-collapse="false" keep-together="always" >the Director"</fo:block>
																					<fo:block text-align="left" font-size="12pt" white-space-collapse="false" keep-together="always" > &#160;</fo:block>
										</fo:table-cell>
										<fo:table-cell>
											<fo:block text-align="left" font-size="12pt" white-space-collapse="false" keep-together="always" > &#160;</fo:block>
											<fo:block text-align="left" font-size="12pt" white-space-collapse="false" keep-together="always" > &#160; </fo:block>
											<fo:block text-align="left" font-size="12pt" white-space-collapse="false" keep-together="always" > &#160; </fo:block>
											<fo:block text-align="right" font-size="12pt" white-space-collapse="false" keep-together="always" >&#160;</fo:block>
										</fo:table-cell>
										<fo:table-cell >
											<fo:block text-align="left" font-size="12pt" white-space-collapse="false" keep-together="always" > &#160; </fo:block>
											<fo:block text-align="center" font-size="12pt" white-space-collapse="false" keep-together="always" >Yours Faithfully,</fo:block>
											<fo:block text-align="center" font-size="12pt" white-space-collapse="false" keep-together="always" > For Mother Dairy.</fo:block>
						 																<fo:block text-align="left" font-size="12pt" white-space-collapse="false" keep-together="always" > &#160;</fo:block>
										</fo:table-cell>
									</fo:table-row>
								</fo:table-body>
							</fo:table>
						</fo:block>
         -->               
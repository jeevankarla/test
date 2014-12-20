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
        <fo:region-body margin-top="1.0in"/>
        <fo:region-before extent="1.in"/>
        <fo:region-after extent="1.5in"/>        
    </fo:simple-page-master>   
</fo:layout-master-set>
${setRequestAttribute("OUTPUT_FILENAME", "arcOrder.pdf")}
 <#if orderDetailsList?has_content> 

<fo:page-sequence master-reference="main" force-page-count="no-force" font-family="Courier,monospace">					
		<fo:static-content flow-name="xsl-region-before">
			<#--<fo:block  keep-together="always" text-align="center" font-family="Courier,monospace" white-space-collapse="false">&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;UserLogin : <#if userLogin?exists>${userLogin.userLoginId?if_exists}</#if></fo:block>
				<fo:block  keep-together="always" text-align="center" font-family="Courier,monospace" white-space-collapse="false">&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;Date:${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(nowTimestamp, "dd/MM/yy HH:mm:ss")}</fo:block> -->
		        <fo:block  keep-together="always" text-align="center" font-family="Courier,monospace" white-space-collapse="false" font-size="12pt" > &#160;&#160;  </fo:block>
				<fo:block  keep-together="always" text-align="center" font-family="Courier,monospace" white-space-collapse="false" font-size="20pt" font-weight="bold" >  KARNATAKA CO-OPERATIVE MILK PRODUCERS FEDERATION LTD., </fo:block>
				<fo:block  keep-together="always" text-align="center" font-family="Courier,monospace" white-space-collapse="false" font-size="17pt" font-weight="bold" >  UNIT : MOTHER DAIRY, YELAHANKA, BANGALORE   </fo:block>
			    <fo:block  keep-together="always" text-align="left" font-family="Courier,monospace" white-space-collapse="false" font-size="5pt" > ----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------</fo:block>
			    <fo:block  keep-together="always" text-align="center" font-family="Courier,monospace" white-space-collapse="false" font-size="12pt" > &#160;&#160;  </fo:block>
			
		          
              	 
            </fo:static-content>		
            <fo:flow flow-name="xsl-region-body"   font-family="Courier,monospace">	
           <#assign OrderHeaderDetails = delegator.findOne("OrderHeader", {"orderId" :parameters.orderId }, true)>
		   		          <#if OrderHeaderDetails?has_content>
			        	<#--   <fo:block white-space-collapse="false" font-size="10pt"  font-family="Helvetica" keep-together="always" >&#160;  STORE CODE:${parameters.stockId}&#160;    &#160;     &#160;  DESCRIPTION:${stockDetails.get("description")?if_exists}</fo:block> -->
			              
              		  
                
	             <fo:block  keep-together="always" text-align="center" font-family="Courier,monospace" white-space-collapse="false" font-size="12pt" >&#160;&#160; </fo:block>
              	<fo:block  keep-together="always" text-align="left" font-family="Courier,monospace" white-space-collapse="false" font-size="12pt" >    Ref  No:  ${allDetailsMap.get("orderId")?if_exists}                                                   DATE: ${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(nowTimestamp, "dd-MMM-yy")}  </fo:block>
                <fo:block  keep-together="always" text-align="center" font-family="Courier,monospace" white-space-collapse="false" font-size="3pt" >&#160; </fo:block>
              	<fo:block  keep-together="always" text-align="left" font-family="Courier,monospace" white-space-collapse="false" font-size="12pt" >&#160;   <#if allDetailsMap.get("address1")?has_content> ${allDetailsMap.get("address1")?if_exists} <#else> </#if>       </fo:block>
                <fo:block  keep-together="always" text-align="left" font-family="Courier,monospace" white-space-collapse="false" font-size="12pt" >&#160;  <#if allDetailsMap.get("address1")?has_content>  ${allDetailsMap.get("address2")?if_exists}, <#else> </#if>     </fo:block>
                <fo:block  keep-together="always" text-align="left" font-family="Courier,monospace" white-space-collapse="false" font-size="12pt" >&#160;   <#if allDetailsMap.get("address1")?has_content> ${allDetailsMap.get("city")?if_exists}-${allDetailsMap.get("postalCode")?if_exists}. <#else> </#if>                          </fo:block>
                <fo:block  keep-together="always" text-align="left" font-family="Courier,monospace" white-space-collapse="false" font-size="12pt" >&#160;    Ph No: ${allDetailsMap.get("phoneNumber")?if_exists}         </fo:block>
                <fo:block  keep-together="always" text-align="left" font-family="Courier,monospace" white-space-collapse="false" font-size="12pt" >&#160;    Mail : ${allDetailsMap.get("emailAddress")?if_exists}        </fo:block>
               	<fo:block  keep-together="always" text-align="left" font-family="Courier,monospace" white-space-collapse="false" font-size="16pt" font-weight="bold" >&#160;&#160;&#160;&#160;                       <fo:inline font-weight="bold" text-decoration="underline">LETTER OF ACCEPTANCE </fo:inline>         </fo:block>
                <fo:block   text-align="left" font-family="Courier,monospace" white-space-collapse="false" font-size="10pt" > &#160;</fo:block>
                <fo:block   text-align="left" font-family="Courier,monospace" white-space-collapse="false" font-size="14pt" font-weight="bold"> Sir,</fo:block>
                <fo:block   text-align="left" font-family="Courier,monospace" white-space-collapse="false" font-size="10pt" > &#160;</fo:block>
                
                <#assign productWiseTotalList =allDetailsMap.get("productDescription")>
                
                <fo:block  text-align="left" font-family="Courier,monospace" white-space-collapse="false" font-size="12pt" ><fo:inline text-align="left" font-family="Courier,monospace"  font-size="12pt" font-weight="bold"> &#160;&#160;&#160;     Sub :</fo:inline> Supply of Corrugated boxes for <#list productWiseTotalList as productWiseEntries>${productWiseEntries}, </#list>on ARC Basis -reg.</fo:block>
               
                <fo:block   text-align="left" font-family="Courier,monospace" white-space-collapse="false" font-size="12pt" ><fo:inline text-align="left" font-family="Courier,monospace"  font-size="12pt" font-weight="bold">&#160;&#160;&#160;     Ref :</fo:inline> 1)Tender Notification No.${allDetailsMap.get("orderId")?if_exists}, Dtd:${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(OrderHeaderDetails.get("orderDate")?if_exists, "dd-MM-yyyy")}.       </fo:block>
                  <fo:block  keep-together="always" text-align="left" font-family="Courier,monospace" white-space-collapse="false" font-size="12pt" >&#160;&#160;&#160;           2)Technical Tender opened on--  </fo:block>
                 <fo:block  keep-together="always" text-align="left" font-family="Courier,monospace" white-space-collapse="false" font-size="12pt" >&#160;&#160;&#160;           3)Commercial Tender opened on--       </fo:block>
                 <fo:block  keep-together="always" text-align="left" font-family="Courier,monospace" white-space-collapse="false" font-size="12pt" >&#160;&#160;&#160;           4)Price Negotiation held on--       </fo:block>
                 
                  <fo:block  keep-together="always" text-align="left" font-family="Verdana" white-space-collapse="false" font-size="15pt" >&#160;&#160;&#160;&#160;   &#160;&#160;&#160;&#160; &#160;&#160;&#160;&#160; &#160;&#160;&#160;&#160;                                                            *****</fo:block>
            
             <fo:block  text-align="left" font-family="Courier,monospace" white-space-collapse="false" font-size="12pt" >&#160;&#160;&#160;     Advertising to the above, this Letter Of Acceptance is hereby issued for the Supply of following items on ARC basis at the tender accepted price as follows: </fo:block>
             <fo:block   text-align="left" font-family="Courier,monospace" white-space-collapse="false" font-size="5pt" > &#160;</fo:block>
            <fo:block >
			        			 <fo:table width="100%" align="right" table-layout="fixed"  font-size="13pt">
					                <fo:table-column column-width="50pt"/>               
					                <fo:table-column column-width="200pt"/>               
						            <fo:table-column column-width="120pt"/>
						            <fo:table-column column-width="140pt"/>
			      			        <fo:table-column column-width="120pt"/> 
						           	<fo:table-body>
				                     <fo:table-row>
				                         <fo:table-cell ><fo:block text-align="center"  border-style="solid" font-weight="bold" keep-together="always">SI.No</fo:block></fo:table-cell>       		
				                     <fo:table-cell ><fo:block text-align="center" border-style="solid" font-weight="bold" keep-together="always" >Particulars</fo:block></fo:table-cell>       		
				                      <fo:table-cell ><fo:block text-align="center" border-style="solid" font-weight="bold" keep-together="always" >Quantity</fo:block></fo:table-cell>       		
				                      <fo:table-cell ><fo:block text-align="center" border-style="solid" font-weight="bold" keep-together="always" >Rate Nett/1000 Nos</fo:block></fo:table-cell>       		
				                      <fo:table-cell ><fo:block text-align="center" border-style="solid" font-weight="bold" keep-together="always" >Total Amount </fo:block></fo:table-cell>    		
				                     </fo:table-row>
				                     
			                	</fo:table-body>
			                		</fo:table>
			        	  </fo:block>	
            	<fo:block>
                 <fo:table text-align="center" >
                    <fo:table-column column-width="50pt"/>               
					 <fo:table-column column-width="200pt"/>               
					<fo:table-column column-width="120pt"/>
					 <fo:table-column column-width="140pt"/>
			      	<fo:table-column column-width="120pt"/> 
            		
                    <fo:table-body text-align="center">
                     <#assign sNo=1>
	                    
	                    <#list orderDetailsList as orderListItem>
	                    
	                  
					<#assign productId= orderListItem("productId")?if_exists >
		          <#assign productNameDetails = delegator.findOne("Product", {"productId" : productId}, true)>
		           <#if productNameDetails?has_content> 
                  	 <fo:table-row >
                	   <fo:table-cell  border-style="solid"><fo:block text-align="center"  font-size="12pt">${sNo}</fo:block></fo:table-cell>     
  				       <fo:table-cell  border-style="solid" ><fo:block text-align="left"   font-size="12pt" >${productNameDetails.get("productName")?if_exists} </fo:block></fo:table-cell>     
  				       <fo:table-cell  border-style="solid"><fo:block text-align="center"  font-size="12pt">${orderListItem.get("quantity")?if_exists}</fo:block></fo:table-cell>     
  			          <fo:table-cell   border-style="solid"><fo:block text-align="right" font-size="12pt" >Rs.${orderListItem.get("unitPrice")?if_exists?string("##0.00")}</fo:block></fo:table-cell>     
  				       <fo:table-cell   border-style="solid"><fo:block text-align="right" font-size="12pt" >Rs.${orderListItem.get("amount")?if_exists?string("##0.00")}</fo:block></fo:table-cell>     
  				         
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
               <fo:block   text-align="left" font-family="Courier,monospace" white-space-collapse="false" font-size="12pt" >The above Price is: Inclusive of all taxes and F.O.R ---.</fo:block>
                <fo:block   text-align="left" font-family="Courier,monospace" white-space-collapse="false" font-size="5pt" >&#160;&#160;</fo:block>
              <fo:block   text-align="left" font-family="Courier,monospace" white-space-collapse="false" font-size="7pt" >&#160;&#160;</fo:block>
               <fo:block   text-align="left" font-family="Courier,monospace" white-space-collapse="false" font-size="12pt" >&#160;&#160;     You are requested to finish the agreement and performance security deposit for Rs.--- in thr form of Bank Guaranty/Demand Draft down in favour of The Director, Mother Dairy within 15 days from the date of receipt of this letter as per clause No.3.0 and 3.1 under section-II of our tender Terms and Conditions.</fo:block>
                         <fo:block  keep-together="always" text-align="left" font-family="Courier,monospace" white-space-collapse="false" font-size="12pt" font-weight="bold">&#160;&#160;  </fo:block>
                          
                          
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
                          
                          
                          
                          
                          
                          
                       <#--  <fo:block  keep-together="always" text-align="left" font-family="Courier,monospace" white-space-collapse="false" font-size="12pt" >&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;                                                    Yours Faithfully,</fo:block>
                          <fo:block  keep-together="always" text-align="left" font-family="Courier,monospace" white-space-collapse="false" font-size="12pt" >&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;                                                    For Mother Dairy.</fo:block>-->
                          <fo:block  keep-together="always" text-align="left" font-family="Courier,monospace" white-space-collapse="false" font-size="12pt"   >&#160;</fo:block>
                          <fo:block  keep-together="always" text-align="left" font-family="Courier,monospace" white-space-collapse="false" font-size="12pt"   >&#160;</fo:block>
                           <fo:block  keep-together="always" text-align="left" font-family="Courier,monospace" white-space-collapse="false" font-size="14pt" font-weight="bold">&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;                                            MANAGER(PUR)              </fo:block>
                           <fo:block  keep-together="always" text-align="left" font-family="Courier,monospace" white-space-collapse="false" font-size="12pt" ><fo:inline font-weight="bold" text-decoration="underline">Encl:</fo:inline>-</fo:block>
                           <fo:block  keep-together="always" text-align="left" font-family="Courier,monospace" white-space-collapse="false" font-size="12pt" >&#160;   1.Agreement Format</fo:block>
                           <fo:block  keep-together="always" text-align="left" font-family="Courier,monospace" white-space-collapse="false" font-size="12pt" >&#160;   2.Bank Guarantee Format</fo:block>
               
		 </#if>
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
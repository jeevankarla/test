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

<#-- do not display columns associated with values specified in the request, ie constraint values -->

<fo:layout-master-set>
	<fo:simple-page-master master-name="main" page-height="12in" page-width="10in"
            margin-top="0.5in" margin-bottom=".5in" margin-left=".3in" margin-right=".5in">
        <fo:region-body margin-top="1.4in"/>
        <fo:region-before extent="1in"/>
        <fo:region-after extent="1in"/>        
    </fo:simple-page-master>   
</fo:layout-master-set>
${setRequestAttribute("OUTPUT_FILENAME", "trabs.txt")}
<#assign lineNumber = 5>
<#assign numberOfLines = 60>
<#assign facilityNumberInPage = 0>
<#assign sno = 0>
<#if boothSalesMap?has_content>	
   <#assign boothsSaleList= boothSalesMap.entrySet()>
    <#list boothsSaleList as boothSaleEntry>
<fo:page-sequence master-reference="main" font-family="Courier,monospace"  force-page-count="no-force">					
			<fo:static-content flow-name="xsl-region-before" font-family="Courier,monospace"> <#assign lineNumber = 5> 
			<fo:block  keep-together="always" text-align="left" font-family="Courier,monospace" white-space-collapse="false">UserLogin:<#if userLogin?exists>${userLogin.userLoginId?if_exists}</#if>&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;Print Date : ${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(nowTimestamp, "dd/MM/yy HH:mm:ss")}</fo:block>
			<fo:block text-align="center" keep-together="always"  white-space-collapse="false">                         UNIT : MOTHER DAIRY:G.K.V.K POST : YELAHANKA:BANGALORE : 560065</fo:block>
				<#assign facilityNumberInPage = 0>
              	<fo:block text-align="center" white-space-collapse="false"> Retailer Ledger Abstract Inclusive Of Products  From:${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(dayStart, "dd-MMMM-yyyy")} To :${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(dayEnd, "dd-MMMM-yyyy")} </fo:block>
              	<fo:block>---------------------------------------------------------------------------------------------------------------------------------</fo:block>
            	 <fo:block text-align="center" font-size="12pt">
            	 <fo:table  table-layout="fixed"   font-size="12pt">                
				                 <fo:table-column column-width="70pt"/>
 						  		<fo:table-column column-width="100pt"/>
 						  		<fo:table-column column-width="50pt"/>
 						   	    <fo:table-column column-width="90pt"/>
 						   	    <fo:table-column column-width="140pt"/>
 						   	    <fo:table-column column-width="70pt"/>
 						   	     <fo:table-column column-width="70pt"/>
				                <fo:table-body>
				                  <fo:table-row >  
  							           <fo:table-cell >
    			                            <fo:block text-align="left" white-space-collapse="false"  keep-together="always">Date</fo:block>
    		                             </fo:table-cell>                  
                            		     <fo:table-cell >
                                			<fo:block  text-align="center" white-space-collapse="false" keep-together="always">Route</fo:block>
                            			 </fo:table-cell>
                            	         <fo:table-cell >
                                	        <fo:block  text-align="left" white-space-collapse="false" keep-together="always">Shift</fo:block>
                            	          </fo:table-cell>	
		                                  <fo:table-cell >
                                	         <fo:block  white-space-collapse="false" text-align="center">Qty(In Packs)&#160;&#160;</fo:block>
                            	         </fo:table-cell>
				                      	<fo:table-cell >
	                                	         <fo:block  white-space-collapse="false" text-align="left">ProductName</fo:block>
	                            	      </fo:table-cell>
	                            	      	<fo:table-cell >
	                                	         <fo:block  white-space-collapse="false" text-align="right">Value</fo:block>
	                            	      </fo:table-cell>
	                            	      <fo:table-cell >
	                                	         <fo:block  white-space-collapse="false" text-align="right">PaidAmt</fo:block>
	                            	      </fo:table-cell>
                         	         </fo:table-row>
				                </fo:table-body>
				                 </fo:table> 
			    </fo:block>       
            	<fo:block>---------------------------------------------------------------------------------------------------------------------------------</fo:block>
			    </fo:static-content>
			<fo:flow flow-name="xsl-region-body"  font-size="12pt">
			<fo:block>	
            				 <fo:table  table-layout="fixed">                
				                <fo:table-column column-width="70pt"/>
 						  		<fo:table-column column-width="100pt"/>
 						  		<fo:table-column column-width="50pt"/>
 						   	    <fo:table-column column-width="70pt"/>
 						   	    <fo:table-column column-width="150pt"/>
 						   	    <fo:table-column column-width="70pt"/>
 						   	     <fo:table-column column-width="70pt"/>
 						   	      <fo:table-column column-width="70pt"/>
 						   	        <fo:table-column column-width="70pt"/>
				                <fo:table-body>
				                 <fo:table-row>
			                        <fo:table-cell>
			                          <fo:block text-align="left" text-indent="4pt" keep-together="always">DealerCode:</fo:block>
			                        </fo:table-cell>
			                         <#assign dealerName = Static["org.ofbiz.party.party.PartyHelper"].getPartyName(delegator, boothSaleEntry.getKey(), false)>
			                        <fo:table-cell>
			                          <fo:block text-align="right" white-space-collapse="false" >${boothSaleEntry.getKey()?if_exists?upper_case}</fo:block>
			                        </fo:table-cell>
									<fo:table-cell  number-columns-spanned="4">
			                          <fo:block text-align="left" white-space-collapse="false" >[${dealerName?if_exists}]</fo:block>
			                        </fo:table-cell>
			                      </fo:table-row>
			                     
				               <#assign boothDaySaleList= boothSaleEntry.getValue().entrySet()>
 								<#assign j=0>
				               <#list boothDaySaleList as daySaleEntry>
				               <#assign bootDetilsList= daySaleEntry.getValue().entrySet()>
			                       <fo:table-row>
			                        <fo:table-cell>
			                          <fo:block text-align="right" white-space-collapse="false" ></fo:block>
			                        </fo:table-cell>
			                        <fo:table-cell>
			                          <fo:block text-align="right" white-space-collapse="false" ></fo:block>
			                        </fo:table-cell>
			                        <fo:table-cell>
			                          <fo:block text-align="right" white-space-collapse="false" ></fo:block>
			                        </fo:table-cell>
			                        <fo:table-cell>
			                          <fo:block text-align="right" white-space-collapse="false" ></fo:block>
			                        </fo:table-cell>
			                        <fo:table-cell>
			                          <fo:block text-align="right" white-space-collapse="false" ></fo:block>
			                        </fo:table-cell>
			                        <fo:table-cell>
			                          <fo:block text-align="right" white-space-collapse="true" >OpeningBalance: </fo:block>
			                        </fo:table-cell>
			                        <fo:table-cell >
			                          <fo:block text-align="right" text-indent="4pt" keep-together="always">${daySaleEntry.getValue().get("OpeningBal")}</fo:block>
			                        </fo:table-cell>
									<fo:table-cell >
			                          <fo:block text-align="left"  white-space-collapse="false" ></fo:block>
			                        </fo:table-cell>
			                      </fo:table-row>
			                       <#list bootDetilsList as eachDayDeatils>
				                       <#if eachDayDeatils.getKey()=="AM" ||  eachDayDeatils.getKey()=="PM">
					                       <#assign  productWiseEntryList=eachDayDeatils.getValue().entrySet()>
					                       <#list productWiseEntryList as productEntry>
						                       <#assign productDeatils = delegator.findOne("Product", {"productId" : productEntry.getKey()}, true)>
												 <#assign routeId = "">
						                        <#if eachDayDeatils.getKey() == "PM">
						                        	<#assign routeId =daySaleEntry.getValue().get("PMroutes")>
						                        <#else>
						                        	<#assign routeId =daySaleEntry.getValue().get("AMroutes")>
						                        </#if> <#assign routeId = "">
						                        <#if eachDayDeatils.getKey() == "PM">
						                        	<#assign routeId =daySaleEntry.getValue().get("PMroutes")>
						                        <#else>
						                        	<#assign routeId =daySaleEntry.getValue().get("AMroutes")>
						                        </#if>
						                       <fo:table-row >
						                        <fo:table-cell>
						                          <fo:block text-align="right" >${daySaleEntry.getKey()}</fo:block>
						                        </fo:table-cell>
						                        <fo:table-cell>
						                          <fo:block text-align="center"  ><#list routeId as route>${route?if_exists}  </#list></fo:block>
						                        </fo:table-cell>
						                        <fo:table-cell>
						                          <fo:block text-align="left"   keep-together="always">${eachDayDeatils.getKey()}</fo:block>
						                        </fo:table-cell>
						                        <fo:table-cell>
						                          <fo:block text-align="right"  >${productEntry.getValue().get("packetQuantity")}&#160;&#160;</fo:block>
						                        </fo:table-cell>
						                        <fo:table-cell>
						                          <fo:block text-align="left"  >&#160;${productDeatils.get("brandName")}</fo:block>
						                        </fo:table-cell>
						                         <fo:table-cell>
						                          <fo:block text-align="right"  >${productEntry.getValue().get("totalRevenue")?string("##0.00")}</fo:block>
						                        </fo:table-cell>
						                         <fo:table-cell>
						                          <fo:block text-align="right"  ></fo:block>
						                        </fo:table-cell>
						                      </fo:table-row>
					                       </#list>
				                       </#if>
			                      </#list>
			                        <#assign totalRevenue=  daySaleEntry.getValue().get("totalRevenue")>
									 <#if shopeeInvoiceList?has_content>
									 <#if sno == 0>
								<fo:table-row>
			                        <fo:table-cell>
			                          <fo:block text-align="left" text-indent="4pt" keep-together="always">&#160;</fo:block>
			                        </fo:table-cell>
			                     </fo:table-row> 
			                      <#list shopeeInvoiceList as shopeerent>
			                       <fo:table-row>
			                        <fo:table-cell>
			                          <fo:block text-align="left" text-indent="4pt" >${shopeerent.get("description")?if_exists}</fo:block>
			                        </fo:table-cell>
			                        <fo:table-cell>
			                          <fo:block text-align="right" white-space-collapse="false" >InvoiceId: </fo:block>
			                        </fo:table-cell>
									<fo:table-cell>
			                          <fo:block text-align="right" white-space-collapse="false" >${shopeerent.get("invoiceId")?if_exists}</fo:block>
			                        </fo:table-cell>
			                        <fo:table-cell number-columns-spanned="2" >
			                          <fo:block text-align="center" white-space-collapse="false" >${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(shopeerent.get("dueDate"), "dd/MM/yy")}</fo:block>
			                        </fo:table-cell>
									<fo:table-cell>
			                          <fo:block text-align="right" white-space-collapse="false" >${shopeerent.get("amount")?string("##0.00")}</fo:block>
			                        </fo:table-cell>
									 <fo:table-cell>
			                          <fo:block text-align="right" ></fo:block>
			                        </fo:table-cell>
			                      </fo:table-row>
								</#list>
									</#if>
								</#if>
								<#if daySaleEntry.getValue().get("paymentDetails")?has_content>
			                        <#assign paymentDetails= daySaleEntry.getValue().get("paymentDetails")>
								<#list paymentDetails as payments>
 								<fo:table-row  >
			                        <fo:table-cell>
			                          <fo:block text-align="right"  ></fo:block>
			                        </fo:table-cell>
			                        <fo:table-cell>
			                          <fo:block text-align="right" >	 PaymentId :</fo:block>
			                        </fo:table-cell>
			                        <fo:table-cell>			
			                          <fo:block text-align="left"  keep-together="always">${payments.paymentId?if_exists}</fo:block>
  			                        </fo:table-cell>
			                        <fo:table-cell>
			                          <fo:block text-align="right" >&#160; </fo:block>
			                        </fo:table-cell>
			                        <fo:table-cell>
			                          <fo:block text-align="right" >${payments.paymentMethodTypeId?if_exists}</fo:block>
			                        </fo:table-cell>
		                        	 <fo:table-cell>
			                          <fo:block text-align="right" ></fo:block>
			                        </fo:table-cell>
			                        <fo:table-cell>
			                          <fo:block text-align="right" >${payments.amount?string("##0.00")}</fo:block>
			                        </fo:table-cell>
			                      </fo:table-row>
			                      </#list>
									</#if>
							<#if daySaleEntry.getValue().get("penalityDetailes")?has_content>
			                        <#assign penalityDetailes= daySaleEntry.getValue().get("penalityDetailes")>
								<#list penalityDetailes as penalityPayments>
 								<fo:table-row  >
			                        <fo:table-cell>
			                          <fo:block text-align="right"  >PenalityDetails</fo:block>
			                        </fo:table-cell>
			                        <fo:table-cell>
			                          <fo:block text-align="right" >	 PaymentId :</fo:block>
			                        </fo:table-cell>
			                        <fo:table-cell>			
			                          <fo:block text-align="left"  keep-together="always">${penalityPayments.paymentId?if_exists}</fo:block>
  			                        </fo:table-cell>
			                        <fo:table-cell>
			                          <fo:block text-align="right" >&#160; </fo:block>
			                        </fo:table-cell>
			                        <fo:table-cell>
			                          <fo:block text-align="right" ></fo:block>
			                        </fo:table-cell>
		                        	 <fo:table-cell>
			                          <fo:block text-align="right" ></fo:block>
			                        </fo:table-cell>
			                        <fo:table-cell>
			                          <fo:block text-align="right" >${penalityPayments.amount?string("##0.00")}</fo:block>
			                        </fo:table-cell>
			                      </fo:table-row>
			                      </#list>
									</#if>
			                       <fo:table-row>
			                         <fo:table-cell>
			                       <fo:block font-family="Courier,monospace" font-size="12pt">-------------------------------------------------------------------------------------------------------------------</fo:block>
 				                      </fo:table-cell>
			                      </fo:table-row>
			                        <fo:table-row  >
			                        <fo:table-cell>
			                          <fo:block text-align="right"  ></fo:block>
			                        </fo:table-cell>
			                        <fo:table-cell>
			                          <fo:block text-align="left" >Total</fo:block>
			                        </fo:table-cell>
			                        <fo:table-cell>
			                          <fo:block text-align="left"  keep-together="always"></fo:block>
			                        </fo:table-cell>
			                        <fo:table-cell>
			                          <fo:block text-align="right" ></fo:block>
			                        </fo:table-cell>
			                        <fo:table-cell>
			                          <fo:block text-align="right" ></fo:block>
			                        </fo:table-cell>
			                         <fo:table-cell>
			                          <fo:block text-align="right" >${totalRevenue?string("##0.00")}</fo:block>
			                        </fo:table-cell>
			                        <fo:table-cell>
			                          <fo:block text-align="right" >${daySaleEntry.getValue().get("PaidAmt")?string("##0.00")}</fo:block>
			                        </fo:table-cell>
			                      </fo:table-row>
			                       <fo:table-row>
			                         <fo:table-cell>
			                       <fo:block font-family="Courier,monospace" font-size="12pt">-------------------------------------------------------------------------------------------------------------------</fo:block>
 				                      </fo:table-cell>
			                      </fo:table-row>
			                       <fo:table-row>
			                        <fo:table-cell>
			                          <fo:block text-align="right" ></fo:block>
			                        </fo:table-cell>
			                        <fo:table-cell>
			                          <fo:block text-align="left" >ClosingBal </fo:block>
			                        </fo:table-cell>
			                        <fo:table-cell>
			                          <fo:block text-align="left"  >As On </fo:block>
			                        </fo:table-cell>
			                         <fo:table-cell>
			                          <fo:block text-align="right" >${daySaleEntry.getKey()}</fo:block>
			                        </fo:table-cell>
			                        <fo:table-cell>
			                          <fo:block text-align="left"  ></fo:block>
			                        </fo:table-cell>
			                           <fo:table-cell>
			                          <fo:block text-align="left"  ></fo:block>
			                        </fo:table-cell>
			                         <fo:table-cell>
			                          <fo:block text-align="right" >${daySaleEntry.getValue().get("ClosingBal")?string("##0.00")}</fo:block>
			                        </fo:table-cell>
			                      </fo:table-row>
			                      <fo:table-row>
			                         <fo:table-cell>
			                       <fo:block font-family="Courier,monospace" font-size="12pt">-------------------------------------------------------------------------------------------------------------------</fo:block>
 				                      </fo:table-cell>
			                      </fo:table-row>
			                      <#assign sno =sno+1>
 				           </#list> 
	                     </fo:table-body>
                      </fo:table>
       </fo:block>
  </fo:flow>						        	
</fo:page-sequence>
  </#list> 
 <#else>
	<fo:page-sequence master-reference="main">
    	<fo:flow flow-name="xsl-region-body" font-family="Courier,monospace">
       		 <fo:block font-size="12pt">
            	${uiLabelMap.OrderNoOrderFound}.
       		 </fo:block>
    	</fo:flow>
	</fo:page-sequence>
</#if>						
</fo:root>
</#escape>
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
	<fo:simple-page-master master-name="main" page-height="12in" page-width="15in"
            margin-top="0.1in" margin-bottom=".7in" margin-left=".5in" margin-right=".5in">
        <fo:region-body margin-top="1.8in"/>
        <fo:region-before extent="1.in"/>
        <fo:region-after extent="1.5in"/>        
    </fo:simple-page-master>   
</fo:layout-master-set>
${setRequestAttribute("OUTPUT_FILENAME", "LoanAvailedReport.pdf")}
 <#if grnList?has_content> 
	
<fo:page-sequence master-reference="main" force-page-count="no-force" font-family="Courier,monospace">					
			<fo:static-content flow-name="xsl-region-before">
			  	<#--   <fo:block white-space-collapse="false" font-size="10pt"  font-family="Helvetica" keep-together="always" >&#160;  STORE CODE:${parameters.stockId}&#160;    &#160;     &#160;  DESCRIPTION:${stockDetails.get("description")?if_exists}</fo:block> -->
			       <fo:block  keep-together="always" text-align="left"  font-family="Courier,monospace" white-space-collapse="false">&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;                                                   UserLogin: <#if userLogin?exists>${userLogin.userLoginId?if_exists}</#if></fo:block>
		   <fo:block  keep-together="always" text-align="left" font-family="Courier,monospace" white-space-collapse="false">&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;                                               Date     : ${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(nowTimestamp, "dd/MM/yy HH:mm:ss")}</fo:block> 
		       
					
					<fo:block  keep-together="always" text-align="left" font-family="Courier,monospace" white-space-collapse="false" font-size="12pt" font-weight="bold" >&#160;&#160;&#160;&#160;&#160;&#160;                  KARNATAKA CO-OPERATIVE MILK PRODUCERS FEDERATION LTD. </fo:block>
				  	<fo:block  keep-together="always" text-align="left" font-family="Courier,monospace" white-space-collapse="false" font-size="12pt" font-weight="bold">&#160;&#160;&#160;&#160;&#160;&#160;                UNIT : MOTHER DAIRY:G.K.V.K POST : YELAHANKA:BANGALORE : 560065            ACCOUNTS / PURCHASE / STORES   </fo:block>
			        <fo:block  keep-together="always" text-align="left" font-family="Courier,monospace" white-space-collapse="false" font-size="10pt" >&#160;&#160;&#160;&#160;&#160;              -------------------------------------------------------------------------------  </fo:block> 
				    <fo:block  keep-together="always" text-align="left" font-family="Courier,monospace" white-space-collapse="false" font-size="12pt" font-weight="bold">&#160;&#160;&#160;&#160;&#160;                                                            MATERIAL RECEIPT REPORT 	    </fo:block> 
				              	<fo:block   text-align="left" font-size="12pt" white-space-collapse="false" >&#160;&#160;  Shipment Seq Id : ${shipmentMap.get("shipmentSequenceId")?if_exists}   </fo:block>
				    
				    <fo:block  keep-together="always" text-align="left" font-family="Courier,monospace" white-space-collapse="false" font-size="12pt" >&#160;&#160;&#160;&#160;&#160;   M R R NO:${shipmentMap.get("receiptId")}                  DATE:  ${shipmentMap.get("dateReceived")?if_exists}                                                             PRINT DATE:Date:  ${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(nowTimestamp, "dd-MMM-yy")} 	    </fo:block> 
			        <fo:block  keep-together="always" text-align="left" font-family="Courier,monospace" white-space-collapse="false" font-size="10pt" > &#160;&#160;&#160;&#160;&#160;______________________________________________________________________________________________________________________________________________________________ </fo:block> 
				 <#--   <fo:block  keep-together="always" text-align="left" font-family="Courier,monospace" white-space-collapse="false" font-size="10pt" > P O NO : ${shipmentMap.get("ordId")?if_exists}                    P O DATE: ${shipmentMap.get("dateReceived")?if_exists}                VENDOR CODE: ${shipmentMap.get("partyId")?if_exists}                                   STORE: ${shipmentMap.get("store")?if_exists?if_exists}   	    </fo:block> 
					<fo:block  keep-together="always" text-align="center" font-family="Courier,monospace" white-space-collapse="false" font-size="10pt"> &#160;  </fo:block> 
			        <fo:block  keep-together="always" text-align="left" font-family="Courier,monospace" white-space-collapse="false" font-size="10pt" > D C NO: ${shipmentMap.get("dcNo")?if_exists}                           D C DATE:<#if (shipmentMap.get("dcDate")?has_content)> ${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(shipmentMap.get("dcDate"), "dd-MMM-yy")?if_exists}   </#if>                          VENDOR NAME: ${shipmentMap.get("partyName")?if_exists}            	    </fo:block> 
             		<fo:block  keep-together="always" text-align="center" font-family="Courier,monospace" white-space-collapse="false" font-size="10pt" > &#160;&#160;  </fo:block> 
			        <fo:block  keep-together="always" text-align="left" font-family="Courier,monospace" white-space-collapse="false" font-size="10pt" > INVOICE NO: ${shipmentMap.get("invoiceNo")?if_exists}                   INVOICE DATE: <#if (shipmentMap.get("invoiceDate")?has_content)>${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(shipmentMap.get("invoiceDate"), "dd-MMM-yy")?if_exists}    </#if>                                                   </fo:block> 
			           
--> 
 </fo:static-content>
 <fo:flow flow-name="xsl-region-body"   font-family="Courier,monospace">		

			 <fo:block  keep-together="always" text-align="left" font-family="Courier,monospace" white-space-collapse="false" font-size="10pt" > &#160;&#160; </fo:block> 
			 <fo:block  keep-together="always" text-align="left" font-family="Courier,monospace" white-space-collapse="false" font-size="10pt" > &#160;&#160; </fo:block> 

          	<fo:block   text-align="left" font-size="12pt" white-space-collapse="false" >&#160;&#160;PO SEQ ID : ${shipmentMap.get("sequenceId")?if_exists}   </fo:block>

  <fo:block>
	                 	<fo:table >
	                    <fo:table-column column-width="100pt"/>
	                    <fo:table-column column-width="150pt"/>
	                    <fo:table-column column-width="100pt"/>  
	               	    <fo:table-column column-width="120pt"/>
	               	    <fo:table-column column-width="100pt"/>
	            		<fo:table-column column-width="250pt"/> 
	            		<fo:table-column column-width="60pt"/> 		
	            		<fo:table-column column-width="130pt"/> 		
	                    <fo:table-body>
	                    <fo:table-row >
	                    		<fo:table-cell >
                            		<fo:block   text-align="left" font-size="12pt" white-space-collapse="false" >&#160; P O NO    : </fo:block>  
                       			</fo:table-cell>                     
	                    		<fo:table-cell >
                            		<fo:block   text-align="left" font-size="12pt" white-space-collapse="false" >${shipmentMap.get("orderNo")?if_exists}   </fo:block>
                       			</fo:table-cell>
	                    		<fo:table-cell >
                            		<fo:block   text-align="right" font-size="12pt" white-space-collapse="false"> P O DATE    :</fo:block> 
                       			</fo:table-cell>
	                    		<fo:table-cell >
                            		<fo:block   text-align="left" font-size="12pt" white-space-collapse="false" >&#160;${shipmentMap.get("dateReceived")?if_exists}</fo:block>  
                       			</fo:table-cell>
	                    		<fo:table-cell >
                            		<fo:block   text-align="right" font-size="12pt" white-space-collapse="false" > VENDOR CODE:</fo:block>  
                       			</fo:table-cell>
	                    		<fo:table-cell >
                            		<fo:block   text-align="left" font-size="12pt" white-space-collapse="false" >&#160;${shipmentMap.get("partyId")}      </fo:block>  
                       			</fo:table-cell>
	                    		<fo:table-cell >
                            		<fo:block   text-align="right" font-size="12pt" white-space-collapse="false" > STORE:</fo:block>  
                       			</fo:table-cell>
	                    		<fo:table-cell >
                            		<fo:block   text-align="left" font-size="12pt" white-space-collapse="false" > &#160;${shipmentMap.get("store")?if_exists} </fo:block>  
                       			</fo:table-cell>	                    		
                			</fo:table-row>
                			<fo:table-row >
	                    		<fo:table-cell >
                            		<fo:block   text-align="left" font-size="12pt" white-space-collapse="false" >&#160; D C NO    : </fo:block>  
                       			</fo:table-cell>                     
	                    		<fo:table-cell >
                            		<fo:block   text-align="left" font-size="12pt" white-space-collapse="false" >${shipmentMap.get("dcNo")?if_exists}   </fo:block>  
                       			</fo:table-cell>
	                    		<fo:table-cell >
                            		<fo:block   text-align="right" font-size="12pt" white-space-collapse="false"> D C DATE    :</fo:block> 
                       			</fo:table-cell>
	                    		<fo:table-cell >
                            		<fo:block   text-align="left" font-size="12pt" white-space-collapse="false" >&#160;<#if (shipmentMap.get("dcDate")?has_content)>${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(shipmentMap.get("dcDate"), "dd-MMM-yy")?if_exists}   </#if> </fo:block>  
                       			</fo:table-cell>
	                    		<fo:table-cell >
                            		<fo:block   text-align="right" font-size="12pt" white-space-collapse="false" >VENDOR NAME: </fo:block>  
                       			</fo:table-cell>
	                    		<fo:table-cell >
                            		<fo:block   text-align="left" font-size="12pt" white-space-collapse="false" >&#160;${shipmentMap.get("partyName")?if_exists}      </fo:block>  
                       			</fo:table-cell>
                			</fo:table-row>
                        <fo:table-row >
	                    		<fo:table-cell >
                            		<fo:block   text-align="left" font-size="12pt" white-space-collapse="false" >&#160; INVOICE NO: </fo:block>  
                       			</fo:table-cell>                     
	                    		<fo:table-cell >
                            		<fo:block   text-align="left" font-size="12pt" white-space-collapse="false" >${shipmentMap.get("invoiceNo")?if_exists}   </fo:block>
                       			</fo:table-cell>
	                    		<fo:table-cell >
                            		<fo:block   text-align="right" font-size="12pt" white-space-collapse="false"> INVOICE DATE: </fo:block> 
                       			</fo:table-cell>
	                    		<fo:table-cell >
                            		<fo:block   text-align="left" font-size="12pt" white-space-collapse="false" >&#160;<#if (shipmentMap.get("invoiceDate")?has_content)>${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(shipmentMap.get("invoiceDate"), "dd-MMM-yy")?if_exists}    </#if> </fo:block>  
                       			</fo:table-cell>
	                    		                   		
                			</fo:table-row>
                    </fo:table-body>
                </fo:table>
              </fo:block>               	

   	    		
          			 <fo:block  keep-together="always" text-align="left" font-family="Courier,monospace" white-space-collapse="false" font-size="10pt" > &#160;&#160; </fo:block> 

            	<fo:block>
                 	<fo:table border-style="solid">
	                    <fo:table-column column-width="30pt"/>
	                    <fo:table-column column-width="60pt"/>
	                    <fo:table-column column-width="140pt"/>  
	               	    <fo:table-column column-width="70pt"/>
	               	    <fo:table-column column-width="80pt"/>
	            		<fo:table-column column-width="80pt"/> 		
	            		<fo:table-column column-width="50pt"/>
	            		<fo:table-column column-width="80pt"/>
	            		<fo:table-column column-width="80pt"/>
	            		<fo:table-column column-width="80pt"/>
	            		<fo:table-column column-width="70pt"/>
	            		<fo:table-column column-width="80pt"/>
	            		<fo:table-column column-width="70pt"/>
	            		<fo:table-column column-width="70pt"/>
	                    <fo:table-body>
	                    <#assign sNo=1>
	                    <#list grnList as grnListItem>
 <fo:table-row >
	                    		<fo:table-cell border-style="solid">
                            		<fo:block   text-align="center" font-size="10pt" white-space-collapse="false" font-weight="bold">SI NO</fo:block>  
                       			</fo:table-cell>                     
                       			<fo:table-cell border-style="solid">
                            		<fo:block   text-align="center" font-size="10pt" white-space-collapse="false" font-weight="bold">MTRL Code </fo:block>
                       			</fo:table-cell>
                       			<fo:table-cell border-style="solid">
                            		<fo:block   text-align="center" font-size="10pt" white-space-collapse="false" font-weight="bold">MATERIAL Name</fo:block> 
                       			</fo:table-cell>
                       			<fo:table-cell border-style="solid">
                            		<fo:block   text-align="center" font-size="10pt" white-space-collapse="false" font-weight="bold">UNIT</fo:block>  
                       			</fo:table-cell>
                       			<fo:table-cell border-style="solid">
                            		<fo:block   text-align="center" font-size="10pt" white-space-collapse="false" font-weight="bold">P O QTY</fo:block>  
                       			</fo:table-cell>
                       			<fo:table-cell border-style="solid">
                            		<fo:block   text-align="center" font-size="10pt" white-space-collapse="false" font-weight="bold">DC/INVOICE QTY</fo:block>  
                       			</fo:table-cell>
                        		<fo:table-cell border-style="solid">
                            		<fo:block   text-align="center" font-size="10pt" white-space-collapse="false" font-weight="bold">RECEIVED QTY</fo:block>   
                        		</fo:table-cell>
                        		<fo:table-cell border-style="solid">
                            		<fo:block   text-align="center" font-size="10pt" white-space-collapse="false" font-weight="bold">REJECTED QTY </fo:block>  
                        		</fo:table-cell>
                        		<fo:table-cell border-style="solid">
                            		<fo:block  text-align="center" font-size="10pt" white-space-collapse="false" font-weight="bold">ACCEPTED QTY</fo:block>  
                        		</fo:table-cell>
                        		<fo:table-cell border-style="solid">
                            		<fo:block   text-align="center" font-size="10pt" white-space-collapse="false" font-weight="bold">UNIT RATE Rs </fo:block>  
                       			</fo:table-cell>
                       			<fo:table-cell border-style="solid">
                            		<fo:block   text-align="center" font-size="10pt" white-space-collapse="false" font-weight="bold">BOOK FOLOIO NO </fo:block>  
                       			</fo:table-cell>
                        		<fo:table-cell border-style="solid">
                            		<fo:block   text-align="center" font-size="10pt" white-space-collapse="false" font-weight="bold">AMOUNT Rs </fo:block>   
                        		</fo:table-cell>
                        		<fo:table-cell border-style="solid">
                            		<fo:block   text-align="center" font-size="10pt" white-space-collapse="false" font-weight="bold">VEHICLE NO</fo:block>  
                        		</fo:table-cell>
                        		<fo:table-cell border-style="solid">
                            		<fo:block  text-align="center" font-size="10pt" white-space-collapse="false" font-weight="bold">REMARKS</fo:block>  
                        		</fo:table-cell>
                        		
                			</fo:table-row>
	                    <fo:table-row >
									<fo:table-cell border-style="solid">
	                            	 <fo:block  text-align="center"  font-size="10pt" >
                                   ${sNo} </fo:block>                 			  
	                       			</fo:table-cell>
                                	<fo:table-cell border-style="solid" >
                                  	<fo:block text-align="center"  font-size="10pt" >
                                   ${grnListItem.get("internalName")?if_exists} </fo:block>                 			  
	                                </fo:table-cell>
                                 	<fo:table-cell border-style="solid">
                                  	<fo:block text-align="center"  font-size="10pt" >
                                   ${grnListItem.get("description")?if_exists} </fo:block>                 			  
	                                </fo:table-cell>
	                       			<fo:table-cell border-style="solid">
                                     <fo:block text-align="center" font-size="10pt" >
                                   ${grnListItem.get("unit")?if_exists} 
                                    </fo:block>
	                                </fo:table-cell>
	                                <fo:table-cell border-style="solid">
                                    <fo:block text-align="center" font-size="10pt" >
                                   ${grnListItem.get("quantity")?if_exists}
                                    </fo:block>
	                                </fo:table-cell>
	                                <fo:table-cell border-style="solid">
	                                    <fo:block text-align="center" font-size="10pt">
	                               ${grnListItem.get("deliveryChallanQty")?if_exists}
	                                    </fo:block>
	                                </fo:table-cell>
	                                <fo:table-cell border-style="solid">
	                                    <fo:block text-align="center" font-size="10pt">
                                   ${grnListItem.get("receivedQty")?if_exists}
	                                    </fo:block>
	                                </fo:table-cell>
	                                <fo:table-cell border-style="solid">
	                                   <fo:block text-align="center" font-size="10pt">
	                               <#if (grnListItem.get("quantityRejected")?has_content)>
                                   ${grnListItem.get("quantityRejected")?if_exists}<#else>0</#if>
	                                    </fo:block>
	                                </fo:table-cell>
	                                <fo:table-cell border-style="solid">
	                                    <fo:block text-align="center" font-size="10pt">
	                               <#if (grnListItem.get("quantityAccepted")?has_content)>
	                               ${grnListItem.get("quantityAccepted")?if_exists}<#else>0</#if>
	                                    </fo:block>
	                                </fo:table-cell>
	                               <fo:table-cell border-style="solid">
	                                    <fo:block text-align="center" font-size="10pt">
	                               <#if (grnListItem.get("unitPrice")?has_content)>
                                    ${(grnListItem.get("unitPrice"))?if_exists?string("##0.00")}<#else>0.00</#if>
	                                    </fo:block>
	                                </fo:table-cell>
	                               <fo:table-cell border-style="solid">
	                                    <fo:block text-align="left" font-size="10pt">
	                                      ${grnListItem.get("folioNo")?if_exists}

	                                    </fo:block>
	                                </fo:table-cell>
	                               <fo:table-cell border-style="solid">
	                                    <fo:block text-align="right" font-size="10pt">
	                                  ${grnListItem.get("amount")?if_exists?string("##0.00")}
	                                    </fo:block>
	                                </fo:table-cell>
	                               <fo:table-cell border-style="solid">
	                                    <fo:block text-align="center" font-size="10pt">
	                                    ${grnListItem.get("vehicleId")?if_exists}
 	                                    </fo:block>
	                                </fo:table-cell>
	                               <fo:table-cell border-style="solid">
	                                    <fo:block text-align="center" font-size="10pt">
	                                    
	                                    </fo:block>
	                                </fo:table-cell>
	                              	<#assign sNo=sNo+1>
	                               </fo:table-row>
                                </#list>
                    </fo:table-body>
                </fo:table>
               </fo:block> 
                               		
                        
            <fo:block  keep-together="always" text-align="center" font-family="Courier,monospace" white-space-collapse="false" font-size="10pt" font-weight="bold"> &#160;&#160;  </fo:block> 
			<fo:block  keep-together="always" text-align="left" font-family="Courier,monospace" white-space-collapse="false" font-size="12pt" > Tax Details:                                                                                            TOTAL VALUE:${shipmentMap.get("total")?if_exists?string("##0.00")}     			</fo:block>  
			<fo:block  keep-together="always" text-align="center" font-family="Courier,monospace" white-space-collapse="false" font-size="10pt" font-weight="bold"> &#160;&#160;  </fo:block> 
			 <fo:block  keep-together="always" text-align="center" font-family="Courier,monospace" white-space-collapse="false" font-size="10pt" font-weight="bold"> &#160;&#160;  </fo:block> 
			 <fo:block  keep-together="always" text-align="left" font-family="Courier,monospace" white-space-collapse="false" font-size="10pt" font-weight="bold"> &#160;&#160; Apply to:  </fo:block> 
			 <fo:block  keep-together="always" text-align="center" font-family="Courier,monospace" white-space-collapse="false" font-size="10pt" font-weight="bold"> &#160;&#160;  </fo:block> 
			 <fo:block  keep-together="always" text-align="center" font-family="Courier,monospace" white-space-collapse="false" font-size="10pt" font-weight="bold"> &#160;&#160;  </fo:block> 
				
				<fo:block>
                 	<fo:table>
	                    <fo:table-column column-width="250pt"/>
	                    	                    <fo:table-column column-width="50pt"/>
	                    
	                    <fo:table-column column-width="250pt"/>
	                    	                    <fo:table-column column-width="50pt"/>
	                    
	                    <fo:table-column column-width="250pt"/>  
	                    
	                    <fo:table-column column-width="250pt"/>  
	                    <fo:table-body>
	                    <fo:table-row >
	                               <fo:table-cell >
	                                    <fo:block text-align="left" font-size="11pt">
	                             	 1.The materials at 
                                  	Sl.no......................... were 
								  	inspected and found as per and 
									specification.Hence accepted
									2.Remaining items are rejected for the 
									following reasons .................    &#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;
	                                    </fo:block>
	                                </fo:table-cell>
	                                <fo:table-cell >
	                                    <fo:block text-align="left" font-size="11pt"> &#160;&#160;</fo:block>
	                                </fo:table-cell>
	                               <fo:table-cell >
	                                    <fo:block text-align="left" font-size="11pt">
	                                 The materials at Sl No.............. 
								  	above have been received and taken into 
									stock , vide ledgerfolio of each item .
	                                    </fo:block>
	                                </fo:table-cell>
	                                  <fo:table-cell >
	                                    <fo:block text-align="left" font-size="11pt"> &#160;&#160; </fo:block>  </fo:table-cell>
	                               <fo:table-cell >
	                                    <fo:block text-align="left" font-size="11pt">
	                               Verified with the ledgerfolio for having 
									taken into stock account.
	                                    </fo:block>
	                                </fo:table-cell>
                          </fo:table-row>
                          <fo:table-row >
	                               <fo:table-cell>
	                                    <fo:block text-align="center" font-size="11pt">
										&#160;&#160;&#160;&#160;	                        
							            </fo:block>
	                                </fo:table-cell>
	                               <fo:table-cell >
	                                    <fo:block text-align="center" font-size="11pt">
	                                 &#160;&#160;&#160;&#160;
	                                    </fo:block>
	                                </fo:table-cell>
	                               <fo:table-cell>
	                                    <fo:block text-align="center" font-size="11pt">
	                               &#160;&#160;&#160;&#160;
	                                    </fo:block>
	                                </fo:table-cell>
                           </fo:table-row>
                           <fo:table-row >
	                               <fo:table-cell >
	                                    <fo:block text-align="center" font-size="11pt">
										&#160;&#160;&#160;&#160;	                        
							            </fo:block>
	                                </fo:table-cell>
	                               <fo:table-cell >
	                                    <fo:block text-align="center" font-size="11pt">
	                                 &#160;&#160;&#160;&#160;
	                                    </fo:block>
	                                </fo:table-cell>
	                               <fo:table-cell>
	                                    <fo:block text-align="center" font-size="11pt">
	                               &#160;&#160;&#160;&#160;
	                                    </fo:block>
	                                </fo:table-cell>
                           </fo:table-row>                    
                           <fo:table-row >
	                               <fo:table-cell >
	                                    <fo:block text-align="left" font-size="11pt">
	                             &#160;&#160;&#160;&#160;  CERTIFIED BY 
	                                    </fo:block>
	                                </fo:table-cell>
	                                 <fo:table-cell >
	                                    <fo:block text-align="left" font-size="11pt"> &#160;&#160;</fo:block>
	                                </fo:table-cell>
	                               <fo:table-cell >
	                                    <fo:block text-align="center" font-size="11pt">
	                                    RECEIVED BY
	                                    </fo:block>
	                                </fo:table-cell>
	                                 <fo:table-cell >
	                                    <fo:block text-align="left" font-size="11pt"> &#160;&#160;</fo:block>
	                                </fo:table-cell>
	                               <fo:table-cell >
	                                    <fo:block text-align="center" font-size="11pt">
	                                STORES OFFICER
	                                    </fo:block>
	                                </fo:table-cell>
                          </fo:table-row>
                    </fo:table-body>
                </fo:table>
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
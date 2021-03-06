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
			<fo:simple-page-master master-name="main" page-height="12in" page-width="15in"
					 margin-left="0.2in" margin-right="0.2in"  margin-top="0.2in" margin-bottom="0.2in" >
				<fo:region-body margin-top="0.3in"/>
				<fo:region-before extent="1in"/>
				<fo:region-after extent="1in"/>
			</fo:simple-page-master>
		</fo:layout-master-set>
		<#if sortedMap?has_content>
       <fo:page-sequence master-reference="main">
		    <fo:static-content font-size="13pt" font-family="Courier,monospace"  flow-name="xsl-region-before" font-weight="bold">	 				       		
			   <fo:block  keep-together="always" text-align="right" font-family="Courier,monospace" font-size="10pt" white-space-collapse="false">&#160;${uiLabelMap.CommonPage}- <fo:page-number/> </fo:block>			
            </fo:static-content>
            <fo:flow flow-name="xsl-region-body"   font-family="Courier,monospace">	
                <fo:block  keep-together="always" text-align="right" font-family="Courier,monospace" white-space-collapse="false">    UserLogin : <#if userLogin?exists>${userLogin.userLoginId?if_exists}</#if></fo:block>
				<fo:block  keep-together="always" text-align="right" font-family="Courier,monospace" white-space-collapse="false">&#160;      Date:${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(nowTimestamp, "dd/MM/yy HH:mm:ss")}</fo:block>
                <#assign reportHeader = delegator.findOne("TenantConfiguration", {"propertyTypeEnumId" : "COMPANY_HEADER","propertyName" : "reportHeaderLable"}, true)>
                <#assign reportSubHeader = delegator.findOne("TenantConfiguration", {"propertyTypeEnumId" : "COMPANY_HEADER","propertyName" : "reportSubHeaderLable"}, true)>
                <fo:block  keep-together="always" text-align="center" font-weight = "bold" font-family="Courier,monospace" white-space-collapse="false">${reportHeader.description?if_exists}</fo:block>
				<fo:block  keep-together="always" text-align="center" font-family="Courier,monospace" white-space-collapse="false" font-weight="bold">${reportSubHeader.description?if_exists}</fo:block>
				<fo:block text-align="center" keep-together="always"  >&#160;------------------------------------------------------------------------------------------</fo:block>
			    <fo:block  keep-together="always" text-align="center" font-family="Courier,monospace" white-space-collapse="false" font-size="12pt" > &#160;&#160;  </fo:block>
				<fo:block text-align="center" white-space-collapse="false" font-size="12pt"  font-weight="bold" >&#160;   ABSTRACT OF THE STORE RECEIPT-ISSUE BETWEEN ${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(fromDate, "dd-MMM-yyyy")} AND ${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(thruDate, "dd-MMM-yyyy")} </fo:block>                
				<fo:block linefeed-treatment="preserve">&#xA;</fo:block>
				<fo:block linefeed-treatment="preserve">&#xA;</fo:block>
			    <fo:block text-align="left" white-space-collapse="false">&#160;&#160;LEDGER FOLIO NO.:<#if ledgerFolioNo?has_content>${ledgerFolioNo}<#else></#if>                                                                                   STORE: ${facilityId?if_exists}</fo:block>                
			    <fo:block linefeed-treatment="preserve">&#xA;</fo:block>				
                <fo:block>
                   <fo:table border-style="solid">
                       <fo:table-column column-width="20pt"/>                      
					   <fo:table-column column-width="50pt"/>
					   <fo:table-column column-width="120pt"/>
					   <fo:table-column column-width="30pt"/>
					   <fo:table-column column-width="180pt"/>
					   <fo:table-column column-width="150pt"/>
					   <fo:table-column column-width="200pt"/>
					   <fo:table-column column-width="130pt"/>
					   <fo:table-column column-width="170pt"/>
					   <fo:table-body> 
					       <fo:table-row height="30pt">
					           <fo:table-cell border-style="solid">
					                     <fo:block text-align="center" padding-before="0.6cm" font-weight="bold" >NO</fo:block>
					           </fo:table-cell>
					           <fo:table-cell border-style="solid">
					                     <fo:block text-align="center" padding-before="0.6cm" font-weight="bold" >ITEM CODE</fo:block>
					           </fo:table-cell>
					           <fo:table-cell border-style="solid">
					                     <fo:block text-align="center" padding-before="0.6cm" font-weight="bold"  >DESCRIPTION</fo:block>
					           </fo:table-cell>
					           <fo:table-cell border-style="solid">
					                     <fo:block text-align="center" padding-before="0.6cm" font-weight="bold"  >UNIT</fo:block>
					           </fo:table-cell>
					           <fo:table-cell border-style="solid">
					                     <fo:block>
						                   <fo:table border-style="solid">
											   <fo:table-column column-width="90pt"/>
											   <fo:table-column column-width="90pt"/>
											   <fo:table-body> 
											      <fo:table-row height="25pt">
											           <fo:table-cell border-style="solid" number-columns-spanned="2">
											                     <fo:block text-align="center"  font-weight="bold"  >OPENING BALANCE</fo:block>
											           </fo:table-cell>
											      </fo:table-row>
											      <fo:table-row height="25pt">
											           <fo:table-cell border-style="solid">
											                     <fo:block text-align="left"  font-weight="bold"  >QTY</fo:block>
											           </fo:table-cell>
											           <fo:table-cell border-style="solid">
											                     <fo:block text-align="left"  font-weight="bold"  >VALUE</fo:block>
											           </fo:table-cell>
											      </fo:table-row>
											    </fo:table-body>  
											    </fo:table>
										 </fo:block> 
					           </fo:table-cell>
                               <fo:table-cell border-style="solid">
					                      <fo:block>
						                   <fo:table border-style="solid">
											   <fo:table-column column-width="75pt"/>
											   <fo:table-column column-width="75pt"/>
											   <fo:table-body> 
											      <fo:table-row height="25pt">
											           <fo:table-cell border-style="solid" number-columns-spanned="2">
											                     <fo:block text-align="center"  font-weight="bold"  >RECEIPTS</fo:block>
											           </fo:table-cell>
											      </fo:table-row>
											      <fo:table-row height="25pt">
											           <fo:table-cell border-style="solid">
											                     <fo:block text-align="left"  font-weight="bold"  >QTY</fo:block>
											           </fo:table-cell>
											           <fo:table-cell border-style="solid">
											                     <fo:block text-align="left"  font-weight="bold"  >VALUE</fo:block>
											           </fo:table-cell>
											      </fo:table-row>
											    </fo:table-body>  
											    </fo:table>
										 </fo:block> 
					           </fo:table-cell>
					          <fo:table-cell border-style="solid">
					                      <fo:block>
						                   <fo:table border-style="solid">
											   <fo:table-column column-width="100pt"/>
											   <fo:table-column column-width="100pt"/>
											   <fo:table-body> 
											      <fo:table-row height="25pt">
											           <fo:table-cell border-style="solid" number-columns-spanned="2">
											                     <fo:block text-align="center"  font-weight="bold"  >TOTAL</fo:block>
											           </fo:table-cell>
											      </fo:table-row>
											      <fo:table-row height="25pt">
											           <fo:table-cell border-style="solid">
											                     <fo:block text-align="left"  font-weight="bold"  >QTY</fo:block>
											           </fo:table-cell>
											           <fo:table-cell border-style="solid">
											                     <fo:block text-align="left"  font-weight="bold"  >VALUE</fo:block>
											           </fo:table-cell>
											      </fo:table-row>
											    </fo:table-body>  
											    </fo:table>
										 </fo:block> 
					           </fo:table-cell>
					           <fo:table-cell border-style="solid">
					                      <fo:block>
						                   <fo:table border-style="solid">
											   <fo:table-column column-width="65pt"/>
											   <fo:table-column column-width="65pt"/>
											   <fo:table-body> 
											      <fo:table-row height="25pt">
											           <fo:table-cell border-style="solid" number-columns-spanned="2">
											                     <fo:block text-align="center"  font-weight="bold"  >ISSUES</fo:block>
											           </fo:table-cell>
											      </fo:table-row>
											      <fo:table-row height="25pt">
											           <fo:table-cell border-style="solid">
											                     <fo:block text-align="left"  font-weight="bold"  >QTY</fo:block>
											           </fo:table-cell>
											           <fo:table-cell border-style="solid">
											                     <fo:block text-align="left"  font-weight="bold"  >VALUE</fo:block>
											           </fo:table-cell>
											      </fo:table-row>
											    </fo:table-body>  
											    </fo:table>
										 </fo:block> 
					           </fo:table-cell>
					           <fo:table-cell border-style="solid">
					                     <fo:block>
						                   <fo:table border-style="solid">
											   <fo:table-column column-width="85pt"/>
											   <fo:table-column column-width="85pt"/>
											   <fo:table-body> 
											      <fo:table-row height="25pt">
											           <fo:table-cell border-style="solid" number-columns-spanned="2">
											                     <fo:block text-align="center"  font-weight="bold"  >CLOSING BALANCE</fo:block>
											           </fo:table-cell>
											      </fo:table-row>
											      <fo:table-row height="25pt">
											           <fo:table-cell border-style="solid">
											                     <fo:block text-align="left"  font-weight="bold"  >QTY</fo:block>
											           </fo:table-cell>
											           <fo:table-cell border-style="solid">
											                     <fo:block text-align="left"  font-weight="bold"  >VALUE</fo:block>
											           </fo:table-cell>
											      </fo:table-row>
											    </fo:table-body>  
											    </fo:table>
										 </fo:block> 
					           </fo:table-cell>
                            <#--   <fo:table-cell border-style="solid">
					                      <fo:block>
						                   <fo:table border-style="solid">
											   <fo:table-column column-width="55pt"/>
											   <fo:table-column column-width="55pt"/>
											   <fo:table-body> 
											      <fo:table-row height="25pt">
											           <fo:table-cell border-style="solid" number-columns-spanned="2">
											                     <fo:block text-align="center"  font-weight="bold"  >PHYSICAL</fo:block>
											           </fo:table-cell>
											      </fo:table-row>
											      <fo:table-row height="25pt">
											           <fo:table-cell border-style="solid">
											                     <fo:block text-align="left"  font-weight="bold"  >QTY</fo:block>
											           </fo:table-cell>
											           <fo:table-cell border-style="solid">
											                     <fo:block text-align="left"  font-weight="bold"  >VALUE</fo:block>
											           </fo:table-cell>
											      </fo:table-row>
											    </fo:table-body>  
											    </fo:table>
										 </fo:block> 
					           </fo:table-cell>
					      <fo:table-cell border-style="solid">
					                     <fo:block text-align="center"  font-weight="bold" padding-before="0.6cm">REMARKS</fo:block>
					           </fo:table-cell> -->
					       </fo:table-row>
				      </fo:table-body>
				   </fo:table>
			   </fo:block> 
			   <fo:block>
                   <fo:table border-style="solid">
                       <fo:table-column column-width="20pt"/>
					   <fo:table-column column-width="50pt"/>
					   <fo:table-column column-width="120pt"/>
					   <fo:table-column column-width="30pt"/>
					   <fo:table-column column-width="90pt"/>
					   <fo:table-column column-width="90pt"/>					   
					   <fo:table-column column-width="75pt"/>
					   <fo:table-column column-width="75pt"/>
					   <fo:table-column column-width="100pt"/>
					   <fo:table-column column-width="100pt"/>
					   <fo:table-column column-width="65pt"/>
					   <fo:table-column column-width="65pt"/>
                       <fo:table-column column-width="85pt"/>
                       <fo:table-column column-width="85pt"/>
					   <fo:table-body> 
					        <#assign sno=1>	
					        <#assign grandOpenTotal=0>
                            <#assign grandReceiptTotal=0>
                            <#assign grandIssueTotal=0>  
                            <#assign finalOpenTotal=0>
                            <#assign finalClosingTotal=0>  
                           <#assign productList = sortedMap.entrySet()>
                           <#list productList as productEntry>
                           <#assign productd=productEntry.getKey()> 
                           <#assign productDetails=productEntry.getValue()> 
                           <#if productDetails.get("openingTot")?has_content><#assign grandOpenTotal=grandOpenTotal+productDetails.get("openingTot")></#if>                           
                           <#if productDetails.get("ReceiptAmount")?has_content><#assign grandReceiptTotal=grandReceiptTotal+productDetails.get("ReceiptAmount")></#if>
                           <#if productDetails.get("IssueAmount")?has_content><#assign grandIssueTotal=grandIssueTotal+productDetails.get("IssueAmount")></#if>  
					       <fo:table-row height="30pt">
					            <fo:table-cell border-style="solid">
									    <fo:block text-align="left" keep-together="always" font-size="10pt" >${sno?if_exists}</fo:block>
								     </fo:table-cell>
					            <fo:table-cell border-style="solid">
					                     <fo:block text-align="left" font-size="10pt" >${productDetails.get("itemCode")?if_exists}</fo:block>
					           </fo:table-cell>	
                               <fo:table-cell border-style="solid">
					                     <fo:block text-align="left" font-size="10pt" white-space-collapse="false">${productDetails.get("description")?if_exists}</fo:block>
					           </fo:table-cell>	
                               <fo:table-cell border-style="solid">
					                     <fo:block text-align="center"  font-size="10pt" >${productDetails.get("unit")?if_exists}</fo:block>
					           </fo:table-cell>
                                <fo:table-cell border-style="solid">
								    <fo:block text-align="right"  font-size="10pt" ><#if productDetails.get("openingQty")?has_content>${productDetails.get("openingQty")?if_exists}<#else>0</#if></fo:block>
							   </fo:table-cell>
							   <fo:table-cell border-style="solid">
								   <fo:block text-align="right" font-size="10pt"  ><#if productDetails.get("openingTot")?has_content>${productDetails.get("openingTot")?if_exists?string("##0.00")?if_exists}<#else>0.00</#if></fo:block>
							  </fo:table-cell>
                              <fo:table-cell border-style="solid">
								   <fo:block text-align="right" font-size="10pt"  ><#if productDetails.get("ReceiptQty")?has_content> ${productDetails.get("ReceiptQty")?if_exists}<#else>0</#if></fo:block>
							  </fo:table-cell>
							  <fo:table-cell border-style="solid">
								   <fo:block text-align="right" font-size="10pt"  ><#if productDetails.get("ReceiptAmount")?has_content>${productDetails.get("ReceiptAmount")?if_exists?string("##0.00")?if_exists}<#else>0.00</#if></fo:block>
							  </fo:table-cell>
                     <#if productDetails.get("ReceiptQty")?has_content>          <#assign totQty=productDetails.get("openingQty")+productDetails.get("ReceiptQty")> 	<#else> <#assign totQty=productDetails.get("openingQty")></#if>						  
                              <fo:table-cell border-style="solid">
								   <fo:block text-align="right" font-size="10pt"  >${totQty?if_exists}</fo:block>
							  </fo:table-cell>
					<#if productDetails.get("ReceiptAmount")?has_content> 		  <#assign totVal=productDetails.get("openingTot")+productDetails.get("ReceiptAmount")> <#else> <#assign totVal=productDetails.get("openingTot")></#if>	
                              <fo:table-cell border-style="solid">
								   <fo:block text-align="right" font-size="10pt"  ><#if totVal?has_content>${totVal?if_exists?string("##0.00")?if_exists}<#else></#if></fo:block>
							  </fo:table-cell>
							  <#assign temp=productDetails.get("IssueQty")>
							  <#if temp?has_content>
									 <#assign issQty=temp>
							 <#else>
								<#assign issQty = 0>
							</#if>
                              <fo:table-cell border-style="solid">
								   <fo:block text-align="right" font-size="10pt"  ><#if issQty?has_content>${issQty}<#else>0</#if></fo:block>
							  </fo:table-cell>
							  <#assign amt=productDetails.get("IssueAmount")>
							  <#if amt?has_content>
									 <#assign issAmt=amt>
							   <#else>
								<#assign issAmt = 0>
							  </#if>
                              <fo:table-cell border-style="solid">
								   <fo:block text-align="right" font-size="10pt"  ><#if issAmt?has_content>${issAmt?if_exists?string("##0.00")?if_exists}<#else>0.00</#if></fo:block>
							  </fo:table-cell>
						<#if issQty?has_content>	  <#assign closeQty=totQty- issQty> <#else><#assign closeQty=totQty></#if>
                              <fo:table-cell border-style="solid">
								   <fo:block text-align="right" font-size="10pt"  >${closeQty?if_exists}</fo:block>
							  </fo:table-cell> 
                        <#if issAmt?has_content>      <#assign closeVal=totVal- issAmt> <#else> <#assign closeVal=totVal></#if>
                              <fo:table-cell border-style="solid">
								   <fo:block text-align="right" font-size="10pt"  ><#if closeVal?has_content>${closeVal?if_exists?string("##0.00")?if_exists}<#else></#if></fo:block>
							  </fo:table-cell>
                             <#-- <fo:table-cell border-style="solid">
								   <fo:block text-align="right" font-size="10pt"  ></fo:block>
							  </fo:table-cell> 
                              <fo:table-cell border-style="solid">
								   <fo:block text-align="right" font-size="10pt"  ></fo:block>
							  </fo:table-cell> 
							  <fo:table-cell border-style="solid">
								   <fo:block text-align="right" font-size="9pt"  ></fo:block>
							  </fo:table-cell> -->	     				           
					       </fo:table-row>
					       <#assign sno=sno+1> 					    
                              </#list> 
			           </fo:table-body>
				   </fo:table>
			   </fo:block> 	
               <fo:block>
                   <fo:table border-style="solid">
                       <fo:table-column column-width="20pt"/>
					   <fo:table-column column-width="50pt"/>
					   <fo:table-column column-width="120pt"/>
					   <fo:table-column column-width="30pt"/>
					   <fo:table-column column-width="90pt"/>
					   <fo:table-column column-width="90pt"/>					   
					   <fo:table-column column-width="75pt"/>
					   <fo:table-column column-width="75pt"/>
					   <fo:table-column column-width="100pt"/>
					   <fo:table-column column-width="100pt"/>
					   <fo:table-column column-width="65pt"/>
					   <fo:table-column column-width="65pt"/>
                       <fo:table-column column-width="85pt"/>
                       <fo:table-column column-width="85pt"/>
					   <fo:table-body>
					   <fo:table-row>
					   <fo:table-cell >
						 <fo:block text-align="left" keep-together="always" font-size="10pt" ></fo:block>
					   </fo:table-cell>
					   <fo:table-cell >
						 <fo:block text-align="left" keep-together="always" font-size="10pt" ></fo:block>
					   </fo:table-cell>
					   <fo:table-cell >
						 <fo:block text-align="left" keep-together="always" font-size="10pt" ></fo:block>
					   </fo:table-cell>
					   <fo:table-cell >
						 <fo:block text-align="left" keep-together="always" font-size="10pt" ></fo:block>
					   </fo:table-cell>
					   <fo:table-cell >
						 <fo:block text-align="cemter" keep-together="always" font-size="10pt" font-weight="bold" >TOTALS :</fo:block>
					   </fo:table-cell>
					   <fo:table-cell >
						 <fo:block text-align="right" keep-together="always" font-size="10pt" font-weight="bold" >${grandOpenTotal?if_exists?string("##0.00")}</fo:block>
					   </fo:table-cell>
					   <fo:table-cell >
						 <fo:block text-align="left" keep-together="always" font-size="10pt" ></fo:block>
					   </fo:table-cell>
					   <fo:table-cell >
						 <fo:block text-align="right" keep-together="always" font-size="10pt" font-weight="bold" >${grandReceiptTotal?if_exists?string("##0.00")}</fo:block>
					   </fo:table-cell>
					   <fo:table-cell >
						 <fo:block text-align="left" keep-together="always" font-size="10pt" ></fo:block>
					   </fo:table-cell>
                       <#assign finalOpenTotal=finalOpenTotal+grandOpenTotal+grandReceiptTotal>
					   <fo:table-cell >
						 <fo:block text-align="right" keep-together="always" font-size="10pt" font-weight="bold" >${finalOpenTotal?if_exists?string("##0.00")}</fo:block>
					   </fo:table-cell>
					   <fo:table-cell >
						 <fo:block text-align="left" keep-together="always" font-size="10pt" ></fo:block>
					   </fo:table-cell>
					   <fo:table-cell >
						 <fo:block text-align="right" keep-together="always" font-size="10pt" font-weight="bold" >${grandIssueTotal?if_exists?string("##0.00")}</fo:block>
					   </fo:table-cell>
					   <fo:table-cell >
						 <fo:block text-align="left" keep-together="always" font-size="10pt" ></fo:block>
					   </fo:table-cell>
                       <#assign finalClosingTotal=finalOpenTotal-grandIssueTotal>
					   <fo:table-cell >
						 <fo:block text-align="right" keep-together="always" font-size="10pt" font-weight="bold" >${finalClosingTotal?if_exists?string("##0.00")}</fo:block>
					   </fo:table-cell>
					   </fo:table-row>
					   </fo:table-body>
				   </fo:table>
			   </fo:block>		   	      
		   </fo:flow>	
       </fo:page-sequence>
       <#else>
           <fo:page-sequence master-reference="main">
	    			<fo:flow flow-name="xsl-region-body" font-family="Courier,monospace">
	       		 		<fo:block font-size="14pt" text-align="center">
	            			 No Records Found....!
	       		 		</fo:block>
	    			</fo:flow>
		  </fo:page-sequence>				
	    </#if>  
</fo:root>
</#escape>	
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
        <fo:region-body margin-top="1.6in"/>
        <fo:region-before extent="1.in"/>
        <fo:region-after extent="1.5in"/>        
    </fo:simple-page-master>   
</fo:layout-master-set>
     <#if productPriceMap?has_content> 
        <#--<#if prodNameMap?has_content> -->   
        <fo:page-sequence master-reference="main">
           <fo:static-content font-size="13pt" font-family="Courier,monospace"  flow-name="xsl-region-before" font-weight="bold">
              <fo:block  keep-together="always" text-align="center" font-weight = "bold" font-family="Courier,monospace" white-space-collapse="false">${uiLabelMap.KMFDairyHeader}</fo:block>
			  <fo:block  keep-together="always" text-align="center" font-family="Courier,monospace" white-space-collapse="false" font-weight="bold">${uiLabelMap.KMFDairySubHeader}</fo:block> 
              <fo:block text-align="center" keep-together="always"> COMPARATIVE STATEMENT SHOWING DETAILS OF OFFERS RECEIVED AS AGAINST OUR ENQUIRY DATE:${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(enquiryDate, "dd-MMM-yyyy")}</fo:block>
              <fo:block text-align="center" keep-together="always"  >&#160;---------------------------------------------------------------------------------------------------</fo:block>
            </fo:static-content>                 
                <fo:flow flow-name="xsl-region-body"   font-family="Courier,monospace">				        
				   <fo:block text-align="left" keep-together="always" white-space-collapse="false" font-size="13pt" font-weight="bold">ENQUIRY NO.: ${parameters.issueToEnquiryNo}                                                          </fo:block>	
				   <fo:block font-family="Courier,monospace">
	                 <fo:table  border-style="solid">
					     <fo:table-column column-width="240pt"/>
					     <#list partyDetList as partyNameList>
					      <fo:table-column column-width="140pt"/>					      
					      </#list> 
					         <fo:table-body>
					             <fo:table-row>
					                 <fo:table-cell >
								        <fo:block text-align="center" keep-together="always" font-weight="bold" font-size="13pt">VENDOR NAME</fo:block>
								        <fo:block linefeed-treatment="preserve">&#xA;</fo:block>							        
								        <fo:block text-align="left" keep-together="always"  font-weight="bold" white-space-collapse="false">&#160;MATERIALS  REQRD QTY    LAST PO</fo:block>
								        <fo:block text-align="left" keep-together="always"  font-weight="bold" white-space-collapse="false">&#160;                       DATE/RATE</fo:block>								        
								     </fo:table-cell>
					                 <#list partyDetList as partyNameList>
    										<fo:table-cell border-style="solid">
						                      <fo:block text-align="center" font-size="13pt" white-space-collapse="false" font-weight="bold">${partyNameList.get("partyName")?if_exists}[${partyNameList.get("partyId")}]</fo:block>
						                  	</fo:table-cell>
    									</#list>    	
					             </fo:table-row> 
					             <fo:table-row>
					                <fo:table-cell>
					                    <fo:block ></fo:block>
					                </fo:table-cell>
					                 <#list partyDetList as partyNameList>
					                 <fo:table-cell border-style="solid">
					                    <fo:block text-align="center" font-size="13pt" white-space-collapse="false" font-weight="bold">UNIT PRICE/TOT COST</fo:block>
					                </fo:table-cell>
					                </#list> 
					            </fo:table-row> 					              		                             
					         </fo:table-body> 
					       </fo:table>  
					  </fo:block>					 
				     <fo:block font-family="Courier,monospace">
	                     <fo:table border-style="solid">
					     <fo:table-column column-width="100pt"/>
					     <fo:table-column column-width="50pt"/>
					     <fo:table-column column-width="90pt"/>
					     <#list partyDetList as partyNameList>
					     <fo:table-column column-width="140pt"/>
					      </#list> 				    
					         <fo:table-body>					         
                           		<#assign productQtyList = productQtyMap.entrySet()>
                           	     <#if productQtyList?has_content>                           	     
		  					     <#list productQtyList as productEntry>   
                           		 <#assign productId=productEntry.getKey()>
                           		 <#assign product = delegator.findOne("Product", {"productId" : productId}, true)?if_exists/>
                           		 <#assign productEntryValue=productEntry.getValue()>
                                <fo:table-row>
		  					        <fo:table-cell border-style="solid">
		  					            <fo:block text-align="center" font-size="11pt" >${product.description}</fo:block>
		  					        </fo:table-cell> 
		  					         <fo:table-cell border-style="solid">
		  					            <fo:block text-align="center" font-size="11pt" >${productEntryValue}</fo:block>
		  					        </fo:table-cell>
		  					        <fo:table-cell border-style="solid">
		  					            <fo:block text-align="center" font-size="11pt" >${poDateMap.get(productId)?if_exists}</fo:block>
		  					        </fo:table-cell>		 
		  					        <#assign partyProdPriceMap=productPriceMap.get(productId)>
		  					        <#assign priceList =  partyProdPriceMap.entrySet()>
		  					        <#list priceList as pList>
		  					        <#if pList.getValue().get("price")?has_content>
		  					        <fo:table-cell border-style="solid" >
		  					            <fo:block text-align="center" font-size="11pt" >${pList.getValue().get("price")?if_exists}/${pList.getValue().get("amount")?if_exists}</fo:block>
		  					        </fo:table-cell> 
		  					        <#else>
		  					        <fo:table-cell border-style="solid" >
		  					            <fo:block text-align="center" font-size="11pt">&#160;</fo:block>
		  					        </fo:table-cell> 
		  					        </#if>
		  					         </#list> 		  					        	  					    
					           </fo:table-row> 					               				             
					               </#list>
					               </#if> 
	                          </fo:table-body> 
					     </fo:table> 					   
					  </fo:block>  
					  <fo:block linefeed-treatment="preserve">&#xA;</fo:block>		        
	                  <fo:block linefeed-treatment="preserve">&#xA;</fo:block>       
				      <fo:block font-family="Courier,monospace">
	                    <fo:table>
					      <fo:table-column column-width="140pt"/>
					      <#list partyDetList as partyNameList>					      
					      <fo:table-column column-width="140pt"/>
					      </#list> 					                					      
					          <fo:table-body>
					              <fo:table-row>
					                 <fo:table-cell border-style="solid">
					                      <fo:block text-align="center" font-weight="bold" font-size="13pt" >VENDOR NAME</fo:block>
					                  </fo:table-cell>					                  
    									<#list partyDetList as partyNameList>
    										<fo:table-cell border-style="solid">
						                      <fo:block text-align="center" font-size="13pt"  font-weight="bold">${partyNameList.get("partyName")?if_exists}</fo:block>
						                  	</fo:table-cell>
    									</#list>    									    								
					              </fo:table-row>
					              <#if quoteDetailList?has_content >
					              <#list quoteDetailList as quoteList>
					               <#assign TermType = delegator.findOne("TermType", {"termTypeId" : quoteList.get("termTypeId")}, true)?if_exists/>
		   						
					              <fo:table-row>
					                  <fo:table-cell border-style="solid">
					                      <fo:block text-align="center" font-size="11pt" font-weight="bold">${TermType.description?if_exists}</fo:block>
					                  </fo:table-cell>
					                 <#list partyDetList as partyNameList>
    										<fo:table-cell border-style="solid">
					                      		<fo:block text-align="center" font-size="11pt" font-weight="bold">${quoteList.get("termValue")?if_exists}</fo:block>
					                 		</fo:table-cell>
    									</#list> 					                
					             </fo:table-row>
					             </#list> 
					             </#if>					              
					          </fo:table-body>
					   </fo:table>
				   </fo:block>  
				   
				   <fo:block linefeed-treatment="preserve">&#xA;</fo:block>		        
				   <fo:block linefeed-treatment="preserve">&#xA;</fo:block>		        				   
                   <fo:block text-align="left" keep-together="always"  font-weight="bold" white-space-collapse="false" font-size="13pt" >VENDOR NAME</fo:block>
			       <#list partyDetList as partyNameList>
						 <fo:block text-align="left" font-size="13pt" font-weight="bold">${partyNameList.get("partyName")}</fo:block>
    				</#list>   
				   <fo:block linefeed-treatment="preserve">&#xA;</fo:block>		        
				   <fo:block linefeed-treatment="preserve">&#xA;</fo:block>		        
				   <fo:block text-align="left" keep-together="always" white-space-collapse="false">(CASH WORKER)                Purchase Officer                Manager(Purchase)                PRI-ADITOR</fo:block>				   
			    </fo:flow>	
		 </fo:page-sequence>	
		 <#-- </#if>   --> 	
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
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
            	 <fo:block  keep-together="always" text-align="center" font-family="Courier,monospace" white-space-collapse="false" font-size="12pt" > &#160;&#160;  </fo:block>
            	  <fo:block  keep-together="always" text-align="center" font-family="Courier,monospace" white-space-collapse="false" font-size="12pt" > &#160;&#160;  </fo:block>
              	<fo:block  keep-together="always" text-align="left" font-family="Courier,monospace" white-space-collapse="false" font-size="16pt" font-weight="bold"  >&#160;&#160;&#160;&#160;                        <fo:inline font-weight="bold" >PO against ARC/CPC</fo:inline>         </fo:block>
                <fo:block text-align="left" font-size="12pt" white-space-collapse="false" keep-together="always" > &#160;</fo:block>
	           <fo:block  keep-together="always" text-align="left" font-family="Courier,monospace" white-space-collapse="false" font-size="12pt" >  ARC/CPC PO NO: ${allDetailsMap.get("orderId")?if_exists}                                                  PO DATED: ${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(allDetailsMap.get("orderDate")?if_exists, "dd-MMM-yy")}</fo:block>
           
                                           
											<fo:block text-align="left" font-size="12pt" white-space-collapse="false" keep-together="always" > &#160;</fo:block>
											<fo:block text-align="left" font-size="12pt" white-space-collapse="false" keep-together="always" > &#160; </fo:block>
											<fo:block text-align="left" font-size="12pt" white-space-collapse="false" keep-together="always" > &#160; </fo:block>
											
                         <fo:block >
			        			 <fo:table width="100%" align="right" table-layout="fixed"  font-size="13pt">
					               
				                     <fo:table-column column-width="30pt"/>					               
					                <fo:table-column column-width="150pt"/>               
						            <fo:table-column column-width="80pt"/>
						            <fo:table-column column-width="100pt"/>
			      			        <fo:table-column column-width="80pt"/> 
                                    <fo:table-column column-width="80pt"/>               
					                <fo:table-column column-width="80pt"/>               
						            <fo:table-column column-width="80pt"/>
						            
						           	<fo:table-body>
				                     <fo:table-row border-style="solid">
				                     <fo:table-cell border-style="solid"><fo:block text-align="center"  font-weight="bold" >SNO </fo:block></fo:table-cell>                    
				                     <fo:table-cell border-style="solid"><fo:block text-align="center"  font-weight="bold" >Material Name</fo:block></fo:table-cell>
				                      <fo:table-cell border-style="solid"><fo:block text-align="center"  font-weight="bold"  >Material Code</fo:block></fo:table-cell>       		
				                      <fo:table-cell border-style="solid"><fo:block text-align="center"  font-weight="bold" >ARC/CPC Quantity</fo:block></fo:table-cell>       		
				                      <fo:table-cell border-style="solid"><fo:block text-align="center" font-weight="bold">Quantity</fo:block></fo:table-cell> 				                    
                                     <fo:table-cell border-style="solid"><fo:block text-align="center"  font-weight="bold"  >Supplied Qty</fo:block></fo:table-cell>
				                      <fo:table-cell border-style="solid"><fo:block text-align="center" font-weight="bold">Balance Quantity</fo:block></fo:table-cell>  
				                      <fo:table-cell border-style="solid"><fo:block text-align="center"  font-weight="bold"  >ARC/CPC Bal Qty</fo:block></fo:table-cell>       		
				                        </fo:table-row>
				                     
			                	</fo:table-body>
			                		</fo:table>
			        	  </fo:block>	
            	<fo:block>
                 <fo:table text-align="center" >
                 		            <fo:table-column column-width="30pt"/>                 
                                   <fo:table-column column-width="150pt"/>               
						            <fo:table-column column-width="80pt"/>
						            <fo:table-column column-width="100pt"/>
			      			        <fo:table-column column-width="80pt"/> 
                                    <fo:table-column column-width="80pt"/>               
					                <fo:table-column column-width="80pt"/>               
						            <fo:table-column column-width="80pt"/>
                    <fo:table-body text-align="center">
                     <#assign sNo=1>
	                    
	                    <#list orderDetailsList as orderListItem>
	                    
	                  
					<#assign productId= orderListItem("productId")?if_exists >
		          <#assign productNameDetails = delegator.findOne("Product", {"productId" : productId}, true)>
		           <#if productNameDetails?has_content> 
                  	 <fo:table-row >
                  	    <fo:table-cell  border-style="solid" ><fo:block text-align="center"   font-size="12pt" >${sNo?if_exists} </fo:block></fo:table-cell>                   	 
  				       <fo:table-cell  border-style="solid" ><fo:block text-align="left"   font-size="12pt" >${productNameDetails.get("productName")?if_exists} </fo:block></fo:table-cell>   
  				       <fo:table-cell  border-style="solid" ><fo:block text-align="left"   font-size="12pt" >${productNameDetails.get("productId")?if_exists} </fo:block></fo:table-cell>     
                       <fo:table-cell  border-style="solid" ><fo:block text-align="center"  font-size="12pt" >${orderListItem.get("quantity")?if_exists}</fo:block></fo:table-cell> 
                	   <fo:table-cell   border-style="solid"><fo:block text-align="center"  font-size="12pt" >${orderListItem.get("poQty")?if_exists}</fo:block></fo:table-cell>     
  				       <fo:table-cell  border-style="solid" ><fo:block text-align="center"   font-size="12pt" >${orderListItem.get("totAccepQty")?if_exists} </fo:block></fo:table-cell>   
  				       <fo:table-cell  border-style="solid" ><fo:block text-align="center"   font-size="12pt" >${orderListItem.get("poBalanceQty")?if_exists} </fo:block></fo:table-cell>     
                       <fo:table-cell   border-style="solid"><fo:block text-align="center"  font-size="12pt" >${orderListItem.get("ARCBalanceQty")?if_exists}</fo:block></fo:table-cell>     
  				         </fo:table-row>
  				    	<#assign sNo=sNo+1>
  				    	</#if>
  				     </#list>
                    </fo:table-body> </fo:table>
               </fo:block>
            	
            	   <fo:block  keep-together="always" text-align="center" font-family="Courier,monospace" white-space-collapse="false" font-size="12pt" >&#160;&#160; </fo:block>
                  <fo:block  keep-together="always" text-align="center" font-family="Courier,monospace" white-space-collapse="false" font-size="12pt" >&#160;&#160; </fo:block>
                             <fo:block  keep-together="always" text-align="left" font-family="Courier,monospace" white-space-collapse="false" font-weight="bold" font-size="14pt" >Individual PO Details</fo:block>
                  <fo:block  keep-together="always" text-align="center" font-family="Courier,monospace" white-space-collapse="false" font-size="12pt" >&#160;&#160; </fo:block>
                          
            	
            	         <fo:block >
			        			 <fo:table width="100%" align="right" table-layout="fixed"  font-size="13pt">
					               
						            <fo:table-column column-width="110pt"/>
						            <fo:table-column column-width="110pt"/>
			      			        <fo:table-column column-width="110pt"/> 
                                    <fo:table-column column-width="110pt"/>               
					               <fo:table-column column-width="110pt"/>               
						           	<fo:table-body>
				                     <fo:table-row border-style="solid">
				                      <fo:table-cell border-style="solid"><fo:block text-align="center" font-weight="bold">PO NO </fo:block></fo:table-cell> 				                    
                                     <fo:table-cell border-style="solid"><fo:block text-align="center"  font-weight="bold"  >Product Id</fo:block></fo:table-cell>
				                      <fo:table-cell border-style="solid"><fo:block text-align="center" font-weight="bold"> Quantity</fo:block></fo:table-cell>  
				                      <fo:table-cell border-style="solid"><fo:block text-align="center"  font-weight="bold"  >Supplied Quantity</fo:block></fo:table-cell>       		
				                      <fo:table-cell border-style="solid"><fo:block text-align="center"  font-weight="bold"  >Balance Quantity</fo:block></fo:table-cell>       		
				                        </fo:table-row>
				                     
			                	</fo:table-body>
			                		</fo:table>
			        	  </fo:block>	
            	<fo:block>
                 <fo:table text-align="center" >
                 		           <fo:table-column column-width="110pt"/>
						            <fo:table-column column-width="110pt"/>
			      			        <fo:table-column column-width="110pt"/> 
                                    <fo:table-column column-width="110pt"/>               
					                <fo:table-column column-width="110pt"/>               
					                 
                    <fo:table-body text-align="center">
	                    
		           <#if pOrderList?has_content> 
                    	                    <#list pOrderList as individualPO>
	                    rderListItem>
	                    

	             
                  	 <fo:table-row >
                  	    
                	   <fo:table-cell   border-style="solid"><fo:block text-align="center"  font-size="12pt" >${individualPO.get("orderId")?if_exists}</fo:block></fo:table-cell>     
  				       <fo:table-cell  border-style="solid" ><fo:block text-align="center"   font-size="12pt" >${individualPO.get("productId")?if_exists} </fo:block></fo:table-cell>   
  				       <fo:table-cell  border-style="solid" ><fo:block text-align="center"   font-size="12pt" >${individualPO.get("poQty")?if_exists} </fo:block></fo:table-cell>     
                       <fo:table-cell   border-style="solid"><fo:block text-align="center"  font-size="12pt" >${individualPO.get("quantityAccepted")?if_exists}</fo:block></fo:table-cell>     
  				       <fo:table-cell   border-style="solid"><fo:block text-align="center"  font-size="12pt" >${individualPO.get("pobalQty")?if_exists}</fo:block></fo:table-cell>     
                    </fo:table-row>
  				    
	     </#list></#if>
                    </fo:table-body> </fo:table>
               </fo:block>
            	
            	
            	
            	
                     <fo:block  keep-together="always" text-align="center" font-family="Courier,monospace" white-space-collapse="false" font-size="12pt" >&#160;&#160; </fo:block>
                                      <fo:block  keep-together="always" text-align="center" font-family="Courier,monospace" white-space-collapse="false" font-size="12pt" >&#160;&#160; </fo:block>
 <fo:block  keep-together="always" text-align="center" font-family="Courier,monospace" white-space-collapse="false" font-size="12pt" >&#160;&#160; </fo:block>
                               <fo:block  keep-together="always" text-align="right" font-family="Courier,monospace" white-space-collapse="false" font-size="12pt" >  for MOTHER DAIRY   &#160;&#160;</fo:block>
                               <fo:block  keep-together="always" text-align="center" font-family="Courier,monospace" white-space-collapse="false" font-size="12pt" >&#160;&#160; </fo:block>
                               <fo:block  keep-together="always" text-align="center" font-family="Courier,monospace" white-space-collapse="false" font-size="12pt" >&#160;&#160; </fo:block>   <fo:block  keep-together="always" text-align="right" font-family="Courier,monospace" white-space-collapse="false" font-size="12pt" >  Manager        &#160;&#160;</fo:block>
                               <fo:block  keep-together="always" text-align="left" font-family="Courier,monospace" white-space-collapse="false" font-size="12pt" >&#160;&#160;  </fo:block>
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
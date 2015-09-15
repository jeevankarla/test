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
${setRequestAttribute("OUTPUT_FILENAME", "UnionPurchaseBillingReportMR.pdf")}
 <#if vehicleWiseDetailsMap?has_content> 

<fo:page-sequence master-reference="main" force-page-count="no-force" font-family="Courier,monospace">	
			<fo:static-content flow-name="xsl-region-before">
		       	 
            </fo:static-content>	
            	
            <fo:flow flow-name="xsl-region-body"   font-family="Courier,monospace">	
                
		        <fo:block  keep-together="always" text-align="right" font-family="Courier,monospace" white-space-collapse="false">   Date: ${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(nowTimestamp, "dd/MM/yy HH:mm:ss")}&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;</fo:block>
		        <fo:block  keep-together="always" text-align="center" font-family="Courier,monospace" white-space-collapse="false" font-size="3pt" > &#160;&#160;  </fo:block>
				<fo:block  keep-together="always" text-align="center" font-family="Courier,monospace" white-space-collapse="false" font-size="13pt" font-weight="bold" >${uiLabelMap.KMFDairyHeader}</fo:block>
				<fo:block  keep-together="always" text-align="center" font-family="Courier,monospace" white-space-collapse="false" font-size="14pt" font-weight="bold" >${uiLabelMap.KMFDairySubHeader}</fo:block>
			    <fo:block  keep-together="always" text-align="left" font-family="Courier,monospace" white-space-collapse="false" font-size="5pt" > ------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------</fo:block>
			    <fo:block  keep-together="always" text-align="center" font-family="Courier,monospace" white-space-collapse="false" font-size="3pt" > &#160;&#160;  </fo:block>
		         
   			 <fo:block  keep-together="always" text-align="center" font-family="Courier,monospace" white-space-collapse="false" font-size="3pt" > &#160;&#160;  </fo:block>
            <fo:block  keep-together="always" text-align="center" font-family="Courier,monospace" white-space-collapse="false" font-size="3pt" > &#160;&#160;  </fo:block>
          	<fo:block  keep-together="always" text-align="center" font-family="Courier,monospace" white-space-collapse="false" font-size="3pt" > &#160;&#160;  </fo:block>
       
         	<fo:block>
	              <fo:table >
	                    <fo:table-column column-width="30pt"/>
	                    <fo:table-column column-width="400pt"/>
	            		<fo:table-column column-width="250pt"/> 		
	                    <fo:table-body>
	                        <fo:table-row >
                                <fo:table-cell>
                            		<fo:block   text-align="left" font-size="12pt" white-space-collapse="false"> TO : </fo:block>  
                       			</fo:table-cell>
                       			 <fo:table-cell>
                            		<fo:block   text-align="left" font-size="12pt" white-space-collapse="false"> </fo:block>  
                       			</fo:table-cell>
                       			<fo:table-cell>
                            		<fo:block   text-align="left" font-size="12pt" white-space-collapse="false">&#160; BILL NO:  </fo:block>  
                       			</fo:table-cell>
                            </fo:table-row >
                             <fo:table-row >
                                <fo:table-cell>
                            		<fo:block   text-align="left" font-size="12pt" white-space-collapse="false">  </fo:block>  
                       			</fo:table-cell>
                       			 <fo:table-cell>
                            		<fo:block   text-align="left" font-size="12pt" white-space-collapse="false">THE MANAGING DIRECTOR  </fo:block>  
                       			</fo:table-cell>
                       			<fo:table-cell>
                            		<fo:block   text-align="left" font-size="12pt" white-space-collapse="false">&#160; BILL DATE: ${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(nowTimestamp, "dd-MMM-yyyy")} </fo:block>  
                       			</fo:table-cell>
                            </fo:table-row >
                            
                             <#if address1?has_content> 
                            <fo:table-row >  
                             <fo:table-cell>
                            		<fo:block   text-align="left" font-size="12pt" white-space-collapse="false">  </fo:block>  
                  			</fo:table-cell>
                    		<fo:table-cell >
                            		<fo:block   text-align="left" font-size="12pt" white-space-collapse="false" >${address1}</fo:block>  
                       		</fo:table-cell>
                			</fo:table-row>
                			</#if>
                			<#if address2?has_content> 
                			 <fo:table-row >  
                             <fo:table-cell>
                            		<fo:block   text-align="left" font-size="12pt" white-space-collapse="false">  </fo:block>  
                  			</fo:table-cell>
                    		<fo:table-cell >
                            		<fo:block   text-align="left" font-size="12pt" white-space-collapse="false" >${address2}</fo:block>  
                       			</fo:table-cell>
                			</fo:table-row>
                			</#if>
                			 <#if city?has_content> 
                			 <fo:table-row >  
                             <fo:table-cell>
                            		<fo:block   text-align="left" font-size="12pt" white-space-collapse="false">  </fo:block>  
                  			</fo:table-cell>
                    		<fo:table-cell >
                            		<fo:block   text-align="left" font-size="12pt" white-space-collapse="false" >${city}</fo:block>  
                       			</fo:table-cell>
                			</fo:table-row>
                			</#if>
                			<#if partyName?has_content> 
                			 <fo:table-row >  
                             <fo:table-cell>
                            		<fo:block   text-align="left" font-size="12pt" white-space-collapse="false">  </fo:block>  
                  			</fo:table-cell>
                    		<fo:table-cell >
                            		<fo:block   text-align="left" font-size="12pt" white-space-collapse="false" >${partyName}</fo:block>  
                       			</fo:table-cell>
                			</fo:table-row>
                			</#if>
                			
                		 <#if postalCode?has_content> 
                			 <fo:table-row >  
                             <fo:table-cell>
                            		<fo:block   text-align="left" font-size="12pt" white-space-collapse="false">  </fo:block>  
                  			</fo:table-cell>
                    		<fo:table-cell >
                            		<fo:block   text-align="left" font-size="12pt" white-space-collapse="false" >${postalCode}</fo:block>  
                       			</fo:table-cell>
                			</fo:table-row>
                	      </#if>
	         		     </fo:table-body>
                </fo:table>
              </fo:block> 
              <#assign products = delegator.findOne("Product", {"productId" : productId}, true)>
				<#assign productName= products.productName>              	
       <fo:block  keep-together="always" text-align="center" font-weight="bold" font-size="12pt" > BILLING FOR A PERIOD BETWEEN ${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(fromDate, "dd-MMM-yy")} AND ${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(thruDate, "dd-MMM-yy")}  </fo:block>
	   <fo:block  keep-together="always" text-align="center" font-weight="bold" font-size="12pt" > FOR THE ${productName} PROCURED FROM ${partyName}   </fo:block>
       <fo:block  keep-together="always" text-align="left" font-size="12pt" >REF NO:   </fo:block>
       
       
       <fo:block >
		 <fo:table width="100%" align="center" table-layout="fixed"  font-size="12pt" border-style="solid">
           <fo:table-column column-width="20pt"/>               
            <fo:table-column column-width="60pt"/>               
            <fo:table-column column-width="60pt"/>
            <fo:table-column column-width="70pt"/>
            <fo:table-column column-width="70pt"/>
            <fo:table-column column-width="40pt"/>
            <fo:table-column column-width="40pt"/>
            <fo:table-column column-width="50pt"/>
            <fo:table-column column-width="70pt"/>
             <fo:table-column column-width="60pt"/>
		    <fo:table-column column-width="60pt"/>
		    <fo:table-column column-width="80pt"/>
		    
       	<fo:table-body>
	         <fo:table-row border-style="solid">
		          <fo:table-cell border-style="solid"><fo:block text-align="left"  font-weight="bold"  font-size="10pt" >SI NO</fo:block></fo:table-cell>       		
		          <fo:table-cell  border-style="solid"><fo:block text-align="left" font-weight="bold"  font-size="10pt">DATE</fo:block></fo:table-cell>       		
		          <fo:table-cell border-style="solid"><fo:block text-align="left"  font-weight="bold"  font-size="10pt">TANKER NO</fo:block></fo:table-cell> 
		          <fo:table-cell border-style="solid"><fo:block text-align="right"  font-weight="bold"  font-size="10pt">DC NO</fo:block></fo:table-cell>       		
		          <fo:table-cell border-style="solid"><fo:block text-align="right"  font-weight="bold"  font-size="10pt">ACK QTY IN KG</fo:block></fo:table-cell>       		
			     <fo:table-cell border-style="solid"><fo:block text-align="right"  font-weight="bold"  font-size="10pt">DISP FAT%</fo:block></fo:table-cell>
		         <fo:table-cell border-style="solid"><fo:block text-align="right"  font-weight="bold"  font-size="10pt">DISP SNF%</fo:block></fo:table-cell>
		         <fo:table-cell border-style="solid"><fo:block text-align="right"  font-weight="bold"  font-size="10pt">UNIT PRICE</fo:block></fo:table-cell>
 		          <fo:table-cell border-style="solid"><fo:block text-align="right"  font-weight="bold"  font-size="10pt">ACTUAL AMOUNT</fo:block></fo:table-cell>
		         <fo:table-cell border-style="solid"><fo:block text-align="right"  font-weight="bold"  font-size="10pt">FAT PREM AMT</fo:block></fo:table-cell>
	       		 <fo:table-cell border-style="solid"><fo:block text-align="right"  font-weight="bold"  font-size="10pt">SNF PREM AMT</fo:block></fo:table-cell>
	       		<fo:table-cell border-style="solid"><fo:block text-align="right"  font-weight="bold"  font-size="10pt"> AMOUNT</fo:block></fo:table-cell>
	        
	         </fo:table-row>
	         <#assign siNo=1>
        <#assign vehicleWiseDetails = vehicleWiseDetailsMap.entrySet()?if_exists>											
         <#list vehicleWiseDetails as vehicleWiseDetail>
         <#assign sno=vehicleWiseDetail.getKey()>
				  <fo:table-row >
					  <fo:table-cell ><fo:block text-align="left"   font-size="10pt">${sno?if_exists}  </fo:block></fo:table-cell>       	
  					  <fo:table-cell ><fo:block text-align="left"  font-size="10pt">${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(vehicleWiseDetail.getValue().get("recdDate"), "dd-MM-yy")}  </fo:block></fo:table-cell>       		
   					  <fo:table-cell ><fo:block text-align="left"    font-size="10pt">${vehicleWiseDetail.getValue().get("vehicleId")?if_exists}  </fo:block></fo:table-cell>       		
  					  <fo:table-cell ><fo:block text-align="right" font-size="10pt">${vehicleWiseDetail.getValue().get("dcNo")?if_exists}  </fo:block></fo:table-cell>       		
  					  <fo:table-cell ><fo:block text-align="right"   font-size="10pt">${vehicleWiseDetail.getValue().get("receivedQuantity")?if_exists?string("#0.00")}  </fo:block></fo:table-cell>       		
  					  <fo:table-cell ><fo:block text-align="right"   font-size="10pt">${vehicleWiseDetail.getValue().get("fatPercent")?if_exists?string("#0.0")}  </fo:block></fo:table-cell>       		
  					  <fo:table-cell ><fo:block text-align="right"   font-size="10pt">${vehicleWiseDetail.getValue().get("snfPercent")?if_exists?string("#0.00")}  </fo:block></fo:table-cell>       		
  					  <fo:table-cell ><fo:block text-align="right"   font-size="10pt">${vehicleWiseDetail.getValue().get("unitPrice")?if_exists?string("#0.00")}  </fo:block></fo:table-cell>       		
  					  <fo:table-cell ><fo:block text-align="right"   font-size="10pt">${vehicleWiseDetail.getValue().get("actualAmt")?if_exists?string("#0.00")}  </fo:block></fo:table-cell>       		
  					  <fo:table-cell ><fo:block text-align="right"   font-size="10pt">${vehicleWiseDetail.getValue().get("fatPremAmt")?if_exists?string("#0.00")}  </fo:block></fo:table-cell>       		
  					  <fo:table-cell ><fo:block text-align="right"    font-size="10pt">${vehicleWiseDetail.getValue().get("snfPremAmt")?if_exists?string("#0.00")}  </fo:block></fo:table-cell>       		
  					  <fo:table-cell ><fo:block text-align="right"    font-size="10pt">${vehicleWiseDetail.getValue().get("vehicleTotAmt")?if_exists?string("#0.00")}  </fo:block></fo:table-cell>       		
		           </fo:table-row>
		           
	         </#list> 
	           <fo:table-row border-style="solid">
					  <fo:table-cell><fo:block text-align="left"    font-size="10pt">  </fo:block></fo:table-cell>       	
  					  <fo:table-cell><fo:block text-align="right" font-weight="bold"  font-size="10pt">Total  </fo:block></fo:table-cell>       		
					  <fo:table-cell><fo:block text-align="left"    font-size="10pt">  </fo:block></fo:table-cell>       	
					  <fo:table-cell><fo:block text-align="right"    font-size="10pt">  </fo:block></fo:table-cell>       	
  					  <fo:table-cell><fo:block text-align="right"  font-weight="bold"  font-size="10pt">${unionTotalsMap.get("totQuantity")?if_exists?string("#0.00")}  </fo:block></fo:table-cell>       	
  					
					  <fo:table-cell><fo:block text-align="right"    font-size="10pt">  </fo:block></fo:table-cell>       	
					  <fo:table-cell><fo:block text-align="right"    font-size="10pt">  </fo:block></fo:table-cell>       	
					  <fo:table-cell><fo:block text-align="right"    font-size="10pt">  </fo:block></fo:table-cell>       	
					  <fo:table-cell><fo:block text-align="right"  font-weight="bold"  font-size="10pt">${unionTotalsMap.get("totActualAmt")?if_exists?string("#0.00")}  </fo:block></fo:table-cell>       	
					  <fo:table-cell><fo:block text-align="right"  font-weight="bold"  font-size="10pt">${unionTotalsMap.get("totFatPremAmt")?if_exists?string("#0.00")} </fo:block></fo:table-cell>       	
					  <fo:table-cell><fo:block text-align="right"  font-weight="bold"  font-size="10pt">${unionTotalsMap.get("totSnfPremamt")?if_exists?string("#0.00")} </fo:block></fo:table-cell>       	
					  <fo:table-cell><fo:block text-align="right"  font-weight="bold"  font-size="10pt">${unionTotalsMap.get("totAmount")?if_exists?string("#0.00")} </fo:block></fo:table-cell>       	
		           </fo:table-row>
   		        </fo:table-body>   
           </fo:table>		
       </fo:block>
         <fo:block  keep-together="always" text-align="center" font-family="Courier,monospace" white-space-collapse="false" font-size="10pt" >&#160;&#160; </fo:block>
         <fo:block  keep-together="always" text-align="center" font-family="Courier,monospace" white-space-collapse="false" font-size="10pt" >&#160;&#160; </fo:block>
        <fo:block  keep-together="always" text-align="center" font-family="Courier,monospace" white-space-collapse="false" font-size="10pt" >&#160;&#160; </fo:block>
        <fo:block  keep-together="always" text-align="center" font-family="Courier,monospace" white-space-collapse="false" font-size="10pt" >&#160;&#160; </fo:block>
       
       
       	<fo:block>
	              <fo:table >
	                    <fo:table-column column-width="170pt"/>
	                    <fo:table-column column-width="180pt"/>
	            		<fo:table-column column-width="180pt"/> 
                		<fo:table-column column-width="150pt"/> 		
	                    <fo:table-body>
	                        <fo:table-row >
                                <fo:table-cell>
                            		<fo:block   text-align="left" font-size="12pt" white-space-collapse="false">PREPARED BY </fo:block>  
                       			</fo:table-cell>
                       			 <fo:table-cell>
                            		<fo:block   text-align="left" font-size="12pt" white-space-collapse="false"> PRE_AUDIT  </fo:block>  
                       			</fo:table-cell>
                       			<fo:table-cell>
                            		<fo:block   text-align="left" font-size="12pt" white-space-collapse="false">&#160;MANAGER  </fo:block>  
                       			</fo:table-cell>
                       		   <fo:table-cell>
                            		<fo:block   text-align="left" font-size="12pt" white-space-collapse="false"> DEPUTY/MANAGER  </fo:block>  
                       			</fo:table-cell>
                            </fo:table-row >
                            <fo:table-row >
                                <fo:table-cell>
                            		<fo:block   text-align="left" font-size="12pt" white-space-collapse="false"> </fo:block>  
                       			</fo:table-cell>
                       			 <fo:table-cell>
                            		<fo:block   text-align="left" font-size="12pt" white-space-collapse="false">   </fo:block>  
                       			</fo:table-cell>
                       			<fo:table-cell>
                            		<fo:block   text-align="left" font-size="12pt" white-space-collapse="false">(Production)  </fo:block>  
                       			</fo:table-cell>
                       		   <fo:table-cell>
                            		<fo:block   text-align="left" font-size="12pt" white-space-collapse="false"> (Finance) </fo:block>  
                       			</fo:table-cell>
                            </fo:table-row >
                        </fo:table-body>
                </fo:table>
              </fo:block> 
       
       
        <#assign pageNumber = pageNumber+1>	
     
        <fo:block  keep-together="always" text-align="center" font-family="Courier,monospace" white-space-collapse="false" font-size="12pt" >&#160;&#160; </fo:block>
        <fo:block  keep-together="always" text-align="center" font-family="Courier,monospace" white-space-collapse="false" font-size="12pt" >&#160;&#160; </fo:block>
        <fo:block  keep-together="always" text-align="center" font-family="Courier,monospace" white-space-collapse="false" font-size="12pt" >&#160;&#160; </fo:block>
 
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


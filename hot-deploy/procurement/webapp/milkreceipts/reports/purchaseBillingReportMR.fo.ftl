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
	<fo:simple-page-master master-name="main" page-height="10in" page-width="12in"
            margin-top="0in" margin-bottom=".7in" margin-left=".5in" margin-right=".5in">
        <fo:region-body margin-top="0.2in"/>
        <fo:region-before extent="1.5in"/>
        <fo:region-after extent="1.5in"/>        
    </fo:simple-page-master>   
</fo:layout-master-set>
${setRequestAttribute("OUTPUT_FILENAME", "PurchaseBillingReportMR.pdf")}
 <#if allProdProcPriceMap?has_content> 

<fo:page-sequence master-reference="main" force-page-count="no-force" font-family="Courier,monospace">	
			<fo:static-content flow-name="xsl-region-before">
		       	 
            </fo:static-content>	
            	
            <fo:flow flow-name="xsl-region-body"   font-family="Courier,monospace">	
  		<#assign pageNumber = 0>	
         <#assign allProdProcPriceDetails = allProdProcPriceMap.entrySet()?if_exists>											
         <#list allProdProcPriceDetails as allProdProcPriceDetail>
         <#assign productId=allProdProcPriceDetail.getKey()>
         <#assign PremAndDeductionMap=allProdProcPriceDetail.getValue().get("PremAndDeductionMap")>
         <#assign vehicleWiseDetailsMap=allProdProcPriceDetail.getValue().get("vehicleWiseDetailsMap")>
		 <#assign totQuantity=allProdProcPriceDetail.getValue().get("totQuantity")>
		 <#assign totAmount=allProdProcPriceDetail.getValue().get("totAmount")>
		 
				<#if pageNumber != 0>	
                  <fo:block page-break-before="always" text-align="center" keep-together="always" font-weight="bold">          </fo:block>				    	                
                </#if>
                
		        <fo:block  keep-together="always" text-align="right" font-family="Courier,monospace" white-space-collapse="false">  Date: ${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(nowTimestamp, "dd/MM/yy HH:mm:ss")}&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;</fo:block>
		        <fo:block  keep-together="always" text-align="center" font-family="Courier,monospace" white-space-collapse="false" font-size="12pt" > &#160;&#160;  </fo:block>
				<fo:block  keep-together="always" text-align="center" font-family="Courier,monospace" white-space-collapse="false" font-size="13pt" font-weight="bold" >${partyName?if_exists}</fo:block>
				<fo:block  keep-together="always" text-align="center" font-family="Courier,monospace" white-space-collapse="false" font-size="14pt" font-weight="bold" ><#if address1?has_content>${address1?if_exists},</#if><#if city?has_content>${city?if_exists},</#if><#if postalCode?has_content>${postalCode?if_exists},</#if></fo:block>
			    <fo:block  keep-together="always" text-align="left" font-family="Courier,monospace" white-space-collapse="false" font-size="5pt" > ------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------</fo:block>
			    <fo:block  keep-together="always" text-align="center" font-family="Courier,monospace" white-space-collapse="false" font-size="12pt" > &#160;&#160;  </fo:block>
			    <fo:block  keep-together="always" text-align="center" font-family="Courier,monospace" white-space-collapse="false" font-size="12pt" font-weight="bold"> DEBIT NOTE\INVOICE IDR 66 D                     TIN NO: ${tinNumber?if_exists} </fo:block>
  			    <fo:block  keep-together="always" text-align="center" font-family="Courier,monospace" white-space-collapse="false" font-size="12pt" > &#160;&#160;  </fo:block>
		         
      <#--    <#assign PremAndDeductionMap=allProdProcPriceDetail.getValue().get("PremAndDeductionMap")> -->
         
  				<fo:block>
	                 	<fo:table >
	                    <fo:table-column column-width="120pt"/>
	                    <fo:table-column column-width="350pt"/>
	            		<fo:table-column column-width="10pt"/> 		
	                    <fo:table-body>
	                    <#assign productId= PremAndDeductionMap.get("productId")?if_exists >
		         		 <#assign productNameDetails = delegator.findOne("Product", {"productId" : productId}, true)>
	                        <fo:table-row >
                                <fo:table-cell>
                            		<fo:block   text-align="left" font-size="12pt" white-space-collapse="false">BILL TO       : </fo:block>  
                       			</fo:table-cell>
                                <fo:table-cell>
                            		<fo:block   text-align="left" font-size="12pt" white-space-collapse="false" >GENERAL MANAGER(TECHNICAL) ${partyToName?if_exists}</fo:block>  
                       			</fo:table-cell>
                                <fo:table-cell>
                            		<fo:block   text-align="left" font-size="12pt" white-space-collapse="false" keep-together="always">&#160;</fo:block>  
                       			</fo:table-cell>
                            </fo:table-row >
                            <fo:table-row >      
	                    		<fo:table-cell >
                            		<fo:block   text-align="left" font-size="12pt" white-space-collapse="false" >SUPPLIED FROM :</fo:block>  
                       			</fo:table-cell>
	                    		<fo:table-cell >
                            		<fo:block   text-align="left" font-size="12pt" white-space-collapse="false" >${partyName?if_exists} (${partyId?if_exists})</fo:block>  
                       			</fo:table-cell>
	                    		<fo:table-cell >
                            		<fo:block   text-align="left" font-size="12pt" white-space-collapse="false" keep-together="always">&#160;</fo:block>  
                       			</fo:table-cell>	                    		
                			</fo:table-row>
	         		  <#if productNameDetails?has_content> 
                			<fo:table-row >
	                    		<fo:table-cell >
                            		<fo:block   text-align="left" font-size="12pt" white-space-collapse="false" >CATG 		   				: </fo:block>  
                       			</fo:table-cell>                     
	                    		<fo:table-cell >
                            		<fo:block   text-align="left" font-size="12pt" white-space-collapse="false" >${productNameDetails.get("brandName")?if_exists}</fo:block>  
                       			</fo:table-cell>
	                    		<fo:table-cell >
                            		<fo:block   text-align="left" font-size="12pt" white-space-collapse="false" keep-together="always">&#160;</fo:block>  
                       			</fo:table-cell>
	                   	</fo:table-row>
	                   	</#if>
                        <fo:table-row >
	                    		<fo:table-cell >
                            		<fo:block   text-align="left" font-size="12pt" white-space-collapse="false" >RATE 									: </fo:block>  
                       			</fo:table-cell>                     
	                    		<fo:table-cell >
                            		<fo:block   text-align="left" font-size="12pt" white-space-collapse="false" >${PremAndDeductionMap.get("price")?if_exists}</fo:block>  
                       			</fo:table-cell>
	                    		<fo:table-cell >
                            		<fo:block   text-align="left" font-size="12pt" white-space-collapse="false" keep-together="always">&#160;</fo:block>  
                       			</fo:table-cell>
                			</fo:table-row>
                	   		<fo:table-row >
                	   		   <fo:table-cell >
                            		<fo:block   text-align="left" font-size="12pt" white-space-collapse="false" >QUTY FAT					 :</fo:block>  
                       		   </fo:table-cell> 
                       		   <fo:table-cell >
                            		<fo:block   text-align="left" font-size="12pt" white-space-collapse="false" >${PremAndDeductionMap.get("fatProcPercent")?if_exists}%</fo:block>  
                       			</fo:table-cell>                       	   	
                       			<fo:table-cell >
                            		<fo:block   text-align="left" font-size="12pt" white-space-collapse="false" keep-together="always">&#160;</fo:block>  
                       		   </fo:table-cell> 
                	   		</fo:table-row>
                	   	    <fo:table-row >
                	   		   <fo:table-cell >
                            		<fo:block   text-align="left" font-size="12pt" white-space-collapse="false" >QUTY SNF 					:</fo:block>  
                       		   </fo:table-cell> 
                       		   <fo:table-cell >
                            		<fo:block   text-align="left" font-size="12pt" white-space-collapse="false" >${PremAndDeductionMap.get("snfProcPercent")?if_exists}%</fo:block>  
                       			</fo:table-cell>                       	   	
                       			<fo:table-cell >
                            		<fo:block   text-align="left" font-size="12pt" white-space-collapse="false" keep-together="always">&#160;</fo:block>  
                       		   </fo:table-cell> 
                	   		</fo:table-row>
                	   		
                    </fo:table-body>
                </fo:table>
              </fo:block>               	
       <fo:block  keep-together="always" text-align="center" font-family="Courier,monospace" white-space-collapse="false" font-size="5pt" >&#160;&#160; </fo:block>
       <fo:block  keep-together="always" text-align="left" font-size="12pt" >PAICE ${PremAndDeductionMap.get("fatPremPrice")?if_exists} FOR FAT AND ${PremAndDeductionMap.get("snfPremPrice")?if_exists?string("#0.00")} FOR SNF FOR EVERY <#if PremAndDeductionMap.get("fatPremium") == PremAndDeductionMap.get("snfPremium") > ${PremAndDeductionMap.get("snfPremium")?if_exists}%<#else>${PremAndDeductionMap.get("fatPremium")?if_exists},${PremAndDeductionMap.get("snfPremium")?if_exists}</#if> > FAT or SNF </fo:block>
       <fo:block  keep-together="always" text-align="left" font-size="12pt" >PAICE ${PremAndDeductionMap.get("snfDedPrice")?if_exists} FOR EVERY <#if PremAndDeductionMap.get("fatDed") == PremAndDeductionMap.get("snfDed") > ${PremAndDeductionMap.get("snfDed")?if_exists}%<#else>${PremAndDeductionMap.get("fatDed")?if_exists},${PremAndDeductionMap.get("snfDed")?if_exists}</#if> FAT AND SNF BELOW ${PremAndDeductionMap.get("fatProcPercent")?if_exists}% FAT and ${PremAndDeductionMap.get("snfProcPercent")?if_exists}% SNF </fo:block>
	    <fo:block  keep-together="always" text-align="center" font-family="Courier,monospace" white-space-collapse="false" font-size="5pt" >&#160;&#160; </fo:block>
	    <fo:block  keep-together="always" text-align="center" font-family="Courier,monospace" white-space-collapse="false" font-size="12pt"  font-weight="bold" >&#160;&#160; Yours Account has been debited as follows </fo:block>
	  	<fo:block  keep-together="always" text-align="center" font-family="Courier,monospace" white-space-collapse="false" font-size="12pt" font-weight="bold">STATEMENT SHOWING MILK SOLD TO MOTHER DAIRY BANGALORE PERIOD  ${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(fromDate, "dd-MMM-yyyy")} TO ${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(thruDate, "dd-MMM-yyyy")}  </fo:block>
	    <fo:block  keep-together="always" text-align="center" font-family="Courier,monospace" white-space-collapse="false" font-size="5pt" >&#160;&#160; </fo:block>
 
   
       <fo:block >
		 <fo:table width="100%" align="center" table-layout="fixed"  font-size="12pt" border-style="solid">
           <fo:table-column column-width="30pt"/>               
            <fo:table-column column-width="60pt"/>               
            <fo:table-column column-width="70pt"/>
            <fo:table-column column-width="80pt"/>
            <fo:table-column column-width="80pt"/>
            <fo:table-column column-width="50pt"/>
            <fo:table-column column-width="50pt"/>
            <fo:table-column column-width="60pt"/>
            <fo:table-column column-width="80pt"/>
             <fo:table-column column-width="80pt"/>
		    <fo:table-column column-width="80pt"/>
		    <fo:table-column column-width="100pt"/>
		    
       	<fo:table-body>
	         <fo:table-row border-style="dotted">
		          <fo:table-cell border-style="dotted"><fo:block text-align="left"  font-weight="bold"  font-size="12pt" >SI NO</fo:block></fo:table-cell>       		
		          <fo:table-cell  border-style="dotted"><fo:block text-align="center" font-weight="bold"  font-size="12pt">DATE</fo:block></fo:table-cell>       		
		          <fo:table-cell border-style="dotted"><fo:block text-align="center"  font-weight="bold"  font-size="12pt">DC NO</fo:block></fo:table-cell>       		
		          <fo:table-cell border-style="dotted"><fo:block text-align="left"  font-weight="bold"  font-size="12pt">TANKER NO</fo:block></fo:table-cell>       		
		          <fo:table-cell border-style="dotted"><fo:block text-align="right"  font-weight="bold"  font-size="12pt">ACK QTY IN KG</fo:block></fo:table-cell>       		
			     <fo:table-cell border-style="dotted"><fo:block text-align="right"  font-weight="bold"  font-size="12pt">DISP FAT%</fo:block></fo:table-cell>
		         <fo:table-cell border-style="dotted"><fo:block text-align="right"  font-weight="bold"  font-size="12pt">DISP SNF%</fo:block></fo:table-cell>
		         <fo:table-cell border-style="dotted"><fo:block text-align="right"  font-weight="bold"  font-size="12pt">UNIT PRICE</fo:block></fo:table-cell>
 		          <fo:table-cell border-style="dotted"><fo:block text-align="right"  font-weight="bold"  font-size="12pt">ACTUAL AMOUNT</fo:block></fo:table-cell>
		         <fo:table-cell border-style="dotted"><fo:block text-align="right"  font-weight="bold"  font-size="12pt">FAT PREM AMT</fo:block></fo:table-cell>
	       		 <fo:table-cell border-style="dotted"><fo:block text-align="right"  font-weight="bold"  font-size="12pt">SNF PREM AMT</fo:block></fo:table-cell>
	        		        <fo:table-cell border-style="dotted"><fo:block text-align="right"  font-weight="bold"  font-size="12pt"> AMOUNT</fo:block></fo:table-cell>
	        
	         </fo:table-row>
	         <#assign siNo=1>
        <#assign vehicleWiseDetails = vehicleWiseDetailsMap.entrySet()?if_exists>											
         <#list vehicleWiseDetails as vehicleWiseDetail>
         <#assign sno=vehicleWiseDetail.getKey()>
				  <fo:table-row border-style="dotted">
					  <fo:table-cell border-style="dotted"><fo:block text-align="left"   font-size="12pt">${sno?if_exists}  </fo:block></fo:table-cell>       	
  					  <fo:table-cell border-style="dotted"><fo:block text-align="center"  font-size="12pt">${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(vehicleWiseDetail.getValue().get("recdDate"), "dd-MM-yy")}  </fo:block></fo:table-cell>       		
  					  <fo:table-cell border-style="dotted"><fo:block text-align="center" font-size="12pt">${vehicleWiseDetail.getValue().get("dcNo")?if_exists}  </fo:block></fo:table-cell>       		
  					  <fo:table-cell border-style="dotted"><fo:block text-align="left"    font-size="12pt">${vehicleWiseDetail.getValue().get("vehicleId")?if_exists}  </fo:block></fo:table-cell>       		
  					  <fo:table-cell border-style="dotted"><fo:block text-align="right"   font-size="12pt">${vehicleWiseDetail.getValue().get("receivedQuantity")?if_exists?string("#0.00")}  </fo:block></fo:table-cell>       		
  					  <fo:table-cell border-style="dotted"><fo:block text-align="right"   font-size="12pt">${vehicleWiseDetail.getValue().get("fatPercent")?if_exists?string("#0.0")}  </fo:block></fo:table-cell>       		
  					  <fo:table-cell border-style="dotted"><fo:block text-align="right"   font-size="12pt">${vehicleWiseDetail.getValue().get("snfPercent")?if_exists?string("#0.00")}  </fo:block></fo:table-cell>       		
  					  <fo:table-cell border-style="dotted"><fo:block text-align="right"   font-size="12pt">${vehicleWiseDetail.getValue().get("unitPrice")?if_exists?string("#0.00")}  </fo:block></fo:table-cell>       		
  					  <fo:table-cell border-style="dotted"><fo:block text-align="right"   font-size="12pt">${vehicleWiseDetail.getValue().get("actualAmt")?if_exists?string("#0.00")}  </fo:block></fo:table-cell>       		
  					  <fo:table-cell border-style="dotted"><fo:block text-align="right"   font-size="12pt">${vehicleWiseDetail.getValue().get("fatPremAmt")?if_exists?string("#0.00")}  </fo:block></fo:table-cell>       		
  					  <fo:table-cell border-style="dotted"><fo:block text-align="right"    font-size="12pt">${vehicleWiseDetail.getValue().get("snfPremAmt")?if_exists?string("#0.00")}  </fo:block></fo:table-cell>       		
  					  <fo:table-cell border-style="dotted"><fo:block text-align="right"    font-size="12pt">${vehicleWiseDetail.getValue().get("vehicleTotAmt")?if_exists?string("#0.00")}  </fo:block></fo:table-cell>       		
		           </fo:table-row>
		           
	         </#list> 
	           <fo:table-row border-style="dotted">
					  <fo:table-cell><fo:block text-align="left"    font-size="12pt">  </fo:block></fo:table-cell>       	
  					  <fo:table-cell><fo:block text-align="right" font-weight="bold"  font-size="12pt">Total  </fo:block></fo:table-cell>       		
					  <fo:table-cell><fo:block text-align="left"    font-size="12pt">  </fo:block></fo:table-cell>       	
					  <fo:table-cell><fo:block text-align="right"    font-size="12pt">  </fo:block></fo:table-cell>       	
  					  <fo:table-cell><fo:block text-align="right"  font-weight="bold"  font-size="12pt">${totQuantity?if_exists?string("#0.00")}  </fo:block></fo:table-cell>       	
  					
					  <fo:table-cell><fo:block text-align="right"    font-size="12pt">  </fo:block></fo:table-cell>       	
					  <fo:table-cell><fo:block text-align="right"    font-size="12pt">  </fo:block></fo:table-cell>       	
					  <fo:table-cell><fo:block text-align="right"    font-size="12pt">  </fo:block></fo:table-cell>       	
					  <fo:table-cell><fo:block text-align="right"    font-size="12pt">  </fo:block></fo:table-cell>       	
					  <fo:table-cell><fo:block text-align="right"    font-size="12pt">  </fo:block></fo:table-cell>       	
					  <fo:table-cell><fo:block text-align="right"    font-size="12pt">  </fo:block></fo:table-cell>       	
					  <fo:table-cell><fo:block text-align="right"  font-weight="bold"  font-size="12pt"> ${totAmount?if_exists?string("#0.00")} </fo:block></fo:table-cell>       	
		           </fo:table-row>
   		        </fo:table-body>   
           </fo:table>		
       </fo:block>
        <#assign pageNumber = pageNumber+1>	
        </#list>
     
     
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


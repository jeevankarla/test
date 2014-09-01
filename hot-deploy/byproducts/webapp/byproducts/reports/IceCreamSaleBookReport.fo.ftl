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
            <fo:simple-page-master master-name="main" page-height="12in" page-width="15in"  margin-left=".3in" margin-right=".3in" margin-top=".5in">
                <fo:region-body margin-top="1.8in"/>
                <fo:region-before extent="1in"/>
                <fo:region-after extent="1in"/>
            </fo:simple-page-master>
        </fo:layout-master-set>
        ${setRequestAttribute("OUTPUT_FILENAME", "IceCreamSalesReport.pdf")}
        <#if errorMessage?has_content>
	<fo:page-sequence master-reference="main">
	<fo:flow flow-name="xsl-region-body" font-family="Helvetica">
	   <fo:block font-size="14pt">
	           ${errorMessage}.
        </fo:block>
	</fo:flow>
	</fo:page-sequence>	
	<#else>
       <#if partWiseSaleMap?has_content>
	        <fo:page-sequence master-reference="main" font-size="12pt">	
	        	<fo:static-content flow-name="xsl-region-before" font-family="Courier,monospace">
	        		<fo:block text-align="center" font-weight="bold" keep-together="always"  white-space-collapse="false">OFICE OF THE DIRECTOR :MOTHER DAIRY :G.KV.K POST :BANGALORE -560 65</fo:block>
                	<fo:block text-align="center"  keep-together="always"  white-space-collapse="false" font-weight="bold">CUSTOMERWISE SALES REGISTER REPORT</fo:block>
          			<fo:block text-align="center" font-weight="bold"  keep-together="always"  white-space-collapse="false"> <#if categoryType=="ICE_CREAM_NANDINI">NANDINI</#if><#if categoryType=="ICE_CREAM_AMUL">AMUL</#if> ICE CREAM SALES BOOK FOR THE PERIOD- ${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(fromDate, "dd/MM/yyyy")} - ${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(thruDate, "dd/MM/yyyy")} </fo:block>
          			<fo:block text-align="left"  keep-together="always"  font-family="Courier,monospace" font-weight="bold" white-space-collapse="false"> UserLogin:<#if userLogin?exists>${userLogin.userLoginId?if_exists}</#if>               &#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;Print Date :${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(nowTimestamp, "dd/MM/yy HH:mm:ss")}</fo:block>
          			<fo:block>----------------------------------------------------------------------------------------------------------------------------------</fo:block>
            	    <fo:block text-align="left" font-weight="bold" font-size="12pt" keep-together="always" font-family="Courier,monospace" white-space-collapse="false">Retailer                        Quantity       Average        Ex-factory    ED             VAT(Rs)    C.S.T(Rs)       Total(Rs)</fo:block>
        			<fo:block text-align="left" font-weight="bold" font-size="12pt" keep-together="always" font-family="Courier,monospace" white-space-collapse="false">Name                            (In Ltrs)                     Value(Rs)     Value(Rs)     </fo:block>
	        		<fo:block>----------------------------------------------------------------------------------------------------------------------------------</fo:block>
            	</fo:static-content>	        	
	        	<fo:flow flow-name="xsl-region-body"   font-family="Courier,monospace">		
        			<fo:block>
        				<fo:table>
		                    <fo:table-column column-width="230pt"/>
		                    <fo:table-column column-width="55pt"/>
		                    <fo:table-column column-width="110pt"/> 
		               	    <fo:table-column column-width="110pt"/>
		            		<fo:table-column column-width="110pt"/> 		
		            		<fo:table-column column-width="85pt"/>
		            		<fo:table-column column-width="90pt"/>
		            		<fo:table-column column-width="125pt"/>
		            		<fo:table-column column-width="95pt"/>
		                    <fo:table-body>
		                    <#assign partWiseSales = partWiseSaleMap.entrySet()>
		                    <#assign totalQty=0> 
		                    <#assign totalAvg=0> 
		                    <#assign totalBasicRev=0>
		                    <#assign totalBedRev=0>
		                    <#assign totalVatRev=0>
		                    <#assign totalCstRev=0>
		                    <#assign total=0>
       							<#list partWiseSales as partWiseSaleDetails>
       							<#assign totalQty=totalQty+partWiseSaleDetails.getValue().get("quantity")>
       							<#assign totalAvg=totalQty+partWiseSaleDetails.getValue().get("average")>
       							<#assign totalBasicRev=totalBasicRev+partWiseSaleDetails.getValue().get("basicRevenue")>
       							<#assign totalBedRev=totalBedRev+partWiseSaleDetails.getValue().get("bedRevenue")>
       							<#assign totalVatRev=totalVatRev+partWiseSaleDetails.getValue().get("vatRevenue")>
       							<#assign totalCstRev=totalCstRev+partWiseSaleDetails.getValue().get("cstRevenue")>
       							<#assign total=total+partWiseSaleDetails.getValue().get("total")>
       							
       							<fo:table-row>
					                    <fo:table-cell>
							            	<fo:block  keep-together="always" text-align="left" font-size="12pt" white-space-collapse="false" font-weight="bold">${partWiseSaleDetails.getKey()}</fo:block>  
							            </fo:table-cell>
							             <fo:table-cell>
							            	<fo:block  keep-together="always" text-align="right" font-size="12pt" white-space-collapse="false" font-weight="bold">${partWiseSaleDetails.getValue().get("quantity")}</fo:block>  
							            </fo:table-cell>
							             <fo:table-cell>
							            	<fo:block  keep-together="always" text-align="right" font-size="12pt" white-space-collapse="false" font-weight="bold">${partWiseSaleDetails.getValue().get("average")?string("#0.00")}</fo:block>  
							            </fo:table-cell>
							             <fo:table-cell>
							            	<fo:block  keep-together="always" text-align="right" font-size="12pt" white-space-collapse="false" font-weight="bold">${partWiseSaleDetails.getValue().get("basicRevenue")}</fo:block>  
							            </fo:table-cell>
							            <fo:table-cell>
							            	<fo:block  keep-together="always" text-align="right" font-size="12pt" white-space-collapse="false" font-weight="bold">${partWiseSaleDetails.getValue().get("bedRevenue")}</fo:block>  
							            </fo:table-cell>
							            <fo:table-cell>
							            	<fo:block  keep-together="always" text-align="right" font-size="12pt" white-space-collapse="false" font-weight="bold">${partWiseSaleDetails.getValue().get("vatRevenue")}</fo:block>  
							            </fo:table-cell>
							            <fo:table-cell>
							            	<fo:block  keep-together="always" text-align="right" font-size="12pt" white-space-collapse="false" font-weight="bold">${partWiseSaleDetails.getValue().get("cstRevenue")}</fo:block>  
							            </fo:table-cell>
							            <fo:table-cell>
							            	<fo:block  keep-together="always" text-align="right" font-size="12pt" white-space-collapse="false" font-weight="bold">${partWiseSaleDetails.getValue().get("total")}</fo:block>  
							            </fo:table-cell>
							     </fo:table-row>
								</#list>
								<fo:table-row> 
							      <fo:table-cell>   						
									<fo:block>----------------------------------------------------------------------------------------------------------------------------------</fo:block>
          						  </fo:table-cell>
          						  </fo:table-row> 
								<fo:table-row>
					                    <fo:table-cell>
							            	<fo:block  keep-together="always" text-align="left" font-size="12pt" white-space-collapse="false" font-weight="bold">Total</fo:block>  
							            </fo:table-cell>
							             <fo:table-cell>
							            	<fo:block  keep-together="always" text-align="right" font-size="12pt" white-space-collapse="false" font-weight="bold">${totalQty}</fo:block>  
							            </fo:table-cell>
							             <fo:table-cell>
							            	<fo:block  keep-together="always" text-align="right" font-size="12pt" white-space-collapse="false" font-weight="bold">${totalAvg?string("#0.00")}</fo:block>  
							            </fo:table-cell>
							             <fo:table-cell>
							            	<fo:block  keep-together="always" text-align="right" font-size="12pt" white-space-collapse="false" font-weight="bold">${totalBasicRev}</fo:block>  
							            </fo:table-cell>
							            <fo:table-cell>
							            	<fo:block  keep-together="always" text-align="right" font-size="12pt" white-space-collapse="false" font-weight="bold">${totalBedRev}</fo:block>  
							            </fo:table-cell>
							            <fo:table-cell>
							            	<fo:block  keep-together="always" text-align="right" font-size="12pt" white-space-collapse="false" font-weight="bold">${totalVatRev}</fo:block>  
							            </fo:table-cell>
							            <fo:table-cell>
							            	<fo:block  keep-together="always" text-align="right" font-size="12pt" white-space-collapse="false" font-weight="bold">${totalCstRev}</fo:block>  
							            </fo:table-cell>
							            <fo:table-cell>
							            	<fo:block  keep-together="always" text-align="right" font-size="12pt" white-space-collapse="false" font-weight="bold">${total}</fo:block>  
							            </fo:table-cell>
							     </fo:table-row>
								<fo:table-row> 
							      <fo:table-cell>   						
									<fo:block>----------------------------------------------------------------------------------------------------------------------------------</fo:block>
          						  </fo:table-cell>
          						  </fo:table-row> 
								<fo:table-row> 
							      <fo:table-cell number-columns-spanned="2" >   						
							 	      <fo:block linefeed-treatment="preserve">&#xA;</fo:block>  
							 	   </fo:table-cell>
						 	         <fo:table-cell number-columns-spanned="2">   						
							 	         <fo:block linefeed-treatment="preserve">&#xA;</fo:block> 
							 	   </fo:table-cell>
							 	   <fo:table-cell number-columns-spanned="2">   						
							 	        <fo:block linefeed-treatment="preserve">&#xA;</fo:block> 
							 	   </fo:table-cell>
						 	         <fo:table-cell >   						
							 	         <fo:block linefeed-treatment="preserve">&#xA;</fo:block> 
							 	   </fo:table-cell>
							 </fo:table-row>	
							<fo:table-row> 
							      <fo:table-cell number-columns-spanned="2" >   						
							 	      <fo:block linefeed-treatment="preserve">&#xA;</fo:block>  
							 	   </fo:table-cell>
						 	         <fo:table-cell number-columns-spanned="2">   						
							 	         <fo:block linefeed-treatment="preserve">&#xA;</fo:block> 
							 	   </fo:table-cell>
							 	   <fo:table-cell number-columns-spanned="2">   						
							 	        <fo:block linefeed-treatment="preserve">&#xA;</fo:block> 
							 	   </fo:table-cell>
						 	         <fo:table-cell >   						
							 	         <fo:block linefeed-treatment="preserve">&#xA;</fo:block> 
							 	   </fo:table-cell>
							 </fo:table-row>	
							<fo:table-row> 
							      <fo:table-cell number-columns-spanned="2" >   						
							 	      <fo:block linefeed-treatment="preserve">&#xA;</fo:block>  
							 	   </fo:table-cell>
						 	         <fo:table-cell number-columns-spanned="2">   						
							 	         <fo:block linefeed-treatment="preserve">&#xA;</fo:block> 
							 	   </fo:table-cell>
							 	   <fo:table-cell number-columns-spanned="2">   						
							 	        <fo:block linefeed-treatment="preserve">&#xA;</fo:block> 
							 	   </fo:table-cell>
						 	         <fo:table-cell >   						
							 	         <fo:block linefeed-treatment="preserve">&#xA;</fo:block> 
							 	   </fo:table-cell>
							 </fo:table-row>	
								<fo:table-row> 
							      <fo:table-cell number-columns-spanned="2" >   						
							 	         <fo:block text-align="left" white-space-collapse="false" font-size="12pt" keep-together="always" font-weight="bold">Verifed By</fo:block>
							 	   </fo:table-cell>
						 	         <fo:table-cell number-columns-spanned="2">   						
							 	         <fo:block text-align="left" white-space-collapse="false" font-size="12pt"  keep-together="always" font-weight="bold">AM(F)/DM(F)</fo:block>
							 	   </fo:table-cell>
							 	   <fo:table-cell number-columns-spanned="3">   						
							 	         <fo:block text-align="left" white-space-collapse="false" font-size="12pt"  keep-together="always" font-weight="bold">M(F)/GM(F)</fo:block>
							 	   </fo:table-cell>
						 	         <fo:table-cell number-columns-spanned="2">   						
							 	         <fo:block text-align="left" white-space-collapse="false" font-size="12pt" keep-together="always" font-weight="bold">Pre-Audit</fo:block>
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
			        	${uiLabelMap.NoOrdersFound}.
			   		 </fo:block>
				</fo:flow>
			</fo:page-sequence>	
		</#if>   
 </#if>
 </fo:root>
</#escape>
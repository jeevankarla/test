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
            <fo:simple-page-master master-name="main" page-height="12in" page-width="15in"  margin-left=".3in" margin-right=".3in" margin-top=".2in">
                <fo:region-body margin-top="1.6in"/>
                <fo:region-before extent="1in"/>
                <fo:region-after extent="1in"/>
            </fo:simple-page-master>
        </fo:layout-master-set>
        ${setRequestAttribute("OUTPUT_FILENAME", "PurchaseRegisterBookReport.pdf")}
        <#if errorMessage?has_content>
	<fo:page-sequence master-reference="main">
	<fo:flow flow-name="xsl-region-body" font-family="Helvetica">
	   <fo:block font-size="14pt">
	           ${errorMessage}.
        </fo:block>
	</fo:flow>
	</fo:page-sequence>	
	<#else>
       <#if invoiceMap?has_content>
	        <fo:page-sequence master-reference="main" font-size="11pt">	
	        	<fo:static-content flow-name="xsl-region-before" font-family="Courier,monospace">
	        		<fo:block  keep-together="always" text-align="center" font-family="Courier,monospace" white-space-collapse="false" font-weight="bold">${uiLabelMap.KMFDairyHeader}</fo:block>
					<fo:block  keep-together="always" text-align="center" font-family="Courier,monospace" white-space-collapse="false" font-weight="bold">${uiLabelMap.KMFDairySubHeader}</fo:block>
                	<fo:block text-align="center"  keep-together="always"  white-space-collapse="false" font-weight="bold">PURCHASE REGISTER REPORT</fo:block>
          			<fo:block text-align="left"  keep-together="always"  font-family="Courier,monospace" font-weight="bold" white-space-collapse="false"> UserLogin:<#if userLogin?exists>${userLogin.userLoginId?if_exists}</#if>               &#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;Print Date :${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(nowTimestamp, "dd/MM/yy HH:mm:ss")}</fo:block>
          			<fo:block>---------------------------------------------------------------------------------------------------------------------------------------------------------------</fo:block>
            	    <fo:block text-align="left" font-weight="bold" font-size="12pt" keep-together="always" font-family="Courier,monospace" white-space-collapse="false">Invoice        	Retailer Name    	Ex-Tax    					ED            VAT      	C.S.T         Freight    			Discount   	Insurance			Total    					TIN</fo:block>
        			<fo:block text-align="left" font-weight="bold" font-size="12pt" keep-together="always" font-family="Courier,monospace" white-space-collapse="false">Number        	              					Value     				Value   							    																	Pack,Forwd,Other																																									Number</fo:block>
	        		<fo:block>---------------------------------------------------------------------------------------------------------------------------------------------------------------</fo:block>
            	</fo:static-content>	        	
	        	<fo:flow flow-name="xsl-region-body"   font-family="Courier,monospace">		
        			<fo:block>
        				<fo:table>
		                    <fo:table-column column-width="100pt"/>
		                    <fo:table-column column-width="70pt"/>
		                    <fo:table-column column-width="110pt"/> 
		               	    <fo:table-column column-width="90pt"/>
		            		<fo:table-column column-width="100pt"/> 		
		            		<fo:table-column column-width="85pt"/>
		            		<fo:table-column column-width="110pt"/>
		            		<fo:table-column column-width="110pt"/>
		            		<fo:table-column column-width="90pt"/>
		            		<fo:table-column column-width="80pt"/>
		            		<fo:table-column column-width="120pt"/>
		            		<fo:table-column column-width="130pt"/>
		                    <fo:table-body>
		                    <#assign invoiceDetails = invoiceMap.entrySet()>
		                    <#assign totalBasicRev=0>
		                    <#assign totalBedRev=0>
		                    <#assign totalVatRev=0>
		                    <#assign totalCstRev=0>
		                    <#assign totalRevenue=0>
		                    <#assign totalFreightAmount=0>
		                    <#assign totalDiscountAmount=0>
		                    <#assign totalInsuranceAmount=0>
		                    <#assign totalAmount=0>
		                    
       							<#list invoiceDetails as invoiceDet>
       							<#assign totalBasicRev=totalBasicRev+invoiceDet.getValue().get("basicRevenue")?if_exists>
       							<#assign totalBedRev=totalBedRev+invoiceDet.getValue().get("bedRevenue")?if_exists>
       							<#assign totalVatRev=totalVatRev+invoiceDet.getValue().get("vatRevenue")?if_exists>
       							<#assign totalCstRev=totalCstRev+invoiceDet.getValue().get("cstRevenue")?if_exists>
       							<#assign totalRevenue=totalRevenue+invoiceDet.getValue().get("totalRevenue")?if_exists>
       							<#assign totalFreightAmount=totalFreightAmount+invoiceDet.getValue().get("freightAmount")?if_exists>
       							<#assign totalDiscountAmount=totalDiscountAmount+invoiceDet.getValue().get("discountAmount")?if_exists>
       							<#assign totalInsuranceAmount=totalInsuranceAmount+invoiceDet.getValue().get("insuranceAmount")?if_exists>
       							<#assign totalAmount=totalAmount+invoiceDet.getValue().get("grandTotal")?if_exists>
       							<fo:table-row>
       								<fo:table-cell>
							            <fo:block  keep-together="always" font-weight = "bold" text-align="left" font-size="11pt" white-space-collapse="false" >${invoiceDet.getValue().get("invoiceDate")?if_exists}</fo:block>  
							         </fo:table-cell>
							         <fo:table-cell number-columns-spanned="2">
							            <fo:block  keep-together="always" text-align="left" font-size="11pt" font-weight = "bold" white-space-collapse="false" >MRR NUMBER: ${invoiceDet.getValue().get("mrrNumber")?if_exists}</fo:block>  
							         </fo:table-cell>
							         <fo:table-cell number-columns-spanned="2">
							            <fo:block  keep-together="always" text-align="left" font-size="11pt" font-weight = "bold" white-space-collapse="false" >PO NUMBER: ${invoiceDet.getValue().get("poNumber")?if_exists}</fo:block>  
							         </fo:table-cell>
							         <fo:table-cell number-columns-spanned="3">
							            <fo:block  keep-together="always" text-align="left" font-size="11pt" font-weight = "bold" white-space-collapse="false" >SUP INVOICE NUMBER: ${invoiceDet.getValue().get("supInvNumber")?if_exists}</fo:block>  
							         </fo:table-cell>
							         <fo:table-cell>
							            <fo:block  keep-together="always" text-align="left" font-size="11pt" font-weight = "bold" white-space-collapse="false" >SUP INVOICE DATE: ${invoiceDet.getValue().get("supInvDate")?if_exists}</fo:block>  
							         </fo:table-cell>
							    </fo:table-row>
							    <fo:table-row> 
							      <fo:table-cell>   						
									<fo:block>---------------------------------------------------------------------------------------------------------------------------------------------------------------</fo:block>
          						  </fo:table-cell>
          						</fo:table-row>
       							<fo:table-row>
							             <fo:table-cell>
							            	<fo:block  keep-together="always" font-weight = "bold" text-align="left" font-size="11pt" white-space-collapse="false" >${invoiceDet.getKey()?if_exists}</fo:block>  
							            </fo:table-cell>
							             <fo:table-cell>
							            	<fo:block   text-align="left" font-size="11pt" white-space-collapse="false" >${invoiceDet.getValue().get("partyName")?if_exists}</fo:block>  
							            </fo:table-cell>
							             <fo:table-cell>
							            	<fo:block  keep-together="always" text-align="right" font-size="11pt" white-space-collapse="false" >${invoiceDet.getValue().get("basicRevenue")?if_exists?string("#0.00")}</fo:block>  
							            </fo:table-cell>
							            <fo:table-cell>
							            	<fo:block  keep-together="always" text-align="right" font-size="11pt" white-space-collapse="false" >${invoiceDet.getValue().get("bedRevenue")?if_exists?string("#0.00")}</fo:block>  
							            </fo:table-cell>
							            <fo:table-cell>
							            	<fo:block  keep-together="always" text-align="right" font-size="11pt" white-space-collapse="false" >${invoiceDet.getValue().get("vatRevenue")?if_exists?string("#0.00")}</fo:block>  
							            </fo:table-cell>
							            <fo:table-cell>
							            	<fo:block  keep-together="always" text-align="right" font-size="11pt" white-space-collapse="false" >${invoiceDet.getValue().get("cstRevenue")?if_exists?string("#0.00")}</fo:block>  
							            </fo:table-cell>
							            <fo:table-cell>
							            	<fo:block  keep-together="always" text-align="right" font-size="11pt" white-space-collapse="false" >${invoiceDet.getValue().get("freightAmount")?if_exists?string("#0.00")}</fo:block>  
							            </fo:table-cell>
							            <fo:table-cell>
							            	<fo:block  keep-together="always" text-align="right" font-size="11pt" white-space-collapse="false" >${invoiceDet.getValue().get("discountAmount")?if_exists?string("#0.00")}</fo:block>  
							            </fo:table-cell>
							             <fo:table-cell>
							            	<fo:block  keep-together="always" text-align="right" font-size="11pt" white-space-collapse="false" >${invoiceDet.getValue().get("insuranceAmount")?if_exists?string("#0.00")}</fo:block>  
							            </fo:table-cell>
							            <fo:table-cell>
							            	<fo:block  keep-together="always" text-align="right" font-size="11pt" white-space-collapse="false" >${invoiceDet.getValue().get("grandTotal")?if_exists?string("#0.00")}</fo:block>  
							            </fo:table-cell>
							            <fo:table-cell>
							            	<fo:block  keep-together="always" text-align="center" font-size="11pt" white-space-collapse="false" >${invoiceDet.getValue().get("tinNumber")?if_exists}</fo:block>  
							            </fo:table-cell>
							     </fo:table-row>
							     <fo:table-row> 
							      <fo:table-cell>   						
									<fo:block>---------------------------------------------------------------------------------------------------------------------------------------------------------------</fo:block>
          						  </fo:table-cell>
          						  </fo:table-row>
								</#list>
								<fo:table-row>
					                    <fo:table-cell>
							            	<fo:block  keep-together="always" text-align="left" font-size="11pt" white-space-collapse="false" font-weight="bold">Grand Total</fo:block>  
							            </fo:table-cell>
							             <fo:table-cell>
							            	<fo:block  keep-together="always" text-align="right" font-size="11pt" white-space-collapse="false" font-weight="bold"></fo:block>  
							            </fo:table-cell>
							             <fo:table-cell>
							            	<fo:block  keep-together="always" text-align="right" font-size="11pt" white-space-collapse="false" font-weight="bold">${totalBasicRev?if_exists?string("#0.00")}</fo:block>  
							            </fo:table-cell>
							            <fo:table-cell>
							            	<fo:block  keep-together="always" text-align="right" font-size="11pt" white-space-collapse="false" font-weight="bold">${totalBedRev?if_exists?string("#0.00")}</fo:block>  
							            </fo:table-cell>
							            <fo:table-cell>
							            	<fo:block  keep-together="always" text-align="right" font-size="11pt" white-space-collapse="false" font-weight="bold">${totalVatRev?if_exists?string("#0.00")}</fo:block>  
							            </fo:table-cell>
							            <fo:table-cell>
							            	<fo:block  keep-together="always" text-align="right" font-size="11pt" white-space-collapse="false" font-weight="bold">${totalCstRev?if_exists?string("#0.00")}</fo:block>  
							            </fo:table-cell>
							            <fo:table-cell>
							            	<fo:block  keep-together="always" text-align="right" font-size="11pt" white-space-collapse="false" font-weight="bold">${totalFreightAmount?if_exists?string("#0.00")}</fo:block>  
							            </fo:table-cell>
							            <fo:table-cell>
							            	<fo:block  keep-together="always" text-align="right" font-size="11pt" white-space-collapse="false" font-weight="bold">${totalDiscountAmount?if_exists?string("#0.00")}</fo:block>  
							            </fo:table-cell>
							            <fo:table-cell>
							            	<fo:block  keep-together="always" text-align="right" font-size="11pt" white-space-collapse="false" font-weight="bold">${totalInsuranceAmount?if_exists?string("#0.00")}</fo:block>  
							            </fo:table-cell>
							            <fo:table-cell>
							            	<fo:block  keep-together="always" text-align="right" font-size="11pt" white-space-collapse="false" font-weight="bold">${totalAmount?if_exists?string("#0.00")}</fo:block>  
							            </fo:table-cell>
							     </fo:table-row>
								<fo:table-row> 
							      <fo:table-cell>   						
									<fo:block>---------------------------------------------------------------------------------------------------------------------------------------------------------------</fo:block>
          						  </fo:table-cell>
          						  </fo:table-row>
          						  <fo:table-row> 
							      <fo:table-cell>   						
									<fo:block keep-together="always" font-weight = "bold" font-style="italic">*All figures are in Rupees.</fo:block>
          						  </fo:table-cell>
          						  </fo:table-row>
          						  <#--<#assign totalVat = 0>
          						  <#if vatMap?has_content> 
          						  <#assign vatList = vatMap.entrySet()>
          						  <#list vatList as vat>
          						  <fo:table-row>
          						  	<fo:table-cell>
							            	<fo:block  keep-together="always" text-align="right" font-size="11pt" white-space-collapse="false" font-weight="bold"></fo:block>  
							         </fo:table-cell>
          						  	<fo:table-cell>
							            	<fo:block  keep-together="always" text-align="right" font-size="11pt" white-space-collapse="false" font-weight="bold">Vat @ ${vat.getKey()} = </fo:block>  
							         </fo:table-cell>
							         <#assign totalVat = totalVat+(vat.getValue()?if_exists)>
							         <fo:table-cell>
							            	<fo:block  keep-together="always" text-align="right" font-size="11pt" white-space-collapse="false" font-weight="bold">${vat.getValue()?if_exists?string("#0.00")}</fo:block>  
							         </fo:table-cell>
          						  </fo:table-row>
          						  </#list>
          						  <fo:table-row>
          						  	<fo:table-cell>
							            	<fo:block  keep-together="always" text-align="right" font-size="11pt" white-space-collapse="false" font-weight="bold"></fo:block>  
							         </fo:table-cell>
          						  	<fo:table-cell>
							            	<fo:block  keep-together="always" text-align="right" font-size="11pt" white-space-collapse="false" font-weight="bold">Total = </fo:block>  
							         </fo:table-cell>
							         <fo:table-cell>
							            	<fo:block  keep-together="always" text-align="right" font-size="11pt" white-space-collapse="false" font-weight="bold">${totalVat?if_exists?string("#0.00")}</fo:block>  
							         </fo:table-cell>
          						  </fo:table-row>
          						  </#if>-->
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
							 	         <fo:block text-align="left" white-space-collapse="false" font-size="11pt" keep-together="always" font-weight="bold">Verifed By</fo:block>
							 	   </fo:table-cell>
						 	         <fo:table-cell number-columns-spanned="2">   						
							 	         <fo:block text-align="right" white-space-collapse="false" font-size="11pt"  keep-together="always" font-weight="bold">&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;AM(F)/DM(F)</fo:block>
							 	   </fo:table-cell>
							 	   <fo:table-cell number-columns-spanned="4">   						
							 	         <fo:block text-align="right" white-space-collapse="false" font-size="11pt"  keep-together="always" font-weight="bold">&#160;&#160;&#160;&#160;&#160;&#160;M(F)/GM(F)</fo:block>
							 	   </fo:table-cell>
						 	         <fo:table-cell number-columns-spanned="2">   						
							 	         <fo:block text-align="right" white-space-collapse="false" font-size="11pt" keep-together="always" font-weight="bold">&#160;&#160;&#160;&#160;&#160;&#160;Pre-Audit</fo:block>
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
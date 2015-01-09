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
            margin-top="0.1in" margin-bottom=".7in" margin-left=".5in" margin-right=".5in">
        <fo:region-body margin-top="1.0in"/>
        <fo:region-before extent="1.in"/>
        <fo:region-after extent="1.5in"/>        
    </fo:simple-page-master>   
</fo:layout-master-set>
   <#if produtMap?has_content>
    <fo:page-sequence master-reference="main" force-page-count="no-force" font-family="Courier,monospace">					
		<fo:static-content flow-name="xsl-region-before">
		   <fo:block  keep-together="always" text-align="center" font-family="Courier,monospace" white-space-collapse="false" font-size="12pt" > &#160;&#160;  </fo:block>
				<fo:block  keep-together="always" text-align="center" font-family="Courier,monospace" white-space-collapse="false" font-size="12pt" font-weight="bold" >${uiLabelMap.KMFDairyHeader}</fo:block>
				<fo:block  keep-together="always" text-align="center" font-family="Courier,monospace" white-space-collapse="false" font-size="12pt" font-weight="bold" >${uiLabelMap.KMFDairySubHeader}</fo:block>
			    <fo:block  keep-together="always" text-align="center" font-family="Courier,monospace" white-space-collapse="false" font-size="5pt" >-----------------------------------------------------------------------------------------------------------------------------------------------</fo:block>
			    <fo:block  keep-together="always" text-align="center" font-family="Courier,monospace" white-space-collapse="false" font-size="12pt" > &#160;&#160;  </fo:block>
       </fo:static-content>
       <fo:flow flow-name="xsl-region-body"   font-family="Courier,monospace">	
           <fo:block  keep-together="always" text-align="left" font-family="Courier,monospace" white-space-collapse="false" font-size="12pt" >TEL NO:22179004 /41           FAX :080-20462652                         TIN:${allDetailsMap.get("tinNumber")?if_exists} </fo:block>
           <fo:block  keep-together="always" text-align="left" font-family="Courier,monospace" white-space-collapse="false" font-size="12pt" >&#160;&#160;     22179074 /55           Email: purchase@motherdairykmf.in         KST NO: ${allDetailsMap.get("kstNumber")?if_exists} </fo:block>
           <fo:block  keep-together="always" text-align="left" font-family="Courier,monospace" white-space-collapse="false" font-size="12pt" >&#160;&#160;                                                                      CST NO: ${allDetailsMap.get("cstNumber")?if_exists} </fo:block>
           <fo:block  keep-together="always" text-align="center" font-family="Courier,monospace" white-space-collapse="false" font-size="12pt" >---------------------------------------------------------------------------------------------- </fo:block>
	       <fo:block  keep-together="always" text-align="center" font-family="Courier,monospace" white-space-collapse="false" font-size="12pt" font-weight="bold">AMENDED PURCHASE ORDER </fo:block>  
	       <fo:block  keep-together="always" text-align="left" font-family="Courier,monospace" white-space-collapse="false" font-size="12pt" >PO NO:${allDetailsMap.get("orderId")?if_exists}                                                       PO DATED:${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(allDetailsMap.get("orderDate")?if_exists, "dd-MMM-yy")}</fo:block>
	       <fo:block  keep-together="always" text-align="left" font-family="Courier,monospace" white-space-collapse="false" font-size="12pt" >To                                                                 FAX NO: ${allDetailsMap.get("faxNumber")?if_exists}</fo:block>
	       <fo:block text-align="left" white-space-collapse="false">${partyAddressMap.get("address1")?if_exists}</fo:block>
		   <fo:block text-align="left" white-space-collapse="false">${partyAddressMap.get("address2")?if_exists}</fo:block>
		   <fo:block text-align="left" white-space-collapse="false">${partyAddressMap.get("city")?if_exists}</fo:block>				 
		   <fo:block text-align="left" white-space-collapse="false">${partyAddressMap.get("postalCode")?if_exists}</fo:block>
		   	       <fo:block  keep-together="always" text-align="left" font-family="Courier,monospace" white-space-collapse="false" font-size="12pt" >PHONE NO                : ${contactNumber?if_exists}</fo:block>	                
	       <fo:block  keep-together="always" text-align="left" font-family="Courier,monospace" white-space-collapse="false" font-size="12pt" >VENDOR CODE             : ${partyAddressMap.get("fromPartyId")?if_exists}</fo:block>
	       <fo:block  keep-together="always" text-align="left" font-family="Courier,monospace" white-space-collapse="false" font-size="12pt" >MOTHER DAIRY ENQUIRY NO : ${allDetailsMap.get("custRequestId")?if_exists}</fo:block>
	       <fo:block  keep-together="always" text-align="left" font-family="Courier,monospace" white-space-collapse="false" font-size="12pt" >YOUR QUOTATION NO       : ${allDetailsMap.get("quoteId")?if_exists}  </fo:block>
	       <fo:block  font-size="12pt" keep-together="always"  white-space-collapse="false">With reference to your Quotation/Proforma Invoice, we are pleased to place order for the</fo:block>
	       <fo:block  font-size="12pt" keep-together="always"  white-space-collapse="false">supply of materials as detailed below.</fo:block>
	       <fo:block linefeed-treatment="preserve">&#xA;</fo:block>
	       <fo:block font-family="Courier,monospace">
	          <fo:table>
				  <fo:table-column column-width="80pt"/>
				  <fo:table-column column-width="120pt"/>
				  <fo:table-column column-width="150pt"/>
				  <fo:table-column column-width="80pt"/>
				  <fo:table-column column-width="60pt"/>
				   <fo:table-column column-width="120pt"/>
					  <fo:table-body>
					      <fo:table-row >
							  <fo:table-cell >
								  <fo:block text-align="left" keep-together="always">&#160;--------------------------------------------------------------------------------------</fo:block>
							  </fo:table-cell>
						  </fo:table-row>
					      <fo:table-row>
					          <fo:table-cell>
								  <fo:block text-align="center" >SL.No</fo:block>
							  </fo:table-cell>
							  <fo:table-cell >
								  <fo:block text-align="left"  >ITEM CODE</fo:block>
							  </fo:table-cell>
							  <fo:table-cell >
								 <fo:block text-align="left" >DESCRIPTION</fo:block>
							  </fo:table-cell>
							  <fo:table-cell >
								 <fo:block text-align="center" keep-together="always" >UNIT</fo:block>
							  </fo:table-cell>
							  <fo:table-cell >
								  <fo:block text-align="right" keep-together="always" >QTY</fo:block>
							  </fo:table-cell>
							  <fo:table-cell >
								  <fo:block text-align="right" keep-together="always" >UNIT RATE</fo:block>
							  </fo:table-cell>
					     </fo:table-row>
					     <fo:table-row >
							<fo:table-cell >
							    <fo:block text-align="left" keep-together="always">&#160;---------------------------------------------------------------------------------------</fo:block>
							</fo:table-cell>
						</fo:table-row>
					 </fo:table-body> 
			       </fo:table>
			       </fo:block>		        
                  <fo:block font-family="Courier,monospace"  font-size="10pt">
				      <fo:table>
					      <fo:table-column column-width="80pt"/>
				          <fo:table-column column-width="120pt"/>
				          <fo:table-column column-width="150pt"/>
				          <fo:table-column column-width="80pt"/>
				          <fo:table-column column-width="60pt"/>
				          <fo:table-column column-width="120pt"/>		
					         <fo:table-body>
					            <#assign sno=1>
				               <fo:table-row >
				                   <fo:table-cell>
								       <fo:block text-align="center" keep-together="always" >${sno?if_exists}</fo:block>
							       </fo:table-cell>
							       <fo:table-cell>
								      <fo:block text-align="left" keep-together="always" >${produtMap.get("productId")?if_exists}</fo:block>
							       </fo:table-cell>
							       <fo:table-cell>
								       <fo:block text-align="left" white-space-collapse="false" >${produtMap.get("description")?if_exists}</fo:block>
							       </fo:table-cell>
							       <fo:table-cell>
								       <fo:block text-align="center" keep-together="always" >${produtMap.get("quantityUomId")?if_exists}</fo:block>
							       </fo:table-cell>
							       <fo:table-cell>
								       <fo:block text-align="center" keep-together="always" >${produtMap.get("quantity")?if_exists}</fo:block>
							       </fo:table-cell>
							       <fo:table-cell>
								       <fo:block text-align="center" keep-together="always" >${produtMap.get("unitPrice")?if_exists}</fo:block>
							       </fo:table-cell>
						      </fo:table-row > 
						<#assign sno=sno+1>
					 </fo:table-body> 
				 </fo:table>
				 <fo:block text-align="left" keep-together="always">&#160;--------------------------------------------------------------------------------------------------------</fo:block>
			</fo:block>			 	  	   
	       <fo:block  keep-together="always" text-align="left" font-family="Courier,monospace" white-space-collapse="false" font-size="12pt" >PO VALUE                : ${allDetailsMap.get("totVal")?if_exists?string("##0.00")} </fo:block>
	       <fo:block linefeed-treatment="preserve">&#xA;</fo:block>				   	       
	       <fo:block  keep-together="always" text-align="left" font-family="Courier,monospace" white-space-collapse="false" font-size="12pt" >DISCOUNT                : ${allDetailsMap.get("discount")?if_exists} </fo:block>
	       <fo:block linefeed-treatment="preserve">&#xA;</fo:block>				   	       
	       <fo:block  keep-together="always" text-align="left" font-family="Courier,monospace" white-space-collapse="false" font-size="12pt" >P AND F CHARGES         : ${allDetailsMap.get("pckAndFwdAmt")?if_exists} </fo:block>
	       <fo:block linefeed-treatment="preserve">&#xA;</fo:block>				   	      
	       <fo:block  keep-together="always" text-align="left" font-family="Courier,monospace" white-space-collapse="false" font-size="12pt" >EXCISE DUTY             : ${allDetailsMap.get("exciseAmt")?if_exists} </fo:block>
	       <fo:block linefeed-treatment="preserve">&#xA;</fo:block>
	       <fo:block  keep-together="always" text-align="left" font-family="Courier,monospace" white-space-collapse="false" font-size="12pt" >TAX                     : ${allDetailsMap.get("tax")?if_exists} </fo:block>
	       <fo:block linefeed-treatment="preserve">&#xA;</fo:block>				   
	       <fo:block  keep-together="always" text-align="left" font-family="Courier,monospace" white-space-collapse="false" font-size="12pt" >DELIVERY                : ${allDetailsMap.get("delivery")?if_exists}</fo:block>	       
	       <fo:block linefeed-treatment="preserve">&#xA;</fo:block>				   
	       <fo:block  keep-together="always" text-align="left" font-family="Courier,monospace" white-space-collapse="false" font-size="12pt" >MODE OF DISPATCH        : ${allDetailsMap.get("placeOfDispatch")?if_exists}</fo:block>
	       <fo:block linefeed-treatment="preserve">&#xA;</fo:block>	
	       <fo:block  keep-together="always" text-align="left" font-family="Courier,monospace" white-space-collapse="false" font-size="12pt" >WARANTY/GUARANTY        : </fo:block>
	       <fo:block linefeed-treatment="preserve">&#xA;</fo:block>		       
	       <fo:block  keep-together="always" text-align="left" font-family="Courier,monospace" white-space-collapse="false" font-size="12pt" >PAYMENT                 : ${allDetailsMap.get("payment")?if_exists}</fo:block>	
	       <fo:block linefeed-treatment="preserve">&#xA;</fo:block>		              			   	       			   	       
	       <fo:block  font-size="12pt" keep-together="always"  white-space-collapse="false">Please send back the duplicate copy of the P.O.duly signed and sealed as token of acceptance.</fo:block>     
	       <fo:block  font-size="12pt" keep-together="always"  white-space-collapse="false">Your requested to submit the bills inequaduplicate towards the supply of said materials.Also</fo:block>     
	       <fo:block  font-size="12pt" keep-together="always"  white-space-collapse="false">please Quote the Purchase Order No.and Date in all your Letters,Delivery notes,Invoices,etc.</fo:block>
	       <fo:block linefeed-treatment="preserve">&#xA;</fo:block>	            
	       <fo:block  font-size="12pt" keep-together="always"  white-space-collapse="false"  text-align="left">COPY TO                                                                 Yours faithfully</fo:block>
	       <fo:block  font-size="12pt" keep-together="always"  white-space-collapse="false"  text-align="left">&#160;                                                                       for MOTHER DAIRY</fo:block>
	       <fo:block linefeed-treatment="preserve">&#xA;</fo:block>
	       <fo:block linefeed-treatment="preserve">&#xA;</fo:block>
	       <fo:block  font-size="12pt" keep-together="always"  white-space-collapse="false"  text-align="left">&#160;&#160;                                                                      MANAGER(PURCHASE)</fo:block>	             
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
	            
	            
	            
	            
	            
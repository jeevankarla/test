
<#escape x as x?xml>
<fo:root xmlns:fo="http://www.w3.org/1999/XSL/Format">

<#-- do not display columns associated with values specified in the request, ie constraint values -->
<fo:layout-master-set>
	<fo:simple-page-master master-name="main" page-height="12in" page-width="10in"
            margin-top="0in" margin-bottom=".7n" margin-left=".5in" margin-right=".5in">
        <fo:region-body margin-top=".1in"/>
        <fo:region-before extent="1.0in"/>
        <fo:region-after extent="1.5in"/>        
    </fo:simple-page-master>   
</fo:layout-master-set>
${setRequestAttribute("OUTPUT_FILENAME", "LoanAvailedReport.pdf")}
 <#if orderDetailsList?has_content> 

<fo:page-sequence master-reference="main" force-page-count="no-force" font-family="Courier,monospace">					
		<fo:static-content flow-name="xsl-region-before">
			              		<fo:block  keep-together="always" text-align="right" font-family="Courier,monospace" white-space-collapse="false"> &#160;Page - <fo:page-number/></fo:block>
	  
            </fo:static-content>		
           <fo:flow flow-name="xsl-region-body"   font-family="Courier,monospace">	
			        	<#--   <fo:block white-space-collapse="false" font-size="10pt"  font-family="Helvetica" keep-together="always" >&#160;  STORE CODE:${parameters.stockId}&#160;    &#160;     &#160;  DESCRIPTION:${stockDetails.get("description")?if_exists}</fo:block> -->
			    <#assign reportHeader = delegator.findOne("TenantConfiguration", {"propertyTypeEnumId" : "COMPANY_HEADER","propertyName" : "reportHeaderLable"}, true)>
 			    <#assign reportSubHeader = delegator.findOne("TenantConfiguration", {"propertyTypeEnumId" : "COMPANY_HEADER","propertyName" : "reportSubHeaderLable"}, true)>
 			    <fo:block  keep-together="always" text-align="center" font-family="Courier,monospace" white-space-collapse="false" font-size="12pt" font-weight="bold" >${reportHeader.description?if_exists}</fo:block>
 			    <fo:block  keep-together="always" text-align="center" font-family="Courier,monospace" white-space-collapse="false" font-size="12pt" font-weight="bold" >${reportSubHeader.description?if_exists}</fo:block>
                		
                <fo:block  keep-together="always" text-align="center" font-family="Courier,monospace" white-space-collapse="false" font-size="12pt" >--------------------------------------------------------------------------------------------------- </fo:block>
	            <fo:block  keep-together="always" text-align="center" font-family="Courier,monospace" white-space-collapse="false" font-size="12pt" font-weight="bold">INDENT FORM FOR PURCHASE OF YARN</fo:block>
	                          	
	                          		<fo:block linefeed-treatment="preserve">&#xA;</fo:block>
	                      		<fo:block linefeed-treatment="preserve">&#xA;</fo:block>
	            
	                          	
	                             <fo:block >
			        			 <fo:table width="100%" align="right" table-layout="fixed"  font-size="12pt">
					               <fo:table-column column-width="250pt"/>               
					                <fo:table-column column-width="160pt"/>               
						           <fo:table-column column-width="50pt"/>               
					                <fo:table-column column-width="100pt"/>               

						           	<fo:table-body>
				                     <fo:table-row>
				                     <fo:table-cell  ><fo:block text-align="left" font-size="12pt"  >Ref NO   &#160;:</fo:block></fo:table-cell>       			
				                     <fo:table-cell  ><fo:block text-align="left"  font-size="12pt"  >&#160;</fo:block></fo:table-cell>       		
				                     <fo:table-cell  ><fo:block text-align="right"  font-size="12pt"  >&#160;&#160;&#160;&#160;DATE</fo:block></fo:table-cell>       		
				                     <fo:table-cell  ><fo:block text-align="right"  font-size="12pt"  > :${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(allDetailsMap.get("orderDate")?if_exists, "dd-MMM-yy")}</fo:block></fo:table-cell>       		
                                  </fo:table-row>
                                  	</fo:table-body>
			                		</fo:table>
			        	  </fo:block>	
	                      	
              	
              	<fo:block font-weight="bold" keep-together="always" text-align="left" font-family="Courier,monospace" white-space-collapse="false" font-size="12pt" >ADDRESS:${partyId},      </fo:block>
              	<fo:block font-weight="bold" keep-together="always" text-align="left" font-family="Courier,monospace" white-space-collapse="false" font-size="12pt" >&#160;&#160;&#160;     ${partyName?if_exists} </fo:block>
              	<fo:block font-weight="bold" keep-together="always" text-align="left" font-family="Courier,monospace" white-space-collapse="false" font-size="12pt" ><#if partyAddressMap.get("address1")?has_content>&#160;&#160;&#160;     ${partyAddressMap.get("address1")}   <#else> </#if>     </fo:block>
                <fo:block font-weight="bold" keep-together="always" text-align="left" font-family="Courier,monospace" white-space-collapse="false" font-size="12pt" ><#if partyAddressMap.get("address2")?has_content>&#160;&#160;&#160;     ${partyAddressMap.get("address2")?if_exists} <#else> </#if>     </fo:block>
                <fo:block font-weight="bold" keep-together="always" text-align="left" font-family="Courier,monospace" white-space-collapse="false" font-size="12pt" ><#if partyAddressMap.get("city")?has_content>&#160;&#160;&#160;     ${partyAddressMap.get("city")?if_exists}-${allDetailsMap.get("postalCode")?if_exists}. <#else> </#if>                          </fo:block>
                
              	 <fo:block font-weight="bold" keep-together="always" text-align="left" font-family="Courier,monospace" white-space-collapse="false" font-size="12pt" ><#if allDetailsMap.get("phoneNumber")?has_content>PHONE NO:${allDetailsMap.get("phoneNumber")?if_exists}</#if>         </fo:block>
              	 <fo:block font-weight="bold" keep-together="always" text-align="left" font-family="Courier,monospace" white-space-collapse="false" font-size="12pt" ><#if fromPartyTinNo?has_content>TIN NO  :${fromPartyTinNo?if_exists}</#if>         </fo:block>
				<fo:block  keep-together="always" text-align="left" font-family="Courier,monospace" white-space-collapse="false" font-size="12pt" ><#if allDetailsMap.get("refNo")?has_content> REFERENCE NO :${allDetailsMap.get("refNo")?if_exists}</#if></fo:block>
               <fo:block  keep-together="always" text-align="center" font-family="Courier,monospace" white-space-collapse="false" font-size="12pt" >&#160;&#160; </fo:block>
             	<fo:block  keep-together="always" text-align="left" font-family="Courier,monospace" white-space-collapse="false" font-size="12pt" ><#if allDetailsMap.get("enquiryId")?has_content> DAIRY ENQUIRY NO :${allDetailsMap.get("enquiryId")?if_exists}</#if>                                             <#if allDetailsMap.get("enquiryDate")?has_content>DATE:${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(allDetailsMap.get("enquiryDate")?if_exists, "dd-MMM-yy")}  </#if>  </fo:block>
              	<fo:block  keep-together="always" text-align="left" font-family="Courier,monospace" white-space-collapse="false" font-size="12pt" ><#if allDetailsMap.get("quoteId")?has_content>QUOTATION NO     :${allDetailsMap.get("quoteId")?if_exists}</#if>                                            <#if allDetailsMap.get("qutationDate")?has_content> DATE:${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(allDetailsMap.get("qutationDate")?if_exists, "dd-MMM-yy")} </#if>   <#if allDetailsMap.get("qutationDateAttr")?has_content> DATE:${allDetailsMap.get("qutationDateAttr")?if_exists}</#if> </fo:block>
              	              	<fo:block  keep-together="always" text-align="left" font-family="Courier,monospace" white-space-collapse="false" font-size="12pt" ><#if allDetailsMap.get("quoteRef")?has_content>QUOTE REF NO     :${allDetailsMap.get("quoteRef")?if_exists}</#if>       </fo:block>
              	
                <fo:block  keep-together="always" text-align="center" font-family="Courier,monospace" white-space-collapse="false" font-size="12pt" >&#160;&#160;-------------------------------------------------------------------------------------------</fo:block>
              	<fo:block  keep-together="always" text-align="left" font-family="Courier,monospace" white-space-collapse="false" font-size="12pt" font-weight="bold">&#160;&#160;SNO           ITEM                     QUANTITY       EX-MILL RATE RATE      SUPPLIER MILL</fo:block>
              																																																                                          PER BUNDLE
              	<fo:block  keep-together="always" text-align="center" font-family="Courier,monospace" white-space-collapse="false" font-size="12pt" >&#160;&#160;-------------------------------------------------------------------------------------------</fo:block>
            	<fo:block>
                 <fo:table text-align="center" >
                    <fo:table-column column-width="50pt"/>
                    <fo:table-column column-width="90pt"/>
                    <fo:table-column column-width="140pt"/>  
               	    <fo:table-column column-width="90pt"/>
               	    <fo:table-column column-width="90pt"/>
               	    <fo:table-column column-width="80pt"/>
               	    <fo:table-column column-width="80pt"/>
               	    
            		
                    <fo:table-body text-align="center">
                     <#assign sNo=1>
	                    
	                    <#list orderDetailsList as orderListItem>
	                    
	                  
					<#assign productId= orderListItem("productId")?if_exists >
		          <#assign productNameDetails = delegator.findOne("Product", {"productId" : productId}, true)>
		           <#if productNameDetails?has_content> 
		  <#--          		          <#assign UomIdDetails = delegator.findOne("Uom", {"uomId" : ${productNameDetails.get("quantityUomId")?if_exists} }, true)> -->
		           
                  	 <fo:table-row >
                	   <fo:table-cell ><fo:block text-align="center"  font-size="12pt" >${sNo} </fo:block></fo:table-cell>     
  				  	   <fo:table-cell ><fo:block text-align="left" font-size="12pt">${productNameDetails.get("internalName")?if_exists}</fo:block></fo:table-cell>     
  				       <fo:table-cell  ><fo:block text-align="left"    font-size="12pt" >${productNameDetails.get("productName")?if_exists} </fo:block></fo:table-cell>     
  				       <fo:table-cell  ><fo:block text-align="center"  font-size="12pt">${orderListItem.get("quantity")?if_exists}</fo:block></fo:table-cell>
  				       <fo:table-cell  ><fo:block text-align="center"  font-size="12pt">&#160;</fo:block></fo:table-cell>
  				       <fo:table-cell  ><fo:block text-align="right"  font-size="12pt">&#160;</fo:block></fo:table-cell>
  				       <fo:table-cell  ><fo:block text-align="right"  font-size="12pt">${allDetailsMap.get("supplierName")?if_exists}</fo:block></fo:table-cell>     
  				         
  				     </fo:table-row>
  				    	<#assign sNo=sNo+1>
  				    	</#if>
  				     </#list>
                    </fo:table-body>
                </fo:table>
               </fo:block>
	<fo:block linefeed-treatment="preserve">&#xA;</fo:block>
    <fo:block  keep-together="always" text-align="center" font-family="Courier,monospace" white-space-collapse="false" font-size="12pt" >&#160;&#160;-------------------------------------------------------------------------------------------</fo:block>
    <fo:block  keep-together="always" text-align="left"  white-space-collapse="false" font-size="12pt" font-weight="bold"> <fo:inline font-weight="bold">DELIVERY SCHEDULE / DESTINATION</fo:inline>                          </fo:block>
	<fo:block  keep-together="always" text-align="left" font-family="Courier,monospace" white-space-collapse="false" font-size="12pt" >-------------------------------</fo:block> 
    <fo:block linefeed-treatment="preserve">&#xA;</fo:block>
	 <fo:block  keep-together="always" text-align="left"  white-space-collapse="false" font-size="10pt" font-weight="bold"> <fo:inline font-weight="bold">PAYMENT:${payment}               RTGS/CTS/CH N0/ DD No:</fo:inline>                          </fo:block>
	 	<fo:block linefeed-treatment="preserve">&#xA;</fo:block>
		<fo:block linefeed-treatment="preserve">&#xA;</fo:block>
		<fo:block linefeed-treatment="preserve">&#xA;</fo:block>
	 <fo:block  keep-together="always" text-align="right"  white-space-collapse="false" font-size="11pt" font-weight="bold"> <fo:inline font-weight="bold">Signature of the Authorised Person With Seal</fo:inline>                          </fo:block>                 
	 <fo:block linefeed-treatment="preserve">&#xA;</fo:block>
     <fo:block linefeed-treatment="preserve">&#xA;</fo:block>
     <fo:block linefeed-treatment="preserve">&#xA;</fo:block>
	 
	 <fo:block  keep-together="always" text-align="center"  white-space-collapse="false" font-size="12pt" font-weight="bold"> <fo:inline font-weight="bold">TERMS &amp; CONDITIONS</fo:inline>                          </fo:block>		
	  
	 <fo:block  keep-together="always" text-align="center" font-family="Courier,monospace" white-space-collapse="false" font-size="12pt" >-------------------</fo:block>		 
          <fo:block linefeed-treatment="preserve">&#xA;</fo:block>
          <fo:block linefeed-treatment="preserve">&#xA;</fo:block>
     
     
     
     
     <fo:block   text-align="left"  white-space-collapse="false" font-size="10pt" > 1. SELLING PRICE                 :NHDC Selling Price will be informed at the time of confirmation of              </fo:block>
      <fo:block   text-align="left"  white-space-collapse="false" font-size="10pt" >&#160;	                                supplies through letter/faz and invoice will be raised immediately on</fo:block>
      <fo:block   text-align="left"  white-space-collapse="false" font-size="10pt" >&#160;	                                dispatch of material.</fo:block>
     <fo:block   text-align="left"  white-space-collapse="false" font-size="10pt" > 2. PAYMENT                       :100% of the estimated amount ofor yarn may be paid in advance through </fo:block>
     <fo:block   text-align="left"  white-space-collapse="false" font-size="10pt" >&#160;	                                Demand Draft in favour of National Handloom Development Corporation </fo:block>
     <fo:block   text-align="left"  white-space-collapse="false" font-size="10pt" >&#160;	                                Limited.</fo:block>
     <fo:block   text-align="left"  white-space-collapse="false" font-size="10pt" > 3. DEMURRAGES                    :Demurrages if any will be borned by the buyer.</fo:block>
     <fo:block   text-align="left"  white-space-collapse="false" font-size="10pt" > 4. CANCELLATION                  :This will be entertained only if received 15days prior to confirmation of </fo:block>
     <fo:block   text-align="left"  white-space-collapse="false" font-size="10pt" >&#160;	                                dispatch of contract order.</fo:block>
     <fo:block   text-align="left"  white-space-collapse="false" font-size="10pt" > Any loss/damages to consignment or part thereof may be covered by transit insurance by the manufacturer/buyer.</fo:block>
     	           <fo:block linefeed-treatment="preserve">&#xA;</fo:block>
     
     <fo:block   text-align="left"  white-space-collapse="false" font-size="10pt" > NOTE: For undertaking to be signed by indenting agency.</fo:block>			 
	 
	 <fo:block  keep-together="always" text-align="center" font-family="Courier,monospace" white-space-collapse="false" font-size="12pt" >&#160;--------------------------------------------------------------------------------------------------- </fo:block>		 
	
	<fo:block   text-align="center"  white-space-collapse="false" font-size="10pt" font-weight="bold" > NOTE: FOR NHDC OFFICE USE ONLY</fo:block>		 
			
			          <fo:block linefeed-treatment="preserve">&#xA;</fo:block>
			          <fo:block linefeed-treatment="preserve">&#xA;</fo:block>
			 
			 
	<fo:block   text-align="left"  white-space-collapse="false" font-size="10pt" >PURCHASE ORDER NO./DATE :${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(orderDate?if_exists, "dd-MMM-yy")} </fo:block>
				          <fo:block linefeed-treatment="preserve">&#xA;</fo:block>
	<fo:block   text-align="left"  white-space-collapse="false" font-size="10pt" >Arrangment made for supply </fo:block>
		<fo:block linefeed-treatment="preserve">&#xA;</fo:block>
		<fo:block linefeed-treatment="preserve">&#xA;</fo:block>
	 <fo:block  keep-together="always" text-align="right"  white-space-collapse="false" font-size="11pt" font-weight="bold">        Authorized Signature &#160;&#160;&#160;</fo:block>
	 <fo:block  keep-together="always" text-align="right"  white-space-collapse="false" font-size="11pt" font-weight="bold">  (Designation with Office Seal)                          </fo:block>                 
	
			 
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
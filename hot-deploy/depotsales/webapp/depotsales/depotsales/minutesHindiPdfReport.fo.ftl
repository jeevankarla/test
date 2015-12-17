<#escape x as x?xml>
<fo:root xmlns:fo="http://www.w3.org/1999/XSL/Format">

<#-- do not display columns associated with values specified in the request, ie constraint values -->
<fo:layout-master-set>
	<fo:simple-page-master master-name="main" page-height="12in" page-width="10in"
            margin-top="0.1in" margin-bottom="1.5n" margin-left=".5in" margin-right=".5in">
        <fo:region-body margin-top=".1in"/>
        <fo:region-before extent="1.0in"/>
        <fo:region-after extent="1.5in"/>        
    </fo:simple-page-master>   
</fo:layout-master-set>
${setRequestAttribute("OUTPUT_FILENAME", "LoanAvailedReport.pdf")}
 <#if OrderItemList?has_content> 

<fo:page-sequence master-reference="main" force-page-count="no-force" font-family="Courier,Mangal">
<#assign locale= Static["org.ofbiz.base.util.UtilMisc"].parseLocale("hi_IN")>					
		<fo:static-content flow-name="xsl-region-before">
			              		<#--><fo:block  keep-together="always" text-align="right" font-family="Courier,Mangal" white-space-collapse="false"> &#160;Page - <fo:page-number/></fo:block>-->
	  
            </fo:static-content>		
           <fo:flow flow-name="xsl-region-body"   font-family="Courier,Mangal">	
			        	<#assign partyName = dispatcher.runSync("convertToIndicScript", Static["org.ofbiz.base.util.UtilMisc"].toMap("messageStr", partyName, "toScript", "devanagari")).get("result")/>
			        	
			        	<#assign fromDate = dispatcher.runSync("convertToIndicScript", Static["org.ofbiz.base.util.UtilMisc"].toMap("messageStr", Static["org.ofbiz.base.util.UtilDateTime"].toDateString(nowTimestamp, "dd/MM/yyyy"), "toScript", "devanagari")).get("result")/>
			        	<#--   <fo:block white-space-collapse="false" font-size="10pt"  font-family="Helvetica" keep-together="always" >&#160;  STORE CODE:${parameters.stockId}&#160;    &#160;     &#160;  DESCRIPTION:${stockDetails.get("description")?if_exists}</fo:block> -->
			    <#assign reportHeader = delegator.findOne("TenantConfiguration", {"propertyTypeEnumId" : "COMPANY_HEADER","propertyName" : "reportHeaderLable"}, true)>
 			    <#assign reportSubHeader = delegator.findOne("TenantConfiguration", {"propertyTypeEnumId" : "COMPANY_HEADER","propertyName" : "reportSubHeaderLable"}, true)>
 			    <fo:block  keep-together="always" text-align="center" font-family="Courier,Mangal" white-space-collapse="false" font-size="14pt" font-weight="bold">${Static["org.ofbiz.base.util.UtilProperties"].getMessage("DepotSalesUiLabels", "HindiMinutes1", locale)}</fo:block>
 			    <fo:block  keep-together="always" text-align="center" font-family="Courier,Mangal" white-space-collapse="false" font-size="17pt" font-weight="bold">---------------------</fo:block>
 			    <fo:block  keep-together="always" text-align="center" font-family="Courier,Mangal" white-space-collapse="false" font-size="12pt" font-weight="bold">${Static["org.ofbiz.base.util.UtilProperties"].getMessage("DepotSalesUiLabels", "HindiMinutes2", locale)}   </fo:block>
                <fo:block linefeed-treatment="preserve">&#xA;</fo:block>		
                <#--><fo:block  keep-together="always" text-align="center" font-family="Courier,Mangal" white-space-collapse="false" font-size="12pt" >--------------------------------------------------------------------------------------------------- </fo:block>-->
	            <fo:block  keep-together="always" text-align="center" font-family="Courier,Mangal" white-space-collapse="false" font-size="11pt" >${Static["org.ofbiz.base.util.UtilProperties"].getMessage("DepotSalesUiLabels", "HindiMinutes3", locale)}</fo:block>
	                   <fo:block  keep-together="always" text-align="center" font-family="Courier,Mangal" white-space-collapse="false" font-size="11pt" font-weight="bold">  nhdcltdvns@yahoo.in</fo:block>
	                   <fo:block>&#160;</fo:block>
	                   <fo:block>
	                     <fo:table>
	                        <fo:table-column column-width="100"/>
	                        <fo:table-column column-width="800"/>
	                          <fo:table-body>
	                             <fo:table-row>
	                               <fo:table-cell>
	                                  <fo:block  keep-together="always" text-align="left" font-family="Courier,Mangal" white-space-collapse="false" font-size="11pt" >${Static["org.ofbiz.base.util.UtilProperties"].getMessage("DepotSalesUiLabels", "HindiMinutes5", locale)}</fo:block>              
	                               </fo:table-cell>
	                               <fo:table-cell>
	                                  <fo:block  keep-together="always" text-align="center" font-family="Courier,Mangal" white-space-collapse="false" font-size="11pt" >${Static["org.ofbiz.base.util.UtilProperties"].getMessage("DepotSalesUiLabels", "HindiMinutes6", locale)}  ${fromDate?if_exists}</fo:block>              
	                               </fo:table-cell>
	                             </fo:table-row>
	                          </fo:table-body>
	                     </fo:table>
	                   </fo:block>
	                   
	                   <fo:block linefeed-treatment="preserve">&#xA;</fo:block>
	                   <fo:block  keep-together="always" text-align="left" font-family="Courier,Mangal" white-space-collapse="false" font-size="11pt" >${Static["org.ofbiz.base.util.UtilProperties"].getMessage("DepotSalesUiLabels", "HindiMinutes7", locale)}</fo:block>
	                   <fo:block  keep-together="always" text-align="left" font-family="Courier,Mangal" white-space-collapse="false" font-size="8pt" font-weight="bold">-------------------------------------------------------------------------------------------</fo:block>
	                   <fo:block>&#160;</fo:block>

	                   <fo:block  keep-together="always" text-align="left" font-family="Courier,Mangal" white-space-collapse="false" font-size="11pt" >${Static["org.ofbiz.base.util.UtilProperties"].getMessage("DepotSalesUiLabels", "HindiMinutes8", locale)}</fo:block>
	                   <fo:block  keep-together="always" text-align="left" font-family="Courier,Mangal" white-space-collapse="false" font-size="11pt" >${Static["org.ofbiz.base.util.UtilProperties"].getMessage("DepotSalesUiLabels", "HindiMinutes9", locale)}</fo:block>
	                   <fo:block  keep-together="always" text-align="left" font-family="Courier,Mangal" white-space-collapse="false" font-size="11pt" >${Static["org.ofbiz.base.util.UtilProperties"].getMessage("DepotSalesUiLabels", "HindiMinutes10", locale)}</fo:block>
	                   <fo:block linefeed-treatment="preserve">&#xA;</fo:block>
	                   <fo:block>
	                     <fo:table>
	                        <fo:table-column column-width="100"/>
	                        <fo:table-column column-width="650"/>
	                          <fo:table-body>
	                             <fo:table-row>
	                               <fo:table-cell>
	                                  <fo:block  keep-together="always" text-align="left" font-family="Courier,Mangal" white-space-collapse="false" font-size="11pt" >${Static["org.ofbiz.base.util.UtilProperties"].getMessage("DepotSalesUiLabels", "HindiMinutes11", locale)}</fo:block>              
	                               </fo:table-cell>
	                               <fo:table-cell>
	                                  <fo:block  keep-together="always" text-align="center" font-family="Courier,Mangal" white-space-collapse="false" font-size="11pt" >${Static["org.ofbiz.base.util.UtilProperties"].getMessage("DepotSalesUiLabels", "HindiMinutes12", locale)}</fo:block>              
	                               </fo:table-cell>
	                             </fo:table-row>
	                          </fo:table-body>
	                     </fo:table>
	                   </fo:block>
	                   <fo:block linefeed-treatment="preserve">&#xA;</fo:block>
	           <fo:block>
            	<fo:table text-align="center" border-style="solid">
            	  <fo:table-column column-width="58pt"/>
            	  <fo:table-column column-width="160pt"/>
            	  <fo:table-column column-width="60pt"/>
            	  <fo:table-column column-width="70pt"/>
            	  <fo:table-column column-width="75pt"/>
            	  <fo:table-column column-width="85pt"/>
            	  <fo:table-column column-width="85pt"/>
            	  <fo:table-column column-width="65pt"/>
            	    <fo:table-body text-align="center">
            	        <fo:table-row margin-top="2in">
            	          <fo:table-cell border-right-style="solid" border-bottom-style="solid">
            	            <fo:block margin-top=".2in" font-size="10pt" font-weight="bold" keep-together="always"  text-align="center" white-space-collapse="false">${Static["org.ofbiz.base.util.UtilProperties"].getMessage("DepotSalesUiLabels", "HindiMinutes13", locale)}   </fo:block>
            	          </fo:table-cell >
            	          <fo:table-cell border-right-style="solid" border-bottom-style="solid">
            	            <fo:block margin-top=".2in" font-size="10pt" font-weight="bold" keep-together="always"  text-align="center" white-space-collapse="false">${Static["org.ofbiz.base.util.UtilProperties"].getMessage("DepotSalesUiLabels", "HindiMinutes14", locale)}   </fo:block>
            	          </fo:table-cell >
            	          <fo:table-cell border-right-style="solid" border-bottom-style="solid">
            	            <fo:block margin-top=".2in" font-size="10pt" font-weight="bold" keep-together="always"  text-align="center" white-space-collapse="false">${Static["org.ofbiz.base.util.UtilProperties"].getMessage("DepotSalesUiLabels", "HindiMinutes15", locale)}   </fo:block>
            	          </fo:table-cell >
            	          <fo:table-cell border-right-style="solid" border-bottom-style="solid">
            	            <fo:block margin-top=".2in" font-size="10pt" font-weight="bold" keep-together="always"  text-align="center" white-space-collapse="false">${Static["org.ofbiz.base.util.UtilProperties"].getMessage("DepotSalesUiLabels", "HindiMinutes16", locale)}   </fo:block>
            	            <fo:block margin-top=".2in" font-size="10pt" font-weight="bold" keep-together="always"  text-align="center" white-space-collapse="false">${Static["org.ofbiz.base.util.UtilProperties"].getMessage("DepotSalesUiLabels", "HindiMinutes17", locale)}   </fo:block>
            	          </fo:table-cell >
            	          <fo:table-cell border-right-style="solid" border-bottom-style="solid">
            	            <fo:block margin-top=".2in" font-size="10pt" font-weight="bold" keep-together="always"  text-align="center" white-space-collapse="false">${Static["org.ofbiz.base.util.UtilProperties"].getMessage("DepotSalesUiLabels", "HindiMinutes18", locale)} </fo:block>
            	            <fo:block margin-top=".2in" font-size="10pt" font-weight="bold" keep-together="always"  text-align="center" white-space-collapse="false">${Static["org.ofbiz.base.util.UtilProperties"].getMessage("DepotSalesUiLabels", "HindiMinutes19", locale)} </fo:block>
            	          </fo:table-cell >
            	          <fo:table-cell border-right-style="solid" border-bottom-style="solid">
            	            <fo:block margin-top=".2in" font-size="10pt" font-weight="bold" keep-together="always"  text-align="center" white-space-collapse="false">${Static["org.ofbiz.base.util.UtilProperties"].getMessage("DepotSalesUiLabels", "HindiMinutes20", locale)}    </fo:block>
            	            <fo:block margin-top=".2in" font-size="10pt" font-weight="bold" keep-together="always"  text-align="center" white-space-collapse="false">${Static["org.ofbiz.base.util.UtilProperties"].getMessage("DepotSalesUiLabels", "HindiMinutes21", locale)}    </fo:block>
            	          </fo:table-cell >
            	          <fo:table-cell border-right-style="solid" border-bottom-style="solid">
            	            <fo:block margin-top=".2in" font-size="10pt" font-weight="bold" keep-together="always"  text-align="center" white-space-collapse="false">${Static["org.ofbiz.base.util.UtilProperties"].getMessage("DepotSalesUiLabels", "HindiMinutes22", locale)}    </fo:block>
            	          </fo:table-cell >
            	          <fo:table-cell border-right-style="solid" border-bottom-style="solid">
            	            <fo:block margin-top=".2in" font-size="10pt" font-weight="bold" keep-together="always"  text-align="center" white-space-collapse="false">${Static["org.ofbiz.base.util.UtilProperties"].getMessage("DepotSalesUiLabels", "HindiMinutes23", locale)}    </fo:block>
            	            <fo:block margin-top=".2in" font-size="10pt" font-weight="bold" keep-together="always"  text-align="center" white-space-collapse="false">${Static["org.ofbiz.base.util.UtilProperties"].getMessage("DepotSalesUiLabels", "HindiMinutes24", locale)}    </fo:block>
            	          </fo:table-cell >
            	        </fo:table-row >
            	    </fo:table-body>
            	</fo:table>
                 <fo:table text-align="center" border-style="solid">
                    <fo:table-column column-width="58pt"/>
                    <fo:table-column column-width="160pt"/>
                    <fo:table-column column-width="60pt"/>  
               	    <fo:table-column column-width="70pt"/>
               	    <fo:table-column column-width="75pt"/>
               	    <fo:table-column column-width="85pt"/>
               	    <fo:table-column column-width="85pt"/>
               	    <fo:table-column column-width="65pt"/>
                    <fo:table-body text-align="center">
                     <#assign sNo=0>
	                    
	                    <#list OrderItemList as eachItem>
	                  
					<#assign productId= eachItem.productId?if_exists >
		          <#assign productNameDetails = delegator.findOne("Product", {"productId" : productId}, true)>
		           <#if productNameDetails?has_content>
		           <#assign productName = productNameDetails.get("productName")>
		           <#assign productName = dispatcher.runSync("convertToIndicScript", Static["org.ofbiz.base.util.UtilMisc"].toMap("messageStr", productName, "toScript", "devanagari")).get("result")/> 
		       <#--<#assign UomIdDetails = delegator.findOne("Uom", {"uomId" : ${productNameDetails.get("quantityUomId")?if_exists} }, true)> -->
		           
                  	 <fo:table-row >
                	   <fo:table-cell border-right-style="solid" border-bottom-style="solid"><fo:block margin-top=".2in" text-align="center"  font-size="12pt" >${eachItem.get("SrNo")?if_exists} </fo:block></fo:table-cell>     
  				       <fo:table-cell  border-right-style="solid" border-bottom-style="solid"><fo:block margin-top=".2in" text-align="left"    font-size="12pt" >${productName?if_exists} </fo:block></fo:table-cell>     
  				       <fo:table-cell border-right-style="solid" border-bottom-style="solid"><fo:block margin-top=".2in" text-align="left" font-size="12pt">&#160;</fo:block></fo:table-cell>
  				       <fo:table-cell  border-right-style="solid" border-bottom-style="solid"><fo:block margin-top=".2in" text-align="right"  font-size="12pt">${eachItem.get("quantity")?if_exists}</fo:block></fo:table-cell>
  				       <fo:table-cell  border-right-style="solid" border-bottom-style="solid"><fo:block margin-top=".2in" text-align="right"  font-size="12pt">${eachItem.get("unitPrice")?if_exists}</fo:block></fo:table-cell>
  				       <fo:table-cell  border-right-style="solid" border-bottom-style="solid"><fo:block margin-top=".2in" text-align="right"  font-size="12pt"></fo:block></fo:table-cell>
  				       <fo:table-cell  border-right-style="solid" border-bottom-style="solid"><fo:block margin-top=".2in" text-align="right"  font-size="12pt"></fo:block></fo:table-cell>
  				       <fo:table-cell  border-right-style="solid" border-bottom-style="solid"><fo:block margin-top=".2in" text-align="right"  font-size="12pt">${eachItem.get("unitPrice")?if_exists}</fo:block></fo:table-cell>     
  				     </fo:table-row>
  				    	</#if>
  				    	<#assign sNo=sNo+1>
  				     </#list>
  				     <fo:table-row>
  				        <fo:table-cell border-right-style="solid" border-bottom-style="solid"><fo:block margin-top=".2in" text-align="center"  font-size="12pt" > </fo:block></fo:table-cell>
  				        <fo:table-cell border-right-style="solid" border-bottom-style="solid"><fo:block margin-top=".2in" text-align="right"  font-size="12pt" > </fo:block></fo:table-cell>
  				        <fo:table-cell border-right-style="solid" border-bottom-style="solid"><fo:block margin-top=".2in" text-align="center"  font-size="12pt" > </fo:block></fo:table-cell>
  				        <fo:table-cell border-right-style="solid" border-bottom-style="solid"><fo:block margin-top=".2in" text-align="center"  font-size="12pt" > </fo:block></fo:table-cell>
  				        <fo:table-cell border-right-style="solid" border-bottom-style="solid"><fo:block margin-top=".2in" text-align="center"  font-size="12pt" > </fo:block></fo:table-cell>
  				        <fo:table-cell border-right-style="solid" border-bottom-style="solid"><fo:block margin-top=".2in" text-align="center"  font-size="12pt" > </fo:block></fo:table-cell>
  				        <fo:table-cell border-right-style="solid" border-bottom-style="solid"><fo:block margin-top=".2in" text-align="center"  font-size="12pt" > </fo:block></fo:table-cell>
  				        <fo:table-cell border-right-style="solid" border-bottom-style="solid"><fo:block margin-top=".2in" text-align="center"  font-size="12pt" > </fo:block></fo:table-cell>
  				     </fo:table-row>
                    </fo:table-body>
                </fo:table>
               </fo:block>
               //
               <fo:block linefeed-treatment="preserve">&#xA;</fo:block>       
               <fo:block  keep-together="always" text-align="left" font-family="Courier,Mangal" white-space-collapse="false" font-size="11pt" font-weight="bold">${Static["org.ofbiz.base.util.UtilProperties"].getMessage("DepotSalesUiLabels", "HindiMinutes25", locale)}</fo:block>
               <fo:block linefeed-treatment="preserve">&#xA;</fo:block>
                <fo:block>
            	<fo:table text-align="center" border-style="solid">
            	  <fo:table-column column-width="58pt"/>
            	  <fo:table-column column-width="160pt"/>
            	  <fo:table-column column-width="60pt"/>
            	  <fo:table-column column-width="70pt"/>
            	  <fo:table-column column-width="75pt"/>
            	  <fo:table-column column-width="85pt"/>
            	  <fo:table-column column-width="85pt"/>
            	  <fo:table-column column-width="65pt"/>
            	    <fo:table-body text-align="center">
            	        <fo:table-row >
            	          <fo:table-cell border-right-style="solid" border-bottom-style="solid">
            	            <fo:block margin-top=".2in" font-size="10pt" font-weight="bold" keep-together="always"  text-align="center" white-space-collapse="false">${Static["org.ofbiz.base.util.UtilProperties"].getMessage("DepotSalesUiLabels", "HindiMinutes26", locale)}   </fo:block>
            	          </fo:table-cell >
            	          <fo:table-cell border-right-style="solid" border-bottom-style="solid">
            	            <fo:block margin-top=".2in" font-size="10pt" font-weight="bold" keep-together="always"  text-align="center" white-space-collapse="false">${Static["org.ofbiz.base.util.UtilProperties"].getMessage("DepotSalesUiLabels", "HindiMinutes27", locale)}   </fo:block>
            	          </fo:table-cell >
            	          <fo:table-cell border-right-style="solid" border-bottom-style="solid">
            	            <fo:block margin-top=".2in" font-size="10pt" font-weight="bold" keep-together="always"  text-align="center" white-space-collapse="false">${Static["org.ofbiz.base.util.UtilProperties"].getMessage("DepotSalesUiLabels", "HindiMinutes28", locale)}   </fo:block>
            	          </fo:table-cell >
            	          <fo:table-cell border-right-style="solid" border-bottom-style="solid">
            	            <fo:block margin-top=".2in" font-size="10pt" font-weight="bold" keep-together="always"  text-align="center" white-space-collapse="false">${Static["org.ofbiz.base.util.UtilProperties"].getMessage("DepotSalesUiLabels", "HindiMinutes29", locale)}   </fo:block>
            	            <fo:block margin-top=".2in" font-size="10pt" font-weight="bold" keep-together="always"  text-align="center" white-space-collapse="false">${Static["org.ofbiz.base.util.UtilProperties"].getMessage("DepotSalesUiLabels", "HindiMinutes30", locale)}   </fo:block>
            	          </fo:table-cell >
            	          <fo:table-cell border-right-style="solid" border-bottom-style="solid">
            	            <fo:block margin-top=".2in" font-size="10pt" font-weight="bold" keep-together="always"  text-align="center" white-space-collapse="false">${Static["org.ofbiz.base.util.UtilProperties"].getMessage("DepotSalesUiLabels", "HindiMinutes31", locale)} </fo:block>
            	            <fo:block margin-top=".2in" font-size="10pt" font-weight="bold" keep-together="always"  text-align="center" white-space-collapse="false">${Static["org.ofbiz.base.util.UtilProperties"].getMessage("DepotSalesUiLabels", "HindiMinutes32", locale)} </fo:block>
            	          </fo:table-cell >
            	          <fo:table-cell border-right-style="solid" border-bottom-style="solid">
            	            <fo:block margin-top=".2in" font-size="10pt" font-weight="bold" keep-together="always"  text-align="center" white-space-collapse="false">${Static["org.ofbiz.base.util.UtilProperties"].getMessage("DepotSalesUiLabels", "HindiMinutes33", locale)}    </fo:block>
            	            <#--><fo:block font-size="10pt" font-weight="bold" keep-together="always"  text-align="center" white-space-collapse="false">${Static["org.ofbiz.base.util.UtilProperties"].getMessage("DepotSalesUiLabels", "HindiMinutes21", locale)}    </fo:block>-->
            	          </fo:table-cell >
            	          <fo:table-cell border-right-style="solid" border-bottom-style="solid">
            	            <fo:block margin-top=".2in" font-size="10pt" font-weight="bold" keep-together="always"  text-align="center" white-space-collapse="false">${Static["org.ofbiz.base.util.UtilProperties"].getMessage("DepotSalesUiLabels", "HindiMinutes34", locale)}    </fo:block>
            	            <fo:block margin-top=".2in" font-size="10pt" font-weight="bold" keep-together="always"  text-align="center" white-space-collapse="false">${Static["org.ofbiz.base.util.UtilProperties"].getMessage("DepotSalesUiLabels", "HindiMinutes35", locale)}    </fo:block>
            	          </fo:table-cell >
            	          <fo:table-cell border-right-style="solid" border-bottom-style="solid">
            	            <fo:block margin-top=".2in" font-size="10pt" font-weight="bold" keep-together="always"  text-align="center" white-space-collapse="false">${Static["org.ofbiz.base.util.UtilProperties"].getMessage("DepotSalesUiLabels", "HindiMinutes36", locale)}    </fo:block>
            	            <fo:block margin-top=".2in" font-size="10pt" font-weight="bold" keep-together="always"  text-align="center" white-space-collapse="false">${Static["org.ofbiz.base.util.UtilProperties"].getMessage("DepotSalesUiLabels", "HindiMinutes37", locale)}    </fo:block>
            	          </fo:table-cell >
            	        </fo:table-row >
            	    </fo:table-body>
            	</fo:table>
                 <fo:table text-align="center" border-style="solid">
                    <fo:table-column column-width="58pt"/>
                    <fo:table-column column-width="160pt"/>
                    <fo:table-column column-width="60pt"/>  
               	    <fo:table-column column-width="70pt"/>
               	    <fo:table-column column-width="75pt"/>
               	    <fo:table-column column-width="85pt"/>
               	    <fo:table-column column-width="85pt"/>
               	    <fo:table-column column-width="65pt"/>
                    <fo:table-body text-align="center">
                     <#assign sNo=1>
			                     <#assign totquantityKgs = 0>
			                      <#assign toTunitPrice = 0>
			                       <#assign totSalesValue = 0>
	                    <#list OrderItemList as eachItem>
	                  
					<#assign productId= orderListItem("productId")?if_exists >
		          <#assign productNameDetails = delegator.findOne("Product", {"productId" : productId}, true)>
		           <#if productNameDetails?has_content>
		           <#assign productName = productNameDetails.get("productName")>
		           <#assign productName = dispatcher.runSync("convertToIndicScript", Static["org.ofbiz.base.util.UtilMisc"].toMap("messageStr", productName, "toScript", "devanagari")).get("result")/> 
		  <#--          		          <#assign UomIdDetails = delegator.findOne("Uom", {"uomId" : ${productNameDetails.get("quantityUomId")?if_exists} }, true)> -->
		           
                  	 <fo:table-row >
                	   <fo:table-cell border-right-style="solid" border-bottom-style="solid"><fo:block margin-top=".2in" text-align="center"  font-size="12pt" >${eachItem.get("SrNo")?if_exists}</fo:block></fo:table-cell>     
  				       <fo:table-cell  border-right-style="solid" border-bottom-style="solid"><fo:block margin-top=".2in" text-align="left"    font-size="12pt" >${productName?if_exists} </fo:block></fo:table-cell>     
  				       <fo:table-cell border-right-style="solid" border-bottom-style="solid"><fo:block margin-top=".2in" text-align="left" font-size="12pt">&#160;</fo:block></fo:table-cell>
  				       <fo:table-cell  border-right-style="solid" border-bottom-style="solid"><fo:block margin-top=".2in" text-align="right"  font-size="12pt">${eachItem.get("quantity")?if_exists}</fo:block></fo:table-cell>
  				        <fo:table-cell  border-right-style="solid" border-bottom-style="solid">
  				          <#assign totquantityKgs = totquantityKgs+eachItem.get("quantity")>
  				          <fo:block margin-top=".2in" text-align="right"  font-size="12pt">${eachItem.get("unitPrice")?if_exists}</fo:block>
  				       </fo:table-cell>
  				       <fo:table-cell  border-right-style="solid" border-bottom-style="solid">
  				        <#assign purchValue = 0>
					                <#if  eachItem.get("quantity")?has_content>
					                      <#assign purchValue = eachItem.get("quantity")*eachItem.get("unitPrice")>
					                 </#if>
					                 
					                 <#assign toTunitPrice = toTunitPrice+purchValue>
  				          <fo:block text-align="right"  font-size="12pt"> <#if purchValue!=0>${purchValue?if_exists}<#else>&#160;</#if>  </fo:block>
  				       
  				       </fo:table-cell>
  				       <fo:table-cell  border-right-style="solid" border-bottom-style="solid"><fo:block margin-top=".2in" text-align="right"  font-size="12pt">${eachItem.get("unitPrice")?if_exists}</fo:block></fo:table-cell>
  				       <fo:table-cell  border-right-style="solid" border-bottom-style="solid"><fo:block margin-top=".2in" text-align="right"  font-size="12pt"><#if purchValue!=0>${purchValue?if_exists}<#else>&#160;</#if></fo:block></fo:table-cell>     
  				     </fo:table-row>
  				    	<#assign sNo=sNo+1>
  				    	</#if>
  				     </#list>
  				     <fo:table-row>
  				        <fo:table-cell border-right-style="solid" border-bottom-style="solid"><fo:block margin-top=".2in" text-align="center"  font-size="12pt" > </fo:block></fo:table-cell>
  				        <fo:table-cell border-right-style="solid" border-bottom-style="solid"><fo:block margin-top=".2in" text-align="right"  font-size="12pt" > ${Static["org.ofbiz.base.util.UtilProperties"].getMessage("DepotSalesUiLabels", "HindiMinutes38", locale)}</fo:block></fo:table-cell>
  				        <fo:table-cell border-right-style="solid" border-bottom-style="solid"><fo:block margin-top=".2in" text-align="center"  font-size="12pt" > </fo:block></fo:table-cell>
  				        <fo:table-cell border-right-style="solid" border-bottom-style="solid"><fo:block margin-top=".2in" text-align="right"  font-size="12pt" > ${totquantityKgs?if_exists} </fo:block></fo:table-cell>
  				        <fo:table-cell border-right-style="solid" border-bottom-style="solid"><fo:block margin-top=".2in" text-align="center"  font-size="12pt" > </fo:block></fo:table-cell>
  				        <fo:table-cell border-right-style="solid" border-bottom-style="solid"><fo:block margin-top=".2in" text-align="right"  font-size="12pt" > ${toTunitPrice?if_exists}</fo:block></fo:table-cell>
  				        <fo:table-cell border-right-style="solid" border-bottom-style="solid"><fo:block margin-top=".2in" text-align="center"  font-size="12pt" > </fo:block></fo:table-cell>
  				        <fo:table-cell border-right-style="solid" border-bottom-style="solid"><fo:block margin-top=".2in" text-align="right"  font-size="12pt" > ${toTunitPrice?if_exists}</fo:block></fo:table-cell>
  				     </fo:table-row>
                    </fo:table-body>
                </fo:table>
               </fo:block>
			                   <fo:block linefeed-treatment="preserve">&#xA;</fo:block>

	                   <fo:block linefeed-treatment="preserve">&#xA;</fo:block>

               <fo:block text-align="left"  font-size="11pt" > ${Static["org.ofbiz.base.util.UtilProperties"].getMessage("DepotSalesUiLabels", "HindiMinutes39", locale)}</fo:block>
               <fo:block linefeed-treatment="preserve">&#xA;</fo:block>    
               <fo:block text-align="left"  font-size="11pt" > ${Static["org.ofbiz.base.util.UtilProperties"].getMessage("DepotSalesUiLabels", "HindiMinutes40", locale)}</fo:block>
               <fo:block linefeed-treatment="preserve">&#xA;</fo:block>    
               <fo:block text-align="left"  font-size="11pt" > ${Static["org.ofbiz.base.util.UtilProperties"].getMessage("DepotSalesUiLabels", "HindiMinutes41", locale)}</fo:block>
               <fo:block text-align="left"  font-size="11pt" > ${Static["org.ofbiz.base.util.UtilProperties"].getMessage("DepotSalesUiLabels", "HindiMinutes42", locale)}</fo:block>
               <fo:block text-align="left"  font-size="11pt" > ${Static["org.ofbiz.base.util.UtilProperties"].getMessage("DepotSalesUiLabels", "HindiMinutes43", locale)}</fo:block>
               <fo:block text-align="left"  font-size="11pt" > ${Static["org.ofbiz.base.util.UtilProperties"].getMessage("DepotSalesUiLabels", "HindiMinutes44", locale)}</fo:block>
               <fo:block text-align="left"  font-size="11pt" > ${Static["org.ofbiz.base.util.UtilProperties"].getMessage("DepotSalesUiLabels", "HindiMinutes45", locale)}</fo:block>
               <fo:block linefeed-treatment="preserve">&#xA;</fo:block>
               <fo:block text-align="left"  font-size="11pt" > ${Static["org.ofbiz.base.util.UtilProperties"].getMessage("DepotSalesUiLabels", "HindiMinutes46", locale)} : ${partyName?if_exists}</fo:block>
               <fo:block linefeed-treatment="preserve">&#xA;</fo:block>
               <fo:block text-align="left"  font-size="11pt" > ${Static["org.ofbiz.base.util.UtilProperties"].getMessage("DepotSalesUiLabels", "HindiMinutes47", locale)}</fo:block>
               <fo:block text-align="left"  font-size="11pt" >&#160;&#160; ${Static["org.ofbiz.base.util.UtilProperties"].getMessage("DepotSalesUiLabels", "HindiMinutes48", locale)}</fo:block>
               <fo:block linefeed-treatment="preserve">&#xA;</fo:block>
               <fo:block text-align="left"  font-size="11pt" > ${Static["org.ofbiz.base.util.UtilProperties"].getMessage("DepotSalesUiLabels", "HindiMinutes60", locale)}</fo:block>
               <fo:block linefeed-treatment="preserve">&#xA;</fo:block>
               <fo:block text-align="left"  font-size="11pt" > ${Static["org.ofbiz.base.util.UtilProperties"].getMessage("DepotSalesUiLabels", "HindiMinutes50", locale)}</fo:block>
               <fo:block linefeed-treatment="preserve">&#xA;</fo:block>
               <fo:block text-align="left"  font-size="11pt" > ${Static["org.ofbiz.base.util.UtilProperties"].getMessage("DepotSalesUiLabels", "HindiMinutes51", locale)}</fo:block>
               <fo:block linefeed-treatment="preserve">&#xA;</fo:block>
               <fo:block text-align="left"  font-size="11pt" > ${Static["org.ofbiz.base.util.UtilProperties"].getMessage("DepotSalesUiLabels", "HindiMinutes52", locale)}</fo:block>
               <fo:block linefeed-treatment="preserve">&#xA;</fo:block>
               <fo:block text-align="left"  font-size="11pt" > ${Static["org.ofbiz.base.util.UtilProperties"].getMessage("DepotSalesUiLabels", "HindiMinutes53", locale)}</fo:block>
               <fo:block linefeed-treatment="preserve">&#xA;</fo:block>
               <fo:block text-align="left"  font-size="11pt" > ${Static["org.ofbiz.base.util.UtilProperties"].getMessage("DepotSalesUiLabels", "HindiMinutes54", locale)}</fo:block>
               <fo:block linefeed-treatment="preserve">&#xA;</fo:block>
               <fo:block text-align="left"  font-size="11pt" > ${Static["org.ofbiz.base.util.UtilProperties"].getMessage("DepotSalesUiLabels", "HindiMinutes55", locale)}</fo:block>
               <fo:block linefeed-treatment="preserve">&#xA;</fo:block>
               <fo:block text-align="left"  font-size="11pt" > ${Static["org.ofbiz.base.util.UtilProperties"].getMessage("DepotSalesUiLabels", "HindiMinutes56", locale)}</fo:block>
               <fo:block linefeed-treatment="preserve">&#xA;</fo:block>
               <fo:block text-align="left"  font-size="11pt" > ${Static["org.ofbiz.base.util.UtilProperties"].getMessage("DepotSalesUiLabels", "HindiMinutes57", locale)}</fo:block>
               <fo:block linefeed-treatment="preserve">&#xA;</fo:block>
               <fo:block text-align="center"  font-size="11pt" > ${Static["org.ofbiz.base.util.UtilProperties"].getMessage("DepotSalesUiLabels", "HindiMinutes58", locale)}</fo:block>
               <fo:block text-align="center"  font-size="11pt" > ${Static["org.ofbiz.base.util.UtilProperties"].getMessage("DepotSalesUiLabels", "HindiMinutes59", locale)}</fo:block>
               
			 </fo:flow>
			 </fo:page-sequence>
			 <#else>
				<fo:page-sequence master-reference="main">
    			<fo:flow flow-name="xsl-region-body" font-family="Courier,Mangal">
       		 		<fo:block font-size="14pt">
            			NO RECORDS FOUND
       		 		</fo:block>
    			</fo:flow>
			</fo:page-sequence>
			</#if>
</fo:root>
</#escape>
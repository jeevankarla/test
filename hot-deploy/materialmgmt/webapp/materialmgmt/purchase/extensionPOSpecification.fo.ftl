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
        <fo:region-body margin-top="1.1in"/>
        <fo:region-before extent="1.in"/>
        <fo:region-after extent="1.5in"/>        
    </fo:simple-page-master>   
</fo:layout-master-set>
${setRequestAttribute("OUTPUT_FILENAME", "LoanAvailedReport.pdf")}
 <#if orderDetailsList?has_content> 

<fo:page-sequence master-reference="main" force-page-count="no-force" font-family="Courier,monospace">					
		<fo:static-content flow-name="xsl-region-before">
			   <fo:block  keep-together="always" text-align="center" font-family="Courier,monospace" white-space-collapse="false">&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;UserLogin : <#if userLogin?exists>${userLogin.userLoginId?if_exists}</#if></fo:block>
				<fo:block  keep-together="always" text-align="center" font-family="Courier,monospace" white-space-collapse="false">&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;Date:${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(nowTimestamp, "dd/MM/yy HH:mm:ss")}</fo:block> 
		        <fo:block  keep-together="always" text-align="center" font-family="Courier,monospace" white-space-collapse="false" font-size="12pt" > &#160;&#160;  </fo:block>
				<fo:block  keep-together="always" text-align="center" font-family="Courier,monospace" white-space-collapse="false" font-size="12pt" font-weight="bold" >  KARNATAKA CO-OPERATIVE MILK PRODUCERS FEDERATION LTD. </fo:block>
				<fo:block  keep-together="always" text-align="center" font-family="Courier,monospace" white-space-collapse="false" font-size="12pt" font-weight="bold" >  UNIT : MOTHER DAIRY:G.K.V.K POST : YELAHANKA:BANGALORE : 560065  </fo:block>
			    <fo:block  keep-together="always" text-align="center" font-family="Courier,monospace" white-space-collapse="false" font-size="5pt" > ----------------------------------------------------------------------------------------------------------------------------------------------------------------------</fo:block>
			    <fo:block  keep-together="always" text-align="center" font-family="Courier,monospace" white-space-collapse="false" font-size="12pt" > &#160;&#160;  </fo:block>
			
		          
              	 
            </fo:static-content>		
           <fo:flow flow-name="xsl-region-body"   font-family="Courier,monospace">	
			        	<#--   <fo:block white-space-collapse="false" font-size="10pt"  font-family="Helvetica" keep-together="always" >&#160;  STORE CODE:${parameters.stockId}&#160;    &#160;     &#160;  DESCRIPTION:${stockDetails.get("description")?if_exists}</fo:block> -->
			              
              		  
                  <fo:block  keep-together="always" text-align="left" font-family="Courier,monospace" white-space-collapse="false" font-size="12pt" >phon no:22179004   FAX :     080-20462652                TIN:    ${allDetailsMap.get("tinNumber")?if_exists} </fo:block>
                 <fo:block  keep-together="always" text-align="left" font-family="Courier,monospace" white-space-collapse="false" font-size="12pt" >&#160;       22179074   Telegram: MOTHER-NANDINI              KST NO: ${allDetailsMap.get("kstNumber")?if_exists} </fo:block>
                 <fo:block  keep-together="always" text-align="left" font-family="Courier,monospace" white-space-collapse="false" font-size="12pt" >&#160;&#160;      28565277   Email:    purchase@motherdairykmf.in  CST NO: ${allDetailsMap.get("cstNumber")?if_exists} </fo:block>
                <fo:block  keep-together="always" text-align="center" font-family="Courier,monospace" white-space-collapse="false" font-size="12pt" >--------------------------------------------------------------------------------------------------- </fo:block>
	            <fo:block  keep-together="always" text-align="center" font-family="Courier,monospace" white-space-collapse="false" font-size="12pt" font-weight="bold">MATERIAL SPECIFICATION </fo:block>
	            <fo:block  keep-together="always" text-align="center" font-family="Courier,monospace" white-space-collapse="false" font-size="12pt" > &#160;&#160;</fo:block>
              	<fo:block  keep-together="always" text-align="left" font-family="Courier,monospace" white-space-collapse="false" font-size="12pt" > PO NO:   ${allDetailsMap.get("orderId")?if_exists}                                            PO DATED: ${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(allDetailsMap.get("orderDate")?if_exists, "dd-MMM-yy")}</fo:block>
                <fo:block  keep-together="always" text-align="center" font-family="Courier,monospace" white-space-collapse="false" font-size="12pt" > &#160;&#160; </fo:block>
               <fo:block   text-align="left" font-family="Courier,monospace" white-space-collapse="false" font-size="12pt" >  EARLIER CONFIRMATORY PO NO: ${allDetailsMap.get("refNo")?if_exists}                                     DTD: ${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(allDetailsMap.get("orderDate")?if_exists, "dd-MMM-yy")}</fo:block>
             	<fo:block   text-align="left" font-family="Courier,monospace" white-space-collapse="false" font-size="12pt" >  DAIRY ENQUIRY NO: ${allDetailsMap.get("custRequestId")?if_exists}                                             DATE:<#if allDetailsMap.get("custRequestDate")?has_content> ${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(allDetailsMap.get("custRequestDate")?if_exists, "dd-MMM-yy")} <#else> </#if>  </fo:block>
              	<fo:block  text-align="left" font-family="Courier,monospace" white-space-collapse="false" font-size="12pt" >QUOTATION NO:    ${allDetailsMap.get("quoteId")?if_exists}                                              DATE:<#if allDetailsMap.get("qutationDate")?has_content> ${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(allDetailsMap.get("qutationDate")?if_exists, "dd-MMM-yy")}         <#else> </#if> </fo:block>
               <fo:block  keep-together="always" text-align="center" font-family="Courier,monospace" white-space-collapse="false" font-size="12pt" > &#160;&#160; </fo:block>
              	<fo:block   text-align="center" font-family="Courier,monospace" white-space-collapse="false" font-size="12pt" > &#160;&#160; </fo:block>
              	<fo:block   text-align="center" font-family="Courier,monospace" white-space-collapse="false" font-size="12pt" > &#160;&#160; </fo:block>
              	
              	 <#assign sNo=1>
	                    
	                    <#list orderDetailsList as orderListItem>
	                    
	                  
					<#assign productId= orderListItem("productId")?if_exists >
		          <#assign productNameDetails = delegator.findOne("Product", {"productId" : productId}, true)>
		           <#if productNameDetails?has_content>
                
              <fo:block  text-align="left" font-family="Courier,monospace" white-space-collapse="false" font-size="12pt" ><fo:inline text-align="left" font-family="Courier,monospace"  font-size="12pt" font-weight="bold">PRODUCT NAME:</fo:inline><#if productNameDetails.get("productName")?has_content> ${productNameDetails.get("productName")?if_exists}, <#else> </#if>  </fo:block>
              	<fo:block text-align="left" font-family="Courier,monospace" white-space-collapse="false" font-size="12pt" > &#160;&#160;          <#if productNameDetails.get("productWeight")?has_content>  ${productNameDetails.get("productWeight")?if_exists}, <#else> </#if>  <#if productNameDetails.get("productHeight")?has_content> ${productNameDetails.get("productHeight")?if_exists},<#else> </#if>  <#if productNameDetails.get("productWidth")?has_content> ${productNameDetails.get("productWidth")?if_exists},  <#else> </#if>                     </fo:block>
                <fo:block   text-align="center" font-family="Courier,monospace" white-space-collapse="false" font-size="12pt" > &#160;&#160; </fo:block>
              
              <#assign sNo=sNo+1>
  				    	</#if>
  				     </#list>
                <fo:block   text-align="left" font-family="Courier,monospace" white-space-collapse="false" font-size="12pt" >NOTE: Certificate regarding using of Virgin and Food Grade material, Migration Test Certificate for the finished material and Batch No. shall be furnished compulsorily. </fo:block>
  				  <fo:block   text-align="center" font-family="Courier,monospace" white-space-collapse="false" font-size="12pt" > &#160;&#160; </fo:block>
  				  <fo:block   text-align="center" font-family="Courier,monospace" white-space-collapse="false" font-size="12pt" > &#160;&#160; </fo:block>
                  <fo:block   text-align="right" font-family="Courier,monospace" white-space-collapse="false" font-size="12pt" font-weight="bold">                                Manager(Purchase)        &#160;&#160; </fo:block>
   				  <fo:block   text-align="center" font-family="Courier,monospace" white-space-collapse="false" font-size="12pt" > &#160;&#160; </fo:block>
                  <fo:block   text-align="right" font-family="Courier,monospace" white-space-collapse="false" font-size="12pt" font-weight="bold">                                Mother Dairy             &#160;&#160; </fo:block>
  				     
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
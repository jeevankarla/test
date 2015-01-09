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
 <#if orderDetailsList?has_content> 

    <fo:page-sequence master-reference="main" force-page-count="no-force" font-family="Courier,monospace">					
		<fo:static-content flow-name="xsl-region-before">		   
       </fo:static-content>
       <fo:flow flow-name="xsl-region-body"   font-family="Courier,monospace">	
           <fo:block  keep-together="always" text-align="left" font-family="Courier,monospace" white-space-collapse="false" font-size="13pt" >&#160;&#160;PO NO:${allDetailsMap.get("orderId")?if_exists}                                                 PO DATED:${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(nowTimestamp, "dd-MMM-yy")}</fo:block>
           <fo:block  keep-together="always" text-align="left" font-family="Courier,monospace" white-space-collapse="false" font-size="13pt" >&#160;&#160;To                                                                 </fo:block>
		   <fo:block  keep-together="always" text-align="left" font-family="Courier,monospace" white-space-collapse="false" font-size="13pt" >&#160;&#160;${partyAddressMap.get("address1")?if_exists}</fo:block>
		   <fo:block  keep-together="always" text-align="left" font-family="Courier,monospace" white-space-collapse="false" font-size="13pt" >&#160;&#160;${partyAddressMap.get("address2")?if_exists}</fo:block>
		   <fo:block  keep-together="always" text-align="left" font-family="Courier,monospace" white-space-collapse="false" font-size="13pt" >&#160;&#160;${partyAddressMap.get("city")?if_exists}</fo:block>				 
		   <fo:block  keep-together="always" text-align="left" font-family="Courier,monospace" white-space-collapse="false" font-size="13pt" >&#160;&#160;${partyAddressMap.get("postalCode")?if_exists}</fo:block>
		   <fo:block  keep-together="always" text-align="left" font-family="Courier,monospace" white-space-collapse="false" font-size="13pt" >&#160;&#160;PHONE NO  : ${contactNumber?if_exists}</fo:block>	                
           <fo:block linefeed-treatment="preserve">&#xA;</fo:block>          
           <fo:block  keep-together="always" text-align="left" font-family="Courier,monospace" white-space-collapse="false" font-size="13pt" >&#160;&#160;Sir,                                                                 </fo:block>
           <fo:block  keep-together="always" text-align="center" font-family="Courier,monospace" white-space-collapse="false" font-size="16pt" font-weight="bold" >          <fo:inline font-weight="bold" text-decoration="underline">LETTER OF INDENT </fo:inline>         </fo:block>
           <fo:block linefeed-treatment="preserve">&#xA;</fo:block>                         
           <fo:block   text-align="left" font-family="Courier,monospace" white-space-collapse="false" font-size="13pt">&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;Sub: Supply of Milk and Dark Chocomass-reg.</fo:block>
           <fo:block   text-align="left" font-family="Courier,monospace" white-space-collapse="false" font-size="13pt">&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;Ref: LOA NO :${allDetailsMap.get("attrValue")?if_exists}  Date:${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(allDetailsMap.get("orderDate")?if_exists, "dd-MMM-yy")}  </fo:block>
           <fo:block  keep-together="always" text-align="left" font-family="Verdana" white-space-collapse="false" font-size="15pt" >&#160;&#160;&#160;&#160;   &#160;&#160;&#160;&#160; &#160;&#160;&#160;&#160; &#160;&#160;&#160;&#160;                                                            *****</fo:block>
           <fo:block linefeed-treatment="preserve">&#xA;</fo:block>                         
           <fo:block   text-align="left" font-family="Courier,monospace" white-space-collapse="false" font-size="13pt">&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;With reference to the above,we request you to supply the following item.                               </fo:block>
           <fo:block font-family="Courier,monospace" text-align="center">
	           <fo:table text-align="center"  table-layout="fixed">
	           	  <fo:table-column column-width="80pt" />
	           	  <fo:table-column column-width="40pt" />	           	  
				  <fo:table-column column-width="80pt"/>
				  <fo:table-column column-width="120pt"/>
				  <fo:table-column column-width="150pt"/>
					  <fo:table-body>					  
					      <fo:table-row >		      
					          <fo:table-cell>
					              <fo:block></fo:block>
					          </fo:table-cell>
					          <fo:table-cell>
					               <fo:block></fo:block>
					            </fo:table-cell>							  
					         <fo:table-cell border-style="solid" >
								  <fo:block text-align="center" >SL.No</fo:block>
							  </fo:table-cell>
							  <fo:table-cell border-style="solid">
								  <fo:block text-align="center"  >DESCRIPTION</fo:block>
							  </fo:table-cell>
							  <fo:table-cell border-style="solid">
								 <fo:block text-align="center" >REQRD QTY</fo:block>
							  </fo:table-cell>
					     </fo:table-row>
					 </fo:table-body> 
			   </fo:table>
		   </fo:block>
		   <fo:block font-family="Courier,monospace" text-align="center">
	           <fo:table  table-layout="fixed"  space-start="1in">
	              <fo:table-column column-width="80pt"/>	           
				  <fo:table-column column-width="40pt"/>
				  <fo:table-column column-width="80pt" />				  
				  <fo:table-column column-width="120pt"/>
				  <fo:table-column column-width="150pt"/>
					  <fo:table-body>
					   <#assign sNo=1>	                    
	                   <#list orderDetailsList as orderListItem>	                    	                  
					   <#assign productId= orderListItem("productId")?if_exists >
		               <#assign productNameDetails = delegator.findOne("Product", {"productId" : productId}, true)>
		               <#if productNameDetails?has_content> 
					       <fo:table-row >
					           <fo:table-cell>
					               <fo:block></fo:block>
					            </fo:table-cell>
					            <fo:table-cell>
					               <fo:block></fo:block>
					            </fo:table-cell>
					            <fo:table-cell border-style="solid">
								   <fo:block text-align="center" >${sNo} </fo:block>
							    </fo:table-cell> 
							    <fo:table-cell border-style="solid">
								   <fo:block text-align="left" >${productNameDetails.get("productName")?if_exists}</fo:block>
							   </fo:table-cell> 	
							   <fo:table-cell border-style="solid">
								  <fo:block text-align="center" >${orderListItem.get("quantity")?if_exists}</fo:block>
							  </fo:table-cell> 								  
				          </fo:table-row>
				         <#assign sNo=sNo+1>
  				    	</#if>
  				     </#list>
					 </fo:table-body> 
			   </fo:table>
		   </fo:block>			  
           <fo:block   text-align="left" font-family="Courier,monospace" font-size="13pt">&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;Please arrange to send the material immediately to ${companyAddressMap.get("address1")?if_exists}, ${companyAddressMap.get("address2")?if_exists}, ${companyAddressMap.get("city")?if_exists}, ${companyAddressMap.get("postalCode")?if_exists}. </fo:block>
           <fo:block   text-align="left" font-family="Courier,monospace" white-space-collapse="false" font-size="13pt">&#160; Thanking you,                               </fo:block>
           <fo:block  font-size="13pt" keep-together="always"  white-space-collapse="false"  text-align="left">&#160;                                                                Yours faithfully</fo:block>
	       <fo:block  font-size="13pt" keep-together="always"  white-space-collapse="false"  text-align="left">&#160;                                                                for MOTHER DAIRY</fo:block>
	       <fo:block linefeed-treatment="preserve">&#xA;</fo:block>
	       <fo:block linefeed-treatment="preserve">&#xA;</fo:block>
	       <fo:block  font-size="12pt" keep-together="always"  white-space-collapse="false"  text-align="left">&#160;&#160;                                                                     MANAGER(PURCHASE)</fo:block>	             
           <fo:block linefeed-treatment="preserve">&#xA;</fo:block>
           <fo:block linefeed-treatment="preserve">&#xA;</fo:block>	                       	            
	       <fo:block  font-size="13pt" keep-together="always"  white-space-collapse="false"  text-align="left" font-weight="bold">COPY TO                                                   </fo:block>
           <fo:block  font-size="13pt" keep-together="always"  white-space-collapse="false"  text-align="left">&#160;&#160;1.Inchange(ICP),Bellary for information.</fo:block>
           <fo:block  font-size="13pt" keep-together="always"  white-space-collapse="false"  text-align="left">&#160;&#160;2.Store Officer(ICP),Bellary for information.</fo:block>
           <fo:block  font-size="13pt" keep-together="always"  white-space-collapse="false"  text-align="left">&#160;&#160;3.office Copy.</fo:block>           
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
	          
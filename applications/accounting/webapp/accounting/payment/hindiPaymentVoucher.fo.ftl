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
            margin-top="0.1in" margin-bottom="1.5n" margin-left=".5in" margin-right=".5in">
        <fo:region-body margin-top=".1in"/>
        <fo:region-before extent="1.0in"/>
        <fo:region-after extent="1.5in"/>        
    </fo:simple-page-master>   
</fo:layout-master-set>
${setRequestAttribute("OUTPUT_FILENAME", "LoanAvailedReport.pdf")}

<fo:page-sequence master-reference="main" force-page-count="no-force" font-family="Courier,Mangal">
<#assign locale= Static["org.ofbiz.base.util.UtilMisc"].parseLocale("hi_IN")>					
		<fo:static-content flow-name="xsl-region-before">
			              		<#--><fo:block  keep-together="always" text-align="right" font-family="Courier,Mangal" white-space-collapse="false"> &#160;Page - <fo:page-number/></fo:block>-->
	  
            </fo:static-content>		
           <fo:flow flow-name="xsl-region-body"   font-family="Courier,Mangal">	
            <fo:block  keep-together="always" text-align="left" font-family="Courier,Mangal" white-space-collapse="false" font-size="11pt" font-weight="bold">${Static["org.ofbiz.base.util.UtilProperties"].getMessage("DepotSalesUiLabels", "Invoice1", locale)}</fo:block>
            <fo:block  keep-together="always" text-align="left" font-family="Courier,Mangal" white-space-collapse="false" font-size="11pt" font-weight="bold">${Static["org.ofbiz.base.util.UtilProperties"].getMessage("DepotSalesUiLabels", "Invoice2", locale)}</fo:block>
            <fo:block linefeed-treatment="preserve">&#xA;</fo:block>
            <fo:block  keep-together="always" text-align="center" font-family="Courier,Mangal" white-space-collapse="false" font-size="13pt" font-weight="bold">${Static["org.ofbiz.base.util.UtilProperties"].getMessage("DepotSalesUiLabels", "Invoice3", locale)}</fo:block>
            <fo:block  keep-together="always" text-align="center" font-family="Courier,Mangal" white-space-collapse="false" font-size="11pt" font-weight="bold">${Static["org.ofbiz.base.util.UtilProperties"].getMessage("DepotSalesUiLabels", "Invoice4", locale)}</fo:block>
            <fo:block  keep-together="always" text-align="center" font-family="Courier,Mangal" white-space-collapse="false" font-size="11pt" font-weight="bold">${Static["org.ofbiz.base.util.UtilProperties"].getMessage("DepotSalesUiLabels", "Invoice5", locale)}</fo:block>
            <fo:block  keep-together="always" text-align="center" font-family="Courier,Mangal" white-space-collapse="false" font-size="11pt" font-weight="bold">${Static["org.ofbiz.base.util.UtilProperties"].getMessage("DepotSalesUiLabels", "Invoice6", locale)}</fo:block>
            <fo:block linefeed-treatment="preserve">&#xA;</fo:block>
            <fo:block>
               <fo:table text-align="center" border-style="solid">
                 <fo:table-column column-width="280pt"/>
                 <fo:table-column column-width="90pt"/>
                 <fo:table-column column-width="90pt"/>
                 <fo:table-column column-width="90pt"/>
                 <fo:table-column column-width="90pt"/>
                  <fo:table-body>
                      <fo:table-row>
                        <fo:table-cell border-right-style="solid" >
                             <fo:block white-space-collapse="false" font-size="11pt" text-align="center"></fo:block>
                        </fo:table-cell>
                         <fo:table-cell border-right-style="solid" border-bottom-style="solid">
                             <fo:block white-space-collapse="false" font-size="11pt" text-align="center">${Static["org.ofbiz.base.util.UtilProperties"].getMessage("DepotSalesUiLabels", "Invoice7", locale)}</fo:block>
                        </fo:table-cell>
                        <fo:table-cell border-right-style="solid" border-bottom-style="solid">
                             <fo:block white-space-collapse="false" font-size="11pt" text-align="center"></fo:block>
                        </fo:table-cell>
                        <fo:table-cell border-right-style="solid" border-bottom-style="solid">
                             <fo:block white-space-collapse="false" font-size="11pt" text-align="center">${Static["org.ofbiz.base.util.UtilProperties"].getMessage("DepotSalesUiLabels", "Invoice8", locale)}</fo:block>
                        </fo:table-cell>
                        <fo:table-cell border-right-style="solid" border-bottom-style="solid">
                             <fo:block white-space-collapse="false" font-size="11pt" text-align="center"></fo:block>
                        </fo:table-cell>
                      </fo:table-row>
                      <fo:table-row>
                        <fo:table-cell border-right-style="solid" >
                             <fo:block white-space-collapse="false" font-size="11pt" text-align="center"></fo:block>
                        </fo:table-cell>
                         <fo:table-cell border-right-style="solid" border-bottom-style="solid">
                             <fo:block white-space-collapse="false" font-size="11pt" text-align="center">${Static["org.ofbiz.base.util.UtilProperties"].getMessage("DepotSalesUiLabels", "Invoice9", locale)}</fo:block>
                             <fo:block white-space-collapse="false" font-size="11pt" text-align="center">${Static["org.ofbiz.base.util.UtilProperties"].getMessage("DepotSalesUiLabels", "Invoice10", locale)}</fo:block>
                        </fo:table-cell>
                        <fo:table-cell border-right-style="solid" border-bottom-style="solid">
                             <fo:block white-space-collapse="false" font-size="11pt" text-align="center"></fo:block>
                        </fo:table-cell>
                        <fo:table-cell border-right-style="solid" border-bottom-style="solid">
                             <fo:block white-space-collapse="false" font-size="11pt" text-align="center">${Static["org.ofbiz.base.util.UtilProperties"].getMessage("DepotSalesUiLabels", "Invoice11", locale)}</fo:block>
                        </fo:table-cell>
                        <fo:table-cell border-right-style="solid" border-bottom-style="solid">
                             <fo:block white-space-collapse="false" font-size="11pt" text-align="center"></fo:block>
                        </fo:table-cell>
                      </fo:table-row>
                      <fo:table-row>
                        <fo:table-cell border-right-style="solid" >
                             <fo:block white-space-collapse="false" font-size="11pt" text-align="center"></fo:block>
                        </fo:table-cell>
                         <fo:table-cell border-right-style="solid" border-bottom-style="solid">
                             <fo:block white-space-collapse="false" font-size="11pt" text-align="center">${Static["org.ofbiz.base.util.UtilProperties"].getMessage("DepotSalesUiLabels", "Invoice12", locale)}</fo:block>
                             <fo:block white-space-collapse="false" font-size="11pt" text-align="center">${Static["org.ofbiz.base.util.UtilProperties"].getMessage("DepotSalesUiLabels", "Invoice13", locale)}</fo:block>
                        </fo:table-cell>
                        <fo:table-cell border-right-style="solid" border-bottom-style="solid">
                             <fo:block white-space-collapse="false" font-size="11pt" text-align="center"></fo:block>
                        </fo:table-cell>
                        <fo:table-cell border-right-style="solid" border-bottom-style="solid">
                             <fo:block white-space-collapse="false" font-size="11pt" text-align="center">${Static["org.ofbiz.base.util.UtilProperties"].getMessage("DepotSalesUiLabels", "Invoice14", locale)}</fo:block>
                        </fo:table-cell>
                        <fo:table-cell border-right-style="solid" border-bottom-style="solid">
                             <fo:block white-space-collapse="false" font-size="11pt" text-align="center"></fo:block>
                        </fo:table-cell>
                      </fo:table-row>
                      <fo:table-row>
                        <fo:table-cell border-right-style="solid" >
                             <fo:block white-space-collapse="false" font-size="11pt" text-align="center"></fo:block>
                        </fo:table-cell>
                         <fo:table-cell border-right-style="solid" border-bottom-style="solid">
                             <fo:block white-space-collapse="false" font-size="11pt" text-align="center">${Static["org.ofbiz.base.util.UtilProperties"].getMessage("DepotSalesUiLabels", "Invoice15", locale)}</fo:block>
                             <fo:block white-space-collapse="false" font-size="11pt" text-align="center">${Static["org.ofbiz.base.util.UtilProperties"].getMessage("DepotSalesUiLabels", "Invoice16", locale)}</fo:block>
                        </fo:table-cell>
                        <fo:table-cell border-right-style="solid" border-bottom-style="solid">
                             <fo:block white-space-collapse="false" font-size="11pt" text-align="center"></fo:block>
                        </fo:table-cell>
                        <fo:table-cell border-right-style="solid" border-bottom-style="solid">
                             <fo:block white-space-collapse="false" font-size="11pt" text-align="center"><fo:block white-space-collapse="false" font-size="11pt" text-align="center">${Static["org.ofbiz.base.util.UtilProperties"].getMessage("DepotSalesUiLabels", "Invoice17", locale)}</fo:block></fo:block>
                        </fo:table-cell>
                        <fo:table-cell border-right-style="solid" border-bottom-style="solid">
                             <fo:block white-space-collapse="false" font-size="11pt" text-align="center"></fo:block>
                        </fo:table-cell>
                      </fo:table-row>
                  </fo:table-body>
               </fo:table>
            </fo:block>
            <fo:block linefeed-treatment="preserve">&#xA;</fo:block>
               <fo:block white-space-collapse="false" font-size="11pt" text-align="left">${Static["org.ofbiz.base.util.UtilProperties"].getMessage("DepotSalesUiLabels", "Invoice18", locale)}</fo:block>
               <fo:block white-space-collapse="false" font-size="11pt" text-align="left">${Static["org.ofbiz.base.util.UtilProperties"].getMessage("DepotSalesUiLabels", "Invoice19", locale)}</fo:block>
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
            	            <fo:block font-size="10pt" font-weight="bold" keep-together="always"  text-align="center" white-space-collapse="false">${Static["org.ofbiz.base.util.UtilProperties"].getMessage("DepotSalesUiLabels", "Invoice21", locale)}   </fo:block>
            	          </fo:table-cell >
            	          <fo:table-cell border-right-style="solid" border-bottom-style="solid">
            	            <fo:block font-size="10pt" font-weight="bold" keep-together="always"  text-align="center" white-space-collapse="false">${Static["org.ofbiz.base.util.UtilProperties"].getMessage("DepotSalesUiLabels", "Invoice22", locale)}   </fo:block>
            	            <fo:block font-size="10pt" font-weight="bold" keep-together="always"  text-align="center" white-space-collapse="false">${Static["org.ofbiz.base.util.UtilProperties"].getMessage("DepotSalesUiLabels", "Invoice23", locale)}   </fo:block>
            	          </fo:table-cell >
            	          <fo:table-cell border-right-style="solid" border-bottom-style="solid">
            	            <fo:block font-size="10pt" font-weight="bold" keep-together="always"  text-align="center" white-space-collapse="false">${Static["org.ofbiz.base.util.UtilProperties"].getMessage("DepotSalesUiLabels", "Invoice24", locale)}   </fo:block>
            	            <fo:block font-size="10pt" font-weight="bold" keep-together="always"  text-align="center" white-space-collapse="false">${Static["org.ofbiz.base.util.UtilProperties"].getMessage("DepotSalesUiLabels", "Invoice25", locale)}   </fo:block>
            	          </fo:table-cell >
            	          <fo:table-cell border-right-style="solid" border-bottom-style="solid">
            	            <fo:block font-size="10pt" font-weight="bold" keep-together="always"  text-align="center" white-space-collapse="false">${Static["org.ofbiz.base.util.UtilProperties"].getMessage("DepotSalesUiLabels", "Invoice26", locale)}   </fo:block>
            	          </fo:table-cell >
            	          <fo:table-cell border-right-style="solid" border-bottom-style="solid">
            	            <fo:block font-size="10pt" font-weight="bold" keep-together="always"  text-align="center" white-space-collapse="false">${Static["org.ofbiz.base.util.UtilProperties"].getMessage("DepotSalesUiLabels", "Invoice27", locale)} </fo:block>
            	            <fo:block font-size="10pt" font-weight="bold" keep-together="always"  text-align="center" white-space-collapse="false">${Static["org.ofbiz.base.util.UtilProperties"].getMessage("DepotSalesUiLabels", "Invoice28", locale)} </fo:block>
            	          </fo:table-cell >
            	          <fo:table-cell border-right-style="solid" border-bottom-style="solid">
            	            <fo:block font-size="10pt" font-weight="bold" keep-together="always"  text-align="center" white-space-collapse="false">${Static["org.ofbiz.base.util.UtilProperties"].getMessage("DepotSalesUiLabels", "Invoice29", locale)}    </fo:block>
            	            <fo:block font-size="10pt" font-weight="bold" keep-together="always"  text-align="center" white-space-collapse="false">${Static["org.ofbiz.base.util.UtilProperties"].getMessage("DepotSalesUiLabels", "Invoice30", locale)}    </fo:block>
            	          </fo:table-cell >
            	          <fo:table-cell border-right-style="solid" border-bottom-style="solid">
            	            <fo:block font-size="10pt" font-weight="bold" keep-together="always"  text-align="center" white-space-collapse="false">${Static["org.ofbiz.base.util.UtilProperties"].getMessage("DepotSalesUiLabels", "Invoice31", locale)}    </fo:block>
            	            <fo:block font-size="10pt" font-weight="bold" keep-together="always"  text-align="center" white-space-collapse="false">${Static["org.ofbiz.base.util.UtilProperties"].getMessage("DepotSalesUiLabels", "Invoice32", locale)}    </fo:block>
            	          </fo:table-cell >
            	          <fo:table-cell border-right-style="solid" border-bottom-style="solid">
            	            <fo:block font-size="10pt" font-weight="bold" keep-together="always"  text-align="center" white-space-collapse="false">${Static["org.ofbiz.base.util.UtilProperties"].getMessage("DepotSalesUiLabels", "Invoice33", locale)}    </fo:block>
            	            <fo:block font-size="10pt" font-weight="bold" keep-together="always"  text-align="center" white-space-collapse="false">    </fo:block>
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
		           
		  <#--          		          <#assign UomIdDetails = delegator.findOne("Uom", {"uomId" : ${productNameDetails.get("quantityUomId")?if_exists} }, true)> -->
		           
                  	 <fo:table-row >
                	   <fo:table-cell border-right-style="solid" border-bottom-style="solid"><fo:block text-align="center"  font-size="12pt" >ddddddddddddddd${sNO?if_exists} </fo:block></fo:table-cell>     
  				       <fo:table-cell  border-right-style="solid" border-bottom-style="solid"><fo:block text-align="left"    font-size="12pt" >ddddd${productNameDetails.get("productName")?if_exists} </fo:block></fo:table-cell>     
  				       <fo:table-cell border-right-style="solid" border-bottom-style="solid"><fo:block text-align="left" font-size="12pt">dddddd&#160;</fo:block></fo:table-cell>
  				       <fo:table-cell  border-right-style="solid" border-bottom-style="solid"><fo:block text-align="right"  font-size="12pt">dddddddd${eachItem.get("quantity")?if_exists}</fo:block></fo:table-cell>
  				       <fo:table-cell  border-right-style="solid" border-bottom-style="solid"><fo:block text-align="right"  font-size="12pt">ddddd${eachItem.get("unitPrice")?if_exists}</fo:block></fo:table-cell>
  				       <fo:table-cell  border-right-style="solid" border-bottom-style="solid"><fo:block text-align="right"  font-size="12pt">dddd</fo:block></fo:table-cell>
  				       <fo:table-cell  border-right-style="solid" border-bottom-style="solid"><fo:block text-align="right"  font-size="12pt">ddddd</fo:block></fo:table-cell>
  				       <fo:table-cell  border-right-style="solid" border-bottom-style="solid"><fo:block text-align="right"  font-size="12pt">ddddddddddd${eachItem.get("unitPrice")?if_exists}</fo:block></fo:table-cell>     
  				     </fo:table-row>
  				    	<#assign sNo=sNo+1>
  				     </#list>
  				     <fo:table-row>
  				        <fo:table-cell border-right-style="solid" border-bottom-style="solid"><fo:block text-align="center"  font-size="12pt" > </fo:block></fo:table-cell>
  				        <fo:table-cell border-right-style="solid" border-bottom-style="solid"><fo:block text-align="right"  font-size="12pt" >${Static["org.ofbiz.base.util.UtilProperties"].getMessage("DepotSalesUiLabels", "Invoice34", locale)} </fo:block></fo:table-cell>
  				        <fo:table-cell border-right-style="solid" border-bottom-style="solid"><fo:block text-align="center"  font-size="12pt" > </fo:block></fo:table-cell>
  				        <fo:table-cell border-right-style="solid" border-bottom-style="solid"><fo:block text-align="center"  font-size="12pt" > </fo:block></fo:table-cell>
  				        <fo:table-cell border-right-style="solid" border-bottom-style="solid"><fo:block text-align="center"  font-size="12pt" > </fo:block></fo:table-cell>
  				        <fo:table-cell border-right-style="solid" border-bottom-style="solid"><fo:block text-align="center"  font-size="12pt" > </fo:block></fo:table-cell>
  				        <fo:table-cell border-right-style="solid" border-bottom-style="solid"><fo:block text-align="center"  font-size="12pt" > </fo:block></fo:table-cell>
  				        <fo:table-cell border-right-style="solid" border-bottom-style="solid"><fo:block text-align="center"  font-size="12pt" > </fo:block></fo:table-cell>
  				     </fo:table-row>
                    </fo:table-body>
                </fo:table>
               </fo:block>   
                <fo:block linefeed-treatment="preserve">&#xA;</fo:block>
                <fo:block>
	                     <fo:table>
	                        <fo:table-column column-width="100"/>
	                        <fo:table-column column-width="350"/>
	                          <fo:table-body>
	                             <fo:table-row>
	                               <fo:table-cell><fo:block white-space-collapse="false" font-size="11pt" text-align="left">${Static["org.ofbiz.base.util.UtilProperties"].getMessage("DepotSalesUiLabels", "Invoice35", locale)}</fo:block>	</fo:table-cell>
                                                                                  
	                               <fo:table-cell>
	                                  <fo:block white-space-collapse="false" font-size="11pt" text-align="right">${Static["org.ofbiz.base.util.UtilProperties"].getMessage("DepotSalesUiLabels", "Invoice36", locale)}</fo:block>              
	                               </fo:table-cell>
	                             </fo:table-row>
	                          </fo:table-body>
	                     </fo:table>
	                   </fo:block>
                     <fo:block white-space-collapse="false" font-size="11pt" text-align="center">&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;${Static["org.ofbiz.base.util.UtilProperties"].getMessage("DepotSalesUiLabels", "Invoice37", locale)}</fo:block>
                     <fo:block white-space-collapse="false" font-size="11pt" text-align="center">&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;${Static["org.ofbiz.base.util.UtilProperties"].getMessage("DepotSalesUiLabels", "Invoice38", locale)}</fo:block>
                     <fo:block white-space-collapse="false" font-size="11pt" text-align="left">${Static["org.ofbiz.base.util.UtilProperties"].getMessage("DepotSalesUiLabels", "Invoice39", locale)}</fo:block>
                <fo:block linefeed-treatment="preserve">&#xA;</fo:block>
                 
               //
               <fo:block>
               <fo:table text-align="center" border-style="solid">
                 <fo:table-column column-width="200pt"/>
                 <fo:table-column column-width="90pt"/>
                 <fo:table-column column-width="90pt"/>
                 <fo:table-column column-width="90pt"/>
                 <fo:table-column column-width="180pt"/>
                  <fo:table-body>
                      <fo:table-row>
                        <fo:table-cell  >
                             <fo:block white-space-collapse="false" font-size="11pt" text-align="left">${Static["org.ofbiz.base.util.UtilProperties"].getMessage("DepotSalesUiLabels", "Invoice41", locale)}</fo:block>
                        </fo:table-cell>
                         <fo:table-cell  >
                             <fo:block white-space-collapse="false" font-size="11pt" text-align="center"></fo:block>
                        </fo:table-cell>
                        <fo:table-cell >
                             <fo:block white-space-collapse="false" font-size="11pt" text-align="center"></fo:block>
                        </fo:table-cell>
                        <fo:table-cell border-right-style="solid" >
                             <fo:block white-space-collapse="false" font-size="11pt" text-align="center"></fo:block>
                        </fo:table-cell>
                        <fo:table-cell border-right-style="solid" border-bottom-style="solid">
                             <fo:block white-space-collapse="false"  font-size="11pt" text-align="center">${Static["org.ofbiz.base.util.UtilProperties"].getMessage("DepotSalesUiLabels", "Invoice40", locale)}</fo:block>
                        </fo:table-cell>
                      </fo:table-row>
                      <fo:table-row>
                        <fo:table-cell  >
                             <fo:block white-space-collapse="false" font-size="11pt" text-align="left">${Static["org.ofbiz.base.util.UtilProperties"].getMessage("DepotSalesUiLabels", "Invoice42", locale)}</fo:block>
                        </fo:table-cell>
                         <fo:table-cell >
                             <fo:block white-space-collapse="false" font-size="11pt" text-align="center"></fo:block>
                        </fo:table-cell>
                        <fo:table-cell >
                             <fo:block white-space-collapse="false" font-size="11pt" text-align="center"></fo:block>
                        </fo:table-cell>
                        <fo:table-cell border-right-style="solid" >
                             <fo:block white-space-collapse="false" font-size="11pt" text-align="center"></fo:block>
                        </fo:table-cell>
                        <fo:table-cell border-right-style="solid" border-bottom-style="solid">
                             <fo:block  font-size="11pt" text-align="center"></fo:block>
                        </fo:table-cell>
                      </fo:table-row>
                      <fo:table-row>
                        <fo:table-cell >
                             <fo:block white-space-collapse="false" font-size="11pt" text-align="left">${Static["org.ofbiz.base.util.UtilProperties"].getMessage("DepotSalesUiLabels", "Invoice43", locale)}</fo:block>
                        </fo:table-cell>
                         <fo:table-cell >
                             <fo:block white-space-collapse="false" font-size="11pt" text-align="center"></fo:block>
                        </fo:table-cell>
                        <fo:table-cell >
                             <fo:block white-space-collapse="false" font-size="11pt" text-align="center"></fo:block>
                        </fo:table-cell>
                        <fo:table-cell border-right-style="solid" >
                             <fo:block white-space-collapse="false" font-size="11pt" text-align="center"></fo:block>
                        </fo:table-cell>
                        <fo:table-cell border-right-style="solid" border-bottom-style="solid">
                             <fo:block  font-size="11pt" text-align="center"></fo:block>
                        </fo:table-cell>
                      </fo:table-row>
                      <fo:table-row>
                        <fo:table-cell >
                             <fo:block white-space-collapse="false" font-size="11pt" text-align="left">${Static["org.ofbiz.base.util.UtilProperties"].getMessage("DepotSalesUiLabels", "Invoice44", locale)}</fo:block>
                        </fo:table-cell>
                         <fo:table-cell >
                             <fo:block white-space-collapse="false" font-size="11pt" text-align="left">${Static["org.ofbiz.base.util.UtilProperties"].getMessage("DepotSalesUiLabels", "Invoice45", locale)}</fo:block>
                        </fo:table-cell>
                        <fo:table-cell >
                             <fo:block white-space-collapse="false" font-size="11pt" text-align="center">${Static["org.ofbiz.base.util.UtilProperties"].getMessage("DepotSalesUiLabels", "Invoice46", locale)}</fo:block>
                        </fo:table-cell>
                        <fo:table-cell border-right-style="solid" >
                             <fo:block white-space-collapse="false" font-size="11pt" text-align="center">${Static["org.ofbiz.base.util.UtilProperties"].getMessage("DepotSalesUiLabels", "Invoice47", locale)}</fo:block>
                             <fo:block white-space-collapse="false" font-size="11pt" text-align="center">${Static["org.ofbiz.base.util.UtilProperties"].getMessage("DepotSalesUiLabels", "Invoice48", locale)}</fo:block>
                        </fo:table-cell>
                        <fo:table-cell border-right-style="solid" border-bottom-style="solid">
                        <fo:block>&#160;</fo:block>
                        <fo:block>&#160;</fo:block>
                             <fo:block  font-size="11pt" text-align="center">${Static["org.ofbiz.base.util.UtilProperties"].getMessage("DepotSalesUiLabels", "Invoice49", locale)}</fo:block>
                        </fo:table-cell>
                      </fo:table-row>
                      
                  </fo:table-body>
               </fo:table>
            </fo:block>
            <fo:block>&#160;</fo:block><fo:block>&#160;</fo:block><fo:block>&#160;</fo:block>
               <fo:block  keep-together="always" text-align="left" font-family="Courier,Mangal" white-space-collapse="false" font-size="11pt" font-weight="bold">${Static["org.ofbiz.base.util.UtilProperties"].getMessage("DepotSalesUiLabels", "Invoice50", locale)}</fo:block>
               <fo:block  keep-together="always" text-align="left" font-family="Courier,Mangal" white-space-collapse="false" font-size="11pt" >${Static["org.ofbiz.base.util.UtilProperties"].getMessage("DepotSalesUiLabels", "Invoice51", locale)}</fo:block>
               <fo:block  keep-together="always" text-align="left" font-family="Courier,Mangal" white-space-collapse="false" font-size="11pt" >${Static["org.ofbiz.base.util.UtilProperties"].getMessage("DepotSalesUiLabels", "Invoice52", locale)}</fo:block>
               <fo:block  keep-together="always" text-align="left" font-family="Courier,Mangal" white-space-collapse="false" font-size="11pt" >${Static["org.ofbiz.base.util.UtilProperties"].getMessage("DepotSalesUiLabels", "Invoice53", locale)}</fo:block>
               
               <fo:block>&#160;</fo:block>
               <fo:block>&#160;</fo:block>
               <fo:block>&#160;</fo:block>
               
               <fo:block  keep-together="always" text-align="left" font-family="Courier,Mangal" white-space-collapse="false" font-size="11pt" >${Static["org.ofbiz.base.util.UtilProperties"].getMessage("DepotSalesUiLabels", "Invoice54", locale)}</fo:block>
               
               
			 </fo:flow>
			 </fo:page-sequence>
				<fo:page-sequence master-reference="main">
    			<fo:flow flow-name="xsl-region-body" font-family="Courier,Mangal">
       		 		<fo:block font-size="14pt">
            			NO RECORDS FOUND
       		 		</fo:block>
    			</fo:flow>
			</fo:page-sequence>
</fo:root>
</#escape>
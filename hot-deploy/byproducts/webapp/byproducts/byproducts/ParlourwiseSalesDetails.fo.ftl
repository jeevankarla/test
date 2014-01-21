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
	<fo:simple-page-master master-name="main" page-height="12in" page-width="15in"
             margin-bottom=".5in" margin-left=".3in" margin-right=".5in">
        <fo:region-body margin-top="1.6in"/>
        <fo:region-before extent="1in"/>
        <fo:region-after extent="1in"/>        
    </fo:simple-page-master>   
</fo:layout-master-set>
${setRequestAttribute("OUTPUT_FILENAME", "ParlourwiseSalesDetails.txt")}

<#if finalParloursList?has_content>
<#list finalParloursList as eachParlour>
	<#assign parlourDetailsList = eachParlour.entrySet()>
	<#list parlourDetailsList as parlourDetails>
	<#assign parlourProducts = parlourDetails.getValue()>
	<#assign productEntry = parlourProducts.entrySet()>
	<fo:page-sequence master-reference="main" force-page-count="no-force">					
			<fo:static-content flow-name="xsl-region-before"> 
					<fo:block white-space-collapse="false" font-size="7pt"  font-family="Courier,monospace"  text-align="left">${uiLabelMap.CommonPage} <fo:page-number/>                                                 </fo:block>
					<fo:block font-size="7pt" text-align="left" keep-together="always" white-space-collapse="false">&#160;                                                                                                    ${uiLabelMap.aavinDairyMsg}</fo:block>
					<fo:block font-size="7pt" text-align="left" keep-together="always" white-space-collapse="false">&#160;                                                                                                   CORPORATE OFFICE, MARKETING UNIT, NANDANAM, CHENNAI-35,</fo:block>			
					<fo:block font-size="7pt" text-align="left" keep-together="always" white-space-collapse="false">&#160;                                                                                                   PARLOUR WISE DETAILS FOR THE MONTH OF ${monthDate.toUpperCase()?if_exists}.</fo:block>
					<#assign parlourFacility = delegator.findOne("Facility", {"facilityId" : parlourDetails.getKey()}, true)>
              		<fo:block font-size="12pt" align-text="left">------------------------------------------------------------------------------------------------------------------------------------------------------------------</fo:block>
              		<fo:block white-space-collapse="false" font-size="8pt"  font-family="Courier,monospace" keep-together="always" text-align="left">PARTY CODE ::  ${parlourDetails.getKey().toUpperCase()}             PARTY NAME :: ${parlourFacility.facilityName}                        </fo:block>
  	              	<fo:block font-size="12pt" align-text="left">------------------------------------------------------------------------------------------------------------------------------------------------------------------</fo:block>
            		<fo:block white-space-collapse="false" font-size="8pt"  font-family="Courier,monospace" keep-together="always" text-align="left"> SL  PROD  PRODUCT NAME       QTY    BASIC   VAT  VAT           &lt;&lt; P A R L O U R &gt;&gt;        &lt;&lt; W H O L E S A L E DEALER &gt;&gt;  &lt;&lt;DIFF-VAL&gt;&gt; UTP</fo:block>
            		<fo:block white-space-collapse="false" font-size="8pt"  font-family="Courier,monospace" keep-together="always" text-align="left"> NO  CODE               	            RATE   RATE  (%)   BASIC-VAL     VAT-VAL     TOT-VAL  BASIC-VAL    VAT-VAL    TOT-VAL    PAR-WSD   VALUE</fo:block>
            		<fo:block font-size="12pt" align-text="left">------------------------------------------------------------------------------------------------------------------------------------------------------------------</fo:block>
			</fo:static-content>
			<fo:flow flow-name="xsl-region-body" font-family="Courier,monospace">
       		
				<fo:table width="100%" table-layout="fixed" space-after="0.0in">
					<fo:table-column column-width="100%"/>
            		<fo:table-body>
			        	<fo:table-row column-width="100%">
			            <fo:table-cell column-width="100%">
            			<fo:table  table-layout="fixed" > 	
            				<fo:table-column column-width="11pt"/>
				   		 	<fo:table-column column-width="22pt"/>
				   		 	<fo:table-column column-width="55pt"/>
				   		 	<fo:table-column column-width="67pt"/>
				   		 	<fo:table-column column-width="37pt"/>
				   		 	<fo:table-column column-width="34pt"/>
				   		 	<fo:table-column column-width="28pt"/>
				   		 	<fo:table-column column-width="46pt"/>
				   		 	<fo:table-column column-width="60pt"/>
				   		 	<fo:table-column column-width="58pt"/>
				   		 	<fo:table-column column-width="55pt"/>
				   		 	<fo:table-column column-width="45pt"/>
				   		 	<fo:table-column column-width="45pt"/>
				    		<fo:table-column column-width="50pt"/>
				    		<fo:table-column column-width="30pt"/>
				    			<fo:table-body>
				    			<#assign totalPBasicValue = 0>
				    			<#assign totalPVatValue = 0>
				    			<#assign totalPTotValue = 0>
				    			<#assign totalWBasicValue = 0>
				    			<#assign totalWVatValue = 0>
				    			<#assign totalWTotValue = 0>
				    			<#assign totalDiffValue = 0>
				    			<#assign serialNo = 1>
				    			<#list productEntry as eachProduct>
				    			<#assign productDetail = eachProduct.getValue()>
				    			<#assign product = delegator.findOne("Product", {"productId" : eachProduct.getKey()}, true)>
                				<fo:table-row font-size="9pt">                            
                            		<fo:table-cell><fo:block font-size="7pt" text-align="left">${serialNo?if_exists}</fo:block></fo:table-cell>
                            		<fo:table-cell><fo:block font-size="7pt" text-align="left">${eachProduct.getKey()?if_exists}</fo:block></fo:table-cell>
                            		<fo:table-cell><fo:block font-size="7pt" text-align="left" keep-together="always">${product.productName?if_exists}</fo:block></fo:table-cell>
                            		<fo:table-cell><fo:block font-size="7pt" text-align="right">${productDetail.get('quantity')?if_exists}</fo:block></fo:table-cell>
                            		<fo:table-cell><fo:block font-size="7pt" text-align="right">${productDetail.get('BasicParlourPrice')?if_exists?string("##0.00")}</fo:block></fo:table-cell>
                            		<fo:table-cell><fo:block font-size="7pt" text-align="right">${productDetail.get('P_VAT_amount')?if_exists?string("##0.00")}</fo:block></fo:table-cell>
                            		<fo:table-cell><fo:block font-size="7pt" text-align="right">${productDetail.get('P_VAT_percent')?if_exists}</fo:block></fo:table-cell>
                            		<#assign basicParlorRate = productDetail.get('quantity')*productDetail.get('BasicParlourPrice')>
                            		<#assign vatParlorRate = productDetail.get('quantity')*productDetail.get('P_VAT_amount')>
                            		<#assign totalParlorRate = basicParlorRate+vatParlorRate>
                            		<fo:table-cell><fo:block font-size="7pt" text-align="right">${basicParlorRate?if_exists?string("##0.00")}</fo:block></fo:table-cell>
                            		<fo:table-cell><fo:block font-size="7pt" text-align="right">${vatParlorRate?if_exists?string("##0.00")}</fo:block></fo:table-cell>
                            		<fo:table-cell><fo:block font-size="7pt" text-align="right">${totalParlorRate?if_exists?string("##0.00")}</fo:block></fo:table-cell>
                            		<#assign basicWSDRate = productDetail.get('quantity')*productDetail.get('BasicWholeSalePrice')>
                            		<#assign vatWSDRate = productDetail.get('quantity')*productDetail.get('W_VAT_amount')>
                            		<#assign totalWSDRate = basicWSDRate+vatWSDRate>
                            		<fo:table-cell><fo:block font-size="7pt" text-align="right">${basicWSDRate?if_exists?string("##0.00")}</fo:block></fo:table-cell>
                            		<fo:table-cell><fo:block font-size="7pt" text-align="right">${vatWSDRate?if_exists?string("##0.00")}</fo:block></fo:table-cell>
                            		<fo:table-cell><fo:block font-size="7pt" text-align="right">${totalWSDRate?if_exists?string("##0.00")}</fo:block></fo:table-cell>
                            		<#assign diffValue = totalParlorRate-totalWSDRate>
                            		<fo:table-cell><fo:block font-size="7pt" text-align="right">${diffValue?if_exists?string("##0.00")}</fo:block></fo:table-cell>
                            		<fo:table-cell></fo:table-cell>	
                            		<#assign totalPBasicValue = totalPBasicValue+basicParlorRate>
                            		<#assign totalPVatValue = totalPVatValue+vatParlorRate>
                            		<#assign totalPTotValue = totalPTotValue+totalParlorRate>
                            		<#assign totalWBasicValue = totalWBasicValue+basicWSDRate>
                            		<#assign totalWVatValue = totalWVatValue+vatWSDRate>
                            		<#assign totalWTotValue = totalWTotValue+totalWSDRate>
                            		<#assign totalDiffValue = totalDiffValue+diffValue>                           
                       			</fo:table-row>
                       			<#assign serialNo = serialNo + 1>
                       			</#list>
                       			<fo:table-row>                            
                            		<fo:table-cell>
                                		<fo:block font-size="9pt">------------------------------------------------------------------------------------------------------------------------------------------------------------------</fo:block>
                            		</fo:table-cell>
                            	</fo:table-row>
                            	<fo:table-row font-size="9pt">      
                            		<fo:table-cell/>
                            		<fo:table-cell>
                            			<fo:block font-size="7pt" text-align="left" keep-together="always" white-space-collapse="false">&lt; &lt; PARTY CODE WISE TOTAL &gt; &gt; </fo:block>
                            		</fo:table-cell>
                            		<fo:table-cell/>
                            		<fo:table-cell/>
                            		<fo:table-cell/>
                            		<fo:table-cell/>
                            		<fo:table-cell/>
                            		<fo:table-cell><fo:block font-size="7pt" text-align="right">${totalPBasicValue?if_exists?string("##0.00")}</fo:block></fo:table-cell>
                            		<fo:table-cell><fo:block font-size="7pt" text-align="right">${totalPVatValue?if_exists?string("##0.00")}</fo:block></fo:table-cell>
                            		<fo:table-cell><fo:block font-size="7pt" text-align="right">${totalPTotValue?if_exists?string("##0.00")}</fo:block></fo:table-cell>
                            		<fo:table-cell><fo:block font-size="7pt" text-align="right">${totalWBasicValue?if_exists?string("##0.00")}</fo:block></fo:table-cell>
                            		<fo:table-cell><fo:block font-size="7pt" text-align="right">${totalWVatValue?if_exists?string("##0.00")}</fo:block></fo:table-cell>
                            		<fo:table-cell><fo:block font-size="7pt" text-align="right">${totalWTotValue?if_exists?string("##0.00")}</fo:block></fo:table-cell>
                            		<fo:table-cell><fo:block font-size="7pt" text-align="right">${totalDiffValue?if_exists?string("##0.00")}</fo:block></fo:table-cell>
                            		<fo:table-cell/>
                            	</fo:table-row>
                            	<fo:table-row>                            
                            		<fo:table-cell>
                                		<fo:block font-size="9pt">------------------------------------------------------------------------------------------------------------------------------------------------------------------</fo:block>
                            		</fo:table-cell>
                            	</fo:table-row>
            				</fo:table-body>
       					 </fo:table>
       				</fo:table-cell>
        		</fo:table-row>
         	</fo:table-body>
         </fo:table>
	   </fo:flow>
	    </fo:page-sequence>	
	  </#list>
	  </#list>  
	     <#else>
		<fo:page-sequence master-reference="main">
	    	<fo:flow flow-name="xsl-region-body" font-family="Helvetica">
	       		 <fo:block font-size="14pt">
	            	${uiLabelMap.OrderNoOrderFound}.
	       		 </fo:block>
	    	</fo:flow>
		</fo:page-sequence>
	</#if>						        	
</fo:root>
</#escape>
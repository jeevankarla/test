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
            <fo:simple-page-master master-name="main" page-height="12in" page-width="14.5in"
                   margin-top="0.8in" margin-bottom="0.8in" margin-left=".2in" margin-right=".1in">
                <fo:region-body margin-top="1.8in"/>
                <fo:region-before extent=".5in"/>
                <fo:region-after extent=".5in"/>
            </fo:simple-page-master>
        </fo:layout-master-set>
      		<#assign routeWiseTotalList =routeWiseTotalMap.entrySet()>
		    <#if routeWiseTotalList?has_content>  
		    	<#list routeWiseTotalList as routeWiseEntries>
			        <fo:page-sequence master-reference="main">
			        	<fo:static-content flow-name="xsl-region-before">
			        		<fo:block text-align="center" font-family="Helvetica" white-space-collapse="false" font-size="16pt" keep-together="always" font-weight="bold">SUPRAJA DAIRY PVT.LTD</fo:block>
			        		<fo:block text-align="center" font-family="Helvetica" white-space-collapse="false" font-size="13pt" keep-together="always" font-weight="bold">DAY WISE DAILY ROUTE SALES AND COLLECTION REPORT</fo:block>
			        
			        		<fo:block linefeed-treatment="preserve">&#xA;</fo:block>
			        		<#assign facilityDetails = delegator.findOne("Facility", {"facilityId" : routeWiseEntries.getKey()}, true)>
			        		<fo:block white-space-collapse="false" font-size="13pt"  font-family="Helvetica" keep-together="always" >&#160;    Period : ${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(dayBegin, "dd-MMM-yyyy")}  --  ${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(dayEnd, "dd-MMM-yyyy")}    &#160;                                &#160;                       (Qty. in Liters)                   &#160;                   Route : ${facilityDetails.get("facilityName")?if_exists}</fo:block>
			        	    <fo:block linefeed-treatment="preserve">&#xA;</fo:block>
			        		<fo:block white-space-collapse="false" font-size="11pt"    text-align="left" font-family="Helvetica" keep-together="always" >ROUTE :${routeWiseEntries.getKey()?if_exists}&#160;   ; &#160; AREA NAME  :${facilityDetails.get("facilityName")?if_exists}</fo:block>
			        		<fo:block >
			        			 <fo:table width="100%" table-layout="fixed" border-style="solid" font-size="10pt">
					                  <fo:table-column column-width="40pt"/>
					                 <fo:table-column column-width="130pt"/>               
						            <#list byProductsList as product>            
						             	<fo:table-column column-width="40pt"/> 						             	
						            </#list>						            
						            <fo:table-column column-width="50pt"/>
						            <fo:table-column column-width="50pt"/>
						             <fo:table-column column-width="50pt"/>
						            <fo:table-column column-width="50pt"/>
						            <#--
						             <fo:table-column column-width="50pt"/>
						            <fo:table-column column-width="50pt"/>
						             <fo:table-column column-width="50pt"/> -->
						           	<fo:table-body>
				                     <fo:table-row>
				                      <fo:table-cell border-style="solid"><fo:block text-align="center" font-weight="bold" keep-together="always" >S.No</fo:block></fo:table-cell>
				                     <fo:table-cell border-style="solid"><fo:block text-align="center"  font-weight="bold" keep-together="always" >SALE DATE</fo:block></fo:table-cell>       		
				                       <#list lmsProductList as product>                       		
				                       		<fo:table-cell border-style="solid">
				                       			<fo:block keep-together="always" text-align="center" font-weight="bold" white-space-collapse="false"  >${product.brandName?if_exists}</fo:block>
				                       		</fo:table-cell>
				                       	</#list>
				                     </fo:table-row>
				                     
				                     	<fo:table-row>
				                     	<fo:table-cell><fo:block text-align="center" keep-together="always" ></fo:block></fo:table-cell>
	                					<fo:table-cell><fo:block text-align="center" keep-together="always" ></fo:block></fo:table-cell>       		
				                       <#list byProductsList as product>                       		
				                       	<fo:table-cell  border-style="solid">
				                       			<fo:block  text-align="center" font-weight="bold" white-space-collapse="false"  >${product.brandName?if_exists}</fo:block>
				                       	</fo:table-cell>
				                       	</#list>				                       			                       	
				                       	<fo:table-cell border-style="solid">
			                       			<fo:block text-align="left" white-space-collapse="false" font-weight="bold" >TOTAL</fo:block>
			                       		</fo:table-cell>			                       		
			                       		<fo:table-cell border-style="solid">
			                       			<fo:block text-align="center" white-space-collapse="false" font-weight="bold" >DES VAL</fo:block>
			                       		</fo:table-cell>
			                       		<fo:table-cell border-style="solid">
			                       			<fo:block text-align="center" white-space-collapse="false" font-weight="bold">Paid Amount</fo:block>
			                       		</fo:table-cell>
			                       		<fo:table-cell border-style="solid">
			                       			<fo:block text-align="center" white-space-collapse="false" font-weight="bold">DUE</fo:block>
			                       		</fo:table-cell>
			                       		<#-- <fo:table-cell border-style="solid">
			                       			<fo:block text-align="center" white-space-collapse="false" font-weight="bold">Opening Bal</fo:block>
			                       		</fo:table-cell>
			                       		<fo:table-cell border-style="solid">
			                       			<fo:block text-align="center" white-space-collapse="false" font-weight="bold">Closing Bal</fo:block>
			                       		</fo:table-cell>
			                       		<fo:table-cell border-style="solid">
			                       			<fo:block text-align="center" white-space-collapse="false" font-weight="bold">Special Discount</fo:block>
			                       		</fo:table-cell> -->
			                       	</fo:table-row>
			                	</fo:table-body>
			                		</fo:table>
			        		</fo:block>
			        		</fo:static-content>
			       		<fo:flow flow-name="xsl-region-body" font-family="Helvetica">
			        		<fo:block >
			        			 <fo:table width="100%" table-layout="fixed" border-style="solid" font-size="10pt">
					                  <fo:table-column column-width="40pt"/>
					                 <fo:table-column column-width="130pt"/>               
						            <#list byProductsList as product>            
						             	<fo:table-column column-width="40pt"/> 						             	
						            </#list>						            
						            <fo:table-column column-width="50pt"/>
						            <fo:table-column column-width="50pt"/>
						             <fo:table-column column-width="50pt"/>
						            <fo:table-column column-width="50pt"/>
						           	<fo:table-body>
				                    
			       			    <#assign agentWiseList = routeWiseEntries.getValue().entrySet()>
			       			  <#assign sno=1>
			       			  <#list agentWiseList as agentWiseEntries>
			       			  	
			       			  	 <#assign totalProductList=agentWiseEntries.getValue().entrySet()>
			       			  	  <#list totalProductList as productList>
			       			    <#if productList.getKey()=="LMS">
			                       	<fo:table-row>
			                       	<fo:table-cell border-style="dotted"><fo:block text-align="center" font-weight="bold" font-size="10pt" keep-together="always">${sno}</fo:block></fo:table-cell>
			                       	 
			                       	<fo:table-cell border-style="dotted"><fo:block text-align="center" font-weight="bold" font-size="10pt" keep-together="always">${agentWiseEntries.getKey()?if_exists}</fo:block></fo:table-cell>
			                       	<#assign lmsProdList = productList.getValue().entrySet()>
			                       	 <#list lmsProdList as eachProd>
				                     <fo:table-cell border-style="dotted"><fo:block text-align="right"  font-weight="bold" font-size="10pt" keep-together="always">${eachProd.getValue()}</fo:block></fo:table-cell>       		
				                     </#list>
				                     </fo:table-row>
				                  </#if>
				                  <#if productList.getKey()=="BYPROD">
			                       	<fo:table-row>
			                       	<fo:table-cell ><fo:block text-align="left" font-weight="bold" font-size="10pt" keep-together="always"></fo:block></fo:table-cell>
			                       	<fo:table-cell><fo:block text-align="right"  font-size="10pt" keep-together="always"></fo:block></fo:table-cell>
			                       	<#assign byProdList =productList.getValue().entrySet()>
			                       	 <#list byProdList as eachProd>
				                     <fo:table-cell border-style="dotted" ><fo:block text-align="right" font-weight="bold"  font-size="10pt" keep-together="always">${eachProd.getValue()}</fo:block></fo:table-cell>       		
				                     </#list>
				                     </fo:table-row>
				                   </#if>
				                  
				                   </#list>
				                   	<fo:table-row>
			                       	<fo:table-cell ><fo:block text-align="left"  font-size="10pt" keep-together="always">&#160;</fo:block></fo:table-cell>
			                       </fo:table-row>
				                      
			                         <#assign sno=sno+1>
			                      </#list>	
			                       <fo:table-row>
			                        	<fo:table-cell ><fo:block text-align="left"  font-size="10pt" keep-together="always">&#160;</fo:block></fo:table-cell>
			                       </fo:table-row>
			                       <#assign grandTotalList=(routeWiseGrandTotalMap.get(routeWiseEntries.getKey())).entrySet()>
			                        <#list grandTotalList as grandProductList>
			       			    <#if grandProductList.getKey()=="LMS">
			                       	<fo:table-row>
			                       	<fo:table-cell border-style="dotted" ><fo:block text-align="left" font-weight="bold" font-size="10pt" keep-together="always"></fo:block></fo:table-cell>
			                       	<fo:table-cell border-style="dotted"><fo:block text-align="left"  font-size="10pt" font-weight="bold" keep-together="always">GRAND TOTAL</fo:block></fo:table-cell>
			                       	<#assign lmsProdList = grandProductList.getValue().entrySet()>
			                       	 <#list lmsProdList as eachProd>
				                     <fo:table-cell border-style="dotted"><fo:block text-align="right" font-weight="bold" font-size="10pt" keep-together="always">${eachProd.getValue()}</fo:block></fo:table-cell>       		
				                     </#list>
				                     </fo:table-row>
				                  </#if>
				                  <#if grandProductList.getKey()=="BYPROD">
			                       	<fo:table-row>
			                       		<fo:table-cell ><fo:block text-align="left" font-weight="bold" font-size="10pt" keep-together="always"></fo:block></fo:table-cell>
			                       	<fo:table-cell><fo:block text-align="left"  font-size="10pt" keep-together="always"></fo:block></fo:table-cell>
			                       	<#assign byProdList =grandProductList.getValue().entrySet()>
			                       	 <#list byProdList as eachProd>
				                     <fo:table-cell border-style="dotted" ><fo:block text-align="right" font-weight="bold" font-size="10pt" keep-together="always">${eachProd.getValue()}</fo:block></fo:table-cell>       		
				                     </#list>
				                     </fo:table-row>
				                   </#if>
				                   </#list>
			                     
				                    </fo:table-body> 	
			        			 </fo:table>
			        		</fo:block>
						</fo:flow>
				 	</fo:page-sequence>
				</#list> 
			<#else>
				<fo:page-sequence master-reference="main">
    			<fo:flow flow-name="xsl-region-body" font-family="Helvetica">
       		 		<fo:block font-size="14pt">
            			${uiLabelMap.NoOrdersFound}.
       		 		</fo:block>
    			</fo:flow>
			</fo:page-sequence>
			</#if>
	</fo:root>
</#escape>
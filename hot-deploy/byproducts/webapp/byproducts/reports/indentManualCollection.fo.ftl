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
                      margin-top="0.2in" margin-bottom=".3in" margin-left=".3in" margin-right=".3in">
                <fo:region-body margin-top="1in"/>
                <fo:region-before extent="1in"/>
                <fo:region-after extent="1in"/>
            </fo:simple-page-master>
        </fo:layout-master-set>
         ${setRequestAttribute("indent_collection", "indentCollection.txt")} 
           <#assign routesList = routeCollectionMap.entrySet()>
         <#list routesList as routeBooths>      
        <fo:page-sequence master-reference="main">
        	<fo:static-content flow-name="xsl-region-before" font-family="Courier,monospace" >
        	<fo:block font-size="10pt">VST_ASCII-015</fo:block> 
        		<fo:block text-align="left" white-space-collapse="false">${uiLabelMap.CommonPage}:<fo:page-number/>                 </fo:block>
        		<fo:block text-align="center" keep-together="always" white-space-collapse="false">   KARNATAKA CO-OPERATIVE MILK PRODUCERS FEDERATION LTD.</fo:block>
			    <fo:block text-align="center" keep-together="always" white-space-collapse="false">          UNIT : MOTHER DAIRY:G.K.V.K POST : YELAHANKA:BANGALORE : 560065</fo:block>
        	</fo:static-content>
            <fo:flow flow-name="xsl-region-body"  font-size="7pt" font-family="Courier,monospace">
            <fo:block text-align="left" keep-together="always"  font-size="11pt" white-space-collapse="false">&#160;          RouteCode:${routeBooths.getKey()}                            DAILY INDENTS FOR __________________________________                 SupplyDate:<#if parameters.supplyDate?has_content>${parameters.supplyDate}<#else>${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(nowTimestamp, "MMM d, yyyy")}</#if></fo:block>
            	 <fo:block font-family="Courier,monospace" >                
                <fo:block font-size="7pt">|----------|-----------------|---------|-------|---------|---------|-------|---------|--------|--------|-------|-------|--------|--------|-------|-------|-------|-------|-------|-------|-------|-------|-------|-------|-------|-------|</fo:block> 
                <fo:table  border-style="dotted">
                    <fo:table-column column-width="45pt"/>
                    <fo:table-column column-width="75pt"/>  
                    <fo:table-column column-width="37pt"/>
                    <fo:table-column column-width="33pt"/>
                    <fo:table-column column-width="37pt"/> 
                     <fo:table-column column-width="37pt"/>
                    <fo:table-column column-width="30pt"/>
                    <fo:table-column column-width="37pt"/> 
                     <fo:table-column column-width="35pt"/>
                    <fo:table-column column-width="37pt"/>
                    <fo:table-column column-width="37pt"/> 
                     <fo:table-column column-width="35pt"/>
                    <fo:table-column column-width="35pt"/>
                    <fo:table-column column-width="35pt"/> 
                     <fo:table-column column-width="30pt"/>
                    <fo:table-column column-width="30pt"/>
                    <fo:table-column column-width="30pt"/> 
                     <fo:table-column column-width="30pt"/>
                    <fo:table-column column-width="30pt"/>
                    <fo:table-column column-width="28pt"/> 
                     <fo:table-column column-width="28pt"/>
                    <fo:table-column column-width="28pt"/>
                    <fo:table-column column-width="30pt"/> 
                     <fo:table-column column-width="30pt"/>
                    <fo:table-column column-width="30pt"/>
                    <fo:table-column column-width="30pt"/> 
                     <fo:table-column column-width="30pt"/>
		          	<fo:table-header  border-style="dotted">
		            			<fo:table-cell padding="3pt"  border-style="dotted"><fo:block text-align="left" >|CODE   </fo:block><fo:block text-align="left" >| </fo:block></fo:table-cell>		                    	                  
		            			<fo:table-cell padding="3pt"  border-style="dotted"><fo:block text-align="left">|    NAME   </fo:block><fo:block text-align="left" >| </fo:block></fo:table-cell>
		            			<fo:table-cell padding="3pt"  border-style="dotted">
		            			<fo:block text-align="left" >| TMS 1</fo:block>
		            			<fo:block text-align="left" >|  Ltr</fo:block>
		            			</fo:table-cell>
		            			<fo:table-cell padding="3pt"  border-style="dotted">
		            			<fo:block text-align="left" >|  TMS  </fo:block>
		            			<fo:block text-align="left" >|  500  </fo:block>
		            			</fo:table-cell>
		            			<fo:table-cell padding="3pt"  border-style="dotted">
		            			<fo:block text-align="left" >| TMS 6</fo:block>
		            			<fo:block text-align="left" >| Ltr </fo:block>
		            			</fo:table-cell>
		            			<fo:table-cell padding="3pt"  border-style="dotted">
		            			<fo:block text-align="left" >| SBM 1</fo:block>
		            			<fo:block text-align="left" >| Ltr</fo:block>
		            			</fo:table-cell>
		            			<fo:table-cell padding="3pt"  border-style="dotted">
		            			<fo:block text-align="left" >| SBM</fo:block>
		            			<fo:block text-align="left" >| 500</fo:block>
		            			</fo:table-cell>	
		            			<fo:table-cell padding="3pt"  border-style="dotted">
		            			<fo:block text-align="left" >|DTMS 1</fo:block>
		            			<fo:block text-align="left" >| Ltr</fo:block>
		            			</fo:table-cell>
		            			<fo:table-cell padding="3pt"  border-style="dotted">
		            			<fo:block text-align="left" >| DTMS</fo:block>
		            			<fo:block text-align="left" >| 500</fo:block>
		            			</fo:table-cell>
		            			<fo:table-cell padding="3pt"  border-style="dotted">
		            			<fo:block text-align="left" >| DTMS</fo:block>
		            			<fo:block text-align="left" >| 250</fo:block>
		            			</fo:table-cell>
		            			<fo:table-cell padding="3pt"  border-style="dotted">
		            			<fo:block text-align="left" >| HCM</fo:block>
		            			<fo:block text-align="left" >| 500</fo:block>
		            			</fo:table-cell>
		            			<fo:table-cell padding="3pt"  border-style="dotted">
		            			<fo:block text-align="left" >| HCM</fo:block>
		            			<fo:block text-align="left" >| 250</fo:block>
		            			</fo:table-cell>
		            			<fo:table-cell padding="3pt"  border-style="dotted">
		            			<fo:block text-align="left" >| FCSM</fo:block>
		            			<fo:block text-align="left" >| 500</fo:block>
		            			</fo:table-cell>
		            			<fo:table-cell padding="3pt"  border-style="dotted">
		            			<fo:block text-align="left" >| NSM</fo:block>
		            			<fo:block text-align="left" >| 1000</fo:block>
		            			</fo:table-cell>
		            			<fo:table-cell padding="3pt"  border-style="dotted">
		            			<fo:block text-align="left" >| NSM</fo:block>
		            			<fo:block text-align="left" >| 500</fo:block>
		            			</fo:table-cell>
		            			<fo:table-cell padding="3pt"  border-style="dotted">
		            			<fo:block text-align="left" >| NSM</fo:block>
		            			<fo:block text-align="left" >| 250</fo:block>
		            			</fo:table-cell>
		            			<fo:table-cell padding="3pt"  border-style="dotted">
		            			<fo:block text-align="left" >| CRD</fo:block>
		            			<fo:block text-align="left" >|  2</fo:block>
		            			</fo:table-cell>
		            			<fo:table-cell padding="3pt"  border-style="dotted">
		            			<fo:block text-align="left" >| CRD</fo:block>
		            			<fo:block text-align="left" >| 5</fo:block>
		            			</fo:table-cell>
		            			<fo:table-cell padding="3pt"  border-style="dotted">
		            			<fo:block text-align="left" >| MBM</fo:block>
		            			<fo:block text-align="left" >| 200</fo:block>
		            			</fo:table-cell>
		            			<fo:table-cell padding="3pt"  border-style="dotted">
		            			<fo:block text-align="left" >| SL</fo:block>
		            			<fo:block text-align="left" >| 200</fo:block>
		            			</fo:table-cell>
		            			<fo:table-cell padding="3pt"  border-style="dotted">
		            			<fo:block text-align="left" >| SLM</fo:block>
		            			<fo:block text-align="left" >| 200</fo:block>
		            			</fo:table-cell>
		            			<fo:table-cell padding="3pt"  border-style="dotted">
		            			<fo:block text-align="left" >|G2SP</fo:block>
		            			<fo:block text-align="left" >|200</fo:block>
		            			</fo:table-cell>
		            			<fo:table-cell padding="3pt"  border-style="dotted">
		            			<fo:block text-align="left" >|G5ML</fo:block>
		            			<fo:block text-align="left" >| </fo:block>
		            			</fo:table-cell>
		            			<fo:table-cell padding="3pt"  border-style="dotted">
		            			<fo:block text-align="left" >|SLIM</fo:block>
		            			<fo:block text-align="left" >| </fo:block>
		            			</fo:table-cell>
		            			<fo:table-cell padding="3pt"  border-style="dotted">
		            			<fo:block text-align="left" >| GL</fo:block>
		            			<fo:block text-align="left" >| 200</fo:block>
		            			</fo:table-cell>
		            			<fo:table-cell padding="3pt"  border-style="dotted">
		            			<fo:block text-align="left" >| GL</fo:block>
		            			<fo:block text-align="left" >| 500</fo:block>
		            			</fo:table-cell>
		            			<fo:table-cell padding="3pt"  border-style="dotted">
		            			<fo:block text-align="left" >|</fo:block>
		            			<fo:block text-align="left" >|</fo:block>
		            			</fo:table-cell>
		            			
		            			<#-- 
		            			TMS 1 L	TMS 500	TMS 6 Ltr	SBM 1 Ltr	SBM 500	DTMS 1 Itr	DTMS 500	DTMS 250	HCM 500	HCM 250	FCSM 500	NSM 1000	NSM 500	NSM 250	CRD 2	CRD 5	MBM 200	SL 200	SLM 200	G2SP	G5ML	SLIM	GL 200	GL 500
		            				 -->                   	                  		            
				    </fo:table-header>		
				   <#assign boothList= routeBooths.getValue()>        
                    <fo:table-body>                    	
	                        <fo:table-row >
	                        	<fo:table-cell  >	
	                            	    <fo:block font-size="7pt">|----------|-----------------|---------|-------|---------|---------|-------|---------|--------|--------|-------|-------|--------|--------|-------|-------|-------|-------|-------|-------|-------|-------|-------|-------|-------|-------|</fo:block>                              
	                            </fo:table-cell>	
	                        </fo:table-row>
	                        </fo:table-body>
                </fo:table>
                 <fo:table>
                  <fo:table-column column-width="45pt"/>
                    <fo:table-column column-width="75pt"/>  
                    <fo:table-column column-width="37pt"/>
                    <fo:table-column column-width="37pt"/>
                    <fo:table-column column-width="42pt"/> 
                     <fo:table-column column-width="42pt"/>
                    <fo:table-column column-width="33pt"/>
                    <fo:table-column column-width="42pt"/> 
                     <fo:table-column column-width="40pt"/>
                    <fo:table-column column-width="37pt"/>
                    <fo:table-column column-width="33pt"/> 
                     <fo:table-column column-width="35pt"/>
                    <fo:table-column column-width="35pt"/>
                    <fo:table-column column-width="38pt"/> 
                     <fo:table-column column-width="35pt"/>
                    <fo:table-column column-width="33pt"/>
                    <fo:table-column column-width="33pt"/> 
                     <fo:table-column column-width="33pt"/>
                    <fo:table-column column-width="35pt"/>
                    <fo:table-column column-width="33pt"/> 
                     <fo:table-column column-width="33pt"/>
                    <fo:table-column column-width="35pt"/>
                    <fo:table-column column-width="33pt"/> 
                     <fo:table-column column-width="33pt"/>
                    <fo:table-column column-width="33pt"/>
                    <fo:table-column column-width="35pt"/> 
                     <fo:table-column column-width="33pt"/>
                     <fo:table-column column-width="33pt"/>  
                 <fo:table-body>
                    <#list  boothList as booth>
                    <fo:table-row>
                    <fo:table-cell border-style="dotted" ><fo:block text-align="left" >| ${booth.get("code")}</fo:block><fo:block text-align="left" >| </fo:block></fo:table-cell>		                    	                  
		            			<fo:table-cell  border-style="dotted"  ><fo:block text-align="left" >|${Static["org.ofbiz.order.order.OrderServices"].nameTrim((StringUtil.wrapString(booth.get("name")?if_exists)),20)}</fo:block><fo:block text-align="left" >| </fo:block>
		            			</fo:table-cell>
		            			<fo:table-cell padding="3pt"  border-style="dotted">
		            			<fo:block text-align="left" >|&#160;</fo:block>
		            			<fo:block text-align="left" >|&#160;</fo:block>
		            			</fo:table-cell>
		            			<fo:table-cell padding="3pt"  border-style="dotted">
		            			<fo:block text-align="left" >&#160;|&#160;</fo:block>
		            			<fo:block text-align="left" >&#160;|&#160;</fo:block>
		            			</fo:table-cell>
		            			<fo:table-cell padding="3pt"  border-style="dotted">
		            			<fo:block text-align="left" >| </fo:block>
		            			<fo:block text-align="left" >| </fo:block>
		            			</fo:table-cell>
		            			<fo:table-cell padding="3pt"  border-style="dotted">
		            			<fo:block text-align="left" >|</fo:block>
		            			<fo:block text-align="left" >|</fo:block>
		            			</fo:table-cell>
		            			<fo:table-cell padding="3pt"  border-style="dotted">
		            			<fo:block text-align="left" >|</fo:block>
		            			<fo:block text-align="left" >|</fo:block>
		            			</fo:table-cell>	
		            			<fo:table-cell padding="3pt"  border-style="dotted">
		            			<fo:block text-align="left" >|</fo:block>
		            			<fo:block text-align="left" >|</fo:block>
		            			</fo:table-cell>
		            			<fo:table-cell padding="3pt"  border-style="dotted">
		            			<fo:block text-align="left" >|</fo:block>
		            			<fo:block text-align="left" >|</fo:block>
		            			</fo:table-cell>
		            			<fo:table-cell padding="3pt"  border-style="dotted">
		            			<fo:block text-align="left" >|</fo:block>
		            			<fo:block text-align="left" >|</fo:block>
		            			</fo:table-cell>
		            			<fo:table-cell padding="3pt"  border-style="dotted">
		            			<fo:block text-align="left" >|</fo:block>
		            			<fo:block text-align="left" >|</fo:block>
		            			</fo:table-cell>
		            			<fo:table-cell padding="3pt"  border-style="dotted">
		            			<fo:block text-align="left" >|</fo:block>
		            			<fo:block text-align="left" >|</fo:block>
		            			</fo:table-cell>
		            			<fo:table-cell padding="3pt"  border-style="dotted">
		            			<fo:block text-align="left" >|</fo:block>
		            			<fo:block text-align="left" >|</fo:block>
		            			</fo:table-cell>
		            			<fo:table-cell padding="3pt"  border-style="dotted">
		            			<fo:block text-align="left" >|</fo:block>
		            			<fo:block text-align="left" >|</fo:block>
		            			</fo:table-cell>
		            			<fo:table-cell padding="3pt"  border-style="dotted">
		            			<fo:block text-align="left" >|</fo:block>
		            			<fo:block text-align="left" >|</fo:block>
		            			</fo:table-cell>
		            			<fo:table-cell padding="3pt"  border-style="dotted">
		            			<fo:block text-align="left" >|</fo:block>
		            			<fo:block text-align="left" >|</fo:block>
		            			</fo:table-cell>
		            			<fo:table-cell padding="3pt"  border-style="dotted">
		            			<fo:block text-align="left" >|</fo:block>
		            			<fo:block text-align="left" >|</fo:block>
		            			</fo:table-cell>
		            			<fo:table-cell padding="3pt"  border-style="dotted">
		            			<fo:block text-align="left" >|</fo:block>
		            			<fo:block text-align="left" >|</fo:block>
		            			</fo:table-cell>
		            			<fo:table-cell padding="3pt"  border-style="dotted">
		            			<fo:block text-align="left" >|</fo:block>
		            			<fo:block text-align="left" >|</fo:block>
		            			</fo:table-cell>
		            			<fo:table-cell padding="3pt"  border-style="dotted">
		            			<fo:block text-align="left" >|</fo:block>
		            			<fo:block text-align="left" >|</fo:block>
		            			</fo:table-cell>
		            			<fo:table-cell padding="3pt"  border-style="dotted">
		            			<fo:block text-align="left" >|</fo:block>
		            			<fo:block text-align="left" >|</fo:block>
		            			</fo:table-cell>
		            			<fo:table-cell padding="3pt"  border-style="dotted">
		            			<fo:block text-align="left" >|</fo:block>
		            			<fo:block text-align="left" >|</fo:block>
		            			</fo:table-cell>
		            			<fo:table-cell padding="3pt"  border-style="dotted">
		            			<fo:block text-align="left" >|</fo:block>
		            			<fo:block text-align="left" >|</fo:block>
		            			</fo:table-cell>
		            			<fo:table-cell padding="3pt"  border-style="dotted">
		            			<fo:block text-align="left" >|</fo:block>
		            			<fo:block text-align="left" >|</fo:block>
		            			</fo:table-cell>
		            			<fo:table-cell padding="3pt"  border-style="dotted">
		            			<fo:block text-align="left" >|</fo:block>
		            			<fo:block text-align="left" >|</fo:block>
		            			</fo:table-cell>
		            			<fo:table-cell padding="3pt"  border-style="dotted">
		            			<fo:block text-align="left" >|</fo:block>
		            			<fo:block text-align="left" >|</fo:block>
		            			</fo:table-cell>
		            			<fo:table-cell padding="3pt"  border-style="dotted">
		            			<fo:block text-align="left" >|</fo:block>
		            			<fo:block text-align="left" >|</fo:block>
		            			</fo:table-cell>
		            			
                    </fo:table-row>
                    <fo:table-row>
                    <fo:table-cell>	
                        	   <fo:block font-size="7pt">|----------|-----------------|---------|-------|---------|---------|-------|---------|--------|--------|-------|-------|--------|--------|-------|-------|-------|-------|-------|-------|-------|-------|-------|-------|-------|-------|</fo:block>                               
	                </fo:table-cell>
                    </fo:table-row>
                    </#list>
                    </fo:table-body>
                </fo:table>
                 </fo:block>
           </fo:flow>
        </fo:page-sequence>
         </#list>
     </fo:root>
</#escape>
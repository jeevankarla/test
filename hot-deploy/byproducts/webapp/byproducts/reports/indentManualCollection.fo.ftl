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
            <fo:simple-page-master master-name="main" page-height="12in" page-width="16in"
                      margin-right=".2in" margin-left=".2in" >
                <fo:region-body margin-top="1in"/>
                <fo:region-before extent="1in"/>
                <fo:region-after extent="1in"/>
            </fo:simple-page-master>
        </fo:layout-master-set>
         ${setRequestAttribute("indent_collection", "indentCollection.txt")}       
        <fo:page-sequence master-reference="main">
        	<fo:static-content flow-name="xsl-region-before">
        		<fo:block text-align="left" white-space-collapse="false">${uiLabelMap.CommonPage}:<fo:page-number/>                 </fo:block>
        		<fo:block text-align="center" keep-together="always" white-space-collapse="false">   KARNATAKA CO-OPERATIVE MILK PRODUCERS FEDERATION LTD.</fo:block>
			    <fo:block text-align="center" keep-together="always" white-space-collapse="false">          UNIT : MOTHER DAIRY:G.K.V.K POST : YELAHANKA:BANGALORE : 560065</fo:block>
        	</fo:static-content>
            <fo:flow flow-name="xsl-region-body"  font-size="10pt" font-family="Helvetica">
            <fo:block text-align="left" keep-together="always"  font-size="11pt" white-space-collapse="false">&#160;          RouteCode:${parameters.routeId}                                 DAILY INDENTS FOR __________________________________                                        SupplyDate: ${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(nowTimestamp, "MMM d, yyyy")}</fo:block>
             <fo:block text-align="left" keep-together="always" white-space-collapse="false">&#160;</fo:block>
            	 <fo:block font-family="Courier,monospace" >                
                <fo:table  border-style="dotted">
                    <fo:table-column column-width="50pt"/>
                    <fo:table-column column-width="70pt"/>  
                    <fo:table-column column-width="40pt"/>
                    <fo:table-column column-width="40pt"/>
                    <fo:table-column column-width="40pt"/> 
                     <fo:table-column column-width="40pt"/>
                    <fo:table-column column-width="40pt"/>
                    <fo:table-column column-width="40pt"/> 
                     <fo:table-column column-width="40pt"/>
                    <fo:table-column column-width="40pt"/>
                    <fo:table-column column-width="40pt"/> 
                     <fo:table-column column-width="40pt"/>
                    <fo:table-column column-width="40pt"/>
                    <fo:table-column column-width="40pt"/> 
                     <fo:table-column column-width="40pt"/>
                    <fo:table-column column-width="40pt"/>
                    <fo:table-column column-width="40pt"/> 
                     <fo:table-column column-width="40pt"/>
                    <fo:table-column column-width="40pt"/>
                    <fo:table-column column-width="40pt"/> 
                     <fo:table-column column-width="40pt"/>
                    <fo:table-column column-width="40pt"/>
                    <fo:table-column column-width="40pt"/> 
                     <fo:table-column column-width="40pt"/>
                    <fo:table-column column-width="40pt"/>
                    <fo:table-column column-width="40pt"/> 
                     <fo:table-column column-width="40pt"/>
                    <fo:table-column column-width="40pt"/>
                    <fo:table-column column-width="40pt"/> 
                     <fo:table-column column-width="40pt"/>
                    <fo:table-column column-width="40pt"/>
                    <fo:table-column column-width="40pt"/>    
                     <fo:table-column column-width="40pt"/>
                    <fo:table-column column-width="40pt"/>      
                     <fo:table-column column-width="40pt"/>
                    <fo:table-column column-width="40pt"/>                     
                           
		          	<fo:table-header  border-style="dotted">
		            			<fo:table-cell padding="3pt"  border-style="dotted"><fo:block text-align="center" >CODE</fo:block></fo:table-cell>		                    	                  
		            			<fo:table-cell padding="3pt"  border-style="dotted"><fo:block text-align="center" >NAME</fo:block></fo:table-cell>
		            			
		            			<fo:table-cell padding="3pt"  border-style="dotted">
		            			<fo:block text-align="center" >TMS 1</fo:block>
		            			<fo:block text-align="center" >Ltr</fo:block>
		            			</fo:table-cell>
		            			<fo:table-cell padding="3pt"  border-style="dotted">
		            			<fo:block text-align="center" >TMS </fo:block>
		            			<fo:block text-align="center" >500</fo:block>
		            			</fo:table-cell>
		            			<fo:table-cell padding="3pt"  border-style="dotted">
		            			<fo:block text-align="center" >TMS 6</fo:block>
		            			<fo:block text-align="center" >Ltr</fo:block>
		            			</fo:table-cell>
		            			<fo:table-cell padding="3pt"  border-style="dotted">
		            			<fo:block text-align="center" >SBM 1</fo:block>
		            			<fo:block text-align="center" >Ltr</fo:block>
		            			</fo:table-cell>
		            			<fo:table-cell padding="3pt"  border-style="dotted">
		            			<fo:block text-align="center" >SBM</fo:block>
		            			<fo:block text-align="center" >500</fo:block>
		            			</fo:table-cell>	
		            			<fo:table-cell padding="3pt"  border-style="dotted">
		            			<fo:block text-align="center" >DTMS 1</fo:block>
		            			<fo:block text-align="center" >Ltr</fo:block>
		            			</fo:table-cell>
		            			<fo:table-cell padding="3pt"  border-style="dotted">
		            			<fo:block text-align="center" >DTMS</fo:block>
		            			<fo:block text-align="center" >500</fo:block>
		            			</fo:table-cell>
		            			<fo:table-cell padding="3pt"  border-style="dotted">
		            			<fo:block text-align="center" >DTMS</fo:block>
		            			<fo:block text-align="center" >250</fo:block>
		            			</fo:table-cell>
		            			<fo:table-cell padding="3pt"  border-style="dotted">
		            			<fo:block text-align="center" >HCM</fo:block>
		            			<fo:block text-align="center" >500</fo:block>
		            			</fo:table-cell>
		            			<fo:table-cell padding="3pt"  border-style="dotted">
		            			<fo:block text-align="center" >HCM</fo:block>
		            			<fo:block text-align="center" >250</fo:block>
		            			</fo:table-cell>
		            			<fo:table-cell padding="3pt"  border-style="dotted">
		            			<fo:block text-align="center" >FCSM</fo:block>
		            			<fo:block text-align="center" >500</fo:block>
		            			</fo:table-cell>
		            			<fo:table-cell padding="3pt"  border-style="dotted">
		            			<fo:block text-align="center" >NSM</fo:block>
		            			<fo:block text-align="center" >1000</fo:block>
		            			</fo:table-cell>
		            			<fo:table-cell padding="3pt"  border-style="dotted">
		            			<fo:block text-align="center" >NSM</fo:block>
		            			<fo:block text-align="center" >500</fo:block>
		            			</fo:table-cell>
		            			<fo:table-cell padding="3pt"  border-style="dotted">
		            			<fo:block text-align="center" >NSM</fo:block>
		            			<fo:block text-align="center" >500</fo:block>
		            			</fo:table-cell>
		            			<fo:table-cell padding="3pt"  border-style="dotted">
		            			<fo:block text-align="center" >CRD 2</fo:block>
		            			<fo:block text-align="center" ></fo:block>
		            			</fo:table-cell>
		            			<fo:table-cell padding="3pt"  border-style="dotted">
		            			<fo:block text-align="center" >CRD 5</fo:block>
		            			<fo:block text-align="center" ></fo:block>
		            			</fo:table-cell>
		            			<fo:table-cell padding="3pt"  border-style="dotted">
		            			<fo:block text-align="center" >MBM</fo:block>
		            			<fo:block text-align="center" >200</fo:block>
		            			</fo:table-cell>
		            			<fo:table-cell padding="3pt"  border-style="dotted">
		            			<fo:block text-align="center" >MBM</fo:block>
		            			<fo:block text-align="center" >200</fo:block>
		            			</fo:table-cell>
		            			<fo:table-cell padding="3pt"  border-style="dotted">
		            			<fo:block text-align="center" >SL</fo:block>
		            			<fo:block text-align="center" >200</fo:block>
		            			</fo:table-cell>
		            			<fo:table-cell padding="3pt"  border-style="dotted">
		            			<fo:block text-align="center" >SLM</fo:block>
		            			<fo:block text-align="center" >200</fo:block>
		            			</fo:table-cell>
		            			<fo:table-cell padding="3pt"  border-style="dotted">
		            			<fo:block text-align="center" >G2SP</fo:block>
		            			<fo:block text-align="center" >200</fo:block>
		            			</fo:table-cell>
		            			<fo:table-cell padding="3pt"  border-style="dotted">
		            			<fo:block text-align="center" >G5ML</fo:block>
		            			<fo:block text-align="center" ></fo:block>
		            			</fo:table-cell>
		            			<fo:table-cell padding="3pt"  border-style="dotted">
		            			<fo:block text-align="center" >SLIM</fo:block>
		            			<fo:block text-align="center" ></fo:block>
		            			</fo:table-cell>
		            			<fo:table-cell padding="3pt"  border-style="dotted">
		            			<fo:block text-align="center" >GL</fo:block>
		            			<fo:block text-align="center" >200</fo:block>
		            			</fo:table-cell>
		            			<fo:table-cell padding="3pt"  border-style="dotted">
		            			<fo:block text-align="center" >GL</fo:block>
		            			<fo:block text-align="center" >500</fo:block>
		            			</fo:table-cell>
		            			
		            			<#-- 
		            			TMS 1 L	TMS 500	TMS 6 Ltr	SBM 1 Ltr	SBM 500	DTMS 1 Itr	DTMS 500	DTMS 250	HCM 500	HCM 250	FCSM 500	NSM 1000	NSM 500	NSM 250	CRD 2	CRD 5	MBM 200	SL 200	SLM 200	G2SP	G5ML	SLIM	GL 200	GL 500
		            				 -->                   	                  		            
				    </fo:table-header>		
				   <#assign boothList= routeCollectionMap.get(parameters.routeId?if_exists)>        
                    <fo:table-body>                    	
	                        <fo:table-row >
	                        	<fo:table-cell  >	
	                            	<fo:block text-align="left" keep-together="always" white-space-collapse="false">&#160;</fo:block>                               
	                            </fo:table-cell>	
	                        </fo:table-row>
                    <#list  boothList as booth>
                    <fo:table-row>
                    <fo:table-cell padding="3pt" border-style="dotted" ><fo:block text-align="left" >${booth.get("code")}</fo:block></fo:table-cell>		                    	                  
		            			<fo:table-cell  border-style="dotted"  ><fo:block text-align="left" >${booth.get("name")}</fo:block></fo:table-cell>
		            			<fo:table-cell padding="3pt"  border-style="dotted">
		            			<fo:block text-align="center" ></fo:block>
		            			<fo:block text-align="center" ></fo:block>
		            			</fo:table-cell>
		            			<fo:table-cell padding="3pt"  border-style="dotted">
		            			<fo:block text-align="center" > </fo:block>
		            			<fo:block text-align="center" ></fo:block>
		            			</fo:table-cell>
		            			<fo:table-cell padding="3pt"  border-style="dotted">
		            			<fo:block text-align="center" ></fo:block>
		            			<fo:block text-align="center" ></fo:block>
		            			</fo:table-cell>
		            			<fo:table-cell padding="3pt"  border-style="dotted">
		            			<fo:block text-align="center" ></fo:block>
		            			<fo:block text-align="center" ></fo:block>
		            			</fo:table-cell>
		            			<fo:table-cell padding="3pt"  border-style="dotted">
		            			<fo:block text-align="center" ></fo:block>
		            			<fo:block text-align="center" ></fo:block>
		            			</fo:table-cell>	
		            			<fo:table-cell padding="3pt"  border-style="dotted">
		            			<fo:block text-align="center" ></fo:block>
		            			<fo:block text-align="center" ></fo:block>
		            			</fo:table-cell>
		            			<fo:table-cell padding="3pt"  border-style="dotted">
		            			<fo:block text-align="center" ></fo:block>
		            			<fo:block text-align="center" ></fo:block>
		            			</fo:table-cell>
		            			<fo:table-cell padding="3pt"  border-style="dotted">
		            			<fo:block text-align="center" ></fo:block>
		            			<fo:block text-align="center" ></fo:block>
		            			</fo:table-cell>
		            			<fo:table-cell padding="3pt"  border-style="dotted">
		            			<fo:block text-align="center" ></fo:block>
		            			<fo:block text-align="center" ></fo:block>
		            			</fo:table-cell>
		            			<fo:table-cell padding="3pt"  border-style="dotted">
		            			<fo:block text-align="center" ></fo:block>
		            			<fo:block text-align="center" ></fo:block>
		            			</fo:table-cell>
		            			<fo:table-cell padding="3pt"  border-style="dotted">
		            			<fo:block text-align="center" ></fo:block>
		            			<fo:block text-align="center" ></fo:block>
		            			</fo:table-cell>
		            			<fo:table-cell padding="3pt"  border-style="dotted">
		            			<fo:block text-align="center" ></fo:block>
		            			<fo:block text-align="center" ></fo:block>
		            			</fo:table-cell>
		            			<fo:table-cell padding="3pt"  border-style="dotted">
		            			<fo:block text-align="center" ></fo:block>
		            			<fo:block text-align="center" ></fo:block>
		            			</fo:table-cell>
		            			<fo:table-cell padding="3pt"  border-style="dotted">
		            			<fo:block text-align="center" ></fo:block>
		            			<fo:block text-align="center" ></fo:block>
		            			</fo:table-cell>
		            			<fo:table-cell padding="3pt"  border-style="dotted">
		            			<fo:block text-align="center" ></fo:block>
		            			<fo:block text-align="center" ></fo:block>
		            			</fo:table-cell>
		            			<fo:table-cell padding="3pt"  border-style="dotted">
		            			<fo:block text-align="center" ></fo:block>
		            			<fo:block text-align="center" ></fo:block>
		            			</fo:table-cell>
		            			<fo:table-cell padding="3pt"  border-style="dotted">
		            			<fo:block text-align="center" ></fo:block>
		            			<fo:block text-align="center" ></fo:block>
		            			</fo:table-cell>
		            			<fo:table-cell padding="3pt"  border-style="dotted">
		            			<fo:block text-align="center" ></fo:block>
		            			<fo:block text-align="center" ></fo:block>
		            			</fo:table-cell>
		            			<fo:table-cell padding="3pt"  border-style="dotted">
		            			<fo:block text-align="center" ></fo:block>
		            			<fo:block text-align="center" ></fo:block>
		            			</fo:table-cell>
		            			<fo:table-cell padding="3pt"  border-style="dotted">
		            			<fo:block text-align="center" ></fo:block>
		            			<fo:block text-align="center" ></fo:block>
		            			</fo:table-cell>
		            			<fo:table-cell padding="3pt"  border-style="dotted">
		            			<fo:block text-align="center" ></fo:block>
		            			<fo:block text-align="center" ></fo:block>
		            			</fo:table-cell>
		            			<fo:table-cell padding="3pt"  border-style="dotted">
		            			<fo:block text-align="center" ></fo:block>
		            			<fo:block text-align="center" ></fo:block>
		            			</fo:table-cell>
		            			<fo:table-cell padding="3pt"  border-style="dotted">
		            			<fo:block text-align="center" ></fo:block>
		            			<fo:block text-align="center" ></fo:block>
		            			</fo:table-cell>
		            			<fo:table-cell padding="3pt"  border-style="dotted">
		            			<fo:block text-align="center" ></fo:block>
		            			<fo:block text-align="center" ></fo:block>
		            			</fo:table-cell>
		            			<fo:table-cell padding="3pt"  border-style="dotted">
		            			<fo:block text-align="center" ></fo:block>
		            			</fo:table-cell>
                    </fo:table-row>
                    </#list>
                    </fo:table-body>
                </fo:table>
                 </fo:block>
           </fo:flow>
        </fo:page-sequence>
     </fo:root>
</#escape>
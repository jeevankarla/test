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
            <fo:simple-page-master master-name="main" page-height="12in" page-width="8in"  margin-left=".3in" margin-right=".3in" margin-top=".8in" margin-bottom="0.5in">
                <fo:region-body margin-top="1in"/>
                <fo:region-before extent="1in"/>
                <fo:region-after extent="1in"/>
            </fo:simple-page-master>
        </fo:layout-master-set>
      <#if categoryTotalMap?has_content> 	    
       ${setRequestAttribute("OUTPUT_FILENAME", "PCMRetailerWorkingNotes.pdf")} 
        <fo:page-sequence master-reference="main" force-page-count="no-force" font-family="Courier,monospace">		
        <fo:static-content flow-name="xsl-region-before">
        		<fo:block text-align="center" white-space-collapse="false" font-size="12pt" font-weight="bold" keep-together="always">KARNATAKA CO-OPERATIVE MILK PRODUCERS FEDERATION LTD. </fo:block>
        		<fo:block text-align="center" white-space-collapse="false" font-size="12pt" font-weight="bold" keep-together="always">UNIT : MOTHER DAIRY  : G.K.V.K POST : YELAHANKA : BANGALORE - 560065</fo:block>
        		<fo:block text-align="center" white-space-collapse="false" font-size="12pt" font-weight="bold" keep-together="always"> STATEMENT SHOWING DETAILS OF NO. OF  SACHET AGENTS IN SACHET ROUTES BETWEEN </fo:block>
        		<fo:block text-align="center" white-space-collapse="false" font-size="12pt" font-weight="bold" keep-together="always"> ${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(cMonthStart, "dd-MMM-yyyy")}   TO  :  ${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(cMonthEnd, "dd-MMM-yyyy")}</fo:block>
        		<fo:block text-align="left"  keep-together="always"  font-family="Courier,monospace" font-weight="bold" white-space-collapse="false"> UserLogin:<#if userLogin?exists>${userLogin.userLoginId?if_exists}</#if>               &#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;Print Date :${printDate?if_exists}</fo:block>
        	</fo:static-content>	        	
        	   <fo:flow flow-name="xsl-region-body"  font-family="Courier,monospace">	
            	<fo:block text-align="left"  keep-together="always"  white-space-collapse="false">===========================================================================</fo:block> 
        		<fo:block text-align="left"  keep-together="always"  font-family="Courier,monospace" font-weight="bold" white-space-collapse="false">AVERAGE SALES / DAY      					     NO OF AGENTS  </fo:block> 
        		<fo:block text-align="left"  keep-together="always"   white-space-collapse="false">===========================================================================</fo:block> 
            	<fo:block>
                 	<fo:table>
                    <fo:table-column column-width="200pt"/>
                    <fo:table-column column-width="100pt"/> 
               	    <fo:table-column column-width="75pt"/>
            		<fo:table-column column-width="120pt"/> 	
            		<fo:table-column column-width="110pt"/>	
                    <fo:table-body>
                   			<fo:table-row>
                   			<#assign currentMonthCatLst=curntCatAbsMap.entrySet()>	
                   			<#assign less100count=0>
		                    <#list currentMonthCatLst as cMonthVal>
		                    <#assign cMonthValMap=cMonthVal.getValue()>
		                    <#assign less100cnt=cMonthValMap.get("less100")>
		                    <#assign less100count=less100count+less100cnt>
							</#list>
							<fo:table-cell>
	                           		<fo:block  keep-together="always" font-size="12pt" text-align="left" white-space-collapse="false">ABOVE 0 - BELOW 100</fo:block>  
	                       	</fo:table-cell>
							<fo:table-cell>
	                           		<fo:block  keep-together="always" font-size="12pt" text-align="right" white-space-collapse="false">${less100count}</fo:block>  
	                       	</fo:table-cell>
						</fo:table-row>
						   <fo:table-row>
                   			<#assign currentMonthCatLst=curntCatAbsMap.entrySet()>	
                   			<#assign bwt101To250count=0>
		                    <#list currentMonthCatLst as cMonthVal>
		                    <#assign cMonthValMap=cMonthVal.getValue()>
		                    <#assign bwt101To250cnt=cMonthValMap.get("bwt101To250")>
		                    <#assign bwt101To250count=bwt101To250count+bwt101To250cnt>
							</#list>
							<fo:table-cell>
	                           		<fo:block  keep-together="always" font-size="12pt" text-align="left" white-space-collapse="false">ABOVE 100 - BELOW 250 </fo:block>  
	                       	</fo:table-cell>
							<fo:table-cell>
	                           		<fo:block  keep-together="always" font-size="12pt" text-align="right" white-space-collapse="false">${bwt101To250count}</fo:block>  
	                       	</fo:table-cell>
						</fo:table-row>
					    <fo:table-row>
                   			<#assign currentMonthCatLst=curntCatAbsMap.entrySet()>	
                   			<#assign bwt251To500count=0>
		                    <#list currentMonthCatLst as cMonthVal>
		                    <#assign cMonthValMap=cMonthVal.getValue()>
		                    <#assign bwt251To500cnt=cMonthValMap.get("bwt251To500")>
		                    <#assign bwt251To500count=bwt251To500count+bwt251To500cnt>
							</#list>
							<fo:table-cell>
	                           		<fo:block  keep-together="always" font-size="12pt" text-align="left" white-space-collapse="false">ABOVE 251 - BELOW 500 </fo:block>  
	                       	</fo:table-cell>
							<fo:table-cell>
	                           		<fo:block  keep-together="always" font-size="12pt" text-align="right" white-space-collapse="false">${bwt251To500count}</fo:block>  
	                       	</fo:table-cell>
						</fo:table-row>
						<fo:table-row>
                   			<#assign currentMonthCatLst=curntCatAbsMap.entrySet()>	
                   			<#assign bwt501To750count=0>
		                    <#list currentMonthCatLst as cMonthVal>
		                    <#assign cMonthValMap=cMonthVal.getValue()>
		                    <#assign bwt501To750cnt=cMonthValMap.get("bwt501To750")>
		                    <#assign bwt501To750count=bwt501To750count+bwt501To750cnt>
							</#list>
							<fo:table-cell>
	                           		<fo:block  keep-together="always" font-size="12pt" text-align="left" white-space-collapse="false">ABOVE 501 - BELOW 750</fo:block>  
	                       	</fo:table-cell>
							<fo:table-cell>
	                           		<fo:block  keep-together="always" font-size="12pt" text-align="right" white-space-collapse="false">${bwt501To750count}</fo:block>  
	                       	</fo:table-cell>
						</fo:table-row>
						<fo:table-row>
                   			<#assign currentMonthCatLst=curntCatAbsMap.entrySet()>	
                   			<#assign bwt751To1000count=0>
		                    <#list currentMonthCatLst as cMonthVal>
		                    <#assign cMonthValMap=cMonthVal.getValue()>
		                    <#assign bwt751To1000cnt=cMonthValMap.get("bwt751To1000")>
		                    <#assign bwt751To1000count=bwt751To1000count+bwt751To1000cnt>
							</#list>
							<fo:table-cell>
	                           		<fo:block  keep-together="always" font-size="12pt" text-align="left" white-space-collapse="false">ABOVE 750 - BELOW 1000 </fo:block>  
	                       	</fo:table-cell>
							<fo:table-cell>
	                           		<fo:block  keep-together="always" font-size="12pt" text-align="right" white-space-collapse="false">${bwt751To1000count}</fo:block>  
	                       	</fo:table-cell>
						</fo:table-row>
						<fo:table-row>
                   			<#assign currentMonthCatLst=curntCatAbsMap.entrySet()>	
                   			<#assign above1000count=0>
		                    <#list currentMonthCatLst as cMonthVal>
		                    <#assign cMonthValMap=cMonthVal.getValue()>
		                    <#assign above1000cnt=cMonthValMap.get("above1000")>
		                    <#assign above1000count=above1000count+above1000cnt>
							</#list>
							<fo:table-cell>
	                           		<fo:block  keep-together="always" font-size="12pt" text-align="left" white-space-collapse="false">ABOVE 1000 </fo:block>  
	                       	</fo:table-cell>
							<fo:table-cell>
	                           		<fo:block  keep-together="always" font-size="12pt" text-align="right" white-space-collapse="false">${above1000count}</fo:block>  
	                       	</fo:table-cell>
						</fo:table-row>
				        <fo:table-row>
						<fo:table-cell>
		            		<fo:block  keep-together="always">---------------------------------------------------------------------------</fo:block>  
		            	</fo:table-cell>
				        </fo:table-row>
						<fo:table-row>	
				            	<fo:table-cell>
				            		<fo:block linefeed-treatment="preserve">&#xA;</fo:block> 
				            	</fo:table-cell>
				        </fo:table-row>
						<fo:table-row>	
				            	<fo:table-cell>
				            		<fo:block linefeed-treatment="preserve">&#xA;</fo:block> 
				            	</fo:table-cell>
				        </fo:table-row>
				        <fo:table-row>	
				            	<fo:table-cell>
				            		<fo:block linefeed-treatment="preserve">&#xA;</fo:block> 
				            	</fo:table-cell>
				        </fo:table-row>
						<fo:table-row>	
				            	<fo:table-cell>
				            		<fo:block linefeed-treatment="preserve">&#xA;</fo:block> 
				            	</fo:table-cell>
				        </fo:table-row>
	                </fo:table-body>
                </fo:table>
            </fo:block> 		
		</fo:flow>
		</fo:page-sequence>
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
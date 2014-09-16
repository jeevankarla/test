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
            <fo:simple-page-master master-name="main" page-height="12in" page-width="10in"  margin-left=".3in" margin-right=".3in" margin-top=".8in" margin-bottom="0.5in">
                <fo:region-body margin-top="0.5in"/>
                <fo:region-before extent="1in"/>
                <fo:region-after extent="1in"/>
            </fo:simple-page-master>
        </fo:layout-master-set>
      <#if curntCatAbsMap?has_content> 	 
       ${setRequestAttribute("OUTPUT_FILENAME", "PCMReport.pdf")}   
        <fo:page-sequence master-reference="main" force-page-count="no-force" font-family="Courier,monospace">		
        <fo:static-content flow-name="xsl-region-before">
              		<fo:block  keep-together="always" text-align="right" font-family="Courier,monospace" white-space-collapse="false"> &#160;${uiLabelMap.CommonPage}- <fo:page-number/></fo:block>
              		<fo:block text-align="left"  keep-together="always"  white-space-collapse="false" linefeed-treatment="preserve">&#xA;</fo:block> 
            </fo:static-content>	
              	
		        	<fo:flow flow-name="xsl-region-body"  font-family="Courier,monospace">	
		        	    <fo:block text-align="left"  keep-together="always"  font-family="Courier,monospace" font-weight="bold" white-space-collapse="false">&#160;               KARNATAKA CO-OPERATIVE MILK PRODUCERS FEDERATION LTD</fo:block>
                    	<fo:block text-align="left"  keep-together="always"  font-family="Courier,monospace" font-weight="bold" white-space-collapse="false">&#160;             UNIT : MOTHER DAIRY , G.K.V.K POST : YELAHANKA, BANGALORE -560065.</fo:block>
                    	<fo:block text-align="left"  font-family="Courier,monospace" font-weight="bold"  white-space-collapse="false">&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;            ANALYSIS OF RETAILER'S MILK SALES  </fo:block>
                    	<fo:block text-align="left"  font-family="Courier,monospace" font-weight="bold"  white-space-collapse="false">&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;    WITH COMPARISION TO PREVIOUS MONTH SALES(SACHET ROUTES) </fo:block>
                    	<fo:block text-align="left"  keep-together="always"  font-family="Courier,monospace" font-weight="bold" white-space-collapse="false"> UserLogin:<#if userLogin?exists>${userLogin.userLoginId?if_exists}</#if>               &#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;Print Date :${printDate?if_exists}</fo:block>
              			<fo:block text-align="left"  keep-together="always"  white-space-collapse="false">==============================================================================================</fo:block> 
		        		<fo:block text-align="left"  keep-together="always"  font-family="Courier,monospace" font-weight="bold" white-space-collapse="false">PARTICULARS				                           				      ${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(pMonthStart, "MMM-yy")}		                    ${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(cMonthStart, "MMM-yy")}	       </fo:block> 
		        		<fo:block text-align="left"  keep-together="always"   white-space-collapse="false">==============================================================================================</fo:block> 
            	<fo:block>
                 	<fo:table>
                    <fo:table-column column-width="180pt"/>
                    <fo:table-column column-width="150pt"/> 
               	    <fo:table-column column-width="70pt"/>
               	    <fo:table-column column-width="70pt"/>
               	    <fo:table-column column-width="70pt"/>
                    <fo:table-column column-width="70pt"/> 
               	    <fo:table-column column-width="70pt"/>
               	    <fo:table-column column-width="60pt"/>
               	    <fo:table-column column-width="60pt"/>
                    <fo:table-body>
                    <fo:table-row>
               				<fo:table-cell>
                           		<fo:block  keep-together="always" text-align="left" white-space-collapse="false">&#xA;</fo:block>  
                       		</fo:table-cell>
	                     
	                    <#assign previousMonthCatLst=prvCatAbsMap.entrySet()>	
						<#list previousMonthCatLst as pMonthVal>
               				<fo:table-cell>
                           		<fo:block  font-weight="bold" keep-together="always" font-size="11pt" text-align="right" white-space-collapse="false"><#if pMonthVal.getKey() == "SCT_RTLR">SACHET</#if><#if pMonthVal.getKey() == "CR_INST">CREDIT</#if>
		            	  		<#if pMonthVal.getKey() == "SHP_RTLR">BVB</#if></fo:block> 
		            	  		</fo:table-cell>
						</#list>
						<#assign currentMonthCatLst=curntCatAbsMap.entrySet()>	
	                    <#list currentMonthCatLst as cMonthVal>
	                    <fo:table-cell>
               				<fo:block  font-weight="bold" font-size="11pt" keep-together="always" text-align="right" white-space-collapse="false"><#if cMonthVal.getKey() == "SCT_RTLR">SACHET</#if><#if cMonthVal.getKey() == "CR_INST">CREDIT</#if>
		            	  		<#if cMonthVal.getKey() == "SHP_RTLR">BVB</#if></fo:block> 
		            	  </fo:table-cell>	
						</#list>
						 </fo:table-row>
                     <fo:table-row>
               				<fo:table-cell>
                           		<fo:block  keep-together="always" font-size="11pt" text-align="left" white-space-collapse="false">NO OF RETAILER</fo:block>  
                       		</fo:table-cell>
	                     
	                     <#assign previousMonthCatLst=prvCatAbsMap.entrySet()>	
						<#list previousMonthCatLst as pMonthVal>
	                       <#assign pMonthValMap=pMonthVal.getValue()>
               				<fo:table-cell>
                           		<fo:block  keep-together="always" font-size="11pt" text-align="right" white-space-collapse="false">${pMonthValMap.get("catSize")}</fo:block>  
                       		</fo:table-cell>
						</#list>
						<#assign currentMonthCatLst=curntCatAbsMap.entrySet()>	
	                    <#list currentMonthCatLst as cMonthVal>
	                       <#assign cMonthValMap=cMonthVal.getValue()>
               				<fo:table-cell>
                           		<fo:block  keep-together="always" font-size="11pt" text-align="right" white-space-collapse="false">${cMonthValMap.get("catSize")}</fo:block>  
                       		</fo:table-cell>
						</#list>
						 </fo:table-row>
						 <fo:table-row>	
				            	<fo:table-cell>
				            		<fo:block linefeed-treatment="preserve">&#xA;</fo:block> 
				            	</fo:table-cell>
				        </fo:table-row>
						  <fo:table-row>
                       		<fo:table-cell>
                           		<fo:block  keep-together="always" font-size="11pt" text-align="left" white-space-collapse="false">RETAILER AVERAGE SALES</fo:block>  
                       		</fo:table-cell>
                       	<#assign previousMonthCatLst=prvCatAbsMap.entrySet()>	
						<#list previousMonthCatLst as pMonthVal>
	                       <#assign pMonthValMap=pMonthVal.getValue()>
               				<fo:table-cell>
                           		<fo:block  keep-together="always" font-size="11pt" text-align="right" white-space-collapse="false">${(Static["java.lang.Math"].round(pMonthValMap.get("milkAvgTot")))?string("0")}</fo:block>  
                       		</fo:table-cell>
						</#list>
						
		                <#assign currentMonthCatLst=curntCatAbsMap.entrySet()>	
	                    <#list currentMonthCatLst as cMonthVal>
	                       <#assign cMonthValMap=cMonthVal.getValue()>
               				<fo:table-cell>
                           		<fo:block  keep-together="always" font-size="11pt" text-align="right" white-space-collapse="false">${(Static["java.lang.Math"].round(cMonthValMap.get("milkAvgTot")))?string("0")}</fo:block>  
                       		</fo:table-cell>
						</#list>
						 </fo:table-row>
						  <fo:table-row>	
				            	<fo:table-cell>
				            		<fo:block linefeed-treatment="preserve">&#xA;</fo:block> 
				            	</fo:table-cell>
				        </fo:table-row>
						 <fo:table-row>
               				<fo:table-cell>
                           		<fo:block  keep-together="always" font-size="11pt" text-align="left" white-space-collapse="false">NO OF RETAILER SELLING SAME QTY</fo:block>  
                       		</fo:table-cell>
                       	<#assign previousMonthCatLst=prvCatAbsMap.entrySet()>	
						<#list previousMonthCatLst as pMonthVal>
	                       <#assign pMonthValMap=pMonthVal.getValue()>
	                        <#assign sameQty=pMonthValMap.get("sameQty")>
               				<fo:table-cell>
                           		<fo:block  keep-together="always" font-size="11pt" text-align="right" white-space-collapse="false"></fo:block>  
                       		</fo:table-cell>
						</#list>
	                    <#assign currentMonthCatLst=curntCatAbsMap.entrySet()>	
	                    <#list currentMonthCatLst as cMonthVal>
	                    <#assign cMonthValMap=cMonthVal.getValue()>
               				<fo:table-cell>
                           		<fo:block  keep-together="always" font-size="11pt" text-align="right" white-space-collapse="false">${cMonthValMap.get("sameQty")}</fo:block>  
                       		</fo:table-cell>
						</#list>
						 </fo:table-row>
						 <fo:table-row>	
				            	<fo:table-cell>
				            		<fo:block linefeed-treatment="preserve">&#xA;</fo:block> 
				            	</fo:table-cell>
				        </fo:table-row>
				        <fo:table-row>
               				<fo:table-cell>
                           		<fo:block  keep-together="always" font-size="11pt" text-align="left" white-space-collapse="false">NO OF RETAILER DECREASED SALES</fo:block>  
                       		</fo:table-cell>
                       	<#assign previousMonthCatLst=prvCatAbsMap.entrySet()>	
						<#list previousMonthCatLst as pMonthVal>
	                       <#assign pMonthValMap=pMonthVal.getValue()>
               				<fo:table-cell>
                           		<fo:block  keep-together="always" font-size="11pt" text-align="right" white-space-collapse="false"></fo:block>  
                       		</fo:table-cell>
						</#list>
	                    <#assign currentMonthCatLst=curntCatAbsMap.entrySet()>	
	                    <#list currentMonthCatLst as cMonthVal>
	                    <#assign cMonthValMap=cMonthVal.getValue()>
               				<fo:table-cell>
                           		<fo:block  keep-together="always" font-size="11pt" text-align="right" white-space-collapse="false">${cMonthValMap.get("descQty")}</fo:block>  
                       		</fo:table-cell>
						</#list>
						 </fo:table-row>
						 <fo:table-row>	
				            	<fo:table-cell>
				            		<fo:block linefeed-treatment="preserve">&#xA;</fo:block> 
				            	</fo:table-cell>
				        </fo:table-row>
				        <fo:table-row>
               				<fo:table-cell>
                           		<fo:block  keep-together="always" font-size="11pt" text-align="left" white-space-collapse="false">NO OF RETAILER INCREASED SALES</fo:block>  
                       		</fo:table-cell>
                       	   <#assign previousMonthCatLst=prvCatAbsMap.entrySet()>	
						<#list previousMonthCatLst as pMonthVal>
	                       <#assign pMonthValMap=pMonthVal.getValue()>
               				<fo:table-cell>
                           		<fo:block  keep-together="always" text-align="right" white-space-collapse="false"></fo:block>  
                       		</fo:table-cell>
						</#list>
	                    <#assign currentMonthCatLst=curntCatAbsMap.entrySet()>	
	                    <#list currentMonthCatLst as cMonthVal>
	                    <#assign cMonthValMap=cMonthVal.getValue()>
               				<fo:table-cell>
                           		<fo:block  keep-together="always" font-size="11pt" text-align="right" white-space-collapse="false">${cMonthValMap.get("incQty")}</fo:block>  
                       		</fo:table-cell>
						</#list>
						 </fo:table-row>
						<fo:table-row>	
			            	<fo:table-cell>
			            		<fo:block linefeed-treatment="preserve">&#xA;</fo:block> 
			            	</fo:table-cell>
				        </fo:table-row>
				        <fo:table-row>
               				<fo:table-cell>
                           		<fo:block  keep-together="always" font-size="11pt" text-align="left" white-space-collapse="false">NO OF NEW RETAILER</fo:block>  
                       		</fo:table-cell>
                       	
                       	<#list categorysList as eachCat>
                       	 	<#assign catValue = prvNewFacilityMap.get(eachCat)?if_exists>
                       		<#if catValue?has_content>
                       			<fo:table-cell>
	                           		<fo:block  keep-together="always" font-size="11pt" text-align="right" white-space-collapse="false">${catValue}</fo:block>  
	                       		</fo:table-cell>
	                       		<#else>
	                       		<fo:table-cell>
	                           		<fo:block  keep-together="always" font-size="11pt" text-align="right" white-space-collapse="false"></fo:block>  
	                       		</fo:table-cell>
                       		</#if>
                       	</#list>
                       		<#list categorysList as eachCat>
                       	 	<#assign catValue = curNewFacilityMap.get(eachCat)?if_exists>
                       		<#if catValue?has_content>
                       			<fo:table-cell>
	                           		<fo:block  keep-together="always" font-size="11pt" text-align="right" white-space-collapse="false">${catValue}</fo:block>  
	                       		</fo:table-cell>
	                       		<#else>
	                       		<fo:table-cell>
	                           		<fo:block  keep-together="always" font-size="11pt" text-align="right" white-space-collapse="false"></fo:block>  
	                       		</fo:table-cell>
                       		</#if>
                       	</#list>
                       	
                       	<#--<#assign previousMonthfacLst=prvFacilityMap.entrySet()>	
						<#list previousMonthfacLst as pMonthfac>
	                       <#assign pMonthfacility=pMonthfac.getValue()>
               				<fo:table-cell>
                           		<fo:block  keep-together="always" font-size="11pt" text-align="right" white-space-collapse="false">${pMonthfacility}</fo:block>  
                       		</fo:table-cell>
                       		
						</#list>
	                    <#assign currentMonthfacLst=curFacilityMap.entrySet()>	
	                    <#list currentMonthfacLst as cMonthfac>
	                    <#assign cMonthfacility=cMonthfac.getValue()>
               				<fo:table-cell>
                           		<fo:block  keep-together="always" font-size="11pt" text-align="right" white-space-collapse="false">${cMonthfacility}</fo:block>  
                       		</fo:table-cell>
						</#list>-->
						 </fo:table-row>
						 <fo:table-row>	
			            	<fo:table-cell>
			            		<fo:block linefeed-treatment="preserve">&#xA;</fo:block> 
			            	</fo:table-cell>
				        </fo:table-row>
				        <fo:table-row>
               				<fo:table-cell>
                           		<fo:block  keep-together="always" font-size="11pt" text-align="left" white-space-collapse="false">NO OF RETAILER SELLING LESS THAN 100 LTRS</fo:block>  
                       		</fo:table-cell>
                       	<#assign previousMonthCatLst=prvCatAbsMap.entrySet()>	
						<#list previousMonthCatLst as pMonthVal>
	                       <#assign pMonthValMap=pMonthVal.getValue()>
               				<fo:table-cell>
                           		<fo:block  keep-together="always" font-size="11pt" text-align="right" white-space-collapse="false">${pMonthValMap.get("less100")}</fo:block>  
                       		</fo:table-cell>
						</#list>
	                    <#assign currentMonthCatLst=curntCatAbsMap.entrySet()>	
	                    <#list currentMonthCatLst as cMonthVal>
	                    <#assign cMonthValMap=cMonthVal.getValue()>
               				<fo:table-cell>
                           		<fo:block  keep-together="always" font-size="11pt" text-align="right" white-space-collapse="false">${cMonthValMap.get("less100")}</fo:block>  
                       		</fo:table-cell>
						</#list>
						 </fo:table-row>
						 <fo:table-row>	
				            	<fo:table-cell>
				            		<fo:block linefeed-treatment="preserve">&#xA;</fo:block> 
				            	</fo:table-cell>
				        </fo:table-row>
				        <fo:table-row>
               				<fo:table-cell>
                           		<fo:block  keep-together="always" font-size="11pt" text-align="left" white-space-collapse="false">NO OF RETAILER SELLING 101 TO 250 LTRS</fo:block>  
                       		</fo:table-cell>
                       	<#assign previousMonthCatLst=prvCatAbsMap.entrySet()>	
						<#list previousMonthCatLst as pMonthVal>
	                       <#assign pMonthValMap=pMonthVal.getValue()>
               				<fo:table-cell>
                           		<fo:block  keep-together="always" font-size="11pt" text-align="right" white-space-collapse="false">${pMonthValMap.get("bwt101To250")}</fo:block>  
                       		</fo:table-cell>
						</#list>
	                    <#assign currentMonthCatLst=curntCatAbsMap.entrySet()>	
	                    <#list currentMonthCatLst as cMonthVal>
	                    <#assign cMonthValMap=cMonthVal.getValue()>
               				<fo:table-cell>
                           		<fo:block  keep-together="always" font-size="11pt" text-align="right" white-space-collapse="false">${cMonthValMap.get("bwt101To250")}</fo:block>  
                       		</fo:table-cell>
						</#list>
						 </fo:table-row>
						 <fo:table-row>	
				            	<fo:table-cell>
				            		<fo:block linefeed-treatment="preserve">&#xA;</fo:block> 
				            	</fo:table-cell>
				        </fo:table-row>
				        <fo:table-row>
               				<fo:table-cell>
                           		<fo:block  keep-together="always" font-size="11pt" text-align="left" white-space-collapse="false">NO OF RETAILER SELLING 251 TO 500 LTRS</fo:block>  
                       		</fo:table-cell>
                       	<#assign previousMonthCatLst=prvCatAbsMap.entrySet()>	
                       	<#list previousMonthCatLst as pMonthVal>
	                       <#assign pMonthValMap=pMonthVal.getValue()>
               				<fo:table-cell>
                           		<fo:block  keep-together="always" font-size="11pt" text-align="right" white-space-collapse="false">${pMonthValMap.get("bwt251To500")}</fo:block>  
                       		</fo:table-cell>
						</#list>	
	                    <#assign currentMonthCatLst=curntCatAbsMap.entrySet()>	
	                    <#list currentMonthCatLst as cMonthVal>
	                    <#assign cMonthValMap=cMonthVal.getValue()>
               				<fo:table-cell>
                           		<fo:block  keep-together="always" font-size="11pt" text-align="right" white-space-collapse="false">${cMonthValMap.get("bwt251To500")}</fo:block>  
                       		</fo:table-cell>
						</#list>
						 </fo:table-row>
						  <fo:table-row>	
				            	<fo:table-cell>
				            		<fo:block linefeed-treatment="preserve">&#xA;</fo:block> 
				            	</fo:table-cell>
				        </fo:table-row>
				        <fo:table-row>
               				<fo:table-cell>
                           		<fo:block  keep-together="always" font-size="11pt" text-align="left" white-space-collapse="false">NO OF RETAILER SELLING 501 TO 750 LTRS</fo:block>  
                       		</fo:table-cell>
                       	<#assign previousMonthCatLst=prvCatAbsMap.entrySet()>	
                       	<#list previousMonthCatLst as pMonthVal>
	                       <#assign pMonthValMap=pMonthVal.getValue()>
               				<fo:table-cell>
                           		<fo:block  keep-together="always" font-size="11pt" text-align="right" white-space-collapse="false">${pMonthValMap.get("bwt501To750")}</fo:block>  
                       		</fo:table-cell>
						</#list>
	                    <#assign currentMonthCatLst=curntCatAbsMap.entrySet()>	
	                    <#list currentMonthCatLst as cMonthVal>
	                    <#assign cMonthValMap=cMonthVal.getValue()>
               				<fo:table-cell>
                           		<fo:block  keep-together="always" font-size="11pt" text-align="right" white-space-collapse="false">${cMonthValMap.get("bwt501To750")}</fo:block>  
                       		</fo:table-cell>
						</#list>
						 </fo:table-row>
						  <fo:table-row>	
				            	<fo:table-cell>
				            		<fo:block linefeed-treatment="preserve">&#xA;</fo:block> 
				            	</fo:table-cell>
				        </fo:table-row>
						  <fo:table-row>
                       		<fo:table-cell>
                           		<fo:block  keep-together="always" font-size="11pt" text-align="left" white-space-collapse="false">NO OF RETAILER SELLING 751 TO 1000 LTRS</fo:block>  
                       		</fo:table-cell>
                       		<#assign previousMonthCatLst=prvCatAbsMap.entrySet()>	
                       		<#list previousMonthCatLst as pMonthVal>
	                       <#assign pMonthValMap=pMonthVal.getValue()>
               				<fo:table-cell>
                           		<fo:block  keep-together="always" font-size="11pt" text-align="right" white-space-collapse="false">${pMonthValMap.get("bwt751To1000")}</fo:block>  
                       		</fo:table-cell>
						   </#list>
		                    <#assign currentMonthCatLst=curntCatAbsMap.entrySet()>	
		                    <#list currentMonthCatLst as cMonthVal>
		                     <#assign cMonthValMap=cMonthVal.getValue()>
		               		<fo:table-cell>
                           		<fo:block  keep-together="always" font-size="11pt" text-align="right" white-space-collapse="false">${cMonthValMap.get("bwt751To1000")}</fo:block>  
                       		</fo:table-cell>
						</#list>
						 </fo:table-row>
						  <fo:table-row>	
				            	<fo:table-cell>
				            		<fo:block linefeed-treatment="preserve">&#xA;</fo:block> 
				            	</fo:table-cell>
				        </fo:table-row>
						  <fo:table-row>
                       		<fo:table-cell>
                           		<fo:block  keep-together="always" font-size="11pt" text-align="left" white-space-collapse="false">NO OF RETAILER SELLING ABOVE 1000 LTRS</fo:block>  
                       		</fo:table-cell>
                       		<#assign previousMonthCatLst=prvCatAbsMap.entrySet()>
                       		<#list previousMonthCatLst as pMonthVal>
	                       <#assign pMonthValMap=pMonthVal.getValue()>
               				<fo:table-cell>
                           		<fo:block  keep-together="always" font-size="11pt" text-align="right" white-space-collapse="false">${pMonthValMap.get("above1000")}</fo:block>  
                       		</fo:table-cell>
						</#list>
		                    <#assign currentMonthCatLst=curntCatAbsMap.entrySet()>	
		                    <#list currentMonthCatLst as cMonthVal>
		                     <#assign cMonthValMap=cMonthVal.getValue()>
		               		<fo:table-cell>
                           		<fo:block  keep-together="always" font-size="11pt" text-align="right" white-space-collapse="false">${cMonthValMap.get("above1000")}</fo:block>  
                       		</fo:table-cell>
						</#list>
						 </fo:table-row>
				        <fo:table-row>
						<fo:table-cell>
		            		<fo:block  keep-together="always">-----------------------------------------------------------------------------------------------------</fo:block>  
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
				          <fo:table-row>
				                 <fo:table-cell>
				            		<fo:block linefeed-treatment="preserve">&#xA;</fo:block> 
				            	</fo:table-cell>
				            	<fo:table-cell>
				            		<fo:block linefeed-treatment="preserve">&#xA;</fo:block> 
				            	</fo:table-cell>
				            	<fo:table-cell>
				            		<fo:block linefeed-treatment="preserve">&#xA;</fo:block> 
				            	</fo:table-cell>
				            	<fo:table-cell>
				            		<fo:block linefeed-treatment="preserve">&#xA;</fo:block> 
				            	</fo:table-cell>
				            	<fo:table-cell>
				            		<fo:block linefeed-treatment="preserve">&#xA;</fo:block> 
				            	</fo:table-cell>
				            	<fo:table-cell>
				            	    <fo:block text-align="right"  keep-together="always"  font-weight="bold" white-space-collapse="false">	                                                                         GM(System)</fo:block>
				            		<fo:block text-align="right"  keep-together="always"  font-weight="bold"  white-space-collapse="false">	                                                                           Mother Dairy</fo:block>  
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
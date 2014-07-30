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
                <fo:region-body margin-top="0.5in"/>
                <fo:region-before extent="1in"/>
                <fo:region-after extent="1in"/>
            </fo:simple-page-master>
        </fo:layout-master-set>
      <#if facilitySaleMap?has_content> 	    
        ${setRequestAttribute("OUTPUT_FILENAME", "NewOrTerminatedRetailerReport.txt")}
        <fo:page-sequence master-reference="main" force-page-count="no-force" font-family="Courier,monospace">		
        <fo:static-content flow-name="xsl-region-before">
              		<fo:block  keep-together="always" text-align="right" font-family="Courier,monospace" white-space-collapse="false"> &#160;${uiLabelMap.CommonPage}- <fo:page-number/></fo:block>
              		<fo:block text-align="left"  keep-together="always"  white-space-collapse="false" linefeed-treatment="preserve">&#xA;</fo:block> 
            </fo:static-content>	
              	
		        	<fo:flow flow-name="xsl-region-body"  font-family="Courier,monospace">	
		        	<fo:block text-align="left"  keep-together="always"  font-family="Courier,monospace" font-weight="bold" white-space-collapse="false">&#160;     KARNATAKA CO-OPERATIVE MILK PRODUCERS FEDERATION LTD</fo:block>
                    	<fo:block text-align="left"  keep-together="always"  font-family="Courier,monospace" font-weight="bold" white-space-collapse="false">&#160;    UNIT : MOTHER DAIRY , G.K.V.K POST : YELAHANKA, BANGALORE -560065.</fo:block>
                    	<fo:block text-align="left"  font-family="Courier,monospace" font-weight="bold"  white-space-collapse="false">&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;     LIST OF NEW AGENTS FOR ${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(newOrTerminateDate, "dd-MMM-yyyy")} </fo:block>
                    	<fo:block text-align="left"  keep-together="always"  font-family="Courier,monospace" font-weight="bold" white-space-collapse="false"> UserLogin:<#if userLogin?exists>${userLogin.userLoginId?if_exists}</#if>               &#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;Print Date :${printDate?if_exists}</fo:block>
              			<fo:block text-align="left"  keep-together="always"  white-space-collapse="false">==========================================================================</fo:block> 
		        		<fo:block text-align="left"  keep-together="always"  font-family="Courier,monospace" font-weight="bold" white-space-collapse="false">SRNO   	    CODE		   NAME OF THE RETAILER  	         DATE OF COMMISSION								       </fo:block> 
		        		<fo:block text-align="left"  keep-together="always"   white-space-collapse="false">=========================================================================</fo:block> 
            	<fo:block>
                 	<fo:table>
                    <fo:table-column column-width="88pt"/>
                    <fo:table-column column-width="65pt"/> 
            		<fo:table-column column-width="230pt"/> 
            		<fo:table-column column-width="30pt"/> 	
                    <fo:table-body>
                    <#assign sno=0>
                    <#assign lineNo = 0>
                    <#assign facilityList=facilitySaleMap.entrySet()>
                    <#list facilityList as facility>
                    <#assign sno=0>
                    <fo:table-row>
						<fo:table-cell>
		            	  		<fo:block  font-weight="bold" keep-together="always" text-align="left" white-space-collapse="false"><#if facility.getKey() == "SCT_RTLR">SACHET</#if><#if facility.getKey() == "CR_INST">CREDIT</#if>
		            	  		<#if facility.getKey() == "SHP_RTLR">BVB</#if></fo:block>  
                       	</fo:table-cell>
				        </fo:table-row>
					<#assign sachet=facility.getValue().entrySet()>
					<#list sachet as sachetfacility>
					<#assign sno=sno+1>
					<fo:table-row>
               				<fo:table-cell>
                           		<fo:block  keep-together="always" text-align="left" white-space-collapse="false">${sno}</fo:block>  
                       		</fo:table-cell>
                       		
                       		<fo:table-cell>
                           		<fo:block  keep-together="always" text-align="left" white-space-collapse="false">${sachetfacility.getKey()}</fo:block>  
                       		</fo:table-cell>
                       		<#assign sachetDetails = sachetfacility.getValue()>
                       			<fo:table-cell>
                           			<fo:block  keep-together="always" text-align="left" white-space-collapse="false">${(sachetDetails.get("name"))}</fo:block>  
                       			</fo:table-cell>
                       			<fo:table-cell>
                           			<fo:block  keep-together="always" text-align="left" white-space-collapse="false">${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(sachetDetails.get("date"), "dd-MMM-yyyy")}</fo:block>  
                       			</fo:table-cell>
                       		
						</fo:table-row>
						</#list>
						</#list>
				        <fo:table-row>
						<fo:table-cell>
		            		<fo:block  keep-together="always">--------------------------------------------------------------------------</fo:block>  
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
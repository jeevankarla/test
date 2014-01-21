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
            margin-top="0.5in" margin-bottom="1in" margin-left=".3in" margin-right="1in">
        <fo:region-body margin-top="1.7in"/>
        <fo:region-before extent="1in"/>
        <fo:region-after extent="1in"/>        
    </fo:simple-page-master>   
</fo:layout-master-set>
${setRequestAttribute("OUTPUT_FILENAME", "IndentPartyListReport.pdf")}
<#assign lineNumber = 5>
<#assign numberOfLines = 60>
<#assign facilityNumberInPage = 0>
<#if partyList?has_content>
<fo:page-sequence master-reference="main" force-page-count="no-force">					
			<fo:static-content flow-name="xsl-region-before"> <#assign lineNumber = 5> 
				<#assign facilityNumberInPage = 0>
					<fo:block  keep-together="always" text-align="left" font-family="Courier,monospace" white-space-collapse="false">&#160;      ${uiLabelMap.KMFDairyHeader}</fo:block>
					<fo:block  keep-together="always" text-align="left" font-family="Courier,monospace" white-space-collapse="false">&#160;      ${uiLabelMap.KMFDairySubHeader}</fo:block>
					<fo:block linefeed-treatment="preserve">&#xA;</fo:block>			
              		<fo:block keep-together="always" font-family="Courier,monospace" white-space-collapse="false">MILK PRODUCTS INDENT PARTY LIST                           DATE: ${indentDate}</fo:block>
              		<fo:block linefeed-treatment="preserve">&#xA;</fo:block>
  	              	<fo:block>-------------------------------------------------------------------------------------------------------------------------------------</fo:block>
            	<fo:block white-space-collapse="false" font-size="12pt"  font-family="Courier,monospace"  text-align="left">  SNO  ROUTE   PARTY-CD      PARTY NAME                AREA </fo:block>
            	<fo:block>-------------------------------------------------------------------------------------------------------------------------------------</fo:block>
			</fo:static-content>
			<fo:flow flow-name="xsl-region-body" font-family="Courier,monospace">
       		
				<fo:table width="100%" table-layout="fixed" space-after="0.0in">
					<fo:table-column column-width="100%"/>
            		<fo:table-body>
			        	<fo:table-row column-width="100%">
			            <fo:table-cell column-width="100%">
            			<fo:table  table-layout="fixed" > 	
            				<fo:table-column column-width="30pt"/>
				   		 	<fo:table-column column-width="50pt"/>
				    		<fo:table-column column-width="90pt"/>
				    		<fo:table-column column-width="200pt"/>
				    		<fo:table-column column-width="180pt"/>
				    			<fo:table-body>
				    			<#assign serialNo = 1/>
				    			<#assign partyDetailList = partyList.entrySet()> 
				    			<#list partyDetailList as eachEntry>
				    			<#assign facilityMap = eachEntry.getValue()>
                				<fo:table-row>                            
                            		<fo:table-cell>
                                		<fo:block text-align="left">${serialNo}</fo:block>
                            		</fo:table-cell>
                            		<fo:table-cell>
                            			<#assign routeList= facilityMap.get('byProdRouteId')>
                            			<fo:block text-align="center">
                            			<#if routeList.size()&gt; 1>
                            					${routeList}
                            				<#else>
                            					${routeList.get(0)?if_exists}
                            				</#if>
                            			</fo:block>
                            		</fo:table-cell>
                            		<fo:table-cell>
                            			<fo:block text-align="center">${facilityMap.get('facilityId')?if_exists}</fo:block>
                            		</fo:table-cell>
                            		<fo:table-cell>
                            			<fo:block text-align="left">${facilityMap.get('facilityName')?if_exists}</fo:block>
                            		</fo:table-cell>
                            		<fo:table-cell>
                            			<fo:block text-align="left">${facilityMap.get('area')?if_exists}</fo:block>
                            		</fo:table-cell>	                           
                       			</fo:table-row>
                       			<#assign serialNo = serialNo+1>
                       			</#list>
            				</fo:table-body>
       					 </fo:table>
       				</fo:table-cell>
        		</fo:table-row>
         	</fo:table-body>
         </fo:table>
	   </fo:flow>						        	
   </fo:page-sequence>
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
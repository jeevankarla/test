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
        <fo:region-body margin-top="1in"/>
        <fo:region-before extent="1in"/>
        <fo:region-after extent="1in"/>        
    </fo:simple-page-master>   
</fo:layout-master-set>
${setRequestAttribute("OUTPUT_FILENAME", "IndentPartyListReport.txt")}
<#assign lineNumber = 5>
<#assign numberOfLines = 55>
<#if routeMap?has_content>
<fo:page-sequence master-reference="main" force-page-count="no-force">					
			<fo:static-content flow-name="xsl-region-before">
					<fo:block text-align="left" font-size="7pt" white-space-collapse="false">&#160;              ${uiLabelMap.aavinDairyMsg}</fo:block>
					<fo:block text-align="left" font-size="7pt" white-space-collapse="false">&#160;              MARKETING UNIT, PRODUCTS DIVISION, CHENNAI-98</fo:block>
					<fo:block linefeed-treatment="preserve" font-size="7pt">&#xA;</fo:block>			
              		<fo:block font-size="7pt" white-space-collapse="false">MILK PRODUCTS INDENT PARTY LIST      DATE: ${indentDate}</fo:block>
              		<fo:block linefeed-treatment="preserve" font-size="7pt">&#xA;</fo:block>
  	              	<fo:block font-size="10pt">--------------------------------------------------------------------------------</fo:block>
            	<fo:block white-space-collapse="false" font-size="7pt"  font-family="Courier,monospace"  text-align="left">  SNO ROUTE PRT-CD   PARTY NAME                AREA</fo:block>
            	<fo:block font-size="10pt">--------------------------------------------------------------------------------</fo:block>
			</fo:static-content>
			<fo:flow flow-name="xsl-region-body" font-family="Courier,monospace">
       		
				<fo:table width="100%" table-layout="fixed" space-after="0.0in">
					<fo:table-column column-width="100%"/>
            		<fo:table-body>
			        	<fo:table-row column-width="100%">
			            <fo:table-cell column-width="100%">
            			<fo:table  table-layout="fixed" > 	
            				<fo:table-column column-width="16pt"/>
				   		 	<fo:table-column column-width="18pt"/>
				    		<fo:table-column column-width="30pt"/>
				    		<fo:table-column column-width="110pt"/>
				    		<fo:table-column column-width="5pt"/>
				    		<fo:table-column column-width="150pt"/>
                    <fo:table-body>
				    			<#assign serialNo = 1/>
				    			<#assign partyDetailList = routeMap.entrySet()> 
				    			<#list partyDetailList as eachEntry>
				    				<#assign routeId = eachEntry.getKey()>
				    				<#assign facilityList = eachEntry.getValue()>
				    				<#list facilityList as eachFacility>
				    					<fo:table-row> 
	    									<fo:table-cell>
                        						<fo:block text-align="left" font-size="7pt">${serialNo}</fo:block>
                    						</fo:table-cell>
                    						<fo:table-cell>
                        						<fo:block text-align="left" font-size="7pt">${routeId}</fo:block>
                    						</fo:table-cell>
                    						<fo:table-cell>
                        						<fo:block text-align="left" font-size="7pt">${eachFacility.get("facilityId")?if_exists}</fo:block>
                    						</fo:table-cell>
                    						<fo:table-cell>
                    						<#if eachFacility.get("facilityName")?has_content>
                        						<fo:block text-align="left" font-size="7pt">${Static["org.ofbiz.order.order.OrderServices"].nameTrim((StringUtil.wrapString(eachFacility.get("facilityName"))),26)?if_exists?if_exists}</fo:block>
                        					<#else>	
                        						<fo:block text-align="left" font-size="7pt"></fo:block>
                        					</#if>
                    						</fo:table-cell>
                    						<fo:table-cell>
                        						<fo:block linefeed-treatment="preserve" font-size="7pt">&#xA;</fo:block>
                    						</fo:table-cell>
                    						<fo:table-cell>
                    						<#if eachFacility.get("area")?has_content>
                        						<fo:block text-align="left" font-size="7pt">${Static["org.ofbiz.order.order.OrderServices"].nameTrim((StringUtil.wrapString(eachFacility.get("area"))),30)?if_exists}</fo:block>
                        					<#else>	
                        						<fo:block text-align="left" font-size="7pt"></fo:block>
                        					</#if>
                    						</fo:table-cell>
                    						<#assign serialNo = serialNo + 1 />
                            		  </fo:table-row>
                            		<#assign lineNumber = lineNumber + 1>
                    				<#if (lineNumber >= numberOfLines)>
                    				<#assign lineNumber = 5>
			                    		<fo:table-row>
			                   	     		<fo:table-cell>
				                            	<fo:block font-size="7pt" page-break-after="always"></fo:block>        
				                        	</fo:table-cell>
				                    	</fo:table-row>
			                    	<#else>
			                    	</#if>
				    				</#list>
                       			</#list>
                       					<fo:table-row>
			                   	     		<fo:table-cell>
			                   	     		<fo:block font-size="10pt">================================================================================</fo:block>
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
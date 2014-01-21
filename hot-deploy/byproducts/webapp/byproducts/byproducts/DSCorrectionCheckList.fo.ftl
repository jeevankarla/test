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
            <fo:simple-page-master master-name="main" page-height="12in" page-width="15in"  margin-left=".3in" margin-right=".3in" margin-top=".5in">
                <fo:region-body margin-top=".9in"/>
                <fo:region-before extent="1in"/>
                <fo:region-after extent="1in"/>
            </fo:simple-page-master>
        </fo:layout-master-set>
               ${setRequestAttribute("OUTPUT_FILENAME", "DSCorrectionCheckList.txt")}
		        <fo:page-sequence master-reference="main" font-size="10pt">	
		        	<fo:static-content flow-name="xsl-region-before" font-family="Courier,monospace">
		        		<fo:block text-align="left" white-space-collapse="false"  font-weight="bold" keep-together="always"  >&#160;                                 ${uiLabelMap.KMFDairyHeader}</fo:block>
		        		<fo:block text-align="left" white-space-collapse="false"  font-weight="bold" keep-together="always">&#160;                                      ${uiLabelMap.KMFDairySubHeader}</fo:block>
		        		<fo:block text-align="left" white-space-collapse="false" font-weight="bold" keep-together="always">&#160;                                               DELIVERY SCHEDULE CORRECTION CHECKLIST</fo:block>
		        		<fo:block linefeed-treatment="preserve">&#xA;</fo:block>
		        		<fo:block text-align="left" keep-together="always" white-space-collapse="false" font-weight="bold">ROUTE   PARTY    PRODUCT   INDENT-QTY   CORRECTED QTY    CORRECTED TIME    USER</fo:block>	 	 	  
		        		<fo:block font-size="9pt">----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------</fo:block>
		        	</fo:static-content>	        	
		        	<fo:flow flow-name="xsl-region-body" font-family="Courier,monospace" font-size="9pt">	        		
		        		  <fo:block>
		        		 		<fo:table  table-layout="fixed" font-family="Courier,monospace" font-size="8pt" >                
		               				<fo:table-column column-width="40pt"/>
		                			<fo:table-column column-width="70pt"/>
		               				<fo:table-column column-width="70pt"/>
		              				<fo:table-column column-width="70pt"/>
		             				<fo:table-column column-width="70pt"/>
		                			<fo:table-column column-width="140"/>
		                			<fo:table-column column-width="130"/>
	                					<fo:table-body>
	                						<#list correctionList as correctionListEntry>
	                							<fo:table-row>
						                   	     	<fo:table-cell>
							                            <fo:block text-align="left" keep-together="always" font-size="7pt" white-space-collapse="false">${correctionListEntry.get("routeId")?if_exists}</fo:block>        
							                        </fo:table-cell>
							                        <fo:table-cell>
							                            <fo:block text-align="left" keep-together="always" font-size="7pt" white-space-collapse="false">${correctionListEntry.get("facilityId")?if_exists}</fo:block>        
							                        </fo:table-cell>
							                        <fo:table-cell>
							                            <fo:block text-align="left" keep-together="always" font-size="7pt" white-space-collapse="false">${correctionListEntry.get("productId")?if_exists}</fo:block>        
							                        </fo:table-cell>
							                        <fo:table-cell>
							                            <fo:block text-align="left" keep-together="always" font-size="7pt" white-space-collapse="false">${correctionListEntry.get("indentQty")?if_exists}</fo:block>        
							                        </fo:table-cell>
							                        <fo:table-cell>
							                            <fo:block text-align="left" keep-together="always" font-size="7pt" white-space-collapse="false">${correctionListEntry.get("correctedQty")?if_exists}</fo:block>        
							                        </fo:table-cell>
							                        <fo:table-cell>
							                            <fo:block text-align="left" keep-together="always" font-size="7pt" white-space-collapse="false">${correctionListEntry.get("modifiedTime")?if_exists}</fo:block>        
							                        </fo:table-cell>
							                        <fo:table-cell>
							                            <fo:block text-align="left" keep-together="always" font-size="7pt" white-space-collapse="false">${correctionListEntry.get("modifiedByUser")?if_exists}</fo:block>        
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
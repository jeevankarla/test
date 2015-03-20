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
            <#--><fo:simple-page-master master-name="main" page-height="12in" page-width="10in"  margin-left=".3in" margin-right=".1in" margin-top=".5in"> -->
              <fo:simple-page-master master-name="main" page-height="11.69in" page-width="12in"
                     margin-left=".3in" margin-right=".1in">
                <fo:region-body margin-top="1.7in"/>
                <fo:region-before extent="1in"/>
                <fo:region-after extent="1in"/>
            </fo:simple-page-master>
        </fo:layout-master-set>
        ${setRequestAttribute("OUTPUT_FILENAME", "PartywiseLedgerAbstract.pdf")}
        <#if errorMessage?has_content>
	<fo:page-sequence master-reference="main">
	<fo:flow flow-name="xsl-region-body" font-family="Helvetica">
	   <fo:block font-size="14pt">
	           ${errorMessage}.
        </fo:block>
	</fo:flow>
	</fo:page-sequence>	
	<#else>
       <#if invoiceItemList?has_content>
      
		        <fo:page-sequence master-reference="main" font-size="12pt">	
		        	<fo:static-content flow-name="xsl-region-before" font-family="Courier,monospace">
		        		<fo:block text-align="center" white-space-collapse="false"  font-size="18pt" keep-together="always" color="blue">&#160;MSME- TOOL ROOM,HYDERABAD</fo:block>
               			<fo:block text-align="center" white-space-collapse="false"  font-size="16pt" keep-together="always" color="red">&#160;CENTRAL INSTITUTE OF TOOL DESIGN</fo:block>
               			<fo:block text-align="center" font-size="12pt" keep-together="always"  white-space-collapse="false" font-weight="bold">&#160; LIST OF INVOICE ITEMS TYPES </fo:block>
              			<fo:block font-size="11pt" text-align="left">=========================================================================================================================</fo:block> 
	                   
            			<fo:block text-align="left" font-size="12pt" keep-together="always" font-family="Courier,monospace" white-space-collapse="false" font-weight="bold">&#160; ORGANIZATION PARTY ID: ${organizationPartyId?if_exists}                  INVOICE TYPE ID:  ${parentTypeId?if_exists}                  </fo:block>
              			<fo:block font-size="11pt" text-align="left">-------------------------------------------------------------------------------------------------------------------------</fo:block>  
		        	<fo:block>
                 	<fo:table>
                    <fo:table-column column-width="200pt"/>
            		<fo:table-column column-width="140pt"/>            	
            		<fo:table-column column-width="380pt"/>
            		<fo:table-column column-width="70pt"/>
            		
                    <fo:table-body>
                    <fo:table-row>
		                    <fo:table-cell>
				            	<fo:block  keep-together="always" text-align="left" font-size="12pt" white-space-collapse="false">Invoicetype</fo:block>  
				            </fo:table-cell>
				             <fo:table-cell>
				            	<fo:block  keep-together="always" text-align="left" font-size="12pt" white-space-collapse="false">InvoiceItemType</fo:block> 
				            </fo:table-cell>
				             <fo:table-cell>
				            	<fo:block  keep-together="always" text-align="left" font-size="12pt" white-space-collapse="false">InvocieItemTypeDescription</fo:block> 
				            </fo:table-cell>
				            <fo:table-cell>
				            	<fo:block  keep-together="always" text-align="left" font-size="12pt" white-space-collapse="false">GlAccountId</fo:block> 
				            </fo:table-cell>
				            
                        </fo:table-row>
                       </fo:table-body>
                </fo:table>
              </fo:block> 
              <fo:block font-size="11pt" text-align="left">-------------------------------------------------------------------------------------------------------------------------</fo:block>	
		        	</fo:static-content>	        	
		        	<fo:flow flow-name="xsl-region-body"   font-family="Courier,monospace">		
            	<fo:block>
                 	<fo:table>
                    <fo:table-column column-width="200pt"/>
            		<fo:table-column column-width="140pt"/>            	
            		<fo:table-column column-width="380pt"/>
            		<fo:table-column column-width="70pt"/>
                    <fo:table-body>
                    <#list invoiceItemList as invoiceItems>
                        <fo:table-row>
		                    <fo:table-cell>
				            	<fo:block  text-align="left" font-size="12pt" white-space-collapse="false">${invoiceItems.invoiceType}</fo:block>  
				            </fo:table-cell>
				             <fo:table-cell>
				            	<fo:block  text-align="left" font-size="12pt" white-space-collapse="false">${invoiceItems.invoiceItemTypeId}</fo:block>  
				            </fo:table-cell>
				            <fo:table-cell>
				            	<fo:block  text-align="left" font-size="12pt" white-space-collapse="false">${invoiceItems.description}</fo:block>  
				            </fo:table-cell>
				            <fo:table-cell>
				            	<fo:block  text-align="right" font-size="12pt" white-space-collapse="false">${invoiceItems.glAccountId}</fo:block>  
				            </fo:table-cell>
				            
                        </fo:table-row>
                         </#list>
                    </fo:table-body>
                </fo:table>
              </fo:block> 		
			</fo:flow>
		</fo:page-sequence>
		</#if>
		
			<#if !(invoiceItemList?has_content) >
			<fo:page-sequence master-reference="main">
				<fo:flow flow-name="xsl-region-body" font-family="Helvetica">
			   		 <fo:block font-size="14pt">
			        	${uiLabelMap.NoOrdersFound}
			   		 </fo:block>
				</fo:flow>
			</fo:page-sequence>	
		</#if>   
 </#if>
 </fo:root>
</#escape>
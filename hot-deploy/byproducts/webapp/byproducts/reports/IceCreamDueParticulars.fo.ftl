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
            <fo:simple-page-master master-name="main" page-height="12in" page-width="10in"  margin-left=".3in" margin-right=".3in" margin-top=".5in">
                <fo:region-body margin-top="1.8in"/>
                <fo:region-before extent="1in"/>
                <fo:region-after extent="1in"/>
            </fo:simple-page-master>
        </fo:layout-master-set>
        ${setRequestAttribute("OUTPUT_FILENAME", "ICPSundryDebitorReport.pdf")}
        <#if errorMessage?has_content>
	<fo:page-sequence master-reference="main">
	<fo:flow flow-name="xsl-region-body" font-family="Helvetica">
	   <fo:block font-size="14pt">
	           ${errorMessage}.
        </fo:block>
	</fo:flow>
	</fo:page-sequence>	
	<#else>
       <#if partyLedgerList?has_content>
	        <fo:page-sequence master-reference="main" font-size="12pt">	
	        	<fo:static-content flow-name="xsl-region-before" font-family="Courier,monospace">
	        		<fo:block text-align="center" font-weight="bold" keep-together="always"  font-family="Courier,monospace" white-space-collapse="false">${uiLabelMap.KMFDairyHeader}</fo:block>
	        		<fo:block  keep-together="always" text-align="center" font-family="Courier,monospace" white-space-collapse="false" font-weight="bold">${uiLabelMap.KMFDairySubHeader}</fo:block>
                	<fo:block text-align="center"  keep-together="always"  white-space-collapse="false" font-weight="bold">STATEMENT FOR SUNDRY DEBITOR - ${categoryType} ICE CREAM SALES</fo:block>
          			<fo:block text-align="center" font-weight="bold"  keep-together="always"  white-space-collapse="false">FOR THE PERIOD- ${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(fromDateTime, "dd/MMM/yyyy")} - ${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(thruDateTime, "dd/MMM/yyyy")} </fo:block>
          			<fo:block text-align="left" font-size="10pt" keep-together="always"  font-family="Courier,monospace" white-space-collapse="false"> UserLogin:<#if userLogin?exists>${userLogin.userLoginId?if_exists}</#if>               &#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;Print Date :${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(nowTimestamp, "dd/MM/yy HH:mm:ss")}</fo:block>
          			<fo:block>----------------------------------------------------------------------------------------------------------------------------------</fo:block>
            	    <fo:block text-align="left" font-weight="bold" font-size="11pt" keep-together="always" font-family="Courier,monospace" white-space-collapse="false">PARTY        PARTY                 OPENING BALANCE          DURING THE PERIOD        CLOSING BALANCE</fo:block>
        			<fo:block text-align="left" font-weight="bold" font-size="11pt" keep-together="always" font-family="Courier,monospace" white-space-collapse="false">CODE         NAME                  DEBIT        CREDIT      DEBIT       CREDIT      DEBIT       CREDIT</fo:block>
	        		<fo:block>----------------------------------------------------------------------------------------------------------------------------------</fo:block>
            	</fo:static-content>	        	
	        	<fo:flow flow-name="xsl-region-body"   font-family="Courier,monospace">		
        			<fo:block>
        				<fo:table>
		                    <fo:table-column column-width="50pt"/>
		                    <fo:table-column column-width="150pt"/>
		                    <fo:table-column column-width="80pt"/> 
		               	    <fo:table-column column-width="80pt"/>
		            		<fo:table-column column-width="80pt"/> 		
		            		<fo:table-column column-width="80pt"/>
		            		<fo:table-column column-width="80pt"/>
		            		<fo:table-column column-width="80pt"/>
		                    <fo:table-body>
		                    <#assign obCr=0> 
		                    <#assign obDr=0> 
		                    <#assign cbDr=0>
		                    <#assign cbCr=0>
		                    <#assign saleCr=0>
		                    <#assign saleDr=0>
       						<#list partyLedgerList as eachPartyData>
       							<#assign obCr = obCr+ eachPartyData.get('obCredit')> 
       							<#assign obDr = obDr+ eachPartyData.get('obDebit')> 
       							<#assign cbDr = cbDr+ eachPartyData.get('cbDebit')> 
       							<#assign cbCr = cbCr+ eachPartyData.get('cbCredit')> 
       							<#assign saleCr = saleCr+ eachPartyData.get('saleCredit')> 
       							<#assign saleDr = saleDr+ eachPartyData.get('saleDebit')> 
   								<fo:table-row>
				                    <fo:table-cell>
						            	<fo:block  keep-together="always" text-align="left" font-size="11pt" white-space-collapse="false">${eachPartyData.get('partyCode')?if_exists}</fo:block>  
						            </fo:table-cell>
						             <fo:table-cell>
						            	<fo:block text-align="left" font-size="11pt" white-space-collapse="false" wrap-option="wrap">${eachPartyData.get('partyName')?if_exists}</fo:block>  
						            </fo:table-cell>
						             <fo:table-cell>
						            	<fo:block  keep-together="always" text-align="right" font-size="11pt" white-space-collapse="false">${eachPartyData.get('obDebit')?if_exists?string("#0.00")}</fo:block>  
						            </fo:table-cell>
						             <fo:table-cell>
						            	<fo:block  keep-together="always" text-align="right" font-size="11pt" white-space-collapse="false">${eachPartyData.get('obCredit')?if_exists?string("#0.00")}</fo:block>  
						            </fo:table-cell>
						            <fo:table-cell>
						            	<fo:block  keep-together="always" text-align="right" font-size="11pt" white-space-collapse="false">${eachPartyData.get('saleDebit')?if_exists?string("#0.00")}</fo:block>  
						            </fo:table-cell>
						            <fo:table-cell>
						            	<fo:block  keep-together="always" text-align="right" font-size="11pt" white-space-collapse="false">${eachPartyData.get('saleCredit')?if_exists?string("#0.00")}</fo:block>  
						            </fo:table-cell>
						            <fo:table-cell>
						            	<fo:block  keep-together="always" text-align="right" font-size="11pt" white-space-collapse="false">${eachPartyData.get('cbDebit')?if_exists?string("#0.00")}</fo:block>  
						            </fo:table-cell>
						            <fo:table-cell>
						            	<fo:block  keep-together="always" text-align="right" font-size="11pt" white-space-collapse="false">${eachPartyData.get('cbCredit')?if_exists?string("#0.00")}</fo:block>  
						            </fo:table-cell>
						     	</fo:table-row>
							</#list>
							<fo:table-row> 
								<fo:table-cell>   						
									<fo:block>----------------------------------------------------------------------------------------------------------------------------------</fo:block>
          						  </fo:table-cell>
          					</fo:table-row> 
							<fo:table-row>
					        	<fo:table-cell>
							    	<fo:block  keep-together="always" text-align="left" font-size="11pt" white-space-collapse="false" font-weight="bold"></fo:block>  
							    </fo:table-cell>
					             <fo:table-cell>
					            	<fo:block  keep-together="always" text-align="left" font-size="11pt" white-space-collapse="false" font-weight="bold">TOTAL</fo:block>  
					            </fo:table-cell>
					             <fo:table-cell>
					            	<fo:block  keep-together="always" text-align="right" font-size="11pt" white-space-collapse="false" font-weight="bold">${obDr?if_exists?string("#0.00")}</fo:block>  
					            </fo:table-cell>
					             <fo:table-cell>
					            	<fo:block  keep-together="always" text-align="right" font-size="11pt" white-space-collapse="false" font-weight="bold">${obCr?if_exists?string("#0.00")}</fo:block>  
					            </fo:table-cell>
					            <fo:table-cell>
					            	<fo:block  keep-together="always" text-align="right" font-size="11pt" white-space-collapse="false" font-weight="bold">${saleDr?if_exists?string("#0.00")}</fo:block>  
					            </fo:table-cell>
					            <fo:table-cell>
					            	<fo:block  keep-together="always" text-align="right" font-size="11pt" white-space-collapse="false" font-weight="bold">${saleCr?if_exists?string("#0.00")}</fo:block>  
					            </fo:table-cell>
					            <fo:table-cell>
					            	<fo:block  keep-together="always" text-align="right" font-size="11pt" white-space-collapse="false" font-weight="bold">${cbDr?if_exists?string("#0.00")}</fo:block>  
					            </fo:table-cell>
					            <fo:table-cell>
					            	<fo:block  keep-together="always" text-align="right" font-size="11pt" white-space-collapse="false" font-weight="bold">${cbCr?if_exists?string("#0.00")}</fo:block>  
					            </fo:table-cell>
							     </fo:table-row>
								<fo:table-row> 
							    	<fo:table-cell>   						
										<fo:block>----------------------------------------------------------------------------------------------------------------------------------</fo:block>
          						  	</fo:table-cell>
          						</fo:table-row> 
								<fo:table-row> 
							    	<fo:table-cell number-columns-spanned="8" >   						
							 	    	<fo:block linefeed-treatment="preserve">&#xA;</fo:block>  
							 	   	</fo:table-cell>
								 </fo:table-row>	
								<fo:table-row> 
							      <fo:table-cell number-columns-spanned="8" >   						
							 	      <fo:block linefeed-treatment="preserve">&#xA;</fo:block>  
							 	   </fo:table-cell>
							 	</fo:table-row>	
								<fo:table-row> 
							    	<fo:table-cell number-columns-spanned="8" >   						
							 	    	<fo:block linefeed-treatment="preserve">&#xA;</fo:block>  
							 	   	</fo:table-cell>
							 </fo:table-row>
							 <fo:table-row> 
							    	<fo:table-cell number-columns-spanned="8" >   						
							 	    	<fo:block linefeed-treatment="preserve">&#xA;</fo:block>  
							 	   	</fo:table-cell>
							 </fo:table-row>	
							<fo:table-row> 
							      <fo:table-cell number-columns-spanned="2" >   						
							 	         <fo:block text-align="left" white-space-collapse="false" font-size="11pt" keep-together="always" font-weight="bold">Verifed By</fo:block>
							 	   </fo:table-cell>
						 	         <fo:table-cell number-columns-spanned="2">   						
							 	         <fo:block text-align="left" white-space-collapse="false" font-size="11pt"  keep-together="always" font-weight="bold">AM(F)/DM(F)</fo:block>
							 	   </fo:table-cell>
							 	   <fo:table-cell number-columns-spanned="2">   						
							 	         <fo:block text-align="left" white-space-collapse="false" font-size="11pt"  keep-together="always" font-weight="bold">M(F)/GM(F)</fo:block>
							 	   </fo:table-cell>
						 	         <fo:table-cell number-columns-spanned="2">   						
							 	         <fo:block text-align="left" white-space-collapse="false" font-size="11pt" keep-together="always" font-weight="bold">Pre-Audit</fo:block>
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
 </#if>
 </fo:root>
</#escape>
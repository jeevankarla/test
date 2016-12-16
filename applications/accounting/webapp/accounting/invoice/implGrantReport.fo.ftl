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
	<fo:simple-page-master master-name="main" page-height="12in" page-width="13.0in"
            margin-top="0.5in" margin-bottom="0.5in" margin-left="1.0in" margin-right="0.5in">
        <fo:region-body margin-top="0.5in"/>
        <fo:region-before extent="1.in"/>
        <fo:region-after extent="1.5in"/>        
    </fo:simple-page-master>   
</fo:layout-master-set>
		<#if finAccountTransMap?has_content>
   <#assign partyDetails = delegator.findOne("PartyAndGroup", {"partyId" : "Company"}, false)>                    			                                                
			<fo:page-sequence master-reference="main">
			    <fo:static-content font-size="13pt" font-family="Courier,monospace"  flow-name="xsl-region-before" font-weight="bold">
	                <fo:block text-align="right" keep-together="always" white-space-collapse="false" >Page Number: <fo:page-number/></fo:block>                    			    				  
				    </fo:static-content>	
				    <fo:flow flow-name="xsl-region-body"   font-family="Courier,monospace">	
				    <#assign reportHeader = delegator.findOne("TenantConfiguration", {"propertyTypeEnumId" : "COMPANY_HEADER","propertyName" : "reportHeaderLable"}, true)>
					<#assign reportSubHeader = delegator.findOne("TenantConfiguration", {"propertyTypeEnumId" : "COMPANY_HEADER","propertyName" : "reportSubHeaderLable"}, true)>
	      		    <#assign reportSecSubHeader = delegator.findOne("TenantConfiguration", {"propertyTypeEnumId" : "COMPANY_HEADER","propertyName" : "reportSubHeaderLable_01"}, true)>
				    <fo:table>
				        <fo:table-column column-width="20%"/>
				        <fo:table-column column-width="60%"/>
				        <fo:table-column column-width="10%"/>
				           <fo:table-body>
				              <fo:table-row>
				                 <fo:table-cell>
				                    <fo:block-container >
                                        <fo:block text-align="left" font-size="13pt"></fo:block>
                                    </fo:block-container>
				                 </fo:table-cell>
				                 <fo:table-cell>
				                    <fo:block text-align="center" font-size="13pt" keep-together="always"  white-space-collapse="false" font-weight="bold">&#160;${reportHeader.description?if_exists}</fo:block>
					                <fo:block text-align="center" font-size="12pt" keep-together="always"  white-space-collapse="false" font-weight="bold">&#160;${reportSubHeader.description?if_exists}</fo:block>
				                    <fo:block text-align="center" font-size="10pt" keep-together="always"  white-space-collapse="false" font-weight="bold">&#160;&#160;&#160;&#160;&#160;&#160;${reportSecSubHeader.description?if_exists}</fo:block>
			                        <fo:block text-align="center" font-size="13pt" keep-together="always"  white-space-collapse="false" font-weight="bold">&#160;</fo:block>
			                        <fo:block text-align="center" font-size="12pt" keep-together="always"  white-space-collapse="false" font-weight="bold">&#160;&#160;&#160;&#160;&#160;&#160;Utilization Report for ${finAccountName}</fo:block>
			                     
			                     </fo:table-cell>
			                     <fo:table-cell>
			                         <fo:block text-align="center" font-size="13pt" keep-together="always"  white-space-collapse="false" font-weight="bold">&#160;</fo:block>
									<fo:block text-align="center" font-size="13pt" keep-together="always"  white-space-collapse="false" font-weight="bold">&#160;</fo:block>
									<fo:block text-align="center" font-size="13pt" keep-together="always"  white-space-collapse="false" font-weight="bold">&#160;</fo:block>
			                         <fo:block  keep-together="always" text-align="center" font-size="10pt" font-family="Courier,monospace" white-space-collapse="false"  >&#160;&#160;&#160;&#160;&#160;&#160;UserLogin:<#if userLogin?exists>${userLogin.userLoginId?if_exists}</#if></fo:block>
				                      <fo:block  keep-together="always" text-align="center" font-size="10pt" font-family="Courier,monospace" white-space-collapse="false">&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;Date:${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(nowTimestamp, "dd/MM/yy HH:mm:ss")}</fo:block>
				                 </fo:table-cell>
			                   </fo:table-row>
			                </fo:table-body> 
			             </fo:table>
				    
				    <fo:block text-align="left" keep-together="always"  >-------------------------------------------------------------------------------------------------------------------</fo:block>
			        <fo:block linefeed-treatment="preserve">&#xA;</fo:block>
	                <fo:block font-family="Courier,monospace">
	                <fo:table>
					
					
					<fo:table-column column-width="150pt"/>
					<fo:table-column column-width="100pt"/>
					<fo:table-column column-width="80pt"/>
					<fo:table-column column-width="240pt"/>
					<fo:table-column column-width="80pt"/>
					     <fo:table-body>
					         <fo:table-row >
							   <fo:table-cell >
				                  <fo:block text-align="left" keep-together="always"  >-------------------------------------------------------------------------------------------------------------------</fo:block>
								</fo:table-cell>
						   </fo:table-row>
					         <fo:table-row>
					            
								<fo:table-cell >
									<fo:block text-align="left"  font-weight="bold">Invoice Date</fo:block>
								</fo:table-cell>
								
								<fo:table-cell >
									<fo:block text-align="left" font-weight="bold" >Invoice Id</fo:block>
								</fo:table-cell>									
								<fo:table-cell >
									<fo:block text-align="center" keep-together="always" font-weight="bold">Amount</fo:block>
								</fo:table-cell>
							<fo:table-cell >
									<fo:block text-align="center" keep-together="always" font-weight="bold">Invoice Item Description</fo:block>
								</fo:table-cell>	
								<fo:table-cell >
									<fo:block text-align="center" keep-together="always" font-weight="bold">Narration</fo:block>
								</fo:table-cell>					
					    </fo:table-row>
					    <fo:table-row >
							<fo:table-cell >
							<fo:block text-align="left" keep-together="always"  >-------------------------------------------------------------------------------------------------------------------</fo:block></fo:table-cell>
						</fo:table-row>
					 </fo:table-body> 
			       </fo:table>
			       </fo:block>		             	   			 
	               <fo:block font-family="Courier,monospace" font-size="10pt">
	                   <fo:table>
					
					<fo:table-column column-width="150pt"/>
					<fo:table-column column-width="80pt"/>
					<fo:table-column column-width="80pt"/>
					<fo:table-column column-width="300pt"/>
					<fo:table-column column-width="240pt"/>
					          <fo:table-body>
					          
                              <#if finAccountTransMap?has_content>
					          	
				              <#assign FinList = finAccountTransMap.entrySet()>  
				                        					          
                               <#list FinList as finaccountEntry>
                               <#assign finaccountId=finaccountEntry.getKey()> 
                               <#assign FinList2=finaccountEntry.getValue()>
                                
                                 <#list FinList2 as finaccountEntry2>
                                 
				                  <fo:table-row >
				                   
					                 
								      <fo:table-cell >
								      
									    <fo:block text-align="left"  font-size="12pt">${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(finaccountEntry2.get("invoiceDate")?if_exists, "dd-MM-yyyy")}</fo:block>
									      
								     </fo:table-cell>	
								    
								     <fo:table-cell >
								   
									   <fo:block text-align="center"  font-size="12pt">${finaccountEntry2.get("invoiceId")?if_exists}</fo:block>
									  
									   </fo:table-cell>
								      <fo:table-cell >
											<fo:block text-align="left" >
														 <fo:table>
														  <fo:table-column column-width="80pt"/>
									                      <fo:table-body>
									                      <#assign amountList=finaccountEntry2.get("amount")>
                                 						  <#list amountList as amount>
														   <fo:table-row>
													           <fo:table-cell>
																	 <fo:block text-align="right" font-size="12pt" >${amount?if_exists}</fo:block>  
															   </fo:table-cell>
									                       </fo:table-row>
									                       </#list>
														</fo:table-body>   
													</fo:table>	
									       </fo:block>
									   </fo:table-cell>
									   
								     <fo:table-cell >
											<fo:block text-align="left" >
														 <fo:table>
														  <fo:table-column column-width="250pt"/>
									                      <fo:table-body>
									                      <#assign descriptionList=finaccountEntry2.get("description")>
                                 						  <#list descriptionList as desc>
														   <fo:table-row>
													           <fo:table-cell>
																	 <fo:block text-align="right" font-size="12pt" >${desc?if_exists}</fo:block>  
															   </fo:table-cell>
									                       </fo:table-row>
									                       </#list>
														</fo:table-body>   
													</fo:table>	
									       </fo:block>
									   </fo:table-cell>
								    <fo:table-cell>
									   
									   <fo:block text-align="left"  font-size="12pt">${finaccountEntry2.get("narration")?if_exists}</fo:block>
									  
								    </fo:table-cell>
								     
					            </fo:table-row>
					            </#list> 
					            </#list>
					            </#if>   
					        
					          
                              
                             
                           
					      </fo:table-body>  
					    </fo:table>
				   <fo:block text-align="left" keep-together="always"  >------------------------------------------------------------------------------------------------------------------------------------------</fo:block>
				   </fo:block>
                    <fo:block linefeed-treatment="preserve">&#xA;</fo:block>
                   <fo:block linefeed-treatment="preserve">&#xA;</fo:block>
                   <fo:block linefeed-treatment="preserve">&#xA;</fo:block>
	               <fo:block linefeed-treatment="preserve">&#xA;</fo:block>
	           </fo:flow> 	          
	      </fo:page-sequence>
         <#else>
           <fo:page-sequence master-reference="main">
	    			<fo:flow flow-name="xsl-region-body" font-family="Courier,monospace">
	       		 		<fo:block font-size="14pt" text-align="center">
	            			 No Records Found....!
	       		 		</fo:block>
	    			</fo:flow>
		  </fo:page-sequence>				
	    </#if>  
     </fo:root>
</#escape>	    
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
            <fo:simple-page-master master-name="main" page-height="12in" page-width="15in"
                      margin-right=".5in">
                <fo:region-body margin-top=".6in"/>
                <fo:region-before extent="1in"/>
                <fo:region-after extent="1in"/>
            </fo:simple-page-master>
        </fo:layout-master-set>
        ${setRequestAttribute("OUTPUT_FILENAME", "BankAbst.txt")}
        <#if bankAbstract?has_content>        
        <fo:page-sequence master-reference="main">
        	<fo:static-content flow-name="xsl-region-before">
        	<fo:block font-size="7pt" linefeed-treatment="preserve" >&#xA;</fo:block>
        	<#assign reportHeader = delegator.findOne("TenantConfiguration", {"propertyTypeEnumId" : "MILK_PROCUREMENT","propertyName" : "reportHeaderLable"}, true)>
    		<fo:block font-size="7pt" text-align="left" white-space-collapse="false" font-weight="bold">.                                                               ${reportHeader.description?if_exists}.</fo:block> 	 	  
        	<fo:block font-size="7pt" text-align="left" white-space-collapse="false" font-weight="bold">.                                                                   B  A  N  K        W  I  S  E        A  B  S  T  R  A  C  T                  ${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(fromDateTime, "dd/MM/yyyy")} TO ${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(thruDateTime, "dd/MM/yyyy")}                  </fo:block>
        	<fo:block font-size="7pt" linefeed-treatment="preserve">&#xA;</fo:block>
        	<fo:block font-size="7pt" text-align="left" white-space-collapse="false" font-weight="bold">UNIT CODE  AND  NAME : ${unitCode?if_exists}   ${unitName?if_exists}                </fo:block>
        	</fo:static-content>
        	<fo:flow flow-name="xsl-region-body" font-family="Courier,monospace">   		           
                 <fo:block font-size="7pt">
                 	<fo:table border-width="1pt">
                    <fo:table-column column-width="28pt"/>
                    <fo:table-column column-width="28pt"/>  
               	    <fo:table-column column-width="140pt"/>
               	    <fo:table-column column-width="90pt"/>
                    <fo:table-column column-width="95pt"/>  
                    <fo:table-column column-width="95pt"/>
                    <fo:table-column column-width="95pt"/>
                    <fo:table-column column-width="90pt"/>  
                    <fo:table-column column-width="6pt"/>
		          	<fo:table-header>
		            	<fo:table-cell>
		            		<fo:block font-size="7pt" text-align="left" white-space-collapse="false" >-------------------------------------------------------------------------------------------------------------------------------------------------------------------</fo:block>
			            	<fo:block font-size="7pt" text-align="left" white-space-collapse="false" keep-together="always">BANK  BRCH   NAME OF THE BANK                  NAME OF THE BRANCH                 GROSS                 DEDUCTION                NET                  RND-NET   </fo:block>                 
			            	<fo:block font-size="7pt" text-align="left" white-space-collapse="false" keep-together="always">CODE  CODE                                                                        AMOUNT                 AMOUNT                AMOUNT                 AMOUNT</fo:block>
			            	<fo:block font-size="7pt" text-align="left" white-space-collapse="false" >-------------------------------------------------------------------------------------------------------------------------------------------------------------------</fo:block>
		            	</fo:table-cell>		                    	                  
				    </fo:table-header>		           
                    <fo:table-body>
            				<fo:table-row>	
	                            <fo:table-cell >	
	                            	<fo:block font-size="7pt" linefeed-treatment="preserve">&#xA;</fo:block>                              
	                            </fo:table-cell>
	                             <fo:table-cell >	
	                            	<fo:block font-size="7pt" linefeed-treatment="preserve">&#xA;</fo:block>                               
	                            </fo:table-cell>
	                            <fo:table-cell >	
	                            	<fo:block font-size="7pt" text-align="left">${bankAbstract.get("nameOfTheBank")?if_exists}</fo:block>                               
	                            </fo:table-cell>
	                            <fo:table-cell >	
	                            	<fo:block font-size="7pt" text-align="left">${bankAbstract.get("nameOfTheBrch")?if_exists}</fo:block>                               
	                            </fo:table-cell>
	                            <fo:table-cell >	
	                            	<fo:block font-size="7pt" text-align="right"><#if bankAbstract.get("grossAmount")?has_content>${bankAbstract.get("grossAmount")?if_exists?string("##0.00")}<#else>0.00</#if></fo:block>                               
	                            </fo:table-cell>
	                            <fo:table-cell >	
	                            	<fo:block font-size="7pt" text-align="right"><#if bankAbstract.get("dedAmount")?has_content>${bankAbstract.get("dedAmount")?if_exists?string("##0.00")}<#else>0.00</#if></fo:block>                               
	                            </fo:table-cell>
	                            <fo:table-cell >	
	                            	<fo:block font-size="7pt" text-align="right"><#if bankAbstract.get("netAmount")?has_content>${bankAbstract.get("netAmount")?if_exists?string("##0.00")}<#else>0.00</#if></fo:block>                               
	                            </fo:table-cell>
	                            <fo:table-cell >	
	                            	<fo:block font-size="7pt" text-align="right"><#if bankAbstract.get("netRndAmount")?has_content>${bankAbstract.get("netRndAmount")?if_exists?string("##0.00")}<#else>0.00</#if></fo:block>                               
	                            </fo:table-cell>
				            </fo:table-row>	
				            <fo:table-row>	
	                            <fo:table-cell >	
	                            	<fo:block font-size="7pt" linefeed-treatment="preserve">&#xA;</fo:block>                              
	                            </fo:table-cell>
	                             <fo:table-cell >	
	                            	<fo:block font-size="7pt" linefeed-treatment="preserve">&#xA;</fo:block>                               
	                            </fo:table-cell>
	                            <fo:table-cell >	
	                            	<fo:block font-size="7pt" text-align="left">${bankAbstract.get("nameOfTheBank")?if_exists}</fo:block>                               
	                            </fo:table-cell>
	                            <fo:table-cell >	
	                            	<fo:block font-size="7pt" text-align="left">TOTAL:</fo:block>                               
	                            </fo:table-cell>
	                            <fo:table-cell >	
	                            	<fo:block font-size="7pt" text-align="right"><#if bankAbstract.get("grossAmount")?has_content>${bankAbstract.get("grossAmount")?if_exists?string("##0.00")}<#else>0.00</#if></fo:block>                               
	                            </fo:table-cell>
	                            <fo:table-cell >	
	                            	<fo:block font-size="7pt" text-align="right"><#if bankAbstract.get("dedAmount")?has_content>${bankAbstract.get("dedAmount")?if_exists?string("##0.00")}<#else>0.00</#if></fo:block>                               
	                            </fo:table-cell>
	                            <fo:table-cell >	
	                            	<fo:block font-size="7pt" text-align="right"><#if bankAbstract.get("netAmount")?has_content>${bankAbstract.get("netAmount")?if_exists?string("##0.00")}<#else>0.00</#if></fo:block>                               
	                            </fo:table-cell>
	                            <fo:table-cell >	
	                            	<fo:block font-size="7pt" text-align="right"><#if bankAbstract.get("netRndAmount")?has_content>${bankAbstract.get("netRndAmount")?if_exists?string("##0.00")}<#else>0.00</#if></fo:block>                               
	                            </fo:table-cell>
	                            <fo:table-cell >	
	                            	<fo:block font-size="7pt" text-align="left">*</fo:block>                               
	                            </fo:table-cell>
				            </fo:table-row>	
				            <fo:table-row>	
	                            <fo:table-cell >	
	                            	<fo:block font-size="7pt" text-align="left" white-space-collapse="false" >-------------------------------------------------------------------------------------------------------------------------------------------------------------------</fo:block>                               
	                            </fo:table-cell>
				            </fo:table-row>	
				            <fo:table-row>	
	                            <fo:table-cell >	
	                            	<fo:block font-size="7pt" linefeed-treatment="preserve">&#xA;</fo:block>                              
	                            </fo:table-cell>
	                             <fo:table-cell >	
	                            	<fo:block font-size="7pt" linefeed-treatment="preserve">&#xA;</fo:block>                               
	                            </fo:table-cell>
	                            <fo:table-cell >	
	                            	<fo:block font-size="7pt" text-align="left">${unitName?if_exists}</fo:block>                               
	                            </fo:table-cell>
	                            <fo:table-cell >	
	                            	<fo:block font-size="7pt" text-align="left">TOTAL:</fo:block>                               
	                            </fo:table-cell>
	                            <fo:table-cell >	
	                            	<fo:block font-size="7pt" text-align="right"><#if bankAbstract.get("grossAmount")?has_content>${bankAbstract.get("grossAmount")?if_exists?string("##0.00")}<#else>0.00</#if></fo:block>                               
	                            </fo:table-cell>
	                            <fo:table-cell >	
	                            	<fo:block font-size="7pt" text-align="right"><#if bankAbstract.get("dedAmount")?has_content>${bankAbstract.get("dedAmount")?if_exists?string("##0.00")}<#else>0.00</#if></fo:block>                               
	                            </fo:table-cell>
	                            <fo:table-cell >	
	                            	<fo:block font-size="7pt" text-align="right"><#if bankAbstract.get("netAmount")?has_content>${bankAbstract.get("netAmount")?if_exists?string("##0.00")}<#else>0.00</#if></fo:block>                               
	                            </fo:table-cell>
	                            <fo:table-cell >	
	                            	<fo:block font-size="7pt" text-align="right"><#if bankAbstract.get("netRndAmount")?has_content>${bankAbstract.get("netRndAmount")?if_exists?string("##0.00")}<#else>0.00</#if></fo:block>                               
	                            </fo:table-cell>
				            </fo:table-row>
				            <fo:table-row>	
	                            <fo:table-cell >	
	                            	<fo:block font-size="7pt" text-align="left" white-space-collapse="false" >-------------------------------------------------------------------------------------------------------------------------------------------------------------------</fo:block>                               
	                            </fo:table-cell>
				            </fo:table-row>	
                    </fo:table-body>
                </fo:table>
               </fo:block>      
           </fo:flow>
        </fo:page-sequence>
        <#else>
				<fo:page-sequence master-reference="main">
	    			<fo:flow flow-name="xsl-region-body" font-family="Courier,monospace">
	       		 		<fo:block font-size="14pt">
	       		 			<#if unitCode?has_content>
	            				No Financial Account For the Selected Unit.......!
	            			<#else>
	            				No Unit Has Been Selected.......!
	            			</#if>
	       		 		</fo:block>
	    			</fo:flow>
				</fo:page-sequence>
		</#if> 
     </fo:root>
</#escape>
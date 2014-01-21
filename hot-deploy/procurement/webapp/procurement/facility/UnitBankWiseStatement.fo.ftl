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
        <fo:layout-master-set >
            <fo:simple-page-master master-name="main" page-height="12in" page-width="15in">
                <fo:region-body margin-top="1.5in"/>
                <fo:region-before extent="1in"/>
                <fo:region-after extent="1in"/>
            </fo:simple-page-master>
        </fo:layout-master-set>
        <#if unitBankWiseAbstract?has_content>   
        ${setRequestAttribute("OUTPUT_FILENAME", "unitBankWiseStatement.txt")}
        <fo:page-sequence master-reference="main">
        <!--<fo:page-sequence master-reference="main" print = "LPRINT CHR$(27);">-->
        	<fo:static-content flow-name="xsl-region-before">
        		<#assign pageNumber = 1>
        	 	<#assign facility = delegator.findOne("Facility", {"facilityId" : parameters.shedId}, true)>
        	 	<#assign reportHeader = delegator.findOne("TenantConfiguration", {"propertyTypeEnumId" : "MILK_PROCUREMENT","propertyName" : "reportHeaderLable"}, true)>
        	 	<fo:block text-align="left" white-space-collapse="false" font-size="9pt" font-weight="bold">&#160;                 										   		${reportHeader.description?if_exists}</fo:block>
        	 	<fo:block text-align="left" white-space-collapse="false" font-size="9pt" font-weight="bold">&#160;                 											STATEMENT SHOWING THE MILK BILL PAYMENT  OF  UNIT-WISE , BANK-WISE  ABSTRACT</fo:block>
        		<fo:block linefeed-treatment="preserve">&#xA;</fo:block>
        		<fo:block text-align="left" white-space-collapse="false" font-size="9pt" font-weight="bold">    NAME OF THE SHED  :   ${facility.get("facilityName")?if_exists}       PERIOD FROM    :   ${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(fromDate, "dd/MM/yyyy")}  TO   ${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(thruDate, "dd/MM/yyyy")}           PAGE NO  :  ${pageNumber}     </fo:block>
        		<fo:block text-align="left" keep-together="always" white-space-collapse="false">-------------------------------------------------------------------------------------------------------------------------------------------------------------------</fo:block>	 	 	  
        		<fo:block text-align="left" white-space-collapse="false" font-size="9pt" font-weight="bold">CODE      NAME OF UNIT         NAME OF THE BANK    NAME OF THE BRANCH      IFSCNO          ACCOUNT HOLDER             BANK A/C                   AMOUNT</fo:block>
        		<fo:block text-align="left" keep-together="always" white-space-collapse="false">-------------------------------------------------------------------------------------------------------------------------------------------------------------------</fo:block>
        	</fo:static-content>
        	<fo:flow flow-name="xsl-region-body" font-family="Helvetica">   		           
                 <fo:block>
                 	<fo:table >
                    <fo:table-column column-width="40pt"/>
                    <fo:table-column column-width="90pt"/>
                    <fo:table-column column-width="100pt"/>
                    <fo:table-column column-width="120pt"/>
                    <fo:table-column column-width="60pt"/>
                    <fo:table-column column-width="120pt"/>
                    <fo:table-column column-width="60pt"/> 
                    <fo:table-column column-width="40pt"/>  
                    <fo:table-body>
				        <#assign prevBank = "newBank">
				     <#list unitBankWiseAbstract as unitBankTot>
				        <#assign currentbank = unitBankTot.get("nameOfTheBank")>
				      <#if (prevBank != "newBank")&&(prevBank != currentbank)>
				        <fo:table-row>
                   	 		<fo:table-cell>
	                            <fo:block text-align="left" keep-together="always" white-space-collapse="false">-------------------------------------------------------------------------------------------------------------------------------------------------------------------</fo:block>
	                           	 <#if ((prevBank == ddAmountMap.get("nameOfTheBank")))>
	                           	 <fo:block text-align="left" font-size="9pt" font-weight="bold" keep-together="always" white-space-collapse="false">TOTAL  :																																																																																																																																																																																																																	                     ${(bankWiseTotalAmountMap.get(prevBank)+ ddAmountMap.get("amount"))?if_exists?string("##0")}</fo:block>
	                           	 <#else>
	                           	 	<fo:block text-align="left" font-size="9pt" font-weight="bold" keep-together="always" white-space-collapse="false">TOTAL  :																																																																																																																																																																																																																		                     ${(bankWiseTotalAmountMap.get(prevBank))?if_exists?string("##0")} </fo:block>
	                           	 </#if>
	                           	
	                            <fo:block text-align="left" keep-together="always" white-space-collapse="false">-------------------------------------------------------------------------------------------------------------------------------------------------------------------</fo:block> 
	                        	<fo:block linefeed-treatment="preserve">&#xA;</fo:block>
	                        	<fo:block linefeed-treatment="preserve">&#xA;</fo:block>   
	                        	<fo:block text-align="left" font-size="9pt" keep-together="always" white-space-collapse="false">&#160;                                                                                                                               DEPUTY DIRECTOR / GENERAL MANAGER</fo:block>
	                        	<fo:block font-size="10pt" break-before="page"/>
	                        </fo:table-cell>
				        </fo:table-row>
				        </#if>
				        <fo:table-row>
                   	 		<fo:table-cell>
	                            	<fo:block linefeed-treatment="preserve" font-size="4pt">&#xA;</fo:block>
	                        </fo:table-cell>
	                    </fo:table-row>    
                   	 	<fo:table-row>
                   	 		<fo:table-cell>
	                            	<fo:block text-align="left" font-size="4pt" >${unitBankTot.get("unitCode")?if_exists}</fo:block>
	                        </fo:table-cell>
                   	     	<fo:table-cell>
	                            	<fo:block text-align="left" font-size="4pt" >${unitBankTot.get("nameOfUnit")?if_exists}</fo:block>
	                        </fo:table-cell>
	                        <fo:table-cell>
	                            	<fo:block text-align="left" font-size="4pt" >${unitBankTot.get("nameOfTheBank")?if_exists}</fo:block>
	                        </fo:table-cell>
	                        <fo:table-cell>
	                            	<fo:block text-align="left" font-size="4pt" >${unitBankTot.get("nameOfTheBrch")?if_exists}</fo:block>
	                        </fo:table-cell>
	                         <fo:table-cell>
	                            	<fo:block text-align="left" font-size="4pt" >${unitBankTot.get("ifscCode")?if_exists}</fo:block>
	                        </fo:table-cell>
	                        <fo:table-cell>
	                            	<fo:block text-align="left" font-size="4pt" >${unitBankTot.get("accountHolder")?if_exists}</fo:block>
	                        </fo:table-cell>
	                        <fo:table-cell>
	                            	<fo:block text-align="left" font-size="4pt" >${unitBankTot.get("bankAccNo")?if_exists}</fo:block>
	                        </fo:table-cell>
	                        <fo:table-cell>
	                            	<fo:block text-align="right" font-size="4pt" >${unitBankTot.get("amount")?if_exists?string("##0")}</fo:block>	                            	
	                        </fo:table-cell>
				        </fo:table-row>
				     <#if ((unitBankTot.get("nameOfTheBank") == ddAmountMap.get("nameOfTheBank")) && (unitBankTot.get("nameOfTheBrch") == ddAmountMap.get("nameOfTheBrch")))>   
				        <fo:table-row>
				        	<fo:table-cell>
				        		<fo:block linefeed-treatment="preserve" font-size="4pt">&#xA;</fo:block>
				        	</fo:table-cell>
				        </fo:table-row>
				        <fo:table-row>
                   	 		<fo:table-cell>
	                            	<fo:block text-align="left" font-size="4pt" >${ddAmountMap.get("unitCode")?if_exists}</fo:block>
	                        </fo:table-cell>
                   	     	<fo:table-cell>
	                            	<fo:block text-align="left" font-size="4pt" >${ddAmountMap.get("nameOfUnit")?if_exists}</fo:block>
	                        </fo:table-cell>
	                        <fo:table-cell>
	                            	<fo:block text-align="left" font-size="4pt" >${ddAmountMap.get("nameOfTheBank")?if_exists}</fo:block>
	                        </fo:table-cell>
	                        <fo:table-cell>
	                            	<fo:block text-align="left" font-size="4pt" >${ddAmountMap.get("nameOfTheBrch")?if_exists}</fo:block>
	                        </fo:table-cell>
	                         <fo:table-cell>
	                            	<fo:block text-align="left" font-size="4pt" >${ddAmountMap.get("ifscCode")?if_exists}</fo:block>
	                        </fo:table-cell>
	                        <fo:table-cell>
	                            	<fo:block text-align="left" font-size="4pt" >${ddAmountMap.get("accountHolder")?if_exists}</fo:block>
	                        </fo:table-cell>
	                        <fo:table-cell>
	                            	<fo:block text-align="left" font-size="4pt" >${ddAmountMap.get("bankAccNo")?if_exists}</fo:block>
	                        </fo:table-cell>
	                        <fo:table-cell>
	                            	<fo:block text-align="right" font-size="4pt" >${ddAmountMap.get("amount")?if_exists?string("##0")}</fo:block>	                            	
	                        </fo:table-cell>
				        </fo:table-row>
				      </#if>  
				        <#assign prevBank = currentbank>
				        </#list>
				        <fo:table-row>
                   	 		<fo:table-cell>
	                            <fo:block text-align="left" keep-together="always" white-space-collapse="false">-------------------------------------------------------------------------------------------------------------------------------------------------------------------</fo:block>
	                           	<fo:block text-align="left" font-size="9pt" font-weight="bold" keep-together="always" white-space-collapse="false">TOTAL  :																																																																																																																																																																																																																		                     ${bankWiseTotalAmountMap.get(prevBank)?if_exists?string("##0")}</fo:block>
	                            <fo:block text-align="left" keep-together="always" white-space-collapse="false">-------------------------------------------------------------------------------------------------------------------------------------------------------------------</fo:block> 
	                        	<fo:block linefeed-treatment="preserve">&#xA;</fo:block>
	                        	<fo:block linefeed-treatment="preserve">&#xA;</fo:block>   
	                        	<fo:block text-align="left" font-size="9pt" keep-together="always" white-space-collapse="false">&#160;                                                                                                                               DEPUTY DIRECTOR / GENERAL MANAGER</fo:block>
	                        </fo:table-cell>
	                        <fo:table-cell/>
	                        <fo:table-cell/>
	                        <fo:table-cell/>
	                        <fo:table-cell/>
	                        <fo:table-cell/>
	                        <fo:table-cell/>
	                        <fo:table-cell/>
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
	            			Shed Not Selected.......!
	       		 		</fo:block>
	    			</fo:flow>
				</fo:page-sequence>
		</#if> 
     </fo:root>
</#escape>
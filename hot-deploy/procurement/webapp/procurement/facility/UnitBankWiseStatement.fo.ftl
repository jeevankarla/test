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
            <fo:simple-page-master master-name="main" page-height="12in" page-width="15in" margin-left=".5in"  margin-right=".2in" margin-top=".2in" margin-bottom=".6in">
                <fo:region-body margin-top="1in"/>
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
        	 	<fo:block text-align="left" white-space-collapse="false" font-size="9pt" font-weight="bold">VST_ASCII-018&#160;                										   		${reportHeader.description?if_exists}</fo:block>
        	 	<fo:block text-align="left" white-space-collapse="false" font-size="9pt" font-weight="bold">&#160;                 											STATEMENT SHOWING THE MILK BILL PAYMENT  OF  UNIT-WISE , BANK-WISE  ABSTRACT</fo:block>
        		<fo:block linefeed-treatment="preserve">&#xA;</fo:block>
        		<fo:block text-align="left" white-space-collapse="false" font-size="8pt" >NAME OF THE SHED  : ${facility.get("facilityName").toUpperCase()?if_exists}      PERIOD FROM : ${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(fromDate, "dd/MM/yyyy")} TO ${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(thruDate, "dd/MM/yyyy")} </fo:block>
        		<fo:block text-align="left" keep-together="always" white-space-collapse="false">----------------------------------------------------------------------------------------------------------------------------------------</fo:block>	 	 	  
        		<fo:block text-align="left" white-space-collapse="false" font-size="6pt">CODE    NAME OF UNIT     BANK NAME            BRANCH NAME     IFSCNO    ACCOUNT HOLDER    BANK A/C   AMOUNT</fo:block>
        		<fo:block text-align="left" keep-together="always" white-space-collapse="false">----------------------------------------------------------------------------------------------------------------------------------------</fo:block>
        	</fo:static-content>
        	<fo:flow flow-name="xsl-region-body" font-family="Courier,monospace">   		           
                 <fo:block>
                 	<fo:table >
                    <fo:table-column column-width="20pt"/>
                    <fo:table-column column-width="55pt"/>
                    <fo:table-column column-width="60pt"/>
                    <fo:table-column column-width="40pt"/>
                    <fo:table-column column-width="35pt"/>
                    <fo:table-column column-width="60pt"/>
                    <fo:table-column column-width="20pt"/> 
                    <fo:table-column column-width="25pt"/>  
                    <fo:table-body>
                    	<#assign ddAccPrint =true>
				        <#assign prevBank = "newBank">
				     <#list unitBankWiseAbstract as unitBankTot>
				        <#assign currentbank = unitBankTot.get("nameOfTheBank")>
				      <#if (prevBank != "newBank")&&(prevBank != currentbank)>
				      		<#if bankWiseTotalAmountMap.get(prevBank) !=0>
				        	<fo:table-row>
	                   	 		<fo:table-cell>
		                            	<fo:block font-size="9pt">----------------------------------------------------------------------------------------------------------------------------------------</fo:block>
		                        </fo:table-cell>
					        </fo:table-row>
					        <fo:table-row>
	                   	 		<fo:table-cell>
		                            	<fo:block text-align="left" keep-together="always" white-space-collapse="false">&#160;</fo:block>
		                        </fo:table-cell>
	                   	     	<fo:table-cell>
		                            	<fo:block text-align="left" font-size="4pt" >TOTAL :</fo:block>
		                        </fo:table-cell>
		                        <fo:table-cell>
		                            	<fo:block text-align="left" font-size="4pt" >&#160;</fo:block>
		                        </fo:table-cell>
		                        <fo:table-cell>
		                            	<fo:block text-align="left" font-size="4pt" >&#160;</fo:block>
		                        </fo:table-cell>
		                         <fo:table-cell>
		                            	<fo:block text-align="left" font-size="4pt" >&#160;</fo:block>
		                        </fo:table-cell>
		                        <fo:table-cell>
		                            	<fo:block text-align="left" font-size="4pt" >&#160;</fo:block>
		                        </fo:table-cell>
		                        <fo:table-cell>
		                            	<fo:block text-align="left" font-size="4pt" >&#160;</fo:block>
		                        </fo:table-cell>
		                        <fo:table-cell>
		                            <#if ((prevBank == ddAmountMap.get("nameOfTheBank")))>
		                           		<fo:block text-align="right" font-size="4pt" >${(bankWiseTotalAmountMap.get(prevBank)+ ddAmountMap.get("amount"))?if_exists?string("##0")}</fo:block>
		                           	<#else>
		                           	 	<fo:block text-align="right" font-size="4pt" >${(bankWiseTotalAmountMap.get(prevBank))?if_exists?string("##0")}</fo:block>
		                           	</#if>	                            	
		                        </fo:table-cell>
					        </fo:table-row>
					        <fo:table-row>
	                   	 		<fo:table-cell>
		                            	<fo:block font-size="4pt" white-space-collapse="false">----------------------------------------------------------------------------------------------------------------------------------------</fo:block>
		                        </fo:table-cell>
					        </fo:table-row>	
					        <fo:table-row>
	                   	 		<fo:table-cell>
	                   	 				<fo:block linefeed-treatment="preserve" font-size="3pt" >&#xA;</fo:block>
	                   	 				<fo:block linefeed-treatment="preserve" font-size="3pt" >&#xA;</fo:block>
		                        		<fo:block text-align="left" font-size="9pt" keep-together="always" white-space-collapse="false">&#160;                                        DEPUTY DIRECTOR / GENERAL MANAGER</fo:block>
		                        	<fo:block break-after="page"/>
		                        </fo:table-cell>
					        </fo:table-row>
					        </#if>
				        </#if>
	                   <#if unitBankTot.get("amount") !=0>
	                   <fo:table-row>
				        	<fo:table-cell>
				        		<fo:block linefeed-treatment="preserve" font-size="3pt">&#xA;</fo:block>
				        	</fo:table-cell>
				        </fo:table-row>
                   	 	<fo:table-row>
                   	 		<fo:table-cell>
	                            	<fo:block text-align="left" font-size="4pt" >${unitBankTot.get("unitCode")?if_exists}</fo:block>
	                        </fo:table-cell>
                   	     	<fo:table-cell>
	                            	<fo:block text-align="left" font-size="4pt" >${Static["org.ofbiz.order.order.OrderServices"].nameTrim((StringUtil.wrapString(unitBankTot.get("nameOfUnit"))),20)}</fo:block>
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
	                            	<fo:block text-align="left" font-size="4pt" >${Static["org.ofbiz.order.order.OrderServices"].nameTrim((StringUtil.wrapString(unitBankTot.get("accountHolder"))),17)}</fo:block>
	                        </fo:table-cell>                                      
	                        <fo:table-cell>
	                            	<fo:block text-align="right" font-size="4pt" >${unitBankTot.get("bankAccNo")?if_exists}</fo:block>
	                        </fo:table-cell>
	                        <fo:table-cell>
	                            	<fo:block text-align="right" font-size="4pt" >${unitBankTot.get("amount")?if_exists?string("##0")}</fo:block>	                            	
	                        </fo:table-cell>
				        </fo:table-row>
				       </#if> 
				    <#if (unitBankTot.get("nameOfTheBank")?has_content && ddAmountMap.get("nameOfTheBank")?has_content)>    
				     <#if ((unitBankTot.get("nameOfTheBank") == ddAmountMap.get("nameOfTheBank"))) && (ddAccPrint==true)>   
				        <#assign ddAccPrint =false>
				        <#if ddAmountMap.get("amount") !=0>
				        <fo:table-row>
				        	<fo:table-cell>
				        		<fo:block linefeed-treatment="preserve" font-size="3pt" >&#xA;</fo:block>
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
	                            	<fo:block text-align="right" font-size="4pt" >${ddAmountMap.get("bankAccNo")?if_exists}</fo:block>
	                        </fo:table-cell>
	                        <fo:table-cell>
	                            	<fo:block text-align="right" font-size="4pt" >${ddAmountMap.get("netAmtPayable")?if_exists?string("##0")}</fo:block>	                            	
	                        </fo:table-cell>
				        </fo:table-row>     
				        </#if> 
				      </#if> 
				      </#if>  
				        <#assign prevBank = currentbank>
				        </#list>
				        <#if bankWiseTotalAmountMap.get(prevBank) !=0>
				        <fo:table-row>
	                   	 		<fo:table-cell>
		                            	<fo:block font-size="9pt" white-space-collapse="false">----------------------------------------------------------------------------------------------------------------------------------------</fo:block>
		                        </fo:table-cell>
					        </fo:table-row>
					        <fo:table-row>
	                   	 		<fo:table-cell>
		                            	<fo:block text-align="left" keep-together="always" white-space-collapse="false">&#160;</fo:block>
		                        </fo:table-cell>
	                   	     	<fo:table-cell>
		                            	<fo:block text-align="left" font-size="4pt" >TOTAL :</fo:block>
		                        </fo:table-cell>
		                        <fo:table-cell>
		                            	<fo:block text-align="left" font-size="4pt" >&#160;</fo:block>
		                        </fo:table-cell>
		                        <fo:table-cell>
		                            	<fo:block text-align="left" font-size="4pt" >&#160;</fo:block>
		                        </fo:table-cell>
		                         <fo:table-cell>
		                            	<fo:block text-align="left" font-size="4pt" >&#160;</fo:block>
		                        </fo:table-cell>
		                        <fo:table-cell>
		                            	<fo:block text-align="left" font-size="4pt" >&#160;</fo:block>
		                        </fo:table-cell>
		                        <fo:table-cell>
		                            	<fo:block text-align="left" font-size="4pt" >&#160;</fo:block>
		                        </fo:table-cell>
		                        <fo:table-cell>
		                           	 	
	                           	 	<#if ((prevBank == ddAmountMap.get("nameOfTheBank")))>
		                           		<fo:block text-align="right" font-size="4pt" >${(bankWiseTotalAmountMap.get(prevBank)+ ddAmountMap.get("amount"))?if_exists?string("##0")}</fo:block>
		                           	<#else>
		                           	 	<fo:block text-align="right" font-size="4pt" >${(bankWiseTotalAmountMap.get(prevBank))?if_exists?string("##0")}</fo:block>
		                           	</#if>
		                        </fo:table-cell>
					        </fo:table-row>
					        <fo:table-row>
	                   	 		<fo:table-cell>
		                            	<fo:block font-size="4pt" white-space-collapse="false">----------------------------------------------------------------------------------------------------------------------------------------</fo:block>
		                        </fo:table-cell>
					        </fo:table-row>	
					        <fo:table-row>
	                   	 		<fo:table-cell>
	                   	 				<fo:block linefeed-treatment="preserve" font-size="3pt" >&#xA;</fo:block>
	                   	 				<fo:block linefeed-treatment="preserve" font-size="3pt" >&#xA;</fo:block>
		                        		<fo:block text-align="left" font-size="9pt" keep-together="always" white-space-collapse="false">&#160;                                        DEPUTY DIRECTOR / GENERAL MANAGER</fo:block>
		                        		<fo:block break-after="page"/>
		                        </fo:table-cell>
					        </fo:table-row>
				        </#if>
				        <#if (ddAmountMap.get("nameOfTheBank")?has_content)>    
					     	<#if (ddAccPrint==true)>   
					        	<#assign ddAccPrint =false>
						        <#if ddAmountMap.get("amount") !=0>
						        <fo:table-row>
						        	<fo:table-cell>
						        		<fo:block linefeed-treatment="preserve" font-size="3pt" >&#xA;</fo:block>
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
			                            	<fo:block text-align="right" font-size="4pt" >${ddAmountMap.get("bankAccNo")?if_exists}</fo:block>
			                        </fo:table-cell>
			                        <fo:table-cell>
			                            	<fo:block text-align="right" font-size="4pt" >${ddAmountMap.get("netAmtPayable")?if_exists?string("##0")}</fo:block>	                            	
			                        </fo:table-cell>
						        </fo:table-row>
						        
						        <fo:table-row>
	                   	 		<fo:table-cell>
		                            	<fo:block font-size="9pt" white-space-collapse="false">----------------------------------------------------------------------------------------------------------------------------------------</fo:block>
		                        </fo:table-cell>
					        </fo:table-row>
					        <fo:table-row>
	                   	 		<fo:table-cell>
		                            	<fo:block text-align="left" keep-together="always" white-space-collapse="false">&#160;</fo:block>
		                        </fo:table-cell>
	                   	     	<fo:table-cell>
		                            	<fo:block text-align="left" font-size="4pt" >TOTAL :</fo:block>
		                        </fo:table-cell>
		                        <fo:table-cell>
		                            	<fo:block text-align="left" font-size="4pt" >&#160;</fo:block>
		                        </fo:table-cell>
		                        <fo:table-cell>
		                            	<fo:block text-align="left" font-size="4pt" >&#160;</fo:block>
		                        </fo:table-cell>
		                         <fo:table-cell>
		                            	<fo:block text-align="left" font-size="4pt" >&#160;</fo:block>
		                        </fo:table-cell>
		                        <fo:table-cell>
		                            	<fo:block text-align="left" font-size="4pt" >&#160;</fo:block>
		                        </fo:table-cell>
		                        <fo:table-cell>
		                            	<fo:block text-align="left" font-size="4pt" >&#160;</fo:block>
		                        </fo:table-cell>
		                        <fo:table-cell>
		                           	 	<fo:block text-align="right" font-size="4pt" > ${ddAmountMap.get("netAmtPayable")?if_exists?string("##0")}</fo:block>
		                        </fo:table-cell>
					        </fo:table-row>
					        <fo:table-row>
	                   	 		<fo:table-cell>
		                            	<fo:block font-size="4pt" white-space-collapse="false">----------------------------------------------------------------------------------------------------------------------------------------</fo:block>
		                        </fo:table-cell>
					        </fo:table-row>	
					        <fo:table-row>
	                   	 		<fo:table-cell>
	                   	 				<fo:block linefeed-treatment="preserve" font-size="3pt" >&#xA;</fo:block>
	                   	 				<fo:block linefeed-treatment="preserve" font-size="3pt" >&#xA;</fo:block>
		                        		<fo:block text-align="left" font-size="9pt" keep-together="always" white-space-collapse="false">&#160;                                        DEPUTY DIRECTOR / GENERAL MANAGER</fo:block>
		                        </fo:table-cell>
					        </fo:table-row>     
						        </#if> 
				     		</#if> 
				      	</#if>
                   </fo:table-body>
                </fo:table>
               </fo:block>      
           </fo:flow>
        </fo:page-sequence>
         <#else>
				<fo:page-sequence master-reference="main">
	    			<fo:flow flow-name="xsl-region-body" font-family="Helvetica">
	       		 		<fo:block font-size="14pt">	       		 		
	       		 		<#if negativeAmtMsg?has_content>
	       		 			${negativeAmtMsg?if_exists}
	       		 		<#else>
	            			Shed Not Selected.......!
	            		</#if>	
	       		 		</fo:block>
	    			</fo:flow>
				</fo:page-sequence>
		</#if> 
     </fo:root>
</#escape>
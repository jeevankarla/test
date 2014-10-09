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
            <fo:simple-page-master master-name="main" page-height="12in" page-width="15in" margin-top=".5in" margin-bottom=".5in">
                <fo:region-body margin-top=".6in"/>
                <fo:region-before extent="1in"/>
                <fo:region-after extent="1in"/>
            </fo:simple-page-master>
        </fo:layout-master-set>
        ${setRequestAttribute("OUTPUT_FILENAME", "BankWiseTotPayment.txt")}
<#if errorMessage?has_content>
<fo:page-sequence master-reference="main">
   <fo:flow flow-name="xsl-region-body" font-family="Helvetica">
      <fo:block font-size="14pt">
              ${errorMessage}.
   	  </fo:block>
   </fo:flow>
</fo:page-sequence>        
<#else>         
        <#if bankWiseTotalAmountMap?has_content>   
        <fo:page-sequence master-reference="main">
        	<fo:static-content flow-name="xsl-region-before">
        	 	<#assign facility = delegator.findOne("Facility", {"facilityId" : parameters.shedId}, true)>
        	 	<fo:block text-align="left" white-space-collapse="false" font-weight="bold" font-size="10pt">          SHED NAME        :   ${facility.get("facilityName")?if_exists} </fo:block>
        		<fo:block text-align="left" white-space-collapse="false" font-weight="bold" font-size="10pt">          PERIOD FROM    :   ${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(fromDate, "dd/MM/yyyy")}  TO   ${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(thruDate, "dd/MM/yyyy")}               </fo:block>
        		<fo:block text-align="left" keep-together="always" white-space-collapse="false" font-size="10pt">----------------------------------------------------</fo:block>	 	 	  
        		<fo:block linefeed-treatment="preserve">&#xA;</fo:block>
        	</fo:static-content>
        	<fo:flow flow-name="xsl-region-body" font-family="Courier,monospace">   		           
                 <fo:block>
                 	<fo:table >
                    <!--<fo:table-column column-width="50pt"/>-->
                    <fo:table-column column-width="170pt"/> 
                    <fo:table-column column-width="50pt"/>  
                    <fo:table-body>
                    	<fo:table-row>
                   	 		<fo:table-cell><fo:block text-align="left" keep-together="always" white-space-collapse="false" font-weight="bold" font-size="11pt"> NAME OF THE BANK    AMOUNT</fo:block></fo:table-cell>
				        </fo:table-row>
                    	<fo:table-row>
                   	 		<fo:table-cell>
	                            	<fo:block text-align="left" keep-together="always" white-space-collapse="false" font-size="10pt">----------------------------------------------------</fo:block> 
	                        </fo:table-cell>
				        </fo:table-row>
                    <#assign bankTotals = bankWiseTotalAmountMap.entrySet()>
                    <#list bankTotals as bankTot>
                    	<#if bankTot.getKey() == "TOTAL">
                    	<fo:table-row>
                   	 		<fo:table-cell>
	                            	<fo:block text-align="left" keep-together="always" white-space-collapse="false" font-size="10pt">----------------------------------------------------</fo:block> 
	                        </fo:table-cell>
				        </fo:table-row>
				        </#if>
				        <#if bankTot.getKey() != "">
				        	<#assign amt=bankTot.getValue()>
				        	<#if amt!=0>
                   	 	<fo:table-row>
                   	 		<!--<fo:table-cell>
	                            	<fo:block linefeed-treatment="preserve">&#xA;</fo:block>        
	                        </fo:table-cell>-->
	                        
                   	     	<fo:table-cell>
                   	     		<#if ddAmountMap.get("nameOfTheBank")== bankTot.getKey()>
	                            	<fo:block text-align="left" font-size="10pt">${bankTot.getKey()?if_exists}</fo:block>
	                            <#else>	
	                            	<fo:block text-align="left" font-size="10pt">${bankTot.getKey()?if_exists}</fo:block>
	                            </#if>	
	                        </fo:table-cell>
	                        <fo:table-cell>
	                        	<#if ddAmountMap.get("nameOfTheBank")== bankTot.getKey()>
	                            	<fo:block text-align="right" font-size="10pt">${(bankTot.getValue()+ddAmountMap.get("netAmtPayable"))?if_exists?string("##0")}</fo:block>
	                            <#elseif bankTot.getKey() == "TOTAL">	
	                            	<fo:block text-align="right" font-size="10pt">${(bankTot.getValue()+ddAmountMap.get("netAmtPayable"))?if_exists?string("##0")}</fo:block>
	                            <#else>
	                            	<fo:block text-align="right" font-size="10pt">${(bankTot.getValue())?if_exists?string("##0")}</fo:block>
	                            </#if>	
	                        </fo:table-cell>
				        </fo:table-row>
				        	</#if>
				        </#if>
				        <#if bankTot.getKey() == "TOTAL">
                    	<fo:table-row>
                   	 		<fo:table-cell>
	                            	<fo:block text-align="left" keep-together="always" white-space-collapse="false" font-size="10pt">----------------------------------------------------</fo:block> 
	                        </fo:table-cell>
				        </fo:table-row>
				        </#if>
				   </#list>
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
		</#if>
     </fo:root>
</#escape>
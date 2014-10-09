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
                <fo:region-body margin-top=".7in"/>
                <fo:region-before extent="1in"/>
                <fo:region-after extent="1in"/>
            </fo:simple-page-master>
        </fo:layout-master-set>
         ${setRequestAttribute("OUTPUT_FILENAME", "unitWisePtcRecovery.txt")}
<#if errorMessage?has_content>
<fo:page-sequence master-reference="main">
   <fo:flow flow-name="xsl-region-body" font-family="Helvetica">
      <fo:block font-size="14pt">
              ${errorMessage}.
   	  </fo:block>
   </fo:flow>
</fo:page-sequence>        
<#else>         
        <#if routeWiseCentersList?has_content>
        <fo:page-sequence master-reference="main">
        	<fo:static-content flow-name="xsl-region-before">
        	<#assign facility = delegator.findOne("Facility", {"facilityId" : parameters.unitId}, true)>
        	<fo:block font-size="7pt" linefeed-treatment="preserve">&#xA;</fo:block>
    		<fo:block font-size="8pt" text-align="left" white-space-collapse="false" font-weight="bold">.                                             STATEMENT SHOWING THE RECOVERABLE MILK AMOUNT FROM THE PRIVATE TRANSPORT CONTRACTOR                                                                                                                                                                                               PAGE NO:<fo:page-number/> </fo:block> 	 	  
        	<fo:block font-size="8pt" text-align="left" white-space-collapse="false" >-------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------</fo:block>
        	<fo:block font-size="8pt" text-align="left" white-space-collapse="false" font-weight="bold">UNIT CODE :  ${facility.get("facilityCode")?if_exists}                                                                    UNIT NAME :  ${facility.get("facilityName")?if_exists}                                                                PERIOD FROM :  ${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(fromDateTime, "dd/MM/yyyy")}     TO    ${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(thruDateTime, "dd/MM/yyyy")} </fo:block>
        	</fo:static-content>
        	<fo:flow flow-name="xsl-region-body" font-family="Helvetica">   		           
                 <fo:block font-size="7pt">
                 	<fo:table border-width="1pt">
                    <fo:table-column column-width="48pt"/>
                    <fo:table-column column-width="30pt"/>  
               	    <fo:table-column column-width="110pt"/>
               	    <fo:table-column column-width="15pt"/>
                    <fo:table-column column-width="20pt"/>  
                    <fo:table-column column-width="20pt"/>
                    <fo:table-column column-width="43pt"/>
                    <fo:table-column column-width="43pt"/>  
                    <fo:table-column column-width="43pt"/>
                    <fo:table-column column-width="43pt"/>
                    <fo:table-column column-width="43pt"/>
                    <fo:table-column column-width="40pt"/>
                    <fo:table-column column-width="40pt"/>
                    <fo:table-column column-width="43pt"/>
                    <fo:table-column column-width="45pt"/>
                    <fo:table-column column-width="42pt"/>
                    <fo:table-column column-width="40pt"/>  
                    <fo:table-column column-width="40pt"/>
                    <fo:table-column column-width="40pt"/>
                    <fo:table-column column-width="50pt"/>  
                    <fo:table-column column-width="20pt"/>
                    <fo:table-column column-width="40pt"/>
                    <fo:table-column column-width="10pt"/>
                    <fo:table-column column-width="40pt"/>
                    <fo:table-column column-width="10pt"/>
                    <fo:table-column column-width="50pt"/>
                    <fo:table-column column-width="50pt"/>  
		          	<fo:table-header>
		            	<fo:table-cell>
		            		<fo:block font-size="8pt" text-align="left" white-space-collapse="false" >-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------</fo:block>
			            	<fo:block font-size="8pt" text-align="left" white-space-collapse="false" keep-together="always">.                                                                                     R    D    T                                            BOURNED BY P.T.C                                                     |                                                 P.T.C RECOVERABLE AMOUNT                                             |                     |                      |                          |         NET         | </fo:block>
			            	<fo:block font-size="8pt" text-align="left" white-space-collapse="false" keep-together="always">.                                                                                     N    A    Y   ---------------------------------------------------------------------------------------------------------- | ------------------------------------------------------------------------------------------------------------------------- |                      |      CURD     |      SOUR          |      AMOUNT    |</fo:block>
			            	<fo:block font-size="8pt" text-align="left" white-space-collapse="false" keep-together="always">DATED        CODE     NAME OF THE CENTER          O    Y    P         QTY-LTS         QTY-KG         FAT         SNF         KGFAT         KGSNF         |         RATE         TOTAL         PREM/DED         NET         TIP         DIF         PENALITY         |    TOTAL      |       OR         |        VALUE       |       TO BE       | </fo:block>                 
			            	<fo:block font-size="8pt" text-align="left" white-space-collapse="false" keep-together="always">.                                                                                                                                                                                                                                       |                            VAL             AMT                    AMT         AMT       AMT           AMT               |    AMOUNT   |      SOUR     |          ( - )          |    RECOVER    |</fo:block>
			            	<fo:block font-size="8pt" text-align="left" white-space-collapse="false" >-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------</fo:block>
		            	</fo:table-cell>		                    	                  
				    </fo:table-header>		           
                    <fo:table-body>
                    	<#list routeWiseCentersList as ptcData>
                    		<#if (ptcData.get("dated") == "ROUTE-TOT")||(ptcData.get("dated") == "UNIT-TOT")>
            				<fo:table-row>	
	                            <fo:table-cell >	
	                            	<fo:block font-size="7pt" text-align="left">-------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------</fo:block>                               
	                            </fo:table-cell>
				            </fo:table-row>	
            				</#if>
            				<#if ptcData.get("dated") != "">
            				<fo:table-row>	
	                            <fo:table-cell >	
	                            	<fo:block font-size="7pt" text-align="left">${ptcData.get("dated")?if_exists}</fo:block>                               
	                            </fo:table-cell>
	                            <fo:table-cell >	
	                            	<fo:block font-size="7pt" text-align="left">${ptcData.get("centerCode")?if_exists}</fo:block>                               
	                            </fo:table-cell>
	                            <#if (ptcData.get("dated") == "ROUTE-TOT")||(ptcData.get("dated") == "UNIT-TOT")>
	                            <fo:table-cell >	
	                            	<fo:block font-size="7pt" text-align="left">${ptcData.get("centerName")?if_exists}</fo:block>                               
	                            </fo:table-cell>
            					<#else>
	                            <fo:table-cell >	
	                            	<fo:block font-size="7pt" text-align="left">${ptcData.get("centerName")?if_exists}</fo:block>                               
	                            </fo:table-cell>
	                            </#if>
	                            
	                            <fo:table-cell >	
	                            	<fo:block font-size="7pt" text-align="left">${ptcData.get("routeNo")?if_exists}</fo:block>                               
	                            </fo:table-cell>
	                            
	                            <#if (ptcData.get("dated") == "ROUTE-TOT")||(ptcData.get("dated") == "UNIT-TOT")>
	                            <fo:table-cell >	
	                            	<fo:block font-size="7pt" linefeed-treatment="preserve"></fo:block>                               
	                            </fo:table-cell>
            					<#else>
	                            <fo:table-cell >	
	                            	<fo:block font-size="7pt" text-align="left">${ptcData.get("day")?if_exists}</fo:block>                               
	                            </fo:table-cell>
	                            </#if>
	                            
	                            <#if ptcData.get("typ")?has_content>
		                            <fo:table-cell >	
		                            	<fo:block font-size="7pt" text-align="left">${ptcData.get("typ")?if_exists}</fo:block>                               
		                            </fo:table-cell>
		                        <#else>
	                            <fo:table-cell >	
	                            	<fo:block font-size="7pt" linefeed-treatment="preserve"></fo:block>                               
	                            </fo:table-cell>
	                            </#if>
	                            
	                            <fo:table-cell >	
	                            	<fo:block font-size="7pt" text-align="center">${ptcData.get("qtyLtrs")?if_exists?string("##0.0")}</fo:block>                               
	                            </fo:table-cell>
	                            <fo:table-cell >	
	                            	<fo:block font-size="7pt" text-align="center">${ptcData.get("qtyKgs")?if_exists?string("##0.0")}</fo:block>                               
	                            </fo:table-cell>
	                            <fo:table-cell >	
	                            	<fo:block font-size="7pt" text-align="center"><#if ptcData.get("qtyKgs") !=0>${((ptcData.get("kgFat")*100)/ptcData.get("qtyKgs"))?if_exists?string("##0.0")}</#if></fo:block>                               
	                            </fo:table-cell>
	                            <fo:table-cell >	
	                            	<fo:block font-size="7pt" text-align="center"><#if ptcData.get("qtyKgs") !=0>${((ptcData.get("kgSnf")*100)/ptcData.get("qtyKgs"))?if_exists?string("##0.0")}</#if></fo:block>                               
	                            </fo:table-cell>
	                            <fo:table-cell >	
	                            	<fo:block font-size="7pt" text-align="center">${ptcData.get("kgFat")?if_exists?string("##0.000")}</fo:block>                               
	                            </fo:table-cell>
	                            <fo:table-cell >	
	                            	<fo:block font-size="7pt" text-align="center">${ptcData.get("kgSnf")?if_exists?string("##0.000")}</fo:block>                               
	                            </fo:table-cell>
	                            <fo:table-cell >	
	                            	<fo:block font-size="7pt" text-align="center">|</fo:block>                               
	                            </fo:table-cell>
	                            <fo:table-cell >	
	                            	<fo:block font-size="7pt" text-align="left">${ptcData.get("rate")?if_exists}</fo:block>                               
	                            </fo:table-cell>
	                            <fo:table-cell >	
	                            	<fo:block font-size="7pt" text-align="left">${ptcData.get("totAmount")?if_exists?string("##0.00")}</fo:block>                               
	                            </fo:table-cell>
	                            <fo:table-cell >	
	                            	<fo:block font-size="7pt" text-align="left">${ptcData.get("totPrem")?if_exists?string("##0.00")}</fo:block>                               
	                            </fo:table-cell>
	                            <fo:table-cell >	
	                            	<fo:block font-size="7pt" text-align="center">${ptcData.get("netAmount")?if_exists?string("##0.00")}</fo:block>                               
	                            </fo:table-cell>
	                            <fo:table-cell >	
	                            	<fo:block font-size="7pt" text-align="center">${ptcData.get("tipAmount")?if_exists?string("##0.00")}</fo:block>                               
	                            </fo:table-cell>
	                            <fo:table-cell >	
	                            	<fo:block font-size="7pt" text-align="center">${ptcData.get("difAmount")?if_exists?string("##0.00")}</fo:block>                               
	                            </fo:table-cell>
	                            <fo:table-cell >	
	                            	<fo:block font-size="7pt" text-align="center">${ptcData.get("penality")?if_exists?string("##0.00")}</fo:block>                               
	                            </fo:table-cell>
	                            <fo:table-cell >	
	                            	<fo:block font-size="7pt" text-align="center">|</fo:block>                               
	                            </fo:table-cell>
	                            <fo:table-cell >	
	                            	<fo:block font-size="7pt" text-align="left">${ptcData.get("totValue")?if_exists?string("##0.00")}</fo:block>                               
	                            </fo:table-cell>
	                            <fo:table-cell >	
	                            	<fo:block font-size="7pt" text-align="left">|</fo:block>                               
	                            </fo:table-cell>
	                            <fo:table-cell >	
	                            	<fo:block font-size="7pt" text-align="center">${ptcData.get("curdOrSour")?if_exists}</fo:block>                               
	                            </fo:table-cell>
	                            <fo:table-cell >	
	                            	<fo:block font-size="7pt" text-align="left">|</fo:block>                               
	                            </fo:table-cell>
	                            <fo:table-cell >	
	                            	<fo:block font-size="7pt" text-align="center">${ptcData.get("sValue")?if_exists?string("##0.00")}</fo:block>                               
	                            </fo:table-cell>
	                            <fo:table-cell >	
	                            	<fo:block font-size="7pt" text-align="center">${ptcData.get("netAmountToRecover")?if_exists?string("##0.00")}</fo:block>                               
	                            </fo:table-cell>
				            </fo:table-row>	
				            </#if>
				            <#if (ptcData.get("dated") == "ROUTE-TOT")||(ptcData.get("dated") == "UNIT-TOT")>
            				<fo:table-row>	
	                            <fo:table-cell >	
	                            	<fo:block font-size="7pt" text-align="left">-------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------</fo:block>                               
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
                        ${uiLabelMap.NoOrdersFound}.
                    </fo:block>
                </fo:flow>
            </fo:page-sequence>
        </#if>
        </#if>
     </fo:root>
</#escape>
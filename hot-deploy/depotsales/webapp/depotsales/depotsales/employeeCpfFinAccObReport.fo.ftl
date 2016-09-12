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
            <fo:simple-page-master master-name="main" page-height="12in" page-width="10in" 
                 margin-top="0.3in" margin-bottom=".7n" margin-left=".5in" margin-right=".5in">
        <fo:region-body margin-top="1.1in"/>
        <fo:region-before extent="1.0in"/>
        <fo:region-after extent="1.5in"/>  
            </fo:simple-page-master>
     </fo:layout-master-set>
     	<#if partyWiseFinMap?has_content>
		<fo:page-sequence master-reference="main">
        	<fo:static-content flow-name="xsl-region-before" font-family="Courier,monospace">
        		<#assign reportHeader = delegator.findOne("TenantConfiguration", {"propertyTypeEnumId" : "MILK_PROCUREMENT","propertyName" : "reportHeaderLable"}, true)>
        		<#--<fo:block text-align="left" keep-together="always" white-space-collapse="false">&#160;                        ${reportHeader.description?if_exists}</fo:block>-->
				<fo:block text-align="left" keep-together="always" white-space-collapse="false" font-weight="bold">&#160;&#160; &#160;&#160;&#160;                               PF CONTRIBUTIONS BALANCE</fo:block> 		
            	<fo:block linefeed-treatment="preserve">&#xA;</fo:block>
            	<fo:block keep-together="always" white-space-collapse="false">&#160;      FROM DATE : ${parameters.fromDate}</fo:block>
            	<fo:block>&#160;&#160;   ----------------------------------------------------------------------------------</fo:block>
        	</fo:static-content>
        	<fo:flow flow-name="xsl-region-body" font-family="Helvetica">        				
     				<fo:block font-family="Courier,monospace"> 
     				
     					<fo:table align="center">
                    		<fo:table-column column-width="20%"/>
                    		<#list finAccountIds as finAcc>
                   			<fo:table-column column-width="30%"/> 
                   			</#list>               
                   		    <fo:table-body>  
                   		    <fo:table-row>
                    				<fo:table-cell>
                    					<fo:block font-weight="bold">Employee Name</fo:block>
                    				</fo:table-cell>
                          		<#list finAccountIds as finAcc>
                          			<#assign finAccountDetail = delegator.findOne("FinAccount", {"finAccountId" : finAcc}, true)?if_exists/> 
                          			<fo:table-cell>
                    					<fo:block>${finAccountDetail.finAccountName?if_exists}</fo:block>
                    				</fo:table-cell>
                          		</#list>
                          		</fo:table-row> 
                   		    <#assign partyWiseFinList =partyWiseFinMap.entrySet()>
                   		     <#list partyWiseFinList as partyWiseFin>
                    			<fo:table-row>
                    				<#assign partyFullName = Static["org.ofbiz.party.party.PartyHelper"].getPartyName(delegator, partyWiseFin.getKey(), false)>
                    				<fo:table-cell>
                    					<fo:block>${partyFullName}</fo:block>
                    				</fo:table-cell>
                          		<#list finAccountIds as finAcc>
                          		<#assign partyFinList =partyWiseFin.getValue()>
                          		<#assign partyFinOb =partyFinList.get(finAcc)>
                          			<fo:table-cell>
                    					<fo:block>${partyFinOb?if_exists}</fo:block>
                    				</fo:table-cell>
                          		</#list>
                          		</fo:table-row>  
                    		</#list>                    			
                   			</fo:table-body>
                		</fo:table>
     				</fo:block>
     			</fo:flow>
    	</fo:page-sequence> 
    	</#if>   	
</fo:root>
</#escape>
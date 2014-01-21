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
            <fo:simple-page-master master-name="main" page-height="12in" page-width="22in"
                   margin-top="0.8in" margin-bottom="0.8in" margin-left=".2in" margin-right=".1in">
                <fo:region-body margin-top="1.8in"/>
                <fo:region-before extent=".5in"/>
                <fo:region-after extent=".5in"/>
            </fo:simple-page-master>
        </fo:layout-master-set>
      		 <#assign routeWiseList =routeWiseMap.entrySet()>
		    <#if routeWiseList?has_content>  
		    	<#list routeWiseList as routeWiseEntries>
			        <fo:page-sequence master-reference="main">
			        	<fo:static-content flow-name="xsl-region-before">
			        		<fo:block text-align="center" font-family="Helvetica" white-space-collapse="false" font-size="16pt" keep-together="always" font-weight="bold">SUPRAJA DAIRY PVT.LTD</fo:block>
			        		<fo:block text-align="center" font-family="Helvetica" white-space-collapse="false" font-size="13pt" keep-together="always" font-weight="bold">AGENT TARGET AND PERFORMANCE REPORT</fo:block>                                             
			        			<#assign routeDetails = delegator.findOne("Facility", {"facilityId" : routeWiseEntries.getKey()}, true)>
			        		<fo:block linefeed-treatment="preserve">&#xA;</fo:block>
			        		<fo:block white-space-collapse="false" font-size="13pt"  font-family="Helvetica" keep-together="always" font-weight="bold">&#160;    Period : ${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(dayBegin, "dd-MMM-yyyy")}  --  ${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(dayEnd, "dd-MMM-yyyy")}    &#160;                                               &#160;                                  (Qty. in Liters)                                  &#160;                               Route : ${routeDetails.get("facilityName")?if_exists}</fo:block>
			        	    <fo:block linefeed-treatment="preserve">&#xA;</fo:block>
			        		
			        	      <#assign monthDaysList = monthDaysMap.entrySet()>
			        		<fo:block >
			        			 <fo:table width="100%" table-layout="fixed" border-style="solid" font-size="10pt">
					                  <fo:table-column column-width="40pt"/>
					                 <fo:table-column column-width="130pt"/>  
					                  <fo:table-column column-width="40pt"/>             
						            <#list monthDaysList as product>            
						             	<fo:table-column column-width="40pt"/> 						             	
						            </#list>						            
						           <fo:table-column column-width="40pt"/>
						            <fo:table-column column-width="40pt"/>
						              <fo:table-column column-width="40pt"/>
						            <fo:table-column column-width="40pt"/>
						             
						           	<fo:table-body>
				                     
				                     	<fo:table-row>
				                     	<fo:table-cell border-style="solid"><fo:block text-align="center" keep-together="always" >S.No</fo:block></fo:table-cell>
				                     	<fo:table-cell border-style="solid"><fo:block text-align="center" keep-together="always" >AGENT NAME</fo:block></fo:table-cell>
	                					<fo:table-cell border-style="solid"><fo:block text-align="center" keep-together="always" >TARGET</fo:block></fo:table-cell>       		
				                       <#list monthDaysList as product>                       		
				                       	<fo:table-cell  border-style="solid">
				                       			<fo:block  text-align="center" white-space-collapse="false"  font-weight="bold">${product.getValue()}</fo:block>
				                       	</fo:table-cell>
				                       	</#list>				                       			                       	
				                       	<fo:table-cell border-style="solid">
			                       			<fo:block text-align="left" white-space-collapse="false" font-weight="bold" >TOTAL</fo:block>
			                       		</fo:table-cell>			                       		
			                       		<fo:table-cell >
			                       			<fo:block text-align="center" white-space-collapse="false" font-weight="bold"></fo:block>
			                       		</fo:table-cell>
			                    
			                       		
			                       	</fo:table-row>
			                	</fo:table-body>
			                		</fo:table>
			        		</fo:block>
			        		</fo:static-content>
			        		
			       		<fo:flow flow-name="xsl-region-body" font-family="Helvetica">
			        		<fo:block >
			        		<#assign monthDaysList = monthDaysMap.entrySet()>
			        			 <fo:table width="100%" table-layout="fixed" border-style="solid" font-size="10pt">
					                  <fo:table-column column-width="40pt"/>
					                 <fo:table-column column-width="130pt"/> 
					                  <fo:table-column column-width="40pt"/>              
						            <#list monthDaysList as product>            
						             	<fo:table-column column-width="40pt"/> 						             	
						            </#list>						            
						            <fo:table-column column-width="40pt"/> 		
						           <fo:table-column column-width="40pt"/> 		
						            <fo:table-column column-width="40pt"/> 		
						           <fo:table-column column-width="40pt"/>
						           	<fo:table-body>
				                     <#assign agentWiseList = routeWiseEntries.getValue().entrySet()>
			       			  <#assign sno=1>
			       			  <#list agentWiseList as agentWiseEntries>
			       			  
			                       	<fo:table-row>
			                       	<fo:table-cell border-style="dotted"><fo:block text-align="center" font-weight="bold" font-size="10pt" keep-together="always">${sno}</fo:block></fo:table-cell>
			                       	 <#assign facilityDetails = delegator.findOne("Facility", {"facilityId" : agentWiseEntries.getKey()}, true)>
			                       	<fo:table-cell border-style="dotted"><fo:block text-align="left" font-weight="bold" font-size="10pt" keep-together="always">${agentWiseEntries.getKey()?if_exists}- ${Static["org.ofbiz.order.order.OrderServices"].nameTrim((StringUtil.wrapString(facilityDetails.get("description").toUpperCase())),12)}</fo:block></fo:table-cell>
			                       	<#assign totalProductList=agentWiseEntries.getValue().entrySet()>
			       			  	  <#list totalProductList as eachProd>
				                     <fo:table-cell border-style="dotted"><fo:block text-align="left"  font-weight="bold" font-size="10pt" keep-together="always">${eachProd.getValue()}</fo:block></fo:table-cell>       		
				                     </#list>
				                     </fo:table-row>
				                       <#assign sno=sno+1 >
				                     </#list> 	
			                	</fo:table-body>
			                		</fo:table>
			        		</fo:block>
						</fo:flow>
				 	</fo:page-sequence>
				</#list>
			<#else>
				<fo:page-sequence master-reference="main">
    			<fo:flow flow-name="xsl-region-body" font-family="Helvetica">
       		 		<fo:block font-size="14pt">
            			${uiLabelMap.NoOrdersFound}.
       		 		</fo:block>
    			</fo:flow>
			</fo:page-sequence>
			</#if>
	</fo:root>
</#escape>
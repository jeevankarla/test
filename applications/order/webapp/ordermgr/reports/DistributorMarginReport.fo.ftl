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
    <fo:simple-page-master master-name="main" page-height="12in" page-width="10in"
            margin-top="0.5in" margin-bottom="1in" margin-left=".5in" margin-right=".5in">
        <fo:region-body margin-top=".5in"/>
        <fo:region-before extent=".5in"/>
        <fo:region-after extent="1in"/>
    </fo:simple-page-master>
</fo:layout-master-set>
<#if masterList?has_content>
	<#list masterList as distributorReportEntry>
	<#assign distributorReportEntries = (distributorReportEntry).entrySet()>
	<#if distributorReportEntries?has_content>
	<#list distributorReportEntries as distributorReport>
	<#assign parentFacilityId = distributorReport.getKey()>			                      	                     	
    <#assign facility = delegator.findOne("Facility", {"facilityId" : parentFacilityId}, true)>
		 <fo:page-sequence master-reference="main" >
			<fo:static-content flow-name="xsl-region-before" font-family="Courier,monospace">
				<fo:block text-align="center" keep-together="always"> ${uiLabelMap.ApDairyMsg}</fo:block>
				<fo:block text-align="left" keep-together="always" white-space-collapse="false">.            Distributor Margin For The Month Of ${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(fromDateTime, "MMMMM-yyyy")}</fo:block>				    		
              	<fo:block  white-space-collapse="false"  keep-together="always">Booth No. :            Booth Name:     Agent Name :</fo:block>				 		
            	<fo:block></fo:block>
            </fo:static-content>
         <fo:flow flow-name="xsl-region-body" font-family="Courier,monospace">
				<fo:block keep-together="always" white-space-collapse="false">-------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------</fo:block>
					<fo:block  font-size="10pt">
            			<fo:table width="100%" table-layout="fixed">
            				 		<fo:table-column column-width="100%"/>
            				 		<fo:table-body>
			                		<fo:table-row column-width="100%">
			                			<fo:table-cell column-width="100%">
            				 				<fo:table width="100%" table-layout="fixed">                
				                				<fo:table-column column-width="35pt"/>
				                				<fo:table-column column-width="50pt"/>
				                				<fo:table-column column-width="40pt"/>
				                				<fo:table-column column-width="40pt"/>
				                				<fo:table-column column-width="40pt"/>
				                				<fo:table-column column-width="40pt"/>
				                			<fo:table-header>
			                    			<fo:table-row>
			                    				<fo:table-cell><fo:block  keep-together="always" white-space-collapse="false">Distributor Name          ${uiLabelMap.Day}                 ${uiLabelMap.CommonTotal}            Total Margin</fo:block></fo:table-cell>
			                    			</fo:table-row>
			                    			<fo:table-row>                        
			                        			<fo:table-cell><fo:block>-----------------------------------------------------------------------------------------------</fo:block></fo:table-cell>
			                    			</fo:table-row>				                     		                     
			                			</fo:table-header>	                   
                					<fo:table-body>
                					<fo:table-row >                            
                            			<fo:table-cell >
								            	<fo:block keep-together="always">${facility.facilityName}</fo:block>
								        </fo:table-cell>
								        <fo:table-cell/>
								        <fo:table-cell/>
								        <fo:table-cell/>
								        <fo:table-cell>
                            				<fo:block>
                            					<#assign reportValues=(distributorReport.getValue()).entrySet()>
								                <#list reportValues as distValues>
								                <fo:table table-layout="fixed" >
		                                			<fo:table-column column-width="35pt"/>
			             				  			<fo:table-column column-width="100pt"/>
			             							<fo:table-column column-width="150pt"/>
			             							<fo:table-column column-width="40pt"/>
			             							<fo:table-column column-width="40pt"/>
			             							<fo:table-column column-width="40pt"/>
			             							<fo:table-column column-width="40pt"/>
			             							
			             							<fo:table-body>
			             							<fo:table-row>
				              									<fo:table-cell >		
								                         			<fo:block >	${distValues.getKey()}</fo:block>
								                         		</fo:table-cell>
								                         		<#assign distEntries=(distValues.getValue()).entrySet()>
								                         		<#list distEntries as dist>
								                         		<fo:table-cell >
								                         			
								                         				<fo:block text-align="right">${dist.getValue()} </fo:block>
								                         			
								                         		</fo:table-cell>
								                         		</#list>
								                    </fo:table-row>								                        
					                         		</fo:table-body>
				                         		</fo:table>
				                         		</#list>
                                	 		</fo:block>
                            			</fo:table-cell>	                           
                       				</fo:table-row>
                      			</fo:table-body>
        					</fo:table>
      	  				</fo:table-cell>
     	 			</fo:table-row>	
					</fo:table-body>
	 			</fo:table>	
			</fo:block>
			<fo:block keep-together="always"  line-height="15pt"  white-space-collapse="false" font-size="10pt">-----------------------------------------------------------------------------------------------</fo:block>
			</fo:flow>
		</fo:page-sequence>
		</#list>
		</#if>
		</#list>
 	<#else>
		<fo:page-sequence master-reference="main">
	    	<fo:flow flow-name="xsl-region-body" font-family="Helvetica">
	       		 <fo:block font-size="14pt">
	            	${uiLabelMap.OrderNoOrderFound}.
	       		 </fo:block>
	    	</fo:flow>
		</fo:page-sequence>
	</#if>
</fo:root>
</#escape>
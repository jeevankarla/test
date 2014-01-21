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
            <fo:simple-page-master master-name="main" page-height="12in" page-width="10in" margin-left=".1in"  margin-right=".5in" margin-top=".2in" margin-bottom=".2in">
                <fo:region-body margin-top="1.3in"/>
                <fo:region-before extent=".2in"/>
                <fo:region-after extent=".2in"/>
            </fo:simple-page-master>
       </fo:layout-master-set>
       <#if gradesList?has_content>
       <fo:page-sequence master-reference="main">
            <fo:static-content flow-name="xsl-region-before" font-family="Times">
        		
        		<fo:block keep-together="always" white-space-collapse="false" font-size="10pt" text-align="center">&#160;  SUPRAJA DAIRY(P) LTD</fo:block>
        		<fo:block keep-together="always" white-space-collapse="false" font-size="10pt" text-align="center">&#160; VISAKHAPATNAM </fo:block>
        		<fo:block keep-together="always" white-space-collapse="false" font-size="10pt" text-align="left">&#160;                  																																																	 LIST OF FAT AND SNF WISE REPORT FOR THE PERIOD  FROM   ${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(fromDate, "dd/MM/yyyy")}    TO   ${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(thruDate, "dd/MM/yyyy")} </fo:block>
        		<fo:block font-size="8pt">-------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------</fo:block>
        		<fo:block keep-together="always" white-space-collapse="false" font-size="10pt">&#160; UNIT NAME   :   ${facilityName}																																																																																																										<#if unitId=="supervisor" || unitId!="allUnits">SUPERVISER NAME  :  ${supervisorName}</#if></fo:block>
        		<fo:block linefeed-treatment="preserve">&#xA;</fo:block>
        		<fo:block keep-together="always" white-space-collapse="false" font-size="10pt">&#160; SNO 								CODE  																			VILLAGE	      																			MILKTYPE							           QTY-LTRS			        					FAT 												SNF								GRADE  								<#if unitId!="supervisor">SUPERVISOR<#else>UNIT</#if></fo:block>
        		<fo:block font-size="8pt">-------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------</fo:block>
            </fo:static-content>
            <fo:flow flow-name="xsl-region-body" font-family="Courier,monospace" font-size="9pt">
                <#assign sno = 0>
                <#list gradesList as gradeDetails >
                	<#if gradeDetails.qtyLtrs!=0>	
		                <fo:block keep-together="always">
		                		<fo:table font-family="Courier,monospace">
		                				<fo:table-column column-width="25pt"/>
		                				<fo:table-column column-width="80pt"/>
		                				<fo:table-column column-width="160pt"/>
		                				<fo:table-column column-width="40pt"/>
		                				<fo:table-column column-width="65pt"/>
		                				<fo:table-column column-width="65pt"/>
		                				<fo:table-column column-width="65pt"/>
		                				<fo:table-column column-width="65pt"/>
		                				<fo:table-column column-width="160pt"/>
		                			<fo:table-body>
		                			  	
		                				<fo:table-row>
		                					<#assign sno = sno+1>
		                					<fo:table-cell>
		                						<fo:block text-align="center">${sno}</fo:block>
		                					</fo:table-cell>
		                					<fo:table-cell>
		                						<fo:block text-align="center">${gradeDetails.centerCode}</fo:block>
		                					</fo:table-cell>
		                					<fo:table-cell>
		                						<fo:block text-align="left">${Static["org.ofbiz.order.order.OrderServices"].nameTrim((StringUtil.wrapString(gradeDetails.centerName)),19)}</fo:block>
		                					</fo:table-cell>
		                					<fo:table-cell>
		                						<fo:block text-align="left">${gradeDetails.milkType}</fo:block>
		                					</fo:table-cell>
		                					<fo:table-cell>
		                						<fo:block text-align="right">${gradeDetails.qtyLtrs?if_exists?string("##0.00")}</fo:block>
		                					</fo:table-cell>
		                					<fo:table-cell>
		                						<fo:block text-align="right">${gradeDetails.fat?if_exists?string("##0.0")}</fo:block>
		                					</fo:table-cell>
		                					<fo:table-cell>
		                						<fo:block text-align="right">${gradeDetails.snf?if_exists?string("##0.0")}</fo:block>
		                					</fo:table-cell>
		                					<fo:table-cell>
		                						<fo:block text-align="center">${gradeDetails.grade}</fo:block>
		                					</fo:table-cell>
		                					<fo:table-cell>
		                						<#if unitId!="supervisor">
		                							<fo:block text-align="left">${Static["org.ofbiz.order.order.OrderServices"].nameTrim((StringUtil.wrapString(gradeDetails.superviser)),19)}</fo:block>
		                						<#else>
		                							<fo:block text-align="left">${gradeDetails.unit}</fo:block>
		                						</#if>
		                					</fo:table-cell>
		                					
		                				</fo:table-row>
		                			 
		                			</fo:table-body>
		                		</fo:table>
                		</fo:block>
                	</#if>		
                 </#list>
                 <fo:block font-size="8pt">-------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------</fo:block>
                 <#list totalsList as totals>
                 	<#if totals.qtyLtrs!=0>
                 	<fo:block keep-together="always">
		                		<fo:table font-family="Courier,monospace">
		                				<fo:table-column column-width="25pt"/>
		                				<fo:table-column column-width="80pt"/>
		                				<fo:table-column column-width="160pt"/>
		                				<fo:table-column column-width="40pt"/>
		                				<fo:table-column column-width="65pt"/>
		                				<fo:table-column column-width="65pt"/>
		                				<fo:table-column column-width="65pt"/>
		                				<fo:table-column column-width="65pt"/>
		                				<fo:table-column column-width="160pt"/>
		                			<fo:table-body>
		                			  <#if totals.milkType=="TOT">
		                			  		<fo:table-row>
		                			  			<fo:table-cell>
		                			  				<fo:block font-size="8pt">-------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------</fo:block>
		                			  			</fo:table-cell>
		                			  		</fo:table-row>
		                			  </#if>	
		                				<fo:table-row>
		                					<fo:table-cell>
		                					</fo:table-cell>
		                					<fo:table-cell>
		                					</fo:table-cell>
		                					<fo:table-cell>
		                					</fo:table-cell>
		                					<fo:table-cell>
	                							<fo:block text-align="left">${totals.milkType}</fo:block>
		                					</fo:table-cell>
		                					<fo:table-cell>
		                						<fo:block text-align="right">${totals.qtyLtrs?if_exists?string("##0.00")}</fo:block>
		                					</fo:table-cell>
		                					<fo:table-cell>
		                						<fo:block text-align="right">${totals.fat?if_exists?string("##0.0")}</fo:block>
		                					</fo:table-cell>
		                					<fo:table-cell>
		                						<fo:block text-align="right">${totals.snf?if_exists?string("##0.0")}</fo:block>
		                					</fo:table-cell>
		                					<fo:table-cell>
		                						<#if totals.grade?has_content>
		                							<fo:block text-align="center">${totals.grade}</fo:block>
		                					    </#if>		
		                					</fo:table-cell>
		                					<fo:table-cell>
		                					
		                					</fo:table-cell>
		                					
		                				</fo:table-row>
		                			 	<#if totals.milkType=="TOT">
		                			  		<fo:table-row>
		                			  			<fo:table-cell>
		                			  				<fo:block font-size="8pt">-------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------</fo:block>
		                			  			</fo:table-cell>
		                			  		</fo:table-row>
		                			  </#if>
		                			</fo:table-body>
		                		</fo:table>
                		</fo:block>
                		</#if>
                 </#list>	
            </fo:flow>
       </fo:page-sequence>
      
      <#else>
      	<fo:page-sequence master-reference="main">
           <fo:flow flow-name="xsl-region-body" font-family="Courier,monospace">
              <fo:block font-size="14pt">NO Records Found </fo:block>
           </fo:flow>
         </fo:page-sequence>
     </#if>  
   </fo:root> 
 </#escape>     
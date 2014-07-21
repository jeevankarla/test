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
            <fo:simple-page-master master-name="main" page-height="12in" page-width="18in"
                     margin-left="0.2in" margin-right="0.2in"  margin-top="0.2in" margin-bottom="0.2in" >
                <fo:region-body margin-top="1.5in"/>
                <fo:region-before extent="1in"/>
                <fo:region-after extent="1in"/>
            </fo:simple-page-master>
        </fo:layout-master-set>
        ${setRequestAttribute("OUTPUT_FILENAME", "mnthlyAttendencechlst.txt")}
        
		<fo:page-sequence master-reference="main">
        	<fo:static-content font-size="14pt" font-family="Courier,monospace"  flow-name="xsl-region-before" font-weight="bold">        
        		<fo:block text-align="left" white-space-collapse="false">&#160;                                               KARNATAKA CO-OPERATIVE MILK PRODUCERS FEDERATION LIMITED                       Page:<fo:page-number/></fo:block>
        		<fo:block text-align="center" keep-together="always" white-space-collapse="false">UNIT :  MOTHER DAIRY BANGALORE - 560065</fo:block>	 	 	  
        		 <fo:block text-align="center" keep-together="always">ATTENDANCE CHECK LIST FROM ${fromDate}  TO   ${thruDate}</fo:block>
        		 <fo:block text-align="left" keep-together="always"  >---------------------------------------------------------------------------------------------------------------------------------------</fo:block>
        		 <fo:block font-family="Courier,monospace">                
                <fo:table >
                    <fo:table-column column-width="40pt"/>
                    <fo:table-column column-width="60pt"/>
                    <fo:table-column column-width="80pt"/>                
                    <fo:table-column column-width="100pt"/>
                    <fo:table-column column-width="60pt"/>
                    <fo:table-column column-width="60pt"/>
                    <fo:table-column column-width="60pt"/>
                    <fo:table-column column-width="60pt"/>
                    <fo:table-column column-width="60pt"/>
                    <fo:table-column column-width="70pt"/>
                    <fo:table-column column-width="65pt"/>
                    <fo:table-column column-width="80pt"/>
                    <fo:table-column column-width="150pt"/>
                    <fo:table-column column-width="60pt"/>
                    <fo:table-column column-width="45pt"/>
                    <fo:table-column column-width="60pt"/>
                    <fo:table-column column-width="60pt"/>
                    <fo:table-column column-width="60pt"/>
                    <fo:table-body> 
                     <fo:table-row >
	                           <fo:table-cell >	
	                            	<fo:block text-align="center" keep-together="always" >S.No</fo:block>
	                            </fo:table-cell>
	                        	<fo:table-cell >	
	                            	<fo:block text-align="center" keep-together="always" >EmpNo</fo:block>    
	                            </fo:table-cell>	
	                        	<fo:table-cell >	
	                            	<fo:block text-align="center" keep-together="always" >Name</fo:block>    
	                            </fo:table-cell>
	                             <fo:table-cell>
	                             	<fo:block linefeed-treatment="preserve">&#xA;</fo:block>
	                             </fo:table-cell>		                                                    
	                            <fo:table-cell >
	                            	<fo:block text-align="center" keep-together="always">HALF</fo:block>
	                            	<fo:block text-align="left" white-space-collapse="false">&#160; PAY</fo:block>	
	                            </fo:table-cell>	                            
	                            <fo:table-cell >
	                            	<fo:block text-align="left" keep-together="always">BALANCE</fo:block>
	                            	<fo:block text-align="left" keep-together="always">HALFPAY</fo:block>	 
	                            </fo:table-cell>
	                            <fo:table-cell >
	                            	<fo:block text-align="center" keep-together="always">CASUAL</fo:block>
	                            	<fo:block text-align="center" keep-together="always">LEAVE</fo:block>	                               
	                            </fo:table-cell>	
		                        <fo:table-cell >
	                            	<fo:block text-align="center" keep-together="always">BALANCE</fo:block>
	                            	<fo:block text-align="center" keep-together="always">CL</fo:block>	                               
	                            </fo:table-cell>
	                            <fo:table-cell >
	                            	<fo:block text-align="center" keep-together="always">EARNED</fo:block>
	                            	<fo:block text-align="center" keep-together="always">LEAVE</fo:block>	                               
	                            </fo:table-cell>	
	                            <fo:table-cell >
	                            	<fo:block text-align="center" keep-together="always">BALANCE</fo:block>
	                            	<fo:block text-align="center" keep-together="always">EL</fo:block>	                               
	                            </fo:table-cell>
	                            <fo:table-cell >
	                            	<fo:block text-align="left" keep-together="always">HOLIDAYS</fo:block>	                               
	                            	<fo:block text-align="left" keep-together="always">WORKED</fo:block>
	                            </fo:table-cell>
	                            <fo:table-cell >
	                            	<fo:block text-align="center" keep-together="always">IInd SAT</fo:block>
	                            	<fo:block text-align="center" keep-together="always">WORKED</fo:block>	                               
	                            </fo:table-cell>
	                            <fo:table-cell >
	                            	<fo:block text-align="center" white-space-collapse="false" keep-together="always">&#160;SHIFT WORKED</fo:block>
	                            	<fo:block text-align="left" white-space-collapse="false" keep-together="always">&#160;I   II  III  GEN</fo:block>	                               
	                            </fo:table-cell>	
	                            <fo:table-cell >
	                            	<fo:block text-align="center" keep-together="always">COLD</fo:block>
	                            	<fo:block text-align="center" keep-together="always">DAYS</fo:block>	                               
	                            </fo:table-cell>
	                            <fo:table-cell >
	                            	<fo:block text-align="center" keep-together="always">CASH</fo:block>
	                            	<fo:block text-align="center" keep-together="always">DAYS</fo:block>	                               
	                            </fo:table-cell>
	                            <fo:table-cell >
	                            	<fo:block text-align="center" keep-together="always">PAYABLE</fo:block>
	                            	<fo:block text-align="center" keep-together="always">DAYS</fo:block>	                               
	                            </fo:table-cell>
	                        </fo:table-row>	
	                         <fo:table-row >
	                           <fo:table-cell >	
	             					<fo:block text-align="left" keep-together="always"  >---------------------------------------------------------------------------------------------------------------------------------------</fo:block>
	                            </fo:table-cell>
	                       </fo:table-row>
                    </fo:table-body>
                   </fo:table>
                  </fo:block>
        	</fo:static-content>
        	<fo:flow flow-name="xsl-region-body" font-family="Courier,monospace">
            	 <fo:block font-family="Courier,monospace"  font-size="14pt">
            	 <#assign sno=1>
            	 <#list employeeList as employee>
            	                 
                <fo:table >
                     <fo:table-column column-width="40pt"/>
                    <fo:table-column column-width="60pt"/>
                    <fo:table-column column-width="80pt"/>
                    <fo:table-column column-width="60pt"/>
                    <fo:table-column column-width="85pt"/>
                    <fo:table-column column-width="65pt"/>
                    <fo:table-column column-width="60pt"/>
                    <fo:table-column column-width="60pt"/>
                    <fo:table-column column-width="60pt"/>
                    <fo:table-column column-width="70pt"/>
                    <fo:table-column column-width="60pt"/>
                    <fo:table-column column-width="70pt"/>
                    <fo:table-column column-width="50pt"/>
                    <fo:table-column column-width="40pt"/>
                    <fo:table-column column-width="40pt"/>
                    <fo:table-column column-width="40pt"/>
                    <fo:table-column column-width="55pt"/>
                    <fo:table-column column-width="55pt"/>
                    <fo:table-column column-width="50pt"/>
                    <fo:table-body> 
                    
                    <fo:table-row>
                    <fo:table-cell><fo:block text-align="left" keep-together="always">${sno}</fo:block></fo:table-cell>
                    <fo:table-cell><fo:block text-align="center" keep-together="always">${employee.employeeId}</fo:block></fo:table-cell>
                    <fo:table-cell><fo:block text-align="left" keep-together="always">${employee.name}</fo:block></fo:table-cell>
                    <fo:table-cell><fo:block linefeed-treatment="preserve">&#xA;</fo:block></fo:table-cell>
                    <fo:table-cell><fo:block text-align="right" keep-together="always">${(employee.HPL)?if_exists?string("##0.0")}</fo:block></fo:table-cell>
                    <fo:table-cell><fo:block text-align="right" keep-together="always">${(employee.BHP)?if_exists?string("##0.0")}</fo:block></fo:table-cell>
                    <fo:table-cell><fo:block text-align="right" keep-together="always">${(employee.CL)?if_exists?string("##0.0")}</fo:block></fo:table-cell>
                    <fo:table-cell><fo:block text-align="right" keep-together="always">${employee.BCL?if_exists?string("##0.0")}</fo:block></fo:table-cell>
                    <fo:table-cell><fo:block text-align="right" keep-together="always">${(employee.EL)?if_exists?string("##0.0")}</fo:block></fo:table-cell>
                    <fo:table-cell><fo:block text-align="right" keep-together="always">${employee.BEL?if_exists?string("##0.0")}</fo:block></fo:table-cell>
                    <fo:table-cell><fo:block text-align="right" keep-together="always">${employee.workedHolidays?if_exists?string("##0.0")}</fo:block></fo:table-cell>
                    <fo:table-cell><fo:block text-align="right" keep-together="always">${employee.workedSsDays?if_exists?string("##0.0")}</fo:block></fo:table-cell>
                    <fo:table-cell><fo:block text-align="right" keep-together="always">${employee.shift_01?if_exists?string("##0.0")}</fo:block></fo:table-cell>
                    <fo:table-cell><fo:block text-align="right" keep-together="always">${employee.shift_02?if_exists?string("##0.0")}</fo:block></fo:table-cell>
                    <fo:table-cell><fo:block text-align="right" keep-together="always">${employee.shift_03?if_exists?string("##0.0")}</fo:block></fo:table-cell>
                    <fo:table-cell><fo:block text-align="right" keep-together="always">${employee.shift_gen?if_exists?string("##0.0")}</fo:block></fo:table-cell>
                    <fo:table-cell><fo:block text-align="right" keep-together="always">${(employee.cldallow)?if_exists?string("##0.0")}</fo:block></fo:table-cell>
                    <fo:table-cell><fo:block text-align="right" keep-together="always">${(employee.cashallow)?if_exists?string("##0.0")}</fo:block></fo:table-cell>
                    <fo:table-cell><fo:block text-align="right" keep-together="always">${(employee.payableDays)?if_exists?string("##0.0")}</fo:block></fo:table-cell>
                    <#assign sno=sno+1>
                    </fo:table-row>
                    </fo:table-body>
                   </fo:table>
                   
                   </#list>
                 </fo:block>
           </fo:flow>
        </fo:page-sequence>
     </fo:root>
</#escape>
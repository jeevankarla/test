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
            <fo:simple-page-master master-name="main" page-height="12in" page-width="20in"
                     margin-left="0.09in" margin-right="0.2in"  margin-top="0.09in" margin-bottom="0.2in" >
                <fo:region-body margin-top="1.6in"/>
                <fo:region-before extent="1in"/>
                <fo:region-after extent="1in"/>
            </fo:simple-page-master>
        </fo:layout-master-set>
        ${setRequestAttribute("OUTPUT_FILENAME", "mnthlyAttendencechlst.txt")}
        
		<fo:page-sequence master-reference="main">
        	<fo:static-content font-size="14pt" font-family="Courier,monospace"  flow-name="xsl-region-before">        
        		<fo:block text-align="left" white-space-collapse="false"  font-weight="bold">&#160;                                                        KARNATAKA CO-OPERATIVE MILK PRODUCERS FEDERATION LIMITED                       Page:<fo:page-number/></fo:block>
        		<fo:block text-align="center" keep-together="always" white-space-collapse="false"  font-weight="bold">UNIT :  MOTHER DAIRY BANGALORE - 560065</fo:block>	 	 	  
        		 <fo:block text-align="center" keep-together="always"  font-weight="bold">ATTENDANCE CHECK LIST FROM ${fromDate}  TO   ${thruDate}</fo:block>
        		 <fo:block text-align="left" keep-together="always"  font-weight="bold" >--------------------------------------------------------------------------------------------------------------------------------------------------------------------------</fo:block>
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
                    <fo:table-column column-width="50pt"/>
                    <fo:table-column column-width="48pt"/>
                    <fo:table-column column-width="45pt"/>
                    <fo:table-column column-width="48pt"/>
                    <fo:table-column column-width="65pt"/>
                    <fo:table-column column-width="45pt"/>
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
	                            	<fo:block text-align="center" keep-together="always">LATE</fo:block>
	                            	<fo:block text-align="center" keep-together="always">HOURS</fo:block>	                               
	                            </fo:table-cell>
	                            <fo:table-cell >
	                            	<fo:block text-align="center" keep-together="always">EARLY</fo:block>
	                            	<fo:block text-align="center" keep-together="always">HOURS</fo:block>	                               
	                            </fo:table-cell>
	                            <fo:table-cell >
	                            	<fo:block text-align="center" keep-together="always">EXTRA</fo:block>
	                            	<fo:block text-align="center" keep-together="always">HOURS</fo:block>	                               
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
	                            	<fo:block text-align="center" keep-together="always">HOT</fo:block>
	                            	<fo:block text-align="center" keep-together="always">DAYS</fo:block>	                               
	                            </fo:table-cell>
	                             <fo:table-cell >
	                            	<fo:block text-align="center" keep-together="always">ATTN</fo:block>
	                            	<fo:block text-align="center" keep-together="always">BONS</fo:block>	                               
	                            </fo:table-cell>
	                            <fo:table-cell >
	                            	<fo:block text-align="center" keep-together="always">ARR</fo:block>
	                            	<fo:block text-align="center" keep-together="always">DAYS</fo:block>	                               
	                            </fo:table-cell>
	                            <fo:table-cell >
	                            	<fo:block text-align="center" keep-together="always">PAYABLE</fo:block>
	                            	<fo:block text-align="center" keep-together="always">DAYS</fo:block>	                               
	                            </fo:table-cell>
	                        </fo:table-row>	
	                         <fo:table-row >
	                           <fo:table-cell >	
	             <fo:block text-align="left" keep-together="always"  font-weight="bold" >--------------------------------------------------------------------------------------------------------------------------------------------------------------------------</fo:block>
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
                    <fo:table-column column-width="50pt"/>
                    <fo:table-column column-width="55pt"/>
                    <fo:table-column column-width="53pt"/>
                    <fo:table-column column-width="51pt"/>
                    <fo:table-column column-width="51pt"/>
                    <fo:table-column column-width="50pt"/>
                    <fo:table-column column-width="50pt"/>
                    <fo:table-column column-width="50pt"/>
                    <fo:table-column column-width="60pt"/>
                    <fo:table-column column-width="50pt"/>
                    <fo:table-body> 
                    <fo:table-row>
                    <fo:table-cell><fo:block linefeed-treatment="preserve">&#xA;</fo:block></fo:table-cell>
                    </fo:table-row>
                    <fo:table-row>
                    <fo:table-cell><fo:block text-align="left" keep-together="always">${sno}</fo:block></fo:table-cell>
                    <fo:table-cell><fo:block text-align="center" keep-together="always">${employee.employeeId}</fo:block></fo:table-cell>
                    <fo:table-cell><fo:block text-align="left" keep-together="always">${(employee.name)?upper_case}</fo:block></fo:table-cell>
                    <fo:table-cell><fo:block linefeed-treatment="preserve">&#xA;</fo:block></fo:table-cell>
                    <#assign HPL=0>
                    <#assign HPL=employee.HPL>
                    <fo:table-cell><fo:block text-align="right" keep-together="always"><#if HPL !=0>${(HPL)?if_exists?string("##0.0")}</#if></fo:block></fo:table-cell>
                    <#assign BHP=0>
                    <#assign BHP=employee.BHP>
                    <fo:table-cell><fo:block text-align="right" keep-together="always"><#if BHP !=0>${(BHP)?if_exists?string("##0.0")}</#if></fo:block></fo:table-cell>
                    <#assign CL=0>
                    <#assign CL=employee.CL>
                    <fo:table-cell><fo:block text-align="right" keep-together="always"><#if CL !=0>${(CL)?if_exists?string("##0.0")}</#if></fo:block></fo:table-cell>
                    <#assign BCL=0>
                    <#assign BCL=employee.BCL>
                    <fo:table-cell><fo:block text-align="right" keep-together="always"><#if BCL !=0>${BCL?if_exists?string("##0.0")}</#if></fo:block></fo:table-cell>
                    <#assign EL=0>
                    <#assign EL=employee.EL>
                    <fo:table-cell><fo:block text-align="right" keep-together="always"><#if EL !=0>${(EL)?if_exists?string("##0.0")}</#if></fo:block></fo:table-cell>
                    <#assign BEL=0>
                    <#assign BEL=employee.BEL>
                    <fo:table-cell><fo:block text-align="right" keep-together="always"><#if BEL !=0>${BEL?if_exists?string("##0.0")}</#if></fo:block></fo:table-cell>
                    <#assign HDays=0>
                    <#assign HDays=employee.workedHolidays>
                    <fo:table-cell><fo:block text-align="right" keep-together="always"><#if HDays !=0>${HDays?if_exists?string("##0.0")}</#if></fo:block></fo:table-cell>
                    <#assign SsDays=0>
                    <#assign SsDays=employee.workedSsDays>
                    <fo:table-cell><fo:block text-align="right" keep-together="always"><#if SsDays !=0>${SsDays?if_exists?string("##0.0")}</#if></fo:block></fo:table-cell>
                    <#assign shift_01=0>
                    <#assign shift_01=employee.SHIFT_01>
                    <fo:table-cell><fo:block text-align="right" keep-together="always"><#if shift_01 !=0>${shift_01?if_exists?string("##0.0")}</#if></fo:block></fo:table-cell>
                    <#assign shift_02=0>
                    <#assign shift_02=employee.SHIFT_02>
                    <fo:table-cell><fo:block text-align="right" keep-together="always"><#if shift_02 !=0>${shift_02?if_exists?string("##0.0")}</#if></fo:block></fo:table-cell>
                    <#assign shift_03=0>
                    <#assign shift_03=employee.SHIFT_03>
                    <fo:table-cell><fo:block text-align="right" keep-together="always"><#if shift_03 !=0>${shift_03?if_exists?string("##0.0")}</#if></fo:block></fo:table-cell>
                    <#assign shift_gen=0>
                    <#assign shift_gen=employee.SHIFT_GEN>
                    <fo:table-cell><fo:block text-align="right" keep-together="always"><#if shift_gen !=0>${shift_gen?if_exists?string("##0.0")}</#if></fo:block></fo:table-cell>
                    <#assign lateHours=0>
                    <#assign lateHours=(employee.lateMin)/60>
                    <fo:table-cell><fo:block text-align="right" keep-together="always"><#if lateHours !=0>${lateHours?if_exists?string("##0.0")}</#if></fo:block></fo:table-cell>
                    
                    <fo:table-cell><fo:block text-align="right" keep-together="always"></fo:block></fo:table-cell>
                    <#assign extraHours=0>
                    <#assign extraHours=(employee.extraMin)/60>
                    <fo:table-cell><fo:block text-align="right" keep-together="always"><#if extraHours !=0>${extraHours?if_exists?string("##0.0")}</#if></fo:block></fo:table-cell>
                    <fo:table-cell><fo:block text-align="right" keep-together="always"><#if employee.cldDays !=0>${(employee.cldDays)?if_exists?string("##0.0")}</#if></fo:block></fo:table-cell>
                    <fo:table-cell><fo:block text-align="right" keep-together="always"><#if employee.caDays !=0>${(employee.caDays)?if_exists?string("##0.0")}</#if></fo:block></fo:table-cell>
                    <fo:table-cell><fo:block text-align="right" keep-together="always"><#if employee.heatDays !=0>${(employee.heatDays)?if_exists?string("##0.0")}</#if></fo:block></fo:table-cell>
                    <fo:table-cell><fo:block text-align="right" keep-together="always"></fo:block></fo:table-cell>
                    <fo:table-cell><fo:block text-align="right" keep-together="always"><#if employee.arrearDays !=0>${employee.arrearDays?if_exists?string("##0.0")}</#if></fo:block></fo:table-cell>
                    <fo:table-cell><fo:block text-align="right" keep-together="always"><#if employee.payableDays !=0>${(employee.payableDays)?if_exists?string("##0.0")}</#if></fo:block></fo:table-cell>
                    </fo:table-row>
                    <#assign sno=sno+1>
                    </fo:table-body>
                   </fo:table>
                   </#list>
                   </fo:block>
                    <fo:block font-family="Courier,monospace"  font-size="14pt">
                	<fo:table >
                     	<fo:table-column column-width="100pt"/>
                     	<fo:table-column column-width="100pt"/>
                     	<fo:table-column column-width="100pt"/>
                     	<fo:table-column column-width="50pt"/>
                     	<fo:table-column column-width="70pt"/>
                     	<fo:table-column column-width="100pt"/>
                     	<fo:table-column column-width="100pt"/>
                     	<fo:table-column column-width="100pt"/>
                     	<fo:table-column column-width="100pt"/>
                     	<fo:table-column column-width="100pt"/>
                     	<fo:table-column column-width="100pt"/>
                     	<fo:table-column column-width="100pt"/>
                     	<fo:table-column column-width="100pt"/>
                     	<fo:table-column column-width="100pt"/>
                     	<fo:table-body>
	                     	<fo:table-row><fo:table-cell><fo:block linefeed-treatment="preserve">&#xA;</fo:block></fo:table-cell>
	                     	</fo:table-row>
                     		<fo:table-row>
                     			<fo:table-cell><fo:block linefeed-treatment="preserve">&#xA;</fo:block></fo:table-cell>
                     			<fo:table-cell><fo:block linefeed-treatment="preserve">&#xA;</fo:block></fo:table-cell>
                     			<fo:table-cell><fo:block text-align="center" white-space-collapse="false" >&#160;&#160;&#160;&#160;&#160;                      CASE WORKER	                   			</fo:block></fo:table-cell>
                     			<fo:table-cell><fo:block linefeed-treatment="preserve">&#xA;</fo:block></fo:table-cell>
                     			<fo:table-cell><fo:block linefeed-treatment="preserve">&#xA;</fo:block></fo:table-cell>
                     			<fo:table-cell><fo:block linefeed-treatment="preserve">&#xA;</fo:block></fo:table-cell>
                    			<fo:table-cell><fo:block text-align="left" white-space-collapse="false" >                                                                 A.D.O/A.M(Admin)			</fo:block></fo:table-cell>
                    			<fo:table-cell><fo:block linefeed-treatment="preserve">&#xA;</fo:block></fo:table-cell>
                    			<fo:table-cell><fo:block linefeed-treatment="preserve">&#xA;</fo:block></fo:table-cell>
                    			<fo:table-cell><fo:block text-align="right" white-space-collapse="false" >&#160;                                                    							   PRE AUDITOR       
                    			</fo:block></fo:table-cell>
                    			<fo:table-cell><fo:block linefeed-treatment="preserve">&#xA;</fo:block></fo:table-cell>
                    			<fo:table-cell><fo:block linefeed-treatment="preserve">&#xA;</fo:block></fo:table-cell>
                    			<fo:table-cell><fo:block text-align="right" white-space-collapse="false" >&#160;                                                    							  DIRECTOR       
                    			</fo:block></fo:table-cell>
                   			</fo:table-row>
                     	</fo:table-body>
                     </fo:table>
                 </fo:block>
           </fo:flow>
        </fo:page-sequence>
     </fo:root>
</#escape>
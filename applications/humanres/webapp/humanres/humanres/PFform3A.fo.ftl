<#escape x as x?xml>
    <fo:root xmlns:fo="http://www.w3.org/1999/XSL/Format">
        <fo:layout-master-set>
            <fo:simple-page-master master-name="main" page-height="12in" page-width="10in"
                     margin-left="0.2in" margin-right="0.2in"  margin-top="0.2in" margin-bottom="0.2in" >
                <fo:region-body margin-top="1.6in"/>
                <fo:region-before extent="1in"/>
                <fo:region-after extent="1in"/>
            </fo:simple-page-master>
        </fo:layout-master-set>
        ${setRequestAttribute("OUTPUT_FILENAME", "EmployeeMisPunchData.pdf")}
		
		<#if employeeWiseMap?has_content>
			<#assign noofLines=1>
			<#assign SNo=1>
			<fo:page-sequence master-reference="main">
        		<fo:static-content font-family="Helvetica"  flow-name="xsl-region-before" font-weight="bold">        
	        		<fo:block  keep-together="always" text-align="center" font-family="Helvetica" white-space-collapse="false" font-weight="bold" font-size="12pt">&#160; THE EMPLOYEES PROVIDUNT FUND SCHEME, 1952</fo:block>
					<fo:block  keep-together="always" text-align="center" font-family="Helvetica" white-space-collapse="false" font-weight="bold" font-size="12pt">&#160; (Paras 35 and 42 )  </fo:block>
	        		<fo:block  keep-together="always" text-align="center" font-family="Helvetica" white-space-collapse="false" font-weight="bold" font-size="12pt">&#160; AND THE EMPLOYEES PENSION SCHEME 1995[Para 19]     </fo:block>
	        		<fo:block  keep-together="always" text-align="center" font-family="Helvetica" white-space-collapse="false" font-weight="bold" font-size="12pt">&#160; (Para 14 )</fo:block>
	        		<fo:block  keep-together="always" text-align="center" font-family="Helvetica" white-space-collapse="false" font-weight="bold" font-size="13pt">&#160; FORM 3A (REVISED)     </fo:block>
	        		<fo:block  keep-together="always" text-align="center" font-family="Helvetica" white-space-collapse="false" font-weight="bold">&#160;      </fo:block>  
	        		<fo:block  keep-together="always" text-align="left" font-family="Helvetica" white-space-collapse="false" font-weight="bold" font-size="11pt">&#160;Statement of contribution for the period from ${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(fromDate, "dd-MMMMM-yyyy")}  to ${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(thruDate, "dd-MMMMM-yyyy")}</fo:block> 
          			<fo:block  keep-together="always" text-align="center" font-family="Helvetica" white-space-collapse="false" font-weight="bold">&#160;      </fo:block>
          		</fo:static-content>
          		<fo:flow flow-name="xsl-region-body" font-family="Helvetica">
          			<#assign employeeWiseDetails = employeeWiseMap.entrySet()>
	            	<#list employeeWiseDetails as employeeVal>
	            		<fo:block font-family="Helvetica">
		                	<fo:table >
		                		<fo:table-column column-width="350pt"/>
			                	<fo:table-column column-width="300pt"/>
		                		<fo:table-body> 
		                			<#assign employeeDetails = employeeMap.entrySet()>
		            				<#list employeeDetails as employee>
		                				<#if employeeVal.getKey() == employee.getKey()>
				                     		<fo:table-row >
				                     			<fo:table-cell >	
					                            	<fo:block text-align="left" keep-together="always" font-weight="bold" font-size="11pt">1. Account Number : ${employee.getValue().get("pfAccNo")?if_exists}</fo:block>
					                            </fo:table-cell>
					                            <fo:table-cell >	
					                            	<fo:block text-align="left" keep-together="always" font-weight="bold" font-size="11pt">5. Statutory Rate of contribution &#160;&#160;&#160;: 12 %</fo:block>
					                            </fo:table-cell>
						                   	</fo:table-row>
						                   	<fo:table-row >
					                            <fo:table-cell >	
					                            	<fo:block text-align="left" keep-together="always" font-weight="bold" font-size="11pt">2. Name / Surname : ${employee.getValue().get("employeeName")}</fo:block>
					                            </fo:table-cell>
					                            <fo:table-cell >	
					                            	<fo:block text-align="left" keep-together="always" font-weight="bold" font-size="11pt">6. Voluntary Higher Rate of &#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;: No</fo:block>
					                            </fo:table-cell>
						                   	</fo:table-row>
						                   	<fo:table-row >
					                            <fo:table-cell >	
					                            	<fo:block text-align="left" keep-together="always" font-weight="bold" font-size="11pt">3. Father's/Husband's Name : <#if employee.getValue().get("fatherName")?has_content> ${employee.getValue().get("fatherName")}<#else>&#160;</#if></fo:block>
					                            </fo:table-cell>
					                            <fo:table-cell >	
					                            	<fo:block text-align="left" keep-together="always" font-weight="bold" font-size="11pt">&#160;&#160;&#160; Employee's Contribution if any &#160;&#160;: </fo:block>
					                            </fo:table-cell>
						                   	</fo:table-row>
						                   	<fo:table-row >
					                            <fo:table-cell >	
					                            	<fo:block text-align="left" font-weight="bold" font-size="11pt">4. Name and Address of the Factory / Establishment :&#160;&#160;&#160;    ${uiLabelMap.KMFDairySubHeader}</fo:block>
					                            </fo:table-cell>
					                            <fo:table-cell >	
					                            	<fo:block text-align="left" keep-together="always" font-weight="bold" font-size="11pt">7. Employee Code : ${employee.getKey()}</fo:block>
					                            </fo:table-cell>
						                   	</fo:table-row>
						            	</#if>
						    		</#list>
				             	</fo:table-body>
	                     	</fo:table>
	                  	</fo:block>
						<fo:block>&#160;</fo:block>
						<fo:block>
	                     	<fo:table border-style = "solid">
		                		<fo:table-column column-width="120pt"/>
			                	<fo:table-column column-width="70pt"/>
			                	<fo:table-column column-width="327pt"/>
			                	<fo:table-column column-width="60pt"/>
			                	<fo:table-column column-width="60pt"/>
			                	<fo:table-column column-width="53pt"/>
		                		<fo:table-body> 
	                     			<fo:table-row >
		                     			<fo:table-cell border-right-style = "solid">	
			                            	<fo:block text-align="center"  font-weight="bold" font-size="10pt" line-height = "13pt">Return Month</fo:block>
			                            </fo:table-cell>
			                            <fo:table-cell border-right-style = "solid">	
			                            	<fo:block text-align="center" font-weight="bold" font-size="10pt" line-height = "13pt">Amount of Wages  </fo:block>
			                            </fo:table-cell>
			                            <fo:table-cell border-right-style = "solid">	
			                            	<fo:block text-align="center"  font-weight="bold" font-size="10pt" line-height = "13pt">CONTRIBUTIONS</fo:block>
			                            	<fo:block>
			                            		<fo:table border-style = "solid">
			                            			<fo:table-column column-width="90pt"/>
								                	<fo:table-column column-width="146pt"/>
								                	<fo:table-column column-width="90pt"/>
								                	<fo:table-body> 
						                     			<fo:table-row >
							                     			<fo:table-cell border-right-style = "solid" border-bottom-style = "solid">	
								                            	<fo:block text-align="center"  font-weight="bold" font-size="10pt" line-height = "15pt">Worker's Share</fo:block>
								                            </fo:table-cell>
								                            <fo:table-cell border-right-style = "solid">	
								                            	<fo:block text-align="center"  font-weight="bold" font-size="10pt" line-height = "15pt">Employer's Share</fo:block>
								                            </fo:table-cell>
								                            <fo:table-cell >	
								                            	<fo:block text-align="center"  font-weight="bold" font-size="10pt">Total</fo:block>
								                            	<fo:block text-align="center"  font-weight="bold" font-size="10pt" line-height = "13pt">(col3+4a+4b)</fo:block>
								                            </fo:table-cell>
								                		</fo:table-row>
								                		<fo:table-row >
							                     			<fo:table-cell >	
								                            	<fo:block text-align="center"  font-weight="bold" font-size="10pt" line-height = "18pt">E.P.F. + V.P.F</fo:block>
								                            </fo:table-cell>
								                            <fo:table-cell>	
								                            	<fo:block>
								                            		<fo:table >
								                            			<fo:table-column column-width="73pt"/>
													                	<fo:table-column column-width="73pt"/>
													                	<fo:table-body> 
											                     			<fo:table-row >
												                     			<fo:table-cell border-right-style = "solid" border-left-style = "solid" border-top-style = "solid">	
													                            	<fo:block text-align="center"  font-weight="bold" font-size="10pt" line-height = "12pt">E.P.F. Difference between 10/12% and 81/3%</fo:block>
													                            </fo:table-cell>
													                            <fo:table-cell border-right-style = "solid" border-top-style = "solid">	
													                            	<fo:block text-align="center"  font-weight="bold" font-size="10pt" line-height = "13pt">Pension Fund</fo:block>
													                            </fo:table-cell>
													                		</fo:table-row>
									              						</fo:table-body>
								                            		</fo:table>
								                            	</fo:block>
								                            </fo:table-cell>
								                		</fo:table-row>
				              						</fo:table-body>
			                            		</fo:table>
			                            	</fo:block>
			                            </fo:table-cell>
			                            <fo:table-cell border-right-style = "solid">	
			                            	<fo:block text-align="center" font-weight="bold" font-size="10pt" line-height = "13pt">&#160;</fo:block>
			                            	<fo:block text-align="center" font-weight="bold" font-size="10pt" line-height = "13pt">Refund of Advance</fo:block>
			                            </fo:table-cell>
			                            <fo:table-cell border-right-style = "solid">	
			                            	<fo:block text-align="center" font-weight="bold" font-size="10pt" line-height = "13pt">&#160;</fo:block>
			                            	<fo:block text-align="center"  font-weight="bold" font-size="10pt" line-height = "13pt">Break in Membership reckonable service</fo:block>
			                            </fo:table-cell>
			                            <fo:table-cell >
			                            	<fo:block text-align="center" font-weight="bold" font-size="10pt" line-height = "13pt">&#160;</fo:block>	
			                            	<fo:block text-align="center" font-weight="bold" font-size="10pt" line-height = "13pt">Remarks</fo:block>
			                            </fo:table-cell>
				                   	</fo:table-row>
				              	</fo:table-body>
	                     	</fo:table>	
				            <fo:table border-style = "solid">
		                		<fo:table-column column-width="120pt"/>
			                	<fo:table-column column-width="70pt"/>
			                	<fo:table-column column-width="90pt"/>
			                	<fo:table-column column-width="73pt"/>
								<fo:table-column column-width="73pt"/>
			                	<fo:table-column column-width="90pt"/>
			                	<fo:table-column column-width="60pt"/>
			                	<fo:table-column column-width="60pt"/>
			                	<fo:table-column column-width="53pt"/>
			                	<fo:table-body>
			                		<fo:table-row >
		                     			<fo:table-cell border-right-style = "solid" border-bottom-style = "solid">	
			                            	<fo:block text-align="center"  font-weight="bold" font-size="10pt" line-height = "18pt">1 </fo:block>
			                            </fo:table-cell>
			                            <fo:table-cell border-right-style = "solid" border-bottom-style = "solid">	
			                            	<fo:block text-align="center"  font-weight="bold" font-size="10pt" line-height = "18pt">2</fo:block>
			                            </fo:table-cell>
			                            <fo:table-cell border-right-style = "solid" border-bottom-style = "solid">	
			                            	<fo:block text-align="center"  font-weight="bold" font-size="10pt" line-height = "18pt">3</fo:block>
			                            </fo:table-cell>
			                            <fo:table-cell border-right-style = "solid" border-bottom-style = "solid">	
			                            	<fo:block text-align="center"  font-weight="bold" font-size="10pt" line-height = "18pt">4a</fo:block>
			                            </fo:table-cell>
			                            <fo:table-cell border-right-style = "solid" border-bottom-style = "solid">	
			                            	<fo:block text-align="center"  font-weight="bold" font-size="10pt" line-height = "18pt">4b</fo:block>
			                            </fo:table-cell>
			                            <fo:table-cell border-right-style = "solid" border-bottom-style = "solid">	
			                            	<fo:block text-align="center"  font-weight="bold" font-size="10pt" line-height = "18pt">5</fo:block>
			                            </fo:table-cell>
			                            <fo:table-cell border-right-style = "solid" border-bottom-style = "solid">	
			                            	<fo:block text-align="center"  font-weight="bold" font-size="10pt" line-height = "18pt">6</fo:block>
			                            </fo:table-cell>
			                            <fo:table-cell border-right-style = "solid" border-bottom-style = "solid">	
			                            	<fo:block text-align="center"  font-weight="bold" font-size="10pt" line-height = "18pt">7</fo:block>
			                            </fo:table-cell>
			                            <fo:table-cell border-bottom-style = "solid">	
			                            	<fo:block text-align="center"  font-weight="bold" font-size="10pt" line-height = "18pt">8</fo:block>
			                            </fo:table-cell>
			                    	</fo:table-row>
			                    	<#assign totalWages =0>
			                    	<#assign totalWorkShare =0>
			                    	<#assign totalEPF =0>
			                    	<#assign totalFPF =0>
			                    	<#assign grandTot = 0>
		                			<#assign employeeMonthWiseDet = employeeVal.getValue().entrySet()>
		                			<#list employeeMonthWiseDet as monthDetails>
		                				<#assign monthNameMapDet = monthNameMap.entrySet()>
		                				<#list monthNameMapDet as monthName>
		                					<#if monthName.getKey() == monthDetails.getKey()>
		                						<#assign total = 0>
				                     			<fo:table-row >
					                     			<fo:table-cell border-right-style = "solid" border-bottom-style = "solid">	
					                     				<#assign currMonthNameVal = currMonthNameMap.entrySet()>
						                				<#list currMonthNameVal as currMonthName>
						                					<#if currMonthName.getKey() == monthDetails.getKey()>
						                            			<fo:block text-align="left"  font-weight="bold" font-size="10pt" line-height = "18pt">&#160;${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(monthName.getValue(), "MMM")}'${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(monthName.getValue(), "yy")} paid in ${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(currMonthName.getValue(), "MMM")}'${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(currMonthName.getValue(), "yy")}</fo:block>
						                            		</#if>
						                            	</#list>
						                            </fo:table-cell>
						                            <fo:table-cell border-right-style = "solid" border-bottom-style = "solid">	
						                            	<fo:block text-align="right"  font-weight="bold" font-size="10pt" line-height = "18pt">${monthDetails.getValue().get("monthTotWages")?if_exists?string("#0.00")}&#160;&#160; </fo:block>
						                            	<#assign totalWages = totalWages + monthDetails.getValue().get("monthTotWages")>
						                            </fo:table-cell>
						                            <fo:table-cell border-right-style = "solid" border-bottom-style = "solid">	
						                            	<fo:block text-align="right"  font-weight="bold" font-size="10pt" line-height = "18pt">${monthDetails.getValue().get("workerShare")?if_exists?string("#0.00")}&#160;&#160; </fo:block>
						                            	<#assign total = total + monthDetails.getValue().get("workerShare")>
						                            	<#assign totalWorkShare = totalWorkShare + monthDetails.getValue().get("workerShare")>
						                            </fo:table-cell>
						                            <fo:table-cell border-right-style = "solid" border-bottom-style = "solid">	
						                            	<fo:block text-align="right"  font-weight="bold" font-size="10pt" line-height = "18pt">${monthDetails.getValue().get("employerEpf")?if_exists?string("#0.00")}&#160;&#160; </fo:block>
						                            	<#assign total = total + monthDetails.getValue().get("employerEpf")>
						                            	<#assign totalEPF = totalEPF + monthDetails.getValue().get("employerEpf")>
						                            </fo:table-cell>
						                            <fo:table-cell border-right-style = "solid" border-bottom-style = "solid">	
						                            	<fo:block text-align="right"  font-weight="bold" font-size="10pt" line-height = "18pt">${monthDetails.getValue().get("employerFpf")?if_exists?string("#0.00")}&#160;&#160; </fo:block>
						                           		<#assign total = total + monthDetails.getValue().get("employerFpf")>
						                           		<#assign totalFPF = totalFPF + monthDetails.getValue().get("employerFpf")>
						                            </fo:table-cell>
						                            <fo:table-cell border-right-style = "solid" border-bottom-style = "solid">	
						                            	<fo:block text-align="right"  font-weight="bold" font-size="10pt" line-height = "18pt">${total?if_exists?string("#0.00")}&#160;&#160; </fo:block>
						                            	<#assign grandTot = grandTot + total>
						                            </fo:table-cell>
						                            <fo:table-cell border-right-style = "solid" border-bottom-style = "solid">
						                            	<fo:block text-align="right"  font-weight="bold" font-size="10pt" line-height = "18pt">0.00&#160;&#160;</fo:block>
						                            </fo:table-cell>
						                            <fo:table-cell border-right-style = "solid" border-bottom-style = "solid">	
						                            	<fo:block text-align="right"  font-weight="bold" font-size="10pt" line-height = "18pt">&#160;&#160;</fo:block>
						                            </fo:table-cell>
						                            <fo:table-cell border-bottom-style = "solid">	
						                            	<fo:block text-align="right"  font-weight="bold" font-size="10pt" line-height = "18pt">&#160;&#160;</fo:block>
						                            </fo:table-cell>
						                    	</fo:table-row>
						                    </#if>
					                   	</#list>
					              	</#list>
					              	<fo:table-row >
		                     			<fo:table-cell border-right-style = "solid" border-bottom-style = "solid">	
			                            	<fo:block text-align="center"  font-weight="bold" font-size="10pt" line-height = "18pt">Total :</fo:block>
			                            </fo:table-cell>
			                            <fo:table-cell border-right-style = "solid" border-bottom-style = "solid">	
			                            	<fo:block text-align="right"  font-weight="bold" font-size="10pt" line-height = "18pt">${totalWages?if_exists?string("#0.00")}&#160;&#160;</fo:block>
			                            </fo:table-cell>
			                            <fo:table-cell border-right-style = "solid" border-bottom-style = "solid">	
			                            	<fo:block text-align="right"  font-weight="bold" font-size="10pt" line-height = "18pt">${totalWorkShare?if_exists?string("#0.00")}&#160;&#160;</fo:block>
			                            </fo:table-cell>
			                            <fo:table-cell border-right-style = "solid" border-bottom-style = "solid">	
			                            	<fo:block text-align="right"  font-weight="bold" font-size="10pt" line-height = "18pt">${totalEPF?if_exists?string("#0.00")}&#160;&#160;</fo:block>
			                            </fo:table-cell>
			                            <fo:table-cell border-right-style = "solid" border-bottom-style = "solid">	
			                            	<fo:block text-align="right"  font-weight="bold" font-size="10pt" line-height = "18pt">${totalFPF?if_exists?string("#0.00")}&#160;&#160;</fo:block>
			                            </fo:table-cell>
			                            <fo:table-cell border-right-style = "solid" border-bottom-style = "solid">	
			                            	<fo:block text-align="right"  font-weight="bold" font-size="10pt" line-height = "18pt">${grandTot?if_exists?string("#0.00")}&#160;&#160;</fo:block>
			                            </fo:table-cell>
			                            <fo:table-cell border-right-style = "solid" border-bottom-style = "solid">	
			                            	<fo:block text-align="center"  font-weight="bold" font-size="10pt" line-height = "18pt">&#160;</fo:block>
			                            </fo:table-cell>
			                            <fo:table-cell border-right-style = "solid" border-bottom-style = "solid">	
			                            	<fo:block text-align="center"  font-weight="bold" font-size="10pt" line-height = "18pt">&#160;</fo:block>
			                            </fo:table-cell>
			                            <fo:table-cell border-bottom-style = "solid">	
			                            	<fo:block text-align="center"  font-weight="bold" font-size="10pt" line-height = "18pt">&#160;</fo:block>
			                            </fo:table-cell>
			                    	</fo:table-row> 
				              	</fo:table-body>
	                     	</fo:table>
	                  	</fo:block>	
	                  	<fo:block font-size="10pt" font-weight="bold">&#160;</fo:block>
                  		<fo:block font-size="10pt" font-weight="bold">&#160;</fo:block>
                 	  	<fo:block font-size="10pt" font-weight="bold">Certified that total amount of contribution(both shares) indicated in this card i.e. Rs.${(totalWorkShare + totalEPF)?if_exists?string("#0.00")} has remitted in full in E.P.F A/c No.1 and Pension Fund A/c No. 10 Rs. ${totalFPF?if_exists?string("#0.00")}(vide noted below)</fo:block>
                  		<fo:block font-size="10pt" font-weight="bold">&#160;</fo:block>
                  		<fo:block font-size="10pt" font-weight="bold">&#160;</fo:block>
                  		<fo:block font-size="10pt" font-weight="bold">Certified that the difference between the total of the contribution shown under cols. 3 and 4a and 4b of the above table and that arrived at on the total wages shown in column 2 at the prescribed rate is solely due to rounding of contributions to the nearest rupee under the rules.</fo:block>
                  		<fo:block font-size="10pt" font-weight="bold">&#160;</fo:block>
                  		<fo:block font-size="10pt" font-weight="bold">&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;Date&#160;&#160; : ${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(nowTimestamp, "dd-MMM-yyyy")}</fo:block>
                  		<fo:block font-size="10pt" font-weight="bold">&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;Place : Bangalore</fo:block>
                  		<fo:block font-size="10pt" font-weight="bold" text-align="right">Signature of Employer with Office Seal&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;</fo:block>  
                  		<fo:block page-break-after="always"></fo:block>    
                  		
                  		
	              	</#list>
				</fo:flow>
  			</fo:page-sequence>
  		<#else>
	  		<fo:page-sequence master-reference="main">
		    	<fo:flow flow-name="xsl-region-body" font-family="Helvetica">
		       		 <fo:block font-size="14pt">
		            	${uiLabelMap.NoEmployeeFound}.
		       		 </fo:block>
		    	</fo:flow>
			</fo:page-sequence>
  		</#if>
     </fo:root>
</#escape>
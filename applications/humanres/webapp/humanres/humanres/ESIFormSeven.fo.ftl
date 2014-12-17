<#escape x as x?xml>
    <fo:root xmlns:fo="http://www.w3.org/1999/XSL/Format">
        <fo:layout-master-set>
            <fo:simple-page-master master-name="main" page-height="11in" page-width="10in"
                     margin-left="0.1in" margin-right="0.2in"  margin-top="0.2in" margin-bottom="0.2in" >
                <fo:region-body margin-top="1.4in"/>
                <fo:region-before extent="1in"/>
                <fo:region-after extent="1in"/>
            </fo:simple-page-master>
        </fo:layout-master-set>
        ${setRequestAttribute("OUTPUT_FILENAME", "EmployeeMisPunchData.pdf")}
        <#if errorMessage?has_content>
			<fo:page-sequence master-reference="main">
			   <fo:flow flow-name="xsl-region-body" font-family="Courier,monospace">
			      <fo:block font-size="14pt">
			              ${errorMessage}.
			   	  </fo:block>
			   </fo:flow>
			</fo:page-sequence> 
		<#else>
			<#if EmplWiseDetailsMap?has_content>
				<#assign noofLines=1>
				<#assign SNo=1>
				<#assign noOfLines=1>
				<#assign pagetotDays=0>
			  	<#assign pagetotWages=0>
			  	<#assign pagetotContribution=0>
			  	<#assign grandtotDays=0>
			  	<#assign grandtotWages=0>
			  	<#assign grandtotContribution=0>
				<fo:page-sequence master-reference="main">
	        		<fo:static-content font-size="10pt" font-family="Courier,monospace"  flow-name="xsl-region-before" font-weight="bold">        
		        		<fo:block  keep-together="always" text-align="center" font-family="Courier,monospace" white-space-collapse="false" font-weight="bold">&#160;      ${uiLabelMap.KMFDairyHeader}</fo:block>
						<fo:block  keep-together="always" text-align="center" font-family="Courier,monospace" white-space-collapse="false" font-weight="bold">&#160;      ${uiLabelMap.KMFDairySubHeader}</fo:block>
		        		<fo:block  keep-together="always" text-align="center" font-family="Courier,monospace" white-space-collapse="false" font-weight="bold">&#160;      </fo:block>
		        		<fo:block text-align="center" keep-together="always" white-space-collapse="false">REGISTER OF EMPLOYEES  </fo:block>	
		        		<fo:block  keep-together="always" text-align="center" font-family="Courier,monospace" white-space-collapse="false" font-weight="bold">&#160;  Contribution Period From ${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(fromDate, "dd/MM/yyyy")} TO ${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(thruDate, "dd/MM/yyyy")}    </fo:block> 
		        		<fo:block text-align="right" keep-together="always" white-space-collapse="false">FORM SUBMITTED ON DATE: ${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(nowTimestamp, "dd-MMM-yyyy")}</fo:block>	 
		        		<fo:block text-align="right" keep-together="always" white-space-collapse="false">PAGE                  : <fo:page-number/>&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160; </fo:block>	 	 	  	 	  
	          		</fo:static-content>
	          		<fo:flow flow-name="xsl-region-body" font-family="Courier,monospace">
		            	<fo:block font-family="Courier,monospace">
		            		<#assign slNo=1>
	                     	<fo:table table-layout="fixed" width="50%" border-end-style="solid" border-left-style="solid">
	                     		<fo:table-column column-width="40pt"/>
			                	<fo:table-column column-width="80pt"/>
			                    <fo:table-column column-width="50pt"/>
			                    <fo:table-column column-width="130pt"/>
		                    	<fo:table-column column-width="70pt"/>
		                    	<fo:table-column column-width="60pt"/>
		                    	<fo:table-column column-width="85pt"/>
			                    <fo:table-column column-width="85pt"/>
		                    	<fo:table-column column-width="85pt"/>
	                     		<fo:table-body> 
		                     		<fo:table-row >
		                     			<fo:table-cell >	
			                            	<fo:block text-align="center" border-style="solid" keep-together="always" font-weight="bold" font-size="9pt">SNo</fo:block>
			                            </fo:table-cell>
			                            <fo:table-cell >	
			                            	<fo:block text-align="center" border-style="solid" keep-together="always" font-weight="bold" font-size="9pt">ESI No</fo:block>
			                            </fo:table-cell>
			                            <fo:table-cell >	
			                            	<fo:block text-align="center" border-style="solid" keep-together="always" font-weight="bold" font-size="9pt">EmpNo</fo:block>
			                            </fo:table-cell>
			                            <fo:table-cell >	
			                            	<fo:block text-align="center" border-style="solid" font-weight="bold" font-size="9pt">Name of Insured Person</fo:block>
			                            </fo:table-cell>
			                             <fo:table-cell >	
			                            	<fo:block text-align="center" border-style="solid" font-weight="bold" font-size="9pt">Month</fo:block>
			                            </fo:table-cell>
			                            <fo:table-cell >	
			                            	<fo:block text-align="center" border-style="solid" font-weight="bold" font-size="9pt">No Of Days</fo:block>
			                            </fo:table-cell>
			                            <fo:table-cell >	
			                            	<fo:block text-align="center" border-style="solid" font-weight="bold" font-size="9pt">Tot Amount of Wages</fo:block>
			                            </fo:table-cell>
			                            <fo:table-cell >	
			                            	<fo:block text-align="center" border-style="solid" font-weight="bold" font-size="9pt">Employees Contribution</fo:block>
			                            </fo:table-cell>
			                            <fo:table-cell >	
			                            	<fo:block text-align="center" border-style="solid" font-weight="bold" font-size="9pt">Daily Wages</fo:block>
			                            </fo:table-cell>
			                      	</fo:table-row>
	                           </fo:table-body>
	                    	</fo:table>
	                    	<fo:table table-layout="fixed" width="50%" border-left-style="solid">
	                     		<fo:table-column column-width="40pt"/>
			                	<fo:table-column column-width="80pt"/>
			                    <fo:table-column column-width="50pt"/>
			                    <fo:table-column column-width="130pt"/>
		                    	<fo:table-column column-width="70pt"/>
		                    	<fo:table-column column-width="60pt"/>
		                    	<fo:table-column column-width="85pt"/>
			                    <fo:table-column column-width="85pt"/>
		                    	<fo:table-column column-width="85pt"/>
	                     		<fo:table-body> 
	                     			<#assign EmplWiseDetails = EmplWiseDetailsMap.entrySet()>
			                    	<#list EmplWiseDetails as EmplDetails>
			                    		<#assign totDays=0>
			                    		<#assign totWages=0>
			                    		<#assign totContribution=0>
			                    		<fo:table-row >
			                     			<fo:table-cell >	
				                            	<fo:block border-top-style="solid" linefeed-treatment="preserve" border-right-style="solid" >&#xA;</fo:block>
				                            	<fo:block text-align="center" border-bottom-style="solid" border-right-style="solid" keep-together="always" font-size="9pt">${slNo}</fo:block>
				                            	<#assign slNo=slNo+1>
				                            </fo:table-cell>
				                            <fo:table-cell >	
				                            	<fo:block border-top-style="solid" linefeed-treatment="preserve" border-right-style="solid" >&#xA;</fo:block>
				                            	<fo:block text-align="center" border-bottom-style="solid" border-right-style="solid" keep-together="always" font-size="9pt">&#160;</fo:block>
				                            </fo:table-cell>
				                            <fo:table-cell >	
				                            	<fo:block border-top-style="solid" linefeed-treatment="preserve" border-right-style="solid" >&#xA;</fo:block>
				                            	<fo:block text-align="center" border-bottom-style="solid" border-right-style="solid" keep-together="always" font-size="9pt">${EmplDetails.getKey()}</fo:block>
				                            </fo:table-cell>
				                            <#assign EmplNameList = EmplNameMap.entrySet()>
				                            <#if EmplNameList?has_content>
					                            <#list EmplNameList as EmplName>
					                            	<#if EmplName.getKey()==EmplDetails.getKey()>
							                            <fo:table-cell >	
							                            	<fo:block border-top-style="solid" linefeed-treatment="preserve">&#xA;</fo:block>
							                            	<fo:block text-align="left" border-bottom-style="solid" keep-together="always" font-size="9pt">&#160;${EmplName.getValue()}</fo:block>
							                            </fo:table-cell>
							                       	</#if>
						                     	</#list>
						                     </#if>
					                     	<fo:table-cell >
					                     		<fo:block font-family="Courier,monospace">
					                     			<fo:table table-layout="fixed" width="50%" border-style="solid">
							                     		<fo:table-column column-width="70pt"/>
								                    	<fo:table-column column-width="60pt"/>
								                    	<fo:table-column column-width="85pt"/>
									                    <fo:table-column column-width="85pt"/>
									                    <fo:table-column column-width="85pt"/>
									                    <fo:table-body>
										                    <#assign EmplWagesDetails = EmplDetails.getValue().entrySet()>
										                    <#if EmplWagesDetails?has_content>
											                    <#list EmplWagesDetails as EmplWages>
										                     		<#list periodList as PeriodId>
										                     			<#if PeriodId==EmplWages.getKey()>
											                    			<fo:table-row border-bottom-style="solid" >
											                    				<fo:table-cell >	
													                            	<fo:block border-right-style="solid" linefeed-treatment="preserve">&#xA;</fo:block>
													                            	<fo:block text-align="center" keep-together="always" border-right-style="solid" font-size="9pt">${PeriodId?replace("_", ",")}</fo:block>
													                            </fo:table-cell>
											                    				<fo:table-cell >	
													                            	<fo:block border-right-style="solid" linefeed-treatment="preserve">&#xA;</fo:block>
													                            	<fo:block text-align="right" keep-together="always" border-right-style="solid" font-size="9pt"><#if EmplWages.getValue().get("payableDays")?has_content>${EmplWages.getValue().get("payableDays")?if_exists?string("#0.0")}<#else>0.0</#if>&#160;</fo:block>
													                            	<#if EmplWages.getValue().get("payableDays")?has_content>
													                            		<#assign totDays=totDays+EmplWages.getValue().get("payableDays")>
													                            	</#if>
													                            </fo:table-cell>
													                            <fo:table-cell >	
													                            	<fo:block border-right-style="solid" linefeed-treatment="preserve">&#xA;</fo:block>
													                            	<fo:block text-align="right" keep-together="always" border-right-style="solid" font-size="9pt"><#if EmplWages.getValue().get("Wages")?has_content>${EmplWages.getValue().get("Wages")?if_exists?string("#0.00")}<#else>0.00</#if>&#160;</fo:block>
													                            	<#if EmplWages.getValue().get("Wages")?has_content>
													                            		<#assign totWages=totWages+EmplWages.getValue().get("Wages")>
													                            	</#if>
													                            </fo:table-cell>
													                            <fo:table-cell >	
													                            	<fo:block border-right-style="solid" linefeed-treatment="preserve">&#xA;</fo:block>
													                            	<fo:block text-align="right" keep-together="always" border-right-style="solid" font-size="9pt"><#if EmplWages.getValue().get("Contribution")?has_content><#if (EmplWages.getValue().get("Contribution") < 0)>${((-1)*EmplWages.getValue().get("Contribution"))?if_exists?string("#0.00")}<#else> ${EmplWages.getValue().get("Contribution")?if_exists?string("#0.00")}</#if><#else>0.00</#if>&#160;</fo:block>
													                            	<#if EmplWages.getValue().get("Contribution")?has_content>
													                            		<#assign totContribution=totContribution+EmplWages.getValue().get("Contribution")>
													                            	</#if>
													                            </fo:table-cell>
											                    			</fo:table-row >
											                    		</#if>
											                    	</#list>
											                    </#list>
											               	</#if>
										                    <fo:table-row >
										                    	<fo:table-cell >
										                    		<fo:block linefeed-treatment="preserve" border-right-style="solid">&#xA;</fo:block> 	
									                            	<fo:block text-align="center" keep-together="always" font-weight="bold" font-size="9pt" border-right-style="solid">Total</fo:block>
									                            </fo:table-cell>
											                    <fo:table-cell >	
									                            	<fo:block linefeed-treatment="preserve" border-right-style="solid">&#xA;</fo:block> 
									                            	<fo:block text-align="right" keep-together="always" font-weight="bold" border-right-style="solid" font-size="9pt"><#if totDays?has_content>${totDays?if_exists?string("#0.0")}<#else>0.0</#if>&#160;</fo:block>
									                            	<#if totDays?has_content>
										                            	<#assign pagetotDays=pagetotDays+totDays>
										                            	<#assign grandtotDays=grandtotDays+totDays>
										                            </#if>
									                            </fo:table-cell>
									                            <fo:table-cell >	
									                            	<fo:block border-right-style="solid" linefeed-treatment="preserve">&#xA;</fo:block> 
									                            	<fo:block text-align="right" keep-together="always" font-weight="bold" border-right-style="solid" font-size="9pt"><#if totWages?has_content>${totWages?if_exists?string("#0.00")}<#else>0.00</#if>&#160;</fo:block>
									                            	<#if totWages?has_content>
										                            	<#assign pagetotWages=pagetotWages+totWages>
										                            	<#assign grandtotWages=grandtotWages+totWages>
										                            </#if>
									                            </fo:table-cell>
									                            <fo:table-cell >	
									                            	<fo:block border-right-style="solid" linefeed-treatment="preserve">&#xA;</fo:block> 
									                            	<fo:block text-align="right" keep-together="always" font-weight="bold" border-right-style="solid" font-size="9pt"><#if totContribution?has_content><#if (totContribution < 0)>${((-1)*totContribution)?if_exists?string("#0.00")}<#else>${(totContribution)?if_exists?string("#0.00")}</#if><#else>0.00</#if>&#160;</fo:block>
									                            	<#if totContribution?has_content>
										                            	<#assign pagetotContribution=pagetotContribution+totContribution>
										                            	<#assign grandtotContribution=grandtotContribution+totContribution>
										                            </#if>
									                            </fo:table-cell>
									                            <fo:table-cell >	
									                            	<fo:block linefeed-treatment="preserve">&#xA;</fo:block> 
									                            	<fo:block text-align="right" keep-together="always" font-weight="bold" font-size="9pt"><#if totDays!=0>${(totWages/totDays)?if_exists?string("#0.00")}<#else>0.00</#if>&#160;</fo:block>
									                            </fo:table-cell>
									                     	</fo:table-row >
									                     	<#assign noOfLines=noOfLines+1>
										             	</fo:table-body>
									               	</fo:table>
	                 							</fo:block>
	                 						</fo:table-cell>  
			                    		</fo:table-row>
			                    		<#if (noOfLines>2) >
			                    			<#assign noOfLines=1>
			                    			<fo:table-row >
			                    				<fo:table-cell >
			                    					<fo:block border-top-style="solid" linefeed-treatment="preserve" border-right-style="solid" >&#xA;</fo:block>
			                    					<fo:block text-align="right" keep-together="always" font-weight="bold" border-right-style="solid" font-size="9pt" border-bottom-style="solid" >&#160;</fo:block>
			                    				</fo:table-cell>
			                    				<fo:table-cell >
			                    					<fo:block border-top-style="solid" linefeed-treatment="preserve" border-right-style="solid" >&#xA;</fo:block>
			                    					<fo:block text-align="right" keep-together="always" font-weight="bold" border-right-style="solid" font-size="9pt" border-bottom-style="solid" >Page Total</fo:block>
			                    				</fo:table-cell>
			                    				<fo:table-cell >
			                    					<fo:block border-top-style="solid" linefeed-treatment="preserve" border-right-style="solid" >&#xA;</fo:block>
			                    					<fo:block text-align="right" keep-together="always" font-weight="bold" border-right-style="solid" font-size="9pt" border-bottom-style="solid" >&#160;</fo:block>
			                    				</fo:table-cell>
			                    				<fo:table-cell >
			                    					<fo:block border-top-style="solid" linefeed-treatment="preserve" border-right-style="solid" >&#xA;</fo:block>
			                    					<fo:block text-align="right" keep-together="always" font-weight="bold" border-right-style="solid" font-size="9pt" border-bottom-style="solid" >&#160;</fo:block>
			                    				</fo:table-cell>
			                    				<fo:table-cell >
			                    					<fo:block border-top-style="solid" linefeed-treatment="preserve" border-right-style="solid" >&#xA;</fo:block>
			                    					<fo:block text-align="right" keep-together="always" font-weight="bold" border-right-style="solid" font-size="9pt" border-bottom-style="solid" >&#160;</fo:block>
			                    				</fo:table-cell>
			                    				<fo:table-cell >
			                    					<fo:block border-top-style="solid" linefeed-treatment="preserve" border-right-style="solid" >&#xA;</fo:block>
			                    					<fo:block text-align="right" keep-together="always" font-weight="bold" border-right-style="solid" font-size="9pt" border-bottom-style="solid" >${pagetotDays?if_exists?string("#0.0")}&#160;</fo:block>
			                    				</fo:table-cell>
			                    				<fo:table-cell >
			                    					<fo:block border-top-style="solid" linefeed-treatment="preserve" border-right-style="solid" >&#xA;</fo:block>
			                    					<fo:block text-align="right" keep-together="always" font-weight="bold" border-right-style="solid" font-size="9pt" border-bottom-style="solid" >${pagetotWages?if_exists?string("#0.00")}&#160;</fo:block>
			                    				</fo:table-cell>
			                    				<fo:table-cell >
			                    					<fo:block border-top-style="solid" linefeed-treatment="preserve" border-right-style="solid" >&#xA;</fo:block>
			                    					<fo:block text-align="right" keep-together="always" font-weight="bold" border-right-style="solid" font-size="9pt" border-bottom-style="solid" ><#if (pagetotContribution < 0)>${((-1)*pagetotContribution)?if_exists?string("#0.00")}<#else>${(pagetotContribution)?if_exists?string("#0.00")}</#if> &#160;</fo:block>
			                    				</fo:table-cell>
			                    				<fo:table-cell >
			                    					<fo:block border-top-style="solid" linefeed-treatment="preserve" border-right-style="solid" >&#xA;</fo:block>
			                    					<fo:block text-align="right" keep-together="always" font-weight="bold" border-right-style="solid" font-size="9pt" border-bottom-style="solid" ><#if pagetotDays!=0>${(pagetotWages/pagetotDays)?if_exists?string("#0.00")}<#else>0.00</#if>&#160;</fo:block>
			                    				</fo:table-cell>
			                     			</fo:table-row>
			                     			<#assign pagetotDays=0>
			                     			<#assign pagetotWages=0>
			                     			<#assign pagetotContribution=0>
			                    			<fo:table-row >
			                    				<fo:table-cell >	
					                        		<fo:block page-break-after="always"></fo:block>
					                        	</fo:table-cell>
			                     			</fo:table-row>
			                    		</#if>
			                    	</#list>
			                    	<fo:table-row >
	                    				<fo:table-cell >
	                    					<fo:block border-top-style="solid" linefeed-treatment="preserve" border-right-style="solid" >&#xA;</fo:block>
	                    					<fo:block text-align="right" keep-together="always" font-weight="bold" border-right-style="solid" font-size="9pt" border-bottom-style="solid" >&#160;</fo:block>
	                    				</fo:table-cell>
	                    				<fo:table-cell >
	                    					<fo:block border-top-style="solid" linefeed-treatment="preserve" border-right-style="solid" >&#xA;</fo:block>
	                    					<fo:block text-align="center" keep-together="always" font-weight="bold" border-right-style="solid" font-size="9pt" border-bottom-style="solid" >Grand Total</fo:block>
	                    				</fo:table-cell>
	                    				<fo:table-cell >
	                    					<fo:block border-top-style="solid" linefeed-treatment="preserve" border-right-style="solid" >&#xA;</fo:block>
	                    					<fo:block text-align="right" keep-together="always" font-weight="bold" border-right-style="solid" font-size="9pt" border-bottom-style="solid" >&#160;</fo:block>
	                    				</fo:table-cell>
	                    				<fo:table-cell >
	                    					<fo:block border-top-style="solid" linefeed-treatment="preserve" border-right-style="solid" >&#xA;</fo:block>
	                    					<fo:block text-align="right" keep-together="always" font-weight="bold" border-right-style="solid" font-size="9pt" border-bottom-style="solid" >&#160;</fo:block>
	                    				</fo:table-cell>
	                    				<fo:table-cell >
	                    					<fo:block border-top-style="solid" linefeed-treatment="preserve" border-right-style="solid" >&#xA;</fo:block>
	                    					<fo:block text-align="right" keep-together="always" font-weight="bold" border-right-style="solid" font-size="9pt" border-bottom-style="solid" >&#160;</fo:block>
	                    				</fo:table-cell>
	                    				<fo:table-cell >
	                    					<fo:block border-top-style="solid" linefeed-treatment="preserve" border-right-style="solid" >&#xA;</fo:block>
	                    					<fo:block text-align="right" keep-together="always" font-weight="bold" border-right-style="solid" font-size="9pt" border-bottom-style="solid" >${grandtotDays?if_exists?string("#0.0")}&#160;</fo:block>
	                    				</fo:table-cell>
	                    				<fo:table-cell >
	                    					<fo:block border-top-style="solid" linefeed-treatment="preserve" border-right-style="solid" >&#xA;</fo:block>
	                    					<fo:block text-align="right" keep-together="always" font-weight="bold" border-right-style="solid" font-size="9pt" border-bottom-style="solid" >${grandtotWages?if_exists?string("#0.00")}&#160;</fo:block>
	                    				</fo:table-cell>
	                    				<fo:table-cell >
	                    					<fo:block border-top-style="solid" linefeed-treatment="preserve" border-right-style="solid" >&#xA;</fo:block>
	                    					<fo:block text-align="right" keep-together="always" font-weight="bold" border-right-style="solid" font-size="9pt" border-bottom-style="solid" ><#if (grandtotContribution < 0)>${((-1)*grandtotContribution)?if_exists?string("#0.00")}<#else>${(grandtotContribution)?if_exists?string("#0.00")}</#if> &#160;</fo:block> 
	                    				</fo:table-cell>
	                    				<fo:table-cell >
	                    					<fo:block border-top-style="solid" linefeed-treatment="preserve" border-right-style="solid" >&#xA;</fo:block>
	                    					<fo:block text-align="right" keep-together="always" font-weight="bold" border-right-style="solid" font-size="9pt" border-bottom-style="solid" ><#if grandtotDays!=0>${(grandtotWages/grandtotDays)?if_exists?string("#0.00")}<#else>0.00</#if>&#160;</fo:block>
	                    				</fo:table-cell>
	                     			</fo:table-row>
			                    </fo:table-body>
	                    	</fo:table>
	                 	</fo:block>
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
	  	</#if>
     </fo:root>
</#escape>
<#escape x as x?xml>
    <fo:root xmlns:fo="http://www.w3.org/1999/XSL/Format">
        <fo:layout-master-set>
            <fo:simple-page-master master-name="main" page-height="11in" page-width="11in"
                     margin-left="0.3in" margin-right="0.2in"  margin-top="0.2in" margin-bottom="0.2in" >
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
				<fo:page-sequence master-reference="main">
	        		<fo:static-content font-size="12pt" font-family="Courier,monospace"  flow-name="xsl-region-before" font-weight="bold">        
		        		<fo:block  keep-together="always" text-align="center" font-family="Courier,monospace" white-space-collapse="false" font-weight="bold">&#160;      ${uiLabelMap.KMFDairyHeader}</fo:block>
						<fo:block  keep-together="always" text-align="center" font-family="Courier,monospace" white-space-collapse="false" font-weight="bold">&#160;      ${uiLabelMap.KMFDairySubHeader}</fo:block>
		        		<fo:block  keep-together="always" text-align="center" font-family="Courier,monospace" white-space-collapse="false" font-weight="bold">&#160;      </fo:block>
		        		<fo:block  keep-together="always" text-align="center" font-family="Courier,monospace" white-space-collapse="false" font-weight="bold">&#160;  Monthly report of ESI  for Month : ${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(timePeriodStart, "MMM-yyyy")}    </fo:block> 
		        		<fo:block text-align="right" keep-together="always" white-space-collapse="false">DATE: ${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(nowTimestamp, "dd-MMM-yyyy")}</fo:block>	 
		        		<fo:block text-align="right" keep-together="always" white-space-collapse="false">PAGE: <fo:page-number/></fo:block>	 	 	  	 	  
	          		</fo:static-content>
	          		<fo:flow flow-name="xsl-region-body" font-family="Courier,monospace">
		            	<fo:block font-family="Courier,monospace">
		            		<#assign slNo=1>
		            		<fo:table table-layout="fixed" width="50%">
		                		<fo:table-column column-width="40pt"/>
			                	<fo:table-column column-width="70pt"/>
			                    <fo:table-column column-width="100pt"/>
			                    <fo:table-column column-width="170pt"/>
			                    <#list periodList as periodId>                
			                    	<fo:table-column column-width="250pt"/>
			                    </#list>
			                    <fo:table-column column-width="100pt"/>
		                		<fo:table-body> 
		                     		<fo:table-row >
		                     			<fo:table-cell >	
			                            	<fo:block text-align="center" border-style="solid" keep-together="always" font-weight="bold" font-size="11pt"> &#160;</fo:block>
			                            </fo:table-cell>
			                            <fo:table-cell >	
			                            	<fo:block text-align="center" border-style="solid" keep-together="always" font-weight="bold" font-size="11pt">&#160;</fo:block>
			                            </fo:table-cell>
			                            <fo:table-cell >	
			                            	<fo:block text-align="center" border-style="solid" keep-together="always" font-weight="bold" font-size="11pt">&#160;</fo:block>
			                            </fo:table-cell>
			                            <fo:table-cell >	
			                            	<fo:block text-align="center" border-style="solid" font-weight="bold" font-size="12pt">&#160;</fo:block>
			                            </fo:table-cell>
			                            <#list periodList as periodId>
				                            <fo:table-cell >	
				                            	<fo:block text-align="center" border-style="solid" keep-together="always" font-weight="bold" font-size="11pt">${periodId}</fo:block>
				                            </fo:table-cell>
				                      	</#list>
			                            <fo:table-cell >	
			                            	<fo:block text-align="center" border-style="solid" font-weight="bold" font-size="11pt">&#160;</fo:block>
			                            </fo:table-cell>
			                      	</fo:table-row>
	                           </fo:table-body>
	                     	</fo:table>
	                     	<fo:table table-layout="fixed" width="50%" border-style="solid">
	                     		<fo:table-column column-width="40pt"/>
			                	<fo:table-column column-width="70pt"/>
			                    <fo:table-column column-width="100pt"/>
			                    <fo:table-column column-width="170pt"/>
			                    <#list periodList as periodId>                
			                    	<fo:table-column column-width="60pt"/>
			                    	<fo:table-column column-width="100pt"/>
			                    	<fo:table-column column-width="90pt"/>
			                    </#list>
		                    	<fo:table-column column-width="100pt"/>
	                     		<fo:table-body> 
		                     		<fo:table-row >
		                     			<fo:table-cell >	
			                            	<fo:block text-align="center" border-style="solid" keep-together="always" font-weight="bold" font-size="11pt">SNo</fo:block>
			                            </fo:table-cell>
			                            <fo:table-cell >	
			                            	<fo:block text-align="center" border-style="solid" keep-together="always" font-weight="bold" font-size="11pt">EmpNo</fo:block>
			                            </fo:table-cell>
			                            <fo:table-cell >	
			                            	<fo:block text-align="center" border-style="solid" keep-together="always" font-weight="bold" font-size="11pt">ESI No</fo:block>
			                            </fo:table-cell>
			                            <fo:table-cell >	
			                            	<fo:block text-align="center" border-style="solid" font-weight="bold" font-size="11pt">Name</fo:block>
			                            </fo:table-cell>
			                            <#list periodList as periodId>
				                            <fo:table-cell >	
				                            	<fo:block text-align="center" border-style="solid" font-weight="bold" font-size="11pt">Present Days</fo:block>
				                            </fo:table-cell>
				                            <fo:table-cell >	
				                            	<fo:block text-align="center" border-style="solid" font-weight="bold" font-size="11pt">Wages</fo:block>
				                            </fo:table-cell>
				                            <fo:table-cell >	
				                            	<fo:block text-align="center" border-style="solid" font-weight="bold" font-size="11pt">ESI Employees Contribution</fo:block>
				                            </fo:table-cell>
				                      	</#list>
			                            <fo:table-cell >	
			                            	<fo:block text-align="center" border-style="solid" font-weight="bold" font-size="11pt">Daily Wages</fo:block>
			                            </fo:table-cell>
			                      	</fo:table-row>
	                           </fo:table-body>
	                    	</fo:table>
	                    	<fo:table table-layout="fixed" width="50%" border-style="solid">
	                     		<fo:table-column column-width="40pt"/>
			                	<fo:table-column column-width="70pt"/>
			                    <fo:table-column column-width="100pt"/>
			                    <fo:table-column column-width="170pt"/>
			                    <#list periodList as periodId>                
			                    	<fo:table-column column-width="60pt"/>
			                    	<fo:table-column column-width="100pt"/>
			                    	<fo:table-column column-width="90pt"/>
			                    </#list>
		                    	<fo:table-column column-width="100pt"/>
	                     		<fo:table-body>
	                     			<#assign totDailyWages=0> 
	                     			<#assign totPayableDays=0>
	                     			<#assign totWages=0>
	                     			<#assign totContribution=0>
	                     			<#assign pagetotPayableDays=0>
	                     			<#assign pagetotWages=0>
	                     			<#assign pagetotContribution=0>
	                     			<#assign EmplWiseDetails = EmplWiseDetailsMap.entrySet()>
			                    	<#list EmplWiseDetails as EmplDetails>
			                    		<#assign dailyWages=0>
			                    		<fo:table-row >
			                     			<fo:table-cell >	
				                            	<fo:block text-align="center" border-style="solid" keep-together="always" font-size="11pt">${slNo}</fo:block>
				                            	<#assign slNo=slNo+1>
				                            </fo:table-cell>
				                            <fo:table-cell >	
				                            	<fo:block text-align="center" border-style="solid" keep-together="always" font-size="11pt">${EmplDetails.getKey()}</fo:block>
				                            </fo:table-cell>
				                            <fo:table-cell >	
				                            	<fo:block text-align="center" border-style="solid" keep-together="always" font-size="11pt">&#160;</fo:block>
				                            </fo:table-cell>
				                            <#assign EmplNameList = EmplNameMap.entrySet()>
				                            <#list EmplNameList as EmplName>
				                            	<#if EmplName.getKey()==EmplDetails.getKey()>
						                            <fo:table-cell >	
						                            	<fo:block text-align="left" border-style="solid" keep-together="always" font-size="11pt">&#160;${EmplName.getValue()}</fo:block>
						                            </fo:table-cell>
						                       	</#if>
					                     	</#list>
					                     	<#assign EmplWagesDetails = EmplDetails.getValue().entrySet()>
					                     	<#list EmplWagesDetails as EmplWages>
					                     		<#list periodList as PeriodId>
					                     			<#if PeriodId==EmplWages.getKey()>
					                     				<fo:table-cell >	
							                            	<fo:block text-align="right" border-style="solid" keep-together="always" font-size="11pt"><#if EmplWages.getValue().get("payableDays")?has_content>${EmplWages.getValue().get("payableDays")?if_exists?string("#0.0")}<#else>0.0</#if>&#160;</fo:block>
							                            </fo:table-cell>
							                            <#if EmplWages.getValue().get("payableDays")?has_content>
							                            	<#assign pagetotPayableDays=pagetotPayableDays+EmplWages.getValue().get("payableDays")>
							                            	<#assign totPayableDays=totPayableDays+EmplWages.getValue().get("payableDays")>
							                            	<#assign dailyWages=((EmplWages.getValue().get("Wages"))/(EmplWages.getValue().get("payableDays")))>
							                            </#if>
							                            <fo:table-cell >	
							                            	<fo:block text-align="right" border-style="solid" keep-together="always" font-size="11pt"><#if EmplWages.getValue().get("Wages")?has_content>${EmplWages.getValue().get("Wages")?if_exists?string("#0.00")}<#else>0.00</#if>&#160;</fo:block>
							                            </fo:table-cell>
							                            <#if EmplWages.getValue().get("Wages")?has_content>
							                            	<#assign pagetotWages=pagetotWages+EmplWages.getValue().get("Wages")>
							                            	<#assign totWages=totWages+EmplWages.getValue().get("Wages")>
							                            </#if>
							                            <fo:table-cell >	
							                            	<fo:block text-align="right" border-style="solid" keep-together="always" font-size="11pt"><#if EmplWages.getValue().get("Contribution")?has_content>${EmplWages.getValue().get("Contribution")?if_exists?string("#0.00")}<#else>0.00</#if>&#160;</fo:block>
							                            </fo:table-cell>
							                            <#if EmplWages.getValue().get("Contribution")?has_content>
							                            	<#assign pagetotContribution=pagetotContribution+EmplWages.getValue().get("Contribution")>
							                            	<#assign totContribution=totContribution+EmplWages.getValue().get("Contribution")>
							                            </#if>
							                       	</#if>
					                     		</#list>
					                     	</#list>
				                            <fo:table-cell >	
				                            	<fo:block text-align="right" border-style="solid" keep-together="always" font-size="11pt">${dailyWages?if_exists?string("#0.00")}&#160;</fo:block>
				                            </fo:table-cell>
				                            <#assign noOfLines=noOfLines+1>
			                    		</fo:table-row>
			                    		<#if (noOfLines>35) >
			                    			<#assign noOfLines=1>
			                     			<fo:table-row >
					                    		<fo:table-cell >	
					                            	<fo:block text-align="center" border-style="solid" keep-together="always" font-size="11pt">&#160;</fo:block>
					                            </fo:table-cell>
					                    		<fo:table-cell >	
					                            	<fo:block text-align="center" border-style="solid" keep-together="always" font-size="11pt" font-weight="bold" >Page Total</fo:block>
					                            </fo:table-cell>
					                    		<fo:table-cell >	
					                            	<fo:block text-align="center" border-style="solid" keep-together="always" font-size="11pt">&#160;</fo:block>
					                            </fo:table-cell>
					                            <fo:table-cell >	
					                            	<fo:block text-align="center" border-style="solid" keep-together="always" font-size="11pt">&#160;</fo:block>
					                            </fo:table-cell>
					                            <fo:table-cell >	
					                            	<fo:block text-align="right" border-style="solid" keep-together="always" font-size="11pt" font-weight="bold" >${pagetotPayableDays?if_exists?string("#0.0")}&#160;</fo:block>
					                            </fo:table-cell>
					                            <fo:table-cell >	
					                            	<fo:block text-align="right" border-style="solid" keep-together="always" font-size="11pt" font-weight="bold" >${pagetotWages?if_exists?string("#0.00")}&#160;</fo:block>
					                            </fo:table-cell>
					                            <fo:table-cell >	
					                            	<fo:block text-align="right" border-style="solid" keep-together="always" font-size="11pt" font-weight="bold" >${pagetotContribution?if_exists?string("#0.00")}&#160;</fo:block>
					                            </fo:table-cell>
					                            <fo:table-cell >	
					                            	<fo:block text-align="right" border-style="solid" keep-together="always" font-size="11pt" font-weight="bold" ><#if pagetotPayableDays!=0>${(pagetotWages/pagetotPayableDays)?if_exists?string("#0.00")}<#else>0.00</#if>&#160;</fo:block>
					                            </fo:table-cell>
					                    	</fo:table-row>
					                    	<#assign pagetotPayableDays=0>
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
			                            	<fo:block text-align="center" border-style="solid" keep-together="always" font-size="11pt">&#160;</fo:block>
			                            </fo:table-cell>
			                    		<fo:table-cell >	
			                            	<fo:block text-align="center" border-style="solid" keep-together="always" font-size="11pt" font-weight="bold" >Total</fo:block>
			                            </fo:table-cell>
			                    		<fo:table-cell >	
			                            	<fo:block text-align="center" border-style="solid" keep-together="always" font-size="11pt">&#160;</fo:block>
			                            </fo:table-cell>
			                            <fo:table-cell >	
			                            	<fo:block text-align="center" border-style="solid" keep-together="always" font-size="11pt">&#160;</fo:block>
			                            </fo:table-cell>
			                            <fo:table-cell >	
			                            	<fo:block text-align="right" border-style="solid" keep-together="always" font-size="11pt" font-weight="bold" >${totPayableDays?if_exists?string("#0.0")}&#160;</fo:block>
			                            </fo:table-cell>
			                            <fo:table-cell >	
			                            	<fo:block text-align="right" border-style="solid" keep-together="always" font-size="11pt" font-weight="bold" >${totWages?if_exists?string("#0.00")}&#160;</fo:block>
			                            </fo:table-cell>
			                            <fo:table-cell >	
			                            	<fo:block text-align="right" border-style="solid" keep-together="always" font-size="11pt" font-weight="bold" >${totContribution?if_exists?string("#0.00")}&#160;</fo:block>
			                            </fo:table-cell>
			                            <fo:table-cell >	
			                            	<fo:block text-align="right" border-style="solid" keep-together="always" font-size="11pt" font-weight="bold" ><#if totPayableDays!=0>${(totWages/totPayableDays)?if_exists?string("#0.00")}<#else>0.00</#if>&#160;</fo:block>
			                            </fo:table-cell>
			                    	</fo:table-row>
			                    </fo:table-body>
	                    	</fo:table>
	                    	<fo:table>
	                     		<fo:table-column column-width="690pt"/>
	                     		<fo:table-body> 
	                     			<fo:table-row >
				                   		<fo:table-cell >	
		                            		<fo:block keep-together="always" font-weight="bold">&#160;</fo:block>
		                            	</fo:table-cell>
		                            </fo:table-row>
		                             <fo:table-row >
				                   		<fo:table-cell >	
		                            		<fo:block keep-together="always" font-weight="bold">&#160;</fo:block>
		                            	</fo:table-cell>
		                            </fo:table-row>
		                             <fo:table-row >
				                   		<fo:table-cell >	
		                            		<fo:block keep-together="always" font-weight="bold">&#160;</fo:block>
		                            	</fo:table-cell>
		                            </fo:table-row>
		                             <fo:table-row >
				                   		<fo:table-cell >	
		                            		<fo:block keep-together="always" font-weight="bold">&#160;</fo:block>
		                            	</fo:table-cell>
		                            </fo:table-row>
		                     		<fo:table-row >
			                            <fo:table-cell >	
			                            	<fo:block keep-together="always" text-align="right">Signature</fo:block>
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
			            	${uiLabelMap.NoDetailsFound}.
			       		 </fo:block>
			    	</fo:flow>
				</fo:page-sequence>
	  		</#if>
	  	</#if>
     </fo:root>
</#escape>
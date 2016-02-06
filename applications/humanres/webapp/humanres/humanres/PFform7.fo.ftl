<#escape x as x?xml>
    <fo:root xmlns:fo="http://www.w3.org/1999/XSL/Format">
        <fo:layout-master-set>
            <fo:simple-page-master master-name="main" page-height="12in" page-width="10in"
                     margin-left="0.2in" margin-right="0.2in"  margin-top="0.2in" margin-bottom="0.2in" >
                <fo:region-body margin-top="1in"/>
                <fo:region-before extent="1in"/>
                <fo:region-after extent="1in"/>
            </fo:simple-page-master>
        </fo:layout-master-set>
        ${setRequestAttribute("OUTPUT_FILENAME", "PFform7.pdf")}        
		
		<#if employeeMap?has_content>
			<#assign noofLines=1>
			<fo:page-sequence master-reference="main">
        		<fo:static-content font-family="Helvetica"  flow-name="xsl-region-before" font-weight="bold">        
	        		<fo:block  keep-together="always" text-align="center" font-family="Helvetica" white-space-collapse="false" font-weight="bold" font-size="15pt">&#160; THE EMPLOYEES PENSION SCHEME, 1995</fo:block>
	        		<fo:block  keep-together="always" text-align="center" font-family="Helvetica" white-space-collapse="false" font-weight="bold" font-size="12pt">&#160; (Paragraph 19 )</fo:block>
	        		<fo:block  keep-together="always" margin-top="0.2in" text-align="left" font-family="Helvetica" white-space-collapse="false" font-weight="bold" font-size="11pt">&#160; Contribution Card for members of the year - ${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(fromDate, "yyyy")} TO ${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(thruDate, "yyyy")} </fo:block> 
          		</fo:static-content>
          		<fo:flow flow-name="xsl-region-body" font-family="Helvetica">
          			<#assign employeeDetailsMap = employeeMap.entrySet()>
	            	<#list employeeDetailsMap as employeeVal>
	            		<fo:block font-family="Helvetica">
		                	<fo:table >
		                		<fo:table-column column-width="500pt"/>
		                		<fo:table-body> 
		                			<#assign employeeDetails = employeeMap.entrySet()>
		            				<#list employeeDetails as employee>
		                				<#if employeeVal.getKey() == employee.getKey()>
				                     		
				                     		<fo:table-row >
				                     			<fo:table-cell >	
					                            	<fo:block text-align="left" keep-together="always" font-weight="bold" font-size="11pt">1. Account Number : ${employee.getValue().get("pfAccNo")?if_exists}</fo:block>
					                            </fo:table-cell>					                           
						                   	</fo:table-row>
						                   	
						                   	<fo:table-row >
					                            <fo:table-cell >	
					                            	<fo:block text-align="left" keep-together="always" font-weight="bold" font-size="11pt">2. Name / Surname : ${employee.getValue().get("employeeName")?if_exists}</fo:block>
					                            </fo:table-cell>					                            
						                   	</fo:table-row>
						                   	
						                   	<fo:table-row >
					                            <fo:table-cell >	
					                            	<fo:block text-align="left" keep-together="always" font-weight="bold" font-size="11pt">3. Father's/Husband's Name : <#if employee.getValue().get("fatherName")?has_content> ${employee.getValue().get("fatherName")}<#else>&#160;</#if></fo:block>
					                            </fo:table-cell>					                           
						                   	</fo:table-row>
						                   	
						                   	<fo:table-row >
						                   	<#assign reportHeader = delegator.findOne("TenantConfiguration", {"propertyTypeEnumId" : "COMPANY_HEADER","propertyName" : "reportHeaderLable"}, true)>
						                    <#assign reportSubHeader = delegator.findOne("TenantConfiguration", {"propertyTypeEnumId" : "COMPANY_HEADER","propertyName" : "reportSubHeaderLable"}, true)>      
					                            <fo:table-cell >	
					                            	<fo:block text-align="left" font-weight="bold" font-size="11pt">4. Name and Address of the : &#160;${reportHeader.description?if_exists}</fo:block>
					                            	<fo:block text-align="left" font-size="11pt">&#160;&#160;&#160;&#160;&#160;&#160; Establishment &#160;&#160;&#160;${reportSubHeader.description?if_exists}</fo:block>
					                            </fo:table-cell>					                           
						                   	</fo:table-row>
						                   	
											<fo:table-row>
						                   		 <fo:table-cell >	
					                            	<fo:block text-align="left" keep-together="always" font-weight="bold" font-size="11pt">5. Statutory Rate of contribution &#160;&#160;&#160;: 8.33 %</fo:block>
					                            </fo:table-cell>
					                       	</fo:table-row>
					                       	
						               	</#if>
						    		</#list>
				             	</fo:table-body>
	                     	</fo:table>
	                  	</fo:block>
						<fo:block>&#160;</fo:block>
						<#assign totalWage = 0 >
						<#assign totalContribution = 0 >
						<fo:block>
	                     	<fo:table border-style = "solid">
		                		<fo:table-column column-width="120pt"/>
			                	<fo:table-column column-width="200pt"/>
			                	<fo:table-column column-width="100pt"/>
			                	<fo:table-column column-width="100pt"/>
		                	    <fo:table-body> 
	                     			 
	                     	            <fo:table-row border-style = "solid"> 
			                     		<fo:table-cell border-right-style = "solid">	
				                            	<fo:block text-align="center"  font-weight="bold" font-size="10pt" line-height = "13pt">Month</fo:block>
				                            </fo:table-cell>
				                            <fo:table-cell border-right-style = "solid">	
				                            	<fo:block text-align="center" font-weight="bold" font-size="10pt" line-height = "13pt">Amount of Wages retaining allowances if any and DA including cash value of food concession paid during the month</fo:block>
				                            </fo:table-cell>
				                            <fo:table-cell border-right-style = "solid">	
				                            	<fo:block text-align="center"  font-weight="bold" font-size="10pt" line-height = "13pt">Contribution to Pension Fund 8.33%</fo:block>                           	
				                            </fo:table-cell>			                           
				                            <fo:table-cell border-right-style = "solid">
				                            	<fo:block text-align="center" font-weight="bold" font-size="10pt" line-height = "13pt">Remarks</fo:block>
				                            </fo:table-cell>
					                   	</fo:table-row>
						    		
						    		<#list monthStartList as month>            		  
		                     		<#assign employeeWageDetails = employeeMap.entrySet()>
		            				<#list employeeWageDetails as employee>
		                				<#if employeeVal.getKey() == employee.getKey()>	
		                     			<fo:table-row border-style = "solid">
								             
				                             <fo:table-cell border-right-style = "solid">	
				                            	<fo:block text-align="center"  font-weight="bold" font-size="10pt" line-height = "13pt"><#if month?has_content>${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(month, "MMM - yyyy")}</#if> </fo:block>
				                            </fo:table-cell>
				                            <fo:table-cell border-right-style = "solid">	
				                            	<fo:block text-align="center" font-weight="bold" font-size="10pt" line-height = "13pt">${employee.getValue().get("wages").get(month)?if_exists}</fo:block>
				                            </fo:table-cell>
				                            <fo:table-cell border-right-style = "solid">	
				                            	<fo:block text-align="center"  font-weight="bold" font-size="10pt" line-height = "13pt"><#if employee.getValue().get("wages").get(month)?has_content>${((employee.getValue().get("wages").get(month))*(0.0833))?if_exists?string("#0.00")}<#else></#if></fo:block>                           	
				                            </fo:table-cell>			                           
				                            <fo:table-cell border-right-style = "solid">
				                            	<fo:block text-align="center" font-weight="bold" font-size="10pt" line-height = "13pt"></fo:block>
				                            </fo:table-cell>
					                   	
					                   
		                                <#if employee.getValue().get("wages").get(month)?exists>
							                 			
								                           			<#if employee.getValue().get("wages").get(month)?has_content>
								                           				<#if (employee.getValue().get("wages").get(month)*(0.0833))?has_content>
								                           				<#assign totalWage = totalWage + employee.getValue().get("wages").get(month)>
								                           			    <#assign totalContribution = totalContribution +  (employee.getValue().get("wages").get(month)*(0.0833))>
								                           			    </#if>
								                                     </#if>
					                   	</#if>
					                   	</#if>
					                   	</fo:table-row>
						    		    
						    		</#list>
						            </#list>
				              	
				         
				              	<fo:table-row border-style = "solid">
			                     		<fo:table-cell border-right-style = "solid">	
				                            	<fo:block text-align="center"  font-weight="bold" font-size="10pt" line-height = "13pt">Total</fo:block>
				                            </fo:table-cell>
				                            <fo:table-cell border-right-style = "solid">	
				                            	<fo:block text-align="center" font-weight="bold" font-size="10pt" line-height = "13pt">${totalWage?if_exists?string("#0.00")}&#160;&#160;</fo:block>
				                            </fo:table-cell>
				                            <fo:table-cell border-right-style = "solid">	
				                            	<fo:block text-align="center"  font-weight="bold" font-size="10pt" line-height = "13pt">${totalContribution?if_exists?string("#0.00")}</fo:block>                           	
				                            </fo:table-cell>			                           
				                            <fo:table-cell border-right-style = "solid">
				                            	<fo:block text-align="center" font-weight="bold" font-size="10pt" line-height = "13pt"></fo:block>
				                            </fo:table-cell>
					                   	</fo:table-row>
						    		
						     
	                     	</fo:table-body>
	                     	</fo:table>	
				   
	                  	</fo:block>	
	                  	<fo:block font-size="10pt" font-weight="bold">&#160;</fo:block>
                  		<fo:block font-size="10pt" font-weight="bold">&#160;</fo:block>
                 	  	<fo:block font-size="10pt" font-weight="bold">Certified that the difference between two contributions showed under column[3] of above and that arrived at total wages shown in column[2]at the prescribed rate is solely due to the rounding of contribution to the nearest Rs. under the rule</fo:block>
                  		<fo:block font-size="10pt" font-weight="bold">&#160;</fo:block>
                  		<fo:block font-size="10pt" font-weight="bold">Certified that the total of the contribution indicated under column[3] has already been remitted in full in Account No.10 (Pension Fund Contribution)</fo:block>
                  		<fo:block font-size="10pt" font-weight="bold">&#160;</fo:block>
                  		<fo:block font-size="10pt" font-weight="bold">&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;Date&#160;&#160; : ${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(nowTimestamp, "dd-MMM-yyyy")}</fo:block>
                  		<fo:block font-size="10pt" font-weight="bold">&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;Place : Bangalore</fo:block>
                  		<fo:block font-size="10pt" font-weight="bold" text-align="right">Signature of Employer</fo:block>
                  		<fo:block font-size="10pt" text-align="right">[office seal]</fo:block>  
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
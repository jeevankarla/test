<#escape x as x?xml>
    <fo:root xmlns:fo="http://www.w3.org/1999/XSL/Format">
        <fo:layout-master-set>
            <fo:simple-page-master master-name="main" page-height="12in" page-width="14.5in"
                     margin-left="0.1in" margin-right="0.2in">
                <fo:region-body margin-top="0.3in"/>
                <fo:region-before extent="1in"/>
                <fo:region-after extent="1in"/>
            </fo:simple-page-master>
        </fo:layout-master-set>
        
        <#if payRollEmployeeMap?has_content>
        	<fo:page-sequence master-reference="main">
        		<fo:static-content font-size="12pt" font-family="Courier,monospace"  flow-name="xsl-region-before" font-weight="bold">        
	        		<fo:block  keep-together="always" text-align="center" font-family="Courier,monospace" white-space-collapse="false" font-weight="bold">&#160;      </fo:block>
	        		<fo:block  keep-together="always" text-align="center" font-family="Courier,monospace" white-space-collapse="false" font-weight="bold">&#160;      </fo:block>
	        		<fo:block  keep-together="always" text-align="center" font-family="Courier,monospace" white-space-collapse="false" font-weight="bold">&#160;      </fo:block>
          		</fo:static-content>
          		<fo:flow flow-name="xsl-region-body" font-family="Courier,monospace">
          			<#assign noOfRecords = 0>
          			<#assign payRollEmployeeList = payRollEmployeeMap.entrySet()>
		            <#list payRollEmployeeList as payRollEmployee>
		            	<#assign totBasic = 0> <#assign totDA = 0> <#assign totIR = 0>
		            	<#assign totSP = 0> <#assign totPP = 0> <#assign totHRA = 0>
		            	<#assign totCCA = 0> <#assign totMED = 0> <#assign totCONV = 0>
		            	<#assign totINCOME = 0> <#assign totPTAX =0> <#assign totSSS = 0>
		            	<#assign totEPF = 0> <#assign totGPF = 0> <#assign totVOLPF = 0>
		            	<#assign totGIS = 0> <#assign totIT = 0> <#assign totDPTHB = 0>
		            	<#assign noOfRecords = noOfRecords+1>
		            	<#assign periodIter = 1>
		            	<#assign emplPositionAndFulfilment=delegator.findByAnd("EmplPositionAndFulfillment", {"employeePartyId" : payRollEmployee.getKey()})/>
		            	<#assign emplPositionId = emplPositionAndFulfilment[0].emplPositionTypeId>
	            		<#assign designation = delegator.findOne("EmplPositionType", {"emplPositionTypeId" : emplPositionId?if_exists}, true)>
		            	<#assign designationName=emplPositionAndFulfilment[0].name?if_exists>
		            	<#assign currentDate = Static["org.ofbiz.base.util.UtilDateTime"].toDateString(nowTimestamp, "MMM-yyyy").toUpperCase()>
		            	<fo:block font-family="Courier,monospace">
			                <fo:table border-style = "solid"> 
			                	<fo:table-column column-width="150pt"/>
			                	<fo:table-column column-width="300pt"/>
			                	<fo:table-column column-width="300pt"/>
			                	<fo:table-column column-width="275pt"/>
			                	<fo:table-body> 
			                     	<fo:table-row >
			                     		<fo:table-cell >
			                     			<fo:block text-align="left" font-size="9pt" >EMPNO : ${Static["org.ofbiz.party.party.PartyServices"].getPartyInternal(delegator, payRollEmployee.getKey())}</fo:block>
			                     		</fo:table-cell >
			                     		<#assign payRollEmployeDetails = payRollEmployee.getValue().entrySet()>
			                     		<#list payRollEmployeDetails as employeeDetail>
			                     			<#if employeeDetail.getKey() == "empName">
			                     				<#assign empName = employeeDetail.getValue()>
			                     			</#if>
			                     		</#list>
			                     		<fo:table-cell >
			                     			<fo:block text-align="left" font-size="9pt" >EMPNAME : ${(empName).toUpperCase()?if_exists}</fo:block>
			                     		</fo:table-cell >
			                     		<fo:table-cell >
		                     				<fo:block text-align="left" font-size="9pt">DESIGNATION : <#if designationName?has_content>${designationName?if_exists}<#else><#if designation?has_content>${designation.description?if_exists}</#if></#if></fo:block>
		                     			</fo:table-cell>
			                     	</fo:table-row >
			                     	<fo:table-row >
			                     		<fo:table-cell >
			                     			<fo:block text-align="left" font-size="9pt" > </fo:block>
			                     		</fo:table-cell >
			                     		<fo:table-cell >
			                     			<fo:block text-align="left" font-size="9pt" >SHED : </fo:block>
			                     		</fo:table-cell >
			                     		<#assign payRollEmployeDetails = payRollEmployee.getValue().entrySet()>
			                     		<#list payRollEmployeDetails as employeeDetail>
			                     			<#if employeeDetail.getKey() == "unitId">
			                     				<#assign unitId = employeeDetail.getValue()>
			                     			</#if>
			                     		</#list>
			                     		<fo:table-cell >
			                     			<fo:block text-align="left" font-size="9pt" >UNIT : ${unitId?if_exists}</fo:block>
			                     		</fo:table-cell >
			                     		<fo:table-cell >
			                     			<fo:block text-align="left" font-size="9pt" >COST : </fo:block>
			                     		</fo:table-cell >
			                     	</fo:table-row >
			                   </fo:table-body>
			             	</fo:table>
			             </fo:block>
			             <fo:block font-family="Courier,monospace">
			                <fo:table border-style = "solid"> 
			                	<fo:table-column column-width="50pt"/>
			                	<fo:table-column column-width="47pt"/>
			                	<fo:table-column column-width="47pt"/>
			                	<fo:table-column column-width="47pt"/>
			                	<fo:table-column column-width="47pt"/>
			                	
			                	<fo:table-column column-width="47pt"/>
			                	<fo:table-column column-width="47pt"/>
			                	<fo:table-column column-width="47pt"/>
			                	<fo:table-column column-width="47pt"/>
			                	<fo:table-column column-width="47pt"/>
			                	
			                	<fo:table-column column-width="47pt"/>
			                	<fo:table-column column-width="47pt"/>
			                	<fo:table-column column-width="47pt"/>
			                	<fo:table-column column-width="47pt"/>
			                	<fo:table-column column-width="47pt"/>
			                	
			                	<fo:table-column column-width="47pt"/>
			                	<fo:table-column column-width="47pt"/>
			                	<fo:table-column column-width="47pt"/>
			                	<fo:table-column column-width="47pt"/>
			                	<fo:table-column column-width="47pt"/>
			                	<fo:table-column column-width="47pt"/>
			                	<fo:table-column column-width="35pt"/>
			                	<fo:table-body> 
			                     	<fo:table-row >
			                     		<fo:table-cell border-style = "solid" >
			                     			<fo:block text-align="center" font-size="9pt" >MONTH</fo:block>
			                     		</fo:table-cell >
			                     		<fo:table-cell border-style = "solid" >
			                     			<fo:block text-align="center" font-size="9pt" >BASIC</fo:block>
			                     		</fo:table-cell >
			                     		<fo:table-cell border-style = "solid" >
			                     			<fo:block text-align="center" font-size="9pt" >DA</fo:block>
			                     		</fo:table-cell >
			                     		<fo:table-cell border-style = "solid" >
			                     			<fo:block text-align="center" font-size="9pt" >IR</fo:block>
			                     		</fo:table-cell >
			                     		<fo:table-cell border-style = "solid" >
			                     			<fo:block text-align="center" font-size="9pt" >SP</fo:block>
			                     		</fo:table-cell >
			                     		<fo:table-cell border-style = "solid" >
			                     			<fo:block text-align="center" font-size="9pt" >PP</fo:block>
			                     		</fo:table-cell >
			                     		<fo:table-cell border-style = "solid" >
			                     			<fo:block text-align="center" font-size="9pt" >HRA</fo:block>
			                     		</fo:table-cell >
			                     		<fo:table-cell border-style = "solid" >
			                     			<fo:block text-align="center" font-size="9pt" >CCA</fo:block>
			                     		</fo:table-cell >
			                     		<fo:table-cell border-style = "solid" >
			                     			<fo:block text-align="center" font-size="9pt" >MED</fo:block>
			                     		</fo:table-cell >
			                     		<fo:table-cell border-style = "solid" >
			                     			<fo:block text-align="center" font-size="9pt" >CONV</fo:block>
			                     		</fo:table-cell >
			                     		<fo:table-cell border-style = "solid" >
			                     			<fo:block text-align="center" font-size="9pt" >WASH</fo:block>
			                     		</fo:table-cell >
			                     		<fo:table-cell border-style = "solid" >
			                     			<fo:block text-align="center" font-size="9pt" >OTHERS</fo:block>
			                     		</fo:table-cell >
			                     		<fo:table-cell border-style = "solid" >
			                     			<fo:block text-align="center" font-size="9pt" >INCOME </fo:block>
			                     		</fo:table-cell >
			                     		<fo:table-cell border-style = "solid" >
			                     			<fo:block text-align="center" font-size="9pt" >PTAX</fo:block>
			                     		</fo:table-cell >
			                     		<fo:table-cell border-style = "solid" >
			                     			<fo:block text-align="center" font-size="9pt" >SSS</fo:block>
			                     		</fo:table-cell >
			                     		<fo:table-cell border-style = "solid" >
			                     			<fo:block text-align="center" font-size="9pt" >EPF</fo:block>
			                     		</fo:table-cell >
			                     		<fo:table-cell border-style = "solid" >
			                     			<fo:block text-align="center" font-size="9pt" >GPF</fo:block>
			                     		</fo:table-cell >
			                     		<fo:table-cell border-style = "solid" >
			                     			<fo:block text-align="center" font-size="9pt" >VOLPF</fo:block>
			                     		</fo:table-cell >
			                     		<fo:table-cell border-style = "solid" >
			                     			<fo:block text-align="center" font-size="9pt" >GIS</fo:block>
			                     		</fo:table-cell >
			                     		<fo:table-cell border-style = "solid" >
			                     			<fo:block text-align="center" font-size="9pt" >IT</fo:block>
			                     		</fo:table-cell >
			                     		<fo:table-cell border-style = "solid" >
			                     			<fo:block text-align="center" font-size="9pt" >DPTHB</fo:block>
			                     		</fo:table-cell >
			                     	</fo:table-row >
		                     		<#assign payRollEmployeDetails = payRollEmployee.getValue().entrySet()>
		                     		<#list payRollEmployeDetails as employeeDetail>
		                     			<#if employeeDetail.getKey()!= "empName" && employeeDetail.getKey()!= "unitId">
		                     				<#assign income = 0>
		                     				<#assign unIncome = 0>
		                     				<fo:table-row   border-style = "solid">
			                     				<fo:table-cell  border-style = "solid">
					                     			<fo:block text-align="right" font-size="9pt" >${employeeDetail.getKey()?replace("_", "-")?if_exists}</fo:block>
					                     		</fo:table-cell >
					                     		<#if employeeDetail.getValue()?has_content>
						                     	 	<fo:table-cell  border-style = "solid">
						                     			<fo:block text-align="right" font-size="9pt" ><#if employeeDetail.getValue()?has_content>${employeeDetail.getValue().get("PAYROL_BEN_SALARY")?if_exists}<#else>&#160;</#if></fo:block>
						                     			<#if employeeDetail.getValue().get("PAYROL_BEN_SALARY")?has_content>
						                     				<#assign income = income+employeeDetail.getValue().get("PAYROL_BEN_SALARY")>
						                     				<#assign totBasic = totBasic + employeeDetail.getValue().get("PAYROL_BEN_SALARY")>
						                     			</#if>
						                     		</fo:table-cell >
						                    	 	<fo:table-cell  border-style = "solid">
						                     			<fo:block text-align="right" font-size="9pt" ><#if employeeDetail.getValue()?has_content>${employeeDetail.getValue().get("PAYROL_BEN_DA")?if_exists}<#else>&#160;</#if></fo:block>
						                     			<#if employeeDetail.getValue().get("PAYROL_BEN_DA")?has_content>
						                     				<#assign income = income+employeeDetail.getValue().get("PAYROL_BEN_DA")>
						                     				<#assign totDA = totDA + employeeDetail.getValue().get("PAYROL_BEN_DA")>
						                     			</#if>
						                     		</fo:table-cell >
						                     		<fo:table-cell  border-style = "solid">
						                     			<fo:block text-align="right" font-size="9pt" ><#if employeeDetail.getValue()?has_content>${employeeDetail.getValue().get("PAYROL_BEN_IR")?if_exists}<#else>&#160;</#if></fo:block>
						                     			<#if employeeDetail.getValue().get("PAYROL_BEN_IR")?has_content>
						                     				<#assign income = income+employeeDetail.getValue().get("PAYROL_BEN_IR")>
						                     				<#assign totIR = totIR + employeeDetail.getValue().get("PAYROL_BEN_IR")>
						                     			</#if>
						                     		</fo:table-cell >
						                     		<fo:table-cell  border-style = "solid">
						                     			<fo:block text-align="right" font-size="9pt" ><#if employeeDetail.getValue()?has_content>${employeeDetail.getValue().get("PAYROL_BEN_SPLPAY")?if_exists}<#else>&#160;</#if></fo:block>
						                     			<#if employeeDetail.getValue().get("PAYROL_BEN_SPLPAY")?has_content>
						                     				<#assign income = income+employeeDetail.getValue().get("PAYROL_BEN_SPLPAY")>
						                     				<#assign totSP = totSP + employeeDetail.getValue().get("PAYROL_BEN_SPLPAY")>
						                     			</#if>
						                     		</fo:table-cell >
						                     		<fo:table-cell  border-style = "solid">
						                     			<fo:block text-align="right" font-size="9pt" ><#if employeeDetail.getValue()?has_content>${employeeDetail.getValue().get("PAYROL_BEN_PNLPAY")?if_exists}<#else>&#160;</#if></fo:block>
						                     			<#if employeeDetail.getValue().get("PAYROL_BEN_PNLPAY")?has_content>
						                     				<#assign income = income+employeeDetail.getValue().get("PAYROL_BEN_PNLPAY")>
						                     				<#assign totPP = totPP + employeeDetail.getValue().get("PAYROL_BEN_PNLPAY")>
						                     			</#if>
						                     		</fo:table-cell >
						                     		<fo:table-cell  border-style = "solid">
						                     			<fo:block text-align="right" font-size="9pt" ><#if employeeDetail.getValue()?has_content>${employeeDetail.getValue().get("PAYROL_BEN_HRA")?if_exists}<#else>&#160;</#if></fo:block>
						                     			<#if employeeDetail.getValue().get("PAYROL_BEN_HRA")?has_content>
						                     				<#assign income = income+employeeDetail.getValue().get("PAYROL_BEN_HRA")>
						                     				<#assign totHRA = totHRA + employeeDetail.getValue().get("PAYROL_BEN_HRA")>
						                     			</#if>
						                     		</fo:table-cell >
						                     		<fo:table-cell  border-style = "solid">
						                     			<fo:block text-align="right" font-size="9pt" ><#if employeeDetail.getValue()?has_content>${employeeDetail.getValue().get("PAYROL_BEN_CCA")?if_exists}<#else>&#160;</#if></fo:block>
						                     			<#if employeeDetail.getValue().get("PAYROL_BEN_CCA")?has_content>
						                     				<#assign income = income+employeeDetail.getValue().get("PAYROL_BEN_CCA")>
						                     				<#assign totCCA = totCCA + employeeDetail.getValue().get("PAYROL_BEN_CCA")>
						                     			</#if>
						                     		</fo:table-cell >
						                     		<fo:table-cell  border-style = "solid">
						                     			<fo:block text-align="right" font-size="9pt" ><#if employeeDetail.getValue()?has_content>${employeeDetail.getValue().get("PAYROL_BEN_MEDALW")?if_exists}<#else>&#160;</#if></fo:block>
						                     			<#if employeeDetail.getValue().get("PAYROL_BEN_MEDALW")?has_content>
						                     				<#assign income = income+employeeDetail.getValue().get("PAYROL_BEN_MEDALW")>
						                     				<#assign totMED = totMED + employeeDetail.getValue().get("PAYROL_BEN_MEDALW")>
						                     			</#if>
						                     		</fo:table-cell >
						                     		<fo:table-cell  border-style = "solid">
						                     			<fo:block text-align="right" font-size="9pt" ><#if employeeDetail.getValue()?has_content>${employeeDetail.getValue().get("PAYROL_BEN_CONVEY")?if_exists}<#else>&#160;</#if></fo:block>
						                     			<#if employeeDetail.getValue().get("PAYROL_BEN_CONVEY")?has_content>
						                     				<#assign income = income+employeeDetail.getValue().get("PAYROL_BEN_CONVEY")>
						                     				<#assign totCONV = totCONV + employeeDetail.getValue().get("PAYROL_BEN_CONVEY")>
						                     			</#if>
						                     		</fo:table-cell >
						                     		<fo:table-cell  border-style = "solid">
						                     			<fo:block text-align="right" font-size="9pt" ><#if employeeDetail.getValue()?has_content>${employeeDetail.getValue().get("PAYROL_BEN_WASHALW")?if_exists}<#else>&#160;</#if></fo:block>
						                     			<#if employeeDetail.getValue().get("PAYROL_BEN_WASHALW")?has_content>
						                     				<#assign income = income+employeeDetail.getValue().get("PAYROL_BEN_WASHALW")>
						                     			</#if>
						                     		</fo:table-cell >
						                     		<fo:table-cell  border-style = "solid">
						                     			<fo:block text-align="right" font-size="9pt" ><#if employeeDetail.getValue()?has_content>${employeeDetail.getValue().get("PAYROL_BEN_OTHERS")?if_exists}<#else>&#160;</#if></fo:block>
						                     			<#if employeeDetail.getValue().get("PAYROL_BEN_OTHERS")?has_content>
						                     				<#assign income = income+employeeDetail.getValue().get("PAYROL_BEN_OTHERS")>
						                     			</#if>
						                     		</fo:table-cell >
						                     		<fo:table-cell  border-style = "solid">
						                     			<fo:block text-align="right" font-size="9pt" ><#if income != 0>${income?if_exists}<#else>&#160;</#if></fo:block>
						                     		</fo:table-cell >
						                     		<#if unIncome != 0>
					                     				<#assign totINCOME = totINCOME + income>
					                     			</#if>
						                     		<#if employeeDetail.getValue().get("PAYROL_DD_PTAX")?has_content>
							                     		<fo:table-cell  border-style = "solid">
							                     			<fo:block text-align="right" font-size="9pt" ><#if employeeDetail.getValue()?has_content>${employeeDetail.getValue().get("PAYROL_DD_PTAX")*(-1)?if_exists}<#else>&#160;</#if></fo:block>
							                     		</fo:table-cell >
							                     		<#assign totPTAX = totPTAX + employeeDetail.getValue().get("PAYROL_DD_PTAX")*(-1)>
						                     		<#else>
							                     		<fo:table-cell  border-style = "solid">
							                     			<fo:block text-align="right" font-size="9pt" >&#160;</fo:block>
							                     		</fo:table-cell > 
							                     	</#if>
							                     	<#if employeeDetail.getValue().get("PAYROL_DD_SSS")?has_content>
							                     		<fo:table-cell  border-style = "solid">
							                     			<fo:block text-align="right" font-size="9pt" ><#if employeeDetail.getValue()?has_content>${employeeDetail.getValue().get("PAYROL_DD_SSS")*(-1)?if_exists}<#else>&#160;</#if></fo:block>
							                     		</fo:table-cell >
							                     		<#assign totSSS = totSSS + employeeDetail.getValue().get("PAYROL_DD_SSS")*(-1)>
						                     		<#else>
							                     		<fo:table-cell  border-style = "solid">
							                     			<fo:block text-align="right" font-size="9pt" >&#160;</fo:block>
							                     		</fo:table-cell > 
							                     	</#if>
							                     	<#if employeeDetail.getValue().get("PAYROL_DD_EPF")?has_content>
							                     		<fo:table-cell  border-style = "solid">
							                     			<fo:block text-align="right" font-size="9pt" ><#if employeeDetail.getValue()?has_content>${employeeDetail.getValue().get("PAYROL_DD_EPF")*(-1)?if_exists}<#else>&#160;</#if></fo:block>
							                     		</fo:table-cell >
							                     		<#assign totEPF = totEPF + employeeDetail.getValue().get("PAYROL_DD_EPF")*(-1)>
						                     		<#else>
							                     		<fo:table-cell  border-style = "solid">
							                     			<fo:block text-align="right" font-size="9pt" >&#160;</fo:block>
							                     		</fo:table-cell > 
							                     	</#if>
						                     		<#if employeeDetail.getValue().get("PAYROL_DD_GPF")?has_content>
							                     		<fo:table-cell  border-style = "solid">
							                     			<fo:block text-align="right" font-size="9pt" ><#if employeeDetail.getValue()?has_content>${employeeDetail.getValue().get("PAYROL_DD_GPF")*(-1)?if_exists}<#else>&#160;</#if></fo:block>
							                     		</fo:table-cell >
							                     		<#assign totGPF = totGPF + employeeDetail.getValue().get("PAYROL_DD_GPF")*(-1)> 
							                     	<#else>
							                     		<fo:table-cell  border-style = "solid">
							                     			<fo:block text-align="right" font-size="9pt" >&#160;</fo:block>
							                     		</fo:table-cell > 
							                     	</#if>
							                     	<#if employeeDetail.getValue().get("PAYROL_DD_VOLPF")?has_content>
							                     		<fo:table-cell  border-style = "solid">
							                     			<fo:block text-align="right" font-size="9pt" ><#if employeeDetail.getValue()?has_content>${employeeDetail.getValue().get("PAYROL_DD_VOLPF")*(-1)?if_exists}<#else>&#160;</#if></fo:block>
							                     		</fo:table-cell >
							                     		<#assign totVOLPF = totVOLPF + employeeDetail.getValue().get("PAYROL_DD_VOLPF")*(-1)>
						                     		<#else>
							                     		<fo:table-cell  border-style = "solid">
							                     			<fo:block text-align="right" font-size="9pt" >&#160;</fo:block>
							                     		</fo:table-cell > 
							                     	</#if>
							                     	<#if employeeDetail.getValue().get("PAYROL_DD_GIS")?has_content>
							                     		<fo:table-cell  border-style = "solid">
							                     			<fo:block text-align="right" font-size="9pt" ><#if employeeDetail.getValue()?has_content>${employeeDetail.getValue().get("PAYROL_DD_GIS")*(-1)?if_exists}<#else>&#160;</#if></fo:block>
							                     		</fo:table-cell >
							                     		<#assign totGIS = totGIS + employeeDetail.getValue().get("PAYROL_DD_GIS")*(-1)>
						                     		<#else>
							                     		<fo:table-cell  border-style = "solid">
							                     			<fo:block text-align="right" font-size="9pt" >&#160;</fo:block>
							                     		</fo:table-cell > 
							                     	</#if>
						                     		<#if employeeDetail.getValue().get("PAYROL_DD_IT")?has_content>
							                     		<fo:table-cell  border-style = "solid">
							                     			<fo:block text-align="right" font-size="9pt" >${employeeDetail.getValue().get("PAYROL_DD_IT")*(-1)?if_exists}</fo:block>
							                     		</fo:table-cell >
							                     		<#assign totIT = totIT + employeeDetail.getValue().get("PAYROL_DD_IT")*(-1)>
						                     		<#else>
							                     		<fo:table-cell  border-style = "solid">
							                     			<fo:block text-align="right" font-size="9pt" >&#160;</fo:block>
							                     		</fo:table-cell > 
							                     	</#if>
						                     		<#if employeeDetail.getValue().get("PAYROL_DD_DPTHB")?has_content>
							                       		<fo:table-cell  border-style = "solid">
							                     			<fo:block text-align="right" font-size="9pt" >${employeeDetail.getValue().get("PAYROL_DD_DPTHB")*(-1)?if_exists}</fo:block>
							                     		</fo:table-cell >  
							                     		<#assign totDPTHB = totDPTHB + employeeDetail.getValue().get("PAYROL_DD_DPTHB")*(-1)>
							                     	<#else>
							                     		<fo:table-cell  border-style = "solid">
							                     			<fo:block text-align="right" font-size="9pt" >&#160;</fo:block>
							                     		</fo:table-cell > 
							                     	</#if>
						                     	<#else>
						                     		<#assign unPayRollDetails = unPayRollEmployeeMap.entrySet()>
						                     		<#list unPayRollDetails as unPayRollValues>
						                     			<#if unPayRollValues.getKey() == payRollEmployee.getKey()>
								                     		<fo:table-cell  border-style = "solid">
								                     			<fo:block text-align="right" font-size="9pt" ><#if unPayRollValues.getValue()?has_content>${unPayRollValues.getValue().get("PAYROL_BEN_SALARY")?if_exists}<#else>&#160;</#if></fo:block>
								                     			<#if unPayRollValues.getValue().get("PAYROL_BEN_SALARY")?has_content>
								                     				<#assign unIncome = unIncome+unPayRollValues.getValue().get("PAYROL_BEN_SALARY")>
								                     				<#assign totBasic = totBasic + unPayRollValues.getValue().get("PAYROL_BEN_SALARY")>
								                     			</#if>
								                     		</fo:table-cell >
								                    	 	<fo:table-cell  border-style = "solid">
								                     			<fo:block text-align="right" font-size="9pt" ><#if unPayRollValues.getValue()?has_content>${unPayRollValues.getValue().get("PAYROL_BEN_DA")?if_exists}<#else>&#160;</#if></fo:block>
								                     			<#if unPayRollValues.getValue().get("PAYROL_BEN_DA")?has_content>
								                     				<#assign unIncome = unIncome+unPayRollValues.getValue().get("PAYROL_BEN_DA")>
								                     				<#assign totDA = totDA + unPayRollValues.getValue().get("PAYROL_BEN_DA")>
								                     			</#if>
								                     		</fo:table-cell >
								                     		<fo:table-cell  border-style = "solid">
								                     			<fo:block text-align="right" font-size="9pt" ><#if unPayRollValues.getValue()?has_content>${unPayRollValues.getValue().get("PAYROL_BEN_IR")?if_exists}<#else>&#160;</#if></fo:block>
								                     			<#if unPayRollValues.getValue().get("PAYROL_BEN_IR")?has_content>
								                     				<#assign unIncome = unIncome+unPayRollValues.getValue().get("PAYROL_BEN_IR")>
								                     				<#assign totIR = totIR + unPayRollValues.getValue().get("PAYROL_BEN_IR")>
								                     			</#if>
								                     		</fo:table-cell >
								                     		<fo:table-cell  border-style = "solid">
								                     			<fo:block text-align="right" font-size="9pt" ><#if unPayRollValues.getValue()?has_content>${unPayRollValues.getValue().get("PAYROL_BEN_SPLPAY")?if_exists}<#else>&#160;</#if></fo:block>
								                     			<#if unPayRollValues.getValue().get("PAYROL_BEN_SPLPAY")?has_content>
								                     				<#assign unIncome = unIncome+unPayRollValues.getValue().get("PAYROL_BEN_SPLPAY")>
								                     				<#assign totSP = totSP + unPayRollValues.getValue().get("PAYROL_BEN_SPLPAY")>
								                     			</#if>
								                     		</fo:table-cell >
								                     		<fo:table-cell  border-style = "solid">
								                     			<fo:block text-align="right" font-size="9pt" ><#if unPayRollValues.getValue()?has_content>${unPayRollValues.getValue().get("PAYROL_BEN_PNLPAY")?if_exists}<#else>&#160;</#if></fo:block>
								                     			<#if unPayRollValues.getValue().get("PAYROL_BEN_PNLPAY")?has_content>
								                     				<#assign unIncome = unIncome+unPayRollValues.getValue().get("PAYROL_BEN_PNLPAY")>
								                     				<#assign totPP = totPP + unPayRollValues.getValue().get("PAYROL_BEN_PNLPAY")>
								                     			</#if>
								                     		</fo:table-cell >
								                     		<fo:table-cell  border-style = "solid">
								                     			<fo:block text-align="right" font-size="9pt" ><#if unPayRollValues.getValue()?has_content>${unPayRollValues.getValue().get("PAYROL_BEN_HRA")?if_exists}<#else>&#160;</#if></fo:block>
								                     			<#if unPayRollValues.getValue().get("PAYROL_BEN_HRA")?has_content>
								                     				<#assign unIncome = unIncome+unPayRollValues.getValue().get("PAYROL_BEN_HRA")>
								                     				<#assign totHRA = totHRA + unPayRollValues.getValue().get("PAYROL_BEN_HRA")>
								                     			</#if>
								                     		</fo:table-cell >
								                     		<fo:table-cell  border-style = "solid">
								                     			<fo:block text-align="right" font-size="9pt" ><#if unPayRollValues.getValue()?has_content>${unPayRollValues.getValue().get("PAYROL_BEN_CCA")?if_exists}<#else>&#160;</#if></fo:block>
								                     			<#if unPayRollValues.getValue().get("PAYROL_BEN_CCA")?has_content>
								                     				<#assign unIncome = unIncome+unPayRollValues.getValue().get("PAYROL_BEN_CCA")>
								                     				<#assign totCCA = totCCA + unPayRollValues.getValue().get("PAYROL_BEN_CCA")>
								                     			</#if>
								                     		</fo:table-cell >
								                     		<fo:table-cell  border-style = "solid">
								                     			<fo:block text-align="right" font-size="9pt" ><#if unPayRollValues.getValue()?has_content>${unPayRollValues.getValue().get("PAYROL_BEN_MEDALW")?if_exists}<#else>&#160;</#if></fo:block>
								                     			<#if unPayRollValues.getValue().get("PAYROL_BEN_MEDALW")?has_content>
								                     				<#assign unIncome = unIncome+unPayRollValues.getValue().get("PAYROL_BEN_MEDALW")>
								                     				<#assign totMED = totMED + unPayRollValues.getValue().get("PAYROL_BEN_MEDALW")>
								                     			</#if>
								                     		</fo:table-cell >
								                     		<fo:table-cell  border-style = "solid">
								                     			<fo:block text-align="right" font-size="9pt" ><#if unPayRollValues.getValue()?has_content>${unPayRollValues.getValue().get("PAYROL_BEN_CONVEY")?if_exists}<#else>&#160;</#if></fo:block>
								                     			<#if unPayRollValues.getValue().get("PAYROL_BEN_CONVEY")?has_content>
								                     				<#assign unIncome = income+unPayRollValues.getValue().get("PAYROL_BEN_CONVEY")>
								                     				<#assign totCONV = totCONV + unPayRollValues.getValue().get("PAYROL_BEN_CONVEY")>
								                     			</#if>
								                     		</fo:table-cell >
								                     		<fo:table-cell  border-style = "solid">
								                     			<fo:block text-align="right" font-size="9pt" ><#if unPayRollValues.getValue()?has_content>${unPayRollValues.getValue().get("PAYROL_BEN_WASHALW")?if_exists}<#else>&#160;</#if></fo:block>
								                     			<#if unPayRollValues.getValue().get("PAYROL_BEN_WASHALW")?has_content>
								                     				<#assign unIncome = unIncome+unPayRollValues.getValue().get("PAYROL_BEN_WASHALW")>
								                     				<#assign totWASH = totWASH + unPayRollValues.getValue().get("PAYROL_BEN_WASHALW")>
								                     			</#if>
								                     		</fo:table-cell >
								                     		<fo:table-cell  border-style = "solid">
								                     			<fo:block text-align="right" font-size="9pt" ><#if unPayRollValues.getValue()?has_content>${unPayRollValues.getValue().get("PAYROL_BEN_OTHERS")?if_exists}<#else>&#160;</#if></fo:block>
								                     			<#if unPayRollValues.getValue().get("PAYROL_BEN_OTHERS")?has_content>
								                     				<#assign unIncome = unIncome+unPayRollValues.getValue().get("PAYROL_BEN_OTHERS")>
								                     				<#assign totOTHERS = totOTHERS + unPayRollValues.getValue().get("PAYROL_BEN_OTHERS")>
								                     			</#if>
								                     		</fo:table-cell >
								                     		<fo:table-cell  border-style = "solid">
								                     			<fo:block text-align="right" font-size="9pt" ><#if unIncome != 0>${unIncome?if_exists}<#else>&#160;</#if></fo:block>
								                     			<#if unIncome != 0>
								                     				<#assign totINCOME = totINCOME + unIncome>
								                     			</#if>
								                     		</fo:table-cell >
								                     		<#if unPayRollValues.getValue().get("PAYROL_DD_PTAX")?has_content>
									                     		<fo:table-cell  border-style = "solid">
									                     			<fo:block text-align="right" font-size="9pt" ><#if unPayRollValues.getValue()?has_content>${unPayRollValues.getValue().get("PAYROL_DD_PTAX")*(-1)?if_exists}<#else>&#160;</#if></fo:block>
									                     		</fo:table-cell >
									                     		<#assign totPTAX = totPTAX + unPayRollValues.getValue().get("PAYROL_DD_PTAX")*(-1)>
								                     		<#else>
									                     		<fo:table-cell  border-style = "solid">
									                     			<fo:block text-align="right" font-size="9pt" >&#160;</fo:block>
									                     		</fo:table-cell > 
									                     	</#if>
									                     	<#if unPayRollValues.getValue().get("PAYROL_DD_SSS")?has_content>
									                     		<fo:table-cell  border-style = "solid">
									                     			<fo:block text-align="right" font-size="9pt" ><#if unPayRollValues.getValue()?has_content>${unPayRollValues.getValue().get("PAYROL_DD_SSS")*(-1)?if_exists}<#else>&#160;</#if></fo:block>
									                     		</fo:table-cell >
									                     		<#assign totSSS = totSSS + unPayRollValues.getValue().get("PAYROL_DD_SSS")*(-1)>
								                     		<#else>
									                     		<fo:table-cell  border-style = "solid">
									                     			<fo:block text-align="right" font-size="9pt" >&#160;</fo:block>
									                     		</fo:table-cell > 
									                     	</#if>
									                     	<#if unPayRollValues.getValue().get("PAYROL_DD_EPF")?has_content>
									                     		<fo:table-cell  border-style = "solid">
									                     			<fo:block text-align="right" font-size="9pt" ><#if unPayRollValues.getValue()?has_content>${unPayRollValues.getValue().get("PAYROL_DD_EPF")*(-1)?if_exists}<#else>&#160;</#if></fo:block>
									                     		</fo:table-cell >
									                     		<#assign totEPF = totEPF + unPayRollValues.getValue().get("PAYROL_DD_EPF")*(-1)>
								                     		<#else>
									                     		<fo:table-cell  border-style = "solid">
									                     			<fo:block text-align="right" font-size="9pt" >&#160;</fo:block>
									                     		</fo:table-cell > 
									                     	</#if>
								                     		<#if unPayRollValues.getValue().get("PAYROL_DD_GPF")?has_content>
									                     		<fo:table-cell  border-style = "solid">
									                     			<fo:block text-align="right" font-size="9pt" ><#if unPayRollValues.getValue()?has_content>${unPayRollValues.getValue().get("PAYROL_DD_GPF")*(-1)?if_exists}<#else>&#160;</#if></fo:block>
									                     		</fo:table-cell > 
									                     		<#assign totGPF = totGPF + unPayRollValues.getValue().get("PAYROL_DD_GPF")*(-1)>
									                     	<#else>
									                     		<fo:table-cell  border-style = "solid">
									                     			<fo:block text-align="right" font-size="9pt" >&#160;</fo:block>
									                     		</fo:table-cell > 
									                     	</#if>
									                     	<#if unPayRollValues.getValue().get("PAYROL_DD_VOLPF")?has_content>
									                     		<fo:table-cell  border-style = "solid">
									                     			<fo:block text-align="right" font-size="9pt" ><#if unPayRollValues.getValue()?has_content>${unPayRollValues.getValue().get("PAYROL_DD_VOLPF")*(-1)?if_exists}<#else>&#160;</#if></fo:block>
									                     		</fo:table-cell >
									                     		<#assign totVOLPF = totVOLPF + unPayRollValues.getValue().get("PAYROL_DD_VOLPF")*(-1)>
								                     		<#else>
									                     		<fo:table-cell  border-style = "solid">
									                     			<fo:block text-align="right" font-size="9pt" >&#160;</fo:block>
									                     		</fo:table-cell > 
									                     	</#if>
									                     	<#if unPayRollValues.getValue().get("PAYROL_DD_GIS")?has_content>
									                     		<fo:table-cell  border-style = "solid">
									                     			<fo:block text-align="right" font-size="9pt" ><#if unPayRollValues.getValue()?has_content>${unPayRollValues.getValue().get("PAYROL_DD_GIS")*(-1)?if_exists}<#else>&#160;</#if></fo:block>
									                     		</fo:table-cell >
									                     		<#assign totGIS = totGIS + unPayRollValues.getValue().get("PAYROL_DD_GIS")*(-1)>
								                     		<#else>
									                     		<fo:table-cell  border-style = "solid">
									                     			<fo:block text-align="right" font-size="9pt" >&#160;</fo:block>
									                     		</fo:table-cell > 
									                     	</#if>
								                     		<#if unPayRollValues.getValue().get("PAYROL_DD_IT")?has_content>
									                     		<fo:table-cell  border-style = "solid">
									                     			<fo:block text-align="right" font-size="9pt" >${unPayRollValues.getValue().get("PAYROL_DD_IT")*(-1)?if_exists}</fo:block>
									                     		</fo:table-cell >
									                     		<#assign totIT = totIT + unPayRollValues.getValue().get("PAYROL_DD_IT")*(-1)>
								                     		<#else>
									                     		<fo:table-cell  border-style = "solid">
									                     			<fo:block text-align="right" font-size="9pt" >&#160;</fo:block>
									                     		</fo:table-cell > 
									                     	</#if>
								                     		<#if unPayRollValues.getValue().get("PAYROL_DD_DPTHB")?has_content>
									                       		<fo:table-cell  border-style = "solid">
									                     			<fo:block text-align="right" font-size="9pt" >${unPayRollValues.getValue().get("PAYROL_DD_DPTHB")*(-1)?if_exists}</fo:block>
									                     		</fo:table-cell > 
									                     		 <#assign totDPTHB = totDPTHB + unPayRollValues.getValue().get("PAYROL_DD_DPTHB")*(-1)>
									                     	<#else>
									                     		<fo:table-cell  border-style = "solid">
									                     			<fo:block text-align="right" font-size="9pt" >&#160;</fo:block>
									                     		</fo:table-cell > 
									                     	</#if>
									                     	
									                     	<#if periodIter == 0>
									                     		<fo:table-cell  border-style = "solid">
									                     			<fo:block text-align="right" font-size="9pt" >PRO</fo:block>
									                     		</fo:table-cell > 
									                     	</#if>
									                     	<#if currentDate == (employeeDetail.getKey()?replace("_", "-"))>
									                     		<#assign periodIter = 0>
									                     	</#if>
									                     	
								                     	</#if>
								                     </#list>
						                     	</#if>
			                     			</fo:table-row >
			                     		</#if>
		                     		</#list>
		                     		<fo:table-row >
		                     			<fo:table-cell  border-style = "solid">
			                     			<fo:block text-align="center" font-size="9pt">TOTAL </fo:block>
			                     		</fo:table-cell >
		                     			<fo:table-cell  border-style = "solid">
			                     			<fo:block text-align="right" font-size="9pt" >${totBasic?if_exists}</fo:block>
			                     		</fo:table-cell >
			                     		<fo:table-cell  border-style = "solid">
			                     			<fo:block text-align="right" font-size="9pt" >${totDA?if_exists}</fo:block>
			                     		</fo:table-cell >
			                     		<fo:table-cell  border-style = "solid">
			                     			<fo:block text-align="right" font-size="9pt" >${totIR?if_exists}</fo:block>
			                     		</fo:table-cell >
			                     		<fo:table-cell  border-style = "solid">
			                     			<fo:block text-align="right" font-size="9pt" >${totSP?if_exists}</fo:block>
			                     		</fo:table-cell >
			                     		<fo:table-cell  border-style = "solid">
			                     			<fo:block text-align="right" font-size="9pt" >${totPP?if_exists}</fo:block>
			                     		</fo:table-cell >
			                     		<fo:table-cell  border-style = "solid">
			                     			<fo:block text-align="right" font-size="9pt" >${totHRA?if_exists}</fo:block>
			                     		</fo:table-cell >
			                     		<fo:table-cell  border-style = "solid">
			                     			<fo:block text-align="right" font-size="9pt" >${totCCA?if_exists}</fo:block>
			                     		</fo:table-cell >
			                     		<fo:table-cell  border-style = "solid">
			                     			<fo:block text-align="right" font-size="9pt" >${totMED?if_exists}</fo:block>
			                     		</fo:table-cell >
			                     		<fo:table-cell  border-style = "solid">
			                     			<fo:block text-align="right" font-size="9pt" >${totCONV?if_exists}</fo:block>
			                     		</fo:table-cell > 
			                     		<fo:table-cell  border-style = "solid">
			                     			<fo:block text-align="right" font-size="9pt" >${totWASH?if_exists}</fo:block>
			                     		</fo:table-cell >
			                     		<fo:table-cell  border-style = "solid">
			                     			<fo:block text-align="right" font-size="9pt" >${totOTHERS?if_exists}</fo:block>
			                     		</fo:table-cell >
			                     		<fo:table-cell  border-style = "solid">
			                     			<fo:block text-align="right" font-size="9pt" >${totINCOME?if_exists}</fo:block>
			                     		</fo:table-cell >
			                     		<fo:table-cell  border-style = "solid">
			                     			<fo:block text-align="right" font-size="9pt" >${totPTAX?if_exists}</fo:block>
			                     		</fo:table-cell > 
			                     		<fo:table-cell  border-style = "solid">
			                     			<fo:block text-align="right" font-size="9pt" >${totSSS?if_exists}</fo:block>
			                     		</fo:table-cell >
			                     		<fo:table-cell  border-style = "solid">
			                     			<fo:block text-align="right" font-size="9pt" >${totEPF?if_exists}</fo:block>
			                     		</fo:table-cell >
			                     		<fo:table-cell  border-style = "solid">
			                     			<fo:block text-align="right" font-size="9pt" >${totGPF?if_exists}</fo:block>
			                     		</fo:table-cell > 
			                     		<fo:table-cell  border-style = "solid">
			                     			<fo:block text-align="right" font-size="9pt" >${totVOLPF?if_exists}</fo:block>
			                     		</fo:table-cell >
			                     		<fo:table-cell  border-style = "solid">
			                     			<fo:block text-align="right" font-size="9pt" >${totGIS?if_exists}</fo:block>
			                     		</fo:table-cell >
			                     		<fo:table-cell  border-style = "solid">
			                     			<fo:block text-align="right" font-size="9pt" >${totIT?if_exists}</fo:block>
			                     		</fo:table-cell >
			                     		<fo:table-cell  border-style = "solid">
			                     			<fo:block text-align="right" font-size="9pt" >${totDPTHB?if_exists}</fo:block>
			                     		</fo:table-cell >
		                     		</fo:table-row >
			                   </fo:table-body>
			             	</fo:table>
			             </fo:block>
			             <fo:block linefeed-treatment="preserve">&#xA;</fo:block>
			             <#if noOfRecords == 4 >
         					<#assign noOfRecords=0>
							<fo:block page-break-after="always"></fo:block>
             			</#if>
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
        	
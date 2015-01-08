<#escape x as x?xml>
    <fo:root xmlns:fo="http://www.w3.org/1999/XSL/Format">
        <fo:layout-master-set>
            <fo:simple-page-master master-name="main" page-height="12in" page-width="12in"
                     margin-left="0.5in" margin-right="0.2in">
                <fo:region-body margin-top="0.1in"/>
                <fo:region-before extent="1in"/>
                <fo:region-after extent="1in"/>
            </fo:simple-page-master>
        </fo:layout-master-set>
        
        <#if payRollMap?has_content>
			<#assign noofLines=1>
			<#assign SNo=1>
			<#assign listNo=1>
			<fo:page-sequence master-reference="main">
        		<fo:static-content font-size="12pt" font-family="Courier,monospace"  flow-name="xsl-region-before" font-weight="bold">        
	        		<fo:block  keep-together="always" text-align="center" font-family="Courier,monospace" white-space-collapse="false" font-weight="bold">&#160;      </fo:block>
	        		<fo:block  keep-together="always" text-align="center" font-family="Courier,monospace" white-space-collapse="false" font-weight="bold">&#160;      </fo:block>
	        		<fo:block  keep-together="always" text-align="center" font-family="Courier,monospace" white-space-collapse="false" font-weight="bold">&#160;      </fo:block>
          		</fo:static-content>
          		<fo:flow flow-name="xsl-region-body" font-family="Courier,monospace">
          			<#assign payRollHeaderList = payRollMap.entrySet()>
		            <#list payRollHeaderList as payRollHeader>
		            	<#assign payHeader = delegator.findOne("PayrollHeader", {"payrollHeaderId" : payRollHeader.getKey()}, true)>
      	 				<#assign partyId = payHeader.partyIdFrom>
	            		<#assign emplPositionAndFulfilment=delegator.findByAnd("EmplPositionAndFulfillment", {"employeePartyId" : partyId})/>
	            		<#assign designation = delegator.findOne("EmplPositionType", {"emplPositionTypeId" : emplPositionAndFulfilment[0].emplPositionTypeId?if_exists}, true)>
                     	<#assign designationName=emplPositionAndFulfilment[0].name?if_exists>
	            		<#assign emplLeavesDetails = delegator.findOne("PayrollAttendance", {"partyId" : partyId, "customTimePeriodId": timePeriod?if_exists}, true)/>
	            		<#assign EmployeeDetails = delegator.findOne("EmployeeDetail", {"partyId" : partyId}, true)>
	            		<#assign costCodeDetails=delegator.findByAnd("PartyRelationship", {"partyIdTo" : partyId})/>
	            		<#assign costCode=costCodeDetails[0].comments?if_exists>
	            		<#assign totalEarnings=0>
	            		<#assign totalDeductions=0>
	            		<#if EmployeeDetails?has_content>
	            			<#assign GISNo=EmployeeDetails.get("presentEpf")?if_exists>
	            		</#if>
          				<#if emplLeavesDetails?has_content>
                			<#assign noOfPayableDays=emplLeavesDetails.get("noOfPayableDays")?if_exists>
	            		<#else>
	            			<#assign noOfPayableDays=0>
	            		</#if>
	            		<#assign hdfcDedAmount = 0>
	            		<#assign SBHDedAmount = 0>
	            		<#assign canaraDedAmount = 0>
	            		<#if parameters.OrganizationId == "MPF_HYD">
	            			<#assign hdfcDedAmount = payRollHeader.getValue().get("PAYROL_DD_DEDRS04")>
	            			<#assign SBHDedAmount = payRollHeader.getValue().get("PAYROL_DD_DEDRS04")>
	            			<#assign canaraDedAmount = payRollHeader.getValue().get("PAYROL_DD_DEDID18")>
	            			<#assign otherDedAmount = payRollHeader.getValue().get("PAYROL_DD_BL_317")>
	            		<#else>
	            			<#assign hdfcDedAmount = payRollHeader.getValue().get("PAYROL_DD_1150_BL315")>
	            			<#assign SBHDedAmount = payRollHeader.getValue().get("PAYROL_DD_DEDRS11")>
	            			<#assign canaraDedAmount = payRollHeader.getValue().get("PAYROL_DD_CANR_308")>
	            			<#assign otherDedAmount = payRollHeader.getValue().get("PAYROL_DD_OTHERBANK")>
	            		</#if>
	            		<#assign hblBalance = 0>
						<#assign festAdvBalance = 0>
						<#assign convBalance = 0>
						<#assign mrgBalance = 0>
						<#assign gpfBalance = 0>
						<#assign eduBalance = 0>
						<#assign medicalBalance = 0>
						<#assign payAdvBalance = 0>
						<#assign courtBalance = 0>
						<#assign SBHBalance = 0>
						<#assign canaraBalance = 0>
	            		<#if loanBalancesMap?has_content>
							<#assign loanBalancesList = loanBalancesMap.entrySet()>
							<#list loanBalancesList as loanBalancesDetails>
								<#if loanBalancesDetails.getKey() == partyId>
									<#assign festAdvBalance = loanBalancesDetails.getValue().get("PAYROL_DD_FESTADV")>
									<#assign convBalance = loanBalancesDetails.getValue().get("PAYROL_BEN_CONVEY")>
									<#assign mrgBalance = loanBalancesDetails.getValue().get("PAYROL_DD_MRGLN")>
									<#assign gpfBalance = loanBalancesDetails.getValue().get("PAYROL_DD_GPFLN")>
									<#assign eduBalance = loanBalancesDetails.getValue().get("PAYROL_DD_EDNADV")>
									<#assign medicalBalance = loanBalancesDetails.getValue().get("PAYROL_DD_MEDADV")>
									<#assign payAdvBalance = loanBalancesDetails.getValue().get("PAYROL_DD_PAYADV")>
									<#assign courtBalance = loanBalancesDetails.getValue().get("PAYROL_DD_DEDRS02")>
									<#if parameters.OrganizationId == "MPF_HYD">
										<#assign SBHBalance = loanBalancesDetails.getValue().get("PAYROL_DD_1150_BL315")>
										<#assign canaraBalance = loanBalancesDetails.getValue().get("PAYROL_DD_DEDID18")>
										<#assign hblBalance = loanBalancesDetails.getValue().get("PAYROL_DD_DEDRS04")>
										<#assign otherBalance = loanBalancesDetails.getValue().get("PAYROL_DD_BL_317")>
									<#else>
										<#assign SBHBalance = loanBalancesDetails.getValue().get("PAYROL_DD_DEDRS11")>
										<#assign canaraBalance = loanBalancesDetails.getValue().get("PAYROL_DD_CANR_308")>
										<#assign hblBalance = loanBalancesDetails.getValue().get("PAYROL_DD_DEDID20")>
										<#assign otherBalance = loanBalancesDetails.getValue().get("PAYROL_DD_OTHERBANK")>
									</#if>
								</#if>
							</#list>
						</#if>
	            		<#if unitIdMap?has_content>
	            			<#assign unitList = unitIdMap.entrySet()>
	            			<#list unitList as unitIdsList>
	            				<#if unitIdsList.getKey()== partyId>
	            					<#assign unitCode=unitIdsList.getValue()>
	            				</#if>
	            			</#list>
	            		</#if>
	            		<#assign emplPayRate = 0>
	            		<#if payRateMap?has_content>
	            			<#assign payRateMapList = payRateMap.entrySet()>
	            			<#list payRateMapList as payRate>
	            				<#if payRate.getKey()== partyId>
	            					<#assign emplPayRate = payRate.getValue()>
	            				</#if>
	            			</#list>
	            		</#if>
	            		<#assign organizationDetails = delegator.findOne("PartyGroup", {"partyId" : parameters.OrganizationId}, true)>
	            		<#assign oragnizationId = organizationDetails.get("comments")>
		            	<#if listNo=1>
		            		<fo:block font-family="Courier,monospace">
			                	<fo:table> 
			                		<fo:table-column column-width="840pt"/>
			                		<fo:table-body> 
			                     		<fo:table-row >
			                     			<fo:table-cell >
			                     				<fo:block font-family="Courier,monospace">
			                     					<fo:table>
			                     						<fo:table-column column-width="40pt"/>
			                							<fo:table-column column-width="738pt"/>
			                							<fo:table-body> 
								                     		<fo:table-row >
								                     			<fo:table-cell >
								                     				<fo:block text-align="center" font-size="12pt" >&#160;</fo:block>
								                     			</fo:table-cell>
								                     			<fo:table-cell >
								                     				<fo:block font-family="Courier,monospace">
								                     					<fo:table>
								                     						<fo:table-column column-width="800pt"/>
								                     						<fo:table-body> 
												                     			<fo:table-row >
								                     								<fo:table-cell >
													                     				<fo:block font-family="Courier,monospace">
												                     						<fo:table> 
												                								<fo:table-column column-width="65pt"/>
												                								<fo:table-column column-width="80pt"/>
												                								<fo:table-column column-width="75pt"/>
												                								<fo:table-column column-width="60pt"/>
												                								<fo:table-column column-width="60pt"/>
												                								<fo:table-column column-width="65pt"/>
												                								<fo:table-column column-width="45pt"/>
												                								<fo:table-column column-width="153pt"/>
												                								<fo:table-column column-width="135pt"/>
												                								<fo:table-body> 
																	                     			<fo:table-row >
																	                     				<fo:table-cell >
																		                     				<fo:block text-align="center" font-size="12pt" font-weight="bold"><#if GISNo?has_content>${GISNo}<#else>&#160;</#if></fo:block>
																		                     				<fo:block linefeed-treatment="preserve" >&#xA;</fo:block>
																		                     			</fo:table-cell>
																		                     			<fo:table-cell >
																		                     				
																		                     				<fo:block text-align="center" font-size="12pt" font-weight="bold">&#160;</fo:block>
																		                     				<fo:block linefeed-treatment="preserve" >&#xA;</fo:block>
																		                     			</fo:table-cell>
																		                     			<fo:table-cell >
																		                     				<fo:block text-align="center" font-size="12pt" font-weight="bold"><#if emplPayRate!= 0>${emplPayRate?if_exists}<#else>&#160;</#if></fo:block>
																		                     				<fo:block linefeed-treatment="preserve" >&#xA;</fo:block>
																		                     			</fo:table-cell>
																		                     			<fo:table-cell >
																		                     				<fo:block text-align="center" font-size="12pt" font-weight="bold">${oragnizationId?if_exists}</fo:block>
																		                     				<fo:block linefeed-treatment="preserve" >&#xA;</fo:block>
																		                     			</fo:table-cell>
																		                     			<fo:table-cell >
																		                     				<fo:block text-align="center" font-size="12pt" font-weight="bold">${unitCode?if_exists}</fo:block>
																		                     				<fo:block linefeed-treatment="preserve" >&#xA;</fo:block>
																		                     			</fo:table-cell>
																		                     			<fo:table-cell >
																		                     				<fo:block text-align="center" font-size="12pt" font-weight="bold">${costCode?if_exists}</fo:block>
																		                     				<fo:block linefeed-treatment="preserve" >&#xA;</fo:block>
																		                     			</fo:table-cell>
																		                     			<#assign partyDetails = delegator.findOne("Party", {"partyId" : partyId}, true)>
																		                     			<fo:table-cell >
																		                     				<fo:block text-align="left" font-size="12pt" font-weight="bold"><#if partyDetails?has_content>${partyDetails.externalId?if_exists}<#else>${partyId?if_exists}</#if></fo:block>
																		                     				<fo:block linefeed-treatment="preserve" >&#xA;</fo:block>
																		                     			</fo:table-cell>
																		                     			<#assign personDetails = delegator.findOne("Person", {"partyId" : partyId}, true)>
																		                     			<fo:table-cell >
																		                     				<fo:block text-align="center" font-size="12pt" font-weight="bold"><#if personDetails?has_content>${(personDetails.nickname).toUpperCase()}<#else>${(Static["org.ofbiz.order.order.OrderServices"].nameTrim((Static["org.ofbiz.party.party.PartyHelper"].getPartyName(delegator, partyId, false)),15)).toUpperCase()}</#if></fo:block>
																		                     				<fo:block linefeed-treatment="preserve" >&#xA;</fo:block>
																		                     			</fo:table-cell>
																		                     			<fo:table-cell >
																		                     				<fo:block text-align="center" font-size="12pt" font-weight="bold" keep-together="always"><#if designationName?has_content>${Static["org.ofbiz.order.order.OrderServices"].nameTrim((designationName?if_exists),13)}<#else><#if designation?has_content>${Static["org.ofbiz.order.order.OrderServices"].nameTrim((designation.description?if_exists),13)}</#if></#if></fo:block>
																		                     				<fo:block linefeed-treatment="preserve" >&#xA;</fo:block>
																		                     			</fo:table-cell>
																	                     			</fo:table-row>
																	                           </fo:table-body>
																	                     	</fo:table>
																	                 	</fo:block>
																	                </fo:table-cell>
														                      	</fo:table-row>
														                      	<fo:table-row >
								                     								<fo:table-cell >
													                     				<fo:block font-family="Courier,monospace">
												                     						<fo:table> 
												                								<fo:table-column column-width="60pt"/>
												                								<fo:table-column column-width="60pt"/>
												                								<fo:table-column column-width="60pt"/>
												                								<fo:table-column column-width="60pt"/>
												                								<fo:table-column column-width="60pt"/>
												                								<fo:table-column column-width="60pt"/>
												                								<fo:table-column column-width="60pt"/>
												                								<fo:table-column column-width="60pt"/>
												                								<fo:table-column column-width="65pt"/>
												                								<fo:table-column column-width="60pt"/>
												                								<fo:table-column column-width="55pt"/>
												                								<fo:table-column column-width="78pt"/>
												                								<fo:table-body> 
																	            					<fo:table-row >
																		                     			<fo:table-cell >
																		                     				<fo:block linefeed-treatment="preserve" >&#xA;</fo:block>
																		                     				<fo:block text-align="center" font-size="12pt" font-weight="bold"><#if hblBalance!=0>${hblBalance?if_exists}<#else>&#160;</#if></fo:block>
																		                     			</fo:table-cell>
																		                     			<fo:table-cell >
																		                     				<fo:block linefeed-treatment="preserve" >&#xA;</fo:block>
																		                     				<fo:block text-align="center" font-size="12pt" font-weight="bold"><#if festAdvBalance!=0>${festAdvBalance?if_exists}<#else>&#160;</#if></fo:block>
																		                     			</fo:table-cell>
																		                     			<fo:table-cell >
																		                     				<fo:block linefeed-treatment="preserve" >&#xA;</fo:block>
																		                     				<fo:block text-align="center" font-size="12pt" font-weight="bold"><#if convBalance!= 0>${convBalance?if_exists}<#else>&#160;</#if></fo:block>
																		                     			</fo:table-cell>
																		                     			<fo:table-cell >
																		                     				<fo:block linefeed-treatment="preserve" >&#xA;</fo:block>
																		                     				<fo:block text-align="center" font-size="12pt" font-weight="bold"><#if mrgBalance!= 0>${mrgBalance?if_exists}<#else>&#160;</#if></fo:block>
																		                     			</fo:table-cell>
																		                     			<fo:table-cell >
																		                     				<fo:block linefeed-treatment="preserve" >&#xA;</fo:block>
																		                     				<fo:block text-align="center" font-size="12pt" font-weight="bold"><#if gpfBalance!= 0>${gpfBalance?if_exists}<#else>&#160;</#if></fo:block>
																		                     			</fo:table-cell>
																		                     			<fo:table-cell >
																		                     				<fo:block linefeed-treatment="preserve" >&#xA;</fo:block>
																		                     				<fo:block text-align="center" font-size="12pt" font-weight="bold"><#if eduBalance!= 0>${eduBalance?if_exists}<#else>&#160;</#if></fo:block>
																		                     			</fo:table-cell>
																		                     			<fo:table-cell >
																		                     				<fo:block linefeed-treatment="preserve" >&#xA;</fo:block>
																		                     				<fo:block text-align="center" font-size="12pt" font-weight="bold"><#if medicalBalance!= 0>${medicalBalance?if_exists}<#else>&#160;</#if></fo:block>
																		                     			</fo:table-cell>
																		                     			<fo:table-cell >
																		                     				<fo:block linefeed-treatment="preserve" >&#xA;</fo:block>
																		                     				<fo:block text-align="center" font-size="12pt" font-weight="bold"><#if payAdvBalance!= 0>${payAdvBalance?if_exists}<#else>&#160;</#if></fo:block>
																		                     			</fo:table-cell>
																		                     			<fo:table-cell >
																		                     				<#if SBHBalance!= 0>
																			                     				<fo:block text-align="center" font-size="12pt" font-weight="bold">SBH</fo:block>
																			                     				<fo:block text-align="center" font-size="12pt" font-weight="bold">${SBHBalance?if_exists}</fo:block>
																			                     			<#else>
																			                     				<fo:block linefeed-treatment="preserve" >&#xA;</fo:block>
																			                     				<fo:block text-align="center" font-size="12pt" font-weight="bold">&#160;</fo:block>
																			                     			</#if>
																		                     			</fo:table-cell>
																		                     			<fo:table-cell >
																		                     				<#if canaraBalance!= 0>
																			                     				<fo:block text-align="center" font-size="12pt" font-weight="bold">CANARA</fo:block>
																			                     				<fo:block text-align="center" font-size="12pt" font-weight="bold">${canaraBalance?if_exists}</fo:block>
																			                     			<#else>
																			                     				<fo:block linefeed-treatment="preserve" >&#xA;</fo:block>
																			                     				<fo:block text-align="center" font-size="12pt" font-weight="bold">&#160;</fo:block>
																			                     			</#if>
																		                     			</fo:table-cell>
																		                     			<fo:table-cell >
																		                     				<#if courtBalance!= 0>
																			                     				<fo:block text-align="center" font-size="12pt" font-weight="bold">COURT</fo:block>
																			                     				<fo:block text-align="center" font-size="12pt" font-weight="bold">${courtBalance?if_exists}</fo:block>
																			                     			<#else>
																			                     				<fo:block linefeed-treatment="preserve" >&#xA;</fo:block>
																			                     				<fo:block text-align="center" font-size="12pt" font-weight="bold">&#160;</fo:block>
																			                     			</#if>
																		                     			</fo:table-cell>
																		                     			<fo:table-cell >
																		                     				<#if otherBalance!= 0>
																			                     				<fo:block text-align="center" font-size="12pt" font-weight="bold">OTHER</fo:block>
																			                     				<fo:block text-align="center" font-size="12pt" font-weight="bold">${otherBalance?if_exists}</fo:block>
																			                     			<#else>
																			                     				<fo:block linefeed-treatment="preserve" >&#xA;</fo:block>
																			                     				<fo:block text-align="center" font-size="12pt" font-weight="bold">&#160;</fo:block>
																			                     			</#if>
																		                     			</fo:table-cell> 
																		                     		</fo:table-row>
																	                           </fo:table-body>
																	                     	</fo:table>
																	                 	</fo:block>
																	                </fo:table-cell>
														                      	</fo:table-row>
														                      	<fo:table-row >
								                     								<fo:table-cell >
													                     				<fo:block font-family="Courier,monospace">
												                     						<fo:table>
														                      					<fo:table-column column-width="60pt"/>
												                								<fo:table-column column-width="55pt"/>
												                								<fo:table-column column-width="55pt"/>
												                								<fo:table-column column-width="55pt"/>
												                								<fo:table-column column-width="55pt"/>
												                								<fo:table-column column-width="55pt"/>
												                								<fo:table-column column-width="55pt"/>
												                								<fo:table-column column-width="55pt"/>
												                								<fo:table-column column-width="55pt"/>
												                								<fo:table-column column-width="55pt"/>
												                								<fo:table-column column-width="55pt"/>
												                								<fo:table-column column-width="57pt"/>
												                								<fo:table-column column-width="71pt"/>
														                      					<fo:table-body> 
																	                     			<fo:table-row >
																	                     				<fo:table-cell >
																		                     				<fo:block linefeed-treatment="preserve" >&#xA;</fo:block>
																		                     				<fo:block text-align="center" font-size="12pt" font-weight="bold"><#if payRollHeader.getValue().get("PAYROL_BEN_SALARY")?has_content>${payRollHeader.getValue().get("PAYROL_BEN_SALARY")?if_exists}<#else>&#160;</#if></fo:block>
																		                     				<#if payRollHeader.getValue().get("PAYROL_BEN_SALARY")?has_content>
																		                     					<#assign totalEarnings=totalEarnings+payRollHeader.getValue().get("PAYROL_BEN_SALARY")>
																		                     				</#if>
																		                     			</fo:table-cell>
																		                     			<fo:table-cell >
																		                     				<fo:block linefeed-treatment="preserve" >&#xA;</fo:block>
																		                     				<fo:block text-align="center" font-size="12pt" font-weight="bold"><#if payRollHeader.getValue().get("PAYROL_BEN_PNLPAY")?has_content>${payRollHeader.getValue().get("PAYROL_BEN_PNLPAY")?if_exists}<#else>&#160;</#if></fo:block>
																		                     				<#if payRollHeader.getValue().get("PAYROL_BEN_PNLPAY")?has_content>
																		                     					<#assign totalEarnings=totalEarnings+payRollHeader.getValue().get("PAYROL_BEN_PNLPAY")>
																		                     				</#if>
																		                     			</fo:table-cell>
																		                     			<fo:table-cell >
																		                     				<fo:block linefeed-treatment="preserve" >&#xA;</fo:block>
																		                     				<fo:block text-align="center" font-size="12pt" font-weight="bold"><#if payRollHeader.getValue().get("PAYROL_BEN_SPLPAY")?has_content>${payRollHeader.getValue().get("PAYROL_BEN_SPLPAY")?if_exists}<#else>&#160;</#if></fo:block>
																		                     				<#if payRollHeader.getValue().get("PAYROL_BEN_SPLPAY")?has_content>
																		                     					<#assign totalEarnings=totalEarnings+payRollHeader.getValue().get("PAYROL_BEN_SPLPAY")>
																		                     				</#if>
																		                     			</fo:table-cell>
																		                     			<fo:table-cell >
																		                     				<fo:block linefeed-treatment="preserve" >&#xA;</fo:block>
																		                     				<fo:block text-align="center" font-size="12pt" font-weight="bold"><#if payRollHeader.getValue().get("PAYROL_BEN_DA")?has_content>${payRollHeader.getValue().get("PAYROL_BEN_DA")?if_exists}<#else>&#160;</#if></fo:block>
																		                     				<#if payRollHeader.getValue().get("PAYROL_BEN_DA")?has_content>
																		                     					<#assign totalEarnings=totalEarnings+payRollHeader.getValue().get("PAYROL_BEN_DA")>
																		                     				</#if>
																		                     			</fo:table-cell>
																		                     			<fo:table-cell >
																		                     				<fo:block linefeed-treatment="preserve" >&#xA;</fo:block>
																		                     				<fo:block text-align="center" font-size="12pt" font-weight="bold"><#if payRollHeader.getValue().get("PAYROL_BEN_HRA")?has_content>${payRollHeader.getValue().get("PAYROL_BEN_HRA")?if_exists}<#else>&#160;</#if></fo:block>
																		                     				<#if payRollHeader.getValue().get("PAYROL_BEN_HRA")?has_content>
																		                     					<#assign totalEarnings=totalEarnings+payRollHeader.getValue().get("PAYROL_BEN_HRA")>
																		                     				</#if>
																		                     			</fo:table-cell>
																		                     			<fo:table-cell >
																		                     				<fo:block linefeed-treatment="preserve" >&#xA;</fo:block>
																		                     				<fo:block text-align="center" font-size="12pt" font-weight="bold"><#if payRollHeader.getValue().get("PAYROL_BEN_CCA")?has_content>${payRollHeader.getValue().get("PAYROL_BEN_CCA")?if_exists}<#else>&#160;</#if></fo:block>
																		                     				<#if payRollHeader.getValue().get("PAYROL_BEN_CCA")?has_content>
																		                     					<#assign totalEarnings=totalEarnings+payRollHeader.getValue().get("PAYROL_BEN_CCA")>
																		                     				</#if>
																		                     			</fo:table-cell>
																		                     			<fo:table-cell >
																		                     				<fo:block linefeed-treatment="preserve" >&#xA;</fo:block>
																		                     				<fo:block text-align="center" font-size="12pt" font-weight="bold"><#if payRollHeader.getValue().get("PAYROL_BEN_IR")?has_content>${payRollHeader.getValue().get("PAYROL_BEN_IR")?if_exists}<#else>&#160;</#if></fo:block>
																		                     				<#if payRollHeader.getValue().get("PAYROL_BEN_IR")?has_content>
																		                     					<#assign totalEarnings=totalEarnings+payRollHeader.getValue().get("PAYROL_BEN_IR")>
																		                     				</#if>
																		                     			</fo:table-cell>
																		                     			<fo:table-cell >
																		                     				<fo:block linefeed-treatment="preserve" >&#xA;</fo:block>
																		                     				<fo:block text-align="center" font-size="12pt" font-weight="bold"><#if payRollHeader.getValue().get("PAYROL_BEN_SPLALW")?has_content>${payRollHeader.getValue().get("PAYROL_BEN_SPLALW")?if_exists}<#else>&#160;</#if></fo:block>
																		                     				<#if payRollHeader.getValue().get("PAYROL_BEN_SPLALW")?has_content>
																		                     					<#assign totalEarnings=totalEarnings+payRollHeader.getValue().get("PAYROL_BEN_SPLALW")>
																		                     				</#if>
																		                     			</fo:table-cell>
																		                     			<fo:table-cell >
																		                     				<fo:block linefeed-treatment="preserve" >&#xA;</fo:block>
																		                     				<fo:block text-align="center" font-size="12pt" font-weight="bold"><#if payRollHeader.getValue().get("PAYROL_BEN_TELALLO")?has_content>${payRollHeader.getValue().get("PAYROL_BEN_TELALLO")?if_exists}<#else>&#160;</#if></fo:block>
																		                     				<#if payRollHeader.getValue().get("PAYROL_BEN_TELALLO")?has_content>
																		                     					<#assign totalEarnings=totalEarnings+payRollHeader.getValue().get("PAYROL_BEN_TELALLO")>
																		                     				</#if>
																		                     			</fo:table-cell>
																		                     			<fo:table-cell >
																		                     				<fo:block linefeed-treatment="preserve" >&#xA;</fo:block>
																		                     				<fo:block text-align="center" font-size="12pt" font-weight="bold"><#if payRollHeader.getValue().get("PAYROL_BEN_NDALW")?has_content>${payRollHeader.getValue().get("PAYROL_BEN_NDALW")?if_exists}<#else>&#160;</#if></fo:block>
																		                     				<#if payRollHeader.getValue().get("PAYROL_BEN_NDALW")?has_content>
																		                     					<#assign totalEarnings=totalEarnings+payRollHeader.getValue().get("PAYROL_BEN_NDALW")>
																		                     				</#if>
																		                     			</fo:table-cell>
																		                     			<fo:table-cell >
																		                     				<fo:block linefeed-treatment="preserve" >&#xA;</fo:block>
																		                     				<fo:block text-align="center" font-size="12pt" font-weight="bold"><#if payRollHeader.getValue().get("PAYROL_BEN_WASHALW")?has_content>${payRollHeader.getValue().get("PAYROL_BEN_WASHALW")?if_exists}<#else>&#160;</#if></fo:block>
																		                     				<#if payRollHeader.getValue().get("PAYROL_BEN_WASHALW")?has_content>
																		                     					<#assign totalEarnings=totalEarnings+payRollHeader.getValue().get("PAYROL_BEN_WASHALW")>
																		                     				</#if>
																		                     			</fo:table-cell>
																		                     			<fo:table-cell >
																		                     				<fo:block linefeed-treatment="preserve" >&#xA;</fo:block>
																		                     				<fo:block text-align="center" font-size="12pt" font-weight="bold"><#if payRollHeader.getValue().get("PAYROL_BEN_MEDALW")?has_content>${payRollHeader.getValue().get("PAYROL_BEN_MEDALW")?if_exists}<#else>&#160;</#if></fo:block>
																		                     				<#if payRollHeader.getValue().get("PAYROL_BEN_MEDALW")?has_content>
																		                     					<#assign totalEarnings=totalEarnings+payRollHeader.getValue().get("PAYROL_BEN_MEDALW")>
																		                     				</#if>
																		                     			</fo:table-cell>
																		                     			<fo:table-cell >
																		                     				<fo:block linefeed-treatment="preserve" >&#xA;</fo:block>
																		                     				<fo:block text-align="center" font-size="12pt" font-weight="bold">${(Static["org.ofbiz.base.util.UtilDateTime"].toDateString(timePeriodEnd, "MM/yyyy")).toUpperCase()}</fo:block>
																		                     			</fo:table-cell>
																	                     			</fo:table-row>
																	                           </fo:table-body>
																	                     	</fo:table>
																	                 	</fo:block>
														                            </fo:table-cell>
														                      	</fo:table-row>
														                      	<fo:table-row >
								                     								<fo:table-cell >
													                     				<fo:block font-family="Courier,monospace">
												                     						<fo:table>
														                      					<fo:table-column column-width="60pt"/>
												                								<fo:table-column column-width="55pt"/>
												                								<fo:table-column column-width="55pt"/>
												                								<fo:table-column column-width="55pt"/>
												                								<fo:table-column column-width="55pt"/>
												                								<fo:table-column column-width="55pt"/>
												                								<fo:table-column column-width="55pt"/>
												                								<fo:table-column column-width="55pt"/>
												                								<fo:table-column column-width="55pt"/>
												                								<fo:table-column column-width="55pt"/>
												                								<fo:table-column column-width="55pt"/>
												                								<fo:table-column column-width="57pt"/>
												                								<fo:table-column column-width="71pt"/>
														                      					<fo:table-body> 
																	                     			<fo:table-row >
																	                     				<fo:table-cell >
																		                     				<fo:block linefeed-treatment="preserve" >&#xA;</fo:block>
																		                     				<fo:block linefeed-treatment="preserve" >&#xA;</fo:block>
																		                     				<fo:block text-align="center" font-size="12pt" font-weight="bold"><#if payRollHeader.getValue().get("PAYROL_BEN_OTALW")?has_content>${payRollHeader.getValue().get("PAYROL_BEN_OTALW")?if_exists}<#else>&#160;</#if></fo:block>
																		                     				<#if payRollHeader.getValue().get("PAYROL_BEN_OTALW")?has_content>
																		                     					<#assign totalEarnings=totalEarnings+payRollHeader.getValue().get("PAYROL_BEN_OTALW")>
																		                     				</#if>
																		                     			</fo:table-cell>
																		                     			<fo:table-cell >
																		                     				<fo:block linefeed-treatment="preserve" >&#xA;</fo:block>
																		                     				<fo:block linefeed-treatment="preserve" >&#xA;</fo:block>
																		                     				<fo:block text-align="center" font-size="12pt" font-weight="bold"><#if payRollHeader.getValue().get("PAYROL_BEN_HDALW")?has_content>${payRollHeader.getValue().get("PAYROL_BEN_HDALW")?if_exists}<#else>&#160;</#if></fo:block>
																		                     				<#if payRollHeader.getValue().get("PAYROL_BEN_HDALW")?has_content>
																		                     					<#assign totalEarnings=totalEarnings+payRollHeader.getValue().get("PAYROL_BEN_HDALW")>
																		                     				</#if>
																		                     			</fo:table-cell>
																		                     			<fo:table-cell >
																		                     				<fo:block linefeed-treatment="preserve" >&#xA;</fo:block>
																		                     				<fo:block linefeed-treatment="preserve" >&#xA;</fo:block>
																		                     				<fo:block text-align="center" font-size="12pt" font-weight="bold"><#if payRollHeader.getValue().get("PAYROL_BEN_OPALW")?has_content>${payRollHeader.getValue().get("PAYROL_BEN_OPALW")?if_exists}<#else>&#160;</#if></fo:block>
																		                     				<#if payRollHeader.getValue().get("PAYROL_BEN_OPALW")?has_content>
																		                     					<#assign totalEarnings=totalEarnings+payRollHeader.getValue().get("PAYROL_BEN_OPALW")>
																		                     				</#if>
																		                     			</fo:table-cell>
																		                     			<fo:table-cell >
																		                     				<fo:block linefeed-treatment="preserve" >&#xA;</fo:block>
																		                     				<fo:block linefeed-treatment="preserve" >&#xA;</fo:block>
																		                     				<fo:block text-align="center" font-size="12pt" font-weight="bold"><#if payRollHeader.getValue().get("PAYROL_BEN_ICALW")?has_content>${payRollHeader.getValue().get("PAYROL_BEN_ICALW")?if_exists}<#else>&#160;</#if></fo:block>
																		                     				<#if payRollHeader.getValue().get("PAYROL_BEN_ICALW")?has_content>
																		                     					<#assign totalEarnings=totalEarnings+payRollHeader.getValue().get("PAYROL_BEN_ICALW")>
																		                     				</#if>
																		                     			</fo:table-cell>
																		                     			<fo:table-cell >
																		                     				<fo:block linefeed-treatment="preserve" >&#xA;</fo:block>
																		                     				<fo:block linefeed-treatment="preserve" >&#xA;</fo:block>
																		                     				<fo:block text-align="center" font-size="12pt" font-weight="bold"><#if payRollHeader.getValue().get("PAYROL_BEN_RISKALW")?has_content>${payRollHeader.getValue().get("PAYROL_BEN_RISKALW")?if_exists}<#else>&#160;</#if></fo:block>
																		                     				<#if payRollHeader.getValue().get("PAYROL_BEN_RISKALW")?has_content>
																		                     					<#assign totalEarnings=totalEarnings+payRollHeader.getValue().get("PAYROL_BEN_RISKALW")>
																		                     				</#if>
																		                     			</fo:table-cell>
																		                     			<fo:table-cell >
																		                     				<fo:block linefeed-treatment="preserve" >&#xA;</fo:block>
																		                     				<fo:block linefeed-treatment="preserve" >&#xA;</fo:block>
																		                     				<fo:block text-align="center" font-size="12pt" font-weight="bold"><#if payRollHeader.getValue().get("PAYROL_BEN_SUPARRS")?has_content>${payRollHeader.getValue().get("PAYROL_BEN_SUPARRS")?if_exists}<#else>&#160;</#if></fo:block>
																		                     				<#if payRollHeader.getValue().get("PAYROL_BEN_SUPARRS")?has_content>
																		                     					<#assign totalEarnings=totalEarnings+payRollHeader.getValue().get("PAYROL_BEN_SUPARRS")>
																		                     				</#if>
																		                     			</fo:table-cell>
																		                     			<fo:table-cell >
																		                     				<fo:block linefeed-treatment="preserve" >&#xA;</fo:block>
																		                     				<fo:block linefeed-treatment="preserve" >&#xA;</fo:block>
																		                     				<fo:block text-align="center" font-size="12pt" font-weight="bold"><#if payRollHeader.getValue().get("PAYROL_BEN_MISCONE")?has_content>${payRollHeader.getValue().get("PAYROL_BEN_MISCONE")?if_exists}<#else>&#160;</#if></fo:block>
																		                     				<#if payRollHeader.getValue().get("PAYROL_BEN_MISCONE")?has_content>
																		                     					<#assign totalEarnings=totalEarnings+payRollHeader.getValue().get("PAYROL_BEN_MISCONE")>
																		                     				</#if>
																		                     			</fo:table-cell>
																		                     			<fo:table-cell >
																		                     				<fo:block linefeed-treatment="preserve" >&#xA;</fo:block>
																		                     				<fo:block linefeed-treatment="preserve" >&#xA;</fo:block>
																		                     				<fo:block text-align="center" font-size="12pt" font-weight="bold"><#if payRollHeader.getValue().get("PAYROL_BEN_MISCTWO")?has_content>${payRollHeader.getValue().get("PAYROL_BEN_MISCTWO")?if_exists}<#else>&#160;</#if></fo:block>
																		                     				<#if payRollHeader.getValue().get("PAYROL_BEN_MISCTWO")?has_content>
																		                     					<#assign totalEarnings=totalEarnings+payRollHeader.getValue().get("PAYROL_BEN_MISCTWO")>
																		                     				</#if>
																		                     			</fo:table-cell>
																		                     			<fo:table-cell >
																		                     				<fo:block linefeed-treatment="preserve" >&#xA;</fo:block>
																		                     				<fo:block linefeed-treatment="preserve" >&#xA;</fo:block>
																		                     				<fo:block text-align="center" font-size="12pt" font-weight="bold"><#if payRollHeader.getValue().get("PAYROL_BEN_MISCTHREE")?has_content>${payRollHeader.getValue().get("PAYROL_BEN_MISCTHREE")?if_exists}<#else>&#160;</#if></fo:block>
																		                     				<#if payRollHeader.getValue().get("PAYROL_BEN_MISCTHREE")?has_content>
																		                     					<#assign totalEarnings=totalEarnings+payRollHeader.getValue().get("PAYROL_BEN_MISCTHREE")>
																		                     				</#if>
																		                     			</fo:table-cell>
																		                     			<fo:table-cell >
																		                     				<fo:block linefeed-treatment="preserve" >&#xA;</fo:block>
																		                     				<fo:block linefeed-treatment="preserve" >&#xA;</fo:block>
																		                     				<fo:block text-align="center" font-size="12pt" font-weight="bold"><#if payRollHeader.getValue().get("PAYROL_BEN_CONALW")?has_content>${payRollHeader.getValue().get("PAYROL_BEN_CONALW")?if_exists}<#else>&#160;</#if></fo:block>
																		                     				<#if payRollHeader.getValue().get("PAYROL_BEN_CONALW")?has_content>
																		                     					<#assign totalEarnings=totalEarnings+payRollHeader.getValue().get("PAYROL_BEN_CONALW")>
																		                     				</#if>
																		                     			</fo:table-cell>
																		                     			<fo:table-cell >
																		                     				<fo:block linefeed-treatment="preserve" >&#xA;</fo:block>
																		                     				<fo:block linefeed-treatment="preserve" >&#xA;</fo:block>
																		                     				<fo:block text-align="center" font-size="12pt" font-weight="bold"><#if payRollHeader.getValue().get("PAYROL_BEN_CBALW")?has_content>${payRollHeader.getValue().get("PAYROL_BEN_CBALW")?if_exists}<#else>&#160;</#if></fo:block>
																		                     				<#if payRollHeader.getValue().get("PAYROL_BEN_CBALW")?has_content>
																		                     					<#assign totalEarnings=totalEarnings+payRollHeader.getValue().get("PAYROL_BEN_CBALW")>
																		                     				</#if>
																		                     			</fo:table-cell>
																		                     			<fo:table-cell >
																		                     				<fo:block linefeed-treatment="preserve" >&#xA;</fo:block>
																		                     				<fo:block linefeed-treatment="preserve" >&#xA;</fo:block>
																		                     				<fo:block text-align="center" font-size="12pt" font-weight="bold">&#160;</fo:block>
																		                     			</fo:table-cell>
																		                     			<fo:table-cell >
																		                     				<fo:block linefeed-treatment="preserve" >&#xA;</fo:block>
																		                     				<fo:block linefeed-treatment="preserve" >&#xA;</fo:block>
																		                     				<fo:block text-align="center" font-size="12pt" font-weight="bold">${(noOfPayableDays?if_exists)}  </fo:block>
																		                     			</fo:table-cell>
																	                     			</fo:table-row>
																	                           </fo:table-body>
																	                     	</fo:table>
																	                 	</fo:block>
														                            </fo:table-cell>
														                      	</fo:table-row>
														                      	<fo:table-row >
								                     								<fo:table-cell >
													                     				<fo:block font-family="Courier,monospace">
												                     						<fo:table>
														                      					<fo:table-column column-width="60pt"/>
												                								<fo:table-column column-width="55pt"/>
												                								<fo:table-column column-width="55pt"/>
												                								<fo:table-column column-width="55pt"/>
												                								<fo:table-column column-width="55pt"/>
												                								<fo:table-column column-width="55pt"/>
												                								<fo:table-column column-width="55pt"/>
												                								<fo:table-column column-width="55pt"/>
												                								<fo:table-column column-width="55pt"/>
												                								<fo:table-column column-width="55pt"/>
												                								<fo:table-column column-width="55pt"/>
												                								<fo:table-column column-width="57pt"/>
												                								<fo:table-column column-width="71pt"/>
														                      					<fo:table-body> 
																	                     			<fo:table-row >
																	                     				<fo:table-cell >
																		                     				<fo:block linefeed-treatment="preserve" >&#xA;</fo:block>
																		                     				<fo:block text-align="center" font-size="12pt" font-weight="bold"><#if payRollHeader.getValue().get("PAYROL_DD_EPF")?has_content>${(payRollHeader.getValue().get("PAYROL_DD_EPF")*(-1))?if_exists}<#else>&#160;</#if></fo:block>
																		                     				<#if payRollHeader.getValue().get("PAYROL_DD_EPF")?has_content>
																		                     					<#assign totalDeductions=totalDeductions+(payRollHeader.getValue().get("PAYROL_DD_EPF")*(-1))>
																		                     				</#if>
																		                     			</fo:table-cell>
																		                     			<fo:table-cell >
																		                     				<fo:block linefeed-treatment="preserve" >&#xA;</fo:block>
																		                     				<fo:block text-align="center" font-size="12pt" font-weight="bold"><#if payRollHeader.getValue().get("PAYROL_DD_APGLIF")?has_content>${(payRollHeader.getValue().get("PAYROL_DD_APGLIF")*(-1))?if_exists}<#else>&#160;</#if></fo:block>
																		                     				<#if payRollHeader.getValue().get("PAYROL_DD_APGLIF")?has_content>
																		                     					<#assign totalDeductions=totalDeductions+(payRollHeader.getValue().get("PAYROL_DD_APGLIF")*(-1))>
																		                     				</#if>
																		                     			</fo:table-cell>
																		                     			<fo:table-cell >
																		                     				<fo:block linefeed-treatment="preserve" >&#xA;</fo:block>
																		                     				<fo:block text-align="center" font-size="12pt" font-weight="bold"><#if payRollHeader.getValue().get("PAYROL_DD_VOLPF")?has_content>${(payRollHeader.getValue().get("PAYROL_DD_VOLPF")*(-1))?if_exists}<#else>&#160;</#if></fo:block>
																		                     				<#if payRollHeader.getValue().get("PAYROL_DD_VOLPF")?has_content>
																		                     					<#assign totalDeductions=totalDeductions+(payRollHeader.getValue().get("PAYROL_DD_VOLPF")*(-1))>
																		                     				</#if>
																		                     			</fo:table-cell>
																		                     			<fo:table-cell >
																		                     				<fo:block linefeed-treatment="preserve" >&#xA;</fo:block>
																		                     				<fo:block text-align="center" font-size="12pt" font-weight="bold"><#if payRollHeader.getValue().get("PAYROL_DD_GPFLN")?has_content>${(payRollHeader.getValue().get("PAYROL_DD_GPFLN")*(-1))?if_exists}<#else>&#160;</#if></fo:block>
																		                     				<#if payRollHeader.getValue().get("PAYROL_DD_GPFLN")?has_content>
																		                     					<#assign totalDeductions=totalDeductions+(payRollHeader.getValue().get("PAYROL_DD_GPFLN")*(-1))>
																		                     				</#if>
																		                     			</fo:table-cell>
																		                     			<fo:table-cell >
																		                     				<fo:block linefeed-treatment="preserve" >&#xA;</fo:block>
																		                     				<fo:block text-align="center" font-size="12pt" font-weight="bold"><#if payRollHeader.getValue().get("PAYROL_DD_DEDRS01")?has_content>${(payRollHeader.getValue().get("PAYROL_DD_DEDRS01")*(-1))?if_exists}<#else>&#160;</#if></fo:block>
																		                     				<#if payRollHeader.getValue().get("PAYROL_DD_DEDRS01")?has_content>
																		                     					<#assign totalDeductions=totalDeductions+(payRollHeader.getValue().get("PAYROL_DD_DEDRS01")*(-1))>
																		                     				</#if>
																		                     			</fo:table-cell>
																		                     			<fo:table-cell >
																		                     				<fo:block linefeed-treatment="preserve" >&#xA;</fo:block>
																		                     				<fo:block text-align="center" font-size="12pt" font-weight="bold"><#if payRollHeader.getValue().get("PAYROL_DD_EDNADV")?has_content>${(payRollHeader.getValue().get("PAYROL_DD_EDNADV")*(-1))?if_exists}<#else>&#160;</#if></fo:block>
																		                     				<#if payRollHeader.getValue().get("PAYROL_DD_EDNADV")?has_content>
																		                     					<#assign totalDeductions=totalDeductions+(payRollHeader.getValue().get("PAYROL_DD_EDNADV")*(-1))>
																		                     				</#if>
																		                     			</fo:table-cell>
																		                     			<fo:table-cell >
																		                     				<fo:block linefeed-treatment="preserve" >&#xA;</fo:block>
																		                     				<fo:block text-align="center" font-size="12pt" font-weight="bold"><#if payRollHeader.getValue().get("PAYROL_DD_SSS")?has_content>${(payRollHeader.getValue().get("PAYROL_DD_SSS")*(-1))?if_exists}<#else>&#160;</#if></fo:block>
																		                     				<#if payRollHeader.getValue().get("PAYROL_DD_SSS")?has_content>
																		                     					<#assign totalDeductions=totalDeductions+(payRollHeader.getValue().get("PAYROL_DD_SSS")*(-1))>
																		                     				</#if>
																		                     			</fo:table-cell>
																		                     			<fo:table-cell >
																		                     				<fo:block linefeed-treatment="preserve" >&#xA;</fo:block>
																		                     				<fo:block text-align="center" font-size="12pt" font-weight="bold"><#if payRollHeader.getValue().get("PAYROL_DD_IT")?has_content>${(payRollHeader.getValue().get("PAYROL_DD_IT")*(-1))?if_exists}<#else>&#160;</#if></fo:block>
																		                     				<#if payRollHeader.getValue().get("PAYROL_DD_IT")?has_content>
																		                     					<#assign totalDeductions=totalDeductions+(payRollHeader.getValue().get("PAYROL_DD_IT")*(-1))>
																		                     				</#if>
																		                     			</fo:table-cell>
																		                     			<fo:table-cell >
																		                     				<fo:block linefeed-treatment="preserve" >&#xA;</fo:block>
																		                     				<fo:block text-align="center" font-size="12pt" font-weight="bold"><#if payRollHeader.getValue().get("PAYROL_DD_HRR")?has_content>${(payRollHeader.getValue().get("PAYROL_DD_HRR")*(-1))?if_exists}<#else>&#160;</#if></fo:block>
																		                     				<#if payRollHeader.getValue().get("PAYROL_DD_HRR")?has_content>
																		                     					<#assign totalDeductions=totalDeductions+(payRollHeader.getValue().get("PAYROL_DD_HRR")*(-1))>
																		                     				</#if>
																		                     			</fo:table-cell>
																		                     			<fo:table-cell >
																		                     				<fo:block linefeed-treatment="preserve" >&#xA;</fo:block>
																		                     				<fo:block text-align="center" font-size="12pt" font-weight="bold"><#if payRollHeader.getValue().get("PAYROL_DD_ELECT")?has_content>${(payRollHeader.getValue().get("PAYROL_DD_ELECT")*(-1))?if_exists}<#else>&#160;</#if></fo:block>
																		                     				<#if payRollHeader.getValue().get("PAYROL_DD_ELECT")?has_content>
																		                     					<#assign totalDeductions=totalDeductions+(payRollHeader.getValue().get("PAYROL_DD_ELECT")*(-1))>
																		                     				</#if>
																		                     			</fo:table-cell>
																		                     			<fo:table-cell >
																		                     				<fo:block linefeed-treatment="preserve" >&#xA;</fo:block>
																		                     				<fo:block text-align="center" font-size="12pt" font-weight="bold"><#if payRollHeader.getValue().get("PAYROL_DD_WATER")?has_content>${(payRollHeader.getValue().get("PAYROL_DD_WATER")*(-1))?if_exists}<#else>&#160;</#if></fo:block>
																		                     				<#if payRollHeader.getValue().get("PAYROL_DD_WATER")?has_content>
																		                     					<#assign totalDeductions=totalDeductions+(payRollHeader.getValue().get("PAYROL_DD_WATER")*(-1))>
																		                     				</#if>
																		                     			</fo:table-cell>
																		                     			<fo:table-cell >
																		                     				<fo:block linefeed-treatment="preserve" >&#xA;</fo:block>
																		                     				<fo:block text-align="center" font-size="12pt" font-weight="bold"><#if payRollHeader.getValue().get("PAYROL_DD_WELFARE")?has_content>${(payRollHeader.getValue().get("PAYROL_DD_WELFARE")*(-1))?if_exists}<#else>&#160;</#if></fo:block>
																		                     				<#if payRollHeader.getValue().get("PAYROL_DD_WELFARE")?has_content>
																		                     					<#assign totalDeductions=totalDeductions+(payRollHeader.getValue().get("PAYROL_DD_WELFARE")*(-1))>
																		                     				</#if>
																		                     			</fo:table-cell>
																		                     			<fo:table-cell >
																		                     				<fo:block linefeed-treatment="preserve" >&#xA;</fo:block>
																		                     				<fo:block text-align="left" font-size="12pt" font-weight="bold">${totalEarnings?if_exists?string("#0")}</fo:block>
																		                     			</fo:table-cell>
																	                     			</fo:table-row>
																	                           </fo:table-body>
																	                     	</fo:table>
																	                 	</fo:block>
														                            </fo:table-cell>
														                      	</fo:table-row>
														                      	<fo:table-row >
								                     								<fo:table-cell >
													                     				<fo:block font-family="Courier,monospace">
												                     						<fo:table>
														                      					<fo:table-column column-width="60pt"/>
												                								<fo:table-column column-width="55pt"/>
												                								<fo:table-column column-width="55pt"/>
												                								<fo:table-column column-width="55pt"/>
												                								<fo:table-column column-width="55pt"/>
												                								<fo:table-column column-width="55pt"/>
												                								<fo:table-column column-width="55pt"/>
												                								<fo:table-column column-width="55pt"/>
												                								<fo:table-column column-width="55pt"/>
												                								<fo:table-column column-width="55pt"/>
												                								<fo:table-column column-width="55pt"/>
												                								<fo:table-column column-width="57pt"/>
												                								<fo:table-column column-width="71pt"/>
														                      					<fo:table-body> 
																	                     			<fo:table-row >
																	                     				<fo:table-cell >
																		                     				<fo:block linefeed-treatment="preserve" >&#xA;</fo:block>
																		                     				<fo:block linefeed-treatment="preserve" >&#xA;</fo:block>
																		                     				<fo:block text-align="center" font-size="12pt" font-weight="bold"><#if hdfcDedAmount != 0>${hdfcDedAmount?if_exists}<#else>&#160;</#if></fo:block>
																		                     				<#if hdfcDedAmount!=0>
																		                     					<#assign totalDeductions=totalDeductions+(hdfcDedAmount*(-1))>
																		                     				</#if>
																		                     			</fo:table-cell>
																		                     			<fo:table-cell >
																		                     				<fo:block linefeed-treatment="preserve" >&#xA;</fo:block>
																		                     				<fo:block linefeed-treatment="preserve" >&#xA;</fo:block>
																		                     				<fo:block text-align="center" font-size="12pt" font-weight="bold"><#if payRollHeader.getValue().get("PAYROL_DD_FESTADV")?has_content>${(payRollHeader.getValue().get("PAYROL_DD_FESTADV")*(-1))?if_exists}<#else>&#160;</#if></fo:block>
																		                     				<#if payRollHeader.getValue().get("PAYROL_DD_FESTADV")?has_content>
																		                     					<#assign totalDeductions=totalDeductions+(payRollHeader.getValue().get("PAYROL_DD_FESTADV")*(-1))>
																		                     				</#if>
																		                     			</fo:table-cell>
																		                     			<fo:table-cell >
																		                     				<fo:block linefeed-treatment="preserve" >&#xA;</fo:block>
																		                     				<fo:block linefeed-treatment="preserve" >&#xA;</fo:block>
																		                     				<fo:block text-align="center" font-size="12pt" font-weight="bold">&#160;</fo:block>
																		                     			</fo:table-cell>
																		                     			<fo:table-cell >
																		                     				<fo:block linefeed-treatment="preserve" >&#xA;</fo:block>
																		                     				<fo:block linefeed-treatment="preserve" >&#xA;</fo:block>
																		                     				<fo:block text-align="center" font-size="12pt" font-weight="bold"><#if payRollHeader.getValue().get("PAYROL_DD_MRGLN")?has_content>${(payRollHeader.getValue().get("PAYROL_DD_MRGLN")*(-1))?if_exists}<#else>&#160;</#if></fo:block>
																		                     				<#if payRollHeader.getValue().get("PAYROL_DD_MRGLN")?has_content>
																		                     					<#assign totalDeductions=totalDeductions+(payRollHeader.getValue().get("PAYROL_DD_MRGLN")*(-1))>
																		                     				</#if>
																		                     			</fo:table-cell>
																		                     			<fo:table-cell >
																		                     				<fo:block linefeed-treatment="preserve" >&#xA;</fo:block>
																		                     				<fo:block linefeed-treatment="preserve" >&#xA;</fo:block>
																		                     				<fo:block text-align="center" font-size="12pt" font-weight="bold"><#if payRollHeader.getValue().get("PAYROL_DD_PTAX")?has_content>${(payRollHeader.getValue().get("PAYROL_DD_PTAX")*(-1))?if_exists}<#else>&#160;</#if></fo:block>
																		                     				<#if payRollHeader.getValue().get("PAYROL_DD_PTAX")?has_content>
																		                     					<#assign totalDeductions=totalDeductions+(payRollHeader.getValue().get("PAYROL_DD_PTAX")*(-1))>
																		                     				</#if>
																		                     			</fo:table-cell>
																		                     			<fo:table-cell >
																		                     				<fo:block linefeed-treatment="preserve" >&#xA;</fo:block>
																		                     				<fo:block linefeed-treatment="preserve" >&#xA;</fo:block>
																		                     				<fo:block text-align="center" font-size="12pt" font-weight="bold"><#if payRollHeader.getValue().get("PAYROL_DD_MILKCARDS")?has_content>${(payRollHeader.getValue().get("PAYROL_DD_MILKCARDS")*(-1))?if_exists}<#else>&#160;</#if></fo:block>
																		                     				<#if payRollHeader.getValue().get("PAYROL_DD_MILKCARDS")?has_content>
																		                     					<#assign totalDeductions=totalDeductions+(payRollHeader.getValue().get("PAYROL_DD_MILKCARDS")*(-1))>
																		                     				</#if>
																		                     			</fo:table-cell>
																		                     			<fo:table-cell >
																		                     				<fo:block linefeed-treatment="preserve" >&#xA;</fo:block>
																		                     				<fo:block linefeed-treatment="preserve" >&#xA;</fo:block>
																		                     				<fo:block text-align="center" font-size="12pt" font-weight="bold"><#if payRollHeader.getValue().get("PAYROL_DD_MILKDUES")?has_content>${(payRollHeader.getValue().get("PAYROL_DD_MILKDUES")*(-1))?if_exists}<#else>&#160;</#if></fo:block>
																		                     				<#if payRollHeader.getValue().get("PAYROL_DD_MILKDUES")?has_content>
																		                     					<#assign totalDeductions=totalDeductions+(payRollHeader.getValue().get("PAYROL_DD_MILKDUES")*(-1))>
																		                     				</#if>
																		                     			</fo:table-cell>
																		                     			<fo:table-cell >
																		                     				<fo:block linefeed-treatment="preserve" >&#xA;</fo:block>
																		                     				<fo:block linefeed-treatment="preserve" >&#xA;</fo:block>
																		                     				<fo:block text-align="center" font-size="12pt" font-weight="bold"><#if payRollHeader.getValue().get("PAYROL_DD_MEDADV")?has_content>${(payRollHeader.getValue().get("PAYROL_DD_MEDADV")*(-1))?if_exists}<#else>&#160;</#if></fo:block>
																		                     				<#if payRollHeader.getValue().get("PAYROL_DD_MEDADV")?has_content>
																		                     					<#assign totalDeductions=totalDeductions+(payRollHeader.getValue().get("PAYROL_DD_MEDADV")*(-1))>
																		                     				</#if>
																		                     			</fo:table-cell>
																		                     			<fo:table-cell >
																		                     				<fo:block linefeed-treatment="preserve" >&#xA;</fo:block>
																		                     				<fo:block linefeed-treatment="preserve" >&#xA;</fo:block>
																		                     				<fo:block text-align="center" font-size="12pt" font-weight="bold"><#if payRollHeader.getValue().get("PAYROL_DD_DEDRS02")?has_content>${(payRollHeader.getValue().get("PAYROL_DD_DEDRS02")*(-1))?if_exists}<#else>&#160;</#if></fo:block>
																		                     				<#if payRollHeader.getValue().get("PAYROL_DD_DEDRS02")?has_content>
																		                     					<#assign totalDeductions=totalDeductions+(payRollHeader.getValue().get("PAYROL_DD_DEDRS02")*(-1))>
																		                     				</#if>
																		                     			</fo:table-cell>
																		                     			<fo:table-cell >
																		                     				<fo:block linefeed-treatment="preserve" >&#xA;</fo:block>
																		                     				<fo:block linefeed-treatment="preserve" >&#xA;</fo:block>
																		                     				<fo:block text-align="center" font-size="12pt" font-weight="bold"><#if payRollHeader.getValue().get("PAYROL_DD_ESI")?has_content>${(payRollHeader.getValue().get("PAYROL_DD_ESI")*(-1))?if_exists}<#else>&#160;</#if></fo:block>
																		                     				<#if payRollHeader.getValue().get("PAYROL_DD_ESI")?has_content>
																		                     					<#assign totalDeductions=totalDeductions+(payRollHeader.getValue().get("PAYROL_DD_ESI")*(-1))>
																		                     				</#if>
																		                     			</fo:table-cell>
																		                     			<fo:table-cell >
																		                     				<fo:block linefeed-treatment="preserve" >&#xA;</fo:block>
																		                     				<fo:block linefeed-treatment="preserve" >&#xA;</fo:block>
																		                     				<fo:block text-align="center" font-size="12pt" font-weight="bold"><#if payRollHeader.getValue().get("PAYROL_DD_GIS")?has_content>${(payRollHeader.getValue().get("PAYROL_DD_GIS")*(-1))?if_exists}<#else>&#160;</#if></fo:block>
																		                     				<#if payRollHeader.getValue().get("PAYROL_DD_GIS")?has_content>
																		                     					<#assign totalDeductions=totalDeductions+(payRollHeader.getValue().get("PAYROL_DD_GIS")*(-1))>
																		                     				</#if>
																		                     			</fo:table-cell>
																		                     			<fo:table-cell >
																		                     				<fo:block linefeed-treatment="preserve" >&#xA;</fo:block>
																		                     				<fo:block linefeed-treatment="preserve" >&#xA;</fo:block>
																		                     				<fo:block text-align="center" font-size="12pt" font-weight="bold"><#if payRollHeader.getValue().get("PAYROL_DD_DPTDUES")?has_content>${(payRollHeader.getValue().get("PAYROL_DD_DPTDUES")*(-1))?if_exists}<#else>&#160;</#if></fo:block>
																		                     				<#if payRollHeader.getValue().get("PAYROL_DD_DPTDUES")?has_content>
																		                     					<#assign totalDeductions=totalDeductions+(payRollHeader.getValue().get("PAYROL_DD_DPTDUES")*(-1))>
																		                     				</#if>
																		                     			</fo:table-cell>
																		                     			<fo:table-cell >
																		                     				<fo:block linefeed-treatment="preserve" >&#xA;</fo:block>
																		                     				<fo:block linefeed-treatment="preserve" >&#xA;</fo:block>
																		                     				<fo:block text-align="left" font-size="12pt" font-weight="bold">${totalDeductions?if_exists?string("#0.00")}</fo:block>
																		                     			</fo:table-cell>
																	                     			</fo:table-row>
																	                           </fo:table-body>
																	                     	</fo:table>
																	                 	</fo:block>
														                            </fo:table-cell>
														                      	</fo:table-row>
														                      	<fo:table-row >
								                     								<fo:table-cell >
													                     				<fo:block font-family="Courier,monospace">
												                     						<fo:table>
														                      					<fo:table-column column-width="60pt"/>
												                								<fo:table-column column-width="55pt"/>
												                								<fo:table-column column-width="55pt"/>
												                								<fo:table-column column-width="55pt"/>
												                								<fo:table-column column-width="55pt"/>
												                								<fo:table-column column-width="55pt"/>
												                								<fo:table-column column-width="55pt"/>
												                								<fo:table-column column-width="55pt"/>
												                								<fo:table-column column-width="55pt"/>
												                								<fo:table-column column-width="55pt"/>
												                								<fo:table-column column-width="55pt"/>
												                								<fo:table-column column-width="57pt"/>
												                								<fo:table-column column-width="71pt"/>
														                      					<fo:table-body> 
																	                     			<fo:table-row >
																	                     				<fo:table-cell >
																		                     				<fo:block linefeed-treatment="preserve" >&#xA;</fo:block>
																		                     				<fo:block text-align="center" font-size="12pt" font-weight="bold"><#if payRollHeader.getValue().get("PAYROL_DD_DEDRS10")?has_content>${(payRollHeader.getValue().get("PAYROL_DD_DEDRS10")*(-1))?if_exists}<#else>&#160;</#if></fo:block>
																		                     				<#if payRollHeader.getValue().get("PAYROL_DD_DEDRS10")?has_content>
																		                     					<#assign totalDeductions=totalDeductions+(payRollHeader.getValue().get("PAYROL_DD_DEDRS10")*(-1))>
																		                     				</#if>
																		                     				<fo:block linefeed-treatment="preserve" >&#xA;</fo:block>
																		                     			</fo:table-cell>
																		                     			<fo:table-cell >
																		                     				<#if SBHDedAmount != 0>
																			                     				<fo:block text-align="center" font-size="12pt" font-weight="bold">SBH</fo:block>
																			                     				<fo:block text-align="center" font-size="12pt" font-weight="bold">${SBHDedAmount?if_exists}</fo:block>
																			                     				<#if SBHDedAmount!=0>
																			                     					<#assign totalDeductions=totalDeductions+(SBHDedAmount*(-1))>
																			                     				</#if>
																		                     				<#else>
																			                     				<fo:block linefeed-treatment="preserve" >&#xA;</fo:block>
																		                     					<fo:block text-align="center" font-size="12pt" font-weight="bold">&#160;</fo:block>
																			                     			</#if>
																		                     				<fo:block linefeed-treatment="preserve" >&#xA;</fo:block>
																		                     			</fo:table-cell>
																		                     			<fo:table-cell >
																		                     				<#if canaraDedAmount != 0>
																			                     				<fo:block text-align="center" font-size="12pt" font-weight="bold">CANARA</fo:block>
																			                     				<fo:block text-align="center" font-size="12pt" font-weight="bold">${canaraDedAmount?if_exists}</fo:block>
																			                     				<#if canaraDedAmount!=0>
																			                     					<#assign totalDeductions=totalDeductions+(canaraDedAmount*(-1))>
																			                     				</#if>
																			                     			<#else>
																			                     				<fo:block linefeed-treatment="preserve" >&#xA;</fo:block>
																		                     					<fo:block text-align="center" font-size="12pt" font-weight="bold">&#160;</fo:block>
																			                     			</#if>
																		                     				<fo:block linefeed-treatment="preserve" >&#xA;</fo:block>
																		                     			</fo:table-cell>
																		                     			<fo:table-cell >
																		                     				<#if otherDedAmount != 0>
																			                     				<fo:block text-align="center" font-size="12pt" font-weight="bold">OTHER</fo:block>
																			                     				<fo:block text-align="center" font-size="12pt" font-weight="bold">${otherDedAmount?if_exists}</fo:block>
																			                     				<#if otherDedAmount!=0>
																			                     					<#assign totalDeductions=totalDeductions+(otherDedAmount*(-1))>
																			                     				</#if>
																			                     			<#else>
																			                     				<fo:block linefeed-treatment="preserve" >&#xA;</fo:block>
																		                     					<fo:block text-align="center" font-size="12pt" font-weight="bold">&#160;</fo:block>
																			                     			</#if>
																		                     				<fo:block text-align="center" font-size="12pt" font-weight="bold">&#160;</fo:block>
																		                     				<fo:block linefeed-treatment="preserve" >&#xA;</fo:block>
																		                     			</fo:table-cell>
																		                     			<fo:table-cell >
																		                     				<fo:block linefeed-treatment="preserve" >&#xA;</fo:block>
																		                     				<fo:block text-align="center" font-size="12pt" font-weight="bold">&#160;</fo:block>
																		                     				<fo:block linefeed-treatment="preserve" >&#xA;</fo:block>
																		                     			</fo:table-cell>
																		                     			<fo:table-cell >
																		                     				<fo:block linefeed-treatment="preserve" >&#xA;</fo:block>
																		                     				<fo:block text-align="center" font-size="12pt" font-weight="bold">&#160;</fo:block>
																		                     				<fo:block linefeed-treatment="preserve" >&#xA;</fo:block>
																		                     			</fo:table-cell>
																		                     			<fo:table-cell >
																		                     				<fo:block linefeed-treatment="preserve" >&#xA;</fo:block>
																		                     				<fo:block text-align="center" font-size="12pt" font-weight="bold">&#160;</fo:block>
																		                     				<fo:block linefeed-treatment="preserve" >&#xA;</fo:block>
																		                     			</fo:table-cell>
																		                     			<fo:table-cell >
																		                     				<fo:block linefeed-treatment="preserve" >&#xA;</fo:block>
																		                     				<fo:block text-align="center" font-size="12pt" font-weight="bold">&#160;</fo:block>
																		                     				<fo:block linefeed-treatment="preserve" >&#xA;</fo:block>
																		                     			</fo:table-cell>
																		                     			<fo:table-cell >
																		                     				<fo:block linefeed-treatment="preserve" >&#xA;</fo:block>
																		                     				<fo:block text-align="center" font-size="12pt" font-weight="bold">&#160;</fo:block>
																		                     				<fo:block linefeed-treatment="preserve" >&#xA;</fo:block>
																		                     			</fo:table-cell>
																		                     			<fo:table-cell >
																		                     				<fo:block linefeed-treatment="preserve" >&#xA;</fo:block>
																		                     				<fo:block text-align="center" font-size="12pt" font-weight="bold">&#160;</fo:block>
																		                     				<fo:block linefeed-treatment="preserve" >&#xA;</fo:block>
																		                     			</fo:table-cell>
																		                     			<fo:table-cell >
																		                     				<fo:block linefeed-treatment="preserve" >&#xA;</fo:block>
																		                     				<fo:block text-align="center" font-size="12pt" font-weight="bold">&#160;</fo:block>
																		                     				<fo:block linefeed-treatment="preserve" >&#xA;</fo:block>
																		                     			</fo:table-cell>
																		                     			<fo:table-cell >
																		                     				<fo:block linefeed-treatment="preserve" >&#xA;</fo:block>
																		                     				<fo:block text-align="center" font-size="12pt" font-weight="bold">${(totalEarnings-totalDeductions)?if_exists?string("#0")}</fo:block>
																		                     				<fo:block linefeed-treatment="preserve" >&#xA;</fo:block>
																		                     			</fo:table-cell>
																		                     			<fo:table-cell >
																		                     				<fo:block linefeed-treatment="preserve" >&#xA;</fo:block>
																		                     				<fo:block text-align="left" font-size="12pt" font-weight="bold">${(totalEarnings-totalDeductions)?if_exists?string("#0.00")}</fo:block>
																		                     				<fo:block linefeed-treatment="preserve" >&#xA;</fo:block>
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
									                      	</fo:table-row>
							                           </fo:table-body>
							                     	</fo:table>
							                 	</fo:block>
				                            </fo:table-cell>
				                      	</fo:table-row>
				                      	<fo:table-row>
					                      	<fo:table-cell >
			                     				<fo:block linefeed-treatment="preserve">&#xA;</fo:block>
			                     			</fo:table-cell>
		                     			</fo:table-row>
		                     			<fo:table-row>
					                      	<fo:table-cell >
			                     				<fo:block linefeed-treatment="preserve">&#xA;</fo:block>
			                     			</fo:table-cell>
		                     			</fo:table-row>
		                           	</fo:table-body>
		                     	</fo:table>
	                 		</fo:block>
	                 	</#if>
	                 	
	                 	
	                 	<#if listNo=2>
			            	<#assign payHeader = delegator.findOne("PayrollHeader", {"payrollHeaderId" : payRollHeader.getKey()}, true)>
	      	 				<#assign partyId = payHeader.partyIdFrom>
		            		<#assign emplPositionAndFulfilment=delegator.findByAnd("EmplPositionAndFulfillment", {"employeePartyId" : partyId})/>
		            		<#assign designation = delegator.findOne("EmplPositionType", {"emplPositionTypeId" : emplPositionAndFulfilment[0].emplPositionTypeId?if_exists}, true)>
	                     	<#assign designationName=emplPositionAndFulfilment[0].name?if_exists>
		            		<#assign emplLeavesDetails = delegator.findOne("PayrollAttendance", {"partyId" : partyId, "customTimePeriodId": timePeriod?if_exists}, true)/>
		            		<#assign totalEarnings=0>
		            		<#assign totalDeductions=0>
	          				<#if emplLeavesDetails?has_content>
		            			<#assign noOfPayableDays=emplLeavesDetails.get("noOfPayableDays")?if_exists>
		            		<#else>
		            			<#assign noOfPayableDays=0>
		            		</#if>
	          				
		            		<fo:block font-family="Courier,monospace">
			            	
			                	<fo:table> 
			                		<fo:table-column column-width="840pt"/>
			                		<fo:table-body> 
			                     		<fo:table-row >
			                     			<fo:table-cell >
			                     				<fo:block font-family="Courier,monospace">
			                     					<fo:table>
			                     						<fo:table-column column-width="40pt"/>
			                							<fo:table-column column-width="738pt"/>
			                							<fo:table-body> 
								                     		<fo:table-row >
								                     			<fo:table-cell >
								                     				<fo:block text-align="center" font-size="12pt" >&#160;</fo:block>
								                     			</fo:table-cell>
								                     			<fo:table-cell >
								                     				<fo:block font-family="Courier,monospace">
								                     					<fo:table>
								                     						<fo:table-column column-width="800pt"/>
								                     						<fo:table-body> 
												                     			<fo:table-row >
								                     								<fo:table-cell >
													                     				<fo:block font-family="Courier,monospace">
												                     						<fo:table> 
												                								<fo:table-column column-width="65pt"/>
												                								<fo:table-column column-width="80pt"/>
												                								<fo:table-column column-width="75pt"/>
												                								<fo:table-column column-width="60pt"/>
												                								<fo:table-column column-width="60pt"/>
												                								<fo:table-column column-width="65pt"/>
												                								<fo:table-column column-width="45pt"/>
												                								<fo:table-column column-width="153pt"/>
												                								<fo:table-column column-width="135pt"/>
												                								<fo:table-body> 
																	                     			<fo:table-row >
																	                     				<fo:table-cell >
																	                     					<fo:block linefeed-treatment="preserve" >&#xA;</fo:block>
																		                     				<fo:block text-align="center" font-size="12pt" font-weight="bold"><#if GISNo?has_content>${GISNo}<#else>&#160;</#if></fo:block>
																		                     			</fo:table-cell>
																		                     			<fo:table-cell >
																		                     				<fo:block linefeed-treatment="preserve" >&#xA;</fo:block>
																		                     				<fo:block text-align="center" font-size="12pt" font-weight="bold">&#160;</fo:block>
																		                     			</fo:table-cell>
																		                     			<fo:table-cell >
																		                     				<fo:block linefeed-treatment="preserve" >&#xA;</fo:block>
																		                     				<fo:block text-align="center" font-size="12pt" font-weight="bold"><#if emplPayRate!= 0>${emplPayRate?if_exists}<#else>&#160;</#if></fo:block>
																		                     			</fo:table-cell>
																		                     			<fo:table-cell >
																		                     				<fo:block linefeed-treatment="preserve" >&#xA;</fo:block>
																		                     				<fo:block text-align="center" font-size="12pt" font-weight="bold">${oragnizationId?if_exists}</fo:block>
																		                     			</fo:table-cell>
																		                     			<fo:table-cell >
																		                     				<fo:block linefeed-treatment="preserve" >&#xA;</fo:block>
																		                     				<fo:block text-align="center" font-size="12pt" font-weight="bold">${unitCode?if_exists}</fo:block>
																		                     			</fo:table-cell>
																		                     			<fo:table-cell >
																		                     				<fo:block linefeed-treatment="preserve" >&#xA;</fo:block>
																		                     				<fo:block text-align="center" font-size="12pt" font-weight="bold">${costCode?if_exists}</fo:block>
																		                     			</fo:table-cell>
																		                     			<#assign partyDetails = delegator.findOne("Party", {"partyId" : partyId}, true)>
																		                     			<fo:table-cell >
																		                     				<fo:block linefeed-treatment="preserve" >&#xA;</fo:block>
																		                     				<fo:block text-align="left" font-size="12pt" font-weight="bold"><#if partyDetails?has_content>${partyDetails.externalId?if_exists}<#else>${partyId?if_exists}</#if></fo:block>
																		                     			</fo:table-cell>
																		                     			<#assign personDetails = delegator.findOne("Person", {"partyId" : partyId}, true)>											                     			
																		                     			<fo:table-cell >
																		                     				<fo:block linefeed-treatment="preserve" >&#xA;</fo:block>
																		                     				<fo:block text-align="center" font-size="12pt" font-weight="bold"><#if personDetails?has_content>${(personDetails.nickname).toUpperCase()}<#else>${(Static["org.ofbiz.order.order.OrderServices"].nameTrim((Static["org.ofbiz.party.party.PartyHelper"].getPartyName(delegator, partyId, false)),15)).toUpperCase()}</#if></fo:block>
																		                     			</fo:table-cell>
																		                     			<fo:table-cell >
																		                     				<fo:block linefeed-treatment="preserve" >&#xA;</fo:block>
																		                     				<fo:block text-align="center" font-size="12pt" font-weight="bold"><#if designationName?has_content>${Static["org.ofbiz.order.order.OrderServices"].nameTrim((designationName?if_exists),13)}<#else><#if designation?has_content>${Static["org.ofbiz.order.order.OrderServices"].nameTrim((designation.description?if_exists),13)}</#if></#if></fo:block>
																		                     			</fo:table-cell>
																	                     			</fo:table-row>
																	                           </fo:table-body>
																	                     	</fo:table>
																	                 	</fo:block>
																	                </fo:table-cell>
														                      	</fo:table-row>
														                      	<fo:table-row >
								                     								<fo:table-cell >
													                     				<fo:block font-family="Courier,monospace">
												                     						<fo:table> 
												                								<fo:table-column column-width="60pt"/>
												                								<fo:table-column column-width="60pt"/>
												                								<fo:table-column column-width="60pt"/>
												                								<fo:table-column column-width="60pt"/>
												                								<fo:table-column column-width="60pt"/>
												                								<fo:table-column column-width="60pt"/>
												                								<fo:table-column column-width="60pt"/>
												                								<fo:table-column column-width="60pt"/>
												                								<fo:table-column column-width="65pt"/>
												                								<fo:table-column column-width="60pt"/>
												                								<fo:table-column column-width="55pt"/>
												                								<fo:table-column column-width="78pt"/>
												                								<fo:table-body>
																	                     			<fo:table-row >
																	                     				<fo:table-cell >
																		                     				<fo:block linefeed-treatment="preserve" >&#xA;</fo:block>
																		                     				<fo:block linefeed-treatment="preserve" >&#xA;</fo:block>
																		                     				<fo:block text-align="center" font-size="12pt" font-weight="bold"><#if hblBalance!= 0>${hblBalance?if_exists}<#else>&#160;</#if></fo:block>
																		                     			</fo:table-cell>
																		                     			<fo:table-cell >
																		                     				<fo:block linefeed-treatment="preserve" >&#xA;</fo:block>
																		                     				<fo:block linefeed-treatment="preserve" >&#xA;</fo:block>
																		                     				<fo:block text-align="center" font-size="12pt" font-weight="bold"><#if festAdvBalance!= 0>${festAdvBalance?if_exists}<#else>&#160;</#if></fo:block>
																		                     			</fo:table-cell>
																		                     			<fo:table-cell >
																		                     				<fo:block linefeed-treatment="preserve" >&#xA;</fo:block>
																		                     				<fo:block linefeed-treatment="preserve" >&#xA;</fo:block>
																		                     				<fo:block text-align="center" font-size="12pt" font-weight="bold"><#if convBalance!= 0>${convBalance?if_exists}<#else>&#160;</#if></fo:block>
																		                     			</fo:table-cell>
																		                     			<fo:table-cell >
																		                     				<fo:block linefeed-treatment="preserve" >&#xA;</fo:block>
																		                     				<fo:block linefeed-treatment="preserve" >&#xA;</fo:block>
																		                     				<fo:block text-align="center" font-size="12pt" font-weight="bold"><#if mrgBalance!= 0>${mrgBalance?if_exists}<#else>&#160;</#if></fo:block>
																		                     			</fo:table-cell>
																		                     			<fo:table-cell >
																		                     				<fo:block linefeed-treatment="preserve" >&#xA;</fo:block>
																		                     				<fo:block linefeed-treatment="preserve" >&#xA;</fo:block>
																		                     				<fo:block text-align="center" font-size="12pt" font-weight="bold"><#if gpfBalance!= 0>${gpfBalance?if_exists}<#else>&#160;</#if></fo:block>
																		                     			</fo:table-cell>
																		                     			<fo:table-cell >
																		                     				<fo:block linefeed-treatment="preserve" >&#xA;</fo:block>
																		                     				<fo:block linefeed-treatment="preserve" >&#xA;</fo:block>
																		                     				<fo:block text-align="center" font-size="12pt" font-weight="bold"><#if eduBalance!= 0>${eduBalance?if_exists}<#else>&#160;</#if></fo:block>
																		                     			</fo:table-cell>
																		                     			<fo:table-cell >
																		                     				<fo:block linefeed-treatment="preserve" >&#xA;</fo:block>
																		                     				<fo:block linefeed-treatment="preserve" >&#xA;</fo:block>
																		                     				<fo:block text-align="center" font-size="12pt" font-weight="bold"><#if medicalBalance!= 0>${medicalBalance?if_exists}<#else>&#160;</#if></fo:block>
																		                     			</fo:table-cell>
																		                     			<fo:table-cell >
																		                     				<fo:block linefeed-treatment="preserve" >&#xA;</fo:block>
																		                     				<fo:block linefeed-treatment="preserve" >&#xA;</fo:block>
																		                     				<fo:block text-align="center" font-size="12pt" font-weight="bold"><#if payAdvBalance!= 0>${payAdvBalance?if_exists}<#else>&#160;</#if></fo:block>
																		                     			</fo:table-cell>
																		                     			<fo:table-cell >
																		                     				<fo:block linefeed-treatment="preserve" >&#xA;</fo:block>
																		                     				<#if SBHBalance!= 0>
																			                     				<fo:block text-align="center" font-size="12pt" font-weight="bold">SBH</fo:block>
																			                     				<fo:block text-align="center" font-size="12pt" font-weight="bold">${SBHBalance?if_exists}</fo:block>
																		                     				<#else>
																			                     				<fo:block linefeed-treatment="preserve" >&#xA;</fo:block>
																			                     				<fo:block text-align="center" font-size="12pt" font-weight="bold">&#160;</fo:block>
																			                     			</#if>
																		                     			</fo:table-cell>
																		                     			<fo:table-cell >
																		                     				<fo:block linefeed-treatment="preserve" >&#xA;</fo:block>
																		                     				<#if canaraBalance!= 0>
																			                     				<fo:block text-align="center" font-size="12pt" font-weight="bold">CANARA</fo:block>
																			                     				<fo:block text-align="center" font-size="12pt" font-weight="bold">${canaraBalance?if_exists}</fo:block>
																		                     				<#else>
																			                     				<fo:block linefeed-treatment="preserve" >&#xA;</fo:block>
																			                     				<fo:block text-align="center" font-size="12pt" font-weight="bold">&#160;</fo:block>
																			                     			</#if>
																		                     			</fo:table-cell>
																		                     			<fo:table-cell >
																		                     				<fo:block linefeed-treatment="preserve" >&#xA;</fo:block>
																		                     				<#if courtBalance!= 0>
																			                     				<fo:block text-align="center" font-size="12pt" font-weight="bold">COURT</fo:block>
																			                     				<fo:block text-align="center" font-size="12pt" font-weight="bold">${courtBalance?if_exists}</fo:block>
																		                     				<#else>
																			                     				<fo:block linefeed-treatment="preserve" >&#xA;</fo:block>
																			                     				<fo:block text-align="center" font-size="12pt" font-weight="bold">&#160;</fo:block>
																			                     			</#if>
																		                     			</fo:table-cell>
																		                     			<fo:table-cell >
																		                     				<fo:block linefeed-treatment="preserve" >&#xA;</fo:block>
																		                     				<#if otherBalance!= 0>
																			                     				<fo:block text-align="center" font-size="12pt" font-weight="bold">OTHER</fo:block>
																			                     				<fo:block text-align="center" font-size="12pt" font-weight="bold">${otherBalance?if_exists}</fo:block>
																			                     			<#else>
																			                     				<fo:block linefeed-treatment="preserve" >&#xA;</fo:block>
																			                     				<fo:block text-align="center" font-size="12pt" font-weight="bold">&#160;</fo:block>
																			                     			</#if>
																		                     			</fo:table-cell> 
																	                     			</fo:table-row> 
																	                           </fo:table-body>
																	                     	</fo:table>
																	                 	</fo:block>
																	                </fo:table-cell>
														                      	</fo:table-row>
														                      	<fo:table-row >
								                     								<fo:table-cell >
													                     				<fo:block font-family="Courier,monospace">
												                     						<fo:table>
														                      					<fo:table-column column-width="60pt"/>
												                								<fo:table-column column-width="55pt"/>
												                								<fo:table-column column-width="55pt"/>
												                								<fo:table-column column-width="55pt"/>
												                								<fo:table-column column-width="55pt"/>
												                								<fo:table-column column-width="55pt"/>
												                								<fo:table-column column-width="55pt"/>
												                								<fo:table-column column-width="55pt"/>
												                								<fo:table-column column-width="55pt"/>
												                								<fo:table-column column-width="55pt"/>
												                								<fo:table-column column-width="55pt"/>
												                								<fo:table-column column-width="57pt"/>
												                								<fo:table-column column-width="71pt"/>
														                      					<fo:table-body> 
																	                     			<fo:table-row >
																	                     				<fo:table-cell >
																		                     				<fo:block linefeed-treatment="preserve" >&#xA;</fo:block>
																		                     				<fo:block text-align="center" font-size="12pt" font-weight="bold"><#if payRollHeader.getValue().get("PAYROL_BEN_SALARY")?has_content>${payRollHeader.getValue().get("PAYROL_BEN_SALARY")?if_exists}<#else>&#160;</#if></fo:block>
																		                     				<#if payRollHeader.getValue().get("PAYROL_BEN_SALARY")?has_content>
																		                     					<#assign totalEarnings=totalEarnings+payRollHeader.getValue().get("PAYROL_BEN_SALARY")>
																		                     				</#if>
																		                     			</fo:table-cell>
																		                     			<fo:table-cell >
																		                     				<fo:block linefeed-treatment="preserve" >&#xA;</fo:block>
																		                     				<fo:block text-align="center" font-size="12pt" font-weight="bold"><#if payRollHeader.getValue().get("PAYROL_BEN_PNLPAY")?has_content>${payRollHeader.getValue().get("PAYROL_BEN_PNLPAY")?if_exists}<#else>&#160;</#if></fo:block>
																		                     				<#if payRollHeader.getValue().get("PAYROL_BEN_PNLPAY")?has_content>
																		                     					<#assign totalEarnings=totalEarnings+payRollHeader.getValue().get("PAYROL_BEN_PNLPAY")>
																		                     				</#if>
																		                     			</fo:table-cell>
																		                     			<fo:table-cell >
																		                     				<fo:block linefeed-treatment="preserve" >&#xA;</fo:block>
																		                     				<fo:block text-align="center" font-size="12pt" font-weight="bold"><#if payRollHeader.getValue().get("PAYROL_BEN_SPLPAY")?has_content>${payRollHeader.getValue().get("PAYROL_BEN_SPLPAY")?if_exists}<#else>&#160;</#if></fo:block>
																		                     				<#if payRollHeader.getValue().get("PAYROL_BEN_SPLPAY")?has_content>
																		                     					<#assign totalEarnings=totalEarnings+payRollHeader.getValue().get("PAYROL_BEN_SPLPAY")>
																		                     				</#if>
																		                     			</fo:table-cell>
																		                     			<fo:table-cell >
																		                     				<fo:block linefeed-treatment="preserve" >&#xA;</fo:block>
																		                     				<fo:block text-align="center" font-size="12pt" font-weight="bold"><#if payRollHeader.getValue().get("PAYROL_BEN_DA")?has_content>${payRollHeader.getValue().get("PAYROL_BEN_DA")?if_exists}<#else>&#160;</#if></fo:block>
																		                     				<#if payRollHeader.getValue().get("PAYROL_BEN_DA")?has_content>
																		                     					<#assign totalEarnings=totalEarnings+payRollHeader.getValue().get("PAYROL_BEN_DA")>
																		                     				</#if>
																		                     			</fo:table-cell>
																		                     			<fo:table-cell >
																		                     				<fo:block linefeed-treatment="preserve" >&#xA;</fo:block>
																		                     				<fo:block text-align="center" font-size="12pt" font-weight="bold"><#if payRollHeader.getValue().get("PAYROL_BEN_HRA")?has_content>${payRollHeader.getValue().get("PAYROL_BEN_HRA")?if_exists}<#else>&#160;</#if></fo:block>
																		                     				<#if payRollHeader.getValue().get("PAYROL_BEN_HRA")?has_content>
																		                     					<#assign totalEarnings=totalEarnings+payRollHeader.getValue().get("PAYROL_BEN_HRA")>
																		                     				</#if>
																		                     			</fo:table-cell>
																		                     			<fo:table-cell >
																		                     				<fo:block linefeed-treatment="preserve" >&#xA;</fo:block>
																		                     				<fo:block text-align="center" font-size="12pt" font-weight="bold"><#if payRollHeader.getValue().get("PAYROL_BEN_CCA")?has_content>${payRollHeader.getValue().get("PAYROL_BEN_CCA")?if_exists}<#else>&#160;</#if></fo:block>
																		                     				<#if payRollHeader.getValue().get("PAYROL_BEN_CCA")?has_content>
																		                     					<#assign totalEarnings=totalEarnings+payRollHeader.getValue().get("PAYROL_BEN_CCA")>
																		                     				</#if>
																		                     			</fo:table-cell>
																		                     			<fo:table-cell >
																		                     				<fo:block linefeed-treatment="preserve" >&#xA;</fo:block>
																		                     				<fo:block text-align="center" font-size="12pt" font-weight="bold"><#if payRollHeader.getValue().get("PAYROL_BEN_IR")?has_content>${payRollHeader.getValue().get("PAYROL_BEN_IR")?if_exists}<#else>&#160;</#if></fo:block>
																		                     				<#if payRollHeader.getValue().get("PAYROL_BEN_IR")?has_content>
																		                     					<#assign totalEarnings=totalEarnings+payRollHeader.getValue().get("PAYROL_BEN_IR")>
																		                     				</#if>
																		                     			</fo:table-cell>
																		                     			<fo:table-cell >
																		                     				<fo:block linefeed-treatment="preserve" >&#xA;</fo:block>
																		                     				<fo:block text-align="center" font-size="12pt" font-weight="bold"><#if payRollHeader.getValue().get("PAYROL_BEN_SPLALW")?has_content>${payRollHeader.getValue().get("PAYROL_BEN_SPLALW")?if_exists}<#else>&#160;</#if></fo:block>
																		                     				<#if payRollHeader.getValue().get("PAYROL_BEN_SPLALW")?has_content>
																		                     					<#assign totalEarnings=totalEarnings+payRollHeader.getValue().get("PAYROL_BEN_SPLALW")>
																		                     				</#if>
																		                     			</fo:table-cell>
																		                     			<fo:table-cell >
																		                     				<fo:block linefeed-treatment="preserve" >&#xA;</fo:block>
																		                     				<fo:block text-align="center" font-size="12pt" font-weight="bold"><#if payRollHeader.getValue().get("PAYROL_BEN_TELALLO")?has_content>${payRollHeader.getValue().get("PAYROL_BEN_TELALLO")?if_exists}<#else>&#160;</#if></fo:block>
																		                     				<#if payRollHeader.getValue().get("PAYROL_BEN_TELALLO")?has_content>
																		                     					<#assign totalEarnings=totalEarnings+payRollHeader.getValue().get("PAYROL_BEN_TELALLO")>
																		                     				</#if>
																		                     			</fo:table-cell>
																		                     			<fo:table-cell >
																		                     				<fo:block linefeed-treatment="preserve" >&#xA;</fo:block>
																		                     				<fo:block text-align="center" font-size="12pt" font-weight="bold"><#if payRollHeader.getValue().get("PAYROL_BEN_NDALW")?has_content>${payRollHeader.getValue().get("PAYROL_BEN_NDALW")?if_exists}<#else>&#160;</#if></fo:block>
																		                     				<#if payRollHeader.getValue().get("PAYROL_BEN_NDALW")?has_content>
																		                     					<#assign totalEarnings=totalEarnings+payRollHeader.getValue().get("PAYROL_BEN_NDALW")>
																		                     				</#if>
																		                     			</fo:table-cell>
																		                     			<fo:table-cell >
																		                     				<fo:block linefeed-treatment="preserve" >&#xA;</fo:block>
																		                     				<fo:block text-align="center" font-size="12pt" font-weight="bold"><#if payRollHeader.getValue().get("PAYROL_BEN_WASHALW")?has_content>${payRollHeader.getValue().get("PAYROL_BEN_WASHALW")?if_exists}<#else>&#160;</#if></fo:block>
																		                     				<#if payRollHeader.getValue().get("PAYROL_BEN_WASHALW")?has_content>
																		                     					<#assign totalEarnings=totalEarnings+payRollHeader.getValue().get("PAYROL_BEN_WASHALW")>
																		                     				</#if>
																		                     			</fo:table-cell>
																		                     			<fo:table-cell >
																		                     				<fo:block linefeed-treatment="preserve" >&#xA;</fo:block>
																		                     				<fo:block text-align="center" font-size="12pt" font-weight="bold"><#if payRollHeader.getValue().get("PAYROL_BEN_MEDALW")?has_content>${payRollHeader.getValue().get("PAYROL_BEN_MEDALW")?if_exists}<#else>&#160;</#if></fo:block>
																		                     				<#if payRollHeader.getValue().get("PAYROL_BEN_MEDALW")?has_content>
																		                     					<#assign totalEarnings=totalEarnings+payRollHeader.getValue().get("PAYROL_BEN_MEDALW")>
																		                     				</#if>
																		                     			</fo:table-cell>
																		                     			<fo:table-cell >
																		                     				<fo:block linefeed-treatment="preserve" >&#xA;</fo:block>
																		                     				<fo:block text-align="center" font-size="12pt" font-weight="bold">${(Static["org.ofbiz.base.util.UtilDateTime"].toDateString(timePeriodEnd, "MM/yyyy")).toUpperCase()}</fo:block>
																		                     			</fo:table-cell>
																	                     			</fo:table-row>
																	                           </fo:table-body>
																	                     	</fo:table>
																	                 	</fo:block>
														                            </fo:table-cell>
														                      	</fo:table-row>
														                      	<fo:table-row >
								                     								<fo:table-cell >
													                     				<fo:block font-family="Courier,monospace">
												                     						<fo:table>
														                      					<fo:table-column column-width="60pt"/>
												                								<fo:table-column column-width="55pt"/>
												                								<fo:table-column column-width="55pt"/>
												                								<fo:table-column column-width="55pt"/>
												                								<fo:table-column column-width="55pt"/>
												                								<fo:table-column column-width="55pt"/>
												                								<fo:table-column column-width="55pt"/>
												                								<fo:table-column column-width="55pt"/>
												                								<fo:table-column column-width="55pt"/>
												                								<fo:table-column column-width="55pt"/>
												                								<fo:table-column column-width="55pt"/>
												                								<fo:table-column column-width="57pt"/>
												                								<fo:table-column column-width="71pt"/>
														                      					<fo:table-body> 
																	                     			<fo:table-row >
																	                     				<fo:table-cell >
																		                     				<fo:block linefeed-treatment="preserve" >&#xA;</fo:block>
																		                     				<fo:block linefeed-treatment="preserve" >&#xA;</fo:block>
																		                     				<fo:block text-align="center" font-size="12pt" font-weight="bold"><#if payRollHeader.getValue().get("PAYROL_BEN_OTALW")?has_content>${payRollHeader.getValue().get("PAYROL_BEN_OTALW")?if_exists}<#else>&#160;</#if></fo:block>
																		                     				<#if payRollHeader.getValue().get("PAYROL_BEN_OTALW")?has_content>
																		                     					<#assign totalEarnings=totalEarnings+payRollHeader.getValue().get("PAYROL_BEN_OTALW")>
																		                     				</#if>
																		                     			</fo:table-cell>
																		                     			<fo:table-cell >
																		                     				<fo:block linefeed-treatment="preserve" >&#xA;</fo:block>
																		                     				<fo:block linefeed-treatment="preserve" >&#xA;</fo:block>
																		                     				<fo:block text-align="center" font-size="12pt" font-weight="bold"><#if payRollHeader.getValue().get("PAYROL_BEN_HDALW")?has_content>${payRollHeader.getValue().get("PAYROL_BEN_HDALW")?if_exists}<#else>&#160;</#if></fo:block>
																		                     				<#if payRollHeader.getValue().get("PAYROL_BEN_HDALW")?has_content>
																		                     					<#assign totalEarnings=totalEarnings+payRollHeader.getValue().get("PAYROL_BEN_HDALW")>
																		                     				</#if>
																		                     			</fo:table-cell>
																		                     			<fo:table-cell >
																		                     				<fo:block linefeed-treatment="preserve" >&#xA;</fo:block>
																		                     				<fo:block linefeed-treatment="preserve" >&#xA;</fo:block>
																		                     				<fo:block text-align="center" font-size="12pt" font-weight="bold"><#if payRollHeader.getValue().get("PAYROL_BEN_OPALW")?has_content>${payRollHeader.getValue().get("PAYROL_BEN_OPALW")?if_exists}<#else>&#160;</#if></fo:block>
																		                     				<#if payRollHeader.getValue().get("PAYROL_BEN_OPALW")?has_content>
																		                     					<#assign totalEarnings=totalEarnings+payRollHeader.getValue().get("PAYROL_BEN_OPALW")>
																		                     				</#if>
																		                     			</fo:table-cell>
																		                     			<fo:table-cell >
																		                     				<fo:block linefeed-treatment="preserve" >&#xA;</fo:block>
																		                     				<fo:block linefeed-treatment="preserve" >&#xA;</fo:block>
																		                     				<fo:block text-align="center" font-size="12pt" font-weight="bold"><#if payRollHeader.getValue().get("PAYROL_BEN_ICALW")?has_content>${payRollHeader.getValue().get("PAYROL_BEN_ICALW")?if_exists}<#else>&#160;</#if></fo:block>
																		                     				<#if payRollHeader.getValue().get("PAYROL_BEN_ICALW")?has_content>
																		                     					<#assign totalEarnings=totalEarnings+payRollHeader.getValue().get("PAYROL_BEN_ICALW")>
																		                     				</#if>
																		                     			</fo:table-cell>
																		                     			<fo:table-cell >
																		                     				<fo:block linefeed-treatment="preserve" >&#xA;</fo:block>
																		                     				<fo:block linefeed-treatment="preserve" >&#xA;</fo:block>
																		                     				<fo:block text-align="center" font-size="12pt" font-weight="bold"><#if payRollHeader.getValue().get("PAYROL_BEN_RISKALW")?has_content>${payRollHeader.getValue().get("PAYROL_BEN_RISKALW")?if_exists}<#else>&#160;</#if></fo:block>
																		                     				<#if payRollHeader.getValue().get("PAYROL_BEN_RISKALW")?has_content>
																		                     					<#assign totalEarnings=totalEarnings+payRollHeader.getValue().get("PAYROL_BEN_RISKALW")>
																		                     				</#if>
																		                     			</fo:table-cell>
																		                     			<fo:table-cell >
																		                     				<fo:block linefeed-treatment="preserve" >&#xA;</fo:block>
																		                     				<fo:block linefeed-treatment="preserve" >&#xA;</fo:block>
																		                     				<fo:block text-align="center" font-size="12pt" font-weight="bold"><#if payRollHeader.getValue().get("PAYROL_BEN_SUPARRS")?has_content>${payRollHeader.getValue().get("PAYROL_BEN_SUPARRS")?if_exists}<#else>&#160;</#if></fo:block>
																		                     				<#if payRollHeader.getValue().get("PAYROL_BEN_SUPARRS")?has_content>
																		                     					<#assign totalEarnings=totalEarnings+payRollHeader.getValue().get("PAYROL_BEN_SUPARRS")>
																		                     				</#if>
																		                     			</fo:table-cell>
																		                     			<fo:table-cell >
																		                     				<fo:block linefeed-treatment="preserve" >&#xA;</fo:block>
																		                     				<fo:block linefeed-treatment="preserve" >&#xA;</fo:block>
																		                     				<fo:block text-align="center" font-size="12pt" font-weight="bold"><#if payRollHeader.getValue().get("PAYROL_BEN_MISCONE")?has_content>${payRollHeader.getValue().get("PAYROL_BEN_MISCONE")?if_exists}<#else>&#160;</#if></fo:block>
																		                     				<#if payRollHeader.getValue().get("PAYROL_BEN_MISCONE")?has_content>
																		                     					<#assign totalEarnings=totalEarnings+payRollHeader.getValue().get("PAYROL_BEN_MISCONE")>
																		                     				</#if>
																		                     			</fo:table-cell>
																		                     			<fo:table-cell >
																		                     				<fo:block linefeed-treatment="preserve" >&#xA;</fo:block>
																		                     				<fo:block linefeed-treatment="preserve" >&#xA;</fo:block>
																		                     				<fo:block text-align="center" font-size="12pt" font-weight="bold"><#if payRollHeader.getValue().get("PAYROL_BEN_MISCTWO")?has_content>${payRollHeader.getValue().get("PAYROL_BEN_MISCTWO")?if_exists}<#else>&#160;</#if></fo:block>
																		                     				<#if payRollHeader.getValue().get("PAYROL_BEN_MISCTWO")?has_content>
																		                     					<#assign totalEarnings=totalEarnings+payRollHeader.getValue().get("PAYROL_BEN_MISCTWO")>
																		                     				</#if>
																		                     			</fo:table-cell>
																		                     			<fo:table-cell >
																		                     				<fo:block linefeed-treatment="preserve" >&#xA;</fo:block>
																		                     				<fo:block linefeed-treatment="preserve" >&#xA;</fo:block>
																		                     				<fo:block text-align="center" font-size="12pt" font-weight="bold"><#if payRollHeader.getValue().get("PAYROL_BEN_MISCTHREE")?has_content>${payRollHeader.getValue().get("PAYROL_BEN_MISCTHREE")?if_exists}<#else>&#160;</#if></fo:block>
																		                     				<#if payRollHeader.getValue().get("PAYROL_BEN_MISCTHREE")?has_content>
																		                     					<#assign totalEarnings=totalEarnings+payRollHeader.getValue().get("PAYROL_BEN_MISCTHREE")>
																		                     				</#if>
																		                     			</fo:table-cell>
																		                     			<fo:table-cell >
																		                     				<fo:block linefeed-treatment="preserve" >&#xA;</fo:block>
																		                     				<fo:block linefeed-treatment="preserve" >&#xA;</fo:block>
																		                     				<fo:block text-align="center" font-size="12pt" font-weight="bold"><#if payRollHeader.getValue().get("PAYROL_BEN_CONALW")?has_content>${payRollHeader.getValue().get("PAYROL_BEN_CONALW")?if_exists}<#else>&#160;</#if></fo:block>
																		                     				<#if payRollHeader.getValue().get("PAYROL_BEN_CONALW")?has_content>
																		                     					<#assign totalEarnings=totalEarnings+payRollHeader.getValue().get("PAYROL_BEN_CONALW")>
																		                     				</#if>
																		                     			</fo:table-cell>
																		                     			<fo:table-cell >
																		                     				<fo:block linefeed-treatment="preserve" >&#xA;</fo:block>
																		                     				<fo:block linefeed-treatment="preserve" >&#xA;</fo:block>
																		                     				<fo:block text-align="center" font-size="12pt" font-weight="bold"><#if payRollHeader.getValue().get("PAYROL_BEN_CBALW")?has_content>${payRollHeader.getValue().get("PAYROL_BEN_CBALW")?if_exists}<#else>&#160;</#if></fo:block>
																		                     				<#if payRollHeader.getValue().get("PAYROL_BEN_CBALW")?has_content>
																		                     					<#assign totalEarnings=totalEarnings+payRollHeader.getValue().get("PAYROL_BEN_CBALW")>
																		                     				</#if>
																		                     			</fo:table-cell>
																		                     			<fo:table-cell >
																		                     				<fo:block linefeed-treatment="preserve" >&#xA;</fo:block>
																		                     				<fo:block linefeed-treatment="preserve" >&#xA;</fo:block>
																		                     				<fo:block text-align="center" font-size="12pt" font-weight="bold">&#160;</fo:block>
																		                     			</fo:table-cell>
																		                     			<fo:table-cell >
																		                     				<fo:block linefeed-treatment="preserve" >&#xA;</fo:block>
																		                     				<fo:block linefeed-treatment="preserve" >&#xA;</fo:block>
																		                     				<fo:block text-align="center" font-size="12pt" font-weight="bold">${(noOfPayableDays?if_exists)}  </fo:block>
																		                     			</fo:table-cell>
																	                     			</fo:table-row>
																	                           </fo:table-body>
																	                     	</fo:table>
																	                 	</fo:block>
														                            </fo:table-cell>
														                      	</fo:table-row>
														                      	<fo:table-row >
								                     								<fo:table-cell >
													                     				<fo:block font-family="Courier,monospace">
												                     						<fo:table>
														                      					<fo:table-column column-width="60pt"/>
												                								<fo:table-column column-width="55pt"/>
												                								<fo:table-column column-width="55pt"/>
												                								<fo:table-column column-width="55pt"/>
												                								<fo:table-column column-width="55pt"/>
												                								<fo:table-column column-width="55pt"/>
												                								<fo:table-column column-width="55pt"/>
												                								<fo:table-column column-width="55pt"/>
												                								<fo:table-column column-width="55pt"/>
												                								<fo:table-column column-width="55pt"/>
												                								<fo:table-column column-width="55pt"/>
												                								<fo:table-column column-width="57pt"/>
												                								<fo:table-column column-width="71pt"/>
														                      					<fo:table-body> 
																	                     			<fo:table-row >
																	                     				<fo:table-cell >
																		                     				<fo:block linefeed-treatment="preserve" >&#xA;</fo:block>
																		                     				<fo:block text-align="center" font-size="12pt" font-weight="bold"><#if payRollHeader.getValue().get("PAYROL_DD_EPF")?has_content>${(payRollHeader.getValue().get("PAYROL_DD_EPF")*(-1))?if_exists}<#else>&#160;</#if></fo:block>
																		                     				<fo:block linefeed-treatment="preserve" >&#xA;</fo:block>
																		                     				<#if payRollHeader.getValue().get("PAYROL_DD_EPF")?has_content>
																		                     					<#assign totalDeductions=totalDeductions+(payRollHeader.getValue().get("PAYROL_DD_EPF")*(-1))>
																		                     				</#if>
																		                     			</fo:table-cell>
																		                     			<fo:table-cell >
																		                     				<fo:block linefeed-treatment="preserve" >&#xA;</fo:block>
																		                     				<fo:block text-align="center" font-size="12pt" font-weight="bold"><#if payRollHeader.getValue().get("PAYROL_DD_APGLIF")?has_content>${(payRollHeader.getValue().get("PAYROL_DD_APGLIF")*(-1))?if_exists}<#else>&#160;</#if></fo:block>
																		                     				<fo:block linefeed-treatment="preserve" >&#xA;</fo:block>
																		                     				<#if payRollHeader.getValue().get("PAYROL_DD_APGLIF")?has_content>
																		                     					<#assign totalDeductions=totalDeductions+(payRollHeader.getValue().get("PAYROL_DD_APGLIF")*(-1))>
																		                     				</#if>
																		                     			</fo:table-cell>
																		                     			<fo:table-cell >
																		                     				<fo:block linefeed-treatment="preserve" >&#xA;</fo:block>
																		                     				<fo:block text-align="center" font-size="12pt" font-weight="bold"><#if payRollHeader.getValue().get("PAYROL_DD_VOLPF")?has_content>${(payRollHeader.getValue().get("PAYROL_DD_VOLPF")*(-1))?if_exists}<#else>&#160;</#if></fo:block>
																		                     				<fo:block linefeed-treatment="preserve" >&#xA;</fo:block>
																		                     				<#if payRollHeader.getValue().get("PAYROL_DD_VOLPF")?has_content>
																		                     					<#assign totalDeductions=totalDeductions+(payRollHeader.getValue().get("PAYROL_DD_VOLPF")*(-1))>
																		                     				</#if>
																		                     			</fo:table-cell>
																		                     			<fo:table-cell >
																		                     				<fo:block linefeed-treatment="preserve" >&#xA;</fo:block>
																		                     				<fo:block text-align="center" font-size="12pt" font-weight="bold"><#if payRollHeader.getValue().get("PAYROL_DD_GPFLN")?has_content>${(payRollHeader.getValue().get("PAYROL_DD_GPFLN")*(-1))?if_exists}<#else>&#160;</#if></fo:block>
																		                     				<fo:block linefeed-treatment="preserve" >&#xA;</fo:block>
																		                     				<#if payRollHeader.getValue().get("PAYROL_DD_GPFLN")?has_content>
																		                     					<#assign totalDeductions=totalDeductions+(payRollHeader.getValue().get("PAYROL_DD_GPFLN")*(-1))>
																		                     				</#if>
																		                     			</fo:table-cell>
																		                     			<fo:table-cell >
																		                     				<fo:block linefeed-treatment="preserve" >&#xA;</fo:block>
																		                     				<fo:block text-align="center" font-size="12pt" font-weight="bold"><#if payRollHeader.getValue().get("PAYROL_DD_DEDRS01")?has_content>${(payRollHeader.getValue().get("PAYROL_DD_DEDRS01")*(-1))?if_exists}<#else>&#160;</#if></fo:block>
																		                     				<fo:block linefeed-treatment="preserve" >&#xA;</fo:block>
																		                     				<#if payRollHeader.getValue().get("PAYROL_DD_DEDRS01")?has_content>
																		                     					<#assign totalDeductions=totalDeductions+(payRollHeader.getValue().get("PAYROL_DD_DEDRS01")*(-1))>
																		                     				</#if>
																		                     			</fo:table-cell>
																		                     			<fo:table-cell >
																		                     				<fo:block linefeed-treatment="preserve" >&#xA;</fo:block>
																		                     				<fo:block text-align="center" font-size="12pt" font-weight="bold"><#if payRollHeader.getValue().get("PAYROL_DD_EDNADV")?has_content>${(payRollHeader.getValue().get("PAYROL_DD_EDNADV")*(-1))?if_exists}<#else>&#160;</#if></fo:block>
																		                     				<fo:block linefeed-treatment="preserve" >&#xA;</fo:block>
																		                     				<#if payRollHeader.getValue().get("PAYROL_DD_EDNADV")?has_content>
																		                     					<#assign totalDeductions=totalDeductions+(payRollHeader.getValue().get("PAYROL_DD_EDNADV")*(-1))>
																		                     				</#if>
																		                     			</fo:table-cell>
																		                     			<fo:table-cell >
																		                     				<fo:block linefeed-treatment="preserve" >&#xA;</fo:block>
																		                     				<fo:block text-align="center" font-size="12pt" font-weight="bold"><#if payRollHeader.getValue().get("PAYROL_DD_SSS")?has_content>${(payRollHeader.getValue().get("PAYROL_DD_SSS")*(-1))?if_exists}<#else>&#160;</#if></fo:block>
																		                     				<fo:block linefeed-treatment="preserve" >&#xA;</fo:block>
																		                     				<#if payRollHeader.getValue().get("PAYROL_DD_SSS")?has_content>
																		                     					<#assign totalDeductions=totalDeductions+(payRollHeader.getValue().get("PAYROL_DD_SSS")*(-1))>
																		                     				</#if>
																		                     			</fo:table-cell>
																		                     			<fo:table-cell >
																		                     				<fo:block linefeed-treatment="preserve" >&#xA;</fo:block>
																		                     				<fo:block text-align="center" font-size="12pt" font-weight="bold"><#if payRollHeader.getValue().get("PAYROL_DD_IT")?has_content>${(payRollHeader.getValue().get("PAYROL_DD_IT")*(-1))?if_exists}<#else>&#160;</#if></fo:block>
																		                     				<fo:block linefeed-treatment="preserve" >&#xA;</fo:block>
																		                     				<#if payRollHeader.getValue().get("PAYROL_DD_IT")?has_content>
																		                     					<#assign totalDeductions=totalDeductions+(payRollHeader.getValue().get("PAYROL_DD_IT")*(-1))>
																		                     				</#if>
																		                     			</fo:table-cell>
																		                     			<fo:table-cell >
																		                     				<fo:block linefeed-treatment="preserve" >&#xA;</fo:block>
																		                     				<fo:block text-align="center" font-size="12pt" font-weight="bold"><#if payRollHeader.getValue().get("PAYROL_DD_HRR")?has_content>${(payRollHeader.getValue().get("PAYROL_DD_HRR")*(-1))?if_exists}<#else>&#160;</#if></fo:block>
																		                     				<fo:block linefeed-treatment="preserve" >&#xA;</fo:block>
																		                     				<#if payRollHeader.getValue().get("PAYROL_DD_HRR")?has_content>
																		                     					<#assign totalDeductions=totalDeductions+(payRollHeader.getValue().get("PAYROL_DD_HRR")*(-1))>
																		                     				</#if>
																		                     			</fo:table-cell>
																		                     			<fo:table-cell >
																		                     				<fo:block linefeed-treatment="preserve" >&#xA;</fo:block>
																		                     				<fo:block text-align="center" font-size="12pt" font-weight="bold"><#if payRollHeader.getValue().get("PAYROL_DD_ELECT")?has_content>${(payRollHeader.getValue().get("PAYROL_DD_ELECT")*(-1))?if_exists}<#else>&#160;</#if></fo:block>
																		                     				<fo:block linefeed-treatment="preserve" >&#xA;</fo:block>
																		                     				<#if payRollHeader.getValue().get("PAYROL_DD_ELECT")?has_content>
																		                     					<#assign totalDeductions=totalDeductions+(payRollHeader.getValue().get("PAYROL_DD_ELECT")*(-1))>
																		                     				</#if>
																		                     			</fo:table-cell>
																		                     			<fo:table-cell >
																		                     				<fo:block linefeed-treatment="preserve" >&#xA;</fo:block>
																		                     				<fo:block text-align="center" font-size="12pt" font-weight="bold"><#if payRollHeader.getValue().get("PAYROL_DD_WATER")?has_content>${(payRollHeader.getValue().get("PAYROL_DD_WATER")*(-1))?if_exists}<#else>&#160;</#if></fo:block>
																		                     				<fo:block linefeed-treatment="preserve" >&#xA;</fo:block>
																		                     				<#if payRollHeader.getValue().get("PAYROL_DD_WATER")?has_content>
																		                     					<#assign totalDeductions=totalDeductions+(payRollHeader.getValue().get("PAYROL_DD_WATER")*(-1))>
																		                     				</#if>
																		                     			</fo:table-cell>
																		                     			<fo:table-cell >
																		                     				<fo:block linefeed-treatment="preserve" >&#xA;</fo:block>
																		                     				<fo:block text-align="center" font-size="12pt" font-weight="bold"><#if payRollHeader.getValue().get("PAYROL_DD_WELFARE")?has_content>${(payRollHeader.getValue().get("PAYROL_DD_WELFARE")*(-1))?if_exists}<#else>&#160;</#if></fo:block>
																		                     				<fo:block linefeed-treatment="preserve" >&#xA;</fo:block>
																		                     				<#if payRollHeader.getValue().get("PAYROL_DD_WELFARE")?has_content>
																		                     					<#assign totalDeductions=totalDeductions+(payRollHeader.getValue().get("PAYROL_DD_WELFARE")*(-1))>
																		                     				</#if>
																		                     			</fo:table-cell>
																		                     			<fo:table-cell >
																		                     				<fo:block linefeed-treatment="preserve" >&#xA;</fo:block>
																		                     				<fo:block text-align="left" font-size="12pt" font-weight="bold">${totalEarnings?if_exists?string("#0")}</fo:block>
																		                     				<fo:block linefeed-treatment="preserve" >&#xA;</fo:block>
																		                     			</fo:table-cell>
																	                     			</fo:table-row>
																	                           </fo:table-body>
																	                     	</fo:table>
																	                 	</fo:block>
														                            </fo:table-cell>
														                      	</fo:table-row>
														                      	<fo:table-row >
								                     								<fo:table-cell >
													                     				<fo:block font-family="Courier,monospace">
												                     						<fo:table>
														                      					<fo:table-column column-width="60pt"/>
												                								<fo:table-column column-width="55pt"/>
												                								<fo:table-column column-width="55pt"/>
												                								<fo:table-column column-width="55pt"/>
												                								<fo:table-column column-width="55pt"/>
												                								<fo:table-column column-width="55pt"/>
												                								<fo:table-column column-width="55pt"/>
												                								<fo:table-column column-width="55pt"/>
												                								<fo:table-column column-width="55pt"/>
												                								<fo:table-column column-width="55pt"/>
												                								<fo:table-column column-width="55pt"/>
												                								<fo:table-column column-width="57pt"/>
												                								<fo:table-column column-width="71pt"/>
														                      					<fo:table-body> 
																	                     			<fo:table-row >
																	                     				<fo:table-cell >
																		                     				<fo:block linefeed-treatment="preserve" >&#xA;</fo:block>
																		                     				<fo:block text-align="center" font-size="12pt" font-weight="bold"><#if hdfcDedAmount != 0>${hdfcDedAmount?if_exists}<#else>&#160;</#if></fo:block>
																		                     				<#if hdfcDedAmount!= 0>
																		                     					<#assign totalDeductions=totalDeductions+(hdfcDedAmount*(-1))>
																		                     				</#if>
																		                     				<fo:block linefeed-treatment="preserve" >&#xA;</fo:block>
																		                     			</fo:table-cell>
																		                     			<fo:table-cell >
																		                     				<fo:block linefeed-treatment="preserve" >&#xA;</fo:block>
																		                     				<fo:block text-align="center" font-size="12pt" font-weight="bold"><#if payRollHeader.getValue().get("PAYROL_DD_FESTADV")?has_content>${(payRollHeader.getValue().get("PAYROL_DD_FESTADV")*(-1))?if_exists}<#else>&#160;</#if></fo:block>
																		                     				<fo:block linefeed-treatment="preserve" >&#xA;</fo:block>
																		                     				<#if payRollHeader.getValue().get("PAYROL_DD_FESTADV")?has_content>
																		                     					<#assign totalDeductions=totalDeductions+(payRollHeader.getValue().get("PAYROL_DD_FESTADV")*(-1))>
																		                     				</#if>
																		                     			</fo:table-cell>
																		                     			<fo:table-cell >
																		                     				<fo:block linefeed-treatment="preserve" >&#xA;</fo:block>
																		                     				<fo:block text-align="center" font-size="12pt" font-weight="bold">&#160;</fo:block>
																		                     				<fo:block linefeed-treatment="preserve" >&#xA;</fo:block>
																		                     			</fo:table-cell>
																		                     			<fo:table-cell >
																		                     				<fo:block linefeed-treatment="preserve" >&#xA;</fo:block>
																		                     				<fo:block text-align="center" font-size="12pt" font-weight="bold"><#if payRollHeader.getValue().get("PAYROL_DD_MRGLN")?has_content>${(payRollHeader.getValue().get("PAYROL_DD_MRGLN")*(-1))?if_exists}<#else>&#160;</#if></fo:block>
																		                     				<fo:block linefeed-treatment="preserve" >&#xA;</fo:block>
																		                     				<#if payRollHeader.getValue().get("PAYROL_DD_MRGLN")?has_content>
																		                     					<#assign totalDeductions=totalDeductions+(payRollHeader.getValue().get("PAYROL_DD_MRGLN")*(-1))>
																		                     				</#if>
																		                     			</fo:table-cell>
																		                     			<fo:table-cell >
																		                     				<fo:block linefeed-treatment="preserve" >&#xA;</fo:block>
																		                     				<fo:block text-align="center" font-size="12pt" font-weight="bold"><#if payRollHeader.getValue().get("PAYROL_DD_PTAX")?has_content>${(payRollHeader.getValue().get("PAYROL_DD_PTAX")*(-1))?if_exists}<#else>&#160;</#if></fo:block>
																		                     				<fo:block linefeed-treatment="preserve" >&#xA;</fo:block>
																		                     				<#if payRollHeader.getValue().get("PAYROL_DD_PTAX")?has_content>
																		                     					<#assign totalDeductions=totalDeductions+(payRollHeader.getValue().get("PAYROL_DD_PTAX")*(-1))>
																		                     				</#if>
																		                     			</fo:table-cell>
																		                     			<fo:table-cell >
																		                     				<fo:block linefeed-treatment="preserve" >&#xA;</fo:block>
																		                     				<fo:block text-align="center" font-size="12pt" font-weight="bold"><#if payRollHeader.getValue().get("PAYROL_DD_MILKCARDS")?has_content>${(payRollHeader.getValue().get("PAYROL_DD_MILKCARDS")*(-1))?if_exists}<#else>&#160;</#if></fo:block>
																		                     				<fo:block linefeed-treatment="preserve" >&#xA;</fo:block>
																		                     				<#if payRollHeader.getValue().get("PAYROL_DD_MILKCARDS")?has_content>
																		                     					<#assign totalDeductions=totalDeductions+(payRollHeader.getValue().get("PAYROL_DD_MILKCARDS")*(-1))>
																		                     				</#if>
																		                     			</fo:table-cell>
																		                     			<fo:table-cell >
																		                     				<fo:block linefeed-treatment="preserve" >&#xA;</fo:block>
																		                     				<fo:block text-align="center" font-size="12pt" font-weight="bold"><#if payRollHeader.getValue().get("PAYROL_DD_MILKDUES")?has_content>${(payRollHeader.getValue().get("PAYROL_DD_MILKDUES")*(-1))?if_exists}<#else>&#160;</#if></fo:block>
																		                     				<fo:block linefeed-treatment="preserve" >&#xA;</fo:block>
																		                     				<#if payRollHeader.getValue().get("PAYROL_DD_MILKDUES")?has_content>
																		                     					<#assign totalDeductions=totalDeductions+(payRollHeader.getValue().get("PAYROL_DD_MILKDUES")*(-1))>
																		                     				</#if>
																		                     			</fo:table-cell>
																		                     			<fo:table-cell >
																		                     				<fo:block linefeed-treatment="preserve" >&#xA;</fo:block>
																		                     				<fo:block text-align="center" font-size="12pt" font-weight="bold"><#if payRollHeader.getValue().get("PAYROL_DD_MEDADV")?has_content>${(payRollHeader.getValue().get("PAYROL_DD_MEDADV")*(-1))?if_exists}<#else>&#160;</#if></fo:block>
																		                     				<fo:block linefeed-treatment="preserve" >&#xA;</fo:block>
																		                     				<#if payRollHeader.getValue().get("PAYROL_DD_MEDADV")?has_content>
																		                     					<#assign totalDeductions=totalDeductions+(payRollHeader.getValue().get("PAYROL_DD_MEDADV")*(-1))>
																		                     				</#if>
																		                     			</fo:table-cell>
																		                     			<fo:table-cell >
																		                     				<fo:block linefeed-treatment="preserve" >&#xA;</fo:block>
																		                     				<fo:block text-align="center" font-size="12pt" font-weight="bold"><#if payRollHeader.getValue().get("PAYROL_DD_DEDRS02")?has_content>${(payRollHeader.getValue().get("PAYROL_DD_DEDRS02")*(-1))?if_exists}<#else>&#160;</#if></fo:block>
																		                     				<fo:block linefeed-treatment="preserve" >&#xA;</fo:block>
																		                     				<#if payRollHeader.getValue().get("PAYROL_DD_DEDRS02")?has_content>
																		                     					<#assign totalDeductions=totalDeductions+(payRollHeader.getValue().get("PAYROL_DD_DEDRS02")*(-1))>
																		                     				</#if>
																		                     			</fo:table-cell>
																		                     			<fo:table-cell >
																		                     				<fo:block linefeed-treatment="preserve" >&#xA;</fo:block>
																		                     				<fo:block text-align="center" font-size="12pt" font-weight="bold"><#if payRollHeader.getValue().get("PAYROL_DD_ESI")?has_content>${(payRollHeader.getValue().get("PAYROL_DD_ESI")*(-1))?if_exists}<#else>&#160;</#if></fo:block>
																		                     				<#if payRollHeader.getValue().get("PAYROL_DD_ESI")?has_content>
																		                     					<#assign totalDeductions=totalDeductions+(payRollHeader.getValue().get("PAYROL_DD_ESI")*(-1))>
																		                     				</#if>
																		                     				<fo:block linefeed-treatment="preserve" >&#xA;</fo:block>
																		                     			</fo:table-cell>
																		                     			<fo:table-cell >
																		                     				<fo:block linefeed-treatment="preserve" >&#xA;</fo:block>
																		                     				<fo:block text-align="center" font-size="12pt" font-weight="bold"><#if payRollHeader.getValue().get("PAYROL_DD_GIS")?has_content>${(payRollHeader.getValue().get("PAYROL_DD_GIS")*(-1))?if_exists}<#else>&#160;</#if></fo:block>
																		                     				<fo:block linefeed-treatment="preserve" >&#xA;</fo:block>
																		                     				<#if payRollHeader.getValue().get("PAYROL_DD_GIS")?has_content>
																		                     					<#assign totalDeductions=totalDeductions+(payRollHeader.getValue().get("PAYROL_DD_GIS")*(-1))>
																		                     				</#if>
																		                     			</fo:table-cell>
																		                     			<fo:table-cell >
																		                     				<fo:block linefeed-treatment="preserve" >&#xA;</fo:block>
																		                     				<fo:block text-align="center" font-size="12pt" font-weight="bold"><#if payRollHeader.getValue().get("PAYROL_DD_DPTDUES")?has_content>${(payRollHeader.getValue().get("PAYROL_DD_DPTDUES")*(-1))?if_exists}<#else>&#160;</#if></fo:block>
																		                     				<#if payRollHeader.getValue().get("PAYROL_DD_DPTDUES")?has_content>
																		                     					<#assign totalDeductions=totalDeductions+(payRollHeader.getValue().get("PAYROL_DD_DPTDUES")*(-1))>
																		                     				</#if>
																		                     				<fo:block linefeed-treatment="preserve" >&#xA;</fo:block>
																		                     			</fo:table-cell>
																		                     			<fo:table-cell >
																		                     				<fo:block linefeed-treatment="preserve" >&#xA;</fo:block>
																		                     				<fo:block text-align="left" font-size="12pt" font-weight="bold">${totalDeductions?if_exists?string("#0.00")}</fo:block>
																		                     				<fo:block linefeed-treatment="preserve" >&#xA;</fo:block>
																		                     			</fo:table-cell>
																	                     			</fo:table-row>
																	                           </fo:table-body>
																	                     	</fo:table>
																	                 	</fo:block>
														                            </fo:table-cell>
														                      	</fo:table-row>
														                      	<fo:table-row >
								                     								<fo:table-cell >
													                     				<fo:block font-family="Courier,monospace">
												                     						<fo:table>
														                      					<fo:table-column column-width="60pt"/>
												                								<fo:table-column column-width="55pt"/>
												                								<fo:table-column column-width="55pt"/>
												                								<fo:table-column column-width="55pt"/>
												                								<fo:table-column column-width="55pt"/>
												                								<fo:table-column column-width="55pt"/>
												                								<fo:table-column column-width="55pt"/>
												                								<fo:table-column column-width="55pt"/>
												                								<fo:table-column column-width="55pt"/>
												                								<fo:table-column column-width="55pt"/>
												                								<fo:table-column column-width="55pt"/>
												                								<fo:table-column column-width="57pt"/>
												                								<fo:table-column column-width="71pt"/>
														                      					<fo:table-body> 
																	                     			<fo:table-row >
																	                     				<fo:table-cell >
																		                     				<fo:block text-align="center" font-size="12pt" font-weight="bold"><#if payRollHeader.getValue().get("PAYROL_DD_DEDRS10")?has_content>${(payRollHeader.getValue().get("PAYROL_DD_DEDRS10")*(-1))?if_exists}<#else>&#160;</#if></fo:block>
																		                     				<#if payRollHeader.getValue().get("PAYROL_DD_DEDRS10")?has_content>
																		                     					<#assign totalDeductions=totalDeductions+(payRollHeader.getValue().get("PAYROL_DD_DEDRS10")*(-1))>
																		                     				</#if>
																		                     				<fo:block linefeed-treatment="preserve" >&#xA;</fo:block>
																		                     			</fo:table-cell>
																		                     			<fo:table-cell >
																		                     				<#if SBHDedAmount != 0>
																			                     				<fo:block text-align="center" font-size="12pt" font-weight="bold">SBH</fo:block>
																			                     				<fo:block text-align="center" font-size="12pt" font-weight="bold">${SBHDedAmount?if_exists}</fo:block>
																			                     				<#if SBHDedAmount!=0>
																			                     					<#assign totalDeductions=totalDeductions+(SBHDedAmount*(-1))>
																			                     				</#if>
																			                     			<#else>
																		                     					<fo:block text-align="center" font-size="12pt" font-weight="bold">&#160;</fo:block>
																		                     					<fo:block linefeed-treatment="preserve" >&#xA;</fo:block>
																			                     			</#if>
																		                     			</fo:table-cell>
																		                     			<fo:table-cell >
																		                     				<#if canaraDedAmount != 0>
																			                     				<fo:block text-align="center" font-size="12pt" font-weight="bold">CANARA</fo:block>
																			                     				<fo:block text-align="center" font-size="12pt" font-weight="bold">${canaraDedAmount?if_exists}</fo:block>
																			                     				<#if canaraDedAmount!=0>
																			                     					<#assign totalDeductions=totalDeductions+(canaraDedAmount*(-1))>
																			                     				</#if>
																			                     			<#else>
																		                     					<fo:block text-align="center" font-size="12pt" font-weight="bold">&#160;</fo:block>
																		                     					<fo:block linefeed-treatment="preserve" >&#xA;</fo:block>
																			                     			</#if>
																		                     			</fo:table-cell>
																		                     			<fo:table-cell >
																		                     				<#if otherDedAmount != 0>
																			                     				<fo:block text-align="center" font-size="12pt" font-weight="bold">OTHER</fo:block>
																			                     				<fo:block text-align="center" font-size="12pt" font-weight="bold">${otherDedAmount?if_exists}</fo:block>
																			                     				<#if otherDedAmount!=0>
																			                     					<#assign totalDeductions=totalDeductions+(otherDedAmount*(-1))>
																			                     				</#if>
																			                     			<#else>
																		                     					<fo:block text-align="center" font-size="12pt" font-weight="bold">&#160;</fo:block>
																		                     					<fo:block linefeed-treatment="preserve" >&#xA;</fo:block>
																			                     			</#if>
																		                     			</fo:table-cell>
																		                     			<fo:table-cell >
																		                     				<fo:block text-align="center" font-size="12pt" font-weight="bold">&#160;</fo:block>
																		                     				<fo:block linefeed-treatment="preserve" >&#xA;</fo:block>
																		                     			</fo:table-cell>
																		                     			<fo:table-cell >
																		                     				<fo:block text-align="center" font-size="12pt" font-weight="bold">&#160;</fo:block>
																		                     				<fo:block linefeed-treatment="preserve" >&#xA;</fo:block>
																		                     			</fo:table-cell>
																		                     			<fo:table-cell >
																		                     				<fo:block text-align="center" font-size="12pt" font-weight="bold">&#160;</fo:block>
																		                     				<fo:block linefeed-treatment="preserve" >&#xA;</fo:block>
																		                     			</fo:table-cell>
																		                     			<fo:table-cell >
																		                     				<fo:block text-align="center" font-size="12pt" font-weight="bold">&#160;</fo:block>
																		                     				<fo:block linefeed-treatment="preserve" >&#xA;</fo:block>
																		                     			</fo:table-cell>
																		                     			<fo:table-cell >
																		                     				<fo:block text-align="center" font-size="12pt" font-weight="bold">&#160;</fo:block>
																		                     				<fo:block linefeed-treatment="preserve" >&#xA;</fo:block>
																		                     			</fo:table-cell>
																		                     			<fo:table-cell >
																		                     				<fo:block text-align="center" font-size="12pt" font-weight="bold">&#160;</fo:block>
																		                     				<fo:block linefeed-treatment="preserve" >&#xA;</fo:block>
																		                     			</fo:table-cell>
																		                     			<fo:table-cell >
																		                     				<fo:block text-align="center" font-size="12pt" font-weight="bold">&#160;</fo:block>
																		                     				<fo:block linefeed-treatment="preserve" >&#xA;</fo:block>
																		                     			</fo:table-cell>
																		                     			<fo:table-cell >
																		                     				<fo:block text-align="center" font-size="12pt" font-weight="bold">${(totalEarnings-totalDeductions)?if_exists?string("#0")}</fo:block>
																		                     				<fo:block linefeed-treatment="preserve" >&#xA;</fo:block>
																		                     			</fo:table-cell>
																		                     			<fo:table-cell >
																		                     				<fo:block text-align="left" font-size="12pt" font-weight="bold">${(totalEarnings-totalDeductions)?if_exists?string("#0.00")}</fo:block>
																		                     				<fo:block linefeed-treatment="preserve" >&#xA;</fo:block>
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
									                      	</fo:table-row>
							                           </fo:table-body>
							                     	</fo:table>
							                 	</fo:block>
				                            </fo:table-cell>
				                      	</fo:table-row>
				                      	<fo:table-row>
					                      	<fo:table-cell >
			                     				<fo:block linefeed-treatment="preserve">&#xA;</fo:block>
			                     			</fo:table-cell>
		                     			</fo:table-row>
		                     			<fo:table-row>
					                      	<fo:table-cell >
			                     				<fo:block linefeed-treatment="preserve">&#xA;</fo:block>
			                     			</fo:table-cell>
		                     			</fo:table-row>
		                           	</fo:table-body>
		                     	</fo:table>
	                 		</fo:block>
	                 	</#if>
	                 	
	                 	
	                 	
	                 	
	                 	<#if listNo=3>
			            	<#assign payHeader = delegator.findOne("PayrollHeader", {"payrollHeaderId" : payRollHeader.getKey()}, true)>
	      	 				<#assign partyId = payHeader.partyIdFrom>
		            		<#assign emplPositionAndFulfilment=delegator.findByAnd("EmplPositionAndFulfillment", {"employeePartyId" : partyId})/>
		            		<#assign designation = delegator.findOne("EmplPositionType", {"emplPositionTypeId" : emplPositionAndFulfilment[0].emplPositionTypeId?if_exists}, true)>
	                     	<#assign designationName=emplPositionAndFulfilment[0].name?if_exists>
		            		<#assign emplLeavesDetails = delegator.findOne("PayrollAttendance", {"partyId" : partyId, "customTimePeriodId": timePeriod?if_exists}, true)/>
		            		<#assign totalEarnings=0>
		            		<#assign totalDeductions=0>
	          				<#if emplLeavesDetails?has_content>
		            			<#assign noOfPayableDays=emplLeavesDetails.get("noOfPayableDays")?if_exists>
		            		<#else>
		            			<#assign noOfPayableDays=0>
		            		</#if>
	          				
		            		<fo:block font-family="Courier,monospace">
			            	
			                	<fo:table> 
			                		<fo:table-column column-width="840pt"/>
			                		<fo:table-body> 
			                     		<fo:table-row >
			                     			<fo:table-cell >
			                     				<fo:block font-family="Courier,monospace">
			                     					<fo:table>
			                     						<fo:table-column column-width="40pt"/>
			                							<fo:table-column column-width="738pt"/>
			                							<fo:table-body> 
								                     		<fo:table-row >
								                     			<fo:table-cell >
								                     				<fo:block text-align="center" font-size="12pt" >&#160;</fo:block>
								                     			</fo:table-cell>
								                     			<fo:table-cell >
								                     				<fo:block font-family="Courier,monospace">
								                     					<fo:table>
								                     						<fo:table-column column-width="800pt"/>
								                     						<fo:table-body> 
												                     			<fo:table-row >
								                     								<fo:table-cell >
													                     				<fo:block font-family="Courier,monospace">
												                     						<fo:table> 
												                								<fo:table-column column-width="65pt"/>
												                								<fo:table-column column-width="80pt"/>
												                								<fo:table-column column-width="75pt"/>
												                								<fo:table-column column-width="60pt"/>
												                								<fo:table-column column-width="60pt"/>
												                								<fo:table-column column-width="65pt"/>
												                								<fo:table-column column-width="45pt"/>
												                								<fo:table-column column-width="153pt"/>
												                								<fo:table-column column-width="135pt"/>
												                								<fo:table-body> 
																	                     			<fo:table-row >
																	                     				<fo:table-cell >
																	                     					<fo:block linefeed-treatment="preserve" >&#xA;</fo:block>
																		                     				<fo:block text-align="center" font-size="12pt" font-weight="bold"><#if GISNo?has_content>${GISNo}<#else>&#160;</#if></fo:block>
																		                     			</fo:table-cell>
																		                     			<fo:table-cell >
																		                     				<fo:block linefeed-treatment="preserve" >&#xA;</fo:block>
																		                     				<fo:block text-align="center" font-size="12pt" font-weight="bold">&#160;</fo:block>
																		                     			</fo:table-cell>
																		                     			<fo:table-cell >
																		                     				<fo:block linefeed-treatment="preserve" >&#xA;</fo:block>
																		                     				<fo:block text-align="center" font-size="12pt" font-weight="bold"><#if emplPayRate!= 0>${emplPayRate?if_exists}<#else>&#160;</#if></fo:block>
																		                     			</fo:table-cell>
																		                     			<fo:table-cell >
																		                     				<fo:block linefeed-treatment="preserve" >&#xA;</fo:block>
																		                     				<fo:block text-align="center" font-size="12pt" font-weight="bold">${oragnizationId?if_exists}</fo:block>
																		                     			</fo:table-cell>
																		                     			<fo:table-cell >
																		                     				<fo:block linefeed-treatment="preserve" >&#xA;</fo:block>
																		                     				<fo:block text-align="center" font-size="12pt" font-weight="bold">${unitCode?if_exists}</fo:block>
																		                     			</fo:table-cell>
																		                     			<fo:table-cell >
																		                     				<fo:block linefeed-treatment="preserve" >&#xA;</fo:block>
																		                     				<fo:block text-align="center" font-size="12pt" font-weight="bold">${costCode?if_exists}</fo:block>
																		                     			</fo:table-cell>
																		                     			<#assign partyDetails = delegator.findOne("Party", {"partyId" : partyId}, true)>
																		                     			<fo:table-cell >
																		                     				<fo:block linefeed-treatment="preserve" >&#xA;</fo:block>
																		                     				<fo:block text-align="left" font-size="12pt" font-weight="bold"><#if partyDetails?has_content>${partyDetails.externalId?if_exists}<#else>${partyId?if_exists}</#if></fo:block>
																		                     			</fo:table-cell>
																		                     			<#assign personDetails = delegator.findOne("Person", {"partyId" : partyId}, true)>
																		                     			<fo:table-cell >
																		                     				<fo:block linefeed-treatment="preserve" >&#xA;</fo:block>
																		                     				<fo:block text-align="center" font-size="12pt" font-weight="bold"><#if personDetails?has_content>${(personDetails.nickname).toUpperCase()}<#else>${(Static["org.ofbiz.order.order.OrderServices"].nameTrim((Static["org.ofbiz.party.party.PartyHelper"].getPartyName(delegator, partyId, false)),15)).toUpperCase()}</#if></fo:block>
																		                     			</fo:table-cell>
																		                     			<fo:table-cell >
																		                     				<fo:block linefeed-treatment="preserve" >&#xA;</fo:block>
																		                     				<fo:block text-align="center" font-size="12pt" font-weight="bold"><#if designationName?has_content>${Static["org.ofbiz.order.order.OrderServices"].nameTrim((designationName?if_exists),13)}<#else><#if designation?has_content>${Static["org.ofbiz.order.order.OrderServices"].nameTrim((designation.description?if_exists),13)}</#if></#if></fo:block>
																		                     			</fo:table-cell>
																	                     			</fo:table-row>
																	                           </fo:table-body>
																	                     	</fo:table>
																	                 	</fo:block>
																	                </fo:table-cell>
														                      	</fo:table-row>
														                      	<fo:table-row >
								                     								<fo:table-cell >
													                     				<fo:block font-family="Courier,monospace">
												                     						<fo:table> 
												                								<fo:table-column column-width="60pt"/>
												                								<fo:table-column column-width="60pt"/>
												                								<fo:table-column column-width="60pt"/>
												                								<fo:table-column column-width="60pt"/>
												                								<fo:table-column column-width="60pt"/>
												                								<fo:table-column column-width="60pt"/>
												                								<fo:table-column column-width="60pt"/>
												                								<fo:table-column column-width="60pt"/>
												                								<fo:table-column column-width="65pt"/>
												                								<fo:table-column column-width="60pt"/>
												                								<fo:table-column column-width="55pt"/>
												                								<fo:table-column column-width="78pt"/>
												                								<fo:table-body> 
																	                     			<fo:table-row >
																	                     				<fo:table-cell >
																		                     				<fo:block linefeed-treatment="preserve" >&#xA;</fo:block>
																		                     				<fo:block linefeed-treatment="preserve" >&#xA;</fo:block>
																		                     				<fo:block text-align="center" font-size="12pt" font-weight="bold"><#if hblBalance!= 0>${hblBalance?if_exists}<#else>&#160;</#if></fo:block>
																		                     			</fo:table-cell>
																		                     			<fo:table-cell >
																		                     				<fo:block linefeed-treatment="preserve" >&#xA;</fo:block>
																		                     				<fo:block linefeed-treatment="preserve" >&#xA;</fo:block>
																		                     				<fo:block text-align="center" font-size="12pt" font-weight="bold"><#if festAdvBalance!= 0>${festAdvBalance?if_exists}<#else>&#160;</#if></fo:block>
																		                     			</fo:table-cell>
																		                     			<fo:table-cell >
																		                     				<fo:block linefeed-treatment="preserve" >&#xA;</fo:block>
																		                     				<fo:block linefeed-treatment="preserve" >&#xA;</fo:block>
																		                     				<fo:block text-align="center" font-size="12pt" font-weight="bold"><#if convBalance!= 0>${convBalance?if_exists}<#else>&#160;</#if></fo:block>
																		                     			</fo:table-cell>
																		                     			<fo:table-cell >
																		                     				<fo:block linefeed-treatment="preserve" >&#xA;</fo:block>
																		                     				<fo:block linefeed-treatment="preserve" >&#xA;</fo:block>
																		                     				<fo:block text-align="center" font-size="12pt" font-weight="bold"><#if mrgBalance!= 0>${mrgBalance?if_exists}<#else>&#160;</#if></fo:block>
																		                     			</fo:table-cell>
																		                     			<fo:table-cell >
																		                     				<fo:block linefeed-treatment="preserve" >&#xA;</fo:block>
																		                     				<fo:block linefeed-treatment="preserve" >&#xA;</fo:block>
																		                     				<fo:block text-align="center" font-size="12pt" font-weight="bold"><#if gpfBalance!= 0>${gpfBalance?if_exists}<#else>&#160;</#if></fo:block>
																		                     			</fo:table-cell>
																		                     			<fo:table-cell >
																		                     				<fo:block linefeed-treatment="preserve" >&#xA;</fo:block>
																		                     				<fo:block linefeed-treatment="preserve" >&#xA;</fo:block>
																		                     				<fo:block text-align="center" font-size="12pt" font-weight="bold"><#if eduBalance!= 0>${eduBalance?if_exists}<#else>&#160;</#if></fo:block>
																		                     			</fo:table-cell>
																		                     			<fo:table-cell >
																		                     				<fo:block linefeed-treatment="preserve" >&#xA;</fo:block>
																		                     				<fo:block linefeed-treatment="preserve" >&#xA;</fo:block>
																		                     				<fo:block text-align="center" font-size="12pt" font-weight="bold"><#if medicalBalance!= 0>${medicalBalance?if_exists}<#else>&#160;</#if></fo:block>
																		                     			</fo:table-cell>
																		                     			<fo:table-cell >
																		                     				<fo:block linefeed-treatment="preserve" >&#xA;</fo:block>
																		                     				<fo:block linefeed-treatment="preserve" >&#xA;</fo:block>
																		                     				<fo:block text-align="center" font-size="12pt" font-weight="bold"><#if payAdvBalance!= 0>${payAdvBalance?if_exists}<#else>&#160;</#if></fo:block>
																		                     			</fo:table-cell>
																		                     			<fo:table-cell >
																		                     				<fo:block linefeed-treatment="preserve" >&#xA;</fo:block>
																		                     				<#if SBHBalance!= 0>
																			                     				<fo:block text-align="center" font-size="12pt" font-weight="bold">SBH</fo:block>
																			                     				<fo:block text-align="center" font-size="12pt" font-weight="bold">${SBHBalance?if_exists}</fo:block>
																			                     			<#else>
																			                     				<fo:block linefeed-treatment="preserve" >&#xA;</fo:block>
																			                     				<fo:block text-align="center" font-size="12pt" font-weight="bold">&#160;</fo:block>
																			                     			</#if>
																		                     			</fo:table-cell>
																		                     			<fo:table-cell >
																		                     				<fo:block linefeed-treatment="preserve" >&#xA;</fo:block>
																		                     				<#if canaraBalance!= 0>
																			                     				<fo:block text-align="center" font-size="12pt" font-weight="bold">CANARA</fo:block>
																			                     				<fo:block text-align="center" font-size="12pt" font-weight="bold">${canaraBalance?if_exists}</fo:block>
																			                     			<#else>
																			                     				<fo:block linefeed-treatment="preserve" >&#xA;</fo:block>
																			                     				<fo:block text-align="center" font-size="12pt" font-weight="bold">&#160;</fo:block>
																			                     			</#if>
																		                     			</fo:table-cell>
																		                     			<fo:table-cell >
																		                     				<fo:block linefeed-treatment="preserve" >&#xA;</fo:block>
																		                     				<#if courtBalance!= 0>
																			                     				<fo:block text-align="center" font-size="12pt" font-weight="bold">COURT</fo:block>
																			                     				<fo:block text-align="center" font-size="12pt" font-weight="bold">${courtBalance?if_exists}</fo:block>
																			                     			<#else>
																			                     				<fo:block linefeed-treatment="preserve" >&#xA;</fo:block>
																			                     				<fo:block text-align="center" font-size="12pt" font-weight="bold">&#160;</fo:block>
																			                     			</#if>
																		                     			</fo:table-cell>
																		                     			<fo:table-cell >
																		                     				<fo:block linefeed-treatment="preserve" >&#xA;</fo:block>
																		                     				<#if otherBalance!= 0>
																			                     				<fo:block text-align="center" font-size="12pt" font-weight="bold">OTHER</fo:block>
																			                     				<fo:block text-align="center" font-size="12pt" font-weight="bold">${otherBalance?if_exists}</fo:block>
																			                     			<#else>
																			                     				<fo:block linefeed-treatment="preserve" >&#xA;</fo:block>
																			                     				<fo:block text-align="center" font-size="12pt" font-weight="bold">&#160;</fo:block>
																			                     			</#if>
																		                     			</fo:table-cell> 
																	                     			</fo:table-row>
																	                           </fo:table-body>
																	                     	</fo:table>
																	                 	</fo:block>
																	                </fo:table-cell>
														                      	</fo:table-row>
														                      	<fo:table-row >
								                     								<fo:table-cell >
													                     				<fo:block font-family="Courier,monospace">
												                     						<fo:table>
														                      					<fo:table-column column-width="60pt"/>
												                								<fo:table-column column-width="55pt"/>
												                								<fo:table-column column-width="55pt"/>
												                								<fo:table-column column-width="55pt"/>
												                								<fo:table-column column-width="55pt"/>
												                								<fo:table-column column-width="55pt"/>
												                								<fo:table-column column-width="55pt"/>
												                								<fo:table-column column-width="55pt"/>
												                								<fo:table-column column-width="55pt"/>
												                								<fo:table-column column-width="55pt"/>
												                								<fo:table-column column-width="55pt"/>
												                								<fo:table-column column-width="57pt"/>
												                								<fo:table-column column-width="71pt"/>
														                      					<fo:table-body> 
																	                     			<fo:table-row >
																	                     				<fo:table-cell >
																		                     				<fo:block linefeed-treatment="preserve" >&#xA;</fo:block>
																		                     				<fo:block text-align="center" font-size="12pt" font-weight="bold"><#if payRollHeader.getValue().get("PAYROL_BEN_SALARY")?has_content>${payRollHeader.getValue().get("PAYROL_BEN_SALARY")?if_exists}<#else>&#160;</#if></fo:block>
																		                     				<#if payRollHeader.getValue().get("PAYROL_BEN_SALARY")?has_content>
																		                     					<#assign totalEarnings=totalEarnings+payRollHeader.getValue().get("PAYROL_BEN_SALARY")>
																		                     				</#if>
																		                     			</fo:table-cell>
																		                     			<fo:table-cell >
																		                     				<fo:block linefeed-treatment="preserve" >&#xA;</fo:block>
																		                     				<fo:block text-align="center" font-size="12pt" font-weight="bold"><#if payRollHeader.getValue().get("PAYROL_BEN_PNLPAY")?has_content>${payRollHeader.getValue().get("PAYROL_BEN_PNLPAY")?if_exists}<#else>&#160;</#if></fo:block>
																		                     				<#if payRollHeader.getValue().get("PAYROL_BEN_PNLPAY")?has_content>
																		                     					<#assign totalEarnings=totalEarnings+payRollHeader.getValue().get("PAYROL_BEN_PNLPAY")>
																		                     				</#if>
																		                     			</fo:table-cell>
																		                     			<fo:table-cell >
																		                     				<fo:block linefeed-treatment="preserve" >&#xA;</fo:block>
																		                     				<fo:block text-align="center" font-size="12pt" font-weight="bold"><#if payRollHeader.getValue().get("PAYROL_BEN_SPLPAY")?has_content>${payRollHeader.getValue().get("PAYROL_BEN_SPLPAY")?if_exists}<#else>&#160;</#if></fo:block>
																		                     				<#if payRollHeader.getValue().get("PAYROL_BEN_SPLPAY")?has_content>
																		                     					<#assign totalEarnings=totalEarnings+payRollHeader.getValue().get("PAYROL_BEN_SPLPAY")>
																		                     				</#if>
																		                     			</fo:table-cell>
																		                     			<fo:table-cell >
																		                     				<fo:block linefeed-treatment="preserve" >&#xA;</fo:block>
																		                     				<fo:block text-align="center" font-size="12pt" font-weight="bold"><#if payRollHeader.getValue().get("PAYROL_BEN_DA")?has_content>${payRollHeader.getValue().get("PAYROL_BEN_DA")?if_exists}<#else>&#160;</#if></fo:block>
																		                     				<#if payRollHeader.getValue().get("PAYROL_BEN_DA")?has_content>
																		                     					<#assign totalEarnings=totalEarnings+payRollHeader.getValue().get("PAYROL_BEN_DA")>
																		                     				</#if>
																		                     			</fo:table-cell>
																		                     			<fo:table-cell >
																		                     				<fo:block linefeed-treatment="preserve" >&#xA;</fo:block>
																		                     				<fo:block text-align="center" font-size="12pt" font-weight="bold"><#if payRollHeader.getValue().get("PAYROL_BEN_HRA")?has_content>${payRollHeader.getValue().get("PAYROL_BEN_HRA")?if_exists}<#else>&#160;</#if></fo:block>
																		                     				<#if payRollHeader.getValue().get("PAYROL_BEN_HRA")?has_content>
																		                     					<#assign totalEarnings=totalEarnings+payRollHeader.getValue().get("PAYROL_BEN_HRA")>
																		                     				</#if>
																		                     			</fo:table-cell>
																		                     			<fo:table-cell >
																		                     				<fo:block linefeed-treatment="preserve" >&#xA;</fo:block>
																		                     				<fo:block text-align="center" font-size="12pt" font-weight="bold"><#if payRollHeader.getValue().get("PAYROL_BEN_CCA")?has_content>${payRollHeader.getValue().get("PAYROL_BEN_CCA")?if_exists}<#else>&#160;</#if></fo:block>
																		                     				<#if payRollHeader.getValue().get("PAYROL_BEN_CCA")?has_content>
																		                     					<#assign totalEarnings=totalEarnings+payRollHeader.getValue().get("PAYROL_BEN_CCA")>
																		                     				</#if>
																		                     			</fo:table-cell>
																		                     			<fo:table-cell >
																		                     				<fo:block linefeed-treatment="preserve" >&#xA;</fo:block>
																		                     				<fo:block text-align="center" font-size="12pt" font-weight="bold"><#if payRollHeader.getValue().get("PAYROL_BEN_IR")?has_content>${payRollHeader.getValue().get("PAYROL_BEN_IR")?if_exists}<#else>&#160;</#if></fo:block>
																		                     				<#if payRollHeader.getValue().get("PAYROL_BEN_IR")?has_content>
																		                     					<#assign totalEarnings=totalEarnings+payRollHeader.getValue().get("PAYROL_BEN_IR")>
																		                     				</#if>
																		                     			</fo:table-cell>
																		                     			<fo:table-cell >
																		                     				<fo:block linefeed-treatment="preserve" >&#xA;</fo:block>
																		                     				<fo:block text-align="center" font-size="12pt" font-weight="bold"><#if payRollHeader.getValue().get("PAYROL_BEN_SPLALW")?has_content>${payRollHeader.getValue().get("PAYROL_BEN_SPLALW")?if_exists}<#else>&#160;</#if></fo:block>
																		                     				<#if payRollHeader.getValue().get("PAYROL_BEN_SPLALW")?has_content>
																		                     					<#assign totalEarnings=totalEarnings+payRollHeader.getValue().get("PAYROL_BEN_SPLALW")>
																		                     				</#if>
																		                     			</fo:table-cell>
																		                     			<fo:table-cell >
																		                     				<fo:block linefeed-treatment="preserve" >&#xA;</fo:block>
																		                     				<fo:block text-align="center" font-size="12pt" font-weight="bold"><#if payRollHeader.getValue().get("PAYROL_BEN_TELALLO")?has_content>${payRollHeader.getValue().get("PAYROL_BEN_TELALLO")?if_exists}<#else>&#160;</#if></fo:block>
																		                     				<#if payRollHeader.getValue().get("PAYROL_BEN_TELALLO")?has_content>
																		                     					<#assign totalEarnings=totalEarnings+payRollHeader.getValue().get("PAYROL_BEN_TELALLO")>
																		                     				</#if>
																		                     			</fo:table-cell>
																		                     			<fo:table-cell >
																		                     				<fo:block linefeed-treatment="preserve" >&#xA;</fo:block>
																		                     				<fo:block text-align="center" font-size="12pt" font-weight="bold"><#if payRollHeader.getValue().get("PAYROL_BEN_NDALW")?has_content>${payRollHeader.getValue().get("PAYROL_BEN_NDALW")?if_exists}<#else>&#160;</#if></fo:block>
																		                     				<#if payRollHeader.getValue().get("PAYROL_BEN_NDALW")?has_content>
																		                     					<#assign totalEarnings=totalEarnings+payRollHeader.getValue().get("PAYROL_BEN_NDALW")>
																		                     				</#if>
																		                     			</fo:table-cell>
																		                     			<fo:table-cell >
																		                     				<fo:block linefeed-treatment="preserve" >&#xA;</fo:block>
																		                     				<fo:block text-align="center" font-size="12pt" font-weight="bold"><#if payRollHeader.getValue().get("PAYROL_BEN_WASHALW")?has_content>${payRollHeader.getValue().get("PAYROL_BEN_WASHALW")?if_exists}<#else>&#160;</#if></fo:block>
																		                     				<#if payRollHeader.getValue().get("PAYROL_BEN_WASHALW")?has_content>
																		                     					<#assign totalEarnings=totalEarnings+payRollHeader.getValue().get("PAYROL_BEN_WASHALW")>
																		                     				</#if>
																		                     			</fo:table-cell>
																		                     			<fo:table-cell >
																		                     				<fo:block linefeed-treatment="preserve" >&#xA;</fo:block>
																		                     				<fo:block text-align="center" font-size="12pt" font-weight="bold"><#if payRollHeader.getValue().get("PAYROL_BEN_MEDALW")?has_content>${payRollHeader.getValue().get("PAYROL_BEN_MEDALW")?if_exists}<#else>&#160;</#if></fo:block>
																		                     				<#if payRollHeader.getValue().get("PAYROL_BEN_MEDALW")?has_content>
																		                     					<#assign totalEarnings=totalEarnings+payRollHeader.getValue().get("PAYROL_BEN_MEDALW")>
																		                     				</#if>
																		                     			</fo:table-cell>
																		                     			<fo:table-cell >
																		                     				<fo:block linefeed-treatment="preserve" >&#xA;</fo:block>
																		                     				<fo:block text-align="center" font-size="12pt" font-weight="bold">${(Static["org.ofbiz.base.util.UtilDateTime"].toDateString(timePeriodEnd, "MM/yyyy")).toUpperCase()}</fo:block>
																		                     			</fo:table-cell>
																	                     			</fo:table-row>
																	                           </fo:table-body>
																	                     	</fo:table>
																	                 	</fo:block>
														                            </fo:table-cell>
														                      	</fo:table-row>
														                      	<fo:table-row >
								                     								<fo:table-cell >
													                     				<fo:block font-family="Courier,monospace">
												                     						<fo:table>
														                      					<fo:table-column column-width="60pt"/>
												                								<fo:table-column column-width="55pt"/>
												                								<fo:table-column column-width="55pt"/>
												                								<fo:table-column column-width="55pt"/>
												                								<fo:table-column column-width="55pt"/>
												                								<fo:table-column column-width="55pt"/>
												                								<fo:table-column column-width="55pt"/>
												                								<fo:table-column column-width="55pt"/>
												                								<fo:table-column column-width="55pt"/>
												                								<fo:table-column column-width="55pt"/>
												                								<fo:table-column column-width="55pt"/>
												                								<fo:table-column column-width="57pt"/>
												                								<fo:table-column column-width="71pt"/>
														                      					<fo:table-body> 
																	                     			<fo:table-row >
																	                     				<fo:table-cell >
																		                     				<fo:block linefeed-treatment="preserve" >&#xA;</fo:block>
																		                     				<fo:block linefeed-treatment="preserve" >&#xA;</fo:block>
																		                     				<fo:block text-align="center" font-size="12pt" font-weight="bold"><#if payRollHeader.getValue().get("PAYROL_BEN_OTALW")?has_content>${payRollHeader.getValue().get("PAYROL_BEN_OTALW")?if_exists}<#else>&#160;</#if></fo:block>
																		                     				<#if payRollHeader.getValue().get("PAYROL_BEN_OTALW")?has_content>
																		                     					<#assign totalEarnings=totalEarnings+payRollHeader.getValue().get("PAYROL_BEN_OTALW")>
																		                     				</#if>
																		                     			</fo:table-cell>
																		                     			<fo:table-cell >
																		                     				<fo:block linefeed-treatment="preserve" >&#xA;</fo:block>
																		                     				<fo:block linefeed-treatment="preserve" >&#xA;</fo:block>
																		                     				<fo:block text-align="center" font-size="12pt" font-weight="bold"><#if payRollHeader.getValue().get("PAYROL_BEN_HDALW")?has_content>${payRollHeader.getValue().get("PAYROL_BEN_HDALW")?if_exists}<#else>&#160;</#if></fo:block>
																		                     				<#if payRollHeader.getValue().get("PAYROL_BEN_HDALW")?has_content>
																		                     					<#assign totalEarnings=totalEarnings+payRollHeader.getValue().get("PAYROL_BEN_HDALW")>
																		                     				</#if>
																		                     			</fo:table-cell>
																		                     			<fo:table-cell >
																		                     				<fo:block linefeed-treatment="preserve" >&#xA;</fo:block>
																		                     				<fo:block linefeed-treatment="preserve" >&#xA;</fo:block>
																		                     				<fo:block text-align="center" font-size="12pt" font-weight="bold"><#if payRollHeader.getValue().get("PAYROL_BEN_OPALW")?has_content>${payRollHeader.getValue().get("PAYROL_BEN_OPALW")?if_exists}<#else>&#160;</#if></fo:block>
																		                     				<#if payRollHeader.getValue().get("PAYROL_BEN_OPALW")?has_content>
																		                     					<#assign totalEarnings=totalEarnings+payRollHeader.getValue().get("PAYROL_BEN_OPALW")>
																		                     				</#if>
																		                     			</fo:table-cell>
																		                     			<fo:table-cell >
																		                     				<fo:block linefeed-treatment="preserve" >&#xA;</fo:block>
																		                     				<fo:block linefeed-treatment="preserve" >&#xA;</fo:block>
																		                     				<fo:block text-align="center" font-size="12pt" font-weight="bold"><#if payRollHeader.getValue().get("PAYROL_BEN_ICALW")?has_content>${payRollHeader.getValue().get("PAYROL_BEN_ICALW")?if_exists}<#else>&#160;</#if></fo:block>
																		                     				<#if payRollHeader.getValue().get("PAYROL_BEN_ICALW")?has_content>
																		                     					<#assign totalEarnings=totalEarnings+payRollHeader.getValue().get("PAYROL_BEN_ICALW")>
																		                     				</#if>
																		                     			</fo:table-cell>
																		                     			<fo:table-cell >
																		                     				<fo:block linefeed-treatment="preserve" >&#xA;</fo:block>
																		                     				<fo:block linefeed-treatment="preserve" >&#xA;</fo:block>
																		                     				<fo:block text-align="center" font-size="12pt" font-weight="bold"><#if payRollHeader.getValue().get("PAYROL_BEN_RISKALW")?has_content>${payRollHeader.getValue().get("PAYROL_BEN_RISKALW")?if_exists}<#else>&#160;</#if></fo:block>
																		                     				<#if payRollHeader.getValue().get("PAYROL_BEN_RISKALW")?has_content>
																		                     					<#assign totalEarnings=totalEarnings+payRollHeader.getValue().get("PAYROL_BEN_RISKALW")>
																		                     				</#if>
																		                     			</fo:table-cell>
																		                     			<fo:table-cell >
																		                     				<fo:block linefeed-treatment="preserve" >&#xA;</fo:block>
																		                     				<fo:block linefeed-treatment="preserve" >&#xA;</fo:block>
																		                     				<fo:block text-align="center" font-size="12pt" font-weight="bold"><#if payRollHeader.getValue().get("PAYROL_BEN_SUPARRS")?has_content>${payRollHeader.getValue().get("PAYROL_BEN_SUPARRS")?if_exists}<#else>&#160;</#if></fo:block>
																		                     				<#if payRollHeader.getValue().get("PAYROL_BEN_SUPARRS")?has_content>
																		                     					<#assign totalEarnings=totalEarnings+payRollHeader.getValue().get("PAYROL_BEN_SUPARRS")>
																		                     				</#if>
																		                     			</fo:table-cell>
																		                     			<fo:table-cell >
																		                     				<fo:block linefeed-treatment="preserve" >&#xA;</fo:block>
																		                     				<fo:block linefeed-treatment="preserve" >&#xA;</fo:block>
																		                     				<fo:block text-align="center" font-size="12pt" font-weight="bold"><#if payRollHeader.getValue().get("PAYROL_BEN_MISCONE")?has_content>${payRollHeader.getValue().get("PAYROL_BEN_MISCONE")?if_exists}<#else>&#160;</#if></fo:block>
																		                     				<#if payRollHeader.getValue().get("PAYROL_BEN_MISCONE")?has_content>
																		                     					<#assign totalEarnings=totalEarnings+payRollHeader.getValue().get("PAYROL_BEN_MISCONE")>
																		                     				</#if>
																		                     			</fo:table-cell>
																		                     			<fo:table-cell >
																		                     				<fo:block linefeed-treatment="preserve" >&#xA;</fo:block>
																		                     				<fo:block linefeed-treatment="preserve" >&#xA;</fo:block>
																		                     				<fo:block text-align="center" font-size="12pt" font-weight="bold"><#if payRollHeader.getValue().get("PAYROL_BEN_MISCTWO")?has_content>${payRollHeader.getValue().get("PAYROL_BEN_MISCTWO")?if_exists}<#else>&#160;</#if></fo:block>
																		                     				<#if payRollHeader.getValue().get("PAYROL_BEN_MISCTWO")?has_content>
																		                     					<#assign totalEarnings=totalEarnings+payRollHeader.getValue().get("PAYROL_BEN_MISCTWO")>
																		                     				</#if>
																		                     			</fo:table-cell>
																		                     			<fo:table-cell >
																		                     				<fo:block linefeed-treatment="preserve" >&#xA;</fo:block>
																		                     				<fo:block linefeed-treatment="preserve" >&#xA;</fo:block>
																		                     				<fo:block text-align="center" font-size="12pt" font-weight="bold"><#if payRollHeader.getValue().get("PAYROL_BEN_MISCTHREE")?has_content>${payRollHeader.getValue().get("PAYROL_BEN_MISCTHREE")?if_exists}<#else>&#160;</#if></fo:block>
																		                     				<#if payRollHeader.getValue().get("PAYROL_BEN_MISCTHREE")?has_content>
																		                     					<#assign totalEarnings=totalEarnings+payRollHeader.getValue().get("PAYROL_BEN_MISCTHREE")>
																		                     				</#if>
																		                     			</fo:table-cell>
																		                     			<fo:table-cell >
																		                     				<fo:block linefeed-treatment="preserve" >&#xA;</fo:block>
																		                     				<fo:block linefeed-treatment="preserve" >&#xA;</fo:block>
																		                     				<fo:block text-align="center" font-size="12pt" font-weight="bold"><#if payRollHeader.getValue().get("PAYROL_BEN_CONALW")?has_content>${payRollHeader.getValue().get("PAYROL_BEN_CONALW")?if_exists}<#else>&#160;</#if></fo:block>
																		                     				<#if payRollHeader.getValue().get("PAYROL_BEN_CONALW")?has_content>
																		                     					<#assign totalEarnings=totalEarnings+payRollHeader.getValue().get("PAYROL_BEN_CONALW")>
																		                     				</#if>
																		                     			</fo:table-cell>
																		                     			<fo:table-cell >
																		                     				<fo:block linefeed-treatment="preserve" >&#xA;</fo:block>
																		                     				<fo:block linefeed-treatment="preserve" >&#xA;</fo:block>
																		                     				<fo:block text-align="center" font-size="12pt" font-weight="bold"><#if payRollHeader.getValue().get("PAYROL_BEN_CBALW")?has_content>${payRollHeader.getValue().get("PAYROL_BEN_CBALW")?if_exists}<#else>&#160;</#if></fo:block>
																		                     				<#if payRollHeader.getValue().get("PAYROL_BEN_CBALW")?has_content>
																		                     					<#assign totalEarnings=totalEarnings+payRollHeader.getValue().get("PAYROL_BEN_CBALW")>
																		                     				</#if>
																		                     			</fo:table-cell>
																		                     			<fo:table-cell >
																		                     				<fo:block linefeed-treatment="preserve" >&#xA;</fo:block>
																		                     				<fo:block linefeed-treatment="preserve" >&#xA;</fo:block>
																		                     				<fo:block text-align="center" font-size="12pt" font-weight="bold">&#160;</fo:block>
																		                     			</fo:table-cell>
																		                     			<fo:table-cell >
																		                     				<fo:block linefeed-treatment="preserve" >&#xA;</fo:block>
																		                     				<fo:block linefeed-treatment="preserve" >&#xA;</fo:block>
																		                     				<fo:block text-align="center" font-size="12pt" font-weight="bold">${(noOfPayableDays?if_exists)}  </fo:block>
																		                     			</fo:table-cell>
																	                     			</fo:table-row>
																	                           </fo:table-body>
																	                     	</fo:table>
																	                 	</fo:block>
														                            </fo:table-cell>
														                      	</fo:table-row>
														                      	<fo:table-row >
								                     								<fo:table-cell >
													                     				<fo:block font-family="Courier,monospace">
												                     						<fo:table>
														                      					<fo:table-column column-width="60pt"/>
												                								<fo:table-column column-width="55pt"/>
												                								<fo:table-column column-width="55pt"/>
												                								<fo:table-column column-width="55pt"/>
												                								<fo:table-column column-width="55pt"/>
												                								<fo:table-column column-width="55pt"/>
												                								<fo:table-column column-width="55pt"/>
												                								<fo:table-column column-width="55pt"/>
												                								<fo:table-column column-width="55pt"/>
												                								<fo:table-column column-width="55pt"/>
												                								<fo:table-column column-width="55pt"/>
												                								<fo:table-column column-width="57pt"/>
												                								<fo:table-column column-width="71pt"/>
														                      					<fo:table-body> 
																	                     			<fo:table-row >
																	                     				<fo:table-cell >
																		                     				<fo:block linefeed-treatment="preserve" >&#xA;</fo:block>
																		                     				<fo:block text-align="center" font-size="12pt" font-weight="bold"><#if payRollHeader.getValue().get("PAYROL_DD_EPF")?has_content>${(payRollHeader.getValue().get("PAYROL_DD_EPF")*(-1))?if_exists}<#else>&#160;</#if></fo:block>
																		                     				<#if payRollHeader.getValue().get("PAYROL_DD_EPF")?has_content>
																		                     					<#assign totalDeductions=totalDeductions+(payRollHeader.getValue().get("PAYROL_DD_EPF")*(-1))>
																		                     				</#if>
																		                     			</fo:table-cell>
																		                     			<fo:table-cell >
																		                     				<fo:block linefeed-treatment="preserve" >&#xA;</fo:block>
																		                     				<fo:block text-align="center" font-size="12pt" font-weight="bold"><#if payRollHeader.getValue().get("PAYROL_DD_APGLIF")?has_content>${(payRollHeader.getValue().get("PAYROL_DD_APGLIF")*(-1))?if_exists}<#else>&#160;</#if></fo:block>
																		                     				<#if payRollHeader.getValue().get("PAYROL_DD_APGLIF")?has_content>
																		                     					<#assign totalDeductions=totalDeductions+(payRollHeader.getValue().get("PAYROL_DD_APGLIF")*(-1))>
																		                     				</#if>
																		                     			</fo:table-cell>
																		                     			<fo:table-cell >
																		                     				<fo:block linefeed-treatment="preserve" >&#xA;</fo:block>
																		                     				<fo:block text-align="center" font-size="12pt" font-weight="bold"><#if payRollHeader.getValue().get("PAYROL_DD_VOLPF")?has_content>${(payRollHeader.getValue().get("PAYROL_DD_VOLPF")*(-1))?if_exists}<#else>&#160;</#if></fo:block>
																		                     				<#if payRollHeader.getValue().get("PAYROL_DD_VOLPF")?has_content>
																		                     					<#assign totalDeductions=totalDeductions+(payRollHeader.getValue().get("PAYROL_DD_VOLPF")*(-1))>
																		                     				</#if>
																		                     			</fo:table-cell>
																		                     			<fo:table-cell >
																		                     				<fo:block linefeed-treatment="preserve" >&#xA;</fo:block>
																		                     				<fo:block text-align="center" font-size="12pt" font-weight="bold"><#if payRollHeader.getValue().get("PAYROL_DD_GPFLN")?has_content>${(payRollHeader.getValue().get("PAYROL_DD_GPFLN")*(-1))?if_exists}<#else>&#160;</#if></fo:block>
																		                     				<#if payRollHeader.getValue().get("PAYROL_DD_GPFLN")?has_content>
																		                     					<#assign totalDeductions=totalDeductions+(payRollHeader.getValue().get("PAYROL_DD_GPFLN")*(-1))>
																		                     				</#if>
																		                     			</fo:table-cell>
																		                     			<fo:table-cell >
																		                     				<fo:block linefeed-treatment="preserve" >&#xA;</fo:block>
																		                     				<fo:block text-align="center" font-size="12pt" font-weight="bold"><#if payRollHeader.getValue().get("PAYROL_DD_DEDRS01")?has_content>${(payRollHeader.getValue().get("PAYROL_DD_DEDRS01")*(-1))?if_exists}<#else>&#160;</#if></fo:block>
																		                     				<#if payRollHeader.getValue().get("PAYROL_DD_DEDRS01")?has_content>
																		                     					<#assign totalDeductions=totalDeductions+(payRollHeader.getValue().get("PAYROL_DD_DEDRS01")*(-1))>
																		                     				</#if>
																		                     			</fo:table-cell>
																		                     			<fo:table-cell >
																		                     				<fo:block linefeed-treatment="preserve" >&#xA;</fo:block>
																		                     				<fo:block text-align="center" font-size="12pt" font-weight="bold"><#if payRollHeader.getValue().get("PAYROL_DD_EDNADV")?has_content>${(payRollHeader.getValue().get("PAYROL_DD_EDNADV")*(-1))?if_exists}<#else>&#160;</#if></fo:block>
																		                     				<#if payRollHeader.getValue().get("PAYROL_DD_EDNADV")?has_content>
																		                     					<#assign totalDeductions=totalDeductions+(payRollHeader.getValue().get("PAYROL_DD_EDNADV")*(-1))>
																		                     				</#if>
																		                     			</fo:table-cell>
																		                     			<fo:table-cell >
																		                     				<fo:block linefeed-treatment="preserve" >&#xA;</fo:block>
																		                     				<fo:block text-align="center" font-size="12pt" font-weight="bold"><#if payRollHeader.getValue().get("PAYROL_DD_SSS")?has_content>${(payRollHeader.getValue().get("PAYROL_DD_SSS")*(-1))?if_exists}<#else>&#160;</#if></fo:block>
																		                     				<#if payRollHeader.getValue().get("PAYROL_DD_SSS")?has_content>
																		                     					<#assign totalDeductions=totalDeductions+(payRollHeader.getValue().get("PAYROL_DD_SSS")*(-1))>
																		                     				</#if>
																		                     			</fo:table-cell>
																		                     			<fo:table-cell >
																		                     				<fo:block linefeed-treatment="preserve" >&#xA;</fo:block>
																		                     				<fo:block text-align="center" font-size="12pt" font-weight="bold"><#if payRollHeader.getValue().get("PAYROL_DD_IT")?has_content>${(payRollHeader.getValue().get("PAYROL_DD_IT")*(-1))?if_exists}<#else>&#160;</#if></fo:block>
																		                     				<#if payRollHeader.getValue().get("PAYROL_DD_IT")?has_content>
																		                     					<#assign totalDeductions=totalDeductions+(payRollHeader.getValue().get("PAYROL_DD_IT")*(-1))>
																		                     				</#if>
																		                     			</fo:table-cell>
																		                     			<fo:table-cell >
																		                     				<fo:block linefeed-treatment="preserve" >&#xA;</fo:block>
																		                     				<fo:block text-align="center" font-size="12pt" font-weight="bold"><#if payRollHeader.getValue().get("PAYROL_DD_HRR")?has_content>${(payRollHeader.getValue().get("PAYROL_DD_HRR")*(-1))?if_exists}<#else>&#160;</#if></fo:block>
																		                     				<#if payRollHeader.getValue().get("PAYROL_DD_HRR")?has_content>
																		                     					<#assign totalDeductions=totalDeductions+(payRollHeader.getValue().get("PAYROL_DD_HRR")*(-1))>
																		                     				</#if>
																		                     			</fo:table-cell>
																		                     			<fo:table-cell >
																		                     				<fo:block linefeed-treatment="preserve" >&#xA;</fo:block>
																		                     				<fo:block text-align="center" font-size="12pt" font-weight="bold"><#if payRollHeader.getValue().get("PAYROL_DD_ELECT")?has_content>${(payRollHeader.getValue().get("PAYROL_DD_ELECT")*(-1))?if_exists}<#else>&#160;</#if></fo:block>
																		                     				<#if payRollHeader.getValue().get("PAYROL_DD_ELECT")?has_content>
																		                     					<#assign totalDeductions=totalDeductions+(payRollHeader.getValue().get("PAYROL_DD_ELECT")*(-1))>
																		                     				</#if>
																		                     			</fo:table-cell>
																		                     			<fo:table-cell >
																		                     				<fo:block linefeed-treatment="preserve" >&#xA;</fo:block>
																		                     				<fo:block text-align="center" font-size="12pt" font-weight="bold"><#if payRollHeader.getValue().get("PAYROL_DD_WATER")?has_content>${(payRollHeader.getValue().get("PAYROL_DD_WATER")*(-1))?if_exists}<#else>&#160;</#if></fo:block>
																		                     				<#if payRollHeader.getValue().get("PAYROL_DD_WATER")?has_content>
																		                     					<#assign totalDeductions=totalDeductions+(payRollHeader.getValue().get("PAYROL_DD_WATER")*(-1))>
																		                     				</#if>
																		                     			</fo:table-cell>
																		                     			<fo:table-cell >
																		                     				<fo:block linefeed-treatment="preserve" >&#xA;</fo:block>
																		                     				<fo:block text-align="center" font-size="12pt" font-weight="bold"><#if payRollHeader.getValue().get("PAYROL_DD_WELFARE")?has_content>${(payRollHeader.getValue().get("PAYROL_DD_WELFARE")*(-1))?if_exists}<#else>&#160;</#if></fo:block>
																		                     				<#if payRollHeader.getValue().get("PAYROL_DD_WELFARE")?has_content>
																		                     					<#assign totalDeductions=totalDeductions+(payRollHeader.getValue().get("PAYROL_DD_WELFARE")*(-1))>
																		                     				</#if>
																		                     			</fo:table-cell>
																		                     			<fo:table-cell >
																		                     				<fo:block linefeed-treatment="preserve" >&#xA;</fo:block>
																		                     				<fo:block text-align="left" font-size="12pt" font-weight="bold">${totalEarnings?if_exists?string("#0")}</fo:block>
																		                     			</fo:table-cell>
																	                     			</fo:table-row>
																	                           </fo:table-body>
																	                     	</fo:table>
																	                 	</fo:block>
														                            </fo:table-cell>
														                      	</fo:table-row>
														                      	<fo:table-row >
								                     								<fo:table-cell >
													                     				<fo:block font-family="Courier,monospace">
												                     						<fo:table>
														                      					<fo:table-column column-width="60pt"/>
												                								<fo:table-column column-width="55pt"/>
												                								<fo:table-column column-width="55pt"/>
												                								<fo:table-column column-width="55pt"/>
												                								<fo:table-column column-width="55pt"/>
												                								<fo:table-column column-width="55pt"/>
												                								<fo:table-column column-width="55pt"/>
												                								<fo:table-column column-width="55pt"/>
												                								<fo:table-column column-width="55pt"/>
												                								<fo:table-column column-width="55pt"/>
												                								<fo:table-column column-width="55pt"/>
												                								<fo:table-column column-width="57pt"/>
												                								<fo:table-column column-width="71pt"/>
														                      					<fo:table-body> 
																	                     			<fo:table-row >
																	                     				<fo:table-cell >
																		                     				<fo:block linefeed-treatment="preserve" >&#xA;</fo:block>
																		                     				<fo:block linefeed-treatment="preserve" >&#xA;</fo:block>
																		                     				<fo:block text-align="center" font-size="12pt" font-weight="bold"><#if hdfcDedAmount != 0>${hdfcDedAmount?if_exists}<#else>&#160;</#if></fo:block>
																		                     				<#if hdfcDedAmount != 0>
																		                     					<#assign totalDeductions=totalDeductions+(hdfcDedAmount*(-1))>
																		                     				</#if>
																		                     			</fo:table-cell>
																		                     			<fo:table-cell >
																		                     				<fo:block linefeed-treatment="preserve" >&#xA;</fo:block>
																		                     				<fo:block linefeed-treatment="preserve" >&#xA;</fo:block>
																		                     				<fo:block text-align="center" font-size="12pt" font-weight="bold"><#if payRollHeader.getValue().get("PAYROL_DD_FESTADV")?has_content>${(payRollHeader.getValue().get("PAYROL_DD_FESTADV")*(-1))?if_exists}<#else>&#160;</#if></fo:block>
																		                     				<#if payRollHeader.getValue().get("PAYROL_DD_FESTADV")?has_content>
																		                     					<#assign totalDeductions=totalDeductions+(payRollHeader.getValue().get("PAYROL_DD_FESTADV")*(-1))>
																		                     				</#if>
																		                     			</fo:table-cell>
																		                     			<fo:table-cell >
																		                     				<fo:block linefeed-treatment="preserve" >&#xA;</fo:block>
																		                     				<fo:block linefeed-treatment="preserve" >&#xA;</fo:block>
																		                     				<fo:block text-align="center" font-size="12pt" font-weight="bold">&#160;</fo:block>
																		                     			</fo:table-cell>
																		                     			<fo:table-cell >
																		                     				<fo:block linefeed-treatment="preserve" >&#xA;</fo:block>
																		                     				<fo:block linefeed-treatment="preserve" >&#xA;</fo:block>
																		                     				<fo:block text-align="center" font-size="12pt" font-weight="bold"><#if payRollHeader.getValue().get("PAYROL_DD_MRGLN")?has_content>${(payRollHeader.getValue().get("PAYROL_DD_MRGLN")*(-1))?if_exists}<#else>&#160;</#if></fo:block>
																		                     				<#if payRollHeader.getValue().get("PAYROL_DD_MRGLN")?has_content>
																		                     					<#assign totalDeductions=totalDeductions+(payRollHeader.getValue().get("PAYROL_DD_MRGLN")*(-1))>
																		                     				</#if>
																		                     			</fo:table-cell>
																		                     			<fo:table-cell >
																		                     				<fo:block linefeed-treatment="preserve" >&#xA;</fo:block>
																		                     				<fo:block linefeed-treatment="preserve" >&#xA;</fo:block>
																		                     				<fo:block text-align="center" font-size="12pt" font-weight="bold"><#if payRollHeader.getValue().get("PAYROL_DD_PTAX")?has_content>${(payRollHeader.getValue().get("PAYROL_DD_PTAX")*(-1))?if_exists}<#else>&#160;</#if></fo:block>
																		                     				<#if payRollHeader.getValue().get("PAYROL_DD_PTAX")?has_content>
																		                     					<#assign totalDeductions=totalDeductions+(payRollHeader.getValue().get("PAYROL_DD_PTAX")*(-1))>
																		                     				</#if>
																		                     			</fo:table-cell>
																		                     			<fo:table-cell >
																		                     				<fo:block linefeed-treatment="preserve" >&#xA;</fo:block>
																		                     				<fo:block linefeed-treatment="preserve" >&#xA;</fo:block>
																		                     				<fo:block text-align="center" font-size="12pt" font-weight="bold"><#if payRollHeader.getValue().get("PAYROL_DD_MILKCARDS")?has_content>${(payRollHeader.getValue().get("PAYROL_DD_MILKCARDS")*(-1))?if_exists}<#else>&#160;</#if></fo:block>
																		                     				<#if payRollHeader.getValue().get("PAYROL_DD_MILKCARDS")?has_content>
																		                     					<#assign totalDeductions=totalDeductions+(payRollHeader.getValue().get("PAYROL_DD_MILKCARDS")*(-1))>
																		                     				</#if>
																		                     			</fo:table-cell>
																		                     			<fo:table-cell >
																		                     				<fo:block linefeed-treatment="preserve" >&#xA;</fo:block>
																		                     				<fo:block linefeed-treatment="preserve" >&#xA;</fo:block>
																		                     				<fo:block text-align="center" font-size="12pt" font-weight="bold"><#if payRollHeader.getValue().get("PAYROL_DD_MILKDUES")?has_content>${(payRollHeader.getValue().get("PAYROL_DD_MILKDUES")*(-1))?if_exists}<#else>&#160;</#if></fo:block>
																		                     				<#if payRollHeader.getValue().get("PAYROL_DD_MILKDUES")?has_content>
																		                     					<#assign totalDeductions=totalDeductions+(payRollHeader.getValue().get("PAYROL_DD_MILKDUES")*(-1))>
																		                     				</#if>
																		                     			</fo:table-cell>
																		                     			<fo:table-cell >
																		                     				<fo:block linefeed-treatment="preserve" >&#xA;</fo:block>
																		                     				<fo:block linefeed-treatment="preserve" >&#xA;</fo:block>
																		                     				<fo:block text-align="center" font-size="12pt" font-weight="bold"><#if payRollHeader.getValue().get("PAYROL_DD_MEDADV")?has_content>${(payRollHeader.getValue().get("PAYROL_DD_MEDADV")*(-1))?if_exists}<#else>&#160;</#if></fo:block>
																		                     				<#if payRollHeader.getValue().get("PAYROL_DD_MEDADV")?has_content>
																		                     					<#assign totalDeductions=totalDeductions+(payRollHeader.getValue().get("PAYROL_DD_MEDADV")*(-1))>
																		                     				</#if>
																		                     			</fo:table-cell>
																		                     			<fo:table-cell >
																		                     				<fo:block linefeed-treatment="preserve" >&#xA;</fo:block>
																		                     				<fo:block linefeed-treatment="preserve" >&#xA;</fo:block>
																		                     				<fo:block text-align="center" font-size="12pt" font-weight="bold"><#if payRollHeader.getValue().get("PAYROL_DD_DEDRS02")?has_content>${(payRollHeader.getValue().get("PAYROL_DD_DEDRS02")*(-1))?if_exists}<#else>&#160;</#if></fo:block>
																		                     				<#if payRollHeader.getValue().get("PAYROL_DD_DEDRS02")?has_content>
																		                     					<#assign totalDeductions=totalDeductions+(payRollHeader.getValue().get("PAYROL_DD_DEDRS02")*(-1))>
																		                     				</#if>
																		                     			</fo:table-cell>
																		                     			<fo:table-cell >
																		                     				<fo:block linefeed-treatment="preserve" >&#xA;</fo:block>
																		                     				<fo:block linefeed-treatment="preserve" >&#xA;</fo:block>
																		                     				<fo:block text-align="center" font-size="12pt" font-weight="bold"><#if payRollHeader.getValue().get("PAYROL_DD_ESI")?has_content>${(payRollHeader.getValue().get("PAYROL_DD_ESI")*(-1))?if_exists}<#else>&#160;</#if></fo:block>
																		                     				<#if payRollHeader.getValue().get("PAYROL_DD_ESI")?has_content>
																		                     					<#assign totalDeductions=totalDeductions+(payRollHeader.getValue().get("PAYROL_DD_ESI")*(-1))>
																		                     				</#if>
																		                     			</fo:table-cell>
																		                     			<fo:table-cell >
																		                     				<fo:block linefeed-treatment="preserve" >&#xA;</fo:block>
																		                     				<fo:block linefeed-treatment="preserve" >&#xA;</fo:block>
																		                     				<fo:block text-align="center" font-size="12pt" font-weight="bold"><#if payRollHeader.getValue().get("PAYROL_DD_GIS")?has_content>${(payRollHeader.getValue().get("PAYROL_DD_GIS")*(-1))?if_exists}<#else>&#160;</#if></fo:block>
																		                     				<#if payRollHeader.getValue().get("PAYROL_DD_GIS")?has_content>
																		                     					<#assign totalDeductions=totalDeductions+(payRollHeader.getValue().get("PAYROL_DD_GIS")*(-1))>
																		                     				</#if>
																		                     			</fo:table-cell>
																		                     			<fo:table-cell >
																		                     				<fo:block linefeed-treatment="preserve" >&#xA;</fo:block>
																		                     				<fo:block linefeed-treatment="preserve" >&#xA;</fo:block>
																		                     				<fo:block text-align="center" font-size="12pt" font-weight="bold"><#if payRollHeader.getValue().get("PAYROL_DD_DPTDUES")?has_content>${(payRollHeader.getValue().get("PAYROL_DD_DPTDUES")*(-1))?if_exists}<#else>&#160;</#if></fo:block>
																		                     				<#if payRollHeader.getValue().get("PAYROL_DD_DPTDUES")?has_content>
																		                     					<#assign totalDeductions=totalDeductions+(payRollHeader.getValue().get("PAYROL_DD_DPTDUES")*(-1))>
																		                     				</#if>
																		                     			</fo:table-cell>
																		                     			<fo:table-cell >
																		                     				<fo:block linefeed-treatment="preserve" >&#xA;</fo:block>
																		                     				<fo:block linefeed-treatment="preserve" >&#xA;</fo:block>
																		                     				<fo:block text-align="left" font-size="12pt" font-weight="bold">${totalDeductions?if_exists?string("#0.00")}</fo:block>
																		                     			</fo:table-cell>
																	                     			</fo:table-row>
																	                           </fo:table-body>
																	                     	</fo:table>
																	                 	</fo:block>
														                            </fo:table-cell>
														                      	</fo:table-row>
														                      	<fo:table-row >
								                     								<fo:table-cell >
													                     				<fo:block font-family="Courier,monospace">
												                     						<fo:table>
														                      					<fo:table-column column-width="60pt"/>
												                								<fo:table-column column-width="55pt"/>
												                								<fo:table-column column-width="55pt"/>
												                								<fo:table-column column-width="55pt"/>
												                								<fo:table-column column-width="55pt"/>
												                								<fo:table-column column-width="55pt"/>
												                								<fo:table-column column-width="55pt"/>
												                								<fo:table-column column-width="55pt"/>
												                								<fo:table-column column-width="55pt"/>
												                								<fo:table-column column-width="55pt"/>
												                								<fo:table-column column-width="55pt"/>
												                								<fo:table-column column-width="57pt"/>
												                								<fo:table-column column-width="71pt"/>
														                      					<fo:table-body> 
																	                     			<fo:table-row >
																	                     				<fo:table-cell >
																		                     				<fo:block linefeed-treatment="preserve" >&#xA;</fo:block>
																		                     				<fo:block text-align="center" font-size="12pt" font-weight="bold"><#if payRollHeader.getValue().get("PAYROL_DD_DEDRS10")?has_content>${(payRollHeader.getValue().get("PAYROL_DD_DEDRS10")*(-1))?if_exists}<#else>&#160;</#if></fo:block>
																		                     				<#if payRollHeader.getValue().get("PAYROL_DD_DEDRS10")?has_content>
																		                     					<#assign totalDeductions=totalDeductions+(payRollHeader.getValue().get("PAYROL_DD_DEDRS10")*(-1))>
																		                     				</#if>
																		                     			</fo:table-cell>
																		                     			<fo:table-cell >
																		                     				<#if SBHDedAmount != 0>
																			                     				<fo:block text-align="center" font-size="12pt" font-weight="bold">SBH</fo:block>
																			                     				<fo:block text-align="center" font-size="12pt" font-weight="bold">${SBHDedAmount?if_exists}</fo:block>
																			                     				<#if SBHDedAmount!=0>
																			                     					<#assign totalDeductions=totalDeductions+(SBHDedAmount*(-1))>
																			                     				</#if>
																			                     			<#else>
																		                     					<fo:block linefeed-treatment="preserve" >&#xA;</fo:block>
																		                     					<fo:block text-align="center" font-size="12pt" font-weight="bold">&#160;</fo:block>
																			                     			</#if>
																		                     			</fo:table-cell>
																		                     			<fo:table-cell >
																		                     				<#if canaraDedAmount != 0>
																			                     				<fo:block text-align="center" font-size="12pt" font-weight="bold">CANARA</fo:block>
																			                     				<fo:block text-align="center" font-size="12pt" font-weight="bold">${canaraDedAmount?if_exists}</fo:block>
																			                     				<#if canaraDedAmount!=0>
																			                     					<#assign totalDeductions=totalDeductions+(canaraDedAmount*(-1))>
																			                     				</#if>
																			                     			<#else>
																		                     					<fo:block linefeed-treatment="preserve" >&#xA;</fo:block>
																		                     					<fo:block text-align="center" font-size="12pt" font-weight="bold">&#160;</fo:block>
																			                     			</#if>
																		                     			</fo:table-cell>
																		                     			<fo:table-cell >
																		                     				<#if otherDedAmount != 0>
																			                     				<fo:block text-align="center" font-size="12pt" font-weight="bold">OTHER</fo:block>
																			                     				<fo:block text-align="center" font-size="12pt" font-weight="bold">${otherDedAmount?if_exists}</fo:block>
																			                     				<#if otherDedAmount!=0>
																			                     					<#assign totalDeductions=totalDeductions+(otherDedAmount*(-1))>
																			                     				</#if>
																			                     			<#else>
																		                     					<fo:block linefeed-treatment="preserve" >&#xA;</fo:block>
																		                     					<fo:block text-align="center" font-size="12pt" font-weight="bold">&#160;</fo:block>
																			                     			</#if>
																		                     			</fo:table-cell>
																		                     			<fo:table-cell >
																		                     				<fo:block linefeed-treatment="preserve" >&#xA;</fo:block>
																		                     				<fo:block text-align="center" font-size="12pt" font-weight="bold">&#160;</fo:block>
																		                     			</fo:table-cell>
																		                     			<fo:table-cell >
																		                     				<fo:block linefeed-treatment="preserve" >&#xA;</fo:block>
																		                     				<fo:block text-align="center" font-size="12pt" font-weight="bold">&#160;</fo:block>
																		                     			</fo:table-cell>
																		                     			<fo:table-cell >
																		                     				<fo:block linefeed-treatment="preserve" >&#xA;</fo:block>
																		                     				<fo:block text-align="center" font-size="12pt" font-weight="bold">&#160;</fo:block>
																		                     			</fo:table-cell>
																		                     			<fo:table-cell >
																		                     				<fo:block linefeed-treatment="preserve" >&#xA;</fo:block>
																		                     				<fo:block text-align="center" font-size="12pt" font-weight="bold">&#160;</fo:block>
																		                     			</fo:table-cell>
																		                     			<fo:table-cell >
																		                     				<fo:block linefeed-treatment="preserve" >&#xA;</fo:block>
																		                     				<fo:block text-align="center" font-size="12pt" font-weight="bold">&#160;</fo:block>
																		                     			</fo:table-cell>
																		                     			<fo:table-cell >
																		                     				<fo:block linefeed-treatment="preserve" >&#xA;</fo:block>
																		                     				<fo:block text-align="center" font-size="12pt" font-weight="bold">&#160;</fo:block>
																		                     			</fo:table-cell>
																		                     			<fo:table-cell >
																		                     				<fo:block linefeed-treatment="preserve" >&#xA;</fo:block>
																		                     				<fo:block text-align="center" font-size="12pt" font-weight="bold">&#160;</fo:block>
																		                     			</fo:table-cell>
																		                     			<fo:table-cell >
																		                     				<fo:block linefeed-treatment="preserve" >&#xA;</fo:block>
																		                     				<fo:block text-align="center" font-size="12pt" font-weight="bold">${(totalEarnings-totalDeductions)?if_exists?string("#0")}</fo:block>
																		                     			</fo:table-cell>
																		                     			<fo:table-cell >
																		                     				<fo:block linefeed-treatment="preserve" >&#xA;</fo:block>
																		                     				<fo:block text-align="left" font-size="12pt" font-weight="bold">${(totalEarnings-totalDeductions)?if_exists?string("#0.00")}</fo:block>
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
									                      	</fo:table-row>
							                           </fo:table-body>
							                     	</fo:table>
							                 	</fo:block>
				                            </fo:table-cell>
				                      	</fo:table-row>
				                      	<fo:table-row>
					                      	<fo:table-cell >
			                     				<fo:block linefeed-treatment="preserve">&#xA;</fo:block>
			                     			</fo:table-cell>
		                     			</fo:table-row>
		                     			<fo:table-row>
					                      	<fo:table-cell >
			                     				<fo:block linefeed-treatment="preserve">&#xA;</fo:block>
			                     			</fo:table-cell>
		                     			</fo:table-row>
		                           	</fo:table-body>
		                     	</fo:table>
	                 		</fo:block>
	                 	</#if>
	                 	<#if listNo==3>
	                 		<#assign listNo=1>
	                 		<fo:block page-break-after="always"></fo:block>
	                 	<#else>
	                 		<#assign listNo=listNo+1>
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
		                	
    						
    
    
    
    
    
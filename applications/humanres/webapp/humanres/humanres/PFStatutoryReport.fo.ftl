
<#escape x as x?xml>
	<fo:root xmlns:fo="http://www.w3.org/1999/XSL/Format">
    	<fo:layout-master-set>
      		<fo:simple-page-master master-name="main" page-height="12in" page-width="15in"
        		margin-top="0.3in" margin-bottom="1in" margin-left=".3in" margin-right=".3in">
		          <fo:region-body margin-top="1.3in"/>
		          <fo:region-before extent=".5in"/>
		          <fo:region-after extent=".5in"/>
      		</fo:simple-page-master>
    	</fo:layout-master-set>
    	<#if EachEmployeeMap?has_content>
   			<fo:page-sequence master-reference="main">
		        <fo:static-content flow-name="xsl-region-before">
			  		<fo:block font-weight="bold" keep-together="always" font-size="14pt" text-align="center" font-family="Courier,monospace">FOR UNEXEMPTED ESTABLISHMENTS ONLY</fo:block>
					<fo:block font-weight="bold" keep-together="always" font-size="14pt" text-align="center" font-family="Courier,monospace">KARNATAKA MILK FEDERATION LTD</fo:block>
					<fo:block font-weight="bold" keep-together="always" font-size="12pt" text-align="right" font-family="Courier,monospace"> Page No: <fo:page-number/> &#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;</fo:block>
		  			<fo:block font-weight="bold" keep-together="always" font-size="13pt" text-align="right" font-family="Courier,monospace">Monthly Statement of Employees Provident Fund &#160;: ${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(timePeriodStart, "MMMM-yyyy")}&#160;&#160;&#160;&#160;&#160;&#160;&#160;Date : ${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(nowTimestamp, "dd-MMM-yyyy")}&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;</fo:block>
					<fo:block font-weight="bold" keep-together="always" font-size="12pt" text-align="right" font-family="Courier,monospace">&#160;</fo:block>
					<fo:block font-weight="bold" keep-together="always" font-size="11.5pt"  text-align="center" font-family="Courier,monospace">&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;Employees Contribution&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;Employer's Contribution</fo:block>
		  		</fo:static-content>
	       		<fo:flow flow-name="xsl-region-body" font-family="Helvetica">
            		<fo:block >     
            			<#assign noOfLines=1>
            			<fo:table table-layout="fixed" width="50%" >
							<fo:table-column column-width="40pt"/>
							<fo:table-column column-width="135pt"/>
							<fo:table-column column-width="60pt"/>
							<fo:table-column column-width="180pt"/>
							<fo:table-column column-width="70pt"/>
							<fo:table-column column-width="70pt"/>
							<fo:table-column column-width="70pt"/>
							<fo:table-column column-width="60pt"/>
							<fo:table-column column-width="30pt"/>
							<fo:table-column column-width="70pt"/>
							<fo:table-column column-width="70pt"/>
							<fo:table-column column-width="70pt"/>
							<fo:table-column column-width="70pt"/>
							<fo:table-header font-weight="bold" font-size="12pt">		
								<fo:table-cell border-style="solid"><fo:block keep-together="always" text-align="center">Sl No.  </fo:block></fo:table-cell>
								<fo:table-cell border-style="solid"><fo:block keep-together="always" text-align="center"> PF A/c No </fo:block></fo:table-cell>
								<fo:table-cell border-style="solid"><fo:block keep-together="always" text-align="center">Employee</fo:block><fo:block keep-together="always" text-align="center">Code </fo:block></fo:table-cell>
								<fo:table-cell border-style="solid"><fo:block keep-together="always" text-align="center"> Name </fo:block></fo:table-cell>
								<fo:table-cell border-style="solid"><fo:block keep-together="always" text-align="center"> Wages </fo:block></fo:table-cell>
								<fo:table-cell border-style="solid"><fo:block keep-together="always" text-align="center"> E.P.F </fo:block></fo:table-cell> 
								<fo:table-cell border-style="solid"><fo:block keep-together="always" text-align="center"> V.P.F </fo:block></fo:table-cell> 
								<fo:table-cell border-style="solid"><fo:block keep-together="always" text-align="center"> Total </fo:block></fo:table-cell> 
								<fo:table-cell border-style="solid"><fo:block keep-together="always" text-align="center">  </fo:block></fo:table-cell>
								<fo:table-cell border-style="solid"><fo:block keep-together="always" text-align="center"> E.P.F </fo:block></fo:table-cell>
								<fo:table-cell border-style="solid"><fo:block keep-together="always" text-align="center"> Pension </fo:block></fo:table-cell>
								<fo:table-cell border-style="solid"><fo:block keep-together="always" text-align="center"> Total </fo:block></fo:table-cell>    														
								<fo:table-cell border-style="solid"><fo:block keep-together="always" text-align="center"> Total </fo:block></fo:table-cell>						   														
							</fo:table-header>
							<fo:table-body >
								<fo:table-row font-size="12pt">	
			  						<fo:table-cell></fo:table-cell>
								</fo:table-row>
							</fo:table-body>
						</fo:table>
						<fo:table table-layout="fixed" width="50%" >
							<fo:table-column column-width="40pt"/>
							<fo:table-column column-width="135pt"/>
							<fo:table-column column-width="60pt"/>
							<fo:table-column column-width="180pt"/>
							<fo:table-column column-width="70pt"/>
							<fo:table-column column-width="70pt"/>
							<fo:table-column column-width="70pt"/>
							<fo:table-column column-width="60pt"/>
							<fo:table-column column-width="30pt"/>
							<fo:table-column column-width="70pt"/>
							<fo:table-column column-width="70pt"/>
							<fo:table-column column-width="70pt"/>
							<fo:table-column column-width="70pt"/>
							<fo:table-body >
					
								<#assign employeeTotal=0> 
								<#assign employerTotal=0> 
								<#assign slNo=1>
								<#assign pageEmplwage=0>
			             		<#assign pageEmplepf=0>
			             		<#assign pageemployeeTotal=0>
			             		<#assign pageemployerEpf=0>
			             		<#assign pageemployerFpf=0>
			             		<#assign pageemployerTotal=0>
			             		<#assign pageemployerVpf=0>
			             		
			             		<#assign totEmplwage=0>
			             		<#assign totEmplepf=0>
			             		<#assign totemployeeTotal=0>
			             		<#assign totemployerEpf=0>
			             		<#assign totemployerFpf=0>
			             		<#assign totemployerTotal=0>
			             		<#assign totemployerVpf=0>
					
								<#assign employeeMap =EachEmployeeMap.entrySet()>
								<#list employeeMap as employeeMapvalues>
									<#assign employeeTotal=	employeeMapvalues.getValue().get("EmployeesMap").get("epf")?if_exists+employeeMapvalues.getValue().get("EmployersMap").get("employerVpf")?if_exists>	
									<#assign employerTotal=	employeeMapvalues.getValue().get("EmployersMap").get("employerEpf")?if_exists+employeeMapvalues.getValue().get("EmployersMap").get("employerFpf")?if_exists>
								 	<fo:table-row font-size="12pt">	
										<fo:table-cell border-style="solid"><fo:block keep-together="always" text-align="center"> ${slNo} </fo:block></fo:table-cell>
										<fo:table-cell border-style="solid"><fo:block text-align="center"> ${employeeMapvalues.getValue().get("EmployeesMap").get("pfNum")?if_exists} </fo:block></fo:table-cell>
								        <fo:table-cell border-style="solid"><fo:block keep-together="always" text-align="center">${employeeMapvalues.getValue().get("EmployeesMap").get("partyId")?if_exists} </fo:block></fo:table-cell>
								        <fo:table-cell border-style="solid"><fo:block text-align="left">&#160;&#160;${employeeMapvalues.getValue().get("EmployeesMap").get("firstName")?if_exists} ${employeeMapvalues.getValue().get("EmployeesMap").get("lastName")?if_exists}&#160; </fo:block></fo:table-cell>
								        <fo:table-cell border-style="solid"><fo:block keep-together="always" text-align="center">${employeeMapvalues.getValue().get("EmployeesMap").get("wages")?string("0.##")?if_exists}</fo:block></fo:table-cell>
										<#assign pageEmplwage=pageEmplwage+employeeMapvalues.getValue().get("EmployeesMap").get("wages")?if_exists>
										<#assign totEmplwage=totEmplwage+employeeMapvalues.getValue().get("EmployeesMap").get("wages")?if_exists>
										<fo:table-cell border-style="solid"><fo:block keep-together="always" text-align="center"> ${employeeMapvalues.getValue().get("EmployeesMap").get("epf")?string("0.##")?if_exists} </fo:block></fo:table-cell>
								        <#assign pageEmplepf=pageEmplepf+employeeMapvalues.getValue().get("EmployeesMap").get("epf")?if_exists>
										<#assign totEmplepf=totEmplepf+employeeMapvalues.getValue().get("EmployeesMap").get("epf")?if_exists>
										<fo:table-cell border-style="solid"><fo:block keep-together="always" text-align="center">${employeeMapvalues.getValue().get("EmployersMap").get("employerVpf")?if_exists}</fo:block></fo:table-cell>
							        	<#assign pageemployerVpf=pageemployerVpf+employeeMapvalues.getValue().get("EmployersMap").get("employerVpf")?if_exists>
										<#assign totemployerVpf=totemployerVpf+employeeMapvalues.getValue().get("EmployersMap").get("employerVpf")?if_exists>
								        <fo:table-cell border-style="solid"><fo:block keep-together="always" text-align="center">${(employeeMapvalues.getValue().get("EmployeesMap").get("epf")+employeeMapvalues.getValue().get("EmployersMap").get("employerVpf"))?string("0.##")}</fo:block></fo:table-cell>						
										<#assign pageemployeeTotal=pageemployeeTotal+employeeTotal?if_exists>
										<#assign totemployeeTotal=totemployeeTotal+employeeTotal?if_exists>
										<fo:table-cell><fo:block keep-together="always" text-align="right"> </fo:block></fo:table-cell>
								        <fo:table-cell border-style="solid"><fo:block keep-together="always" text-align="center">${employeeMapvalues.getValue().get("EmployersMap").get("employerEpf")?string("0.##")?if_exists}</fo:block></fo:table-cell>						
								        <#assign pageemployerEpf=pageemployerEpf+employeeMapvalues.getValue().get("EmployersMap").get("employerEpf")?if_exists>
										<#assign totemployerEpf=totemployerEpf+employeeMapvalues.getValue().get("EmployersMap").get("employerEpf")?if_exists>
								        <fo:table-cell border-style="solid"><fo:block keep-together="always" text-align="center">${employeeMapvalues.getValue().get("EmployersMap").get("employerFpf")?string("0.##")?if_exists}</fo:block></fo:table-cell>
										<#assign pageemployerFpf=pageemployerFpf+employeeMapvalues.getValue().get("EmployersMap").get("employerFpf")?if_exists>
										<#assign totemployerFpf=totemployerFpf+employeeMapvalues.getValue().get("EmployersMap").get("employerFpf")?if_exists>
										<fo:table-cell border-style="solid"><fo:block keep-together="always" text-align="center">${employerTotal?string("0.##")} </fo:block></fo:table-cell>
								        <#assign pageemployerTotal=pageemployerTotal+employerTotal?if_exists>
										<#assign totemployerTotal=totemployerTotal+employerTotal?if_exists>
								        <fo:table-cell border-style="solid"><fo:block keep-together="always" text-align="center">${(employeeTotal+employerTotal)?string("0.##")}</fo:block></fo:table-cell>
							        	<#assign noOfLines= noOfLines+1>
							        </fo:table-row>
					
									<#assign slNo=slNo+1>
									<#if (noOfLines == 36)>
		                 				<#assign noOfLines=1>
			                 			<fo:table-row font-size="12pt">
			                 				<fo:table-cell border-style="solid"><fo:block keep-together="always" text-align="center">&#160; </fo:block></fo:table-cell>	
											<fo:table-cell border-style="solid"><fo:block keep-together="always" text-align="center" font-weight="bold">Page Total : </fo:block></fo:table-cell>
											<fo:table-cell border-style="solid"><fo:block keep-together="always" text-align="center">&#160; </fo:block></fo:table-cell>
											<fo:table-cell border-style="solid"><fo:block keep-together="always" text-align="center">&#160; </fo:block></fo:table-cell>
									        <fo:table-cell border-style="solid"><fo:block keep-together="always" text-align="center" font-weight="bold">${pageEmplwage?string("0.##")?if_exists}</fo:block></fo:table-cell>
											<fo:table-cell border-style="solid"><fo:block keep-together="always" text-align="center" font-weight="bold"> ${pageEmplepf?string("0.##")?if_exists} </fo:block></fo:table-cell>
											<fo:table-cell border-style="solid"><fo:block keep-together="always" text-align="center" font-weight="bold">${pageemployerVpf?if_exists}</fo:block></fo:table-cell> 
									        <fo:table-cell border-style="solid"><fo:block keep-together="always" text-align="center" font-weight="bold">${pageemployeeTotal?string("0.##")}</fo:block></fo:table-cell>						
											<fo:table-cell><fo:block keep-together="always" text-align="right"> </fo:block></fo:table-cell>
									        <fo:table-cell border-style="solid"><fo:block keep-together="always" text-align="center" font-weight="bold">${pageemployerEpf?string("0.##")?if_exists}</fo:block></fo:table-cell>						
									        <fo:table-cell border-style="solid"><fo:block keep-together="always" text-align="center" font-weight="bold">${pageemployerFpf?string("0.##")?if_exists}</fo:block></fo:table-cell>
											<fo:table-cell border-style="solid"><fo:block keep-together="always" text-align="center" font-weight="bold">${pageemployerTotal?string("0.##")} </fo:block></fo:table-cell>
									        <fo:table-cell border-style="solid"><fo:block keep-together="always" text-align="center" font-weight="bold">${(pageemployeeTotal+pageemployerTotal)?string("0.##")}</fo:block></fo:table-cell>
								        </fo:table-row>
				                 		<#assign pageEmplwage=0>
				                 		<#assign pageEmplepf=0>
				                 		<#assign pageEmplfpf=0>
				                 		<#assign pageemployeeTotal=0>
				                 		<#assign pageemployerEpf=0>
				                 		<#assign pageemployerFpf=0>
				                 		<#assign pageemployerTotal=0>
				                 		<#assign pageemployerVpf=0>
				                     	<fo:table-row>
				                        	<fo:table-cell >	
				                        		<fo:block page-break-after="always"></fo:block>
				                        	</fo:table-cell>
				                        </fo:table-row>
				                    </#if>
								</#list> 
								<fo:table-row font-size="12pt">	
									<fo:table-cell border-style="solid"><fo:block keep-together="always" text-align="center"> &#160;</fo:block></fo:table-cell>
									<fo:table-cell border-style="solid"><fo:block keep-together="always" text-align="center" font-weight="bold">Grand Total : </fo:block></fo:table-cell>
							        <fo:table-cell border-style="solid"><fo:block keep-together="always" text-align="left">&#160; </fo:block></fo:table-cell>
									<fo:table-cell border-style="solid"><fo:block keep-together="always" text-align="center">&#160; </fo:block></fo:table-cell>
							        <fo:table-cell border-style="solid"><fo:block keep-together="always" text-align="center" font-weight="bold">${totEmplwage?string("0.##")?if_exists}</fo:block></fo:table-cell>
									<fo:table-cell border-style="solid"><fo:block keep-together="always" text-align="center" font-weight="bold"> ${totEmplepf?string("0.##")?if_exists} </fo:block></fo:table-cell>
									<fo:table-cell border-style="solid"><fo:block keep-together="always" text-align="center" font-weight="bold">${totemployerVpf?if_exists}</fo:block></fo:table-cell>
							        <fo:table-cell border-style="solid"><fo:block keep-together="always" text-align="center" font-weight="bold">${totemployeeTotal?string("0.##")}</fo:block></fo:table-cell>						
									<fo:table-cell><fo:block keep-together="always" text-align="right"> </fo:block></fo:table-cell>
							        <fo:table-cell border-style="solid"><fo:block keep-together="always" text-align="center" font-weight="bold">${totemployerEpf?string("0.##")?if_exists}</fo:block></fo:table-cell>						
							        <fo:table-cell border-style="solid"><fo:block keep-together="always" text-align="center" font-weight="bold">${totemployerFpf?string("0.##")?if_exists}</fo:block></fo:table-cell>
									<fo:table-cell border-style="solid"><fo:block keep-together="always" text-align="center" font-weight="bold">${totemployerTotal?string("0.##")} </fo:block></fo:table-cell>
							        <fo:table-cell border-style="solid"><fo:block keep-together="always" text-align="center" font-weight="bold">${(totemployeeTotal+totemployerTotal)?string("0.##")}</fo:block></fo:table-cell>
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
 	</fo:root>
</#escape>
<#escape x as x?xml>
	<fo:root xmlns:fo="http://www.w3.org/1999/XSL/Format">
    	<fo:layout-master-set>
      		<fo:simple-page-master master-name="main" page-height="12in" page-width="15in"
        		margin-top="0.3in" margin-bottom="1in" margin-left=".3in" margin-right=".3in">
		          <fo:region-body margin-top="1.3in"/>
		          <fo:region-before extent=".5in"/>
		          <fo:region-after extent=".5in"/>
      		</fo:simple-page-master>
    	</fo:layout-master-set>
    	<#if EachEmployeeMap?has_content>
   			<fo:page-sequence master-reference="main">
		        <fo:static-content flow-name="xsl-region-before">
			  		<fo:block font-weight="bold" keep-together="always" font-size="14pt" text-align="center" font-family="Courier,monospace">FOR UNEXEMPTED ESTABLISHMENTS ONLY</fo:block>
					<fo:block font-weight="bold" keep-together="always" font-size="14pt" text-align="center" font-family="Courier,monospace">KARNATAKA MILK FEDERATION LTD</fo:block>
					<fo:block font-weight="bold" keep-together="always" font-size="12pt" text-align="right" font-family="Courier,monospace"> Page No: <fo:page-number/> &#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;</fo:block>
		  			<fo:block font-weight="bold" keep-together="always" font-size="13pt" text-align="right" font-family="Courier,monospace">Monthly Statement of Employees Provident Fund &#160;: ${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(timePeriodStart, "MMMM-yyyy")}&#160;&#160;&#160;&#160;&#160;&#160;&#160;Date : ${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(nowTimestamp, "dd-MMM-yyyy")}&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;</fo:block>
					<fo:block font-weight="bold" keep-together="always" font-size="12pt" text-align="right" font-family="Courier,monospace">&#160;</fo:block>
					<fo:block font-weight="bold" keep-together="always" font-size="11.5pt"  text-align="center" font-family="Courier,monospace">&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;Employees Contribution&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;Employer's Contribution</fo:block>
		  		</fo:static-content>
	       		<fo:flow flow-name="xsl-region-body" font-family="Helvetica">
            		<fo:block >     
            			<#assign noOfLines=1>
            			<fo:table table-layout="fixed" width="50%" >
							<fo:table-column column-width="40pt"/>
							<fo:table-column column-width="135pt"/>
							<fo:table-column column-width="60pt"/>
							<fo:table-column column-width="180pt"/>
							<fo:table-column column-width="70pt"/>
							<fo:table-column column-width="70pt"/>
							<fo:table-column column-width="70pt"/>
							<fo:table-column column-width="60pt"/>
							<fo:table-column column-width="30pt"/>
							<fo:table-column column-width="70pt"/>
							<fo:table-column column-width="70pt"/>
							<fo:table-column column-width="70pt"/>
							<fo:table-column column-width="70pt"/>
							<fo:table-header font-weight="bold" font-size="12pt">		
								<fo:table-cell border-style="solid"><fo:block keep-together="always" text-align="center">Sl No.  </fo:block></fo:table-cell>
								<fo:table-cell border-style="solid"><fo:block keep-together="always" text-align="center"> PF A/c No </fo:block></fo:table-cell>
								<fo:table-cell border-style="solid"><fo:block keep-together="always" text-align="center">Employee</fo:block><fo:block keep-together="always" text-align="center">Code </fo:block></fo:table-cell>
								<fo:table-cell border-style="solid"><fo:block keep-together="always" text-align="center"> Name </fo:block></fo:table-cell>
								<fo:table-cell border-style="solid"><fo:block keep-together="always" text-align="center"> Wages </fo:block></fo:table-cell>
								<fo:table-cell border-style="solid"><fo:block keep-together="always" text-align="center"> E.P.F </fo:block></fo:table-cell> 
								<fo:table-cell border-style="solid"><fo:block keep-together="always" text-align="center"> V.P.F </fo:block></fo:table-cell> 
								<fo:table-cell border-style="solid"><fo:block keep-together="always" text-align="center"> Total </fo:block></fo:table-cell> 
								<fo:table-cell border-style="solid"><fo:block keep-together="always" text-align="center">  </fo:block></fo:table-cell>
								<fo:table-cell border-style="solid"><fo:block keep-together="always" text-align="center"> E.P.F </fo:block></fo:table-cell>
								<fo:table-cell border-style="solid"><fo:block keep-together="always" text-align="center"> Pension </fo:block></fo:table-cell>
								<fo:table-cell border-style="solid"><fo:block keep-together="always" text-align="center"> Total </fo:block></fo:table-cell>    														
								<fo:table-cell border-style="solid"><fo:block keep-together="always" text-align="center"> Total </fo:block></fo:table-cell>						   														
							</fo:table-header>
							<fo:table-body >
								<fo:table-row font-size="12pt">	
			  						<fo:table-cell></fo:table-cell>
								</fo:table-row>
							</fo:table-body>
						</fo:table>
						<fo:table table-layout="fixed" width="50%" >
							<fo:table-column column-width="40pt"/>
							<fo:table-column column-width="135pt"/>
							<fo:table-column column-width="60pt"/>
							<fo:table-column column-width="180pt"/>
							<fo:table-column column-width="70pt"/>
							<fo:table-column column-width="70pt"/>
							<fo:table-column column-width="70pt"/>
							<fo:table-column column-width="60pt"/>
							<fo:table-column column-width="30pt"/>
							<fo:table-column column-width="70pt"/>
							<fo:table-column column-width="70pt"/>
							<fo:table-column column-width="70pt"/>
							<fo:table-column column-width="70pt"/>
							<fo:table-body >
					
								<#assign employeeTotal=0> 
								<#assign employerTotal=0> 
								<#assign slNo=1>
								<#assign pageEmplwage=0>
			             		<#assign pageEmplepf=0>
			             		<#assign pageemployeeTotal=0>
			             		<#assign pageemployerEpf=0>
			             		<#assign pageemployerFpf=0>
			             		<#assign pageemployerTotal=0>
			             		<#assign pageemployerVpf=0>
			             		
			             		<#assign totEmplwage=0>
			             		<#assign totEmplepf=0>
			             		<#assign totemployeeTotal=0>
			             		<#assign totemployerEpf=0>
			             		<#assign totemployerFpf=0>
			             		<#assign totemployerTotal=0>
			             		<#assign totemployerVpf=0>
					
								<#assign employeeMap =EachEmployeeMap.entrySet()>
								<#list employeeMap as employeeMapvalues>
									<#assign employeeTotal=	employeeMapvalues.getValue().get("EmployeesMap").get("epf")?if_exists+employeeMapvalues.getValue().get("EmployersMap").get("employerVpf")?if_exists>	
									<#assign employerTotal=	employeeMapvalues.getValue().get("EmployersMap").get("employerEpf")?if_exists+employeeMapvalues.getValue().get("EmployersMap").get("employerFpf")?if_exists>
								 	<fo:table-row font-size="12pt">	
										<fo:table-cell border-style="solid"><fo:block keep-together="always" text-align="center"> ${slNo} </fo:block></fo:table-cell>
										<fo:table-cell border-style="solid"><fo:block text-align="center"> ${employeeMapvalues.getValue().get("EmployeesMap").get("pfNum")?if_exists} </fo:block></fo:table-cell>
								        <fo:table-cell border-style="solid"><fo:block keep-together="always" text-align="center">${employeeMapvalues.getValue().get("EmployeesMap").get("partyId")?if_exists} </fo:block></fo:table-cell>
								        <fo:table-cell border-style="solid"><fo:block text-align="left">&#160;&#160;${employeeMapvalues.getValue().get("EmployeesMap").get("firstName")?if_exists} ${employeeMapvalues.getValue().get("EmployeesMap").get("lastName")?if_exists}&#160; </fo:block></fo:table-cell>
								        <fo:table-cell border-style="solid"><fo:block keep-together="always" text-align="center">${employeeMapvalues.getValue().get("EmployeesMap").get("wages")?string("0.##")?if_exists}</fo:block></fo:table-cell>
										<#assign pageEmplwage=pageEmplwage+employeeMapvalues.getValue().get("EmployeesMap").get("wages")?if_exists>
										<#assign totEmplwage=totEmplwage+employeeMapvalues.getValue().get("EmployeesMap").get("wages")?if_exists>
										<fo:table-cell border-style="solid"><fo:block keep-together="always" text-align="center"> ${employeeMapvalues.getValue().get("EmployeesMap").get("epf")?string("0.##")?if_exists} </fo:block></fo:table-cell>
								        <#assign pageEmplepf=pageEmplepf+employeeMapvalues.getValue().get("EmployeesMap").get("epf")?if_exists>
										<#assign totEmplepf=totEmplepf+employeeMapvalues.getValue().get("EmployeesMap").get("epf")?if_exists>
										<fo:table-cell border-style="solid"><fo:block keep-together="always" text-align="center">${employeeMapvalues.getValue().get("EmployersMap").get("employerVpf")?if_exists}</fo:block></fo:table-cell>
							        	<#assign pageemployerVpf=pageemployerVpf+employeeMapvalues.getValue().get("EmployersMap").get("employerVpf")?if_exists>
										<#assign totemployerVpf=totemployerVpf+employeeMapvalues.getValue().get("EmployersMap").get("employerVpf")?if_exists>
								        <fo:table-cell border-style="solid"><fo:block keep-together="always" text-align="center">${(employeeMapvalues.getValue().get("EmployeesMap").get("epf")+employeeMapvalues.getValue().get("EmployersMap").get("employerVpf"))?string("0.##")}</fo:block></fo:table-cell>						
										<#assign pageemployeeTotal=pageemployeeTotal+employeeTotal?if_exists>
										<#assign totemployeeTotal=totemployeeTotal+employeeTotal?if_exists>
										<fo:table-cell><fo:block keep-together="always" text-align="right"> </fo:block></fo:table-cell>
								        <fo:table-cell border-style="solid"><fo:block keep-together="always" text-align="center">${employeeMapvalues.getValue().get("EmployersMap").get("employerEpf")?string("0.##")?if_exists}</fo:block></fo:table-cell>						
								        <#assign pageemployerEpf=pageemployerEpf+employeeMapvalues.getValue().get("EmployersMap").get("employerEpf")?if_exists>
										<#assign totemployerEpf=totemployerEpf+employeeMapvalues.getValue().get("EmployersMap").get("employerEpf")?if_exists>
								        <fo:table-cell border-style="solid"><fo:block keep-together="always" text-align="center">${employeeMapvalues.getValue().get("EmployersMap").get("employerFpf")?string("0.##")?if_exists}</fo:block></fo:table-cell>
										<#assign pageemployerFpf=pageemployerFpf+employeeMapvalues.getValue().get("EmployersMap").get("employerFpf")?if_exists>
										<#assign totemployerFpf=totemployerFpf+employeeMapvalues.getValue().get("EmployersMap").get("employerFpf")?if_exists>
										<fo:table-cell border-style="solid"><fo:block keep-together="always" text-align="center">${employerTotal?string("0.##")} </fo:block></fo:table-cell>
								        <#assign pageemployerTotal=pageemployerTotal+employerTotal?if_exists>
										<#assign totemployerTotal=totemployerTotal+employerTotal?if_exists>
								        <fo:table-cell border-style="solid"><fo:block keep-together="always" text-align="center">${(employeeTotal+employerTotal)?string("0.##")}</fo:block></fo:table-cell>
							        	<#assign noOfLines= noOfLines+1>
							        </fo:table-row>
					
									<#assign slNo=slNo+1>
									<#if (noOfLines == 36)>
		                 				<#assign noOfLines=1>
			                 			<fo:table-row font-size="12pt">
			                 				<fo:table-cell border-style="solid"><fo:block keep-together="always" text-align="center">&#160; </fo:block></fo:table-cell>	
											<fo:table-cell border-style="solid"><fo:block keep-together="always" text-align="center" font-weight="bold">Page Total : </fo:block></fo:table-cell>
											<fo:table-cell border-style="solid"><fo:block keep-together="always" text-align="center">&#160; </fo:block></fo:table-cell>
											<fo:table-cell border-style="solid"><fo:block keep-together="always" text-align="center">&#160; </fo:block></fo:table-cell>
									        <fo:table-cell border-style="solid"><fo:block keep-together="always" text-align="center" font-weight="bold">${pageEmplwage?string("0.##")?if_exists}</fo:block></fo:table-cell>
											<fo:table-cell border-style="solid"><fo:block keep-together="always" text-align="center" font-weight="bold"> ${pageEmplepf?string("0.##")?if_exists} </fo:block></fo:table-cell>
											<fo:table-cell border-style="solid"><fo:block keep-together="always" text-align="center" font-weight="bold">${pageemployerVpf?if_exists}</fo:block></fo:table-cell> 
									        <fo:table-cell border-style="solid"><fo:block keep-together="always" text-align="center" font-weight="bold">${pageemployeeTotal?string("0.##")}</fo:block></fo:table-cell>						
											<fo:table-cell><fo:block keep-together="always" text-align="right"> </fo:block></fo:table-cell>
									        <fo:table-cell border-style="solid"><fo:block keep-together="always" text-align="center" font-weight="bold">${pageemployerEpf?string("0.##")?if_exists}</fo:block></fo:table-cell>						
									        <fo:table-cell border-style="solid"><fo:block keep-together="always" text-align="center" font-weight="bold">${pageemployerFpf?string("0.##")?if_exists}</fo:block></fo:table-cell>
											<fo:table-cell border-style="solid"><fo:block keep-together="always" text-align="center" font-weight="bold">${pageemployerTotal?string("0.##")} </fo:block></fo:table-cell>
									        <fo:table-cell border-style="solid"><fo:block keep-together="always" text-align="center" font-weight="bold">${(pageemployeeTotal+pageemployerTotal)?string("0.##")}</fo:block></fo:table-cell>
								        </fo:table-row>
				                 		<#assign pageEmplwage=0>
				                 		<#assign pageEmplepf=0>
				                 		<#assign pageEmplfpf=0>
				                 		<#assign pageemployeeTotal=0>
				                 		<#assign pageemployerEpf=0>
				                 		<#assign pageemployerFpf=0>
				                 		<#assign pageemployerTotal=0>
				                 		<#assign pageemployerVpf=0>
				                     	<fo:table-row>
				                        	<fo:table-cell >	
				                        		<fo:block page-break-after="always"></fo:block>
				                        	</fo:table-cell>
				                        </fo:table-row>
				                    </#if>
								</#list> 
								<fo:table-row font-size="12pt">	
									<fo:table-cell border-style="solid"><fo:block keep-together="always" text-align="center"> &#160;</fo:block></fo:table-cell>
									<fo:table-cell border-style="solid"><fo:block keep-together="always" text-align="center" font-weight="bold">Grand Total : </fo:block></fo:table-cell>
							        <fo:table-cell border-style="solid"><fo:block keep-together="always" text-align="left">&#160; </fo:block></fo:table-cell>
									<fo:table-cell border-style="solid"><fo:block keep-together="always" text-align="center">&#160; </fo:block></fo:table-cell>
							        <fo:table-cell border-style="solid"><fo:block keep-together="always" text-align="center" font-weight="bold">${totEmplwage?string("0.##")?if_exists}</fo:block></fo:table-cell>
									<fo:table-cell border-style="solid"><fo:block keep-together="always" text-align="center" font-weight="bold"> ${totEmplepf?string("0.##")?if_exists} </fo:block></fo:table-cell>
									<fo:table-cell border-style="solid"><fo:block keep-together="always" text-align="center" font-weight="bold">${totemployerVpf?if_exists}</fo:block></fo:table-cell>
							        <fo:table-cell border-style="solid"><fo:block keep-together="always" text-align="center" font-weight="bold">${totemployeeTotal?string("0.##")}</fo:block></fo:table-cell>						
									<fo:table-cell><fo:block keep-together="always" text-align="right"> </fo:block></fo:table-cell>
							        <fo:table-cell border-style="solid"><fo:block keep-together="always" text-align="center" font-weight="bold">${totemployerEpf?string("0.##")?if_exists}</fo:block></fo:table-cell>						
							        <fo:table-cell border-style="solid"><fo:block keep-together="always" text-align="center" font-weight="bold">${totemployerFpf?string("0.##")?if_exists}</fo:block></fo:table-cell>
									<fo:table-cell border-style="solid"><fo:block keep-together="always" text-align="center" font-weight="bold">${totemployerTotal?string("0.##")} </fo:block></fo:table-cell>
							        <fo:table-cell border-style="solid"><fo:block keep-together="always" text-align="center" font-weight="bold">${(totemployeeTotal+totemployerTotal)?string("0.##")}</fo:block></fo:table-cell>
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
 	</fo:root>
</#escape>
<#escape x as x?xml>
	<fo:root xmlns:fo="http://www.w3.org/1999/XSL/Format">
    	<fo:layout-master-set>
      		<fo:simple-page-master master-name="main" page-height="12in" page-width="15in"
        		margin-top="0.3in" margin-bottom="1in" margin-left=".3in" margin-right=".3in">
		          <fo:region-body margin-top="1.3in"/>
		          <fo:region-before extent=".5in"/>
		          <fo:region-after extent=".5in"/>
      		</fo:simple-page-master>
    	</fo:layout-master-set>
    	<#if EachEmployeeMap?has_content>
   			<fo:page-sequence master-reference="main">
		        <fo:static-content flow-name="xsl-region-before">
			  		<fo:block font-weight="bold" keep-together="always" font-size="14pt" text-align="center" font-family="Courier,monospace">FOR UNEXEMPTED ESTABLISHMENTS ONLY</fo:block>
					<fo:block font-weight="bold" keep-together="always" font-size="14pt" text-align="center" font-family="Courier,monospace">KARNATAKA MILK FEDERATION LTD</fo:block>
					<fo:block font-weight="bold" keep-together="always" font-size="12pt" text-align="right" font-family="Courier,monospace"> Page No: <fo:page-number/> &#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;</fo:block>
		  			<fo:block font-weight="bold" keep-together="always" font-size="13pt" text-align="right" font-family="Courier,monospace">Monthly Statement of Employees Provident Fund &#160;: ${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(timePeriodStart, "MMMM-yyyy")}&#160;&#160;&#160;&#160;&#160;&#160;&#160;Date : ${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(nowTimestamp, "dd-MMM-yyyy")}&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;</fo:block>
					<fo:block font-weight="bold" keep-together="always" font-size="12pt" text-align="right" font-family="Courier,monospace">&#160;</fo:block>
					<fo:block font-weight="bold" keep-together="always" font-size="11.5pt"  text-align="center" font-family="Courier,monospace">&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;Employees Contribution&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;Employer's Contribution</fo:block>
		  		</fo:static-content>
	       		<fo:flow flow-name="xsl-region-body" font-family="Helvetica">
            		<fo:block >     
            			<#assign noOfLines=1>
            			<fo:table table-layout="fixed" width="50%" >
							<fo:table-column column-width="40pt"/>
							<fo:table-column column-width="135pt"/>
							<fo:table-column column-width="60pt"/>
							<fo:table-column column-width="180pt"/>
							<fo:table-column column-width="70pt"/>
							<fo:table-column column-width="70pt"/>
							<fo:table-column column-width="70pt"/>
							<fo:table-column column-width="60pt"/>
							<fo:table-column column-width="30pt"/>
							<fo:table-column column-width="70pt"/>
							<fo:table-column column-width="70pt"/>
							<fo:table-column column-width="70pt"/>
							<fo:table-column column-width="70pt"/>
							<fo:table-header font-weight="bold" font-size="12pt">		
								<fo:table-cell border-style="solid"><fo:block keep-together="always" text-align="center">Sl No.  </fo:block></fo:table-cell>
								<fo:table-cell border-style="solid"><fo:block keep-together="always" text-align="center"> PF A/c No </fo:block></fo:table-cell>
								<fo:table-cell border-style="solid"><fo:block keep-together="always" text-align="center">Employee</fo:block><fo:block keep-together="always" text-align="center">Code </fo:block></fo:table-cell>
								<fo:table-cell border-style="solid"><fo:block keep-together="always" text-align="center"> Name </fo:block></fo:table-cell>
								<fo:table-cell border-style="solid"><fo:block keep-together="always" text-align="center"> Wages </fo:block></fo:table-cell>
								<fo:table-cell border-style="solid"><fo:block keep-together="always" text-align="center"> E.P.F </fo:block></fo:table-cell> 
								<fo:table-cell border-style="solid"><fo:block keep-together="always" text-align="center"> V.P.F </fo:block></fo:table-cell> 
								<fo:table-cell border-style="solid"><fo:block keep-together="always" text-align="center"> Total </fo:block></fo:table-cell> 
								<fo:table-cell border-style="solid"><fo:block keep-together="always" text-align="center">  </fo:block></fo:table-cell>
								<fo:table-cell border-style="solid"><fo:block keep-together="always" text-align="center"> E.P.F </fo:block></fo:table-cell>
								<fo:table-cell border-style="solid"><fo:block keep-together="always" text-align="center"> Pension </fo:block></fo:table-cell>
								<fo:table-cell border-style="solid"><fo:block keep-together="always" text-align="center"> Total </fo:block></fo:table-cell>    														
								<fo:table-cell border-style="solid"><fo:block keep-together="always" text-align="center"> Total </fo:block></fo:table-cell>						   														
							</fo:table-header>
							<fo:table-body >
								<fo:table-row font-size="12pt">	
			  						<fo:table-cell></fo:table-cell>
								</fo:table-row>
							</fo:table-body>
						</fo:table>
						<fo:table table-layout="fixed" width="50%" >
							<fo:table-column column-width="40pt"/>
							<fo:table-column column-width="135pt"/>
							<fo:table-column column-width="60pt"/>
							<fo:table-column column-width="180pt"/>
							<fo:table-column column-width="70pt"/>
							<fo:table-column column-width="70pt"/>
							<fo:table-column column-width="70pt"/>
							<fo:table-column column-width="60pt"/>
							<fo:table-column column-width="30pt"/>
							<fo:table-column column-width="70pt"/>
							<fo:table-column column-width="70pt"/>
							<fo:table-column column-width="70pt"/>
							<fo:table-column column-width="70pt"/>
							<fo:table-body >
					
								<#assign employeeTotal=0> 
								<#assign employerTotal=0> 
								<#assign slNo=1>
								<#assign pageEmplwage=0>
			             		<#assign pageEmplepf=0>
			             		<#assign pageemployeeTotal=0>
			             		<#assign pageemployerEpf=0>
			             		<#assign pageemployerFpf=0>
			             		<#assign pageemployerTotal=0>
			             		<#assign pageemployerVpf=0>
			             		
			             		<#assign totEmplwage=0>
			             		<#assign totEmplepf=0>
			             		<#assign totemployeeTotal=0>
			             		<#assign totemployerEpf=0>
			             		<#assign totemployerFpf=0>
			             		<#assign totemployerTotal=0>
			             		<#assign totemployerVpf=0>
					
								<#assign employeeMap =EachEmployeeMap.entrySet()>
								<#list employeeMap as employeeMapvalues>
									<#assign employeeTotal=	employeeMapvalues.getValue().get("EmployeesMap").get("epf")?if_exists+employeeMapvalues.getValue().get("EmployersMap").get("employerVpf")?if_exists>	
									<#assign employerTotal=	employeeMapvalues.getValue().get("EmployersMap").get("employerEpf")?if_exists+employeeMapvalues.getValue().get("EmployersMap").get("employerFpf")?if_exists>
								 	<fo:table-row font-size="12pt">	
										<fo:table-cell border-style="solid"><fo:block keep-together="always" text-align="center"> ${slNo} </fo:block></fo:table-cell>
										<fo:table-cell border-style="solid"><fo:block text-align="center"> ${employeeMapvalues.getValue().get("EmployeesMap").get("pfNum")?if_exists} </fo:block></fo:table-cell>
								        <fo:table-cell border-style="solid"><fo:block keep-together="always" text-align="center">${employeeMapvalues.getValue().get("EmployeesMap").get("partyId")?if_exists} </fo:block></fo:table-cell>
								        <fo:table-cell border-style="solid"><fo:block text-align="left">&#160;&#160;${employeeMapvalues.getValue().get("EmployeesMap").get("firstName")?if_exists} ${employeeMapvalues.getValue().get("EmployeesMap").get("lastName")?if_exists}&#160; </fo:block></fo:table-cell>
								        <fo:table-cell border-style="solid"><fo:block keep-together="always" text-align="center">${employeeMapvalues.getValue().get("EmployeesMap").get("wages")?string("0.##")?if_exists}</fo:block></fo:table-cell>
										<#assign pageEmplwage=pageEmplwage+employeeMapvalues.getValue().get("EmployeesMap").get("wages")?if_exists>
										<#assign totEmplwage=totEmplwage+employeeMapvalues.getValue().get("EmployeesMap").get("wages")?if_exists>
										<fo:table-cell border-style="solid"><fo:block keep-together="always" text-align="center"> ${employeeMapvalues.getValue().get("EmployeesMap").get("epf")?string("0.##")?if_exists} </fo:block></fo:table-cell>
								        <#assign pageEmplepf=pageEmplepf+employeeMapvalues.getValue().get("EmployeesMap").get("epf")?if_exists>
										<#assign totEmplepf=totEmplepf+employeeMapvalues.getValue().get("EmployeesMap").get("epf")?if_exists>
										<fo:table-cell border-style="solid"><fo:block keep-together="always" text-align="center">${employeeMapvalues.getValue().get("EmployersMap").get("employerVpf")?if_exists}</fo:block></fo:table-cell>
							        	<#assign pageemployerVpf=pageemployerVpf+employeeMapvalues.getValue().get("EmployersMap").get("employerVpf")?if_exists>
										<#assign totemployerVpf=totemployerVpf+employeeMapvalues.getValue().get("EmployersMap").get("employerVpf")?if_exists>
								        <fo:table-cell border-style="solid"><fo:block keep-together="always" text-align="center">${(employeeMapvalues.getValue().get("EmployeesMap").get("epf")+employeeMapvalues.getValue().get("EmployersMap").get("employerVpf"))?string("0.##")}</fo:block></fo:table-cell>						
										<#assign pageemployeeTotal=pageemployeeTotal+employeeTotal?if_exists>
										<#assign totemployeeTotal=totemployeeTotal+employeeTotal?if_exists>
										<fo:table-cell><fo:block keep-together="always" text-align="right"> </fo:block></fo:table-cell>
								        <fo:table-cell border-style="solid"><fo:block keep-together="always" text-align="center">${employeeMapvalues.getValue().get("EmployersMap").get("employerEpf")?string("0.##")?if_exists}</fo:block></fo:table-cell>						
								        <#assign pageemployerEpf=pageemployerEpf+employeeMapvalues.getValue().get("EmployersMap").get("employerEpf")?if_exists>
										<#assign totemployerEpf=totemployerEpf+employeeMapvalues.getValue().get("EmployersMap").get("employerEpf")?if_exists>
								        <fo:table-cell border-style="solid"><fo:block keep-together="always" text-align="center">${employeeMapvalues.getValue().get("EmployersMap").get("employerFpf")?string("0.##")?if_exists}</fo:block></fo:table-cell>
										<#assign pageemployerFpf=pageemployerFpf+employeeMapvalues.getValue().get("EmployersMap").get("employerFpf")?if_exists>
										<#assign totemployerFpf=totemployerFpf+employeeMapvalues.getValue().get("EmployersMap").get("employerFpf")?if_exists>
										<fo:table-cell border-style="solid"><fo:block keep-together="always" text-align="center">${employerTotal?string("0.##")} </fo:block></fo:table-cell>
								        <#assign pageemployerTotal=pageemployerTotal+employerTotal?if_exists>
										<#assign totemployerTotal=totemployerTotal+employerTotal?if_exists>
								        <fo:table-cell border-style="solid"><fo:block keep-together="always" text-align="center">${(employeeTotal+employerTotal)?string("0.##")}</fo:block></fo:table-cell>
							        	<#assign noOfLines= noOfLines+1>
							        </fo:table-row>
					
									<#assign slNo=slNo+1>
									<#if (noOfLines == 36)>
		                 				<#assign noOfLines=1>
			                 			<fo:table-row font-size="12pt">
			                 				<fo:table-cell border-style="solid"><fo:block keep-together="always" text-align="center">&#160; </fo:block></fo:table-cell>	
											<fo:table-cell border-style="solid"><fo:block keep-together="always" text-align="center" font-weight="bold">Page Total : </fo:block></fo:table-cell>
											<fo:table-cell border-style="solid"><fo:block keep-together="always" text-align="center">&#160; </fo:block></fo:table-cell>
											<fo:table-cell border-style="solid"><fo:block keep-together="always" text-align="center">&#160; </fo:block></fo:table-cell>
									        <fo:table-cell border-style="solid"><fo:block keep-together="always" text-align="center" font-weight="bold">${pageEmplwage?string("0.##")?if_exists}</fo:block></fo:table-cell>
											<fo:table-cell border-style="solid"><fo:block keep-together="always" text-align="center" font-weight="bold"> ${pageEmplepf?string("0.##")?if_exists} </fo:block></fo:table-cell>
											<fo:table-cell border-style="solid"><fo:block keep-together="always" text-align="center" font-weight="bold">${pageemployerVpf?if_exists}</fo:block></fo:table-cell> 
									        <fo:table-cell border-style="solid"><fo:block keep-together="always" text-align="center" font-weight="bold">${pageemployeeTotal?string("0.##")}</fo:block></fo:table-cell>						
											<fo:table-cell><fo:block keep-together="always" text-align="right"> </fo:block></fo:table-cell>
									        <fo:table-cell border-style="solid"><fo:block keep-together="always" text-align="center" font-weight="bold">${pageemployerEpf?string("0.##")?if_exists}</fo:block></fo:table-cell>						
									        <fo:table-cell border-style="solid"><fo:block keep-together="always" text-align="center" font-weight="bold">${pageemployerFpf?string("0.##")?if_exists}</fo:block></fo:table-cell>
											<fo:table-cell border-style="solid"><fo:block keep-together="always" text-align="center" font-weight="bold">${pageemployerTotal?string("0.##")} </fo:block></fo:table-cell>
									        <fo:table-cell border-style="solid"><fo:block keep-together="always" text-align="center" font-weight="bold">${(pageemployeeTotal+pageemployerTotal)?string("0.##")}</fo:block></fo:table-cell>
								        </fo:table-row>
				                 		<#assign pageEmplwage=0>
				                 		<#assign pageEmplepf=0>
				                 		<#assign pageEmplfpf=0>
				                 		<#assign pageemployeeTotal=0>
				                 		<#assign pageemployerEpf=0>
				                 		<#assign pageemployerFpf=0>
				                 		<#assign pageemployerTotal=0>
				                 		<#assign pageemployerVpf=0>
				                     	<fo:table-row>
				                        	<fo:table-cell >	
				                        		<fo:block page-break-after="always"></fo:block>
				                        	</fo:table-cell>
				                        </fo:table-row>
				                    </#if>
								</#list> 
								<fo:table-row font-size="12pt">	
									<fo:table-cell border-style="solid"><fo:block keep-together="always" text-align="center"> &#160;</fo:block></fo:table-cell>
									<fo:table-cell border-style="solid"><fo:block keep-together="always" text-align="center" font-weight="bold">Grand Total : </fo:block></fo:table-cell>
							        <fo:table-cell border-style="solid"><fo:block keep-together="always" text-align="left">&#160; </fo:block></fo:table-cell>
									<fo:table-cell border-style="solid"><fo:block keep-together="always" text-align="center">&#160; </fo:block></fo:table-cell>
							        <fo:table-cell border-style="solid"><fo:block keep-together="always" text-align="center" font-weight="bold">${totEmplwage?string("0.##")?if_exists}</fo:block></fo:table-cell>
									<fo:table-cell border-style="solid"><fo:block keep-together="always" text-align="center" font-weight="bold"> ${totEmplepf?string("0.##")?if_exists} </fo:block></fo:table-cell>
									<fo:table-cell border-style="solid"><fo:block keep-together="always" text-align="center" font-weight="bold">${totemployerVpf?if_exists}</fo:block></fo:table-cell>
							        <fo:table-cell border-style="solid"><fo:block keep-together="always" text-align="center" font-weight="bold">${totemployeeTotal?string("0.##")}</fo:block></fo:table-cell>						
									<fo:table-cell><fo:block keep-together="always" text-align="right"> </fo:block></fo:table-cell>
							        <fo:table-cell border-style="solid"><fo:block keep-together="always" text-align="center" font-weight="bold">${totemployerEpf?string("0.##")?if_exists}</fo:block></fo:table-cell>						
							        <fo:table-cell border-style="solid"><fo:block keep-together="always" text-align="center" font-weight="bold">${totemployerFpf?string("0.##")?if_exists}</fo:block></fo:table-cell>
									<fo:table-cell border-style="solid"><fo:block keep-together="always" text-align="center" font-weight="bold">${totemployerTotal?string("0.##")} </fo:block></fo:table-cell>
							        <fo:table-cell border-style="solid"><fo:block keep-together="always" text-align="center" font-weight="bold">${(totemployeeTotal+totemployerTotal)?string("0.##")}</fo:block></fo:table-cell>
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
 	</fo:root>
</#escape>
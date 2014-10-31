<#escape x as x?xml>
    <fo:root xmlns:fo="http://www.w3.org/1999/XSL/Format">
        <fo:layout-master-set>
            <fo:simple-page-master master-name="main" page-height="12in" page-width="15in" margin-top=".3in" margin-left=".3in">
                <fo:region-body margin-top="1.7in"/>
                <fo:region-before extent="1in"/>
                <fo:region-after extent="1in"/>
            </fo:simple-page-master>
        </fo:layout-master-set>
         ${setRequestAttribute("OUTPUT_FILENAME", "FederationUnitsMlkRcptsValueFrmShed.txt")}
    <#if shedWiseTotalsMap?has_content> 
		<fo:page-sequence master-reference="main">
			<fo:static-content flow-name="xsl-region-before" font-family="Courier,monospace" font-size="9pt">
            	<fo:block text-align="center">THE ANDHRA PRADESH DAIRY DEVELOPMENT COOPERATIVE  FEDERATION LIMITED</fo:block>
            	<fo:block text-align="center" keep-together="always">&#160;     ADDITIONAL WORKING CAPTITAL REQUIREMENT OF PERIOD FROM   ${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(fromDate, "dd-MM-yyyy")} TO:${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(thruDate, "dd-MM-yyyy")}    (RUPEES IN LAKHS)</fo:block>
               	<fo:block font-size="9pt">---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------</fo:block>
				<fo:block font-size="9pt">
					<fo:table>
						<fo:table-column column-width="20pt"/>
						<fo:table-column column-width="200pt"/>
						<fo:table-column column-width="80pt"/>
						<fo:table-body>
							<fo:table-row>
								<fo:table-cell>
									<fo:block keep-together="always"  white-space-collapse="false">&#160;                                                INCOME DETAILS                                     |                    EXPENDITURE DETAILS                  |      |        |</fo:block>
									<fo:block>----------------------------|----------------------------------------------------------------------------------------------------------------------------------------------------------</fo:block>
									<fo:block keep-together="always"  white-space-collapse="false">NAME OF THE  SHED           |  MILK       OP       IUT-SENT   MILK &amp;        ACTUAL    OUT    TOTAL  | MILK     STAFF    STORE     MILK &amp;     IUT-RECT   TOTAL |ELIGI-|OP-COST |ADDL</fo:block>
									<fo:block keep-together="always"  white-space-collapse="false">&#160;                           | RECPTS     COST      INCOME     PRODUCTS      CASH     STAND   INCOME |PROCURE  SALARIES  MATERLS  PRODUCTS     OTH-EXP   EXPNDR|BILITY|RELESED |W.CAPTL</fo:block>
									<fo:block keep-together="always"  white-space-collapse="false">&#160;                           | VALUE     VALUE      VALUE      VALUE         RMTS     DUES    VALUE  |BILL     AMOUNT    VALUE    VALUE        VALUE     VALUE |VALUE |VALUE   |VALUE</fo:block>
									<fo:block>----------------------------|----------------------------------------------------------------------------------------------------------------------------------------------------------</fo:block>
									<fo:block keep-together="always"  white-space-collapse="false">&#160;                           |  (1)       (2)        (3)        (4)          (5)       (6)     (7)   | (8)      (9)       (10)      (11)         (12)      (13)| (14) | (15)   | (16)</fo:block>
									<fo:block>----------------------------|----------------------------------------------------------------------------------------------------------------------------------------------------------</fo:block>
								</fo:table-cell>								
							</fo:table-row>
						</fo:table-body>
					</fo:table>
				</fo:block>
			</fo:static-content>
			<fo:flow flow-name="xsl-region-body" font-family="Courier,monospace" font-size="9pt">       				   
						<fo:block font-family="Courier,monospace">
						 	<fo:table>
						 		<fo:table-column column-width="130pt"/>
           						<fo:table-column column-width="60pt"/>
           						<fo:table-column column-width="60pt"/>
           						<fo:table-column column-width="55pt"/>
           						<fo:table-column column-width="60pt"/>
           						<fo:table-column column-width="60pt"/>
           						<fo:table-column column-width="60pt"/>
           						<fo:table-column column-width="50pt"/>
           						<fo:table-column column-width="40pt"/>
           						<fo:table-column column-width="50pt"/>
           						<fo:table-column column-width="50pt"/>
           						<fo:table-column column-width="65pt"/>
           						<fo:table-column column-width="50pt"/>
           						<fo:table-column column-width="60pt"/>
           						<fo:table-column column-width="40pt"/>
           						<fo:table-column column-width="45pt"/>
           						<fo:table-column column-width="45pt"/>
           						<fo:table-body> 
           						<#assign shedWiseList=shedWiseTotalsMap.entrySet()>
           						<#assign grMlkRcptAmt=0>
           						<#assign grOpCostAmt=0>
           						<#assign grProdValueAmt=0>
           						<#assign grCashRmtAmt=0>
           						<#assign grOutStandDueAmt=0>
           						<#assign grIncomeAmt=0>
           						<#assign grMlkBillAmt=0>
           						<#assign grSalaryAmt=0>
           						<#assign grStoreAmt=0>
           						<#assign grMlkProdValue=0>
           						<#assign grIutRcptValue=0>
           						<#assign grTotExpAmt=0>
           						<#assign grTotEligibilityAmt=0>
           						<#assign grTotOpcostRlsdAmt=0>
           						<#assign grTotCapitalAmt=0>
           						
           						<#list shedWiseList as shedDetails>
           							<#assign milkRcptAmt=0>
           							<#assign mlkRcptOpCost=0>
           							<#assign mlkProcBill=0>
           							<#assign unitDetailsList=shedDetails.getValue().entrySet()>
           							<#list unitDetailsList as unitDetails>
           								<#if unitDetails.getKey() =="TOTAL">
           									<#if milkRcptAmt !=0>
				       							<#assign shed = delegator.findOne("Facility", {"facilityId" : shedDetails.getKey()}, true)>
				       							<#assign totalIncome= ((milkRcptAmt/100000)+(mlkRcptOpCost/100000)+(unitDetails.getValue().get("shedIutSentValue"))+(unitDetails.getValue().get("cashRemittance")))>
				       							<#assign grMlkRcptAmt=grMlkRcptAmt+(milkRcptAmt/100000)>
				           						<#assign grOpCostAmt=grOpCostAmt+(mlkRcptOpCost/100000)>
				           						<#assign grProdValueAmt=grProdValueAmt+(unitDetails.getValue().get("milkProductsSaleValue"))>
				           						<#assign grCashRmtAmt=grCashRmtAmt+(unitDetails.getValue().get("cashRemittance"))>
				           						<#assign grOutStandDueAmt=grOutStandDueAmt+(unitDetails.getValue().get("outStandDues"))>
				           						<#assign grIncomeAmt=grIncomeAmt+totalIncome>
				           						<#assign grMlkBillAmt=grMlkBillAmt+(mlkProcBill/100000)>
				           						<#assign grSalaryAmt=grSalaryAmt+(unitDetails.getValue().get("salaryAmt"))>
				           						<#assign grStoreAmt=grStoreAmt+(unitDetails.getValue().get("storeMaterialAmt"))>
				           						<#assign grMlkProdValue=grMlkProdValue+(unitDetails.getValue().get("milkProdValue"))>
				       							<fo:table-row>
				       								<fo:table-cell>
				       									<fo:block>${shed.get("facilityName")?if_exists}</fo:block>
				       								</fo:table-cell>
				       								
				       								<fo:table-cell>
				       									<fo:block text-align="right">${(milkRcptAmt/100000)?if_exists?string("##0.000")}</fo:block>
				       								</fo:table-cell>
				       								<fo:table-cell>
				       									<fo:block text-align="right">${(mlkRcptOpCost/100000)?if_exists?string("##0.000")}</fo:block>
				       								</fo:table-cell>
				       								<fo:table-cell>
				       									<fo:block text-align="right">${(unitDetails.getValue().get("shedIutSentValue"))}</fo:block>
				       								</fo:table-cell>
				       								<fo:table-cell>
				       									<fo:block text-align="right">${(unitDetails.getValue().get("milkProductsSaleValue"))}</fo:block>
				       								</fo:table-cell>
				       								<fo:table-cell>
				       									<fo:block text-align="right">${(unitDetails.getValue().get("cashRemittance"))}</fo:block>
				       								</fo:table-cell>
				       								<fo:table-cell>
				       									<fo:block text-align="right">${(unitDetails.getValue().get("outStandDues"))}</fo:block>
				       								</fo:table-cell>
				       								<fo:table-cell>
				       									<fo:block text-align="right">${totalIncome?if_exists?string("##0.00")}</fo:block>
				       								</fo:table-cell>
				       								<fo:table-cell>
				       									<fo:block text-align="right">${(mlkProcBill/100000)?if_exists?string("##0.00")}</fo:block>
				       								</fo:table-cell>
				       								<fo:table-cell>
				       									<fo:block text-align="right">${(unitDetails.getValue().get("salaryAmt"))?if_exists?string("##0.00")}</fo:block>
				       								</fo:table-cell>
				       								<fo:table-cell>
				       									<fo:block text-align="right">${(unitDetails.getValue().get("storeMaterialAmt"))?if_exists?string("##0.00")}</fo:block>
				       								</fo:table-cell>
				       								<fo:table-cell>
				       									<fo:block text-align="right">${(unitDetails.getValue().get("milkProdValue"))?if_exists?string("##0.00")}</fo:block>
				       								</fo:table-cell>
				       								<#assign grIutRcptValue=grIutRcptValue+(unitDetails.getValue().get("shedMlkRecptValue")+(mlkRcptOpCost/100000))>
				       								<fo:table-cell>
				       									<fo:block text-align="right">${(unitDetails.getValue().get("shedMlkRecptValue")+(mlkRcptOpCost/100000))?if_exists?string("##0.000")}</fo:block>
				       								</fo:table-cell>
				       								<#assign totalExpAmt= (mlkProcBill/100000)+(unitDetails.getValue().get("salaryAmt"))+(unitDetails.getValue().get("storeMaterialAmt"))+(unitDetails.getValue().get("milkProdValue"))+(unitDetails.getValue().get("shedMlkRecptValue")+(mlkRcptOpCost/100000))>
				       								<#assign grTotExpAmt=grTotExpAmt+totalExpAmt>
				       								<fo:table-cell>
				       									<fo:block text-align="right">${(totalExpAmt)?if_exists?string("##0.000")}</fo:block>
				       								</fo:table-cell>
				       								<#assign grTotEligibilityAmt=grTotEligibilityAmt+(totalIncome-totalExpAmt)>
				       								<fo:table-cell>
				       									<fo:block text-align="right">${(totalIncome-totalExpAmt)?if_exists?string("##0.000")}</fo:block>
				       								</fo:table-cell>
				       								<#assign grTotOpcostRlsdAmt=grTotOpcostRlsdAmt+(unitDetails.getValue().get("opCostReleased"))>
				       								<fo:table-cell>
				       									<fo:block text-align="right">${(unitDetails.getValue().get("opCostReleased"))?if_exists?string("##0.000")}</fo:block>
				       								</fo:table-cell>
				       								<#assign grTotCapitalAmt=grTotCapitalAmt+((totalIncome-totalExpAmt)-(unitDetails.getValue().get("opCostReleased")))>
				       								<fo:table-cell>
				       									<fo:block text-align="right">${((totalIncome-totalExpAmt)-(unitDetails.getValue().get("opCostReleased")))?if_exists?string("##0.000")}</fo:block>
				       								</fo:table-cell>
				       							</fo:table-row>
			       							</#if>	
		       							<#else>
		       								<#assign mlkamt=unitDetails.getValue().get("mlkAmount")?if_exists>
		       								<#assign grsAmt=unitDetails.getValue().get("procurement").get("grossAmt")?if_exists>
		       								<#assign tip=unitDetails.getValue().get("procurement").get("tipAmount")?if_exists>
		       								<#assign milkRcptAmt=milkRcptAmt+mlkamt>
		       								<#assign mlkRcptOpCost=mlkRcptOpCost+unitDetails.getValue().get("totOpCost")>
		       								<#assign mlkProcBill=mlkProcBill+grsAmt+tip>
		       							</#if>
	       							</#list>
       							</#list>
       								<fo:table-row>
       									<fo:table-cell>
       										<fo:block >---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------</fo:block>
       									</fo:table-cell>
       								</fo:table-row>
       								<fo:table-row>
	       								<fo:table-cell>
	       									<fo:block>TOTAL</fo:block>
	       								</fo:table-cell>	       								
	       								<fo:table-cell>
	       									<fo:block text-align="right">${(grMlkRcptAmt)?if_exists?string("##0.000")}</fo:block>
	       								</fo:table-cell>
	       								<fo:table-cell>
	       									<fo:block text-align="right">${(grOpCostAmt)?if_exists?string("##0.000")}</fo:block>
	       								</fo:table-cell>
	       								<fo:table-cell>
	       									<fo:block text-align="right">0.00</fo:block>
	       								</fo:table-cell>
	       								<fo:table-cell>
	       									<fo:block text-align="right">${(grProdValueAmt)}</fo:block>
	       								</fo:table-cell>
	       								<fo:table-cell>
	       									<fo:block text-align="right">${(grCashRmtAmt)}</fo:block>
	       								</fo:table-cell>
	       								<fo:table-cell>
	       									<fo:block text-align="right">${(grOutStandDueAmt)}</fo:block>
	       								</fo:table-cell>
	       								<fo:table-cell>
	       									<fo:block text-align="right">${grIncomeAmt?if_exists?string("##0.00")}</fo:block>
	       								</fo:table-cell>
	       								<fo:table-cell>
	       									<fo:block text-align="right">${(grMlkBillAmt)?if_exists?string("##0.00")}</fo:block>
	       								</fo:table-cell>
	       								<fo:table-cell>
	       									<fo:block text-align="right">${(grSalaryAmt)?if_exists?string("##0.00")}</fo:block>
	       								</fo:table-cell>
	       								<fo:table-cell>
	       									<fo:block text-align="right">${(grStoreAmt)?if_exists?string("##0.00")}</fo:block>
	       								</fo:table-cell>
	       								<fo:table-cell>
	       									<fo:block text-align="right">${(grMlkProdValue)?if_exists?string("##0.00")}</fo:block>
	       								</fo:table-cell>
	       								<fo:table-cell>
	       									<fo:block text-align="right">${(grIutRcptValue)?if_exists?string("##0.000")}</fo:block>
	       								</fo:table-cell>
	       								<fo:table-cell>
	       									<fo:block text-align="right">${(grTotExpAmt)?if_exists?string("##0.000")}</fo:block>
	       								</fo:table-cell>
	       								<fo:table-cell>
	       									<fo:block text-align="right">${(grTotEligibilityAmt)?if_exists?string("##0.000")}</fo:block>
	       								</fo:table-cell>
	       								<fo:table-cell>
	       									<fo:block text-align="right">${(grTotOpcostRlsdAmt)?if_exists?string("##0.000")}</fo:block>
	       								</fo:table-cell>
	       								<fo:table-cell>
	       									<fo:block text-align="right">${(grTotCapitalAmt)?if_exists?string("##0.000")}</fo:block>
	       								</fo:table-cell>
	       							</fo:table-row>
       								<fo:table-row>
       									<fo:table-cell>
       										<fo:block >---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------</fo:block>
       									</fo:table-cell>
       								</fo:table-row>
							</fo:table-body>
			 			</fo:table>
					</fo:block>
					<fo:block linefeed-treatment="preserve" font-size="7pt">&#xA;</fo:block>
					<fo:block linefeed-treatment="preserve" font-size="7pt">&#xA;</fo:block>
					<fo:block linefeed-treatment="preserve" font-size="7pt">&#xA;</fo:block>
					<fo:block linefeed-treatment="preserve" font-size="7pt">&#xA;</fo:block>
					<fo:block keep-togehter="always" font-size="10pt" white-space-collapse="false">Copy submitted to the Managing Diretor,                                                                                         DEPUTY DIRECTOR  (MIS)</fo:block>
					<fo:block keep-togehter="always" font-size="10pt">Copy submitted to the Executive Director,</fo:block>
					<fo:block keep-togehter="always" font-size="10pt">Copy submitted to the General Manager (P&amp;I),</fo:block>
					<fo:block keep-togehter="always" font-size="10pt">Copy submitted to the General Manager (MPF),</fo:block>
					<fo:block keep-togehter="always" font-size="10pt">Copy submitted to the General Manager (F&amp;A),</fo:block>
			    	</fo:flow>		
		   			</fo:page-sequence>	
					<#else>
					<fo:page-sequence master-reference="main">
		    			<fo:flow flow-name="xsl-region-body" font-family="Courier,monospace">
		       		 		<fo:block font-size="14pt">
		            			${uiLabelMap.NoOrdersFound}.
		       		 		</fo:block>
		    			</fo:flow>
					</fo:page-sequence>
				</#if>
    
	<#if shedWiseTotalsMap?has_content> 
		<fo:page-sequence master-reference="main">
			<fo:static-content flow-name="xsl-region-before" font-family="Courier,monospace">
            	<fo:block text-align="center">UNIT-WISE MILK BILLING OF MILK RECEIVED FROM MILK SHEDS TO MPF..HYDERABAD PERIOD FROM : ${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(fromDate, "dd-MM-yyyy")} TO:${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(thruDate, "dd-MM-yyyy")}</fo:block>
            	<fo:block text-align="center">&#160;                        ONLY FOR BOOK ADJUSTMENT NOT FOR PAYMENT        ANNEXURE-1</fo:block>
               	<fo:block>------------------------------------------------------------------------------------------------------------------------------------------------</fo:block>
				<fo:block>
					<fo:table>
						<fo:table-column column-width="30pt"/>
						<fo:table-column column-width="200pt"/>
						<fo:table-column column-width="80pt"/>
						<fo:table-body>
							<fo:table-row>
								<fo:table-cell>
									<fo:block keep-together="always"  white-space-collapse="false">NAME OF THE UNIT/SHED    MILK     QUANTITY    QUANTITY     TOTAL      TOTAL     PROC RATE    MILK    OPCOST    TOTAL      TOTAL   AVG     AVG</fo:block>
									<fo:block keep-together="always"  white-space-collapse="false">&#160;                        TYPE     (LTS)         (KGS)     KG-FAT      KG-SNF    PER/KG      AMOUNT   PER/LT   OP COST    AMOUNT   FAT%    SNF%</fo:block>
								</fo:table-cell>								
							</fo:table-row>
						</fo:table-body>
					</fo:table>
				</fo:block>  
					<fo:block>------------------------------------------------------------------------------------------------------------------------------------------------</fo:block> 
			</fo:static-content>
			<fo:flow flow-name="xsl-region-body" font-family="Courier,monospace">       				   
						<fo:block font-family="Courier,monospace">
						 	<fo:table>
						 		<fo:table-column column-width="150pt"/>
           						<fo:table-column column-width="60pt"/>
           						<fo:table-column column-width="80pt"/>
           						<fo:table-column column-width="90pt"/>
           						<fo:table-column column-width="80pt"/>
           						<fo:table-column column-width="80pt"/>
           						<fo:table-column column-width="80pt"/>
           						<fo:table-column column-width="80pt"/>
           						<fo:table-column column-width="60pt"/>
           						<fo:table-column column-width="75pt"/>
           						<fo:table-column column-width="83pt"/>
           						<fo:table-column column-width="50pt"/>
           						<fo:table-column column-width="50pt"/>
           						<fo:table-column column-width="80pt"/>
           						<fo:table-body> 
           						<#assign shedWiseList=shedWiseTotalsMap.entrySet()>
           						<#list shedWiseList as shedDetails>
           							<#assign milkAmt=0>
           							<#assign totOpCost=0>
           							<#assign totAmount=0>
           							<#assign unitDetailsList=shedDetails.getValue().entrySet()>
           							<#list unitDetailsList as unitDetails>
           								<#if unitDetails.getKey() !="TOTAL">
           								<#assign unitDetailValues = delegator.findOne("Facility", {"facilityId" : unitDetails.getKey()}, true)>
										<fo:table-row>
		       								<fo:table-cell>
		       									<fo:block>${unitDetailValues.get("facilityName")?if_exists}</fo:block>
		       								</fo:table-cell>
		       								<fo:table-cell>
		       									<fo:block text-align="right">MM</fo:block>
		       								</fo:table-cell>
		       								<fo:table-cell>
		       									<fo:block text-align="right">${unitDetails.getValue().get("qtyLtrs")?if_exists?string("##0.0")}</fo:block>
		       								</fo:table-cell>
		       								<fo:table-cell>
		       									<fo:block text-align="right">${unitDetails.getValue().get("qtyKgs")?if_exists?string("##0.0")}</fo:block>
		       								</fo:table-cell>
		       								<fo:table-cell>
		       									<fo:block text-align="right">${unitDetails.getValue().get("kgFat")?if_exists?string("##0.00")}</fo:block>
		       								</fo:table-cell>
		       								<fo:table-cell>
		       									<fo:block text-align="right">${unitDetails.getValue().get("kgSnf")?if_exists?string("##0.00")}</fo:block>
		       								</fo:table-cell>
		       								<fo:table-cell>
		       									<fo:block text-align="right">${unitDetails.getValue().get("rate")?if_exists?string("##0.000")}</fo:block>
		       								</fo:table-cell>
		       								<#assign milkAmt=milkAmt+unitDetails.getValue().get("mlkAmount")>
		       								<fo:table-cell>
		       									<fo:block text-align="right">${(unitDetails.getValue().get("mlkAmount"))?if_exists?string("##0.00")}</fo:block>
		       								</fo:table-cell>
		       								<fo:table-cell>
		       									<fo:block text-align="right">${(unitDetails.getValue().get("opCost"))?if_exists?string("##0.00")}</fo:block>
		       								</fo:table-cell>
		       								<#assign totOpCost=totOpCost+unitDetails.getValue().get("totOpCost")>
		       								<fo:table-cell>
		       									<fo:block text-align="right">${(unitDetails.getValue().get("totOpCost"))?if_exists?string("##0.00")}</fo:block>
		       								</fo:table-cell>
		       								<#assign totAmount=totAmount+unitDetails.getValue().get("totAmt")>
		       								<fo:table-cell>
		       									<fo:block text-align="right">${(unitDetails.getValue().get("totAmt"))?if_exists?string("##0.00")}</fo:block>
		       								</fo:table-cell>
		       								<fo:table-cell>
		       									<fo:block text-align="right"><#if unitDetails.getValue().get("qtyKgs") !=0>${((unitDetails.getValue().get("kgFat")*100)/unitDetails.getValue().get("qtyKgs"))?if_exists?string("##0.00")}</#if></fo:block>
		       								</fo:table-cell>
		       								<fo:table-cell>
		       									<fo:block text-align="right"><#if unitDetails.getValue().get("qtyKgs") !=0>${((unitDetails.getValue().get("kgSnf")*100)/unitDetails.getValue().get("qtyKgs"))?if_exists?string("##0.00")}</#if></fo:block>
		       								</fo:table-cell>
		       							</fo:table-row>
		       							<#else>
		       								<fo:table-row>
		       									<fo:table-cell>
		       										<fo:block>------------------------------------------------------------------------------------------------------------------------------------------------</fo:block>
		       									</fo:table-cell>
		       								</fo:table-row>
		       								<fo:table-row>
		       								<fo:table-cell>
		       									<fo:block>${shedDetails.getKey()}</fo:block>
		       								</fo:table-cell>
		       								<fo:table-cell>
		       									<fo:block text-align="right">MM</fo:block>
		       								</fo:table-cell>
		       								<fo:table-cell>
		       									<fo:block text-align="right">${unitDetails.getValue().get("qtyLtrs")?if_exists?string("##0.0")}</fo:block>
		       								</fo:table-cell>
		       								<fo:table-cell>
		       									<fo:block text-align="right">${unitDetails.getValue().get("qtyKgs")?if_exists?string("##0.0")}</fo:block>
		       								</fo:table-cell>
		       								<fo:table-cell>
		       									<fo:block text-align="right">${unitDetails.getValue().get("kgFat")?if_exists?string("##0.00")}</fo:block>
		       								</fo:table-cell>
		       								<fo:table-cell>
		       									<fo:block text-align="right">${unitDetails.getValue().get("kgSnf")?if_exists?string("##0.00")}</fo:block>
		       								</fo:table-cell>
		       								<fo:table-cell>
		       									<fo:block text-align="right"><#if unitDetails.getValue().get("qtyKgs") !=0>${(milkAmt/unitDetails.getValue().get("qtyKgs"))?if_exists?string("##0.000")}</#if></fo:block>
		       								</fo:table-cell>
		       								<fo:table-cell>
		       									<fo:block text-align="right">${(milkAmt)?string("##0.00")}</fo:block>
		       								</fo:table-cell>
		       								<fo:table-cell>
		       									<fo:block text-align="right"></fo:block>
		       								</fo:table-cell>
		       								<fo:table-cell>
		       									<fo:block text-align="right">${(totOpCost)?string("##0.00")}</fo:block>
		       								</fo:table-cell>
		       								<fo:table-cell>
		       									<fo:block text-align="right">${(totAmount)?string("##0.00")}</fo:block>
		       								</fo:table-cell>
		       								<fo:table-cell>
		       									<fo:block text-align="right"><#if unitDetails.getValue().get("qtyKgs") !=0>${((unitDetails.getValue().get("kgFat")*100)/unitDetails.getValue().get("qtyKgs"))?if_exists?string("##0.00")}</#if></fo:block>
		       								</fo:table-cell>
		       								<fo:table-cell>
		       									<fo:block text-align="right"><#if unitDetails.getValue().get("qtyKgs") !=0>${((unitDetails.getValue().get("kgSnf")*100)/unitDetails.getValue().get("qtyKgs"))?if_exists?string("##0.00")}</#if></fo:block>
		       								</fo:table-cell>
		       							</fo:table-row>
		       							<fo:table-row>
	       									<fo:table-cell>
	       										<fo:block>------------------------------------------------------------------------------------------------------------------------------------------------</fo:block>
	       									</fo:table-cell>
	       								</fo:table-row>
		       							</#if>
	       							</#list>
       							</#list>
							</fo:table-body>
			 			</fo:table>
					</fo:block>
					
			    	</fo:flow>		
		   			</fo:page-sequence>	
					<#else>
					<fo:page-sequence master-reference="main">
		    			<fo:flow flow-name="xsl-region-body" font-family="Courier,monospace">
		       		 		<fo:block font-size="14pt">
		            			${uiLabelMap.NoOrdersFound}.
		       		 		</fo:block>
		    			</fo:flow>
					</fo:page-sequence>
				</#if>
				    <#if shedWiseTotalsMap?has_content> 
						<fo:page-sequence master-reference="main">
							<fo:static-content flow-name="xsl-region-before" font-family="Courier,monospace" font-size="11pt">
				            	<fo:block text-align="center">UNIT WISE MILK BILLING PARTICULARS AT MILK SHED LEVEL PERIOD FROM :  ${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(fromDate, "dd-MM-yyyy")} TO:${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(thruDate, "dd-MM-yyyy")}</fo:block>
				               	<fo:block>------------------------------------------------------------------------------------------------------------------------------------------</fo:block>
								<fo:block>
									<fo:table>
										<fo:table-column column-width="20pt"/>
										<fo:table-column column-width="200pt"/>
										<fo:table-column column-width="80pt"/>
										<fo:table-body>
											<fo:table-row>
												<fo:table-cell>
													<fo:block keep-together="always"  white-space-collapse="false">&#160;                      | TOTAL BUFFALO MILK   |    TOTAL COW MILK    |    TOTAL MIXED MILK  |  TOTAL   | TOTAL   | GROSS    |PROC   <!--| AVG  |AVG  |--></fo:block>
													<fo:block keep-together="always"  white-space-collapse="false">&#160;                      |----------------------|----------------------|----------------------|          |ADDN AMT |          |RATE   <!--| FAT  |SNF  |--></fo:block>
													<fo:block keep-together="always"  white-space-collapse="false">NAME OF THE  SHED      | KGS          VALUE   |  KGS          VALUE  |  KGS          VALUE  |  TIP     |+COMSN   | VALUE    |PER/KG <!--|  %   |  %  |--></fo:block>
													<fo:block>------------------------------------------------------------------------------------------------------------------------------------------</fo:block>
												</fo:table-cell>								
											</fo:table-row>
										</fo:table-body>
									</fo:table>
								</fo:block>
							</fo:static-content>
							<fo:flow flow-name="xsl-region-body" font-family="Courier,monospace" font-size="11pt">       				   
								<fo:block font-family="Courier,monospace">
								 	<fo:table>
								 		<fo:table-column column-width="130pt"/>
		           						<fo:table-column column-width="60pt"/>
		           						<fo:table-column column-width="90pt"/>
		           						<fo:table-column column-width="80pt"/>
		           						<fo:table-column column-width="80pt"/>
		           						<fo:table-column column-width="70pt"/>
		           						<fo:table-column column-width="95pt"/>
		           						<fo:table-column column-width="70pt"/>
		           						<fo:table-column column-width="60pt"/>
		           						<fo:table-column column-width="70pt"/>
		           						<fo:table-column column-width="70pt"/>
		           						<fo:table-column column-width="60pt"/>
		           						<fo:table-column column-width="60pt"/>
		           						<fo:table-body> 
		           						<#assign shedWiseList=shedWiseTotalsMap.entrySet()>       						
		           						<#list shedWiseList as shedDetails>
		           							<#assign totBmKgs=0>
			           						<#assign totCmKgs=0>
			           						<#assign totBmAmt=0>
			           						<#assign totCmAmt=0>
			           						<#assign totMixKgs=0>
			           						<#assign totMixAmt=0>
			           						<#assign totTip=0>
			           						<#assign totAddnComn=0>
			           						<#assign totGross=0>
		           							<#assign unitDetailsList=shedDetails.getValue().entrySet()>
		           							<#list unitDetailsList as unitDetails>
		           								<#if unitDetails.getKey() !="TOTAL">
		           								<#if (unitDetails.getValue().get("procurement").get("mixProcQty")?exists) && (unitDetails.getValue().get("procurement").get("mixProcQty") !=0)>
			           								<#assign unitDetailValues = delegator.findOne("Facility", {"facilityId" : unitDetails.getKey()}, true)>
			           								<#assign totBmKgs=totBmKgs+unitDetails.getValue().get("procurement").get("BMQtyKgs")>
					           						<#assign totCmKgs=totCmKgs+unitDetails.getValue().get("procurement").get("CMQtyKgs")>
					           						<#assign totBmAmt=totBmAmt+unitDetails.getValue().get("procurement").get("BMPrice")>
					           						<#assign totCmAmt=totCmAmt+unitDetails.getValue().get("procurement").get("CMPrice")>
					           						<#assign totMixKgs=totMixKgs+unitDetails.getValue().get("procurement").get("mixProcQty")>
					           						<#assign totMixAmt=totMixAmt+unitDetails.getValue().get("procurement").get("mixedMlkAmount")>
					           						<#assign totTip=totTip+unitDetails.getValue().get("procurement").get("tipAmount")>
					           						<#assign totAddnComn=totAddnComn+unitDetails.getValue().get("procurement").get("addnAmt")>
					           						<#assign totGross=totGross+unitDetails.getValue().get("procurement").get("grossAmt")>
													<fo:table-row>
					       								<fo:table-cell>
					       									<fo:block>${unitDetailValues.get("facilityName")?if_exists}</fo:block>
					       								</fo:table-cell>				       								
					       								<fo:table-cell>
					       									<fo:block text-align="right">${unitDetails.getValue().get("procurement").get("BMQtyKgs")?if_exists?string("##0.0")}</fo:block>
					       								</fo:table-cell>
					       								<fo:table-cell>
					       									<fo:block text-align="right">${unitDetails.getValue().get("procurement").get("BMPrice")?if_exists?string("##0.00")}</fo:block>
					       								</fo:table-cell>
					       								<fo:table-cell>
					       									<fo:block text-align="right">${unitDetails.getValue().get("procurement").get("CMQtyKgs")?if_exists?string("##0.0")}</fo:block>
					       								</fo:table-cell>
					       								<fo:table-cell>
					       									<fo:block text-align="right">${unitDetails.getValue().get("procurement").get("CMPrice")?if_exists?string("##0.00")}</fo:block>
					       								</fo:table-cell>
					       								<fo:table-cell>
					       									<fo:block text-align="right">${unitDetails.getValue().get("procurement").get("mixProcQty")?if_exists?string("##0.0")}</fo:block>
					       								</fo:table-cell>
					       								<fo:table-cell>
					       									<fo:block text-align="right">${unitDetails.getValue().get("procurement").get("mixedMlkAmount")?if_exists?string("##0.00")}</fo:block>
					       								</fo:table-cell>
					       								<fo:table-cell>
					       									<fo:block text-align="right">${unitDetails.getValue().get("procurement").get("tipAmount")?if_exists?string("##0.00")}</fo:block>
					       								</fo:table-cell>
					       								<fo:table-cell>
					       									<fo:block text-align="right">${unitDetails.getValue().get("procurement").get("addnAmt")?if_exists?string("##0.00")}</fo:block>
					       								</fo:table-cell>
					       								<fo:table-cell>
					       									<fo:block text-align="right">${unitDetails.getValue().get("procurement").get("grossAmt")?if_exists?string("##0.00")}</fo:block>
					       								</fo:table-cell>
					       								<fo:table-cell>
					       									<fo:block text-align="right">${unitDetails.getValue().get("procurement").get("rate")?if_exists?string("##0.000")}</fo:block>
					       								</fo:table-cell>					       								
					       							</fo:table-row>
					       						</#if>	
				       							<#else>
				       								<#if totMixKgs !=0>
					       								<fo:table-row>
					       									<fo:table-cell>
					       										<fo:block>------------------------------------------------------------------------------------------------------------------------------------------</fo:block>
					       									</fo:table-cell>
					       								</fo:table-row>
					       								<fo:table-row>
					       								<fo:table-cell>
					       									<fo:block>${shedDetails.getKey()}</fo:block>
					       								</fo:table-cell>
					       								<fo:table-cell>
					       									<fo:block text-align="right">${totMixKgs?if_exists?string("##0.0")}</fo:block>
					       								</fo:table-cell>
					       								<fo:table-cell>
					       									<fo:block text-align="right">${totBmAmt?if_exists?string("##0.00")}</fo:block>
					       								</fo:table-cell>
					       								<fo:table-cell>
					       									<fo:block text-align="right">${totCmKgs?if_exists?string("##0.0")}</fo:block>
					       								</fo:table-cell>
					       								<fo:table-cell>
					       									<fo:block text-align="right">${totCmAmt?if_exists?string("##0.00")}</fo:block>
					       								</fo:table-cell>
					       								<fo:table-cell>
					       									<fo:block text-align="right">${totMixKgs?if_exists?string("##0.0")}</fo:block>
					       								</fo:table-cell>
					       								<fo:table-cell>
					       									<fo:block text-align="right">${totMixAmt?if_exists?string("##0.00")}</fo:block>
					       								</fo:table-cell>
					       								<fo:table-cell>
					       									<fo:block text-align="right">${totTip?if_exists?string("##0.00")}</fo:block>
					       								</fo:table-cell>
					       								<fo:table-cell>
					       									<fo:block text-align="right">${totAddnComn?if_exists?string("##0.00")}</fo:block>
					       								</fo:table-cell>
					       								<fo:table-cell>
					       									<fo:block text-align="right">${totGross?if_exists?string("##0.00")}</fo:block>
					       								</fo:table-cell>
					       								<fo:table-cell>
					       									<fo:block text-align="right">${(totGross/totMixKgs)?if_exists?string("##0.000")}</fo:block>
					       								</fo:table-cell>
					       							</fo:table-row>
					       							<fo:table-row>
				       									<fo:table-cell>
				       										<fo:block>------------------------------------------------------------------------------------------------------------------------------------------</fo:block>
				       									</fo:table-cell>
				       								</fo:table-row>
				       								</#if>
				       							</#if>
			       							</#list>
		       							</#list>
									</fo:table-body>
					 			</fo:table>
							</fo:block>
							
					    </fo:flow>		
				   	</fo:page-sequence>	
				<#else>
					<fo:page-sequence master-reference="main">
				    	<fo:flow flow-name="xsl-region-body" font-family="Courier,monospace">
				       		 <fo:block font-size="14pt">
				            	${uiLabelMap.NoOrdersFound}.
				       		 </fo:block>
				    	</fo:flow>
					</fo:page-sequence>
				</#if>
			</fo:root>
		</#escape>
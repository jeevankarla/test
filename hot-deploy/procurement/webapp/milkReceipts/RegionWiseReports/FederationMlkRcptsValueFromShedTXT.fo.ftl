<#escape x as x?xml>
    <fo:root xmlns:fo="http://www.w3.org/1999/XSL/Format">
        <fo:layout-master-set>
            <fo:simple-page-master master-name="main" page-height="12in" page-width="15in" margin-top=".3in" margin-left=".3in" margin-bottom=".3in">
                <fo:region-body margin-top="1.2in"/>
                <fo:region-before extent="1in"/>
                <fo:region-after extent="1in"/>
            </fo:simple-page-master>
        </fo:layout-master-set>
         ${setRequestAttribute("OUTPUT_FILENAME", "FederationUnitsMlkRcptsValueFrmShed.txt")}
    <#--<#if shedWiseTotalsMap?has_content> 
		<fo:page-sequence master-reference="main">
			<fo:static-content flow-name="xsl-region-before" font-family="Courier,monospace" font-size="9pt">
			</fo:static-content>
			<fo:flow flow-name="xsl-region-body" font-family="Courier,monospace" font-size="9pt">  
				<fo:block text-align="center">THE ANDHRA PRADESH DAIRY DEVELOPMENT COOPERATIVE  FEDERATION LIMITED</fo:block>
            	<fo:block text-align="center" keep-together="always">&#160;     ADDITIONAL WORKING CAPTITAL REQUIREMENT OF PERIOD FROM   ${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(fromDate, "dd-MM-yyyy")} TO:${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(thruDate, "dd-MM-yyyy")}    (RUPEES IN LAKHS)</fo:block>
               	<fo:block font-size="9pt">----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------</fo:block>
				<fo:block font-size="9pt">
					<fo:table>
						<fo:table-column column-width="20pt"/>
						<fo:table-column column-width="200pt"/>
						<fo:table-column column-width="80pt"/>
						<fo:table-body>
							<fo:table-row>
								<fo:table-cell>
									<fo:block keep-together="always"  white-space-collapse="false">&#160;                                                INCOME DETAILS                                  |                        EXPENDITURE DETAILS                         |         |         |</fo:block>
									<fo:block>----------------------|--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------</fo:block>
									<fo:block keep-together="always"  white-space-collapse="false">NAME OF THE  SHED     | MILK         OP        IUT-SENT    MILK &amp;      ACTUAL      OUT     TOTAL  |   MILK      STAFF       STORE       MILK&amp;       IUT-RECT    TOTAL |   ELIGI-| OP-COST | ADDL </fo:block>
									<fo:block keep-together="always"  white-space-collapse="false">&#160;                     |RECPTS       COST       INCOME      PRODUCTS    CASH       STAND    INCOME|  PROCURE    SALARIES     MATERLS     PRODUCTS    OTH-EXP     EXPNDR|   BILITY| RELESED | W.CAPTL </fo:block>
									<fo:block keep-together="always"  white-space-collapse="false">&#160;                     |VALUE       VALUE       VALUE       VALUE       RMTS       DUES     VALUE |   BILL      AMOUNT       VALUE       VALUE       VALUE       VALUE |   VALUE | VALUE   |  VALUE </fo:block>
									<fo:block>----------------------|-------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------</fo:block>
									<fo:block keep-together="always"  white-space-collapse="false">&#160;                     | (1)          (2)         (3)         (4)       (5)        (6)        (7)  |    (8)        (9)        (10)       (11)        (12)         (13)  |   (14) |    (15)  | (16) </fo:block>
									<fo:block>----------------------|-------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------</fo:block>
								</fo:table-cell>								
							</fo:table-row>
						</fo:table-body>
					</fo:table>
				</fo:block>     				   
				<fo:block font-family="Courier,monospace">
				 	<fo:table>
				 		<fo:table-column column-width="80pt"/>
   						<fo:table-column column-width="30pt"/>
   						<fo:table-column column-width="44pt"/>
   						<fo:table-column column-width="55pt"/>
   						<fo:table-column column-width="60pt"/>
   						<fo:table-column column-width="50pt"/>
   						<fo:table-column column-width="45pt"/>
   						<fo:table-column column-width="37pt"/>
   						<fo:table-column column-width="40pt"/>
   						<fo:table-column column-width="48pt"/>
   						<fo:table-column column-width="54pt"/>
   						<fo:table-column column-width="45pt"/>
   						<fo:table-column column-width="46pt"/>
   						<fo:table-column column-width="47pt"/>
   						<fo:table-column column-width="33pt"/>
   						<fo:table-column column-width="55pt"/>
   						<fo:table-column column-width="35pt"/>
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
			       									<fo:block keep-together="always" font-size="3pt">${(Static["org.ofbiz.order.order.OrderServices"].nameTrim((StringUtil.wrapString(shed.get("facilityName").toUpperCase())),16))?if_exists}</fo:block>
			       								</fo:table-cell>
			       								<fo:table-cell>
			       									<fo:block text-align="right" font-size="3pt">${(milkRcptAmt/100000)?if_exists?string("##0.000")}</fo:block>
			       								</fo:table-cell>
			       								<fo:table-cell>
			       									<fo:block text-align="right" font-size="3pt">${(mlkRcptOpCost/100000)?if_exists?string("##0.000")}</fo:block>
			       								</fo:table-cell>
			       								<fo:table-cell>
			       									<fo:block text-align="right" font-size="3pt">${(unitDetails.getValue().get("shedIutSentValue"))}</fo:block>
			       								</fo:table-cell>
			       								<fo:table-cell>
			       									<fo:block text-align="right" font-size="3pt">${(unitDetails.getValue().get("milkProductsSaleValue"))}</fo:block>
			       								</fo:table-cell>
			       								<fo:table-cell>
			       									<fo:block text-align="right" font-size="3pt">${(unitDetails.getValue().get("cashRemittance"))}</fo:block>
			       								</fo:table-cell>
			       								<fo:table-cell>
			       									<fo:block text-align="right" font-size="3pt">${(unitDetails.getValue().get("outStandDues"))}</fo:block>
			       								</fo:table-cell>
			       								<fo:table-cell>
			       									<fo:block text-align="right" font-size="3pt">${totalIncome?if_exists?string("##0.00")}</fo:block>
			       								</fo:table-cell>
			       								<fo:table-cell>
			       									<fo:block text-align="right" font-size="3pt">${(mlkProcBill/100000)?if_exists?string("##0.00")}</fo:block>
			       								</fo:table-cell>
			       								<fo:table-cell>
			       									<fo:block text-align="right" font-size="3pt">${(unitDetails.getValue().get("salaryAmt"))?if_exists?string("##0.00")}</fo:block>
			       								</fo:table-cell>
			       								<fo:table-cell>
			       									<fo:block text-align="right" font-size="3pt">${(unitDetails.getValue().get("storeMaterialAmt"))?if_exists?string("##0.00")}</fo:block>
			       								</fo:table-cell>
			       								<fo:table-cell>
			       									<fo:block text-align="right" font-size="3pt">${(unitDetails.getValue().get("milkProdValue"))?if_exists?string("##0.00")}</fo:block>
			       								</fo:table-cell>
			       								<#assign grIutRcptValue=grIutRcptValue+(unitDetails.getValue().get("shedMlkRecptValue")+(mlkRcptOpCost/100000))>
			       								<fo:table-cell>
			       									<fo:block text-align="right" font-size="3pt">${(unitDetails.getValue().get("shedMlkRecptValue")+(mlkRcptOpCost/100000))?if_exists?string("##0.000")}</fo:block>
			       								</fo:table-cell>
			       								<#assign totalExpAmt= (mlkProcBill/100000)+(unitDetails.getValue().get("salaryAmt"))+(unitDetails.getValue().get("storeMaterialAmt"))+(unitDetails.getValue().get("milkProdValue"))+(unitDetails.getValue().get("shedMlkRecptValue")+(mlkRcptOpCost/100000))>
			       								<#assign grTotExpAmt=grTotExpAmt+totalExpAmt>
			       								<fo:table-cell>
			       									<fo:block text-align="right" font-size="3pt">${(totalExpAmt)?if_exists?string("##0.000")}</fo:block>
			       								</fo:table-cell>
			       								<#assign grTotEligibilityAmt=grTotEligibilityAmt+(totalIncome-totalExpAmt)>
			       								<fo:table-cell>
			       									<fo:block text-align="right" font-size="3pt">${(totalIncome-totalExpAmt)?if_exists?string("##0.000")}</fo:block>
			       								</fo:table-cell>
			       								<#assign grTotOpcostRlsdAmt=grTotOpcostRlsdAmt+(unitDetails.getValue().get("opCostReleased"))>
			       								<fo:table-cell>
			       									<fo:block text-align="right" font-size="3pt">${(unitDetails.getValue().get("opCostReleased"))?if_exists?string("##0.000")}</fo:block>
			       								</fo:table-cell>
			       								<#assign grTotCapitalAmt=grTotCapitalAmt+((totalIncome-totalExpAmt)-(unitDetails.getValue().get("opCostReleased")))>
			       								<fo:table-cell>
			       									<fo:block text-align="right" font-size="3pt">${((totalIncome-totalExpAmt)-(unitDetails.getValue().get("opCostReleased")))?if_exists?string("##0.000")}</fo:block>
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
       							<fo:table-row>
									<fo:table-cell>
										<fo:block linefeed-treatment="preserve" font-size="2pt">&#xA;</fo:block>
									</fo:table-cell>
								</fo:table-row>
   							</#list>
							<fo:table-row>
								<fo:table-cell>
									<fo:block font-size="7pt">----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------</fo:block>
								</fo:table-cell>
							</fo:table-row>
							<fo:table-row>
   								<fo:table-cell>
   									<fo:block>TOTAL</fo:block>
   								</fo:table-cell>	       								
   								<fo:table-cell>
   									<fo:block font-size="3pt" text-align="right">${(grMlkRcptAmt)?if_exists?string("##0.000")}</fo:block>
   								</fo:table-cell>
   								<fo:table-cell>
   									<fo:block text-align="right" font-size="3pt">${(grOpCostAmt)?if_exists?string("##0.000")}</fo:block>
   								</fo:table-cell>
   								<fo:table-cell>
   									<fo:block text-align="right" font-size="3pt">0.00</fo:block>
   								</fo:table-cell>
   								<fo:table-cell>
   									<fo:block text-align="right" font-size="3pt">${(grProdValueAmt)}</fo:block>
   								</fo:table-cell>
   								<fo:table-cell>
   									<fo:block text-align="right" font-size="3pt">${(grCashRmtAmt)}</fo:block>
   								</fo:table-cell>
   								<fo:table-cell>
   									<fo:block text-align="right" font-size="3pt">${(grOutStandDueAmt)}</fo:block>
   								</fo:table-cell>
   								<fo:table-cell>
   									<fo:block text-align="right" font-size="3pt">${grIncomeAmt?if_exists?string("##0.00")}</fo:block>
   								</fo:table-cell>
   								<fo:table-cell>
   									<fo:block text-align="right" font-size="3pt">${(grMlkBillAmt)?if_exists?string("##0.00")}</fo:block>
   								</fo:table-cell>
   								<fo:table-cell>
   									<fo:block text-align="right" font-size="3pt">${(grSalaryAmt)?if_exists?string("##0.00")}</fo:block>
   								</fo:table-cell>
   								<fo:table-cell>
   									<fo:block text-align="right" font-size="3pt">${(grStoreAmt)?if_exists?string("##0.00")}</fo:block>
   								</fo:table-cell>
   								<fo:table-cell>
   									<fo:block text-align="right" font-size="3pt">${(grMlkProdValue)?if_exists?string("##0.00")}</fo:block>
   								</fo:table-cell>
   								<fo:table-cell>
   									<fo:block text-align="right" font-size="3pt">${(grIutRcptValue)?if_exists?string("##0.000")}</fo:block>
   								</fo:table-cell>
   								<fo:table-cell>
   									<fo:block text-align="right" font-size="3pt">${(grTotExpAmt)?if_exists?string("##0.000")}</fo:block>
   								</fo:table-cell>
   								<fo:table-cell>
   									<fo:block text-align="right" font-size="3pt">${(grTotEligibilityAmt)?if_exists?string("##0.000")}</fo:block>
   								</fo:table-cell>
   								<fo:table-cell>
   									<fo:block text-align="right" font-size="3pt">${(grTotOpcostRlsdAmt)?if_exists?string("##0.000")}</fo:block>
   								</fo:table-cell>
   								<fo:table-cell>
   									<fo:block text-align="right" font-size="3pt">${(grTotCapitalAmt)?if_exists?string("##0.000")}</fo:block>
   								</fo:table-cell>
   							</fo:table-row>
							<fo:table-row>
								<fo:table-cell>
									<fo:block >----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------</fo:block>
								</fo:table-cell>
							</fo:table-row>
						</fo:table-body>
	 				</fo:table>
				</fo:block>
				<fo:block linefeed-treatment="preserve" font-size="7pt">&#xA;</fo:block>
				<fo:block linefeed-treatment="preserve" font-size="7pt">&#xA;</fo:block>
				<fo:block linefeed-treatment="preserve" font-size="7pt">&#xA;</fo:block>
				<fo:block linefeed-treatment="preserve" font-size="7pt">&#xA;</fo:block>
				<fo:block keep-togehter="always" font-size="8pt" white-space-collapse="false">Copy submitted to the Managing Diretor,                                                                                         DEPUTY DIRECTOR  (MIS)</fo:block>
				<fo:block keep-togehter="always" font-size="8pt">Copy submitted to the Executive Director,</fo:block>
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
		</#if> -->
    
	<#if shedWiseTotalsMap?has_content> 
		<fo:page-sequence master-reference="main">
			<fo:static-content flow-name="xsl-region-before" font-family="Courier,monospace">
				<fo:block font-size="5pt">VST_ASCII-018 </fo:block>
				<fo:block text-align="center">UNIT-WISE MILK BILLING OF MILK RECEIVED FROM MILK SHEDS TO MPF..HYDERABAD PERIOD FROM : ${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(fromDate, "dd-MM-yyyy")} TO:${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(thruDate, "dd-MM-yyyy")}</fo:block>
            	<fo:block text-align="center">&#160;                        ONLY FOR BOOK ADJUSTMENT NOT FOR PAYMENT        ANNEXURE-1</fo:block>
               	<fo:block>------------------------------------------------------------------------------------------------------------------------------------------------------</fo:block>
				<fo:block>
					<fo:table>
						<fo:table-column column-width="30pt"/>
						<fo:table-column column-width="200pt"/>
						<fo:table-column column-width="80pt"/>
						<fo:table-body>
							<fo:table-row>
								<fo:table-cell>
									<fo:block keep-together="always"  white-space-collapse="false">NAME OF THE UNIT/SHED    MILK     QUANTITY    QUANTITY      TOTAL     TOTAL    PROC RATE   MILK     OPCOST        TOTAL       TOTAL     AVG   AVG</fo:block>
									<fo:block keep-together="always"  white-space-collapse="false">&#160;                        TYPE     (LTS)         (KGS)      KG-FAT     KG-SNF   PER/KG     AMOUNT    PER/LT      OP COST       AMOUNT    FAT   SNF</fo:block>
								</fo:table-cell>								
							</fo:table-row>
						</fo:table-body>
					</fo:table>
				</fo:block>  
				<fo:block>------------------------------------------------------------------------------------------------------------------------------------------------------</fo:block>       				   
			</fo:static-content>
			<fo:flow flow-name="xsl-region-body" font-family="Courier,monospace"> 
				<fo:block font-family="Courier,monospace">
				 	<fo:table>
				 		<fo:table-column column-width="150pt"/>
   						<fo:table-column column-width="55pt"/>
   						<fo:table-column column-width="85pt"/>
   						<fo:table-column column-width="85pt"/>
   						<fo:table-column column-width="85pt"/>
   						<fo:table-column column-width="90pt"/>
   						<fo:table-column column-width="55pt"/>
   						<fo:table-column column-width="90pt"/>
   						<fo:table-column column-width="60pt"/>
   						<fo:table-column column-width="105pt"/>
   						<fo:table-column column-width="100pt"/>
   						<fo:table-column column-width="50pt"/>
   						<fo:table-column column-width="45pt"/>
   						<fo:table-column column-width="80pt"/>
   						<fo:table-body> 
   							<#assign totLtrs=0>
   							<#assign totKgs=0>
   							<#assign totkgFat=0>
   							<#assign totkgSnf=0>
   							<#assign totmilkAmt=0>
   							<#assign totalOpCost=0>
   							<#assign totalAmount=0>
   							
       						<#assign shedWiseList=shedWiseTotalsMap.entrySet()>
       						<#assign shedCount = 0>
       						<#list shedWiseList as shedDetails>
       							<#assign milkAmt=0>
       							<#assign totOpCost=0>
       							<#assign totAmount=0>
       							<#assign unitDetailsList=shedDetails.getValue().entrySet()>
       							<#list unitDetailsList as unitDetails>
       								<#if unitDetails.getKey() !="TOTAL">
	       								<#assign unitDetailValues = delegator.findOne("Facility", {"facilityId" : unitDetails.getKey()}, true)>
	       								<#if unitDetails.getValue().get("qtyLtrs") !=0>
											<fo:table-row>
			       								<fo:table-cell>
			       									<fo:block >${unitDetailValues.get("facilityName")?if_exists}</fo:block>
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
			       									<fo:block text-align="right">${unitDetails.getValue().get("rate")?if_exists?string("##0.00")}</fo:block>
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
		       							</#if>
		       						<#else>
	       								<#if unitDetails.getValue().get("qtyLtrs") !=0>
	       								<#assign shedCount = shedCount+1>
	       								<fo:table-row>
	       									<fo:table-cell>
	       										<fo:block>------------------------------------------------------------------------------------------------------------------------------------------------------</fo:block>
	       									</fo:table-cell>
	       								</fo:table-row>
	       								
		       								<fo:table-row>
		       									<#assign shedValues = delegator.findOne("Facility", {"facilityId" : shedDetails.getKey()}, true)>
			       								<fo:table-cell>
			       									<fo:block>${Static["org.ofbiz.order.order.OrderServices"].nameTrim((StringUtil.wrapString(shedValues.get("facilityName").toUpperCase())),18)}</fo:block>
			       								</fo:table-cell>
			       								<fo:table-cell>
			       									<fo:block text-align="right"></fo:block>
			       								</fo:table-cell>
			       								<fo:table-cell>
			       									<fo:block text-align="right">${unitDetails.getValue().get("qtyLtrs")?if_exists?string("##0.0")}</fo:block>
			       									<#assign totLtrs=totLtrs+unitDetails.getValue().get("qtyLtrs")>
			       								</fo:table-cell>
			       								<fo:table-cell>
			       									<fo:block text-align="right">${unitDetails.getValue().get("qtyKgs")?if_exists?string("##0.0")}</fo:block>
			       									<#assign totKgs=totKgs+unitDetails.getValue().get("qtyKgs")>
			       								</fo:table-cell>
			       								<fo:table-cell>
			       									<fo:block text-align="right">${unitDetails.getValue().get("kgFat")?if_exists?string("##0.00")}</fo:block>
			       									<#assign totkgFat=totkgFat+unitDetails.getValue().get("kgFat")>
			       								</fo:table-cell>
			       								<fo:table-cell>
			       									<fo:block text-align="right">${unitDetails.getValue().get("kgSnf")?if_exists?string("##0.00")}</fo:block>
			       									<#assign totkgSnf=totkgSnf+unitDetails.getValue().get("kgSnf")>
			       								</fo:table-cell>
			       								<fo:table-cell>
			       									<fo:block text-align="right"><#if unitDetails.getValue().get("qtyKgs") !=0>${(milkAmt/unitDetails.getValue().get("qtyKgs"))?if_exists?string("##0.00")}</#if></fo:block>
			       								</fo:table-cell>
			       								<fo:table-cell>
			       									<fo:block text-align="right">${(milkAmt)?string("##0.00")}</fo:block>
			       									<#assign totmilkAmt=totmilkAmt+milkAmt>
			       								</fo:table-cell>
			       								<fo:table-cell>
			       									<fo:block text-align="right"></fo:block>
			       								</fo:table-cell>
			       								<fo:table-cell>
			       									<fo:block text-align="right">${(totOpCost)?string("##0.00")}</fo:block>
			       									<#assign totalOpCost=totalOpCost+totOpCost>
			       								</fo:table-cell>
			       								<fo:table-cell>
			       									<fo:block text-align="right">${(totAmount)?string("##0.00")}</fo:block>
			       									<#assign totalAmount=totalAmount+totAmount>
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
	       										<fo:block>--------------------------------------------------------------------------------------------------------------------------------------------------</fo:block>
	       									</fo:table-cell>
	       								</fo:table-row>
	       								<#if (shedCount>6)>
		       									<#assign shedCount =0>
		       									<fo:table-row>
		       									<fo:table-cell>
		       										<fo:block font-family="Courier,monospace" font-size="10pt" break-before="page"/>
		       									</fo:table-cell>
		       								</fo:table-row>
		       								
		       								</#if>
	       								</#if>
		       						</#if>
	       						</#list>
       						</#list>
       						<fo:table-row>
   								<fo:table-cell>
   									<fo:block>FEDERATION TOTAL</fo:block>
   								</fo:table-cell>
   								<fo:table-cell>
   									<fo:block text-align="right"></fo:block>
   								</fo:table-cell>
   								<fo:table-cell>
   									<fo:block text-align="right">${totLtrs?if_exists?string("##0.0")}</fo:block>
   								</fo:table-cell>
   								<fo:table-cell>
   									<fo:block text-align="right">${totKgs?if_exists?string("##0.0")}</fo:block>
   								</fo:table-cell>
   								<fo:table-cell>
   									<fo:block text-align="right">${totkgFat?if_exists?string("##0.00")}</fo:block>
   								</fo:table-cell>
   								<fo:table-cell>
   									<fo:block text-align="right">${totkgSnf?if_exists?string("##0.00")}</fo:block>
   								</fo:table-cell>
   								<fo:table-cell>
   									<fo:block text-align="right"><#if totKgs !=0>${(totmilkAmt/totKgs)?if_exists?string("##0.00")}</#if></fo:block>
   								</fo:table-cell>
   								<fo:table-cell>
   									<fo:block text-align="right">${(totmilkAmt)?string("##0.00")}</fo:block>
   								</fo:table-cell>
   								<fo:table-cell>
   									<fo:block text-align="right"></fo:block>
   								</fo:table-cell>
   								<fo:table-cell>
   									<fo:block text-align="right">${(totalOpCost)?string("##0.00")}</fo:block>
   								</fo:table-cell>
   								<fo:table-cell>
   									<fo:block text-align="right">${(totalAmount)?string("##0.00")}</fo:block>
   								</fo:table-cell>
   								<fo:table-cell>
   									<fo:block text-align="right"><#if totKgs !=0>${((totkgFat*100)/totKgs)?if_exists?string("##0.00")}</#if></fo:block>
   								</fo:table-cell>
   								<fo:table-cell>
   									<fo:block text-align="right"><#if totKgs !=0>${((totkgSnf*100)/totKgs)?if_exists?string("##0.00")}</#if></fo:block>
   								</fo:table-cell>
   							</fo:table-row>
   							<fo:table-row>
								<fo:table-cell>
									<fo:block>--------------------------------------------------------------------------------------------------------------------------------------------------</fo:block>
								</fo:table-cell>
							</fo:table-row>
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
               	<fo:block>--------------------------------------------------------------------------------------------------------------------------------------------------------</fo:block>
				<fo:block>
					<fo:table>
						<fo:table-column column-width="20pt"/>
						<fo:table-column column-width="200pt"/>
						<fo:table-column column-width="80pt"/>
						<fo:table-body>
							<fo:table-row>
								<fo:table-cell>
									<fo:block keep-together="always"  white-space-collapse="false">&#160;                         |   TOTAL BUFFALO MILK   |     TOTAL COW MILK   |     TOTAL MIXED MILK     |    TOTAL   | TOTAL    |   GROSS    | PROC   <!--| AVG  |AVG  |--></fo:block>
									<fo:block keep-together="always"  white-space-collapse="false">&#160;                         |------------------------|----------------------|--------------------------|            |  ADDN AMT |           | RATE   <!--| FAT  |SNF  |--></fo:block>
									<fo:block keep-together="always"  white-space-collapse="false">NAME OF THE  SHED         |   KGS        VALUE     |   KGS        VALUE   |      KGS        VALUE    |     TIP    | +COMSN   |   VALUE    | PER/KG <!--|  %   |  %  |--></fo:block>
									<fo:block>-------------------------------------------------------------------------------------------------------------------------------------------------------</fo:block>
								</fo:table-cell>								
							</fo:table-row>
						</fo:table-body>
					</fo:table>
				</fo:block>   
			</fo:static-content>
			<fo:flow flow-name="xsl-region-body" font-family="Courier,monospace" font-size="11pt">
				<fo:block font-family="Courier,monospace">
				 	<fo:table>
				 		<#assign shedtotBmKgs=0>
   						<#assign shedtotCmKgs=0>
   						<#assign shedtotBmAmt=0>
   						<#assign shedtotCmAmt=0>
   						<#assign shedtotMixKgs=0>
   						<#assign shedtotMixAmt=0>
   						<#assign shedtotTip=0>
   						<#assign shedtotAddnComn=0>
   						<#assign shedtotGross=0>
				 		<fo:table-column column-width="120pt"/>
   						<fo:table-column column-width="98pt"/>
   						<fo:table-column column-width="90pt"/>
   						<fo:table-column column-width="80pt"/>
   						<fo:table-column column-width="91pt"/>
   						<fo:table-column column-width="80pt"/>
   						<fo:table-column column-width="100pt"/>
   						<fo:table-column column-width="78pt"/>
   						<fo:table-column column-width="81pt"/>
   						<fo:table-column column-width="97pt"/>
   						<fo:table-column column-width="45pt"/>
   						<fo:table-body> 
       						<#assign shedWiseList=shedWiseTotalsMap.entrySet()>       						
       						<#assign shedCount = 0>
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
           								<#assign bmQtyKgs=0>
           								<#if unitDetails.getValue().get("procurement").get("BMQtyKgs")?has_content>
           									<#assign bmQtyKgs=unitDetails.getValue().get("procurement").get("BMQtyKgs")>
           								</#if>
           								<#assign cmQtyKgs=0>
           								<#if unitDetails.getValue().get("procurement").get("CMQtyKgs")?has_content>
           									<#assign cmQtyKgs=unitDetails.getValue().get("procurement").get("CMQtyKgs")>
           								</#if>
           								<#assign bmPrice=0>
           								<#if unitDetails.getValue().get("procurement").get("BMPrice")?has_content>
           									<#assign bmPrice=unitDetails.getValue().get("procurement").get("BMPrice")>
           								</#if>
           								<#assign cmPrice=0>
           								<#if unitDetails.getValue().get("procurement").get("CMPrice")?has_content>
           									<#assign cmPrice=unitDetails.getValue().get("procurement").get("CMPrice")>
           								</#if>
           								<#assign mixedQtyKgs=0>
           								<#if unitDetails.getValue().get("procurement").get("mixProcQty")?has_content>
           									<#assign mixedQtyKgs=unitDetails.getValue().get("procurement").get("mixProcQty")>
           								</#if>
           								<#assign mixMlkAmt=0>
           								<#if unitDetails.getValue().get("procurement").get("mixedMlkAmount")?has_content>
           									<#assign mixMlkAmt=unitDetails.getValue().get("procurement").get("mixedMlkAmount")>
           								</#if>
           								<#assign tipAmt=0>
           								<#if unitDetails.getValue().get("procurement").get("tipAmount")?has_content>
           									<#assign tipAmt=unitDetails.getValue().get("procurement").get("tipAmount")>
           								</#if>
           								<#assign addnComn=0>
           								<#if unitDetails.getValue().get("procurement").get("addnAmt")?has_content>
           									<#assign addnComn=unitDetails.getValue().get("procurement").get("addnAmt")>
           								</#if>
           								<#assign grossAmt=0>
           								<#if unitDetails.getValue().get("procurement").get("grossAmt")?has_content>
           									<#assign grossAmt=unitDetails.getValue().get("procurement").get("grossAmt")>
           								</#if>
           								<#assign totBmKgs=totBmKgs+bmQtyKgs>
		           						<#assign totCmKgs=totCmKgs+cmQtyKgs>
		           						<#assign totBmAmt=totBmAmt+bmPrice>
		           						<#assign totCmAmt=totCmAmt+cmPrice>
		           						<#assign totMixKgs=totMixKgs+mixedQtyKgs>
		           						<#assign totMixAmt=totMixAmt+mixMlkAmt>
		           						<#assign totTip=totTip+tipAmt>
		           						<#assign totAddnComn=totAddnComn+addnComn>
		           						<#assign totGross=totGross+grossAmt>
										<fo:table-row>
		       								<fo:table-cell>
		       									<fo:block>${unitDetailValues.get("facilityName")?if_exists}</fo:block>
		       								</fo:table-cell>				       								
		       								<fo:table-cell>
		       									<fo:block text-align="right">${bmQtyKgs?if_exists?string("##0.0")}</fo:block>
		       								</fo:table-cell>
		       								<fo:table-cell>
		       									<fo:block text-align="right">${bmPrice?if_exists?string("##0.00")}</fo:block>
		       								</fo:table-cell>
		       								<fo:table-cell>
		       									<fo:block text-align="right">${cmQtyKgs?if_exists?string("##0.0")}</fo:block>
		       								</fo:table-cell>
		       								<fo:table-cell>
		       									<fo:block text-align="right">${cmPrice?if_exists?string("##0.00")}</fo:block>
		       								</fo:table-cell>
		       								<fo:table-cell>
		       									<fo:block text-align="right">${mixedQtyKgs?if_exists?string("##0.0")}</fo:block>
		       								</fo:table-cell>
		       								<fo:table-cell>
		       									<fo:block text-align="right">${mixMlkAmt?if_exists?string("##0.00")}</fo:block>
		       								</fo:table-cell>
		       								<fo:table-cell>
		       									<fo:block text-align="right">${tipAmt?if_exists?string("##0.00")}</fo:block>
		       								</fo:table-cell>
		       								<fo:table-cell>
		       									<fo:block text-align="right">${addnComn?if_exists?string("##0.00")}</fo:block>
		       								</fo:table-cell>
		       								<fo:table-cell>
		       									<fo:block text-align="right">${grossAmt?if_exists?string("##0.00")}</fo:block>
		       								</fo:table-cell>
		       								<#assign procRate=0>
		       								<#if unitDetails.getValue().get("procurement").get("rate")?has_content>
		       									<#assign procRate=unitDetails.getValue().get("procurement").get("rate")>
		       								</#if>
		       								<fo:table-cell>
					       							<fo:block text-align="right" font-size="12pt" text-indent="10pt">${procRate?if_exists?string("##0.00")}</fo:block>
		       								</fo:table-cell>					       								
		       							</fo:table-row>
		       						</#if>	
	       							<#else>
	       								<#assign shedCount = shedCount+1>
	       								<#if totMixKgs !=0>
		       								<fo:table-row>
		       									<fo:table-cell>
		       										<fo:block>------------------------------------------------------------------------------------------------------------------------------------------------------</fo:block>
		       									</fo:table-cell>
		       								</fo:table-row>
		       								<fo:table-row>
		       									<#assign shedValues = delegator.findOne("Facility", {"facilityId" : shedDetails.getKey()}, true)>
			       								<fo:table-cell>
			       									<fo:block>${Static["org.ofbiz.order.order.OrderServices"].nameTrim((StringUtil.wrapString(shedValues.get("facilityName").toUpperCase())),18)}</fo:block>
			       								</fo:table-cell>
			       								<fo:table-cell>
			       									<fo:block text-align="right">${totBmKgs?if_exists?string("##0.0")}</fo:block>
			       									<#assign shedtotBmKgs=shedtotBmKgs+totBmKgs>
			       								</fo:table-cell>
			       								<fo:table-cell>
			       									<fo:block text-align="right">${totBmAmt?if_exists?string("##0.00")}</fo:block>
			       									<#assign shedtotBmAmt=shedtotBmAmt+totBmAmt>
			       								</fo:table-cell>
			       								<fo:table-cell>
			       									<fo:block text-align="right">${totCmKgs?if_exists?string("##0.0")}</fo:block>
			       									<#assign shedtotCmKgs=shedtotCmKgs+totCmKgs>
			       								</fo:table-cell>
			       								<fo:table-cell>
			       									<fo:block text-align="right">${totCmAmt?if_exists?string("##0.00")}</fo:block>
			       									<#assign shedtotCmAmt=shedtotCmAmt+totCmAmt>
			       								</fo:table-cell>
			       								<fo:table-cell>
			       									<fo:block text-align="right">${totMixKgs?if_exists?string("##0.0")}</fo:block>
			       									<#assign shedtotMixKgs=shedtotMixKgs+totMixKgs>
			       								</fo:table-cell>
			       								<fo:table-cell>
			       									<fo:block text-align="right">${totMixAmt?if_exists?string("##0.00")}</fo:block>
			       									<#assign shedtotMixAmt=shedtotMixAmt+totMixAmt>
			       								</fo:table-cell>
			       								<fo:table-cell>
			       									<fo:block text-align="right">${totTip?if_exists?string("##0.00")}</fo:block>
			       									<#assign shedtotTip=shedtotTip+totTip>
			       								</fo:table-cell>
			       								<fo:table-cell>
			       									<fo:block text-align="right">${totAddnComn?if_exists?string("##0.00")}</fo:block>
			       									<#assign shedtotAddnComn=shedtotAddnComn+totAddnComn>
			       								</fo:table-cell>
			       								<fo:table-cell>
			       									<fo:block text-align="right">${totGross?if_exists?string("##0.00")}</fo:block>
			       									<#assign shedtotGross=shedtotGross+totGross>
			       								</fo:table-cell>
			       								<fo:table-cell>
					       									<fo:block text-align="right" font-size="12pt" text-indent="10pt">${(totGross/totMixKgs)?if_exists?string("##0.00")}</fo:block>
			       								</fo:table-cell>
			       							</fo:table-row>
			       							<fo:table-row>
		       									<fo:table-cell>
		       										<fo:block>-------------------------------------------------------------------------------------------------------------------------------------------------------</fo:block>
		       									</fo:table-cell>
		       								</fo:table-row>
		       								
		       								<#if (shedCount>6)>
		       									<#assign shedCount =0>
		       									<fo:table-row>
		       									<fo:table-cell>
		       										<fo:block font-family="Courier,monospace" font-size="10pt" break-before="page"/>
		       									</fo:table-cell>
		       								</fo:table-row>
		       								</#if>
	       								</#if>
	       							</#if>
       							</#list>
   							</#list>
   							<fo:table-row>
   								<fo:table-cell>
   									<fo:block>FEDERATION TOTAL</fo:block>
   								</fo:table-cell>
   								<fo:table-cell>
   									<fo:block text-align="right">${shedtotBmKgs?if_exists?string("##0.0")}</fo:block>
   								</fo:table-cell>
   								<fo:table-cell>
   									<fo:block text-align="right">${shedtotBmAmt?if_exists?string("##0.00")}</fo:block>
   								</fo:table-cell>
   								<fo:table-cell>
   									<fo:block text-align="right">${shedtotCmKgs?if_exists?string("##0.0")}</fo:block>
   								</fo:table-cell>
   								<fo:table-cell>
   									<fo:block text-align="right">${shedtotCmAmt?if_exists?string("##0.00")}</fo:block>
   								</fo:table-cell>
   								<fo:table-cell>
   									<fo:block text-align="right">${shedtotMixKgs?if_exists?string("##0.0")}</fo:block>
   								</fo:table-cell>
   								<fo:table-cell>
   									<fo:block text-align="right">${shedtotMixAmt?if_exists?string("##0.00")}</fo:block>
   								</fo:table-cell>
   								<fo:table-cell>
   									<fo:block text-align="right">${shedtotTip?if_exists?string("##0.00")}</fo:block>
   								</fo:table-cell>
   								<fo:table-cell>
   									<fo:block text-align="right">${shedtotAddnComn?if_exists?string("##0.00")}</fo:block>
   								</fo:table-cell>
   								<fo:table-cell>
   									<fo:block text-align="right">${shedtotGross?if_exists?string("##0.00")}</fo:block>
   								</fo:table-cell>
   								<fo:table-cell>
	       									<fo:block text-align="right" font-size="12pt" text-indent="10pt">${(shedtotGross/shedtotMixKgs)?if_exists?string("##0.00")}</fo:block>
   								</fo:table-cell>
   							</fo:table-row>
   							<fo:table-row>
								<fo:table-cell>
									<fo:block>-------------------------------------------------------------------------------------------------------------------------------------------------------</fo:block>
								</fo:table-cell>
							</fo:table-row>
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
	
	
	
	<!-- Qulity Details -->
	
	
	<#if shedWiseTotalsMap?has_content> 
		<fo:page-sequence master-reference="main">
			<fo:static-content flow-name="xsl-region-before" font-family="Courier,monospace" font-size="11pt">
				<fo:block text-align="left">UNIT WISE MILK BILLING PARTICULARS AT MILK SHED LEVEL PERIOD FROM :  ${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(fromDate, "dd-MM-yyyy")} TO:${Static["org.ofbiz.base.util.UtilDateTime"].toDateString(thruDate, "dd-MM-yyyy")}</fo:block>
               	<fo:block>--------------------------------------------------------------------------------------------------------</fo:block>
				<fo:block>
					<fo:table>
						<fo:table-column column-width="20pt"/>
						<fo:table-column column-width="200pt"/>
						<fo:table-column column-width="80pt"/>
						<fo:table-body>
							<fo:table-row>
								<fo:table-cell>
									<fo:block keep-together="always"  white-space-collapse="false">&#160;                         |   TOTAL BUFFALO MILK   |     TOTAL COW MILK   |     TOTAL MIXED MILK     </fo:block>
									<fo:block keep-together="always"  white-space-collapse="false">&#160;                         |------------------------|----------------------|--------------------------</fo:block>
									<fo:block keep-together="always"  white-space-collapse="false">NAME OF THE  SHED         |   KGFAT        KGSNF   |   KGFAT        KGSNF |      KGFAT        KGSNF   </fo:block>
									<fo:block>--------------------------------------------------------------------------------------------------------</fo:block>
								</fo:table-cell>								
							</fo:table-row>
						</fo:table-body>
					</fo:table>
				</fo:block>   
			</fo:static-content>
			<fo:flow flow-name="xsl-region-body" font-family="Courier,monospace" font-size="11pt">
				<fo:block font-family="Courier,monospace">
				 	<fo:table>
				 		<#assign shedtotBmKgFat=0>
   						<#assign shedtotCmKgFat=0>
   						<#assign shedtotBmKgSnf=0>
   						<#assign shedtotCmKgSnf=0>
   						<#assign shedtotMixKgFat=0>
   						<#assign shedtotMixKgSnf=0>
				 		<fo:table-column column-width="120pt"/>
   						<fo:table-column column-width="98pt"/>
   						<fo:table-column column-width="90pt"/>
   						<fo:table-column column-width="80pt"/>
   						<fo:table-column column-width="91pt"/>
   						<fo:table-column column-width="80pt"/>
   						<fo:table-column column-width="100pt"/>
   						<fo:table-column column-width="78pt"/>
   						<fo:table-body> 
       						<#assign shedWiseList=shedWiseTotalsMap.entrySet()>       						
       						<#assign shedCount = 0>
       						<#list shedWiseList as shedDetails>
       							<#assign totBmKgFat=0>
           						<#assign totCmKgFat=0>
           						<#assign totBmKgSnf=0>
           						<#assign totCmKgSnf=0>
           						<#assign totMixKgFat=0>
           						<#assign totMixKgSnf=0>
           						
       							<#assign unitDetailsList=shedDetails.getValue().entrySet()>
       							<#list unitDetailsList as unitDetails>
       								<#if unitDetails.getKey() !="TOTAL">
       								<#if (unitDetails.getValue().get("procurement").get("mixProcQty")?exists) && (unitDetails.getValue().get("procurement").get("mixProcQty") !=0)>
           								<#assign unitDetailValues = delegator.findOne("Facility", {"facilityId" : unitDetails.getKey()}, true)>
           								<#assign bmKgFat=0>
           								<#if unitDetails.getValue().get("procurement").get("BMQtyKgs")?has_content>
           									<#assign bmKgFat=unitDetails.getValue().get("procurement").get("BMKgFat")>
           								</#if>
           								<#assign cmKgFat=0>
           								<#if unitDetails.getValue().get("procurement").get("CMQtyKgs")?has_content>
           									<#assign cmKgFat=unitDetails.getValue().get("procurement").get("CMKgFat")>
           								</#if>
           								<#assign bmKgSnf=0>
           								<#if unitDetails.getValue().get("procurement").get("BMKgSnf")?has_content>
           									<#assign bmKgSnf=unitDetails.getValue().get("procurement").get("BMKgSnf")>
           								</#if>
           								<#assign cmKgSnf=0>
           								<#if unitDetails.getValue().get("procurement").get("CMKgSnf")?has_content>
           									<#assign cmKgSnf=unitDetails.getValue().get("procurement").get("CMKgSnf")>
           								</#if>
           								<#assign mixedKgFat=0>
           								<#if unitDetails.getValue().get("procurement").get("mixProcKgFat")?has_content>
           									<#assign mixedKgFat=unitDetails.getValue().get("procurement").get("mixProcKgFat")>
           								</#if>
           								<#assign mixedKgSnf=0>
           								<#if unitDetails.getValue().get("procurement").get("mixProcKgFat")?has_content>
           									<#assign mixedKgSnf=unitDetails.getValue().get("procurement").get("mixProcKgSnf")>
           								</#if>
           								
           								<#assign totBmKgFat=totBmKgFat+bmKgFat>
		           						<#assign totCmKgFat=totCmKgFat+cmKgFat>
		           						<#assign totBmKgSnf=totBmKgSnf+bmKgSnf>
		           						<#assign totCmKgSnf=totCmKgSnf+cmKgSnf>
		           						<#assign totMixKgFat=totMixKgFat+mixedKgFat>
		           						<#assign totMixKgSnf=totMixKgSnf+mixedKgSnf>
										<fo:table-row>
		       								<fo:table-cell>
		       									<fo:block>${unitDetailValues.get("facilityName")?if_exists}</fo:block>
		       								</fo:table-cell>				       								
		       								<fo:table-cell>
		       									<fo:block text-align="right">${bmKgFat?if_exists?string("##0.0")}</fo:block>
		       								</fo:table-cell>
		       								<fo:table-cell>
		       									<fo:block text-align="right">${bmKgSnf?if_exists?string("##0.00")}</fo:block>
		       								</fo:table-cell>
		       								<fo:table-cell>
		       									<fo:block text-align="right">${cmKgFat?if_exists?string("##0.0")}</fo:block>
		       								</fo:table-cell>
		       								<fo:table-cell>
		       									<fo:block text-align="right">${cmKgSnf?if_exists?string("##0.00")}</fo:block>
		       								</fo:table-cell>
		       								<fo:table-cell>
		       									<fo:block text-align="right">${mixedKgFat?if_exists?string("##0.0")}</fo:block>
		       								</fo:table-cell>
		       								<fo:table-cell>
		       									<fo:block text-align="right">${mixedKgSnf?if_exists?string("##0.00")}</fo:block>
		       								</fo:table-cell>
		       							</fo:table-row>
		       						</#if>	
	       							<#else>
	       								<#assign shedCount = shedCount+1>
	       								<#if  totMixKgFat  !=0>
		       								<fo:table-row>
		       									<fo:table-cell>
		       										<fo:block>--------------------------------------------------------------------------------------------------------</fo:block>
		       									</fo:table-cell>
		       								</fo:table-row>
		       								<fo:table-row>
		       									<#assign shedValues = delegator.findOne("Facility", {"facilityId" : shedDetails.getKey()}, true)>
			       								<fo:table-cell>
			       									<fo:block>${Static["org.ofbiz.order.order.OrderServices"].nameTrim((StringUtil.wrapString(shedValues.get("facilityName").toUpperCase())),18)}</fo:block>
			       								</fo:table-cell>
			       								<fo:table-cell>
			       									<fo:block text-align="right">${totBmKgFat?if_exists?string("##0.0")}</fo:block>
			       									<#assign shedtotBmKgFat=shedtotBmKgFat+totBmKgFat>
			       								</fo:table-cell>
			       								<fo:table-cell>
			       									<fo:block text-align="right">${totBmKgSnf?if_exists?string("##0.00")}</fo:block>
			       									<#assign shedtotBmKgSnf=shedtotBmKgSnf+totBmKgSnf>
			       								</fo:table-cell>
			       								<fo:table-cell>
			       									<fo:block text-align="right">${totCmKgFat?if_exists?string("##0.0")}</fo:block>
			       									<#assign shedtotCmKgFat=shedtotCmKgFat+totCmKgFat>
			       								</fo:table-cell>
			       								<fo:table-cell>
			       									<fo:block text-align="right">${totCmKgSnf?if_exists?string("##0.00")}</fo:block>
			       									<#assign shedtotCmKgSnf=shedtotCmKgSnf+totCmKgSnf>
			       								</fo:table-cell>
			       								<fo:table-cell>
			       									<fo:block text-align="right">${totMixKgFat?if_exists?string("##0.0")}</fo:block>
			       									<#assign shedtotMixKgFat=shedtotMixKgFat+totMixKgFat>
			       								</fo:table-cell>
			       								<fo:table-cell>
			       									<fo:block text-align="right">${totMixKgSnf?if_exists?string("##0.00")}</fo:block>
			       									<#assign shedtotMixKgSnf=shedtotMixKgSnf+totMixKgSnf>
			       								</fo:table-cell>
			       							</fo:table-row>
			       							<fo:table-row>
		       									<fo:table-cell>
		       										<fo:block>--------------------------------------------------------------------------------------------------------</fo:block>
		       									</fo:table-cell>
		       								</fo:table-row>
		       								
		       								<#if (shedCount>6)>
		       									<#assign shedCount =0>
		       									<fo:table-row>
		       									<fo:table-cell>
		       										<fo:block font-family="Courier,monospace" font-size="10pt" break-before="page"/>
		       									</fo:table-cell>
		       								</fo:table-row>
		       								</#if>
	       								</#if>
	       							</#if>
       							</#list>
   							</#list>
   							<fo:table-row>
   								<fo:table-cell>
   									<fo:block>FEDERATION TOTAL</fo:block>
   								</fo:table-cell>
   								<fo:table-cell>
   									<fo:block text-align="right">${shedtotBmKgFat?if_exists?string("##0.0")}</fo:block>
   								</fo:table-cell>
   								<fo:table-cell>
   									<fo:block text-align="right">${shedtotBmKgSnf?if_exists?string("##0.00")}</fo:block>
   								</fo:table-cell>
   								<fo:table-cell>
   									<fo:block text-align="right">${shedtotCmKgFat?if_exists?string("##0.0")}</fo:block>
   								</fo:table-cell>
   								<fo:table-cell>
   									<fo:block text-align="right">${shedtotCmKgSnf?if_exists?string("##0.00")}</fo:block>
   								</fo:table-cell>
   								<fo:table-cell>
   									<fo:block text-align="right">${shedtotMixKgFat?if_exists?string("##0.0")}</fo:block>
   								</fo:table-cell>
   								<fo:table-cell>
   									<fo:block text-align="right">${shedtotMixKgSnf?if_exists?string("##0.00")}</fo:block>
   								</fo:table-cell>
   							</fo:table-row>
   							<fo:table-row>
								<fo:table-cell>
									<fo:block>--------------------------------------------------------------------------------------------------------</fo:block>
								</fo:table-cell>
							</fo:table-row>
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
	
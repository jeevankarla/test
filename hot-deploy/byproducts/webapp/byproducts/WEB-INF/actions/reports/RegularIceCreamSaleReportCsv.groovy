import org.ofbiz.base.util.*;
import org.ofbiz.entity.Delegator;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.entity.util.EntityUtil;
import java.util.*;
import java.lang.*;
import org.ofbiz.entity.*;
import org.ofbiz.entity.condition.*;
import org.ofbiz.base.util.UtilMisc;
import org.ofbiz.entity.condition.EntityCondition;
import org.ofbiz.entity.condition.EntityOperator;
import java.sql.*;
import javolution.util.FastList;
import javolution.util.FastMap;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import org.ofbiz.service.DispatchContext;
import org.ofbiz.service.GenericServiceException;
import org.ofbiz.service.ServiceUtil;
import org.ofbiz.party.party.PartyHelper;
import java.math.BigDecimal;
import java.math.MathContext;
import org.ofbiz.base.util.UtilNumber;
import in.vasista.vbiz.byproducts.ByProductNetworkServices;
import org.ofbiz.accounting.invoice.InvoiceWorker;
import in.vasista.vbiz.byproducts.SalesInvoiceServices;


dayWiseInvoice=context.get("dayWiseInvoice");
ppdMap=context.get("ppdMap");
vatAdjMap=context.get("vatAdjMap");
shippingDetails=context.get("shippingDetails");
categoryType=context.get("categoryType");
 totalQty=0;
 totalBasicRev=0;
 totalBedRev=0;
 totalVatRev=0;
 totalCstRev=0;
 totalCstRev=0;
 totalPpd=0;
 totalVatAdj=0;
 totalVat=0;
 grandTotal=0;
 Regularsaleslist=[];
 grandtotal=[:];
 dayWiseInvoice.each{ dayWiseTotalsDetails ->
	 invoiceMap=dayWiseTotalsDetails.getValue();
	 invoiceDate=dayWiseTotalsDetails.getKey();
	 sequenceId = null;
	 prvinvoiceId=" ";
	 invoiceMap.each{ eachinvoiceMap ->
		temptotal=[:];
		 invoiceId=eachinvoiceMap.getKey();
		 invoice1=eachinvoiceMap.getValue();
		 description=[];
		 quantity=[];
					invoice1.each{ invoiceDtls ->
						  if(ppdMap.get(invoiceId)){
						ppdMapvalue=ppdMap.get(invoiceId);
						totalPpd=totalPpd+ppdMapvalue;
						}
						if(vatAdjMap.get(invoiceId)){
						  vatAdjMapvalue=vatAdjMap.get(invoiceId);
						  totalVatAdj=totalVatAdj+vatAdjMapvalue;
						  }
						invoice=delegator.findOne("Invoice", ["invoiceId" :invoiceId], true);
						invoiceSeqDetails = delegator.findByAnd("BillOfSaleInvoiceSequence", [invoiceId : invoiceId]);
						temppartyName = org.ofbiz.party.party.PartyHelper.getPartyName(delegator, invoice.get("partyId"), false);
						sequenceId="";
						if(invoiceSeqDetails){
						  sequenceId = invoiceSeqDetails[0].get("sequenceId");
						}
						if("ICE_CREAM_AMUL".equals(categoryType)){
						  tempname=[shippingDetails.get(invoiceId).get("partyId")];
						  partyName=temppartyName+tempname;
						}else{
						partyName=temppartyName;
						}
						productTotals=invoiceDtls.get("productTotals");
						qty=0;
						exVal=0;
						edVal=0;
						vat=0;
						cst=0;
						total=0;
						productTotals.each{ productDtls ->
							  tempMap=[:];
						     qty=qty+productDtls.getValue().get("quantity");
						     exVal=exVal+productDtls.getValue().get("basicRevenue");
						     edVal=edVal+productDtls.getValue().get("bedRevenue");
						     vat=vat+productDtls.getValue().get("vatRevenue");
						     cst=cst+productDtls.getValue().get("cstRevenue");
						     total=total+productDtls.getValue().get("totalRevenue");
						      totalQty=totalQty+productDtls.getValue().get("quantity");
							  totalBasicRev=totalBasicRev+productDtls.getValue().get("basicRevenue");
							  totalBedRev=totalBedRev+productDtls.getValue().get("bedRevenue");
							  totalVatRev=totalVatRev+productDtls.getValue().get("vatRevenue");
							  totalCstRev=totalCstRev+productDtls.getValue().get("cstRevenue");
							  grandTotal=grandTotal+productDtls.getValue().get("totalRevenue");
							  if(prvinvoiceId==invoiceId){
								  tempMap.put("invoiceId", " ");
								  tempMap.put("invoiceSequenceId", " ");
								  tempMap.put("partyName", " ");
							  }else{
							  tempMap.put("invoiceId", invoiceId);
							  tempMap.put("invoiceSequenceId", sequenceId);
							  tempMap.put("partyName", partyName);
							  }
							  prvinvoiceId=invoiceId;
							  if(Util.Validate.isNotEmpty(parameters.categoryType)){
							  tempMap.put("Description", productDtls.getKey());
							  }else{
							  productDes = delegator.findOne("Product", [productId : productDtls.getKey()], false);
							  tempMap.put("Description", productDes.description);
							  }
							  tempMap.put("quantity", productDtls.getValue().get("quantity"));
							  tempMap.put("basicRevenue", productDtls.getValue().get("basicRevenue"));
							  tempMap.put("bedRevenue", productDtls.getValue().get("bedRevenue"));
							  tempMap.put("vatRevenue", productDtls.getValue().get("vatRevenue"));
							  tempMap.put("cstRevenue", productDtls.getValue().get("cstRevenue"));
							  tempMap.put("total", productDtls.getValue().get("totalRevenue"));
							  Regularsaleslist.add(tempMap);
						}
						temptotal.put("Description","Total");
						temptotal.put("quantity",qty);
						temptotal.put("basicRevenue",exVal);
						temptotal.put("bedRevenue",edVal);
						temptotal.put("vatRevenue",vat);
						temptotal.put("cstRevenue",cst);
						temptotal.put("total",total);
						}
		  Regularsaleslist.add(temptotal);
	 	}
	 }
 grandtotal.put("Description","Total");
 grandtotal.put("quantity",totalQty);
 grandtotal.put("basicRevenue",totalBasicRev);
 grandtotal.put("bedRevenue",totalBedRev);
 grandtotal.put("vatRevenue",totalVatRev);
 grandtotal.put("cstRevenue",totalCstRev);
 grandtotal.put("total",grandTotal);
 Regularsaleslist.add(grandtotal);
// Debug.log("Regularsaleslist====================="+Regularsaleslist);
 context.Regularsaleslist=Regularsaleslist;
 
 
 
 
 
 
 
 
 
 

import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.entity.GenericValue;
import org.ofbiz.widget.html.HtmlFormWrapper;
import org.ofbiz.manufacturing.jobshopmgt.ProductionRun;
import org.ofbiz.base.util.Debug;
import javolution.util.FastList;
import in.vasista.vbiz.production.ProductionServices;
dctx = dispatcher.getDispatchContext();

productionRunId=parameters.productionRunId;
if (productionRunId) {
	ProductionRun productionRun = new ProductionRun(productionRunId, delegator, dispatcher);
	if (productionRun.exist()) {
		productionRunId = productionRun.getGenericValue().workEffortId;
		context.productionRunId = productionRunId;
		context.productionRun = productionRun.getGenericValue();
		
		quantityToProduce = productionRun.getGenericValue().get("quantityToProduce") ?: 0.0;

		// Find the inventory items produced
		inventoryItems = delegator.findByAnd("WorkEffortInventoryProduced", [workEffortId : productionRunId]);
		context.inventoryItems = inventoryItems;
		if (inventoryItems) {
			lastWorkEffortInventoryProduced = (GenericValue)inventoryItems.get(inventoryItems.size() - 1);
			lastInventoryItem = lastWorkEffortInventoryProduced.getRelatedOne("InventoryItem");
			context.lastLotId = lastInventoryItem.lotId;
		}

		// Find if the production run can produce inventory.
		//quantityProduced = productionRun.getGenericValue().quantityProduced ?: 0.0;
		quantityProduced = productionRun.getQuantity() ?: 0.0;
		quantityRejected = productionRun.getGenericValue().quantityRejected ?: 0.0;

		lastTask = productionRun.getLastProductionRunRoutingTask();
		quantityDeclared = lastTask ? (lastTask.quantityProduced ?: 0.0) : 0.0 ;

		maxQuantity = quantityDeclared - quantityProduced;

		productionRunData = [:];
		productionRunData.workEffortId = productionRunId;
		productionRunData.productId = productionRun.getProductProduced().productId;
		productionRunData.product = productionRun.getProductProduced();
		if (maxQuantity > 0 && !"WIP".equals(productionRun.getProductProduced().productTypeId)) {
			productionRunData.quantity = maxQuantity;
			context.canProduce = "Y";
		}
		productionRunData.quantityToProduce = quantityToProduce;
		productionRunData.quantityProduced = quantityProduced;
		productionRunData.quantityRejected = quantityRejected;
		productionRunData.quantityRemaining = quantityToProduce - quantityProduced;
		productionRunData.estimatedCompletionDate = productionRun.getEstimatedCompletionDate();
		productionRunData.productionRunName = productionRun.getProductionRunName();
		productionRunData.description = productionRun.getDescription();
		productionRunData.estimatedStartDate = productionRun.getEstimatedStartDate();
		productionRunData.actualStartDate = productionRun.getGenericValue().getTimestamp("actualStartDate");
		productionRunData.actualCompletionDate = productionRun.getGenericValue().getTimestamp("actualCompletionDate");
		productionRunData.currentStatusId = productionRun.getGenericValue().currentStatusId;

		context.productionRunData = productionRunData;

		actionForm = parameters.actionForm ?: "beforeActionProductionRun";
		context.actionForm = actionForm;
	}
}

productionDetails = ProductionServices.getProductionRunDetails(dctx, [ workEffortId: productionRunId, userLogin: userLogin,]);

issuedProductsMap=productionDetails.get("issuedProductsMap");
declareProductsList=productionDetails.get("declareProductsList");
returnProductsList=productionDetails.get("returnProductsList");
qcComponentsList=productionDetails.get("qcComponentsList");
context.issuedProductsMap=issuedProductsMap; 
context.declareProductsList=declareProductsList;
context.returnProductsList=returnProductsList;
context.qcComponentsList=qcComponentsList;



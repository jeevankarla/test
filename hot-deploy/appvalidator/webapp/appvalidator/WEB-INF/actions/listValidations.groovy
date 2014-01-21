
validationList = [];
validationList.add(["component":"byproducts", "category":"Party", "id":"validateBoothParty", "name":"Party Validation", 
	"description" : "Check each active booth/party has valid name, description, owner and classification"]);
validationList.add(["component":"byproducts", "category":"Product", "id":"validateProduct", "name":"Product Validation",
	"description" : "Check each active byproducts product has valid name, brandName, description and uom"]);
validationList.add(["component":"byproducts", "category":"Product", "id":"validateProductPrice", "name":"Product Price Validation",
	"description" : "Check each active byproducts product has valid price for all classifications"]);
validationList.add(["component":"byproducts", "category":"Product", "id":"validatePartyProductPrice", "name":"Product Party Price Validation",
	"description" : "Check each active party has valid byproducts product price"]);
validationList.add(["component":"byproducts", "category":"Product", "id":"validateProductCategory", "name":"Product Category Validation",
	"description" : "Check each byproducts product is associated with required categories"]);
context.validationList = validationList;

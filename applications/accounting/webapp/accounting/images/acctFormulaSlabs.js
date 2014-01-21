
jQuery(document).ready(function() {
	//console.log('ready! isSlabBased=' + jQuery('[name=isSlabBased]').val());
	if (jQuery('[name=isSlabBased]').val() != 'Y') {
		jQuery('#accountFormulaSlabs').hide();
	}
	if (jQuery('#testFormula') && jQuery('[name=isSlabBased]').val() != 'Y') {
		jQuery('[name=slabAmount]').parent().parent().hide();		
	}
});
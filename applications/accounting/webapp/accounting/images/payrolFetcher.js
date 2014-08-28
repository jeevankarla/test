
jQuery(document).ready(function() {
	//console.log('ready! isSlabBased=' + jQuery('[name=isSlabBased]').val());
	jQuery.loading({onAjax:true, mask: 'true', img:'/images/jquery/plugins/loading/loading.gif'});		
});




function getPartyBenefitCost(partyId, benefitTypeId){
//    var request = '/accounting/control/getPartyBenefitCost';

    var request = 'getPartyBenefitCost';    
    optionList = '';
    jQuery.ajax({
        url: request,
        data: 'partyId=' + partyId + '&benefitTypeId=' + benefitTypeId, 
        dataType: 'json',
        async: false,
        type: 'POST',
        success: function(result){
            jQuery('#AddPartyBenefit_cost').val(result['amount']);
        }
    });
}

function getPartyDeductionAmount(partyId, deductionTypeId){
  var request = 'Deductionevent';    
  optionList = '';
  jQuery.ajax({
      url: request,
      data: 'partyId=' + partyId + '&deductionTypeId=' + deductionTypeId, 
      dataType: 'json',
      async: false,
      type: 'POST',
      success: function(result){
          jQuery('#Deductioncost').val(result['amount']);
      }
  });
}
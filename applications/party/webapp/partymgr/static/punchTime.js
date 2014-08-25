jQuery(document).ready(function () {
	jQuery("#AdminPunch_punchdate").datepicker({dateFormat:'yy-mm-dd'}).datepicker("option", { setDate:'0',maxDate:'-0d', minDate:'-1y' } );
	jQuery("#AdminPunch_punchdate").attr( 'readOnly' , 'true' );
	jQuery('#AdminPunch_punchtime').timepicker({ 
	showSecond: true,	
	timeFormat: 'hh:mm:ss',
	showOn: 'button',
        buttonImage: '/vasista/images/cal.gif',
	buttonImageOnly: false
       

 });
});



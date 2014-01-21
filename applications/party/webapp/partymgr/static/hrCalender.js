if(jQuery('#prevFlag').val() =="false"){
	jQuery("#prevLink").addClass('disabled');
	jQuery('#prevLink').bind('click', disableLink);   
    }

if(jQuery('#nextFlag').val() =="false"){
	jQuery("#nextLink").bind('click', disableLink);
	jQuery("#nextLink").addClass('disabled');   
    }

function disableLink(e) {
    // cancels the event
    e.preventDefault();

    return false;
}





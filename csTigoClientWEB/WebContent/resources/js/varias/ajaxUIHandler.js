//AJAX UI BLOCKING
var delayToStarAjaxAnimationInMillis = 350;
var currentAjaxUIExecution;

function manageAjaxEvents(data) {
    if (data.status == "begin") {
        currentAjaxUIExecution = setTimeout('showAjaxWaitMessage(true)', delayToStarAjaxAnimationInMillis);
    } else if (data.status == "complete") {
        clearTimeout(currentAjaxUIExecution);
        showAjaxWaitMessage(false);
    }
}

function showAjaxWaitMessage(show) {
    if (show) {
    	blocker.show();
    } else {
    	blocker.hide();
    }
}
//END AJAX UI BLOCKING

//AJAX UI ERROR HANDLER
function manageAjaxErrors(data) {
	growlVar.renderMessage({summary:'ERROR', detail: error, severity: 1});
}
//END AJAX UI ERROR HANDLER
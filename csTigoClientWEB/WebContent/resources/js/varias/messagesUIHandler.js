//CONFIRM MESSAGE HANDLER
function showStdConfirmDeleteRegistryMessage(callerObject) {
    return showConfirmMessage(recordDelete, callerObject);
}

function showStdSonfirmDeleteRegistryCollection(callerObject) {
    return showConfirmMessage(recordsDelete, callerObject);
}

function showStdSonfirmDeleteRegistryMeta(callerObject) {
    return showConfirmMessage(massDelete, callerObject);
}

function showStConfirmMarkOrders(callerObject) {
    return showConfirmMessage(markOrders, callerObject);
}

function showStdConfirmShowAllRegistries(callerObject) {
    return showConfirmMessage(showRecords, callerObject);
}

var lastConfirmResult = false;
var lastConfirmCallerObject = null;

function showConfirmMessage(message, callerObject) {
    if (lastConfirmCallerObject != callerObject) {
        lastConfirmCallerObject = callerObject;
        lastConfirmResult = false;
    }
    
    if (!lastConfirmResult) {
    	document.getElementById('dialogMessageForm:message').innerHTML = message;
        confirmation.show();        
        
        return false;
    } else {
        lastConfirmResult = false;
        lastConfirmCallerObject = null;
        return true;
    }
}

function confirmYesButtonClick() {
    lastConfirmResult = true;
    if (lastConfirmCallerObject != null) {
        lastConfirmCallerObject.click();
    }
}

function confirmNoButtonClick() {
    lastConfirmResult = false;
    lastConfirmCallerObject = null;
}
//END CONFIRM MESSAGE HANDLER

//ON SAVE FOR NAME CHANGE
function showStdConfirmHistorySaveMessage(callerObject) {
	if(document.getElementById('crudForm:changeName').value == document.getElementById('crudForm:employeeCodeEdit').value)
        return true;
    else
        return showConfirmHistorySaveMessage(callerObject);
}

function showConfirmHistorySaveMessage(callerObject) {
    if (lastConfirmCallerObject != callerObject) {
        lastConfirmCallerObject = callerObject;
        lastConfirmResult = false;
    }

    if (!lastConfirmResult) {
    	confirmationSaveHistory.show(); 
        return false;
    } else {
        lastConfirmResult = false;
        lastConfirmCallerObject = null;
        return true;
    }
}

function confirmHistoryYesButtonClick() {
    document.getElementById('crudForm:savehistory').setAttribute('value', 'true');
    
    lastConfirmResult = true;
    if (lastConfirmCallerObject != null) {
        lastConfirmCallerObject.click();
    }
}

function confirmHistoryNoButtonClick() {
    document.getElementById('crudForm:savehistory').setAttribute('value', 'false');
    
    lastConfirmResult = true;
    if (lastConfirmCallerObject != null) {
        lastConfirmCallerObject.click();
    }
}

function showStdConfirmUserphoneSaveMessage(callerObject) {
    return showConfirmUserphoneSaveMessage(callerObject);
}

function showConfirmUserphoneSaveMessage(callerObject) {
    if (lastConfirmCallerObject != callerObject) {
        lastConfirmCallerObject = callerObject;
        lastConfirmResult = false;
    }

    if (!lastConfirmResult) {
    	confirmationSaveUserphone.show(); 
        return false;
    } else {
        lastConfirmResult = false;
        lastConfirmCallerObject = null;
        return true;
    }
}

function confirmUserphoneYesButtonClick() {
    document.getElementById('crudForm:saveuserphone').setAttribute('value', 'true');
    
    lastConfirmResult = true;
    if (lastConfirmCallerObject != null) {
        lastConfirmCallerObject.click();
    }
}

function confirmUserphoneNoButtonClick() {
    document.getElementById('crudForm:saveuserphone').setAttribute('value', 'false');
    
    lastConfirmResult = true;
    if (lastConfirmCallerObject != null) {
        lastConfirmCallerObject.click();
    }
}
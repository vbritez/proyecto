function checkUncheckAllInForm(elementoChk, checkedFlag) {
    var formulario = elementoChk.form, z = 0;
    for(z = 0; z < formulario.length; z++) {
        if(formulario[z].type == 'checkbox' && formulario[z] != elementoChk && !formulario[z].disabled && formulario[z].id.indexOf('chkBoxSelected', 0) > 0) {
            formulario[z].checked = checkedFlag;
        }
    }
}


function checkUncheckAllOrders(elementoChk, checkedFlag) {
    var formulario = elementoChk.form, z = 0;
    for(z = 0; z < formulario.length; z++) {
        if(formulario[z].type == 'checkbox' && formulario[z] != elementoChk && !formulario[z].disabled && formulario[z].id.indexOf('chkBoxMarkOrder', 0) > 0) {
            formulario[z].checked = checkedFlag;
        }
    }
}

function checkUncheckAllDetail(elementoChk, checkedFlag) {
    var formulario = elementoChk.form, z = 0;
    for(z = 0; z < formulario.length; z++) {
        if(formulario[z].type == 'checkbox' && formulario[z] != elementoChk && !formulario[z].disabled && formulario[z].id.indexOf('detailChkBoxSelected', 0) > 0) {
            formulario[z].checked = checkedFlag;
        }
    }
}

function checkUncheckAllRoutes(elementoChk, checkedFlag) {
    var formulario = elementoChk.form, z = 0;
    for(z = 0; z < formulario.length; z++) {
        if(formulario[z].type == 'checkbox' && formulario[z] != elementoChk && !formulario[z].disabled && formulario[z].id.indexOf('chkBoxRoutes', 0) > 0) {
            formulario[z].checked = checkedFlag;
        }
    }
}

function limitTextArea(element, limit) {
    document.getElementById('smsSendForm:smscontentcount').value = limit - element.value.length;
    if (element.value.length > limit) {
        element.value = element.value.substring(0, limit);                                    
    }
}

function checkUncheckAllRoutesDetails(elementoChk, checkedFlag) {
    var formulario = elementoChk.form, z = 0;
    for(z = 0; z < formulario.length; z++) {
        if(formulario[z].type == 'checkbox' && formulario[z] != elementoChk && !formulario[z].disabled && formulario[z].id.indexOf('chkBoxRouteDetails', 0) > 0) {
            formulario[z].checked = checkedFlag;
        }
    }
}
function enableDisableInput(idElemento, enabledFlag) {
    var objeto = document.getElementById(idElemento);
    if (objeto != undefined) {
        objeto.disabled = !enabledFlag;
    }
}

function searchFieldOnchange(idListSearchOption, idtextSearchCriteria, idBtnSearch) {
    var listSearchOptionCorrectValue = document.getElementById(idListSearchOption).value != '-1';
    var textSearchCriteriaCorrectValue = document.getElementById(idtextSearchCriteria).value != '';
    enableDisableInput(idtextSearchCriteria, listSearchOptionCorrectValue);
    enableDisableInput(idBtnSearch, listSearchOptionCorrectValue && textSearchCriteriaCorrectValue);
}

function blockNonNumbers(inputObj, e, allowDecimal, allowNegative) {
    var key = null;
    var isCtrl = false;
    var keychar;
    var reg;

    if(window.event) {
        key = e.keyCode;
        isCtrl = window.event.ctrlKey;
    }
    else if(e.which) {
        key = e.which;
        isCtrl = e.ctrlKey;
    }

    if (isNaN(key)) return true;

    keychar = String.fromCharCode(key);

    // check for backspace or delete, or if Ctrl was pressed
    if (key == 8 || isCtrl)
    {
        return true;
    }

    reg = /\d/;
    var isFirstN = allowNegative ? keychar == '-' && inputObj.value.indexOf('-') == -1 : false;
    var isFirstD = allowDecimal ? keychar == '.' && inputObj.value.indexOf('.') == -1 : false;

    return isFirstN || isFirstD || reg.test(keychar);
}


function blockNonNumbersHour(inputObj, e, allowTwoPoints) {
    var key = null;
    var isCtrl = false;
    var keychar;
    var reg;

    if(window.event) {
        key = e.keyCode;
        isCtrl = window.event.ctrlKey;
    }
    else if(e.which) {
        key = e.which;
        isCtrl = e.ctrlKey;
    }

    if (isNaN(key)) return true;

    keychar = String.fromCharCode(key);

    // check for backspace or delete, or if Ctrl was pressed
    if (key == 8 || isCtrl)
    {
        return true;
    }

    reg = /\d/;
    var isFirstN = allowTwoPoints ? keychar == ':' && inputObj.value.indexOf(':') == -1 : false;

    return isFirstN || reg.test(keychar);
}

function getCaretPosition (inputObj) {
    var iCaretPos = 0;

    // IE Support
    if (document.selection) {
        // To get cursor position, get empty selection range
        var oSel = document.selection.createRange ().duplicate();
        // Move selection start to 0 position
        oSel.moveStart ('character', -inputObj.value.length);
        // The caret position is selection length
        iCaretPos = oSel.text.length;
    } else if (inputObj.selectionStart || inputObj.selectionStart == '0') {
        iCaretPos = inputObj.selectionStart;
    }

    return iCaretPos;
}

function setCaretPosition (inputObj, position) {
    if (inputObj.setSelectionRange) {
        inputObj.focus();
        inputObj.setSelectionRange(position, position);
    } else if (inputObj.createTextRange) {
        var range = inputObj.createTextRange();
        range.move('character', position);
        range.select();
    } else if(window.getSelection){
        s = window.getSelection();
        var r1 = document.createRange();

        var walker=document.createTreeWalker(inputObj, NodeFilter.SHOW_ELEMENT, null, false);
        var p = position;
        var n = inputObj;

        while(walker.nextNode()) {
            n = walker.currentNode;
            if(p > n.value.length) {
                p -= n.value.length;
            } else {
                break;
            }
        }
        n = n.firstChild;
        r1.setStart(n, p);
        r1.setEnd(n, p);

        s.removeAllRanges();
        s.addRange(r1);
    } else if (document.selection) {
        var r1 = document.body.createTextRange();
        r1.moveToElementText(inputObj);
        r1.setEndPoint("EndToEnd", r1);
        r1.moveStart('character', position);
        r1.moveEnd('character', position-inputObj.innerText.length);
        r1.select();
    }
}

function reportPrint(serviceId) {
    document.getElementById(serviceId).target = '_blank';
    document.getElementById(serviceId).onsubmit = function() {
        if (document.getElementById(serviceId).target == '_blank') {
            setTimeout("document.getElementById('" + serviceId + "').target = '';", 500);
            document.getElementById(serviceId).onsubmit = null;
        }
    };
    return true;
}

function reloadImg(imgObjId, reset, newSource) {
    document.getElementById(imgObjId).src = newSource;
    document.getElementById(reset).value = null;
}

function getTimeString() {
    var currDate = new Date();
    return '' + currDate.getYear() + currDate.getMonth() + currDate.getDate() + currDate.getHours() + currDate.getMinutes() + currDate.getSeconds() + currDate.getMilliseconds();
}

function checkUncheckAllInTable(chkName, elementoChk, checkedFlag) {
    var formulario = elementoChk.form, z = 0;
    for(z = 0; z < formulario.length; z++) {
        if(formulario[z].type == 'checkbox' && formulario[z] != elementoChk && formulario[z].id.indexOf(chkName, 0) > 0) {
            formulario[z].checked = checkedFlag;
        }
    }
}

function showStdConfirmAssignTrackingPeriodMessage(callerObject) {
    return showConfirmMessage(trackingPeriodAssign,callerObject);
}

function showStdConfirmDeleteReportConfigMessage(callerObject) {
	return showConfirmMessage(reportConfigDelete,callerObject);
}


function handleSaveRequest(xhr, status, args) {  
    if(!args.datasValidated) {  
    	entryDialog.show(); 
    } else {  
    	entryDialog.hide();  
    }  
} 

function handleNewChildRequest(xhr, status, args) {  
    if(args.datasValidated) {  
    	entryDialog.show(); 
    } else {  
    	entryDialog.hide();  
    }  
} 

function handleAdjustRequest(xhr, status, args) {  
    if(!args.datasValidated && args.adjust) {  
    	detailDialog.show(); 
    } else {  
    	detailDialog.hide();  
    }  
} 
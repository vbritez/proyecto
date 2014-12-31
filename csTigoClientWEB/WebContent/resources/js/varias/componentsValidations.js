function cellPhoneTextValidateDeleting(theElement, theEvent) {
    if (getCaretPosition(theElement) != theElement.value.length) {
        setCaretPosition (theElement, theElement.value.length);
    }
    var minLength = 5;
    var breakPointPosition = 6;
    var key = null;

    if(window.event) {
        key = theEvent.keyCode;
    } else if(theEvent.which) {
        key = theEvent.which;
    }

    if (key == 8 || key == 46) {
        if (theElement.value.length <= minLength) {
            return false;
        }
        if (theElement.value.length == breakPointPosition + 2) {
            theElement.value = theElement.value.substring(0, breakPointPosition + 1);
        }
        return true;
    }
}

function cellPhoneTextValidateInput(theElement, theEvent) {
    var breakPointPosition = 6;
    var key = null;

    if(window.event) {
        key = theEvent.keyCode;
    } else if(theEvent.which) {
        key = theEvent.which;
    }

    if (key != 8 && key != 46) {
        var valid = blockNonNumbers(theElement, theEvent, false, false);

        if (valid && theElement.value.length == breakPointPosition + 1) {
            theElement.value = theElement.value + ' ';
            insertASpace = true;
        }
        return valid;
    }
}

function cellPhoneTextValidateSpaces(theElement) {
    var breakPointPosition = 6;

    if (theElement.value.length == breakPointPosition + 1 && theElement.value.indexOf(' ') == theElement.value.lastIndexOf(' ')) {
        theElement.value = theElement.value + ' ';
    }

    if (theElement.value.length >= breakPointPosition + 2 && theElement.value.indexOf(' ') == theElement.value.lastIndexOf(' ')) {
        theElement.value = theElement.value.substring(0, 7) + ' ' + theElement.value.substring(7, Math.min(13, theElement.value.length));
    }
}

function containerTextValidateSpaces(theElement) {
    var breakPointPosition = 4;

    if (theElement.value.length == breakPointPosition) {
        theElement.value = theElement.value + '-';
    }

    if (theElement.value.length == 11) {
        theElement.value = theElement.value.substring(0, 11) + '-';
    }
}

function newUbicationTextValidateSpaces(theElement) {
    var breakPointPosition = 1;

    if (theElement.value.length == breakPointPosition) {
        theElement.value = theElement.value + '/';
    }
    
    if (theElement.value.length == 4) {
        theElement.value = theElement.value + '/';
    }
    
    if (theElement.value.length == 7) {
        theElement.value = theElement.value + '-';
    }
    
    if (theElement.value.length == 10) {
        theElement.value = theElement.value + '/';
    }
    
		if (document.getElementById('repTerportFullForm:newUbicationEdit').value != '') {
			document.getElementById('repTerportFullForm:numChapaEdit').value = '';
			document.getElementById('repTerportFullForm:numChapaEdit').disabled = true;
		} else {
			document.getElementById('repTerportFullForm:numChapaEdit').disabled = false;
		}
    
}

function disableNewUbication() {
	if (document.getElementById('repTerportFullForm:numChapaEdit').value != '') {
		document.getElementById('repTerportFullForm:newUbicationEdit').value = '';
		document.getElementById('repTerportFullForm:newUbicationEdit').disabled = true;
	} else {
		document.getElementById('repTerportFullForm:newUbicationEdit').disabled = false;
	}

}

function disableNumChapa() {
	if (document.getElementById('repTerportFullForm:newUbicationEdit').value != '') {
		document.getElementById('repTerportFullForm:numChapaEdit').value = '';
		document.getElementById('repTerportFullForm:numChapaEdit').disabled = true;
	} else {
		document.getElementById('repTerportFullForm:numChapaEdit').disabled = false;
	}
}
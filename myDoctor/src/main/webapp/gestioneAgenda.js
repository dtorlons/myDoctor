/**
 * Each button is associated with an event listener in order to invoke the relative function when clicked 
 */
window.addEventListener("load", ()=>{

    var addTimebandButtons = document.getElementsByClassName("addButton");
    Array.from(addTimebandButtons).forEach(function(el){
        el.addEventListener("click", addTimeband);
    } );    
    var addAppointmentButtons = document.getElementsByClassName("addAppointment");
    Array.from(addAppointmentButtons).forEach(function(el){
        el.addEventListener("click", addAppointment);
    });
    var modifyTimebandButtons = document.getElementsByClassName("modifyTimeband");
    Array.from(modifyTimebandButtons).forEach(function(el){
        el.addEventListener("click", modifyTimeband);
    });
    var appointmentButtons = document.getElementsByClassName("appointment");
    Array.from(appointmentButtons).forEach(function(el){
        el.addEventListener("click", appointmentDetails)
    });

})



/**
 * Opens the relative modal window and fills the form
 * @param {*} event: The target event
 */
function addTimeband(event){
    document.getElementById("insertTimebandModal").style.display = "block";
    document.getElementById("timebandDate").value = event.target.closest("div").getAttribute("date");
    document.getElementById("standardLength").value = 20;
}

/**
 * Opens the relative modal window and fills the form
 * @param {*} event: The target event
 */
function addAppointment(event){
    document.getElementById("insertAppointment").style.display = "block";
    document.getElementById("idDisponibilita").value = event.target.closest("div").getAttribute("idDisponibilita");
    document.getElementById("appointmentDate").value = event.target.closest("div").getAttribute("data");
    
}

/**
 * Opens the relative modal window and fills the form
 * @param {*} event: The target event
 */
function modifyTimeband(event){    
    document.getElementById("ModTimeband").style.display = "block";
    document.getElementById("deleteTimebandId").value = event.target.closest("span").getAttribute("timebandid");
    document.getElementById("modifyTimebandId").value = event.target.closest("span").getAttribute("timebandid");
    document.getElementById("ModTimebandDate").value = event.target.closest("span").getAttribute("data");
    document.getElementById("ModBegintime").value = event.target.closest("span").getAttribute("inizio");
    document.getElementById("ModEndtime").value = event.target.closest("span").getAttribute("fine");
    document.getElementById("ModstandardLength").value = event.target.closest("span").getAttribute("length");   
}

/**
 * Opens the relative modal window and fills the form
 * @param {*} event: The target event
 */
function appointmentDetails(event){
    document.getElementById("modificaAppuntamento").style.display = "block";    
    document.getElementById("ModAppointmentId").value = event.target.closest("div").getAttribute("appointmentid");
    document.getElementById("DelAppointmentId").value = event.target.closest("div").getAttribute("appointmentid");
    document.getElementById("nome").innerHTML = event.target.closest("div").getAttribute("name");
    document.getElementById("indirizzo").innerHTML = event.target.closest("div").getAttribute("address");
    document.getElementById("telefono").innerHTML = event.target.closest("div").getAttribute("phone");
    document.getElementById("ModAppointmentDate").value = event.target.closest("div").getAttribute("data");
    document.getElementById("ModAppointmentStart").value = event.target.closest("div").getAttribute("start");
    document.getElementById("ModAppointmentEnd").value = event.target.closest("div").getAttribute("end");
}

/**
 * Closes the modal window
 * @param {*} event The target event
 */
function closeModal(event){
    event.target.closest("div").style.display = "none";
}


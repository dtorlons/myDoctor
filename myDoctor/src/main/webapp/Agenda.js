
window.addEventListener("load", ()=>{
	
	var appointmentButtons = document.getElementsByClassName("freeAppointment");
	Array.from(appointmentButtons).forEach(function(el){
		el.addEventListener("click", selectAppointment);
	});	

})

function selectAppointment(event){
	document.getElementById("insertAppointment").style.display = "block";

	document.getElementById("disponibilitaId").value = event.target.closest("div").getAttribute("idDisponibilita");
	document.getElementById("date").value = event.target.closest("div").getAttribute("date");
	document.getElementById("inizio").value = event.target.closest("div").getAttribute("start");
	document.getElementById("fine").value = event.target.closest("div").getAttribute("end");
}

function closeModal(event){
    event.target.closest("div").style.display = "none";
}


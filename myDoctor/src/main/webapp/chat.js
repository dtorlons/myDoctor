window.addEventListener("load", function(){ 

    let selectorMenu = document.getElementById("patientSelector");

    if(selectorMenu == null){
        //E' un paziente
        window.setInterval(getArr, 1000); //SI ESEGUE SE E' PAZIENTE
        
    }else{
        document.getElementById("chatWindow").innerHTML = "Seleziona un paziente con cui chattare...";
        window.setInterval(getChat, 1000); //SI ESEGUE SE E' DOTTORE
        
    }

});

var size;
var idPaziente;


function selectChatter(event){

    if(event.target.closest("select").value == ""){
       document.getElementById("chatWindow").innerHTML = "Seleziona un paziente con cui chattare...";
    }else{
        idPaziente= event.target.closest("select").value;
        document.getElementById("idPazienteForm").value = idPaziente;
        document.getElementById("chatWindow").innerHTML = "";
    }
}

function getChat(){
   
    if(document.getElementById("patientSelector").value == ''){
        return;
    }



    makeCall("GET", 'ChatMessage?patientId='+idPaziente, null, function (req) {
        if (req.readyState == 4) {
            if (req.status == 200) {
                var responseArray = JSON.parse(req.responseText);                   
                if(responseArray.length != size || size == undefined){
                    render(responseArray);
                }               
                size = responseArray.length;               
                return;
            } else {
                switch (req.status){
                    case 400: // bad request
                    alert(req.responseText);
                    break;
                    case 401: // unauthorized
                    alert(req.responseText);
                    window.location = "index.html";
                    break;
                    case 403: //Forbidden
                    alert(req.responseText);
                    window.location = "index.html";
                    break;
                    case 500: // server error
                    alert(req.responseText);
                    break;
                    }//
                    return;
            }
        }
    });
}





function getArr(){   

  //  makeCall("GET", 'PatientChat', null, function (req) {
	  makeCall("GET", 'ChatMessage', null,  function(req){
        if (req.readyState == 4) {
            if (req.status == 200) {
                var responseArray = JSON.parse(req.responseText);                   
                if(responseArray.length>size || size == undefined){
                    render(responseArray);
                }               
                size = responseArray.length;               
                return;
            } else {
                switch (req.status){
                    case 400: // bad request
                    alert(req.responseText);
                    break;
                    case 401: // unauthorized
                    alert(req.responseText);
                    window.location = "index.html";
                    break;
                    case 403: //Forbidden
                    alert(req.responseText);
                    window.location = "index.html";
                    break;
                    case 500: // server error
                    alert(req.responseText);
                    break;
                    }//
                    return;
            }
        }
    });
}

function render(messageArray){

	document.getElementById("chatWindow").innerHTML = "";
	
    for(i=0; i<messageArray.length; i++){

        var msgdiv = document.createElement("div");       
        msgdiv.style.cssText = "border: solid; align-self: flex-end; border-radius: 8px; padding: 3px; margin: 2px;";

        //DIVIO TRA DESTRA E SINISTRA
        if(messageArray[i].isSender){
            msgdiv.style.background = "#0068D2";
            msgdiv.style.borderColor = '#0068D2';
            msgdiv.style.color = "white";            
        }else{
            msgdiv.style.background = "#548D8A";            
            msgdiv.style.borderColor = '#548D8A';
            msgdiv.style.color = "white";
        }

        //APPENDO IL MESSAGGIO
        msgdiv.innerHTML = '<i style="font-size:13.5px">' +messageArray[i].timestap+ "</i>" +"<br><b>" + messageArray[i].message; +'</b>'; 

        //RENDO, SE CI SONO, ALLEGATI

        var attachmentDiv = document.createElement("div");
        var anchor = document.createElement("a");        
        
        anchor.href ="GetFile?id=" + messageArray[i].messageId;
        var img = document.createElement("img");
        img.src = "images/document.png"; 
        img.style.width = "40px";
        img.style.padding = "7px";
        anchor.style.color = "white";
        if(messageArray[i].filename){
            msgdiv.append(attachmentDiv);            
        }
        
        attachmentDiv.append(anchor);
        anchor.append(img);
        anchor.innerHTML += messageArray[i].filename;
        
        
        //METTO A DESTRA O SINISTRA

        if(messageArray[i].isSender ){
            msgdiv.style.alignSelf = "flex-end";
            msgdiv.style.marginRight = '20px';
        }else{
            msgdiv.style.alignSelf = "flex-start";
            msgdiv.style.marginLeft = '20px';
        }
       
        document.getElementById("chatWindow").append(msgdiv);
    }
       document.getElementById("chatWindow").scrollTop = document.getElementById("chatWindow").scrollHeight; 
}





 
  function post(event){
    event.preventDefault();

    form = document.getElementById("postForm");
 

    if(document.getElementById('patientSelector')){
        if(document.getElementById('patientSelector').value == ''){
            alert("Seleziona qualcuno con cui chattare!");
            return;
        }
    }

   // let url = (document.getElementById("patientSelector") == null)? 'PatientChat' : 'DoctorChat';
   
   let url = 'ChatMessage';

    makeCall("post", url, form, function (req) {
        if (req.readyState == 4) {
            if (req.status == 200) {
                              
                return;
            } else {
                switch (req.status){
                    case 400: // bad request
                    alert(req.responseText);
                    break;
                    case 401: // unauthorized
                    alert(req.responseText);
                    window.location = "index.html";
                    break;
                    case 403: //Forbidden
                    alert(req.responseText);
                    window.location = "index.html";
                    break;
                    case 500: // server error
                    alert(req.responseText);
                    break;
                    }//
                    return;
            }
        }
    });

  }
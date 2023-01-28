var notificationArrayLength = -1;

window.addEventListener("load", function(){ 

    
    window.setInterval(fetch, 1000); 
});

function fetch(){

    makeCall("GET", 'Notifications', null, function (req) {
        if (req.readyState == 4) {
            if (req.status == 200) {
                var responseArray = JSON.parse(req.responseText);                   
                 
                if(responseArray.length != notificationArrayLength){
                    notificationArrayLength = responseArray.length;

                    if(notificationArrayLength ==0){
                        notificationBell.innerHTML ='';
                        let offbell = document.createElement('img');
                        offbell.src ="images/bell_off.png";
                        offbell.style.width ="20px"
                        document.getElementById("nofiticationContainer").innerHTML = "Nessuna notifica";
                        notificationBell.append(offbell);
                        
                    }
                    else{
                        
                        let onbell = document.createElement('img');
                        onbell.src ="images/bell_on.png";
                        onbell.style.width ="20px"

                        notificationBell.innerHTML = '';
                        notificationBell.append(onbell);

                        renderNotifications(responseArray);                        
                    }

                }else{
                    notificationArrayLength = -1;
                }
                
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

function renderNotifications(notificationArray){

     
    notificationArrayLength = 0;

    var nofiticationContainer = document.getElementById("nofiticationContainer");
    nofiticationContainer.innerHTML ='';        

    for(let i =0; i<notificationArray.length; i++){         
    
        let tpl = document.createElement("p");
        tpl.innerHTML = "<i>"+notificationArray[i].timestamp+ "</i><br><b>"+notificationArray[i].text+"</b><hr>";
        tpl.style.fontSize = '11px';
        nofiticationContainer.append(tpl);
    };

    let deleteAllNotificationsBtn = document.createElement("p");

    deleteAllNotificationsBtn.innerHTML = "Elimina tutte";
    deleteAllNotificationsBtn.className = "delNotificationsBtn";

    nofiticationContainer.append(deleteAllNotificationsBtn);

    deleteAllNotificationsBtn.addEventListener("click", deleteAll);


}

function deleteAll(){
    modalWin.style.display = "none";

    var nofiticationContainer = document.getElementById("nofiticationContainer");
    nofiticationContainer.innerHTML ='Nessuna notifica';

    


    
    makeCall("POST", 'Notifications', null, function (req) {
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









// Get the modal element
var modalWin = document.getElementById("notification-modal");

// Get the button that opens the modal
var btn = document.getElementById("notificationBell");

// Get the <span> element that closes the modal
var span = document.getElementsByClassName("close-button")[0];


btn.onclick = function() {
    modalWin.style.display = "block";
}


span.onclick = function() {
    modalWin.style.display = "none";
}

window.onload = popUp();
function popUp() {
    var otherusername1 = document.getElementById("otherusername1");
    document.getElementById("nousers").style.display = "none";
    if(!otherusername1){
        document.getElementById("bigtable").style.display = "none";
        document.getElementById("nousers").style.display = "block";
    }
    document.getElementById("checkpopup1").style.display = "none";
    var popUp = document.getElementById("newRequests");
    checkRequests = document.getElementById("checkpopup1").innerHTML;
    if (checkRequests == "true"){
        popUp.classList.toggle("show");
    }
}

function closePopUp() {
    var popUp = document.getElementById("newRequests");
    popUp.classList.toggle("show");
}

var steamid = document.getElementById("steamidhidden").innerHTML;
var tempSteamID = steamid.match(/\d/g);
if (tempSteamID != null){
    tempSteamID = tempSteamID.join("");
    if(tempSteamID.length == 17) {
        steamid = steamid.match(/\d/g);
        steamid = steamid.join("");
    }
}
/* else {
    var splitSteamId = steamid.split('/id/').pop();
    const getSteamID = {
        url2: 'http://api.steampowered.com/ISteamUser/',
        version2: 'v0001',
        type2: 'ResolveVanityURL',
        id2: splitSteamId,
    }
    
    const {url2, type2, version2,id2} = getSteamID
    const steamIDUrl = `https://blooming-headland-71532.herokuapp.com/${url2}/${type2}/${version2}/?key=89024256E255B94B2EB67AC30A60D4A6&vanityurl=${id2}`
    fetch(steamIDUrl)
        .then( (data) => data.json())
        .then( (user) => generateSteamID(user) )
        .then( (data1) => steamid = data1.response.steamid)
    
        function generateSteamID(data){
            //console.log(data);
            steamid = data.response.steamid;
            console.log(steamid);
        }
    console.log(steamid)
} */


document.getElementById("steamidhidden").style.display = "none";
$('.nav ul li').click(function() 
{
    $(this).addClass("active").siblings().removeClass('active');
}
)

const tabBtn= document.querySelectorAll('.nav ul li');
const tab = document.querySelectorAll('.tab');

function tabs(panel)
{
    tab.forEach(function(node) {
        node.style.display='none';
    });
    tab[panel].style.display='block';
}

tabs(0);

window.onload = popUp();
function popUp() {
    document.getElementById("checkpopup").style.display = "none";
    document.getElementById("steam").style.display = "none";
    document.getElementById("steamGames").style.display = "none";
    var popUp = document.getElementById("newRequests");
    checkRequests = document.getElementById("checkpopup").innerHTML;
    if (checkRequests == "true"){
        popUp.classList.toggle("show");
    }
}

function closePopUp() {
    var popUp = document.getElementById("newRequests");
    popUp.classList.toggle("show");
}


document.getElementById("slider1").addEventListener("input", function(event){
    let value = event.target.value;
    document.getElementById("current-value").innerText  = value;
    document.getElementById("current-value").classList.add("active");
    document.getElementById("current-value").style.left = `${value/0.2425}%`
   });

   document.getElementById("slider2").addEventListener("input", function(event){
    let value = event.target.value;
    document.getElementById("current-value1").innerText  = value;
    document.getElementById("current-value1").classList.add("active");
    document.getElementById("current-value1").style.left = `${value/0.2425}%`
   });

   document.getElementById("slider3").addEventListener("input", function(event){
    let value = event.target.value;
    document.getElementById("current-value2").innerText  = value;
    document.getElementById("current-value2").classList.add("active");
    document.getElementById("current-value2").style.left = `${value/0.2425}%`
   });
const apiData = {
    url: 'http://api.steampowered.com/ISteamUser/',
    version: 'v0001',
    type: 'GetPlayerSummaries',
    id: steamid,
}

const {url, type, version,id} = apiData
const apiUrl = `https://blooming-headland-71532.herokuapp.com/${url}/${type}/${version}/?key=89024256E255B94B2EB67AC30A60D4A6&steamids=${id}`
fetch(apiUrl)
    .then( (data) => data.json())
    .then( (user) => generateHtml(user) )

const generateHtml = (data) => {
    //console.log(data)
    const html = `
        <div class="name" style="color:white" id="steamName" >Steam Name: ${data.response.players.player[0].personaname}</div>
        <img id="steamImage" src=${data.response.players.player[0].avatarmedium}>
        <a class="link" style="color:white" id="steamLink" href=${data.response.players.player[0].profileurl} >Take me to Steam </a><br/><br/>
    `
    document.getElementById("steam").style.display = "block";
    const steamDiv = document.querySelector('.steam')
    //console.log(html);
    steamDiv.innerHTML = html
}

const recentData = {
    url1: 'http://api.steampowered.com/IPlayerService/',
    version1: 'v0001',
    type1: 'GetRecentlyPlayedGames',
    id1: steamid,
}

const {url1, type1, version1,id1} = recentData
const recentUrl = `https://blooming-headland-71532.herokuapp.com/${url1}/${type1}/${version1}/?key=89024256E255B94B2EB67AC30A60D4A6&steamid=${id1}`
fetch(recentUrl)
    .then( (data) => data.json())
    .then( (user) => generateRecent(user) )

const generateRecent = (data) => {
    //console.log(data)

    var html1 = '<div class="name" style="color:white; text-decoration: underline" id="recentTitle" > Recently Played Games</div><br/><br/><div id="steamGames1" >';
    var total = data.response.total_count;
    console.log('recent total:' + total);
    if(total > 5){
        total = 5;
    }
    if (total){
        document.getElementById("steamGames").style.display = "block";
    }
    for(var i = 0; i < total; i++) {
        var timePlayed = Math.round(data.response.games[i].playtime_forever/60);
        html1 += `<div id="specificGame" ><div class="name" style="color:white" id="gameName" >${data.response.games[i].name}</div><br/> <a href="https://store.steampowered.com/app/${data.response.games[i].appid}">
                  <img src=http://media.steampowered.com/steamcommunity/public/images/apps/${data.response.games[i].appid}/${data.response.games[i].img_logo_url}.jpg id="gameImage" ></a><br/>`
        html1 += '<div class="name" style="color:white" id="timePlayed" >Time Played: ' + timePlayed+' hours</div><br/><br/></div>';
    }
    html1 += '</div>';
    //console.log(html1);
    const steamGames = document.querySelector('.steamGames');
    steamGames.innerHTML = html1;
}









 



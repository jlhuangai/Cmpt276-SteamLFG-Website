document.getElementById("requestsent").style.display = "none";
document.getElementById("friendreceive").style.display = "none";
document.getElementById("acceptfriend").style.display = "none";
document.getElementById("declinefriend").style.display = "none";
document.getElementById("addedfriend").style.display = "none";
document.getElementById("deletefriend").style.display = "none";
var steamid = document.getElementById("steamidhidden").innerHTML;

steamid = steamid.match(/\d/g);
if (steamid != null){
    steamid = steamid.join("");
}

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

document.getElementById("steamidhidden").style.display = "none";

window.onload = test();
console.log("testing");
function test(){
    document.getElementById("steam").style.display = "none";
    document.getElementById("steamGames").style.display = "none";
    document.getElementById("request").style.display = "none";
    request = document.getElementById("request").innerHTML;
    console.log(request);
    if (request == "sent"){
        document.getElementById("requestsent").style.display = "block";
        document.getElementById("addfriend").style.display = "none";
    }
    if (request == "received"){
        document.getElementById("friendreceive").style.display = "block";
        document.getElementById("addfriend").style.display = "none";
        document.getElementById("acceptfriend").style.display = "block";
        document.getElementById("declinefriend").style.display = "block";
    }
    if (request == "accepted"){
        document.getElementById("addedfriend").style.display = "block";
        document.getElementById("deletefriend").style.display = "block";
        document.getElementById("addfriend").style.display = "none";
    }
}

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
    console.log(data)
    const html = `
        <div class="name" style="color:white" id="steamName" >Steam Name: ${data.response.players.player[0].personaname}</div>
        <img id="steamImage" src=${data.response.players.player[0].avatarmedium}>
        <a class="link" style="color:white" id="steamLink" href=${data.response.players.player[0].profileurl} >Take me to Steam </a><br/><br/>
    `
    document.getElementById("steam").style.display = "block";
    const steamDiv = document.querySelector('.steam')
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
    console.log(data)

    var html1 = '<div class="name" style="color:white; text-decoration: underline" id="recentTitle" > Recently Played Games</div><br/><br/><div id="steamGames1" >';
    var total = data.response.total_count;
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


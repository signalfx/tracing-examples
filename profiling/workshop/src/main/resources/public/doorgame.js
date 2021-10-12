
console.log('doorgame starting.');

let gameId = null;
let revealedDoor = null;
let chosenDoor = null;

function startGame(){
    console.log('Starting new game');
    document.getElementById('game-heading').innerText = 'THE DOOR GAME';
    return fetch(`/new-game`)
        .then(res => res.text())
        .then(id => {
            gameId = id;
            console.log(`New game started: ${gameId}`);
            enableDoors();
            show('door-row');
            show('choose-a-door');
            hide('start-button');
        });
}

async function doorClicked(num) {
    console.log(`Door ${num} picked.`);
    chosenDoor = num;
    disableDoors();

    const url = `/game/${gameId}/pick/${num}`;
    await fetch(url, { method: 'POST'})
        .then(res => res.text())
        .then(res => {
            console.log(`Response: ${res}`);
        });
    hide('choose-a-door');
    showChoice(num);
    fetch(`/game/${gameId}/reveal`)
        .then(res => res.text())
        .then(res => parseInt(res))
        .then(res => {
            console.log(`Revealing door ${res}`);
            revealedDoor = res;
            showZonk(res);
        });
}

function disableDoors(){
    ['door0', 'door1', 'door2'].forEach(sel => {
        document.getElementById(sel).style.cursor = 'default';
        document.getElementById(sel).onclick = () => {};
    });
}

function enableDoors(){
    [0, 1, 2].forEach(n => {
        document.getElementById(`door${n}`).style.cursor = 'pointer';
        document.getElementById(`door${n}`).onclick = () => doorClicked(n);
    });
}

function showChoice(doorNum){
    show(`choose${doorNum}`);
}

function showZonk(){
    document.getElementById('change-prompt').innerHTML = `You picked door #${chosenDoor+1}.<br/>There is a ZONK behind door #${revealedDoor+1}.`;
    show('change-prompt');
    hide(`door${revealedDoor}`);
    show('change-button');
    document.getElementById('stay-button').innerText = `Stay with door #${chosenDoor+1}`;
    // this is a clever way of finding the remaining door..._too_ clever
    let remaining = 3 - (chosenDoor + revealedDoor);
    document.getElementById('change-button').innerText = `Switch to door #${remaining+1}`;
    show('stay-button');
    show(`zonk${revealedDoor}`);
}

function choiceKeep(){

}

function choiceChange() {
    let changeTo = 3 - (chosenDoor + revealedDoor);
}

function show(sel){
    document.getElementById(sel).classList.remove('visually-hidden');
}
function hide(sel){
    document.getElementById(sel).classList.add('visually-hidden');
}
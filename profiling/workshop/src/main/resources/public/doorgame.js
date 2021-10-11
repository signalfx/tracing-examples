
console.log('doorgame starting.');

let gameId = null;

function doorClicked(num) {
    console.log(`Door ${num} clicked.`);
}

function startGame(){
    console.log('Starting new game');
    return fetch(`/new-game`)
        .then(res => res.text())
        .then(id => {
            gameId = id;
            document.getElementById('door-row').classList.remove('visually-hidden');
            document.getElementById('choose-a-door').classList.remove('visually-hidden');
            document.getElementById('start-button').classList.add('visually-hidden');
        });
}

function choiceKeep(){

}

function choiceChange() {

}
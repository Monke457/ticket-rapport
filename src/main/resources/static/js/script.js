const resizeOnType = (evt) => {
    const elTextarea = evt.currentTarget;
    console.log("resizing");
    elTextarea.style.height = "auto";
    elTextarea.style.height = `${elTextarea.scrollHeight + 1}px`;
};

document.querySelectorAll(".resizer").forEach(el => {
    el.addEventListener("keyup", resizeOnType);
});

function change_lang(option) {
    window.location.replace('?lang=' + option);
}
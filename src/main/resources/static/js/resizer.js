/* Resizes the height of a textarea element to show all content. */
const resizeOnType = (evt) => {
    const elTextarea = evt.currentTarget;
    elTextarea.style.height = "auto";
    elTextarea.style.height = `${elTextarea.scrollHeight + 2}px`;
};

/* Add an event listener to textarea elements to call the resizer function each time a key is pressed. */
document.querySelectorAll('textarea').forEach(el => {
    el.addEventListener('keyup', resizeOnType);
    el.style.height = `${el.scrollHeight + 2}px`;
});
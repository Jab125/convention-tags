HTMLElement.prototype.getElementById = (id) => {
    b = a.getElementsByTagName("*");
    for (i in Array.from(b)) {
        if (b[i].id == id) return b[i];
    }
}
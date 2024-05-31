document.addEventListener('DOMContentLoaded', function() {
    function updateClock() {
        const now = new Date();
        const hours = String(now.getHours()).padStart(2, '0');
        const minutes = String(now.getMinutes()).padStart(2, '0');
        const seconds = String(now.getSeconds()).padStart(2, '0');
        document.getElementById('clock').textContent = `${hours}:${minutes}:${seconds}`;
    }

    const buttons = document.querySelectorAll(".toggleButton");
    buttons.forEach(button => {
        button.addEventListener("click", function() {
            this.classList.toggle("active-button");
        });
    });

    updateClock();
    setInterval(updateClock, 1000);
});

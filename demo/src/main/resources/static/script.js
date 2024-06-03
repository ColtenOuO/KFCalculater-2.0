document.addEventListener('DOMContentLoaded', function() {
    fetch('http://127.0.0.1:8080/DataBase/images')
        .then(response => response.json())
        .then(data => {
            const imageContainer = document.getElementById('imageContainer');
            data.forEach(imageUrl => {
                const img = document.createElement('img');
                img.src = imageUrl;
                img.alt = 'Example Image';
                img.className = 'img-responsive';
                imageContainer.appendChild(img);
            });
        })
        .catch(error => console.error('Error fetching images:', error));
    
    function updateClock() {x``
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

document.addEventListener('DOMContentLoaded', function() {
    const updateButton = document.querySelectorAll(".update-button");
    updateButton.forEach(button => {
        button.addEventListener("click", function() {
            fetch('http://127.0.0.1:8080/DataBase/get');
        });
    });

    function listAll() {
        fetch('http://127.0.0.1:8080/DataBase/images')
        .then(response => response.json())
        .then(data => {
            const imageContainer = document.getElementById('imageContainer');
            imageContainer.innerHTML = '';
            data.forEach(imageUrl => {
                const img = document.createElement('img');
                img.src = imageUrl;
                img.alt = 'Example Image';
                img.className = 'img-responsive';
                imageContainer.appendChild(img);
            });
        })
        .catch(error => console.error('Error fetching images:', error));
    }
    
    fetch('http://127.0.0.1:8080/DataBase/images')
        .then(response => response.json())
        .then(data => {
            const imageContainer = document.getElementById('imageContainer');
            imageContainer.innerHTML = '';
            data.forEach(imageUrl => {
                const img = document.createElement('img');
                img.src = imageUrl;
                img.alt = 'Example Image';
                img.className = 'img-responsive';
                imageContainer.appendChild(img);
            });
        })
        .catch(error => console.error('Error fetching images:', error));
    
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

    buttons.forEach(function(button) {
        button.addEventListener('click', function() {
            const activeButtons = document.querySelectorAll('.active-button');
            var click_button = "";
            activeButtons.forEach( choose_button => {
                click_button += choose_button.classList[2];
            })
            if( click_button === "" ) { imageContainer.innerHTML = ''; listAll(); }
            else {
                imageContainer.innerHTML = '';
                fetch('http://127.0.0.1:8080/calculater/req_meal?meal_char=' + click_button )
                .then( response => response.json() )
                .then( data => {
                    const imageContainer = document.getElementById('imageContainer');
                    imageContainer.innerHTML = '';
                    data.forEach(imageUrl => {
                        const img = document.createElement('img');
                        img.src = imageUrl;
                        img.alt = 'Example Image';
                        img.className = 'img-responsive';
                        imageContainer.appendChild(img);
                    });
                })
                .catch(error => console.error('Error fetching images:', error));
            }
        });
    });

    function Option2() {
        var money = document.getElementById('name');
        console.log(money.value);
        fetch('http://127.0.0.1:8080/calculater/req_money?money=' + money.value )
        .then( response => response.json() )
        .then( data => {
            const imageContainer = document.getElementById('imageContainer');
            imageContainer.innerHTML = '';
            data.forEach(imageUrl => {
                const img = document.createElement('img');
                img.src = imageUrl;
                img.alt = 'Example Image';
                img.className = 'img-responsive';
                imageContainer.appendChild(img);
            });
        })
        .catch(error => console.error('Error fetching images:', error));
    }
    function Option3() {
        var money = document.getElementById('name');
        fetch('http://127.0.0.1:8080/calculater/req_danta?money=' + money.value )
        .then( response => response.json() )
        .then( data => {
                const imageContainer = document.getElementById('imageContainer');
                imageContainer.innerHTML = '';
                data.forEach(imageUrl => {
                const img = document.createElement('img');
                img.src = imageUrl;
                img.alt = 'Example Image';
                img.className = 'img-responsive';
                imageContainer.appendChild(img);
            });
        })
    }
    function Option4() {
        var money = document.getElementById('name');
        fetch('http://127.0.0.1:8080/calculater/req_chicken?money=' + money.value )
        .then( response => response.json() )
        .then( data => {
                const imageContainer = document.getElementById('imageContainer');
                imageContainer.innerHTML = '';
                data.forEach(imageUrl => {
                const img = document.createElement('img');
                img.src = imageUrl;
                img.alt = 'Example Image';
                img.className = 'img-responsive';
                imageContainer.appendChild(img);
            });
        })
    }
    function Option5() {
        var money = document.getElementById('name');
        fetch('http://127.0.0.1:8080/calculater/req_danta_nolimit?money=' + money.value )
        .then( response => response.json() )
        .then( data => {
                const imageContainer = document.getElementById('imageContainer');
                imageContainer.innerHTML = '';
                data.forEach(imageUrl => {
                const img = document.createElement('img');
                img.src = imageUrl;
                img.alt = 'Example Image';
                img.className = 'img-responsive';
                imageContainer.appendChild(img);
            });
        })
    }
    function Option6() {
        var money = document.getElementById('name');
        fetch('http://127.0.0.1:8080/calculater/req_chicken_nolimit?money=' + money.value )
        .then( response => response.json() )
        .then( data => {
                const imageContainer = document.getElementById('imageContainer');
                imageContainer.innerHTML = '';
                data.forEach(imageUrl => {
                const img = document.createElement('img');
                img.src = imageUrl;
                img.alt = 'Example Image';
                img.className = 'img-responsive';
                imageContainer.appendChild(img);
            });
        })
    }

    var selectElement = document.getElementById('standard-select');
    selectElement.addEventListener('change', function() {
        var selectedValue = this.value;
        if( selectedValue === "Option 1") { listAll(); }
        else if( selectedValue === "Option 2" ) { Option2(); }
        else if( selectedValue === "Option 3" ) { Option3(); }
        else if( selectedValue === "Option 4" ) { Option4(); }
        else if( selectedValue === "Option 5" ) { Option5(); }
        else if( selectedValue === "Option 6" ) { Option6(); }
    });

    const inputBox = document.getElementById('name');
    inputBox.addEventListener('input', function() {
        if( selectElement.value === "Option 2" ) { Option2(); }
        if( selectElement.value === "Option 3" ) { Option3(); }
        if( selectElement.value === "Option 4" ) { Option4(); }
        if( selectElement.value === "Option 5" ) { Option5(); }
        if( selectElement.value === "Option 6" ) { Option6(); }
    })

    updateClock();
    setInterval(updateClock, 1000);
});

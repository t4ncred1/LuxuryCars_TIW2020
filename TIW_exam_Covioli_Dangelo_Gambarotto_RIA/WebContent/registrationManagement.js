(function() {

document.getElementById("registerbutton").addEventListener('click', (e) => {
	var form = e.target.closest("form");
	form.checkpwd = function (){
		var pwd1 = this.elements.namedItem("pwd1").value;
		var pwd2 = this.elements.namedItem("pwd2").value;
		return pwd1 == pwd2;
	}
	if (form.checkValidity() && form.checkpwd()) {
	makeCall("POST", 'Registration', e.target.closest("form"),
		function(req) {
			if (req.readyState == XMLHttpRequest.DONE) {
				var message = req.responseText;
				switch (req.status) {
					case 200:
						window.location.href = message;
						break;
					case 400:
						document.getElementById("text").textContent = message;
						document.getElementById("errorbox").setAttribute("class", "error");
						document.getElementById("xbutton").setAttribute("class", "close");
						break;
					case 500:
						document.getElementById("text").textContent = message;
						document.getElementById("errorbox").setAttribute("class", "error");
						document.getElementById("xbutton").setAttribute("class", "close");
						break;
				}
			}
		}
	);
	}
	else if (!form.checkValidity()) {
		 form.reportValidity();
	}
	else if (!form.checkpwd()){
		document.getElementById("text").textContent = "Le passwrod non coincidono";
		document.getElementById("errorbox").setAttribute("class", "error");
		document.getElementById("xbutton").setAttribute("class", "close");
	}
});

document.getElementById("xbutton").addEventListener('click', (e) => {
	document.getElementById("errorbox").setAttribute("class", "invisible");
	document.getElementById("text").textContent="";
});

})();
(function() {

document.getElementById("registerbutton").addEventListener('click', (e) => {
	var form = e.target.closest("form");
	
	form.checkpwd = function (){
		var pwd1 = this.elements.namedItem("pwd1").value;
		var pwd2 = this.elements.namedItem("pwd2").value;
		return pwd1 == pwd2;
	}
	
	form.checkrole = function (){
		var role = this.elements.namedItem("role").value;
		return (role.match("client") || role.match("worker"));
	}
	
//	form.checkemail = function (){
//		var mail = this.elements.namedItem("mail").value;
//		var pattern = "[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}$";
//		return (mail.match(pattern));
//	}
	
	if (form.checkValidity() && form.checkpwd() && form.checkrole()) {
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
		document.getElementById("text").textContent = "Le password inserite non combaciano";
		document.getElementById("errorbox").setAttribute("class", "error");
		document.getElementById("xbutton").setAttribute("class", "close");
	}
	else if (!form.checkrole()){
		document.getElementById("text").textContent = "Seleziona un ruolo corretto!";
		document.getElementById("errorbox").setAttribute("class", "error");
		document.getElementById("xbutton").setAttribute("class", "close");
	}
});

document.getElementById("xbutton").addEventListener('click', (e) => {
	document.getElementById("errorbox").setAttribute("class", "invisible");
	document.getElementById("text").textContent="";
});

})();
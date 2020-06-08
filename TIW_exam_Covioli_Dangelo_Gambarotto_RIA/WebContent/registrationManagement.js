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
	
	form.checkemail = function (){
		var mail = this.elements.namedItem("mail").value;
		var pattern = "^([_a-zA-Z0-9-]+(\\.[_a-zA-Z0-9-]+)*@[a-zA-Z0-9-]+(\\.[a-zA-Z0-9-]+)*(\\.[a-zA-Z]{1,6}))?$";
		return (mail.match(pattern));
	}
	
	function highlightErrors(form){
		if(!form.checkemail()){
			document.getElementById("mail").setAttribute("class", "registerfield errorfield");
		}
		if(!form.checkpwd()){
			document.getElementById("pwd1").setAttribute("class", "registerfield errorfield");
			document.getElementById("pwd2").setAttribute("class", "registerfield errorfield");
		}
	}
	
	function goBackToLogin(){
		window.location.href = "index.html";
	}
	
	document.getElementById("errorbox").setAttribute("class", "invisible");
	document.getElementById("text").textContent="";
	document.getElementById("mail").setAttribute("class", "registerfield field");
	document.getElementById("pwd1").setAttribute("class", "registerfield field");
	document.getElementById("pwd2").setAttribute("class", "registerfield field");
	if (form.checkValidity() && form.checkpwd() && form.checkrole() && form.checkemail()) {
	makeCall("POST", 'Registration', e.target.closest("form"),
		function(req) {
			if (req.readyState == XMLHttpRequest.DONE) {
				var message = req.responseText;
				switch (req.status) {
					case 200:
						document.getElementById("text").textContent = message;
						document.getElementById("errorbox").setAttribute("class", "success");
						document.getElementById("xbutton").setAttribute("class", "close");
						document.getElementById("errorbox").addEventListener('click', (e) => {
							window.location.href = "index.html";
						}, false);
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
		document.getElementById("errorbox").addEventListener('click', (e) => {
			return;
		});
		highlightErrors(form);
	}
	else if (!form.checkemail()){
		document.getElementById("text").textContent = "La mail inserita non e' valida";
		document.getElementById("errorbox").setAttribute("class", "error");
		document.getElementById("xbutton").setAttribute("class", "close");
		document.getElementById("errorbox").addEventListener('click', (e) => {
			// This function acts as a removeEventListener
			return;
		});
		highlightErrors(form);
	}
	else if (!form.checkrole()){
		document.getElementById("text").textContent = "Seleziona un ruolo corretto!";
		document.getElementById("errorbox").setAttribute("class", "error");
		document.getElementById("xbutton").setAttribute("class", "close");
		document.getElementById("errorbox").addEventListener('click', (e) => {
			return;
		});
	}
});

document.getElementById("xbutton").addEventListener('click', (e) => {
	e.stopPropagation();
	document.getElementById("errorbox").setAttribute("class", "invisible");
	document.getElementById("text").textContent="";
	document.getElementById("mail").setAttribute("class", "registerfield field");
	document.getElementById("pwd1").setAttribute("class", "registerfield field");
	document.getElementById("pwd2").setAttribute("class", "registerfield field");
	document.getElementById("errorbox").addEventListener('click', (e) => {
		return;
	});
});

document.getElementById("back").addEventListener('click', (e) => {
	window.location.href = "index.html";
});

})();
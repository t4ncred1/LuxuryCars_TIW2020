(function() {

function hideErrors(){
	document.getElementById("errorboxregister").setAttribute("class", "invisible");
	document.getElementById("textregister").textContent="";
	document.getElementById("mail").setAttribute("class", "registerfield field");
	document.getElementById("pwd1").setAttribute("class", "registerfield field");
	document.getElementById("pwd2").setAttribute("class", "registerfield field");
	document.getElementById("username").setAttribute("class", "registerfield field");
	document.getElementById("errorboxregister").addEventListener('click', (e) => {
		return;
	});	
}	
	
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
	      document.getElementById("pagebody").setAttribute("class", "indexpage");
	      document.getElementById("registerview").setAttribute("class", "hidden");
	      document.getElementById("loginview").setAttribute("class", "");
	}
	
	hideErrors();
	if (form.checkValidity() && form.checkpwd() && form.checkrole() && form.checkemail()) {
	makeCall("POST", 'Registration', e.target.closest("form"),
		function(req) {
			if (req.readyState == XMLHttpRequest.DONE) {
				var message = req.responseText;
				switch (req.status) {
					case 200:
						document.getElementById("textregister").textContent = message;
						document.getElementById("errorboxregister").setAttribute("class", "success");
						document.getElementById("xbuttonregister").setAttribute("class", "close");
						document.getElementById("errorboxregister").addEventListener('click', (e) => {
						      document.getElementById("pagebody").setAttribute("class", "indexpage");
						      document.getElementById("registerview").setAttribute("class", "hidden");
						      document.getElementById("loginview").setAttribute("class", "");
						      hideErrors();
						}, false);
						break;
					case 400:
						document.getElementById("textregister").textContent = message;
						document.getElementById("errorboxregister").setAttribute("class", "error");
						document.getElementById("xbuttonregister").setAttribute("class", "close");
						document.getElementById("username").setAttribute("class", "registerfield errorfield");
						break;
					case 500:
						document.getElementById("textregister").textContent = message;
						document.getElementById("errorboxregister").setAttribute("class", "error");
						document.getElementById("xbuttonregister").setAttribute("class", "close");
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
		document.getElementById("textregister").textContent = "Le password inserite non combaciano";
		document.getElementById("errorboxregister").setAttribute("class", "error");
		document.getElementById("xbuttonregister").setAttribute("class", "close");
		document.getElementById("errorboxregister").addEventListener('click', (e) => {
			return;
		});
		highlightErrors(form);
	}
	else if (!form.checkemail()){
		document.getElementById("textregister").textContent = "La mail inserita non e' valida";
		document.getElementById("errorboxregister").setAttribute("class", "error");
		document.getElementById("xbuttonregister").setAttribute("class", "close");
		document.getElementById("errorboxregister").addEventListener('click', (e) => {
			// This function acts as a removeEventListener
			return;
		});
		highlightErrors(form);
	}
	else if (!form.checkrole()){
		document.getElementById("textregister").textContent = "Seleziona un ruolo corretto!";
		document.getElementById("errorboxregister").setAttribute("class", "error");
		document.getElementById("xbuttonregister").setAttribute("class", "close");
		document.getElementById("errorboxregister").addEventListener('click', (e) => {
			return;
		});
	}
});

document.getElementById("xbuttonregister").addEventListener('click', (e) => {
	e.stopPropagation();
	hideErrors();
});

document.getElementById("back").addEventListener('click', (e) => {
	hideErrors();
	document.getElementById("registerform").reset();
    document.getElementById("pagebody").setAttribute("class", "indexpage");
    document.getElementById("loginview").setAttribute("class", "");
    document.getElementById("registerview").setAttribute("class", "hidden");
});

})();
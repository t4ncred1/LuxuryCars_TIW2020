(function() {

	function hideErrors(){
		document.getElementById("errorbox").setAttribute("class", "invisible");
		document.getElementById("text").textContent="";
		document.getElementById("mail").setAttribute("class", "registerfield field");
		document.getElementById("pwd1").setAttribute("class", "registerfield field");
		document.getElementById("pwd2").setAttribute("class", "registerfield field");
		document.getElementById("username").setAttribute("class", "registerfield field");
		document.getElementById("errorbox").addEventListener('click', (e) => {
			return;
		});	
	}	
  
	function checkLogout(){
		var showerror = function(message){
			alert(message);
		}
		if (sessionStorage.getItem("logout")) {
			showerror("Logout effettuato");
			sessionStorage.removeItem('logout');
		}
	}
	
  checkLogout();
  document.getElementById("loginbutton").addEventListener('click', (e) => {
    var form = e.target.closest("form");
    if (form.checkValidity()) {
      makeCall("POST", 'Login', e.target.closest("form"),
        function(req) {
          if (req.readyState == XMLHttpRequest.DONE) {
            var message = req.responseText;
            switch (req.status) {
              case 200:
            	var personalData = JSON.parse(message);
            	sessionStorage.setItem('name', personalData.name);
            	sessionStorage.setItem('role', personalData.role);
            	console.log(personalData.page);
                window.location.href = personalData.page;
                break;
              case 400:
                document.getElementById("text").textContent = message;
                document.getElementById("errorbox").setAttribute("class", "error");
                document.getElementById("xbutton").setAttribute("class", "close");
                break;
              case 401:
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
    } else {
    	 form.reportValidity();
    }
  });
  
  document.getElementById("xbutton").addEventListener('click', (e) => {
	  document.getElementById("errorbox").setAttribute("class", "hidden");
	  document.getElementById("text").textContent="";
  });

  document.getElementById("toregister").addEventListener('click', (e) => {
	  hideErrors();
	  document.getElementById("loginform").reset();
      document.getElementById("pagebody").setAttribute("class", "registerpage");
      document.getElementById("loginview").setAttribute("class", "hidden");
      document.getElementById("registerview").setAttribute("class", "");
  });
  
})();
/**
 * 
 */

(function() {

// page components
  var quotationtable, freetable, pricediv, errorBox, namefield, body,
    pageOrchestrator = new PageOrchestrator(); // main controller

  window.addEventListener("load", () => {
      pageOrchestrator.start(); // initialize the components
      pageOrchestrator.refresh(); // reset components to the default 
  }, false);

  function ErrorBox(_errorbox, _xbutton, _errormessage){
	  	//error box at the top of the page, signaling wether a submission went smoothly or not.
		//can have either class error or success. 
	  var errorbox = _errorbox;
		var xbutton = _xbutton;
		var errormessage = _errormessage;
		
		this.hide = function(){
			errorbox.classList.add("invisible");
		}
		
		this.setError = function(message){
			self = this;
			errorbox.setAttribute("class", "error");
			errormessage.textContent = message;
			xbutton.addEventListener('click', (e) => {
				errorbox.classList.add("invisible");
				errormessage.textContent="";
			});	
		}
		
		this.setSuccess = function(message){
			self = this;
			errorbox.setAttribute("class", "success");
			errormessage.textContent = message;
			xbutton.addEventListener('click', (e) => {
				errorbox.classList.add("invisible");
				errormessage.textContent="";
			});	
		}
		
	}
  
  function NameField(_name, namecontainer){
	  //User name.
		this.name = _name;
		this.show = function(){
			namecontainer.textContent = this.name;
			namecontainer.classList.remove("invisible");
		}
		this.hide = function(){
			namecontainer.classList.add("invisible");
		}
	}
  
  function QuotationTable(_div_tabella){ 
	  //table containing already priced quotations.
	  
	  var tabella = _div_tabella.getElementsByTagName("table")[0];
	  var body = tabella.getElementsByTagName("tbody")[0];
	  var error = _div_tabella.getElementsByClassName("error")[0];
	  
	  this.hide = function(){
		  tabella.classList.add("invisible");
		  error.classList.add("invisible");
		  body.innerHTML = ""; //empty the body of the table.
		  _div_tabella.classList.add("invisible");
	  };
	  
	  this.show = function(){
		  var self=this;
		  makeCall("GET", "ManagedQuotations", null,
				  function(req) {
			  if (req.readyState == XMLHttpRequest.DONE) {
				  var message = req.responseText;
				  switch (req.status) {
				  case 200: //response ok (can still be empty).
					  var list = JSON.parse(message);
					  self.update(list);
					  break;
				  case 500: // server error + message.
					  tabella.classList.add("invisible");
					  error.textContent = message;
					  error.classList.remove("invisible");
					  break;
				  case 403: //if access is forbidden (like an expired session) a message is sent containing the destination url.
	            	  window.location.href = message;
	            	  break;
				  default: // servlet connection failed, or other errors arose.
					  tabella.classList.add("invisible");
					  error.textContent = 
						  "È avvenuto un errore (Servlet non disponibile)";
				  	  error.classList.remove("invisible");
				  }
			  }
		  }
		  );
		  _div_tabella.classList.remove("invisible");;
	  };

	  this.update = function(quotation_array){
		  body.innerHTML = ""; //empty the body of the table.
		  if(quotation_array.length === 0){ 
			  //if response is empty, show an error and hide the table.
			  tabella.classList.add("invisible");
			  error.textContent = 
				  "Non è stata fatta ancora alcuna quotazione.";
			  error.classList.remove("invisible");
		  }
		  else{
			  //show the table and hide the error
			  // (an error might remain from previous state changes).
			  quotation_array.forEach(item => {
				  	var row = document.createElement("tr");
				  	var v1cell = document.createElement("td");
				  	v1cell.textContent = item.quotationId;
				  	row.appendChild(v1cell);
				  	var v2cell = document.createElement("td");
				  	v2cell.textContent = item.productName;
				  	row.appendChild(v2cell);
				  	var v3cell = document.createElement("td");
				  	v3cell.textContent = item.clientUsername;
				  	row.appendChild(v3cell);
				  	var v4cell = document.createElement("td");
				  	v4cell.textContent = item.value + " €";
				  	row.appendChild(v4cell);
				  	body.appendChild(row);
				  	error.classList.add("invisible");
				  	tabella.classList.remove("invisible");
				  }
			  )
		  }
	  };

  }
  
  function FreeTable(_div_tabella){
	  // represents the table containing the list of quotation 
	  // that are to be priced.
	  
	  var tabella = _div_tabella.getElementsByTagName("table")[0];
	  var body = tabella.getElementsByTagName("tbody")[0];
	  var error = _div_tabella.getElementsByClassName("error")[0];
	  
	  
	  this.hide = function(){
		  tabella.classList.add("invisible");;
		  error.classList.add("invisible");;
		  body.innerHTML = ""; //empty the body of the table.
		  _div_tabella.classList.add("invisible");;
	  };
	  
	  this.show = function(){
		  var self=this;
		  makeCall("GET", "FreeQuotations", null,
				  function(req) {
			  if (req.readyState == XMLHttpRequest.DONE) {
				  var message = req.responseText;
				  switch (req.status) {
				  case 200: //response arrived (can still be empty).
					  var list = JSON.parse(message);
					  self.update(list);
					  break;
				  case 500: // server error + message.
					  error.textContent = message;
					  tabella.classList.add("invisible"); 
					  	//inserted because the table could be visible after some manipulation.
					  error.classList.remove("invisible");
					  break;
				  case 403: //if access is forbidden (like an expired session) a message is sent containing the destination url.
	            	  window.location.href = message;
	            	  break;
				  default: // servlet connection failed, or other errors arose.
					  error.textContent = 
						  "È avvenuto un errore (Servlet non disponibile)";
				  	  tabella.classList.add("invisible");
				  	  error.classList.remove("invisible");
				  }
			  }
		  }
		  );
		  _div_tabella.classList.remove("invisible");
	  };
	  
	  this.update = function(free_array){
		  body.innerHTML = ""; //empty the body of the table.
		  if(free_array.length === 0){ 
			  //if the response contains an empty array, show an error 
			  //and hide the table.
			  error.textContent = 
				  "Non ci sono quotazioni libere.";
		  	  error.classList.remove("invisible");
		  	  tabella.classList.add("invisible");
		  }
		  else{
			  //fill the table, show it and hide the error.
			  free_array.forEach(item => { 
				  	var row = document.createElement("tr");
				  	var v1cell = document.createElement("td");
				  	v1cell.textContent = item.quotationId;
				  	row.appendChild(v1cell);
				  	var v2cell = document.createElement("td");
				  	v2cell.textContent = item.productName;
				  	row.appendChild(v2cell);
				  	var v3cell = document.createElement("td");
				  	v3cell.textContent = item.clientUsername;
				  	row.appendChild(v3cell);
				  	
				  	var v4cell = document.createElement("td");
				  	v4cell.classList.add("freecellbutton");
				  	var visualButton = document.createElement("button");
				  	var span = document.createElement("span");
				  	span.textContent = "Visualizza Richiesta";
				  	
				  	visualButton.addEventListener('click', (e) => {
				  		
				  		pageOrchestrator.changeToPricePage(item);
				  			
				  	})
				  	visualButton.classList.add("button");
				  	visualButton.classList.add("visualButton");
				  	
				  	visualButton.appendChild(span);
				  	v4cell.appendChild(visualButton);
				  	row.appendChild(v4cell);
				  	
				  	body.appendChild(row);
				  } 
			  )
			  tabella.classList.remove("invisible");
			  error.classList.add("invisible");
			  
		  }
	  };
	  
  }
  
  function Options(_optionTab){
	  
	  // object representing the table containing the options chosen
	  // by the user.
	  
	  var optionerror = _optionTab.getElementsByClassName("error")[0];
	  var optiontable = _optionTab.getElementsByTagName("table")[0];
	  var optiontablebody = optiontable.getElementsByTagName("tbody")[0];
	  
	  this.show = function(item){
		  self = this;
		  optiontablebody.innerHTML = ""; //empty the body of the table.
		  if(item.options.length === 0){
			  //if no options were chosen by the client.
		  		optiontable.classList.add("invisible");
		  		optionerror.textContent="Non sono disponibili opzioni " +
		  								"per questo prodotto."
		  		optionerror.classList.remove("invisible");
		  }
		  else{
			  // if some options were chosen by the client.
			optionerror.classList.add("invisible");
	  		self.update(item);
	  		optiontable.classList.remove("invisible");
	  	  }
		  _optionTab.classList.remove("invisible");
	  }
	  this.update = function(item){
		  item.options.forEach(option =>{
			var row = document.createElement("tr");
			var v1cell = document.createElement("td");
			v1cell.textContent = option.name;
			row.appendChild(v1cell);
			var v2cell = document.createElement("td");
			v2cell.textContent = (option.inOffer ? "si" : "no");
			row.appendChild(v2cell);
			optiontablebody.appendChild(row);
		  });
	  }
	  this.hide = function(){
		  _optionTab.classList.add("invisible");
		  optionerror.classList.add("invisible");
	  }
  
  }
  
  function PriceForm(_div_price){
	  
	  //Object representing the input form for the price.
	  //please note: all the checks on the value of price are
	  //made automatically by the html browser.
	  
	  //In any case, a check was also explicitly made since 
	  // some browsers may not support form popup captions
	  // and form input limitations.
	  
	  var inputPrice = document.getElementById("price");
	  var inputQuotation = document.getElementById("quotation");
	  var inputButton = document.getElementById("submitprice");
	  var priceerror = document.getElementById("priceerror");
	  var backButton = document.getElementById("priceback");
	  
	  this.hide = function(){
		  _div_price.classList.add("invisible");
		  priceerror.classList.add("invisible");
		  inputQuotation.setAttribute("value","default");
	  }
	  
	  this.show = function(item){
		  self = this;
		  _div_price.classList.remove("invisible");
		  self.update(item);
	  }
	  
	  this.update = function(item){
		  
		  // I need to substitute the button with a new one
		  // to remove all the eventListeners 
		  // Otherwise, new event listeners do not overwrite previous ones.
		  inputButton_new = inputButton.cloneNode(true);
		  inputButton.parentNode.replaceChild(inputButton_new, inputButton);
		  inputButton = inputButton_new;
		  
		  inputButton.addEventListener('click', (e) => {
			  priceerror.classList.add("invisible");
			  
			  var price = inputPrice.value;
			  var form = e.target.closest("form");
			  if(form.checkValidity()){
				  //checks AFTER the form validity.
				  
				  if (price <= 0|| isNaN(price)){
					  //if the price is 0 or Not a Number (e.g. a string).
					  //These two cases were put together because the 
					  // default behaviour for a input box is to give to it
					  // the minum possible amount if the input does not respect the 
					  // format of the box and the validity is not checked.
					  var errormessage="Il prezzo inserito deve essere un numero " +
					  					"e deve essere maggiore di 0";
					  priceerror.textContent = errormessage;
					  priceerror.classList.remove("invisible");
				  }
				  else if (price.toString().indexOf('.') != -1 
					  && price.toString().split('.')[1].length > 2){
					  var errormessage = "Si prega di inserire un prezzo " +
					  			"valido che rispetti il formato visualizzato";
					  priceerror.textContent = errormessage;
					  priceerror.classList.remove("invisible");
				  }
				  
				  else{
					  inputQuotation.setAttribute("value", item.quotationId);
					  makeCall("POST", 'PricePost', e.target.closest("form"),
			            function(req) {
						  
			              if (req.readyState == XMLHttpRequest.DONE) {
			            	var message = req.responseText; // error message or message response.
			            	switch (req.status) {
			            	case 200 :
			                	//all ok. Insertion went smoothly.
			                	pageOrchestrator.showSuccess(message);
			                	break;
			            	case 500:
			                	//server error
			                	pageOrchestrator.showError(message);
			                	break;
			              	case 403: //if access is forbidden (like an expired session) a message is sent containing the destination url.
			            	  window.location.href = message;
			            	  break;
			                default:
			                	//user input or other kinds of errors.
			                  priceerror.textContent = message;
			  				  priceerror.classList.remove("invisible");
			                }
			              }
			            }
			          ,true);
				  }
			  }
			  else{
				  form.reportValidity(); 
				  //This brings up the popup bubble on the form.
			  }
		  })
		  backButton.addEventListener('click', (e) => {
			  pageOrchestrator.refresh();
		  });
	  }
  }
  
  function PriceQuotation(_div_info){
	  
	  //meta object controlling Option, PriceForm and the info table. 
	  //The latter is not represented as an object, since it is just the 
	  // table containing the information about the client and the 
	  // chosen product.
	  var optionTab = document.getElementById("optiontable");
	  var optionsTable = new Options(optionTab);
	  
	  var formDiv = document.getElementById("priceinsertion");
	  var priceForm = new PriceForm(formDiv);
	  
	  var infoTab = document.getElementById("infotable");
	  // only used to make the info section visible/invisible
	  
	  var productEntry = document.getElementById("productname");
	  var clientEntry = document.getElementById("clientname");
	  // actual info section to be filled.
	  
	  this.hide = function(){
		  _div_info.classList.add("invisible");
		  infoTab.classList.add("invisible");
		  optionsTable.hide();
		  priceForm.hide();
	  };
	  
	  this.show = function(item){
		  this.update(item);
		  optionsTable.show(item);
		  priceForm.show(item);
		  infoTab.classList.remove("invisible");
		  _div_info.classList.remove("invisible");
	  }
	  
	  this.update = function(item){
		  //set the infotable up.
		  clientEntry.textContent = item.clientUsername;
		  productEntry.textContent = item.productName;
	  }
  }

  
  function PageOrchestrator(){

	this.start = function(){
		//first check to be logged in as a worker.
		// This check is made on the server side too.
		if(sessionStorage.getItem('role')!="worker"){
			window.location.href = "index.html";
		}
		else{
			body = document.getElementsByTagName("body")[0];
			
			//set the name of the user.
			namefield = new NameField(sessionStorage.getItem('name'),
					document.getElementById('user_name')
				  );
			
			//set up all the objects listed above:
			
			// 1 - the error box, used to show the success or error on 
			// the pricing operation.
			errorBox = new ErrorBox(document.getElementById("errorbox"),
					document.getElementById("xbutton"),
					document.getElementById("errortext")
			);
			
			// 2 - Meta object controlling the objects needed to 
			// list the informations of a price requests and to 
			// perform the price insertion.
			var priceQuot = document.getElementById("pricequotation");
			pricediv = new PriceQuotation(priceQuot);
			
			// 3 - Table containing already done quotations. 
		    var quotTab = document.getElementById("quotationtable");
		    quotationtable = new QuotationTable(quotTab);
		    
		    //4 - Table containing price requests to be done.
		    var freeTab = document.getElementById("freetable");
		    freetable = new FreeTable(freeTab);
		}
	};
	
	this.refresh = function(){	//gets the page to a default status	
		
		//these change the background image.
		body.classList.add("workerpage");
		body.classList.remove("pricepage");
		
		namefield.show();
		quotationtable.show();
		freetable.show();
		pricediv.hide();
		errorBox.hide();
	};
	
	this.changeToPricePage = function(item){ 
		//called to change the current page to only show useful informations
		// to make the quotation.
  		freetable.hide();
  		quotationtable.hide();
		body.classList.add("pricepage");
		body.classList.remove("workerpage");
		pricediv.show(item);
	}
	
	this.showSuccess = function(message){
		// called in case of success.
		pageOrchestrator.refresh();
		errorBox.setSuccess(message);
		window.scrollTo({
			  top: 0,
			  left: 100,
			  behavior: 'smooth'
			});
	};
	this.showError = function(message){
		// called in case of error.
		pageOrchestrator.refresh();
		errorBox.setError(message);
		window.scrollTo({
			  top: 0,
			  left: 100,
			  behavior: 'smooth'
			});
	};

  }


})();
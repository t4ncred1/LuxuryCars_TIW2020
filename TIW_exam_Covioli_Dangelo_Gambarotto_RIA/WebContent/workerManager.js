/**
 * 
 */

(function() {

// page components
  var quotationtable, freetable, pricediv, success,
  servererror,
    pageOrchestrator = new PageOrchestrator(); // main controller

  window.addEventListener("load", () => {
      pageOrchestrator.start(); // initialize the components
      pageOrchestrator.refresh();
      pageOrchestrator.show();
  }, false);

  function QuotationTable(_div_tabella){ 
	  
	  var tabella = _div_tabella.getElementsByTagName("table")[0];
	  var body = tabella.getElementsByTagName("tbody")[0];
	  var error = _div_tabella.getElementsByClassName("error")[0];
	  
	  this.reset = function(){
		  tabella.style.visibility = "hidden";
		  tabella.style.display = "none";
		  error.style.visibility = "hidden";
		  error.style.display = "none";
		  body.innerHTML = ""; //empty the body of the table.
		  _div_tabella.style.display = "none";
	  };

	  this.show = function(){
		  var self=this;
		  makeCall("GET", "ManagedQuotations", null,
				  function(req) {
			  if (req.readyState == XMLHttpRequest.DONE) {
				  var message = req.responseText;
				  switch (req.status) {
				  case 200:
					  var list = JSON.parse(message);
					  self.update(list);
					  break;
				  case 503:
					  error.textContent = message;
					  error.style.visibility = "visible";
					  error.style.display = "";
					  break;
				  case 411:
					  
				  default:
					  error.textContent = 
						  "È avvenuto un errore (Servlet non disponibile)";
				  	  error.style.visibility = "visible";
				  	  error.style.display = "";
				  }
			  }
		  }
		  );
		  _div_tabella.style.display = "";
	  };

	  this.update = function(quotation_array){
		  var self=this;
		  body.innerHTML = ""; //empty the body of the table.
		  if(quotation_array.length == 0){
			  error.textContent = 
				  "Non è stata fatta ancora alcuna quotazione.";
		  	  error.style.visibility = "visible";
		  	  error.style.display = "";
		  }
		  else{
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
				  	v4cell.textContent = item.value;
				  	row.appendChild(v4cell);
				  	body.appendChild(row);
				  	tabella.style.visibility = "visible";
				  	tabella.style.display = "";
				  }  	
			  )
		  }
	  };

  }
  
  function FreeTable(_div_tabella){
	  var tabella = _div_tabella.getElementsByTagName("table")[0];
	  var body = tabella.getElementsByTagName("tbody")[0];
	  var error = _div_tabella.getElementsByClassName("error")[0];
	  
	  
	  this.reset = function(){
		  tabella.style.visibility = "hidden";
		  tabella.style.display = "none";
		  error.style.visibility = "hidden";
		  error.style.display = "none";
		  body.innerHTML = ""; //empty the body of the table.
		  _div_tabella.style.display="none";
	  };
	  
	  this.show = function(){
		  var self=this;
		  makeCall("GET", "FreeQuotations", null,
				  function(req) {
			  if (req.readyState == XMLHttpRequest.DONE) {
				  var message = req.responseText;
				  switch (req.status) {
				  case 200:
					  var list = JSON.parse(message);
					  self.update(list);
					  break;
				  case 503:
					  error.textContent = message;
					  error.style.visibility = "visible";
					  error.style.display = "";
					  break;
				  default:
					  error.textContent = 
						  "È avvenuto un errore (Servlet non disponibile)";
				  	  error.style.visibility = "visible";
				  	  error.style.display = "";
				  }
			  }
		  }
		  );
		  _div_tabella.style.display="";
	  };
	  
	  this.update = function(free_array){
		  body.innerHTML = ""; //empty the body of the table.
		  if(free_array.length == 0){
			  error.textContent = 
				  "Non ci sono quotazioni libere.";
		  	  error.style.visibility = "visible";
		  	  error.style.display = "";
		  }
		  else{
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
				  	var visualButton = document.createElement("button");
				  	var span = document.createElement("span");
				  	span.textContent = "Visualizza Richiesta";
				  	
				  	visualButton.addEventListener('click', (e) => {
				  		pricediv.show(item);
				  	})
				  	
				  	visualButton.appendChild(span);
				  	v4cell.appendChild(visualButton);
				  	row.appendChild(v4cell);
				  	
				  	body.appendChild(row);
				  } 
			  )
			  tabella.style.visibility = "visible";
			  tabella.style.display = "";
			  
		  }
	  };
	  
  }
  
  function Options(_optionTab){
	  var optionerror = _optionTab.getElementsByClassName("error")[0];
	  var optiontable = _optionTab.getElementsByTagName("table")[0];
	  var optiontablebody = optiontable.getElementsByTagName("tbody")[0];
	  
	  this.show = function(item){
		  self = this;
		  optiontablebody.innerHTML = ""; //empty the body of the table.
		  if(item.options.length == 0){
		  		optiontable.style.display = "none";
		  		optionerror.textContent="Non sono disponibili opzioni per questo prodotto."
		  		optionerror.style.visibility = "visible";
		  }
		  else{
			optiontable.style.display = "";
			optiontable.style.visibility = "visible"
	  		self.update(item);
	  	  }
		  _optionTab.style.display = "";
		  _optionTab.style.visibility = "visible";
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
	  this.reset = function(){
		  _optionTab.style.display = "none";
		  optionerror.style.visibility = "hidden";
	  }
  
  }
  
  function PriceForm(_div_price){
	  var inputPrice = document.getElementById("price");
	  var inputQuotation = document.getElementById("quotation");
	  var inputButton = document.getElementById("submitprice");
	  var priceerror = document.getElementById("priceerror");
	  
	  this.reset = function(){
		  _div_price.style.display = "none";
		  priceerror.style.display = "none";
		  priceerror.style.visibility = "hidden";
		  inputQuotation.setAttribute("value","default");
	  }
	  
	  this.show = function(item){
		  self = this;
		  _div_price.style.display = "";
		  _div_price.style.visibility = "visible";
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
			  priceerror.style.display = "none";
			  priceerror.style.visibility = "hidden";
			  
			  var price = inputPrice.value;
			  var form = e.target.closest("form");
			  if(form.checkValidity()){
				  if (price <= 0){
					  var errormessage="Il prezzo inserito deve essere maggiore di 0";
					  priceerror.textContent = errormessage;
					  priceerror.style.display = "";
					  priceerror.style.visibility = "visible";
				  }
				  else if (price.toString().indexOf('.') != -1 
					  && price.toString().split('.')[1].length > 2){
					  var errormessage = "Si prega di inserire un prezzo valido che rispetti il formato visualizzato";
					  priceerror.textContent = errormessage;
					  priceerror.style.display = "";
					  priceerror.style.visibility = "visible";
				  }
				  
				  else{
					  inputQuotation.setAttribute("value", item.quotationId);
					  makeCall("POST", 'PricePost', e.target.closest("form"),
			            function(req) {
			              if (req.readyState == XMLHttpRequest.DONE) {
			                
			            	var message = req.responseText; // error message or message response.
			                
			                if (req.status == 200) {
			                	pageOrchestrator.refresh();
			                    pageOrchestrator.show();
			                    success.textContent = message;
			                    success.style.display = "";
			                    success.style.visibility = "visible"
			                } 
			                else if (req.status == 503){
			                	pageOrchestrator.refresh();
			                    pageOrchestrator.show();
			                	servererror.textContent = message;
			                	servererror.style.display = "";
			                	servererror.style.visibility = "visible"
			                }
			                else{
			                  priceerror.textContent = message;
			  				  priceerror.style.display = "";
			  				  priceerror.style.visibility = "visible";
			                }
			              }
			            }
			          ,true);
				  }
			  }
			  else{
				  form.reportValidity();
			  }
		  })
	  }
  }
  
  function PriceQuotation(_div_info){
	  
	  var optionTab = document.getElementById("optiontable");
	  var optionsTable = new Options(optionTab);
	  
	  var formDiv = document.getElementById("priceinsertion");
	  var priceForm = new PriceForm(formDiv);
	  
	  var infoTab = document.getElementById("infotable");
	  
	  var productEntry = document.getElementById("productname");
	  var clientEntry = document.getElementById("clientname");
	  this.reset = function(){
		  _div_info.style.display = "none";
		  infoTab.style.visibility = "hidden";
		  optionsTable.reset();
		  priceForm.reset();
	  };
	  
	  this.show = function(item){
		  this.update(item);
		  optionsTable.show(item);
		  priceForm.show(item);
		  infoTab.style.visibility = "visible";
		  _div_info.style.display= "";
		  _div_info.style.visibility= "visible";
	  }
	  
	  this.update = function(item){
		  clientEntry.textContent = item.clientUsername;
		  productEntry.textContent = item.productName;
	  }
  }

  
  function PageOrchestrator(){

	this.start = function(){
		var priceQuot = document.getElementById("pricequotation");
		pricediv = new PriceQuotation(priceQuot);
		
	    var quotTab = document.getElementById("quotationtable");
	    quotationtable = new QuotationTable(quotTab);
	    
	    var freeTab = document.getElementById("freetable");
	    freetable = new FreeTable(freeTab);
	    
	    success = document.getElementById("success");
	    servererror = document.getElementById("servererror");
	};
	
	this.show = function(){
		quotationtable.show();
		freetable.show();
	}
	
	this.refresh = function(){
		quotationtable.reset();
		freetable.reset();
		pricediv.reset();
		success.style.display = "none";
		success.textContent = "Il prezzo è stato inserito correttamente.";
		servererror.style.display = "none";
		servererror.textContent = "È stato riscontrato un errore lato server.";
	};

  }

})();
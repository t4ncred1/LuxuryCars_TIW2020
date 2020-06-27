/**
 * 
 */

(function() {

// page components
  var quotationtable, freetable, pricediv,
    pageOrchestrator = new PageOrchestrator(); // main controller

  window.addEventListener("load", () => {
      pageOrchestrator.start(); // initialize the components
      pageOrchestrator.refresh();
  }, false);

  function QuotationTable(_div_tabella){ 
	  
	  var tabella = _div_tabella.getElementsByTagName("table")[0];
	  var body = tabella.getElementsByTagName("tbody")[0];
	  var error = _div_tabella.getElementsByClassName("error")[0];
	  
	  this.reset = function(){
		  tabella.style.visibility = "hidden";
		  error.style.visibility = "hidden";
	  };

	  this.show = function(){
		  var self = this;
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
					  self.error.textContent = message;
					  self.error.style.visibility = "visible";
					  break;
				  case 411:
					  
				  default:
					  self.error.textContent = 
						  "È avvenuto un errore (Servlet non disponibile)";
				  	  self.error.style.visibility = "visible";
				  }
			  }
		  }
		  );
	  };

	  this.update = function(quotation_array){
		  body.innerHTML = ""; //empty the body of the table.
		  if(quotation_array.length == 0){
			  self.error.textContent = 
				  "Non è stata fatta ancora alcuna quotazione.";
		  	  self.error.style.visibility = "visible";
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
		  error.style.visibility = "hidden";
	  };
	  
	  this.show = function(){
		  var self = this;
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
					  self.error.textContent = message;
					  self.error.style.visibility = "visible";
					  break;
				  default:
					  self.error.textContent = 
						  "È avvenuto un errore (Servlet non disponibile)";
				  	  self.error.style.visibility = "visible";
				  }
			  }
		  }
		  );
	  };
	  
	  this.update = function(free_array){
		  body.innerHTML = ""; //empty the body of the table.
		  if(free_array.length == 0){
			  self.error.textContent = 
				  "Non ci sono quotazioni libere.";
		  	  self.error.style.visibility = "visible";
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
		  }
	  };
	  
  }
  
  function PriceQuotation(_div_info){
	  
	  var productEntry = document.getElementById("productname");
	  var clientEntry = document.getElementById("clientname");
	  
	  
	  var optionTable = document.getElementById("optiontable");
	  var optionerror = optionTable.getElementsByClassName("error")[0];
	  var optionTableBody = optionTable.getElementsByTagName("tbody")[0];
	  
	  this.reset = function(){
		  //TODO
	  };
	  
	  this.show = function(item){
	  	
		//Option table
		clientEntry.textContent = item.clientUsername;
	  	productEntry.textContent = item.productName;
	  	document.getElementById("infotable").style.visibility = "visible";
	  	
	  	optionTableBody.innerHTML = ""; //empty the body of the table.
	  	if(item.options.length == 0){
	  		optionerror.textContent="Non sono disponibili opzioni per questo prodotto."
	  		optionerror.style.visibility = "visible";
	  	}
	  	else{
	  		item.options.forEach(option =>{
		  		var row = document.createElement("tr");
			  	var v1cell = document.createElement("td");
			  	v1cell.textContent = option.name;
			  	row.appendChild(v1cell);
			  	var v2cell = document.createElement("td");
			  	v2cell.textContent = (option.inOffer ? "si" : "no");
			  	row.appendChild(v2cell);
			  	optionTableBody.appendChild(row);
		  	});
		  	optionTable.style.visibility = "visible";
	  	}
	  	//TODO
	  }
  }

  
  function PageOrchestrator(){

	this.start = function(){
		var priceQuot = document.getElementById("pricequotation");
		pricediv = new PriceQuotation(priceQuot);
		
	    var quotTab = document.getElementById("quotationtable");
	    quotationtable = new QuotationTable(quotTab);
	    quotationtable.show();
	    
	    var freeTab = document.getElementById("freetable");
	    freetable = new FreeTable(freeTab);
	    freetable.show();
	};
	
	this.refresh = function(){
		//TODO insert here which elements shall be hidden/reset.
	};

  }

})();
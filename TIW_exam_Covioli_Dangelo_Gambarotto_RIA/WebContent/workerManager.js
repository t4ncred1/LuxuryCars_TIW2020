/**
 * 
 */

(function() {

// page components
  var quotationtable, freetable, pricediv, errorBox, namefield,
    pageOrchestrator = new PageOrchestrator(); // main controller

  window.addEventListener("load", () => {
      pageOrchestrator.start(); // initialize the components
      pageOrchestrator.refresh();
      pageOrchestrator.show();
  }, false);

  function ErrorBox(_errorbox, _xbutton, _errormessage){
		this.errorbox = _errorbox;
		this.xbutton = _xbutton;
		this.errormessage = _errormessage;
		
		this.hide = function(){
			this.errorbox.setAttribute("class", "invisible");
		}
		
		this.setError = function(message){
			self = this;
			this.errorbox.setAttribute("class", "error")
			this.errormessage.textContent = message;
			this.xbutton.addEventListener('click', (e) => {
				self.errorbox.setAttribute("class", "invisible");
				self.errormessage.textContent="";
			});	
		}
		
		this.setSuccess = function(message){
			self = this;
			this.errorbox.setAttribute("class", "success");
			this.errormessage.textContent = message;
			this.xbutton.addEventListener('click', (e) => {
				self.errorbox.setAttribute("class", "invisible");
				self.errormessage.textContent="";
			});	
		}
		
	}
  
  function NameField(_name, namecontainer){
		this.name = _name;
		this.show = function(){
			namecontainer.textContent = this.name;
		}
	}
  
  function QuotationTable(_div_tabella){ 
	  
	  var tabella = _div_tabella.getElementsByTagName("table")[0];
	  var body = tabella.getElementsByTagName("tbody")[0];
	  var error = _div_tabella.getElementsByClassName("error")[0];
	  
	  this.reset = function(){
		  tabella.setAttribute("class","table invisible");
		  error.setAttribute("class","error invisible");
		  body.innerHTML = ""; //empty the body of the table.
		  _div_tabella.setAttribute("class","invisible");
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
					  error.setAttribute("class","error");
					  break;
				  case 411:
					  
				  default:
					  error.textContent = 
						  "È avvenuto un errore (Servlet non disponibile)";
				  	  error.setAttribute("class","error");
				  }
			  }
		  }
		  );
		  _div_tabella.setAttribute("class","");
	  };

	  this.update = function(quotation_array){
		  var self=this;
		  body.innerHTML = ""; //empty the body of the table.
		  if(quotation_array.length == 0){
			  error.textContent = 
				  "Non è stata fatta ancora alcuna quotazione.";
			  error.setAttribute("class","error");
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
				  	v4cell.textContent = item.value + " €";
				  	row.appendChild(v4cell);
				  	body.appendChild(row);
				  	tabella.setAttribute("class","table");
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
		  tabella.setAttribute("class","table invisible");
		  error.setAttribute("class","error invisible");
		  body.innerHTML = ""; //empty the body of the table.
		  _div_tabella.setAttribute("class","invisible");
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
					  error.setAttribute("class","error");
					  break;
				  default:
					  error.textContent = 
						  "È avvenuto un errore (Servlet non disponibile)";
				  	  error.setAttribute("class","error");
				  }
			  }
		  }
		  );
		  _div_tabella.setAttribute("class","");
	  };
	  
	  this.update = function(free_array){
		  body.innerHTML = ""; //empty the body of the table.
		  if(free_array.length == 0){
			  error.textContent = 
				  "Non ci sono quotazioni libere.";
		  	  error.setAttribute("class","error");
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
			  tabella.setAttribute("class","table");
			  
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
		  		optiontable.setAttribute("class","workertable table hidden");
		  		optionerror.textContent="Non sono disponibili opzioni per questo prodotto."
		  		optionerror.setAttribute("class","error");
		  }
		  else{
			optiontable.setAttribute("class","workertable table");
	  		self.update(item);
	  	  }
		  _optionTab.setAttribute("class","");
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
		  _optionTab.setAttribute("class","hidden");
		  optionerror.setAttribute("class","error invisible");
	  }
  
  }
  
  function PriceForm(_div_price){
	  var inputPrice = document.getElementById("price");
	  var inputQuotation = document.getElementById("quotation");
	  var inputButton = document.getElementById("submitprice");
	  var priceerror = document.getElementById("priceerror");
	  
	  this.reset = function(){
		  _div_price.setAttribute("class","box invisible");
		  priceerror.setAttribute("class","error-paragraph invisible");
		  inputQuotation.setAttribute("value","default");
	  }
	  
	  this.show = function(item){
		  self = this;
		  _div_price.setAttribute("class","box");
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
			  priceerror.setAttribute("class","error-paragraph invisible");
			  
			  var price = inputPrice.value;
			  var form = e.target.closest("form");
			  if(form.checkValidity()){
				  if (price <= 0){
					  var errormessage="Il prezzo inserito deve essere maggiore di 0";
					  priceerror.textContent = errormessage;
					  priceerror.setAttribute("class","error-paragraph");
				  }
				  else if (price.toString().indexOf('.') != -1 
					  && price.toString().split('.')[1].length > 2){
					  var errormessage = "Si prega di inserire un prezzo valido che rispetti il formato visualizzato";
					  priceerror.textContent = errormessage;
					  priceerror.setAttribute("class","error-paragraph");
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
			                    errorBox.setSuccess(message);
			                } 
			                else if (req.status == 503){
			                	pageOrchestrator.refresh();
			                    pageOrchestrator.show();
			                	errorBox.setError(message);
			                }
			                else{
			                  priceerror.textContent = message;
			  				  priceerror.setAttribute("class","error-paragraph");
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
		  _div_info.setAttribute("class","invisible");
		  infoTab.setAttribute("class","invisible");
		  optionsTable.reset();
		  priceForm.reset();
	  };
	  
	  this.show = function(item){
		  this.update(item);
		  optionsTable.show(item);
		  priceForm.show(item);
		  infoTab.setAttribute("class","");
		  _div_info.setAttribute("class","");
	  }
	  
	  this.update = function(item){
		  clientEntry.textContent = item.clientUsername;
		  productEntry.textContent = item.productName;
	  }
  }

  
  function PageOrchestrator(){

	this.start = function(){
		if(sessionStorage.getItem('role')!="worker"){
			window.location.href = "index.html";
		}
		else{
			namefield = new NameField(sessionStorage.getItem('name'),
					document.getElementById('user_name')
				  );
			errorBox = new ErrorBox(document.getElementById("errorbox"),
					document.getElementById("xbutton"),
					document.getElementById("errortext")
			);
			
			var priceQuot = document.getElementById("pricequotation");
			pricediv = new PriceQuotation(priceQuot);
			
		    var quotTab = document.getElementById("quotationtable");
		    quotationtable = new QuotationTable(quotTab);
		    
		    var freeTab = document.getElementById("freetable");
		    freetable = new FreeTable(freeTab);
		}
	};
	
	this.show = function(){
		quotationtable.show();
		freetable.show();
	}
	
	this.refresh = function(){
		namefield.show();
		quotationtable.reset();
		freetable.reset();
		pricediv.reset();
		errorBox.hide();
	};

  }

})();
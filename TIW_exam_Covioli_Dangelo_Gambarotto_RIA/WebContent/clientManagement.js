/*  _______ _______          __                                    
 * |__   __|_   _\ \        / /                                    
 *    | |    | |  \ \  /\  / /                                     
 *    | |    | |   \ \/  \/ /                                      
 *    | |   _| |_   \  /\  /                                       
 *    |_|  |_____|   \/  \/   
 * 
 * exam project - a.y. 2019-2020
 * Politecnico di Milano
 * 
 * Tancredi Covioli   mat. 944834
 * Alessandro Dangelo mat. 945149
 * Gambarotto Luca    mat. 928094
 */


(function() {

	/* errorBox = pop-up bar on top of the page
	 * reqTable = table containing all the submitted requests
	 * carform = form to select the car, the options and send a request
	 * quotationDetails = box that shows the details of a submitted
	 * 						request of quotation
	 */
    var errorBox, reqTable, carForm, quotationDetails, pageOrchestrator = new PageOrchestrator();
    
    /* On load the page orchestrator build all the objects needed,
     * bind them to the relative elements in the html page and then
     * refresh them all to populate the page with the personal information
     * of the user 
     */
    window.addEventListener("load", () => {
        pageOrchestrator.start();
		pageOrchestrator.refresh();
    }, false);

   
    /* this function builds an object that maintains the
     * welcome message on top of the page, filling it with the
     * name of the user
     */
	function NameField(_name, namecontainer){
		this.name = _name;
		this.show = function(){
			namecontainer.textContent = this.name;
		}
	}

	/* this function builds the errorBox and populate it with
	 * a set of useful functions to easily change its behavior
	 */
	function ErrorBox(_errorbox, _xbutton, _errormessage){
		this.errorbox = _errorbox;
		this.xbutton = _xbutton;
		this.errormessage = _errormessage;
		
		/* This function hide the errorBox. It is binded to the X
		 * button in the box itself but it is used by the orchestrator
		 * too when the page is refreshed
		 */
		this.hide = function(){
			this.errorbox.setAttribute("class", "invisible");
		}
		
		/* This function set the errorBox with an error message
		 * The background the the font text are set to red using
		 * the class "error". Notice that the X button is associated to
		 * the previous function to allow the user to close the box
		 */
		this.setError = function(message){
			self = this;
			this.errorbox.setAttribute("class", "error")
			this.errormessage.textContent = message;
			this.xbutton.addEventListener('click', (e) => {
				self.errorbox.setAttribute("class", "hidden");
				self.errormessage.textContent="";
			});	
		}

		/* This function set the errorBox with a success message
		 * The background the the font text are set to green using
		 * the class "success". Notice that the X button is associated to
		 * the previous function to allow the user to close the box
		 */
		this.setSuccess = function(message){
			self = this;
			this.errorbox.setAttribute("class", "success")
			this.errormessage.textContent = message;
			this.xbutton.addEventListener('click', (e) => {
				self.errorbox.setAttribute("class", "hidden");
				self.errormessage.textContent="";
			});	
		}
		
	}
	
	/* This function builds the requestTable and a set of
	 * associated function to maintain the table itself, allowing to
	 * populate it with the personal data of the user (the previously
	 * submitted requests) and to refresh it.
	 */
	function RequestTable(_table, _body, _requests, _norequests){
		this.table = _table;
		this.body = _body;
		this.requestsBox = _requests;
		this.norequestsBox = _norequests;
		
		/* This function make a call to the server to request all
		 * the user previously submitted requests. If the response from
		 * the server has code 200 the response message is passed to the
		 * function update, otherwise the orchestrator is called and the
		 * error box is set to the message received from the server.
		 */
		this.show = function(orchestrator){
			selfTable = this;
	        makeCall("GET", "GetClientRequests", null,
	          function(req) {
	            if (req.readyState == 4) {
	              var message = req.responseText;
	              if (req.status == 200) {
	                selfTable.update(JSON.parse(req.responseText)); 
	              } else {
	                pageOrchestrator.showError(message);
	              }
	            }
	          }
	        );
		}
		
		/* This function shows a message of alert in case the empty boolean is set
		 * true, hiding the table. Otherwise the message is removed and the table
		 * with the previously submitted requests is shown. This allows to completely
		 * hide the table in case the user has not send any request yet.
		 */
		this.emptyTable = function(empty){
			if(empty==true){
				this.norequestsBox.setAttribute("class", "");
				this.norequestsBox.textContent = "Richiedi il tuo primo preventivo!";
				this.requestsBox.setAttribute("class", "invisible");
			}
			else{
				this.norequestsBox.setAttribute("class", "invisible");
				this.norequestsBox.textContent = "";
				this.requestsBox.setAttribute("class", "");
			}
		}
		
		/* This function, called by the callback function specified in the
		 * show function, uses the information retrieved from the server
		 * to populate the table with all the previous requests of quotation.
		 * Each quotation is associated to a dedicated row of the table.
		 */
		this.update = function(reqArray){
			var l = reqArray.length;
			var qId, pName, qDate, qPrice;
			if (l == 0) {
				this.emptyTable(true);
			} else {
				this.emptyTable(false);
				this.body.innerHTML = "";
				var self = this;
				reqArray.forEach(function(request) {
					row = document.createElement("tr");
					qId = document.createElement("td");
					qId.textContent = request.quotationId;
					row.appendChild(qId);
					pName = document.createElement("td");
					pName.textContent = request.productName;
					row.appendChild(pName);
					qDate = document.createElement("td");
					qDate.textContent = request.date;
					row.appendChild(qDate);
					qPrice = document.createElement("td");
					price = request.value;
					if(price==0){
						qPrice.textContent = "In attesa"
					}
					else qPrice.textContent = price + " €";
					row.appendChild(qPrice);
					row.addEventListener('click', (e) => {
						quotationDetails.showInv(request.quotationId);
					});	
					self.body.appendChild(row);
		      });
			}
		}
	}
	
	
	/* This function build the object QuotationDetails, that manages
	 * the box on the right side that shows the details of a
	 * selected report
	 */
	function QuotationDetails(_detailBox, _details){
		this.detailBox = _detailBox;
		this.details = _details;
		/* this variable store the id of the quotation that is
		 * currently shown. If the value is 0 no quotation is
		 * shown (the box is hidden)
		 */
		this.currentQuotation = 0;
		
		/* This is the main function, called once the user clicks
		 * on a row of the requests table. If the currently shown
		 * request is the one that the user selected the box is hidden
		 * and the currentQuotation parameter is set back to 0. Otherwise
		 * the box is populated with the details of the quotation
		 * that the user selected and the currentQuotation value
		 * is updated.
		 */
		this.showInv = function(quotationId){
			if(quotationId == this.currentQuotation){
				this.hide();
				this.currentQuotation = 0;
			}
			else{
				this.currentQuotation = quotationId;
				this.show(quotationId);
			}
		}
		
		/* This function show the quotation whose id is passed
		 * as parameter, retrieving the data from the server. If the
		 * server send back an error message it is passed to the
		 * orchestrator that shows it.
		 */
		this.show = function(quotationId, orchestrator){
			selfDet = this;
	        makeCall("GET", "GetQuotation?id=" + quotationId, null,
	          function(req, orchestrator) {
	            if (req.readyState == 4) {
	              var message = req.responseText;
	              if (req.status == 200) {
	                selfDet.update(JSON.parse(req.responseText)); 
	              } else {
	                pageOrchestrator.showError(message);
	              }
	            }
	          }
	        );
		}
		
		/* This function build the box, using the data retrieved
		 * from the server. It is called in the callback function defined in
		 * the function show().
		 */
		this.update = function(quotation){
			this.detailBox.setAttribute("class", "detailBox");
			this.details.productName.textContent = quotation.productName;
			this.details.productImage.setAttribute("src", "ProductImage?product=" + quotation.productId);
			this.details.date.textContent = quotation.date;
			this.details.selectedOptions.innerHTML = "";
			selfdet = this;
			quotation.options.forEach(function(option) {
				opt = document.createElement("span");
				opt.textContent = option.name;
				selfdet.details.selectedOptions.appendChild(opt);
				selfdet.details.selectedOptions.appendChild(document.createElement("br"));
			});
			if(quotation.value == undefined){
				this.details.price.textContent = "In attesa"
			}
			else this.details.price.textContent = quotation.value + " €";
		}
		
		/* This function hide the details box changing its class to "invisible".
		 * Notice that this function is called once the orchestrator object is
		 * initialized since, at the beginning of the interaction, the box with
		 * the details must be hidden (no autoclick, the user must manually
		 * select a request to visualize the details).
		 */
		this.hide = function(){
			this.detailBox.setAttribute("class", "invisible");
			return;
		}
		
		
	}
	
	
	/* This function create the object that manages the selection of the car and
	 * the forward of a new request of quotation.
	 */
	function RequestForm(_productsList, _optionsSelection){
		this.productsList = _productsList;
		this.optionSelection = _optionsSelection;
		/* This variable stores all the products retrieved from the server
		 * (only the textual data, the images are the only elements loaded
		 * dynamically). It is used to avoid a new request to the server once
		 * the user select a car, as required in the specification.
		 */
		this.products;
		
		/* Thif function send a request to the server to retrive
		 * all the products available. It is called by the orchestrator
		 * every time that an action from the user require a refresh
		 * of any part of the page.
		 */
		this.show = function(orchestrator){
			selfForm = this;
	        makeCall("GET", "GetAvailableProducts", null,
	          function(req) {
	            if (req.readyState == 4) {
	              var message = req.responseText;
	              if (req.status == 200) {
	            	selfForm.products = JSON.parse(req.responseText);
	                selfForm.updateProds(selfForm.products); 
	              } else {
	                orchestrator.setError(message);
	              }
	            }
	          }
	        );
		}
		
		/* This function creates the thumbnail of the products to
		 * be selected by the user. Notice that every time that a product
		 * is selected showDetails() for the relative product is
		 * called, in order to show the second part of the form and
		 * allow the user to select some options and send the
		 * request.
		 */
		this.updateProds = function(prodArray){
			console.log(prodArray.length);
			var qId, pName, qDate, qPrice;
			this.productsList.innerHTML = "";
			var self = this;
			prodArray.forEach(function(product) {
				// MAIN DIV
				pButton = document.createElement("div");
				// FORM RADIO INPUT
				carInput = document.createElement("input");
				carInput.setAttribute("type", "radio");
				carInput.setAttribute("name", "car");
				carInput.setAttribute("value", product.id);
				carInput.setAttribute("id", "p" + product.id);
				carInput.addEventListener('click', (e) => {
					self.showDetails(product.id);
				});	
				pButton.appendChild(carInput);
				// FORM LABEL
				carLabel = document.createElement("label");
				carLabel.setAttribute("for", "p" + product.id);
				carThumb = document.createElement("img");
				carThumb.setAttribute("src", "ProductImage?product=" + product.id + "&thumbnail=true");
				carThumb.setAttribute("width", "200");
				carLabel.appendChild(carThumb);
				pButton.appendChild(carLabel);
				pButton.appendChild(document.createElement("br"));
				//CAR NAME;
				carName = document.createElement("span");
				carName.textContent = product.name;
				pButton.appendChild(carName);
				self.productsList.appendChild(pButton);
				self.optionSelection.mainBox.setAttribute("class", "invisible");
	      });
		}
		
		/* This function show and update the second part of the form, to
		 * allow the user to see the options available for the selected product
		 * and send a quotation request.
		 */
		this.showDetails = function(id){
			selfDet = this;
			this.products.forEach(function(product){
				if(product.id==id){
					selfDet.optionSelection.prodId.setAttribute("value", product.id);
					selfDet.optionSelection.mainBox.setAttribute("class", "carSelection");
					selfDet.optionSelection.carName.textContent = product.name;
					selfDet.optionSelection.carImage.setAttribute("src", "ProductImage?product=" + id);
					selfDet.optionSelection.options.innerHTML = "";
					product.options.forEach(function(option){
						opt = document.createElement("div");
						input = document.createElement("input");
						input.setAttribute("type", "checkbox");
						input.setAttribute("id", option.id);
						input.setAttribute("name", "options");
						input.setAttribute("value", option.id);
						opt.appendChild(input);
						text = document.createElement("label");
						text.setAttribute("for", option.id);
						text.textContent = option.name;
						if(option.inOffer==true){
							text.textContent = text.textContent + " (OFFERTA SPECIALE!)"
						}
						opt.appendChild(text);
						selfDet.optionSelection.options.appendChild(opt);
					});
				}
			})
		}
		
		
		/* This function is called once when the page is loaded. It binds to the
		 * submit button a function that send the request to the server.
		 * Here is implemented the check of the validity of the request (at least
		 * 1 option selected), if the request is not compliant an error is shown
		 * and nothing is submitted to the server.
		 */
		this.registerEvents = function(orchestrator){
			this.optionSelection.submitButton.addEventListener('click', (e) => {
				var form = event.target.closest("form");
				var opts = form.querySelectorAll("input[type='checkbox']");
				var numSel = 0;
				opts.forEach(function(o){
					if(o.checked==true) numSel++;
				})
				/* Changing the numSel comparison it is possible to change
				 * the requirements. Not it is set to "at least one option selected"
				 */
				if(numSel < 1) orchestrator.showError("Seleziona almeno una opzione!");
				else{
					makeCall("POST", 'SubmitRequest', form,
					          function(req) {
					            if (req.readyState == 4) {
						          var message = req.responseText;
					              if (req.status == 200) {
					                orchestrator.refresh();
					                orchestrator.showSuccess("Richiesta inviata correttamente");
					              } else {
					                orchestrator.showError(message);
					              }
					            }
					          }
					        );
					
				}
			});
		}
		
	}
	
	/* Constructor of the orchestrator of the page
	 */
    function PageOrchestrator(){

		this.start = function(){
        	if(sessionStorage.getItem('role')=="client"){
					namefield = new NameField(sessionStorage.getItem('name'),
												document.getElementById('user_name')
											  );					
					errorBox = new ErrorBox(document.getElementById("errorbox"),
												document.getElementById("xbutton"),
												document.getElementById("errortext")
											);
					reqTable = new RequestTable(document.getElementById("requestsTable"),
													document.getElementById("requestsTableBody"),
													document.getElementById("requests"),
													document.getElementById("norequests")
												);
					carForm = new RequestForm(document.getElementById("imageslist"),
												{
													mainBox : document.getElementById("optionSelection"),
													carImage : document.getElementById("bigImage"),
													carName : document.getElementById("prodName"),
													options : document.getElementById("options"),
													prodId : document.getElementById("productId"),
													submitButton : document.getElementById("submitRequest")
												}
												);
					quotationDetails = new QuotationDetails(document.getElementById("detailBox"),
															{
																productName : document.getElementById("productName"),
																productImage : document.getElementById("productImage"),
																date : document.getElementById("date"),
																selectedOptions : document.getElementById("selectedOptions"),
																price : document.getElementById("price")
															})
					carForm.registerEvents(this);
					quotationDetails.hide();
        	}
        	else window.location.href = "index.html";
        };

        /* This function refreshes all the dynamic elements of the page. It is called
         * immediately after the orchestrator is built and then every time that an
         * action of the user require a refresh of an element of the page. 
         */
        this.refresh = function(){
        	errorBox.hide();
        	namefield.show();        	
        	reqTable.show(this);
        	carForm.show(this);
        };
        
        /* The following two functions have been implemented to allow all the components
         * of the page to show errors without directly calling other elements, passing through
         * the orchestrator. This allows to improve the flexibility, adding a new way of
         * notifying the error to the user impacts only these functions.
         * 
         */
        
        this.showError = function(message){
        	errorBox.setError(message);
        	window.scrollTo(0,0);
        }
        
        this.showSuccess = function(message){
        	errorBox.setSuccess(message);
        	window.scrollTo(0,0);
        }

    }

})();
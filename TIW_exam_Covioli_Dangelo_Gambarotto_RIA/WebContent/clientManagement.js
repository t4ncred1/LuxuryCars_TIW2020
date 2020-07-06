(function() {

    var errorBox, reqTable, carForm, quotationDetails, pageOrchestrator = new PageOrchestrator();

    window.addEventListener("load", () => {
        pageOrchestrator.start();
		pageOrchestrator.refresh();
    }, false);

   
	function NameField(_name, namecontainer){
		this.name = _name;
		this.show = function(){
			namecontainer.textContent = this.name;
		}
	}

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
				self.errorbox.setAttribute("class", "hidden");
				self.errormessage.textContent="";
			});	
		}
		
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
	
	function RequestTable(_table, _body, _requests, _norequests){
		this.table = _table;
		this.body = _body;
		this.requestsBox = _requests;
		this.norequestsBox = _norequests;
		
		this.show = function(orchestrator){
			selfTable = this;
	        makeCall("GET", "GetClientRequests", null,
	          function(req) {
	            if (req.readyState == 4) {
	              var message = req.responseText;
	              if (req.status == 200) {
	                selfTable.update(JSON.parse(req.responseText)); 
	              } else {
	                orchestrator.setError(message);
	              }
	            }
	          }
	        );
		}
		
		/* This function shows a message of alert in case the empty boolean is set
		 * true, hiding the table. Otherwise the message is removed and the table
		 * with the previously submitted requests is shown. 
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
	
	
	
	function QuotationDetails(_detailBox, _details){
		this.detailBox = _detailBox;
		this.details = _details;
		this.currentQuotation = 0;
		
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
		
		this.show = function(quotationId){
			console.log("Mostro la " + quotationId);
			return
		}
		
		this.update = function(quotation){
			
		}
		
		this.hide = function(){
			console.log("Nascondo il box");
			return;
		}
		
		
	}
	
	/*
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 */
	
	function RequestForm(_productsList, _optionsSelection){
		this.productsList = _productsList;
		this.optionSelection = _optionsSelection;
		this.products;
		
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
				carInput.setAttribute("id", product.id);
				carInput.addEventListener('click', (e) => {
					self.showDetails(product.id);
				});	
				pButton.appendChild(carInput);
				// FORM LABEL
				carLabel = document.createElement("label");
				carLabel.setAttribute("for", product.id);
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
		
		
		this.registerEvents = function(orchestrator){
			this.optionSelection.submitButton.addEventListener('click', (e) => {
				var form = event.target.closest("form");
				var opts = form.querySelectorAll("input[type='checkbox']");
				var numSel = 0;
				opts.forEach(function(o){
					if(o.checked==true) numSel++;
				})
				if(numSel < 1) orchestrator.showError("Seleziona almeno una opzione!");
				else{
					// makecall passando il form
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
        	}
        	else window.location.href = "index.html";
        };

        this.refresh = function(){
        	errorBox.hide();
        	namefield.show();        	
        	reqTable.show(this);
        	carForm.show(this);
        };
        
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
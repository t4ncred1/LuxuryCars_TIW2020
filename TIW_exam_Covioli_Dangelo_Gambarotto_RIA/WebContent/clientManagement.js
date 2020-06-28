(function() {

    var errorBox, reqTable, pageOrchestrator = new PageOrchestrator();

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
			self = this;
	        makeCall("GET", "GetClientRequests", null,
	          function(req) {
	            if (req.readyState == 4) {
	              var message = req.responseText;
	              if (req.status == 200) {
	                self.update(JSON.parse(req.responseText)); 
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
					self.body.appendChild(row);
		      });
			}
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
        	}
        	else window.location.href = "index.html";
        };

        this.refresh = function(){
        	errorBox.hide();
        	namefield.show();        	
        	reqTable.show(this);
        };
        
        this.showError = function(message){
        	this.errorBox.setError(message);
        }
        
        this.showSuccess = function(message){
        	this.errorBox.setSuccess(message);
        }

    }

})();
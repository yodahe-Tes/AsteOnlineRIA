{ 

    // page components
    let listaAsteApAcq, listaAsteCsAcq, datiArticolo, listaOfferteAcq, listaAsteAperteVnd, listaAsteChiuseVnd, formNuovaASta,
        detAstaAperta, listaOfferDetAst, detAstaCs;
    let controllore = new Controllore();


    let accesso = "checkAccesso";
    let CkyultimaAzione = "ultimaAzione";
    let CkyultimaAzioneCrea = "CkyultimaAzioneCrea";
    let CkyUltimeAsteViste = "UltimeAsteViste";


    window.addEventListener("load", () => {
        if (sessionStorage.getItem("username") == null) {
            window.location.href = "index.html";
        } else {
            controllore.inizializza(); 
            controllore.mostra();
        } 
    }, false);

// Constructors of view components

    
    function PersonalMessage(_username, messagecontainer) {
        this.username = _username;
        this.show = function() {
            messagecontainer.textContent = this.username;
        }
    }
    // Component that handles the form to search Articoli in asta

    function FormRiCerca(formRicercaAsta, alert) {
        this.alert = alert;
        this.formRicercaAsta = formRicercaAsta;
        this.registerClick = function() {

            // Obtain button and set click listener
            this.formRicercaAsta.querySelector("input[type='button']").addEventListener('click',
                (e) => {
                    // Obtain form from event
                    var form = e.target.closest("form");

                    
                    if (form.checkValidity()) {
                        var self = this;
                        makeCall("POST", "CercaAsta", form,
                            function(req) {

                                if (req.readyState == XMLHttpRequest.DONE) {
                                    var message = req.responseText;
                                    if (req.status == 200) {
                                        var asteDaMostrare = JSON.parse(message);
                                        if (asteDaMostrare.length == 0) {
                                            self.alert.textContent = "Non ci sono aste con questa parola di ricerca";
                                        } 
                                        var nomeUtente = document.getElementById("id_username").innerText;
                                        scriviCookie(CkyUltimeAsteViste + nomeUtente, req.responseText);

                                        listaAsteApAcq.update(asteDaMostrare);
                                        document.getElementById("contenitoreVendo").style.display = "none";
                                        document.getElementById("contenitoreAcquisto").style.display = "block";
                                        document.getElementById("contenitoreOfferta").style.display = "none";
                                        scriviCookie(CkyultimaAzione + nomeUtente, document.getElementById("iDFormRicerca").getAttribute("id"));


                                    } else {
                                         self.alert.textContent = message;
                                          
                                    }
                                }
                            }
                        );
                    } else {
                        
                        form.reportValidity();
                    }
                });
        }

    }

    function ListaAsteApAcq(alert, dettAstAprt, dettAstAprtBody) {
        this.alert = alert;
        this.dettAstAprt = dettAstAprt;
        this.dettAstAprtBody = dettAstAprtBody;
       

        this.update = function(listaAste) {
            var row, codiceCell, nomeCell, scadenzaCell,linkcell, anchor;
            this.dettAstAprtBody.innerHTML = ""; 
           
            var self = this;
            listaAste.forEach(function(asta) { 
                row = document.createElement("tr");
                codiceCell = document.createElement("td");
                codiceCell.textContent = asta.codice;
                row.appendChild(codiceCell);
                nomeCell = document.createElement("td");
                nomeCell.textContent = asta.nome;
                row.appendChild(nomeCell);
                scadenzaCell = document.createElement("td");
                scadenzaCell.textContent = asta.scadenza;
                row.appendChild(scadenzaCell);

                linkcell = document.createElement("td");
                anchor = document.createElement("a");
                linkcell.appendChild(anchor);
                linkText = document.createTextNode("Lista offerte");
                anchor.appendChild(linkText);

                anchor.setAttribute('idAsta', asta.idAsta);
                anchor.addEventListener("click", (e) => {

                    datiArticolo.show(e.target.getAttribute("idAsta"));  
                    listaOfferteAcq.show(e.target.getAttribute("idAsta"));  
                    datiArticolo.registerEvents();  
                    document.getElementById("contenitoreVendo").style.display = "none";
                    document.getElementById("contenitoreOfferta").style.display = "block";
                    document.getElementById("contenitoreAcquisto").style.display = "none";

                }, false);
                anchor.href = "#";
                row.appendChild(linkcell);
                self.dettAstAprtBody.appendChild(row);


            });
            this.dettAstAprt.style.visibility = "visible";
            var nomeUtente = document.getElementById("id_username").innerText;
            scriviCookie(CkyUltimeAsteViste + nomeUtente, JSON.stringify(listaAste));
            scriviCookie(CkyultimaAzione + nomeUtente, document.getElementById("iDdettAstAprt").getAttribute("id"));

        }

    }



    function ListaAsteCsAcq(alert, dettAstCs, dettAstCsBody) {
        this.alert = alert;
        this.dettAstCs = dettAstCs;
        this.dettAstCsBody = dettAstCsBody;

       

        this.show = function() {
            var self = this;
            makeCall("GET", "GetAcquisto", null,
                function(req) {
                    if (req.readyState == 4) {
                        var message = req.responseText;
                        if (req.status == 200) {
                            var asteDaMostrare = JSON.parse(req.responseText);
                            if (asteDaMostrare.length == 0) {
                                self.alert.textContent = "Non hai aste aggiudicate";
                                return;
                            }
                            self.update(asteDaMostrare); 
                            scriviCookie(CkyultimaAzione + nomeUtente, document.getElementById("iDdettAstCsAcq").getAttribute("id"));

                        } else if (req.status == 403) {
                            window.location.href = req.getResponseHeader("Location");
                            window.sessionStorage.removeItem('username');
                        } else {
                            self.alert.textContent = message;
                        }
                    }
                }
            );
        };


        this.update = function(listaAste) {
            var row, codiceCell, nomeCell, descrizioneCell, prezzoAggCell;
            this.dettAstCsBody.innerHTML = ""; 
            var self = this;
            listaAste.forEach(function(asta) { 
                row = document.createElement("tr");
                codiceCell = document.createElement("td");
                codiceCell.textContent = asta.codice;
                row.appendChild(codiceCell);
                nomeCell = document.createElement("td");
                nomeCell.textContent = asta.nome;
                row.appendChild(nomeCell);
                descrizioneCell = document.createElement("td");
                descrizioneCell.textContent = asta.descrizione;
                row.appendChild(descrizioneCell);
                prezzoAggCell = document.createElement("td");
                prezzoAggCell.textContent = new Intl.NumberFormat('it-IT', {
                    style: 'currency',
                    currency: 'EUR'
                }).format(asta.prezzoAggiudica);
                row.appendChild(prezzoAggCell);


                self.dettAstCsBody.appendChild(row);
            });
            this.dettAstCs.style.visibility = "visible";

        }

    }


    function DatiArticolo(dati) {
        this.alert = dati['alert']
        this.codice = dati['codice'];
        this.nome = dati['nome'];
        this.image = dati['image'];
        this.prezzoIniziale = dati['prezzoIniziale'];
        this.rialzoMinimo = dati['rialzoMinimo'];
        this.scadenza = dati['scadenza'];
        this.idAsta = dati['idAsta'];
        this.dettArt = dati['dettArt'];
        this.dettArtBody = dati['dettArtBody'];
        this.formOfferta = dati['formOfferta'];



        this.show = function(idAsta) {
            var self = this;
            this.idAsta = idAsta;
            makeCall("GET", "MostraDatiArticolo?idAsta=" + idAsta, null,
                function(req) {
                    if (req.readyState == 4) {
                        var message = req.responseText;
                        if (req.status == 200) {
                            var asta = JSON.parse(req.responseText);
                            self.update(asta); 
                            self.dettArt.style.visibility = "visible";
                            self.formOfferta.style.visibility = "visible";
                            scriviCookie(CkyultimaAzione + nomeUtente, document.getElementById("iDdettArt").getAttribute("id"));

                            self.formOfferta.idAsta = self.idAsta;
                        } else if (req.status == 403) {
                            window.location.href = req.getResponseHeader("Location");
                            window.sessionStorage.removeItem('username');
                        } else {
                            self.alert.textContent = message;

                        }
                    }
                }
            );
        };
        this.registerEvents = function() {
            this.formOfferta.querySelector("button").addEventListener('click', (event) => {
                var form = event.target.closest("form");
                if (form.checkValidity()) {
                    var self = this,
                        idAstaPerOfferta = this.idAsta;

                    form.getElementsByTagName("input")[1].value = this.idAsta;
                    var ammontare = form.getElementsByTagName("input")[0].value;
                    makeCall("POST", "FaiOfferta?idAstaArticolo=" + this.idAsta + "&ammontareOffertaArticolo=" + ammontare, form,
                        function(req) {
                            if (req.readyState == 4) {
                                var message = req.responseText;
                                if (req.status == 200) {
                                    listaOfferteAcq.show(idAstaPerOfferta);
                                    scriviCookie(CkyultimaAzione + nomeUtente, document.getElementById("iDfaiOffertaFormArticolo").getAttribute("id"));
                                    document.getElementById("contenitoreVendo").style.display = "none";
                                    document.getElementById("contenitoreOfferta").style.display = "block";
                                    document.getElementById("contenitoreAcquisto").style.display = "none";
                                    document.getElementById("ContenitoredetAstaChiusa").style.display = "none";
                                    document.getElementById("contenitoreDetAstaAperta").style.display = "none";


                                } else if (req.status == 403) {
                                    window.location.href = req.getResponseHeader("Location");
                                    window.sessionStorage.removeItem('username');
                                } else {
                                    self.alert.textContent = message;
                                }
                            }
                        }
                    );
                } else {
                    form.reportValidity();
                }
            });
        }


        this.update = function(detAst) {
            this.codice.textContent = detAst.codice;
            this.nome.textContent = detAst.nome;
            this.prezzoIniziale.textContent = new Intl.NumberFormat('it-IT', {
                style: 'currency',
                currency: 'EUR'
            }).format(detAst.prezzoIniziale);
            this.rialzoMinimo.textContent = new Intl.NumberFormat('it-IT', {
                style: 'currency',
                currency: 'EUR'
            }).format(detAst.rialzoMinimo);
            this.scadenza.textContent = detAst.scadenza;
            this.image.src = "data:image/jpeg;base64," + detAst.image;

        }
    }


    function ListaOfferteAcq(alert, offerte, offerteBody) {
        this.alert = alert;
        this.offerte = offerte;
        this.offerteBody = offerteBody;


        this.show = function(idAsta) {
            var self = this;
            makeCall("GET", "GetOfferta?idAsta=" + idAsta, null,
                function(req) {
                    if (req.readyState == 4) {
                        var message = req.responseText;
                        if (req.status == 200) {
                            var offerteteDaMostrare = JSON.parse(req.responseText);
                            self.alert.textContent = "";
                            if (offerteteDaMostrare.length == 0) {
                                self.alert.textContent = "non ci sono offerte da vedere!";
                                return;
                            }
                            self.update(offerteteDaMostrare); 
                            scriviCookie(CkyultimaAzione + nomeUtente, document.getElementById("iDofferte").getAttribute("id"));

                        } else if (req.status == 403) {
                            window.location.href = req.getResponseHeader("Location");
                            window.sessionStorage.removeItem('username');
                        } else {
                            self.alert.textContent = message;
                        }
                    }
                }
            );
        };

        this.update = function(offert) {
            var row, idOfferentCell, ammontareOffertCell, dataOraOfCell;
            this.offerteBody.innerHTML = ""; // empty the table body
            // build updated list
            var self = this;
            offert.forEach(function(offertaD) { 
                row = document.createElement("tr");
                idOfferentCell = document.createElement("td");
                idOfferentCell.textContent = offertaD.idOfferente;
                row.appendChild(idOfferentCell);
                ammontareOffertCell = document.createElement("td");
                ammontareOffertCell.textContent = new Intl.NumberFormat('it-IT', {
                    style: 'currency',
                    currency: 'EUR'
                }).format(offertaD.ammontareOfferta);
                row.appendChild(ammontareOffertCell);
                dataOraOfCell = document.createElement("td");
                dataOraOfCell.textContent = offertaD.dataOraofferta;
                row.appendChild(dataOraOfCell);

                self.offerteBody.appendChild(row);
            });
            this.offerte.style.visibility = "visible";

        }

    }

    function ListaAsteAperteVnd(alert, dettAstArtcliAprt, dettAstArtcliAprtBody) {
        this.alert = alert;
        this.dettAstArtcliAprt = dettAstArtcliAprt;
        this.dettAstArtcliAprtBody = dettAstArtcliAprtBody;


        this.show = function() {
            var self = this;
            makeCall("GET", "GetAsteAperteVendo", null,
                function(req) {
                    if (req.readyState == 4) {
                        var message = req.responseText;
                        if (req.status == 200) {
                            var asteDaMostrare = JSON.parse(req.responseText);
                            if (asteDaMostrare.length == 0) {
                                self.alert.textContent = "Non hai aste in vendita";
                                return;
                            }
                            self.update(asteDaMostrare); 
                            document.getElementById("contenitoreVendo").style.display = "block";
                            document.getElementById("contenitoreAcquisto").style.display = "none";
                            document.getElementById("contenitoreOfferta").style.display = "none";
                            document.getElementById("ContenitoredetAstaChiusa").style.display = "none";
                            document.getElementById("contenitoreDetAstaAperta").style.display = "none";

                        } else if (req.status == 403) {
                            window.location.href = req.getResponseHeader("Location");
                            window.sessionStorage.removeItem('username');
                        } else {
                            self.alert.textContent = message;
                        }
                    }
                }
            );
        };


        this.update = function(asta) {
            var row, codiceCell, nomeCell, offertaMaxCell, tempoMancanteCell, linkcell, linkText,anchor;
            this.dettAstArtcliAprtBody.innerHTML = ""; 
            var self = this;
            asta.forEach(function(asta) { 
                row = document.createElement("tr");
                codiceCell = document.createElement("td");
                codiceCell.textContent = asta.codice;
                row.appendChild(codiceCell);
                nomeCell = document.createElement("td");
                nomeCell.textContent = asta.nome;
                row.appendChild(nomeCell);
                offertaMaxCell = document.createElement("td");
                offertaMaxCell.textContent = new Intl.NumberFormat('it-IT', {
                    style: 'currency',
                    currency: 'EUR'
                }).format(asta.prezzoAggiudica);
                row.appendChild(offertaMaxCell);
                tempoMancanteCell = document.createElement("td");
                tempoMancanteCell.textContent = asta.tempoMancante;
                row.appendChild(tempoMancanteCell);
                linkcell = document.createElement("td");
                anchor = document.createElement("a");
                linkcell.appendChild(anchor);
                linkText = document.createTextNode("Dettaglio Asta");
                anchor.appendChild(linkText);
                anchor.setAttribute('idAsta', asta.idAsta); 
                anchor.addEventListener("click", (e) => {
                    detAstaAperta.show(asta.idAsta); 
                    listaOfferDetAst.show(asta.idAsta); 
                    detAstaAperta.registerEvents();
                    document.getElementById("contenitoreAcquisto").style.display = "none";
                    document.getElementById("contenitoreOfferta").style.display = "none";
                    document.getElementById("ContenitoredetAstaChiusa").style.display = "none";
                    document.getElementById("contenitoreDetAstaAperta").style.display = "block";
                    document.getElementById("contenitoreVendo").style.display = "none";
                }, false);
                anchor.href = "#";
                row.appendChild(linkcell);


                self.dettAstArtcliAprtBody.appendChild(row);
            });
            this.dettAstArtcliAprt.style.visibility = "visible";
            scriviCookie(CkyultimaAzione + nomeUtente, document.getElementById("iDdettAstArtcliAprt").getAttribute("id"));

        }

    }


    function ListaAsteChiuseVnd(alert, dettAstArtcliCs, dettAstArtcliCsBody) {
        this.alert = alert;
        this.dettAstArtcliCs = dettAstArtcliCs;
        this.dettAstArtcliCsBody = dettAstArtcliCsBody;

     

        this.show = function() {
            var self = this;
            makeCall("GET", "GetAsteChiuseVendo", null,
                function(req) {
                    if (req.readyState == 4) {
                        var message = req.responseText;
                        if (req.status == 200) {
                            var asteDaMostrare = JSON.parse(req.responseText);
                            if (asteDaMostrare.length == 0) {
                                self.alert.textContent = "Non ci sono aste chiuse";
                                return;
                            }
                            self.update(asteDaMostrare); 

                        } else if (req.status == 403) {
                            window.location.href = req.getResponseHeader("Location");
                            window.sessionStorage.removeItem('username');
                        } else {
                            self.alert.textContent = message;
                        }
                    }
                }
            );
        };


        this.update = function(asta) {
            var row, codiceCell, nomeCell, prezzoAggCell, linkcell, linkText, anchor;
            this.dettAstArtcliCsBody.innerHTML = "";
            
            var self = this;
            asta.forEach(function(asta) { 
                row = document.createElement("tr");
                codiceCell = document.createElement("td");
                codiceCell.textContent = asta.codice;
                row.appendChild(codiceCell);
                nomeCell = document.createElement("td");
                nomeCell.textContent = asta.nome;
                row.appendChild(nomeCell);
                prezzoAggCell = document.createElement("td");
                prezzoAggCell.textContent = new Intl.NumberFormat('it-IT', {
                    style: 'currency',
                    currency: 'EUR'
                }).format(asta.prezzoAggiudica);
                row.appendChild(prezzoAggCell);
                linkcell = document.createElement("td");
                anchor = document.createElement("a");
                linkcell.appendChild(anchor);
                linkText = document.createTextNode("Dettaglio Asta");
                anchor.appendChild(linkText);
                anchor.setAttribute('idAsta', asta.idAsta); 
                anchor.addEventListener("click", (e) => {
                    detAstaCs.show(asta.idAsta); 
                    document.getElementById("contenitoreVendo").style.display = "none";
                    document.getElementById("contenitoreOfferta").style.display = "none";
                    document.getElementById("contenitoreDetAstaAperta").style.display = "none";
                    document.getElementById("ContenitoredetAstaChiusa").style.display = "block";
                    document.getElementById("contenitoreAcquisto").style.display = "none";
                }, false);
                anchor.href = "#";
                row.appendChild(linkcell);

                self.dettAstArtcliCsBody.appendChild(row);
            });
            this.dettAstArtcliCs.style.visibility = "visible";
            scriviCookie(CkyultimaAzione + nomeUtente, document.getElementById("dettAstArtcliCs").getAttribute("id"));

        }

    }


    function FormNuovaASta(formPerNuovaASta) {
        this.formPerNuovaASta = formPerNuovaASta;
        this.registerClick = function() {

            this.formPerNuovaASta.querySelector("input[type='button'].submit").addEventListener('click',
                (e) => {
                    var form = e.target.closest("form");

                    if (form.checkValidity()) {
                        var self = this;
                        makeCall("POST", "NuovaAsta", form,
                            function(req) {

                                if (req.readyState == XMLHttpRequest.DONE) {
                                    var message = req.responseText;
                                    if (req.status == 200) {
                                        listaAsteAperteVnd.show();
                                        var nomeUtente = document.getElementById("id_username").innerText;
           								 scriviCookie(CkyultimaAzione + nomeUtente, document.getElementById("formCreazioneAsta").getAttribute("id"));

                                    } else {
                                        if (message == "")
                                            message = "An issue has occurred";
                                        alert(message); 
                                    }
                                }
                            }
                        );
                    } else {
                        
                        form.reportValidity();
                    }
                });
            scriviCookie(CkyultimaAzione + nomeUtente, document.getElementById("formCreazioneAsta").getAttribute("id"));
        }
    }

    function DetAstaAperta(asta) {
        this.alert = asta['alert']
        this.codice = asta['codice'];
        this.nome = asta['nome'];
        this.descrizione = asta['descrizione'];
        this.prezzoIniziale = asta['prezzoIniziale'];
        this.rialzoMinimo = asta['rialzoMinimo'];
        this.scadenza = asta['scadenza'];
        this.image = asta['image'];
        this.idAsta = asta['idAsta'];
        this.detAstaApr = asta['detAstaApr'];
        this.detAstaAprBody = asta['detAstaAprBody'];
        this.botChiudi = asta['botChiudi'];

        this.show = function(idAsta) {
            var self = this;
            this.idAsta = idAsta;
            makeCall("GET", "GetDettaglioAstaAp?idAsta=" + idAsta, null,
                function(req) {
                    if (req.readyState == 4) {
                        var message = req.responseText;
                        if (req.status == 200) {
                            var asta = JSON.parse(req.responseText);
                            self.update(asta); 
                            self.detAstaApr.style.visibility = "visible";
                            //scriviCookie(CkyultimaAzione + nomeUtente, document.getElementById("iDdetAsta").getAttribute("id"));

                        } else if (req.status == 403) {
                            window.location.href = req.getResponseHeader("Location");
                            window.sessionStorage.removeItem('username');
                        } else {
                            self.alert.textContent = message;

                        }
                    }
                }
            );
        };

        this.registerEvents = function() {
            this.botChiudi.querySelector("input[type='button']").addEventListener('click', (event) => {
                var form = event.target.closest("form");
                if (form.checkValidity()) {
                    var self = this,
                        idAstaDaChiudere = this.idAsta;

                    form.getElementsByTagName("input")[0].value = this.idAsta;

                    makeCall("POST", "ChiudiAsta?idAstaDaChiudere=" + idAstaDaChiudere, form,
                        function(req) {
                            if (req.readyState == 4) {
                                var message = req.responseText;
                                if (req.status == 200) {
                                    detAstaCs.show(idAstaDaChiudere);
                                    document.getElementById("contenitoreVendo").style.display = "none";
                                    document.getElementById("contenitoreOfferta").style.display = "none";
                                    document.getElementById("contenitoreDetAstaAperta").style.display = "none";
                                    document.getElementById("ContenitoredetAstaChiusa").style.display = "block";
                                    document.getElementById("contenitoreAcquisto").style.display = "none";
                                    scriviCookie(CkyultimaAzione + nomeUtente, document.getElementById("iDChiudiAsta").getAttribute("id"));

                                } else if (req.status == 403) {
                                    window.location.href = req.getResponseHeader("Location");
                                    window.sessionStorage.removeItem('username');
                                } else {
                                    self.alert.textContent = message;
                                }
                            }
                        }
                    );
                } else {
                    form.reportValidity();
                }
            });
        }

        this.update = function(detAst) {

            this.codice.textContent = detAst.codice;
            this.nome.textContent = detAst.nome;
            this.descrizione.textContent = detAst.descrizione;
            this.prezzoIniziale.textContent = new Intl.NumberFormat('it-IT', {
                style: 'currency',
                currency: 'EUR'
            }).format(detAst.prezzoIniziale);
            this.rialzoMinimo.textContent = new Intl.NumberFormat('it-IT', {
                style: 'currency',
                currency: 'EUR'
            }).format(detAst.rialzoMinimo);
            this.scadenza.textContent = detAst.scadenza;
            this.image.src = "data:image/jpeg;base64," + detAst.image;


        }

    }


    function ListaOfferDetAst(alert, offerte, offerteBody) {
        this.alert = alert;
        this.offerte = offerte;
        this.offerteBody = offerteBody;

        this.show = function(idAsta) {
            var self = this;

            makeCall("GET", "GetOfferta?idAsta=" + idAsta, null,
                function(req) {
                    if (req.readyState == 4) {
                        var message = req.responseText;
                        if (req.status == 200) {
                            var offerteDaMostrare = JSON.parse(req.responseText);
                            if (offerteDaMostrare.length == 0) {
                                self.alert.textContent = "Non ci sono ancora offerte";
                                return;
                            }
                            self.update(offerteDaMostrare); 
                            scriviCookie(CkyultimaAzione + nomeUtente, document.getElementById("iDofferteDt").getAttribute("id"));

                        } else if (req.status == 403) {
                            window.location.href = req.getResponseHeader("Location");
                            window.sessionStorage.removeItem('username');
                        } else {
                            self.alert.textContent = message;
                        }
                    }
                }
            );
        };


        this.update = function(offer) {
            var row, nomeOfferCell, PrezzoOffCell, DataOffCell;
            this.offerteBody.innerHTML = ""; 
           
            var self = this;
            offer.forEach(function(offer) { 
                row = document.createElement("tr");
                nomeOfferCell = document.createElement("td");
                nomeOfferCell.textContent = offer.nomeOfferente;
                row.appendChild(nomeOfferCell);
                PrezzoOffCell = document.createElement("td");
                PrezzoOffCell.textContent = new Intl.NumberFormat('it-IT', {
                    style: 'currency',
                    currency: 'EUR'
                }).format(offer.ammontareOfferta);
                row.appendChild(PrezzoOffCell);
                DataOffCell = document.createElement("td");
                DataOffCell.textContent = offer.dataOraofferta;
                row.appendChild(DataOffCell);


                self.offerteBody.appendChild(row);
            });
            this.offerte.style.visibility = "visible";

        }

    }


    function DetAstaCs(asta) {
        this.codice = asta['codice'];
        this.nome = asta['nome'];
        this.prezzoAggiudica = asta['prezzoAggiudica'];
        this.image = asta['image'];
        this.name = asta['name'];
        this.indirizzo = asta['indirizzo'];
        this.detAstaCs = asta['detAstaCs'];

        this.show = function(idAsta) {
            var self = this;
            makeCall("GET", "GetDettaglioAstaCs?idAsta=" + idAsta, null,
                function(req) {
                    if (req.readyState == 4) {
                        var message = req.responseText;
                        if (req.status == 200) {
                            var asta = JSON.parse(req.responseText);
                            self.update(asta.firstObj, asta.secondObj); 
                            self.detAstaCs.style.visibility = "visible";
                            scriviCookie(CkyultimaAzione + nomeUtente, document.getElementById("iDdetAstaCs").getAttribute("id"));

                        } else if (req.status == 403) {
                            window.location.href = req.getResponseHeader("Location");
                            window.sessionStorage.removeItem('username');
                        } else {
                            self.alert.textContent = message;

                        }
                    }
                }
            );
        };

        this.update = function(detAst, vincAst) {
            this.codice.textContent = detAst.codice;
            this.nome.textContent = detAst.nome;
            this.prezzoAggiudica.textContent = new Intl.NumberFormat('it-IT', {
                style: 'currency',
                currency: 'EUR'
            }).format(detAst.prezzoAggiudica);
            this.image.src = "data:image/jpeg;base64," + detAst.image;
            this.name.textContent = vincAst.name;
            this.indirizzo.textContent = vincAst.indirizzo;
			scriviCookie(CkyultimaAzione + nomeUtente, document.getElementById("iDdetAstaCs").getAttribute("id"));

        }
    }




     //Main Controller

    function Controllore() {
        var alertContainer = document.getElementById("id_alert");



        this.inizializza = function() {

            personalMessage = new PersonalMessage(sessionStorage.getItem('username'),
            document.getElementById("id_username"));
            personalMessage.show();

			formRiCerca = new FormRiCerca(document.getElementById("iDFormRicerca"), alertContainer);
			
            listaAsteApAcq = new ListaAsteApAcq(
                alertContainer,
                document.getElementById("iDdettAstAprt"),
                document.getElementById("iDdettAstAprtbody"));

  

            listaAsteCsAcq = new ListaAsteCsAcq(
                alertContainer,
                document.getElementById("iDdettAstCsAcq"),
                document.getElementById("iDdettAstCsAcqbody"));


            datiArticolo = new DatiArticolo({ 
                alert: alertContainer,
                codice: document.getElementById("codiceArticolo"),
                nome: document.getElementById("nomeArticolo"),
                image: document.getElementById("imageArticolo"),
                prezzoIniziale: document.getElementById("prezzoInizialeArticolo"),
                rialzoMinimo: document.getElementById("rialzoMinimoArticolo"),
                scadenza: document.getElementById("scadenzaArticolo"),
                formOfferta: document.getElementById("iDfaiOffertaFormArticolo"),
                ammOfferta: document.getElementById("ammontareOffertaArticolo"),
                idAsta: document.getElementById("idAstaArticolo"),
                dettArt: document.getElementById("iDdettArt"),
                dettArtBody: document.getElementById("iDdettArtBody")
            });


            listaOfferteAcq = new ListaOfferteAcq(
                alertContainer,
                document.getElementById("iDofferte"),
                document.getElementById("iDofferteBody"));


            listaAsteAperteVnd = new ListaAsteAperteVnd(alertContainer,
                document.getElementById("iDdettAstArtcliAprt"),
                document.getElementById("iDdettAstArtcliAprtBody"));

            listaAsteChiuseVnd = new ListaAsteChiuseVnd(alertContainer,
                document.getElementById("dettAstArtcliCs"),
                document.getElementById("dettAstArtcliCsBody"));

            formNuovaASta = new FormNuovaASta(document.getElementById("formCreazioneAsta"), alertContainer);

            detAstaAperta = new DetAstaAperta({
                alert: alertContainer,
                codice: document.getElementById("codiceDetAstAp"),
                nome: document.getElementById("nomeDetAstAp"),
                descrizione: document.getElementById("descrizioneDetAstAp"),
                prezzoIniziale: document.getElementById("prezzoInizialeDetAstAp"),
                rialzoMinimo: document.getElementById("rialzoMinimoDetAstAp"),
                scadenza: document.getElementById("scadenzaDetAstAp"),
                image: document.getElementById("imageDetAstAp"),
                idAsta: document.getElementById("idAstaDetAstAp"),
                detAstaApr: document.getElementById("iDdetAsta"),
                detAstaAprBody: document.getElementById("iDdetAstaBody"),
                botChiudi: document.getElementById("iDChiudiAsta")
            });

            listaOfferDetAst = new ListaOfferDetAst(
                alertContainer,
                document.getElementById("iDofferteDt"),
                document.getElementById("iDofferteDtBody"));


            detAstaCs = new DetAstaCs({
                alert: alertContainer,
                codice: document.getElementById("codiceDetAstCs"),
                nome: document.getElementById("nomeDetAstCs"),
                prezzoAggiudica: document.getElementById("prezzoAggiudicaDetAstCs"),
                name: document.getElementById("vincitoreAstaNomeDetAstCs"),
                indirizzo: document.getElementById("vincitoreAstaIndirizzoDetAstCs"),
                image: document.getElementById("imageDetAstCs"),
                idAsta: document.getElementById("idAstaDetAstCs"),
                detAstaCs: document.getElementById("iDdetAstaCs"),
                detAstaCsBody: document.getElementById("iDdetAstaCsBody")
            });


            document.getElementById("idVaiAdAcquisto").addEventListener("click", () => {
                //var nomeUtente = document.getElementById("id_username").innerText;
                //cancellaCookie(CkyultimaAzioneCrea + nomeUtente);
                window.OpenAcquisto()
            });

            document.getElementById("idOpenVendo").addEventListener("click", () => {
               // var nomeUtente = document.getElementById("id_username").innerText;

                //cancellaCookie(CkyultimaAzioneCrea + nomeUtente);
                window.OpenVendo()
            });

            document.querySelector("a[href='Logout']").addEventListener('click', () => {
                window.sessionStorage.removeItem('username');
            })
        };

        this.mostra = function() {
	
			let primoAccesso = false;
            var nomeUtente = document.getElementById("id_username").innerText;
            letto = leggiCookie(accesso + nomeUtente);
            if (letto == '') {
                primoAccesso = true;
                scriviCookie(accesso + nomeUtente, document.getElementById("id_username").innerText);
            } else {
                ultimaAzione = leggiCookie(CkyultimaAzione + nomeUtente);
                if (ultimaAzione != 'formCreazioneAsta') {
                    letto = leggiCookie(CkyUltimeAsteViste + nomeUtente);
                    ultimeAsteViste = '';
                    if (letto != '') {

                        letto = leggiCookie(CkyUltimeAsteViste + nomeUtente)

                    }
				
				}
			}
                

            if (primoAccesso) {
                OpenAcquisto();
            } else {
                if (ultimaAzione == "formCreazioneAsta") {
                    OpenVendo();
                } else {
                    if (letto != ''){
                     
                        OpenAcquistoUpdate(letto);
					}else{
						OpenAcquisto();
					}
                }
            }


        };
    }


    function OpenVendo() {
        document.getElementById("id_alert").textContent = "";
        document.getElementById("contenitoreVendo").style.display = "block";
        document.getElementById("contenitoreAcquisto").style.display = "none";
        document.getElementById("contenitoreOfferta").style.display = "none";
        document.getElementById("contenitoreDetAstaAperta").style.display = "none";
        document.getElementById("ContenitoredetAstaChiusa").style.display = "none";
        listaAsteAperteVnd.show();
        listaAsteChiuseVnd.show();
        formNuovaASta.registerClick();
    }


    function OpenAcquisto() {
        document.getElementById("id_alert").textContent = "";
        document.getElementById("contenitoreAcquisto").style.display = "block";
        document.getElementById("contenitoreOfferta").style.display = "none";
        document.getElementById("contenitoreVendo").style.display = "none";
        document.getElementById("contenitoreDetAstaAperta").style.display = "none";
        document.getElementById("ContenitoredetAstaChiusa").style.display = "none";
        listaAsteCsAcq.show();
        formRiCerca.registerClick();
    }

    function OpenAcquistoUpdate(asteVisitate) {
        document.getElementById("contenitoreOfferta").style.display = "none";
        document.getElementById("contenitoreVendo").style.display = "none";
        document.getElementById("contenitoreDetAstaAperta").style.display = "none";
        document.getElementById("ContenitoredetAstaChiusa").style.display = "none";
        var aste = JSON.parse(asteVisitate);
        listaAsteApAcq.update(aste)
        listaAsteCsAcq.show();
        formRiCerca.registerClick();
    }
    

    // Funzione  per lettura dei cookie
    function leggiCookie(nomeCookie) {
        if (document.cookie.length > 0) {
            var inizio = document.cookie.indexOf(nomeCookie + "=");
            if (inizio != -1) {
                inizio = inizio + nomeCookie.length + 1;
                var fine = document.cookie.indexOf(";", inizio);
                if (fine == -1) fine = document.cookie.length;
                return unescape(document.cookie.substring(inizio, fine));
            } else {
                return "";
            }
        }
        return "";
    }

    // Funzione per scrivere cookie
    function scriviCookie(nomeCookie, valoreCookie) {
        var scadenza = new Date();
        var adesso = new Date();
        scadenza.setTime(adesso.getTime() + (30 * 24 * 60 * 60 * 1000));
        document.cookie = nomeCookie + '=' + escape(valoreCookie) + '; expires=' +
            scadenza.toGMTString() + '; path=/';
    }

    // Funzione che serve per cancellare cookie settando la sua durata ad un valore minore di zero. 
    
    function cancellaCookie(nomeCookie) {
        scriviCookie(nomeCookie, '', -1);
    }
};

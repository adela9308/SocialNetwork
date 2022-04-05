# _SocialNetwork - Mini-rețea de socializare_

SocialNetwork este un proiect care reprezintă un model simplificat al unei rețele de socializare, 
însa din care pot fi extrase informații relevante. Conturi de utilizatori, cereri de prietenie, 
accept, reject, chat, mesaje de grup, you have it all! 

## Funcționalități de bază

- Autentificare utilizator (logare/ delogare)
- Trimitere/Retragere, respectiv Acceptare/Refuzare cerere de prietenie 
- Afișarea cererilor de prietenie, împreuna cu statusurile lor
- Ștergerea unui prieten
- Adăugare/Ștergere eveniment
- Participare/Încetare participare la eveniment
- Generare de rapoarte (istoric evenimente/ istoric mesaje dintr-o perioadă calendaristică) - în format .pdf
- Simularea de trimitere de mesaje (trimitere mesaj + reply la un mesaj anterior)

## Altele
- Folosirea șablonului Observer
- Repository paginat (datele sunt afișate paginat)
- Citire date din baza de date (PostgreSQL)
- Parole hash-uite în baza de date

## Tehnologii

- Limbaj de programare: Java 
- GUI: JavaFX
- Bază de date: PostgreSQL

##Rulare
Înainte de a porni aplicația este necesar să se creeze baza de date în PostgreSQL (cu numele 
"SocialNetwork"), după care se va acționa butonul de Run din proiect. Fiecare câmp al fiecărui tabel 
se regăseste în fișierele de Repository (src\main\java\socialnetwork\repository\database) în funcțiile
de "insert" sau "extractEntity".

## DEMO al aplicației

La deschiderea aplicației se deschide ecranul de Login/Register, unde utilizatorul se poate autentifica 
daca deține deja un cont, sau să creeze un cont nou de utilizator.
<p align="center">
     <img src = "Imagini\read_me\1.PNG" height="300" width="400" style="float:left">
</p>

Odată logat, se va deschide ecranul principal cu toate opțiunile utilizatorului.
<p align="center">
     <img src = "Imagini\read_me\1_1.PNG" height="300" width="400" style="float:left">
</p>

În continuare vom exemplifica pe scurt ce se întamplă când alegem una din opțiunile prezente pe ecran.
- Show Friends - Toâi prietenii utilizatorului logat vor fi afișati într-un tabel. Acesta ii poate
șterge din lista de prieteni prin selectarea unei persoane din tabel și acționarea butonului "Delete".
<p align="center">
     <img src = "Imagini\read_me\2.PNG" height="300" width="400" style="float:left">
</p>

- Add Friend - Un prieten nou se poate adăuga prin selectarea lui din listă, urmat de acționarea butonului "Send Friend Request".
<p align="center">
     <img src = "Imagini\read_me\3.PNG" height="300" width="400" style="float:left">
</p>

- Friend Requests Sent - Afișarea tututot cererilor de prietenie trimise de către utilizatorul logat, 
care încă se află în statusul "Pending" (nu au fost înca acceptate). La selectarea unei cereri 
trimise și acționarea butonului "Delete", se poate retrage cererea de prietenie.
<p align="center">
     <img src = "Imagini\read_me\4.PNG" height="300" width="400" style="float:left">
</p>

- Friend Requests Received - Afișarea tututot cererilor de prietenie pe care utilizatorul logat le-a 
primit. Prin selectarea unei cereri din listă, aceasta se poate accepta sau respinge.
<p align="center">
     <img src = "Imagini\read_me\5.PNG" height="300" width="400" style="float:left">
</p>

- Reports - Genereare de rapoarte referitoare la conversațiile cu un alt utilizator sau evenimentele 
dintr-o anumită perioadă de timp. Se alege tipul raportului, iar în cazul in care se alege prima 
opțiune, se va indica și utilizatorul.
<p align="center">
     <img src = "Imagini\read_me\6.PNG" height="300" width="400" style="float:left">
</p>

- Events - Vizualizarea tuturor evenimentelor, alături de cele la care utilizatorul s-a înscris că va 
participa și urmează să se petreacă în curând. Confirmarea participării / Încetarea participării la 
un eveniment se face prin bifarea căsuței de "Participating". 
         - Utilizatorul poate adăuga un eveniment nou.
         - De asemenea, poate șterge un eveniment. Se selectează evenimentul dorit și se acționează butonul "Delete".
<p>
<div align="center" >
        <img src="Imagini\read_me\7_1.PNG" height="200" width="300" style="padding:5px" />
        <img src="Imagini\read_me\7_2.PNG" height="200" width="300" style="padding:5px"/>
        <img src="Imagini\read_me\7_3.PNG" height="200" width="300" style="padding:5px"/>
</div>
</p>

- Messages - Afișează toate conversațiile cu alți utilizatori pe care utilizatorul logat le-a avut.
<p align="center">
     <img src = "Imagini\read_me\8.PNG" height="300" width="400" style="float:left">
</p>

- Send New Message - Trimiterea unui mesaj unei persoane sau unui grup de persoane.
<p align="center">
     <img src = "Imagini\read_me\9.PNG" height="300" width="400" style="float:left">
</p>

- Group Messages - Mesajele trimise unui grup de persoane vor apărea atât în conversațiile 
individuale cu fiecare utilizator din acel grup, dar și într-o secțiune cu chat comun, în care 
se poate răspunde la mesaje, răspunsul fiind vizibil tuturor din grupul respectiv.
<p>
<div align="center" >
        <img src="Imagini\read_me\10_1.PNG" height="300" width="500" style="padding:5px" />
        <img src="Imagini\read_me\10_2.PNG" height="300" width="500" style="padding:5px"/>
</div>
</p>

- LogOut - Prin acționarea butonului, utilizatorul va fi automat delogat.

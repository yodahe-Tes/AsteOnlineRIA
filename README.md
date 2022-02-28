                                             ASTE ONLINE WEB APP PROJECT  
                                             
                                                       
                                              MVC Thick client architecture

The  web application allows the management of online auctions. Users log in via login and can sell and buy at the auction.
The HOME page contains two links, one to access the SELL page and one to access the PURCHASE page. The SELL page shows a
list of auctions created by the user and not yet closed, a list auctions created and closed by him and a form to create a 
new item and a new auction to sell it. By clicking on an auction, an AUCTION DETAIL page appears which shows the auction 
data and the list of bids for an open auction. A CLOSE button allows the user to close the auction if the deadline has. 
If the auction is closed, the page shows the auction’s data, the name of the successful bidder, the final price and the 
user's shipping address. The PURCHASE page contains a keyword search form. When the buyer submits a keyword, the PURCHASE
page reloads and shows a list of open auctions who contain the keyword in their name or description. By clicking on an 
open auction, the BID page appears showing item data, the list of offers received in order of date + time decreasing and
an input field to place a bid. After sending the offer, the OFFER page shows the list of updated offers. The PURCHASE page 
also contains a list of offers awarded to the user with the article data and the final price.

IMPLEMENTATION

Languages :

- Back-end  with JAVA
- Front-end with javascript , HTML and CSS .

DBMS used : MYSQL
  
Web Server used : Apache Tomcat
  
IDE used : ECLIPSE
  
![Screenshot (28)](https://user-images.githubusercontent.com/61747783/156007319-6edcdb0d-dd6e-4851-acc7-512b0ebd7d90.png)

A document with all the Database and Application Design processes and sequence diagrams is also included in the repository.

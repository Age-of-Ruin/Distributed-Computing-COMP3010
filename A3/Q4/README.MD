# Web ATM using PHP
In essence, question 4 implements a web application that works by relaying commands recieved by bank.html webpage to the bank.php script which returns the correct response. The program emulates a simple banking service and offers the following commands:

Create Account [Acct Num]
Retrieve Balance [Acct Num]
Deposit Amount [Acct Num] [Acct Num]
Withdraw Amount [Acct Num] [Acct Num]
Show All Accounts

Unlike the previous assignments, this simple PHP implementation offers no synchronization/controlled access to the DB. 

Note: bank.html and bank.php should both reside in the same directory on a web server. Particularly for this question, these assets are stored in the public_html folder in my student directory on www3.cs.umanitoba.ca/~constan7. 

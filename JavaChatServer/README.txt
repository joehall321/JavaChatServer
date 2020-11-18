Dear Netcraft developers, 
Thank you for taking the time to code review my basic java ChatServer application. 
This application was written by myself as a small side project last year, while learning the principles of object oriented programming (OOP).
Admittingly after reviewing my code for this review, I would structure my code differently, using a more developed idea of OOP to make the application more easily 
implemented into future projects.
The application I have submitted is a simple implementation of a java networking chat room which allows multiple client connections. 

How to run my java ChatServer application:

NOTE: Please make sure the latest version of Oracle's Java SDK is installed on your machine. 
The best way to see the functionality of the multi-client chat room is to run the compiled code via the command prompt 
so that multiple client side codes can be run on one machine using more than one command prompt window. Else if source code is planned to be run in an IDE, 
then duplicate the client code to produce more than one client.

Contents: The compiled code can be found in the 'out' folder and the source code in 'src' folder. A java documentation on this application is also included in the 'doc' folder. 



I am aware my application has a few drawbacks, such as:
If a connected client terminates his/her program without typing 'quit' then the server throws an error IOexception and still counts the number of clients as the same. 
Clients do not get the opportunity to assign themselves a name. Furthermore, as a client connects the server echos CLIENT has connected which is not meaningful to other clients.
When messages are sent, connected clients can not see who sent it.
No user interface! 

All these drawbacks mentioned above can be easily rectified by implementing simple features but due to my current logistical circumstances,
I am unable to revisit this current project. I am currently in the process of prototyping a web applicaion called 'HouseMates' which will provide a platform for university 
students living in shared rented accommodation. This platform aims to reduce the risk of 'household tensions' between student house mates by introducing a way of fairly
distributing house hold chores, eveningly spreading the weight of rent according to the size of their rooms, creating a 'take out bins' schedule/rotation and much more! 
This is a side project I plan to develop throughout this year until my industrial placement starts. I would love to share this with Netcraft as soon as my friends and 
I role out our first prototype! 



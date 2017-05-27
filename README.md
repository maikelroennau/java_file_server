# Java File Server

This project aims to create a file server system for [this](https://github.com/selatotal/SistemasDistribuidos/blob/master/Trabalhos/201701/README.md) work of Distributed Systems subject.

## Description
- The system needs to be controled by a central manager. 
- This manager will be the interface between the client and the server. 
- The manager will receive the files from the cilent and save them in the server(s).
- The user can upload a file, download a file and delete a file
- When a user request a file the manager will search for it in the server(s) and send to the user

## Usage

**Preparing the environment:**
- Clone the repository
- Open the project Manager and Server (open the Client if you don't have one)
- Compile the projects and run them
- Make sure your firewall is disabled and you have permission to write on the Server's folder

**Simple system: one client -> one manger -> one server**
- Start the manager
- Start the server
- Wait the manager to find the online server
- Start the client and try the functions:
  1. Upload a file: type the whole path and name (i.e. C:\myfile.txt)
  2. Download a file: type the filename to be downloaded
  3. Delete a file: type the filename to be deleted
 
**Multi-server system: one client -> one manager -> multiple servers**
- Copy the project Server and create a new one with a new name, then, change its connection port by adding 1 to the value (i.e.: 'SERVER_PORT = 2090' becomes 'SERVER_PORT = 2091', on line 32)
- Start the manager
- Start the servers
- Wait the manager to find the online servers
- Start the client and try the functions:
  1. Upload a file: type the whole path and name (i.e. C:\myfile.txt)
  2. Download a file: type the filename to be downloaded
  3. Delete a file: type the filename to be deleted
 
 **NOTE:**
 - It is highly recommended to change the range of servers on Manager class to search only for one server if you are going using the simple system
 - To do that, change the value of 'FILE_SERVER_END_PORT' on line 27 to 2090
 
## Authors
- Maikel Maciel Rönnau
- Bruno Accioli

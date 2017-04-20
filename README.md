# Java File Server

This project aims to create a file server system for [this](https://github.com/selatotal/SistemasDistribuidos/blob/master/Trabalhos/201701/README.md) work of Distributed Systems subject.

## Description
- The system needs to be controled by a central manager. 
- This manager will be the interface between the client and the server. 
- The manager will receive the files from the cilent and save them in the server(s).
- The user can upload a file, download a file and delete a file
- When a user request a file the manager will search for it in the server(s) and send to the user

## Usage

Simple system: one client -> one manger -> one server
- Start the manager
- Start the server
- Wait the manager to find the online server
- Start the client and try the functions:
  - 1. Upload a file..: type the whole path and name (i.e. C:\myfile.txt)
  - 2. Download a file: type the filename to be downloaded
  - 3. Delete a file..: type the filename to be deleted
 
Multi-server system: one client -> one manager -> multiple servers
- Start the manager
- Start the servers
- Wait the manager to find the online servers
- Start the client and try the functions:
  - 1. Upload a file..: type the whole path and name (i.e. C:\myfile.txt)
  - 2. Download a file: type the filename to be downloaded
  - 3. Delete a file..: type the filename to be deleted
  
## Authors
- Burno Accioli
- Maikel Maciel Rönnau

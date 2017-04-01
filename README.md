# Java File Server

This project aims to create a file server system for [this](https://github.com/selatotal/SistemasDistribuidos/blob/master/Trabalhos/201701/README.md) work of Distributed Systems subject.

## Description
- The system needs to be controled by a central manager. 
- This manager will be the interface between the client and the server. 
- The manager will receive the files from the cilent and save them in the server(s).
- The user can send a upload a file, download a file and delete a file
- When a user request a file the manager will search for it in the server(s) and send to the user

## Usage

Current status implementation
 - Send file to server(no manager yet)
 - Start the server
 - Start the client:
  - Select option 1 (only option 1 and 0 are implemented)
  - Type the whole path to the file, i.e. C:\myfile.txt
 - The server will print a messagin saying if the transference was ok
 - The client will receive a confirmation from the server

## Authors
- Burno Accioli
- Maikel Maciel Rönnau

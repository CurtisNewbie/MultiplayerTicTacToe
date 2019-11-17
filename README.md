# MultiplierTicTacToe

TicTacToe that supports multiplayers.

<h3>What Is This?</h3>

This is a Tic Tac Toe game that supports multiplayers (Two Players).

<h3>How To Run It</h3>

This is a Maven Project, so Maven must be installed in order to run this game.

To execute the mvn project, type in following command in CLI:

    "mvn clean compile"
    "mvn javafx:run"

To play this game, two Programs need to be executed that the one in folder "Host" and another one in folder "Client". The two terminals (Host and Client) can be deployed and ran on the same machine, please use "localhost" as an IP address in such case. 

On the "Client" side (the computer that runs the "Client" maven project), a dialog will be poped up that ask you to enter an IP address, if nothing provided (e.g., dialog closed, or cancel button being pressed), it will try the default "localhost" address to connect. On the "Host" side (the computer that runs the "Host" maven project), it will simply wait for connection. Both terminals close the connection (Socket and ServerSocekt) when the game finishes. When connection fails, dialogs will be shown.

If you see following messages in you CLI, it means the two terminals have successfully connected.

    "Connected to Client : [Ip Address Of Client]"
    "Connected to Host : [IP Address Of Host]"

<h3>How It Looks Like</h3>

![Before Connection](https://user-images.githubusercontent.com/45169791/69012558-08178380-096f-11ea-9fa8-86f7c243e700.png)
[Before You Connect The Two Terminals]

![During The Game](https://user-images.githubusercontent.com/45169791/69012605-83793500-096f-11ea-800c-0419a4ed739a.png)
[During The Game]


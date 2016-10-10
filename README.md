# Sockets
Network Client-Intermediary-Server with Selective Repeat

# Run program commands 

```
 	Command line
    $ javac Server.java Client.java Intermediary.java	
    $ java Server 
    $ java Intermediary
    $ java Client
```


# Close Open Sockets

Step 1: sudo netstat -ap | grep :< port_number >

Step 2: kill  < pid > 
		OR 
		kill -9 < pid >

import socket

# Establish IP and Port Number
HOST = socket.gethostname()
PORT = 13064

# Receive Until \n 'new-line' Character
def recvall(cliSock): 
    data = ''
    while (1):
        part = cliSock.recv(4096)
        data += part
        if '\n' in data:
            break
    return data

# FUNCTION: Process User Input
def processInput(kbInput):

	# Create Socket and Connect to Server
	# with socket.socket(socket.AF_INET, socket.SOCK_STREAM) as s:  *** Python 3 ***
	cliSock = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
	cliSock.connect((HOST,PORT))

	# Read Welcome Message
	response = recvall(cliSock)
	print('\n' + response)

	# Send Request/Command
	msg = kbInput + '\n'
	cliSock.sendall(msg)

	# Read Response and Closing Message
	response = recvall(cliSock)
	print('\n' + response)

# MAIN:
# Main Loop
while (1):

	# Read Keyboard Input
	# keyboard = input("\nPlease enter command: !-connect, C-create, R-retrieve, D-deposit, W-withdraw, E-quit client") *** Python 3 ***
    keyboard = raw_input("Please enter command: C-create, R-retrieve, D-deposit, W-withdraw, E-quit client\n")
    
    # Quit Client when E is pressed
    if keyboard[0] == 'E':
    	break

    # Process Keyboard Input
    processInput(keyboard)

print("Client Ended...")
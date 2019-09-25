use IO::Socket::INET;
use Sys::Hostname;

# Establish host, port number, and protocol
my $HOST = hostname();
my $PORT = 13064;
my $PROTO = 'tcp';

# Receive Until \n 'new-line' Character
sub recvall {
	my ($cliSock) = @_;
    
    my $data = "";
    while (1) {
		$cliSock -> recv($part,4096);
        $data = $data . $part;
        if (index($data, "\n") != -1) {
            last;
        }
    }
    return $data;
}


# Process User Input
sub processInput {
	my ($kbInput) = @_;

	# Create Socket and Connect to Server
	$cliSock = new IO::Socket::INET (
	PeerHost => $HOST,
	PeerPort => $PORT,
	Proto => $PROTO,
	) or die "ERROR in Socket Creation : $!\n";

	# Read Welcome Message
	$response = recvall($cliSock);
	print "\n" . $response . "\n";

	# Send Message to Server
	print $cliSock $kbInput;

	# Read Response and Closing Message
	$response = recvall($cliSock);
	print "\n" . $response . "\n";

}


# Main Loop
while(1) {

	print "Please enter command: C-create, R-retrieve, D-deposit, W-withdraw, E-quit client\n";

	# Read Keyboard
	my $kbInput = <STDIN>;

	# Exit Client
	if ($kbInput eq "E\n") {
		last;
	}

	# Process Input
	processInput($kbInput);
}

print "Client Ended...\n"

import java.io.*;
import java.net.*;
import java.util.ArrayList;
import javax.swing.*;

@SuppressWarnings("serial")
public class CentralServer extends JFrame{
	JTextArea responseWindow;
	ServerSocket server;
	Socket client;
	ArrayList<String[]> peerList;
	ArrayList<ClientThread> threadList;
	BufferedReader input;
	PrintWriter output;
	ObjectOutputStream outputObject;
	int peerID;
	
	public CentralServer(){
		peerList = new ArrayList<String[]>();
		threadList = new ArrayList<ClientThread>();
		peerID = 0;
		responseWindow = new JTextArea();
		add(new JScrollPane(responseWindow));
		setSize(600,160);
		setVisible(true);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		runServer();
	}
	
	private void runServer(){
		createServer();
		while(true) {
			openSocket();
		}
	}
	
	private void createServer() {
		try {
			server = new ServerSocket(9999);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private void openSocket() {
		try {
			client = server.accept();
			ClientThread thread = new ClientThread(client, responseWindow, peerList, peerID);
			peerID++;
			threadList.add(thread);
			thread.start();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	
	
}

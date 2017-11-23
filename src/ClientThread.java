import java.net.*;
import java.util.ArrayList;
import javax.swing.*;
import java.io.*;

public class ClientThread extends Thread{
	Socket client;
	BufferedReader input;
	PrintWriter output;
	ObjectOutputStream outputObject;
	JTextArea responseWindow;
	ArrayList<String[]> peerList;
	int peerID;
	String peerPort;
	
	public ClientThread(Socket client, JTextArea responseWindow, ArrayList<String[]> peerList, int peerID){
		this.client = client;
		this.responseWindow = responseWindow;
		this.peerList = peerList;
		this.peerID = peerID;
	}
	
	private void openStreams() {
		try{
			input = new BufferedReader(new InputStreamReader(client.getInputStream()));
			output = new PrintWriter(client.getOutputStream(), true);
			outputObject = new ObjectOutputStream(client.getOutputStream());
			outputObject.flush();
		}
		catch(IOException e) {
			e.printStackTrace();
		}
	}
	
	private void getClientAddress() {
		String peerInet = client.getInetAddress().toString();
		showMessage(peerInet + "\n");
		showMessage(peerPort + "\n");
		String[] peerInfo = {Integer.toString(peerID), peerInet, peerPort};
		peerID++;
		peerList.add(peerInfo);
	}
	
	private void removePeerAddress(String pID) {
		for (int i = 0; i < peerList.size(); i++) {
			String[] tempArray = peerList.get(i);
			if(tempArray[0].equals(pID)) {
				peerList.remove(i);
				break;
			}
		}
	}
	
	private void respondToClient(){
		output.println("SERVER-START");
		while(true) {
			try {
				String responseLine = input.readLine();
				showMessage(responseLine + "\n");
				if(responseLine.equals("CLIENT-START")) {
					output.println(Integer.toString(peerID));
					output.println("SERVER-PEER-PORT-REQUEST");
					peerPort = input.readLine();
					getClientAddress();
					outputObject.writeObject(peerList);
					outputObject.flush();
				}
				if(responseLine.equals("CLIENT-PORT-UPDATE")) {
					showMessage("Beginning update\n");
					output.println("SERVER-PORT-UPDATE-OK");
					showMessage("Sent update response\n");
					int tempPeerID = Integer.parseInt(input.readLine());
					int tempPeerPort = Integer.parseInt(input.readLine());
					String[] tempArray = peerList.get(tempPeerID);
					tempArray[2] = Integer.toString(tempPeerPort);
					peerList.set(tempPeerID, tempArray);
				}
				if(responseLine.equals("CLIENT-CLOSE")) {
					output.println("SERVER-CLOSE");
					closeConnection();
					return;
				}
			}
			catch(IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	private void closeConnection() {
		try {
			input.close();
			output.close();
			client.close();
		}
		catch(IOException e) {
			e.printStackTrace();
		}
	}
	
	private void showMessage(final String text){
		SwingUtilities.invokeLater(
			new Runnable(){
				public void run(){
					responseWindow.append(text);
				}
			}
		);
	}
	
	public void run() {
		openStreams();
		respondToClient();
	}
	
}

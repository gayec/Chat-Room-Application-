package lab;

import java.net.*;
import java.util.ArrayList;
import java.io.*;
public class TCPServer 
{
	public static void main(String args[]) throws Exception
	{	
		ArrayList<Socket> friendsSocketList = new ArrayList<>();
		ArrayList<String> friendsList = new ArrayList<>();
		ServerSocket chatSocket = new ServerSocket(6789);
		while (true) 
		{
			Socket newClientFriendSocket = chatSocket.accept();

			BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(newClientFriendSocket.getInputStream()));
			String clientFriendName = bufferedReader.readLine();
		
			friendsList.add(clientFriendName);
			friendsSocketList.add(newClientFriendSocket);

			new Thread(new chatServer(newClientFriendSocket, friendsSocketList, clientFriendName, friendsList)).start();
		}
	}
}
class chatServer extends Thread 
{
	private ArrayList<Socket> socketFriendsList;
	private ArrayList<String> clientFriendsList;
	private Socket onlineFriendSocket;
	private String onlineFriendName;
	private String friendsText;
	
	public chatServer(Socket currentFriendSocket, ArrayList<Socket> socketFriendsList, String currentFriendName, ArrayList<String> clientFriendsList)
	{
		this.onlineFriendSocket = currentFriendSocket;
		this.socketFriendsList = socketFriendsList;
		this.onlineFriendName = currentFriendName;
		this.clientFriendsList = clientFriendsList;
	}

	@Override
	public void run() 
	{
		try 
		{
			System.out.println("Incoming connection from: " + onlineFriendSocket.getRemoteSocketAddress());
			System.out.println("connected: " + onlineFriendName);
			BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(onlineFriendSocket.getInputStream()));

			for (Socket socket : socketFriendsList) 
			{
				if (socket != onlineFriendSocket) 
				{
					DataOutputStream toAllFriendClients = new DataOutputStream(socket.getOutputStream());
					toAllFriendClients.writeBytes(onlineFriendName + " has connected." + '\n');
				}
			}

			for (Socket socket : socketFriendsList) 
			{
				DataOutputStream toAllFriendClients = new DataOutputStream(socket.getOutputStream());
				toAllFriendClients.writeBytes("Chatroom Participants: " + clientFriendsList.toString() + '\n');
			}
			
			while (true) 
			{
				DataOutputStream dataOutputStream = null;
				friendsText = bufferedReader.readLine();
				if (friendsText.contains("PM")) 
				{
					String[] PrivateMessage = friendsText.split(" ", 3);
	
					int privateMessageReceiverSocketIndex = clientFriendsList.indexOf(PrivateMessage[1]);
					DataOutputStream privateMessage = new DataOutputStream(socketFriendsList.get(privateMessageReceiverSocketIndex).getOutputStream());
					privateMessage.writeBytes("PM from " + onlineFriendName + ": " + PrivateMessage[2] + '\n');
					
				}
				if (!friendsText.contains("PM") && !friendsText.equalsIgnoreCase("list")) 
				{
					for (Socket socket : socketFriendsList) 
					{
						if(socket != onlineFriendSocket) 
						{
							dataOutputStream = new DataOutputStream(socket.getOutputStream());
							dataOutputStream.writeBytes(onlineFriendName + ": " + friendsText + '\n');
						}
					}
				}
			}
		} 
		catch (IOException e) 
		{
			System.out.println(onlineFriendName + "has disconnected"+"\n");
		
			for(Socket socket: socketFriendsList)
			{
				try
				{
					DataOutputStream toAllFriends = new DataOutputStream(socket.getOutputStream());
					toAllFriends.writeBytes(onlineFriendName +" has disconnected!"+'\n');
				}
				catch(IOException exception){}
			}
		}
	}
}
package lab;

import java.net.*;
import java.io.*;
public class TCPClient 
{
	public static void main(String args[]) throws Exception 
	{
		String clientFriendName;
		BufferedReader getFromKeyboard = new BufferedReader(new InputStreamReader(System.in));
		System.out.print("Type your nick: ");
		clientFriendName = getFromKeyboard.readLine();
		
		Socket clientFriendSocket = new Socket("localhost", 6789); 
		DataOutputStream postToServer = new DataOutputStream(clientFriendSocket.getOutputStream());
		BufferedReader getFromServer = new BufferedReader(new InputStreamReader(clientFriendSocket.getInputStream()));
		
		new Thread(new Runnable() 
		{
			@Override
			public void run() 
			{
				try 
				{
						postToServer.writeBytes(clientFriendName + '\n');
						while (true) 
						{
							String messageFromOthers = getFromServer.readLine();
							System.out.println(messageFromOthers);
						}
				} 
				catch (IOException e) 
				{
					e.printStackTrace();
				} 
			}
		}).start();
		
		while (true) 
		{
			String messageToBeSent = getFromKeyboard.readLine();
			postToServer.writeBytes(messageToBeSent + '\n');
		}
	}

}
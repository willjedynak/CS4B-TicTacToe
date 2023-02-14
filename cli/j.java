import java.io.*;
import java.net.*;
import java.util.*;

public class j{
public static void main(String[] args){
	ArrayList<Server> serverList = new ArrayList<>(); // We should probably do a map of threads rather than a list of threads. 
	Voice_of_God voice_of_God = new Voice_of_God(serverList);
	while (true){
		try (ServerSocket serverSocket = new ServerSocket(6969)){
			Socket socket = serverSocket.accept();
			Server srv = new Server(serverSocket,socket,serverList);
			// serverList.add(srv); // Moved inside constructor. 
			System.out.println("Spawned a new server, there are now "+serverList.size()+" servers.");
		}
		catch (Exception e){
			System.out.println("Error in server spawning function.");
			System.out.println(e.getStackTrace()); // `e.getStackTrace` or just `e`? 
		}
	}
}}

class Voice_of_God extends Thread{
	ArrayList<Server> lst;
	public void run(){
		java.util.Scanner in = new java.util.Scanner(System.in);
		while (true){
			String msg = in.nextLine();
			for (var srv : lst)
				srv.send(msg);
			//System.out.println("Voice of God spun.");
		}
	}
	Voice_of_God(ArrayList<Server> lst){
		this.lst = lst;
		this.start();
	}
}

class Server extends Thread{
	ServerSocket serverSocket;
	Socket socket;
	BufferedReader bufferedReader;
	BufferedWriter bufferedWriter;
	ArrayList<Server> lst; // The list of all servers. 
	Server(ServerSocket serverSocket,Socket socket,ArrayList<Server> lst){
		this.serverSocket = serverSocket;
		this.socket = socket;
		this.lst = lst; // Important line! 
		System.out.println("Connection established.");
		try{
			this.bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			this.bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
		}
		catch (IOException e){
			System.out.println("Error spawning reader and writer.");
			return; // WATCH OUT.  // Maybe it would be better to throw an error? 
		}
		lst.add(this);
		this.start(); // Should this be _here_?! 
	}
	@Override
	public void run(){
		while (true){
			try{
				String in = bufferedReader.readLine();
				if (in==null)// We sure about this? 
				{	
					lst.remove(this); // We sure about this? 
					break;// We sure about this? 
				}
				for (var srv : lst)
					srv.send(in);
				//System.out.println(in);
				//System.out.println("There are "+lst.size()+" servers.");
				System.out.println("SERVER_COUNT: ("+lst.size()+")\t"+in);
			}
			catch (/*IO*/Exception e){
				System.out.println("Error in server.run sending message.");
			}
			//System.out.println("A message was sent.");
		}
	}
	void send(String in){
		try{
			bufferedWriter.write(in);
			bufferedWriter.newLine(); // Why do we need this? 
			bufferedWriter.flush(); // Think I know why this. 
		}
		catch (Exception e){
			System.out.println("Error in server.send sending message.");
		}
	}
}

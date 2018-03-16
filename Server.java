import java.net.*;
import java.io.*;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.Enumeration;
import java.util.Scanner;

import java.nio.FloatBuffer;
import java.nio.ByteBuffer;
import java.text.DecimalFormat;
import java.util.ArrayList;

public class Server {
	ServerSocket serverSocket;
	String message = "";
	static final int socketServerPORT = 30000;

	public Server() {
		Thread socketServerThread = new Thread(new SocketServerThread());
		socketServerThread.start();
	}

	public int getPort() {
		return socketServerPORT;
	}

	public void onDestroy() {
		if (serverSocket != null) {
			try {
				serverSocket.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	private class SocketServerThread extends Thread {

		int count = 0;

		@Override
		public void run() {
			try{
				// create ServerSocket using specified port
				serverSocket = new ServerSocket(socketServerPORT);
				while (true) {
					// block the call until connection is created and return
					// Socket object
					Socket socket = serverSocket.accept();
					count++;
					message = "#" + count + " from "
					+ socket.getInetAddress() + ":"
					+ socket.getPort();

					System.out.println(message);
					//message = "";
					SocketServerReplyThread socketServerReplyThread = 
					new SocketServerReplyThread(socket, count);
					socketServerReplyThread.run();

				}
			}catch(IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	private class SocketServerReplyThread extends Thread {

		private Socket hostThreadSocket;
		int cnt;

		SocketServerReplyThread(Socket socket, int c) {
			hostThreadSocket = socket;
			cnt = c;
		}

		@Override
		public void run() {
			//RECEIVING MESSAGE
			DataInputStream dIn = null;
			OutputStream outputStream = null;
			byte messageType = 0;
			boolean done = false;
			int bufferSize=0;
			String msg;
			int numPoints = 0;
			msg="";
			FloatBuffer pointCloud = null;
			FloatBuffer smallFloatBuffer=null;
			int pos =1;
			int i =0 ;
			int j = 0;
			int received=0;
			try{
				dIn = new DataInputStream(hostThreadSocket.getInputStream());
			} catch (IOException e) {
					// TODO Auto-generated catch block
				e.printStackTrace();
				System.out.println("Something wrong! " + e.toString() + "\n");
			}
			while(!done) {
				try{
					messageType = dIn.readByte();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					System.out.println("Something wrong! " + e.toString() + "\n");
				}
				try{
					switch(messageType)
					{
						case 1: // Type A
							bufferSize =  dIn.readInt();
							numPoints =  dIn.readInt();

							if(bufferSize == 0)break;
							System.out.println("Numero de floats a serem recebidos: " + Integer.toString(bufferSize/4));
							System.out.println("Numero de pontos a serem recebidos: " + Integer.toString(numPoints));
							//FloatBuffer buf = FloatBuffer.allocate(bufferSize/4);
							bufferSize = 4+bufferSize-bufferSize%4;
							byte[] byteBuffer = new byte[bufferSize];
							byte[] miniBuffer =null;
							pointCloud = FloatBuffer.allocate(bufferSize/4);
							for (i = 0; i < bufferSize/4; i++) {
								//received = dIn.available();
								//System.out.println("Available: "+received/4);
								miniBuffer = new byte[4];
								dIn.read(miniBuffer);
								smallFloatBuffer = ByteBuffer.wrap(miniBuffer).asFloatBuffer();
								//System.out.println("Capacidade: "+smallFloatBuffer.capacity());
								/*
								for(int k = 3;k>0;k--){
									if(Float.compare(smallFloatBuffer.get(k),0.0f) != 0){
										pos = k;	
										break;
									}
								}
								*/

								for (j = 0; j < pos; j++) {

									//byteBuffer[received+j] = miniBuffer[j]; 
									pointCloud.put(smallFloatBuffer.get(j));
									//bigFloatBuffer.put(99999.9f);
									
								}
								//received+=j;
								//}
							}
							//pointCloud = ByteBuffer.wrap(byteBuffer).asFloatBuffer();
							System.out.println("Primeiros 5 pontos:" +  printNFirstPoints(pointCloud,5));
							System.out.println("Ultimos 5 pontos:" +  print5LastPoints(pointCloud));	
							System.out.println("Printando os pontos em um arquivo...");
							printFile(pointCloud,cnt);	
							System.out.println("Pronto!");
						break;
						case 2: // Type B
							System.out.println("Message B: " + dIn.readUTF());
						break;
						case 3: // Type C
							System.out.println("Message C [1]: " + dIn.readUTF());
							System.out.println("Message C [2]: " + dIn.readUTF());
						break;
						default:
						done = true;
					}
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					System.out.println("Something wrong! " + e.toString() + "\n");
				}
			}
			//END OF RECEIVING MESSAGE
			String msgReply = print5LastPoints(pointCloud);

			try {
				outputStream = hostThreadSocket.getOutputStream();
				PrintStream printStream = new PrintStream(outputStream);
				printStream.print(msgReply);
				printStream.close();

				message = "replyed: " + msgReply;

				System.out.println(message);
				
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				message += "Something wrong! " + e.toString() + "\n";
			}

			System.out.println(message);
			try{
				dIn.close();
				outputStream.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				message += "Something wrong! " + e.toString() + "\n";
			}
		}
	}

	public String getIpAddress() {
		String ip = "";
		try {
			Enumeration<NetworkInterface> enumNetworkInterfaces = NetworkInterface
			.getNetworkInterfaces();
			while (enumNetworkInterfaces.hasMoreElements()) {
				NetworkInterface networkInterface = enumNetworkInterfaces
				.nextElement();
				Enumeration<InetAddress> enumInetAddress = networkInterface
				.getInetAddresses();
				while (enumInetAddress.hasMoreElements()) {
					InetAddress inetAddress = enumInetAddress
					.nextElement();

					if (inetAddress.isSiteLocalAddress()) {
						ip += "Server running at : "
						+ inetAddress.getHostAddress();
					}
				}
			}

		} catch (SocketException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			ip += "Something Wrong! " + e.toString() + "\n";
		}
		return ip;
	}
	public static void main (String []args) {
		Server server;

		server = new Server();
		System.out.println(server.getIpAddress());
		/*
		Scanner reader = new Scanner(System.in);  // Reading from System.in
		System.out.println("Comando: ");
		String l = reader.nextLine(); 
		//once finished
		while(!(l.trim().equalsIgnoreCase("quit"))){
			System.out.println("Comando: ");
			System.out.println(l);
			l = reader.nextLine(); 
		}
		server.onDestroy();
		reader.close(); 
		*/
	}
    private String print5LastPoints(FloatBuffer pointCloudBuffer) {
        String pointsString = "";
        if (pointCloudBuffer.position() > 0) {
            for (int i = pointCloudBuffer.position()-21; i < pointCloudBuffer.position()-1; i++) {
                pointsString +=" " + pointCloudBuffer.get(i);
            }
        }else{
            pointsString="Não deu pra recuperar a nuvem de pontos :(";
        }
        return pointsString;
    }
	private String printNFirstPoints(FloatBuffer pointCloudBuffer, int numPoints) {
		String pointsString = "";
		if (numPoints != 0) {
			int numFloats = 4 * numPoints;
			for (int i = 0; i < numFloats; i = i + 1) {
				pointsString +=" " + pointCloudBuffer.get(i);
			}
		}else{
			pointsString="Não deu pra recuperar a nuvem de pontos :(";
		}
		return pointsString;
	}
	private void printFile(FloatBuffer floatBuffer,int cnt) {
		int i=0;
		int count=0;
		int count2=0;
		float aux;
		try{
			PrintStream ps = new PrintStream("data_tango_"+cnt+".txt");
			for(i = 0; i < floatBuffer.position()-1; i++)
			{
				aux = floatBuffer.get(i);
				//pra printar so x y e z
				if(count==3){
					count=0;
					ps.println("");
				}else{
					ps.print(" "+aux);	
					count++;
				}	
				count2++;
			}
			/*
			ps.println("");
			ps.println("Printed "+count2/4+" points!");
			ps.println("Number of points was "+numPoints+"!");
			ps.println("Capacity was "+floatBuffer.capacity()+" floats!");
			*/
			ps.close();
		}catch(FileNotFoundException e){
			e.printStackTrace();
		}
	}
}
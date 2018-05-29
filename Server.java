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
import java.lang.Process;

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
			Process process = null; //THIS IS THE PROCESS WE ARE GOING TO CALL AFTER RECEIVEING THE MESSAGE
			String responseFile = "\\output_openpose\\";
			File imgDirectory = null;
			String picturePath = null;
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
							if(bufferSize == 0){
								System.out.println("Buffer recebido estava vazio!");				
								break;
							}
							System.out.println("Tamanho do buffer a ser recebido: " + Integer.toString(bufferSize));
							int bytesRead = 0;
							byte[] byteBuffer = new byte[bufferSize];
							byte[] miniBuffer =null;
							int bytesPerTime = 4;
							for (i = 0; i < bufferSize/bytesPerTime; i++) {
								
								miniBuffer = new byte[bytesPerTime];
								dIn.read(miniBuffer);

								for (j = 0; j < bytesPerTime; j++) {
									byteBuffer[bytesRead] = (miniBuffer[j]);
									bytesRead++;
								}
							}
							// Create a directory; all non-existent ancestor directories are
							// automatically created
							System.out.println("Creating images folder...");
							imgDirectory = (new File("./images"));
							if (imgDirectory.mkdirs()) {
							    System.out.println("Something wrong! Directory creation Failed! Does it already exist?");
							}else{
								System.out.println("Done!");
							}
							picturePath = ".\\images\\picture" + imgDirectory.list().length + ".jpg";
							FileOutputStream fos = new FileOutputStream(picturePath);
							System.out.println("Saving image to folder...");
							fos.write(byteBuffer);
							fos.close();
							System.out.println("Done!");
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
			String msgReply = "Error while running the skeleton lib!";
			System.out.println("Done Receiveing! Starting lib! ");
			try{//run the libary
				process = new ProcessBuilder("bin\\OpenPoseDemo.exe","--display","0","--image_dir",".\\images" ,"--write_json",".\\output_openpose\\","--write_images",".\\output_photos_openpose\\").start();
				//bin\OpenPoseDemo.exe --image_dir examples\media\
				//bin\OpenPoseDemo.exe -image_dir .\images -write_json .\output_openpose\
				//bin\OpenPoseDemo.exe -image_dir .\images -write_json .\output_openpose\ --write_images .\output_photos_openpose\ --display 0
				//-- keypoint_scale default:0 hint:"Scaling of the (x,y) coordinates of the final pose data array,
				//the scale of the (x,y) coordinates that will be saved with the write_keypoint & write_keypoint_json flags. 
				//0 to scale it to the original source resolution, 
				//1 to scale it to the net output size (set with net_resolution), 
				//2 to scale it to the final output size (set with resolution), 
				//3 to scale it in the range [0,1], 
				//and 4 for range [-1,1].
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				message += "Something wrong! " + e.toString() + "\n";
			}
			try{//waiting the lib to process the image
				if(process != null)process.waitFor();	
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				message += "Something wrong! " + e.toString() + "\n";
			}
			try{
				BufferedReader stdInput = new BufferedReader(new 
				     InputStreamReader(process.getInputStream()));

				BufferedReader stdError = new BufferedReader(new 
				     InputStreamReader(process.getErrorStream()));

				// read the output from the command
				String s = null;
				while ((s = stdInput.readLine()) != null) {
				    System.out.println(s);
				}

				// read any errors from the attempted command
				while ((s = stdError.readLine()) != null) {
				    System.out.println(s);
				}
			}catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				message += "Something wrong! " + e.toString() + "\n";
			}
			try {
				FileReader arq = new FileReader(".\\"+responseFile + "picture"+(imgDirectory.list().length-1)+"_keypoints.json");
				BufferedReader lerArq = new BufferedReader(arq);
				String linha = lerArq.readLine(); // lê a primeira linha
				// a variável "linha" recebe o valor "null" quando o processo
				// de repetição atingir o final do arquivo texto
				msgReply = linha;
				while (linha != null) {
					linha = lerArq.readLine(); // lê da segunda até a última linha
					if(linha != null)msgReply += linha;
				}
				arq.close();
			} catch (IOException e) {
				System.err.printf("Error opening response file: %s.\n",
					e.getMessage());
			}

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
}
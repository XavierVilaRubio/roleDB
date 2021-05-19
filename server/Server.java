//Cristian Oprea & Xavier Vila

import java.net.ServerSocket;
import java.net.Socket;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.EOFException;

public class Server {

	private static int port = 1235;
    private static final String CHARACTERS_DB_NAME = "charactersDB.dat";
	private static CharactersDB charactersDB;

	public static void main(String[] args){
		try {
			charactersDB = new CharactersDB (CHARACTERS_DB_NAME);
		} catch (IOException ex) {
			System.err.println ("Error opening database!");
			System.exit (-1);
		}
		try {
			ServerSocket ss = new ServerSocket(port);
			Socket s = ss.accept();
			ss.close();
			DataInputStream  dis = new DataInputStream  (s.getInputStream());
			DataOutputStream dos = new DataOutputStream (s.getOutputStream());

			Thread reader = new Thread(new Reader(s, dis, dos));
			reader.start();

		} catch (Exception e){
			e.printStackTrace();
		}
	}

	public static class Reader implements Runnable {

		private Socket s;
		private static DataInputStream dis;
		private static DataOutputStream dos;

		public Reader(Socket socket, DataInputStream dis, DataOutputStream dos) {
			this.s = socket;
			this.dis = dis;
			this.dos = dos;
		}

		@Override
		public void run() {
			try {
				int option = 0;
				//Mentres no rebi 5 anem llegint
				for(;;) {
					try {
						option = dis.readInt();
                        // System.out.println(option);
                        switch (option) {
						case 1:
							serverListNames();
							break;
						case 2:
							serverInfoFromOneCharacter();
							break;
						case 3:
							serverAddCharacter();
							break;
						case 4:
							serverDeleteCharacter();
							break;
						case 5:
							// quit();
							break;
					    }
					    // System.out.println();
					} catch (EOFException e) {
						dos.close();
						dis.close();
						s.close();
						System.exit (0);
					}
				}
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				// Quan arribi aquí haurá sortit del while, per tant haurà rebut FI i haurà d'acabar
				try {
					dos.close();
					dis.close();
					s.close();
					System.exit (0);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

		private static void serverListNames() throws IOException {
			int numCharacters = charactersDB.getNumCharacters();
			dos.writeInt(numCharacters);
			try {
				for (int i = 0; i < numCharacters; i++) {
					CharacterInfo character = charactersDB.readCharacterInfo (i)	;
					dos.writeUTF(character.getName());
				}
			} catch (IOException ex) {
				System.err.println ("Database error!");
			}
		}

		private static void serverInfoFromOneCharacter() throws IOException {
			String name;
			try {
				name = dis.readUTF();
			} catch (IOException ex) {
				System.err.println ("Error while reading name!");
				return;
			}
			try {
				int n = charactersDB.searchCharacterByName (name);
				if (n != -1) {
					dos.writeBoolean(true);
					CharacterInfo character = charactersDB.readCharacterInfo (n);
					dos.write(character.toBytes());
				} else {
					dos.writeBoolean(false);
				}
			} catch (IOException ex) {
				System.err.println ("Database error!");
			}
		}

		private void serverAddCharacter() throws IOException {
			byte[] personatge = new byte[CharacterInfo.SIZE];
			dis.read(personatge);
			try {
				boolean success = charactersDB.insertNewCharacter (CharacterInfo.fromBytes(personatge));
				if (success) {
					dos.writeUTF("Persontage afegit correctament.");
				} else {
					dos.writeUTF("Aquest personatge ja estava a la base 	de dades.");
				}
			} catch (IOException ex) {
				dos.writeUTF("Database error!");
			}
		}

		private void serverDeleteCharacter() throws IOException {
			try {
				boolean success = charactersDB.deleteByName (dis.readUTF());
				if (success) {
					dos.writeUTF("Personatge esborrat.");
				} else {
					dos.writeUTF("Personatge no trobat.");
				}
			} catch (IOException ex) {
				dos.writeUTF("Database error!");
			}
		}

	}

}

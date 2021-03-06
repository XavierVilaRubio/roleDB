//Cristian Oprea & Xavier Vila

import java.net.ServerSocket;
import java.net.Socket;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.EOFException;

public class Server {

	private static int port = 1235;
    private static final String CHARACTERS_DB_NAME = "charactersDB.dat";
	private static CharactersDB charactersDB;
	private static DataInputStream  dis;
	private static DataOutputStream dos;

	public static void main(String[] args){
		try {
			charactersDB = new CharactersDB (CHARACTERS_DB_NAME);
		} catch (IOException ex) {
			System.err.println ("Error opening database!");
			System.exit (-1);
		}
		for(;;) {
			try {
				ServerSocket ss = new ServerSocket(port);
				Socket s = ss.accept();
				ss.close();
				dis = new DataInputStream  (s.getInputStream());
				dos = new DataOutputStream (s.getOutputStream());

				int option = 0;
				try {
					option = dis.readInt();
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
				    }
				} catch (EOFException e) {
					e.printStackTrace();
				}

			} catch (Exception e){
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

		private static void serverAddCharacter() throws IOException {
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

		private static void serverDeleteCharacter() throws IOException {
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

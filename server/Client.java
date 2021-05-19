//Cristian Oprea & Xavier Vila

import java.net.Socket;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.EOFException;

public class Client {

    private static String address = "localhost";
    private static int port = 1235;

    public static void main(String[] args) {
        try {
            Socket s = new Socket(address, port);
            DataInputStream  dis = new DataInputStream  (s.getInputStream());
            DataOutputStream dos = new DataOutputStream (s.getOutputStream());
			
			Thread writer = new Thread(new Writer(s, dos, dis));
			writer.start();

        } catch (Exception e) {
            // e.printStackTrace();
            System.err.println("Servidor ocupat. Torna-ho a intentar aviat.");
        }
    }

	public static class Writer implements Runnable {
		
		private Socket s;
        private DataOutputStream dos;
        private static DataInputStream dis;

		public Writer(Socket socket, DataOutputStream dos, DataInputStream dis) {
            this.s = socket;
            this.dos = dos;
            this.dis = dis;
        }

		@Override
        public void run() {
            try {
				int option = 0;
                //Mentres no envii 5 anem enviant
				while (option != 5)
				{
					printMenu();
					option = getOption();
					switch (option) {
						case 1:
							clientListNames();
							break;
						case 2:
							clientInfoFromOneCharacter();
							break;
						case 3:
							clientAddCharacter();
							break;
						case 4:
							clientDeleteCharacter();
							break;
						case 5:
							// quit();
							break;
					}
			    	System.out.println();
				}
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
                // Quan arribi aquí haurá sortit del while, per tant haurà enviat FI i haurà d'acabar
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
		
		private void clientListNames() throws IOException {
			dos.writeInt(1);
			int numCharacters = dis.readInt();
			System.out.println();
			for(int i = 0; i < numCharacters; i++) {
				System.out.println(dis.readUTF());
			}
		}

		private void clientInfoFromOneCharacter() throws IOException {
			BufferedReader in = new BufferedReader (new InputStreamReader (System.in));
			System.out.println ("Escriu el nom del personatge: ");
			String name;
			try {
				name = in.readLine();
			} catch (IOException ex) {
				System.err.println ("Error while reading name!");
				return;
			}
			dos.writeInt(2);
			dos.writeUTF(name);
			if(dis.readBoolean()) {
				byte[] personatge = new byte[CharacterInfo.SIZE];
				dis.read(personatge);
				System.out.println(CharacterInfo.fromBytes(personatge).toString());
			} else {
				System.out.println ("Personatge no trobat.");
			}
		}

		private void clientAddCharacter() throws IOException {
			BufferedReader in = new BufferedReader (new InputStreamReader (System.in));
			CharacterInfo character;
			try {
				System.out.println ("Escriu el nom del personatge a afegir: ");
				String name = in.readLine();
				while (name == null || name.isEmpty()) {
					System.out.println ("El nom del personatge no pot ser buit.	");
					System.out.println ("Escriu el nom del personatge a afegir: 	");
					name = in.readLine();
				}
				int intelligence = -1;
				while (intelligence < 0) {
					System.out.println ("Introdueix la intel·ligència: ");
					String intelligenceStr = in.readLine();
					if (intelligenceStr != null) {
						try {
							intelligence = Integer.parseInt (intelligenceStr);
						} catch (NumberFormatException ex) {
							// Ignore
						}
					}
				}
				int strength = -1;
				while (strength < 0) {
					System.out.println ("Introdueix la força: ");
					String strengthStr = in.readLine();
					if (strengthStr != null) {
						try {
							strength = Integer.parseInt (strengthStr);
						} catch (NumberFormatException ex) {
							// Ignore
						}
					}
				}
				int constitution = -1;
				while (constitution < 0) {
					System.out.println ("Introdueix la constitució: ");
					String constitutionStr = in.readLine();
					if (constitutionStr != null) {
						try {
							constitution = Integer.parseInt (constitutionStr);
						} catch (NumberFormatException ex) {
							// Ignore
						}
					}
				}
				character = new CharacterInfo (name, intelligence, strength, 	constitution);
			} catch (IOException ex) {
				System.err.println ("Error while reading character 	information!");
				return;
			}
			dos.writeInt(3);
			dos.write(character.toBytes());
			System.out.println(dis.readUTF());
		}
		
		private void clientDeleteCharacter() throws IOException {
			BufferedReader in = new BufferedReader (new InputStreamReader 	(System.in));
			System.out.println ("Escriu el nom del personatge a eliminar: ");
			String name;
			try {
				name = in.readLine();
			} catch (IOException ex) {
				System.err.println ("Error while reading name!");
				return;
			}
			dos.writeInt(4);
			dos.writeUTF(name);
			System.out.println(dis.readUTF());
		}
		
	}

    private static void printMenu() {
		System.out.println ("Menú d'opcions:");
		System.out.println ("1 - Llista tots els noms de personatge.");
		System.out.println ("2 - Obté la informació d'un personatge.");
		System.out.println ("3 - Afegeix un personatge.");
		System.out.println ("4 - Elimina un personatge.");
		System.out.println ("5 - Sortir.");
	}

	private static int getOption() {
		for (;;) {
			try {
				BufferedReader in = new BufferedReader (new InputStreamReader (System.in));
				System.out.println ("Escull una opció: ");
				String optionStr = in.readLine();
				int option = Integer.parseInt (optionStr);
				if (0 < option && option <= 5) {
					return option;
				}
			} catch (Exception ex) {
				System.err.println ("Error reading option.");
			}
		}
	}
    
}
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
					dos.writeInt(option);
					switch (option) {
						case 1:
							readListNames();
							break;
						case 2:
							// infoFromOneCharacter();
							break;
						case 3:
							// addCharacter();
							break;
						case 4:
							// deleteCharacter();
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


		private void readListNames() {
			int numCharacters = dis.readInt();
			for(int i = 0; i < numCharacters; i++) {
				System.out.println(i + dis.readUTF());
			}
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

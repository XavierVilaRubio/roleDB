import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.io.IOException;

public class Main {

	private static final String CHARACTERS_DB_NAME = "charactersDB.dat";
	private static CharactersDB charactersDB;

	public static void main (String[] args) {
		try {
			charactersDB = new CharactersDB (CHARACTERS_DB_NAME);
		} catch (IOException ex) {
			System.err.println ("Error opening database!");
			System.exit (-1);
		}
		for (;;) {
			printMenu();
			int option = getOption();
			switch (option) {
				case 1:
					listNames();
					break;
				case 2:
					infoFromOneCharacter();
					break;
				case 3:
					addCharacter();
					break;
				case 4:
					deleteCharacter();
					break;
				case 5:
					quit();
					break;
			}
			System.out.println();
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

	private static void listNames() {
		int numCharacters = charactersDB.getNumCharacters();
		System.out.println();
		try {
			for (int i = 0; i < numCharacters; i++) {
				CharacterInfo character = charactersDB.readCharacterInfo (i);
				System.out.println (character.getName());
			}
		} catch (IOException ex) {
			System.err.println ("Database error!");
		}
	}

	private static void infoFromOneCharacter() {
		BufferedReader in = new BufferedReader (new InputStreamReader (System.in));
		System.out.println ("Escriu el nom del personatge: ");
		String name;
		try {
			name = in.readLine();
		} catch (IOException ex) {
			System.err.println ("Error while reading name!");
			return;
		}
		try {
			int n = charactersDB.searchCharacterByName (name);
			if (n != -1) {
				CharacterInfo character = charactersDB.readCharacterInfo (n);
				System.out.println (character);
			} else {
				System.out.println ("Personatge no trobat.");
			}
		} catch (IOException ex) {
			System.err.println ("Database error!");
		}
	}

	private static void addCharacter() {
		BufferedReader in = new BufferedReader (new InputStreamReader (System.in));
		CharacterInfo character;
		try {
			System.out.println ("Escriu el nom del personatge a afegir: ");
			String name = in.readLine();
			while (name == null || name.isEmpty()) {
				System.out.println ("El nom del personatge no pot ser buit.");
				System.out.println ("Escriu el nom del personatge a afegir: ");
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
			character = new CharacterInfo (name, intelligence, strength, constitution);
		} catch (IOException ex) {
			System.err.println ("Error while reading character information!");
			return;
		}
		try {
			boolean success = charactersDB.insertNewCharacter (character);
			if (!success) {
				System.out.println ("Aquest personatge ja estava a la base de dades.");
			}
		} catch (IOException ex) {
			System.err.println ("Database error!");
		}
	}

	private static void deleteCharacter() {
		BufferedReader in = new BufferedReader (new InputStreamReader (System.in));
		System.out.println ("Escriu el nom del personatge a eliminar: ");
		String name;
		try {
			name = in.readLine();
		} catch (IOException ex) {
			System.err.println ("Error while reading name!");
			return;
		}
		try {
			boolean success = charactersDB.deleteByName (name);
			if (!success) {
				System.out.println ("Personatge no trobat.");
			}
		} catch (IOException ex) {
			System.err.println ("Database error!");
		}
	}

	private static void quit() {
		try {
			charactersDB.close();
			System.exit (0);
		} catch (IOException ex) {
			System.err.println ("Error closing database!");
			System.exit (-1);
		}
	}

}

import java.io.File;
import java.io.FileReader;
import java.io.BufferedReader;
import java.io.IOException;

public class CharacterInfoReader {

	public static CharacterInfo readCharacterFile (String dirName, String fileName) throws IOException {
		File file = new File (dirName, fileName);
		BufferedReader input = new BufferedReader (new FileReader (file));

		String name = input.readLine();
		if (name == null) {
			name = "";
			System.err.println ("Empty name in file " + fileName);
		}

		int intelligence = 100;
		String intelligenceStr = input.readLine();
		if (intelligenceStr != null) {
			try {
				intelligence = Integer.parseInt (intelligenceStr.trim());
			} catch (NumberFormatException ex) {
				System.err.println ("Intelligence error in file " + fileName);
			}
		} else {
			System.err.println ("Empty intelligence in file " + fileName);
		}

		int strength = 100;
		String strengthStr = input.readLine();
		if (strengthStr != null) {
			try {
				strength = Integer.parseInt (strengthStr.trim());
			} catch (NumberFormatException ex) {
				System.err.println ("Strength error in file " + fileName);
			}
		} else {
			System.err.println ("Empty strength in file " + fileName);
		}

		int constitution = 100;
		String constitutionStr = input.readLine();
		if (constitutionStr != null) {
			try {
				constitution = Integer.parseInt (constitutionStr.trim());
			} catch (NumberFormatException ex) {
				System.err.println ("Constitution error in file " + fileName);
			}
		} else {
			System.err.println ("Empty constitution in file " + fileName);
		}

		return new CharacterInfo (name, intelligence, strength, constitution);
	}

}

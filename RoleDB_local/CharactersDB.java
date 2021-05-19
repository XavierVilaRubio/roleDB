import java.io.RandomAccessFile;
import java.io.IOException;

public class CharactersDB {

	private RandomAccessFile charactersDB;
	private int numCharacters;

	public CharactersDB (String fileName) throws IOException {
		charactersDB = new RandomAccessFile (fileName, "rw");
		numCharacters = (int)charactersDB.length() / CharacterInfo.SIZE;
	}

	public int getNumCharacters() {
		return numCharacters;
	}

	public void close() throws IOException {
		charactersDB.close();
	}

	public void reset() throws IOException {
		charactersDB.setLength (0);
		numCharacters = 0;
	}

	public CharacterInfo readCharacterInfo (int n) throws IOException {
		charactersDB.seek (n * CharacterInfo.SIZE);
		byte[] record = new byte[CharacterInfo.SIZE];
		charactersDB.read (record);
		return CharacterInfo.fromBytes (record);
	}

	public int searchCharacterByName (String name) throws IOException {
		for (int i = 0; i < numCharacters; i++) {
			CharacterInfo character = readCharacterInfo (i);
			if (name.equalsIgnoreCase (character.getName())) {
				return i;
			}
		}
		return -1;
	}

	public void writeCharacterInfo (int n, CharacterInfo character) throws IOException {
		charactersDB.seek (n * CharacterInfo.SIZE);
		byte[] record = character.toBytes();
		charactersDB.write (record);
	}

	public void appendCharacterInfo (CharacterInfo character) throws IOException {
		writeCharacterInfo (numCharacters, character);
		numCharacters++;
	}

	public boolean insertNewCharacter (CharacterInfo character) throws IOException {
		int n = searchCharacterByName (character.getName());
		if (n == -1) {
			appendCharacterInfo (character);
			return true;
		}
		return false;
	}

	private void deleteCharacter (int n) throws IOException {
		CharacterInfo lastCharacter = readCharacterInfo (numCharacters - 1);
		writeCharacterInfo (n, lastCharacter);
		charactersDB.setLength ((numCharacters - 1) * CharacterInfo.SIZE);
		numCharacters--;
	}

	public boolean deleteByName (String name) throws IOException {
		int n = searchCharacterByName (name);
		if (n != -1) {
			deleteCharacter (n);
			return true;
		}
		return false;
	}

}

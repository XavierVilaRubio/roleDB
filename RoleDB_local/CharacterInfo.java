public class CharacterInfo {

	private String  name;

	// For the following stats, the expected value for a human is 100
	private int intelligence;
	private int strength;
	private int constitution;

	private static final int NAME_LIMIT = 26;
	public  static final int SIZE = 2 * NAME_LIMIT + 4 + 4 + 4;

	public CharacterInfo (String name, int intelligence, int strength, int constitution) {
		this.name           = name;
		this.intelligence   = intelligence;
		this.strength       = strength;
		this.constitution   = constitution;
	}

	// Getters
	public String  getName          () { return name;           }
	public   int   getIntelligence  () { return intelligence;   }
	public   int   getStrength      () { return strength;       }
	public   int   getConstitution  () { return constitution;   }

	public byte[] toBytes() {
		byte[] record = new byte[SIZE];
		int offset = 0;
		// Name
		PackUtils.packLimitedString (name, NAME_LIMIT, record, offset);
		offset += 2 * NAME_LIMIT;
		// Intelligence
		PackUtils.packInt (intelligence, record, offset);
		offset += 4;
		// Strength
		PackUtils.packInt (strength, record, offset);
		offset += 4;
		// Constitution
		PackUtils.packInt (constitution, record, offset);
		// offset += 4;
		return record;
	}

	public static CharacterInfo fromBytes (byte[] record) {
		int offset = 0;
		// Name
		String name = PackUtils.unpackLimitedString (NAME_LIMIT, record, offset);
		offset += 2 * NAME_LIMIT;
		// Intelligence
		int intelligence = PackUtils.unpackInt (record, offset);
		offset += 4;
		// Strength
		int strength = PackUtils.unpackInt (record, offset);
		offset += 4;
		// Constitution
		int constitution = PackUtils.unpackInt (record, offset);
		// offset += 4;
		return new CharacterInfo (name, intelligence, strength, constitution);
	}

	public String toString() {
		String result = name;
		result += System.lineSeparator() +
			"Intel·ligència: " + intelligence + System.lineSeparator() +
			"Força:          " + strength     + System.lineSeparator() +
			"Constitució:    " + constitution + System.lineSeparator();
		return result;
	}

}

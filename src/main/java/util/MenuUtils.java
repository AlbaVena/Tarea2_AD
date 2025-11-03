package util;

public class MenuUtils {
	
	public static boolean soloLetras(String palabra) {
		if (palabra == null) {
			return false;
		}
		for (char c : palabra.toCharArray()) {
			if (Character.isDigit(c) || Character.isWhitespace(c)) {
				return false;
			}
		}
		return true;
	}

}

package vigenere_cipher;
import java.io.FileWriter;
import java.io.IOException;

public class Main {
    static String cipherText = "XCIUIHTVOQVRLHJEYJXAVICEJFWXRVRUAAEPVPNEQLFZGFQEBXOIUXGIVJXVGLBRBXYIDB" + //
                        "FZVKCSGHNITYJBXTSWXZHWZAYSEPINIIZWBRMITIFMQJRKLKASRIMIJPICEMIGGEIINWUT" + //
                        "PZWVIFFEDRJXJXEHTISVNGOWRRVLIMZZGWLHZWZKFXHDRRASRXCEKKAOINYJIJLWJPVGGG" + //
                        "XMSCSNXVVGTIKLXJXYIAKHVXREKTVZWLPLEERIEJGKGZQVRLBWNSDILBQZWLRSUPZXFVWV" + //
                        "SQIIXZXGJRKIFMSAICIUMVJRZGUHQHYEMUTXDSEWXKSHXYILXGCRFPGZCKVFZAWIMIMIFB" + //
                        "RMIJTGGWZXFEUHYMXFVVXVJVUYDREPXYSJBDZHNEJKEIXZWKNIYFPEXXHZVRPBNHBIWSJX" + //
                        "BVQGPWFEICTSEFYIMTELBSIWJIJOMXIJRGPIIGICHMGZVKEAGGJQDYFBGVXZSFLFTHVJSN" + //
                        "POAZXZMLZOVCFXGZWJEJRXJHVGJRTOXYIUHQHYEMUTXDSEWKHPZPPMFMLZLRRVLSAXYIWG" + //
                        "HPWVVLAMNEGTDBINFFXZPLZRKLWWEOEZWAGQJXZSFHZZVVPWVXMSEMUGIOAFVCLSMEKVWL" + //
                        "XJRRRWEIXXISFBGYIMMUXMAXYIUHQHYEMUTXDSEWHKSQMUIJBWNIIZWWADXYEOTVMEEXKX" + //
                        "IFMEKLASNITSEFYIMTELBSIWKLWIVJZZHWKGVRESLIVJZZHWMLZHRXSUIXELWWBXCEJHWL" + //
                        "MBRVHLAIOITLFHPJKPWMVLOLRXAMGVRESLUIVGTIKLIYFPEFRXCMIHHTVOCNIVHRJXYENX" + //
                        "EICJMDOIMFLPDXXNEEHLAIYMJGMLWDSEWOBXCMEXZXISITYLBZZFIEFVLVVVWLBPGSEKGB" + //
                        "RBAYMDXXCIIIZTWISKCWMFZIEEVXGDWZSFPLZXYIJMSNIVODXKDWCELBSIAVQMLXRSIOOB" + //
                        "XCGFRYKINWZRVNWOVPEUTHZQZGKIVDZRGQZVJYGWSGHJXYIJLXJGIEXMEIEGTJHEXLKLSM" + //
                        "EYHIIKLINECPGYXCIDYDMMKPVGGFTZXZRYVSIGVVFLXCEKLSOIWIVRLAIASTYKHJNSDYUA" + //
                        "HZFRXWUYOAVGSGEGPRKJXIOLRXOXADPCRWXHJRXSAGKCSEIKMEIHZRXHVHIUTMUPDGUITT" + //
                        "XZESSMMLJASIKMXJTISLXGOPZFWKXTEEHKXGPVZXQBRWSKLGNVGENWSGHJYIXWVLISCSYR";

    // frequency of letters in the English language, got from the class notes
    static double[] englishFreq = {
        .082, .015, .028, .043, .127, .022, .020, .061, .070, .002, .008, .040, .024,
        .067, .075, .019, .001, .060, .063, .091, .028, .010, .023, .001, .020, .001
    };

    public static void main(String[] args) {
        String fileName = "output.txt";
        // Prints out each substring for each length of m = 6, 7, 8 as well as each of their index of coincidences to an output file
        try {
        FileWriter out = new FileWriter(fileName);
            out.write(sortSubStrings(6));
            out.write(sortSubStrings(7));
            out.write(sortSubStrings(8));
            String[] table = findKeyword(7);
            out.write(table[0] + "\n");
            out.write(decrypt(table[1]));
            out.close();
        } catch (IOException e) {
            System.out.println("Error occured");
        }
    }

    // Function that takes the original Cipher Text, splits into the specified amount of substrings, and returns the substrings and their IOCs as a string
    public static String sortSubStrings(int length) {
        String result = "Substrings where m = " + length + "\n";
        String[] substrings = new String[length];

        // Divides the original cipher text into substrings containing each ith letter
        for (int i = 0; i < cipherText.length(); i++) {
            if (substrings[i%length] == null) {
                substrings[i%length] = String.valueOf(cipherText.charAt(i));
            }
            else {
                substrings[i%length] += String.valueOf(cipherText.charAt(i));
            }
        }
        // Creates the final result string with the letters the substrings contain as well as their index of coincidence
        for (int i = 0; i < length; i++) {
            String curr = "y" + (i+1);
            result += curr + ": " + substrings[i].toString() + "\n";
            String roundedIOC = String.format("%.4f", indexOfCoincidence(substrings[i]));
            result += "Index of coincidence for " + curr + ": " + roundedIOC + "\n";
        }
        return result;
    }

    // Calculates and returns the index of coincidence for a given substring
    public static double indexOfCoincidence(String substring) {
        int[] letterFrequency = new int[26];
        for (int i = 0; i < substring.length(); i++) {
            letterFrequency[(int)substring.charAt(i) - 65] += 1;
        }
        double numerator = 0;
        for (int i = 0; i < 26; i++) {
            int currAmount = letterFrequency[i];
            if (currAmount > 1) {
                numerator += currAmount * (currAmount - 1);
            }
        }
        double denominator = (substring.length() * (substring.length() - 1));
        return numerator / denominator;
    }  
    
    public static String decrypt(String key) {
        String decrypted = "Raw plaintext:\n";
        
        for (int i = 0; i < cipherText.length(); i++) {
            int n = (((int)cipherText.charAt(i) - 65) - ((int)key.charAt(i%7) - 65));
            if (n < 0) {
                n += 26;
            }
            // System.out.print(n + " ");
            decrypted += (char)(n+97);
            if (i % 50 == 49) {
                decrypted += "\n";
            }
        }
        return decrypted;
    }

     // calculates M_g = p . v_g = sum over i of p_i * f_((i+g) mod 26) / N for every shift g = 0, ..., 25
    public static double[] mValues(String substring) {
        // Counts how many times each letter A-Z appears in the substring
        int[] letterFrequency = new int[26];
        for (int i = 0; i < substring.length(); i++) {
            letterFrequency[(int)substring.charAt(i) - 65] += 1;
        }

        // normalize frequencies
        double N = substring.length();
        double[] M = new double[26];
        // Computes the dot product of the english frequencies with the substring's frequencies shifted by g
        for (int g = 0; g < 26; g++) {
            double sum = 0;
            for (int i = 0; i < 26; i++) {
                sum += englishFreq[i] * letterFrequency[(i + g) % 26] / N;
            }
            M[g] = sum;
        }
        return M;
    }

    // calculates the keyword for a given length
    public static String[] findKeyword(int length) {
        // Divides the original cipher text into substrings containing each ith letter (same as in sortSubStrings)
        String[] substrings = new String[length];
        for (int i = 0; i < cipherText.length(); i++) {
            // If the substring is null initialize it with the current character
            if (substrings[i%length] == null) {
                substrings[i%length] = String.valueOf(cipherText.charAt(i));
            }
            // otherwise append the current character to the substring
            else {
                substrings[i%length] += String.valueOf(cipherText.charAt(i));
            }
        }

        // Computes the 26 M_g values for each substring
        double[][] table = new double[length][];
        for (int i = 0; i < length; i++) {
            table[i] = mValues(substrings[i]);
        }

        // Creates the table as a string: one row per g, one column per substring
        String[] result = new String[2];
        result[0]= "\nTable of M_g values for m = " + length + "\n";
        result[0] += String.format("%4s", "g");
        for (int i = 0; i < length; i++) {
            result[0] += String.format("%10s", "y" + (i+1));
        }
        result[0] += "\n";
        for (int g = 0; g < 26; g++) {
            result[0] += String.format("%4d", g);
            for (int i = 0; i < length; i++) {
                result[0] += String.format("%10.4f", table[i][g]);
            }
            result[0] += "\n";
        }

        // Finds the largest M_g in each column and converts that g to a letter (0 = A, 1 = B, ..., 25 = Z)
        String keyword = "";
        result[0] += "\nLargest M_g in each column:\n";
        for (int i = 0; i < length; i++) {
            int best = 0;
            for (int g = 1; g < 26; g++) {
                if (table[i][g] > table[i][best]) {
                    best = g;
                }
            }
            char keyLetter = (char)('A' + best);
            keyword += keyLetter;
            result[0] += "y" + (i+1) + ": g = " + best + " (M_g = " + String.format("%.4f", table[i][best]) + ") -> " + keyLetter + "\n";
        }
        result[0] += "Keyword: " + keyword + "\n";
        result[1] = keyword;
        return result;
    }
}
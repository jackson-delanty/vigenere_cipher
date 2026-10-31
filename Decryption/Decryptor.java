package vigenere_cipher.Decryption;

import java.util.ArrayList;
import java.util.List;

public class Decryptor {
    private String cipherText;
    private double[] englishFreq = {
        .082, .015, .028, .043, .127, .022, .020, .061, .070, .002, .008, .040, .024,
        .067, .075, .019, .001, .060, .063, .091, .028, .010, .023, .001, .020, .001
    };


    public Decryptor(String cipherText) {
        this.cipherText = cipherText.toUpperCase();
    }

    public List<String> findSubStrings(int length) {
        if (length > cipherText.length()) {
            return null;
        }
        List<String> subStrings = new ArrayList<>(length);

        // Divides the original cipher text into substrings containing each ith letter
        for (int i = 0; i < cipherText.length(); i++) {
            if (subStrings.size() < length) {
                subStrings.add(String.valueOf(cipherText.charAt(i)));
            }
            else {
                String newString = subStrings.get((i%length)) + String.valueOf(cipherText.charAt(i));
                subStrings.set((i%length), newString);
            }
        }
        return subStrings;
    }

    /**
     * Takes a given set of substrings and compares the index of coincidence for each of 
     * those lengths, finding the closest one to .68
     * @param subStrings - the List of substrings
     * @return - the closest index of coincidence for the given substrings
     */
    public double closestIOC(List<String> subStrings) {
        double closest = -1;

        // Creates the final result string with the letters the substrings contain as well as their index of coincidence
        for (int i = 0; i < subStrings.size(); i++) {
            double curr = indexOfCoincidence(subStrings.get(i));
            if (closest == -1) {
                closest = curr;
            }
            else if (Math.abs(.68 - closest) > Math.abs(.68 - curr)) {
                closest = curr;
            }
        }
        return closest;
    }

    // Calculates and returns the index of coincidence for a given substring
    private double indexOfCoincidence(String substring) {
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
    
    public String decrypt(String key) {
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
    public double[] mValues(String substring) {
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
    public String[] findKeyword(List<String> subStrings) {
        int length = subStrings.size();
        // Computes the 26 M_g values for each substring
        double[][] table = new double[length][];
        for (int i = 0; i < length; i++) {
            table[i] = mValues(subStrings.get(i));
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

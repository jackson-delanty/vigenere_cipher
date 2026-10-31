package vigenere_cipher;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import vigenere_cipher.Decryption.Decryptor;

public class VigenereRunner {
    public static void main(String[] args) {
        String input = "input.txt";
        String output = "test.txt"; 
        try {
            FileReader in = new FileReader(input);
            FileWriter out = new FileWriter(output);

            String cipherText = "";
            int character;
            while ((character = in.read()) != -1) {
                cipherText += (char)character;
            }
            Decryptor decrypt = new Decryptor(cipherText);
            for (int i = 1; i < 10; i++) {
                List<String> subStrings = new ArrayList<>(i);
                subStrings = decrypt.findSubStrings(i);
                String roundedIOC = String.format("%.4f", decrypt.closestIOC(subStrings));
                out.write("The IOC for y" + i + " is: " + roundedIOC + "\n");
            }
            in.close();
            out.close();
        } catch (IOException e) {
            System.out.println("Error occured");
        }
    }
}

package experiments;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.regex.Pattern;

import util.Arguments;

public class Summarize_report {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		Arguments params = new Arguments(args); 
		read_files(params.pathFiles, params.protein);
	}
	
	public static void read_files(String path, String protein){
		String files_name;
		double [][]sumary_one = new double[22][9];
		double [][]sumary_one_count = new double[22][9];
		double [][]sumary_two = new double[22][9];
		double [][]sumary_two_count = new double[22][9];
		File files;
		File folder = new File(path);
		File[] listOfFiles = folder.listFiles();
		int pos_i, pos_j;
		for (int i = 0; i < listOfFiles.length; i++) {
			if (listOfFiles[i].isFile()) {
				files = listOfFiles[i];
				files_name = listOfFiles[i].getName();
				if(files_name.contains("summary_one_")){
					String[] positions = files_name.split("_");
					pos_i = Integer.valueOf(positions[positions.length-1]);
					String[] predictions_zero = read_lines(files).split("\n")[0].split(" ");
					String[] predictions_two = read_lines(files).split("\n")[1].split(" ");
					int cont=0;
					for(int j=0; j< predictions_zero.length; j++){
						pos_j = cont;
						if(input_number_check(predictions_zero[j])){
							sumary_one[2*pos_i][pos_j] += Double.valueOf(predictions_zero[j]);
							sumary_one_count[2*pos_i][pos_j] += 1;
						}
						cont++;
					}
					cont = 0;
					for(int j=0; j< predictions_two.length; j++){
						pos_j = cont;
						if(input_number_check(predictions_two[j])){
							sumary_one[2*pos_i+1][pos_j] = Double.valueOf(predictions_zero[j]);
							sumary_one_count[2*pos_i+1][pos_j] += 1;
						}		
						cont++;
					}
				}
				if(files_name.contains("summary_two_")){
					String[] positions = files_name.split("_");
					pos_i = Integer.valueOf(positions[positions.length-1]);
					String[] predictions_zero = read_lines(files).split("\n")[0].split(" ");
					String[] predictions_two = read_lines(files).split("\n")[1].split(" ");
					int cont=0;
					for(int j=0; j< predictions_zero.length; j++){
						pos_j = cont;
						if(input_number_check(predictions_zero[j])){
							sumary_two[2*pos_i][pos_j] += Double.valueOf(predictions_zero[j]);
							sumary_two_count[2*pos_i][pos_j] += 1;
						}
						cont++;
					}
					cont = 0;
					for(int j=0; j< predictions_two.length; j++){
						pos_j = cont;
						if(input_number_check(predictions_two[j])){
							sumary_two[2*pos_i+1][pos_j] = Double.valueOf(predictions_zero[j]);
							sumary_two_count[2*pos_i+1][pos_j] += 1;
						}		
						cont++;
					}
				}
			}
			for(int m=0; m<sumary_one.length;m++){
				for(int n=0; n<sumary_one[0].length;n++){
					if(sumary_one_count[m][n]!=0){
						sumary_one[m][n] = sumary_one[m][n] / sumary_one_count[m][n];
					}
				}
			}
			for(int m=0; m<sumary_two.length;m++){
				for(int n=0; n<sumary_two[0].length;n++){
					if(sumary_two_count[m][n]!=0){
						sumary_two[m][n] = sumary_two[m][n] / sumary_two_count[m][n];
					}
				}
			}
			
		}
	}
	
	public static boolean input_number_check(String myString){
        final String Digits     = "(\\p{Digit}+)";
        final String HexDigits  = "(\\p{XDigit}+)";
        // an exponent is 'e' or 'E' followed by an optionally 
        // signed decimal integer.
        final String Exp        = "[eE][+-]?"+Digits;
        final String fpRegex    =
            ("[\\x00-\\x20]*"+  // Optional leading "whitespace"
             "[+-]?(" + // Optional sign character
             "NaN|" +           // "NaN" string
             "Infinity|" +      // "Infinity" string

             // A decimal floating-point string representing a finite positive
             // number without a leading sign has at most five basic pieces:
             // Digits . Digits ExponentPart FloatTypeSuffix
             // 
             // Since this method allows integer-only strings as input
             // in addition to strings of floating-point literals, the
             // two sub-patterns below are simplifications of the grammar
             // productions from the Java Language Specification, 2nd 
             // edition, section 3.10.2.

             // Digits ._opt Digits_opt ExponentPart_opt FloatTypeSuffix_opt
             "((("+Digits+"(\\.)?("+Digits+"?)("+Exp+")?)|"+

             // . Digits ExponentPart_opt FloatTypeSuffix_opt
             "(\\.("+Digits+")("+Exp+")?)|"+

       // Hexadecimal strings
       "((" +
        // 0[xX] HexDigits ._opt BinaryExponent FloatTypeSuffix_opt
        "(0[xX]" + HexDigits + "(\\.)?)|" +

        // 0[xX] HexDigits_opt . HexDigits BinaryExponent FloatTypeSuffix_opt
        "(0[xX]" + HexDigits + "?(\\.)" + HexDigits + ")" +

        ")[pP][+-]?" + Digits + "))" +
             "[fFdD]?))" +
             "[\\x00-\\x20]*");// Optional trailing "whitespace"
            
        if (Pattern.matches(fpRegex, myString))
            return true; // Will not throw NumberFormatException
        else {
            return false;
        	// Perform suitable alternative action
        }
	}
	public static String read_lines(File file){
		String pred="";
		try{
			FileInputStream in = new FileInputStream(file);
			BufferedReader br = new BufferedReader(new InputStreamReader(in));
			String strLine;
			int cont = 1;
			while((strLine = br.readLine())!= null){
				if(cont%3==0){
					pred +=  strLine;
				}
				cont++;
			}
			return pred;

		}catch(Exception e){
			System.out.println(e);
		}
		return pred;

	}
}



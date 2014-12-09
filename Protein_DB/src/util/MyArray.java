package util;
import java.util.ArrayList;

public class MyArray {
	public static void printArray(int[] A) {
		for (int i = 1; i < A.length; i++) {
			System.out.print(A[i]+" ");
		}
	}

	public static void printArrayFromZero(int[] A) {
		for (int i = 0; i < A.length; i++) {
			System.out.print(A[i]+" ");
		}
	}
	
	public static void printList(ArrayList<Integer> A) {
		for (int i = 0; i < A.size(); i++) {
			System.out.print(A.get(i)+" ");
		}
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}
}

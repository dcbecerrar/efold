package evolutionary;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import com.mathworks.toolbox.javabuilder.*;

import JAVA_ML.*;

import util.Arguments;
import util.Print;

public class EvFold {
	public double[][] Data;
	public double[][] Contacts;
	public double threshold;
	
	public EvFold(int length){
		this.Data=new double[length][length];
	}
	
	public double[][] evolutionary_constraints(Arguments params){
		int cont = 0, index=0;
		int num_evFoldContacts;
		double max = Double.MIN_VALUE;
		List<Double> kthContact = new LinkedList<Double>();
		List<String> indexes = new LinkedList<String>(); 
		JAVA_ML_CLASS x = null;
		Object[] result = null;
		MWNumericArray out = null;
		
		int [] dim = new int[2];
		int [] dim_aux = new int[2];
		try {
			x = new JAVA_ML_CLASS();
			System.out.println(params.Pfam);
			System.out.println(params.Seq_ID);
			//result = x.calculate_evolutionary_constraints_mod(1,params.pathFiles+"Fasta/PF00014.fa","IVBI2_BUNMU");
			result = x.calculate_evolutionary_constraints_mod(1,params.Pfam, params.Seq_ID);
			out = (MWNumericArray)result[0];
		} catch (MWException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	

		this.Contacts = new double[params.sequence.length()+1][params.sequence.length()+1];
		Data = (double[][]) out.toDoubleArray();
		String[] algo = params.Pfam_range.split("-");
		dim[0] = Integer.valueOf(algo[0]);
		dim[1] = Integer.valueOf(algo[1]);
		
		dim_aux = out.getDimensions();
		
		if(dim_aux[1]<dim[1]-dim[0]+1){
			dim[1]=dim[0]+dim_aux[1]-2;
		}
		if(dim_aux[1]<dim[1]){
			dim[1]=dim_aux[1];
		}
		
		
		if((Math.pow(dim[1]-dim[0],2)/2)<params.evFoldBase){
			num_evFoldContacts = (int)Math.pow(dim[1]-dim[0],2)/2;
		}else{
			num_evFoldContacts = params.evFoldBase;
		}

		
		
		System.out.println(dim[0] + " ------- " + dim[1]);
		for(int i=dim[0]-1; i<dim[1]-1;i++){
			for(int j=i+1; j<=dim[1]-1;j++){
				if(kthContact.size()<num_evFoldContacts){
					index = findIndex(kthContact, Data[i][j]);
					kthContact.add(index,Data[i][j]);
					indexes.add(index,i+";"+j);
					if(j-params.evFoldIndex+2<Contacts.length){
						this.Contacts[i-params.evFoldIndex+2][j-params.evFoldIndex+2] = 1d;
						this.Contacts[j-params.evFoldIndex+2][i-params.evFoldIndex+2] = 1d;
					}
				}else{
					
					if(kthContact.get(0)<Data[i][j]){
						if(j-params.evFoldIndex+2<Contacts.length){
							index = findIndex(kthContact, Data[i][j]);
							kthContact.add(index,Data[i][j]);
							kthContact.remove(0);
							this.Contacts[i-params.evFoldIndex+2][j-params.evFoldIndex+2] = 1d;
							this.Contacts[j-params.evFoldIndex+2][i-params.evFoldIndex+2] = 1d;

							String [] ind = indexes.get(0).split(";");

							this.Contacts[Integer.valueOf(ind[0])-params.evFoldIndex+2][Integer.valueOf(ind[1])-params.evFoldIndex+2] = 0d;
							this.Contacts[Integer.valueOf(ind[1])-params.evFoldIndex+2][Integer.valueOf(ind[0])-params.evFoldIndex+2] = 0d;

							indexes.add(index,i+";"+j);
							indexes.remove(0);						
						}
					}

				}
				
			}
		}
		
		
		
		params.ev_threshold = kthContact.get(0);
		if(x != null){
			x.dispose();
		}
		result = null;
		MWNumericArray.disposeArray(out);
		printContact(params.pathFiles+"Contacts/"+params.protein, this.Contacts);
		return this.Contacts;
	}
	
	public static void printContact(String fileName, double[][] contacts){
		for(int i=0; i<contacts.length-1;i++){
			for(int j=i+1;j<contacts.length;j++){
				if(contacts[i][j]>0){
					Print.print_evfold_predictions(fileName, i + ";" + j + ";" + contacts[i][j]);
				}
			}
		}
	}
	
	public void evolutionary_constraints_aux(Arguments params){
		this.Contacts = new double[params.sequence.length()+1][params.sequence.length()+1];
		String fileName = params.pathFiles+"Contacts/"+params.protein;
		String[] splt;
		BufferedReader br = null; 
		int i,j;
		double value;
		try {
 
			String sCurrentLine;
 
			br = new BufferedReader(new FileReader(fileName));
 
			while ((sCurrentLine = br.readLine()) != null) {
				if(sCurrentLine.length()>0){
					splt=sCurrentLine.split(";");
					i=Integer.valueOf(splt[0]);
					j=Integer.valueOf(splt[1]);
					value=Double.valueOf(splt[2]);
					this.Contacts[i][j]=value;
					this.Contacts[j][i]=value;
				}

			}
 
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (br != null)br.close();
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
 
	}
	
	public static void main(String[] args) {
/*		JAVA_ML_CLASS x = null;
		Object[] result = null;
		MWNumericArray out = null;
		double[][] Data;
		int [] dim;
		
		double [] out_evol;
		String arg1 = "/Users/dcbecerrar/Documents/DAVID/MCGILL/THESIS/tFolder/calculate_evolutionary_constraints_v2.0/example_data/PF00071_v25.fa";
		String arg2 = "RASH_HUMAN";
		try {
			x = new JAVA_ML_CLASS();
			result = x.calculate_evolutionary_constraints_mod(1,arg1, arg2);
			out = (MWNumericArray)result[0];
		} catch (MWException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("Size = "+ out.numberOfDimensions() + out.numberOfElements());

		
		Data=(double[][]) out.toDoubleArray();
		dim = out.getDimensions();
		
		System.out.println(dim[0] + " ------- " + dim[1]);
		/*for(int i=0; i<dim[0];i++){
			for(int j=i; j<dim[1];j++){
				System.out.print(" " + Data[i][j]);
			}
			System.out.println();
			
		}*/

	}
	
	public static int findIndex(List<Double> kth, double value){
		for(int i =0; i< kth.size(); i++){
			if(kth.get(i)>value){
				return i;
			}
		}
		return kth.size();		
	}
	
	
	
}

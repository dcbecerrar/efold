
package dynamics;

import java.text.DecimalFormat;

public class matrix 
{
	static double roundTwoDecimals(double d)
	{
    	DecimalFormat twoDForm = new DecimalFormat("#.###");
	return Double.valueOf(twoDForm.format(d));
	}
	
	static void findCross(double[][] primary, double[][] folded)
	{
		boolean check = false;
		int i = 0;
	while((!check) && (i<primary.length)) {	
	
			
		
			if(primary[i][1]<=folded[i][1])
			{
				check = true;
				System.out.println("Unfolded state will fold into THIS state at time\t"+(i-1));
			}
		i++;	
		}
	}
	
	static void printMatrix(double[] a)
	{
		System.out.println("The matrix is \n______________________________");
		for(int i=0;i<a.length;i++)
		{
			System.out.println(a[i]);
			
		}
		System.out.println("__________________________\n");
	}
	
	public static void main(String[] args)
	{
		double[][] unfolded = new double[241][2];
		double[][] fold1 = new double[241][2];
		double[][] fold2 = new double[241][2];
		double[][] fold3 = new double[241][2];
		double[][] fold4 = new double[241][2];
		
		
		double counter = -6.0;
		for(int i=0; i<241; i++)
		{
			unfolded[i][0]=roundTwoDecimals(counter);
			counter = counter + 0.05;
		}
		
		System.out.println(unfolded[238][0]);
		
		for(int i=0;i<241;i++)
		{
			unfolded[i][1]=roundTwoDecimals(-i+50);
			fold1[i][1]=roundTwoDecimals((3/2)*i);
			fold2[i][1]=roundTwoDecimals((14.0/36.0)*i);
			fold3[i][1]=roundTwoDecimals((-0.4*(Math.pow(i,2)))+(12*i)-40);
		}
		
		
		
	
						
			findCross(unfolded,fold1);
			findCross(unfolded,fold2);
			findCross(unfolded,fold3);
			

	}
		
	}
		

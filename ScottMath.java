package calcstuff;
/**
 * 
 * This is a Math library that I wrote to take the place of the standard java math library.
 * It is mostly done for fun and may not be as efficient as the normal library.
 * Accuracy is generally to about 5-6 decimal places
 *
 */
public class ScottMath {

	
	/**
	 * 
	 * Takes a double and returns the absolute value of that double
	 */
	public static double abs(double input){
		if(input >= 0)return input;
		else return input * -1;
	}
	
	/**
	 * 
	 * takes a double and returns the factorial of that double
	 * @throws IllegalArgumentException for negitive and non-whole numbers
	 */
	public static double factorial(double input){
		if(input % 1 != 0) throw new IllegalArgumentException("you cant take factorial of a non-whole number");
		
		if (input == 1 || input == 0) return input;
		else if (input < 0) throw new IllegalArgumentException("you cant take factorial of a negitive number");
		else return input * factorial(input-1);
	}
/**
 * 
 * raises a number to an exponent
 * 
 */
	public static double pow(double base, double exp){
		if(exp%1 == 0){
			//if exp is an integer, pow is easy to find
			double toReturn = 1;
			for(int i = 0; i < abs(exp); i++ ){
				toReturn *= base;
			}
			if (exp > 0) return toReturn;
			else return 1/toReturn;
		}
		else{
			//if exp is not an integer
			double intPart = (double)((int) exp);
			double decPart = exp % 1;
			//base ^ exp = (base ^ intPart) * (base ^ decPart)
			double firstPart = pow(base, intPart);
			// base ^ decPart = e^(decPart*ln(base))
			//find ln(base) first
			double lnBase = ln(base);
			
			double expForE = decPart * lnBase;
			//calculate the e^x part now
			int nextExp = 1;
			double nextToAdd = 1;
			double exponentPart = 1;
			//e^x done here, taylor series for e^x is sum n=0 => inf of x^n/n!
			//we can use pow since it will always be a whole number
			while(abs(nextToAdd) > .0000001){
				nextToAdd = pow(expForE, nextExp)/factorial(nextExp);
				exponentPart += nextToAdd;
				nextExp++;
			}
			
			return firstPart * exponentPart;
		}
	}
	/**
	 * 
	 * Takes the natural log of a number that is greater than zero
	 * 
	 */
	public static double ln(double x){
		//between 0 and 1.9 
		//could do up to 2, but it may not converge fast enough
		if (x > 0 && x <= 1.9){
			double toReturn = 0;
			int exp = 1;
			double next = 1;
			//taylor series ln(x) = sum n=1 -> inf of ((-1)^(n+1))/n * (x-1)^n
			while(abs(next) > .0000001 ){
				next = pow((x - 1), exp)/exp;
				if (exp % 2 == 0) toReturn -= next;
				else toReturn += next;
				exp++;
			}
		 return toReturn;
		}
		else if (x <= 0) return Double.NaN;
		//greater than 1.9
		else{
			double current = x;
			double times = 1;
			// simplify by taking sqrt until you have a number under 1.9 to take the ln of
			while(current > 1.9){
				current = sqrt(current);
				times *= 2;
			}
			return times * ln(current);
		}
	}
	/**
	 * 
	 * takes the sin of a number
	 * 
	 */
	public static double sin(double input){
		double nextPart = 1;
		double runningTotal = 0;
		int exp = 1;
		while(abs(nextPart) > .0000001){
			//taylor series for sin => sum of n=0 -> inf of (-1)^n/((2n+1)!)*x^(2n+1)
			nextPart = pow(input, exp)/factorial(exp);
			if((exp-1)%4 == 0) runningTotal += nextPart;
			else runningTotal -= nextPart;
			exp+=2;
		}
		return runningTotal;
	}
	/**
	 * 
	 * takes the cos of a number
	 * 
	 */
	public static double cos(double input){
		double nextPart = 1;
		double runningTotal = 1;
		int exp = 2;
		while(abs(nextPart) > .0000001){
			//taylor series for cos => sum of n=0 -> inf of (-1)^n/((2n)!)*x^(2n)
			nextPart = pow(input, exp)/factorial(exp);
			if(exp%4 == 0) runningTotal += nextPart;
			else runningTotal -= nextPart;
			exp+=2; 
		}
		return runningTotal;
	}
	/**
	 * 
	 * takes the tan of a number
	 * 
	 */
	public static double tan(double input){
		//tan is sin/cos
		//System.out.println(sin(input) + " " + cos(input) + " mine");
		//System.out.println(Math.sin(input) + " " + Math.cos(input) + " math");
		return sin(input)/cos(input);
	}
	/**
	 * 
	 * takes the sqrt of a number
	 * 
	 */
	public static double sqrt(double input){
		if (input < 0) return Double.NaN;
		double ourGuess = 1;
		while(abs(input / ourGuess - ourGuess) > .0000001){
			ourGuess = (ourGuess + input/ourGuess)/2; 
		}
		return ourGuess;
	}
	
	
	
	
	public static void main(String[] args) {
		
		//power tests
		if(!((pow(2, 1) - Math.pow(2, 1)) <= .000001)){
			System.out.println("failure for power, test1");
		}
		if(!((pow(-2, 2)-Math.pow(-2, 2)) <= .000001)){
			System.out.println("failure for power, test2");
		}
		if(!Double.isNaN(pow(-2, 2.3))) System.out.println("failure for power, test3");
		if(!((pow(2, 2.3)-Math.pow(2, 2.3)) <= .000001)){
			System.out.println("failure for power, test4");
		}
		if(!((pow(2, -4.6)-Math.pow(2, -4.6)) <= .000001)){
			System.out.println("failure for power, test5");
		}
		//factorial tests
		if(factorial(5) != 120){
			System.out.println("failure for factorial, test1");
		}
		if(factorial(1) != 1){
			System.out.println("failure for factorial, test2");
		}
		if(factorial(0) != 0){
			System.out.println("failure for factorial, test3");
		}
		boolean factorialTest4 = false;
		boolean factorialTest5 = false;
		try{
			factorial(-5);
		}
		catch(IllegalArgumentException e){
			factorialTest4 = true;
		}
		try{
			factorial(5.5);
		}
		catch(IllegalArgumentException e){
			factorialTest5 = true;
		}
		if(!factorialTest4) System.out.println("failure for factorial, test4");
		if(!factorialTest5) System.out.println("failure for factorial, test5");
		
		//abs tests
		if(abs(5) != 5){
			System.out.println("failure for abs, test1");
		}
		if(abs(-5) != 5){
			System.out.println("failure for abs, test2");
		}
		
		//ln tests
		
		if(!(abs(ln(2)-Math.log(2)) <= .00001)){
			System.out.println("failure for ln, test1");
		}
		if(!(abs(ln(1.5)-Math.log(1.5)) <= .00001)){
			System.out.println("failure for ln, test2");
		}
		if(!(abs(ln(2.8)-Math.log(2.8)) <= .00001)){
			System.out.println("failure for ln, test3");
		}
		if(!Double.isNaN(ln(0))) System.out.println("failure for ln, test4");
		if(!Double.isNaN(ln(-5))) System.out.println("failure for ln, test5");
		
		//sqrt tests
		
		if(!(abs(sqrt(2)-Math.sqrt(2)) <= .00001)){
			System.out.println("failure for sqrt, test1");
		}
		if(!(abs(sqrt(27)-Math.sqrt(27)) <= .00001)){
			System.out.println("failure for sqrt, test2");
		}
		if(!(abs(sqrt(10.5)-Math.sqrt(10.5)) <= .00001)){
			System.out.println("failure for sqrt, test3");
		}
		if(!Double.isNaN(sqrt(-5))) System.out.println("failure for sqrt, test4");
		
		
		//sin tests
		if(!(abs(sin(2)-Math.sin(2)) <= .00001)){
			System.out.println("failure for sin, test1");
		}
		if(!(abs(sin(-3)-Math.sin(-3)) <= .00001)){
			System.out.println("failure for sin, test2");
		}
		if(!(abs(sin(0)-Math.sin(0)) <= .00001)){
			System.out.println("failure for sin, test3");
		}
		
		//cos
		if(!(abs(cos(2)-Math.cos(2)) <= .00001)){
			System.out.println("failure for cos, test1");
		}
		if(!(abs(cos(-3)-Math.cos(-3)) <= .00001)){
			System.out.println("failure for cos, test2");
		}
		if(!((cos(0)-Math.cos(0)) <= .00001)){
			System.out.println("failure for cos, test3");
		}
		
		//tan
		if(!((abs(tan(2)-Math.tan(2))) <= .0001)){
			System.out.println("failure for tan, test1");
		}
		if(!(abs(tan(-3)-Math.tan(-3)) <= .0001)){
			System.out.println("failure for tan, test2");
		}
		if(!(abs(tan(0)-Math.tan(0)) <= .0001)){
			System.out.println("failure for tan, test3");
		}
				
		
		//System.out.println(factorial(5));
		//System.out.println(sin(2));
		//System.out.println(cos(2));
		//System.out.println(tan(2));
		//System.out.println(sqrt(2));
		//System.out.println(Math.log(2));
		//System.out.println(ln(2));
	}

}

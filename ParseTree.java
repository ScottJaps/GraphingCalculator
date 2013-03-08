package calcstuff;

import java.util.Scanner;
import java.util.Stack;




public class ParseTree {
	private Scanner theInput;
	private Node root;
	
	
	//checks to see if the string is valid to make a ParseTree with 
	//this is not a catch all, but should get most of the screw ups.
	public static boolean checkString(String input){
		Scanner checking = new Scanner(input);
		//need an input, empty string is bad 
		if(!checking.hasNext()){
			checking.close();
			return false;
			}
		//holders for values to check relationships
		String prev = "";
		String current = "";
		//keeps track of left and right parens to make sure they are balanced
		int rightParens = 0;
		int leftParens = 0;
		boolean prevIsDouble = false;
		//next thing needs to be a double or vairable
		boolean needsDouble = false;
		//what we will return
		boolean toReturn = true;
		
		while(checking.hasNext() && toReturn){
			//have to do some strange things to see if there are two doubles in a row
			if (checking.hasNextDouble()){
				needsDouble = false;
				prev = current;
				current = checking.next();
				//current is a double
				if(checking.hasNext()){
					if (checking.hasNextDouble()){
						//two doubles in a row is bad
						toReturn = false;
					}
				}
				//end of the expression, ending in a double is fine and expected
				prevIsDouble = true;
			}
			else{
				//current is not a double, but may be a variable
				prev = current;
				current = checking.next();
				//make sure parens are balanced
				if (current.equals("(")){
					leftParens++;
					if(rightParens >= leftParens) toReturn = false;
				}
				else if (current.equals(")") ){ 
					rightParens++;
					if(rightParens > leftParens)  toReturn = false;
				}
				else if (current.equals("*")|| current.equals("+")||current.equals("-")||current.equals("/")||current.equals("^")) {
					needsDouble = true;
					//can't have an operator after another operator or function
					if(prev.equals("*")|| prev.equals("+")||prev.equals("-")||prev.equals("/")||prev.equals("^")||
					   prev.equals("sin")||prev.equals("sqrt")||prev.equals("cos")||prev.equals("tan")||prev.equals("")){
						toReturn = false;
					}
				}
				else if(current.equals("sin") || current.equals("cos") || current.equals("tan") || current.equals("sqrt") ||current.equals("ln")){
					needsDouble = true;
					//can't have a double before these operations
					if (prevIsDouble) toReturn = false;
					
				}
				else if(current.equals("!")){
					//can't have a non-double or "(" before factorial
					if(!prevIsDouble && !prev.equals("(") && !prev.equals("x") && !prev.equals("y") && !prev.equals("z")) toReturn = false;
				}
				//current is a variable
				else if(current.equals("x") || current.equals("y") || current.equals("z")){
					needsDouble = false;
					if(prevIsDouble) toReturn = false;
				}
				prevIsDouble = false;
			}
		}
		//make sure we are not ending with something we need a double after (somewhere)
		if(needsDouble) {
			toReturn = false;
			}
		//make sure equal number of right and left parens
		if (leftParens != rightParens) toReturn = false;
		checking.close();
		return toReturn;
	}
	
	//simplifies the tree
	public void reduceExpression(){
		root.removeMinus();
		//if reference gets changed, we need to run the loop again until it is not
		Node reference = new Node("1");
		//make sure we can't reduce anymore
		while(!root.equalTo(reference)){
			reference = new Node(root);
			//runs some functions that combine variables
			root.collectTerms();
			//the tree is sorted now
			//this combines constants
			root.simplifyTree();
			//this removes + and * nodes under other + and * nodes
			root.levelTree();
			//this simplifies rationals
			root.simplifyRationals();
		}
		root.removeExtraTerms();
		//sorts the tree into an order that looks nice
		root.miniSort();
	}
	
	//constructor for the tree
	public ParseTree(String input){
		theInput = new Scanner(prefixConvert(input));
		root = new Node();
	}
	
	//take the derivitive of the tree
	//replaces the tree with the derivitive
	public void takeDerivitive(){
		root = root.takeDerivitive();
		reduceExpression();
	}
	
	//evaluates the tree
	public double eval(){
		return root.eval();
	}
	
	//the tree in Infix (normal) notation
	public String toString(){
		return root.toString().trim();
	}
	
	//the tree in prefix notation
	public String toStringPrefix(){
		return root.toStringPrefix().trim();
	}
	
	
	//used by the prefix converter for order of operations
	private static int getPrecedence(String toCheck){
		
		
		if(toCheck.equals("!")) return 4;
		if(toCheck.equals("+") || toCheck.equals("-")) return 1;
		if(toCheck.equals("*") || toCheck.equals("/")) return 2;
		if(toCheck.equals("^")) return 3;
		if(toCheck.equals(")")) return 0;
		throw new IllegalArgumentException("Invalid operator");
	}
	
	//used by prefix converter
	private static boolean isOperator(String toCheck){
		if(toCheck.equals("+") || toCheck.equals("-") || toCheck.equals("*") || toCheck.equals("/") || 
		   toCheck.equals(")") || toCheck.equals("(") || toCheck.equals("^") || toCheck.equals("!")) return true;
		return false;
	}
	
	//used by prefix converter
	private static String reverseString(String toReverse){
		String toReturn = "";
		for(int i = 0; i < toReverse.length(); i++){
			toReturn += toReverse.charAt(toReverse.length() - i - 1);
		}
		return toReturn;
	}
	//converts a string from infix notation (normal) to prefix notation
	public static String prefixConvert(String input){
		Scanner toConvert = new Scanner(reverseString(input));
		Stack<String> operators  = new Stack<String>();
		StringBuilder toReturn = new StringBuilder();
		
		while (toConvert.hasNext()){
			String nextInput = toConvert.next();
			if(isOperator(nextInput)){
				//an operator is something that goes on the operator stack including parens
				if(operators.isEmpty()){
					operators.push(nextInput);
				}
				else if (nextInput.equals("(")){
					while(!operators.peek().equals(")")){
						toReturn.append(operators.pop() + " ");
					}
					//removes the second parens
					operators.pop();        
				}
				else if (nextInput.equals(")")){
					operators.push(nextInput);
				}
				else if(getPrecedence(nextInput) >= getPrecedence(operators.peek())){
					operators.push(nextInput);
				}
				else if(getPrecedence(nextInput) < getPrecedence(operators.peek())){
					while(getPrecedence(nextInput) < getPrecedence(operators.peek()) && operators.size() >= 2){
						toReturn.append(operators.pop() + " ");
					}
					//need to have a second if here so you don't peek when nothing is there after you finish
					if(getPrecedence(nextInput) < getPrecedence(operators.peek()) && operators.size() == 1){
						toReturn.append(operators.pop() + " ");
					}
					//put operator on stack when you are able
					operators.push(nextInput);
				}
				else {
					System.out.println("Something went wrong for " + nextInput + " in the prefix converter"); 
					toConvert.close();
					return "Error2";
				}
			}
			else{
				//not an operator, add to output string
				toReturn.append(nextInput + " ");
			}
		}
		//pop the rest of the operator stack
		while(!operators.isEmpty()){
			toReturn.append(operators.pop() + " ");
		}
		toConvert.close();
		return reverseString(toReturn.toString());
	}
	
	//our private class for making the tree
	private class Node{
		private String value;
		private Node[] children;
		
		//is this a leaf node or not
		//used to tell if we can recurse on the node or not
		private boolean isLeaf(){
			return children.length == 0;
		}
		
		//constructor when we need to make our own node and want size = 0;
		private Node(String x){
			value = x;
			children = new Node[0];
		}
		//constructor for a node with a known size
		private Node(String x, int size){
			value = x;
			children = new Node[size];
		}
		
		//constructor for copying
		private Node(Node toCopy){
			this.value = toCopy.value;
			this.children = new Node[toCopy.children.length];
			for(int i = 0; i < this.children.length; i++){
				this.children[i] = new Node(toCopy.children[i]);
			}
		}
		
		//constructor used when building the tree
		//we use the scanner from the ParseTree class
		private Node(){
			value = theInput.next();
			if(isOperator()){
				children = new Node[2];
				children[0] = new Node();
				children[1] = new Node();
			}
			else if(isFunction()){
				children = new Node[1];
				children[0] = new Node();
			}
			else{
				children = new Node[0];
			}
		}
		
		//is this node an operator
		//operators require two arguments
		private boolean isOperator(){
			if (value.equals("*") || value.equals("+") || value.equals("-") || value.equals("/") || value.equals("^")){
				return true;
			}
			return false;
		}
		//is this value a function
		//functions require one argument
		private boolean isFunction(){
			if (value.equals("sqrt") || value.equals("sin") || value.equals("cos") || value.equals("tan") || value.equals("ln") || value.equals("!")){
				return true;
			}
			return false;
		}
		//is this node a number
		private boolean isNumber(){
			return isLeaf() && !isVariable();
		}
		//is this node the number 1
		private boolean isOne(){
			if(isNumber()){
				//casting to a double can remove extra zeros, aka 01.00 -> 1.0
				double theValue = Double.parseDouble(value);
				if(theValue == 1.0){
					value = "1.0";
					return true;
				}
				else return false;
			}
			else return false;
		}
		//is this node the number 0
		private boolean isZero(){
			if(isNumber()){
				//casting to a double can remove extra zeros, aka 01.00 -> 1.0
				double theValue = Double.parseDouble(value);
				if(theValue == 0.0){
					value = "0.0";
					return true;
				}
				else return false;
			}
			else return false;
		}
		//this increases array size for our levelTree function
		private void increaseArraySize(int newSize, int prev){
			Node[] newArray = new Node[newSize];
			for(int i = 0; i < prev; i++){
				newArray[i] = children[i];
			}
			children = newArray;
		}
		
		//If there is a minus, apply the correct transformation depending on the number of children and get rid of it.
		//There should only be maximum 2 children nodes at this point.
		private void removeMinus(){
			if(value.equals("-")){
				value = "+";
				Node holder = new Node(children[1]);
				children[1] = new Node("*", 2);
				children[1].children[0] = new Node("-1");
				children[1].children[1] = holder;
			}
			
			//recurse top down
			if(!isLeaf()){
				for(int i = 0; i < children.length; i++){
					children[i].removeMinus();
				}
			}
		}
		
		//simplifies + and * nodes 
		//if a + node is below another + node, combine them
		//same with * nodes
		//will also turn a sqrt node into a ^ node
		private void levelTree(){
			if(value.equals("+")){
				//children.length may change so we need to save it 
				int toLoop = children.length;
				for(int i = 0; i < toLoop; i++){
					//can we pull a level up?
					if(children[i].value.equals("+")){
						//do we need to increase the size of the array
						increaseArraySize(children.length + children[i].children.length - 1, children.length);
						//transfer Nodes over, order does not matter for addition
						Node holder = new Node(children[i]);
						//replace the + node with its first child
						children[i] = holder.children[0];
						//add the rest of the children into the array
						for(int j = 1; j < holder.children.length; j++){
							//to loop is the old length
							children[toLoop + j - 1] = holder.children[j];
						}
						//update number of children
					}
				}
			}
			else if(value.equals("*")){
				//Separate variable so we can change the array length if needed
				int toLoop = children.length;
				for(int i = 0; i < toLoop; i++){
					//can we pull a level up?
					if(children[i].value.equals("*")){
						//increase the size of the array
						increaseArraySize(children.length + children[i].children.length - 1, children.length);
						//transfer Nodes over, order does not matter for addition
						Node holder = children[i];
						children[i] = holder.children[0];
						for(int j = 1; j < holder.children.length; j++){
							children[toLoop + j - 1] = holder.children[j];
						}
						//update number of children
					}
				}
			}
			//turn a sqrt node into a x^(.5) node
			else if(value.equals("sqrt")){
				Node holder = new Node(children[0]);
				value = "^";
				children = new Node[2];
				children[0] = holder;
				children[1] = new Node(".5");
			}
			//recursivly do this for the children, this is run top down
			for(int i = 0; i < children.length; i++){
				if(!children[i].isLeaf()) children[i].levelTree();				
			}
		}
		
		//we can simplify rational numbers around * nodes to make the expression simpler
		private void simplifyRationals(){
			//a division node will only have 2 children and always 2 children
			//if not, something went wrong.
			
			//two division nodes with one on left below the other
			//this is the right rotation
			if(value.equals("/") && children[0].value.equals("/")){
				Node leftLeft = new Node(children[0].children[0]);
				Node leftRight = new Node(children[0].children[1]);
				Node right = new Node(children[1]);
				children[0] = leftLeft;
				children[1] = new Node("*", 2);
				children[1].children[0] = leftRight;
				children[1].children[1] = right;
			}
			//two division nodes with one on right below the other
			//this is the right rotation
			if(value.equals("/") && children[1].value.equals("/")){
				Node rightLeft = new Node(children[1].children[0]);
				Node rightRight = new Node(children[1].children[1]);
				Node left = new Node(children[0]);
				children[1] = rightRight;
				children[0] = new Node("*", 2);
				children[0].children[0] = left;
				children[0].children[1] = rightLeft;
			}
			
			//zero divided by something
			if(value.equals("/") && (children[0].isZero())){
				value = "0";
				children = new Node[0];
			}
			
			//something divided by one
			if(value.equals("/") && (children[1].isOne())){
				value = children[0].value;
				children = children[0].children;
			}
			
			//see if we can reduce the rational through canceling
			if(value.equals("/")){
				if(children[0].equalTo(children[1])){
					//if top and bottom are equal, node should be 1
					value = "1";
					children = new Node[0];
				}
				//simplify (a*x)/x and (a*x^b)/x
				else if(children[0].value.equals("*") && !children[1].value.equals("*")){
					for(int i = 0; i < children[0].children.length; i++){
						if(children[0].children[i].equalTo(children[1])){
							//change to a plus node and subtract denominator, reductions get rid of mess
							value = "+";
							Node holder = new Node(children[1]);
							children[1] = new Node("*", 2);
							children[1].children[0] =  new Node("-1");
							children[1].children[1] = holder;
						}
						if(children[0].children[i].value.equals("^")){
							if (children[0].children[i].children[0].equalTo(children[1])){
								//change bottom to 1 and change the exp to a (+ exp -1) node
								Node holder = new Node(children[0].children[i].children[1]);
								children[1] = new Node("1");
								children[0].children[i].children[1] = new Node("+", 2);
								children[0].children[i].children[1].children[0] = holder;
								children[0].children[i].children[1].children[1] = new Node("-1");
							}
							//(a*x^b) / (x^c) to a * x^(b + (-1 * c )) / 1
							if(children[1].value.equals("^")){
								if(children[1].children[0].equalTo(children[0].children[i].children[0])){
									Node holder1 = new Node(children[1].children[1]);
									Node holder0 = new Node(children[0].children[i].children[1]);
									children[1] = new Node("1");
									children[0].children[i].children[1] = new Node("+", 2);
									children[0].children[i].children[1].children[0] = holder0;		
									children[0].children[i].children[1].children[1] = new Node("*", 2);
									children[0].children[i].children[1].children[1].children[0] = new Node("-1");
									children[0].children[i].children[1].children[1].children[1] = holder1;
									
								}
							}
						}
					}
					//simplify (a*x)/b
					if(children[1].isNumber() && children[0].children[0].isNumber()){
						double newValue = Double.parseDouble(children[0].children[0].value) / Double.parseDouble(children[1].value);
						children[1] = new Node("1");
						children[0].children[0] = new Node(Double.toString(newValue));
					}
				}
				//simplify x/(a*x) and x/(a*x^b) and (a*x)/(b*x^c)
				else if(children[1].value.equals("*") && !children[0].value.equals("*")){
					for(int i = 0; i < children[1].children.length; i++){
						if(children[1].children[i].equalTo(children[0])){
							//change left to 1, and change right to a + node with the old right and ( * -1 left )
							Node holder0 = new Node(children[0]);
							Node holder1 = new Node(children[1]); 
							children[0] = new Node("1");
							children[1] = new Node("+", 2);
							children[1].children[0] = holder1;
							children[1].children[1] = new Node("*", 2);
							children[1].children[1].children[0] = new Node("-1");
							children[1].children[1].children[1] = holder0;							
						}
						if(children[1].children[i].value.equals("^")){
							if (children[1].children[i].children[0].equalTo(children[0])){
								//change top to 1 and change exponent to a (+ exp -1) node
								Node holder = new Node(children[1].children[i].children[1]);
								children[0] = new Node("1");
								children[1].children[i].children[1] = new Node("+", 2);
								children[1].children[i].children[1].children[0] = holder;
								children[1].children[i].children[1].children[1] = new Node("-1");
							}
							//(x^a) / (b*x^c) to x^(a + (-1 * c )) / b
							if(children[0].value.equals("^")){
								if(children[0].children[0].equalTo(children[1].children[i].children[0])){
									Node holder0 = new Node(children[0].children[1]);
									Node holder1 = new Node(children[1].children[i].children[1]);
									children[1].children[i] = new Node("1");
									children[0].children[1] = new Node("+", 2);
									children[0].children[1].children[0] = holder0;
									children[0].children[1].children[1] = new Node("*", 2);
									children[0].children[1].children[1].children[0] = new Node("-1");
									children[0].children[1].children[1].children[1] = holder1;
								}
							}
						}
					}
					//simplify a/(b * x)
					if(children[0].isNumber() && children[1].children[0].isNumber() && !children[0].isOne() 
					&& !children[0].value.equals("-1") && !children[0].value.equals("-1.0")){
						double newValue = Double.parseDouble(children[0].value) / Double.parseDouble(children[1].children[0].value);
						children[0] = new Node(Double.toString(newValue));
						children[1].children[0].value = "1";
					}
				}
				//simplify things of the form a*x^b/(c*x^d) or a*x/b*x and things like that
				else if(children[0].value.equals("*") && children[1].value.equals("*")){
					// i will be used to iterate the left children, j will iterate the right children
					for(int i = 0; i < children[0].children.length; i++){
						for (int j = 0; j < children[1].children.length; j++){
							//the two nodes are equal, no exponents, but not 1
							if(children[0].children[i].equalTo(children[1].children[j]) && !children[0].children[i].isOne()){
								//set both to 1 since (a*x)/(b*x) = a/b
								children[0].children[i] = new Node("1");
								children[1].children[j] = new Node("1");
							}
							//both nodes are doubles,  (10*x)/(5*x^2) becomes (2*x)/(1*x^2)
							else if(children[0].children[i].isNumber() && children[1].children[j].isNumber()){
								//change the left one to an evaluated node and the right one to 1
								double newValue = Double.parseDouble(children[0].children[i].value) / Double.parseDouble(children[1].children[j].value);
								children[0].children[i] = new Node(Double.toString(newValue));
								children[1].children[j] = new Node("1");
							}
							//left is power node, right is not (2*x^2)/(1*x) becomes (2*x^(2 + -1)/(1*1)
							if(children[0].children[i].value.equals("^") && !children[1].children[j].value.equals("^")){
								if(children[0].children[i].children[0].equalTo(children[1].children[j])){
									children[1].children[j] = new Node("1");
									Node holder =  new Node(children[0].children[i].children[1]);
									children[0].children[i].children[1] = new Node("+", 2);
									children[0].children[i].children[1].children[0] = holder;
									children[0].children[i].children[1].children[1] = new Node("-1");
								}
							}
							//right is power node, left is not (3*x)/(1*x^3) becomes (3*1)/(1*x^(3 + -1))
							else if(!children[0].children[i].value.equals("^") && children[1].children[j].value.equals("^")){
								if(children[1].children[j].children[0].equalTo(children[0].children[i])){
									children[0].children[i] = new Node("1");
									Node holder =  new Node(children[1].children[i].children[1]);
									children[1].children[i].children[1] = new Node("+", 2);
									children[1].children[i].children[1].children[0] = holder;
									children[1].children[i].children[1].children[1] = new Node("-1");
								}
							}
							//both are power nodes a*x^b/(1*x^d) becomes a*x^(b + ( -1 * d))/(1*1)
							else if(children[0].children[i].value.equals("^") && children[1].children[j].value.equals("^")){
								if(children[0].children[i].children[0].equalTo(children[1].children[j].children[0])){
									Node holder0 = new Node(children[0].children[i].children[1]);
									Node holder1 = new Node(children[1].children[j].children[1]);
									children[1].children[j] = new Node("1");
									children[0].children[i].children[1] = new Node("+", 2);
									children[0].children[i].children[1].children[0] = holder0;
									children[0].children[i].children[1].children[1] = new Node("*", 2);
									children[0].children[i].children[1].children[1].children[0] = new Node("-1");
									children[0].children[i].children[1].children[1].children[1] = holder1;
								}
							}
						}
					}
				}
				//simplify x^c/x
				else if(children[0].value.equals("^") && !children[1].value.equals("^")){
					if(children[0].children[0].value.equals(children[1].value)){
						//change to x^(c + -1) / 1
						Node holder = new Node(children[0].children[1]);
						children[1] = new Node("1");
						children[0].children[1] = new Node("+", 2);
						children[0].children[1].children[0] = holder;
						children[0].children[1].children[1] = new Node("-1");
					}
				}
				//simplify x/x^c
				else if(children[1].value.equals("^") && !children[0].value.equals("^")){
					if(children[1].children[0].value.equals(children[0].value)){
						//change to 1/x^(c + -1)
						Node holder = new Node(children[1].children[1]);
						children[0] = new Node("1");
						children[1].children[1] = new Node("+", 2);
						children[1].children[1].children[0] = holder;
						children[1].children[1].children[1] = new Node("-1");
					}
				}
				//simplify x^a/x^b
				else if(children[0].value.equals("^") && children[1].value.equals("^")){
					if(children[0].children[0].equalTo(children[1].children[0])){
						Node holder0 = new Node(children[0].children[1]);
						Node holder1 = new Node(children[1].children[1]);
						Node holder2 = new Node(children[0].children[0]);
						value = "^";
						children[0] = holder2;
						children[1] = new Node("+", 2);
						children[1].children[0] = holder0;
						children[1].children[1] = new Node("*", 2);
						children[1].children[1].children[0] = new Node("-1");
						children[1].children[1].children[1] = holder1;
					}
				}
				
			}
			
			//multiplication node over a division node
			//need to check all subnodes left to right
			if(value.equals("*")){
				for(int i = 0; i < children.length; i++){
					//we can reduce a node
					if(children[i].value.equals("/")){
						Node left = new Node(children[i].children[0]);
						Node right = new Node(children[i].children[1]);
						children[i] = left;
						Node[] theChildren = children;
						value = "/";
						children = new Node[2];
						children[0] = new Node("*");
						children[0].children = theChildren;
						children[1] = right;
						//end the loop since you can only do this operation once per * node
						i = 2;
					}
				}
			}
			//Recursively check all children
			for(int i = 0; i < children.length; i++){
				if(!children[i].isLeaf()){ 
					children[i].simplifyRationals(); 
				}
			}
		}
		
		//are two nodes equal
		private boolean equalTo(Node that){
			if(!this.value.equals(that.value)) return false;
			if( this.children.length != that.children.length) return false;
			for (int i = 0; i < this.children.length; i++){
				if(!this.children[i].equalTo(that.children[i])) return false;
			}
			return true;
		}
		
		//x, y, and z are variables
		private boolean isVariable(){
			return value.equals("x")||value.equals("y")||value.equals("z");
		}
		
		//this function is used by colledtTerms to add exponents and coefficients to variables
		//add coef to variables under a + node and exponents if under a * node
		//need to do + first to avoid silly things.
		private void addExtraTerms(){			
			
			//going bottom up
			for(int i = 0; i < children.length; i++){
				if(!children[i].isLeaf()){ 
					children[i].addExtraTerms(); 
				}
			}
		
			if (value.equals("+")){
				for(int i = 0; i < children.length; i++){
					if(children[i].isVariable()){
						//replace with a * node with a coef of 1
						Node holder = new Node(children[i]);
						children[i] = new Node("*", 2);
						children[i].children[1] = holder;
						children[i].children[0] = new Node("1");
						
					}			
				}
			}
			if (value.equals("*")){
				for(int i = 0; i < children.length; i++){
					if(children[i].isVariable()){
						//replace with a * node with a coef of 1
						Node holder = new Node(children[i]);
						children[i] = new Node("^", 2);
						children[i].children[0] = holder;
						children[i].children[1] = new Node("1");
						
					}			
				}
			}
		}
		
		//removes some basic terms like 1's and 0's to make the tree simpler
		//removes 1's with a * node and 1's and 0's with ^ nodes
		private void removeExtraTerms(){
			
			//recusivly running this bottom up
			for(int i = 0; i < children.length; i++){
				if(!children[i].isLeaf()){ 
					children[i].removeExtraTerms(); 
				}
			}
			
			if(!isLeaf()){
				if(canSimplify()){
					simplifyTree();
				}
				if(value.equals("*")){
					int numDeleted = 0;
					for(int i = 0; i < children.length; i++){
						if(children[i].isOne()){
							//remove this node
							children[i].value = "deleteMe6";
							numDeleted++;
						}
						else if(children[i].isZero()){
							//we need to set this node to zero if it has a child that is zero
							value = "0";
							numDeleted = 0;
							children = new Node[0];
						}
						
					}
					if(numDeleted > 0){
						Node[] newArray = new Node[children.length-numDeleted];
						int m = 0;
						for(int j = 0; j < children.length;j++){
							if(!children[j].value.equals("deleteMe6")){
								newArray[m] = children[j];
								m++;
							}
						}
						children = newArray;
					}
					if(children.length == 1){
						value = children[0].value;
						children = children[0].children;
					}
				}
				//add 0^0 support
				if(value.equals("^")){
					if(children[0].isZero()){
						value = "0";
					}
					else if(children[0].isOne()){
						value = "1";
					}
					else if(children[1].isZero()){
						value = "1";
					}
					else if(children[1].isOne()){
						value = children[0].value;
						children = children[0].children;
					}
				}
				if(value.equals("+")){
					int numDeleted = 0;
					for(int i = 0; i < children.length; i++){
						if(children[i].isZero()){
							//remove this node
							children[i].value = "deleteMe5";
							numDeleted++;
						}
					}
					if(numDeleted > 0){
						Node[] newArray = new Node[children.length-numDeleted];
						int m=0;
						for(int j = 0; j < children.length; j++){
							if(!children[j].value.equals("deleteMe5")){
								newArray[m] = children[j];
								m++;
							}
						}
						children = newArray;
					}
					if(children.length == 1){
						value = children[0].value;
						children = children[0].children;
					}
				}
			}
		}
		
		//this collects variables together if able
		private void collectAllTerms(){
			//check bottom up
			
			if(!isLeaf()){
				for(int i = 0; i < children.length; i++){
					if(!children[i].isLeaf()){ 
						children[i].collectAllTerms(); 
					}
				}
			}
			if(value.equals("+")){
				for(int i = 0; i < children.length;i++){
					for(int j = i + 1; j < children.length; j++){
						if(children[i].value.equals("*") && children[j].value.equals("*") 
						&& children[i].children.length == 2 && children[j].children.length == 2){
							if (children[i].children[1].value.equals(children[j].children[1].value) && children[i].children[1].isVariable()){
								//we can combine two nodes and get rid of one
								Node holder = new Node(children[i].children[0]);
								children[i].children[0] = new Node("+", 2);
								children[i].children[0].children[0] = holder;
								children[i].children[0].children[1] = children[j].children[0];
								children[j].value = "deleteMe4";
							
								//need to get rid of duplicate
								Node[] newArray = new Node[children.length-1];
								//run two ints down the array and copy over if it is not the one we are deleting
								int m = 0;
								for(int k = 0; k < children.length; k++){
									if(!children[k].value.equals("deleteMe4")){
										newArray[m] = new Node(children[k]);
										m++;
									}
								}
								children = newArray;
								//restart check 
								i = 0;
								j = 1;
							}
						}
					}
				}
			}
			if(value.equals("*")){
				for(int i = 0; i < children.length;i++){
					for(int j = i + 1; j < children.length; j++){
						if(children[i].value.equals("^") && children[j].value.equals("^")){
							if (children[i].children[0].value.equals(children[j].children[0].value)){
								//we can combine two nodes and get rid of one
								Node holder = new Node(children[i].children[1]);
								children[i].children[1] = new Node("+", 2);
								children[i].children[1].children[0] = holder;
								children[i].children[1].children[1] = new Node(children[j].children[1]);
								children[j].value = "deleteMe3";
							
								//need to get rid of duplicate
								Node[] newArray = new Node[children.length - 1];
								int m = 0;
								for(int k = 0; k < children.length; k++){
									if(!children[k].value.equals("deleteMe3")){
										newArray[m] = new Node(children[k]);
										m++;
									}
								}
								children = newArray;
								//restart check 
								i = 0;
								j = 1;
							}
						}
					}
				}
			}
			if(children.length == 1 && !isFunction()){
				//may be able to get rid of a level if the reduces we have done make it unnecessary
				value = children[0].value;
				children = children[0].children;
			}
		}
		
		//makes sure that if there are only 2 children for a + or * node, the var is on the right/second
		//this makes the output look better and keeps it consistent
		private void miniSort(){
			//sort bottom up
			if(!isLeaf()){
				for(int i = 0; i < children.length; i++){
					if(!children[i].isLeaf()){ 
						children[i].miniSort(); 
					}
				}
			}
			if(children.length >= 2 && (value.equals("+") || value.equals("*"))){
				//not using the most efficient sort, but n should be very small
				//bubble sort
				for (int i = 0; i < children.length; i++){
					for(int j = 1 ; j < children.length; j++){
						if (children[i].getPriority() > children[j].getPriority()){
							//swap them
							Node holder = new Node(children[j]);
							children [j] = children[i];
							children[i] = holder;
						}
					}
				}
			}
		}
		
		//priority for sorting
		private int getPriority(){
			if (isOperator()) return 3;
			if (isVariable()) return 2;
			if (value.equals("sin")) return 5;
			if (value.equals("cos")) return 6;
			if (value.equals("tan")) return 7;
			if (value.equals("sqrt")) return 4;
			if (value.equals("ln")) return 8;
			return 1;
		}
		//special case where you have 5*x+x or something like it
		//this is only written for one variable
		private void reducePlusOverTimes(){
			//need to add extra coefs to vars so 5*x+x becomes 5*x+1*x
			//since we only have 1 var, there will only be 2 children of * and + nodes at this point in reduction
			//nested if loops to prevent errors
			if (value.equals("+")){
				if (children[0].value.equals("*") && children[1].value.equals("*")){
					if(children[0].children[1].isVariable() && children[1].children[1].isVariable()){
						//copy the sum over and turn this node into a * node
						double newValue = Double.parseDouble(children[0].children[0].value) + Double.parseDouble(children[1].children[0].value);
						children[1] = new Node(children[0].children[1].value);
						children[0] = new Node(Double.toString(newValue));
						value = "*";
					}
				}
			}
			//recurse top down
			for(int i = 0; i < children.length; i++){
				if(!children[i].isLeaf()){ 
					children[i].reducePlusOverTimes(); 
				}
			}
		}
		
		//need to add some 1's in for PlusOverTimes to work correctly
		//only need the ones to make a + node
		private void addExtraOnesForPlusOverTimes(){	
			//going bottom up
			for(int i = 0; i < children.length; i++){
				if(!children[i].isLeaf()){ 
					children[i].addExtraOnesForPlusOverTimes(); 
				}
			}
			if (value.equals("+")){
				for(int i = 0; i < children.length; i++){
					if(children[i].isVariable()){
						//replace with a * node with a coef of 1
						Node holder = new Node(children[i]);
						children[i] = new Node("*", 2);
						children[i].children[1] = holder;
						children[i].children[0] = new Node("1");
					}			
				}
			}
		}
		
		//runs the functions that allow us to collect variables together and make it look nice
		private void collectTerms(){
			//need to remove extra ones and zeros to prevent stuff from screwing up and then add them back selectivly to reduce variables
			removeExtraTerms();
			//this will not add back all the extra 1's that were removed, just the ones we would need
			addExtraTerms();
			collectAllTerms();
			//remove extra stuff to make it look nice
			removeExtraTerms();
			//puts stuff in a nice order
			miniSort();
			//a special case reduction that does not get done in collectAllTerms
			addExtraOnesForPlusOverTimes();
			reducePlusOverTimes();
			removeExtraTerms();
		}
		
		//can we simplify this node into a leaf node (evaluate it)
		private boolean canSimplify(){
			if(isLeaf()) return false;
			for(int i = 0; i < children.length; i++){
				if(!children[i].isLeaf() || children[i].isVariable()) return false;
			}
			return true;
		}
		
		//evaluates constants in the tree if able
		private void simplifyTree(){
			
			//Recursively run this bottom up
			if(!isLeaf()){
				for(int i = 0; i < children.length; i++){
					children[i].simplifyTree();
				}
			}
			//evaluates this node
			if(canSimplify()){
				value = Double.toString(eval());
				children = new Node[0];
			}
			//combine some children if able when the whole node cannot be evaluated
			//should assume something down there is a variable
			else if(children.length > 2 && value.equals("+")){
				double holder = 0;
				//if we have multiple doubles, we put them in numToCombine
				int numToCombine = -1;
				int combinedNumber = 0;
				for(int i = 0; i < children.length; i++){
					if(isNumber() && numToCombine != -1){
						holder += Double.parseDouble(children[i].value);
						children[i].value = "DeleteMe2";
						children[numToCombine].value = Double.toString(holder);
						combinedNumber++;
					}
					//only run first time a node we might be able to eval is hit
					else if(isNumber()){
						//save where we want to put doubles
						if(numToCombine == -1) numToCombine = i;
						holder = Double.parseDouble(children[i].value);
					}
				}
				//if we need to remove a node
				if(combinedNumber != 0){
					Node[] newArray = new Node[children.length-combinedNumber];
					int j = 0;
					for (int i = 0; i < children.length; i++){
						if(!children[i].value.equals("DeleteMe2")){
							newArray[j] = children[i];
							j++;
						}
					}
					children = newArray;
				}
			}
			//try to combine multiple children under a * node
			//something below this node is a variable
			else if(children.length > 2 && value.equals("*")){
				double holder = 1;
				int numToCombine = -1;
				int combinedNumber = 0;
				for(int i = 0;i < children.length; i++){
					if(children[i].isNumber() && numToCombine != -1){
						holder *= Double.parseDouble(children[i].value);
						children[i].value = "DeleteMe1";
						children[numToCombine].value = Double.toString(holder);
						combinedNumber++;
					}
					//only run first time a node we might be able to eval is hit
					else if(children[i].isNumber()){
						if(numToCombine == -1) numToCombine = i;
						holder = Double.parseDouble(children[i].value);
					}
				}
				//if we need to remove a node
				if(combinedNumber != 0){
					Node[] newArray = new Node[children.length - combinedNumber];
					int j = 0;
					for (int i = 0; i < children.length; i++){
						if(!children[i].value.equals("DeleteMe1")){
							newArray[j] = children[i];
							j++;
						}
					}
					children = newArray;
				}
			}
		}
		
		//evaluate our tree
	    public double eval() {
	    	// + and * can have more than 2 children, others should not
	    	double toReturn;
	    	if (value.equals("+")) {
	    		toReturn = 0;
	        	for(int i = 0; i < children.length; i++){
	        		toReturn = toReturn + children[i].eval();
	        	}
	        }
	        else if (value.equals("*")){ 
	        	toReturn = 1;
	        	for(int i = 0; i < children.length; i++){
	        		toReturn = toReturn * children[i].eval();
	        	}
	        }
	        else if (value.equals("/")) return children[0].eval() / children[1].eval();
	        else if (value.equals("^")) return ScottMath.pow(children[0].eval(), children[1].eval());
	        else if (value.equals("sin")) return ScottMath.sin(children[0].eval());
	        else if (value.equals("cos")) return ScottMath.cos(children[0].eval());
	        else if (value.equals("tan")) return ScottMath.tan(children[0].eval());
	        else if (value.equals("sqrt")) return ScottMath.sqrt(children[0].eval());
	        else if (value.equals("ln")) return ScottMath.ln(children[0].eval());
	        else if (value.equals("!")) return ScottMath.factorial(children[0].eval());
	        else                        return Double.parseDouble(value);
	    	return toReturn;
	    }
	    
	    //toString function in infix notation
	    public String toString(){
	    	StringBuilder toReturn = new StringBuilder();
	    	if (!isLeaf()){
	    		//operator with 2 or more operands such as + and /
	    		if(children.length > 1){
	    			//put the current value between each child, but not at the end
	    			toReturn.append(" ( ");
	    			for(int i = 0; i < children.length - 1; i++){
		    			toReturn.append(children[i] + " ");
		    			toReturn.append(value + " ");
		    		}
	    			toReturn.append(children[children.length - 1] + " ");
	    			toReturn.append(" ) ");
	    		}
	    		//left asc operation like !
	    		else if(value.equals("!")){
	    			toReturn.append(" ( ");
	    			toReturn.append(children[0] + " ");
	    			toReturn.append(" ) " + " ! ");
	    		}
	    		//right asc operation line sin, ln and other functions
	    		else{
	    			//only one child
	    			toReturn.append(value + " ( ");
	    			toReturn.append(children[0] + " ");
	    			toReturn.append(" ) ");
	    		}
	    		return toReturn.toString();
	    	}
	        else
	            return value;
	    }
	    
	    
	    //returns the tree in prefix notation
	    //this would mostly be used in debugging
	    public String toStringPrefix() {
	    	StringBuilder toReturn = new StringBuilder();
	    	if (!isLeaf()){
	    		toReturn.append(value + " ");
	    		for(int i = 0; i < children.length; i++){
	    			toReturn.append(children[i].toStringPrefix() + " ");
	    		}
	    		return toReturn.toString();
	    	}
	        else
	            return value;
	    }
	    
	    //this function takes the derivative of the tree
	    private Node takeDerivitive(){
	    	//Derivative of constant is 0
	    	if(isNumber()) return new Node("0");
	    	//Derivative of a variable is 1
	    	else if(isVariable()) return new Node("1"); 
	    	else if(value.equals("+")){
	    		Node toReturn = new Node(this);
	    		toReturn.children[0] = children[0].takeDerivitive();
	    		toReturn.children[1] = children[1].takeDerivitive();
	    		return toReturn;
	    	}
	    	else if(value.equals("*")){
	    		Node toReturn = new Node(this);
	    		toReturn.value = "+";
	    		Node holder0 = new Node(children[0]);
	    		Node holder1 = new Node(children[1]);
	    		toReturn.children[0] = new Node("*", 2);
	    		toReturn.children[1] = new Node("*", 2);
	    		toReturn.children[0].children[0] = holder0;
	    		toReturn.children[0].children[1] = holder1.takeDerivitive();
	    		toReturn.children[1].children[0] = holder1;
	    		toReturn.children[1].children[1] = holder0.takeDerivitive();
	    		return toReturn;
	    	}
	    	else if(value.equals("/")){
	    		//if the denominator is not a constant
	    		if (!children[1].isNumber()) {
	    			Node toReturn = new Node(this);
	    			Node holder0 = new Node(children[0]);
	    			Node holder1 = new Node(children[1]);
	    		
	    			toReturn.children = new Node[2];
	    			toReturn.children[0] = new Node("+", 2);
	    			toReturn.children[1] = new Node("*", 2);

	    			//ho^2
	    			toReturn.children[1].children[0] = new Node(holder1);
	    			toReturn.children[1].children[1] = new Node(holder1);
	    			
	    			toReturn.children[0].children[0] = new Node("*", 2);
	    			toReturn.children[0].children[1] = new Node("*", 2);
	    			//ho
	    			toReturn.children[0].children[0].children[0] = new Node(holder1);
	    			//di hi
	    			toReturn.children[0].children[0].children[1] = new Node(holder0.takeDerivitive());
	    		
	    			toReturn.children[0].children[1].children[0] = new Node("*", 2);
	    			//less
	    			toReturn.children[0].children[1].children[0].children[0] = new Node("-1");
	    			//hi
	    			toReturn.children[0].children[1].children[0].children[1] = new Node(holder0);
	    			//di ho
	    			toReturn.children[0].children[1].children[1] = new Node(holder1.takeDerivitive());
	    			
	    			return toReturn;
	    		}
	    		else{
	    			//denominator is a constant
	    			Node toReturn = new Node(this);
	    			toReturn.children[0] = toReturn.children[0].takeDerivitive();
	    			return toReturn;
	    		}
	    	}
	    	else if(value.equals("^")){
	    		Node toReturn = new Node(this);
	    		toReturn.value = "*";
	    		Node holder0 = new Node(children[0]);
	    		Node holder1 = new Node(children[1]);
	    		//dont need chain rule with this definition
	    		
	    		toReturn.children[0] = new Node("^", 2);
	    		
	    		toReturn.children[0].children[0] = holder0;
	    		toReturn.children[0].children[1] = holder1;
	    		
	    		Node toDer = new Node("*", 2);
	    		toDer.children[0] = holder1;
	    		toDer.children[1] = new Node("ln", 1);
	    		toDer.children[1].children[0] = holder0;
	    		
	    		toReturn.children[1] = toDer.takeDerivitive(); 

	    		return toReturn;
	    	}
	    	else if(value.equals("ln")){
	    		Node toReturn = new Node(this);
	    		toReturn.value = "*";
	    		Node holder = new Node(children[0]);
	    		toReturn.children = new Node[2];
	    		toReturn.children[0] = new Node("/", 2);
	    		toReturn.children[0].children[0] = new Node("1");
	    		toReturn.children[0].children[1] = holder;
	    		//chain rule
	    		toReturn.children[1] = holder.takeDerivitive();
	    		return toReturn;
	    	}
	    	else if(value.equals("sin")){
	    		Node toReturn = new Node("*", 2);
	    		//chain rule
	    		toReturn.children[0] = new Node(children[0].takeDerivitive());
	    		//non-chain rule
	    		toReturn.children[1] = new Node("cos", 1);
	    		toReturn.children[1].children[0] = this.children[0];
	    		return toReturn;
	    	}
	    	else if(value.equals("cos")){
	    		Node toReturn = new Node("*", 2);
	    		toReturn.children[0] = new Node(children[0].takeDerivitive());
	    		toReturn.children[1] = new Node("*", 2);
	    		toReturn.children[1].children[0] = new Node("-1");
	    		toReturn.children[1].children[1] = new Node("sin", 1);
	    		toReturn.children[1].children[1].children[0] = children[0];	    		
	    		return toReturn;
	    	}
	    	else if(value.equals("tan")){
	    		Node toReturn = new Node("*", 2);
	    		toReturn.children[1] = new Node(children[0].takeDerivitive());
	    		toReturn.children[0] = new Node("/", 2);
	    		toReturn.children[0].children[0] = new Node("1");
	    		toReturn.children[0].children[1] = new Node("^", 2);
	    		toReturn.children[0].children[1].children[0] = new Node("cos", 1);
	    		toReturn.children[0].children[1].children[1] = new Node("2");
	    		toReturn.children[0].children[1].children[0].children[0] = new Node(children[0]);
	    		return toReturn;
	    	}
	    	else return new Node("error, cant take derivitive");
	    	
	    }
	}
	
	public static void main(String[] args) {		
		ParseTree tester = new ParseTree("-0");
		System.out.println(tester.root.isZero());
		//ParseTree tester = new ParseTree(" ");
		tester.reduceExpression();
		System.out.println(tester.toString());
		ParseTree tester3 = new ParseTree(tester.toString().replace("x", "5"));
		System.out.println(tester.toString());
		System.out.println(tester3.toString());
		tester3.reduceExpression();
		System.out.println(tester3.toString());
		//System.out.println(tester.toStringPrefix());
		

	
		System.out.println("----------Starting Tests--------");
		//testing checkString
		if(checkString("1  1")){
			System.out.println("failure for checkString, test1");
		}
		if(checkString("*  1")){
			System.out.println("failure for checkString, test2");
		}
		if(checkString("1  *")){
			System.out.println("failure for checkString, test3");
		}
		if(checkString("( 1  * 1")){
			System.out.println("failure for checkString, test4");
		}
		if(checkString("1  * 1 )")){
			System.out.println("failure for checkString, test5");
		}
		if(checkString("1  sin ( 5 )")){
			System.out.println("failure for checkString, test6");
		}
		if(!checkString("1 *  sin ( 5 )")){
			System.out.println("failure for checkString, test7");
		}
		if(!checkString("1 *  sin ( 5 * x ( ) )")){
			System.out.println("failure for checkString, test8");
		}
		if(!checkString("x !")){
			System.out.println("failure for checkString, test9");
		}
		//testing prefix convert
		if(!prefixConvert("3 * 3 * 3").equals(" * * 3 3 3")){
			System.out.println("failure for prefixConvert, test1");
		}
		if(!prefixConvert("1 + 2 * 3").equals(" + 1 * 2 3")){
			System.out.println("failure for prefixConvert, test2");
		}
		if(!prefixConvert("1 * 2 + 3").equals(" + * 1 2 3")){
			System.out.println("failure for prefixConvert, test3");
		}
		if(!prefixConvert("1 ^ 2 + 3").equals(" + ^ 1 2 3")){
			System.out.println("failure for prefixConvert, test4");
		}
		//tests for removeMinus
		ParseTree test1 = new ParseTree("3 - 2");
		test1.root.removeMinus();
		if(!test1.toString().equals("( 3 +  ( -1 * 2  )   )")){
			System.out.println("failure for removeMinus, test1");
		}
		//test for levelTree
		ParseTree test2 = new ParseTree("3 + 1 + 2");
		test2.root.levelTree();
		if(!test2.toStringPrefix().equals("+ 3 2 1")){
			System.out.println("failure for levelTree, test1");
		}
		ParseTree test3 = new ParseTree("3 * 1 * 2");
		test3.root.levelTree();
		if(!test3.toStringPrefix().equals("* 3 2 1")){
			System.out.println("failure for levelTree, test2");
		}
		ParseTree test38 = new ParseTree("x * x * x");
		test38.root.levelTree();
		if(!test38.toStringPrefix().equals("* x x x")){
			System.out.println("failure for levelTree, test3");
		}
		ParseTree test39 = new ParseTree("x + x + x");
		test39.root.levelTree();
		if(!test39.toStringPrefix().equals("+ x x x")){
			System.out.println("failure for levelTree, test4");
		}
		//tests for collect terms
		ParseTree test40 = new ParseTree("x * x * x");
		test40.reduceExpression();
		if(!test40.toStringPrefix().equals("^ x 3.0")){
			System.out.println("failure for reduce, test1");
		}
		ParseTree test41 = new ParseTree("x + x + x");
		test41.reduceExpression();
		if(!test41.toStringPrefix().equals("* 3.0 x")){
			System.out.println("failure for reduce, test2");
		}
		//tests for simpllify rational
		//2nd divided on the left
		ParseTree test4 = new ParseTree("12 / 5 / 3");
		test4.root.simplifyRationals();
		if(!test4.toStringPrefix().equals("/ 2.4 * 1 3")){
			System.out.println("failure for simplify rational, test1");
		}
		//2nd divided on the right
		ParseTree test5 = new ParseTree(" ( 5 * 3 )  / ( 12 / 3 )");
		test5.root.simplifyRationals();
		if(!test5.toStringPrefix().equals("/ * * 5 3  12  3")){
			System.out.println("failure for simplify rational, test2");
		}
		//zero divided by anything
		ParseTree test6 = new ParseTree(" 0 / 5");
		test6.root.simplifyRationals();
		if(!test6.toStringPrefix().equals("0")){
			System.out.println("failure for simplify rational, test3");
		}
		//anything divided by 1
		ParseTree test7 = new ParseTree(" 5 / 1");
		test7.root.simplifyRationals();
		if(!test7.toStringPrefix().equals("5")){
			System.out.println("failure for simplify rational, test4");
		}
		//reduce (a*x)/x 
		ParseTree test8 = new ParseTree(" ( 5 * x ) / x");
		test8.reduceExpression();
		if(!test8.toStringPrefix().equals("* 4.0 x")){
			System.out.println("failure for simplify rational, test5");
		}
		//reduce x/(a*x)
		ParseTree test9 = new ParseTree(" x / ( 5 * x )");
		test9.reduceExpression();
		if(!test9.toStringPrefix().equals("/ 1.0 * 4.0 x")){
			System.out.println("failure for simplify rational, test6");
		}
		//reduce a/(b*x)
		ParseTree test31 = new ParseTree(" 10 / ( 5 * x )");
		test31.reduceExpression();
		if(!test31.toStringPrefix().equals("/ 2.0 x")){
			System.out.println("failure for simplify rational, test22");
		}
		
		//reduce x/(x^a)
		ParseTree test10 = new ParseTree("  x /   ( x ^ 5 )");
		test10.reduceExpression();
		if(!test10.toStringPrefix().equals("/ 1 ^ x 4.0")){
			System.out.println("failure for simplify rational, test7");
		}
		//reduce (x^a)/x
		ParseTree test11 = new ParseTree("   ( x ^ 5 ) / x");
		test11.reduceExpression();
		if(!test11.toStringPrefix().equals("^ x 4.0")){
			System.out.println("failure for simplify rational, test8");
		}
		//reduce (x^a)/(x^b)
		ParseTree test12 = new ParseTree(" ( x ^ 7 ) /   ( x ^ 5 )");
		test12.reduceExpression();
		if(!test12.toStringPrefix().equals("^ x 2.0")){
			System.out.println("failure for simplify rational, test9");
		}
		//reduce x/a*(x^b) 
		ParseTree test14 = new ParseTree(" x / ( 2 * ( x ^ 5 ) )");
		test14.reduceExpression();
		if(!test14.toStringPrefix().equals("/ 1.0 * 2 ^ x 4.0")){
			System.out.println("failure for simplify rational, test11");
		}	
		//reduce a*(x^b)/x
		ParseTree test15 = new ParseTree(" 2 * ( x ^ 5 ) / x ");
		test15.reduceExpression();
		if(!test15.toStringPrefix().equals("* 2.0 ^ x 4.0")){
			System.out.println("failure for simplify rational, test12");
		}
		//reduce a*(x^b)/c*x
		ParseTree test16 = new ParseTree(" 4 * ( x ^ 5 ) / ( 2 * x ) ");
		test16.reduceExpression();
		if(!test16.toStringPrefix().equals("* 2.0 ^ x 4.0")){
			System.out.println("failure for simplify rational, test13");
		}
		//reduce (a*(x^b))/(c*(x^d))
		ParseTree test17 = new ParseTree(" 4 * ( x ^ 5 ) / ( 2 * ( x ^ 3 ) ) ");
		test17.reduceExpression();
		if(!test17.toStringPrefix().equals("* 2.0 ^ x 2.0")){
			System.out.println("failure for simplify rational, test14");
		}
		//reduce (a*x)/(c*(x^d))
		ParseTree test18 = new ParseTree(" 4 * x  / ( 2 * ( x ^ 3 ) ) ");
		test18.reduceExpression();
		if(!test18.toStringPrefix().equals("/ 2.0 ^ x 2.0")){
			System.out.println("failure for simplify rational, test15");
		}
		//reduce (a*x)/(b*x)
		ParseTree test19 = new ParseTree(" ( 4 * x ) / ( 2 * x ) ");
		test19.reduceExpression();
		if(!test19.toStringPrefix().equals("2.0")){
			System.out.println("failure for simplify rational, test16");
		}
		//reduce (x)/(a*x^b)
		ParseTree test20 = new ParseTree("  x  / ( 2 * x ^ 3 ) ");
		test20.reduceExpression();
		if(!test20.toStringPrefix().equals("/ 1.0 * 2 ^ x 2.0")){
			System.out.println("failure for simplify rational, test17");
		}
		//reduce (a*x^b)/(x^c)
		ParseTree test21 = new ParseTree(" 4 * x ^ 5  / ( x ^ 3 ) ");
		test21.reduceExpression();
		if(!test21.toStringPrefix().equals("* 4.0 ^ x 2.0")){
			System.out.println("failure for simplify rational, test18");
		}
		//reduce (x^a)/(b*x^c)
		ParseTree test22 = new ParseTree(" ( x ^ 3 )  / ( 3 * x ^ 4 ) ");
		test22.reduceExpression();
		if(!test22.toStringPrefix().equals("/ ^ x -1.0  3.0")){
			System.out.println("failure for simplify rational, test19");
		}
		// * node over a / node
		ParseTree test23 = new ParseTree(" 3 * ( x / 4 )  ");
		test23.root.simplifyRationals();
		if(!test23.toStringPrefix().equals("/ * 3 x  4")){
			System.out.println("failure for simplify rational, test20");
		}
		//reducePlusOverTimes 1
		ParseTree test24 = new ParseTree(" x + ( x * 4 )  ");
		test24.root.addExtraOnesForPlusOverTimes();
		test24.root.miniSort();
		test24.root.reducePlusOverTimes();
		if(!test24.toStringPrefix().equals("* 5.0 x")){
			System.out.println("failure for simplify rational, test21");
		}
		//other way around to make sure
		ParseTree test25 = new ParseTree(" ( x * 4 ) + x  ");
		test25.reduceExpression();
		if(!test25.toStringPrefix().equals("* 5.0 x")){
			System.out.println("failure for simplify rational, test22");
		}
		//derivitive of constant
		ParseTree test26 = new ParseTree("1");
		test26.takeDerivitive();
		test26.reduceExpression();
		if(!test26.toStringPrefix().equals("0")){
			System.out.println("failure for derivitive, test1");
		}
		//derivitive of a*x
		ParseTree test27 = new ParseTree(" 2 * x");
		test27.takeDerivitive();
		test27.reduceExpression();
		if(!test27.toStringPrefix().equals("2.0")){
			System.out.println("failure for derivitive, test2");
		}
		//derivitive of a*x^b
		ParseTree test28 = new ParseTree(" 2 * x ^ 3");
		test28.takeDerivitive();
		test28.reduceExpression();
		if(!test28.toStringPrefix().equals("* 6.0 ^ x 2.0")){
			System.out.println("failure for derivitive, test3");
		}
		//derivitive of ln(x)
		ParseTree test29 = new ParseTree(" ln ( x )");
		test29.takeDerivitive();
		test29.reduceExpression();
		if(!test29.toStringPrefix().equals("/ 1 x")){
			System.out.println("failure for derivitive, test4");
		}
		//derivitive of a*ln(b*x)
		ParseTree test30 = new ParseTree(" 5 * ln ( 2 * x )");
		test30.takeDerivitive();
		test30.reduceExpression();
		if(!test30.toStringPrefix().equals("/ 5.0 x")){
			System.out.println("failure for derivitive, test5");
		}
		//derivitive of x*lnx
		ParseTree test32 = new ParseTree("x * ln (  x )");
		test32.takeDerivitive();
		test32.reduceExpression();
		if(!test32.toStringPrefix().equals("+ 1 ln x")){
			System.out.println("failure for derivitive, test6");
		}
		//derivitive of lnx/x
		ParseTree test33 = new ParseTree("ln (  x ) / x");
		test33.takeDerivitive();
		test33.reduceExpression();
		//should be (1 - ln x) / x^2 , but is in a slightly different format since we don't use negitivies
		if(!test33.toStringPrefix().equals("/ + 1 * -1 ln x    ^ x 2.0")){
			System.out.println("failure for derivitive, test7");
		}
		//derivitive of ln(ax)*x^b
		ParseTree test34 = new ParseTree("ln ( 2 * x ) * x ^ 2");
		test34.takeDerivitive();
		test34.reduceExpression();
		if(!test34.toStringPrefix().equals("+ x * 2.0 ln * 2 x   x")){
			System.out.println("failure for derivitive, test8");
		}
		//derivitive of a*sin(b*x)
		ParseTree test35 = new ParseTree("5 * sin ( 3 * x )");
		test35.takeDerivitive();
		test35.reduceExpression();
		if(!test35.toStringPrefix().equals("* 15.0 cos * 3 x")){
			System.out.println("failure for derivitive, test9");
		}
		//derivitive of a*cos(b*x)
		ParseTree test36 = new ParseTree("5 * cos ( 3 * x )");
		test36.takeDerivitive();
		test36.reduceExpression();
		if(!test36.toStringPrefix().equals("* -15.0 sin * 3 x")){
			System.out.println("failure for derivitive, test10");
		}
		//derivitive of a*tan(b*x)
		ParseTree test37 = new ParseTree("5 * tan ( 3 * x )");
		test37.takeDerivitive();
		test37.reduceExpression();
		if(!test37.toStringPrefix().equals("/ 15.0 ^ cos * 3 x   2")){
			System.out.println("failure for derivitive, test11");
		}
		
		
		System.out.println("-----------End of Tests---------");
	}

}


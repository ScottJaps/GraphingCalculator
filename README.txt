This is the code for a graphing calculator that I wrote.  It has basic graphing abilities, but also the ability to evaluate strings as 
mathematical expressions, reduce expressions and take derivitives in one variable.  

Here is a summary of the classes:

GCalculator.java:
	This class runs the calculator with methods for drawing and the handling of inputs.  

ParseTree.java:
	This class sets up the data structure used for evaluating strings as mathematical expressions. 
	It also has functions to reduce, evaluate and take the derivitive of them.

ScottMath.java
	This is the basic math library I wrote for use in the evaluate function of ParseTree.
	It uses various methods for calculating common mathematical functions to about 6 decimal accuracy in most cases
	It does not use the standard java math library at all

Stack.java and StdDraw.java
	These are libraries written by Robert Sedgewick and Kevin Wayne for Algorithms, 4th edition and released under the GNU General Public License
	You can find a link to the site here : http://algs4.cs.princeton.edu/home/
	Just to be clear, I did not write or modify these libraries.
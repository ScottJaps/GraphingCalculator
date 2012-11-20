package calcstuff;



public class GCalculator {
	private static StringBuilder inDisplay;
	private static String inOutput;
	private double xScale;
	private double yScale;
	private boolean drawGraph;
	
	//constructor
	public GCalculator(){
		inDisplay = new StringBuilder();
		inOutput = "";
		StdDraw.setCanvasSize(512, 768);
		xScale = 10;
		yScale = 10;
		drawGraph = false;
	}
	
	//adds stuff to the input string
	private static void addToDisplay(String in){
		inDisplay.append(in);
	}
	
	//this function draws the calculator
	public void drawCalc(){
		StdDraw.clear();

		StdDraw.setPenColor(StdDraw.GRAY);
        StdDraw.setXscale(0, 1000);
        StdDraw.setYscale(0, 1500);
        StdDraw.filledRectangle(500, 750, 500, 750);
        StdDraw.setPenColor(StdDraw.WHITE);
        StdDraw.filledRectangle(500, 1250, 500, 250);
        StdDraw.setPenColor(StdDraw.BLACK);
        StdDraw.setPenRadius(.02);
        
        //draws the lines for the calculator
        StdDraw.line(0, 0, 0, 1500);
        StdDraw.line(0, 0, 1000, 0);
        StdDraw.line(1000, 0, 1000, 1500);
        StdDraw.line(0, 1500, 1000, 1500);
        StdDraw.line(0, 800, 1000, 800);
        StdDraw.line(200, 0, 200, 1000);
        StdDraw.line(400, 0, 400, 1000);
        StdDraw.line(600, 0, 600, 1000);
        StdDraw.line(800, 0, 800, 1000);
        StdDraw.line(0, 150, 1000, 150);
        StdDraw.line(0, 300, 1000, 300);
        StdDraw.line(0, 450, 1000, 450);
        StdDraw.line(0, 600, 1000, 600);
        StdDraw.line(0, 700, 1000, 700);
        StdDraw.line(0, 800, 1000, 800);
        StdDraw.line(0, 900, 1000, 900);
        StdDraw.line(0, 1000, 1000, 1000);
      //text in the boxes
        StdDraw.text(100, 75, "0");
        StdDraw.text(300, 75, ".");
        StdDraw.text(500, 75, "-");
        StdDraw.text(100, 225, "1");
        StdDraw.text(300, 225, "2");
        StdDraw.text(500, 225, "3");
        StdDraw.text(100, 375, "4");
        StdDraw.text(300, 375, "5");
        StdDraw.text(500, 375, "6");
        StdDraw.text(100, 525, "7");
        StdDraw.text(300, 525, "8");
        StdDraw.text(500, 525, "9");
        StdDraw.text(900, 75, "Enter");
        StdDraw.text(900, 225, "-");
        StdDraw.text(900, 375, "+");
        StdDraw.text(700, 225, "/");
        StdDraw.text(700, 75, "*");
        StdDraw.text(100, 650, "(");
        StdDraw.text(300, 650, ")");
        StdDraw.text(500, 650, ",");
        StdDraw.text(900, 750, "Clear");
        StdDraw.text(900, 525, "Pi");
        StdDraw.text(700, 375, "Sqrt");
        StdDraw.text(100, 750, "Sin");
        StdDraw.text(300, 750, "Cos");
        StdDraw.text(500, 750, "Tan");
        StdDraw.text(700, 750, "x");
        StdDraw.text(700, 525, "^");
        StdDraw.text(900, 650, "e");
        StdDraw.text(700, 650, "Backspace");
        StdDraw.text(100, 850, "d/dx");
        StdDraw.text(300, 850, "!");
        //StdDraw.text(500, 850, "something");                                        //space for one more operation
        StdDraw.text(700, 850, "output");
        StdDraw.text(900, 850, "reset graph");
        StdDraw.text(100, 950, "graph y=");
        StdDraw.text(300, 950, "double x");
        StdDraw.text(500, 950, "double y");
        StdDraw.text(700, 950, "half x");
        StdDraw.text(900, 950, "half y");
        StdDraw.textRight(900, 1100, inDisplay.toString());
        StdDraw.textLeft(100, 1400, inOutput);
        if(drawGraph) drawGraph();
	}
	
	//this is called when the user clicks the mouse on the calculator
	public void inputs(double x, double y){
		if (x > 0 && x < 200 && y > 0 && y < 150){                              //0
			addToDisplay("0");
		}
		else if(x > 200 && x < 400 && y > 0 && y < 150){                        // .
			addToDisplay(".");
		}
		else if(x > 400 && x < 600 && y > 0 && y < 150){                        // -
			addToDisplay(" -");
		}
		else if(x > 0 && x < 200 && y > 150 && y < 300){                        //1
			addToDisplay("1");
		}
		else if(x > 200 && x < 400 && y > 150 && y < 300){                      //2
			addToDisplay("2");
		}
		else if(x > 400 && x < 600 && y > 150 && y < 300){                      //3
			addToDisplay("3");
		}
		else if(x > 0 && x < 200 && y > 300 && y < 450){                         //4
			addToDisplay("4");
		}
		else if(x > 200 && x < 400 && y > 300 && y < 450){                      //5
			addToDisplay("5");
		}
		else if(x > 400 && x < 600 && y > 300 && y < 450){                      //6
			addToDisplay("6");
		}
		else if(x > 0 && x < 200 && y > 450 && y < 600){                       //7
			addToDisplay("7");
		}
		else if(x > 200 && x < 400 && y > 450 && y < 600){                      //8
			addToDisplay("8");
		}
		else if(x > 400 && x <600 && y > 450 && y < 600){                       //9
			addToDisplay("9");
		}
		else if(x > 800 && x < 1000 && y > 0 && y < 150){                          // Enter
			if(ParseTree.checkString(inDisplay.toString())){
				ParseTree toEval = new ParseTree(inDisplay.toString());
				toEval.reduceExpression();
				inOutput = toEval.toString();
				inDisplay.delete(0, inDisplay.length());
			}
			else{
				inDisplay.delete(0, inDisplay.length());
				inDisplay.append("Not a vaid Expression");
			}
		}
		else if(x > 600 && x < 800 && y > 0 && y < 150){                          //*
			addToDisplay(" * ");
		}
		else if(x > 600 && x < 800 && y > 150 && y < 300){                       //  /
			addToDisplay(" / ");
		}
		else if(x > 800 && x < 1000 && y > 150 && y < 300){                         //-
			addToDisplay(" - ");
		}
		else if(x > 800 && x < 1000 && y > 300 && y < 450){                        //+
			addToDisplay(" + ");
		}
		else if(x > 800 && x < 1000 && y > 700 &&y < 800){                               // clear
			inDisplay.delete(0, inDisplay.length());
		}
		else if(x > 0 && x < 200 && y > 600 && y < 700){                                   // (
			addToDisplay(" ( ");
		}
		else if (x > 200 && x < 400 && y > 600 && y < 700){                                 // ) 
			addToDisplay(" ) ");
		}
		else if (x > 400 && x < 600 && y > 600 &&y < 700){                                 // , 
			addToDisplay(" , ");
		}
		else if (x > 800 && x < 1000 && y > 450 && y < 600){                                 // pi 
			addToDisplay(" 3.1415926535989");
		}
		else if (x > 600 && x < 800 && y < 450 && y > 300){                            //sqrt
			addToDisplay(" sqrt ");
		}
		else if (x > 0 && x < 200 && y > 700 && y < 800){                             //Sin
			addToDisplay(" sin ");
		}
		else if (x > 200 && x < 400 && y > 700 && y < 800){                            // Cos
			addToDisplay(" cos ");
		}
		else if (x > 400 && x < 600 && y > 700 && y < 800){                            // Tan
			addToDisplay(" tan ");
		}
		else if (x > 600 && x < 800 && y > 700 && y < 800){                            // 'x'
			addToDisplay(" x ");

		}
		else if (x > 600 && x < 800 && y > 450 && y < 600){                                // ^
			addToDisplay(" ^ ");
			
		}
		else if (x > 800 && x < 1000 && y > 600 && y < 700){                              // e
			addToDisplay(" 2.71828182846 ");
		}
		else if (x > 600 && x < 800 && y > 600 && y < 700){                                 //  backspace
			if (inDisplay.length() > 0) inDisplay.delete(inDisplay.length() - 1, inDisplay.length());
		}
		else if (x > 0 && x < 200 && y > 800 && y < 900){								// d/dx		
			if(ParseTree.checkString(inDisplay.toString())){
				ParseTree toEval = new ParseTree(inDisplay.toString());
				toEval.reduceExpression();
				toEval.takeDerivitive();
				inOutput = toEval.toString();
				inDisplay.delete(0, inDisplay.length());
			}
			else{
				inDisplay.delete(0, inDisplay.length());
				inDisplay.append("Not a vaid Expression");
			}
		}
		else if (x > 200 && x < 400 && y > 800 && y < 900){								// factorial
			addToDisplay(" ! ");
		}
		/*else if (x > 400 && x < 600 && y > 800 && y < 900){								// could add something here
			
		}*/
		else if (x > 600 && x < 800 && y > 800 && y < 900){								// add current output to string
			addToDisplay(inOutput);
		}
		else if (x > 0 && x < 200 && y > 900 && y < 1000){								// graph
			drawGraph = true;
		}
		else if (x > 200 && x < 400 && y > 900 && y < 1000){								// double x axis size
			xScale *= 2;
			drawGraph = true;
		}
		else if (x > 400 && x < 600 && y > 900 && y < 1000){								// double y axis size
			yScale *= 2;
			drawGraph = true;
		}
		else if (x > 600 && x < 800 && y > 900 && y < 1000){								// half x axis
			xScale /= 2;
			drawGraph = true;
		}
		else if (x >800 && x < 1000 && y > 900 && y < 1000){								// half y axis
			yScale /= 2;
			drawGraph = true;
		}
		else if (x > 800 && x < 1000 && y > 800 && y < 900){								// reset to default axis
			xScale = 10;
			yScale = 10;
			drawGraph = true;
		}
	}
	
	//called if we need to draw a graph for this mouse click
	private void drawGraph(){
		if(ParseTree.checkString(inDisplay.toString())){
			ParseTree toEval = new ParseTree(inDisplay.toString());
			//reduce to make it easier to work with
			toEval.reduceExpression();
			//draw background
			StdDraw.setPenColor(StdDraw.WHITE);
	        StdDraw.filledRectangle(500, 1250, 475, 225);
			StdDraw.setPenColor(StdDraw.BLACK);
			StdDraw.setPenRadius(.0001);
			//draw axis
			StdDraw.line(500, 1000, 500, 1500);
			StdDraw.line(250, 1250, 750, 1250);
			//axis labels
			StdDraw.text(250, 1280,"-" + Double.toString(xScale));
			StdDraw.text(750, 1280, Double.toString(xScale));
			StdDraw.text(540, 1450, Double.toString(yScale));
			StdDraw.text(540, 1050, Double.toString(yScale));
			//"line" will be in red
			StdDraw.setPenColor(StdDraw.RED);
			//need to find where to draw a dot if at all
			for(double i = 0; i < 1000; i++){
				//where our x is from -xScale to xScale
				double xCord = -xScale + (i*2*xScale)/1000;
				//replace x with the x value we are evaluating for
				ParseTree toGraph = new ParseTree(toEval.toString().replace("x", Double.toString(xCord)));
				//eval should run fast since we dont need to bother with reducing the tree as there should not be any more variables
				double yCord = toGraph.eval();
				if(!Double.isNaN(yCord)){
					double xPoint = 500 + (250 / xScale * xCord);
					double yPoint = 1250 + (250 / yScale * yCord);
					if(yPoint > 1000 && yPoint < 1500) StdDraw.point(xPoint, yPoint);
				}
			}
		}
		else{
			inDisplay.delete(0, inDisplay.length());
			inDisplay.append("Not a vaid Expression to graph");
		}
		drawGraph = false;
	}

	
	public static void main(String[] args) {
		GCalculator theCalc = new GCalculator();
		theCalc.drawCalc();
		
		while (true) {
            if (StdDraw.mousePressed()) {
                double x = StdDraw.mouseX();
                double y = StdDraw.mouseY();
                theCalc.inputs(x,y);
                theCalc.drawCalc();
                //delay for .1 seconds to prevent fast double clicks
               try {
                	  Thread.sleep(100L);	  // one tenth second
                	}
                	catch (Exception e) {System.out.println("error in sleep delay");}	   // should not happen
            }
            StdDraw.show(50);
        }
	}

}

package newProjectComiler;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

enum State {S0, S1, S2, S3, S4, S5,S6,S7, Se} // States for DFA (used to check identifiers and numbers)


public class NewProjectCompiler {
	
        // Lists to store file lines, keywords, and operators
	    private ArrayList<String>fileLines;    
	    private ArrayList<String>keywords;
	    private ArrayList<String>operators;
	    
	    // Symbols for end of line and word
	    private String endOfLine;
	    private String endOfWord;
	    
	    // File name to read from
	    private String fileName;     

	    // Constructor
	    public NewProjectCompiler(String line,String word, String fileName) {
	        this.endOfLine = line;
	        this.endOfWord = word;
	        this.fileName = fileName;
	        keywords=new ArrayList<>();
	        operators=new ArrayList<>();
	        fileLines=new ArrayList<>();  
	        
	        // Adding some keywords
	        keywords.add("case");
	        keywords.add("default");
	        keywords.add("do");   keywords.add("double");
	        keywords.add("else"); keywords.add("enum");
	        keywords.add("float");keywords.add("for");
	        keywords.add("if");   keywords.add("int");
	    
	        // Adding some operators
	        operators.add("!");operators.add(">");operators.add("<");
	        operators.add("=");operators.add("==");operators.add("||");
	        operators.add("|");operators.add("+"); operators.add("-");
	        operators.add("*");operators.add("/"); operators.add("%");
	        operators.add("&&");operators.add("&");operators.add("!=");
	        operators.add(">=");operators.add("<=");
	        operators.add("{");operators.add("}");
	        operators.add("(");operators.add(")");


	    
	    }
	    
	    // This method reads the file and stores each line
	    public void readFile() {
	        try {
	            Path filePath = Paths.get(fileName);
	            Files.lines(filePath).forEach(fileLines::add);
	        } catch (IOException e) {
	            e.printStackTrace();
	        }
	    }
	    
	    // Split the line into words using space
	    public String[] getLaxeme (String line,int n){
	     String[] a=line.split(" ");
	     return a;
	 }  
	    
	    // Check if the string is a keyword
	     public boolean isKeyword(String lex) {
	        return keywords.contains(lex);
	    }
	     
	     // Check if it's an operator
	    public boolean isOperator(String lex) {
	        return operators.contains(lex);
	    }
	    
	    // Check if it's end of line symbol (like ;)
	    public boolean isEndOfLine(String lex) {
	        return endOfLine.equals(lex);
	    }
	    
	    // Check if character is a letter
	    public boolean isLetter(char entry) {
	        return (entry ==  '_')||( entry >= 'A' && entry <= 'Z') || (entry >= 'a' && entry <= 'z');
	    }

	    // Check if character is a number
	    public boolean isDigit(char entry) {
	        return entry >= '0' && entry <= '9';
	    }
	    
	    // DFA logic to check if the token is valid identifier or number
	    public State executeTransition(State currentState, char entry) {
	        switch (currentState) {
	            case S0:
	                if (entry=='_') return State.S7;
	                else if (entry =='0') return State.S2;
	                else if(entry>='1'&& entry <='9')  return State.S1;               
	                else return State.Se;
	            case S1:
	                if (entry=='.') return State.S5;
	                else if (isDigit(entry)) return State.S1;
	                else return State.Se;
	            case S2:
	                if (entry=='.') return State.S3;
	                else return State.Se;
	            case S3:
	                 if (isDigit(entry)) return State.S4;
	                else return State.Se;
	            case S4:
	                if (isDigit(entry)) return State.S4;
	                else return State.Se;
	            case S5:
	                if (isDigit(entry)) return State.S6;
	                else return State.Se;
	             case S6:
	                if (isDigit(entry)) return State.S6;
	                else return State.Se;
	            case S7:
	                if (isLetter(entry)) return State.S7;
	                else if (isDigit(entry)) return State.S7;
	                else return State.Se;
	            default:
	                return State.Se;
	        }
	    }
	    
	    // This method checks each word and prints what type it is
	      public void evaluate(String str,int n) {
	        if (isKeyword(str))
	            System.out.println("<" + str + "," + "Keyword" + ">");
	        else if (isOperator(str))
	            System.out.println("<" + str + "," + "Operator" + ">");
	        else if (str.equals(endOfLine))
	            System.out.println("<" + str + "," + "End_of_line" + ">");
	        else {
	            // Use DFA to check for identifier or number
	            State state = State.S0;
	            for (char c : str.toCharArray()) {
	                state = executeTransition(state, c);
	            }
	            
	            if (state == State.Se)
	                System.out.println("ERROR..." + str +  " Invalid at line : "+n);
	            else if (state == State.S7)
	                System.out.println("<" + str + "," + "Identifier" + ">");
	            else if (state == State.S1 || state == State.S4
	                || state == State.S6 || state == State.S2)
	                System.out.println("<" + str + "," + "Number" + ">");
	        }
	    }
	      
	    public static void main(String[] args) {
	        String [] words;
	        // Create compiler object with end symbols and file name
	        NewProjectCompiler c= new NewProjectCompiler(";"," ","h.txt");
	        c.readFile();//read source code
	        // Go through each line and word to analyze
	        for(int i=0;i<c.fileLines.size();i++){
	             words=c.getLaxeme(c.fileLines.get(i),(i+1));
	                for(String W:words)
	                    c.evaluate(W,(i+1));}
	    
	        }
	    }

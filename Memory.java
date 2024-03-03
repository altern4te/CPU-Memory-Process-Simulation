import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.Scanner;

public class Memory 
{
    public static void main(String[] args) throws IOException 
	{
		// Initialize MemoryTable
		int[] memoryTable = new int[2000];
        for (int i = 0; i < 2000; i++) 
        {
            memoryTable[i] = 0;
        }
        
        // initializing memory
        Scanner inputFile = new Scanner(new File(args[0]));
     	for(int i = 0; inputFile.hasNextLine() && i < 2000; i++)
     	{
     	 	// take out comments and make sure value exists
     	 	String[] memVals = inputFile.nextLine().split("\\s");
     	 	if(!memVals[0].equals(""))
     	 	{	
     	 		try 
     	 		{ 
     	 			if(memVals[0].substring(0, 1).equals("."))
     	 				i = Integer.parseInt(memVals[0].substring(1)) - 1;
     	 			else
     	 				memoryTable[i] = Integer.parseInt(memVals[0]);
     	 		}
     	 		catch (NumberFormatException e) 
     	 		{
     	 			System.err.println("Syntax Error - Number Format Exception...");
     	 			System.exit(1);
     	 		}
     	 	}
     	 	else 
     	 		i--;
     	}
     	if(inputFile.hasNextLine())
     	{
     		System.err.println("Input file contains too many instructions...");
     	 	System.exit(1);
     	}
     	 inputFile.close();
        
		// Initialize I/O Streams
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        PrintWriter writer = new PrintWriter(new OutputStreamWriter(System.out), true);
        
        String input;
        while ((input = reader.readLine()) != null) 
        {
            String[] parts = input.split(" ");
            String command = parts[0];

			// Read Handler
            if ("READ".equals(command)) 
            {
                int addr = Integer.parseInt(parts[1]);
                writer.println(memoryTable[addr]);
            } 
			// Write Handler
            else if ("WRITE".equals(command)) 
            {
                int addr = Integer.parseInt(parts[1]);
                int val = Integer.parseInt(parts[2]);
                memoryTable[addr] = val;
                writer.println(val + " Stored In " + addr);
            }
			// Recognizes print to keep syncronization
            else if(command.contains("PRINT"))
            {
            	writer.println("PRINTED");
            }
	    	// Exit case
			else if("EXIT".equals(command))
	 		{
				writer.println("EXITING");
				reader.close();
				writer.close();
				System.exit(0);
		    }
        }
    }
}

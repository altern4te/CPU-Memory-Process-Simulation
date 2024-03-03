import java.util.Scanner;
import java.io.*;
import java.util.Random;

public class CPU 
{
	static BufferedReader reader;
    static BufferedWriter writer;
	static int PC, SP, IR, AC, X, Y, timer, timerMax;
	static Random rand;
	static boolean kernel, time, interrupted;
	
	public static void main(String[] args) throws IOException
	{
		rand = new Random();
		//creating registers
		PC = 0; SP = 1000; IR = 0; AC = 0; X = 0; Y = 0; timer = 0; timerMax = 0; kernel = time = interrupted = false;
		if(args.length > 0 )
		{
			timerMax = Integer.parseInt(args[0]) + 1;
			time = true;
		}
		
		reader = new BufferedReader(new InputStreamReader(System.in));
	    writer = new BufferedWriter(new OutputStreamWriter(System.out));
		// fetch / execute
	    
		while(true)
		{
			//fetch
			IR = smah();

			//execute
			switch(IR)
			{
				case 1: loadValue(); break;
				case 2: loadAddr(); break;
				case 3: loadIndAddr(); break;
				case 4: loadIdxX(); break;
				case 5: loadIdxY(); break;
				case 6: loadSpX(); break;
				case 7: storeAddr(); break;
				case 8: get(); break;
				case 9: putPort(); break;
				case 10: AddX(); break;
				case 11: AddY(); break;
				case 12: SubX(); break;
				case 13: SubY(); break;
				case 14: CopyToX(); break;
				case 15: CopyFromX(); break;
				case 16: CopyToY(); break;
				case 17: CopyFromY(); break;
				case 18: CopyToSP(); break;
				case 19: CopyFromSP(); break;
				case 20: jumpAddr(); break;
				case 21: jumpIfEqual(); break;
				case 22: jumpIfNotEqual(); break;
				case 23: call(); break;
				case 24: ret(); break;
				case 25: IncX(); break;
				case 26: DecX(); break;
				case 27: push(); break;
				case 28: pop(); break;
				case 29: interr(); break;
				case 30: interrRet(); break;
				case 50: sendCommand("EXIT"); reader.close(); writer.close(); System.exit(0);
				default: sendCommand("PRINTE Syntax Error...");
					 sendCommand("EXIT");
					 reader.close(); writer.close();
					 System.exit(1);
			}
			//increment PC
			PC++;
			//timer check 
			if(time && !interrupted)
				timer++;
			if(!interrupted && time && timer == timerMax)
                                alarm();

		}
		
	}
	
	private static void loadValue() throws IOException { PC++; AC = smah(); } // Load value
	private static void loadAddr() throws IOException // Load address
	{ 
		PC++; 
		AC = smah(smah()); 
	}
	private static void loadIndAddr() throws IOException // Load nested address
	{ 
		PC++; 
		int PC2 = smah();
		AC = smah(PC2); 
	}
	private static void loadIdxX() throws IOException { PC++; AC = smah(smah() + X); } // Load address + X to AC
	private static void loadIdxY() throws IOException { PC++; AC = smah(smah() + Y); } // Load address + Y to AC
	private static void loadSpX() throws IOException { AC = smah(SP + X); } // Load SP + X to AC
	private static void storeAddr() throws IOException 
	{ 
		PC++;
		smwrite(smah(), AC); 
	}
	private static void get() { AC = rand.nextInt(100) + 1; }
	private static void putPort() throws IOException 
	{
		PC++;
		int port = smah();
		if(port == 1)
			sendCommand("PRINT " + AC);
		else if(port == 2)
			sendCommand("PRINTC " + AC);
	}
	private static void AddX() { AC += X; }
	private static void AddY() { AC += Y; }
	private static void SubX() { AC -= X; }
	private static void SubY() { AC -= Y; }
	private static void CopyToX() { X = AC; }
	private static void CopyFromX() { AC = X; }
	private static void CopyToY() { Y = AC; }
	private static void CopyFromY() { AC = Y; }
	private static void CopyToSP() { SP = AC; }
	private static void CopyFromSP() { AC = SP; }
	private static void jumpAddr() throws IOException // Jump to address
	{ 
		PC++;
		PC = smah() - 1; 
	}
	private static void jumpIfEqual() throws IOException 
	{  
		PC++;
		if(AC == 0)
		{
			PC = smah() - 1;
		}
	}
	private static void jumpIfNotEqual() throws IOException 
	{  
		PC++;
		if(AC != 0)
		{
			PC = smah() - 1;
		}
	}
	private static void call() throws IOException { PC++; SP--; smwrite(SP, PC); PC = smah() - 1; } // push address to stack and jump to new address
	private static void ret() throws IOException { int tempAddr = smah(SP); smwrite(SP, 0); SP++; PC = tempAddr; } //pop return address from stack
	private static void IncX() { X++; }
	private static void DecX() { X--; }
	private static void push() throws IOException { SP--; smwrite(SP, AC); } // Push value on stack
	private static void pop() throws IOException // Pop value off stack
	{ 
		int tempAC = smah(SP); 
		smwrite(SP, 0); 
		SP++; 
		AC = tempAC; 
	}
	private static void interr() throws IOException // Start interrupt
	{ 
		if(interrupted)
		{
			sendCommand("PRINTE Deadlock: Interrupted while in interrupt...");
			sendCommand("EXIT");
			reader.close();
			writer.close();
			System.exit(1);
		}
		kernel = true;
		interrupted = true;
		int tempSP = SP;
		SP = 1999;
		smwrite(SP, PC);
		SP--; 
		smwrite(SP, tempSP);
		PC = 1499; 
	}
	private static void interrRet() throws IOException  // Return from interrupt
	{ 
		int tempSP = smah(SP); 
		smwrite(SP, 0); 
		SP++; 
		int tempAddr = smah(SP); 
		smwrite(SP, 0); 
		SP = tempSP;
		PC = tempAddr; 
		kernel = false; 
		interrupted = false; 
	}
	private static void alarm() throws IOException // Alarm for timer
	{  
		interrupted = true; 
		kernel = true; 
		int tempSP = SP;
		SP = 1999;
		smwrite(SP, PC - 1);
		SP--;
		smwrite(SP, tempSP); 
		PC = 1000; 
		timer = 0; 
	}
	// Safe Memory Access Helper
	private static int smah() throws IOException
	{
		int PC2 = Integer.parseInt(sendCommand("READ " + PC));
		if(!kernel && PC2 > 999) // Checks for memory violation
		{
			sendCommand("PRINTE Memory violation: accessing system address 1000 in user mode...");
			sendCommand("EXIT");
			reader.close();
			writer.close();
			System.exit(1);
		}
		return PC2;
	}
	private static int smah(int val) throws IOException
	{
		int PC2 = Integer.parseInt(sendCommand("READ " + val));
		if(!kernel && PC2 > 999) 
		{
			sendCommand("PRINTE Memory violation: accessing system address 1000 in user mode...");
            sendCommand("EXIT");
            reader.close();
            writer.close();
            System.exit(1);
		}
		return PC2;
	}
	// Safe Memory Write
	private static void smwrite(int addr, int val) throws IOException
	{
		if(!kernel && addr > 999)
		{
			sendCommand("PRINTE Memory violation: accessing system address 1000 in user mode...");
            sendCommand("EXIT");
            reader.close();
            writer.close();
            System.exit(1);
		}
		sendCommand("WRITE " + addr  + " " + val);
	}
	private static String sendCommand(String command) throws IOException 
	{
		// send the command
        writer.write(command);
        writer.newLine();
        writer.flush();
		// receive from Memory
        return reader.readLine();
    }
}

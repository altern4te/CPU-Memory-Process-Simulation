import java.io.*;
import java.util.Scanner;

public class Project1 
{
    public static void main(String[] args) 
    {
        // Check for arguments
        if(args.length < 1)
        {
            System.err.println("Insufficient Arguments...");
            System.exit(1);
        }
        try 
        {
            //Initialize Runtime
            Runtime rt = Runtime.getRuntime();

            // Start the Memory process
            Process memoryProcess = rt.exec("java -cp . Memory " + args[0]);
            OutputStream memoryInput = memoryProcess.getOutputStream();
            InputStream memoryOutput = memoryProcess.getInputStream();
	   
            // Start the CPU process
            Process cpuProcess;
            if(args.length > 1)
                cpuProcess = rt.exec("java -cp . CPU " + args[1]);
            else
                cpuProcess = rt.exec("java -cp . CPU");
            OutputStream cpuInput = cpuProcess.getOutputStream();
            InputStream cpuOutput = cpuProcess.getInputStream();

            // Create writers for CPU and Memory
            PrintWriter cpuWriter = new PrintWriter(cpuInput, true);
            PrintWriter memoryWriter = new PrintWriter(memoryInput, true);
            Scanner cpuScanner = new Scanner(cpuOutput);

            // Wait for CPU to send data and forward it to Memory, then return Memory data to CPU
            while (cpuScanner.hasNextLine())
            {
                // Get call from CPU and request from Memory
                String dataFromCPU = cpuScanner.nextLine();
                memoryWriter.println(dataFromCPU);
                memoryWriter.flush();
                    Scanner memoryScanner = new Scanner(memoryOutput);
                    if (memoryScanner.hasNextLine())
                    {
                        //Get response from memory and send to CPU
                        String responseFromMemory = memoryScanner.nextLine();
                        // Print Handlers
			            if(dataFromCPU.contains("PRINTC"))
				            System.out.print((char)Integer.parseInt(dataFromCPU.substring(7)));
			            else if(dataFromCPU.contains("PRINTE"))
                            System.err.println(dataFromCPU.substring(7));
			            else if(dataFromCPU.contains("PRINT"))
				            System.out.print(dataFromCPU.substring(6));
                        cpuWriter.println(responseFromMemory);
                    }
            }
            //Wait for processes to finish
            cpuProcess.waitFor();
            memoryProcess.waitFor();
            
        } 
        catch (IOException | InterruptedException e) 
        {
            e.printStackTrace();
        }
    }
}

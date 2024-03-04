# CPU-Memory-Process-Simulation

## Running The Program

This simulation is ran in command line, the following commands will compile and start the program
* javac CPU.java
* javac Memory.java
* javac Project1.java
* java Project1 [filename] [timer (optional)]

## Project Purpose
  The purpose of this project is to learn the inner workings of processes and how they communicate between themselves, cooperate with one another, and work around the intricacies required for synchronization. It is also to help understand low level concepts necessary in understanding the operating system such as system calls, procedure calls, the purpose of registers, and I/O along with the interruptions necessary for them to function efficiently.

 ## Implementation
  This program was coded in Java, utilizing the Runtime.exec method for process creation as specified in the project description. There are three classes used in the program, Memory, CPU, and Project1. Memory is self-explanatory, it holds the memory table and is in charge of sending and receiving data to and from the CPU when requested by the CPU. First Memory will initialize and load the memory table with the input file, per the specifications of problem details, and will read, write, and confirm information had been read and written. The CPU class is much more complex, handling the execution of the program provided in the input file. The CPU will first initialize the registers, the I/O streams, and check if a timer is in place, setting the appropriate values allowing the timer to start if so. Then the first call is made, fetching the Instruction Register value from the Memory through the I/O streams. Then based on the IR value the program will execute one of thirty-one different instructions implemented with a switch case. If an invalid instruction is given the program will halt with a syntax error. Then the program counter will be incremented, the timer will be incremented and checked, and the process will begin again until the exit instruction is given or there is an error. The stack in the CPU is implemented with a decrement first approach, adding to the end of the user or system stack depending on whether the system is in kernel mode.  Finally, the Project1 class is the bridge between the CPU and the Memory. It is a simple runner class that initializes the CPU and Memory programs using Runtime.exec, Process, OutputStream, and InputStream. It makes sure that the program is run with the correct arguments and that the CPU and Memory are initialized with their individual required arguments. It then uses initializes PrintWriter to write back to each of the processes, and forms a while loop alternating between sending information to the Memory from the CPU and back again. This class also manages the print commands from the CPU, as it is the runner and is needed to actually print to the console. Once CPU exits, it communicates to Memory to exit as well and ends the program.

/*
	Name of the Student: Navneet Viswanadha
	Class: CS 4348
	Section: 501
	Semester: Fall 2019
	Project 1 - Simulates a low-level, simplistic CPU and Memory 
	File: CPU.java (Simulates the CPU portion of the project)
*/

/* Import required libraries */

import java.io.*;
import java.lang.Runtime;
import java.util.*;



public class CPU {
	static int PC, SP, IR, AC, X, Y; //The registers
	static boolean userMode = true; //set default mode as userMode 
	static final int TOP_OF_USER_MEMORY = 999;
	static final int TOP_OF_SYSTEM_MEMORY = 1999;
	static int timer = 0;
	static int instructionCount = 0;
	static boolean interruptProcessing = false;
	static int userStackTopIndex = 999;
	
	public static void main(String args[]) {
		
		String instructionsFileName = null;
		
		if(args.length != 2) { //invalid input
			System.out.println("You can only enter exactly two parameters. Ending the process.");
			System.exit(0);
		}else {
			instructionsFileName = args[0];
			timer = Integer.parseInt(args[1]); //interrupt after ever timer seconds
		} //end else
		
		try {
			Runtime rt = Runtime.getRuntime();
			
			Process proc = rt.exec("java Memory");
			
			OutputStream os = proc.getOutputStream();
			PrintWriter pw = new PrintWriter(os);
			
			InputStream is = proc.getInputStream();
			Scanner readFromMemory = new Scanner(is);
			
			sendFileNametoMemory(pw, is, os, instructionsFileName);
			
			/*Infinite loop constantly on the lookout for signals from the CPU for Memory */
			while(true) {
				if((instructionCount % timer == 0) && instructionCount > 0 && interruptProcessing == false) {
					interruptProcessing = true;
					timerInterruptHandler(readFromMemory, os, is, pw);
				}
				
				int instruction = readFromMemory(pw, is, os, readFromMemory, PC);
				
				if(instruction == -1) {
					break; //no instructions left
				}else {
					runInstruction(pw, os, is,readFromMemory, instruction);
				} //end else
				
			} //end while(true)
			
			
			proc.waitFor();
			int exitVal = proc.exitValue();
			System.out.println("Process exited: " + exitVal);
		} //end try block
		
		catch(Throwable t) {
			t.printStackTrace();
		} //end catch block
	} //end method public static void main(String args[])
	
	private static void runInstruction(PrintWriter pw, OutputStream os, InputStream is, Scanner readFromMemory, int instruction) {
		int operand; //in case the instruction has one
		IR = instruction;
		
		switch(IR) {
			case 1: 
				PC++;
				operand = readFromMemory(pw, is, os, readFromMemory, PC);
				AC = operand;
				if(interruptProcessing == false) {instructionCount++;}
				PC++;
				break;
				
			case 2:
				PC++;
				operand = readFromMemory(pw, is, os, readFromMemory, PC);
				AC = readFromMemory(pw, is, os, readFromMemory, operand);
				if(interruptProcessing == false) {instructionCount++;}
				PC++;
				break;
				
			case 3:
				PC++;
				operand = readFromMemory(pw, is, os, readFromMemory, PC);
				operand = readFromMemory(pw, is, os, readFromMemory, operand);
				
				AC = readFromMemory(pw, is, os, readFromMemory, operand);
				if(interruptProcessing == false) {instructionCount++;}
				PC++;
				break;
			
			case 4:
				PC++;
				operand = readFromMemory(pw, is, os, readFromMemory, PC);
				AC = readFromMemory(pw, is, os, readFromMemory, operand + X);
				if(interruptProcessing == false) {instructionCount++;}
				PC++;
				break;
				
			case 5:
				PC++;
				operand = readFromMemory(pw, is, os, readFromMemory, PC);
				AC = readFromMemory(pw, is, os, readFromMemory, operand + Y);
				if(interruptProcessing == false) {instructionCount++;}
				PC++;
				break;
			
			case 6:
				AC = readFromMemory(pw, is, os, readFromMemory, SP + X);
				if(interruptProcessing == false) {instructionCount++;}
				PC++;
				break;
				
			case 7:
				PC++;
				operand = readFromMemory(pw, is, os, readFromMemory, PC);
				writeToMemory(pw, is, os, operand, AC);
				if(interruptProcessing == false) {instructionCount++;}
				PC++;
				break;
			
			case 8:
				Random r = new Random();
				int random = 1+ r.nextInt(100);
				AC = random;
				if(interruptProcessing == false) {instructionCount++;}
				PC++;
				break;
			
			case 9:
				PC++;
				operand = readFromMemory(pw, is, os, readFromMemory, PC);
				
				if(operand == 1) {
					System.out.print(AC);
				}else if(operand == 2) {
					System.out.print((char)  AC);
				}else {
					System.out.println("Port has an invalid value");
				}
				if(interruptProcessing == false) {instructionCount++;}
				PC++;
				break;
			
			case 10:
				AC = AC + X;
				if(interruptProcessing == false) {instructionCount++;}
				PC++;
				break;
			
			case 11:
				AC = AC + Y;
				incrementInstructionCount();
				PC++;
				break;
			
			case 12:
				AC = AC - X;
				incrementInstructionCount();
				PC++;
				break;
			
			case 13:
				AC = AC - Y;
				incrementInstructionCount();
				PC++;
				break;
			
			case 14:
				X = AC;
				incrementInstructionCount();
				PC++;
				break;
				
			case 15:
				AC = X;
				incrementInstructionCount();
				PC++;
				break;
			
			case 16:
				Y = AC;
				incrementInstructionCount();
				PC++;
				break;
				
			case 17:
				AC = Y;
				incrementInstructionCount();
				PC++;
				break;
			
			case 18:
				SP = AC;
				incrementInstructionCount();
				PC++;
				break;
			
			case 19:
				AC = SP;
				incrementInstructionCount();
				PC++;
				break;
				
			case 20:
				PC++;
				operand = readFromMemory(pw, is, os, readFromMemory, PC);
				PC = operand;
				incrementInstructionCount();
				break;
			
			case 21:
				PC++;
				operand = readFromMemory(pw, is, os, readFromMemory, PC);
				
				if(AC == 0) {
					PC = operand;
					incrementInstructionCount();
					break;
				}
				
				incrementInstructionCount();
				PC++;
				break;
				
			case 22:
				PC++;
				operand = readFromMemory(pw, is, os, readFromMemory, PC);
				if(AC != 0) {
					PC = operand;
					incrementInstructionCount();
					break;
				}
				
				incrementInstructionCount();
				PC++;
				break;
			
			case 23:
				PC++;
				operand = readFromMemory(pw, is, os, readFromMemory, PC); 
				pushToStack(pw, is, os, PC + 1);
				PC = operand;
				userStackTopIndex = SP;
				incrementInstructionCount();
				break;
				
			case 24:
				operand = popFromStack(pw, is, os, readFromMemory);
				PC = operand;
				incrementInstructionCount();
				break;
				
			case 25:
				X++;
				incrementInstructionCount();
				PC++;
				break;
				
			case 26:
				X--;
				incrementInstructionCount();
				PC++;
				break;
				
			case 27:
				pushToStack(pw, is, os, AC);
				PC++;
				incrementInstructionCount();
				break;
				
			case 28:
				AC = popFromStack(pw, is, os, readFromMemory);
				
				incrementInstructionCount();
				PC++;
				break;
				
			case 29:
				
				interruptProcessing = true;
				userMode = false;
				operand = SP;
				SP = 2000;
				pushToStack(pw, is, os, operand);
				
				operand = PC + 1;
				PC = 1500;
				pushToStack(pw, is, os, operand);
				
				incrementInstructionCount();
				break;
				
			case 30:
				PC = popFromStack(pw, is, os, readFromMemory);
				SP = popFromStack(pw, is, os, readFromMemory);
				
				interruptProcessing = false;
				instructionCount++;
				userMode = true;
				
				break;
				
			case 50:
				incrementInstructionCount();
				System.exit(0);
				break;
				
			default:
				System.out.println("Not a recognized instruction. ");
				System.exit(0);	
				
		} //end switch statement
		
	} //end method private static void runInstruction(PrintWriter pw, OutputStream os, InputStream is, Scanner readFromMemory, int instruction)
	
	public static void incrementInstructionCount() {
		if(interruptProcessing == false) {
			instructionCount++;
		}
	}
	
	public static void timerInterruptHandler(Scanner readFromMemory, OutputStream os, InputStream is, PrintWriter pw) {
		userMode = false;
		int operand = SP;
		SP = TOP_OF_SYSTEM_MEMORY;
		pushToStack(pw, is, os, operand);
		
		operand = PC;
		PC = 1000;
		pushToStack(pw, is, os, operand);
		
	} //end public static void timerInterruptHandler(Scanner readFromMemory, OutputStream os, InputStream is, PrintWriter pw)
	
	private static int popFromStack(PrintWriter pw, InputStream is, OutputStream os, Scanner readFromMemory) {
		int read = readFromMemory(pw, is, os, readFromMemory, SP);
		writeToMemory(pw, is, os, SP++, 0);
		return read;
	} //end private static int popFromStack(PrintWriter pw, InputStream is, OutputStream os, Scanner readFromMemory)
	
	static private void pushToStack(PrintWriter pw, InputStream is, OutputStream os, int value) {
		SP--;
		writeToMemory(pw, is, os, SP, 0);
	} //end static private void pushToStack(PrintWriter pw, InputStream is, OutputStream os, int value)
	
	private static void writeToMemory(PrintWriter pw, InputStream is, OutputStream os, int memoryAddress, int data) {
		pw.printf("2-" + memoryAddress + "-" + data + "\n");
		pw.flush();
	} //end private static void writeToMemory(PrintWriter pw, InputStream is, OutputStream os, int memoryAddress, int data)
	
	private static int readFromMemory(PrintWriter pw, InputStream is, OutputStream os, Scanner readFromMemory, int memoryAddress) {
		
		if(!isMemoryViolated(memoryAddress)) {
			pw.printf("1-" + memoryAddress + "\n");
			pw.flush();
			
			if(readFromMemory.hasNext()) {
				String capture = readFromMemory.next();
				
				if(!capture.isEmpty()) {
					return (Integer.parseInt(capture));
				} //end if(capture!= null)
			}// end if(readFromMemory.hasNext())
		} //end if(!isMemoryViolated(memoryAddress))
		
		return -1;
		
	}
	
	public static boolean isMemoryViolated(int address) {
		if(address > 1000 && userMode) {
			System.out.print("You cannot access the system stack.");
			System.exit(0);
		}//end if(address > 1000 && userMode)
		
		return false;
	} //end method public static boolean isMemoryViolated(int address)
	
	public static void sendFileNametoMemory(PrintWriter pw, InputStream is, OutputStream os, String fileName) {
		pw.printf(fileName + "\n");
		pw.flush();
	}
}

/*
	Name of the Student: Navneet Viswanadha
	Class: CS 4348
	Section: 501
	Semester: Fall 2019
	Project 1 - Simulates a low-level, simplistic CPU and Memory 
	File: Memory.java (Simulates the memory portion of the project)
*/

/* Import required libraries */

import java.util.*;
import java.io.*;


public class Memory {
	private static final int[] memoryArray = new int[2000];
	
	public static int[] getMemoryarray() {
		return memoryArray;
	}

	public static void main(String args[]) {
		try {
			File instructionsFile = null;
			Scanner readFromCPU = new Scanner(System.in);
			
			String fileName = readFromCPU.nextLine();
			
			if(fileName != null) {
				instructionsFile = new File(fileName);
				
				if(!instructionsFile.exists()) {
					System.out.println("A file with the given name doesn't exist.");
					System.exit(0);
				} //end if(!instructionsFile.exists())
			}else { //if a file name is not provided
				System.out.println("No file name provided");
				System.exit(0);
			} //end else
			
			init(instructionsFile);
			
			/*Infinite loop constantly on the lookout for signals from the CPU for Memory */
			while(true) {
				if(readFromCPU.hasNext()) {
					String CPUInstruction = readFromCPU.nextLine();
					
					if(CPUInstruction != null && !CPUInstruction.isEmpty()) {
						String[] strarr = CPUInstruction.split("-");
						
						if(strarr[0] == "2") { //write instruction
							int address = Integer.parseInt(strarr[1]);
							int data = Integer.parseInt(strarr[2]);
		
							write(address, data);
						}else {
							int address = Integer.parseInt(strarr[1]);
							System.out.println(read(address));
						} //end else
					}//end if(CPUInstruction != null && !CPUInstruction.isEmpty())
				} else {  /* end if(readFromCPU.hasNext()) */
					break; //no instructions left
				} //end else
			}//end while(true)
			
			readFromCPU.close();
		} //end try block
		
		catch(Exception e) {
			e.printStackTrace();
		} //end catch(Exception e)
	}//end method public static void main(String args[])
	
	public static int read(int readAddress) {
		try {
			if(readAddress >= 0 && readAddress < 2000) {
				int[] memory = getMemoryarray();
				return memory[readAddress];
			} //end if(readAddress >= 0 && readAddress < 2000)
			
			throw new Exception("");
			
		} catch(Exception e) {
			System.out.println(e);
		} //end catch(Exception e)
		 return -1;
	} //end method public static int read(int readAddress)
	
	public static void write(int writeAddress, int data) {
		memoryArray[writeAddress] = data;
	}//end method public static void write(int writeAddress)
	
	public static void init(File instructionsFile) {
		try {
			Scanner readFile = new Scanner(instructionsFile);
			int memoryIndex = 0;
			String stringRead;
			
			while(readFile.hasNextLine()) {
				if(readFile.hasNextInt()) {
					memoryArray[memoryIndex] = readFile.nextInt();
					memoryIndex++;
				} else {
					stringRead = readFile.next();
					
					if(stringRead.equals("//")) {
						readFile.nextLine(); //Skip line
					}else if(stringRead.charAt(0) == '.') {
						stringRead = stringRead.substring(1);
						memoryIndex = Integer.parseInt(stringRead);
					}else {
						readFile.nextLine(); //skip line
					} //end else
				}//end else
			} //end while(readFile.hasNextLine())
			
			readFile.close();
			
		} catch(Exception e) {
			e.printStackTrace();
		}//end catch(Exception e)
	} //end public static void init(File instructionsFile)
} //end class memory

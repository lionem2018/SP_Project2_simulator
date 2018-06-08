package SP18_simulator;

import java.io.*;

/**
 * SicLoader는 프로그램을 해석해서 메모리에 올리는 역할을 수행한다. 이 과정에서 linker의 역할 또한 수행한다. <br>
 * <br>
 * SicLoader가 수행하는 일을 예를 들면 다음과 같다.<br>
 * - program code를 메모리에 적재시키기<br>
 * - 주어진 공간만큼 메모리에 빈 공간 할당하기<br>
 * - 과정에서 발생하는 symbol, 프로그램 시작주소, control section 등 실행을 위한 정보 생성 및 관리
 */
public class SicLoader
{
	ResourceManager rMgr;

	private int currentSection;

	public SicLoader(ResourceManager resourceManager)
	{
		// 필요하다면 초기화
		setResourceManager(resourceManager);
		currentSection = 0;
	}

	/**
	 * Loader와 프로그램을 적재할 메모리를 연결시킨다.
	 * 
	 * @param rMgr
	 */
	public void setResourceManager(ResourceManager resourceManager)
	{
		this.rMgr = resourceManager;
	}

	/**
	 * object code를 읽어서 load과정을 수행한다. load한 데이터는 resourceManager가 관리하는 메모리에 올라가도록 한다.
	 * load과정에서 만들어진 symbol table 등 자료구조 역시 resourceManager에 전달한다.
	 * @param objectCode 읽어들인 파일
	 */
	public void load(File objectCode){
		try
		{
			FileReader fileReader = new FileReader(objectCode);
			BufferedReader bufReader = new BufferedReader(fileReader);
			String line = "";
			
			while((line = bufReader.readLine()) != null)
			{
				if(line.length() == 0)
					continue;
				
				switch(line.charAt(0))
				{
					case 'H':
						int progNameLength = line.length()-13;
						String programName = line.substring(1, progNameLength);
						
						rMgr.setProgName(programName, currentSection);
						rMgr.setProgStartAddr(line.substring(progNameLength+1, progNameLength+7), currentSection);
						rMgr.setProgLength(line.substring(line.length()-6, line.length()), currentSection);
						
						rMgr.symtabList.putSymbol(programName, rMgr.getProgStartAddr(currentSection));
						break;
					
					case 'D':
						int symNameLength = 0;
						int symNameStart = 1;
						
						for(int i = 1; i < line.length(); i++)
						{
							if(line.charAt(i) == '0')
							{
								String symbol = line.substring(symNameStart, symNameStart+symNameLength);
								int address = Integer.parseInt(line.substring(symNameStart+symNameLength, symNameStart+symNameLength+6), 16);
								
								rMgr.symtabList.putSymbol(symbol, address);
								symNameStart += symNameLength + 6;
								i += 5;
								symNameLength = 0;
								continue;
							}
							symNameLength++;
						}
						break;
						
					case 'R':
						
						break;
						
					case 'T':
						int currentAddr = Integer.parseInt(line.substring(1, 7), 16) + rMgr.getProgStartAddr(currentSection);
						int codeLength = Integer.parseInt(line.substring(7, 9), 16);
						char[] packedOpcode = packing(line.substring(9, line.length()).toCharArray());
						
						rMgr.setMemory(currentAddr, packedOpcode, codeLength);
						break;
						
					case 'M':
						
						int modifLocation = Integer.parseInt(line.substring(1, 7), 16) + +rMgr.getProgStartAddr(currentSection);
						int modifSize = Integer.parseInt(line.substring(7, 9), 16);
						char modifMode = line.charAt(9);
						String symbol = line.substring(10, line.length());
						
						rMgr.extabList.putExSymbol(symbol, modifLocation, modifSize, modifMode, currentSection);
						break;
						
					case 'E':
						currentSection++;
						break;

				}
			}
			
			for(int i = 0; i < rMgr.extabList.size(); i++)
			{
				String symbol = rMgr.extabList.getSymbol(i);
				int modifSize = rMgr.extabList.getModifSize(i);
				char modifMode = rMgr.extabList.getModifMode(i);
				
				String modifAddr = "000000";
				if(modifSize == 5)
					modifAddr = String.format("%05X", rMgr.symtabList.search(symbol));
				else if(modifSize == 6)
					modifAddr = String.format("%06X", rMgr.symtabList.search(symbol));
				char[] packedAddr = packing(modifAddr.toCharArray());
				
				rMgr.modifMemory(rMgr.extabList.getaddress(i), packedAddr, packedAddr.length, modifMode);
			}
			
			rMgr.printMemory();
			rMgr.extabList.print();
			bufReader.close();
		}
		catch (FileNotFoundException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (IOException e)
		{
			
		}
		
	};

	private char[] packing(char[] inputData)
	{
		int length = (inputData.length / 2) + (inputData.length % 2);
		char[] outputData = new char[length];

		int upByte = 0;
		int downByte = 0;

		if (inputData.length % 2 == 0)
		{

			for (int i = 0; i < length; i++)
			{
				upByte = inputData[i * 2] - '0';
				downByte = inputData[i * 2 + 1] - '0';
				if (upByte >= 10)
					upByte -= 7;
				if (downByte >= 10)
					downByte -= 7;

				outputData[i] = (char) ((upByte << 8) + downByte);
			}
		}
		else
		{
			downByte = (inputData[0] - '0');
			if(downByte >= 10)
				downByte -= 7;
			outputData[0] = (char) downByte;
			
			for (int i = 1; i < length; i++)
			{
				upByte = inputData[i * 2 - 1] - '0';
				downByte = inputData[i * 2] - '0';
				if (upByte >= 10)
					upByte -= 7;
				if (downByte >= 10)
					downByte -= 7;

				outputData[i] = (char) ((upByte << 8) + downByte);
			}
		}
		return outputData;
	}
}

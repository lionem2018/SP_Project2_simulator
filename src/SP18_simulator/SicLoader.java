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

	// 현재 로드하는 컨트롤 섹션을 표시한다.
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
			// 인자로 들어온 이름의 파일을 열어 오브젝트 코드를 읽어 들인다.
			FileReader fileReader = new FileReader(objectCode);
			BufferedReader bufReader = new BufferedReader(fileReader);
			String line = "";
			
			// 파일의 끝에 도달할 때까지 한 줄씩 읽어 들인다.
			while((line = bufReader.readLine()) != null)
			{
				// 읽어들인 문자열이 비어있다면 다음 라인을 읽는다.
				if(line.length() == 0)
					continue;
				
				// 문자열의 처음 글자로 들어가 있는 레코드 표시로 처리를 달리한다.
				switch(line.charAt(0))
				{
					// Header Record인 경우,
					// 해당 섹션 프로그램의 이름과 시작주소, 프로그램 길이, 프로그램의 메모리상의 시작주소 등을 저장한다.
					case 'H':
						int progNameLength = line.length()-13;
						String programName = line.substring(1, progNameLength);
						
						rMgr.setProgName(programName, currentSection);
						rMgr.setProgStartAddr(line.substring(progNameLength+1, progNameLength+7), currentSection);
						rMgr.setProgLength(line.substring(line.length()-6, line.length()), currentSection);
						
						rMgr.symtabList.putSymbol(programName, rMgr.getProgStartAddr(currentSection));
						break;
					
					// Define Record인 경우,
					// 해당 symbol과 그 주소를 테이블에 저장한다.
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
					
					// Refer Record인 경우, 넘어간다.
					case 'R':
						break;
					
					// Text Record인 경우,
					// 시작주소부터 명시된 길이만큼 메모리에 오브젝트 코드를 로드한다.
					// 오브젝트 코드를 로드하기 전, 한 char형에 두 글자가 담기도록 packing 과정을 거친다.
					case 'T':
						int currentAddr = Integer.parseInt(line.substring(1, 7), 16) + rMgr.getProgStartAddr(currentSection);
						int codeLength = Integer.parseInt(line.substring(7, 9), 16);
						char[] packedOpcode = packing(line.substring(9, line.length()).toCharArray());
						
						rMgr.setMemory(currentAddr, packedOpcode, codeLength);
						break;
					
					// Modification Record인 경우,
					// 수정을 위한 EXTAB에 수정할 주소, 수정할 부분의 개수, 주소를 더할 것인지 뺄 것인지, 심볼 정보를 저장한다.
					case 'M':
						int modifLocation = Integer.parseInt(line.substring(1, 7), 16) + +rMgr.getProgStartAddr(currentSection);
						int modifSize = Integer.parseInt(line.substring(7, 9), 16);
						char modifMode = line.charAt(9);
						String symbol = line.substring(10, line.length());
						
						rMgr.extabList.putExSymbol(symbol, modifLocation, modifSize, modifMode, currentSection);
						break;
					
					// End Record인 경우,
					// 컨트롤 섹션을 표시하는 currentSection의 값을 올린다.
					case 'E':
						currentSection++;
						break;

				}
			}
			
			// EXTAB에 저장되어있는 수정 정보들을 가지고
			// 메모리에 올라가있는 명령어를 수정한다.
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

	// 단순 문자 형태의 데이터를 한 char형 안에 두 개씩 묶는 메소드
	// 예를 들어 1,7이 있다면,
	// 하나의 char형에 0000 0001 0000 0007이 되도록 생성
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

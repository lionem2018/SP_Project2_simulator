package SP18_simulator;

import java.io.*;
import java.util.ArrayList;
import java.util.*;

/**
 * ResourceManager는 컴퓨터의 가상 리소스들을 선언하고 관리하는 클래스이다. 크게 네가지의 가상 자원 공간을 선언하고, 이를
 * 관리할 수 있는 함수들을 제공한다.<br>
 * <br>
 * 
 * 1) 입출력을 위한 외부 장치 또는 device<br>
 * 2) 프로그램 로드 및 실행을 위한 메모리 공간. 여기서는 64KB를 최대값으로 잡는다.<br>
 * 3) 연산을 수행하는데 사용하는 레지스터 공간.<br>
 * 4) SYMTAB 등 simulator의 실행 과정에서 사용되는 데이터들을 위한 변수들. <br>
 * <br>
 * 2번은 simulator위에서 실행되는 프로그램을 위한 메모리공간인 반면, 4번은 simulator의 실행을 위한 메모리 공간이라는 점에서
 * 차이가 있다.
 */
public class ResourceManager
{
	/**
	 * deviceManager는 디바이스의 이름을 입력받았을 때 해당 디바이스의 파일 입출력 관리 클래스를 리턴하는 역할을 한다. 예를 들어,
	 * 'A1'이라는 디바이스에서 파일을 read모드로 열었을 경우, hashMap에 <"A1", scanner(A1)> 등을 넣음으로서 이를
	 * 관리할 수 있다. <br>
	 * <br>
	 * 변형된 형태로 사용하는 것 역시 허용한다.<br>
	 * 예를 들면 key값으로 String대신 Integer를 사용할 수 있다. 파일 입출력을 위해 사용하는 stream 역시 자유로이 선택,
	 * 구현한다. <br>
	 * <br>
	 * 이것도 복잡하면 알아서 구현해서 사용해도 괜찮습니다.
	 */
	private HashMap<String, Object> deviceManager = new HashMap<String, Object>();
	private char[] memory = new char[65536]; // String으로 수정해서 사용하여도 무방함, 인자 하나가 1byte
	private int[] register = new int[10];
	private double register_F;

	SymbolTable symtabList = new SymbolTable();
	// 이외에도 필요한 변수 선언해서 사용할 것.
	
	private int currentSection;
	private int readPointer = 0;
	SymbolTable extabList = new SymbolTable();

	private List<String> progNameList = new ArrayList<>();
	private List<Integer> progLengthList = new ArrayList<>();
	private List<Integer> progStartAddrList = new ArrayList<>();

	/**
	 * 메모리, 레지스터등 가상 리소스들을 초기화한다.
	 */
	public void initializeResource()
	{	
		for (int i = 0; i < register.length; i++)
			register[i] = 0;

		register_F = 0;
		currentSection = 0;
		register[SicSimulator.X_REGISTER] = progStartAddrList.get(currentSection);
		register[SicSimulator.L_REGISTER] = SicSimulator.INIT_RETADR;
	}

	/**
	 * deviceManager가 관리하고 있는 파일 입출력 stream들을 전부 종료시키는 역할. 프로그램을 종료하거나 연결을 끊을 때
	 * 호출한다.
	 */
	public void closeDevice()
	{
		Iterator<String> it = deviceManager.keySet().iterator();

		while (it.hasNext())
		{
			String key = it.next();
			Object stream = deviceManager.get(key);

			try
			{
				if (stream instanceof FileReader)
				{
					((FileReader) stream).close();
				}
				else if (stream instanceof FileWriter)

				{
					((FileWriter) stream).close();
				}
			}
			catch (IOException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
	}

	/**
	 * 디바이스를 사용할 수 있는 상황인지 체크. TD명령어를 사용했을 때 호출되는 함수. 입출력 stream을 열고 deviceManager를
	 * 통해 관리시킨다.
	 * 
	 * @param devName
	 *            확인하고자 하는 디바이스의 번호,또는 이름
	 */
	public void testDevice(String devName)
	{
		try
		{
			File file = new File(devName);
			if (devName.equals("F1"))
			{
				FileReader fileReader = new FileReader(file);
				deviceManager.put(devName, fileReader);
				register[SicSimulator.SW_REGISTER] = 1;
			}
			else if (devName.equals("05"))
			{
				FileWriter fileWriter = new FileWriter(file, true);
				deviceManager.put(devName, fileWriter);
				register[SicSimulator.SW_REGISTER] = 1;
			}
		}
		catch (FileNotFoundException e)
		{
			// TODO Auto-generated catch block
			register[SicSimulator.SW_REGISTER] =  0;
			e.printStackTrace();
		}
		catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * 디바이스로부터 원하는 개수만큼의 글자를 읽어들인다. RD명령어를 사용했을 때 호출되는 함수.
	 * 
	 * @param devName
	 *            디바이스의 이름
	 * @param num
	 *            가져오는 글자의 개수
	 * @return 가져온 데이터
	 */
	public char readDevice(String devName) /////////////////////////////////////////////////////////////
	{
		char input = ' ';
		try
		{
			FileReader fileReader = (FileReader) deviceManager.get(devName);
			int inputChar = 0;
			int index = 0;
			
			while(index <= readPointer)
			{
				inputChar = fileReader.read();
				index++;
			}
			
			if (inputChar != -1)
			{
				input = (char) inputChar;
			}
			else
				input = 0;
			
			readPointer++;
		}
		catch (FileNotFoundException e)
		{
			// 파일을 찾지 못했을 때에 대한 핸들링
		}
		catch (IOException e)
		{
			System.out.println(e);
		}

		return input;
	}

	/**
	 * 디바이스로 원하는 개수 만큼의 글자를 출력한다. WD명령어를 사용했을 때 호출되는 함수.
	 * 
	 * @param devName
	 *            디바이스의 이름
	 * @param data
	 *            보내는 데이터
	 * @param num
	 *            보내는 글자의 개수
	 */
	public void writeDevice(String devName)
	{
		try
		{
			FileWriter fileWriter = (FileWriter) deviceManager.get(devName);

			fileWriter.write((char)(register[SicSimulator.A_REGISTER] & 255));
			fileWriter.flush();

		}
		catch (FileNotFoundException e)
		{
			// 파일을 찾지 못했을 때에 대한 핸들링
		}
		catch (IOException e)
		{
			System.out.println(e);
		}
	}

	/**
	 * 메모리의 특정 위치에서 원하는 개수만큼의 글자를 가져온다.
	 * 
	 * @param location
	 *            메모리 접근 위치 인덱스
	 * @param num
	 *            데이터 개수
	 * @return 가져오는 데이터
	 */
	public char[] getMemory(int location, int num)
	{
		char[] result = new char[num];

		for (int i = location; i < location + num; i++)
		{
			result[i-location] = memory[i];
		}

		return result;
	}

	/**
	 * 메모리의 특정 위치에 원하는 개수만큼의 데이터를 저장한다.
	 * 
	 * @param locate
	 *            접근 위치 인덱스
	 * @param data
	 *            저장하려는 데이터
	 * @param num
	 *            저장하는 데이터의 개수
	 */
	public void setMemory(int locate, char[] data, int num)
	{
		for (int i = locate; i < locate + num; i++)
		{

			memory[i] = data[i - locate];

			System.out.print(data[i - locate] >> 8);
			System.out.print(" ");
			System.out.print(data[i - locate] & 255);
		}
	}

	/**
	 * 번호에 해당하는 레지스터가 현재 들고 있는 값을 리턴한다. 레지스터가 들고 있는 값은 문자열이 아님에 주의한다.
	 * 
	 * @param regNum
	 *            레지스터 분류번호
	 * @return 레지스터가 소지한 값
	 */
	public int getRegister(int regNum)
	{
		return register[regNum];

	}
	
	public double getFRegister()
	{
		return register_F;
	}

	/**
	 * 번호에 해당하는 레지스터에 새로운 값을 입력한다. 레지스터가 들고 있는 값은 문자열이 아님에 주의한다.
	 * 
	 * @param regNum
	 *            레지스터의 분류번호
	 * @param value
	 *            레지스터에 집어넣는 값
	 */
	public void setRegister(int regNum, int value)
	{
		register[regNum] = value;
	}

	/**
	 * 주로 레지스터와 메모리간의 데이터 교환에서 사용된다. int값을 char[]형태로 변경한다.
	 * 
	 * @param data
	 * @return
	 */
	public char[] intToChar(int data)
	{
		char[] inputData = String.format("%X", data).toCharArray();
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

	/**
	 * 주로 레지스터와 메모리간의 데이터 교환에서 사용된다. char[]값을 int형태로 변경한다.
	 * 
	 * @param data
	 * @return
	 */
	public int byteToInt(char[] data)
	{
		int result = 0;
		for(int i = 0; i < data.length; i++)
		{
			result = result << 4;
			result += (data[i] >> 8);
			result = result << 4;
			result += (data[i] & 255);
		}
		return result;
	}

	///////////////////////////////////////////////////
	public void setProgName(String progName, int sectionNum)
	{
		progNameList.add(sectionNum, progName);
	}

	public void setProgStartAddr(String startAddr, int sectionNum)
	{
		int addr = Integer.parseInt(startAddr, 16);

		if (sectionNum > 0)
		{
			addr += progStartAddrList.get(sectionNum - 1) + progLengthList.get(sectionNum - 1);
		}

		progStartAddrList.add(sectionNum, addr);
	}

	public void setProgLength(String length, int sectionNum)
	{
		progLengthList.add(sectionNum, Integer.parseInt(length, 16));
	}

	public int getProgStartAddr(int sectionNum)
	{
		return progStartAddrList.get(sectionNum);
	}
	
	public String getProgName(int sectionNum)
	{
		return progNameList.get(sectionNum);
	}
	
	public int getProgLength(int section)
	{
		return progLengthList.get(section);
	}
	
	public int getProgCount()
	{
		return progNameList.size();
	}

	public void modifMemory(int locate, char[] data, int num, char modifMode)
	{
		if (modifMode == '+')
		{
			for (int i = locate; i < locate + num; i++)
			{
				memory[i] += data[i - locate];
			}
		}
		else if (modifMode == '-')
		{
			for (int i = locate; i < locate + num; i++)
			{
				memory[i] -= data[i - locate];
			}
		}
	}

	public void printMemory()
	{
		File file = new File("test.txt");
		try
		{
			BufferedWriter bufWriter = new BufferedWriter(new FileWriter(file));
			for (int i = 0; i < memory.length; i++)
			{

				if (i % 4 == 0)
					bufWriter.write(" ");
				if (i % 16 == 0)
				{
					bufWriter.newLine();
					bufWriter.write(String.format("%04X ", i));
				}

				bufWriter.write(Integer.toHexString(memory[i] >> 8));
				bufWriter.write(Integer.toHexString(memory[i] & 255));
			}
			bufWriter.close();
		}
		catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
}
package SP18_simulator;

import java.io.File;
import java.util.*;

/**
 * 시뮬레이터로서의 작업을 담당한다. VisualSimulator에서 사용자의 요청을 받으면 이에 따라
 * ResourceManager에 접근하여 작업을 수행한다.  
 * 
 * 작성중의 유의사항 : <br>
 *  1) 새로운 클래스, 새로운 변수, 새로운 함수 선언은 얼마든지 허용됨. 단, 기존의 변수와 함수들을 삭제하거나 완전히 대체하는 것은 지양할 것.<br>
 *  2) 필요에 따라 예외처리, 인터페이스 또는 상속 사용 또한 허용됨.<br>
 *  3) 모든 void 타입의 리턴값은 유저의 필요에 따라 다른 리턴 타입으로 변경 가능.<br>
 *  4) 파일, 또는 콘솔창에 한글을 출력시키지 말 것. (채점상의 이유. 주석에 포함된 한글은 상관 없음)<br>
 * 
 * <br><br>
 *  + 제공하는 프로그램 구조의 개선방법을 제안하고 싶은 분들은 보고서의 결론 뒷부분에 첨부 바랍니다. 내용에 따라 가산점이 있을 수 있습니다.
 */
public class SicSimulator {
	
	// 레지스터 번호 상수
	public static final int A_REGISTER = 0;
	public static final int X_REGISTER = 1;
	public static final int L_REGISTER = 2;
	public static final int B_REGISTER = 3;
	public static final int S_REGISTER = 4;
	public static final int T_REGISTER = 5;
	public static final int F_REGISTER = 6;
	public static final int PC_REGISTER = 8;
	public static final int SW_REGISTER = 9;
	
	// 초기 L_Register 설정 값
	public static final int INIT_RETADR = 0x4000;
	
	private ResourceManager rMgr;
	
	// 현재 명령어의 TargetAddress
	private int targetAddr;
	// 현재 명령어가 사용하고 있는 입출력 디바이스 정보
	private String currentDevice;
	
	// GUI 컴포넌트를 통해 리스 형태로 보여줄 instrucion 리스트와
	// 명령어 실행 형태를 보여줄 log 리스트
	private List<String> instList = new ArrayList<>();
	private List<String> logList = new ArrayList<>();
	
	public SicSimulator(ResourceManager resourceManager) {
		// 필요하다면 초기화 과정 추가
		this.rMgr = resourceManager;
		targetAddr = 0;
		currentDevice = "";
	}

	/**
	 * 레지스터, 메모리 초기화 등 프로그램 load와 관련된 작업 수행.
	 * 단, object code의 메모리 적재 및 해석은 SicLoader에서 수행하도록 한다. 
	 */
	public void load(File program) {
		/* 메모리 초기화, 레지스터 초기화 등*/
		rMgr.initializeResource();
	}

	/**
	 * 1개의 instruction이 수행된 모습을 보인다. 
	 */
	public void oneStep() {
		// bytes: 명령어의 상위 2바이트를 메모리 상에서 가져옴
		// temp: 단순 가져온 명령어를 분석하기 쉬운 int형으로 변환한 것
		// opcode: 명령어의 OPCODE 값
		// extForm: 4형식을 사용한 명령어인지 표시
		// pcRelative: PC Relative addressing을 사용하는지 표시
		// immediate: immediate addressing을 사용하는지 표시
		// indirect: indirect addressing을 사용하는지 표시
		// registerNum: 레지스터 명령어일 경우 피연산자 레지스터 번호
		// difference: 두 값을 비교하는 명령어일 경우, 그 비교한 두 값의 차이
		// instruction: 메모리 상에서 가져온 전체 명령어
		char [] bytes = rMgr.getMemory(rMgr.getRegister(PC_REGISTER), 2);
		int temp = (bytes[0] >>> 4) + (bytes[0] & 15);
		int opcode = temp;
		boolean extForm = false;
		boolean pcRelative = false;
		boolean immediate = false;
		boolean indirect = false;
		int registerNum = 0;
		int difference = 0;
		char [] instruction = new char[1];
		
		// 상위 1바이트를 가지고
		// indirect나 immediate addressing 표시
		if((temp & 2) == 2)
		{
			opcode -= 2;
			indirect = true;
		}
		
		if((temp & 1) == 1)
		{
			opcode -= 1;
			immediate = true;
		}
		
		// 두번째 바이트를 가지고
		// 확장된 4형식 명령어를 사용하였는지,
		// PC relative를 사용하였는지 표시
		temp = (bytes[1] >>> 8);
		extForm = (temp & 1) == 1;
		pcRelative = (temp & 2) == 2;
		// target address 초기화
		targetAddr = 0;
		
		// 분석된 opcode의 값에 따라서 동작을 달리함
		switch(opcode)
		{
			case 0x14:  // STL 명령어: L 레지스터 값을 해당 주소에 저장하는 명령어
				addLog("STL");
				if(extForm)  // 4형식 명령어인 경우,
				{
					logList.set(logList.size()-1, "+" + logList.get(logList.size()-1));
					instruction = rMgr.getMemory(rMgr.getRegister(PC_REGISTER), 4);
					targetAddr = ((instruction[1] & 15) << 16) + ((instruction[2] >>> 8) << 12) + ((instruction[2] & 15) << 8) + ((instruction[3] >>> 8) << 4) + (instruction[3] & 15);
					rMgr.setRegister(PC_REGISTER, rMgr.getRegister(PC_REGISTER) + 4);
					
					char[] data = new char[3];
					rMgr.setMemory(targetAddr, data, 3);
					data = rMgr.intToChar(rMgr.getRegister(L_REGISTER));
					rMgr.modifMemory(targetAddr + (3 - data.length), data, data.length, '+');
				}
				else  // 3형식 명령어인 경우
				{
					instruction = rMgr.getMemory(rMgr.getRegister(PC_REGISTER), 3);
					targetAddr = ((instruction[1] & 15) << 8) + ((instruction[2] >>> 8) << 4) + (instruction[2] & 15);
					rMgr.setRegister(PC_REGISTER, rMgr.getRegister(PC_REGISTER) + 3);
					
					if(pcRelative)
						targetAddr += rMgr.getRegister(PC_REGISTER);
					char[] data = new char[3];
					rMgr.setMemory(targetAddr, data, 3);
					data = rMgr.intToChar(rMgr.getRegister(L_REGISTER));
					rMgr.modifMemory(targetAddr + (3 - data.length), data, data.length, '+');				
				}
				break;
				
			case 0x48: // JSUB 명령어: 주소값으로 들어온 곳으로 이동
				addLog("JSUB");
				if(extForm)  // 4형식 명령어인 경우,
				{
					logList.set(logList.size()-1, "+" + logList.get(logList.size()-1));
					instruction = rMgr.getMemory(rMgr.getRegister(PC_REGISTER), 4);
					targetAddr = ((instruction[1] & 15) << 16) + ((instruction[2] >> 8) << 12) + ((instruction[2] & 15) << 8) + ((instruction[3] >> 8) << 4) + (instruction[3] & 15);
					rMgr.setRegister(PC_REGISTER, rMgr.getRegister(PC_REGISTER) + 4);
					
					rMgr.setRegister(L_REGISTER, rMgr.getRegister(PC_REGISTER));
					rMgr.setRegister(PC_REGISTER, targetAddr);
					rMgr.setCurrentSection();
				}
				else   // 3형식 명령어인 경우,
				{
					instruction = rMgr.getMemory(rMgr.getRegister(PC_REGISTER), 3);
					targetAddr = ((instruction[1] & 15) << 8) + ((instruction[2] >> 8) << 4) + (instruction[2] & 15);
					rMgr.setRegister(PC_REGISTER, rMgr.getRegister(PC_REGISTER) + 3);
					
					rMgr.setRegister(L_REGISTER, rMgr.getRegister(PC_REGISTER));
					
					if(pcRelative)
						targetAddr += rMgr.getRegister(PC_REGISTER);
					rMgr.setRegister(PC_REGISTER, targetAddr);
					rMgr.setCurrentSection();
				}
				break;
				
			case 0x00:  // LDA 명령어: 해당 피연산자 주소에 저장된 값을 A 레지스터로 가져옴
				addLog("LDA");
				if(extForm)  // 4형식인 명령어인 경우,
				{
					logList.set(logList.size()-1, "+" + logList.get(logList.size()-1));
					instruction = rMgr.getMemory(rMgr.getRegister(PC_REGISTER), 4);
					targetAddr = ((instruction[1] & 15) << 16) + ((instruction[2] >> 8) << 12) + ((instruction[2] & 15) << 8) + ((instruction[3] >> 8) << 4) + (instruction[3] & 15);
					rMgr.setRegister(PC_REGISTER, rMgr.getRegister(PC_REGISTER) + 4);

					char[] data = rMgr.getMemory(targetAddr, 3);
					rMgr.setRegister(A_REGISTER, rMgr.byteToInt(data));	
				}
				else  // 3형식 명령어인 경우,
				{
					instruction = rMgr.getMemory(rMgr.getRegister(PC_REGISTER), 3);
					targetAddr = ((instruction[1] & 15) << 8) + ((instruction[2] >> 8) << 4) + (instruction[2] & 15);
					rMgr.setRegister(PC_REGISTER, rMgr.getRegister(PC_REGISTER) + 3);

					if(pcRelative) // PC relative를 사용하는 경우,
					{
						targetAddr += rMgr.getRegister(PC_REGISTER);
					
						char[] data = rMgr.getMemory(targetAddr, 3);
						rMgr.setRegister(A_REGISTER, rMgr.byteToInt(data));
					}
					else if(immediate)  // immediate를 사용하는 경우,
					{
						rMgr.setRegister(A_REGISTER, targetAddr);
					}
				}
				break;
				
			case 0x28: // COMP 명령어: A레지스터 값과 명령어에 주어진 값과 비교한다.
				addLog("COMP");
				if(extForm)  // 4형식 명령어를 사용하는 경우,
				{
					logList.set(logList.size()-1, "+" + logList.get(logList.size()-1));
					instruction = rMgr.getMemory(rMgr.getRegister(PC_REGISTER), 4);
					targetAddr = ((instruction[1] & 15) << 16) + ((instruction[2] >>> 8) << 12) + ((instruction[2] & 15) << 8) + ((instruction[3] >>> 8) << 4) + (instruction[3] & 15);
					rMgr.setRegister(PC_REGISTER, rMgr.getRegister(PC_REGISTER) + 4);
					
					if(immediate)
					{
						difference = rMgr.getRegister(A_REGISTER) - targetAddr;
						rMgr.setRegister(SW_REGISTER, difference);
						System.out.println("SW: " + rMgr.getRegister(SW_REGISTER));
					}
				}
				else  // 3형식 명령어를 사용하는 경우,
				{
					instruction = rMgr.getMemory(rMgr.getRegister(PC_REGISTER), 3);
					targetAddr = ((instruction[1] & 15) << 8) + ((instruction[2] >>> 8) << 4) + (instruction[2] & 15);
					rMgr.setRegister(PC_REGISTER, rMgr.getRegister(PC_REGISTER) + 3);

					if(immediate)
					{
						difference = rMgr.getRegister(A_REGISTER) - targetAddr;
						rMgr.setRegister(SW_REGISTER, difference);
						System.out.println("SW: " + rMgr.getRegister(SW_REGISTER));
					}
				}
				break;
		
			case 0x4c: // RSUB 명령어: L 레지스터에 저장되어있는 주소로 이동(호출 시점 다음 명령어로 돌아감)
				addLog("RSUB");
				instruction = rMgr.getMemory(rMgr.getRegister(PC_REGISTER), 3);
				rMgr.setRegister(PC_REGISTER, rMgr.getRegister(L_REGISTER));
				
				currentDevice = "";
				rMgr.setCurrentSection();
				break;
			
			case 0x50:  // LDCH 명령어: 해당 주소의 값을 A레지스터 하위 1바이트에 불러온다.
				logList.add("LDCH");
				if(extForm)  // 4형식 명령어를 사용하는 경우,
				{
					logList.set(logList.size()-1, "+" + logList.get(logList.size()-1));
					instruction = rMgr.getMemory(rMgr.getRegister(PC_REGISTER), 4);
					targetAddr = ((instruction[1] & 15) << 16) + ((instruction[2] >>> 8) << 12) + ((instruction[2] & 15) << 8) + ((instruction[3] >>> 8) << 4) + (instruction[3] & 15);
					rMgr.setRegister(PC_REGISTER, rMgr.getRegister(PC_REGISTER) + 4);
					
					char[] data = rMgr.getMemory(targetAddr + rMgr.getRegister(X_REGISTER), 1);
					rMgr.setRegister(A_REGISTER, rMgr.byteToInt(data));
					System.out.println((char)rMgr.getRegister(A_REGISTER));
					
				}
				else // 3형식 명령어를 사용하는 경우,
				{
					instruction = rMgr.getMemory(rMgr.getRegister(PC_REGISTER), 3);
					targetAddr = ((instruction[1] & 15) << 8) + ((instruction[2] >>> 8) << 4) + (instruction[2] & 15);
					rMgr.setRegister(PC_REGISTER, rMgr.getRegister(PC_REGISTER) + 3);
					
					if(pcRelative)  // PC relative를 사용하는 경우,
						targetAddr += rMgr.getRegister(PC_REGISTER);
					char[] data = rMgr.getMemory(targetAddr + rMgr.getRegister(X_REGISTER), 1);
					rMgr.setRegister(A_REGISTER, rMgr.byteToInt(data));
					System.out.println((char)rMgr.getRegister(A_REGISTER));
				}
				
				break;
			
			case 0xdc: // WD 명령어: 지정된 기기(또는 파일)에 A 레지스터 하위 1바이트의 값을 출력한다.
				addLog("WD");
				if(extForm)  // 4형식 명령어를 사용하는 경우,
				{
					logList.set(logList.size()-1, "+" + logList.get(logList.size()-1));
					instruction = rMgr.getMemory(rMgr.getRegister(PC_REGISTER), 4);
					targetAddr = ((instruction[1] & 15) << 16) + ((instruction[2] >>> 8) << 12) + ((instruction[2] & 15) << 8) + ((instruction[3] >>> 8) << 4) + (instruction[3] & 15);
					rMgr.setRegister(PC_REGISTER, rMgr.getRegister(PC_REGISTER) + 4);
					
					char[] deviceInfo = rMgr.getMemory(targetAddr, 1);
					String deviceName = String.format("%X%X", deviceInfo[0] >> 8, deviceInfo[0] & 15);
					System.out.println(deviceName);
					rMgr.writeDevice(deviceName);
					System.out.print(Integer.toBinaryString(rMgr.getRegister(A_REGISTER)));
				}
				else  // 3형식 명령어를 사용하는 경우,
				{
					instruction = rMgr.getMemory(rMgr.getRegister(PC_REGISTER), 3);
					targetAddr = ((instruction[1] & 15) << 8) + ((instruction[2] >>> 8) << 4) + (instruction[2] & 15);
					rMgr.setRegister(PC_REGISTER, rMgr.getRegister(PC_REGISTER) + 3);
					
					if(pcRelative)
						targetAddr += rMgr.getRegister(PC_REGISTER);
					char[] deviceInfo = rMgr.getMemory(targetAddr, 1);
					String deviceName = String.format("%X%X", deviceInfo[0] >> 8, deviceInfo[0] & 15);
					System.out.println(deviceName);
					rMgr.writeDevice(deviceName);
					System.out.print(Integer.toBinaryString(rMgr.getRegister(A_REGISTER)));
				}
				
				break;
				
			case 0x3c: // 피연산자로 들어온 주소로 프로그램 흐름을 이동한다.
				addLog("J");
				if(extForm)  // 4형식인 경우,
				{
					logList.set(logList.size()-1, "+" + logList.get(logList.size()-1));
					instruction = rMgr.getMemory(rMgr.getRegister(PC_REGISTER), 4);
					targetAddr = ((instruction[1] & 15) << 16) + ((instruction[2] >> 8) << 12) + ((instruction[2] & 15) << 8) + ((instruction[3] >> 8) << 4) + (instruction[3] & 15);
					if((instruction[1] & 15) == 15)  // 음수인 경우(상위 8비트가 F인 경우)
						targetAddr += (0xFFF << 20); // 상위 나머지 비트들도 F로 채워 음수값을 만든다.
					rMgr.setRegister(PC_REGISTER, rMgr.getRegister(PC_REGISTER) + 4);
					
					rMgr.setRegister(PC_REGISTER, targetAddr);
					rMgr.setCurrentSection();
				}
				else  // 3형식인 경우,
				{
					instruction = rMgr.getMemory(rMgr.getRegister(PC_REGISTER), 3);
					targetAddr = ((instruction[1] & 15) << 8) + ((instruction[2] >> 8) << 4) + (instruction[2] & 15);
					if((instruction[1] & 15) == 15)   // 음수인 경우(상위 8비트가 F인 경우)
						targetAddr += (0xFFFFF << 12);  // 상위 나머지 비트들도 F로 채워 음수값을 만든다.
					rMgr.setRegister(PC_REGISTER, rMgr.getRegister(PC_REGISTER) + 3);

					if(pcRelative)
					{
						targetAddr += rMgr.getRegister(PC_REGISTER);
						
						if(indirect & !immediate)
						{
							targetAddr = rMgr.byteToInt(rMgr.getMemory(targetAddr, 3));
						}
						
						rMgr.setRegister(PC_REGISTER, targetAddr);
					}
					rMgr.setCurrentSection();
				}
				break;
			
			case 0x0c:
				addLog("STA"); //STA 명령어: A 레지스터에 저장된 값을 지정된 주소로 저장한다.
				if(extForm)  // 4형식 명령어인 경우,
				{
					logList.set(logList.size()-1, "+" + logList.get(logList.size()-1));
					instruction = rMgr.getMemory(rMgr.getRegister(PC_REGISTER), 4);
					targetAddr = ((instruction[1] & 15) << 16) + ((instruction[2] >> 8) << 12) + ((instruction[2] & 15) << 8) + ((instruction[3] >> 8) << 4) + (instruction[3] & 15);
					rMgr.setRegister(PC_REGISTER, rMgr.getRegister(PC_REGISTER) + 4);
					
					char[] data = new char[3];
					rMgr.setMemory(targetAddr, data, 3);
					data = rMgr.intToChar(rMgr.getRegister(A_REGISTER));
					rMgr.modifMemory(targetAddr + (3 - data.length), data, data.length, '+');
				}
				else  // 3형식 명령어인 경우,
				{
					instruction = rMgr.getMemory(rMgr.getRegister(PC_REGISTER), 3);
					targetAddr = ((instruction[1] & 15) << 8) + ((instruction[2] >> 8) << 4) + (instruction[2] & 15);
					rMgr.setRegister(PC_REGISTER, rMgr.getRegister(PC_REGISTER) + 3);
					
					if(pcRelative)
						targetAddr += rMgr.getRegister(PC_REGISTER);
					char[] data = new char[3];
					rMgr.setMemory(targetAddr, data, 3);
					data = rMgr.intToChar(rMgr.getRegister(A_REGISTER));
					rMgr.modifMemory(targetAddr + (3 - data.length), data, data.length, '+');
				}
				break;
				
			case 0xb4:  // CLEAR 명령어: 해당 레지스터의 값을 0으로 초기화 시키는 명령어
				logList.add("CLEAR");
				instruction = rMgr.getMemory(rMgr.getRegister(PC_REGISTER), 2);
				rMgr.setRegister(PC_REGISTER, rMgr.getRegister(PC_REGISTER) + 2);
				
				registerNum = instruction[1] >>> 8;
				rMgr.setRegister(registerNum, 0);
				break;
			
			case 0x74:  // LDT 명령어: 해당 피연산자의 값을 T 레지스터에 저장한다.
				addLog("LDT"); 
				if(extForm)
				{
					logList.set(logList.size()-1, "+" + logList.get(logList.size()-1));
					instruction = rMgr.getMemory(rMgr.getRegister(PC_REGISTER), 4);
					targetAddr = ((instruction[1] & 15) << 16) + ((instruction[2] >> 8) << 12) + ((instruction[2] & 15) << 8) + ((instruction[3] >> 8) << 4) + (instruction[3] & 15);
					rMgr.setRegister(PC_REGISTER, rMgr.getRegister(PC_REGISTER) + 4);

					char[] data = rMgr.getMemory(targetAddr, 3);
					rMgr.setRegister(T_REGISTER, rMgr.byteToInt(data));				
				}
				else
				{
					instruction = rMgr.getMemory(rMgr.getRegister(PC_REGISTER), 3);
					targetAddr = ((instruction[1] & 15) << 8) + ((instruction[2] >> 8) << 4) + (instruction[2] & 15);
					rMgr.setRegister(PC_REGISTER, rMgr.getRegister(PC_REGISTER) + 3);

					if(pcRelative)
						targetAddr += rMgr.getRegister(PC_REGISTER);
					char[] data = rMgr.getMemory(targetAddr, 3);
					rMgr.setRegister(T_REGISTER, rMgr.byteToInt(data));
				}
				break;
			
			case 0xe0: // TD 명령어: 해당 이름의 기기(또는 파일)의 입출력 스트림을 확인한다.
				addLog("TD");
				if(extForm)  // 4형식 명령어인 경우,
				{
					logList.set(logList.size()-1, "+" + logList.get(logList.size()-1));
					instruction = rMgr.getMemory(rMgr.getRegister(PC_REGISTER), 4);
					targetAddr = ((instruction[1] & 15) << 16) + ((instruction[2] >> 8) << 12) + ((instruction[2] & 15) << 8) + ((instruction[3] >> 8) << 4) + (instruction[3] & 15);
					rMgr.setRegister(PC_REGISTER, rMgr.getRegister(PC_REGISTER) + 4);
					
					char[] deviceInfo = rMgr.getMemory(targetAddr, 1);
					String deviceName = String.format("%X%X", deviceInfo[0] >> 8, deviceInfo[0] & 15);
					currentDevice = deviceName;
					rMgr.testDevice(deviceName);
				}
				else  // 3형식 명령어인 경우,
				{
					instruction = rMgr.getMemory(rMgr.getRegister(PC_REGISTER), 3);
					targetAddr = ((instruction[1] & 15) << 8) + ((instruction[2] >>> 8) << 4) + (instruction[2] & 15);
					rMgr.setRegister(PC_REGISTER, rMgr.getRegister(PC_REGISTER) + 3);
					
					if(pcRelative)
						targetAddr += rMgr.getRegister(PC_REGISTER);
					char[] deviceInfo = rMgr.getMemory(targetAddr, 1);
					String deviceName = String.format("%X%X", deviceInfo[0] >> 8, deviceInfo[0] & 15);
					currentDevice = deviceName;
					rMgr.testDevice(deviceName);
				}
				
				break;
			
			case 0xd8:  // RD 명령어: 해당 기기(또는 파일)에서 문자 하나를 읽어 A레지스터에 저장한다.
				addLog("RD");
				if(extForm)
				{
					logList.set(logList.size()-1, "+" + logList.get(logList.size()-1));
					instruction = rMgr.getMemory(rMgr.getRegister(PC_REGISTER), 4);
					targetAddr = ((instruction[1] & 15) << 16) + ((instruction[2] >> 8) << 12) + ((instruction[2] & 15) << 8) + ((instruction[3] >> 8) << 4) + (instruction[3] & 15);
					rMgr.setRegister(PC_REGISTER, rMgr.getRegister(PC_REGISTER) + 4);
					
					char[] deviceInfo = rMgr.getMemory(targetAddr, 1);
					String deviceName = String.format("%X%X", deviceInfo[0] >> 8, deviceInfo[0] & 15);
					rMgr.setRegister(A_REGISTER, rMgr.readDevice(deviceName));
				}
				else
				{
					instruction = rMgr.getMemory(rMgr.getRegister(PC_REGISTER), 3);
					targetAddr = ((instruction[1] & 15) << 8) + ((instruction[2] >>> 8) << 4) + (instruction[2] & 15);
					rMgr.setRegister(PC_REGISTER, rMgr.getRegister(PC_REGISTER) + 3);
					
					if(pcRelative)
						targetAddr += rMgr.getRegister(PC_REGISTER);
					char[] deviceInfo = rMgr.getMemory(targetAddr, 1);
					String deviceName = String.format("%X%X", deviceInfo[0] >> 8, deviceInfo[0] & 15);
					rMgr.setRegister(A_REGISTER, rMgr.readDevice(deviceName));
				}				
				break;
				
			case 0xa0:  // COMPR 명령어: 두 레지스터 값을 비교한다.
				addLog("COMPR");
				instruction = rMgr.getMemory(rMgr.getRegister(PC_REGISTER), 2);
				rMgr.setRegister(PC_REGISTER, rMgr.getRegister(PC_REGISTER) + 2);
				
				registerNum = instruction[1] >>> 8;
				int compareRegister = instruction[1] & 15;
				difference = rMgr.getRegister(registerNum) - rMgr.getRegister(compareRegister);
				rMgr.setRegister(SW_REGISTER, difference);
				break;
			
			case 0x54:  // STCH 명령어: A레지스터 하위 1바이트에 저장된 문자를 지정된 주소에 저장한다.
				addLog("STCH");
				if(extForm)  // 4형식 명령어인 경우,
				{
					logList.set(logList.size()-1, "+" + logList.get(logList.size()-1));
					instruction = rMgr.getMemory(rMgr.getRegister(PC_REGISTER), 4);
					targetAddr = ((instruction[1] & 15) << 16) + ((instruction[2] >> 8) << 12) + ((instruction[2] & 15) << 8) + ((instruction[3] >> 8) << 4) + (instruction[3] & 15);
					rMgr.setRegister(PC_REGISTER, rMgr.getRegister(PC_REGISTER) + 4);
					
					char [] data = rMgr.intToChar(rMgr.getRegister(A_REGISTER) & 255);
					rMgr.setMemory(targetAddr + rMgr.getRegister(X_REGISTER), data, 1);
				}
				else  // 3형식 명령어인 경우,
				{
					instruction = rMgr.getMemory(rMgr.getRegister(PC_REGISTER), 3);
					targetAddr = ((instruction[1] & 15) << 8) + ((instruction[2] >> 8) << 4) + (instruction[2] & 15);
					rMgr.setRegister(PC_REGISTER, rMgr.getRegister(PC_REGISTER) + 3);
					
					if(pcRelative)
						targetAddr += rMgr.getRegister(PC_REGISTER);
					char [] data = rMgr.intToChar(rMgr.getRegister(A_REGISTER) & 255);
					rMgr.setMemory(targetAddr + rMgr.getRegister(X_REGISTER), data, 1);
				}
				break;
			
			case 0xb8:  //TIXR 명령어: X 레지스터 값을 1 올리고 피연산자로 들어온 레지스터의 값과 비교한다.
				addLog("TIXR");
				instruction = rMgr.getMemory(rMgr.getRegister(PC_REGISTER), 2);
				rMgr.setRegister(PC_REGISTER, rMgr.getRegister(PC_REGISTER) + 2);
				
				registerNum = instruction[1] >>> 8;
				rMgr.setRegister(X_REGISTER, rMgr.getRegister(X_REGISTER)+1);
				difference = rMgr.getRegister(X_REGISTER) - rMgr.getRegister(registerNum);
				rMgr.setRegister(SW_REGISTER, difference);
				break;
			
			case 0x38:  // JLT 명령어: 비교 후 작다면 명시된 주소로 이동한다.
				addLog("JLT");
				if(extForm)  // 4형식 명령어인 경우,
				{
					logList.set(logList.size()-1, "+" + logList.get(logList.size()-1));
					instruction = rMgr.getMemory(rMgr.getRegister(PC_REGISTER), 4);
					targetAddr = ((instruction[1] & 15) << 16) + ((instruction[2] >>> 8) << 12) + ((instruction[2] & 15) << 8) + ((instruction[3] >>> 8) << 4) + (instruction[3] & 15);
					rMgr.setRegister(PC_REGISTER, rMgr.getRegister(PC_REGISTER) + 4);
					
					if((instruction[1] & 15) == 15)
						targetAddr += (0xFFF << 20);
					
					if(rMgr.getRegister(SW_REGISTER) < 0)
					{
						rMgr.setRegister(PC_REGISTER, targetAddr);
					}
					rMgr.setCurrentSection();
				}
				else  // 3형식 명령어인 경우,
				{
					instruction = rMgr.getMemory(rMgr.getRegister(PC_REGISTER), 3);
					targetAddr = ((instruction[1] & 15) << 8) + ((instruction[2] >>> 8) << 4) + (instruction[2] & 15);
					rMgr.setRegister(PC_REGISTER, rMgr.getRegister(PC_REGISTER) + 3);
					
					if((instruction[1] & 15) == 15)
						targetAddr += (0xFFFFF << 12);
					
					if(pcRelative)
						targetAddr += rMgr.getRegister(PC_REGISTER);
					
					if(rMgr.getRegister(SW_REGISTER) < 0)
					{
						rMgr.setRegister(PC_REGISTER, targetAddr);
					}
					rMgr.setCurrentSection();
				}
				break;
			
			case 0x10:  // STX 명령어: X레지스터의 값을 지정된 주소에 저장한다.
				addLog("STX");
				if(extForm)  // 4형식 명령어인 경우,
				{
					logList.set(logList.size()-1, "+" + logList.get(logList.size()-1));
					instruction = rMgr.getMemory(rMgr.getRegister(PC_REGISTER), 4);
					targetAddr = ((instruction[1] & 15) << 16) + ((instruction[2] >>> 8) << 12) + ((instruction[2] & 15) << 8) + ((instruction[3] >>> 8) << 4) + (instruction[3] & 15);
					rMgr.setRegister(PC_REGISTER, rMgr.getRegister(PC_REGISTER) + 4);
	
					char[] data = new char[3];
					rMgr.setMemory(targetAddr, data, 3);
					data = rMgr.intToChar(rMgr.getRegister(X_REGISTER));
					rMgr.modifMemory(targetAddr + (3 - data.length), data, data.length, '+');
				}
				else  // 3형식 명령어인 경우,
				{
					instruction = rMgr.getMemory(rMgr.getRegister(PC_REGISTER), 3);
					targetAddr = ((instruction[1] & 15) << 8) + ((instruction[2] >>> 8) << 4) + (instruction[2] & 15);
					rMgr.setRegister(PC_REGISTER, rMgr.getRegister(PC_REGISTER) + 3);
					
					if(pcRelative)
						targetAddr += rMgr.getRegister(PC_REGISTER);
					char[] data = new char[3];
					rMgr.setMemory(targetAddr, data, 3);
					data = rMgr.intToChar(rMgr.getRegister(X_REGISTER));
					rMgr.modifMemory(targetAddr + (3 - data.length), data, data.length, '+');
				}
				break;
				
			case 0x30:  // JEQ 명령어: 비교한 두 값이 같은 경우 지정된 주소로 이동한다.
				addLog("JEQ");
				if(extForm)  // 4형식 명령어인 경우,
				{
					logList.set(logList.size()-1, "+" + logList.get(logList.size()-1));
					instruction = rMgr.getMemory(rMgr.getRegister(PC_REGISTER), 4);
					targetAddr = ((instruction[1] & 15) << 16) + ((instruction[2] >>> 8) << 12) + ((instruction[2] & 15) << 8) + ((instruction[3] >>> 8) << 4) + (instruction[3] & 15);
					rMgr.setRegister(PC_REGISTER, rMgr.getRegister(PC_REGISTER) + 4);
					
					if((instruction[1] & 15) == 15)
						targetAddr += (0xFFF << 20);
					
					if(rMgr.getRegister(SW_REGISTER) == 0)
					{
						rMgr.setRegister(PC_REGISTER, targetAddr);
					}
					rMgr.setCurrentSection();
				}
				else // 3형식 명령어인 경우,
				{
					instruction = rMgr.getMemory(rMgr.getRegister(PC_REGISTER), 3);
					targetAddr = ((instruction[1] & 15) << 8) + ((instruction[2] >>> 8) << 4) + (instruction[2] & 15);
					rMgr.setRegister(PC_REGISTER, rMgr.getRegister(PC_REGISTER) + 3);
					
					if((instruction[1] & 15) == 15)
						targetAddr += (0xFFFFF << 12);
					if(pcRelative)
						targetAddr += rMgr.getRegister(PC_REGISTER);
					if(rMgr.getRegister(SW_REGISTER) == 0)
					{
						rMgr.setRegister(PC_REGISTER, targetAddr);
					}
					rMgr.setCurrentSection();
				}
				break;
		}
		
		// instruction 출력을 위해 4비트에 해당하는 값을 문자로 다시 변환
		// 변환한 instruction을 리스트에 추가한다.
		char[] outputInst = new char[instruction.length*2];
		for(int  i = 0; i < instruction.length; i++)
		{
			outputInst[i * 2] = (char)((instruction[i] >> 8) + '0');
			outputInst[i * 2 + 1] = (char)((instruction[i] & 255) + '0');
			
			if((instruction[i] >> 8) >= 10)
				outputInst[i * 2] += 7;
			
			if((instruction[i] & 255) >= 10)
				outputInst[i * 2 + 1] += 7;
		}
		instList.add(new String(outputInst, 0, outputInst.length));
	}
	
	/**
	 * 남은 모든 instruction이 수행된 모습을 보인다.
	 */
	public void allStep() {
		
		while(true)
		{
			oneStep();
			
			// 초기 지정한 L 레지스터 값과 PC 레지스터 값이 같아지면 작동을 멈춘다.
			if(rMgr.getRegister(PC_REGISTER) == INIT_RETADR)
				break;
		}
	}
	
	/**
	 * 각 단계를 수행할 때 마다 관련된 기록을 남기도록 한다.
	 */
	public void addLog(String log) {
		logList.add(log);
	}
	
	/**
	 * GUI 컴포넌트 출력을 위해 log 리스트를 반환한다.
	 * @return log 리스트
	 */
	public List<String> getLogList()
	{
		return logList;
	}
	
	/**
	 * GUI 컴포넌트 출력을 위해 instruction 리스트를 반환한다.
	 * @return instruction 리스트
	 */
	public List<String> getInstList()
	{
		return instList;
	}
	
	/**
	 * 현재 명령어의 target address 값을 반환한다.
	 * @return target address
	 */
	public int getTargetAddr()
	{
		return targetAddr; 
	}
	
	/**
	 * 현재 사용중인 입출력 장치 정보를 반환한다.
	 * @return 입출력 장치 이름
	 */
	public String getDevice()
	{
		return currentDevice;
	}
}

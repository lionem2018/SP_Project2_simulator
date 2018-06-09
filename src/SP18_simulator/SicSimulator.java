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
	ResourceManager rMgr;
	char[] currentInst;
	
	List<String> logList = new ArrayList<>();
	
	public SicSimulator(ResourceManager resourceManager) {
		// 필요하다면 초기화 과정 추가
		this.rMgr = resourceManager;
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
		char [] upperByte = rMgr.getMemory(rMgr.register[8], 2);
		int temp = (upperByte[0] >>> 4) + (upperByte[0] & 15);
		int opcode = temp;
		boolean extForm = false;
		boolean pcRelative = false;
		boolean usedXregister = false;
		boolean immediate = false;
		boolean indirect = false;
		int address = 0;
		
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
		
		temp = (upperByte[1] >>> 8);
		extForm = (temp & 1) == 1;
		pcRelative = (temp & 2) == 2;
		usedXregister = (temp & 8) == 8;
		
		switch(opcode)
		{
			case 0x14:
				logList.add("STL");
				rMgr.register[8] += 3;
				if(extForm)
				{
					rMgr.register[8] += 1;
					logList.set(logList.size()-1, "+" + logList.get(logList.size()-1));
				}
				
				break;
				
			case 0x48:
				logList.add("JSUB");
				rMgr.register[8] += 3;
				if(extForm)
				{
					rMgr.register[8] += 1;
					logList.set(logList.size()-1, "+" + logList.get(logList.size()-1));
				}
				
				break;
				
			case 0x00:
				logList.add("LDA");
				rMgr.register[8] += 3;
				if(extForm)
				{
					rMgr.register[8] += 1;
					logList.set(logList.size()-1, "+" + logList.get(logList.size()-1));
				}
				
				break;
				
			case 0x28:
				logList.add("COMP");
				rMgr.register[8] += 3;
				if(extForm)
				{
					rMgr.register[8] += 1;
					logList.set(logList.size()-1, "+" + logList.get(logList.size()-1));
				}
				
				break;
		
			case 0x4c:
				logList.add("RSUB");
				rMgr.register[8] += 3;
				if(extForm)
				{
					rMgr.register[8] += 1;
					logList.set(logList.size()-1, "+" + logList.get(logList.size()-1));
				}
				
				break;
			
			case 0x50:
				logList.add("LDCH");
				rMgr.register[8] += 3;
				if(extForm)
				{
					rMgr.register[8] += 1;
					logList.set(logList.size()-1, "+" + logList.get(logList.size()-1));
				}
				
				break;
			
			case 0xdc:
				logList.add("WD");
				rMgr.register[8] += 3;
				if(extForm)
				{
					rMgr.register[8] += 1;
					logList.set(logList.size()-1, "+" + logList.get(logList.size()-1));
				}
				
				break;
				
			case 0x3c:
				logList.add("J");
				rMgr.register[8] += 3;
				if(extForm)
				{
					rMgr.register[8] += 1;
					logList.set(logList.size()-1, "+" + logList.get(logList.size()-1));
				}
				
				break;
			
			case 0x0c:
				logList.add("STA");
				rMgr.register[8] += 3;
				if(extForm)
				{
					rMgr.register[8] += 1;
					logList.set(logList.size()-1, "+" + logList.get(logList.size()-1));
				}
				
				break;
				
			case 0xb4:
				logList.add("CLEAR");
				rMgr.register[8] += 2;
				
				break;
			
			case 0x74:
				logList.add("LDT");
				rMgr.register[8] += 3;
				if(extForm)
				{
					rMgr.register[8] += 1;
					logList.set(logList.size()-1, "+" + logList.get(logList.size()-1));
				}
				
				break;
			
			case 0xe0:
				logList.add("TD");
				rMgr.register[8] += 3;
				if(extForm)
				{
					rMgr.register[8] += 1;
					logList.set(logList.size()-1, "+" + logList.get(logList.size()-1));
				}
				
				break;
			
			case 0xd8:
				logList.add("RD");
				rMgr.register[8] += 3;
				if(extForm)
				{
					rMgr.register[8] += 1;
					logList.set(logList.size()-1, "+" + logList.get(logList.size()-1));
				}
				
				break;
				
			case 0xa0:
				logList.add("COMPR");
				rMgr.register[8] += 2;
				
				break;
			
			case 0x54:
				logList.add("STCH");
				rMgr.register[8] += 3;
				if(extForm)
				{
					rMgr.register[8] += 1;
					logList.set(logList.size()-1, "+" + logList.get(logList.size()-1));
				}
				
				break;
			
			case 0xb8:
				logList.add("TIXR");
				rMgr.register[8] += 2;
				
				break;
			
			case 0x38:
				logList.add("JLT");
				rMgr.register[8] += 3;
				if(extForm)
				{
					rMgr.register[8] += 1;
					logList.set(logList.size()-1, "+" + logList.get(logList.size()-1));
				}
				
				break;
			
			case 0x10:
				logList.add("STX");
				rMgr.register[8] += 3;
				if(extForm)
				{
					rMgr.register[8] += 1;
					logList.set(logList.size()-1, "+" + logList.get(logList.size()-1));
				}
				
				break;
				
			case 0x30:
				logList.add("JEQ");
				rMgr.register[8] += 3;
				if(extForm)
				{
					rMgr.register[8] += 1;
					logList.set(logList.size()-1, "+" + logList.get(logList.size()-1));
				}
				
				break;
		}
		
		/*
		char [] xbpe = rMgr.getMemory(rMgr.register[8]+1, 2);
		int format = 3;
		if((xbpe[0] & 1) == 1)
		{
			format = 4;
		}
		
		System.out.println(rMgr.getMemory(rMgr.register[8], format));
		rMgr.register[8] += format/2;
		*/
		System.out.println("PC: " +rMgr.register[8]);
		for(int i = 0; i < logList.size(); i++)
		{
			System.out.print(logList.get(i) + " ");
		}
		System.out.println();
	}
	
	/**
	 * 남은 모든 instruction이 수행된 모습을 보인다.
	 */
	public void allStep() {
	}
	
	/**
	 * 각 단계를 수행할 때 마다 관련된 기록을 남기도록 한다.
	 */
	public void addLog(String log) {
	}	
}

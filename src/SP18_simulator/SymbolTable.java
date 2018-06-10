package SP18_simulator;

import java.util.ArrayList;

/**
 * symbol과 관련된 데이터와 연산을 소유한다. section 별로 하나씩 인스턴스를 할당한다.
 */
public class SymbolTable
{
	ArrayList<String> symbolList;
	ArrayList<Integer> addressList;
	// 기타 literal, external 선언 및 처리방법을 구현한다.
	
	// 수정 사이즈를 담은 리스트
	ArrayList<Integer> modifSizeList;
	// 수정 모드, 즉 부호를 담은 리스트
	ArrayList<Character> modifModeList;
	// 수정할 섹션 프로그램 번호를 담은 리스트
	ArrayList<Integer> sectionList;
	

	public SymbolTable()
	{
		symbolList = new ArrayList<>();
		addressList = new ArrayList<>();
		modifSizeList = new ArrayList<>();
		modifModeList = new ArrayList<>();
		sectionList = new ArrayList<>();
	}

	/**
	 * 새로운 Symbol을 table에 추가한다.
	 * 
	 * @param symbol
	 *            : 새로 추가되는 symbol의 label
	 * @param address
	 *            : 해당 symbol이 가지는 주소값 <br>
	 * 			<br>
	 *            주의 : 만약 중복된 symbol이 putSymbol을 통해서 입력된다면 이는 프로그램 코드에 문제가 있음을 나타낸다.
	 *            매칭되는 주소값의 변경은 modifySymbol()을 통해서 이루어져야 한다.
	 */
	public void putSymbol(String symbol, int address)
	{
		String inputSymbol = symbol;
		if (!symbolList.contains(inputSymbol))
		{
			// 심볼과 인자로 들어온 주소값을 저장함
			symbolList.add(inputSymbol);
			addressList.add(address);
		}

	}
	
	/**
	 * 수정을 위한 extab에 정보를 추가하는 메소드이다.
	 * @param symbol 추가할 심볼
	 * @param address 수정할 주소
	 * @param modifSize 수정할 사이즈
	 * @param modifMode 수정 모드, 즉 부호
	 * @param section 수정할 컨트롤 섹션
	 */
	public void putExSymbol(String symbol, int address, int modifSize, char modifMode, int section)
	{
		symbolList.add(symbol);
		addressList.add(address);
		modifSizeList.add(modifSize);
		modifModeList.add(modifMode);
		sectionList.add(section);
	}

	/**
	 * 기존에 존재하는 symbol 값에 대해서 가리키는 주소값을 변경한다.
	 * 
	 * @param symbol
	 *            : 변경을 원하는 symbol의 label
	 * @param newaddress
	 *            : 새로 바꾸고자 하는 주소값
	 */
	public void modifySymbol(String symbol, int newaddress)
	{
		String inputSymbol = symbol;

		// List 상에 이미 저장되어있는 경우에만 수정이 가능
		if (symbolList.contains(inputSymbol))
		{
			// 저장되어있는 심볼의 위치를 찾아 인자로 받은 새로운 주소값을 넣어줌
			for (int index = 0; index < symbolList.size(); index++)
				if (inputSymbol.equals(symbolList.get(index)))
				{
					symbolList.set(index, inputSymbol);
					addressList.set(index, newaddress);
					break;
				}
		}
	}

	/**
	 * 인자로 전달된 symbol이 어떤 주소를 지칭하는지 알려준다.
	 * 
	 * @param symbol
	 *            : 검색을 원하는 symbol의 label
	 * @return symbol이 가지고 있는 주소값. 해당 symbol이 없을 경우 -1 리턴
	 */
	public int search(String symbol)
	{
		// 출력할 주소값 저장
		int address = 0;

		// 인자로 받은 심볼이 List 상에 있는 경우
		// 해당 심볼의 주소값을 찾아 address에 지정
		if (symbolList.contains(symbol))
		{
			for (int index = 0; index < symbolList.size(); index++)
				if (symbol.equals(symbolList.get(index)))
				{
					address = addressList.get(index);
					break;
				}
		}
		// 없는 경우 -1을 address에 지정
		else
			address = -1;

		// address 리턴
		return address;
	}
	
	/**
	 * 심볼 테이블의 사이즈를 구한다
	 * @return 테이블 사이즈
	 */
	public int size()
	{
		return symbolList.size();
	}
	
	/**
	 * 해당 인덱스의 심볼을 가져온다.
	 * @param index 가져올 심볼 인덱스
	 * @return 심볼 이름
	 */
	public String getSymbol(int index)
	{
		return symbolList.get(index);
	}
	
	/**
	 * 해당 인덱스의 주소를 가져온다.
	 * @param sectionNum 가져올 주소 인덱스
	 * @return 주소
	 */
	public int getaddress(int index)
	{
		return addressList.get(index);
	}
	
	/**
	 * 해당 인덱스의 수정 사이즈를 가져온다.
	 * @param sectionNum 가져올 수정 사이즈 인덱스
	 * @return 수정 사이즈
	 */
	public int getModifSize(int index)
	{
		return modifSizeList.get(index);
	}
	
	/**
	 * 해당 인덱스의 수정 모드 가져온다.
	 * @param sectionNum 가져올 수정 모드 인덱스
	 * @return 수정 모드
	 */
	public char getModifMode(int index)
	{
		return modifModeList.get(index);
	}
	
	/**
	 * 해당 인덱스의 섹션을 가져온다.
	 * @param sectionNum 가져올 섹션 인덱스
	 * @return 섹션
	 */
	public int getSection(int index)
	{
		return sectionList.get(index);
	}
	
}

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
	ArrayList<Integer> modifSizeList;
	ArrayList<Character> modifModeList;
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
	
	public int size()
	{
		return symbolList.size();
	}
	
	public String getSymbol(int index)
	{
		return symbolList.get(index);
	}
	
	public int getaddress(int index)
	{
		return addressList.get(index);
	}
	
	public int getModifSize(int index)
	{
		return modifSizeList.get(index);
	}
	
	public char getModifMode(int index)
	{
		return modifModeList.get(index);
	}
	
	public int getSection(int index)
	{
		return sectionList.get(index);
	}
	
	public void print()
	{
		for(int i = 0; i < symbolList.size(); i++)
		{
			System.out.println(symbolList.get(i) + " " + addressList.get(i) + " " + modifSizeList.get(i) + " " + modifModeList.get(i));
		}
	}

}

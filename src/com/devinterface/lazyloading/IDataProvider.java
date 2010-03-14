package com.devinterface.lazyloading;

import java.util.List;

public interface IDataProvider<T> {

	List<T> getBufferedData(int startRow, int offset);

}

package com.devinterface.lazyloading;


import java.util.AbstractList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * This list stores records in a map with key = "table row index" and value = "element"
 * It keeps a buffer of 3 pages: the first time it will load pages 1,2,3. 
 * When user moves to page 4, all stored data will be cleared and will be retrieved page 3,4,5.
 * Basically, this list will store current page, previous page and next page. 
 * If bufferSize is equals to totalResultsNumber, the dataTable will be non paginated: the first query will retrieve all the dataset.
 *
 * @param <T>
 */
public class LazyLoadingBufferedMapList<T> extends AbstractList<T>
{
  private IDataProvider<T> dataAdapter;

  private int totalResultsNumber;
  private int pageSize = 10;
  private int bufferSize = pageSize * 3;

  /** cache of loadedData items */
  private Map<Integer, T> loadedData;


  /**
   * 
   * @param dataAdapter, the object that will perform the query
   * @param pageSize, the number of rows to be considered as "a page"
   * @param totalResultsNumber, the total number of rows as result of the database query. 
   */
  public LazyLoadingBufferedMapList(IDataProvider<T> dataAdapter, int pageSize, int totalResultsNumber)
  {
    this.dataAdapter = dataAdapter;
    this.totalResultsNumber = totalResultsNumber;
    this.pageSize = pageSize;
    loadedData = new HashMap<Integer, T>();
  }


  @Override
  public T get(int i)
  {
    if (!loadedData.containsKey(i))
    {
      clearMap();

      int startRow = getStartRow(i);

      int numElementToFind = bufferSize;
      if ((startRow + numElementToFind) > totalResultsNumber)
        numElementToFind = totalResultsNumber - startRow;

      List<T> results = dataAdapter.getBufferedData(startRow, numElementToFind);
      for (int j = 0; j < results.size(); j++)
        loadedData.put((startRow + j), (T) results.get(j));
    }
    return loadedData.get(i);

  }


  /**
   * clears the map except the first element that MUST be kept
   */
  private void clearMap()
  {
    T firstElement = loadedData.get(0);
    loadedData.clear();
    loadedData.put(0, firstElement);
  }


  /**
   * Calculates the index of the previous page's first element
   * @param i, the current row index
   * @return the index of the previous page's first element
   */
  private int getStartRow(int i)
  {
    int currentPage = (i / pageSize) + 1;

    int firstIndexOfCurrentPage = pageSize * (currentPage - 1);

    int firstIndexOfPreviusPage = firstIndexOfCurrentPage - (bufferSize / 3);

    if (firstIndexOfPreviusPage < 0)
      firstIndexOfPreviusPage = 0;

    return firstIndexOfPreviusPage;
  }


  @Override
  public int size()
  {
    return totalResultsNumber;
  }


  public void setNumResults(int numResults)
  {
    this.totalResultsNumber = numResults;
  }


  @Override
  public void clear()
  {
    loadedData.clear();
  }
}

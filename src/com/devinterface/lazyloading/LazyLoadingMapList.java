package com.devinterface.lazyloading;

import java.util.AbstractList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This list stores all records in a map with key = "table row index" and value = "T"
 * If pageSize is equals to totalResultsNumber, the dataTable will be non paginated: the first query will retrieve all the dataset.
 * Note that if a user moves from first to last page, one page at time, all record will be in memory.
 *
 * @param <T>
 */
public class LazyLoadingMapList<T> extends AbstractList<T>
{
  private IDataProvider<T> dataProvider;

  private int totalResultsNumber;
  private int pageSize;

  /** cache of loadedData items */
  private Map<Integer, T> loadedData;


  /**
   * @param dataProvider, the object that will perform the query
   * @param pageSize, the number of rows to be considered as "a page"
   * @param totalResultsNumber, the total number of rows as result of the database count query. 
   */
  public LazyLoadingMapList(IDataProvider<T> dataProvider, int pageSize, int totalResultsNumber)
  {
    this.dataProvider = dataProvider;
    this.totalResultsNumber = totalResultsNumber;
    this.pageSize = pageSize;
    loadedData = new HashMap<Integer, T>();
  }


  @Override
  public T get(int i)
  {
    if (!loadedData.containsKey(i))
    {
      int pageIndex = i / pageSize;
      List<T> results = dataProvider.getBufferedData(i, pageSize);
      for (int j = 0; j < results.size(); j++)
      {
        loadedData.put(Integer.valueOf(pageIndex * pageSize + j), (T) results.get(j));
      }
    }
    return loadedData.get(i);

  }


  @Override
  public int size()
  {
    return totalResultsNumber;
  }


  public void setTotalResultsNumber(int totalResultsNumber)
  {
    this.totalResultsNumber = totalResultsNumber;
  }


  @Override
  public void clear()
  {
    loadedData.clear();
  }
  
}

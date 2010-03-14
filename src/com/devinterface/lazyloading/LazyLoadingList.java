package com.devinterface.lazyloading;

import java.util.AbstractList;
import java.util.List;

/**
 * This list loads and stores only the first page and the current page.
 * If pageSize is equals to totalResultsNumber, the dataTable will be non paginated: the first query will retrieve all the dataset.
 *
 * @param <T>
 */
public class LazyLoadingList<T> extends AbstractList<T>
{
  private IDataProvider<T> dataProvider;

  private List<T> firtsPageData;
  private List<T> currentPageData;

  private int currentPage = -1;
  private int totalResultsNumber;
  private int pageSize;


  /**
   * @param dataProvider, the object that will perform the query
   * @param pageSize, the number of rows to be showed in a table page
   * @param totalResultsNumber, the total number of rows as result of the database count query. 
   */
  public LazyLoadingList(IDataProvider<T> dataProvider, int pageSize, int totalResultsNumber)
  {
    this.dataProvider = dataProvider;
    this.totalResultsNumber = totalResultsNumber;
    this.pageSize = pageSize;
  }


  @Override
  public T get(int i)
  {
    if (i < pageSize)
    {
      if (firtsPageData == null)
        firtsPageData = dataProvider.getBufferedData(i, pageSize);
      return firtsPageData.get(i);
    }
    int page = i / pageSize;
    
    if (page != currentPage)
    {
      currentPage = page;
      currentPageData = dataProvider.getBufferedData(i, pageSize);
    }
    
    return currentPageData.get(i % pageSize);
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
    firtsPageData.clear();
    currentPageData.clear();
  }
  
}

package com.blog4java.plugin.pager;

public interface Paginable<T> {

    /** 总记录数 */
    int getTotalCount();

    /** 总页数 */
    int getTotalPage();

    /** 每页记录数 */
    int getPageSize();

    /** 当前页号 */
    int getPageNo();

    /** 是否第一页 */
    boolean isFirstPage();

    /** 是否最后一页 */
    boolean isLastPage();

    /** 返回下页的页号 */
    int getNextPage();

    /** 返回上页的页号 */
    int getPrePage();

    /** 取得当前页显示的项的起始序号 */
    int getBeginIndex();

    /** 取得当前页显示的末项序号 */
    int getEndIndex();
    /** 获取开始页*/
    int getBeginPage();
    /** 获取结束页*/
    int getEndPage();
}

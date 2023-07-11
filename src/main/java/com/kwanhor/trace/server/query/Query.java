package com.kwanhor.trace.server.query;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.domain.Sort.Order;



/**
 * 查询条件模型
 * @author LiangGuanHao
 *
 * @param <E> 查询条件的Example模型
 */
public class Query<E> {
	private Map<String, Boolean> order;//排序字段名->是否升序排列,默认为false
	private int pageIndex;//查询分页索引,从0开始,若为-1则表示请求查询所有
	private int pageSize=30;//查询分页行数,默认为30
	private E args;//查询条件
	public int getPageIndex() {
		return pageIndex;
	}
	public void setPageIndex(int pageIndex) {
		this.pageIndex = pageIndex;
	}
	public int getPageSize() {
		return pageSize<=0?30:pageSize;
	}
	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}
	public E getArgs() {
		return args;
	}
	public void setArgs(E args) {
		this.args = args;
	}
	
	public Pageable toPageable() {
		if(pageIndex<0)
			return Pageable.unpaged();
		return Pageable.ofSize(getPageSize()).withPage(pageIndex);
	}
	
	public Sort toSort() {
		final Map<String, Boolean> orderMap=getOrder();
		if(getOrder().isEmpty())
			return Sort.unsorted();
		List<Order> orderList=new LinkedList<>();
		orderMap.forEach((name,isAsc)->{
			orderList.add(new Order(isAsc?Direction.ASC:Direction.DESC, name));
		});
		return Sort.by(orderList);
	}
	
	public Map<String, Boolean> getOrder() {
		return order==null?Collections.emptyMap():order;
	}
	public void setOrder(Map<String, Boolean> order) {
		this.order = order;
	}
	
}

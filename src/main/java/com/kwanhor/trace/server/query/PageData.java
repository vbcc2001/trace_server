package com.kwanhor.trace.server.query;

import java.util.Collection;

import org.springframework.data.domain.Page;

public class PageData {
	private int pageSize;
	private int pageIndex;
	private int pageCount;
	private Collection<?> content;
	public int getPageSize() {
		return pageSize;
	}
	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}
	public int getPageIndex() {
		return pageIndex;
	}
	public void setPageIndex(int pageIndex) {
		this.pageIndex = pageIndex;
	}
	public int getPageCount() {
		return pageCount;
	}
	public void setPageCount(int pageCount) {
		this.pageCount = pageCount;
	}
	public Collection<?> getContent() {
		return content;
	}
	public void setContent(Collection<?> content) {
		this.content = content;
	}
	public final static <T> PageData fromPage(Page<T> page) {
		PageData data=new PageData();
		if(page.getPageable().isPaged()) {
			data.setPageIndex(page.getPageable().getPageNumber());
			data.setPageCount(page.getTotalPages());
			data.setPageSize(page.getPageable().getPageSize());
		}else {
			data.setPageIndex(0);
			data.setPageCount(page.getContent().size());
			data.setPageSize(page.getContent().size());
		}
		data.setContent(page.getContent());
		return data;
	}
}

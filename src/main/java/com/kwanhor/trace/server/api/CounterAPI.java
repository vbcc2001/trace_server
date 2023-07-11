package com.kwanhor.trace.server.api;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.kwanhor.trace.server.error.RestException;
import com.kwanhor.trace.server.model.CartoonBox;
import com.kwanhor.trace.server.model.Pallet;
import com.kwanhor.trace.server.model.ProductCode;
import com.kwanhor.trace.server.model.ProductLaser;
import com.kwanhor.trace.server.repo.ProductCodeRepo;

import lombok.extern.slf4j.Slf4j;
/**
 * 统一流水计数器API
 * @author LiangGuanHao
 *
 */
@RestController
@RequestMapping("/counter")
@Slf4j
public class CounterAPI {	
	private final ProductCodeRepo productCodeRepo;	
	private final ReentrantLock lock=new ReentrantLock();
	public CounterAPI(ProductCodeRepo productCodeRepo) {
		super();
		this.productCodeRepo = productCodeRepo;
	}
	@Transactional(isolation = Isolation.READ_COMMITTED)
	@GetMapping("/get")// {{server}}/counter/get?prefix=xx&type=xx&isDay=xx&count=xx
	Integer[] getNum(final String prefix,final Integer type,boolean isDay,@RequestParam(required = false) Integer count) {
		if(count==null||count<=0)
			count=1;				
		if(prefix==null||prefix.isEmpty())
			throw new RestException("缺少参数prefix");
		final String fqcn=getFQCN(type);
		log.info("prefix:"+prefix+";fqcn:"+fqcn+";isDay:"+isDay);
		try {
			//初步评估500ms内基本能完成流水查询+更新的操作,前端响应超时为2000ms,网络正常的情况下基本不会丢失响应
			//客户端API单元测试模拟单接口并发，基本上30以内200ms(单接口线程无阻塞),100以内500ms(单接口有部分线程需先等待其他线程处理完毕),未发现数据混乱问题			
			//tomcat默认连接并发数200个,前端模拟测试250并发的时候出现部分连接超时
			if(lock.tryLock(500, TimeUnit.MILLISECONDS)) {
				ProductCode pc=productCodeRepo.selectRecord(prefix, fqcn, isDay);
				if(pc==null) {//新增记录
					pc=new ProductCode();
					pc.setDay(isDay);
					pc.setFqcn(fqcn);
					pc.setPos(count);
					pc.setPrefix(prefix);
					productCodeRepo.save(pc);
					return new Integer[] {1,count};
				}else {//更新记录
					int row=productCodeRepo.updatePos(pc.getId(), pc.getPos()+count,pc.getPos());
					if(row!=1)
						throw new RestException("流水更新失败，稍后再试");
					return new Integer[] {pc.getPos()+1,pc.getPos()+count};
				}
			}else {
				log.warn("前端主动中止流水申请");
				return null;
			}
		} catch (InterruptedException e) {
			log.warn("流水申请正忙，稍后再试");
			return null;
		} finally {
			if(lock.isHeldByCurrentThread()) {
				lock.unlock();
			}	
		}
		
			
	}
	private String getFQCN(Integer type) {
		if(type==null)
			throw new RestException("缺少参数type,0表示laser,1表示box,2表示pallet");
		switch(type) {
		case 0:
			return ProductLaser.class.getName();
		case 1:
			return CartoonBox.class.getName();
		case 2:
			return Pallet.class.getName();
		default:
			throw new RestException("参数type错误,0表示laser,1表示box,2表示pallet");
		}
	}
}

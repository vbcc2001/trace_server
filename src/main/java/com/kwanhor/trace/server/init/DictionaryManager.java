package com.kwanhor.trace.server.init;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.ReentrantLock;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import com.kwanhor.trace.server.TraceConfig;
import com.kwanhor.trace.server.error.RestException;
import com.kwanhor.trace.server.model.Dictionary;
import com.kwanhor.trace.server.repo.DictionaryRepo;
import com.kwanhor.trace.server.util.ModelUtil;

import lombok.extern.slf4j.Slf4j;
/**
 * 字典缓存管理器
 * @author LiangGuanHao
 *
 */
@Component
@Order(1)
@Slf4j
public class DictionaryManager implements ApplicationRunner{
	private static DictionaryManager INST;	
	public static DictionaryManager getInstance() {
		return INST;
	}
	private final DictionaryRepo dictionaryRepo;
	private final TraceConfig traceConfig;
	public DictionaryManager(DictionaryRepo dictionaryRepo, TraceConfig traceConfig) {
		super();
		this.dictionaryRepo = dictionaryRepo;
		this.traceConfig = traceConfig;
	}
	/**
	 * dicType->{code=text}
	 */
	private final Map<String, Map<String, String>> dicMaps=new HashMap<>();
	private final ReentrantLock lock=new ReentrantLock();
	@Override
	public void run(ApplicationArguments args) throws Exception {
		log.info("init dictionary data======");
		List<Dictionary> list=dictionaryRepo.findAll();
		for (Dictionary dic : list) {
			updateCache(dic.getCode(),dic,false);//不加锁更新
		}
		dicMaps.put("system", Map.of("version",getVersion()));
		INST=this;
	}
	
	public Iterable<Dictionary> getByType(String dicType){
		if(dicType==null||dicType.isEmpty())
			throw new RestException("缺少参数dicType");
		return dictionaryRepo.getByDicType(dicType);
	} 
	
	public Iterable<Dictionary> add(List<Dictionary> dicList){
		if(dicList==null||dicList.isEmpty())
			return Collections.emptyList();
		Iterable<Dictionary> rs=dictionaryRepo.saveAllAndFlush(dicList);
		for (Dictionary dictionary : rs) {
			updateCache(dictionary.getCode(),dictionary,true);
		}
		return rs;
	}
	/**
	 * 按类型批量删除字典(物理删除)
	 * @param dicType 字典类型
	 * @return 逻辑删除的行数
	 */
	public int delByCode(String dicType){
		if(dicType==null||dicType.isEmpty())
			throw new RestException("缺少参数dicType");
		int delRow=dictionaryRepo.deleteByDicType(dicType);
		dicMaps.remove(dicType);//删除缓存
		return delRow;
	}
	
	private void updateCache(String originCode,Dictionary dic,boolean isSync) {
		final String dicType=dic.getDicType();
		if(isSync)
			lock.lock();
		try {			
			Map<String, String> valueMap=dicMaps.get(dicType);
			if(valueMap==null) {
				valueMap=new HashMap<>();
				dicMaps.put(dicType, valueMap);
			}
			if(originCode!=null&&!originCode.isEmpty())valueMap.remove(originCode);
			valueMap.put(dic.getCode(), dic.getText());
		}finally {
			if(isSync)
				lock.unlock();
		}
	}
	/**
	 * 
	 * @param dicType 字典类型
	 * @param dicCode 字典编码
	 * @return 字典值,永不为null
	 */
	public String getDicValue(String dicType,String dicCode) {
		if(dicType==null||dicType.isEmpty())
			throw new RestException("缺少参数dicType");
		if(dicCode==null||dicCode.isBlank())
			throw new RestException("缺少参数dicCode");
		Map<String, String> valueMap=dicMaps.get(dicType);
		if(valueMap==null||valueMap.isEmpty())
			return "";
		return valueMap.getOrDefault(dicCode, "");
	}
	public Dictionary update(Dictionary dictionary){
		if(dictionary==null)
			return null;
		final Long id=dictionary.getId();
		if(id==null)
			throw new RestException("更新失败，未指定字典ID");
		Dictionary persist=dictionaryRepo.findById(id).orElse(null);
		if(persist==null)
			throw new RestException("更新失败,字典ID不存在:"+id);
		final String originCode=persist.getCode();//原code
		ModelUtil.merge(dictionary, persist);
		dictionaryRepo.flush();
		updateCache(originCode,persist,true);//更新缓存
		return persist;
	}
	
	public List<String> getAllVersion(){
		return null;
	}
	
	public String getVersion() {
		return traceConfig.getClientType();
	}
	public void addOrUpdate(String dicType,String code,String value) {
		Dictionary dic=dictionaryRepo.findByDicTypeAndCode(dicType, code);
		if(dic==null) {
			final Dictionary input=new Dictionary();
			input.setCode(code);
			input.setDicType(dicType);
			input.setText(value);
			add(List.of(input));
		}else {			
			dic.setText(value);//只更新value
			dictionaryRepo.flush();
			updateCache(null, dic, true);
		}
	}
}

package com.kwanhor.trace.server.api;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.kwanhor.trace.server.init.DictionaryManager;
import com.kwanhor.trace.server.model.Dictionary;

@RestController
@RequestMapping("/dic")
public class DictionaryAPI {
	@Autowired
	private DictionaryManager dictionaryManager;
	
	@PostMapping("/getByType")
	Iterable<Dictionary> add(String dicType){
		return dictionaryManager.getByType(dicType);
	}
	
	@PostMapping("/add")
	Iterable<Dictionary> add(@RequestBody List<Dictionary> dicList){
		return dictionaryManager.add(dicList);
	}
	
	@PostMapping("/delByType")
	int delByType(String dicType){//逻辑删除
		return dictionaryManager.delByCode(dicType);
	}
	@GetMapping(value="/get",produces = "application/json;charset=UTF-8")
	String getDicValue(String dicType,String dicCode) {
		return dictionaryManager.getDicValue(dicType, dicCode);
	}
	
	@PostMapping("/update")
	Dictionary update(@RequestBody Dictionary dictionary){
		return dictionaryManager.update(dictionary);
	}
	/**
	 * 
	 * @param dicType 字典类型
	 * @param code 字典编码
	 * @param value 字典值
	 * @deprecated value可能包含中文,JSON字符串等，改用{@link #saveDic(Dictionary)}
	 */
	@PostMapping("/addOrUpdate")// {{server}}/dic/addOrUpdate
	void addOrUpdate(String dicType,String code,String value) {
		dictionaryManager.addOrUpdate(dicType, code, value);
	}
	@PostMapping("/saveDic")// {{server}}/dic/saveDic 	{dicType:xx,code:xx,text:xx}
	void saveDic(@RequestBody Dictionary dic) {		
		dictionaryManager.addOrUpdate(dic.getDicType(), dic.getCode(), dic.getText());
		
	}
}

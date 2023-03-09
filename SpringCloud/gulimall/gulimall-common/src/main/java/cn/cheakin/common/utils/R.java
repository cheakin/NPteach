/**
 * Copyright (c) 2016-2019 人人开源 All rights reserved.
 *
 * https://www.renren.io
 *
 * 版权所有，侵权必究！
 */

package cn.cheakin.common.utils;

import cn.cheakin.common.vo.MemberResponseVo;
import cn.hutool.core.lang.TypeReference;
import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson.JSONObject;
import org.apache.http.HttpStatus;

import java.util.HashMap;
import java.util.Map;

/**
 * 返回数据
 *
 * @author Mark sunlightcs@gmail.com
 */
public class R extends HashMap<String, Object> {
	private static final long serialVersionUID = 1L;

	public Object getData() {
		Object data = this.get("data");
		return data;
	}

	public <T> T getData(String name , TypeReference<T> typeReference) {
		Object data = this.get(name);	// 默认返回是map类型的
		T t = JSONUtil.toBean(JSONUtil.toJsonStr(data), typeReference, false);
//		T t = JSON.parseObject(String.valueOf(data), typeReference);
		return t;
	}

	public <T> T getData(TypeReference<T> typeReference) {
		return getData("data", typeReference);
	}

	public <T> T getData(String name , Class<T> clazz) {
		Object data = this.get(name);	// 默认返回是map类型的
//		T t = JSONUtil.toBean(JSONUtil.toJsonStr(data), clazz);
		T t = JSONObject.parseObject(JSONObject.toJSONString(data), clazz);
		return t;
	}

	public <T> T getData(Class<T> clazz) {
		return getData("data", clazz);
	}

	public R setData(Object data) {
//		this.put("data", JSONObject.toJSONString(data));
		this.put("data", data);
		return this;
	}

	public R() {
		put("code", 0);
		put("msg", "success");
	}

	public static R error() {
		return error(HttpStatus.SC_INTERNAL_SERVER_ERROR, "未知异常，请联系管理员");
	}

	public static R error(String msg) {
		return error(HttpStatus.SC_INTERNAL_SERVER_ERROR, msg);
	}

	public static R error(int code, String msg) {
		R r = new R();
		r.put("code", code);
		r.put("msg", msg);
		return r;
	}

	public static R ok(String msg) {
		R r = new R();
		r.put("msg", msg);
		return r;
	}

	public static R ok(Map<String, Object> map) {
		R r = new R();
		r.putAll(map);
		return r;
	}

	public static R ok() {
		return new R();
	}

	public R put(String key, Object value) {
		super.put(key, value);
		return this;
	}

	public Integer getCode() {

		return (Integer) this.get("code");
	}

	public static void main(String[] args) {
		/*MemberResponseVo vo = new MemberResponseVo();
//		vo.setCity("123");
		List<MemberResponseVo> vos = new ArrayList<>();
		vos.add(vo);
		R r = R.ok().setData(vos);
		System.out.println("======================");
		List<MemberResponseVo> data = new ArrayList<>();

		List<MemberResponseVo> data = (List<MemberResponseVo>)r.getData();
		System.out.println("data = " + data);

		data = r.getData(List.class);
		System.out.println("data = " + data);

		data = r.getData(new TypeReference<List<MemberResponseVo>>() {
		});
		System.out.println("data = " + data);*/

		MemberResponseVo vo = new MemberResponseVo();
		R r = R.ok().setData(vo);
		System.out.println("================");
		MemberResponseVo data = new MemberResponseVo();

		data = (MemberResponseVo)r.getData();
		System.out.println("data = " + data);

		data = r.getData(MemberResponseVo.class);
		System.out.println("data = " + data);

		data = r.getData(new TypeReference<MemberResponseVo>() {
		});
		System.out.println("data = " + data);

	}

}
